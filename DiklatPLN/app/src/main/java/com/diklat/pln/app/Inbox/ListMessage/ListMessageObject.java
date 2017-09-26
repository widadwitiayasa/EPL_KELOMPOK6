package com.diklat.pln.app.Inbox.ListMessage;

/**
 * Created by Fandy Aditya on 4/29/2017.
 */

public class ListMessageObject {
    private String idMsgId;
    private String idListMessage;
    private String subjek;
    private String name;
    private String ket;
    private String from;
    private String fromPhoto;
    private String to;
    private String toPhoto;
    private String date;
    private String dari;
    private String sampai;
    private String statusLihat;
    private String time;
    private String notes;
    private String diajukanTgl;
    private String diterimaTgl;
    private String durasi;
    private String hari;
//    private String profPic;
    // 6 & `10

    public ListMessageObject(String idMsgId,String idListMessage, String subjek, String name, String ket, String from,
                             String fromPhoto, String to,String toPhoto,
                             String date, String dari, String sampai,String statusLihat,String time,
                             String notes,String diajukanTgl,String diterimaTgl,String durasi,String hari) {
        this.idMsgId = idMsgId;
        this.idListMessage = idListMessage;
        this.subjek = subjek;
        this.name = name;
        this.ket = ket;
        this.from = from;
        this.fromPhoto = fromPhoto;
        this.to = to;
        this.toPhoto = toPhoto;
        this.date = date;
        this.dari = dari;
        this.sampai = sampai;
        this.statusLihat = statusLihat;
        this.time = time;
        this.notes = notes;
        this.diajukanTgl = diajukanTgl;
        this.diterimaTgl = diterimaTgl;
        this.durasi = durasi;
        this.hari = hari;
    }

    public String getFromPhoto() {
        return fromPhoto;
    }

    public String getToPhoto() {
        return toPhoto;
    }

    public String getIdMsgId() {
        return idMsgId;
    }

    public String getIdListMessage() {
        return idListMessage;
    }

    public String getSubjek() {
        return subjek;
    }

    public String getName() {
        return name;
    }

    public String getKet() {
        return ket;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getDate() {
        return date;
    }

    public String getDari() {
        return dari;
    }

    public String getSampai() {
        return sampai;
    }

    public String getStatusLihat() {
        return statusLihat;
    }

    public String getTime() {
        return time;
    }

    public String getNotes() {
        return notes;
    }

    public String getDiajukanTgl() {
        return diajukanTgl;
    }

    public String getDiterimaTgl() {
        return diterimaTgl;
    }

    public String getDurasi() {
        return durasi;
    }

    public String getHari() {
        return hari;
    }
}
