package com.ywz.minidouyin.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;

import static com.ywz.minidouyin.db.FeedContract.*;

import com.ywz.minidouyin.bean.Feed;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FeedDBHelper extends SQLiteOpenHelper {
    // TODO 定义数据库名、版本；创建数据库
    public static final String DATABASE_NAME = "FeedDB.db";
    public static final int DATABASE_VERSION = 2;
    public static final String TAG = "SQL";

    public FeedDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+FeedContract.tableName +" ( "
                + FeedContract.ID + " INTEGER PRIMARY KEY, "
                + FeedContract.student_id + " TEXT, "
                + FeedContract.user_name + " TEXT, "
                + FeedContract.createdAt + " TEXT, "
                + FeedContract.updatedAt + " TEXT, "
                + FeedContract.image_url + " TEXT, "
                + FeedContract.video_url + " TEXT)");
        db.execSQL("create index "+student_id_ind+" on "+tableName+"("+student_id+")");
        db.execSQL("create index "+user_name_ind+" on "+tableName+"("+user_name+")");
    }

    public void insertOneFeed(Feed item,SQLiteDatabase db){
        ContentValues cv = new ContentValues();
        cv.put(image_url,item.getImgUrl());
        cv.put(video_url,item.getVideoUrl());
        cv.put(student_id,item.getStudentID());
        cv.put(user_name,item.getName());
        cv.put(createdAt,item.getCreateAt());
        cv.put(updatedAt,item.getUpdatedAt());
        db.insert(tableName,null,cv);
    }

    public static Feed toFeed(Cursor cursor){
        Feed feed = new Feed();
        feed.setCreateAt(cursor.getString(cursor.getColumnIndex(createdAt)));
        feed.setImgUrl(cursor.getString(cursor.getColumnIndex(image_url)));
        feed.setUpdatedAt(cursor.getString(cursor.getColumnIndex(updatedAt)));
        feed.setName(cursor.getString(cursor.getColumnIndex(user_name)));
        feed.setStudentID(cursor.getString(cursor.getColumnIndex(student_id)));
        feed.setVideoUrl(cursor.getString(cursor.getColumnIndex(video_url)));
        return feed;
    }

    public void updateDB(List<Feed> list,Context context){
        FeedDBHelper feedDBHelper = new FeedDBHelper(context);
        SQLiteDatabase db = feedDBHelper.getWritableDatabase();
        db.execSQL("drop TABLE "+ tableName);
        onCreate(db);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            list.sort(new Comparator<Feed>() {
                @Override
                public int compare(Feed o1, Feed o2) {
                    return o1.getUpdatedAt().compareTo(o2.getUpdatedAt());
                }
            });
        }
        for(Feed item : list ){
            feedDBHelper.insertOneFeed(item,db);
        }
        db.close();
    }

    public static ArrayList<Feed> refreshList(Context context){
        Log.d(TAG, "refreshList: ");
        FeedDBHelper dbHelper = new FeedDBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+ FeedContract.tableName
                +" order by "+FeedContract.updatedAt+" DESC",null);
        ArrayList<Feed> feedList= new ArrayList<>();
        while(cursor.moveToNext()){
            Feed f = dbHelper.toFeed(cursor);
            feedList.add(f);
        }
        return feedList;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for(int i = oldVersion;i<newVersion;i++){
            switch (i){
                case 1:
                    db.execSQL("create index "+student_id_ind+" on "+tableName+"("+student_id+")");
                    db.execSQL("create index "+user_name_ind+" on "+tableName+"("+user_name+")");
                    break;
                default:
            }
        }
    }
}
