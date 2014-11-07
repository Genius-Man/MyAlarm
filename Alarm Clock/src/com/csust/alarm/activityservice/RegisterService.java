package com.csust.alarm.activityservice;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.csust.alarm.bean.Alarm;
import com.csust.alarm.bean.User;
import com.csust.alarm.dao.AlarmDB;
import com.csust.alarm.dao.UserDB;
import com.csust.alarm.dao.impl.UserDBImpl;

public class RegisterService {

	private Context context;

	private UserDB userDao = new UserDBImpl(context);

	public RegisterService(Context applicationContext) {
		
		
		
		this.context = applicationContext;
	}

	/**
	 * 校验用户名是否输入正确 用户名不能重复
	 * 
	 * @param username
	 * @return
	 */
	public boolean validationUsername(String username) {

		User user = userDao.getUser(username);

		/*
		 * 如果user为空，说明数据库不存在对应记录， 则该用户名可以使用，返回true
		 * 
		 * 如果user不为空，说明数据库已经存在对应的记录 该用户名不可使用，返回false
		 */
		return user == null;
	}

	/**
	 * 校验密码是否输入正确(密码由6 - 16 位的字母 下划线 数字 组成)
	 * 
	 * @param password
	 * @return
	 */
	public boolean validationPassword(String password) {

		/* 匹配密码的正则表达式 */
		String reg = "^\\w{6,16}";

		return password.matches(reg);
	}

	/**
	 * 校验确认密码是否与密码一致
	 * 
	 * @param password
	 * @param confirmPassword
	 * @return
	 */
	public boolean validationConfirmPassword(String password,
			String confirmPassword) {

		if (password != null && confirmPassword != null)
			return password.equals(confirmPassword);
		return false;
	}

	/**
	 * 
	 * @param confirmPassword
	 * @param password
	 * @param username
	 * @return
	 */
	public boolean validationRegister(String username, String password,
			String confirmPassword) {

		return validationUsername(username) && validationPassword(password)
				&& validationConfirmPassword(password, confirmPassword);
	}

	/**
	 * 
	 * @param user
	 * @return
	 */
	public boolean insertUser(User user) {
		
		//UserDBImpl.init(context);
		
		//userDao.insert(user);
		return userDao.insert(user);
	}

	/**
	 * 修改闹钟外键的值： 0 -》 id
	 * @param id
	 * @return
	 */
	public boolean updateAlarmToCurrentUser(int id) {
		
		List<Alarm> alarms = new ArrayList<Alarm>();
		alarms = AlarmDB.getAll(0);
		
		/*
		 * 将闹钟的外键设置为 id 后
		 * 
		 * 更新闹钟 
		 */
		for (Alarm alarm : alarms) {
			
			alarm.setUserId(id);
			
			int flag = AlarmDB.update(alarm);
			
			if(flag < 0){
				return false;
			}
			
		}
		return true;
	}

}
