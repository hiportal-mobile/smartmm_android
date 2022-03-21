package com.ex.smartmm;

import java.util.ArrayList;
import java.util.List;

import com.ex.smartmm.common.Common;
import com.ex.smartmm.common.Configuration;
import com.ex.smartmm.common.DBAdapter;
import com.ex.smartmm.common.DBAdapter_jangbi;
import com.ex.smartmm.vo.Bsinfo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class DialogActivity_BsSel extends Activity implements OnClickListener{
	final String TAG = "BsSelActivity";
	ImageView btn_confirm, btn_cancel;
	Intent intent;
	
	Spinner sp_bonbu, sp_jisa;
	ImageView sp_bonbuB, sp_jisaB;
	DBAdapter_jangbi db = new DBAdapter_jangbi();
	String userType = "3";//1.본사, 2.본부, 3.지사
	List<Bsinfo> bonbuList = new ArrayList<Bsinfo>();
	List<Bsinfo> jisaList = new ArrayList<Bsinfo>();
	int resId = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG,"onCreate()~!");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_bssel);
		//주변 검정으로 변하지않게
//		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		//다이얼로그 테두리 제거
		
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		
		intent = getIntent();
		resId = intent.getIntExtra("btnType", R.id.btn_kr);
		userType = Common.nullCheck(intent.getStringExtra("userType"));
		
		Log.d(TAG, "resId : "+resId);
		Log.d(TAG, "userType : "+userType);
		
		sp_bonbu = (Spinner)findViewById(R.id.sp_bonbu);
		sp_jisa = (Spinner)findViewById(R.id.sp_jisa);
		
		sp_bonbuB = (ImageView)findViewById(R.id.sp_bonbuB);
		sp_bonbuB.setOnClickListener(this);
		sp_jisaB = (ImageView)findViewById(R.id.sp_jisaB);
		sp_jisaB.setOnClickListener(this);
		
		btn_confirm = (ImageView)findViewById(R.id.btn_confirm);
		btn_confirm.setOnClickListener(this);
		btn_cancel = (ImageView)findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(this);
		
		setSpinnerBonbuInfo();

		if(Common.getPrefString(DialogActivity_BsSel.this, Configuration.SHARED_DEPTNM5).contains("기계화")){
			sp_bonbu.setEnabled(true);
			sp_bonbuB.setEnabled(true);
			sp_jisa.setEnabled(true);
			sp_jisaB.setEnabled(true);
		}else{
			if("2".equals(userType)){//본부 사람일 경우 본부 선택 셀렉트 박스가 표시되지 않는다.
				sp_bonbu.setEnabled(false);
				sp_bonbuB.setEnabled(false);
				sp_bonbu.setBackgroundColor(Color.WHITE);
				sp_bonbuB.setBackgroundColor(Color.WHITE);
			}
			if("3".equals(userType)){//본부 사람일 경우 본부 선택 셀렉트 박스가 표시되지 않는다.
				sp_bonbu.setEnabled(false);
				sp_bonbuB.setEnabled(false);
				sp_bonbu.setBackgroundColor(Color.WHITE);
				sp_bonbuB.setBackgroundColor(Color.WHITE);

				sp_jisa.setEnabled(false);
				sp_jisaB.setEnabled(false);
				sp_jisa.setBackgroundColor(Color.WHITE);
				sp_jisaB.setBackgroundColor(Color.WHITE);
			}
		}

	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(db != null){
			db.close();
		}
	}
	
	@Override
	public void onBackPressed() {
		Common.setPrefString(DialogActivity_BsSel.this, Configuration.SHARED_BTNX, "Y");
		super.onBackPressed();
	}
	
	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.btn_confirm:
			if(resId == R.id.btn_login){
				Log.d(TAG,"onClick(btn_confirm) - BONBU = "+Common.getPrefString(DialogActivity_BsSel.this, Configuration.SHARED_BONBUCODE));
				Log.d(TAG,"onClick(btn_confirm) - JISA = "+Common.getPrefString(DialogActivity_BsSel.this, Configuration.SHARED_JISACODE));

//				SELECTED_BSNAME;
//				SELECTED_JISANAME;

				intent = new Intent(DialogActivity_BsSel.this, MainActivity.class);
				startActivity(intent);
			}
			
			if(resId == R.id.btn_kr){
				Log.d(TAG,"onClick(btn_confirm) - BONBU = "+Common.getPrefString(DialogActivity_BsSel.this, Configuration.SHARED_BONBUCODE));
				Log.d(TAG,"onClick(btn_confirm) - JISA = "+Common.getPrefString(DialogActivity_BsSel.this, Configuration.SHARED_JISACODE));
				//intent = new Intent(DialogActivity_BsSel.this, KyoRyangActivity.class);
				intent = new Intent(DialogActivity_BsSel.this, MainActivity.class);
				startActivity(intent);
			}
			/*if(resId == R.id.btn_kr){
				Log.d(TAG,"onClick(btn_confirm) - BONBU = "+Common.getPrefString(DialogActivity_BsSel.this, Configuration.SHARED_BONBUCODE));
				Log.d(TAG,"onClick(btn_confirm) - JISA = "+Common.getPrefString(DialogActivity_BsSel.this, Configuration.SHARED_JISACODE));
				//intent = new Intent(DialogActivity_BsSel.this, KyoRyangActivity.class);
				intent = new Intent(DialogActivity_BsSel.this, MainActivity.class);
				startActivity(intent);
			}else if(resId == R.id.btn_tn){
				intent = new Intent(DialogActivity_BsSel.this, TunnelActivity_nouse.class);
				startActivity(intent);
			}else if(resId == R.id.btn_ag){
				intent = new Intent(DialogActivity_BsSel.this, AmGeoActivity_nouse.class);
				startActivity(intent);
			}*/
			finish();
			break;
		case R.id.btn_cancel:
			Common.setPrefString(DialogActivity_BsSel.this, Configuration.SHARED_BTNX, "Y");
			finish();
			break;
		case R.id.sp_bonbuB:
			sp_bonbu.performClick();
			break;
		case R.id.sp_jisaB:
			sp_jisa.performClick();
			break;

		default:
			break;
		}
	}
	
	String SELECTED_BSCODE = "";
	String SELECTED_BSNAME = "";
	String SELECTED_JISACODE = "";
	String SELECTED_JISANAME = "";

	String SELECTED_DEPTCODE5 = "";
	String SELECTED_DEPTNAME5 = "";
	
	/**
	 * 본부 선택 박스
	 */
	private void setSpinnerBonbuInfo(){
		Log.d(TAG,"setSpinnerBonbuInfo()~!");
		List<String> spinItems = new ArrayList<String>();
		Cursor cursor = db.selectBms_BSINFO_BONBU();

		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			Log.d(TAG,"JSJ = "+cursor.getString(0)+":"+cursor.getString(1)+":"+cursor.getString(2));
			if(null == cursor.getString(2)){
				Log.d(TAG,"JSJ = sort is null ");
			}else if("".equals(cursor.getString(2))){
				Log.d(TAG,"JSJ = sort is empty ");
			}
		}


		bonbuList.clear();
		Bsinfo bsinfo;
		String userBscode = Common.getPrefString(DialogActivity_BsSel.this, Configuration.SHARED_BSCODE);
		Log.d(TAG,"setSpinnerBonbuInfo() - userBscode userJisacode : " + userBscode);
		int UserPosition = 0;
		Log.d(TAG,"setSpinnerBonbuInfo() - cursor.getCount() : " + cursor.getCount());
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			bsinfo = new Bsinfo();

//			Log.d(TAG,"setSpinnerBonbuInfo = "+cursor.getString(0)+":"+cursor.getString(1)+":"+cursor.getString(2));

			bsinfo.setBscode(cursor.getString(0));
			bsinfo.setBsname(cursor.getString(1));
			
			bonbuList.add(bsinfo);
			if(userBscode.equals(cursor.getString(0)) || i == 0){
				UserPosition = i;
				SELECTED_BSCODE = bonbuList.get(UserPosition).getBscode();
				SELECTED_BSNAME = bonbuList.get(UserPosition).getBsname();
				Log.d(TAG,"setSpinnerBonbuInfo() - SELECTED_BSCODE : " + SELECTED_BSCODE);
				Log.d(TAG,"setSpinnerBonbuInfo() - SELECTED_BSNAME : " + SELECTED_BSNAME);

			}
			
			spinItems.add(cursor.getString(1));
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(DialogActivity_BsSel.this, R.layout.spinner_item, spinItems){
			@SuppressLint("NewApi")
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				if(position == getCount()){
					((TextView)v.findViewById(android.R.id.text1)).setText("");;
					((TextView)v.findViewById(android.R.id.text1)).setText(getItem(getCount()));
				}
				return v;
			}
			@Override
			public int getCount() {
				return super.getCount();
			}
		};
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_bonbu.setAdapter(adapter);
		sp_bonbu.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				
				SELECTED_BSCODE = bonbuList.get(position).getBscode();
				SELECTED_BSNAME = bonbuList.get(position).getBsname();
				Common.setPrefString(DialogActivity_BsSel.this, Configuration.SHARED_BSCODE, SELECTED_BSCODE);
				Common.setPrefString(DialogActivity_BsSel.this, Configuration.SHARED_BONBUCODE, SELECTED_BSCODE);
				setSpinnerJisaInfo(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
			
		});
		sp_bonbu.setSelection(UserPosition);
		setSpinnerJisaInfo(UserPosition);
	}
	
	private void setSpinnerJisaInfo(int bonbuPosition){
		Log.d(TAG,"setSpinnerJisaInfo()~!");
		jisaList.clear();
		List<String> spinItems = new ArrayList<String>();
		Cursor cursor = db.selectBms_BSINFO_JISA(SELECTED_BSCODE);
		Bsinfo bsinfo;
		int UserPosition = 0;
		String userJisacode = Common.getPrefString(DialogActivity_BsSel.this, Configuration.SHARED_JISACODE);
		String userDeptcode5 = Common.getPrefString(DialogActivity_BsSel.this, Configuration.SHARED_DEPTCD5);

		Log.d(TAG,"setSpinnerJisaInfo() - userBscode userJisacode  " + userJisacode);
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			bsinfo = new Bsinfo();
			
			//bsinfo.setBscode(cursor.getString(0));
			//bsinfo.setBsname(cursor.getString(1));
			
			bsinfo.setJscode(cursor.getString(0));
			bsinfo.setJsname(cursor.getString(1));
			
			jisaList.add(bsinfo);
			
			
			
			if(userJisacode.equals(cursor.getString(0)) || i ==0){
				UserPosition = i;
				//SELECTED_JISACODE = jisaList.get(UserPosition).getBscode();
				//SELECTED_JISANAME = jisaList.get(UserPosition).getBsname();
				SELECTED_JISACODE = jisaList.get(UserPosition).getJscode();
				SELECTED_JISANAME = jisaList.get(UserPosition).getJsname();
				Log.d(TAG,"setSpinnerJisaInfo() - SELECTED_BSCODE  : " + SELECTED_JISACODE);
				Log.d(TAG,"setSpinnerJisaInfo() - SELECTED_BSNAME  : " + SELECTED_JISANAME);
			}

			if(userDeptcode5.equals(cursor.getString(0)) || i ==0){
				UserPosition = i;
				//SELECTED_JISACODE = jisaList.get(UserPosition).getBscode();
				//SELECTED_JISANAME = jisaList.get(UserPosition).getBsname();
				SELECTED_DEPTCODE5 = jisaList.get(UserPosition).getJscode();
				SELECTED_DEPTNAME5 = jisaList.get(UserPosition).getJsname();
				Log.d(TAG,"setSpinnerJisaInfo() - SELECTED_DEPTCODE5  : " + SELECTED_DEPTCODE5);
				Log.d(TAG,"setSpinnerJisaInfo() - SELECTED_DEPTNAME5  : " + SELECTED_DEPTNAME5);
			}


			spinItems.add(cursor.getString(1));
		}
		
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(DialogActivity_BsSel.this, R.layout.spinner_item, spinItems){
			@SuppressLint("NewApi")
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				if(position == getCount()){
					((TextView)v.findViewById(android.R.id.text1)).setText("");
					((TextView)v.findViewById(android.R.id.text1)).setText(getItem(getCount()));
				}
				return v;
			}
			@Override
			public int getCount() {
				return super.getCount();
			}
		};
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_jisa.setAdapter(adapter);
		sp_jisa.setSelection(UserPosition);
		sp_jisa.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				SELECTED_JISACODE = jisaList.get(position).getJscode();
				SELECTED_JISANAME = jisaList.get(position).getJsname();
				Log.d(TAG,"onItemSelected() - SELECTED_BSCODE  : " + SELECTED_JISACODE);
				Log.d(TAG,"onItemSelected() - SELECTED_BSNAME  : " + SELECTED_JISANAME);
				Common.setPrefString(DialogActivity_BsSel.this, Configuration.SHARED_JISACODE, SELECTED_JISACODE);
				Common.setPrefString(DialogActivity_BsSel.this, Configuration.SHARED_JISACODE, SELECTED_JISACODE);

				Common.setPrefString(DialogActivity_BsSel.this, Configuration.SHARED_BSSEL_BSCODE, SELECTED_BSCODE);
				Common.setPrefString(DialogActivity_BsSel.this, Configuration.SHARED_BSSEL_BSNAME, SELECTED_BSNAME);
				Common.setPrefString(DialogActivity_BsSel.this, Configuration.SHARED_BSSEL_JISACODE, SELECTED_JISACODE);
				Common.setPrefString(DialogActivity_BsSel.this, Configuration.SHARED_BSSEL_JISANAME, SELECTED_JISANAME);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
			
		});

	}
	
}
