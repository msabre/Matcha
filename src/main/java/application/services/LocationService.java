package application.services;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import java.io.File;

import java.io.IOException;
import java.net.InetAddress;

public class LocationService {

    public static String getPosition(String ip) throws IOException, GeoIp2Exception {

        String path = LocationService.class.getResource("/GeoLite2-City_20210427/GeoLite2-City.mmdb").getPath();
        File database = new File(path);

        if (!database.exists())
            return null;

        DatabaseReader reader = new DatabaseReader.Builder(database).build();

        InetAddress ipAddress = InetAddress.getByName(ip);

        CityResponse response = reader.city(ipAddress);

        return response.getCity().getName();
    }

}
