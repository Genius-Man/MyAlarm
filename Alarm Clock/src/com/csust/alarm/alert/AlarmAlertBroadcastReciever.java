package com.csust.alarm.alert;

import com.csust.alarm.bean.Alarm;
import com.csust.alarm.service.AlarmServiceBroadcastReciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AlarmAlertBroadcastReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		/*
		 * 当前任务执行时，马上通知 AlarmServiceBroadcastReciever 执行下一个闹铃任务
		 */
		Intent mathAlarmServiceIntent = new Intent(context,
				AlarmServiceBroadcastReciever.class);		
		context.sendBroadcast(mathAlarmServiceIntent, null);

		/* 执行当前任务时（显示打开的Activity时），让屏幕保持常亮 */
		StaticWakeLock.lockOn(context);
		
		/* 获取闹钟 */
		Bundle bundle = intent.getExtras();
		final Alarm alarm = (Alarm) bundle.getSerializable("alarm");

		/* 获取跳转至响铃时、显示的解答数学题目的Activity意图 */
		Intent mathAlarmAlertActivityIntent;
		mathAlarmAlertActivityIntent = new Intent(context,
				AlarmAlertActivity.class);

		/* 添加数据：待执行的闹钟*/
		mathAlarmAlertActivityIntent.putExtra("alarm", alarm);
		
		/* 
		 * 添加一个新任务至队列
		 * 如果之前有任务正在执行还没结束
		 * 则等之前任务结束之后，当前任务会继续执行
		 * （处理两个闹铃响铃相隔时间较短的情况）
		 */
		mathAlarmAlertActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		context.startActivity(mathAlarmAlertActivityIntent);
	}

}
