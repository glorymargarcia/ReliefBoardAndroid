package com.stratpoint.reliefboardandroid;
import java.util.ArrayList; 
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class PostBaseAdapter extends BaseAdapter{
	HashMap<PostObjectPOJO, Integer> mIdMap = new HashMap<PostObjectPOJO, Integer>();
	List<PostObjectPOJO> data;
	Context mContext;
	int mLayoutViewResourceId;
	int mCounter;

	public PostBaseAdapter(Context context, int layoutViewResourceId,
			List <PostObjectPOJO> mdata) {

		this.data = mdata;
		mContext = context;
		mLayoutViewResourceId = layoutViewResourceId;
		updateStableIds();
	}



	public void updateStableIds() {
		mIdMap.clear();
		mCounter = 0;
		for (int i = 0; i < data.size(); ++i) {
			mIdMap.put(data.get(i), mCounter++);
		}
	}

	public void addStableIdForDataAtPosition(int position) {
		mIdMap.put(data.get(position), ++mCounter);
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		Holder holder  = null;


		if(row == null) {
			LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();
			row = inflater.inflate(mLayoutViewResourceId, parent, false);
			holder = new Holder();
			holder.tvDate = (TextView) row.findViewById(R.id.textview_date);
			holder.tvTitle = (TextView) row.findViewById(R.id.tv_title);
			holder.ivUser = (ImageView) row.findViewById(R.id.iv_user);
			holder.tvMessage = (TextView) row.findViewById(R.id.tv_message);
			holder.tvUser = (TextView) row.findViewById(R.id.tv_user);
			holder.tvLocation = (TextView) row.findViewById(R.id.tv_location);
			row.setTag(holder);
		}
		else
		{
			holder = (Holder)row.getTag();
		}

		Log.v("Date", ""+data.get(position).GetDateCreated());

		long timeStampCreated = Long.parseLong(data.get(position).GetDateCreated());
		Calendar dateCreated = getDateCurrentTimeZone(timeStampCreated);
		Calendar dateNow = Calendar.getInstance();
		long milliseconds1 = dateCreated.getTimeInMillis();
		long milliseconds2 = dateNow.getTimeInMillis();
		long diff = milliseconds2 - milliseconds1;

		long days = diff / (24 * 60 * 60 * 1000);
		long x = diff / 1000;
		long seconds = x % 60;
		x /= 60;
		long minutes = x % 60;
		x /= 60;
		long hours = x % 24;

		//holder.tvDate.setText(data.get(position).GetDateCreated());

		if (days ==0){
			if (hours == 0){
				if (minutes == 0){
					holder.tvDate.setText(String.valueOf(seconds) + " secs ago");
				}
				else if (minutes == 1){
					holder.tvDate.setText(String.valueOf(minutes) + " min ago");
				}
				else
				{
					holder.tvDate.setText(String.valueOf(minutes) + " mins ago");
				}
			}
			else if (hours == 1){
				holder.tvDate.setText(String.valueOf(hours) + " hour ago");
			}
			else if (hours == 4){
				holder.tvDate.setText(String.valueOf(hours) + " hour ago");
			}
			else
			{
				String myhour = String.valueOf(hours);
				holder.tvDate.setText(String.valueOf(hours) + " hours ago");
				//holder.tvDate.setText( "4 hours ago");
			}
		}
		else if (days ==1){
			holder.tvDate.setText(String.valueOf(days) + " day ago");
		}
		else{
			holder.tvDate.setText(String.valueOf(days) + " days ago");
		}


		holder.tvTitle.setText("from: " + data.get(position).GetAppName());

		if (data.get(position).GetLogo() == null ||data.get(position).GetLogo().equals("") || data.get(position).GetLogo().equals("null")){

		}
		else
		{
			ImageLoaderUtil.getInstance(mContext).displayImage(data.get(position).GetLogo(), holder.ivUser);
		}

		holder.tvMessage.setText(data.get(position).GetMessage() + "\n" + data.get(position).GetSenderNumber());
		holder.tvUser.setText(data.get(position).GetSender());
		holder.tvLocation.setText(data.get(position).GetPlaceTag());



		return row;
	}
	/**
	 * Returns a circular cropped version of the bitmap passed in.
	 */
	private Calendar getDateCurrentTimeZone(long timestamp) {
		Date date = new Date(timestamp * 1000);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int pos) {
		return data.get(pos);
	}


	class Holder {
		TextView tvDate;
		TextView tvTitle;
		TextView tvMessage;
		TextView tvUser;
		TextView tvLocation;
		ImageView ivUser;
	}


	@Override
	public long getItemId(int position) {

		PostObjectPOJO item = (PostObjectPOJO) getItem(position);
		if (mIdMap.containsKey(item)) {
			return mIdMap.get(item);
		}
		return -1;


	}
}




