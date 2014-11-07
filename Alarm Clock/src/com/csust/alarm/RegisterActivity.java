package com.csust.alarm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;

import com.csust.alarm.activityservice.LoginService;
import com.csust.alarm.activityservice.RegisterService;
import com.csust.alarm.bean.User;
import com.csust.alarm.dao.AlarmDB;
import com.csust.alarm.dao.impl.UserDBImpl;
import com.csust.alarm.util.StringUtil;

public class RegisterActivity extends BaseActivity implements
		OnFocusChangeListener {

	/* 用户名 密码 确认密码输入框 */
	private EditText et_add_username, et_add_password, et_confirm_password;

	/* 注册按钮 跳转至登录按钮 */
	private Button bt_register, bt_to_login;

	/* */
	private RegisterService registerService = new RegisterService(
			getBaseContext());
	
	private LoginService login = new LoginService(getBaseContext());
	
	/* 用户名 密码 确认密码输入框 */
	private String username,password,confirmPassword;

	private SharedPreferences sharedPreferences;
	/* */
	//private boolean shouldRegister = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.register_layout);

		initView();

		initListner();
	}

	private void initListner() {

		et_add_username.setOnFocusChangeListener(this);
		et_add_password.setOnFocusChangeListener(this);
		et_confirm_password.setOnFocusChangeListener(this);

		bt_register.setOnClickListener(this);
		bt_to_login.setOnClickListener(this);
	}

	/**
	 * 初始化组件
	 */
	private void initView() {

		/* 用户名 密码 确认密码输入框 */
		et_add_username = (EditText) findViewById(R.id.et_add_username);

		et_add_password = (EditText) findViewById(R.id.et_add_password);

		et_confirm_password = (EditText) findViewById(R.id.et_confirm_password);

		/* 注册按钮 跳转至登录按钮 */
		bt_register = (Button) findViewById(R.id.bt_register);
		bt_to_login = (Button) findViewById(R.id.bt_to_login);
		
		
		sharedPreferences = getSharedPreferences("user",MODE_PRIVATE);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.bt_register:

			/* 获取用户名 密码 确认密码  */
			String username = et_add_username.getText().toString();
			String password = et_add_password.getText().toString();
			String confirmPassword = et_confirm_password.getText().toString();
			
			boolean shouldRegister =  registerService.validationRegister(username,password,confirmPassword);
			
			if(shouldRegister){
				
				/* 初始化数据库 */
				UserDBImpl.init(getApplicationContext());
				
				User user = new User();
				user.setUsername(username);
				
				/* 密码转成md5加密后存入数据库 */
				user.setPassword(StringUtil.toMd5String(password));
				
				boolean isSave = registerService.insertUser(user);
				
				if(isSave){
					
					/* 
					 * 注册成功将已经保存的、外键user_id为0的闹钟 添加至当前用户 
					 * 
					 * 更新loginedId
					 * 
					 * */
					User u = login.getUser(username, password);
					
					/* 更新loginedId */
					login.saveUserId(sharedPreferences, u.getId());
					
					/* Alarm表 初始化 */
					AlarmDB.init(getApplicationContext());
					
					registerService.updateAlarmToCurrentUser(u.getId());
					
					StringUtil.showToast(this, "注册成功，正在更新闹钟列表");
					
					Intent intent = new Intent(this,AlarmActivity.class);
					startActivity(intent);
				}else{
					StringUtil.showToast(this, "系统错误，注册失败，请稍候再试");
				}
				
			}else{
				StringUtil.showToast(this, "信息输入有误");
			}
			break;
		case R.id.bt_to_login:
			break;
		default:
			break;
		}

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
	public void onFocusChange(View v, boolean hasFocus) {

		/*
		 * 失去焦点事件 全部满足条件后登录按钮才变为可点击的
		 */

//		String username = null,password = null,confirmPassword = null;
		
		if (!hasFocus) {
			switch (v.getId()) {
			/* 用户名输入框的失去焦点事件：失去焦点时检查输入的用户名是否已经被占用 */
			case R.id.et_add_username:

				/* 初始化数据库 */
				UserDBImpl.init(getApplicationContext());
				
				username = et_add_username.getText().toString();

				boolean isUsername = registerService
						.validationUsername(username);

				/* 用户名被占用 */
				if (!isUsername) {
					StringUtil.showToast(this, "用户名被占用");
				}

				break;
			/* 密码输入框的失去焦点事件：失去焦点时检查密码长度是否符合要求 */
			case R.id.et_add_password:

				password = et_add_password.getText().toString();
				boolean isPassword = registerService
						.validationPassword(password);

				if (!isPassword) {

					StringUtil.showToast(this, "请输入6-16位的字母、数字、下划线");
				}
				break;
			/* 确认密码输入框失去焦点事件:失去焦点时检查是否和输入的密码一致 */
			case R.id.et_confirm_password:

				confirmPassword = et_confirm_password.getText()
						.toString();
				
				boolean isRePassword = registerService
						.validationConfirmPassword(password,confirmPassword);

				if (!isRePassword) {

					StringUtil.showToast(this, "两次密码输入不一致");
				}
				break;
			default:
				break;
			}
		}
	}
}
