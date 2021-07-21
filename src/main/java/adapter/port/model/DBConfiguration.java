package adapter.port.model;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DBConfiguration {
    private static DBConfiguration instanse = null;

    private final static String PATH_TO_PROPERTIES
            = DBConfiguration.class.getResource("/databaseConfiguration.properties").getPath();

    private static String user;
    private static String password;
    private static String url;
    
    
    private DBConfiguration() {
    }

    public static DBConfiguration getConfig() {
        if (instanse != null)
            return instanse;

        FileInputStream fileInputStream;
        Properties prop = new Properties();

        try {
            fileInputStream = new FileInputStream(PATH_TO_PROPERTIES);
            prop.load(fileInputStream);

            user = prop.getProperty("user");
            password = prop.getProperty("password");
            url = prop.getProperty("url");

            instanse = new DBConfiguration();

            loadDriver();

            return instanse;

        } catch (IOException e) {
            System.out.println("Ошибка в программе: файл " + PATH_TO_PROPERTIES + " не обнаружено");
            e.printStackTrace();
        }
        return null;
    }

    private static void loadDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }
}
