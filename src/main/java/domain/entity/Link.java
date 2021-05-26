package domain.entity;

import com.google.gson.annotations.Expose;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Link {
    private String url;
    private Integer id;
    private boolean open;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

}
