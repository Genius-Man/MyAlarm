package com.csust.alarm.activityservice;

import com.csust.alarm.bean.User;
import com.csust.alarm.util.Constant;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CommonService {

	
	public CommonService(Context applicationContext) {

		//this.context = applicationContext;
	}
	
	/**
	 * 保存用户的
	 * @param sharedPreferences 
	 * @param id 
	 */
	public void saveUserId(SharedPreferences sharedPreferences, int id) {
		
		Editor editor = sharedPreferences.edit();
		
		/* 保存id */
		editor.putInt("loginedId", id);
		
		/* 提交保存 */
		editor.commit();
		
	}
	
	/**
	 * 获取保存的用户ID
	 * 
	 * @param sharedPreferences
	 * @return
	 */
	public int getSharedUserId(SharedPreferences sharedPreferences){
		
		return sharedPreferences.getInt("loginedId", 0);
	}
	
	
	/**
	 * 
	 * @param sharedPreferences
	 * @param user
	 */
	public void saveUser(SharedPreferences sharedPreferences, User user) {

		Editor editor = sharedPreferences.edit();
		
		editor.putInt(Constant.COLUMN_USER_ID, user.getId());
		
		editor.putString(Constant.COLUMN_USER_USERNAME, user.getUsername());
		
		editor.putString(Constant.COLUMN_USER_PASSWORD, user.getPassword());
		
		editor.commit();
		
	}
	
	/**
	 * 
	 * @param sharedPreferences
	 * @return
	 */
	public User getSharedUser(SharedPreferences sharedPreferences){
		
		User user = null;
		
		int id = sharedPreferences.getInt(Constant.COLUMN_USER_ID, 0);
		
		if(id > 0){
			
			user = new User();
			user.setId(sharedPreferences.getInt(Constant.COLUMN_USER_ID, 0));
			user.setUsername(sharedPreferences.getString(Constant.COLUMN_USER_USERNAME, null));
			user.setPassword(sharedPreferences.getString(Constant.COLUMN_USER_PASSWORD, null));
			
		}
		return user;
	}
}
