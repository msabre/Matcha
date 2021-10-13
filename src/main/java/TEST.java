import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;
import java.util.Iterator;

public class TEST {
    public static void main(String[] args) throws Exception {
        byte[] fileBytes = getBase64("C:\\Users\\Андрей\\Desktop\\фронт.psd");
        System.out.println(new String(fileBytes));
//        compressImage(fileBytes, "psd", "Casd");


//        Properties props = MatchUtils.getProps(
//                Paths.get(DBConfiguration.class.getResource("/application.properties").toURI()).toFile().getPath());
//
//        byte[] decodeCode = Base64.getDecoder().decode(props.getProperty("JWT_KEY"));
//        SecretKey JWT_KEY = new SecretKeySpec(decodeCode, 0, decodeCode.length, "HmacSHA256");
    }

    private static void compressImage(byte[] content, String format, String destinationPath) throws IOException {
        ByteArrayInputStream bufferedInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(content));
//        File input = new File("C:\\Users\\Андрей\\Desktop\\фронт.psd");
        BufferedImage img = ImageIO.read(bufferedInputStream);

        File compressedImageFile = new File("destinationPath.jpg");
        OutputStream os =new FileOutputStream(compressedImageFile);

        Iterator<ImageWriter> writers =  ImageIO.getImageWritersByFormatName("jpg");
        ImageWriter writer = (ImageWriter) writers.next();

        ImageOutputStream ios = ImageIO.createImageOutputStream(os);
        writer.setOutput(ios);

        ImageWriteParam param = writer.getDefaultWriteParam();

        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(0.05f);
        writer.write( null , new IIOImage(img, null , null ), param);

        os.close();
        ios.close();
        writer.dispose();
    }

    private static byte[] getBase64(String path) {
        try {
            File file = new File(path);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] fileBytes = new byte[(int) file.length()];
            fileInputStream.read(fileBytes);
            fileInputStream.close();
            return Base64.getEncoder().encode(fileBytes);
        }catch (Exception e) {
            return null;
        }
    }
}
