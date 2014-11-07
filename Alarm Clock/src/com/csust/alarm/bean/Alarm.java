package com.csust.alarm.bean;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.csust.alarm.alert.AlarmAlertBroadcastReciever;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.util.Log;

public class Alarm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 数学问题的难易程度选择
	 * 
	 * @author xiaosir
	 *
	 */
	public enum Difficulty{
		EASY,
		MEDIUM,
		HARD;
		
		@Override
		public String toString() {
			switch(this.ordinal()){
				case 0:
					return "简单";
				case 1:
					return "中等";
				case 2:
					return "困难";
			}
			return super.toString();
		}
	}
	
	/**
	 * 闹钟响铃天数的选择
	 * @author xiaosir
	 *
	 */
	public enum Day{
		SUNDAY,
		MONDAY,
		TUESDAY,
		WEDNESDAY,
		THURSDAY,
		FRIDAY,
		SATURDAY;

		@Override
		public String toString() {
			switch(this.ordinal()){
			
			
			case 0:
				return "周日";
			case 1:
				return "周一";
			case 2:
				return "周二";
			case 3:
				return "周三";
			case 4:
				return "周四";
			case 5:
				return "周五";
			case 6:
				return "周六";

			}
			return super.toString();
		}
		
	}
	
	
	/* 闹钟主键Id */
	private int id;
	
	/* 闹钟是否被激活 默认为激活 */
	private Boolean alarmActive = true;
	
	/* 闹钟响铃时间 默认为当前时间 */
	private Calendar alarmTime = Calendar.getInstance();
	
	/* 闹钟重复天数 */
	private Day[] days = {Day.MONDAY,Day.TUESDAY,Day.WEDNESDAY,Day.THURSDAY,Day.FRIDAY,Day.SATURDAY,Day.SUNDAY};	
	
	/* 闹钟铃声路径 */
	private String alarmTonePath = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString();
	
	/* 闹钟是否震动 默认震动 */
	private Boolean vibrate = true;
	
	/* 闹钟标签 默认为*/
	private String alarmName = "趣味闹钟";
	
	/* 数学问题的难易程度 默认简单 */
	private Difficulty difficulty = Difficulty.EASY;
	
	
	private int userId;
	public Alarm() {

	}



	/**
	 * @return 闹钟是否处于活跃状态
	 */
	public Boolean getAlarmActive() {
		return alarmActive;
	}

	/**
	 * @param alarmActive
	 *            设置活跃状态
	 */
	public void setAlarmActive(Boolean alarmActive) {
		this.alarmActive = alarmActive;
	}

	/**
	 * @return 响铃时间
	 */
	public Calendar getAlarmTime() {
		
		/* 
		 * 如果设置的响铃时间在当前系统之前，
		 * 则说明下次响铃时间为明天的alarmTime。
		 * 如设置的alarmTime为12:00，但此时系统时间为
		 * 13：00，则说明该闹铃下一次响铃时间为明天的
		 * 12:00，天数加一
		 * 
		 */
		if (alarmTime.before(Calendar.getInstance()))
			alarmTime.add(Calendar.DAY_OF_MONTH, 1);
		
	
		/*
		 * alarmTime.get(Calendar.DAY_OF_WEEK):
		 * 获取系统当前时间是周几，周日为 1 周一为2 依次类推
		 *
		 */
		while(!Arrays.asList(getDays()).contains
				(Day.values()[alarmTime.get(Calendar.DAY_OF_WEEK)-1])){
			
			Log.i("Alarm", "while 循环："+Day.values()[alarmTime.get(Calendar.DAY_OF_WEEK)-1].toString());
			alarmTime.add(Calendar.DAY_OF_MONTH, 1);			
		}
		
		Log.i("Alarm",getAlarmTimeString()+ Day.values()[alarmTime.get(Calendar.DAY_OF_WEEK)-1].toString());
		
		return alarmTime;
	}

	/**
	 * @return 响铃时间字符串表示 00:00（时：分）
	 */
	public String getAlarmTimeString() {

		String time = "";
		
		/* 时间小于 10，前面添加 0  填充 */
		if (alarmTime.get(Calendar.HOUR_OF_DAY) <= 9)
			time += "0";
		time += String.valueOf(alarmTime.get(Calendar.HOUR_OF_DAY));
		time += ":";

		if (alarmTime.get(Calendar.MINUTE) <= 9)
			time += "0";
		time += String.valueOf(alarmTime.get(Calendar.MINUTE));

		return time;
	}

	/**
	 * @param alarmTime
	 *            设置响铃时间
	 */
	public void setAlarmTime(Calendar alarmTime) {
		this.alarmTime = alarmTime;
	}

	/**
	 * @param alarmTime
	 *            设置响铃时间
	 */
	public void setAlarmTime(String alarmTime) {

		/* 分割形如 00:00 的字符串，得到设置闹钟时间的时钟与分钟数 */
		String[] timePieces = alarmTime.split(":");

		Calendar newAlarmTime = Calendar.getInstance();
		
		/* 设置响铃的时钟数 */
		newAlarmTime.set(Calendar.HOUR_OF_DAY,
				Integer.parseInt(timePieces[0]));
		
		/* 设置响铃的分钟数 */
		newAlarmTime.set(Calendar.MINUTE, Integer.parseInt(timePieces[1]));
		
		/* 设置响铃的秒钟数 */
		newAlarmTime.set(Calendar.SECOND, 0);
		
		/* 设置闹钟响铃时间 */
		setAlarmTime(newAlarmTime);		
	}

	/**
	 * @return 重复天数
	 */
	public Day[] getDays() {
		return days;
	}

	/**
	 * @param set
	 *            设置重复天数
	 */
	public void setDays(Day[] days) {
		this.days = days;
	}

	/**
	 * 添加一个天数 （周一、周二等）
	 * @param day
	 */
	public void addDay(Day day){
		
		boolean contains = false;
		
		/* 如果等待添加的天数已经存在集合中，则退出*/
		for(Day d : getDays())
			if(d.equals(day))
				contains = true;
		
		if(!contains){
			List<Day> result = new LinkedList<Day>();
			for(Day d : getDays())
				result.add(d);
			
			/* 将day添加进集合 */
			result.add(day);
			setDays(result.toArray(new Day[result.size()]));
		}
	}
	
	/**
	 * 
	 * @param day
	 */
	public void removeDay(Day day) {
	    
		List<Day> result = new LinkedList<Day>();
	    for(Day d : getDays())
	        if(!d.equals(day))
	            result.add(d);
	    setDays(result.toArray(new Day[result.size()]));
	}
	
	/**
	 * @return 铃声路径
	 */
	public String getAlarmTonePath() {
		return alarmTonePath;
	}

	/**
	 * @param 设置铃声路径
	 */
	public void setAlarmTonePath(String alarmTonePath) {
		this.alarmTonePath = alarmTonePath;
	}
	
	/**
	 * @return 获取该闹铃是否设置了震动
	 */
	public Boolean getVibrate() {
		return vibrate;
	}

	/**
	 * @param vibrate
	 *            设置震动
	 */
	public void setVibrate(Boolean vibrate) {
		this.vibrate = vibrate;
	}

	/**
	 * @return 闹钟名称
	 */
	public String getAlarmName() {
		return alarmName;
	}

	/**
	 * @param alarmName
	 *            设置闹钟名称
	 */
	public void setAlarmName(String alarmName) {
		this.alarmName = alarmName;
	}

	public Difficulty getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(Difficulty difficulty) {
		this.difficulty = difficulty;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}



	public void setUserId(int userId) {
		this.userId = userId;
	}



	/**
	 * 
	 * @return 重复响铃天数的字符串 形如 每天 或者 (周一,周二,等)
	 */
	public String getRepeatDaysString() {
		
		StringBuilder daysStringBuilder = new StringBuilder();
		
		if(getDays().length == Day.values().length){
			
			daysStringBuilder.append("每天");		
		}else{
			
			/* 将重复天数按 周日 周一 周二 等排列 */
			Arrays.sort(getDays(), new Comparator<Day>() {
				@Override
				public int compare(Day lhs, Day rhs) {
					
					return lhs.ordinal() - rhs.ordinal();
				}
			});
			
			for(Day d : getDays()){
				switch(d){
				case TUESDAY:
				case THURSDAY:

				default:
					daysStringBuilder.append(d.toString());		
					break;
				}
				
				daysStringBuilder.append(',');
			}
			
			daysStringBuilder.setLength(daysStringBuilder.length()-1);
		}
			
		return daysStringBuilder.toString();
	}

	/**
	 * 将闹铃添加至系统 
	 * @param context
	 */
	public void schedule(Context context) {
		
		setAlarmActive(true);
		
		/* 传送至闹钟响铃广播的意图Intent */
		Intent myIntent = new Intent(context, AlarmAlertBroadcastReciever.class);
		/* 将当前闹铃添加至Intent */
		myIntent.putExtra("alarm", this);

		/* 
		 * 延迟执行的 Intent 
		 * 收到广播后指定时间才会执行 
		 * PendingIntent.FLAG_CANCEL_CURRENT:如果已经有相同的任务存在，则取消之前的那一个，
		 * 并产生一个新的PendingIntent
		 * 防止重复添加相同任务。
		 */
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent,PendingIntent.FLAG_CANCEL_CURRENT);

		/* 获取系统的闹铃服务，设置响铃时间，指定时间到后执行 pendingIntent */
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

		/* 
		 * 设置响铃时间，指定时间到后PendingIntent执行
		 * 
		 * AlarmManager.RTC_WAKEUP：达到指定时间后，
		 * 
		 * 唤醒休眠中的设备
		 */
		alarmManager.set(AlarmManager.RTC_WAKEUP, getAlarmTime().getTimeInMillis(), pendingIntent);					
	}
	
	/**
	 * 
	 * @return 下一次响铃时间的提示字符串
	 */
	public String getTimeUntilNextAlarmMessage(){
		
		/* 下次响铃时间距当前时间的毫秒数 */
		long timeDifference = getAlarmTime().getTimeInMillis() - System.currentTimeMillis();
		
		/* 计算下次响铃时间距离当前时间的天数、小时数、分钟数、秒数*/
		long days = timeDifference / (1000 * 60 * 60 * 24);
		long hours = timeDifference / (1000 * 60 * 60) - (days * 24);
		long minutes = timeDifference / (1000 * 60) - (days * 24 * 60) - (hours * 60);
		long seconds = timeDifference / (1000) - (days * 24 * 60 * 60) - (hours * 60 * 60) - (minutes * 60);
		
		String alert = "闹钟将在 ";
		
		/* 拼接出：闹钟将在 *天*时*分*秒 后响起   */
		if (days > 0) {
			alert += String.format(
					"%d 天, %d 小时, %d 分钟  %d 秒", days,
					hours, minutes, seconds);
		} else {
			if (hours > 0) {
				alert += String.format("%d 小时, %d 分钟  %d 秒",
						hours, minutes, seconds);
			} else {
				if (minutes > 0) {
					alert += String.format("%d 分钟, %d 秒", minutes,
							seconds);
				} else {
					alert += String.format("%d 秒", seconds);
				}
			}
		}
		
		return alert+" 后响起";
	}
}
