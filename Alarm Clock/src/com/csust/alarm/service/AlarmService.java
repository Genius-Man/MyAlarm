package com.csust.alarm.service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.csust.alarm.bean.Alarm;
import com.csust.alarm.activityservice.CommonService;
import com.csust.alarm.alert.AlarmAlertBroadcastReciever;
import com.csust.alarm.dao.AlarmDB;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

public class AlarmService extends Service {

	SharedPreferences sharedPreferences;
	
	CommonService comService = new CommonService(getBaseContext());
	int userId;
	
	@Override
	public IBinder onBind(Intent intent) {

		
		return null;
	}

	@Override
	public void onCreate() {
		
		Log.d(this.getClass().getSimpleName(),"onCreate()方法");
		super.onCreate();		
		
		
	}

	/**
	 * 获取下一个响铃的闹钟
	 * @return
	 */
	private Alarm getNext(){		
		
		Set<Alarm> alarmQueue = new TreeSet<Alarm>(new Comparator<Alarm>() {
			@Override
			public int compare(Alarm lhs, Alarm rhs) {
				int result = 0;
				
				/* 比较两个闹铃的时间，按响铃时间降序排列 */
				long diff = lhs.getAlarmTime().getTimeInMillis() - rhs.getAlarmTime().getTimeInMillis();				
				if(diff>0){
					return 1;
				}else if (diff < 0){
					return -1;
				}
				return result;
			}
		});
		
		/* 初始化数据库，等待操作数据库 */
		AlarmDB.init(getApplicationContext());
		
		/* 获取当前登录用户的闹钟 */
		List<Alarm> alarms = AlarmDB.getAll(userId);
		
		for(Alarm alarm : alarms){
			if(alarm.getAlarmActive())
				alarmQueue.add(alarm);
		}
		
		
		if(alarmQueue.iterator().hasNext()){
			
			/* 获取按降序排列的最后一个闹钟，即下一次响铃的闹钟 */
			return alarmQueue.iterator().next();
		}else{
			return null;
		}
	}
	
	@Override
	public void onDestroy() {
		
		/* 关闭数据库 */
		AlarmDB.deactivate();
		super.onDestroy();
	}

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		Log.d(this.getClass().getSimpleName(),"onStartCommand()方法");
		
		sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
		userId = comService.getSharedUserId(sharedPreferences);
		
		Log.i(this.getClass().getSimpleName(),"onStartCommand()方法 userId = "+ userId);
		
		/*  获取最近一个响铃的闹钟 */
		Alarm alarm = getNext();
		
		if(null != alarm){
			
			/* 往系统添加闹铃计划 */
			alarm.schedule(getApplicationContext());
			
			Log.d(this.getClass().getSimpleName(),alarm.getTimeUntilNextAlarmMessage());
			
		}else{
			
			Log.d(this.getClass().getSimpleName(),"找不到闹钟队列");
			
			/* 
			 * 取消闹铃计划
			 */
			Intent myIntent = new Intent(getApplicationContext(), AlarmAlertBroadcastReciever.class);
			myIntent.putExtra("alarm", new Alarm());
			
			/* 
			 * 如果PendingIntent已经存在，
			 * 那么取消掉当前的PendingIntent，
			 * 然后产生一个新的PendingIntent。
			 * (先使用默认数据替换掉已经存在的任务，然后在取消计划)
			 * 确保没有查询到闹钟时，闹钟不会因为之前的计划而相邻
			 */
			PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent,PendingIntent.FLAG_CANCEL_CURRENT);			
			AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
			
			alarmManager.cancel(pendingIntent);
		}
		
		
		return START_NOT_STICKY;
	}

}
