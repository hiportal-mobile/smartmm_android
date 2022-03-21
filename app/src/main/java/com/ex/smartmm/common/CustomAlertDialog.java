package com.ex.smartmm.common;

import com.ex.smartmm.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAlertDialog extends Dialog implements OnClickListener{

	TextView btn_goCamera;
	TextView btn_goAlbum;
	ImageView title_Dialog;
	ImageView btn_close;
	
	public CustomAlertDialog(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.getimg_dialog);
		
		btn_goCamera = (TextView)findViewById(R.id.btn_goCamera);
		btn_goAlbum = (TextView)findViewById(R.id.btn_goAlbum);
		title_Dialog = (ImageView)findViewById(R.id.title_Dialog);
		btn_close = (ImageView)findViewById(R.id.btn_close);
		
		btn_close.setOnClickListener(this);
	}
	
	public TextView get_btn_goCamera(){
		return btn_goCamera;
	}
	
	public TextView get_btn_goAlbum(){
		return btn_goAlbum;
	}

	public ImageView get_title_Dialog(){
		return title_Dialog;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_close:
			dismiss();
			break;

		default:
			break;
		}
	}

}
