package com.example.lloyd.multifunctionbed.app;

import android.app.Application;

import com.example.lloyd.multifunctionbed.utils.RudenessScreenHelper;

public class MyApplication extends Application {
	
	public static MyApplication app;
	
	@Override
	public void onCreate() {
		app=this;
		super.onCreate();
		new RudenessScreenHelper(this, 750).activate();//±©¡¶  ≈‰∆¡ƒª
	}

}
