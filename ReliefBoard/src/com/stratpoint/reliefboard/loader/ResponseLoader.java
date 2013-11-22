package com.stratpoint.reliefboard.loader;

import org.json.JSONObject;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.stratpoint.reliefboard.api.ApiRequest;
import com.stratpoint.reliefboard.util.ReliefBoardConstants;
import com.stratpoint.reliefboardandroid.R;

public class ResponseLoader extends AsyncTaskLoader<JSONObject>{

	public static final int REQUEST_METHOD_POST = 0x0f1;
	public static final int REQUEST_MEHTOD_GET = 0x0f2;
	public static final int REQUEST_METHOD_POST_MAIN = 0x0f3;
	private int mRequestMethod = REQUEST_METHOD_POST;
	
	private String appId;
	private String name;
	private String message;
	private String parentId;
	private String messageId;
	
	public ResponseLoader(Context context) {
		super(context);
	}

	@Override
	public JSONObject loadInBackground() {
		JSONObject jsonResult = null;
		
		try {
			switch (mRequestMethod) {
			case REQUEST_METHOD_POST:
				jsonResult = ApiRequest.getInstance()
									   .addParameter("app_id", appId)
									   .addParameter("name", name)
									   .addParameter("message", message)
									   .addParameter("parent_id", parentId)
									   .post(ApiRequest.getApiUrl() + ReliefBoardConstants.Api.RESPONSE);
				
				break;
				
			case REQUEST_MEHTOD_GET:
				jsonResult = ApiRequest.getInstance()
										.addParameter("parent_id", parentId)
//										.addParameter("message_id", messageId)
//										.addParameter("offset", "0")
//										.addParameter("limit", "10")
//										.addParameter("app_id", getContext().getResources().getString(R.string.app_id))
										.get(ApiRequest.getApiUrl() + ReliefBoardConstants.Api.COMMENTS);
				break;
			
			case REQUEST_METHOD_POST_MAIN:
				jsonResult = ApiRequest.getInstance()
									   .addParameter("app_id", appId)
									   .addParameter("name", name)
									   .addParameter("message", message)
									   .post(ApiRequest.getApiUrl() + ReliefBoardConstants.Api.RESPONSE);
				
				break;
			
			}
			
		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return jsonResult;
	}

	public void setRequestMethod(int mRequestMethod) {
		this.mRequestMethod = mRequestMethod;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	
}
