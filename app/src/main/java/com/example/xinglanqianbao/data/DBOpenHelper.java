package com.example.xinglanqianbao.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
/**
 * Created by 李福森 2020/1/08
 */

public class DBOpenHelper extends SQLiteOpenHelper {
    private SQLiteDatabase db;
    public DBOpenHelper(Context context){
        super(context,"db_xl",null,1);
        db = getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE IF NOT EXISTS user(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "mobile TEXT," +
                "appUserId TEXT," +
                "id int," +
                "image TEXT)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS user");
        onCreate(db);
    }

    public void add(String mobile,String appUserId ,String image ,int id){
        db.execSQL("INSERT INTO user (mobile,appUserId,image,id) VALUES(?,?,?,?)",new Object[]{mobile,appUserId,image,id});
    }
    public void delete(String mobile,String appUserId ,String image ,int id){
        db.execSQL("DELETE FROM user WHERE mobile = AND appUserId ="+mobile+appUserId);
    }
    public void updata(String mobile){
        db.execSQL("UPDATE user SET mobile = ?",new Object[]{mobile});
    }
    public ArrayList<User> getAllData(){

        ArrayList<User> list = new ArrayList<User>();
        Cursor cursor = db.query("user",null,null,null,null,null,"mobile DESC");
        while(cursor.moveToNext()){
            String mobile = cursor.getString(cursor.getColumnIndex("mobile"));
            String appUserId = cursor.getString(cursor.getColumnIndex("appUserId"));
            String image = cursor.getString(cursor.getColumnIndex("image"));
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            list.add(new User(mobile,appUserId,image,id));
        }
        return list;
    }
}
