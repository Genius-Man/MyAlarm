package com.csust.alarm.alert;

import android.content.Context;
import android.os.PowerManager;

public class StaticWakeLock {
	
	private static PowerManager.WakeLock wl = null;

	@SuppressWarnings("deprecation")
	
	/**
	 * 执行当前任务时，屏幕保存常亮 
	 * @param context
	 */
	public static void lockOn(Context context) {
		
		/* 让设备在执行当前任务时 能长时间保持唤醒状态 */
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		
		if (wl == null)
			wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "MATH_ALARM");
		wl.acquire();
	}

	public static void lockOff(Context context) {
		try {
			if (wl != null)
				wl.release();
		} catch (Exception e) {
			
		}
	}
}