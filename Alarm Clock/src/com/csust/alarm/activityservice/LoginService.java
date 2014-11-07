package com.csust.alarm.activityservice;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


import com.csust.alarm.bean.User;
import com.csust.alarm.dao.UserDB;
import com.csust.alarm.dao.impl.UserDBImpl;
import com.csust.alarm.util.StringUtil;

public class LoginService {

	private Context context;

	private UserDB userDao = new UserDBImpl(context);

	//SharedPreferences sharedPreferences  = LoginActivity.class.getSharedPreferences("user", LoginActivity.MODE_PRIVATE);;
	
	public LoginService(Context applicationContext) {

		this.context = applicationContext;
	}

	/**
	 * 校验输入的用户名和密码是否正确
	 * @param username 用户名
	 * @param password 密码，转成MD5后校验
	 * @return 输入信息能与数据库匹配则返回true
	 * 			输入的信息不能与数据库匹配则返回false
	 */
	public boolean checkLogin(String username, String password) {
		
		/* 将输入的密码转换成MD5后校验 */
		if(password.length()<20){
			password = StringUtil.toMd5String(password);
		}
		
		/*
		 * 根据用户名和密码查找响应的用户user记录
		 * 
		 * 如果找到的user不为空，则说明输入的信息正确，返回true
		 * 
		 * 如果找到的user为空，则说明输入的信息不正确，返回false
		 * 
		 * */
		User user = userDao.getUser(username, password);
		return user != null;
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
	 * 根据用户名和密码 获取用户信息 
	 * @param username
	 * @param password
	 * @return
	 */
	public User getUser(String username, String password) {
		
		/* 
		 * 密码长度规定为 6 -16 ，如果长度大于这个范围
		 * 
		 * 则说明密码已经是被MD5过了的，在这个区间之内
		 * 
		 * 说明还没有获取MD5值，则将输入的密码转换成MD5后校验 
		 * 
		 * */
		if(password.length()<20){
			
			password = StringUtil.toMd5String(password);
		}
		
		/*
		 * 根据用户名和密码查找响应的用户user记录
		 * 
		 * */
		User user = userDao.getUser(username, password);
		return user;
	}
}
