package com.kritsin.rssclient;
 
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Html;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kritsin.rssclient.db.DbCommonHelper;
import com.kritsin.rssclient.db.FavoritesDbAdapter;
import com.kritsin.rssclient.db.NewsDbAdapter;
import com.kritsin.rssclient.entity.LoadStatus;
import com.kritsin.rssclient.entity.News;
import com.kritsin.rssclient.task.GetNewsTask;
import com.kritsin.rssclient.task.GetNewsTask.OnGetNewsListener;

public class MainActivity extends Activity implements OnGetNewsListener, OnRefreshListener, OnItemLongClickListener, OnItemClickListener {

	private ListView mNewsListView ;
	private SwipeRefreshLayout mSwipeLayout;
	private int mCurrentNewsPosition=-1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initViews();
	}
	
	@Override
	protected void onResume() { 
		super.onResume();
		request();
	}		

	private void initViews(){
		mNewsListView = (ListView)findViewById(R.id.news_list);
//		mNewsListView.setDividerHeight(0);
		mNewsListView.setOnItemClickListener(this);
		mNewsListView.setOnItemLongClickListener(this);
		
		mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright, 
	            android.R.color.holo_blue_bright, 
	            android.R.color.holo_blue_bright, 
	            android.R.color.holo_blue_bright);
		
		registerForContextMenu(mNewsListView); 
	}
	
	private void request(){
		mSwipeLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
		mSwipeLayout.setRefreshing(true);
		new GetNewsTask(this, this)
			.execute(new Void[]{});
	}
	
	private void loadNewsList(){
		mCurrentNewsPosition = -1;
		DbCommonHelper dbHelper = new DbCommonHelper(getApplicationContext());
		NewsDbAdapter dbAdapter = new NewsDbAdapter();
		List<News> newsList = dbAdapter.getNews(dbHelper.getReadableDatabase());
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
	public boolean onCreateOptionsMenu(Menu menu) { 
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) { 
		int id = item.getItemId();
		if (id == R.id.account) { 
			Intent intent = new Intent(this, AccountActivity.class); 
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResultGetNews(LoadStatus result, List<News> items) {
		mSwipeLayout.setRefreshing(false);
		if(result == LoadStatus.ALL_OK){
			DbCommonHelper dbHelper = new DbCommonHelper(getApplicationContext());
			NewsDbAdapter dbAdapter = new NewsDbAdapter();
			dbAdapter.deleteNews(dbHelper.getWritableDatabase());
			dbAdapter.addNews(dbHelper.getWritableDatabase(), items);
			dbHelper.close();
		}
		else{
			Toast.makeText(this, "������ ��� ��������", Toast.LENGTH_LONG).show();
		}
 		loadNewsList();
	}

	@Override
	public void onRefresh() {
		request();
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
            	}
            }
            if(item.getItemId()==R.id.remove_favorite){
            	if(mCurrentNewsPosition>-1){
            		News news = ((News)(mNewsListView.getAdapter().getItem(mCurrentNewsPosition)));
            		DbCommonHelper dbHelper = new DbCommonHelper(getApplicationContext());
            		FavoritesDbAdapter favoritesDbAdapter = new FavoritesDbAdapter();
            		favoritesDbAdapter.deleteFavorite(dbHelper.getWritableDatabase(), news.getUrl());
        			dbHelper.close();
            	}
            }
          return true;    
      }   
}
