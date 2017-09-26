package com.diklat.pln.app;

/**
 * Created by Fandy Aditya on 6/7/2017.
 */

public class CutiObject {

    private String id;
    private String namaCuti;
    private String minLapor;
    private String maxLaport;
    private String sisa;
    private String keterangan;
    private String flag;

    public CutiObject(String id, String namaCuti, String minLapor, String maxLaport, String sisa, String keterangan,String flag) {
        this.id = id;
        this.namaCuti = namaCuti;
        this.minLapor = minLapor;
        this.maxLaport = maxLaport;
        this.sisa = sisa;
        this.keterangan = keterangan;
        this.flag = flag;
    }

    public String getId() {
        return id;
    }

    public String getNamaCuti() {
        return namaCuti;
    }

    public String getMinLapor() {
        return minLapor;
    }

    public String getMaxLaport() {
        return maxLaport;
    }

    public String getSisa() {
        return sisa;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public String getFlag() {
        return flag;
    }

}
