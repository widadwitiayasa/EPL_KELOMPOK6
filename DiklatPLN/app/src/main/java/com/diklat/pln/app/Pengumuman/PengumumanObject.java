package com.diklat.pln.app.Pengumuman;

/**
 * Created by Fandy Aditya on 5/13/2017.
 */

public class PengumumanObject {
    private String idPengumuman;
    private String judulPengumuman;
    private String tanggalPengumuman;
    private String deskripsiPengumuman;

    public PengumumanObject(String idPengumuman, String judulPengumuman,String tanggalPengumuman, String deskripsiPengumuman) {
        this.idPengumuman = idPengumuman;
        this.judulPengumuman = judulPengumuman;
        this.tanggalPengumuman = tanggalPengumuman;
        this.deskripsiPengumuman = deskripsiPengumuman;
    }

    public String getIdPengumuman() {
        return idPengumuman;
    }

    public String getJudulPengumuman() {
        return judulPengumuman;
    }

    public String getTanggalPengumuman() {
        return tanggalPengumuman;
    }

    public String getDeskripsiPengumuman() {
        return deskripsiPengumuman;
    }
}
