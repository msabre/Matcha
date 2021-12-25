package domain.entity;

import com.google.gson.annotations.Expose;
import domain.entity.model.OnlineStatus;

import java.time.ZonedDateTime;
import java.util.Date;

public class User {
    @Expose
    private int id;
    private boolean authorized;

    @Expose
    private String firstName;
    @Expose
    private String lastName;
    @Expose
    private String middleName;

    @Expose
    private Integer yearsOld;
    @Expose
    private String location;
    @Expose
    private UserCard card;

    @Expose
    private String email;
    @Expose
    private String userName;
    @Expose
    private Date birthday;
    @Expose
    private OnlineStatus.Status status;
    @Expose
    private ZonedDateTime lastAction;
    
    @Expose
    private boolean banned;
    
    private int fakePoints;
    private String password;
    private String tokenConfirm;
    private boolean confirm;

    @Expose
    private FilterParams filter;

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Integer getYearsOld() {
        return yearsOld;
    }

    public void setYearsOld(Integer yearsOld) {
        this.yearsOld = yearsOld;
    }

    public String getTokenConfirm() {
        return tokenConfirm;
    }

    public void setTokenConfirm(String tokenConfirm) {
        this.tokenConfirm = tokenConfirm;
    }

    public boolean isConfirm() {
        return confirm;
    }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UserCard getCard() {
        return card;
    }

    public void setCard(UserCard card) {
        this.card = card;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public FilterParams getFilter() {
        return filter;
    }

    public void setFilter(FilterParams filter) {
        this.filter = filter;
    }

    public OnlineStatus.Status getStatus() {
        return status;
    }

    public void setStatus(OnlineStatus.Status status) {
        this.status = status;
    }

    public ZonedDateTime getLastAction() {
        return lastAction;
    }

    public void setLastAction(ZonedDateTime lastAction) {
        this.lastAction = lastAction;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public int isFakePoints() {
        return fakePoints;
    }

    public void setFakePoints(int fakePoints) {
        this.fakePoints = fakePoints;
    }
}
