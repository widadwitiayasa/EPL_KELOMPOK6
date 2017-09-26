package com.diklat.pln.app;

import com.diklat.pln.app.Inbox.ListMessage.ListMessageObject;
import com.diklat.pln.app.Pengumuman.PengumumanObject;
import com.diklat.pln.app.Profile.PresenceList.PresenceObject;
import com.diklat.pln.app.Subordinate.ListSubordinate.ListSubordinateObjek;
import com.diklat.pln.app.Subordinate.ListSubordinate.MagangObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fandy Aditya on 4/21/2017.
 */

public class ParseJson {
    private String json;

    public ParseJson(String json) {
        this.json = json;
    }

    public String parseLogin(){
        JSONObject  jObject;
        String parseVal = null;
        try {
            jObject = new JSONObject(json);
            parseVal = jObject.getString("msg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseVal;
    }

    public String parseExpiredTokenTime(){
        JSONObject jsonObject;
        String parseVal = null;

        try {
            jsonObject = new JSONObject(json);
            parseVal = jsonObject.getString("expiredTime");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return parseVal;
    }
    public String parseToken(){
        JSONObject jsonObject;
        String parseVal = null;
        try {
            jsonObject = new JSONObject(json);
            parseVal = jsonObject.getString("id_token");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseVal;
    }
    public List<String> parseProfile(){
        JSONObject jsonObject;
        List<String>parseVal = new ArrayList<>();
        try {
            jsonObject = new JSONObject(json);
            JSONObject data = jsonObject.getJSONObject("data");
            parseVal.add(data.getString("id"));
            parseVal.add(data.getString("name"));
            parseVal.add(data.getString("nip"));
            parseVal.add(data.getString("sap_id"));
            parseVal.add(data.getString("birthday"));
            parseVal.add(data.getString("bloodtype"));
            parseVal.add(data.getString("capeg_date"));
            parseVal.add(data.getString("position_name"));
            parseVal.add(data.getString("unit_name"));
            parseVal.add(data.getString("organization_name"));
            parseVal.add(data.getString("photo"));
            parseVal.add(data.getString("user"));
            parseVal.add(data.getString("gender"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return parseVal;
    }
    public List<ListSubordinateObjek> listSubParse(){
        JSONObject jsonObject;
        List<ListSubordinateObjek> parseval = new ArrayList<>();
        try {
            jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("DATA");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jRow = jsonArray.getJSONObject(i);
                ListSubordinateObjek listSubordinateObjek = new ListSubordinateObjek(jRow.getString("id"),
                        jRow.getString("name"),
                        jRow.getString("position_name"),
                        jRow.getString("nip"),
                        jRow.getString("photo"),
                        jRow.getString("gender"));
                parseval.add(listSubordinateObjek);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseval;
    };

    public List<PengumumanObject> listPengumumanParse(){
        JSONObject jsonObject;
        List<PengumumanObject> parseval = new ArrayList<>();
        try {
            jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("DATA");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jRow = jsonArray.getJSONObject(i);
                PengumumanObject pengumumanObject = new PengumumanObject(jRow.getString("ID"),
                        jRow.getString("JUDUL"),
                        jRow.getString("START"),
                        jRow.getString("DATA"));
                parseval.add(pengumumanObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseval;
    }
    public List<CutiObject> parseCuti(){
        List<CutiObject>cutiObjectList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray data = jsonObject.getJSONArray("DATA");
            for (int i=0;i<data.length();i++){
                JSONObject jRow = data.getJSONObject(i);
                CutiObject cutiObject = new CutiObject(jRow.getString("ID"),
                        jRow.getString("NAME"),
                        jRow.getString("MAX_LAPOR"),
                        jRow.getString("MIN_LAPOR"),
                        jRow.getString("SISA"),
                        jRow.getString("KET"),
                        jRow.getString("FLAG"));
                cutiObjectList.add(cutiObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cutiObjectList;
    }
    public List<String> parseAjukanCuti(){
        List<String> returnVal = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            returnVal.add(jsonObject.getString("msg"));
            returnVal.add(jsonObject.getString("DATA"));
            returnVal.add(jsonObject.getString("TAMBAHAN"));
            returnVal.add(jsonObject.getString("JUMLAH"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return returnVal;
    }
    public List<String> parseSubmitCuti(){
        List<String> returnval = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            returnval.add(jsonObject.getString("msg"));
            returnval.add(jsonObject.getString("DATA"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnval;
    }
    public List<ListMessageObject> parseInbox(){
        List<ListMessageObject> returnVal = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray data = jsonObject.getJSONArray("DATA");
            for (int i=0;i<data.length();i++){
                JSONObject jRow =data.getJSONObject(i);
                ListMessageObject listMessageObject = new ListMessageObject(jRow.getString("MSG_ID"),
                        jRow.getString("ID"),
                        jRow.getString("JENIS"),
                        jRow.getString("NAMA"),
                        jRow.getString("KET"),
                        jRow.getString("FROM"),
                        jRow.getString("FROM_PHOTO"),
                        jRow.getString("TO"),
                        jRow.getString("TO_PHOTO"),
                        jRow.getString("TGL"),
                        jRow.getString("DARI"),
                        jRow.getString("SAMPAI"),
                        jRow.getString("LIHAT"),
                        jRow.getString("TIME"),
                        jRow.getString("PESAN"),
                        jRow.getString("DIAJUKAN"),
                        jRow.getString("DISETUJUI"),
                        jRow.getString("DURASI"),
                        jRow.getString("HARI"));

                returnVal.add(listMessageObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnVal;
    }
    public List<ListMessageObject> parseOutbox(){
        List<ListMessageObject> returnVal = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray data = jsonObject.getJSONArray("DATA");
            for (int i=0;i<data.length();i++){
                JSONObject jRow = data.getJSONObject(i);
                ListMessageObject listMessageObject = new ListMessageObject("",jRow.getString("ID"),
                        "",
                        jRow.getString("NAME"),
                        jRow.getString("STATUS"),
                        jRow.getString("CREATE"),
                        "",
                        jRow.getString("TO"),
                        "",
                        jRow.getString("APPROVE_DATE"),
                        jRow.getString("START"),
                        jRow.getString("END"),
                        "",
                        jRow.getString("TIME"),
                        jRow.getString("PESAN"),
                        "",
                        "",
                        jRow.getString("DURASI"),
                        jRow.getString("HARI"));
                returnVal.add(listMessageObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnVal;
    }
    public List<PresenceObject> parsePresence(){
        List<PresenceObject> returnVal = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray data = jsonObject.getJSONArray("DATA");
            for(int i = 0;i<data.length();i++){
                JSONObject jRow = data.getJSONObject(i);
                PresenceObject presenceObject = new PresenceObject(jRow.getString("TGL"),
                        jRow.getString("DATANG"),
                        jRow.getString("WAJIB_DATANG"),
                        jRow.getString("PULANG"),
                        jRow.getString("WAJIB_PULANG"),
                        jRow.getString("KETERANGAN_PULANG"),
                        jRow.getString("KETERANGAN_DATANG"),
                        jRow.getString("LIBUR"),
                        jRow.getString("CUTI"));
                returnVal.add(presenceObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnVal;
    }
    public List<ListSubordinateObjek> parseMagang(){
        JSONObject jsonObject;
        List<ListSubordinateObjek> parseval = new ArrayList<>();
        try {
            jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("DATA");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jRow = jsonArray.getJSONObject(i);
                ListSubordinateObjek listSubordinateObjek = new ListSubordinateObjek(jRow.getString("ID"),
                        jRow.getString("NAMA"),
                        jRow.getString("TYPE"),
                        jRow.getString("REGISTRASI"),
                        jRow.getString("PHOTO"),
                        jRow.getString("GENDER"));
                parseval.add(listSubordinateObjek);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseval;
    }
    public List<String> parseProfileMagang(){
        JSONObject jsonObject;
        List<String>parseVal = new ArrayList<>();
        try {
            jsonObject = new JSONObject(json);
            JSONArray data = jsonObject.getJSONArray("DATA");
            JSONObject jRow = data.getJSONObject(0);
            parseVal.add(jRow.getString("ID"));
            parseVal.add(jRow.getString("NAMA"));
            parseVal.add(jRow.getString("BIRTHDAY"));
            parseVal.add(jRow.getString("GENDER"));
            parseVal.add(jRow.getString("BLOODTYPE"));
            parseVal.add(jRow.getString("START"));
            parseVal.add(jRow.getString("END"));
            parseVal.add(jRow.getString("HEAD"));
            parseVal.add(jRow.getString("RELIGION"));
            parseVal.add(jRow.getString("REGISTRASI"));
//            parseVal.add(data.getString("position_name"));
//            parseVal.add(data.getString("unit_name"));
//            parseVal.add(data.getString("organization_name"));
            parseVal.add(jRow.getString("PHOTO"));
            parseVal.add(jRow.getString("TYPE"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseVal;
    }
    public List<MagangObject> parseMagangObj(){
        List<MagangObject> returnVal = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("DATA");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jRow = jsonArray.getJSONObject(i);
                MagangObject magangObject = new MagangObject(jRow.getString("id"),
                        jRow.getString("nama"));
                returnVal.add(magangObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnVal;
    }

    public List<UnitObject> parseUnit() {
        List<UnitObject> returnVal = new ArrayList<>();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("DATA");
            for(int i=0; i<jsonArray.length(); i++) {
                JSONObject jRow = jsonArray.getJSONObject(i);
                UnitObject unitObject = new UnitObject(jRow.getString("ID"),
                        jRow.getString("CODE"),
                        jRow.getString("NAME"));
                returnVal.add(unitObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnVal;
    }
}
