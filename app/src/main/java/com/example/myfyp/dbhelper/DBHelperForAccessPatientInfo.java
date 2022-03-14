package com.example.myfyp.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.example.myfyp.vo.LoginformToAccessGetPatientInfoServer;

public class DBHelperForAccessPatientInfo extends SQLiteOpenHelper {
    public DBHelperForAccessPatientInfo(Context context) {
        super(context,"local_4.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("create Table userinfo(deviceId Text primary key,password Text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("drop Table if exists userinfo");
    }

    public Boolean insertUserInfo(String deviceId, String password){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("deviceId",deviceId);
        contentValues.put("password",password);
        Long result=DB.insert("userinfo",null,contentValues);
        return result != -1;
    }


    public LoginformToAccessGetPatientInfoServer getdatabydevice(String deviceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query("userinfo", new String[] {"deviceId","password"}, "deviceId" + "=?",
                new String[] {deviceId}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        return new LoginformToAccessGetPatientInfoServer(cursor.getString(0),cursor.getString(1));
    }

    public Boolean update(String deviceId, String password){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("deviceId",deviceId);
        contentValues.put("password",password);
        int result= DB.update("userinfo",contentValues,"deviceId" + "=?",new String[]{deviceId});
        return result != -1;
    }
}
