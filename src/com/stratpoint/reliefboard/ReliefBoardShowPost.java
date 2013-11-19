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

import com.stratpoint.reliefboard.adapter.PostBaseAdapter;
import com.stratpoint.reliefboard.adapter.SQLiteAdapter;
import com.stratpoint.reliefboardandroid.R;

public class ReliefBoardShowPost extends Activity {

	private SQLiteAdapter mySQLiteAdapter;
	private List<PostObjectPOJO> postObjectList;
	private InsertionListView listviewPost;
	private PostBaseAdapter cAdapter;
	private TelephonyManager mTelephonyManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_relief_board_show_post);

		mySQLiteAdapter = new SQLiteAdapter(this);

		listviewPost = (InsertionListView) findViewById(R.id.listview_Post);

		new MainOperation().execute("http://www.reliefboard.com/messages/feed/");
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
	private class MainOperation  extends AsyncTask<String, Void, String> {

		//private final HttpClient Client = new DefaultHttpClient();
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
			// NOTE: You can call UI Element here.

			// Close progress dialog


			if (Error != null) {

				Log.v("Output : ",""+Error);

			} else {


				/****************** Start Parse Response JSON Data *************/


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

					/*	for(Iterator iterator=jsonResponse.keys();iterator.hasNext();) {
						String key = iterator.next().toString();

						if(jsonResponse.get(key) instanceof JSONObject) {
							JSONObject jsonItem = jsonResponse.getJSONObject(key);
							Log.i("INVENUE", jsonItem.toString());
							String result = jsonItem.optString("result").toString().trim();

							JSONArray cast = jsonResponse.getJSONArray("result");

							//result = result.substring(0, result.length() - 1);
							//result = result.substring(1);
							//Log.i("INVENUE Result ", ""+ result);
							//JSONObject jsonResult= new JSONObject(result);
							//JSONArray arrResults = jsonResult.to;
							//for(Iterator iteratorResult=jsonResult.keys();iteratorResult.hasNext();) {
								//String userKey = iteratorResult.next().toString();

								//if(jsonUser.get(userKey) instanceof JSONObject) {
									String appName = DatabaseUtils.sqlEscapeString(jsonItem.optString("app_name").toString().trim());
									String dateCreated = jsonItem.optString("date_created").toString().trim();
									String fbPostLink = URLDecoder.decode(jsonItem.optString("fb_post_link").toString().trim());
									String postID = jsonItem.optString("id").toString().trim();
									String logo = jsonItem.optString("logo").toString().trim();
									String message = URLDecoder.decode(jsonItem.optString("message").toString().trim());
									String parentID = jsonItem.optString("parent_id").toString().trim();
									String placeTag = URLDecoder.decode(jsonItem.optString("place_tag").toString().trim());
									String sender = URLDecoder.decode(jsonItem.optString("sender").toString().trim());
									String senderNumber = URLDecoder.decode(jsonItem.optString("sender_number").toString().trim());
									String source = jsonItem.optString("source").toString().trim();
									String sourceType = jsonItem.optString("source_type").toString().trim();
									String status = jsonItem.optString("source_type").toString().trim();


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
								//}



							}
						}


					 */
				}

				catch (JSONException e) {

					e.printStackTrace();
					e.getMessage();
				}

				cAdapter = new PostBaseAdapter(ReliefBoardShowPost.this, R.layout.list_view_post, postObjectList);
				listviewPost.setAdapter(cAdapter);
				listviewPost.setData(postObjectList);
				mySQLiteAdapter.close();

				Dialog.dismiss();

			}
		}

	}

	private void sendSMS() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.putExtra("address", "260011");
		intent.putExtra("sms_body", "LOCATION/YOUR NAME/MESSAGE ");
		intent.setType("vnd.android-dir/mms-sms");
		startActivity(intent);
	}
}
