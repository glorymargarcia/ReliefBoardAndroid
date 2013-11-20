package com.stratpoint.reliefboard.loader;

import org.json.JSONObject;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.stratpoint.reliefboard.api.ApiRequest;
import com.stratpoint.reliefboard.util.ReliefBoardConstants;

public class ResponseLoader extends AsyncTaskLoader<JSONObject>{

	public static final int REQUEST_METHOD_POST = 0x0f1;
	private int mRequestMethod = REQUEST_METHOD_POST;
	
	private String appId;
	private String name;
	private String message;
	private String parentId;
	
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
	
}
