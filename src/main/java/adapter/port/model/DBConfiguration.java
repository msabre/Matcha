package adapter.port.model;

import application.services.MatchUtils;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Properties;

public class DBConfiguration {
    private static DBConfiguration instanse = null;

    private static String PATH_TO_PROPERTIES = "";

    static {
        try {
            PATH_TO_PROPERTIES = Paths.get(DBConfiguration.class.getResource("/databaseConfiguration.properties").toURI()).toFile().getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static String user;
    private static String password;
    private static String url;
    
    
    private DBConfiguration() {
    }

    public static DBConfiguration getConfig() {
        if (instanse != null)
            return instanse;
        Properties prop = MatchUtils.getProps(PATH_TO_PROPERTIES);
        user = prop.getProperty("db.mysql.user");
        password = prop.getProperty("db.mysql.password");
        url = prop.getProperty("db.mysql.url");
        instanse = new DBConfiguration();

        loadDriver();
        return instanse;
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
