
package com.csust.alarm;

import java.util.List;

import com.csust.alarm.bean.Alarm;
import com.csust.alarm.dao.AlarmDB;
import com.csust.alarm.preferences.AlarmPreferencesActivity;

import android.os.Bundle;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class AlarmActivity extends BaseActivity {

	ImageButton newButton;
	ListView mathAlarmListView;
	AlarmListAdapter alarmListAdapter;

	int userId;
	SharedPreferences sharedPreference;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.alarm_activity);

		mathAlarmListView = (ListView) findViewById(android.R.id.list);
		
		/* 以私有方式获取(创建)名为user.xml的文件，用于程序Activity之间数据共享 */
		sharedPreference = getSharedPreferences("user", MODE_PRIVATE);
		
		/* 条目长按事件，用于删除闹钟操作 */
		mathAlarmListView.setLongClickable(true);
		
		/* 添加组件的监听事件 */
		initListener();

		/* 发送闹钟广播，将已经设置好的闹钟添加进任务队列 */
		callMathAlarmScheduleService();

		alarmListAdapter = new AlarmListAdapter(this);
		this.mathAlarmListView.setAdapter(alarmListAdapter);
		
		
	}

	/**
	 * 事件监听
	 */
	private void initListener() {
		
		mathAlarmListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
				
				view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
				
				final Alarm alarm = (Alarm) alarmListAdapter.getItem(position);
				
				Builder dialog = new AlertDialog.Builder(AlarmActivity.this);
				dialog.setTitle("删除");
				dialog.setMessage("删除这个闹钟");
				
				dialog.setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						/*
						 * 删除当前选中的闹钟  
						 */
						AlarmDB.init(AlarmActivity.this);
						AlarmDB.deleteEntry(alarm);
						
						/* 重新调用闹钟任务安排服务，更新闹钟安排 */
						AlarmActivity.this.callMathAlarmScheduleService();
						
						/* 更新应显示的闹钟列表 */
						updateAlarmList(userId);
					}
				});
				
				
				dialog.setNegativeButton("取消", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

				dialog.show();

				return true;
			}
		});
		
		/*
		 * 跳转至添加闹钟的界面，显示当前选中闹钟的详细信息
		 * 
		 * 跳转后可以更改当前闹钟设置
		 */
		mathAlarmListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
				
				v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
				
				Alarm alarm = (Alarm) alarmListAdapter.getItem(position);
				Intent intent = new Intent(AlarmActivity.this, AlarmPreferencesActivity.class);
				intent.putExtra("alarm", alarm);
				startActivity(intent);
			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		boolean result = super.onCreateOptionsMenu(menu);
		
		/*
		 * 当前Activity不显示保存与删除菜单 
		 */
		menu.findItem(R.id.menu_item_save).setVisible(false);
		menu.findItem(R.id.menu_item_delete).setVisible(false);
	    return result;
	}
		
	@Override
	protected void onPause() {
		// setListAdapter(null);
		
		/* 关闭闹钟 */
		AlarmDB.deactivate();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		/* 以私有方式获取(创建)名为user.xml的文件，用于程序Activity之间数据共享 */
		sharedPreference = getSharedPreferences("user", MODE_PRIVATE);
		userId = sharedPreference.getInt("loginedId", 0);
		
		updateAlarmList(userId);
	}
	
	public void updateAlarmList(int userId){
		
		AlarmDB.init(AlarmActivity.this);
		final List<Alarm> alarms = AlarmDB.getAll(userId);
		
		alarmListAdapter.setMathAlarms(alarms);
		
		runOnUiThread(new Runnable() {
			public void run() {
				
				/* 重新载入闹钟列表 */
				AlarmActivity.this.alarmListAdapter.notifyDataSetChanged();				
				if(alarms.size() > 0){
					findViewById(android.R.id.empty).setVisibility(View.INVISIBLE);
				}else{
					findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		
		if (v.getId() == R.id.checkBox_alarm_active) {
			
			CheckBox checkBox = (CheckBox) v;
			
			Alarm alarm = (Alarm) alarmListAdapter.getItem((Integer) checkBox.getTag());
			
			alarm.setAlarmActive(checkBox.isChecked());
			
			AlarmDB.update(alarm);
			
			AlarmActivity.this.callMathAlarmScheduleService();
			
			if (checkBox.isChecked()) {
				Toast.makeText(AlarmActivity.this, alarm.getTimeUntilNextAlarmMessage(), Toast.LENGTH_LONG).show();
			}
		}

	}

}