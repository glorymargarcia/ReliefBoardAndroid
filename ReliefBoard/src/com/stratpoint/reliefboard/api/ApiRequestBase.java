package com.stratpoint.reliefboard.api;

import java.io.InputStream;
import java.io.IOException;
import org.json.JSONObject;
import org.json.JSONException;

/**
 * Created by Raymond on 8/5/13.
 * This class is the base class of ApiRequest.
 */
abstract class ApiRequestBase {

    protected JSONObject streamToJson(InputStream is) throws JSONException, IOException {
    	if(is != null) {
    		byte[] buffer = new byte[1024];
            int length = 0;
            StringBuffer sb = new StringBuffer();

            while((length = is.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, length));
            }

            return new JSONObject(sb.toString());
    	}
    	
    	return null;
    }

    protected abstract JSONObject put(String url) throws JSONException, IOException;
    protected abstract JSONObject post(String url) throws JSONException, IOException;
    protected abstract JSONObject get(String url) throws  JSONException, IOException;
    protected abstract JSONObject delete(String url) throws  JSONException, IOException;
}
