package testTemp;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ImageToString {
    public static void main(String[] args) throws IOException {
        // Obtain a WebP ImageReader instance
        ImageReader reader = ImageIO.getImageReadersByMIMEType("image/webp").next();
        ImageWriter writer = ImageIO.getImageWritersByMIMEType("image/webp").next();

        File file = new File(args[0]);

        ByteArrayOutputStream baos = new ByteArrayOutputStream(4000);
        reader.setInput(new FileImageInputStream(file));

        BufferedImage aa = reader.read(0);
        writer.setOutput(baos);

        writer.write(aa);

        byte[] content = baos.toByteArray();
        String result = new String(Base64.getEncoder().encode(content), StandardCharsets.UTF_8);
        System.out.println(result);


//        byte[] content = Files.readAllBytes(Paths.get(args[0]));
//        String result = new String(Base64.getEncoder().encode(content), StandardCharsets.UTF_8);
    }
}
