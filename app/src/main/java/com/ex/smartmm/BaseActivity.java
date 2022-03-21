package com.ex.smartmm;

import com.ex.smartmm.common.Common;
import com.ex.smartmm.common.Configuration;
import com.ex.smartmm.common.DBAdapter;
import com.ex.smartmm.common.DBAdapter_checklist;
import com.ex.smartmm.common.DBAdapter_jangbi;
import com.ex.smartmm.common.PermissionUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

public class BaseActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback{
	public static final String TAG = "BaseActivity";
	Common common;
	DBAdapter db = new DBAdapter();
	DBAdapter_jangbi jangbiDb = new DBAdapter_jangbi();
	DBAdapter_checklist checkDb = new DBAdapter_checklist();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		requestLocationPermission();
//		requestMediaPermission();
		requestReadPhonePermission();

		common = new Common(BaseActivity.this);
		common.copyFile("smartmm_checklist.mp4");
		checkDb = new DBAdapter_checklist();
		checkDb.close();
		checkDb.init();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d("","db.getSqliteStatus() " + db.getSqliteStatus());
		if(!db.getSqliteStatus()){
			db.init();
		}
		Log.d("","jangbiDb.getSqliteStatus() " + jangbiDb.getSqliteStatus());
		if(!jangbiDb.getSqliteStatus()){
			jangbiDb.init();
		}
		Log.d("","checkDb.getSqliteStatus() " + checkDb.getSqliteStatus());
		if(!checkDb.getSqliteStatus()){
			checkDb.init();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		if(null != db){
			db.close();
		}
		if(null != jangbiDb){
			jangbiDb.close();
		}
		if(null != checkDb){
			checkDb.close();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	//********************** 권한 *************************
	private View mLayout;
	private static String[] PERMISSIONS_LOCATION = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
	private static String[] PERMISSIONS_STORAGE = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

	// FlashLight 임시 삭제, shmoon90, 20200109
	//private static String[] PERMISSION_CAMERA = {Manifest.permission.CAMERA, Manifest.permission.FLASHLIGHT};
	private static String[] PERMISSION_CAMERA = {Manifest.permission.CAMERA};
	private static String[] PERMISSION_MEDIA = {Manifest.permission.MEDIA_CONTENT_CONTROL};
	private static String[] PERMISSION_READPHONE = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_SMS};

	private static String[] PERMISSION_ALL = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION,
			android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.CAMERA,
			Manifest.permission.MEDIA_CONTENT_CONTROL,
			Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_SMS};

	private int REQUEST_LOCATION_PERMISSION = 2001;
	private int REQUEST_STORAGE_PERMISSION_TYPE = 2002;
	private int REQUEST_CAMERA_PERMISSION_TYPE = 2003;
	private int REQUEST_MEDIA_PERMISSION_TYPE = 2004;
	private int REQUUEST_READ_PHONE_TYPE = 2005;
	public int REQUUEST_ALL = 2006;


	private void requestReadPhonePermission() {
		mLayout = findViewById(R.id.mainll);
		ActivityCompat.requestPermissions(BaseActivity.this, PERMISSION_ALL, REQUUEST_ALL);

//		if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
//			Log.i("","Displaying LOCATION permission rationale to provide additional context.");
//			ActivityCompat.requestPermissions(BaseActivity.this, PERMISSION_READPHONE, REQUUEST_READ_PHONE_TYPE);
//		} else {
//			ActivityCompat.requestPermissions(BaseActivity.this, PERMISSION_READPHONE, REQUUEST_READ_PHONE_TYPE);
//		}
	}


	private void requestMediaPermission() {
		mLayout = findViewById(R.id.mainll);
		if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.MEDIA_CONTENT_CONTROL)) {
			Log.i("","Displaying LOCATION permission rationale to provide additional context.");
			ActivityCompat.requestPermissions(BaseActivity.this, PERMISSION_MEDIA, REQUEST_MEDIA_PERMISSION_TYPE);
		} else {
			ActivityCompat.requestPermissions(BaseActivity.this, PERMISSION_MEDIA, REQUEST_MEDIA_PERMISSION_TYPE);
		}
	}


	private void requestLocationPermission() {
		mLayout = findViewById(R.id.mainll);
		if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
				|| ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
			Log.i("","Displaying LOCATION permission rationale to provide additional context.");
			ActivityCompat.requestPermissions(BaseActivity.this, PERMISSIONS_LOCATION, REQUEST_LOCATION_PERMISSION);
		} else {
			ActivityCompat.requestPermissions(BaseActivity.this, PERMISSIONS_LOCATION, REQUEST_LOCATION_PERMISSION);
		}
	}

	private void requestStoragePermission() {
		mLayout = findViewById(R.id.mainll);
		if (ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)
				|| ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
			Log.i("","Displaying camera permission rationale to provide additional context.");
			ActivityCompat.requestPermissions(BaseActivity.this, PERMISSIONS_STORAGE, REQUEST_STORAGE_PERMISSION_TYPE);
		} else {
			ActivityCompat.requestPermissions(BaseActivity.this, PERMISSIONS_STORAGE, REQUEST_STORAGE_PERMISSION_TYPE);
		}
	}

	private void requestCameraPermission(){
		mLayout = findViewById(R.id.mainll);
		// FlashLight 임시 삭제, shmoon90, 20200109
		/*
		if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.FLASHLIGHT)
				|| ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.CAMERA)) {
		 */
		if (ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.CAMERA)) {
			Log.i("","Displaying camera permission rationale to provide additional context.");
			ActivityCompat.requestPermissions(BaseActivity.this, PERMISSION_CAMERA, REQUEST_CAMERA_PERMISSION_TYPE);
		} else {
			ActivityCompat.requestPermissions(BaseActivity.this, PERMISSION_CAMERA, REQUEST_CAMERA_PERMISSION_TYPE);
		}
	}

	@SuppressLint("Override")
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode == REQUEST_LOCATION_PERMISSION) {
//							startGpsService();
			Log.i("permission", "Received response for location permissions request." + PermissionUtil.verifyPermissions(grantResults));
			if (PermissionUtil.verifyPermissions(grantResults)) {
				requestStoragePermission();
			} else {
				Log.i("permission", "Contacts permissions were NOT granted.");
				showPermissionDialog();
			}


		}else if (requestCode == REQUEST_STORAGE_PERMISSION_TYPE) {

			Log.i("permission", "Received response for location permissions request." + PermissionUtil.verifyPermissions(grantResults));
			if (PermissionUtil.verifyPermissions(grantResults)) {
				requestCameraPermission();
				Common.setPrefString(BaseActivity.this, Configuration.SHARED_PERMISSION_CALL, "Y");
			} else {
				Log.i("permission", "Contacts permissions were NOT granted.");
				showPermissionDialog();
			}

		}else if(requestCode == REQUEST_CAMERA_PERMISSION_TYPE){
			Log.i("permission", "Received response for location permissions request." + PermissionUtil.verifyPermissions(grantResults));
			if (PermissionUtil.verifyPermissions(grantResults)) {
				
			} else {
				Log.i("permission", "Contacts permissions were NOT granted.");
				showPermissionDialog();
			}
		}else if(requestCode == REQUUEST_ALL){
			Log.i("permission", "Received response for location permissions request." + PermissionUtil.verifyPermissions(grantResults));


		}else{
//							super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}

	}

	
	/** 사용자 이름 만들기 -CameraBaseActivity 에도 잇음
	 * @param context
	 * @return
	 */
	public String getUserNm(Context context){
		String user_nm = Common.getPrefString(context, Configuration.SHARED_USERNM);
		if("".equals(user_nm)){
			//user_nm = db.selectBms_BSINFO_JISANAME(Common.getPrefString(context, Configuration.SHARED_JISACODE));
			user_nm = jangbiDb.selectBms_BSINFO_JISANAME(Common.getPrefString(context, Configuration.SHARED_JISACODE));
			user_nm = user_nm+" 안전순찰";
			Log.d(TAG, "getUserNm() - user_nm : "+user_nm);
		}
		return user_nm;
	}
	
	private void showPermissionDialog(){
		/*AlertDialog.Builder adb	= new AlertDialog.Builder(BaseActivity.this);
		adb.setCancelable(false);
		adb.setTitle("스마트원샷");
		adb.setMessage("사용 권한 획득에 실패하였 습니다.\n관리자에게 문의 하시기 바랍니다.");
		adb.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		adb.show();*/
	}
		
}
