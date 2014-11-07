package com.csust.alarm.dao;

import com.csust.alarm.util.Constant;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseDB extends SQLiteOpenHelper {

	public static BaseDB instance = null;
	public static SQLiteDatabase database = null;

	public static Context context;

	/**
	 * 初始化数据库，每次要使用数据库时必须先调用这个方法
	 * 
	 * @param context
	 */
	public static void init(Context context) {

		instance = (null == instance) ? new BaseDB(context) : instance;
	}

	/**
	 * 获取一个可写的数据库实例
	 * 
	 * @return
	 */
	public static SQLiteDatabase getDatabase() {

		init(context);

		if (null == database) {
			database = instance.getWritableDatabase();
		}
		return database;
	}

	/**
	 * 关闭数据库
	 */
	public static void deactivate() {
		if (null != database && database.isOpen()) {
			database.close();
		}
		database = null;
		instance = null;
	}

	/**
	 * 
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
	public BaseDB(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		BaseDB.context = context;
	}

	/**
	 * 
	 * @param context
	 */
	public BaseDB(Context context) {

		super(context, Constant.DATABASE_NAME, null, Constant.DATABASE_VERSION);
		BaseDB.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		/* 创建user表 */
		db.execSQL(Constant.TABLE_USER_CREATE);

		/* 创建alarm表 */
		db.execSQL(Constant.TABLE_ALARM_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		/* 数据备份省略 */
		
		
		/* 删除alarm表 */
		db.execSQL(Constant.TABLE_ALARM_DROP);

		/* 删除user表 */
		db.execSQL(Constant.TABLE_USER_DROP);

		/* 重新创建新的数据库 */
		onCreate(db);
	}

}
