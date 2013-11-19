package com.stratpoint.reliefboardandroid;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private TelephonyManager mTelephonyManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTelephonyManager  = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		
		Button btn = (Button) findViewById(R.id.btn_sms);
		btn.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("static-access")
			@Override
			public void onClick(View v) {
				if(mTelephonyManager.SIM_STATE_ABSENT == 0){
					sendSMS();	
				} else {
					Toast.makeText(getApplicationContext(), "Please insert sim card.", Toast.LENGTH_LONG).show();
				}
			}
		});

	}

	private void sendSMS() {
/*		Uri uri = null;
		if(mTelephonyManager.getNetworkOperatorName().equals("Globe Telecom-PH")){
			uri = Uri.parse("smsto:23737102");			
		} else if(mTelephonyManager.getNetworkOperatorName().equals("SMART")){
			uri = Uri.parse("smsto:68009");
		} else {
			uri = Uri.parse("smsto:260011");
		}*/
		
		Uri uri = Uri.parse("smsto:260011");
		
		Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
		intent.putExtra("sms_body", "LOCATION/YOUR NAME/MESSAGE ");
		startActivity(intent);
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.main, menu);
	// return true;
	// }

}
