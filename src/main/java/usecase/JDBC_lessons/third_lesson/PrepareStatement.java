package usecase.JDBC_lessons.third_lesson;

import java.sql.*;

public class PrepareStatement {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        String url = "jdbc:mysql://localhost:3306";
        String username = "root";
        String password = "match";

        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection connection = DriverManager.getConnection(url, username, password)) {

            System.out.println("Connection sucessful!");

            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO first_lesson.books(name, price) VALUES (?,?)");
            preparedStatement.setString(1, "Shindler's list");
            preparedStatement.setDouble(2, 29.65);
            preparedStatement.execute();

            ResultSet resultSet = null;
            try {
                Statement statement = connection.createStatement();
                statement.executeQuery("SELECT * FROM first_lesson.books");

                resultSet = statement.getResultSet();
                while (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    String name = resultSet.getString(2);
                    double price = resultSet.getDouble(3);

                    System.out.println("id= " + id + " name= " + name + " price= " + price);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (resultSet != null)
                    resultSet.close();
                else
                    System.out.println("Ошибка подключения к БД!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
