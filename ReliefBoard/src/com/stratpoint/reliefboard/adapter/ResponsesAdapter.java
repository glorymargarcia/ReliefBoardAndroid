package com.stratpoint.reliefboard.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.stratpoint.reliefboard.model.ResponseHelp;
import com.stratpoint.reliefboardandroid.R;

public class ResponsesAdapter extends ArrayAdapter<ResponseHelp> {
	private int mLayoutResourceId;
	private List<ResponseHelp> mPosts;
	private LayoutInflater mInflater;

	public ResponsesAdapter(Context context, int layoutResourceId, List<ResponseHelp> posts) {
		super(context, layoutResourceId);
		mLayoutResourceId = layoutResourceId;
		mPosts= posts;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View view = convertView;
		ViewHolder viewHolder = null;
		ResponseHelp post = mPosts.get(position);
		
		if(view == null){
			view = mInflater.inflate(mLayoutResourceId, null);
			viewHolder = new ViewHolder();
			viewHolder.user_name = (TextView) view.findViewById(R.id.user_name);
			viewHolder.user_response = (TextView) view.findViewById(R.id.user_comment);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
		viewHolder.user_name.setText(post.getSender());
		viewHolder.user_response.setText(post.getMessage());
		
		return view;
	}
	
	@Override
	public int getCount() {		
		return mPosts.size() ;
	}
	
	
	private class ViewHolder {
		public TextView user_name;
		public TextView user_response;
	}
	
}
