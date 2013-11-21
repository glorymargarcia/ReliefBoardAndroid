package com.stratpoint.reliefboard.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Raymond on 8/5/13.
 * This class is use to request an API to Server.
 */
public class ApiRequest extends ApiRequestBase {
    public static ApiRequest mApiRequest;
    private  Map<String, Object> mParameters = new HashMap<String, Object>();
    private static final boolean IS_DEVELOPMENT = false;
    private static final String DEVELOPMENT_URL = "";
//    private static final String PUSH_SERVER_URL = "prod";
//    private static final String PUSH_SERVER_URL = "dev";
    private static final String PRODUCTION_URL = "http://www.reliefboard.com/";

    private ApiRequest() {
    }

    public static ApiRequest getInstance() {
        if(mApiRequest == null)
            mApiRequest = new ApiRequest();
        mApiRequest.mParameters.clear();
        return mApiRequest;
    }

    public ApiRequest addParameter(String key, String value) {
        mParameters.put(key, value);
        return this;
    }
    
    public ApiRequest addParameter(String key, InputStream value) {
    	mParameters.put(key, value);
    	return this;
    }

    public ApiRequest addParameter(String key, int value) {
        mParameters.put(key, String.valueOf(value));
        return this;
    }

    public ApiRequest addParameter(String key, boolean value) {
        mParameters.put(key, (value)?"1":"0");
        return this;
    }

    public ApiRequest addParameter(String key, byte[] value) {
        mParameters.put(key, value);
        return this;
    }
    
    public ApiRequest addParameter(String key, double value) {
    	mParameters.put(key, value);
    	return this;
    }

    @Override
    public synchronized JSONObject put(String url) throws JSONException, IOException {
        InputStream is = RequestExecutor.put(url, mParameters);
        mParameters.clear();
        return streamToJson(is);
    }

    @Override
    public synchronized JSONObject post(String url) throws JSONException, IOException {
        InputStream is = RequestExecutor.post(url, mParameters);
        mParameters.clear();
        return streamToJson(is);
    }

    @Override
    public synchronized JSONObject get(String url) throws JSONException, IOException {
    	InputStream is = RequestExecutor.get(url, mParameters);
        //mParameters.clear();
        return streamToJson(is);
    }

    @Override
    public synchronized JSONObject delete(String url) throws JSONException, IOException {
        InputStream is = RequestExecutor.delete(url, mParameters);
        mParameters.clear();
        return streamToJson(is);
    }

    public static String getApiUrl() {
        if(IS_DEVELOPMENT)
            return DEVELOPMENT_URL;
        else
            return PRODUCTION_URL;
    }
    
    /*public static String getPushServerUrl() {
    	return PUSH_SERVER_URL;
    }*/
}
