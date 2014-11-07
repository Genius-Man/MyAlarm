package com.csust.alarm.alert;

import com.csust.alarm.bean.Alarm;
import com.csust.alarm.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.HapticFeedbackConstants;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class AlarmAlertActivity extends Activity implements OnClickListener {

	private Alarm alarm;
	private MediaPlayer mediaPlayer;

	/* 响铃时左声道音量 */
	static final float  LEFT_VOLUME = 2.0f;
	
	/* 响铃时的右声道音量 */
	static final float RIGHT_VOLUME = 2.0f;
	// 用户输入的数学问题结果
	private StringBuilder answerBuilder = new StringBuilder();

	/* 数学问题 */
	private MathProblem mathProblem;
	
	/* 震动器 */
	private Vibrator vibrator;

	/* 闹钟是否处于活动状态（闹钟是否在响，在响时点击返回键无效） */
	private boolean alarmActive;

	/* 显示数学问题的组件 */
	private TextView problemView;
	
	/* 显示用户输入的答案的组件 */
	private TextView answerView;

	// 数学问题的答案
	private String answerString;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		final Window window = getWindow();

		/*
		 * 设置标志位，让应用在锁屏状态下能够显示 并自动清除所有的非安全类的锁屏界面
		 * 
		 * 确保在关屏状态下，闹钟响起时能自动清除锁屏，并显示数学问题界面
		 * 
		 */
		window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

		/*
		 * 保持屏幕常亮
		 */
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		setContentView(R.layout.alarm_alert);

		// ************获取从其他Activity传过来的alarm对象************
		Bundle bundle = this.getIntent().getExtras();
		alarm = (Alarm) bundle.getSerializable("alarm");

		// ***********改变Activity显示的名称************
		this.setTitle(alarm.getAlarmName());

		// ***********获取数学问题的难易程度************
		// ****根据难易程度分别产生对应个数的的运算因子*****
		switch (alarm.getDifficulty()) {
		case EASY:
			mathProblem = new MathProblem(3);
			break;
		case MEDIUM:
			mathProblem = new MathProblem(5);
			break;
		case HARD:
			mathProblem = new MathProblem(7);
			break;
		}

		// ************ 获取产生的数学问题的答案*************
		answerString = String.valueOf(mathProblem.getAnswer());
		
		if (answerString.endsWith(".0")) {
			answerString = answerString.substring(0, answerString.length() - 2);
		}

		initView();

		/*
		 * 设置 按键 0 - 9 等等的监听事件
		 */
		setListener();

		/* 闹钟响铃时，来电、正在接听电话等情况的处理 */
		TelephonyManager telephonyManager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);

		PhoneStateListener phoneStateListener = new PhoneStateListener() {
			
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				
				switch (state) {
				case TelephonyManager.CALL_STATE_RINGING:
					Log.d(getClass().getSimpleName(), "有电话打来 "
							+ incomingNumber);
					try {
						/* 有电话打来时，暂停闹铃 */
						mediaPlayer.pause();
					} catch (IllegalStateException e) {

					}
					break;
				case TelephonyManager.CALL_STATE_IDLE:
					Log.d(getClass().getSimpleName(), "电话挂断状态");
					try {
						/* 电话挂断状态时，闹铃响起 */
						mediaPlayer.start();
					} catch (IllegalStateException e) {

					}
					break;
				}
				super.onCallStateChanged(state, incomingNumber);
			}
		};

		/* 设置来电等的监听 */
		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_CALL_STATE);

		// ******** 闹钟响铃开始 ********
		startAlarm();

	}

	private void initView() {
		
		problemView = (TextView) findViewById(R.id.textView1);
		problemView.setText(mathProblem.toString());

		answerView = (TextView) findViewById(R.id.textView2);
		answerView.setText("= ?");
	}

	/**
	 * 各个组件的监听事件
	 */
	private void setListener() {

		((Button) findViewById(R.id.Button0)).setOnClickListener(this);
		((Button) findViewById(R.id.Button1)).setOnClickListener(this);
		((Button) findViewById(R.id.Button2)).setOnClickListener(this);
		((Button) findViewById(R.id.Button3)).setOnClickListener(this);
		((Button) findViewById(R.id.Button4)).setOnClickListener(this);
		((Button) findViewById(R.id.Button5)).setOnClickListener(this);
		((Button) findViewById(R.id.Button6)).setOnClickListener(this);
		((Button) findViewById(R.id.Button7)).setOnClickListener(this);
		((Button) findViewById(R.id.Button8)).setOnClickListener(this);
		((Button) findViewById(R.id.Button9)).setOnClickListener(this);
		((Button) findViewById(R.id.Button_clear)).setOnClickListener(this);
		((Button) findViewById(R.id.Button_decimal)).setOnClickListener(this);
		((Button) findViewById(R.id.Button_minus)).setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		alarmActive = true;
	}

	/**
	 * 闹钟开始响铃
	 */
	private void startAlarm() {

		if (alarm.getAlarmTonePath() != "") {

			// ******* 初始化播放器 *******
			mediaPlayer = new MediaPlayer();

			if (alarm.getVibrate()) {

				// ****** 调用系统的震动服务 *********
				vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

				/*
				 * 1000:振动器开启之前等待1秒 200: 在震动器关闭之后再持续震动200ms
				 * 
				 * 之后的200,200或者更多的整型数据表示振动器 震动和停止的时间
				 */
				long[] pattern = { 1000, 200, 200, 200 };

				// *********** 0 表示重复震动 ************
				vibrator.vibrate(pattern, 0);
			}

			try {

				// ******* 响铃时，将音量为设置为最大 *******
				mediaPlayer.setVolume(LEFT_VOLUME, RIGHT_VOLUME);

				// ******* 设置播放的铃声路径(从对象里取) *******
				mediaPlayer.setDataSource(this,
						Uri.parse(alarm.getAlarmTonePath()));
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);

				// ******* 闹铃循环播放 ********
				mediaPlayer.setLooping(true);

				mediaPlayer.prepare();
				mediaPlayer.start();

			} catch (Exception e) {
				mediaPlayer.release();
				alarmActive = false;
			}
		}

	}

	@Override
	public void onBackPressed() {
		
		/* 闹钟还在响时 点击返回键无效 */
		if (!alarmActive)
			super.onBackPressed();
	}

	@Override
	protected void onPause() {
		super.onPause();
		StaticWakeLock.lockOff(this);
	}

	/**
	 * Activity 结束时 释放掉占用的资源 包括释放振动器（取消震动） 停止闹铃，释放媒体资源
	 */
	@Override
	protected void onDestroy() {

		releaseSource();

		super.onDestroy();
	}

	/**
	 * 释放资源 包括释放振动器（取消震动） 停止闹铃，释放媒体资源
	 */
	private void releaseSource() {
		try {

			if (vibrator != null)
				vibrator.cancel();
		} catch (Exception e) {

		}
		try {
			mediaPlayer.stop();
		} catch (Exception e) {

		}
		try {
			mediaPlayer.release();
		} catch (Exception e) {

		}
	}

	@Override
	public void onClick(View v) {
		
		if (!alarmActive)
			return;
		
		String button = (String) v.getTag();
		
		v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
		
		if (button.equalsIgnoreCase("clear")) {
			
			/* 清除按键 当前字符串长度减 1 */
			if (answerBuilder.length() > 0) {
				answerBuilder.setLength(answerBuilder.length() - 1);
				answerView.setText(answerBuilder.toString());
			}
		} else if (button.equalsIgnoreCase(".")) {
			
			/* 如果之前还没有输入任何东西，点击按键时前面自动补0 即：0.*/
			if (!answerBuilder.toString().contains(button)) {
				if (answerBuilder.length() == 0)
					answerBuilder.append(0);
				
				answerBuilder.append(button);
				answerView.setText(answerBuilder.toString());
			}
		} else if (button.equalsIgnoreCase("-")) {
			if (answerBuilder.length() == 0) {
				answerBuilder.append(button);
				answerView.setText(answerBuilder.toString());
			}
		} else {
			answerBuilder.append(button);
			answerView.setText(answerBuilder.toString());

			if (isAnswerCorrect()) {

				// 用户输入了正确答案，设置闹钟停止判断标志
				alarmActive = false;

				// 释放资源
				releaseSource();

				// 退出当前Activity
				this.finish();
			}
		}

		if (answerView.getText().length() >= answerString.length()
				&& !isAnswerCorrect()) {
			answerView.setTextColor(Color.RED);
		} else {
			answerView.setTextColor(Color.BLACK);
		}
	}

	/**
	 * 判断用户是否答对了随机产生的数学题
	 * 
	 * @return 答对了返回true,答错了返回false
	 */
	public boolean isAnswerCorrect() {
		boolean correct = false;
		try {
			correct = mathProblem.getAnswer() == Float.parseFloat(answerBuilder
					.toString());
			
			Log.i("AlarmAlertActivity","答案："+mathProblem.getAnswer()
					+"输入的答案："+Float.parseFloat(answerBuilder
							.toString()));
		} catch (NumberFormatException e) {
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return correct;
	}

}
