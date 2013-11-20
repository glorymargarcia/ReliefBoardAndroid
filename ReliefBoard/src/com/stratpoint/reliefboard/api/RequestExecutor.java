package com.stratpoint.reliefboard.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;

/**
 * This class is use to execute request to the Server.
 */
final class RequestExecutor {
    private static HttpClient mHttpClient;

    public static synchronized InputStream put(String url, Map<String, Object> params) throws IOException, JSONException {
        HttpPut put = new HttpPut(url);
        put.setEntity(createStringEntity(params));
        HttpResponse response = getHttpClient().execute(put);

        return response.getEntity().getContent();
    }

    public static synchronized InputStream post(String url, Map<String, Object> params) throws IOException {
        HttpPost post = new HttpPost(url);
        post.setEntity(createEntity(params));
        HttpResponse response = getHttpClient().execute(post);
        
        return response.getEntity().getContent();
    }

    public static synchronized InputStream get(String url, Map<String, Object> params) throws IOException {
    	if(!url.endsWith("/")) url += "/";
        HttpGet get = new HttpGet(url + ((url.indexOf('?') > -1)?"&":"?") + createQuery(params));
        HttpResponse response = getHttpClient().execute(get);
        return response.getEntity().getContent();
    }

    public static synchronized InputStream delete(String url, Map<String, Object> params) throws IOException, JSONException {
        if(!url.endsWith("/")) url += "/";
        HttpDelete delete = new HttpDelete(URI.create(url));
        delete.setEntity(createStringEntity(createQuery(params)));
        HttpResponse response = getHttpClient().execute(delete);
        
        return response.getEntity().getContent();
    }

    private static HttpClient getHttpClient() {
        if(mHttpClient == null)
            mHttpClient = new DefaultHttpClient();
        return mHttpClient;
    }

    private static ContentBody createContentBody(Object value) throws IOException {
        ContentBody contentBody = null;

        if(value instanceof byte[]) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(value);
            contentBody = new ByteArrayBody(bos.toByteArray(), "image.jpg");
        } else if(value instanceof InputStream) {
        	InputStream is = (InputStream) value;
        	contentBody = new InputStreamBody(is, "image.jpg");
        } else {
        	if(value != null)
        		contentBody = new StringBody(value.toString());
        }

        return contentBody;
    }

    private static MultipartEntity createEntity(Map<String, Object> params) throws IOException {
        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, null, Charset.forName("utf-8"));
        Set<String> keys = params.keySet();

        for(Iterator<String> iterator = keys.iterator();iterator.hasNext();) {
            String key = iterator.next();
            ContentBody contentBody = createContentBody(params.get(key));
            
            if(contentBody != null)
            	entity.addPart(key, contentBody);
        }
        return entity;
    }
    
    public static StringEntity createStringEntity(Map<String, Object> params) throws JSONException, UnsupportedEncodingException {
    	JSONObject jsonObject = new JSONObject();
    	Set<String> keys = params.keySet();
    	
    	for(Iterator<String> iterator=keys.iterator();iterator.hasNext();) {
    		String key = iterator.next();
    		Object value = params.get(key);
    		
    		if(value instanceof byte[]) {
    			byte[] data = (byte[]) value;
    			jsonObject.put(key, Base64.encode(data, Base64.NO_WRAP));
    		} else {
    			jsonObject.put(key, value.toString());
    		}
    	}
    	StringEntity entity = new StringEntity(jsonObject.toString());
    	entity.setContentType("application/json");
    	return entity;
    }
    
    public static StringEntity createStringEntity(String query) throws UnsupportedEncodingException {
    	StringEntity entity = new StringEntity(query);
    	entity.setContentType("application/x-www-form-urlencoded");
    	return entity;
    }

    private static String createQuery(Map<String, Object> params) {
        List<NameValuePair> queries = new ArrayList<NameValuePair>();
        Set<String> keys = params.keySet();

        for(Iterator<String> iterator = keys.iterator();iterator.hasNext();) {
            String key = iterator.next();
            queries.add(new BasicNameValuePair(key, params.get(key).toString()));
        }

        return createQuery(queries);
    }

    private static String createQuery(List<NameValuePair> queries) {
        return URLEncodedUtils.format(queries, "utf-8");
    }
}
