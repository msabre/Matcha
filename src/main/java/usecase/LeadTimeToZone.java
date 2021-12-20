package usecase;

import adapter.port.model.LocationTimeZoneUTC;
import application.services.MatchUtils;
import config.MyProperties;
import domain.entity.Photo;
import domain.entity.User;
import domain.entity.model.OnlineStatus;
import domain.entity.model.types.CityType;
import usecase.port.UserRepository;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.util.Base64;
import java.util.List;

public class LeadTimeToZone {

    private final LocationTimeZoneUTC locationTimeZoneUTC;

    public LeadTimeToZone(LocationTimeZoneUTC locationTimeZoneUTC) {
        this.locationTimeZoneUTC = locationTimeZoneUTC;
    }
    
    public void lastActionToLocationTimeUser(List<User> users, String location) {
        ZoneId zoneId = ZoneId.of(locationTimeZoneUTC.getZoneDictionary().get(CityType.fromValue(location)));
        users.forEach(user -> user.setLastAction(locationTimeZoneUTC.leadToZoneId(user.getLastAction(), zoneId)));
    }

    public void lastActionToLocationTimeStatus(List<OnlineStatus> statusList, String location) {
        ZoneId zoneId = ZoneId.of(locationTimeZoneUTC.getZoneDictionary().get(CityType.fromValue(location)));
        statusList.forEach(status -> status.setLastAction(locationTimeZoneUTC.leadToZoneId(status.getLastAction(), zoneId)));
    }
}
