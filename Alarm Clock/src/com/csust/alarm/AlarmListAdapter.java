package com.csust.alarm;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.csust.alarm.bean.Alarm;
import com.csust.alarm.util.Constant;


public class AlarmListAdapter extends BaseAdapter {

	private AlarmActivity alarmActivity;
	private List<Alarm> alarms = new ArrayList<Alarm>();

	/* 闹钟的激活状态 响铃时间 重复天数 等成员变量 */
	public static final String ALARM_FIELDS[] = { Constant.COLUMN_ALARM_ACTIVE,
		Constant.COLUMN_ALARM_TIME, Constant.COLUMN_ALARM_DAYS };

	public AlarmListAdapter(AlarmActivity alarmActivity) {

		this.alarmActivity = alarmActivity;

	}

	@Override
	public int getCount() {
		return alarms.size();
	}

	@Override
	public Object getItem(int position) {
		return alarms.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		if (null == view)
			view = LayoutInflater.from(alarmActivity).inflate(
					R.layout.alarm_list_element, null);

		/* 获取闹钟 */
		Alarm alarm = (Alarm) getItem(position);

		/* 设置闹钟界面的复选框 */
		CheckBox checkBox = (CheckBox) view
				.findViewById(R.id.checkBox_alarm_active);
		checkBox.setChecked(alarm.getAlarmActive());
		checkBox.setTag(position);
		checkBox.setOnClickListener(alarmActivity);

		/* 显示闹钟的时间 */
		TextView alarmTimeView = (TextView) view
				.findViewById(R.id.textView_alarm_time);
		alarmTimeView.setText(alarm.getAlarmTimeString());

		/* 显示重复天数 */
		TextView alarmDaysView = (TextView) view
				.findViewById(R.id.textView_alarm_days);
		alarmDaysView.setText(alarm.getRepeatDaysString());

		return view;
	}

	public List<Alarm> getMathAlarms() {
		return alarms;
	}

	public void setMathAlarms(List<Alarm> alarms) {
		this.alarms = alarms;
	}

}
