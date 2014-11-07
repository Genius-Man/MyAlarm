package com.csust.alarm.util;

import java.security.MessageDigest;

import android.content.Context;
import android.widget.Toast;

public class StringUtil {

	/**
	 * 获取字符串的MD5值
	 * 
	 * @param key
	 * @return
	 */
	public static String toMd5String(String key) {
		MessageDigest digest = null;

		try {
			digest = MessageDigest.getInstance("MD5");
			digest.reset();
			digest.update(key.getBytes("UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 加密
		byte[] byteArray = digest.digest();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
				builder.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			} else {
				builder.append(Integer.toHexString(0xFF & byteArray[i]));
			}

		}
		return builder.toString();
	}
	
	/**
	 * 显示Toast信息
	 * @param context
	 * @param message
	 */
	public static void showToast(Context context,String message){
		
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}
}
