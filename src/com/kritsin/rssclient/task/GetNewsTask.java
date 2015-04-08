package com.kritsin.rssclient.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;

import com.kritsin.rssclient.entity.LoadStatus;
import com.kritsin.rssclient.entity.News;
import com.kritsin.rssclient.util.Config;
import com.kritsin.rssclient.util.Utils;
import com.kritsin.rssclient.util.XmlUtils;

public class GetNewsTask extends AsyncTask<Void, Void, List<News>>{ 
	
	public interface OnGetNewsListener {
		public void onResultGetNews(LoadStatus result, List<News> items);  
	} 
	
	private OnGetNewsListener mListener;
	private Context mMainContext;
	private LoadStatus mStatus;
	
	public GetNewsTask(Context mainContext, OnGetNewsListener listener){
		this.mListener=listener;
		this.mMainContext=mainContext;  
	}
 
	@Override
    protected List<News> doInBackground(Void... params) { 
		ArrayList<News> result = new ArrayList<News>();
    	try {
			String rssText = Utils.getRSS(Config.RSS_URL);
//			rssText= new String(rssText.getBytes("Windows-1251"), "UTF-8");
			
			Document doc  = null;	
			doc = XmlUtils.loadXMLFromString(rssText);
			Element eElement = (Element) doc.getDocumentElement(); 
			
			for(Element e : XmlUtils.getItems((Element)XmlUtils.getChannel(eElement))){
				News news = new News();
				news.setTitle(XmlUtils.getItemProperty(e, "title"));
				news.setUrl(XmlUtils.getItemProperty(e, "link"));
				news.setInfo(Html.fromHtml(XmlUtils.getItemProperty(e, "description")).toString());
//				news.category.setText(XmlUtils.getItemProperty(e, "category"));					
				
				Date date = new Date(XmlUtils.getItemProperty(e, "pubDate"));
				SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy HH:mm");
				news.setDate(date);
				 
				result.add(news);						 
			}
			mStatus = LoadStatus.ALL_OK;
			
		} catch (Exception e) {
			e.printStackTrace();
			mStatus = LoadStatus.NET_ERROR;
		}
    	
    	return result;
    } 
     

	@Override
    protected void onPostExecute(List<News> result) {
        super.onPostExecute(result);    
        if(mListener!=null)
        	mListener.onResultGetNews(mStatus, result);
    }
 
}