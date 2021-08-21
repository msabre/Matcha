package config;

import adapter.port.model.DBConfiguration;
import application.services.MatchUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Properties;

public class MyProperties {
    public static int SALT_BYTES_COUNT = 20;
    public static String ADMIN_LOGIN = "mathcaPatchaJava@gmail.com";
    public static String ADMIN_PASSWORD = "matcha12345";
    public static SecretKey JWT_KEY
            = new SecretKeySpec(Base64.getDecoder().decode("4pslXPLYPdRVykZVlonLV+Re4Q8gD3Y/B2ymm/MAZAw="), 0, 32, "HmacSHA256");
    public static String IMAGES_PATH = "/Users/a19184580/Desktop/images";
    public static int USERS_LIST_SIZE = 20;
    public static double RATING_STEP = 0.2;
    public static double RATING_FALSITY = 0.75;
    public static String CLIENT_HOST = "localhost:3000";

    static {
        try {
            Properties props = MatchUtils.getProps(
                    Paths.get(DBConfiguration.class.getResource("/application.properties").toURI()).toFile().getPath());
            SALT_BYTES_COUNT = Integer.parseInt(props.getProperty("SALT_BYTES_COUNT"));
            ADMIN_LOGIN = props.getProperty("ADMIN_LOGIN");
            ADMIN_PASSWORD = props.getProperty("ADMIN_PASSWORD");

            String imagePath = props.getProperty("IMAGES_PATH");
            if (Files.exists(Paths.get(imagePath)))
                IMAGES_PATH = imagePath;

            USERS_LIST_SIZE = Integer.parseInt(props.getProperty("USERS_LIST_SIZE"));
            RATING_STEP = Double.parseDouble(props.getProperty("RATING_STEP"));
            RATING_FALSITY = Double.parseDouble(props.getProperty("RATING_FALSITY"));

            byte[] decodeCode = Base64.getDecoder().decode(props.getProperty("JWT_KEY"));
            JWT_KEY = new SecretKeySpec(decodeCode, 0,
                    decodeCode.length, "HmacSHA256");

            CLIENT_HOST = props.getProperty("CLIENT_HOST");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
