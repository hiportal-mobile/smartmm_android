package com.ex.smartmm;

import com.ex.smartmm.common.Common;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DialogActivity extends Activity implements OnClickListener{
	ImageView btn_close;
	Intent intent;
	
	TextView txt_nsname, txt_ejung;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog);
		intent = getIntent();
		
		txt_nsname = (TextView) findViewById(R.id.txt_buseo);
		txt_ejung = (TextView) findViewById(R.id.txt_jangbi);
		
		
		String na_name = intent.getStringExtra("ns_name");
		String ejung = intent.getStringExtra("ejung");
		if("".equals(Common.nullCheck(na_name)) || "".equals(Common.nullCheck(ejung))){
			txt_nsname.setText("노선외");
			txt_ejung.setText("Km");
		}else{
			txt_nsname.setText(na_name);
			txt_ejung.setText(ejung+"Km");
		}
		
		
		//주변 검정으로 변하지않게
//		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		//다이얼로그 테두리 제거
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		
		btn_close = (ImageView)findViewById(R.id.btn_close);
		btn_close.setOnClickListener(this);
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_close:
			finish();
			break;

		default:
			break;
		}
		
	}
}
