package com.cmic.sso;

import android.app.Application;

import com.umeng.analytics.MobclickAgent;

public class MyApplication extends Application {

	String TAG = "MyApplication";

	public static String umcUID = "";
	public static String umcPassId = "";

	private static MyApplication application;

	public static MyApplication getApplication() {
		return application;
	}

	public void onCreate() {
		super.onCreate();
		//设置为普通统计场景
		MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType. E_UM_NORMAL);
		application = this;
	}
}
