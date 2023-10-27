package com.harsimranksaran.nextstarproject.data;

public class FindFriends {

    public String username, profileimage;

    public FindFriends(){

    }

    public FindFriends(String username, String profileimage) {
        this.username = username;
        this.profileimage = profileimage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }
}
