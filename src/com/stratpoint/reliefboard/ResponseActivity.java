package com.stratpoint.reliefboard;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.stratpoint.reliefboardandroid.R;

public class ResponseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_response);
		
		if(getIntent().getExtras() != null) {
//			mPost = getIntent().getExtras().getParcelable(ReliefBoardConstants.Extra.POST);
			String postId = getIntent().getExtras().getString("post_id");
			Log.d("Post", postId);
		}
	}
	
}
