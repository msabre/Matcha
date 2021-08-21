package domain.entity;

import com.google.gson.annotations.Expose;

public class FilterParams {
    private int id;
    @Expose
    private Integer ageBy;
    @Expose
    private Integer ageTo;
    @Expose
    private Double rating;
    @Expose
    private Integer commonTagsCount;
    @Expose
    private String location;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getAgeBy() {
        return ageBy;
    }

    public void setAgeBy(Integer ageBy) {
        this.ageBy = ageBy;
    }

    public Integer getAgeTo() {
        return ageTo;
    }

    public void setAgeTo(Integer ageTo) {
        this.ageTo = ageTo;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getCommonTagsCount() {
        return commonTagsCount;
    }

    public void setCommonTagsCount(Integer commonTagsCount) {
        this.commonTagsCount = commonTagsCount;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
