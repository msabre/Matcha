package domain.entity;

public class FilterParams {
    private int id;
    private Integer ageBy;
    private Integer ageTo;
    private Double rating;
    private Integer commonTagsCount;

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
}
