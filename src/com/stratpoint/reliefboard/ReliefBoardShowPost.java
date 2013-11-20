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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.stratpoint.reliefboard.adapter.EndlessAdapter;
import com.stratpoint.reliefboard.adapter.PostBaseAdapter;
import com.stratpoint.reliefboard.adapter.SQLiteAdapter;
import com.stratpoint.reliefboard.listener.PostActionListener;
import com.stratpoint.reliefboard.model.PostObjectPOJO;
import com.stratpoint.reliefboard.util.ReliefBoardConstants;
import com.stratpoint.reliefboardandroid.R;


public class ReliefBoardShowPost extends Activity implements EndlessListView.EndlessListener {

	private SQLiteAdapter mySQLiteAdapter;
	private List<PostObjectPOJO> postObjectList;
	private EndlessListView listviewPost;
	private PostBaseAdapter cAdapter;
	private TelephonyManager mTelephonyManager;
	private EndlessAdapter adp; 
	private int mPostPosition;

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
			/*if(mTelephonyManager.SIM_STATE_ABSENT == 1){
				sendSMS();	
			} else {
				Toast.makeText(getApplicationContext(), "Please insert sim card.", Toast.LENGTH_LONG).show();
			}*/
//			return true;
		}
		return false;
	}

	private final PostActionListener mPostActionListener = new PostActionListener() {
		
		@Override
		public void onResponseClick(PostObjectPOJO post, int position) {
			mPostPosition = position;
			Intent intent = new Intent(getApplicationContext(), ResponseActivity.class);
//			intent.putExtra(ReliefBoardConstants.Extra.POST, post);
			intent.putExtra(ReliefBoardConstants.Extra.POST_ID, post.GetPostID());
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
						String fbPostLink = URLDecoder.decode(URLDecoder.decode(result.getString("fb_post_link").toString().trim()));
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
						String fbPostLink = URLDecoder.decode(URLDecoder.decode(result.getString("fb_post_link").toString().trim()));
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
