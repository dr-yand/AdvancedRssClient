package com.kritsin.rssclient;
 
import com.kritsin.rssclient.db.DbCommonHelper;
import com.kritsin.rssclient.db.FavoritesDbAdapter;
import com.kritsin.rssclient.db.NewsDbAdapter;
import com.kritsin.rssclient.entity.News;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class DetailActivity extends Activity {

	private News mCurrentNews; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		 
		mCurrentNews = (News)getIntent().getSerializableExtra("news");
		  
		initViews();
		initMenu();
	}
	
	private void initMenu(){
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	private void initViews(){
		WebView webView = (WebView)findViewById(R.id.web);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient());
		webView.loadUrl(mCurrentNews.getUrl());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) { 
		getMenuInflater().inflate(R.menu.detail, menu);
		return true;
	}
	
	

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		DbCommonHelper dbHelper = new DbCommonHelper(getApplicationContext());  
		FavoritesDbAdapter favoritesDbAdapter = new FavoritesDbAdapter(); 
		News tempNews = favoritesDbAdapter.getFavorite(dbHelper.getReadableDatabase(), mCurrentNews.getUrl());
		dbHelper.close();
		if(tempNews!=null)
			((MenuItem)menu.findItem(R.id.add_favorite)).setVisible(false);
		else
			((MenuItem)menu.findItem(R.id.remove_favorite)).setVisible(false);
		return super.onPrepareOptionsMenu(menu);
	}
 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) { 
		int id = item.getItemId();
		if (id == R.id.add_favorite) {
			invalidateOptionsMenu();
			DbCommonHelper dbHelper = new DbCommonHelper(getApplicationContext());
    		FavoritesDbAdapter favoritesDbAdapter = new FavoritesDbAdapter();
    		favoritesDbAdapter.addFavorite(dbHelper.getWritableDatabase(), mCurrentNews);
			dbHelper.close();
			return true;
		}
		if (id == R.id.remove_favorite) {
			invalidateOptionsMenu();
			DbCommonHelper dbHelper = new DbCommonHelper(getApplicationContext());
    		FavoritesDbAdapter favoritesDbAdapter = new FavoritesDbAdapter();
    		favoritesDbAdapter.deleteFavorite(dbHelper.getWritableDatabase(), mCurrentNews.getUrl());
			dbHelper.close();
			return true;
		}
		if (id == android.R.id.home){
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
