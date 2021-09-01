package domain.entity;

import com.google.gson.annotations.Expose;
import domain.entity.model.types.Action;
import domain.entity.model.types.GenderType;
import domain.entity.model.types.SexualPreferenceType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserCard {
    private int id;
    private int userId;

    @Expose private String biography;
    @Expose private String workPlace;
    @Expose private String position;
    @Expose private String education;
    @Expose private double rating;
    @Expose private GenderType gender;
    @Expose private SexualPreferenceType sexualPreference;
    @Expose private List<String> tags;
    @Expose private List<Photo> photos;

    Map<Integer, Action> actionMap;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public GenderType getGender() {
        return gender;
    }

    public void setGender(GenderType gender) {
        this.gender = gender;
    }

    public SexualPreferenceType getSexualPreference() {
        return sexualPreference;
    }

    public void setSexualPreference(SexualPreferenceType sexualPreference) {
        this.sexualPreference = sexualPreference;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getWorkPlace() {
        return workPlace;
    }

    public void setWorkPlace(String workPlace) {
        this.workPlace = workPlace;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public Map<Integer, Action> getActionMap() {
        if (actionMap == null)
            return new HashMap<>();
        return actionMap;
    }

    public void setActionMap(Map<Integer, Action> actionMap) {
        this.actionMap = actionMap;
    }
}
