package com.csust.alarm;

import java.lang.reflect.Field;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewConfiguration;

import com.csust.alarm.activityservice.CommonService;
import com.csust.alarm.bean.User;
import com.csust.alarm.dao.impl.UserDBImpl;
import com.csust.alarm.preferences.AlarmPreferencesActivity;
import com.csust.alarm.service.AlarmServiceBroadcastReciever;


public abstract class BaseActivity  extends ActionBarActivity implements android.view.View.OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		try {
	        ViewConfiguration config = ViewConfiguration.get(this);	        
	        Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
	        if(menuKeyField != null) {
	            menuKeyField.setAccessible(true);
	            menuKeyField.setBoolean(config, false);
	        }
	    } catch (Exception ex) {
	       
	    }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    boolean result = super.onCreateOptionsMenu(menu);
	    
	    MenuItem login = menu.findItem(R.id.menu_item_login);
	    
	    SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
	    int loginedId = new CommonService(getApplicationContext()).getSharedUserId(sharedPreferences);
	    
	    UserDBImpl.init(getApplicationContext());
	    
	    User user = new UserDBImpl(getApplicationContext()).getUser(loginedId);
	    
	    if(user != null ){
	    	login.setTitle(user.getUsername());
	    }
	    
	    return result;
	}

	/**
	 * 主界面菜单
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//String url = null;
		//Intent intent = null;
		switch (item.getItemId()) {
		case R.id.menu_item_new:
			Intent newAlarmIntent = new Intent(getApplicationContext(), AlarmPreferencesActivity.class);
			startActivity(newAlarmIntent);
			break;
		case R.id.menu_item_login:
			Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
			startActivity(loginIntent);
			break;
		case R.id.menu_item_register:
			Intent registerIntent = new Intent(getApplicationContext(),RegisterActivity.class);
			startActivity(registerIntent);
			break;

		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 向AlarmService发送广播，将所有等待响铃的闹钟添加至任务队列
	 */
	protected void callMathAlarmScheduleService() {
		
		Intent mathAlarmServiceIntent = new Intent(this, AlarmServiceBroadcastReciever.class);
		
		sendBroadcast(mathAlarmServiceIntent, null);
	}
}
