package com.ex.smartmm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.Camera.Size;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ex.smartmm.common.Common;
import com.ex.smartmm.common.DBAdapter;
import com.ex.smartmm.common.DBAdapter_checklist;
import com.ex.smartmm.common.DBAdapter_jangbi;
import com.ex.smartmm.common.Preview2;
import com.ex.smartmm.common.VerticalSeekBar;

@SuppressLint({ "ResourceAsColor", "NewApi" })
public class CameraActivity extends CameraBaseActivity implements OnClickListener, SensorEventListener {

	private Preview2 mPreview;
	private byte[][] mImageData;
	private boolean gFocussed = false;
	private boolean gCameraPressed = false;
	// Camera mCamera = null;
	private static SharedPreferences sPrefs = null;
	public static final String KEY_POPUP_ENV = "key_env";
	public static final String KEY_POPUP_ENV_RUN_MODE = "key_env_run";

	public String mFilename;
	private int mFileNameYear;
	private int mFileNameMonth;
	private int mFileNameDay;
	private int mFileNameCount;

	public static final String SAVE_FILE_YEAR = "sava_file_year";
	public static final String SAVE_FILE_MONTH = "sava_file_month";
	public static final String SAVE_FILE_DATE = "sava_file_date";
	public static final String SAVE_FILE_COUNT = "sava_file_count";

	private Camera camera;
	private int m_resWidth;
	private int m_resHeight;

	private Common common = new Common();
	
	ImageButton ibtnRunShutter;
	ImageButton ibtnSave;
	ImageButton ibtnCancel;
	ImageView flashOnOff;
	LinearLayout ll_pic, ll_save;

	boolean canShutter = true;
	String modegubun = "";
	String resultFileName;

	VerticalSeekBar seekbar;
	private static String TAG = "CameraActivity";
	View addview;
	View addviewreal;

	LinearLayout ll_adtag;
	//센서
	public int SCREEN_ORIENT;
	
	String fileExternal_png = ".png";
	int orientationVertical = 0;
	int camera_orientation = 0;
	int preWidth = 0;
	int preHeight = 0;
	
	String SELECTED_KRCODE = "";
	String SELECTED_KRNAME = "";
	String SELECTED_KRSBBEONHO = "";
	String SELECTED_BSCODE = "";
	String SELECTED_JISACODE = "";
	String SELECTED_CONTENT = "";//내용 구성하기
	String SELECTED_BHGUBUN = "";
	String SELECTED_KGJJGUBUN = "";
	String SELECTED_KGJJBEONHO = "";
	String SELECTED_BIGO = "";
	String SELECTED_BJAE1 = "";
	String SELECTED_BJAE2 = "";
	String SELECTED_BJAE3 = "";
	

	String SELECTED_BJAE1_CODE = "";
	String SELECTED_JHBEONHO = "";
	String SELECTED_KDKGGUBUN = "";
	String SELECTED_KRBCBHGUBUN = "";
	String SELECTED_KRBCIRBEONHO = "";
	String SELECTED_KRBCSBBEONHO = "";
	String SELECTED_SWBEONHO = "";
	//String SELECTED_JGUBUN = "";
	String SELECTED_TAGYN = "";
	String SELECTED_SISUL = "";
	
	// 스마트정비관리 태그
	String SELECTED_JBCODE = "";
	String SELECTED_JBMYEONG = "";
	String SELECTED_DRNO = "";
	String SELECTED_DOGONG = "";
	String SELECTED_JEONGBIGUBUN = "";
	String SELECTED_SULIITEM = "";
	String SELECTED_SULIITEMCODE = "";
	String SELECTED_DETAILITEM = "";
	String SELECTED_BEFORE_AFTER = "";
	String SELECTED_BUSEOCODE = "";
	String SELECTED_CHECK  = "";
	//String SELECTED_DRNO = "";
	//String SELECTED_BEFORE_AFTER = "";
	
	
	String nsname = "";
	String currentIjung = "";
	String SMARTONESHOT_SAVEFILENAME = "";
	
	CheckBox chk_tagOnOff;
	
	String AndroidVersion = "";
	boolean AndroidVersionSetMethod= true;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate()~!");
		int orientation = getWindowManager().getDefaultDisplay().getOrientation();
		/*if(orientation == orientationVertical){
			setContentView(R.layout.camera2);
			camera_orientation = 0;
		}else{*/
			setContentView(R.layout.camera2_land);
			camera_orientation = 1;
//		}
		
		AndroidVersion = Build.VERSION.RELEASE;
		if(null != AndroidVersion){
			if(AndroidVersion.length() > 0){
				Log.d("","Android Version Check = "+AndroidVersion.substring(0,3));
				AndroidVersion = AndroidVersion.substring(0,3);
				if(Double.parseDouble(AndroidVersion) <= 4.0){
					AndroidVersionSetMethod = false;	
				}else{
					AndroidVersionSetMethod = true;
				}
			}
		}
		
		Intent acceptIntent = getIntent();
		SELECTED_KRCODE = acceptIntent.getStringExtra("SELECTED_KRCODE");
		SELECTED_KRNAME = acceptIntent.getStringExtra("SELECTED_KRNAME");
		SELECTED_KRSBBEONHO = acceptIntent.getStringExtra("SELECTED_KRSBBEONHO");
		SELECTED_BSCODE = acceptIntent.getStringExtra("SELECTED_BSCODE");
		SELECTED_JISACODE = acceptIntent.getStringExtra("SELECTED_JISACODE");
		SELECTED_CONTENT = acceptIntent.getStringExtra("SELECTED_CONTENT");
		SELECTED_BHGUBUN = acceptIntent.getStringExtra("SELECTED_BHGUBUN");
		SELECTED_KGJJGUBUN = acceptIntent.getStringExtra("SELECTED_KGJJGUBUN");
		SELECTED_KGJJBEONHO = acceptIntent.getStringExtra("SELECTED_KGJJBEONHO");
		SELECTED_BIGO = acceptIntent.getStringExtra("SELECTED_BIGO");
		SELECTED_BJAE1 = acceptIntent.getStringExtra("SELECTED_BJAE1");
		SELECTED_BJAE2 = acceptIntent.getStringExtra("SELECTED_BJAE2");
		SELECTED_BJAE3 = acceptIntent.getStringExtra("SELECTED_BJAE3");
		
		SELECTED_BJAE1_CODE = acceptIntent.getStringExtra("SELECTED_BJAE1_CODE");
		SELECTED_JHBEONHO = acceptIntent.getStringExtra("SELECTED_JHBEONHO");
		SELECTED_KDKGGUBUN = acceptIntent.getStringExtra("SELECTED_KDKGGUBUN");
		SELECTED_KRBCBHGUBUN = acceptIntent.getStringExtra("SELECTED_KRBCBHGUBUN");
		SELECTED_KRBCIRBEONHO = acceptIntent.getStringExtra("SELECTED_KRBCIRBEONHO");
		SELECTED_KRBCSBBEONHO = acceptIntent.getStringExtra("SELECTED_KRBCSBBEONHO");
		SELECTED_SWBEONHO = acceptIntent.getStringExtra("SELECTED_SWBEONHO");
		//SELECTED_JGUBUN = acceptIntent.getStringExtra("SELECTED_JGUBUN");
		SELECTED_SISUL = acceptIntent.getStringExtra("SELECTED_SISUL");
		
		//스마트정비관리 태그
		SELECTED_JBCODE = acceptIntent.getStringExtra("SELECTED_JBCODE");
		SELECTED_JBMYEONG = acceptIntent.getStringExtra("SELECTED_JBMYEONG");
		SELECTED_DRNO = acceptIntent.getStringExtra("SELECTED_DRNO");
		SELECTED_DOGONG = acceptIntent.getStringExtra("SELECTED_DOGONG");
		SELECTED_JEONGBIGUBUN = acceptIntent.getStringExtra("SELECTED_JEONGBIGUBUN");
		SELECTED_SULIITEM = acceptIntent.getStringExtra("SELECTED_SULIITEM");
		SELECTED_SULIITEMCODE = acceptIntent.getStringExtra("SELECTED_SULIITEMCODE");
		SELECTED_DETAILITEM = acceptIntent.getStringExtra("SELECTED_DETAILITEM");
		SELECTED_BEFORE_AFTER = acceptIntent.getStringExtra("SELECTED_BEFORE_AFTER");
		SELECTED_BUSEOCODE = acceptIntent.getStringExtra("SELECTED_BUSEOCODE");
		SELECTED_CHECK = acceptIntent.getStringExtra("SELECTED_CHECK");
		Log.d(TAG, "SELECTED_JBCODE : "+SELECTED_JBCODE);
		Log.d(TAG, "SELECTED_JBMYEONG : "+SELECTED_JBMYEONG);
		Log.d(TAG, "SELECTED_DRNO : "+SELECTED_DRNO);
		Log.d(TAG, "SELECTED_DOGONG : "+SELECTED_DOGONG);
		Log.d(TAG, "SELECTED_JEONGBIGUBUN : "+SELECTED_JEONGBIGUBUN);
		Log.d(TAG, "SELECTED_SULIITEMCODE : "+SELECTED_SULIITEMCODE);
		Log.d(TAG, "SELECTED_DETAILITEM : "+SELECTED_DETAILITEM);
		Log.d(TAG, "SELECTED_BEFORE_AFTER : "+SELECTED_BEFORE_AFTER);
		Log.d(TAG, "SELECTED_BUSEOCODE : "+SELECTED_BUSEOCODE);
		Log.d(TAG, "SELECTED_CHECK : "+SELECTED_CHECK);
		
		nsname = acceptIntent.getStringExtra("nsname");
		currentIjung = acceptIntent.getStringExtra("currentIjung");
		
		ll_adtag = (LinearLayout) findViewById(R.id.ll_adtag);
		Log.d("BLOCK", "BLOCK = " + acceptIntent.getBooleanExtra("BLOCK", false));
		if(false == acceptIntent.getBooleanExtra("TAGYN", false)){
			ll_adtag.setVisibility(View.GONE);
			SELECTED_TAGYN = "N";
		}else{
			ll_adtag.setVisibility(View.VISIBLE);
			SELECTED_TAGYN = "Y";
		}
		
		
		//SMARTONESHOT_SAVEFILENAME = SELECTED_KRCODE+"_"+SELECTED_KRSBBEONHO+"_"+SELECTED_BHGUBUN+"_"+Common.getCalendarDateTime()+fileExternal_png;
		SMARTONESHOT_SAVEFILENAME = SELECTED_JBCODE+"_"+Common.getCalendarDateTime()+fileExternal_png;
		
		customDrawviewDraw();

		Resources resource = Resources.getSystem();
		Configuration config = resource.getConfiguration();
		SCREEN_ORIENT = config.orientation;

		onConfigurationChanged(config);

		mPreview = (Preview2) findViewById(R.id.camera_preview);
		Log.d("",TAG+" onCreate 2");
		
		
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
		Log.d("", TAG + " onCreate 5");

		mImageData = new byte[6][];
		Log.d("",TAG+" onCreate 6");
		ibtnRunShutter = (ImageButton) findViewById(R.id.ibtnRunShutter);
		ibtnRunShutter.setOnClickListener(this);
		ibtnSave = (ImageButton) findViewById(R.id.ibtnSave);
		ibtnSave.setOnClickListener(this);
		ibtnCancel = (ImageButton) findViewById(R.id.ibtnCancel);
		ibtnCancel.setOnClickListener(this);
		Log.d("", TAG + " onCreate 7");
		ibtnSave.setVisibility(ImageButton.GONE);
		ibtnCancel.setVisibility(ImageButton.GONE);
		ll_pic = (LinearLayout)findViewById(R.id.ll_pic);
		ll_save = (LinearLayout)findViewById(R.id.ll_save);
		ll_save.setVisibility(View.GONE);
		
		flashOnOff = (ImageView)findViewById(R.id.flashOnOff);
		flashOnOff.setOnClickListener(this);

		Log.d("", TAG + " onCreate 8");
		setSeekBar();
		setSensor();

		flashOnOff = (ImageView) findViewById(R.id.flashOnOff);
		flashOnOff.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
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
			}
		});
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
	
	public void customDrawviewDraw(){
		Log.d(TAG, "customDrawviewDraw()~!");
		//String[] titleList = {"도공번호","장비명","수리항목","일자"};
		String[] titleList = {"장비명","도공번호","수리내용","정비일시"};
		//String[] contentList = {SELECTED_KRNAME, SELECTED_BJAE1 +" "+SELECTED_BJAE2 ,SELECTED_BJAE3+" "+SELECTED_CONTENT, Common.getCalendarDateYMD()};
		//String[] contentList = {Common.nullCheck(SELECTED_KRNAME), Common.nullCheck(SELECTED_BJAE1+" ("+SELECTED_DRNO+") "), Common.nullCheck(SELECTED_BJAE2+"/"+SELECTED_BJAE3+" ("+SELECTED_BEFORE_AFTER+") "+SELECTED_CONTENT), Common.getCalendarDateYMD()};
		//String[] contentList = {Common.nullCheck(SELECTED_BJAE1+" ("+SELECTED_DRNO+") "), Common.nullCheck(SELECTED_KRNAME),  Common.nullCheck(SELECTED_BJAE2+"/"+SELECTED_BJAE3+" ("+SELECTED_BEFORE_AFTER+") "+SELECTED_CONTENT), Common.getCalendarDateYMDHMS()};
		String[] contentList = {Common.nullCheck(SELECTED_JBMYEONG+" ("+SELECTED_DRNO+") "), Common.nullCheck(SELECTED_DOGONG),  Common.nullCheck(SELECTED_SULIITEM+"/"+SELECTED_DETAILITEM+" ("+SELECTED_BEFORE_AFTER+") "), Common.getCalendarDateYMDHMS().substring(0, 16)};
		
		
		int textLength = 0;
		int curLength = 0;
		Log.d("",TAG+"customDrawviewDraw Acceptlist length = " + contentList.length);
		for (int i = 0; i < contentList.length; i++) {
			Log.d("",TAG+"customDrawviewDraw Acceptlist item = " + contentList[i]);
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
/*		int titleWidth = 120;
		if("SM-N910K".equals(Build.MODEL)){
			titleWidth = 150;
		}
*/		int titleWidth = 160;
		

		
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
			LinearLayout innerLayout = new LinearLayout(CameraActivity.this);
			innerLayout.setLayoutParams(innerLayoutParam);
			innerLayout.setOrientation(LinearLayout.HORIZONTAL);
			if(AndroidVersionSetMethod){
				innerLayout.setBackground(getResources().getDrawable(R.drawable.border_style_ll));
			}
			innerLayout.setGravity(Gravity.CENTER_VERTICAL);
			//제목
			title = new TextView(CameraActivity.this);
			title.setLayoutParams(txtTitleParam);
			title.setText(titleList[i]);
			title.setTextColor(Color.BLACK);
			title.setGravity(Gravity.CENTER);
			title.setTextSize(textSize);
			//title.getPaint().setShadowLayer(8, 0, 0, Color.parseColor("#ffffff"));//보드형태 테두리 없음


			innerLayout.addView(title);


			//내용
			content = new TextView(CameraActivity.this);
			content.setLayoutParams(txtContentParam);
			content.setText(contentList[i]);
			if(AndroidVersionSetMethod){
				content.setBackground(getResources().getDrawable(R.drawable.border_stylecontent));
			}

			content.setTextColor(Color.BLACK);
			content.setGravity(Gravity.CENTER_VERTICAL);
			content.setTextSize(textSize);
			content.setPadding(10, 0, 0, 0);
			//content.getPaint().setShadowLayer(8, 0, 0, Color.parseColor("#ffffff"));//보드형태 테두리 없음
			innerLayout.addView(content);

			ll_adtag.setBackgroundColor(Color.WHITE);
			ll_adtag.addView(innerLayout);
		}

		addview = ll_adtag;
	}
	


	int i =0;
	public void drawView(int textSize, int width, int height, LinearLayout tagView, int rid, View acceptView){
		Log.d(TAG, "drawView()~!");
		addviewreal = tagView;
		i++;
	}

	int rotation = 0;
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.d(TAG, "onConfigurationChanged()~!");
		super.onConfigurationChanged(newConfig);
		rotation = this.getWindowManager().getDefaultDisplay().getRotation();
		//rotation 강제 셋팅 (Galaxy Tab rotation = 0, 다른 스마트폰 rotation = 1)
		rotation = 1;
		
		Log.d("", "onConfigurationChanged rotation = " + rotation);
		if (newConfig.orientation == newConfig.ORIENTATION_PORTRAIT) {
			Log.d("","onConfigurationChanged = portrait");
		}else {
			Log.d("","onConfigurationChanged = landscape");
		}
	}


	/** 
	 *
	 */
	private void setSeekBar(){
		Log.d(TAG, "setSeekBar()~!");
//		Camera.Parameters params = camera.getParameters();
		seekbar = (VerticalSeekBar)findViewById(R.id.SeekBar01);
//		if (SCREEN_ORIENT == Configuration.ORIENTATION_LANDSCAPE) {
//			View seekView = seekbar;
//		}

		seekbar.setMax(mPreview.maxZoom);
		seekbar.setOnSeekBarChangeListener(new VerticalSeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(VerticalSeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(VerticalSeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(VerticalSeekBar seekBar, int progress, boolean fromUser) {
				Log.d("[SeekBar]","onProgressChanged :zoomLevel= " + progress);
				if( mPreview.mCamera != null){
					Camera.Parameters parameters = mPreview.mCamera.getParameters();
					parameters.set("zoom", progress);

//					List<Size> sizes = parameters.getSupportedPictureSizes();
//					Log.d("", TAG + " onCreate 4");
//					for (int i = 0; i < sizes.size(); i++) {
//						Log.d("",TAG+" onCreate 4 for " + i);
//						int width = sizes.get(i).width;
//						int height = sizes.get(i).height;
//						String prefString = width+"x"+height;
//						if(prefString.equals(Common.getPrefString(CameraActivity.this, com.ex.smartoneshot.common.Configuration.CAMERA_PREVIEW))){
//							Log.d("","CameraActivity2 selected size = " + width +" : "+height);
//							parameters.setPictureSize(width, height);
//						}
//						Log.d("","CameraActivity2 width height result = [" + width +"x"+height+"]["+Common.getPrefString(CameraActivity.this, com.ex.smartoneshot.common.Configuration.CAMERA_PREVIEW) +"]");
//					}

//					int preWidth = 0;
//					int preHeight = 0;
//					List<Size> sizes = parameters.getSupportedPictureSizes();
//					for (int i = 0; i < sizes.size(); i++) {
//						int width = sizes.get(i).width;
//						int height = sizes.get(i).height;
//						Log.d("","CameraActivity2 width height = " + width +" : "+height);
//						if(preWidth == 0){
//							preWidth = width;
//							preHeight = height;
//						}else if(preWidth >= width){
//
//							parameters.setPictureSize(width, height);
//						}
//					}

					mPreview.mCamera.setParameters(parameters);
				} else {
					Log.v("Preview", "Camera is null ");
				}
			}
		});

//		SeekBarListener seekListener = new SeekBarListener(seekbar);
//		seekbar.setMax(max);
	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.flashOnOff:
				if(Camera.Parameters.FLASH_MODE_TORCH.equals(Common.getPrefString(CameraActivity.this, com.ex.smartmm.common.Configuration.Flash_key))){
					mPreview.FlashOff();
					Common.setPrefString(CameraActivity.this, com.ex.smartmm.common.Configuration.Flash_key, Camera.Parameters.FLASH_MODE_OFF);
					Log.d("","Flash off");
				}else{
					Common.setPrefString(CameraActivity.this, com.ex.smartmm.common.Configuration.Flash_key, Camera.Parameters.FLASH_MODE_TORCH);
					mPreview.FlashOn();
					Log.d("", "Flash on");
				}
//				mPreview.FlashOff();
//				Common.setPrefString(CameraActivity.this, com.ex.smartoneshot.common.Configuration.Flash_key, Camera.Parameters.FLASH_MODE_OFF);
				break;
			case R.id.ibtnRunShutter:
				Log.d(TAG, "onClick(ibtnRunShutter)~!");
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

				break;

			case R.id.ibtnSave:
				Log.d(TAG, "onClick(ibtnSave)~!");
				new SaveImageBackground().execute("");
				break;

			case R.id.ibtnCancel:
				ll_pic.setVisibility(View.VISIBLE);
				ibtnRunShutter.setVisibility(ImageButton.VISIBLE);
				ll_save.setVisibility(View.GONE);
				ibtnSave.setVisibility(ImageButton.GONE);
				ibtnCancel.setVisibility(ImageButton.GONE);

				canShutter = true;

				onResume();
				break;

		}
	}
	ProgressDialog progressDialog;
	public class SaveImageBackground extends AsyncTask<String, String, String>{
		public SaveImageBackground(){
			Log.d(TAG, "******************** SaveImageBackground CLASS ********************");
			if(progressDialog != null){
				progressDialog.dismiss();
				progressDialog = null;
			}
			progressDialog = ProgressDialog.show(CameraActivity.this, "", "저장중...", true);

		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected String doInBackground(String... params) {
			Log.d("", "SaveImageBackground CLASS - doInBackground()~!");
//			saveViewToImage(addviewreal);
			SaveImage(SMARTONESHOT_SAVEFILENAME);

			/*db.insertBms_Krgb_JGINFO(
					SELECTED_KRCODE, 
					SELECTED_KRSBBEONHO, 
					SELECTED_DOGONG,  //도공번호
					SELECTED_BHGUBUN, 
					SELECTED_KGJJGUBUN, 
					SELECTED_KGJJBEONHO,
					SELECTED_BIGO,
					SELECTED_CONTENT,
					com.ex.smartmm.common.Configuration.dirRoot_kr + SMARTONESHOT_SAVEFILENAME,
					SELECTED_JBMYEONG+"("+SELECTED_DRNO+")",   // 장비명
					SELECTED_BJAE2,
					SELECTED_SULIITEM+"/"+SELECTED_DETAILITEM+"("+SELECTED_BEFORE_AFTER+")"+SELECTED_CHECK,  // 내용
					SELECTED_BJAE1_CODE,
					SELECTED_JHBEONHO,
					SELECTED_KDKGGUBUN,
					SELECTED_KRBCBHGUBUN,
					SELECTED_KRBCIRBEONHO,
					SELECTED_KRBCSBBEONHO,
					SELECTED_SWBEONHO,
					SELECTED_BSCODE,
					SELECTED_BUSEOCODE,
					SELECTED_JGUBUN,
					SELECTED_TAGYN,
					SELECTED_SISUL,
					getUserNm(CameraActivity_Kyoryang.this)
					);*/
			
			//db.insertSmart_DATAINFO(
			checkDb.insertSmart_DATAINFO(
					SELECTED_JEONGBIGUBUN, 
					"["+SELECTED_SULIITEMCODE+"]"+SELECTED_SULIITEM, 
					SELECTED_DETAILITEM, 
					SELECTED_SULIITEM+"/"+SELECTED_DETAILITEM+"("+SELECTED_BEFORE_AFTER+")", 
					com.ex.smartmm.common.Configuration.dirRoot_kr + SMARTONESHOT_SAVEFILENAME, 
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
					getUserNm(CameraActivity.this),
					SELECTED_CHECK
					);
			
			//refreshSD();
			refreshSD(SMARTONESHOT_SAVEFILENAME);

			return "";
		}

		@Override
		protected void onPostExecute(String s) {
			Log.d(TAG, "onPostExecute()~!");
			Log.d("","ddddddddddddddddddd 21");
			if(progressDialog != null){
				Log.d("","ddddddddddddddddddd 22");
				progressDialog.dismiss();
				progressDialog = null;
			}
//			super.onPostExecute(s);
			Log.d("", "ddddddddddddddddddd 23");

			Intent i = getIntent();
			i.putExtra("imgName", com.ex.smartmm.common.Configuration.dirRoot_kr+SMARTONESHOT_SAVEFILENAME);
			i.putExtra("dbId", SMARTONESHOT_SAVEFILENAME);
			i.putExtra("orgName", SMARTONESHOT_SAVEFILENAME);
			setResult(RESULT_OK, i);
			Log.d(TAG, "onPostExecute() - RESULT_OK : "+RESULT_OK);
			Log.d(TAG, "onPostExecute() - getExtras : "+i.getExtras());
			Toast.makeText(CameraActivity.this, "사진이 저장 되었습니다.", Toast.LENGTH_SHORT).show();
			CameraActivity.this.finish();
		}
	}

	private void saveViewToImage( View view ) {
		Log.d(TAG, "saveViewToImage()~!");
		Log.d("", TAG+ "saveViewToImage width,height = " +view.getWidth()+":"+view.getHeight());
		Bitmap  b = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
		if(b!=null){
			try {
				File f = new File(com.ex.smartmm.common.Configuration.dirRoot_kr);//디렉토리경로
				if(!f.exists()){
//					f.mkdir();
					f.mkdirs();
				}
				File f2 = new File(com.ex.smartmm.common.Configuration.dirRoot_kr+fileExternal_png);//디렉토리경로+파일명

				Canvas c = new Canvas( b );
				view.draw( c );
				FileOutputStream fos = new FileOutputStream(f2);

				if ( fos != null )
				{
					b.compress(CompressFormat.PNG, 100, fos );
					fos.close();
				}
				//setWallpaper( b );

			} catch( Exception e ){
				Log.e("testSaveView", "Exception: " + e.toString() );
			}
		}
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		return true;
	}

	private int getPreFileName() {
		Log.d(TAG, "getPreFileName()~!");
		if (sPrefs == null) {
			sPrefs = getSharedPreferences(KEY_POPUP_ENV, Context.MODE_PRIVATE);
		}
		mFileNameYear = sPrefs.getInt(SAVE_FILE_YEAR, 0);
		mFileNameMonth = sPrefs.getInt(SAVE_FILE_MONTH, 0);
		mFileNameDay = sPrefs.getInt(SAVE_FILE_DATE, 0);
		mFileNameCount = sPrefs.getInt(SAVE_FILE_COUNT, 0);
		return mFileNameCount;
	}

	private String getRealFileName() {
		MakeFileName();
		return mFilename;
	}

	private void MakeFileName() {
		Log.d(TAG, "MakeFileName()~!");
		DecimalFormat decimalFormat = new DecimalFormat("00");// decimalformat
		DecimalFormat NumFormat = new DecimalFormat("0000");// 4
		Calendar rightNow = Calendar.getInstance();//
		int year = rightNow.get(Calendar.YEAR);
		int month = rightNow.get(Calendar.MONTH) + 1;
		int date = rightNow.get(Calendar.DATE);//
		int hour = rightNow.get(Calendar.HOUR_OF_DAY);
		int minute = rightNow.get(Calendar.MINUTE);
		int second = rightNow.get(Calendar.SECOND);
		int rnd = (int) (Math.random() * 99);

		int num = getPreFileName();
		String result = Common.FILE_TAG+decimalFormat.format(year)
				+ decimalFormat.format(month) + decimalFormat.format(date)
				+ decimalFormat.format(hour) + decimalFormat.format(minute)
				+ decimalFormat.format(second) + decimalFormat.format(rnd);
		String FormatNum = NumFormat.format(num);
		mFilename = result+"";

		File[] files = new File(com.ex.smartmm.common.Configuration.dirRoot_kr).listFiles();
		if(null == files){
			Log.d("","Files null");
		}else{
			if (files.length == 0) {
				num++;
			} else if (files.length > 0) {

				if (CompareDate(year, month, date) == true) {
					num++;
				} else if (CompareDate(year, month, date) == false) {

					num = 0;
				}
			}
		}

		SaveFileName(year, month, date, num);
	}

	private boolean CompareDate(int year, int month, int date) {
		boolean ret = false;

		if (year == getFileNameYear()) {
			if (month == getFileNameMonth()) {
				if (date == getFileNameDay()) {
					ret = true;
				}
			}
		}

		return ret;
	}

	private int getFileNameYear() {
		return mFileNameYear;
	}

	private int getFileNameMonth() {
		return mFileNameMonth;
	}

	private int getFileNameDay() {
		return mFileNameDay;
	}

	private void SaveFileName(int year, int month, int date, int num) {

		SharedPreferences.Editor editor = sPrefs.edit();
		editor.putInt(SAVE_FILE_YEAR, year);
		editor.putInt(SAVE_FILE_MONTH, month);
		editor.putInt(SAVE_FILE_DATE, date);
		editor.putInt(SAVE_FILE_COUNT, num);
		editor.commit();

	}

	public int SaveImage(String sFilename) {
		Log.d(TAG, "SaveImage()~!");
		Log.d(TAG, "SaveImage() - sFilename : "+sFilename);
		int ret = 0;
		try {
			Log.d("","SaveImage filepath  === "+ com.ex.smartmm.common.Configuration.dirRoot_kr);
			Log.d("","SaveImage filename=== "+  sFilename);
			Log.d("","SaveImage filepath+filename === "+ com.ex.smartmm.common.Configuration.dirRoot_kr + sFilename);

			// data[] 로 넘어온 데이터를 bitmap으로 변환
			Bitmap bmp = BitmapFactory.decodeByteArray(mImageData[0], 0, mImageData[0].length);

			Bitmap rotateBitmap = null;
			Log.d("","SaveImage filepath  === "+ rotation);
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

//			MediaScanner scanner = MediaScanner.newInstance(CameraActivity.this);
//			scanner.mediaScanning(Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera");
//			File file = new File(Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera");
//			CameraActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)) );
//			// MediaStore 미디어 삽입
//			ContentValues newValues = new ContentValues(2);
//			newValues.put(MediaColumns.MIME_TYPE, "image/jpeg");
//			newValues.put(MediaColumns.DATA, Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera"+"/"+sFilename+".jpg");
//
//			ContentResolver contentResolver = this.getContentResolver();
//			Uri newUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, newValues);
//			this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
			//refreshSD();
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
		/*sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
				Uri.parse("file://"+ Environment.getExternalStorageDirectory() )));*/
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
				Uri.parse("file://"+ com.ex.smartmm.common.Configuration.dirRoot_kr+ sFilename)));
		
	}



	/**이미지 붙이기
	 * @param first
	 * @param second
	 * @param isVerticalMode
	 * @param savePath
	 */
	private void combineImage(Bitmap first, Bitmap second, boolean isVerticalMode, String savePath){
		Options option = new Options();
		option.inDither = true;
		option.inPurgeable = true;
		Bitmap bitmap = null;
		if(isVerticalMode)
			bitmap = Bitmap.createScaledBitmap(first, first.getWidth(), first.getHeight()+second.getHeight(), true);
		else
			bitmap = Bitmap.createScaledBitmap(first, first.getWidth()+second.getWidth(), first.getHeight(), true);

		Paint p = new Paint();
		p.setDither(true);
		p.setFlags(Paint.ANTI_ALIAS_FLAG);
		Canvas mCanvas = new Canvas(bitmap);
		mCanvas.drawBitmap(first, 0, 0, p);
		if(isVerticalMode)
			mCanvas.drawBitmap(second, 0, first.getHeight(), p);
		else
			mCanvas.drawBitmap(second, first.getWidth(), 0, p);

		first.recycle();
		second.recycle();
		try{
			FileOutputStream fos = new FileOutputStream(savePath);
			bitmap.compress(CompressFormat.JPEG, 100, fos);

			fos.close();
			bitmap.recycle();
		}catch(Exception e){
			e.printStackTrace();
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

			resultFileName = SMARTONESHOT_SAVEFILENAME;
			//openDialog(f);
			Log.d(TAG, "&^&^&^444 - resultFileName :"+resultFileName);
			//ibtnRunShutter.setAnimation(AnimationUtils.loadAnimation(CameraActivity2.this, R.anim.push_up_out));
			ibtnSave.setAnimation(AnimationUtils.loadAnimation(CameraActivity.this, R.anim.fade));
			ibtnCancel.setAnimation(AnimationUtils.loadAnimation(CameraActivity.this, R.anim.fade));
			Log.d(TAG, "&^&^&^555");
//			ll_pic.setVisibility(View.GONE);
//			ibtnRunShutter.setVisibility(ImageButton.GONE);
//			ll_save.setVisibility(View.VISIBLE);
//			ibtnSave.setVisibility(ImageButton.VISIBLE);
//			ibtnCancel.setVisibility(ImageButton.VISIBLE);
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

//				ToneGenerator tg = new ToneGenerator(
//						AudioManager.STREAM_SYSTEM, 100);
//				if (tg != null)
//					tg.startTone(ToneGenerator.TONE_PROP_BEEP);
//				gFocussed = true;
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
//				ToneGenerator tg = new ToneGenerator(
//						AudioManager.STREAM_SYSTEM, 100);
//				if (tg != null)
//					tg.startTone(ToneGenerator.TONE_PROP_BEEP);

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
		Log.e("onDestroy", "CameraActivity onDestroy...");
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


	private SensorManager mSensorManager;
	private Sensor accelerometer;
	private Sensor magnetometer;
	private float[] mGravity = null;
	private float[] mGeomagnetic = null;
	private float RR[] = new float[9];
	private float II[] = new float[9];

	public void setSensor(){
		Log.d(TAG, "setSensor()~!");
		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

//		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		Log.d(TAG,TAG+" SensorManager onSensorChanged" );


	}


	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}





}