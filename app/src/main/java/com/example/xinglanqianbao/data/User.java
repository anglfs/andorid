package com.example.xinglanqianbao.data;
/**
 * Created by 李福森 2020/1/08
 */
public class User {
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String mobile;            //用户名
    private int id;        //id
    private String appUserId ;
    private String image ;

    public String getAppUserId() {
        return appUserId;
    }

    public void setAppUserId(String appUserId) {
        this.appUserId = appUserId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "User{" +
                "mobile='" + mobile + '\'' +
                ", id=" + id +
                ", appUserId='" + appUserId + '\'' +
                ", image='" + image + '\'' +
                '}';
    }

    public User(String mobile, String appUserId, String image ,int id) {
        this.mobile = mobile;
        this.appUserId = appUserId;
        this.image = image;
        this.id = id;
    }

}


