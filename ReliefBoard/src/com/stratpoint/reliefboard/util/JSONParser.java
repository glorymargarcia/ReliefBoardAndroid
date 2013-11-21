package com.stratpoint.reliefboard.util;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.stratpoint.reliefboard.model.ResponseHelp;

public class JSONParser {

	public static final ResponseHelp parseJSONToResponseHelp(JSONObject jsonResponseHelp) throws JSONException {
		ResponseHelp responseHelp = new ResponseHelp();
		responseHelp.setTags(jsonResponseHelp.has("tags") ? jsonResponseHelp.getString("tags") : "");
		responseHelp.setLogoUrl(jsonResponseHelp.has("logo") ? jsonResponseHelp.getString("logo") : "");
		responseHelp.setFbId(jsonResponseHelp.has("fb_id") ? jsonResponseHelp.getString("fb_id") : "");
		responseHelp.setStatus(jsonResponseHelp.has("status") ? jsonResponseHelp.getString("status") : "");
		responseHelp.setPlaceTag(jsonResponseHelp.has("place_tag") ? jsonResponseHelp.getString("place_tag") : "");
		responseHelp.setActionStatus(jsonResponseHelp.has("action_status") ? jsonResponseHelp.getString("action_status") : "");
		responseHelp.setSourceType(jsonResponseHelp.has("source_type") ? jsonResponseHelp.getString("source_type") : "");
		responseHelp.setDateCreated(new Date(jsonResponseHelp.getLong("date_created") * 1000));
		responseHelp.setAppName(jsonResponseHelp.has("app_name") ? jsonResponseHelp.getString("app_name") : "");
		responseHelp.setSenderNumber(jsonResponseHelp.has("sender_number") ? jsonResponseHelp.getString("sender_number") : "");
		responseHelp.setMessage(jsonResponseHelp.has("message") ? jsonResponseHelp.getString("message") : "");
		responseHelp.setSender(jsonResponseHelp.has("sender") ? jsonResponseHelp.getString("sender") : "");
		responseHelp.setResponseId(jsonResponseHelp.has("id") ? jsonResponseHelp.getString("id") : "");
		responseHelp.setSource(jsonResponseHelp.has("source") ? jsonResponseHelp.getString("source") : "");
		responseHelp.setDateUpdated(new Date(jsonResponseHelp.getLong("date_updated") * 1000));
		responseHelp.setSource(jsonResponseHelp.has("expiry_date") ? jsonResponseHelp.getString("expiry_date") : "");
		responseHelp.setParentId(jsonResponseHelp.has("parent_id") ? jsonResponseHelp.getString("parent_id") : "");		
		return responseHelp;
	}
}
