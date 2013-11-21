package com.stratpoint.reliefboard;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.stratpoint.reliefboard.loader.ResponseLoader;
import com.stratpoint.reliefboard.util.ReliefBoardConstants;
import com.stratpoint.reliefboardandroid.R;

public class ResponsesActivity extends SherlockFragmentActivity implements LoaderManager.LoaderCallbacks<JSONObject>{

	private ListView mListReply;
	private String mPostMessage;
	private String mPostId;
	private String mUsername;
	private String mPlaceTag;
	
	private EditText mName;
	private EditText mMessage;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_replies);
		
		getSupportActionBar().setTitle("Responses");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIcon(android.R.color.transparent);
		
		if(getIntent().getExtras() != null){
			mPostMessage = getIntent().getExtras().getString(ReliefBoardConstants.Extra.POST_MESSAGE);
			mPostId = getIntent().getExtras().getString(ReliefBoardConstants.Extra.POST_ID);
			mUsername = getIntent().getExtras().getString(ReliefBoardConstants.Extra.USERNAME);
			mPlaceTag = getIntent().getExtras().getString(ReliefBoardConstants.Extra.PLACE_TAG);
		}
		
		setUpViews();
	}
	
	private void setUpViews(){
		mListReply = (ListView) findViewById(R.id.list_reply);
		mName = (EditText) findViewById(R.id.name);
		mMessage = (EditText) findViewById(R.id.message);
		
		Button respond = (Button) findViewById(R.id.btn_respond);
		respond.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String name = mName.getText().toString();
				String message = mMessage.getText().toString();
				if(!name.equals("") && !message.equals(""))
					getSupportLoaderManager().restartLoader(ReliefBoardConstants.LoaderId.Respond, null, mSendARespond);
				else
					Toast.makeText(getApplicationContext(), "Please compose a message. ", Toast.LENGTH_LONG).show();
			}
		});
		
		String[] values = new String[] { "Android List View", "Adapter implementation", "Simple List View In Android", "Create List View Android", "Android Example", "List View Source Code", "List View Array Adapter", "Android Example List View" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);
		
		View headerView = LayoutInflater.from(this).inflate(R.layout.adapter_post, null);
		
		TextView userPost = (TextView) headerView.findViewById(R.id.user_post);
		TextView userSender = (TextView) headerView.findViewById(R.id.user_name);
		userPost.setText(mPostMessage);
		userSender.setText(mUsername + " | " + mPlaceTag);
		
		mListReply.addHeaderView(headerView);
		mListReply.setAdapter(adapter);
	}
	
	private final LoaderManager.LoaderCallbacks<JSONObject> mSendARespond = new LoaderManager.LoaderCallbacks<JSONObject>() {

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

				if (status.equals("Success")) {
					mName.setText("");
					mMessage.setText("");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			// RESPONSE
			// {"compress_output":true,"memory_usage":"1.07MB","method":"POST","status":"Success","ellapsed_time":8.4183828830719}
		}

		@Override
		public void onLoaderReset(Loader<JSONObject> arg0) {
		}
	};
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public Loader<JSONObject> onCreateLoader(int arg0, Bundle arg1) {
		return null;
	}

	@Override
	public void onLoadFinished(Loader<JSONObject> arg0, JSONObject arg1) {
	}

	@Override
	public void onLoaderReset(Loader<JSONObject> arg0) {
	}
}
