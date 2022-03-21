package com.ex.smartmm.common;

import com.ex.smartmm.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class CameraDialogActivity extends Dialog implements OnClickListener{

	public final String tag = "smartMM CameraDialogActivity";
	
	TextView before_jeongbi, after_jeongbi, title_Dialog;
	ImageView btn_X;
	
	public CameraDialogActivity(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_camera2);
		
		before_jeongbi = (TextView)findViewById(R.id.before_jeongbi);
		after_jeongbi = (TextView)findViewById(R.id.after_jeongbi);
		btn_X = (ImageView)findViewById(R.id.btn_X);
		title_Dialog = (TextView)findViewById(R.id.title_Dialog);
		btn_X.setOnClickListener(this);
	}
	
	public TextView get_before_jeongbi(){
		return before_jeongbi;
	}
	
	public TextView get_after_jeongbi(){
		return after_jeongbi;
	}

	public TextView get_title_Dialog(){
		return title_Dialog;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_X:
			dismiss();
			break;

		default:
			break;
		}
	}

	
	
		
	}

	
	
	//++++++
	


