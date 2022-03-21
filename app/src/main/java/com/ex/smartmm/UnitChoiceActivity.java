package com.ex.smartmm;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class UnitChoiceActivity extends Activity implements OnClickListener{
	
	TextView txt_mm, txt_cm, txt_m;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_unitchoice);
		txt_mm = (TextView) findViewById(R.id.txt_mm);
		txt_cm = (TextView) findViewById(R.id.txt_cm);
		txt_m = (TextView) findViewById(R.id.txt_m);
		
		txt_mm.setOnClickListener(this);
		txt_cm.setOnClickListener(this);
		txt_m.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent i;
		switch (v.getId()) {
		case R.id.txt_mm:
			i = getIntent();
			i.putExtra("unit", "mm");
			setResult(RESULT_OK, i);
			finish();
			break;
		case R.id.txt_cm:
			i = getIntent();
			i.putExtra("unit", "cm");
			setResult(RESULT_OK, i);
			finish();
			break;
		case R.id.txt_m:
			i = getIntent();
			i.putExtra("unit", "m");
			setResult(RESULT_OK, i);
			finish();
			break;

		default:
			break;
		}
		
	}
		
}
