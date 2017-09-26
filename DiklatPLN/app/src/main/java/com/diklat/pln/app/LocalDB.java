package com.diklat.pln.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.diklat.pln.app.Pengumuman.PengumumanObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fandy Aditya on 5/4/2017.
 */

public class LocalDB extends SQLiteOpenHelper {

    private SQLiteDatabase myDb;
    private Context context;


    public LocalDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
        this.myDb = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("create table if not exists profile(" +
//                "id VARCHAR(5)," +
//                "nama VARCHAR(30),"+
//                "nip VARCHAR(30)," +
//                "sapid VARCHAR(30)," +
//                "born_date VARCHAR(30)," +
//                "blood_type VARCHAR(2)," +
//                "capeg_date VARCHAR(30)," +
//                "position VARCHAR(20)," +
//                "unit VARCHAR(30)," +
//                "organization VARCHAR(30)," +
//                "profpic VARCHAR(100));");
        db.execSQL("create table if not exists pengumuman(" +
                "id VARCHAR(5)," +
                "status_lihat VARCHAR(5));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists pengumuman;");
        onCreate(db);
    }

    public void insert(ContentValues contentValues){
        myDb.insert("pengumuman",null,contentValues);
    }
    public ContentValues showProfile(String id){
        Cursor cur = myDb.rawQuery("select * from profile where id='"+id+"'",null);
        ContentValues contentValues = new ContentValues();
        if(cur.getCount()>0){
            cur.moveToFirst();
            contentValues.put("nama",cur.getString(cur.getColumnIndex("nama")));
            contentValues.put("nip",cur.getString(cur.getColumnIndex("nip")));
            contentValues.put("sapid",cur.getString(cur.getColumnIndex("sapid")));
            contentValues.put("born_date",cur.getString(cur.getColumnIndex("born_date")));
            contentValues.put("blood_type",cur.getString(cur.getColumnIndex("blood_type")));
            contentValues.put("capeg_date",cur.getString(cur.getColumnIndex("capeg_date")));
            contentValues.put("position",cur.getString(cur.getColumnIndex("position")));
            contentValues.put("unit",cur.getString(cur.getColumnIndex("unit")));
            contentValues.put("organization",cur.getString(cur.getColumnIndex("organization")));
            contentValues.put("profpic",cur.getString(cur.getColumnIndex("profpic")));
        }
        cur.close();

        return contentValues;
    }
    public int showPengumumanStatus(){
        int returnVal=0;
        Cursor cur = myDb.rawQuery("select status_lihat from pengumuman",null);
        while (cur.moveToNext()){
            if(cur.getString(cur.getColumnIndex("status_lihat")).equals("0")){
                returnVal++;
            }
        }
        cur.close();
        return  returnVal;
    }
    public boolean checkId (String id){
        boolean returnVal;
        Cursor cur = myDb.rawQuery("select count(*) as jumlah from pengumuman where id='"+id+"'",null);
        cur.moveToFirst();
        returnVal = cur.getCount()<=0;
        cur.close();
        return returnVal;
    }
    public List<PengumumanObject> getPengumuman(){
        Cursor cur = myDb.rawQuery("select * from pengumuman",null);
        List<PengumumanObject> listData = new ArrayList<>();;
        while (cur.moveToNext()){
            listData.add(new PengumumanObject(cur.getString(cur.getColumnIndex("id")),"0","0","0"));
        }
        cur.close();
        return listData;
    }
    public void delete(String id){
        myDb.delete("pengumuman","id='"+id+"'",null);
    }
    public void update(ContentValues contentValues){
        myDb.update("pengumuman",contentValues,"id='"+contentValues.getAsString("id")+"'",null);
    }
}
