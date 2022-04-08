package com.aariyan.pickingplan.Model;

public class AuthenticationModel {
    private int userID,locationID;
    private String pickingTeams;

    public AuthenticationModel(){}

    public AuthenticationModel(int userID, int locationID, String pickingTeams) {
        this.userID = userID;
        this.locationID = locationID;
        this.pickingTeams = pickingTeams;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getLocationID() {
        return locationID;
    }

    public void setLocationID(int locationID) {
        this.locationID = locationID;
    }

    public String getPickingTeams() {
        return pickingTeams;
    }

    public void setPickingTeams(String pickingTeams) {
        this.pickingTeams = pickingTeams;
    }
}
