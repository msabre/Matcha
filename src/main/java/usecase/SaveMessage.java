package usecase;

import domain.entity.Message;
import domain.entity.model.types.MessageStatus;
import domain.entity.model.types.MessageType;
import usecase.port.MessageRepository;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

import static com.sun.imageio.plugins.jpeg.JPEG.JPG;

public class SaveMessage {
    private static final List<String> compressRequiredFormats = Arrays.asList("png", "tiff", "psd", "bmp", "hdr", "jpeg", "tga", "webp", "sgi");

    private final MessageRepository repository;

    public SaveMessage(MessageRepository repository) {
        this.repository = repository;
    }

    public Message save(Message msg) {
        if (msg.getType() == MessageType.IMAGE) {
            byte[] byteContent = msg.getContent().getBytes();
            if (compressRequiredFormats.contains(msg.getTypeInfo())) {
                try {
                    ByteArrayInputStream bufferedInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(byteContent));
                    BufferedImage img = ImageIO.read(bufferedInputStream);

                    final ByteArrayOutputStream os = new ByteArrayOutputStream();
                    ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(os);

                    Iterator<ImageWriter> writers =  ImageIO.getImageWritersByFormatName("jpg");
                    ImageWriter writer = writers.next();
                    ImageWriteParam param = writer.getDefaultWriteParam();
                    writer.setOutput(imageOutputStream);

                    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    param.setCompressionQuality(0.05f);
                    writer.write( null , new IIOImage(img, null , null ), param);
                    msg.setContent(Base64.getEncoder().encodeToString(os.toByteArray()));

                    os.close();
                    writer.dispose();
                    imageOutputStream.close();
                }
                catch (final IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return repository.save(msg);
    }
}
