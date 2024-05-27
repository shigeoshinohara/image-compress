package jp.sync_web;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException {
        System.out.println( "Start!" );
        String baseDir = "/tmp/image/";

        BufferedImage bi = ImageIO.read(new File(baseDir + "neko2.jpeg"));
        byte[] bytes = toByteArray(bi, "jpeg");
        byte[] newbytes = resize(bytes, 1, 0.5f);
        // convert the byte[] back to BufferedImage
        BufferedImage newBi = toBufferedImage(newbytes);

        // save it somewhere
        ImageIO.write(newBi, "jpg", new File(baseDir + "neko2_comp.jpeg"));
        System.out.println( "End!" );
    }

    // convert BufferedImage to byte[]
    public static byte[] toByteArray(BufferedImage bi, String format)
            throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, format, baos);
        byte[] bytes = baos.toByteArray();
        return bytes;

    }


    // convert byte[] to BufferedImage
    public static BufferedImage toBufferedImage(byte[] bytes)
            throws IOException {

        InputStream is = new ByteArrayInputStream(bytes);
        BufferedImage bi = ImageIO.read(is);
        return bi;

    }


    public static BufferedImage resizeImage(final BufferedImage image, final double scale) throws IOException {
        int width = (int) (image.getWidth() * scale);
        int height = (int) (image.getHeight() * scale);
        BufferedImage resizedImage = new BufferedImage(width, height, image.getType());

        // アフィン変換でリサイズ（画質優先）
        AffineTransform transform = AffineTransform.getScaleInstance(scale, scale);
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BICUBIC);
        op.filter(image, resizedImage);

        return resizedImage;
    }

    public static byte[] resize(final byte[] src, final double scale, final float quality) throws IOException {
        try (ByteArrayInputStream is = new ByteArrayInputStream(src);
             ByteArrayOutputStream os = new ByteArrayOutputStream();
             ImageOutputStream ios = ImageIO.createImageOutputStream(os)) {
            BufferedImage srcImage = ImageIO.read(is);
            BufferedImage destImage = resizeImage(srcImage, scale);

            // 保存品質はユーザー指定に従う
            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);
            writer.setOutput(ios);
            writer.write(null, new IIOImage(destImage, null, null), param);
            writer.dispose();

            return os.toByteArray();
        }
    }
}
