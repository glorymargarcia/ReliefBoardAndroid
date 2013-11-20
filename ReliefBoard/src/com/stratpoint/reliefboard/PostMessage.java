package com.stratpoint.reliefboard;

import com.stratpoint.reliefboardandroid.R;
import com.stratpoint.reliefboardandroid.R.layout;
import com.stratpoint.reliefboardandroid.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class PostMessage extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post__message);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.post__message, menu);
		return true;
	}

}
