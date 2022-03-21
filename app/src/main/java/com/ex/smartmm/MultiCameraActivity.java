package com.ex.smartmm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.ex.smartmm.common.Common;
import com.ex.smartmm.common.DBAdapter_checklist;
import com.ex.smartmm.common.DBAdapter_jangbi;
import com.ex.smartmm.common.Preview2;
import com.ex.smartmm.vo.JbInfo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MultiCameraActivity extends CameraBaseActivity implements OnClickListener, SensorEventListener{
	
	public static final String TAG = "MultiCameraActivity";
	
	private Common common = new Common();
	
	String AndroidVersion = "";
	boolean AndroidVersionSetMethod= true;
	
	private Preview2 mPreview;
	ImageButton ibtnRunShutter;
	ImageView flashOnOff;
	LinearLayout ll_pic, ll_adtag;
	View addview;
	
	boolean canShutter = true;
	private byte[][] mImageData;
	String resultFileName;
	String fileExternal_png = ".png";
	int camera_orientation = 0;
	public int SCREEN_ORIENT;
	
	String SMARTMM_SAVEFILENAME = "";
	
	String SELECTED_JBCODE = "";
	String SELECTED_JBMYEONG = "";
	String SELECTED_JEONGBIGUBUN = "";
	String SELECTED_SULIITEMCODE = "";
	String SELECTED_SULIITEM = "";
	String SELECTED_DETAILITEM = "";
	String SELECTED_BEFORE_AFTER = "";
	String SELECTED_DRNO = "";
	String SELECTED_DOGONG = "";
	String SELECTED_SWBEONHO = "";
	String SELECTED_BSCODE = "";
	String SELECTED_BUSEOCODE = "";
	String SELECTED_TAGYN = "";
	String SELECTED_CHECK  = "";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()~!");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.multicamera);
		
		AndroidVersion = Build.VERSION.RELEASE;
		if(null != AndroidVersion){
			if(AndroidVersion.length() > 0){
				//AndroidVersion = "4.4.4";
				Log.d(TAG,"Android Version Check 1 = "+AndroidVersion);
				Log.d(TAG,"Android Version Check length = "+AndroidVersion.length());
				
				if(AndroidVersion.length() > 3){
					AndroidVersion = AndroidVersion.substring(0,3);
				}
				
				Log.d(TAG,"Android Version Check 2 = "+AndroidVersion);
				Log.d(TAG,"Android Version Check 3 = "+Double.parseDouble(AndroidVersion));
				
				if(Double.parseDouble(AndroidVersion) <= 4.0){
					AndroidVersionSetMethod = false;	
				}
				Log.d(TAG, "onCreate() - AndroidVersionSetMethod : "+AndroidVersionSetMethod);
			}
		}
		
		
		Intent acceptIntent = getIntent();
		SELECTED_JBCODE = acceptIntent.getStringExtra("SELECTED_JBCODE"); 
		SELECTED_JBMYEONG = acceptIntent.getStringExtra("SELECTED_JBMYEONG"); 
		SELECTED_JEONGBIGUBUN = acceptIntent.getStringExtra("SELECTED_JEONGBIGUBUN"); 
		SELECTED_SULIITEMCODE = acceptIntent.getStringExtra("SELECTED_SULIITEMCODE"); 
		SELECTED_SULIITEM = acceptIntent.getStringExtra("SELECTED_SULIITEM"); 
		SELECTED_DETAILITEM = acceptIntent.getStringExtra("SELECTED_DETAILITEM"); 
		SELECTED_BEFORE_AFTER = acceptIntent.getStringExtra("SELECTED_BEFORE_AFTER"); 
		SELECTED_DRNO = acceptIntent.getStringExtra("SELECTED_DRNO"); 
		SELECTED_DOGONG = acceptIntent.getStringExtra("SELECTED_DOGONG"); 
		SELECTED_SWBEONHO = acceptIntent.getStringExtra("SELECTED_SWBEONHO"); 
		SELECTED_BSCODE = acceptIntent.getStringExtra("SELECTED_BSCODE"); 
		SELECTED_BUSEOCODE = acceptIntent.getStringExtra("SELECTED_BUSEOCODE"); 
		SELECTED_CHECK  = acceptIntent.getStringExtra("SELECTED_CHECK"); 
		
		Log.d(TAG, "onCreate() - SELECTED_JBCODE : "+SELECTED_JBCODE);
		Log.d(TAG, "onCreate() - SELECTED_JBMYEONG : "+SELECTED_JBMYEONG);
		Log.d(TAG, "onCreate() - SELECTED_JEONGBIGUBUN : "+SELECTED_JEONGBIGUBUN);
		Log.d(TAG, "onCreate() - SELECTED_SULIITEMCODE : "+SELECTED_SULIITEMCODE);
		Log.d(TAG, "onCreate() - SELECTED_SULIITEM : "+SELECTED_SULIITEM);
		Log.d(TAG, "onCreate() - SELECTED_DETAILITEM : "+SELECTED_DETAILITEM);
		Log.d(TAG, "onCreate() - SELECTED_BEFORE_AFTER : "+SELECTED_BEFORE_AFTER);
		Log.d(TAG, "onCreate() - SELECTED_DRNO : "+SELECTED_DRNO);
		Log.d(TAG, "onCreate() - SELECTED_DOGONG : "+SELECTED_DOGONG);
		Log.d(TAG, "onCreate() - SELECTED_SWBEONHO : "+SELECTED_SWBEONHO);
		Log.d(TAG, "onCreate() - SELECTED_BSCODE : "+SELECTED_BSCODE);
		Log.d(TAG, "onCreate() - SELECTED_BUSEOCODE : "+SELECTED_BUSEOCODE);
		Log.d(TAG, "onCreate() - SELECTED_CHECK : "+SELECTED_CHECK);
		Log.d(TAG, "onCreate() - SMARTMM_SAVEFILENAME : "+SMARTMM_SAVEFILENAME);
		
		ll_adtag = (LinearLayout) findViewById(R.id.ll_adtag);
		Log.d("BLOCK", "BLOCK = " + acceptIntent.getBooleanExtra("BLOCK", false));
		if(false == acceptIntent.getBooleanExtra("TAGYN", false)){
			ll_adtag.setVisibility(View.GONE);
			SELECTED_TAGYN = "N";
		}else{
			ll_adtag.setVisibility(View.VISIBLE);
			SELECTED_TAGYN = "Y";
		}
		
		createSaveFileName();
		/*SMARTMM_SAVEFILENAME = SELECTED_JBCODE+"_"+Common.getCalendarDateTime()+fileExternal_png;
		Log.d(TAG, "onCreate() - SMARTMM_SAVEFILENAME : "+SMARTMM_SAVEFILENAME);*/
		
		customDrawviewDraw();
		
		Resources resource = Resources.getSystem();
		Configuration config = resource.getConfiguration();
		SCREEN_ORIENT = config.orientation;
		
		onConfigurationChanged(config);
		
		mPreview = (Preview2) findViewById(R.id.camera_preview);
		Log.d("",TAG+" rotation = " +rotation);  
		//카메라 최저 해상도 설정.
				Camera.Parameters parameters = mPreview.mCamera.getParameters();
				if(rotation == 0){//portrait
					mPreview.mCamera.setDisplayOrientation(90);
				}else if(rotation == 3){//landScape right
					mPreview.mCamera.setDisplayOrientation(180);
				}
				//Camera Preview 사이즈 조절

				List<Size> sizes = parameters.getSupportedPictureSizes();
				Log.d("", TAG + " onCreate 4");
				for (int i = 0; i < sizes.size(); i++) {
					Log.d("",TAG+" onCreate 4 for " + i);
					int width = sizes.get(i).width;
					int height = sizes.get(i).height;
					String prefString = width+"x"+height;
					parameters.setPictureSize(width, height);
					Log.d("","CameraActivity2 width height result = [" + width +"x"+height+"");
					if(width >1000 && width < 2000){
						break;
					}
				}
				
				mPreview.mCamera.setParameters(parameters);
		
		mImageData = new byte[6][];
		ibtnRunShutter = (ImageButton)findViewById(R.id.ibtnRunShutter);
		ibtnRunShutter.setOnClickListener(this);
		
		flashOnOff = (ImageView)findViewById(R.id.flashOnOff);
		flashOnOff.setOnClickListener(this);
		
		//후레시 모양 바꾸기
		if(mPreview.mCamera.getParameters().getFlashMode().equals(android.hardware.Camera.Parameters.FLASH_MODE_TORCH)){
			if(AndroidVersionSetMethod){
				flashOnOff.setBackground(getResources().getDrawable(R.drawable.bolton));
			}
		}else{
			if(AndroidVersionSetMethod){
				flashOnOff.setBackground(getResources().getDrawable(R.drawable.boltoff));
			}
		}
	}

	
	Camera.PictureCallback mPictureCallbackRaw = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] data, Camera c) {

		}
	};
	
	Camera.PictureCallback mPictureCallbackJpeg = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] data, Camera c) {
			Log.d(TAG, "&^&^&^333");

			// 파일로 저장
			mImageData[0] = data;

			resultFileName = SMARTMM_SAVEFILENAME;
			Log.d(TAG, "&^&^&^444 - resultFileName :"+resultFileName);
			Log.d(TAG, "&^&^&^555");
			mPreview.mCamera.stopPreview();
			Log.d(TAG, "&^&^&^222");
			new SaveImageBackground().execute("");
		}
	};
	
	Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
		public void onShutter() {
			Log.i(getClass().getSimpleName(), "SHUTTER CALLBACK");
		}
	};
	
	Camera.AutoFocusCallback cb = new Camera.AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera c) {

			if (success) {
				try {

					if (mPreview.mCamera != null) {

						mPreview.mCamera.takePicture(mShutterCallback,
								mPictureCallbackRaw, mPictureCallbackJpeg);
					}
				} catch (Exception e) {

					Log.d("Camera", e.toString());
				}finally{

					canShutter = true;
				}
			} else {
				try {

					if (mPreview.mCamera != null) {

						mPreview.mCamera.takePicture(mShutterCallback,
								mPictureCallbackRaw, mPictureCallbackJpeg);
					}
				} catch (Exception e) {

					Log.d("Camera", e.toString());
				}finally{

					canShutter = true;
				}
			}
		}
	};
	
	
	public void createSaveFileName(){
		SMARTMM_SAVEFILENAME = SELECTED_JBCODE+"_"+Common.getCalendarDateTime()+fileExternal_png;
		Log.d(TAG, "createSaveFileName() - SMARTMM_SAVEFILENAME : "+SMARTMM_SAVEFILENAME);
	}
	
	
	public void customDrawviewDraw(){
		Log.d(TAG, "customDrawviewDraw()~!");
		String[] titleList = {"장비명","도공번호","수리내용","정비일시"};
		String[] contentList = {Common.nullCheck(SELECTED_JBMYEONG+" ("+SELECTED_DRNO+") "), Common.nullCheck(SELECTED_DOGONG),  Common.nullCheck(SELECTED_SULIITEM+"/"+SELECTED_DETAILITEM+" ("+SELECTED_BEFORE_AFTER+") "), Common.getCalendarDateYMDHMS().substring(0, 16)};
		
		int textLength = 0;
		int curLength = 0;
		Log.d(TAG,"customDrawviewDraw() - Acceptlist length = " + contentList.length);
		for (int i = 0; i < contentList.length; i++) {
			Log.d(TAG,"customDrawviewDraw() - Acceptlist item = " + contentList[i]);
			if(i==0){
				textLength = contentList[i].length();
			}else{
				curLength = contentList[i].length();
				if(textLength < curLength){
					textLength = curLength;
				}
			}
			Log.d(TAG,TAG+"textLength = " + textLength);
		}

		DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		double finder = (textLength/5.0)*0.1 ;

		int listCnt = contentList.length;
		int line = textLength/17;
		if(line == 0 ) line = 1;
		int colHeight = 76*line;
		int totlaHeight = ((listCnt)*colHeight)+20;
		int titleWidth = 160;
		
		int contentWidth = (int)(width*finder);
		if(camera_orientation != 0){//LandScape 상태
			contentWidth = (int)(contentWidth*0.6);
		}
		if(contentWidth > width) contentWidth = width-titleWidth;
		int totalWidth = titleWidth+contentWidth;
		int colWidth = 120;
		int textSize = 10;

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width/3, RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		
		layoutParams.setMargins(10,10,10,10);

		ll_adtag = (LinearLayout) findViewById(R.id.ll_adtag);
		ll_adtag.setLayoutParams(layoutParams);

		
		ll_adtag.setGravity(Gravity.RIGHT);

		TextView title ;
		TextView content ;
		FrameLayout.LayoutParams innerLayoutParam = new FrameLayout.LayoutParams(width/3, FrameLayout.LayoutParams.WRAP_CONTENT);
		FrameLayout.LayoutParams txtTitleParam = new FrameLayout.LayoutParams(titleWidth, FrameLayout.LayoutParams.WRAP_CONTENT);
		FrameLayout.LayoutParams txtContentParam = new FrameLayout.LayoutParams((width/3)-titleWidth, FrameLayout.LayoutParams.WRAP_CONTENT);

		for (int i = 0; i < listCnt; i++) {
			LinearLayout innerLayout = new LinearLayout(MultiCameraActivity.this);
			innerLayout.setLayoutParams(innerLayoutParam);
			innerLayout.setOrientation(LinearLayout.HORIZONTAL);
			if(AndroidVersionSetMethod){
				innerLayout.setBackground(getResources().getDrawable(R.drawable.border_style_ll));
			}
			innerLayout.setGravity(Gravity.CENTER_VERTICAL);
			//제목
			title = new TextView(MultiCameraActivity.this);
			title.setLayoutParams(txtTitleParam);
			title.setText(titleList[i]);
			title.setTextColor(Color.BLACK);
			title.setGravity(Gravity.CENTER);
			title.setTextSize(textSize);


			innerLayout.addView(title);


			//내용
			content = new TextView(MultiCameraActivity.this);
			content.setLayoutParams(txtContentParam);
			content.setText(contentList[i]);
			if(AndroidVersionSetMethod){
				content.setBackground(getResources().getDrawable(R.drawable.border_stylecontent));
			}

			content.setTextColor(Color.BLACK);
			content.setGravity(Gravity.CENTER_VERTICAL);
			content.setTextSize(textSize);
			content.setPadding(10, 0, 0, 0);
			innerLayout.addView(content);

			ll_adtag.setBackgroundColor(Color.WHITE);
			ll_adtag.addView(innerLayout);
		}

		addview = ll_adtag;
	}
	
	
	int rotation = 0;
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onConfigurationChanged()~!");
		super.onConfigurationChanged(newConfig);
		rotation = this.getWindowManager().getDefaultDisplay().getRotation();
		//rotation 강제 셋팅 (Galaxy Tab rotation = 0, 다른 스마트폰 rotation = 1)
		rotation = 1;
		
		Log.d(TAG, "onConfigurationChanged() - rotation = " + rotation);
		if (newConfig.orientation == newConfig.ORIENTATION_PORTRAIT) {
			Log.d(TAG,"onConfigurationChanged() = portrait");
		}else {
			Log.d(TAG,"onConfigurationChanged() = landscape");
		}
	}
	
	private SensorManager mSensorManager;
	public void setSensor(){
		Log.d(TAG, "setSensor()~!");
		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
	}
	
	public void runShutter(){
		Log.d(TAG, "runShutter()~!");
		if (mPreview.mCamera != null) {
			if(canShutter){
				canShutter = false;
				try {
					mPreview.mCamera.autoFocus(cb);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
		
		case R.id.ibtnRunShutter:
			Log.d(TAG, "onClick(ibtnRunShutter)~!");
			/*if (mPreview.mCamera != null) {
				if(canShutter){
					canShutter = false;
					try {
						mPreview.mCamera.autoFocus(cb);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			}*/
			runShutter();
			Log.d(TAG, "onClick(ibtnRunShutter) canShutter : "+canShutter);
			Log.d(TAG, "onClick(ibtnRunShutter) END");
			break;
		
		
		
		case R.id.flashOnOff :
			if(mPreview.mCamera.getParameters().getFlashMode().equals(android.hardware.Camera.Parameters.FLASH_MODE_TORCH)){
				mPreview.FlashOff();
				if(AndroidVersionSetMethod){
					flashOnOff.setBackground(getResources().getDrawable(R.drawable.boltoff));
				}
			}else{
				mPreview.FlashOn();
				if(AndroidVersionSetMethod){
					flashOnOff.setBackground(getResources().getDrawable(R.drawable.bolton));
				}
			}
			break;
		}
		
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Log.d(TAG, "onBackPressed()~!");
		
		if(SELECTED_CHECK != null && !SELECTED_CHECK.equals("")){
			Cursor cursor = checkDb.selectSmart_DATAINFO_forCamera(SELECTED_CHECK);
			Log.d(TAG, "onBackPressed() - cursorCount : "+cursor.getCount());
			while (cursor.moveToNext()) {
				int i = cursor.getPosition();
				//Log.d(TAG, "onBackPressed() - cursor getPosition : "+i);
				//Log.d(TAG,"onBackPressed() - JeongbiGB : "+cursor.getString(0));		// [1]자체
				//Log.d(TAG,"onBackPressed() - ItemGB : "+cursor.getString(1));			// [1]원동기
				//Log.d(TAG,"onBackPressed() - DetailGB : "+cursor.getString(2));			// 오일교환
				//Log.d(TAG,"onBackPressed() - CONTENT : "+cursor.getString(3));			// 원동기/오일교환(정비 전)
				Log.d(TAG,"onBackPressed() - FILENAME : "+cursor.getString(4));			// 사진경로 및 이름
				//Log.d(TAG,"onBackPressed() - JBCODE : "+cursor.getString(5));			// 장비코드
				Log.d(TAG,"onBackPressed() - JBMYEONG : "+cursor.getString(6));			// 장비명 - 지휘순찰차
				//Log.d(TAG,"onBackPressed() - DRNO : "+cursor.getString(7));				// 등록번호 - 인천34거3522
				Log.d(TAG,"onBackPressed() - NAME : "+cursor.getString(8));				// 안전순찰차(인천34거3522)
				Log.d(TAG,"onBackPressed() - DOGONG : "+cursor.getString(9));			// 도공번호
				//Log.d(TAG,"onBackPressed() - DATE : "+cursor.getString(10));			// 정비일시
				//Log.d(TAG,"onBackPressed() - SENDYN : "+cursor.getString(11));			// sendYN
				//Log.d(TAG,"onBackPressed() - SWBEONHO : "+cursor.getString(12));		// 사원번호
				//Log.d(TAG,"onBackPressed() - BONBUCODE : "+cursor.getString(13));		// 본부코드
				//Log.d(TAG,"onBackPressed() - JISACODE : "+cursor.getString(14));		// 지사코드
				//Log.d(TAG,"onBackPressed() - TAGYN : "+cursor.getString(15));			// tegYN
				//Log.d(TAG,"onBackPressed() - SWNAME : "+cursor.getString(16));			// 사원이름
				Log.d(TAG,"onBackPressed() - CHECKDATE : "+cursor.getString(17));		// 체크날짜
			}
			
			
			if(cursor.getCount() > 0){
				Toast.makeText(MultiCameraActivity.this, "사진이 저장 되었습니다.", Toast.LENGTH_SHORT).show();
				MultiCameraActivity.this.finish();
			}
		}
	}

	/****************************** SaveImageBackground CLASS ******************************/
	ProgressDialog progressDialog;
	public class SaveImageBackground extends AsyncTask<String, String, String>{
		
		public SaveImageBackground(){
			Log.d(TAG, "******************** SaveImageBackground CLASS ********************");
			if(progressDialog != null){
				progressDialog.dismiss();
				progressDialog = null;
			}
			progressDialog = ProgressDialog.show(MultiCameraActivity.this, "", "저장중...", true);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... arg0) {
			Log.d("", "SaveImageBackground CLASS - doInBackground()~!");
			String seq = "";
			SaveImage(SMARTMM_SAVEFILENAME);
			
			Cursor cursor = checkDb.selectSmart_DATAINFO_forCamera(SELECTED_CHECK);
			Log.d(TAG, "doInBackground() - cursorCount : "+cursor.getCount());
			Log.d(TAG, "doInBackground() - SELECTED_CHECK1 : "+SELECTED_CHECK);
			if(cursor.getCount() > 0){
			}else{
				SELECTED_CHECK = Common.getCalendarDateYMDHMS();
			}
			
			Log.d(TAG, "doInBackground() - SELECTED_CHECK2 : "+SELECTED_CHECK);
			
			checkDb.insertSmart_DATAINFO(
					SELECTED_JEONGBIGUBUN, 
					"["+SELECTED_SULIITEMCODE+"]"+SELECTED_SULIITEM, 
					SELECTED_DETAILITEM, 
					SELECTED_SULIITEM+"/"+SELECTED_DETAILITEM+"("+SELECTED_BEFORE_AFTER+")", 
					com.ex.smartmm.common.Configuration.dirRoot_kr + SMARTMM_SAVEFILENAME, 
					SELECTED_JBCODE, 
					SELECTED_JBMYEONG, 
					SELECTED_DRNO, 
					SELECTED_JBMYEONG+"("+SELECTED_DRNO+")", 
					common.nullCheck(SELECTED_DOGONG), 
					Common.getCalendarDateYMDHMS(),
					SELECTED_SWBEONHO, 
					SELECTED_BSCODE, 
					SELECTED_BUSEOCODE, 
					SELECTED_TAGYN, 
					getUserNm(MultiCameraActivity.this),
					SELECTED_CHECK
					);
			
			refreshSD(SMARTMM_SAVEFILENAME);
			return "";
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			//super.onPostExecute(result);
			Log.d(TAG, "onPostExecute()~!");
			if(progressDialog != null){
				Log.d(TAG,"onPostExecute() - progressDialog : "+progressDialog);
				progressDialog.dismiss();
				progressDialog = null;
			}
			Log.d(TAG, "onPostExecute() - canShutter : "+canShutter);
			
			//canShutter = true;
			SMARTMM_SAVEFILENAME = "";
			createSaveFileName();
			mPreview.mCamera.startPreview();
			
		}
	}
	
	
	public int SaveImage(String sFilename) {
		Log.d(TAG, "SaveImage()~!");
		Log.d(TAG, "SaveImage() - sFilename : "+sFilename);
		int ret = 0;
		try {
			Log.d(TAG,"SaveImage filepath  === "+ com.ex.smartmm.common.Configuration.dirRoot_kr);
			Log.d(TAG,"SaveImage filename=== "+  sFilename);
			Log.d(TAG,"SaveImage filepath+filename === "+ com.ex.smartmm.common.Configuration.dirRoot_kr + sFilename);

			// data[] 로 넘어온 데이터를 bitmap으로 변환
			Bitmap bmp = BitmapFactory.decodeByteArray(mImageData[0], 0, mImageData[0].length);

			Bitmap rotateBitmap = null;
			Log.d(TAG,"SaveImage filepath  === "+ rotation);
			if(rotation == 1){//portrait
				// 화면 회전을 위한 matrix객체 생성
				Matrix m = new Matrix();
				// matrix객체에 회전될 정보 입력
				m.setRotate(90, (float) bmp.getWidth(), (float) bmp.getHeight());
				// 기존에 저장했던 bmp를 Matrix를 적용하여 다시 생성
				rotateBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, false);
				// 기존에 생성했던 bmp 자원해제

			}else if(rotation == 0 || rotation == 3){
				// 화면 회전을 위한 matrix객체 생성
				Matrix m = new Matrix();
				// matrix객체에 회전될 정보 입력
				m.setRotate(180, (float) bmp.getWidth(), (float) bmp.getHeight());
				// 기존에 저장했던 bmp를 Matrix를 적용하여 다시 생성
				rotateBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, false);
				// 기존에 생성했던 bmp 자원해제
			}


			// 파일로 저장
			OutputStream fileoutstream = new FileOutputStream( new File(com.ex.smartmm.common.Configuration.dirRoot_kr + sFilename ));
			if(rotation == 1){//Landscape left
				fileoutstream.write(mImageData[0]);
				fileoutstream.flush();
			}

			if(rotation == 0 || rotation == 3){//portrait || landscape right
				rotateBitmap.compress(CompressFormat.PNG, 100, fileoutstream);
			}
			fileoutstream.close();
			bmp.recycle();
			rotateBitmap.recycle();
			System.gc();

			refreshSD(sFilename);

		} catch (FileNotFoundException fne) {
			Log.e("writing and scanning image ", fne.toString());
			fne.printStackTrace();
			ret = -1;
		} catch (IOException ioe) {
			Log.e("writing and scanning image ", ioe.toString());
			ioe.printStackTrace();
			ret = -1;
		} catch (Exception e) {
			ret = -1;
		}
		return ret;
	}
	
	public void refreshSD(String sFilename) {
		Log.d(TAG,"refreshSD() - "+Environment.getExternalStorageDirectory());
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,	Uri.parse("file://"+ com.ex.smartmm.common.Configuration.dirRoot_kr+ sFilename)));
		
	}
	

	protected void onResume() {

		super.onResume();
		/*db = new DBAdapter();
		db.close();
		db.init();*/
		jangbiDb = new DBAdapter_jangbi();
		jangbiDb.close();
		jangbiDb.init();
		checkDb = new DBAdapter_checklist();
		checkDb.close();
		checkDb.init();
		
		mPreview.mCamera.startPreview();
	}
	
	
	@Override
	protected void onPause() {
		/*if(null != db){
			db.close();
		}*/
		if(null != jangbiDb){
			jangbiDb.close();
		}
		if(null != checkDb){
			checkDb.close();
		}
		super.onPause();
	}

	
	@Override
	protected void onDestroy() {
		Log.e(TAG, "onDestroy() - CameraActivity onDestroy...");
		if(null != progressDialog){
			if(progressDialog.isShowing()){
				progressDialog.dismiss();
			}
			progressDialog = null;
		}
		if (mPreview != null) {

			if (mPreview.mCamera != null) {
				mPreview.mCamera.stopPreview();
				mPreview.mCamera.release();
				mPreview.mCamera = null; // <- nullpointer exception
			}
		}
		super.onDestroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		setResult(RESULT_CANCELED, getIntent());
		return super.onKeyDown(keyCode, event);
	}

	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
