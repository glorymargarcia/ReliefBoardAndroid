package com.stratpoint.reliefboard;

import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.stratpoint.reliefboard.loader.ResponseLoader;
import com.stratpoint.reliefboard.util.ReliefBoardConstants;
import com.stratpoint.reliefboardandroid.R;

public class ResponseActivity extends SherlockFragmentActivity implements LoaderManager.LoaderCallbacks<JSONObject>{

	private EditText mName;
	private EditText mMessage;
	private String mPostId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_response);
		
		if(getIntent().getExtras() != null) {
//			mPost = getIntent().getExtras().getParcelable(ReliefBoardConstants.Extra.POST);
			mPostId = getIntent().getExtras().getString(ReliefBoardConstants.Extra.POST_ID);
		}
		
		mName = (EditText) findViewById(R.id.et_name);
		mMessage = (EditText) findViewById(R.id.et_message);
		Button respond = (Button) findViewById(R.id.btn_respond);

		respond.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getSupportLoaderManager().restartLoader(ReliefBoardConstants.LoaderId.Response, null, mRespond);
			}
		});
		
	}
	
	private final LoaderManager.LoaderCallbacks<JSONObject> mRespond = new LoaderManager.LoaderCallbacks<JSONObject>() {
		
		@Override
		public Loader<JSONObject> onCreateLoader(int id, Bundle bundle) {
			ResponseLoader responseLoader = new ResponseLoader(getApplicationContext());
			responseLoader.setAppId(getResources().getString(R.string.app_id));
			responseLoader.setName(mName.getText().toString());
			responseLoader.setMessage(mMessage.getText().toString());
			responseLoader.setParentId(mPostId);
			return responseLoader;
		}
		
		@Override
		public void onLoadFinished(Loader<JSONObject> loader, JSONObject result) {
			Log.d("RespondResult", result.toString());
		}
		
		@Override
		public void onLoaderReset(Loader<JSONObject> arg0) { }
	};

	@Override
	public Loader<JSONObject> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onLoadFinished(Loader<JSONObject> arg0, JSONObject arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoaderReset(Loader<JSONObject> arg0) {
		// TODO Auto-generated method stub
		
	}
}
