package com.csust.alarm.util;

public class Constant {

	/** 数据库名 */
	public static final String DATABASE_NAME = "alarm.db";

	/** 数据库版本号 */
	public static final int DATABASE_VERSION = 1;

	/* ======================== Alarm表定义开始 ========================= */

	/** 闹钟表名 */
	public static final String ALARM_TABLE = "alarm";

	/** 闹钟表列名：主键号 */
	public static final String COLUMN_ALARM_ID = "_id";

	/** 闹钟表列名：激活状态（是否启用） */
	public static final String COLUMN_ALARM_ACTIVE = "alarm_active";

	/** 闹钟表列名：响铃时间 */
	public static final String COLUMN_ALARM_TIME = "alarm_time";

	/** 闹钟表列名：重复的天数（周一 周二 周三 等等 ） */
	public static final String COLUMN_ALARM_DAYS = "alarm_days";

	/** 闹钟表列名：数学问题的难易程度（简单 中等 困难 ） */
	public static final String COLUMN_ALARM_DIFFICULTY = "alarm_difficulty";

	/** 闹钟表列名：响铃铃声 */
	public static final String COLUMN_ALARM_TONE = "alarm_tone";

	/** 闹钟表列名：闹钟是否震动 */
	public static final String COLUMN_ALARM_VIBRATE = "alarm_vibrate";

	/** 闹钟表列名：闹钟标签名（标示闹钟用途，如上课 午睡 ） */
	public static final String COLUMN_ALARM_NAME = "alarm_name";

	/** 闹钟表列名：外键，引用user _id*/
	public static final String COLUMN_ALARM_FOREIGN_KEY_USER_ID = "user_id";
	
	/** 闹钟表的删除语句 */
	public static final String TABLE_ALARM_DROP = "DROP TABLE IF EXISTS "
			+ ALARM_TABLE;

	
	/** 闹钟表 alarm 的建表语句 */
	public static final String TABLE_ALARM_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ Constant.ALARM_TABLE + " ( "
			+ Constant.COLUMN_ALARM_ID
			+ " INTEGER primary key autoincrement, "
			+ Constant.COLUMN_ALARM_ACTIVE
			+ " INTEGER NOT NULL, "
			+ Constant.COLUMN_ALARM_TIME
			+ " TEXT NOT NULL, "
			+ Constant.COLUMN_ALARM_DAYS
			+ " BLOB NOT NULL, "
			+ Constant.COLUMN_ALARM_DIFFICULTY
			+ " INTEGER NOT NULL, "
			+ Constant.COLUMN_ALARM_TONE
			+ " TEXT NOT NULL, "
			+ Constant.COLUMN_ALARM_VIBRATE
			+ " INTEGER NOT NULL, "
			+ Constant.COLUMN_ALARM_NAME + " TEXT NOT NULL, " 
			
			
			+ Constant.COLUMN_ALARM_FOREIGN_KEY_USER_ID
			+ " INTEGER , FOREIGN KEY( "
			+ Constant.COLUMN_ALARM_FOREIGN_KEY_USER_ID + " ) REFERENCES   "
			+ Constant.USER_TABLE 
			+ " ( " + Constant.COLUMN_USER_ID + " ))";
				//	")";

	/* ======================== Alarm表定义结束 ========================= */

	/* ======================== User表定义开始 ========================= */

	/** user表名称 */
	public static final String USER_TABLE = "user";

	/** user表列名：主键 */
	public static final String COLUMN_USER_ID = "_id";

	/** user表列名：用户名 */
	public static final String COLUMN_USER_USERNAME = "username";

	/** user表列名：密码 */
	public static final String COLUMN_USER_PASSWORD = "password";

	/** 删除user表 */
	public static final String TABLE_USER_DROP = "DROP TABLE IF EXISTS "+ USER_TABLE;
	
	/** 创建user表 */
	public static final String TABLE_USER_CREATE = "CREATE TABLE IF NOT EXISTS "+USER_TABLE + " ( "
			+ COLUMN_USER_ID + " INTEGER primary key autoincrement, " 
			+ COLUMN_USER_USERNAME	+ " TEXT NOT NULL, "
			+ COLUMN_USER_PASSWORD + " TEXT NOT NULL)";
	
	/* ======================== User表定义结束 ========================= */
}
