package com.kritsin.rssclient.entity;

import java.io.Serializable;
import java.util.Date;

public class News  implements Serializable {  
    private static final long serialVersionUID = 1l;   
    
	private int id;
	private String title, info, url;
	private Date date; 
	
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@Override
	public String toString() {
		String result = "<b>"+title+"</b><br>"+date.toLocaleString()+"<br>"+info;
		return result;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	} 
}
