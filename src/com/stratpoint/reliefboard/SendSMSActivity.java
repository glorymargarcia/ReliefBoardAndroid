package com.stratpoint.reliefboard;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.stratpoint.reliefboardandroid.R;

public class SendSMSActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sendsms);

		final EditText location = (EditText) findViewById(R.id.et_location);
		final EditText name = (EditText) findViewById(R.id.et_name);
		final EditText message = (EditText) findViewById(R.id.et_message);

		Button send = (Button) findViewById(R.id.btn_send_sms);
		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				sendSMS("260011", location.getText().toString() + "/" + name.getText().toString() + "/" + message.getText().toString());
			}
		});

	}

	// Method to send SMS.
	private void sendSMS(String mobNo, String message) {
		String smsSent = "SMS_SENT";
		String smsDelivered = "SMS_DELIVERED";
		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(smsSent), 0);
		PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(smsDelivered), 0);

		// Receiver for Sent SMS.
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(), "SMS sent", Toast.LENGTH_SHORT).show();
					finish();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Toast.makeText(getBaseContext(), "Generic failure", Toast.LENGTH_SHORT).show();
					finish();
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Toast.makeText(getBaseContext(), "No service", Toast.LENGTH_SHORT).show();
					finish();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_SHORT).show();
					finish();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Toast.makeText(getBaseContext(), "Radio off", Toast.LENGTH_SHORT).show();
					finish();
					break;
				}
			}
		}, new IntentFilter(smsSent));

		// Receiver for Delivered SMS.
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(), "SMS delivered", Toast.LENGTH_SHORT).show();
					finish();
					break;
				case Activity.RESULT_CANCELED:
					Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
					finish();
					break;
				}
			}
		}, new IntentFilter(smsDelivered));

		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(mobNo, null, message, sentPI, deliveredPI);
	}

}
