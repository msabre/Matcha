package application.services;

import adapter.port.model.DBConfiguration;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import java.io.File;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.nio.file.Paths;

public class LocationService {

    public static String getPosition(String ip) throws IOException, GeoIp2Exception {

        String path = null;
        try {
            path = Paths.get(DBConfiguration.class.getResource("/GeoLite2-City/GeoLite2-City.mmdb").toURI()).toFile().getPath();
        } catch (URISyntaxException e) {
            return null;
        }

        File database = new File(path);

        if (!database.exists())
            return null;

        DatabaseReader reader = new DatabaseReader.Builder(database).build();

        InetAddress ipAddress = InetAddress.getByName(ip);

        CityResponse response = reader.city(ipAddress);

        return response.getCity().getName();
    }

}
