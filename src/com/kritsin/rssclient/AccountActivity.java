package com.kritsin.rssclient;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kritsin.rssclient.db.DbCommonHelper;
import com.kritsin.rssclient.db.FavoritesDbAdapter;
import com.kritsin.rssclient.entity.AuthStatus;
import com.kritsin.rssclient.entity.News;
import com.kritsin.rssclient.task.AuthTask;
import com.kritsin.rssclient.task.AuthTask.OnAuthResultListener;
import com.kritsin.rssclient.util.PreferencesUtils;

public class AccountActivity extends Activity implements OnItemClickListener, OnItemLongClickListener, OnAuthResultListener {

	private ListView mNewsListView ; 
	private int mCurrentNewsPosition=-1;
	private ProgressDialog mProgressDialog;
	private Dialog mLoginDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);
		
		initViews();
		initMenu();
		createDialogs();
		
	}
	

	private void createDialogs(){ 
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("Пожалуйста подождите...");
		mProgressDialog.setIndeterminate(true); 
		mProgressDialog.setCancelable(true);
        mProgressDialog.setCanceledOnTouchOutside(true);
        
        mLoginDialog = new Dialog(this);
        mLoginDialog.setContentView(R.layout.dialog_login);
        mLoginDialog.setTitle("Авторизация");
//        mLoginDialog.setCancelable(false);
        mLoginDialog.setCanceledOnTouchOutside(false);
   
		((Button)mLoginDialog.findViewById(R.id.ok)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String login = ((EditText)mLoginDialog.findViewById(R.id.login)).getText().toString();
				String password = ((EditText)mLoginDialog.findViewById(R.id.password)).getText().toString();
				if(login.trim().length()==0||password.trim().length()==0){
					Toast.makeText(getApplicationContext(), "Введите логин и пароль", Toast.LENGTH_LONG).show();
				}
				else{
					mLoginDialog.dismiss();
					mProgressDialog.show(); 
					new AuthTask(AccountActivity.this, login, password).execute(new Void[]{});
				}
			}
		});
	}
 
	private void initMenu(){
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) { 
		getMenuInflater().inflate(R.menu.account, menu);
		return true;
	}
	

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if(PreferencesUtils.getUserId(getApplicationContext())==-1){
			((MenuItem)menu.findItem(R.id.signout)).setVisible(false);
		}
		else{
			((MenuItem)menu.findItem(R.id.signin)).setVisible(false);
		} 
		return super.onPrepareOptionsMenu(menu);
	}
	
	private void initViews(){
		mNewsListView = (ListView)findViewById(R.id.news_list); 
		mNewsListView.setOnItemClickListener(this);
		mNewsListView.setOnItemLongClickListener(this);
		 
		
		registerForContextMenu(mNewsListView); 
	}

	@Override
	protected void onResume() { 
		super.onResume();
		

		if(PreferencesUtils.getUserId(getApplicationContext())==-1){
			mLoginDialog.show();
		}
		else{
			loadFavoritesList();
		}
	}		
	
	private void loadFavoritesList(){
		mCurrentNewsPosition = -1;
		DbCommonHelper dbHelper = new DbCommonHelper(getApplicationContext());
		FavoritesDbAdapter dbAdapter = new FavoritesDbAdapter();
		List<News> newsList = dbAdapter.getFavorites(dbHelper.getReadableDatabase());
		dbHelper.close();
		
		ArrayAdapter<News> adapter = new ArrayAdapter<News>(this, R.layout.news_item, 
				R.id.title, newsList){
					@Override
					public View getView(int position, View convertView,
							ViewGroup parent) {
						News news = getItem(position);
						View view = super.getView(position, convertView, parent);
						((TextView)view.findViewById(R.id.title)).setText(Html.fromHtml(news.toString()));
						return view;
					}				
			};
			mNewsListView.setAdapter(adapter);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) { 
		int id = item.getItemId();
		if(id == android.R.id.home){
			finish();
		}
		if(id == R.id.signin){
			mLoginDialog.show();
		}
		if(id == R.id.signout){
			PreferencesUtils.deleteUserId(getApplicationContext()); 
			Intent intent = getIntent();
			finish();
		    startActivity(intent);
			
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) { 
		News news = (News)parent.getItemAtPosition(position);
		Intent intent = new Intent(this, DetailActivity.class);
		intent.putExtra("news", news); 
		startActivity(intent);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		mCurrentNewsPosition = position;
		return false;
	}
	
	@Override   
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)  
    {  
            super.onCreateContextMenu(menu, v, menuInfo);  
            menu.setHeaderTitle("");
            menu.clearHeader();
            DbCommonHelper dbHelper = new DbCommonHelper(getApplicationContext()); 
			FavoritesDbAdapter favoritesDbAdapter = new FavoritesDbAdapter();
			News currentNews = (News)mNewsListView.getAdapter().getItem(mCurrentNewsPosition);
			News tempNews = favoritesDbAdapter.getFavorite(dbHelper.getReadableDatabase(), currentNews.getUrl());
			dbHelper.close();
            if(tempNews!=null)            
            	menu.add(0, R.id.remove_favorite, 0, R.string.remove_favorite);
            else
            	menu.add(0, R.id.add_favorite, 0, R.string.add_favorite);   
    }   
    @Override    
    public boolean onContextItemSelected(MenuItem item){    
            if(item.getItemId()==R.id.add_favorite){
            	if(mCurrentNewsPosition>-1){
            		News news = ((News)(mNewsListView.getAdapter().getItem(mCurrentNewsPosition)));
            		DbCommonHelper dbHelper = new DbCommonHelper(getApplicationContext());
            		FavoritesDbAdapter favoritesDbAdapter = new FavoritesDbAdapter();
            		favoritesDbAdapter.addFavorite(dbHelper.getWritableDatabase(), news);
        			dbHelper.close();
        			loadFavoritesList();
            	}
            }
            if(item.getItemId()==R.id.remove_favorite){
            	if(mCurrentNewsPosition>-1){
            		News news = ((News)(mNewsListView.getAdapter().getItem(mCurrentNewsPosition)));
            		DbCommonHelper dbHelper = new DbCommonHelper(getApplicationContext());
            		FavoritesDbAdapter favoritesDbAdapter = new FavoritesDbAdapter();
            		favoritesDbAdapter.deleteFavorite(dbHelper.getWritableDatabase(), news.getUrl());
        			dbHelper.close();
        			loadFavoritesList();
            	}
            }
          return true;    
      }


	@Override
	public void onResultGetNews(AuthStatus result) {
		mProgressDialog.dismiss();		
		if(result == AuthStatus.ALL_OK){
			loadFavoritesList();
			PreferencesUtils.setUserId(getApplicationContext(), 10);
		}
		else if(result == AuthStatus.NO_USER){
			Toast.makeText(getApplicationContext(), "Такого пользователя не существует", Toast.LENGTH_LONG).show();
			mLoginDialog.show();
		}
		else{
			Toast.makeText(getApplicationContext(), "Ошибка про подключении", Toast.LENGTH_LONG).show();
			mLoginDialog.show();
		}
		invalidateOptionsMenu();
	}   
}
