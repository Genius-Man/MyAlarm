package com.csust.alarm.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.csust.alarm.bean.User;
import com.csust.alarm.dao.BaseDB;
import com.csust.alarm.dao.UserDB;
import com.csust.alarm.util.Constant;

public class UserDBImpl extends BaseDB implements UserDB {

	public UserDBImpl(Context context) {
		super(context);

	}

	@Override
	public User getUser(String username, String password) {

		User user = null;

		/* 根据username和password查询user的sql语句 */
		String sql = "SELECT * FROM USER WHERE USERNAME = ? AND PASSWORD = ?";

		/* 查询条件参数 */
		String[] selectionArgs = { username, password };

		/* 查询出的结果游标 */
		Cursor c = getDatabase().rawQuery(sql, selectionArgs);

		while (c.moveToNext()) {

			user = new User();
			/* 设置user的Id：首先获取_ID 所在列的索引，再根据列的索引找到值 */
			user.setId(c.getInt(c.getColumnIndex(Constant.COLUMN_USER_ID)));

			/* 设置user的username:用户名 */
			user.setUsername(c.getString(c
					.getColumnIndex(Constant.COLUMN_USER_USERNAME)));

			/* 设置user的password */
			user.setPassword(c.getString(c
					.getColumnIndex(Constant.COLUMN_USER_PASSWORD)));
		}

		return user == null ? null : user;
	}

	@Override
	public boolean insert(User user) {

		ContentValues cv = new ContentValues();
		
		/* 设置user的username字段值 */
		cv.put(Constant.COLUMN_USER_USERNAME, user.getUsername());
		/* 设置user的password字段值 */
		cv.put(Constant.COLUMN_USER_PASSWORD, user.getPassword());

		long isInsert = getDatabase().insert(Constant.USER_TABLE, null, cv);

		/*
		 * 插入成功会返回插入数据的主键ID值
		 * 
		 * 插入失败会返回 -1
		 * 
		 */
		return isInsert > 0;
	}

	@Override
	public User getUser(String username) {

		User user = null;

		/* 根据username和password查询user的sql语句 */
		String sql = "SELECT * FROM USER WHERE USERNAME = ?";

		/* 查询条件参数 */
		String[] selectionArgs = { username };

		/* 查询出的结果游标 */
		Cursor c = getDatabase().rawQuery(sql, selectionArgs);

		while (c.moveToNext()) {

			user = new User();
			/* 设置user的Id：首先获取_ID 所在列的索引，再根据列的索引找到值 */
			user.setId(c.getInt(c.getColumnIndex(Constant.COLUMN_USER_ID)));

			/* 设置user的username:用户名 */
			user.setUsername(c.getString(c
					.getColumnIndex(Constant.COLUMN_USER_USERNAME)));

			/* 设置user的password */
			user.setPassword(c.getString(c
					.getColumnIndex(Constant.COLUMN_USER_PASSWORD)));
		}

		return user;
	}

	@Override
	public User getUser(int id) {
		User user = null;

		/* 根据username和password查询user的sql语句 */
		String sql = "SELECT * FROM USER WHERE _ID = "+id;


		/* 查询出的结果游标 */
		Cursor c = getDatabase().rawQuery(sql, null);

		while (c.moveToNext()) {

			user = new User();
			/* 设置user的Id：首先获取_ID 所在列的索引，再根据列的索引找到值 */
			user.setId(c.getInt(c.getColumnIndex(Constant.COLUMN_USER_ID)));

			/* 设置user的username:用户名 */
			user.setUsername(c.getString(c
					.getColumnIndex(Constant.COLUMN_USER_USERNAME)));

			/* 设置user的password */
			user.setPassword(c.getString(c
					.getColumnIndex(Constant.COLUMN_USER_PASSWORD)));
		}

		return user;
	}

}
