package com.stratpoint.reliefboard;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.stratpoint.reliefboard.adapter.ResponsesAdapter;
import com.stratpoint.reliefboard.loader.ResponseLoader;
import com.stratpoint.reliefboard.model.ResponseHelp;
import com.stratpoint.reliefboard.util.JSONParser;
import com.stratpoint.reliefboard.util.ReliefBoardConstants;
import com.stratpoint.reliefboardandroid.R;

public class ResponsesActivity extends SherlockFragmentActivity implements LoaderManager.LoaderCallbacks<JSONObject>{

	private String mPostMessage;
	private String mPostId;
	private String mUsername;
	private String mPlaceTag;
	
	private EditText mName;
	private EditText mMessage;
	private ListView mListReply;
	
	private ResponsesAdapter mAdapter;
	private ResponseHelp mPostHelp;
	private List<ResponseHelp> mResponses; 
	
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
		
		getSupportLoaderManager().restartLoader(ReliefBoardConstants.LoaderId.Comments, null, mComments);
		
		setUpViews();
	}
	
private final LoaderManager.LoaderCallbacks<JSONObject> mComments = new LoaderCallbacks<JSONObject>() {
		
		@Override
		public Loader<JSONObject> onCreateLoader(int id, Bundle bundle) {
			ResponseLoader commentsLoader = new ResponseLoader(getApplicationContext());
			commentsLoader.setRequestMethod(ResponseLoader.REQUEST_MEHTOD_GET);
			commentsLoader.setParentId(mPostId);
			commentsLoader.forceLoad();
			return commentsLoader;
		}
		
		@SuppressWarnings("deprecation")
		@Override
		public void onLoadFinished(Loader<JSONObject> loader, JSONObject result) {
			Log.d("Comments", result.toString());
			
			try {
				String status = result.getString("status");
				
				if(status.equals("Success")){
					JSONArray jsonResponse = result.getJSONObject("data").getJSONArray("result");
					mResponses.clear();
					for(int i=0; i<jsonResponse.length(); i++){
						JSONObject jsonItem = jsonResponse.getJSONObject(i);
						mPostHelp = new ResponseHelp();
						mPostHelp = JSONParser.parseJSONToResponseHelp(jsonItem);
						mPostHelp.setMessage(URLDecoder.decode(URLDecoder.decode(mPostHelp.getMessage())));
						mPostHelp.setSender(URLDecoder.decode(URLDecoder.decode(mPostHelp.getSender())));
						mPostHelp.setPlaceTag(mPostHelp.getPlaceTag());
						mResponses.add(mPostHelp);
					}
					//mAdapter = new ResponsesAdapter(ResponsesActivity.this, R.layout.adapter_response, mResponses);
					//mListReply.setAdapter(mAdapter);
					
					//mListReply.getAdapter().notify();
				
					mAdapter.notifyDataSetChanged();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void onLoaderReset(Loader<JSONObject> loader) {}
		
	};
	
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
		
//		String[] values = new String[] { "Android List View", "Adapter implementation", "Simple List View In Android", "Create List View Android", "Android Example", "List View Source Code", "List View Array Adapter", "Android Example List View" };
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);
		
		View headerView = LayoutInflater.from(this).inflate(R.layout.adapter_post, null);
		
		TextView userPost = (TextView) headerView.findViewById(R.id.user_comment);
		TextView userSender = (TextView) headerView.findViewById(R.id.user_name);
		userPost.setText(mPostMessage);
		userSender.setText(mUsername + " | " + mPlaceTag);

		mListReply.addHeaderView(headerView);

		Log.d("Responses", mResponses+"");
		mResponses = new ArrayList<ResponseHelp>();
		mAdapter = new ResponsesAdapter(ResponsesActivity.this, R.layout.adapter_response, mResponses);
		mListReply.setAdapter(mAdapter);
		
	}
	
	private final LoaderManager.LoaderCallbacks<JSONObject> mSendARespond = new LoaderManager.LoaderCallbacks<JSONObject>() {

		@Override
		public Loader<JSONObject> onCreateLoader(int id, Bundle bundle) {
			ResponseLoader responseLoader = new ResponseLoader(getApplicationContext());
			responseLoader.setRequestMethod(ResponseLoader.REQUEST_METHOD_POST);
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
