package domain.entity;

import domain.entity.model.types.Action;

import java.util.Date;

public class LikeAction {
    private int id;
    private Date creationTime;
    private int fromUsr;
    private int toUsr;
    private Action action;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public int getFromUsr() {
        return fromUsr;
    }

    public void setFromUsr(int fromUsr) {
        this.fromUsr = fromUsr;
    }

    public int getToUsr() {
        return toUsr;
    }

    public void setToUsr(int toUsr) {
        this.toUsr = toUsr;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
