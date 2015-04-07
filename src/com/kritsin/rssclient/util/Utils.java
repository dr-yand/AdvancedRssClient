package com.kritsin.rssclient.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class Utils {
	public static String getRSS(String rss) throws Exception{
        StringBuilder builder = new StringBuilder(); 
        
        URL rssUrl = new URL(rss);
        BufferedReader buffer = null;
    	InputStream is = rssUrl.openStream();
    	buffer = new BufferedReader(new InputStreamReader(is));

    	int byteRead;
    	while ((byteRead = buffer.read()) != -1) {
    	    builder.append((char) byteRead);
    	}
    	buffer.close();

        return builder.toString();
    }
}
