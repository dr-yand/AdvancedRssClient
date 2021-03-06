package com.kritsin.rssclient.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.kritsin.rssclient.entity.News;

public class NewsDbAdapter {
	  
	public void addNews(SQLiteDatabase db, List<News> newsList){
		try{
	    	db.beginTransaction(); 
	    	for(News news:newsList){
	    		String date = news.getDate().getTime()+"";
	    		db.execSQL("insert into " + DbCommonHelper.NEWS_TABLE_NAME + "(title, date, info, url) values(?,?,?,?)", 
	    				new String[]{news.getTitle(), date, news.getInfo(), news.getUrl() });
	    	}
	    	db.setTransactionSuccessful();
    	} catch (SQLException e) {
    		e.printStackTrace();
    		throw new SQLException();
    	} finally {
    	  db.endTransaction();
    	}    
	}
	 
	public void updateFavorite(SQLiteDatabase db, int id, int favorite){
		try{
	    	db.beginTransaction(); 
    		db.execSQL("update " + DbCommonHelper.NEWS_TABLE_NAME + " set favorite=? where id=?", 
    				new String[]{favorite+"",id+""});
	    	db.setTransactionSuccessful();
    	} catch (SQLException e) {
    		e.printStackTrace();
    		throw new SQLException();
    	} finally {
    	  db.endTransaction();
    	}    
	}
	  
	public News getNews(SQLiteDatabase db, int id){
    	News result = null;
    	String sql =  "select * from "+DbCommonHelper.NEWS_TABLE_NAME +" where id=?";
        Cursor c = db.rawQuery(sql,new String[]{id+""}); 
        if (c != null) {  
        	if (c.moveToNext()) {
        		result = getNewsFromCursor(c);
        	}
        }
        c.close(); 
	  return result;
    }
	
	public List<News> getNews(SQLiteDatabase db){
    	List<News> result = new ArrayList<News>();
    	String sql =  "select * from "+DbCommonHelper.NEWS_TABLE_NAME +" order by date desc";
        Cursor c = db.rawQuery(sql,new String[]{}); 
        if (c != null) {  
        	while (c.moveToNext()) {
        		News item = getNewsFromCursor(c);
        		result.add(item);
        	}
        }
        c.close(); 
	  return result;
    }
	 
 
	private News getNewsFromCursor(Cursor c){
		final int ID = c.getColumnIndex("id");
		final int TITLE = c.getColumnIndex("title");
    	final int DATE = c.getColumnIndex("date");
    	final int INFO = c.getColumnIndex("info");
    	final int URL = c.getColumnIndex("url");
    	
    	News news = new News();
    	news.setId(c.getInt(ID));
    	news.setTitle(c.getString(TITLE));
    	long date = c.getLong(DATE);
    	news.setDate(new Date(date));
    	news.setInfo(c.getString(INFO)); 
    	news.setUrl(c.getString(URL));
		
		return news;
	}
	
	public void deleteNews(SQLiteDatabase db){ 
    	try{
	    	db.beginTransaction();
    		db.execSQL("delete from " + DbCommonHelper.NEWS_TABLE_NAME, 
    				new String[]{});
	    	db.setTransactionSuccessful();
    	} catch (SQLException e) {
    		e.printStackTrace();
    		throw new SQLException();
    	} finally {
    	  db.endTransaction();
    	}    	   
	} 
	 
}
