package com.csust.alarm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

import com.csust.alarm.activityservice.CommonService;
import com.csust.alarm.activityservice.LoginService;
import com.csust.alarm.bean.User;
import com.csust.alarm.dao.impl.UserDBImpl;
import com.csust.alarm.util.StringUtil;

public class LoginActivity extends BaseActivity implements
		OnCheckedChangeListener {

	/* 用户名 密码输入框 */
	private EditText et_username, et_pwd;

	/* 登录 注册 跳转至注册界面 按钮 */
	private Button bt_login, bt_to_register;

	/* 记住密码复选框 */
	private CheckBox cb_rem_pwd;

	/* 共享首选项，用来保存用户信息 */
	private SharedPreferences sharedPreferences;
	private LoginService loginService = new LoginService(
			getBaseContext());
	
	private CommonService comService = new CommonService(getBaseContext());

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.bt_login:

			/* 初始化数据库 */
			UserDBImpl.init(getApplicationContext());

			/* 查询数据库 校验输入的信息 */
			String username = et_username.getText().toString();
			String password = et_pwd.getText().toString();

			boolean isLogin = loginService.checkLogin(username, password);

			if (isLogin) {

				
				StringUtil.showToast(this, "登录成功");

				User user = loginService.getUser(username, password);
				
				/* 保存用户信息 */
				if(cb_rem_pwd.isChecked()){
					
					comService.saveUser(sharedPreferences,user);
				}
				/*
				 * 将用户ID存入SharedPreference， 保存为loginedId，并跳转至 AlarmActivity
				 */
				loginService.saveUserId(sharedPreferences,user.getId());

				Intent intent = new Intent(this, AlarmActivity.class);
				startActivity(intent);
			} else {
				
				StringUtil.showToast(this, "用户名或密码输入错误");
			}
			break;
			
		case R.id.bt_to_register:
			
			/* 跳转至注册界面 */
			Intent intent = new Intent(this,RegisterActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);

		/* 初始化组件 */
		initViews();

		/* 初始化监听器 */
		initListener();
		
		/* 
		 * 如果之前记住了用户名和密码
		 * 则把user信息填入输入框 
		 * 
		 * */
		initData();
	}

	/**
	 * 自动填入信息
	 */
	private void initData() {
		
		User user = comService.getSharedUser(sharedPreferences);
		
		if(user != null){
			
			et_username.setText(user.getUsername());
			
			et_pwd.setText(user.getPassword());
			
		}
		
	}

	/**
	 * 添加组件的监听事件
	 */
	private void initListener() {

		bt_login.setOnClickListener(this);
		// bt_register.setOnClickListener(this);
		bt_to_register.setOnClickListener(this);

		cb_rem_pwd.setOnCheckedChangeListener(this);
	}

	/**
	 * 找到各个组件 并初始化
	 */
	private void initViews() {

		/* 获取用户名、密码输入框的组件 */
		et_username = (EditText) findViewById(R.id.et_username);
		et_pwd = (EditText) findViewById(R.id.et_pwd);

		/* 获取登录、注册、跳转至注册界面的按钮 */
		bt_login = (Button) findViewById(R.id.bt_login);
		// bt_register = (Button) findViewById(R.id.bt_register);
		bt_to_register = (Button) findViewById(R.id.bt_to_register);

		/* 获取记住密码的复选框 */
		cb_rem_pwd = (CheckBox) findViewById(R.id.cb_rem_pwd);
		
		
		sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);

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
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		switch (buttonView.getId()) {
		case R.id.cb_rem_pwd:

			/* 将用户信息加密保存在SharedPreference里 */
			
			break;
		default:
			break;
		}

	}

}
