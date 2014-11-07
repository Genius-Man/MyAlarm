package com.csust.alarm.dao;

import com.csust.alarm.bean.User;


public interface UserDB {

	
	/**
	 * 往user表添加实例
	 * @param user
	 * @return
	 */
	public abstract boolean insert(User user);
	
	/**
	 * 根据用户名和密码获取user实例
	 * @param username
	 * @param password
	 * @return
	 */
	public abstract User getUser(String username, String password);
	
	/**
	 * 根据用户名获取user实例
	 * @param username
	 * @return
	 */
	public abstract User getUser(String username);
	
	/**
	 * 根据id获取user实例
	 * @param id
	 * @return
	 */
	 
	public abstract User getUser(int id);
	
}
