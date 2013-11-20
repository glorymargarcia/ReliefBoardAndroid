package com.stratpoint.reliefboard;

import java.util.Timer;
import java.util.TimerTask;

import com.stratpoint.reliefboardandroid.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

public class SplashActivity extends Activity {

	private long splashDelay = 1500;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash);
		
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				startActivity(new Intent(SplashActivity.this, ReliefBoardShowPost.class));
			}
		};
		
		Timer timer = new Timer();
		timer.schedule(task, splashDelay);
	}
	
}
