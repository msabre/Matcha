package usecase.JDBC_lessons.second_lesson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        String url = "jdbc:mysql://localhost:3306";
        String username = "root";
        String password = "match";

        Class.forName("com.mysql.cj.jdbc.Driver");

        try (Connection connection = DriverManager.getConnection(url, username, password);
             BufferedReader sqlFile = new BufferedReader(new FileReader("C:\\Users\\Андрей\\Desktop\\myMatchproject\\src\\main\\java\\com\\JDBC_lessons\\books.sql"));
             Scanner scanner = new Scanner(sqlFile);
             Statement statement = connection.createStatement()) {
            System.out.println("Connection successful!");

            String line = "";
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                if (line.endsWith(";"))
                    line = line.substring(0, line.length() - 1);
                statement.executeUpdate(line);
            }

            ResultSet resultSet = null;

            try {
                resultSet = statement.executeQuery("select * from first_lesson.Books");
                while (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    String name = resultSet.getString(2);
                    double price = resultSet.getDouble(3);
                    System.out.println("id= " + id + " name= " + name + " price= " + price);
                }
            } catch (SQLException e ) {
                System.err.println("SqlException message: " + e.getMessage());
                System.err.println("SqlException SQL state: " + e.getSQLState());
                System.err.println("SqlException error:  " + e.getErrorCode());
            }
            finally {
                if (resultSet != null)
                    resultSet.close();
                else
                    System.err.println("Ошибка чтения данных с бд!");
            }
        }
    }
}
