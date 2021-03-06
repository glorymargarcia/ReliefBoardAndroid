package com.stratpoint.reliefboard;

import java.io.BufferedReader;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.stratpoint.reliefboard.PostDialogFragment.onSubmitListener;
import com.stratpoint.reliefboard.adapter.EndlessAdapter;
import com.stratpoint.reliefboard.adapter.PostBaseAdapter;
import com.stratpoint.reliefboard.adapter.SQLiteAdapter;
import com.stratpoint.reliefboard.listener.PostActionListener;
import com.stratpoint.reliefboard.loader.ResponseLoader;
import com.stratpoint.reliefboard.model.PostObjectPOJO;
import com.stratpoint.reliefboard.util.ReliefBoardConstants;
import com.stratpoint.reliefboardandroid.R;


public class ReliefBoardShowPost extends FragmentActivity implements EndlessListView.EndlessListener{

	private SQLiteAdapter mySQLiteAdapter;
	private List<PostObjectPOJO> postObjectList;
	private EndlessListView listviewPost;
	private PostBaseAdapter cAdapter;
	private TelephonyManager mTelephonyManager;
	private EndlessAdapter adp; 
	private int mPostPosition;
	private EditText etName; 
	private EditText etBody;
	private Button btnPost;
	
	private Dialog dialog;
			
	private int offset = 0, limit = 10;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_relief_board_show_post);

		mySQLiteAdapter = new SQLiteAdapter(this);

		listviewPost = (EndlessListView) findViewById(R.id.listview_Post);
		offset = 0;



		new MainOperation().execute("http://www.reliefboard.com/messages/feed/?offset=" + offset + "&limit=" + limit);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.relief_board_show_post, menu);
		return true;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) { 
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_settings:
			startActivity(new Intent(ReliefBoardShowPost.this, SendSMSActivity.class));
		case R.id.post_message:
			AddNewPost();
//			startActivity(new Intent(ReliefBoardShowPost.this, PostMessage.class));
		}
		return false;
	}
	
	private void AddNewPost(){   
		dialog = new Dialog(ReliefBoardShowPost.this);
		//dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);  
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		dialog.setContentView(R.layout.activity_post__message);
		
		etName= (EditText) dialog.findViewById(R.id.et_username);
		etBody = (EditText) dialog.findViewById(R.id.et_body);
		btnPost = (Button) dialog.findViewById(R.id.btn_post_message);
		btnPost.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) { 
				// TODO Auto-generated method stub
				if (!etName.getText().equals("") ||!etBody.getText().equals("")){
					//Toast.makeText(getApplicationContext(), "HAHA", Toast.LENGTH_LONG).show();
					getSupportLoaderManager().restartLoader(ReliefBoardConstants.LoaderId.Respond, null, mSendAPost);
					dialog.dismiss();
				}				
				
			}
		});
		
		
		dialog.show();
	}

	private final PostActionListener mPostActionListener = new PostActionListener() {
		
		@Override
		public void onResponseClick(PostObjectPOJO post, int position) {

			mPostPosition = position;
//			Intent intent = new Intent(getApplicationContext(), ResponseActivity.class);
			Intent intent = new Intent(getApplicationContext(), ResponsesActivity.class);
			intent.putExtra(ReliefBoardConstants.Extra.USERNAME, post.GetSender());
			intent.putExtra(ReliefBoardConstants.Extra.POST_MESSAGE, post.GetMessage());
			intent.putExtra(ReliefBoardConstants.Extra.POST_ID, post.GetPostID());
			intent.putExtra(ReliefBoardConstants.Extra.PLACE_TAG, post.GetPlaceTag());
			startActivity(intent);
		}
	};
	
	private void sendSMS() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.putExtra("address", "260011");
		intent.putExtra("sms_body", "LOCATION/YOUR NAME/MESSAGE ");
		intent.setType("vnd.android-dir/mms-sms");
		startActivity(intent);
	}

	@Override
	public void loadData() {
		offset = offset + limit;
		new LoadMore().execute("http://www.reliefboard.com/messages/feed/?offset=" + offset + "&limit=" + limit);

	}

	private final LoaderManager.LoaderCallbacks<JSONObject> mSendAPost= new LoaderManager.LoaderCallbacks<JSONObject>() {

		@Override
		public Loader<JSONObject> onCreateLoader(int id, Bundle bundle) {
			ResponseLoader responseLoader = new ResponseLoader(getApplicationContext());
			responseLoader.setRequestMethod(ResponseLoader.REQUEST_METHOD_POST);
			responseLoader.setAppId(getResources().getString(R.string.app_id));
			responseLoader.setName(etName.getText().toString());
			responseLoader.setMessage(etBody.getText().toString());
			responseLoader.forceLoad();
			return responseLoader;
		}

		@Override
		public void onLoadFinished(Loader<JSONObject> loader, JSONObject result) {
			String status;
			try {
				status = result.getString("status");

				if (status.equals("Success")) {
					
					
				Toast.makeText(ReliefBoardShowPost.this, "Post Successful", Toast.LENGTH_LONG).show();
				offset = 0;
				new MainOperation().execute("http://www.reliefboard.com/messages/feed/?offset=" + offset + "&limit=" + limit);
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
	
	private class MainOperation  extends AsyncTask<String, Void, String> {

		private String Error = null;
		private ProgressDialog Dialog;
		private SQLiteAdapter mySQLiteAdapter;


		protected void onPreExecute() {
			Dialog = new ProgressDialog(ReliefBoardShowPost.this);
			Dialog.setMessage("Please wait..");
			Dialog.setCancelable(false);

			if(Dialog.isShowing()) Dialog.dismiss();
			Dialog.show();
			mySQLiteAdapter = new SQLiteAdapter(ReliefBoardShowPost.this);
		}


		protected String doInBackground(String... urls) {

			/************ Make Post Call To Web Server ***********/
			BufferedReader reader=null;
			InputStream inputStream = null;
			String Content = "";

			// Send data 

			mySQLiteAdapter.openToWrite();
			mySQLiteAdapter.execQuery("DROP TABLE IF EXISTS post_tb");
			mySQLiteAdapter.execQuery("CREATE TABLE post_tb (id INTEGER PRIMARY KEY, appName TEXT, dateCreated TEXT, " +
					"fbPostLink TEXT, postID TEXT, logo TEXT, message TEXT, parentID TEXT, placeTag TEXT, " +
					"sender TEXT, sender_number TEXT, source TEXT, source_type TEXT, status TEXT)");


			try
			{ 

				// Defined URL  where to send data
				String myUrl = urls[0];
				DefaultHttpClient   httpclient = new DefaultHttpClient(new BasicHttpParams());
				//HttpPost httppost = new HttpPost(myUrl);
				HttpGet httpget = new HttpGet(myUrl);

				httpget.setHeader("Content-type", "application/json");

				HttpResponse response = httpclient.execute(httpget);           
				HttpEntity entity = response.getEntity();

				inputStream = entity.getContent();

				byte[] buffer = new byte[1024];
				int length = 0;
				StringBuffer str = new StringBuffer();

				while((length = inputStream.read(buffer)) != -1) {
					str.append(new String(buffer, 0, length));
				}

				Log.i("Invenue", "result: " + str.toString());

				Content = str.toString();

			}
			catch(Exception ex)
			{
				Error = ex.getMessage();
			}
			finally
			{
				try
				{

					reader.close();
				}

				catch(Exception ex) {}
			}

			return Content;
		}

		@SuppressWarnings("deprecation")
		protected void onPostExecute(String content) {


			if (Error != null) {

				Log.v("Output : ",""+Error);

			} else {

				JSONObject jsonResponse;


				try {
					//ClearCache.trimCache(MainPage.this);
					postObjectList = new ArrayList<PostObjectPOJO>();

					jsonResponse = new JSONObject(content);

					JSONArray arrResults = jsonResponse.getJSONObject("data").getJSONArray("result");
					Log.v("Test ", ""+arrResults.toString());
					for (int i=0; i<arrResults.length(); i++) {
						JSONObject result = arrResults.getJSONObject(i);


						String appName = DatabaseUtils.sqlEscapeString(result.getString("app_name").toString().trim());
						String dateCreated = result.getString("date_created").toString().trim();
						String fbPostLink = "";
						if(result.has("fb_post_link"))
							fbPostLink = URLDecoder.decode(URLDecoder.decode(result.getString("fb_post_link").toString().trim()));
						String postID = result.getString("id").toString().trim();
						String logo = result.getString("logo").toString().trim();
						String message = URLDecoder.decode(URLDecoder.decode(result.getString("message")));
						String parentID = result.getString("parent_id").toString().trim();
						String placeTag = URLDecoder.decode(URLDecoder.decode(result.getString("place_tag").toString().trim()));
						String sender = URLDecoder.decode(URLDecoder.decode(result.getString("sender").toString().trim()));
						String senderNumber = URLDecoder.decode(URLDecoder.decode(result.getString("sender_number").toString().trim()));
						String source =result.getString("source").toString().trim();
						String sourceType = result.getString("source_type").toString().trim();
						String status = result.getString("source_type").toString().trim();


						if (dateCreated == null || dateCreated.equals("")){

						}
						else
						{
							mySQLiteAdapter.execQuery("INSERT INTO post_tb (appName,dateCreated, fbPostLink, postID, logo, message, parentID, placeTag, " +
									"sender, sender_number, source, source_type, status) VALUES (" +
									"" + DatabaseUtils.sqlEscapeString(appName) + ", " +
									"" + DatabaseUtils.sqlEscapeString(dateCreated) + ", " +
									"" + DatabaseUtils.sqlEscapeString(fbPostLink) + ", " +
									"" + DatabaseUtils.sqlEscapeString(postID) + ", " +
									"" + DatabaseUtils.sqlEscapeString(logo) + ", " +
									"" + DatabaseUtils.sqlEscapeString(message) + ", " +
									"" + DatabaseUtils.sqlEscapeString(parentID) + ", " +
									"" + DatabaseUtils.sqlEscapeString(placeTag) + ", " +
									"" + DatabaseUtils.sqlEscapeString(sender) + ", " +
									"" + DatabaseUtils.sqlEscapeString(senderNumber) + ", " +
									"" + DatabaseUtils.sqlEscapeString(source) + ", " +
									"" + DatabaseUtils.sqlEscapeString(sourceType) + ", " +
									"" + DatabaseUtils.sqlEscapeString(status) + " " +
									")");

							PostObjectPOJO object = new PostObjectPOJO();
							object.SetAppName(appName);
							object.SetDateCreated(dateCreated);
							Log.v("Date Created", ""+dateCreated);
							object.SetFbPostLink(fbPostLink);
							object.SetPostID(postID);
							object.SetLogo(logo);
							object.SetMessage(message);
							object.SetParentID(parentID);
							object.SetPlaceTag(placeTag);
							object.SetSender(sender);
							object.SetSenderNumber(senderNumber);
							object.SetSource(source);
							object.SetSourceType(sourceType);
							object.SetStatus(status);
							postObjectList.add(object);
						}

					}

				}

				catch (JSONException e) {

					e.printStackTrace();
					e.getMessage();
				}

				adp = new EndlessAdapter(ReliefBoardShowPost.this, postObjectList, R.layout.list_view_post);
				adp.setPostActionListener(mPostActionListener);
				listviewPost.setLoadingView(R.layout.loading_layout);
				listviewPost.setAdapter(adp);
				
				listviewPost.setListener(ReliefBoardShowPost.this);
				//listviewPost.setData(postObjectList);
				mySQLiteAdapter.close();

				Dialog.dismiss();
			}
		}

	}
	
	private class LoadMore  extends AsyncTask<String, Void, String> {

		private String Error = null;
		//private ProgressDialog Dialog;
		private SQLiteAdapter mySQLiteAdapter;

		protected void onPreExecute() {
		
			mySQLiteAdapter = new SQLiteAdapter(ReliefBoardShowPost.this);
		}

		protected String doInBackground(String... urls) {

			/************ Make Post Call To Web Server ***********/
			BufferedReader reader=null;
			InputStream inputStream = null;
			String Content = "";

			mySQLiteAdapter.openToWrite();
			try
			{ 

				// Defined URL  where to send data
				String myUrl = urls[0];
				DefaultHttpClient   httpclient = new DefaultHttpClient(new BasicHttpParams());
				//HttpPost httppost = new HttpPost(myUrl);
				HttpGet httpget = new HttpGet(myUrl);

				httpget.setHeader("Content-type", "application/json");

				HttpResponse response = httpclient.execute(httpget);           
				HttpEntity entity = response.getEntity();

				inputStream = entity.getContent();

				byte[] buffer = new byte[1024];
				int length = 0;
				StringBuffer str = new StringBuffer();

				while((length = inputStream.read(buffer)) != -1) {
					str.append(new String(buffer, 0, length));
				}

				Log.i("Invenue", "result: " + str.toString());

				Content = str.toString();

			}
			catch(Exception ex)
			{
				Error = ex.getMessage();
			}
			finally
			{
				try
				{

					reader.close();
				}

				catch(Exception ex) {}
			}

			return Content;
		}

		@SuppressWarnings("deprecation")
		protected void onPostExecute(String content) {


			if (Error != null) {

				Log.v("Output : ",""+Error);

			} else {

				JSONObject jsonResponse;

				ArrayList<PostObjectPOJO> ObjectList = new ArrayList<PostObjectPOJO>();
				try {

					jsonResponse = new JSONObject(content);

					JSONArray arrResults = jsonResponse.getJSONObject("data").getJSONArray("result");
					Log.v("Test ", ""+arrResults.toString());
					for (int i=0; i<arrResults.length(); i++) {
						JSONObject result = arrResults.getJSONObject(i);


						String appName = DatabaseUtils.sqlEscapeString(result.getString("app_name").toString().trim());
						String dateCreated = result.getString("date_created").toString().trim();
						String fbPostLink="";
						if(result.has("fb_post_link"))
							fbPostLink = URLDecoder.decode(URLDecoder.decode(result.getString("fb_post_link").toString().trim()));
						String postID = result.getString("id").toString().trim();
						String logo = result.getString("logo").toString().trim();
						String message = URLDecoder.decode(URLDecoder.decode(result.getString("message")));
						String parentID = result.getString("parent_id").toString().trim();
						String placeTag = URLDecoder.decode(URLDecoder.decode(result.getString("place_tag").toString().trim()));
						String sender = URLDecoder.decode(URLDecoder.decode(result.getString("sender").toString().trim()));
						String senderNumber = URLDecoder.decode(URLDecoder.decode(result.getString("sender_number").toString().trim()));
						String source =result.getString("source").toString().trim();
						String sourceType = result.getString("source_type").toString().trim();
						String status = result.getString("source_type").toString().trim();


						if (dateCreated == null || dateCreated.equals("")){

						}
						else
						{
							mySQLiteAdapter.execQuery("INSERT INTO post_tb (appName,dateCreated, fbPostLink, postID, logo, message, parentID, placeTag, " +
									"sender, sender_number, source, source_type, status) VALUES (" +
									"" + DatabaseUtils.sqlEscapeString(appName) + ", " +
									"" + DatabaseUtils.sqlEscapeString(dateCreated) + ", " +
									"" + DatabaseUtils.sqlEscapeString(fbPostLink) + ", " +
									"" + DatabaseUtils.sqlEscapeString(postID) + ", " +
									"" + DatabaseUtils.sqlEscapeString(logo) + ", " +
									"" + DatabaseUtils.sqlEscapeString(message) + ", " +
									"" + DatabaseUtils.sqlEscapeString(parentID) + ", " +
									"" + DatabaseUtils.sqlEscapeString(placeTag) + ", " +
									"" + DatabaseUtils.sqlEscapeString(sender) + ", " +
									"" + DatabaseUtils.sqlEscapeString(senderNumber) + ", " +
									"" + DatabaseUtils.sqlEscapeString(source) + ", " +
									"" + DatabaseUtils.sqlEscapeString(sourceType) + ", " +
									"" + DatabaseUtils.sqlEscapeString(status) + " " +
									")");

							PostObjectPOJO object = new PostObjectPOJO();
							object.SetAppName(appName);
							object.SetDateCreated(dateCreated);
							Log.v("Date Created", ""+dateCreated);
							object.SetFbPostLink(fbPostLink);
							object.SetPostID(postID);
							object.SetLogo(logo);
							object.SetMessage(message);
							object.SetParentID(parentID);
							object.SetPlaceTag(placeTag);
							object.SetSender(sender);
							object.SetSenderNumber(senderNumber);
							object.SetSource(source);
							object.SetSourceType(sourceType);
							object.SetStatus(status);
							//listviewPost.addRow(object);
							postObjectList.add(object);
							ObjectList.add(object);

						}

					}

				}

				catch (JSONException e) {

					e.printStackTrace();
					e.getMessage();
				}


				listviewPost.addNewData(ObjectList);
				mySQLiteAdapter.close();

			

			}
		}

	}

	

	
	
	
}
