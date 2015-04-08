package com.kritsin.rssclient.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.kritsin.rssclient.entity.News;

public class FavoritesDbAdapter {
	  
	public void addFavorite(SQLiteDatabase db, News news){
		try{
	    	db.beginTransaction(); 
	    		String date = news.getDate().getTime()+"";
	    		db.execSQL("insert into " + DbCommonHelper.FAVORITES_TABLE_NAME + "(id,title, date, info, url) values(?,?,?,?,?)", 
	    				new String[]{news.getId()+"", news.getTitle(), date, news.getInfo(), news.getUrl() });
	    	db.setTransactionSuccessful();
    	} catch (SQLException e) {
    		e.printStackTrace();
    		throw new SQLException();
    	} finally {
    	  db.endTransaction();
    	}    
	}
	 
	public News getFavorite(SQLiteDatabase db, String url){
    	News result = null;
    	String sql =  "select * from "+DbCommonHelper.FAVORITES_TABLE_NAME +" where url=?";
        Cursor c = db.rawQuery(sql,new String[]{url}); 
        if (c != null) {  
        	if (c.moveToNext()) {
        		result = getFavoriteFromCursor(c);
        	}
        }
        c.close(); 
	  return result;
    }
	  
	public List<News> getFavorites(SQLiteDatabase db){
    	List<News> result = new ArrayList<News>();
    	String sql =  "select * from "+DbCommonHelper.FAVORITES_TABLE_NAME +" order by date desc";
        Cursor c = db.rawQuery(sql,new String[]{}); 
        if (c != null) {  
        	while (c.moveToNext()) {
        		News item = getFavoriteFromCursor(c);
        		result.add(item);
        	}
        }
        c.close(); 
	  return result;
    }
	 
 
	private News getFavoriteFromCursor(Cursor c){
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
	
	public void deleteFavorite(SQLiteDatabase db, String url){ 
    	try{
	    	db.beginTransaction();
    		db.execSQL("delete from " + DbCommonHelper.FAVORITES_TABLE_NAME+" where url=?", 
    				new String[]{url});
	    	db.setTransactionSuccessful();
    	} catch (SQLException e) {
    		e.printStackTrace();
    		throw new SQLException();
    	} finally {
    	  db.endTransaction();
    	}    	   
	} 
	 
}
