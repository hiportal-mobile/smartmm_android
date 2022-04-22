package com.ex.smartmm;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.ex.smartmm.common.CameraDialogActivity;
import com.ex.smartmm.common.Common;
import com.ex.smartmm.common.Configuration;
import com.ex.smartmm.common.ImageJgInfo;
import com.ex.smartmm.net.CustomMultiPartEntity;
import com.ex.smartmm.net.Parameters;
import com.ex.smartmm.net.CustomMultiPartEntity.ProgressListener;
import com.ex.smartmm.vo.ItemGB;
import com.ex.smartmm.vo.JbInfo;
import com.ex.smartmm.vo.JeongbiGB;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;


public class MainActivity extends BaseActivity implements OnClickListener{

	public final String TAG = "smartMM MainActivity";
	
	public static Activity contextActivity;
	//파일전송 핸들러
	Handler fileHandler;
	Common common = new Common(MainActivity.this);
	String AndroidVersion = "";
	boolean AndroidVersionSetMethod= true;

	//keyword 에 의한 교량 목록
	List<JbInfo> jbList = new ArrayList<JbInfo>();
	List<JeongbiGB> jeongbiGB_List;
	List<ItemGB> itemGB_List;
	List<ItemGB> detailGB_List;
	
	//정비구분, 수리항목, 세부항목 리스트
	List<String> jeongbiGBList = new ArrayList<String>();
	List<String> itemGBList = new ArrayList<String>();
	List<String> detailGBList = new ArrayList<String>();
	
	//스마트정비관리
/** 운영 **/
	String swbeonho = Common.getPrefString(MainActivity.this, Configuration.SHARED_USERID);
	String bonbucode = Common.getPrefString(MainActivity.this, Configuration.SHARED_BONBUCODE);
	String jisacode = Common.getPrefString(MainActivity.this, Configuration.SHARED_JISACODE);

	
/** 개발 **/	
	/*String swbeonho = "20609710";
	String bonbucode = "N01795";
	String jisacode = "N00218";*/
	
	String SELECTED_BSCODE = "";
	String SELECTED_BUSEOCODE = jisacode;
	String SELECTED_SWBEONHO = "";	//사원번호
	String SELECTED_CODE = "";
	String SELECTED_DOGONG = "";
	String SELECTED_JBMYEONG = "";
	String SELECTED_JEONGBIGUBUN = "";
	String SELECTED_JEONGBIGUBUNCODE = "";
	String SELECTED_SULIITEM = "";
	String SELECTED_SULIITEMCODE = "";
	String SELECTED_DETAILITEM = "";
	String SELECTED_JBCODE = "";
	String SELECTED_DRNO = "";
	String SELECTED_CHAJONG = "";
	String SELECTED_CODEGUBUN = "";
	String SELECTED_BEFORE_AFTER = ""; // 정비전후 저장
	//String SELECTED_JGUBUN = "1";//1.점검, 2.보수
	String SELECTED_TAGYN = "";//태그촬영 여부 Y, N
	
	LinearLayout ll_jblist, ll_jb;
	ListView lv_jb;
	ImageView btn_goCamera, btn_goAlbum, btn_goServer, btn_goCheckList, spin_detailB;
	ImageView btn_X, btn_txt_jbname, btn_ed_jbname;
	Spinner spin_division, spin_jeoungbi, spin_suli, spin_detail;
	TextView txt_deptName, txt_changeDept, txt_jbname, txt_jangbi, txt_dogong, txt_chajong, txt_drno ;
	EditText ed_jbname;
	EditText ed_write;//세부항목 직접입력 
	
	boolean TAGYN = true;//이미지 태그 사용 여부 저장.
	
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		Log.d(TAG,"onCreate()~!!");
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Log.d(TAG, "== bonbucode : "+bonbucode+", jisacode : "+jisacode);

		contextActivity = MainActivity.this;
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
		
		SELECTED_SWBEONHO = swbeonho;
		Log.d(TAG,"MAIN =============== 3");
		ll_jblist = (LinearLayout) findViewById(R.id.ll_jblist);	//장비리스트
		ll_jblist.setOnClickListener(this);
		txt_jbname = (TextView) findViewById(R.id.txt_jbname);
		txt_jbname.setOnClickListener(this);

		txt_deptName = (TextView) findViewById(R.id.txt_deptName);
		txt_deptName.setText(common.getPrefString(MainActivity.this, Configuration.SHARED_BSSEL_BSNAME)+" "+common.getPrefString(MainActivity.this, Configuration.SHARED_BSSEL_JISANAME));


		txt_changeDept = (TextView) findViewById(R.id.txt_changeDept);
		txt_changeDept.setOnClickListener(this);
		if(common.getPrefString(MainActivity.this, Configuration.SHARED_DEPTNM5).contains("기계화")){
			txt_changeDept.setVisibility(View.VISIBLE);
		}else{
			txt_changeDept.setVisibility(View.GONE);
		}

		ed_jbname = (EditText) findViewById(R.id.ed_jbname);
		ed_write = (EditText) findViewById(R.id.ed_write);
		lv_jb = (ListView) findViewById(R.id.lv_jb);
		ll_jb = (LinearLayout) findViewById(R.id.ll_jb);
		
	    spin_jeoungbi = (Spinner) findViewById(R.id.spin_jeoungbi);	// 정비구분
	    spin_suli = (Spinner) findViewById(R.id.spin_suli);	// 수리항목
	    spin_detail = (Spinner) findViewById(R.id.spin_detail);	// 세부항목
	    spin_detailB = (ImageView) findViewById(R.id.spin_detailB);
	    spin_detailB.setOnClickListener(this);
	     
	   	spin_jeoungbi.setPrompt("정비구분");
	   	spin_suli.setPrompt("수리항목");
	   	spin_detail.setPrompt("세부항목");
	   	
	   	btn_X = (ImageView) findViewById(R.id.btn_X);
	   	btn_X.setOnClickListener(this);
	   	btn_txt_jbname = (ImageView) findViewById(R.id.btn_txt_jbname);
	   	btn_txt_jbname.setOnClickListener(this);
	   	btn_ed_jbname = (ImageView) findViewById(R.id.btn_ed_jbname);
		btn_ed_jbname.setOnClickListener(this);
		
		txt_jangbi = (TextView) findViewById(R.id.txt_jangbi);
		txt_dogong = (TextView) findViewById(R.id.txt_dogong);
		txt_chajong = (TextView) findViewById(R.id.txt_chajong);
		txt_drno = (TextView) findViewById(R.id.txt_drno);
		
		btn_goCamera = (ImageView) findViewById(R.id.btn_goCamera);  // 촬영
	    btn_goCamera.setOnClickListener(this);
	    
	    btn_goAlbum = (ImageView) findViewById(R.id.btn_goAlbum);  // 불러오기
	    btn_goAlbum.setOnClickListener(this);
	    
	    btn_goServer = (ImageView) findViewById(R.id.btn_goServer);  // 서버전송
	    btn_goServer.setOnClickListener(this);
	    
	    btn_goCheckList = (ImageView) findViewById(R.id.btn_goCheckList);  // 작성조회
	    btn_goCheckList.setOnClickListener(this);
	    Log.d(TAG,"MAIN =============== 4");
	   	setSpinnerJeongBi();	
	   	setSpinnerJeongBiGB();
		setSpinnerItemGB();
		setSpinnerDetailGB("");
		
		//smartMM 폴더 생성
		File fileDir = new File(Common.FILE_DIR);
		if (!fileDir.exists()){
			fileDir.mkdirs();
		}
		Log.d(TAG,"fileDir exist = "+Common.FILE_DIR+":"+ fileDir.exists());
				
		//처음 들어갔을때 장비리스트 보이게
		ll_jblist.setVisibility(View.VISIBLE);
		
		ed_jbname.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				Log.d(TAG,"addTextChangedListener() - onTextChanged : " +txt_jbname.getText().toString()+"||"+ed_jbname.getText().toString());
				
				if(!txt_jbname.getText().toString().equals(ed_jbname.getText().toString())){
					txt_jangbi.setText("");
					txt_drno.setText("");
					txt_chajong.setText("");
					txt_dogong.setText("");
					SELECTED_JBCODE = "";
				}
				Log.d(TAG,"txt_jbname : "+txt_jbname.getText().toString());
				Log.d(TAG,"ed_jbname : "+ed_jbname.getText().toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {
				if( null == s){
					Log.d("","addTextChangedListener() afterTextChanged Editable is null");
				}
				System.out.println("TEXTCHANGED TIME CHECK start  = " + System.currentTimeMillis());
				txt_jbname.setText(ed_jbname.getText().toString());
				
				getjbList(false);
				System.out.println("TEXTCHANGED TIME CHECK end  = " + System.currentTimeMillis());
			}
		});
	}

	
	@Override
	protected void onResume() {
		Log.d(TAG, "onResume()~!");
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		Log.d(TAG, "onPause()~!");
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//stopGPS();
		Log.d(TAG,"MainActivity onDestroy called!");
	}
	
	//장비 선택 박스
	public void setEdjbname(){
		Log.d(TAG,"setEdjbname()~!!");
	}

	
	public void getjbList(boolean locationGiban){

		bonbucode = Common.getPrefString(MainActivity.this, Configuration.SHARED_BSSEL_BSCODE);
		jisacode = Common.getPrefString(MainActivity.this, Configuration.SHARED_BSSEL_JISACODE);

		Log.d(TAG, "******************** getjbList~! ********************");
		System.out.println("TEXTCHANGED TIME CHECK 1 = " + System.currentTimeMillis());
		String searchKeyword = ed_jbname.getText().toString();
		Cursor cursor = null;
		System.out.println("TEXTCHANGED TIME CHECK 2 = " + System.currentTimeMillis());
		if("도공번호".equals(spin_division.getSelectedItem().toString())){
			System.out.println("TEXTCHANGED TIME CHECK 2-1 = " + System.currentTimeMillis());
			cursor = jangbiDb.select_jeongbi(txt_jbname.getText().toString(), "",bonbucode, jisacode);
		}else if("장비명".equals(spin_division.getSelectedItem().toString())){
			System.out.println("TEXTCHANGED TIME CHECK 2-2 = " + System.currentTimeMillis());
			cursor = jangbiDb.select_jeongbi("", txt_jbname.getText().toString(), bonbucode, jisacode);
		}else{
			Log.d(TAG,"getjbList - select_jeongbi() 도공/장비명/구분 : "+spin_division.getSelectedItem().toString());
			System.out.println("TEXTCHANGED TIME CHECK 2-3 = " + System.currentTimeMillis());
			cursor = jangbiDb.select_jeongbi(txt_jbname.getText().toString(), txt_jbname.getText().toString(), bonbucode, jisacode);
		}
		System.out.println("TEXTCHANGED TIME CHECK 3 = " + System.currentTimeMillis());
		System.out.println("TEXTCHANGED TIME CHECK 4 = " + System.currentTimeMillis());
		if(null != cursor){
			//Log.d("","jbList1 :   " + jbList.toString());
			Log.d("","jbList1 :   " + jbList.size());
			jbList.clear();
			JbInfo item;
			
			/*item = new JbInfo();
			item.setchajongmyeong("정비");
			item.setjangbicode("코드");
			item.setjangbimyeong("이름");
			item.setdogongcode("도공");
			item.setchajongcode("차종");
			item.setdeungrokno("등록");
			item.setgyugyeok("규격");
			item.setpibuseomyeong("부서");
			item.setbuseocode("부서코드");
			item.setbuseoname("부서이름");
			item.setcode("코드2");
			item.setcodegubun("구분");
			jbList.add(item);*/
			
			while (cursor.moveToNext()) {
				
				item = new JbInfo();
				
				item.setchajongmyeong(cursor.getString(0));
				item.setjangbicode(cursor.getString(1));
				item.setjangbimyeong(cursor.getString(2));
				item.setdogongcode(cursor.getString(3));
				item.setchajongcode(cursor.getString(4));
				item.setdeungrokno(cursor.getString(5));
				item.setgyugyeok(cursor.getString(6));
				item.setpibuseomyeong(cursor.getString(7));
				item.setbuseoname(cursor.getString(7));
				
				
				/*item.setchajongmyeong(cursor.getString(0));
				item.setjangbicode(cursor.getString(1));
				item.setjangbimyeong(cursor.getString(2));
				item.setdogongcode(cursor.getString(3));
				item.setchajongcode(cursor.getString(4));
				item.setdeungrokno(cursor.getString(5));
				item.setgyugyeok(cursor.getString(6));
				item.setpibuseomyeong(cursor.getString(7));
				item.setbuseocode(cursor.getString(8));
				item.setbuseoname(cursor.getString(7));
				item.setcode(cursor.getString(9));
				item.setcodegubun(cursor.getString(10));*/
				jbList.add(item);
				
				//Log.d("","getjbList jangbicode : "+cursor.getString(1));
				Log.d("","getjbList jangbimyeong: "+cursor.getString(2));
				//Log.d("","getjbList dogongcode : "+cursor.getString(3));
				//Log.d("","getjbList deungrokno : "+cursor.getString(5));
				
			}
			Log.d(TAG,"jbList2 size :   " + jbList.size());
			lv_jb.setAdapter(new jbListAdapter(MainActivity.this));
		}
		System.out.println("TEXTCHANGED TIME CHECK 5 = " + System.currentTimeMillis());
		lv_jb.setOnItemClickListener(new OnItemClickListener() {
			

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				ll_jblist.setVisibility(View.GONE);
				if("도공번호".equals(spin_division.getSelectedItem().toString())){
					txt_jbname.setText(jbList.get(position).getdogongcode());	
				}else if("장비명".equals(spin_division.getSelectedItem().toString())){
					txt_jbname.setText(jbList.get(position).getjangbimyeong());
				}else{
					txt_jbname.setText(jbList.get(position).getdogongcode());
				}
				SELECTED_CODE = jbList.get(position).getcode();
				SELECTED_JBCODE = jbList.get(position).getjangbicode();
				SELECTED_JBMYEONG = jbList.get(position).getjangbimyeong();
				SELECTED_DOGONG = jbList.get(position).getdogongcode();
				SELECTED_DRNO = jbList.get(position).getdeungrokno();
				SELECTED_CHAJONG = jbList.get(position).getchajongmyeong();
				SELECTED_CODEGUBUN = jbList.get(position).getcodegubun();
				
				Log.d("","getjbList SELECTED_JBCODE : "+SELECTED_JBCODE);
				Log.d("","getjbList SELECTED_JBMYEONG : "+SELECTED_JBMYEONG);
				Log.d("","getjbList SELECTED_DOGONG : "+SELECTED_DOGONG);
				Log.d("","getjbList SELECTED_DRNO : "+SELECTED_DRNO);
				Log.d("","getjbList SELECTED_CHAJONG : "+SELECTED_CHAJONG);
				
				Cursor cursor = jangbiDb.select_jeongbi_detail(bonbucode, jisacode, SELECTED_JBCODE);
				if(cursor != null){
					while (cursor.moveToNext()) {
						
						//String code = cursor.getString(0);//코드
						
						String chajongmyeong = cursor.getString(0);//차종명
						String jangbicode = cursor.getString(1);//장비코드
						String jangbimyeong = cursor.getString(2);//장비명
						String dogongcode = cursor.getString(3);//도공번호
						String chajongcode = cursor.getString(4);//차종코드
						String deungrokno = cursor.getString(5);//등록번호
						String gyugyeok = cursor.getString(6);//규격
						String pibuseomyeong = cursor.getString(7);//기관명
						String buseocode = cursor.getString(9);//부서코드
						String buseoname = cursor.getString(7);//부서명
						
						txt_jangbi.setText(jangbimyeong);
						txt_dogong.setText(dogongcode);
						txt_chajong.setText(chajongmyeong);
						txt_drno.setText(deungrokno);
						
						txt_jangbi.setTextSize(16);
						txt_dogong.setTextSize(16);
						txt_chajong.setTextSize(16);
						txt_drno.setTextSize(16);
						Log.d("","getjbList chajongmyeong : "+chajongmyeong);
						Log.d("","getjbList jangbicode : "+jangbicode);
						Log.d("","getjbList dogongcode : "+dogongcode);
						Log.d("","getjbList chajongcode : "+chajongcode);
						Log.d("","getjbList deungrokno : "+deungrokno);
						Log.d("","getjbList gyugyeok : "+gyugyeok);
						Log.d("","getjbList pibuseomyeong : "+pibuseomyeong);
						Log.d("","getjbList buseocode : "+buseocode);
						Log.d("","getjbList buseoname : "+buseoname);
						break;	
					}
				}
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(arg1.getWindowToken(), 0);
			}
		});
			System.out.println("TEXTCHANGED TIME CHECK 7 = " + System.currentTimeMillis());
	}
	
	
	private class jbListAdapter extends BaseAdapter{
		LayoutInflater inflater;
		
		public jbListAdapter(Context context) {
			super();
			inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		@Override
		public int getCount() {
			return jbList.size();
		}

		@Override
		public JbInfo getItem(int position) {
			return jbList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if(convertView == null){
				view = inflater.inflate(R.layout.listview_item_kr, null);
			}
			TextView item_jangbi = (TextView) view.findViewById(R.id.item_jangbi);  // 장비명
			TextView item_dogong = (TextView) view.findViewById(R.id.item_dogong);  // 도공번호
			TextView item_drno = (TextView) view.findViewById(R.id.item_drno);  	// 등록번호
			item_jangbi.setText(common.nullCheck(getItem(position).getjangbimyeong()));
			item_dogong.setText(common.nullCheck(getItem(position).getdogongcode()));
			item_drno.setText(getItem(position).getdeungrokno());
//			Log.d("","jbListAdapter getView - item_jangbi : "+position+":"+getItem(position).getjangbicode());
//			Log.d("","jbListAdapter getView - item_dogong : "+position+":"+getItem(position).getdogongcode());
//			Log.d("","jbListAdapter getView - item_drno : "+position+":"+getItem(position).getdeungrokno());
//
//			Log.d(TAG,"MainActivity jbListAdapter getView "+position+":"+getItem(position).getjangbicode());
		
			return view;
		}
	}
	
	
	/** 도공번호/장비명 선택 박스 **/
	@SuppressLint("NewApi")
	private void setSpinnerJeongBi(){
		Log.d(TAG, "******************** setSpinnerJeongBi()~! ********************");
		//Log.d("","setSpinnerJeongBi() - 지사코드 : "+jisacode);
		List<String> spinItems = new ArrayList<String>();
		Cursor cursor = jangbiDb.select_jeongbi("","", bonbucode, jisacode);
		spinItems.add("도공번호");
		spinItems.add("장비명");
		Log.d(TAG,"setSpinnerJangBi() - cursor Count = " + cursor.getCount());

		spin_division = (Spinner) findViewById(R.id.spin_division);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.spinner_item, spinItems){
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
			if(AndroidVersionSetMethod){
				spin_division.setDropDownVerticalOffset(8);	
			}
			
			spin_division.setAdapter(adapter);
			Log.d(TAG,"setSpinnerJeongBi() - spinItems : " + spinItems);
			
			spin_division.setOnItemSelectedListener(new OnItemSelectedListener() {
				
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Log.d("","setSpinnerJeongBi() - onItemSelected");
				Log.d("","setSpinnerJeongBi() - position check ======= " + position);

				if("도공번호".equals(spin_division.getSelectedItem().toString())){
					ed_jbname.setInputType(InputType.TYPE_CLASS_NUMBER);
				}else{
					ed_jbname.setInputType(InputType.TYPE_CLASS_TEXT);	
				}
				
				setEdjbname();
				txt_jbname.setText("");
				ed_jbname.setText("");
				getjbList(false);
				txt_jbname.performClick();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
	

	/** 정비구분 스피너 **/
	private void setSpinnerJeongBiGB(){
		Log.d(TAG, "******************** setSpinnerJeongBiGB()~! ********************");
		jeongbiGBList.clear();
		Cursor cursor = jangbiDb.select_jeongbiGB("");
		jeongbiGB_List = new ArrayList<JeongbiGB>();
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			JeongbiGB item = new JeongbiGB();
			item.setCode(cursor.getString(0));
			item.setGubun(cursor.getString(1));
			jeongbiGB_List.add(item);
			jeongbiGBList.add(cursor.getString(1));
			
		}
		jeongbiGBList.add("정비구분");
		
		Log.d("","setSpinnerJeongBiGB - jeongbiGB_List Size : " + jeongbiGB_List.size());
		Log.d("","setSpinnerJeongBiGB - jeongbiGBList : " + jeongbiGBList);
		spin_jeoungbi = (Spinner) findViewById(R.id.spin_jeoungbi);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item){
			@SuppressLint("NewApi")
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				if(position == getCount()){
					((TextView)v.findViewById(android.R.id.text1)).setText("");
					((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount()));
					((TextView)v.findViewById(android.R.id.text1)).setHintTextColor(getResources().getColor(R.color.black));
				}
				return v;
			}
						
			@Override
			public int getCount() {
				return super.getCount()-1;
			}
		};
		
		adapter.addAll(jeongbiGBList);
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_jeoungbi.setAdapter(adapter);
		spin_jeoungbi.setSelection(adapter.getCount()); 
		spin_jeoungbi.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
					Log.d("","setSpinnerJeongBiGB() - position check ======= " + position +":"+jeongbiGBList.size());
					SELECTED_JEONGBIGUBUN = jeongbiGBList.get(position).toString();
					Log.d("","setSpinnerJeongBiGB() - SELECTED_JEONGBIGUBUN :  " +SELECTED_JEONGBIGUBUN);
					selectJeongbiGB(SELECTED_JEONGBIGUBUN);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	public void selectJeongbiGB(String gubun){
		Log.d(TAG, "selectJeongbiGB() - gubun : "+gubun);
		if(!gubun.equals("정비구분")){
			Cursor cursor = jangbiDb.select_jeongbiGB(gubun);
			Log.d(TAG, "selectJeongbiGB() - cursor : "+cursor.getString(0));
			SELECTED_JEONGBIGUBUNCODE = cursor.getString(0);
		}
		Log.d(TAG,"selectJeongbiGB() - SELECTED_JEONGBIGUBUNCODE : " +SELECTED_JEONGBIGUBUNCODE);
	}
	
	
	/* 수리항목 스피너 
	 * */
	private void setSpinnerItemGB(){
		Log.d(TAG, "******************** setSpinnerItemGB()~! ********************");
		itemGBList.clear();
		Cursor cursor = jangbiDb.select_itemGB("");
		itemGB_List = new ArrayList<ItemGB>();
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			ItemGB item = new ItemGB();
			item.setno(cursor.getString(0));
			item.setsuliitem(cursor.getString(1));
			itemGB_List.add(item);
			itemGBList.add(cursor.getString(1));
		}
		
		itemGBList.add("수리항목");
		Log.d(TAG,"setSpinnerItemGB() - itemGB_List Size : " + itemGB_List.size());
		Log.d(TAG,"setSpinnerItemGB() - itemGBList : " + itemGBList);
		spin_suli = (Spinner) findViewById(R.id.spin_suli);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,  android.R.layout.simple_spinner_item){
			@SuppressLint("NewApi")
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				if(position == getCount()){
					((TextView)v.findViewById(android.R.id.text1)).setText("");
					((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount()));
					((TextView)v.findViewById(android.R.id.text1)).setHintTextColor(getResources().getColor(R.color.black));
				}
				return v;
			}
			@Override
			public int getCount() {
				return super.getCount() -1;
			}
		};
		
		Log.d(TAG,"setSpinnerItemGB() - itemGBList : " + itemGBList);
		adapter.addAll(itemGBList);
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_suli.setAdapter(adapter);
		spin_suli.setSelection(adapter.getCount()); 
		spin_suli.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				//if(position != 0){
					Log.d(TAG,"setSpinnerItemGB() - position check ======= " + position +":"+itemGBList.size());
					SELECTED_SULIITEM = spin_suli.getSelectedItem().toString();
					Log.d(TAG,"setSpinnerItemGB() - SELECTED_SULIITEM : " + SELECTED_SULIITEM);
					setSpinnerDetailGB(spin_suli.getSelectedItem().toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
	
	
	/** 세부항목 스피너 설정
	 * @param suliitem
	 */
	private void setSpinnerDetailGB(String suliitem){
		Log.d(TAG, "******************** setSpinnerDetailGB()~! ********************");
		Log.d(TAG,"setSpinnerDetailGB() - suliitem = "+suliitem);
		
		if(!suliitem.equals("수리항목")){
			Cursor cursor = jangbiDb.select_itemGB(suliitem);
			SELECTED_SULIITEMCODE = cursor.getString(2);
			Log.d(TAG, "setSpinnerDetailGB - SELECTED_SULIITEMCODE : "+SELECTED_SULIITEMCODE);
		}
		
		//1. 원동기
		if("원동기".equals(suliitem)){
			detailGBList.clear();
			Cursor cursor = jangbiDb.select_detailGB("원동기");
			detailGB_List = new ArrayList<ItemGB>();
			
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				ItemGB item = new ItemGB();
				item.setno(cursor.getString(0));
				item.setsuliitem(cursor.getString(1));
				item.setdetailitem(cursor.getString(2));
				detailGB_List.add(item);
				detailGBList.add(cursor.getString(2));
			}
		//2. 동력전당장치
		}else if("동력전달장치".equals(suliitem)){
			detailGBList.clear();
			Cursor cursor = jangbiDb.select_detailGB("동력전달장치");
			detailGB_List = new ArrayList<ItemGB>();
			
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				ItemGB item = new ItemGB();
				item.setno(cursor.getString(0));
				item.setsuliitem(cursor.getString(1));
				item.setdetailitem(cursor.getString(2));
				detailGB_List.add(item);
				detailGBList.add(cursor.getString(2));
			}
		//3. 조향장치
		}else if("조향장치".equals(suliitem)){
			detailGBList.clear();
			Cursor cursor = jangbiDb.select_detailGB("조향장치");
			detailGB_List = new ArrayList<ItemGB>();
			
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				ItemGB item = new ItemGB();
				item.setno(cursor.getString(0));
				item.setsuliitem(cursor.getString(1));
				item.setdetailitem(cursor.getString(2));
				detailGB_List.add(item);
				detailGBList.add(cursor.getString(2));
			}
		//4. 제동장치
		}else if("제동장치".equals(suliitem)){
			detailGBList.clear();
			Cursor cursor = jangbiDb.select_detailGB("제동장치");
			detailGB_List = new ArrayList<ItemGB>();
			
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				ItemGB item = new ItemGB();
				item.setno(cursor.getString(0));
				item.setsuliitem(cursor.getString(1));
				item.setdetailitem(cursor.getString(2));
				detailGB_List.add(item);
				detailGBList.add(cursor.getString(2));
			}
		//5. 주행장치
		}else if("주행장치".equals(suliitem)){
			detailGBList.clear();
			Cursor cursor = jangbiDb.select_detailGB("주행장치");
			detailGB_List = new ArrayList<ItemGB>();
			
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				ItemGB item = new ItemGB();
				item.setno(cursor.getString(0));
				item.setsuliitem(cursor.getString(1));
				item.setdetailitem(cursor.getString(2));
				detailGB_List.add(item);
				detailGBList.add(cursor.getString(2));
			}
		//6. 완충장치
		}else if("완충장치".equals(suliitem)){
			detailGBList.clear();
			Cursor cursor = jangbiDb.select_detailGB("완충장치");
			detailGB_List = new ArrayList<ItemGB>();
			
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				ItemGB item = new ItemGB();
				item.setno(cursor.getString(0));
				item.setsuliitem(cursor.getString(1));
				item.setdetailitem(cursor.getString(2));
				detailGB_List.add(item);
				detailGBList.add(cursor.getString(2));
			}	
		//7. 전기전자장치
		}else if("전기전자장치".equals(suliitem)){
			detailGBList.clear();
			Cursor cursor = jangbiDb.select_detailGB("전기전자장치");
			detailGB_List = new ArrayList<ItemGB>();
			
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				ItemGB item = new ItemGB();
				item.setno(cursor.getString(0));
				item.setsuliitem(cursor.getString(1));
				item.setdetailitem(cursor.getString(2));
				detailGB_List.add(item);
				detailGBList.add(cursor.getString(2));
			}
		//8. 유공압장치
		}else if("유공압장치".equals(suliitem)){
			detailGBList.clear();
			Cursor cursor = jangbiDb.select_detailGB("유공압장치");
			detailGB_List = new ArrayList<ItemGB>();
			
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				ItemGB item = new ItemGB();
				item.setno(cursor.getString(0));
				item.setsuliitem(cursor.getString(1));
				item.setdetailitem(cursor.getString(2));
				detailGB_List.add(item);
				detailGBList.add(cursor.getString(2));
			}
		//9. 차체
		}else if("차체".equals(suliitem)){
			detailGBList.clear();
			Cursor cursor = jangbiDb.select_detailGB("차체");
			detailGB_List = new ArrayList<ItemGB>();
			
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				ItemGB item = new ItemGB();
				item.setno(cursor.getString(0));
				item.setsuliitem(cursor.getString(1));
				item.setdetailitem(cursor.getString(2));
				detailGB_List.add(item);
				detailGBList.add(cursor.getString(2));
			}
		//10. 작업장치
		}else if("작업장치".equals(suliitem)){
			detailGBList.clear();
			Cursor cursor = jangbiDb.select_detailGB("작업장치");
			detailGB_List = new ArrayList<ItemGB>();
			
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				ItemGB item = new ItemGB();
				item.setno(cursor.getString(0));
				item.setsuliitem(cursor.getString(1));
				item.setdetailitem(cursor.getString(2));
				detailGB_List.add(item);
				detailGBList.add(cursor.getString(2));
			}
		//11. 정비용품
		}else if("정비용품".equals(suliitem)){
			detailGBList.clear();
			Cursor cursor = jangbiDb.select_detailGB("정비용품");
			detailGB_List = new ArrayList<ItemGB>();
			
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				ItemGB item = new ItemGB();
				item.setno(cursor.getString(0));
				item.setsuliitem(cursor.getString(1));
				item.setdetailitem(cursor.getString(2));
				detailGB_List.add(item);
				detailGBList.add(cursor.getString(2));
			}
		//12. 기타
		}else if("기타".equals(suliitem)){
			detailGBList.clear();
			Cursor cursor = jangbiDb.select_detailGB("기타");
			detailGB_List = new ArrayList<ItemGB>();
			
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				ItemGB item = new ItemGB();
				item.setno(cursor.getString(0));
				item.setsuliitem(cursor.getString(1));
				item.setdetailitem(cursor.getString(2));
				detailGB_List.add(item);
				detailGBList.add(cursor.getString(2));
			}
		}else{
			detailGBList.clear();
			Cursor cursor = jangbiDb.select_detailGB("");
			detailGB_List = new ArrayList<ItemGB>();
			
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				ItemGB item = new ItemGB();
				item.setno(cursor.getString(0));
				item.setsuliitem(cursor.getString(1));
				item.setdetailitem(cursor.getString(2));
				detailGB_List.add(item);
				
				detailGBList.add(cursor.getString(2));
			}
		}
		
		if("".equals(suliitem.trim()) || "수리항목".equals(suliitem.trim())){
			detailGBList.add("수리항목을 선택하세요.");	
			detailGBList.add("세부항목");
			Log.d(TAG,"setSpinnerDetailGB() - detailGBList : " + detailGBList);
			Log.d(TAG,"setSpinnerDetailGB() - detailGB_List : " + detailGB_List);
			spin_detail = (Spinner) findViewById(R.id.spin_detail);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item){
				@SuppressLint("NewApi")
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					View v = super.getView(position, convertView, parent);
					if(position == getCount()){
						((TextView)v.findViewById(android.R.id.text1)).setText("");
						((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount()));
						((TextView)v.findViewById(android.R.id.text1)).setHintTextColor(getResources().getColor(R.color.black));
					}
					return v;
				}
				@Override
				public int getCount() {
					return super.getCount() -1;
				}
			};
			
			adapter.addAll(detailGBList);
			
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spin_detail.setAdapter(adapter);
			spin_detail.setSelection(adapter.getCount()); 
		}else{
		
		detailGBList.add("세부항목");
		Log.d(TAG,"setSpinnerDetailGB() - detailGBList ======= " + detailGBList);
		Log.d(TAG,"setSpinnerDetailGB() - detailGB_List ======= " + detailGB_List);
		spin_detail = (Spinner) findViewById(R.id.spin_detail);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item){
			@SuppressLint("NewApi")
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				if(position == getCount()){
					((TextView)v.findViewById(android.R.id.text1)).setText("");
					((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount()));
					((TextView)v.findViewById(android.R.id.text1)).setHintTextColor(getResources().getColor(R.color.black));
				}
				return v;
			}
			@Override
			public int getCount() {
				return super.getCount() -1;
			}
		};
		
		adapter.addAll(detailGBList);
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin_detail.setAdapter(adapter);
		spin_detail.setSelection(adapter.getCount()); 
		spin_detail.setSelection(0);
		}
		spin_detail.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
					Log.d(TAG,"setSpinnerDetailGB() - position check ======= " + position +":"+detailGB_List.size());
					SELECTED_DETAILITEM = spin_detail.getSelectedItem().toString();
					Log.d(TAG,"setSpinnerDetailGB() - SELECTED_DETAILITEM : " + SELECTED_DETAILITEM);
					
					if("기타".equals(SELECTED_DETAILITEM)){
						ed_write.setVisibility(View.VISIBLE);
						spin_detailB.setVisibility(View.GONE);
						ed_write.requestFocus();
						ed_write.setText("");
						ed_write.setSelection(ed_write.getText().length());
						SELECTED_DETAILITEM = ed_write.getText().toString();
						Log.d(TAG,"setSpinnerDetailGB() - SELECTED_DETAILITEM(ed_write) : " + SELECTED_DETAILITEM);
					}else{
						ed_write.setText("");
						ed_write.setVisibility(View.GONE);
					}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
	
	

	//카메라실행
	public void runCamera_Dialog(){
		Log.d(TAG, "******************** runCamera_Dialog()~! ********************");
		if(ed_write.getVisibility() == View.VISIBLE){
			Log.d(TAG,"runCamera_Dialog() - SELECTED_DETAILITEM = " + SELECTED_DETAILITEM);
			SELECTED_DETAILITEM = ed_write.getText().toString();
		}
		
		boolean showDialog = false;
		String message = "";
		
		Log.d(TAG,"runCamera_Dialog() - SELECTED_JEONGBIGUBUN = " + SELECTED_JEONGBIGUBUN);
		Log.d(TAG,"runCamera_Dialog() - SELECTED_JEONGBIGUBUNCODE = " + SELECTED_JEONGBIGUBUNCODE);
		Log.d(TAG,"runCamera_Dialog() - SELECTED_SULIITEM = " + SELECTED_SULIITEM);
		Log.d(TAG,"runCamera_Dialog() - SELECTED_SULIITEMCODE = " + SELECTED_SULIITEMCODE);
		Log.d(TAG,"runCamera_Dialog() - SELECTED_DETAILITEM = " + SELECTED_DETAILITEM);
		Log.d(TAG,"runCamera_Dialog() - SELECTED_JBCODE = " + SELECTED_JBCODE);
		Log.d(TAG,"runCamera_Dialog() - SELECTED_JBMYEONG = " + SELECTED_JBMYEONG);
		Log.d(TAG,"runCamera_Dialog() - SELECTED_DRNO = " + SELECTED_DRNO);
		Log.d(TAG,"runCamera_Dialog() - SELECTED_DOGONG = " + SELECTED_DOGONG);
		Log.d(TAG,"runCamera_Dialog() - SELECTED_BSCODE = " + SELECTED_BSCODE);
		
		if("".equals(Common.nullCheck(SELECTED_JBCODE))){
			showDialog = true;
			message = "장비를 선택해 주세요.";
		}else if("".equals(Common.nullCheck(SELECTED_JEONGBIGUBUN)) || "정비구분".equals(Common.nullCheck(SELECTED_JEONGBIGUBUN))){
			showDialog = true;
			message = "정비구분을 선택해 주세요.";
		}else if("".equals(Common.nullCheck(SELECTED_SULIITEM)) || "수리항목".equals(Common.nullCheck(SELECTED_SULIITEM))){
			showDialog = true;
			message = "수리항목을 선택해 주세요.";
		}else if("".equals(Common.nullCheck(SELECTED_DETAILITEM)) || "세부항목".equals(Common.nullCheck(SELECTED_DETAILITEM))){
			showDialog = true;
			message = "세부항목을 선택해 주세요.";
		}
		
		Log.d(TAG,"runCamera_Dialog() - showDialog1 = " + showDialog);
		
		if (showDialog) {
			Log.d(TAG,"runCamera_Dialog() - showDialog = true :: " + showDialog);
			AlertDialog.Builder adbLoc	= new AlertDialog.Builder(contextActivity);
			adbLoc.setCancelable(false);
			adbLoc.setTitle("스마트정비관리");  
			adbLoc.setMessage(message);
			adbLoc.setPositiveButton("확인", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					
				}
			});
			adbLoc.show();
		}else{
			Log.d(TAG,"runCamera_Dialog() - showDialog = false :: " + showDialog);
			Log.d(TAG,"runCamera_Dialog() - SELECTED_BEFORE_AFTER = " + SELECTED_BEFORE_AFTER);
			
			/*int changedPoint = db.checkDataChanged_Jeongbi(
					"["+SELECTED_JEONGBIGUBUNCODE+"]"+SELECTED_JEONGBIGUBUN, 
					//SELECTED_SULIITEM, 
					"["+SELECTED_SULIITEMCODE+"]"+SELECTED_SULIITEM,
					SELECTED_DETAILITEM,	
					SELECTED_SULIITEM+"/"+SELECTED_DETAILITEM+"("+SELECTED_BEFORE_AFTER+")", 
					"",
					SELECTED_JBCODE, 
					SELECTED_JBMYEONG, 
					SELECTED_DRNO, 
					SELECTED_JBMYEONG+"("+SELECTED_DRNO+")", 
					SELECTED_DOGONG, 
					SELECTED_SWBEONHO, 
					SELECTED_BSCODE, 
					SELECTED_BUSEOCODE 
					);
			
			if(changedPoint == 1){//위치만 고정
				
			}*/

			final CameraDialogActivity cameraDialog = new CameraDialogActivity(MainActivity.this);
			cameraDialog.show();
			cameraDialog.get_before_jeongbi().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					cameraDialog.dismiss();
					SELECTED_BEFORE_AFTER = "정비 전";
					runCamera();		
				}
			});
			cameraDialog.get_after_jeongbi().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					cameraDialog.dismiss();
					SELECTED_BEFORE_AFTER = "정비 후";
					runCamera();
				}
			});
			
			if(TAGYN == true){
			}else{
			}
		}
	}

	
	//앨범실행
	public void runAlbum_Dialog(){
		Log.d(TAG, "******************** runAlbum_Dialog()~! ********************");
		if(ed_write.getVisibility() == View.VISIBLE){
			Log.d("","runAlbum_Dialog - SELECTED_DETAILITEM = " + SELECTED_DETAILITEM);
			SELECTED_DETAILITEM = ed_write.getText().toString();
		}

		boolean showDialog = false;
		String message = "";
		
		
		Log.d("","runAlbum_Dialog - SELECTED_JEONGBIGUBUN = " + SELECTED_JEONGBIGUBUN);
		Log.d("","runAlbum_Dialog - SELECTED_JEONGBIGUBUNCODE = " + SELECTED_JEONGBIGUBUNCODE);
		Log.d("","runAlbum_Dialog - SELECTED_SULIITEM = " + SELECTED_SULIITEM);
		Log.d("","runAlbum_Dialog - SELECTED_SULIITEMCODE = " + SELECTED_SULIITEMCODE);
		Log.d("","runAlbum_Dialog - SELECTED_DETAILITEM = " + SELECTED_DETAILITEM);
		
		Log.d("","runAlbum_Dialog - SELECTED_JBCODE = " + SELECTED_JBCODE);
		Log.d("","runAlbum_Dialog - SELECTED_JBMYEONG = " + SELECTED_JBMYEONG);
		Log.d("","runAlbum_Dialog - SELECTED_DRNO = " + SELECTED_DRNO);
		Log.d("","runAlbum_Dialog - SELECTED_DOGONG = " + SELECTED_DOGONG);
		Log.d("","runAlbum_Dialog - SELECTED_BSCODE = " + SELECTED_BSCODE);
		
		Log.d("","runAlbum_Dialog - SELECTED_JBCODE = " + SELECTED_JBCODE);
		Log.d("","runAlbum_Dialog - SELECTED_JBMYEONG = " + SELECTED_JBMYEONG);
		Log.d("","runAlbum_Dialog - SELECTED_DRNO = " + SELECTED_DRNO);
		Log.d("","runAlbum_Dialog - SELECTED_DOGONG = " + SELECTED_DOGONG);
		Log.d("","runAlbum_Dialog - SELECTED_SULIITEM = " + SELECTED_SULIITEM);
		Log.d("","runAlbum_Dialog - SELECTED_DETAILITEM = " + SELECTED_DETAILITEM);
		
		if("".equals(Common.nullCheck(SELECTED_JBCODE))){
			showDialog = true;
			message = "장비를 선택해 주세요.";
		}else if("".equals(Common.nullCheck(SELECTED_JEONGBIGUBUN)) || "정비구분".equals(Common.nullCheck(SELECTED_JEONGBIGUBUN))){
			showDialog = true;
			message = "정비구분을 선택해 주세요.";
		}else if("".equals(Common.nullCheck(SELECTED_SULIITEM)) || "수리항목".equals(Common.nullCheck(SELECTED_SULIITEM))){
			showDialog = true;
			message = "수리항목을 선택해 주세요.";
		}else if("".equals(Common.nullCheck(SELECTED_DETAILITEM)) || "세부항목".equals(Common.nullCheck(SELECTED_DETAILITEM))){
			showDialog = true;
			message = "세부항목을 선택해 주세요.";
		}
		
		if (showDialog) {
			AlertDialog.Builder adbLoc	= new AlertDialog.Builder(contextActivity);
			adbLoc.setCancelable(false);
			adbLoc.setTitle("스마트정비관리");
			adbLoc.setMessage(message);
			adbLoc.setPositiveButton("확인", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					
				}
			});
			adbLoc.show();
		}else{
			/*int changedPoint = db.checkDataChanged_Jeongbi(
					"["+SELECTED_JEONGBIGUBUNCODE+"]"+SELECTED_JEONGBIGUBUN,
					"["+SELECTED_SULIITEMCODE+"]"+SELECTED_SULIITEM,
					SELECTED_DETAILITEM,	
					SELECTED_SULIITEM+"/"+SELECTED_DETAILITEM+"("+SELECTED_BEFORE_AFTER+")", 
					"",
					SELECTED_JBCODE, 
					SELECTED_JBMYEONG, 
					SELECTED_DRNO, 
					SELECTED_JBMYEONG+"("+SELECTED_DRNO+")", 
					SELECTED_DOGONG, 
					SELECTED_SWBEONHO, 
					SELECTED_BSCODE, 
					SELECTED_BUSEOCODE
					);
			
			if(changedPoint == 1){//위치만 고정
			}*/

			
			final CameraDialogActivity cameraDialog = new CameraDialogActivity(MainActivity.this);
			cameraDialog.show();
			cameraDialog.get_before_jeongbi().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					cameraDialog.dismiss();
					SELECTED_BEFORE_AFTER = "정비 전";
					runAlbum();	
				}
			});
			cameraDialog.get_after_jeongbi().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					cameraDialog.dismiss();
					SELECTED_BEFORE_AFTER = "정비 후";
					runAlbum();
				}
			});

			
			
			
			if(TAGYN == true){
			}else{
			}
		}
	}

	
	
	private void runCamera(){
		Log.d(TAG, "******************** runCamera()~! ********************");
		
		//Intent cameraIntent = new Intent(MainActivity.this, CameraActivity.class);
		Intent cameraIntent = new Intent(MainActivity.this, MultiCameraActivity.class);

		Log.d(TAG, "runCamera() - SELECTED_JEONGBIGUBUNCODE : "+SELECTED_JEONGBIGUBUNCODE);
		Log.d(TAG, "runCamera() - SELECTED_JEONGBIGUBUN : "+SELECTED_JEONGBIGUBUN);
		Log.d(TAG, "runCamera() - SELECTED_BEFORE_AFTER : "+SELECTED_BEFORE_AFTER);
		
		cameraIntent.putExtra("SELECTED_JBCODE", SELECTED_JBCODE);
		cameraIntent.putExtra("SELECTED_JBMYEONG", SELECTED_JBMYEONG);
		cameraIntent.putExtra("SELECTED_DRNO", SELECTED_DRNO);
		cameraIntent.putExtra("SELECTED_DOGONG", SELECTED_DOGONG);
		cameraIntent.putExtra("SELECTED_JEONGBIGUBUN", "["+SELECTED_JEONGBIGUBUNCODE+"]"+SELECTED_JEONGBIGUBUN);
		cameraIntent.putExtra("SELECTED_SULIITEM", SELECTED_SULIITEM);
		cameraIntent.putExtra("SELECTED_SULIITEMCODE", SELECTED_SULIITEMCODE);
		cameraIntent.putExtra("SELECTED_DETAILITEM", SELECTED_DETAILITEM);
		cameraIntent.putExtra("SELECTED_BEFORE_AFTER", SELECTED_BEFORE_AFTER);
		cameraIntent.putExtra("SELECTED_BUSEOCODE", jisacode);
		cameraIntent.putExtra("SELECTED_BSCODE", SELECTED_BSCODE);
		//cameraIntent.putExtra("SELECTED_JISACODE", SELECTED_JISACODE);
		//cameraIntent.putExtra("SELECTED_CONTENT", SELECTED_CONTENT);
		
		cameraIntent.putExtra("SELECTED_DETAILITEM", SELECTED_DETAILITEM);
		cameraIntent.putExtra("SELECTED_SWBEONHO", SELECTED_SWBEONHO);
		
		//cameraIntent.putExtra("SELECTED_JGUBUN", SELECTED_JGUBUN);
		
		cameraIntent.putExtra("TAGYN", TAGYN);
		
		startActivity(cameraIntent);	
	}
	

	public String getRealPathFromURI(Uri contentUri){
		String [] proj={MediaStore.Images.Media.DATA};
		Cursor cursor = managedQuery( contentUri, proj, null, null, null); 
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult()~!");
		Log.d(TAG, "onActivityResult() - requestCode : "+requestCode);
		Log.d(TAG, "onActivityResult() - resultCode : "+resultCode);
		Log.d(TAG, "onActivityResult() - data : "+data);
		super.onActivityResult(requestCode, resultCode, data);
		boolean showDialog = false;
		String message = "";
		String dataDate = "";
		Log.d(TAG, "onActivityResult() - requestCode : "+requestCode+" // resultCode : "+resultCode+" // data : "+data);
		if(resultCode == RESULT_OK){
			if(requestCode == Configuration.REQUESTCODE_GETIMG){
				Uri mUriSet = data.getData();
				File tempfile = new File(getRealPathFromURI(mUriSet)); 
				Log.d(TAG,"onActivityResult() - getAbsoluteFile = " + tempfile.getAbsoluteFile().toString());
				Log.d(TAG,"onActivityResult() - getName =" + tempfile.getName());
				
				ExifInterface exif;
				try {
					exif = new ExifInterface(tempfile.getAbsoluteFile().toString());
					dataDate = exif.getAttribute(ExifInterface.TAG_DATETIME);
					Log.d(TAG,"onActivityResult() - dataDate1 = " + dataDate);
					if("".equals(common.nullCheck(dataDate))){
						showDialog = true;
						message = "사진메타정보가 존재하지 않습니다.";
						dataDate = Common.getCalendarDateYMDHMS();
						Log.d(TAG,"onActivityResult() - no metaData dateDate : " + dataDate);
					}else{
						dataDate = dataDate.substring(0, 10).replace(":", ".")+" "+dataDate.substring(11, 19);
						Log.d(TAG,"onActivityResult() - yes metaData dateDate : " + dataDate);
					}
					
					if (showDialog) {
						Log.d(TAG,"onActivityResult() - [showDialog True] dataDate = " + dataDate);
						AlertDialog.Builder adbLoc	= new AlertDialog.Builder(contextActivity);
						adbLoc.setCancelable(false);
						adbLoc.setTitle("스마트정비관리");  
						adbLoc.setMessage(message);
						adbLoc.setPositiveButton("확인", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						});
						adbLoc.show();
					}
					Log.d(TAG,"onActivityResult() - dataDate2 = " + dataDate);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				if(TAGYN == true){
					SELECTED_TAGYN ="Y";
				}else{
					SELECTED_TAGYN ="N";
				}
				
				//db.insertSmart_DATAINFO(
				checkDb.insertSmart_DATAINFO(
						/* 2018.06.07.
						 * Camera 촬영 시 처리되는 정비구분, 수리항목 등에 선택인덱스 추가 부분이 누락됨
						 * Camera와 동일하게 처리 추가!
						 * */
						"["+SELECTED_JEONGBIGUBUNCODE+"]"+SELECTED_JEONGBIGUBUN,						 
						"["+SELECTED_SULIITEMCODE+"]"+SELECTED_SULIITEM, 
						SELECTED_DETAILITEM,	
						SELECTED_SULIITEM+"/"+SELECTED_DETAILITEM+"("+SELECTED_BEFORE_AFTER+")", 
						tempfile.getAbsolutePath().toString(),
						SELECTED_JBCODE, 
						SELECTED_JBMYEONG,
						SELECTED_DRNO, 
						SELECTED_JBMYEONG+"("+SELECTED_DRNO+")", 
						common.nullCheck(SELECTED_DOGONG), 
						dataDate,
						SELECTED_SWBEONHO, 
						SELECTED_BSCODE, 
						SELECTED_BUSEOCODE, 
						SELECTED_TAGYN, 
						getUserNm(MainActivity.this),
						""
						);
			}
		}
	}

	
	/*private void runAlbum(){
		Log.d(TAG, "******************** runAlbum()~! ********************");
		if(ed_write.getVisibility() == View.VISIBLE){
			SELECTED_DETAILITEM = ed_write.getText().toString();
		}
		Log.d(TAG, "runCamera() - SELECTED_DETAILITEM : "+SELECTED_DETAILITEM);
		Intent i = new Intent(Intent.ACTION_PICK);
		i.setType("image/*");
		startActivityForResult(i, Configuration.REQUESTCODE_GETIMG);
	}*/
	/*private void runAlbum(){
		Log.d(TAG, "******************** runAlbum()~! ********************");
		Intent albumIntent;
		albumIntent = new Intent(MainActivity.this, AlbumListActivity.class);
		
		albumIntent.putExtra("SELECTED_JBCODE", SELECTED_JBCODE);
		albumIntent.putExtra("SELECTED_JBMYEONG", SELECTED_JBMYEONG);
		albumIntent.putExtra("SELECTED_DRNO", SELECTED_DRNO);
		albumIntent.putExtra("SELECTED_DOGONG", SELECTED_DOGONG);
		albumIntent.putExtra("SELECTED_JEONGBIGUBUN", "["+SELECTED_JEONGBIGUBUNCODE+"]"+SELECTED_JEONGBIGUBUN);
		albumIntent.putExtra("SELECTED_SULIITEM", SELECTED_SULIITEM);
		albumIntent.putExtra("SELECTED_SULIITEMCODE", SELECTED_SULIITEMCODE);
		albumIntent.putExtra("SELECTED_DETAILITEM", SELECTED_DETAILITEM);
		albumIntent.putExtra("SELECTED_BEFORE_AFTER", SELECTED_BEFORE_AFTER);
		albumIntent.putExtra("SELECTED_BUSEOCODE", jisacode);
		albumIntent.putExtra("SELECTED_BSCODE", SELECTED_BSCODE);
		albumIntent.putExtra("SELECTED_JEONGBIGUBUNCODE", SELECTED_JEONGBIGUBUNCODE);
		albumIntent.putExtra("SELECTED_SWBEONHO", SELECTED_SWBEONHO);
		
		//intent = new Intent(MainActivity.this, PhotoListActivity.class);
		startActivity(albumIntent);
	}*/
	
	private void runAlbum(){
		Log.d(TAG, "******************** runAlbum()~! ********************");
		Intent albumIntent;
		albumIntent = new Intent(MainActivity.this, PhotoAllListActivity.class);
		
		albumIntent.putExtra("SELECTED_JBCODE", SELECTED_JBCODE);
		albumIntent.putExtra("SELECTED_JBMYEONG", SELECTED_JBMYEONG);
		albumIntent.putExtra("SELECTED_DRNO", SELECTED_DRNO);
		albumIntent.putExtra("SELECTED_DOGONG", SELECTED_DOGONG);
		albumIntent.putExtra("SELECTED_JEONGBIGUBUN", "["+SELECTED_JEONGBIGUBUNCODE+"]"+SELECTED_JEONGBIGUBUN);
		albumIntent.putExtra("SELECTED_SULIITEM", SELECTED_SULIITEM);
		albumIntent.putExtra("SELECTED_SULIITEMCODE", SELECTED_SULIITEMCODE);
		albumIntent.putExtra("SELECTED_DETAILITEM", SELECTED_DETAILITEM);
		albumIntent.putExtra("SELECTED_BEFORE_AFTER", SELECTED_BEFORE_AFTER);
		albumIntent.putExtra("SELECTED_BUSEOCODE", jisacode);
		albumIntent.putExtra("SELECTED_BSCODE", SELECTED_BSCODE);
		albumIntent.putExtra("SELECTED_JEONGBIGUBUNCODE", SELECTED_JEONGBIGUBUNCODE);
		albumIntent.putExtra("SELECTED_SWBEONHO", SELECTED_SWBEONHO);
		
		//intent = new Intent(MainActivity.this, PhotoListActivity.class);
		startActivity(albumIntent);
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {

		case R.id.txt_changeDept:
			intent = new Intent(MainActivity.this, DialogActivity_BsSel.class);
			startActivity(intent);
			finish();
			break;
		case R.id.btn_X:
			Common.setPrefString(MainActivity.this, Configuration.SHARED_BTNX, "Y");
			finish();
			break;
			
		case R.id.spin_detailB:
			spin_detail.performClick();
			break;
			
		case R.id.btn_txt_jbname:
			Log.d(TAG, "onClick() - btn_txt_jbname");
			txt_jbname.setText("");
			ed_jbname.setText("");
			txt_jangbi.setText("");
			txt_drno.setText("");
			txt_chajong.setText("");
			txt_dogong.setText("");
			SELECTED_JBCODE = "";
			break;
			
		case R.id.ll_jblist:
			Log.d(TAG, "onClick() - ll_jblist");
			ll_jblist.setVisibility(View.GONE);
			break;
		case R.id.txt_jbname:
			Log.d(TAG, "onClick() - txt_jbname");
			ed_jbname.setText(txt_jbname.getText());
			ed_jbname.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
			
			ll_jblist.setVisibility(View.VISIBLE);
			ed_jbname.setSelection(ed_jbname.getText().length());
			break;
			
		case R.id.btn_goCamera:
			TAGYN = true;
			runCamera_Dialog();
			break;
			
		case R.id.btn_goAlbum:
			TAGYN = true;
			runAlbum_Dialog();
			break;
		
		case R.id.btn_goServer:
			Parameters params = new Parameters(Configuration.FILE_PRIMITIVE);
			String wifissid = "";

			WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
			wifissid = wifiManager.getConnectionInfo().getSSID();
			
			Log.d(TAG, "btn_goServer - params : "+params.toString());
			Log.d(TAG, "btn_goServer - wifissid : "+wifissid);
			
			/*if(null != wifissid && "<unknown ssid>".equals(wifissid)){
				Log.d(TAG, "btn_sendImage - 11111");
				Log.d(TAG, "btn_sendImage - wifissid != null "+(wifissid.indexOf("Ex-Smobile")));
				if(!(wifissid.indexOf("Ex-Smobile")>-1) ){
					Log.d(TAG, "btn_sendImage - 22222");
					Log.d(TAG, "btn_sendImage - wifissid = -1 ");
					executeJob(params, MainActivity.this);
				}else{
					Log.d(TAG, "btn_sendImage - 33333");
					Log.d(TAG, "btn_sendImage - wifissid = 1");
					Log.d(TAG, "btn_sendImage - 서버전송-업무접속필요");

					AlertDialog.Builder adbLoc	= new AlertDialog.Builder(MainActivity.this);
					adbLoc.setCancelable(false);
					adbLoc.setTitle("스마트정비관리"); 
					adbLoc.setMessage("업무접속 연결 확인 후 사용하시기 바랍니다.");
					adbLoc.setPositiveButton("확인", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					adbLoc.setNegativeButton("업무접속하기", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = getPackageManager().getLaunchIntentForPackage("com.ex.group.folder");
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			                startActivity(intent);
						}
					});
					adbLoc.show();
				}
			}else{
				Log.d(TAG, "btn_sendImage - 44444");
				Log.d(TAG, "btn_sendImage - wifissid = null ");
				executeJob(params, MainActivity.this);
			}*/
			
			
			//Wifi X, LTE O
			if(wifissid.equals("") || wifissid.equals(null) || wifissid.equals("<unknown ssid>")){
				Log.d(TAG, "btn_goServer - 11111");
				executeJob(params, MainActivity.this);

			//Wifi O
			}else{
				Log.d(TAG, "btn_goServer - 22222");
				//사내 Wifi
				if(!(wifissid.indexOf("StarBobs")>-1) ){
					Log.d(TAG, "btn_goServer - 33333");
					executeJob(params, MainActivity.this);
				//일반 Wifi
				}else{
					Log.d(TAG, "btn_goServer - 44444");
					AlertDialog.Builder adbLoc	= new AlertDialog.Builder(MainActivity.this);
					adbLoc.setCancelable(false);
					adbLoc.setTitle("스마트정비관리"); 
					adbLoc.setMessage("업무접속 연결 확인 후 사용하시기 바랍니다.");
					adbLoc.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					adbLoc.setNegativeButton("업무접속하기", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = getPackageManager().getLaunchIntentForPackage("com.ex.group.folder");
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			                startActivity(intent);
						}
					});
					adbLoc.show();
				}
			}
			break;
			
		case R.id.btn_goCheckList:
			intent = new Intent(MainActivity.this, CheckListActivity2.class);
			startActivity(intent);
			break;
		
		default:
			break;
		}
	}
	
	
	@Override
	public void onBackPressed() {
		if(ll_jblist.getVisibility() == View.VISIBLE){
			ll_jblist.setVisibility(View.GONE);
		}else{
			Common.setPrefString(MainActivity.this, Configuration.SHARED_BTNX, "Y");
			super.onBackPressed();
		}
	}
	
	
	/************************************************************************
	 * 제보 파일 전송 - Progressbar, AsyncTask를 이용하여 파일 전송 - 전송완료 후 제보내용 전송 호출
	 ************************************************************************/
	String sendCheckFile;
	String transData = "Y";
	CustomMultiPartEntity multipartContent;
	public int iPercent;

	public class DoComplecatedJob extends AsyncTask<String, Integer, Long> {
		long totalSize;
		long totalSizeKB;

		// 진행 상태 Progressbar
		ProgressDialog progressDialog;
		Dialog mDialog = null;
		ProgressBar pb = null;
		ImageButton ibtnProgressCancel;

		public TextView tv;
		public TextView tvTrans;
		public TextView tvTot;
		public TextView tvPromptProgress;
		public int iProgress;
		public List<ImageJgInfo> mFileList;
		Parameters params;
		Activity mActivity;
		int fileCnt = 0;
		HttpPost httpPost;
		HttpClient httpClient;
		
		public DoComplecatedJob() {
			super();
		}

		public DoComplecatedJob(List<ImageJgInfo> mList, Parameters params, Activity mActivity) {
			super();
			Log.d(TAG, "******************** DoComplecatedJob CLASS > DoComplecatedJob()~! ********************");
			Log.d(TAG, "params : "+params);
			mFileList = mList;
			this.params = params;
			this.mActivity = mActivity;
		}

		@Override
		protected void onPreExecute() {
			Log.d(TAG, "DoComplecatedJob > onPreExecute()~!!");
			// --------------------------------------------------------------------------------------------
			// #region 전송화면 출력

			progressDialog = new ProgressDialog(mActivity);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setMessage("파일 전송 중...");
			progressDialog.setCancelable(true);

			progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "전송취소",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							transData = "N";
							cancel(true);
							taskCancel();
							Log.d("","fffffffffffffffffffffffffffffffffff 111");
						}
					});

			mDialog = new Dialog(mActivity, R.style.FullHeightDialog);
			mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			mDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mDialog.setContentView(R.layout.cust_progressbar);
			mDialog.setCancelable(false);
			
			pb = (ProgressBar) mDialog.findViewById(R.id.pbProgress);
			tv = (TextView) mDialog.findViewById(R.id.tvPercentProgress);
			tvTrans = (TextView) mDialog.findViewById(R.id.tvProgressTrans);
			tvTot = (TextView) mDialog.findViewById(R.id.tvProgressTot);
			tvPromptProgress = (TextView) mDialog.findViewById(R.id.tvPromptProgress);
			tvPromptProgress.setText("사진을 전송중 입니다.");
			ibtnProgressCancel = (ImageButton) mDialog.findViewById(R.id.ibtnProgressCancel);
			ibtnProgressCancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mDialog.dismiss();
					transData = "N";
					cancel(true);
					taskCancel();
					Log.d("","fffffffffffffffffffffffffffffffffff 222");
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			// 화면 표시 초기화
			tv.setText("0 %");
			tvTrans.setText("0 KB");
			tvTot.setText("0 KB");

			// 진행 변수 초기화
			iProgress = 0;
			iPercent = 0;
			totalSize = 0;
			totalSizeKB = 0;
			transData = "Y";
			mDialog.show();

			// #endregion
			// --------------------------------------------------------------------------------------------
		}

		@Override
		protected Long doInBackground(String... strData) {
			Log.d(TAG, "DoComplecatedJob > - doInBackground()~!!");
			Log.d(TAG, "doInBackground() - strData : "+strData.toString());
			Log.d(TAG, "doInBackground() - mFileListSize : "+mFileList.size());
			// --------------------------------------------------------------------------------------------
			// #region 실 첨부파일 전송, 전송화면 프로그레스 바 업데이트 함수 호출
			for (int i = 0; i < mFileList.size(); i++) {
				httpClient = new DefaultHttpClient();
				HttpContext httpContext = new BasicHttpContext();
				
				fileCnt ++;
				try {
					StringBuffer sb = new StringBuffer();
					sb.append(Configuration.FILE_UPLOAD_PATH);
					sb.append("?");
					sb.append(params.toString());
					Log.d(TAG,"doInBackground() - URL : "+sb.toString());
					//Log.i("","URL = " + sb.toString());

					httpPost = new HttpPost(sb.toString());
					multipartContent = new CustomMultiPartEntity(
							new ProgressListener() {

								int uploadPercent = 0;

								@Override
								public void transferred(long num) {
									// TODO Auto-generated method stub
									publishProgress((int) ((num / (float) totalSize) * 100));
								}
							});

					String absolutePath = "";

					// 파일 첨부 및 전송
					Log.d(TAG, "doInBackground() - before send ReadSdCard Size : "+ ReadSDCard().size());

					ImageJgInfo image = mFileList.get(i);
					absolutePath = image.getFilePath().toString();
					Log.d(TAG, "doInBackground() - send absolutePath = " + absolutePath);
					multipartContent.addPart("uploaded_file" + i, new FileBody(new File(absolutePath)));

					totalSize = multipartContent.getContentLength();
					totalSizeKB = totalSize / 1024;
					// pb.setMax(Integer.parseInt(totalSize + ""));

					// Send it
					multipartContent.addPart("renamePrefix", new StringBody(""));
					
					//다음 형태로 태그 정보 넘기기 (사진태그)
					Log.d(TAG,"Multipartcontent SURIL!! = "+image.getInfo().getreg_date());     														// 수리일
					Log.d(TAG,"Multipartcontent SURIL = "+image.getInfo().getreg_date().replace(".", "").substring(0, 8));     						// 수리일
					Log.d(TAG,"Multipartcontent JANGBICODE = "+Common.nullCheck(image.getInfo().getjangbicode()));     								// 장비코드
					Log.d(TAG,"Multipartcontent SURIDATE = "+image.getInfo().getreg_date().substring(0, 16));										// 정비일시
					Log.d(TAG,"Multipartcontent JANGBIMYEONG = "+Common.nullCheck(image.getInfo().getjangbimyeong()));     							// 장비명
					Log.d(TAG,"Multipartcontent DOGONGBEONHO = "+Common.nullCheck(image.getInfo().getdogongcode()));     						    // 도공번호
					Log.d(TAG,"Multipartcontent CONTENT = "+image.getInfo().getsulicontent());  														// 수리내용
					Log.d(TAG,"Multipartcontent IMGAGE = "+image.getFileName().toString());															// 이미지명
					Log.d(TAG,"Multipartcontent BUSEOCODE = "+image.getInfo().getbuseocode());
					Log.d(TAG,"Multipartcontent GUBUN = "+image.getInfo().getGubun());
					Log.d(TAG,"Multipartcontent BUPUMCODE = "+image.getInfo().getBupumcode());
					Log.d(TAG,"Multipartcontent SWBEONHO = "+image.getInfo().getswbeonho());
					Log.d(TAG,"Multipartcontent CHECKDATE = "+image.getInfo().getCheckdate());
					Log.d(TAG,"Multipartcontent SENDDATE = "+common.getCalendarDateYMDHM());

					String tempDate = image.getInfo().getreg_date();
					String year = tempDate.substring(0,4);
					String month = tempDate.substring(4,6);
					String day = tempDate.substring(6,8);
					String hh = tempDate.substring(8,10);
					String mm = tempDate.substring(10,12);
					String ss = tempDate.substring(12,14);
					
					//모바일 서버 용(사진표출내용)
					multipartContent.addPart("SURIL",new StringBody(image.getInfo().getreg_date().replace(".", "").substring(0, 8), Charset.forName("UTF-8")));
					multipartContent.addPart("JANGBICODE",new StringBody(Common.nullCheck(image.getInfo().getjangbicode()), Charset.forName("UTF-8")));
					multipartContent.addPart("SURIDATE",new StringBody(image.getInfo().getreg_date().substring(0, 16), Charset.forName("UTF-8")));
					multipartContent.addPart("JANGBIMYEONG",new StringBody(Common.nullCheck(image.getInfo().getjangbimyeong()), Charset.forName("UTF-8")));
					multipartContent.addPart("DOGONGBEONHO",new StringBody(Common.nullCheck(image.getInfo().getdogongcode()), Charset.forName("UTF-8")));
					multipartContent.addPart("CONTENT",new StringBody(image.getInfo().getsulicontent(), Charset.forName("UTF-8")));
					multipartContent.addPart("IMGAGE",new StringBody(image.getFileName().toString(), Charset.forName("UTF-8")));
					multipartContent.addPart("BUSEOCODE",new StringBody(image.getInfo().getbuseocode(), Charset.forName("UTF-8")));
					multipartContent.addPart("GUBUN",new StringBody(image.getInfo().getGubun(), Charset.forName("UTF-8")));
					multipartContent.addPart("BUPUMCODE",new StringBody(image.getInfo().getBupumcode(), Charset.forName("UTF-8")));
					multipartContent.addPart("SWBEONHO",new StringBody(image.getInfo().getswbeonho(), Charset.forName("UTF-8")));
					multipartContent.addPart("CHECKDATE",new StringBody(image.getInfo().getCheckdate(), Charset.forName("UTF-8")));
					multipartContent.addPart("SENDDATE",new StringBody(common.getCalendarDateYMDHM(), Charset.forName("UTF-8")));
					
					//기간계 연동데이타 용도
					multipartContent.addPart("legacyParam", new StringBody(
							"?SURIL="+URLEncoder.encode(image.getInfo().getreg_date().replace(".", "").substring(0, 8),"UTF-8")+
							"&JANGBICODE="+URLEncoder.encode(Common.nullCheck(image.getInfo().getjangbicode()),"UTF-8")+
							"&SURIDATE="+URLEncoder.encode(image.getInfo().getreg_date().substring(0, 16),"UTF-8")+
							"&JANGBIMYEONG="+URLEncoder.encode(Common.nullCheck(image.getInfo().getjangbimyeong()), "UTF-8")+
							"&DOGONGBEONHO="+URLEncoder.encode(Common.nullCheck(image.getInfo().getdogongcode()),"UTF-8")+
							"&CONTENT="+URLEncoder.encode(image.getInfo().getsulicontent(),"UTF-8")+
							"&IMGAGE="+URLEncoder.encode(image.getFileName().toString(),"UTF-8")+
							"&BUSEOCODE="+URLEncoder.encode(image.getInfo().getbuseocode(),"UTF-8")+
							"&GUBUN="+URLEncoder.encode(image.getInfo().getGubun(),"UTF-8")+
							"&BUPUMCODE="+URLEncoder.encode(image.getInfo().getBupumcode(),"UTF-8")
							//"&CHECKDATE="+URLEncoder.encode(image.getInfo().getCheckdate(),"UTF-8")
							));
					
					httpPost.setEntity(multipartContent);
					
					// 미디어 파일 전송
					sendCheckFile = "VPNFAIL";
					
					HttpResponse response = httpClient.execute(httpPost, httpContext);
					String serverResponse = EntityUtils.toString(response.getEntity());
					
					Log.d(TAG,"doInBackground() - response : "+response);
					Log.d(TAG,"doInBackground() - serverResponse : "+serverResponse.trim());
					
					sendCheckFile = serverResponse.trim();
					Log.d(TAG,"*****************************************************");
					Log.d(TAG, "DoComplecatedJob > doinbackground() result : " + response.toString());
					Log.d(TAG, "DoComplecatedJob > doinbackground() result : " + response.getParams().toString());
					Log.d(TAG, "DoComplecatedJob > doinbackground() result : " + response.getEntity());
					Log.d(TAG, "DoComplecatedJob > doinbackground() result : " + response.getStatusLine().getStatusCode());
					Log.d(TAG, "DoComplecatedJob > doinbackground() : " + sendCheckFile.trim());
					Log.d(TAG,"*****************************************************");
					
					if(response.getStatusLine().getStatusCode() == 200){
						//전송여부 저장
						sendCheckFile = "<resultcode>1000</resultcode>";
						checkDb.updateJBHistorySendYn(image.getFilePath().toString());
					}else{
					}
				} catch (Exception e) {
					e.printStackTrace();
				}// end try~catch
				Log.d(TAG, "################### DoComplecatedJob doInBackground end");	
			}
			
			return null;

			// #endregion
			// --------------------------------------------------------------------------------------------
		}

		@Override
		protected void onCancelled() {
			// --------------------------------------------------------------------------------------------
			// #region 전송화면에서 취소버튼 클릭
			// 중지 flag가 호출된 경우 파일 upload 중단
			if(null != multipartContent){
				multipartContent.stop();
				multipartContent = null;
			}
			// dialogStartup.dismiss();

			Log.d("ProgressUpdate", "onCancelled()~!");
			super.onCancelled();

			// #endregion
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			// #region 전송화면 프로그레스바 업데이트

			pb.setProgress((int) (progress[0]));

			// thread 중지
			if (transData.equals("N")) {
				cancel(true);
				taskCancel();
			}

			if (iPercent == (int) (progress[0])) {

			} else {

				iPercent = (int) (progress[0]);

				if (iProgress == 0) {
					iProgress++;
				}// end if

				for (int i = iProgress; iProgress < iPercent; i++) {
					iProgress++;
				}// end for

				if(iProgress <= 100 ){
					tv.setText(iProgress + " %");
				}
				
				Log.d("ProgressUpdate", "iPercent : " + iPercent);
				Log.d("ProgressUpdate", "iProgress : " + iProgress);
				Log.d("ProgressUpdate", "totalSize : " + totalSize);

				DecimalFormat df = new DecimalFormat("###,###");

				tvTrans.setText(fileCnt+"개");
				tvTot.setText(mFileList.size()+"개");
			}// end if

			// #endregion
		}

		@Override
		protected void onPostExecute(Long result) {
			// #region 첨부파일 전송 완료 후 제보내용 전송 함수 호출

			progressDialog.dismiss();
			multipartContent = null;
			mDialog.dismiss();

			Log.d("ProgressUpdate", "onPostExecute() - transData : " + transData);
			Log.d("ProgressUpdate", "onPostExecute() - result : " + result);
			Log.d("", "onPostExecute() - sendCheckFile : " + sendCheckFile);
			Log.d("", "onPostExecute() - multipartContent : " + multipartContent);
			Log.d("", "onPostExecute() - iPercent : " + iPercent);
			if (multipartContent != null || (iPercent < 100 && iPercent > 0)) {
				Log.d("", "onPostExecute() ---------- 1");	
				Toast.makeText(MainActivity.this, "파일 전송에 실패했습니다.", Toast.LENGTH_LONG).show();
			}else{
				if("VPNFAIL".equals(sendCheckFile)){
					Log.d("onPostExecute", "VPNFAIL - 업무접속 확인해주세요 ");
					AlertDialog.Builder adbLoc	= new AlertDialog.Builder(MainActivity.this);
					adbLoc.setCancelable(false); 
					adbLoc.setTitle("스마트정비관리"); 
					adbLoc.setMessage("업무접속 연결 확인 후 사용하시기 바랍니다."); 
					adbLoc.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					adbLoc.setNegativeButton("업무접속하기", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = getPackageManager().getLaunchIntentForPackage("com.ex.group.folder");
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			                startActivity(intent);
						}
					});
					adbLoc.show();

				}else if("FAIL".equals(sendCheckFile)){
					Toast.makeText(MainActivity.this, "파일 전송에 실패했습니다.\n message:Fail("+sendCheckFile+")", Toast.LENGTH_LONG).show();
				}else if("".equals(Common.nullCheck(sendCheckFile))){
					Toast.makeText(MainActivity.this, "파일 전송에 실패했습니다.\n message:null("+sendCheckFile+")", Toast.LENGTH_LONG).show();
				}else if("<resultcode>1000</resultcode>".equals(sendCheckFile)){
					Toast.makeText(MainActivity.this, "파일 전송 성공", Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(MainActivity.this, "파일 전송에 실패했습니다.\n message:etc("+sendCheckFile+")", Toast.LENGTH_LONG).show();
				}

			}
//			Common.DeleteDir(Common.FILE_DIR);
		}
		public void taskCancel() {
	        if(httpPost!=null){
	            httpPost.abort();
	            httpPost = null;
	        }
	        if(httpClient!=null){
	            httpClient.getConnectionManager().shutdown();
	        }
	        httpClient = null;
	    }
	}

	double totalFileSize = 0.0;

	
	public void executeJob(final Parameters params, Activity mActivity) {
		Log.d(TAG, "******************** executeJob()~! ********************");
		if (ReadSDCard().size() > 0) {
			final DoComplecatedJob task = new DoComplecatedJob(ReadSDCard(), params, mActivity);
			task.execute("5000");

			fileHandler = new Handler();
			fileHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					fileHandler.postDelayed(this, 2000);
					if (task.getStatus() == AsyncTask.Status.FINISHED) {
						fileHandler.removeMessages(0);
					}// end if
				}
			}, 2000);
		}else{
			Toast.makeText(MainActivity.this, R.string.no_data, Toast.LENGTH_SHORT).show();
		}
	}
	

	
//	Common.FILE_DIR
	public List<ImageJgInfo> ReadSDCard() {
		Log.d(TAG, "******************** ReadSDCard()~! ********************");
		Cursor cursor = checkDb.selectSmart_DATAINFO_ONE();
		File[] files = new File[cursor.getCount()];
		
		Log.d(TAG, "ReadSDCard() - cursor.count : "+cursor.getCount());
		List<ImageJgInfo>  trnImageList = new ArrayList<ImageJgInfo>();

		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			ImageJgInfo item = new ImageJgInfo();
			JbInfo item_sub = new JbInfo();

/*			item_sub.setsulicontent(cursor.getString(3));	// 내용  content
			item_sub.setfilename(cursor.getString(4));	// 파일이름
			item_sub.setjangbicode(cursor.getString(5));	// 장비코드
			item_sub.setjangbimyeong(cursor.getString(8));	// 장비명 name
			item_sub.setdogongcode(cursor.getString(9));	// 도공번호
			item_sub.setreg_date(cursor.getString(10));	//정비일시
			item_sub.setswbeonho(cursor.getString(12));	// 사원번호
			item_sub.setbuseocode(cursor.getString(14));	// 부서코드
			item_sub.settagYn(cursor.getString(16));	// 태그여부
			item_sub.setswname(cursor.getString(17));	// 인천지사 안전순찰
*/			
			item_sub.setjangbicode(cursor.getString(5));  // 장비코드
			item_sub.setdogongcode(cursor.getString(9));  // 도공번호
			item_sub.setfilename(cursor.getString(4));  // 파일이름
			item_sub.setreg_date(cursor.getString(10));	//정비일시
			item_sub.setjangbimyeong(cursor.getString(8));  // 장비명 name
			item_sub.setsulicontent(cursor.getString(3));  // 내용  content
			item_sub.setbuseocode(cursor.getString(14));  // 부서코드
			item_sub.setswbeonho(cursor.getString(12));  // 사원번호
			item_sub.settagYn(cursor.getString(15));  // 태그여부
			item_sub.setswname(cursor.getString(16));  // 이름
			item_sub.setCheckdate(cursor.getString(17));  // 체크일자
			
			String jeongbiGB = cursor.getString(0);
			String itemGB = cursor.getString(1);
			Log.d(TAG, "ReadSDCard() - jeongbiGB : "+jeongbiGB);
			Log.d(TAG, "ReadSDCard() - itemGB : "+itemGB);
			item_sub.setGubun(jeongbiGB.substring(jeongbiGB.indexOf("[")+1, jeongbiGB.indexOf("]")));	//정비구분 gubun 추가
			item_sub.setBupumcode(itemGB.substring(itemGB.indexOf("[")+1, itemGB.indexOf("]")));	//수리항목 bupumcode 추가
			
			item.setInfo(item_sub);
			
			File file = new File(cursor.getString(4));
			
			String fileName[] = Common.split(file.getPath().toString(), "/");
			String fileType[] = Common.split(file.getPath().toString(), ".");

			item.setFilePath(file.getPath().toString());
			item.setFileName(fileName[fileName.length - 1]);
			item.setFileType(fileType[fileType.length - 1]);
			item.setFileSize(file.length());
			
			Log.d(TAG, "ReadSDCard() - getAbsolutePath : " + file.getAbsolutePath());
			Log.d(TAG, "ReadSDCard() - fileName : " + fileName[fileName.length - 1]);

			// 파일리스트 총 용량 받아오기 소수점 1자리, 반올림, MB 표현

			double nByte = file.length();
			double mByte = 0;
			double tvMByte = 0;
			String tvTextSize = "";

			mByte = Math.floor(nByte * 100 / (1024 * 1024));
			tvTextSize = String.valueOf(mByte / 100);
			tvMByte = Math.round(mByte / 10);
			tvMByte = tvMByte / 10;
			totalFileSize = totalFileSize + tvMByte;
			trnImageList.add(item);
		}

		Log.d(TAG, "ReadSdCard() - list Size : " + trnImageList.size());
		return trnImageList;
	}
	
}
