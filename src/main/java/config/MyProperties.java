package config;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.Properties;

public class MyProperties {
    public static final int SALT_BYTES_COUNT;

    public static final String ADMIN_LOGIN;

    public static final String ADMIN_PASSWORD;

    public static final SecretKey JWT_KEY;

    public static final String IMAGES_PATH;

    public static final int COUNT_RECCOMENDED_USERS_LIST_SIZE;

    public static final double RATING_STEP;

    public static final double RATING_FALSITY;

    static {
        SALT_BYTES_COUNT = 20;

        ADMIN_LOGIN = "mathcaPatchaJava@gmail.com";

        ADMIN_PASSWORD = "matcha12345";

        IMAGES_PATH = "C:\\Users\\Андрей\\Desktop\\images\\";

        byte[] decodeCode = Base64.getDecoder().decode("4pslXPLYPdRVykZVlonLV+Re4Q8gD3Y/B2ymm/MAZAw=");

        JWT_KEY = new SecretKeySpec(decodeCode, 0,
                decodeCode.length, "HmacSHA256");

        COUNT_RECCOMENDED_USERS_LIST_SIZE = 10;

        RATING_STEP = 0.1;

        RATING_FALSITY = 0.75;
    }
}
