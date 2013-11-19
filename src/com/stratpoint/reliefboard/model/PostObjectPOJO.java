package com.stratpoint.reliefboard.model;

public class PostObjectPOJO {
	private String app_name; 
	private	String date_created;
	private String fb_post_link;
	private String post_id;
	private String logo;
	private String message;
	private String parent_id;
	private String place_tag;
	private String sender;
	private String sender_number;
	private String source;
	private String source_type;
	private String status; 
	
	public  void SetStatus(String stat){
		status =  stat;
	}
	public String GetStatus(){
		return status;
	}
	
	public  void SetSourceType(String sorceType){
		source_type =  sorceType;
	}
	public String GetSourceType(){
		return source_type;
	}
	
	public  void SetSource(String sorce){
		source =  sorce;
	}
	public String GetSource(){
		return source;
	}
	
	public  void SetSenderNumber(String snderNo){
		sender_number =  snderNo;
	}
	public String GetSenderNumber(){
		return sender_number;
	}
	
	public  void SetSender(String snder){
		sender =  snder;
	}
	public String GetSender(){
		return sender;
	}
	
	public  void SetPlaceTag(String plceTag){
		place_tag =  plceTag;
	}
	public String GetPlaceTag(){
		return place_tag;
	}
	
	public  void SetParentID(String parID){
		parent_id = parID;
	}
	public String GetParentID(){
		return parent_id;
	}
	
	public  void SetMessage(String mssage){
		message = mssage;
	}
	public String GetMessage(){
		return message;
	}
	
	public  void SetLogo(String lgo){
		logo = lgo;
	}
	public String GetLogo(){
		return logo;
	}
	
	public  void SetPostID(String postID){
		post_id = postID;
	}
	public String GetPostID(){
		return post_id;
	}
	
	public  void SetFbPostLink(String postLink){
		fb_post_link = postLink;
	}
	public String GetFbPostLink(){
		return fb_post_link;
	}
	
	public  void SetAppName(String appname){
		app_name = appname;
	}
	public String GetAppName(){
		return app_name;
	}
	
	public  void SetDateCreated(String dateCreated){
		date_created = dateCreated;
	}
	public String GetDateCreated(){
		return date_created;
	}
}
