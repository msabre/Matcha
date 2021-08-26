package application.services;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.UUID;

public class MatchUtils {
    public static Properties getProps(String path) {
        Properties prop = new Properties();

        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(path);
            prop.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }

    public static String encodeASCII(String src) {
        final CharsetEncoder asciiEncoder = StandardCharsets.US_ASCII.newEncoder();
        final StringBuilder result = new StringBuilder();
        for (final Character character : src.toCharArray()) {
            if (asciiEncoder.canEncode(character)) {
                result.append(character);
            } else {
                result.append("\\u");
                result.append(Integer.toHexString(0x10000 | character).substring(1).toUpperCase());
            }
        }
        return StringEscapeUtils.unescapeJava(result.toString());
    }

    public static String getSlash() {
        String os = System.getProperty("os.name");
        return os.contains("Windows") ? "\\" : "/";
    }

    public static String generateRqUid() {
        return UUID.randomUUID().toString().replace("-", StringUtils.EMPTY);
    }
}
