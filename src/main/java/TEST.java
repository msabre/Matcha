import adapter.port.model.DBConfiguration;
import application.services.MatchUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Properties;

public class TEST {
    public static void main(String[] args) throws URISyntaxException {
        Properties props = MatchUtils.getProps(
                Paths.get(DBConfiguration.class.getResource("/application.properties").toURI()).toFile().getPath());

        byte[] decodeCode = Base64.getDecoder().decode(props.getProperty("JWT_KEY"));
        SecretKey JWT_KEY = new SecretKeySpec(decodeCode, 0, decodeCode.length, "HmacSHA256");
    }
}
