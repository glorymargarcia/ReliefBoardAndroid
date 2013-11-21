package com.stratpoint.reliefboard;

import com.stratpoint.reliefboardandroid.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

@SuppressLint("NewApi")
public class PostDialogFragment extends DialogFragment{
	  Button mButton;  
	  EditText mEditText;  
	 onSubmitListener mListener;  
	 String text = "";  
	
	interface onSubmitListener {  
		void setOnSubmitListener(String arg);  
	}  

	@SuppressLint("NewApi")
	@Override  
	public Dialog onCreateDialog(Bundle savedInstanceState) {  
		final Dialog dialog = new Dialog(getActivity());  
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);  
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		dialog.setContentView(R.layout.activity_post__message);  
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  
		dialog.show();  
		 mButton = (Button) dialog.findViewById(R.id.btn_post_message);  
		mEditText = (EditText) dialog.findViewById(R.id.et_body);  
		mEditText.setText(text);  
		mButton.setOnClickListener(new OnClickListener() {  

			@SuppressLint("NewApi")
			@Override  
			public void onClick(View v) {  
				mListener.setOnSubmitListener(mEditText.getText().toString());  
				dismiss();  
			}  
		});  
		return dialog;  
	}  
}
