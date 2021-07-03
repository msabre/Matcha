package domain.entity.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UserCard {
    private int id;
    private int userId;
    private String gender;
    private String sexual_preference;
    private String biography;
    private String workPlace;
    private String position;
    private String education;
    private int yearsOld;
    private double rating;
    private List<String> tags;
    private List<File> photos;

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSexual_preference() {
        return sexual_preference;
    }

    public void setSexual_preference(String sexual_preference) {
        this.sexual_preference = sexual_preference;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public List<String> getTags() {
        if (tags == null)
            tags = new ArrayList<>();
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<File> getPhotos() {
        if (photos == null)
            photos = new ArrayList<>();
        return photos;
    }

    public void setPhotos(List<File> photos) {
        this.photos = photos;
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

    public int getYearsOld() {
        return yearsOld;
    }

    public void setYearsOld(int yearsOld) {
        this.yearsOld = yearsOld;
    }
}
