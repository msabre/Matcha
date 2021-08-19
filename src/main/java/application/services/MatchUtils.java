package application.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

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
}
