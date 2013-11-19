
package com.stratpoint.reliefboard.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteAdapter {


	private static final String MYDATABASE_NAME = "DataBase";
	private static final int MYDATABASE_VERSION = 1;

	//enum BindType {BLOB, DOUBLE, LONG, NULL, STRING};

	private SQLiteHelper sqLiteHelper;
	private SQLiteDatabase sqLiteDatabase;


	private SQLiteStatement insert;
	private Context context;
	private String errorMsg="";

	//static BindType[] bt = BindType.values();

	public SQLiteAdapter(Context c){
		context = c;
	}

	public SQLiteAdapter openToRead() throws android.database.SQLException {
		sqLiteHelper = new SQLiteHelper(context, MYDATABASE_NAME, null, MYDATABASE_VERSION);
		sqLiteDatabase = sqLiteHelper.getReadableDatabase();
		Log.v("DATABASE CREATED", "READ");
		return this;	
	}

	public SQLiteAdapter openToWrite() throws android.database.SQLException {
		sqLiteHelper = new SQLiteHelper(context, MYDATABASE_NAME, null, MYDATABASE_VERSION);
		sqLiteDatabase = sqLiteHelper.getWritableDatabase();
		Log.v("DATABASE CREATED", "WRITE");
		return this;	
	}

	public void close(){
		sqLiteHelper.close();
	}

	//INSERT QUERY MODIFIED
	public boolean execQuery(String query){

		boolean result=false;

		setErrorMsg("");

		try {
			sqLiteDatabase.execSQL(query);
			result=true;
			Log.v("QUERY", " "+query);

		} catch (Exception e) {
			// TODO: handle exception
			Log.i("DBASE ERROR", e.getMessage() +"\r sql:" + query);
			setErrorMsg(e.getMessage());
			result=false;
		}
		return result;
	}


	public class SQLiteHelper extends SQLiteOpenHelper {

		public SQLiteHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			//	db.execSQL(SCRIPT_CREATE_DATABASE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
		}
	}
	//SELECT QUERY MODIFIED
	public Cursor read(String query){
	
		Cursor cursor = sqLiteDatabase.rawQuery(query,null);

		return cursor;
		
	}

	public String getValue(String query){

		String myValue="";

		Cursor cursor = sqLiteDatabase.rawQuery(query,null);

		while(cursor.moveToNext()){

			myValue = cursor.getString(0);

		}
		cursor.close();
		Log.v("GET VALUE ", ""+myValue);
		return myValue;
	}

	public Boolean isRecordExists(String query){

		Boolean result=false;

		Cursor cursor = sqLiteDatabase.rawQuery(query,null);

		while(cursor.moveToNext()){

			result = true;

		}
		cursor.close();
		return result;

	}

	public int recordCount(String query){

		int myValue=0;

		Cursor cursor = sqLiteDatabase.rawQuery(query,null);

		myValue = cursor.getCount();
		cursor.close();
		return myValue;

	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public void beginTransaction(){

		sqLiteDatabase.beginTransaction();

	}

	public void endTransaction(){

		sqLiteDatabase.endTransaction();

	}

	public void compiledStatement(String sql){

		//Log.i("COMPILED:", sql);
		insert = sqLiteDatabase.compileStatement(sql);

	}

	public void bindValues(int index, String strValue){

		//STRING
		//Log.i("BIND:", strValue);
		insert.bindString(index, strValue);

	}

	public void executeBind(){

		insert.execute();

	}

	public void setTransactionSucessul(){
		sqLiteDatabase.setTransactionSuccessful();
	}


}
