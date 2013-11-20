package com.stratpoint.reliefboard;

import org.json.JSONException;
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
import com.actionbarsherlock.view.MenuItem;
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

		getSupportActionBar().setTitle("Responses");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIcon(android.R.color.transparent);
		
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
			responseLoader.forceLoad();
			return responseLoader;
		}
		
		@Override
		public void onLoadFinished(Loader<JSONObject> loader, JSONObject result) {
			String status;
			try {
				status = result.getString("status");
				
				if(status.equals("Success")){
					mName.setText("");
					mMessage.setText("");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
//			RESPONSE
//			{"compress_output":true,"memory_usage":"1.07MB","method":"POST","status":"Success","ellapsed_time":8.4183828830719}
		}
		
		@Override
		public void onLoaderReset(Loader<JSONObject> arg0) { }
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public Loader<JSONObject> onCreateLoader(int arg0, Bundle arg1) { return null; }

	@Override
	public void onLoadFinished(Loader<JSONObject> arg0, JSONObject arg1) {}

	@Override
	public void onLoaderReset(Loader<JSONObject> arg0) {}
}
