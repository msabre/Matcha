package adapter.port.model;

import application.services.MatchUtils;
import domain.entity.User;
import domain.entity.model.types.CityType;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class LocationTimeZoneUTC {
    private static LocationTimeZoneUTC instance;
    private static String PATH_TO_PROPERTIES;

    private final Map<CityType, String> zoneDictionary;

    static {
        try {
            PATH_TO_PROPERTIES = Paths.get(DBConfiguration.class.getResource("/timeZone.properties").toURI()).toFile().getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private LocationTimeZoneUTC() {
        zoneDictionary = new HashMap<>();
        Properties prop = MatchUtils.getProps(PATH_TO_PROPERTIES);
        prop.stringPropertyNames().forEach(name -> zoneDictionary.put(CityType.fromValue(name), prop.getProperty(name)));
    }

    public ZoneId getZoneIdByCity(String city) {
        if (city == null)
            return null;
        String utc = getZoneDictionary().get(Optional.ofNullable(CityType.fromValue(city)).orElse(CityType.MOSCOW));
        return ZoneId.of("UTC" + utc);
    }

    public ZonedDateTime leadToZoneId(ZonedDateTime zonedDateTime, ZoneId zoneId) {
        try {
            return zonedDateTime.withZoneSameInstant(zoneId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zonedDateTime;
    }

    public static LocationTimeZoneUTC getInstance() {
        if (instance == null) {
            instance = new LocationTimeZoneUTC();
        }
        return instance;
    }

    public Map<CityType, String> getZoneDictionary() {
        return zoneDictionary;
    }
}
