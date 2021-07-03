package domain.entity.model;

import javafx.util.Pair;

public class FilterParams {
    private Pair<Integer, Integer> age;
    private Integer rating;
    private Integer commonTagsCount;

    public Pair<Integer, Integer> getAge() {
        return age;
    }

    public void setAge(Pair<Integer, Integer> age) {
        this.age = age;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Integer getCommonTagsCount() {
        return commonTagsCount;
    }

    public void setCommonTagsCount(Integer commonTagsCount) {
        this.commonTagsCount = commonTagsCount;
    }
}
