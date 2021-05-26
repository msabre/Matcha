package usecase.JDBC_lessons.third_lesson;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.*;

public class BlobMain {
    public static void main(String[] args) throws ClassNotFoundException, IOException {
        String url = "jdbc:mysql://localhost:3306";
        String username = "root";
        String password = "match";

        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement state = connection.createStatement()) {

            state.executeUpdate("CREATE TABLE IF NOT EXISTS first_lesson.images (id INT NOT NULL AUTO_INCREMENT, creating_time DATE, name VARCHAR(30), image BLOB, PRIMARY KEY(id))");

            BufferedImage image = ImageIO.read(new File("src\\resourses\\smile.png"));
            Blob smileImg = connection.createBlob();
            try (OutputStream outputStream = smileImg.setBinaryStream(1)) {
                ImageIO.write(image, "png", outputStream);
            }

            PreparedStatement prepState = connection.prepareStatement("INSERT INTO first_lesson.images (creating_time, name, image) VALUES (?,?,?)");
            prepState.setDate(1, Date.valueOf("2021-03-26"));
            prepState.setString(2,"smile");
            prepState.setBlob(3, smileImg);
            prepState.execute();


            state.executeQuery("SELECt * FROM first_lesson.images");
            ResultSet resultSet = null;
            try {
                resultSet = state.getResultSet();
                while (resultSet.next()) {
                    Blob blobImg = resultSet.getBlob("image");
                    BufferedImage newImage = ImageIO.read(blobImg.getBinaryStream());
                    File outputFile = new File("src\\resourses\\image.png");
                    ImageIO.write(newImage, "png", outputFile);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (resultSet != null)
                    resultSet.close();
                else
                    System.err.println("Ошибка подключения к БД!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}