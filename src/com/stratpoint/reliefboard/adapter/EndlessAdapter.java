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

import com.stratpoint.reliefboard.model.PostObjectPOJO;
import com.stratpoint.reliefboard.util.ImageLoaderUtil;
import com.stratpoint.reliefboardandroid.R;

public class EndlessAdapter extends ArrayAdapter<PostObjectPOJO> {
	
	private List<PostObjectPOJO> data;
	private Context ctx;
	private int layoutId;
	
	public EndlessAdapter(Context ctx, List<PostObjectPOJO> itemList, int layoutId) {
		super(ctx, layoutId);
		this.data = itemList;
		this.ctx = ctx;
		this.layoutId = layoutId;
	}

	@Override
	public int getCount() {		
		return data.size() ;
	}

	@Override
	public PostObjectPOJO getItem(int position) {		
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {		
		return data.get(position).hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		Holder holder = null;
		
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

		if (data.get(position).GetAppName() == null || data.get(position).GetAppName().equals("") || data.get(position).GetAppName().equalsIgnoreCase("'null'")){
			holder.tvTitle.setText("");
		}
		else
		{	
		holder.tvTitle.setText("from: " + data.get(position).GetAppName());
		}
		if (data.get(position).GetLogo() == null ||data.get(position).GetLogo().equals("") || data.get(position).GetLogo().equals("null")){
			holder.ivUser.setVisibility(View.GONE);
		}
		else
		{
			holder.ivUser.setVisibility(View.VISIBLE);
			ImageLoaderUtil.getInstance(ctx).displayImage(data.get(position).GetLogo(), holder.ivUser);
		}

		holder.tvMessage.setText(data.get(position).GetMessage() + "\n" + data.get(position).GetSenderNumber());
		
		if (data.get(position).GetSender() == null || data.get(position).GetSender().equals("")|| data.get(position).GetSender().equalsIgnoreCase("'null'") || data.get(position).GetSender().equalsIgnoreCase("null")){
			holder.tvUser.setText("");
		}
		else
		{
			holder.tvUser.setText(data.get(position).GetSender());
		}
		
		if (data.get(position).GetPlaceTag() == null || data.get(position).GetPlaceTag().equals("")|| data.get(position).GetPlaceTag().equalsIgnoreCase("'null'")|| data.get(position).GetPlaceTag().equalsIgnoreCase("null")){
			holder.tvLocation.setText("");
		}
		else
		{
			holder.tvLocation.setText(data.get(position).GetPlaceTag());
		}
		
		return row;

	}
	
	private Calendar getDateCurrentTimeZone(long timestamp) {
		Date date = new Date(timestamp * 1000);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}
	class Holder {
		TextView tvDate;
		TextView tvTitle;
		TextView tvMessage;
		TextView tvUser;
		TextView tvLocation;
		ImageView ivUser;
	}

	

}
