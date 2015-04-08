package com.kritsin.rssclient.task;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import android.os.AsyncTask;

import com.kritsin.rssclient.entity.AuthStatus;
import com.kritsin.rssclient.entity.News;

public class AuthTask extends AsyncTask<Void, Void, AuthStatus>{ 
	
	public interface OnAuthResultListener {
		public void onResultGetNews(AuthStatus result);  
	} 
	
	private OnAuthResultListener mListener;  
	private String mLogin, mPassword;
	
	public AuthTask(OnAuthResultListener listener, String login, String password){
		this.mListener=listener; 
		this.mLogin = login;
		this.mPassword = password;
	}
 
	@Override
    protected AuthStatus doInBackground(Void... params) { 
		AuthStatus result = AuthStatus.NO_USER;
		
		if("test".equals(mLogin.trim())&&
				"test".equals(mPassword.trim())){
			result = AuthStatus.ALL_OK;
		}
		else{
			result = AuthStatus.NO_USER;
		}
		try {
			TimeUnit.SECONDS.sleep(new Random().nextInt(10)+1);
		} catch (InterruptedException e) {
			result = AuthStatus.NET_ERROR;
			e.printStackTrace();
		}
    	
    	return result;
    } 
     

	@Override
    protected void onPostExecute(AuthStatus result) {
        super.onPostExecute(result);    
        if(mListener!=null)
        	mListener.onResultGetNews(result);
    }
 
}