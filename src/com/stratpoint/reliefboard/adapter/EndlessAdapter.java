package com.stratpoint.reliefboard.adapter;

/*
 * Copyright (C) 2012 Surviving with Android (http://www.survivingwithandroid.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.stratpoint.reliefboard.listener.PostActionListener;
import com.stratpoint.reliefboard.model.PostObjectPOJO;
import com.stratpoint.reliefboard.util.ImageLoaderUtil;
import com.stratpoint.reliefboardandroid.R;

public class EndlessAdapter extends ArrayAdapter<PostObjectPOJO> {
	
	private List<PostObjectPOJO> mData;
	private PostActionListener mPostActionListener;
	private Context ctx;
	private int layoutId;
	
	public EndlessAdapter(Context ctx, List<PostObjectPOJO> itemList, int layoutId) {
		super(ctx, layoutId);
		this.mData = itemList;
		this.ctx = ctx;
		this.layoutId = layoutId;
	}

	@Override
	public int getCount() {		
		return mData.size() ;
	}

	@Override
	public PostObjectPOJO getItem(int position) {		
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {		
		return mData.get(position).hashCode();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View row = convertView;
		Holder holder = null;
		final PostObjectPOJO post = mData.get(position);
		
		if(row == null) {
			LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(layoutId, parent, false);
			holder = new Holder();
			holder.tvDate = (TextView) row.findViewById(R.id.textview_date);
			holder.tvTitle = (TextView) row.findViewById(R.id.tv_title);
			holder.ivUser = (ImageView) row.findViewById(R.id.iv_user);
			holder.tvMessage = (TextView) row.findViewById(R.id.tv_message);
			holder.tvUser = (TextView) row.findViewById(R.id.tv_user);
			holder.tvLocation = (TextView) row.findViewById(R.id.tv_location);
			holder.response = (TextView) row.findViewById(R.id.tv_responses);
			row.setTag(holder);
		}
		else
		{
			holder = (Holder)row.getTag();
		}
		
		View.OnClickListener onClickListener = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mPostActionListener != null)
					switch (v.getId()) {
					case R.id.tv_responses:
						mPostActionListener.onResponseClick(post, position);
						break;
					}
				
			}
		};
		
		holder.response.setOnClickListener(onClickListener);
		
		Log.v("Date", ""+mData.get(position).GetDateCreated());

		long timeStampCreated = Long.parseLong(mData.get(position).GetDateCreated());
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

		if (mData.get(position).GetAppName() == null || mData.get(position).GetAppName().equals("") || mData.get(position).GetAppName().equalsIgnoreCase("'null'")){
			holder.tvTitle.setText("");
		}
		else
		{	
		holder.tvTitle.setText("from: " + mData.get(position).GetAppName());
		}
		if (mData.get(position).GetLogo() == null ||mData.get(position).GetLogo().equals("") || mData.get(position).GetLogo().equals("null")){
			holder.ivUser.setVisibility(View.GONE);
		}
		else
		{
			holder.ivUser.setVisibility(View.VISIBLE);
			ImageLoaderUtil.getInstance(ctx).displayImage(mData.get(position).GetLogo(), holder.ivUser);
		}

		holder.tvMessage.setText(mData.get(position).GetMessage() + "\n" + mData.get(position).GetSenderNumber());
		
		if (mData.get(position).GetSender() == null || mData.get(position).GetSender().equals("")|| mData.get(position).GetSender().equalsIgnoreCase("'null'") || mData.get(position).GetSender().equalsIgnoreCase("null")){
			holder.tvUser.setText("");
		}
		else
		{
			holder.tvUser.setText(mData.get(position).GetSender());
		}
		
		if (mData.get(position).GetPlaceTag() == null || mData.get(position).GetPlaceTag().equals("")|| mData.get(position).GetPlaceTag().equalsIgnoreCase("'null'")|| mData.get(position).GetPlaceTag().equalsIgnoreCase("null")){
			holder.tvLocation.setText("");
		}
		else
		{
			holder.tvLocation.setText(mData.get(position).GetPlaceTag());
		}
		
		return row;

	}
	
	private Calendar getDateCurrentTimeZone(long timestamp) {
		Date date = new Date(timestamp * 1000);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}
	private class Holder {
		public TextView tvDate;
		public TextView tvTitle;
		public TextView tvMessage;
		public TextView tvUser;
		public TextView tvLocation;
		public TextView response;
		public ImageView ivUser;
	}

	public void setPostActionListener(PostActionListener listener){
		mPostActionListener = listener;
	}
}
