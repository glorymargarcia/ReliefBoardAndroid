package com.stratpoint.reliefboard.model;

import java.util.Date;

import android.os.Parcel;

public class ResponseHelp extends BaseModel {
	private String tags;
	private String logoUrl;
	private String fbId;
	private String status;
	private String placeTag;
	private String actionStatus;
	private String sourceType;
	private Date dateCreated;
	private String appName;
	private String senderNumber;
	private String message;
	private String sender;
	private String responseId;
	private String source;
	private Date dateUpdated;
	private String expiryDate;
	private String parentId;
	
	public ResponseHelp(){}
	
	public ResponseHelp(Parcel in){
		readFromParcel(in);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(tags);
		dest.writeString(logoUrl);
		dest.writeString(fbId);
		dest.writeString(status);
		dest.writeString(placeTag);
		dest.writeString(actionStatus);
		dest.writeString(sourceType);
		dest.writeLong(dateCreated.getTime());
		dest.writeString(appName);
		dest.writeString(senderNumber);
		dest.writeString(message);
		dest.writeString(sender);
		dest.writeString(responseId);
		dest.writeString(source);
		dest.writeLong(dateUpdated.getTime());
		dest.writeString(expiryDate);
		dest.writeString(parentId);
	}

	@Override
	public void readFromParcel(Parcel in) {
		tags = in.readString();
		logoUrl = in.readString();
		fbId = in.readString();
		status = in.readString();
		placeTag = in.readString();
		actionStatus = in.readString();
		sourceType = in.readString();
		dateCreated = new Date(in.readLong());
		appName = in.readString();
		senderNumber = in.readString();
		message = in.readString();
		sender = in.readString();
		responseId = in.readString();
		source = in.readString();
		dateUpdated = new Date(in.readLong());
		expiryDate = in.readString();
		parentId = in.readString();
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getFbId() {
		return fbId;
	}

	public void setFbId(String fbId) {
		this.fbId = fbId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPlaceTag() {
		return placeTag;
	}

	public void setPlaceTag(String placeTag) {
		this.placeTag = placeTag;
	}

	public String getActionStatus() {
		return actionStatus;
	}

	public void setActionStatus(String actionStatus) {
		this.actionStatus = actionStatus;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getSenderNumber() {
		return senderNumber;
	}

	public void setSenderNumber(String senderNumber) {
		this.senderNumber = senderNumber;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getResponseId() {
		return responseId;
	}

	public void setResponseId(String responseId) {
		this.responseId = responseId;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public void setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

}
