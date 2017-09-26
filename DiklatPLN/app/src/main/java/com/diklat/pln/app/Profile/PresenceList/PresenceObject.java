package com.diklat.pln.app.Profile.PresenceList;

/**
 * Created by Fandy Aditya on 4/23/2017.
 */

public class PresenceObject {
    private String date;
    private String arrivalTime;
    private String arrivalMust;
    private String homeTime;
    private String homeMust;
    private String statusHome;
    private String statusArrival;
    private String statusLibur;
    private String statusCuti;

    public PresenceObject(String date, String arrivalTime, String arrivalMust, String homeTime, String homeMust, String statusHome, String statusArrival,String statusLibur,String statusCuti) {
        this.date = date;
        this.arrivalTime = arrivalTime;
        this.arrivalMust = arrivalMust;
        this.homeTime = homeTime;
        this.homeMust = homeMust;
        this.statusHome = statusHome;
        this.statusArrival = statusArrival;
        this.statusLibur = statusLibur;
        this.statusCuti = statusCuti;
    }

    public String getDate() {
        return date;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getArrivalMust() {
        return arrivalMust;
    }

    public String getHomeTime() {
        return homeTime;
    }

    public String getHomeMust() {
        return homeMust;
    }

    public String getStatusHome() {
        return statusHome;
    }

    public String getStatusArrival() {
        return statusArrival;
    }

    public String getStatusLibur() {
        return statusLibur;
    }

    public String getStatusCuti() {
        return statusCuti;
    }
}
