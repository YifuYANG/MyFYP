package com.example.myfyp.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myfyp.IndexActivity;
import com.example.myfyp.vo.License;


public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context,"local_2.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("create Table license(deviceId Text primary key,driverId Text,password Text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("drop Table if exists license");
    }

    public Boolean insertUserInfo(String deviceId, String driverId,String password){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("deviceId",deviceId);
        contentValues.put("driverId",driverId);
        contentValues.put("password",password);
        Long result=DB.insert("license",null,contentValues);
        return result != -1;
    }

    public int getsize(){
        int i=0;
        Cursor cursor = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM license",null);
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            i=cursor.getInt(0);
        }
        return i;
    }

    public License getdatabydevice(String deviceId) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query("license", new String[] {"deviceId", "driverId","password"}, "deviceId" + "=?",
                new String[] {deviceId}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        return new License(cursor.getString(0),cursor.getString(1), cursor.getString(2));
    }

    public Boolean update(String deviceId, String driverId,String password){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("deviceId",deviceId);
        contentValues.put("driverId",driverId);
        contentValues.put("password",password);
        int result= DB.update("license",contentValues,"deviceId" + "=?",new String[]{deviceId});
        return result != -1;
    }
}
