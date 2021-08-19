package config;

import application.services.MatchUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.filechooser.FileSystemView;
import java.util.Base64;
import java.util.Properties;

public class MyProperties {
    public static final int SALT_BYTES_COUNT;
    public static final String ADMIN_LOGIN;
    public static final String ADMIN_PASSWORD;
    public static final SecretKey JWT_KEY;
    public static final String IMAGES_PATH;
    public static final int USERS_LIST_SIZE;
    public static final double RATING_STEP;
    public static final double RATING_FALSITY;

    static {
        Properties props = MatchUtils.getProps("");

        SALT_BYTES_COUNT = Integer.parseInt(props.getProperty("SALT_BYTES_COUNT"));
        ADMIN_LOGIN = props.getProperty("ADMIN_LOGIN");
        ADMIN_PASSWORD = props.getProperty("ADMIN_PASSWORD");
        IMAGES_PATH = props.getProperty("IMAGES_PATH");
        USERS_LIST_SIZE = Integer.parseInt(props.getProperty("USERS_LIST_SIZE"));
        RATING_STEP = Double.parseDouble(props.getProperty("RATING_STEP"));
        RATING_FALSITY = Double.parseDouble(props.getProperty("RATING_FALSITY"));

        byte[] decodeCode = Base64.getDecoder().decode(props.getProperty("JWT_KEY"));
        JWT_KEY = new SecretKeySpec(decodeCode, 0,
                decodeCode.length, "HmacSHA256");
    }
}
