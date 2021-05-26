package domain.entity;

import com.google.gson.annotations.Expose;

import java.io.File;
import java.util.List;

public class UserCard {
    private int id;
    private int userId;

    @Expose private String gender;
    @Expose private String sexual_preference;
    @Expose private String biography;
    @Expose private String workPlace;
    @Expose private String position;
    @Expose private String education;
    @Expose private int yearsOld;
    @Expose private double rating;
    @Expose private List<String> tags;

    private List<File> photos;
    private List<Integer> likes;
    private List<Integer> dislikes;

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

    public int getYearsOld() {
        return yearsOld;
    }

    public void setYearsOld(int yearsOld) {
        this.yearsOld = yearsOld;
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

    public List<File> getPhotos() {
        return photos;
    }

    public void setPhotos(List<File> photos) {
        this.photos = photos;
    }

    public List<Integer> getLikes() {
        return likes;
    }

    public void setLikes(List<Integer> likes) {
        this.likes = likes;
    }

    public List<Integer> getDislikes() {
        return dislikes;
    }

    public void setDislikes(List<Integer> dislikes) {
        this.dislikes = dislikes;
    }
}
