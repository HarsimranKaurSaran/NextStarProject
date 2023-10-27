package com.harsimranksaran.nextstarproject.data;

public class Comment {

    String uid;

    public Comment(String uid, String username, String comment, String userimage) {
        this.uid = uid;
        this.username = username;
        this.comment = comment;
        this.userimage = userimage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    String username;
    String comment;
    String userimage;

    public Comment(){

    }

    public Comment(String username, String comment, String userimage) {
        this.username = username;
        this.comment = comment;
        this.userimage = userimage;
    }

    public String getUsername() {

        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }
}
