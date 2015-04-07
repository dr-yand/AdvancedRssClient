package com.kritsin.rssclient.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbCommonHelper extends SQLiteOpenHelper { 
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "rssclient.db";
    
    public static final String NEWS_TABLE_NAME = "news";
    private static final String NEWS_TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS " + NEWS_TABLE_NAME + " (" 
                	+ " id INTEGER PRIMARY KEY NOT NULL, "
	                + " title TEXT, "
	                + " date INTEGER,"
	                + " info TEXT,"
	                + " url TEXT,"
	                + " favorite INTEGER);"; 
     
    private SQLiteDatabase mDb;

    public DbCommonHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(NEWS_TABLE_CREATE); 
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { 
        db.execSQL("drop table  IF EXISTS "+NEWS_TABLE_NAME); 
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    
    public SQLiteDatabase open(){
    	if(mDb==null)
    		mDb = getWritableDatabase();
    	return mDb; 
    }
}