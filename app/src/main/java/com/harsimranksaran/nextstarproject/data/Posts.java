package com.harsimranksaran.nextstarproject.data;

public class Posts {

    public String date;
    public String description;
    public String postimage;
    public String profilepic;
    public String time;
    public String uid;
    public String username;
    public String postvideo;

    public Posts(String date, String description, String postimage, String profilepic, String time, String uid, String username, String postvideo) {
        this.date = date;
        this.description = description;
        this.postimage = postimage;
        this.profilepic = profilepic;
        this.time = time;
        this.uid = uid;
        this.username = username;
        this.postvideo = postvideo;
    }

    public String getPostvideo() {

        return postvideo;
    }

    public void setPostvideo(String postvideo) {
        this.postvideo = postvideo;
    }

    public Posts(){

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Posts(String date, String description, String postimage, String profilepic, String time, String uid, String username) {

        this.date = date;
        this.description = description;
        this.postimage = postimage;
        this.profilepic = profilepic;
        this.time = time;
        this.uid = uid;
        this.username = username;
    }
}
