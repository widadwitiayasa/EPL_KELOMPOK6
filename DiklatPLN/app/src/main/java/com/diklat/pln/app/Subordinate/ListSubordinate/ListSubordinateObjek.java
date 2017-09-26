package com.diklat.pln.app.Subordinate.ListSubordinate;

/**
 * Created by Fandy Aditya on 4/29/2017.
 */

public class ListSubordinateObjek {
    private String id;
    private String name;
    private String position;
    private String nip;
    private String img;
    private String gender;

    public ListSubordinateObjek(String id, String name, String position,String nip, String img,String gender) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.nip = nip;
        this.img = img;
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public String getNip() {
        return nip;
    }

    public String getImg() {
        return img;
    }

    public String getGender() {
        return gender;
    }
}
