package com.csust.alarm.dao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.csust.alarm.bean.Alarm;
import com.csust.alarm.bean.Alarm.Difficulty;
import com.csust.alarm.util.Constant;

public class AlarmDB extends BaseDB/* extends SQLiteOpenHelper */{

	public AlarmDB(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	// public static final String ALARM_TABLE = "alarm";
	// public static final String COLUMN_ALARM_ID = "_id";
	// public static final String COLUMN_ALARM_ACTIVE = "alarm_active";
	// public static final String COLUMN_ALARM_TIME = "alarm_time";
	// public static final String COLUMN_ALARM_DAYS = "alarm_days";
	// public static final String COLUMN_ALARM_DIFFICULTY = "alarm_difficulty";
	// public static final String COLUMN_ALARM_TONE = "alarm_tone";
	// public static final String COLUMN_ALARM_VIBRATE = "alarm_vibrate";
	// public static final String COLUMN_ALARM_NAME = "alarm_name";

	/**
	 * 往数据库添加一个alarm实例
	 * 
	 * @param alarm
	 *            等待添加的闹钟
	 * @return
	 */
	public static long create(Alarm alarm) {

		ContentValues cv = new ContentValues();
		cv.put(Constant.COLUMN_ALARM_ACTIVE, alarm.getAlarmActive());
		cv.put(Constant.COLUMN_ALARM_TIME, alarm.getAlarmTimeString());

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = null;
			oos = new ObjectOutputStream(bos);
			oos.writeObject(alarm.getDays());
			byte[] buff = bos.toByteArray();

			cv.put(Constant.COLUMN_ALARM_DAYS, buff);

		} catch (Exception e) {
		}

		cv.put(Constant.COLUMN_ALARM_DIFFICULTY, alarm.getDifficulty()
				.ordinal());
		cv.put(Constant.COLUMN_ALARM_TONE, alarm.getAlarmTonePath());
		cv.put(Constant.COLUMN_ALARM_VIBRATE, alarm.getVibrate());
		cv.put(Constant.COLUMN_ALARM_NAME, alarm.getAlarmName());
		cv.put(Constant.COLUMN_ALARM_FOREIGN_KEY_USER_ID, alarm.getUserId());

		return getDatabase().insert(Constant.ALARM_TABLE, null, cv);
	}

	/**
	 * 更新闹钟
	 * 
	 * @param alarm
	 *            等待更新的闹钟
	 * @return
	 */
	public static int update(Alarm alarm) {
		
		ContentValues cv = new ContentValues();
		
		/*
		 * 将等待更新闹钟的各个字段值存入ContentValues 
		 */
		cv.put(Constant.COLUMN_ALARM_ACTIVE, alarm.getAlarmActive());
		cv.put(Constant.COLUMN_ALARM_TIME, alarm.getAlarmTimeString());

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = null;
			oos = new ObjectOutputStream(bos);
			oos.writeObject(alarm.getDays());
			byte[] buff = bos.toByteArray();

			cv.put(Constant.COLUMN_ALARM_DAYS, buff);

		} catch (Exception e) {
		}

		cv.put(Constant.COLUMN_ALARM_DIFFICULTY, alarm.getDifficulty()
				.ordinal());
		cv.put(Constant.COLUMN_ALARM_TONE, alarm.getAlarmTonePath());
		cv.put(Constant.COLUMN_ALARM_VIBRATE, alarm.getVibrate());
		cv.put(Constant.COLUMN_ALARM_NAME, alarm.getAlarmName());
		cv.put(Constant.COLUMN_ALARM_FOREIGN_KEY_USER_ID, alarm.getUserId());

		return getDatabase().update(Constant.ALARM_TABLE, cv,
				"_id=" + alarm.getId(), null);
	}

	/**
	 * 删除特定闹钟
	 * 
	 * @param alarm
	 * @return
	 */
	public static int deleteEntry(Alarm alarm) {
		return deleteEntry(alarm.getId());
	}

	/**
	 * 删除特定闹钟
	 * 
	 * @param id
	 * @return
	 */
	public static int deleteEntry(int id) {

		return getDatabase().delete(Constant.ALARM_TABLE,
				Constant.COLUMN_ALARM_ID + "=" + id, null);
	}

	/**
	 * 删除所有的闹钟
	 * 
	 * @return
	 */
	public static int deleteAll() {
		return getDatabase().delete(Constant.ALARM_TABLE, "1", null);
	}

	/**
	 * 获取特定Id的闹钟
	 * 
	 * @param id
	 * @return
	 */
	public static Alarm getAlarm(int id) {

		String[] columns = new String[] { Constant.COLUMN_ALARM_ID,
				Constant.COLUMN_ALARM_ACTIVE, Constant.COLUMN_ALARM_TIME,
				Constant.COLUMN_ALARM_DAYS, Constant.COLUMN_ALARM_DIFFICULTY,
				Constant.COLUMN_ALARM_TONE, Constant.COLUMN_ALARM_VIBRATE,
				Constant.COLUMN_ALARM_NAME,
				Constant.COLUMN_ALARM_FOREIGN_KEY_USER_ID };

		/* 查询条件 */
		String whereArgs = Constant.COLUMN_ALARM_ID + "=" + id;

		Cursor c = getDatabase().query(Constant.ALARM_TABLE, columns,
				whereArgs, null, null, null, null);

		Alarm alarm = null;

		/* 设置闹钟的各个属性 */
		if (c.moveToFirst()) {

			alarm = new Alarm();
			alarm.setId(c.getInt(1));
			alarm.setAlarmActive(c.getInt(2) == 1);
			alarm.setAlarmTime(c.getString(3));
			byte[] repeatDaysBytes = c.getBlob(4);

			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
					repeatDaysBytes);

			try {

				ObjectInputStream objectInputStream = new ObjectInputStream(
						byteArrayInputStream);

				Alarm.Day[] repeatDays;

				Object object = objectInputStream.readObject();

				if (object instanceof Alarm.Day[]) {

					repeatDays = (Alarm.Day[]) object;
					alarm.setDays(repeatDays);
				}
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			alarm.setDifficulty(Difficulty.values()[c.getInt(5)]);
			alarm.setAlarmTonePath(c.getString(6));
			alarm.setVibrate(c.getInt(7) == 1);
			alarm.setAlarmName(c.getString(8));
			alarm.setUserId(c.getInt(9));
		}

		c.close();
		return alarm;
	}

	/**
	 * 获取包含所有闹钟信息的Cursor
	 * 
	 * @param userId
	 * @return
	 */
	public static Cursor getCursor(int userId) {

		String[] columns = new String[] { Constant.COLUMN_ALARM_ID,
				Constant.COLUMN_ALARM_ACTIVE, Constant.COLUMN_ALARM_TIME,
				Constant.COLUMN_ALARM_DAYS, Constant.COLUMN_ALARM_DIFFICULTY,
				Constant.COLUMN_ALARM_TONE, Constant.COLUMN_ALARM_VIBRATE,
				Constant.COLUMN_ALARM_NAME,

				Constant.COLUMN_ALARM_FOREIGN_KEY_USER_ID };

		String whereArgs = Constant.COLUMN_ALARM_FOREIGN_KEY_USER_ID + " = "
				+ userId;
		return getDatabase().query(Constant.ALARM_TABLE, columns, whereArgs,
				null, null, null, null);
	}

	

	/**
	 * 获取所有的闹钟
	 * 
	 * @return
	 */
	public static List<Alarm> getAll(int userId) {

		List<Alarm> alarms = new ArrayList<Alarm>();

		Cursor cursor = AlarmDB.getCursor(userId);

		if (cursor.moveToFirst()) {

			do {
				// COLUMN_ALARM_ID,
				// COLUMN_ALARM_ACTIVE,
				// COLUMN_ALARM_TIME,
				// COLUMN_ALARM_DAYS,
				// COLUMN_ALARM_DIFFICULTY,
				// COLUMN_ALARM_TONE,
				// COLUMN_ALARM_VIBRATE,
				// COLUMN_ALARM_NAME

				Alarm alarm = new Alarm();

				alarm.setId(cursor.getInt(0));

				alarm.setAlarmActive(cursor.getInt(1) == 1);

				alarm.setAlarmTime(cursor.getString(2));

				byte[] repeatDaysBytes = cursor.getBlob(3);

				/* 用流的方式将重复天数读取出来 */
				ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
						repeatDaysBytes);

				try {

					ObjectInputStream objectInputStream = new ObjectInputStream(
							byteArrayInputStream);

					Alarm.Day[] repeatDays;

					Object object = objectInputStream.readObject();

					if (object instanceof Alarm.Day[]) {

						repeatDays = (Alarm.Day[]) object;
						/* 设置读取到的天数 */
						alarm.setDays(repeatDays);
					}
				} catch (StreamCorruptedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}

				alarm.setDifficulty(Difficulty.values()[cursor.getInt(4)]);
				alarm.setAlarmTonePath(cursor.getString(5));
				alarm.setVibrate(cursor.getInt(6) == 1);
				alarm.setAlarmName(cursor.getString(7));
				alarm.setUserId(cursor.getInt(8));

				alarms.add(alarm);

			} while (cursor.moveToNext());
		}

		cursor.close();
		return alarms;
	}
}