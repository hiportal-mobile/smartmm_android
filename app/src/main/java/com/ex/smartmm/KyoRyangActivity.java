package com.ex.smartmm;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
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
import com.ex.smartmm.common.CustomAlertDialog;
import com.ex.smartmm.common.ImageJgInfo;
import com.ex.smartmm.net.CustomMultiPartEntity;
import com.ex.smartmm.net.CustomMultiPartEntity.ProgressListener;
import com.ex.smartmm.net.Parameters;
import com.ex.smartmm.vo.Bjae1;
import com.ex.smartmm.vo.Bsinfo;
import com.ex.smartmm.vo.Girdir_GRBC;
import com.ex.smartmm.vo.ItemGB;
import com.ex.smartmm.vo.JbInfo;
import com.ex.smartmm.vo.JeongbiGB;
import com.ex.smartmm.vo.KrInfo;

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
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.ExifInterface;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
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

@SuppressLint("NewApi")
public class KyoRyangActivity extends BaseActivity implements OnClickListener{
	final String tag = "KyoRyangActivity ";
	
	ImageView btn_JgList;
	
	ImageView btn_X;
	Spinner sp_jakup;
	ImageView sp_jakupB;
	
	//Spinner 종별
	Spinner sp_gubun;
	ImageView sp_jongbyulB;
	
	//Spinner 본부
	Spinner sp_bonbu;
	ImageView sp_bonbuB;
	TextView txt_frontjongbyul;
	
	
	//Spinner 지사
	Spinner sp_jisa;
	ImageView sp_jisaB;
	
	TextView txt_krname;
	EditText ed_krname;
	
	
	//교량 목록 표출 화면
	LinearLayout ll_krlist;
	
	ImageView btn_BoardCamera;
	
	
	List<Bsinfo> bonbuList = new ArrayList<Bsinfo>();
	List<Bsinfo> jisaList = new ArrayList<Bsinfo>();
	List<Girdir_GRBC> girdir_grbc_list = new ArrayList<Girdir_GRBC>();//서버전송 거더,교대,교각,교량받침 데이타.
	
	//부재 스피너
	Spinner sp_bjae1;
	Spinner sp_bjae2;
	Spinner sp_bjae3;
	
	ImageView sp_bjae1B;
	ImageView sp_bjae2B;
	ImageView sp_bjae3B;
	
	ImageView btn_album;
//	ImageView btn_delImage;
	
	EditText ed_write;//부재 3 직접입력 
	
	ListView lv_kr;
	LinearLayout ll_kr;
	/*
	 * 1차부재
	교면포장 ----- 경간
	바닥판 ----- 경간
	거더 ----- 경간
	신축이음장치 ----- 교대,교각
	교량받침 ----- 교대,교각
	하부구조 ----- 교대,교각
	2차부재 ----- 경간
	배수시설 ----- 경간
	난간연석 ----- 경간
	기초 ----- 교대,교각
	기타(code = 0) ----- 경간 교대 교각*/
	List<String> bJae1 = new ArrayList<String>();
	List<String> bJae2 = new ArrayList<String>();
	List<String> bJae3 = new ArrayList<String>();
	
	//keyword 에 의한 교량 목록
	List<KrInfo> krList = new ArrayList<KrInfo>();
	List<JbInfo> jbList = new ArrayList<JbInfo>();
	
/*	//
	List<JeongbiGB> aaaaaaaa;
	List<ItemGB> bbbbbbbb;
	List<ItemGB> cccccccc;
	
	//정비구분, 수리항목, 세부항목 리스트
	List<String> a_____a = new ArrayList<String>();
	List<String> b_____b = new ArrayList<String>();
	List<String> c_____c = new ArrayList<String>();
*/	
	List<JeongbiGB> jeongbiGB_List;
	List<ItemGB> itemGB_List;
	List<ItemGB> detailGB_List;
	
	//정비구분, 수리항목, 세부항목 리스트
	List<String> jeongbiGBList = new ArrayList<String>();
	List<String> itemGBList = new ArrayList<String>();
	List<String> detailGBList = new ArrayList<String>();
	
	
	String SELECTED_BSCODE = "";
	String SELECTED_BSNAME = "";
	String SELECTED_JISACODE = "";
	String SELECTED_JISANAME = "";
	
	String SELECTED_KRCODE = "";
	String SELECTED_KRNAME = "";
	String SELECTED_KRSBBEONHO = "";
	String SELECTED_CONTENT = "";//내용 구성하기.
	String SELECTED_BHGUBUN = "";
	String SELECTED_KGJJGUBUN = "";
	String SELECTED_KGJJBEONHO = "";
	String SELECTED_BIGO = "";
	
	String SELECTED_BJAE1 = "";
	String SELECTED_BJAE2 = "";
	String SELECTED_BJAE3 = "";
	String SELECTED_BJAE1_CODE = "";
	String SELECTED_SBBEONHO = "";

	String SELECTED_JHBEONHO = "";
	String SELECTED_KDKGGUBUN = "";
	String SELECTED_KRBCBHGUBUN = "";
	String SELECTED_KRBCIRBEONHO = "";
	String SELECTED_KRBCSBBEONHO = "";
	String SELECTED_SWBEONHO = "";
	String SELECTED_JGUBUN = "1";//1.점검, 2.보수
	String SELECTED_TAGYN = "";//태그촬영 여부 Y, N
	String SELECTED_SISUL = "kr";;// kr:교량, tn:터널, ag:암거
	
	//스마트정비관리
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
	String SELECTED_CHECK = "";
	
	
	String jisacode =Common.getPrefString(KyoRyangActivity.this, Configuration.SHARED_JISACODE);
	String SELECTED_BUSEOCODE = jisacode;
	
	
	//TextView txt_buseo, txt_jangbi, /*txt_kyogo, txt_younjang,*/ txt_drno,txt_chajong, txt_gyugyeok /*txt_ckgansu*/;
	TextView txt_jangbi, txt_dogong, txt_chajong, txt_drno ;
	
	List<Bjae1> bjae1_list; 
	ImageView btn_sendImage;
	
	ImageView btn_txt_krname, btn_ed_krname;
	//파일전송 핸들러
	Handler fileHandler;
	Common common = new Common(KyoRyangActivity.this);
	String AndroidVersion = "";
	boolean AndroidVersionSetMethod= true;
	
	boolean TAGYN = true;//이미지 태그 사용 여부 저장.
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		Log.d(tag,"onCreate()~!!");
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kyoryang2);
		Intent i = getIntent();
		contextActivity = this;
		AndroidVersion = Build.VERSION.RELEASE;
		if(null != AndroidVersion){
			if(AndroidVersion.length() > 0){
				Log.d("","Android Version Check = "+AndroidVersion.substring(0,3));
				AndroidVersion = AndroidVersion.substring(0,3);
				if(Double.parseDouble(AndroidVersion) <= 4.0){
					AndroidVersionSetMethod = false;	
				}
			}
		}
		
		SELECTED_SWBEONHO = Common.getPrefString(KyoRyangActivity.this, Configuration.SHARED_USERID);
		
		ll_krlist = (LinearLayout) findViewById(R.id.ll_krlist);
		txt_krname = (TextView) findViewById(R.id.txt_krname);
		btn_JgList = (ImageView) findViewById(R.id.btn_JgList);
		btn_X = (ImageView) findViewById(R.id.btn_X);
		sp_jakupB = (ImageView) findViewById(R.id.sp_jakupB);
		sp_jongbyulB = (ImageView) findViewById(R.id.sp_gubunlB);
		btn_BoardCamera = (ImageView) findViewById(R.id.btn_BoardCamera);
//		btn_delImage = (ImageView)findViewById(R.id.btn_delImage);
		btn_sendImage = (ImageView) findViewById(R.id.btn_sendImage);
		btn_txt_krname = (ImageView) findViewById(R.id.btn_txt_krname);
		btn_ed_krname = (ImageView) findViewById(R.id.btn_ed_krname);
		btn_album = (ImageView)findViewById(R.id.btn_album);
		
		
		txt_jangbi = (TextView) findViewById(R.id.txt_jangbi);
		txt_dogong = (TextView) findViewById(R.id.txt_dogong);
		txt_chajong = (TextView) findViewById(R.id.txt_chajong);
		txt_drno = (TextView) findViewById(R.id.txt_drno);
		//txt_buseo = (TextView) findViewById(R.id.txt_buseo);
		//txt_gyugyeok = (TextView) findViewById(R.id.txt_gyugyeok);
//		txt_ckgansu = (TextView) findViewById(R.id.txt_ckgansu);
		txt_frontjongbyul = (TextView) findViewById(R.id.txt_frontjongbyul);
		
		
		
		//부재 Spinner 1,2,3
		sp_bjae1 = (Spinner) findViewById(R.id.sp_bjae1);
		sp_bjae2 = (Spinner) findViewById(R.id.sp_bjae2);
		sp_bjae3 = (Spinner) findViewById(R.id.sp_bjae3);
		sp_bjae1B = (ImageView) findViewById(R.id.sp_bjae1B);
		sp_bjae2B = (ImageView) findViewById(R.id.sp_bjae2B);
		sp_bjae3B = (ImageView) findViewById(R.id.sp_bjae3B);
		sp_bjae1B.setOnClickListener(this);
		sp_bjae2B.setOnClickListener(this);
		sp_bjae3B.setOnClickListener(this);
		
		sp_bjae1.setPrompt("정비구분");
		sp_bjae2.setPrompt("수리항목");
		sp_bjae3.setPrompt("세부항목");
		
		ed_krname = (EditText) findViewById(R.id.ed_krname);
		ed_write = (EditText) findViewById(R.id.ed_write);
		
		lv_kr = (ListView) findViewById(R.id.lv_kr);
		ll_kr = (LinearLayout) findViewById(R.id.ll_kr);
//		ll_kr.setBackground(getResources().getDrawable(R.drawable.border_style_ll_white));
		
		//본부지사 선택
		sp_bonbu = (Spinner) findViewById(R.id.sp_bonbu);
		sp_bonbuB = (ImageView) findViewById(R.id.sp_bonbuB);
		sp_jisa = (Spinner) findViewById(R.id.sp_jisa);
		sp_jisaB = (ImageView) findViewById(R.id.sp_jisaB);
		
		ll_krlist.setOnClickListener(this);
		txt_krname.setOnClickListener(this);
		btn_BoardCamera.setOnClickListener(this);
		btn_JgList.setOnClickListener(this);
		btn_X.setOnClickListener(this);
		sp_jakupB.setOnClickListener(this);
//		btn_delImage.setOnClickListener(this);
		sp_bonbuB.setOnClickListener(this);
		sp_jisaB.setOnClickListener(this);
		btn_sendImage.setOnClickListener(this);
		sp_jongbyulB.setOnClickListener(this);
		txt_frontjongbyul.setOnClickListener(this);
		btn_ed_krname.setOnClickListener(this);
		btn_txt_krname.setOnClickListener(this);
		
		btn_album.setOnClickListener(this);

		Log.d("","oncreate - jisacode : " +jisacode);
		
		//setSpinnerBonbuInfo();
		setSpinnerJeongBi();
		//setSpinnerJongByul();//종별 선택 박스
		//setSpinnerBonbuInfo();//본부 선택 박스
		//setSpinner();//점검 보수 선택 박스
		setSpinnerJeongBiGB();
		setSpinnerItemGB();
		setSpinnerDetailGB("");
		
//		startGps();
		
		
		
		File fileDir = new File(Common.FILE_DIR);
		if (!fileDir.exists()){
//			fileDir.mkdir();
			fileDir.mkdirs();
		}

		Log.d("","fileDir exist = "+Common.FILE_DIR+":"+ fileDir.exists());
		ll_krlist.setVisibility(View.VISIBLE);
		if("N".equals(Common.getPrefString(KyoRyangActivity.this, Configuration.SHARED_ISVPN))){
//			btn_sendImage.setEnabled(false);	
		}
		
		ed_krname.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				Log.d("","setEdKrname - onTextChanged " +txt_krname.getText().toString()+":"+ed_krname.getText().toString());
				
				if(!txt_krname.getText().toString().equals(ed_krname.getText().toString())){
					//txt_buseo.setText("");
					txt_jangbi.setText("");
					txt_drno.setText("");
					txt_chajong.setText("");
					txt_dogong.setText("");
					//txt_gyugyeok.setText("");
				//	txt_younjang.setText("");
				//	txt_kyogo.setText("");
				//	txt_ckgansu.setText("");
					SELECTED_KRCODE = "";
					SELECTED_KRNAME = "";
				}
				
				Log.d("","setEdKrname - txt_krname :  " +txt_krname.getText().toString());
				Log.d("","setEdKrname - ed_krname :  " +ed_krname.getText().toString());
				
			}
			
			
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {
				if( null == s){
					Log.d("","TextChangedListener afterTextChanged Editable is null");
				}
				System.out.println("TEXTCHANGED TIME CHECK start  = " + System.currentTimeMillis());
				txt_krname.setText(ed_krname.getText().toString());
				
				getjbList(false);
				System.out.println("TEXTCHANGED TIME CHECK end  = " + System.currentTimeMillis());
				//getKrList(false);
			}
		});
		
	}
		
	@Override
	protected void onResume() {
		super.onResume();
		
		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}
	@Override
	protected void onPause() {
		super.onPause();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopGPS();
		Log.d("","KyoryangActivity onDestroy called!");
	}
	
	//교량 선택 박스
	public void setEdKrname(){
		Log.d(tag,"setEdKrname()~!!");
		
	}
	
	public void getKrList(boolean locationGiban){
		Log.d("","getKrList() time start " + System.currentTimeMillis());
		String searchKeyword = ed_krname.getText().toString();
		Log.d("","getKrList searchKeyword = " + searchKeyword);
		if( null == db){
			Log.d("","getKrList null check db is null");
		}
		if(searchKeyword == null){
			Log.d("","getKrList null check searchkeyword is null");
		}
		if(sp_gubun == null){
			Log.d("","getKrList null check sp_jongbyul is null");
		}
		if(sp_jisa == null){
			Log.d("","getKrList null check sp_jisa is null");
		}
		Log.d("","getKrList searchKeyword test = " + searchKeyword);
		Cursor cursor;
		
		//cursor = db.select_jangbi();
		
		Log.d("","getKrList locationGiban = " + locationGiban);
		if(locationGiban){
			cursor = db.selectBms_Krgb_Ijeong(sp_gubun.getSelectedItem().toString(), SELECTED_JISANAME,ns_code,  currentIjung, ns_code2, currentIjung2);
		}else{
			//cursor = db.select_jangbi();
			cursor = db.selectBms_Krgb(searchKeyword, 
							sp_gubun.getSelectedItem().toString(),
							SELECTED_JISANAME);
		}
		Log.d("","getKrList searchKeyword test2 = " + searchKeyword);
		Log.d("","getKrList searchKeyword test2 = " + sp_gubun.getSelectedItem().toString());
		Log.d("","getKrList searchKeyword test2 = " + SELECTED_JISANAME);
		Log.d("","getKrList searchKeyword test2 - cursor = " + cursor);
		if(null != cursor){
			krList.clear();
			KrInfo item;
			while (cursor.moveToNext()) {
				item = new KrInfo();
				item.setKrcode(cursor.getString(0));
				item.setKrname1(cursor.getString(1));
				item.setKrsbbeonho(cursor.getString(2));
				item.setNsname(cursor.getString(3));
				item.setEjung(cursor.getString(4));
				krList.add(item);
			}
			Log.d("","krList    " + krList.size());
			lv_kr.setAdapter(new KrListAdapter(KyoRyangActivity.this));
		}
		
		
		
		
		lv_kr.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				ll_krlist.setVisibility(View.GONE);
//				ed_krname.setText(krList.get(position).getKrname1());
				txt_krname.setText(krList.get(position).getKrname1());
				SELECTED_KRCODE = krList.get(position).getKrcode();
				SELECTED_KRNAME = krList.get(position).getKrname1();
				SELECTED_KRSBBEONHO = krList.get(position).getKrsbbeonho();
				Cursor cursor = db.selectBms_Krgb_Detail(SELECTED_KRCODE, SELECTED_KRSBBEONHO);
				if(cursor != null){
					while (cursor.moveToNext()) {
						String krcode = cursor.getString(0);//교량코드
						String krname1 = cursor.getString(1);//교량명
						String krsbbeonho = cursor.getString(2);//교량세부번호
						String nscode = cursor.getString(3);//노선코드
						String ejung = cursor.getString(4);//이정
						String nsname = cursor.getString(5);//노선명
						String kyogo = cursor.getString(6);//높이
						String younjang = cursor.getString(7);//연장
						String saddress = cursor.getString(8);//주소
						String sbgjhyungsik1 = cursor.getString(9);//상부구조형식
						String scemhyungsik = cursor.getString(10);//신축이음형식
						String ckgansu = cursor.getString(11);//경간수
						
						//txt_buseo.setText(nsname);
						txt_jangbi.setText(ejung+" km");
					//	txt_kyogo.setText(kyogo+" m");
					//	txt_younjang.setText(younjang+" m");
						txt_drno.setText(saddress);
						txt_chajong.setText(db.selectBms_Code_sbgjhyungsikName(sbgjhyungsik1));
						//txt_gyugyeok.setText(scemhyungsik);
					//	txt_ckgansu.setText(ckgansu+"개");
						break;
					}
				}
				
			}
		});
		Log.d("","getKrList() time end = " + System.currentTimeMillis());
	}
	
	private class KrListAdapter extends BaseAdapter{
		
		LayoutInflater inflater;
		
		public KrListAdapter(Context context) {
			
			super();
			inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		@Override
		public int getCount() {
			return krList.size();
		}

		@Override
		public KrInfo getItem(int position) {
			return krList.get(position);
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
			TextView item_jangbi = (TextView) view.findViewById(R.id.item_jangbi);
			TextView item_dogong = (TextView) view.findViewById(R.id.item_dogong);
			TextView item_drno = (TextView) view.findViewById(R.id.item_drno);
			item_jangbi.setText(getItem(position).getKrname1() );
			item_dogong.setText( getItem(position).getNsname());
			item_drno.setText(getItem(position).getEjung()+"km");
			Log.d("KyoRyangActivity","KyoRyangActivity KrListAdapter getView "+position+":"+getItem(position).getKrname1());
			
			return view;
		}
	}
	
	
	
	
	
	/*********** 스마트정비관리 ***********/	
	
	public void getjbList(boolean locationGiban){
//		Log.d("","getjbList()");
		System.out.println("TEXTCHANGED TIME CHECK 1 = " + System.currentTimeMillis());
		String searchKeyword = ed_krname.getText().toString();
		Cursor cursor = null;
		System.out.println("TEXTCHANGED TIME CHECK 2 = " + System.currentTimeMillis());
		if("도공번호".equals(sp_gubun.getSelectedItem().toString())){
			cursor = db.select_jeongbi(txt_krname.getText().toString(), "",jisacode);
		}else if("장비명".equals(sp_gubun.getSelectedItem().toString())){
			cursor = db.select_jeongbi("", txt_krname.getText().toString(), jisacode);
		}else{
			Log.d("","getjbList() - select_jeongbi() 도공/장비명/구분 : "+sp_gubun.getSelectedItem().toString());
			cursor = db.select_jeongbi(txt_krname.getText().toString(), txt_krname.getText().toString(), jisacode);
		}
		System.out.println("TEXTCHANGED TIME CHECK 3 = " + System.currentTimeMillis());
//			cursor = db.select_jeongbi("","");
			/*cursor = db.selectBms_Krgb(searchKeyword, 
							sp_jongbyul.getSelectedItem().toString(),
							SELECTED_JISANAME);*/
//		cursor = db.select_jeongbi(txt_krname.getText().toString(), "",jisacode);
		System.out.println("TEXTCHANGED TIME CHECK 4 = " + System.currentTimeMillis());
		if(null != cursor){
			Log.d("","jbList1 :   " + jbList.toString());
			Log.d("","jbList1 :   " + jbList.size());
			jbList.clear();
			JbInfo item;
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
				item.setbuseocode(cursor.getString(8));
				item.setbuseoname(cursor.getString(7));
				item.setcode(cursor.getString(9));
				item.setcodegubun(cursor.getString(10));
				jbList.add(item);
				
				Log.d("","getjbList jangbicode : "+cursor.getString(1));
				Log.d("","getjbList jangbimyeong: "+cursor.getString(2));
				Log.d("","getjbList dogongcode : "+cursor.getString(3));
				Log.d("","getjbList deungrokno : "+cursor.getString(5));
				
			}
			Log.d("","jbList2 size :   " + jbList.size());
			lv_kr.setAdapter(new jbListAdapter(KyoRyangActivity.this));
		}
		System.out.println("TEXTCHANGED TIME CHECK 5 = " + System.currentTimeMillis());
		lv_kr.setOnItemClickListener(new OnItemClickListener() {
			

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				ll_krlist.setVisibility(View.GONE);
				if("도공번호".equals(sp_gubun.getSelectedItem().toString())){
					//txt_krname.setInputType(InputType.TYPE_CLASS_NUMBER);
					txt_krname.setText(jbList.get(position).getdogongcode());	
				}else if("장비명".equals(sp_gubun.getSelectedItem().toString())){
					txt_krname.setText(jbList.get(position).getjangbimyeong());
				}else{
					txt_krname.setText(jbList.get(position).getdogongcode());
				}
				//txt_krname.setText(jbList.get(position).getjangbimyeong());
				SELECTED_CODE = jbList.get(position).getcode();
				SELECTED_KRCODE = jbList.get(position).getjangbicode();				
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
				
				//Cursor cursor = db.select_jeongbi_detail(SELECTED_JBCODE, SELECTED_CODE, SELECTED_CODEGUBUN);
				Cursor cursor = db.select_jeongbi_detail(SELECTED_JBCODE);
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
						String buseocode = cursor.getString(8);//부서코드
						String buseoname = cursor.getString(9);//부서명
						
						//txt_buseo.setText(pibuseomyeong);
						txt_jangbi.setText(jangbimyeong);
						txt_dogong.setText(dogongcode);
						txt_chajong.setText(chajongmyeong);
						txt_drno.setText(deungrokno);
						
						txt_jangbi.setTextSize(16);
						txt_dogong.setTextSize(16);
						txt_chajong.setTextSize(16);
						txt_drno.setTextSize(16);
						//txt_gyugyeok.setText(gyugyeok);
						//SELECTED_DOGONG = dogongcode;
						//Log.d("","getjbList SELECTED_DOGONG2 : "+dogongcode);
						Log.d("","getjbList chajongmyeong : "+chajongmyeong);
						Log.d("","getjbList jangbicode : "+jangbicode);
						Log.d("","getjbList dogongcode : "+dogongcode);
						Log.d("","getjbList chajongcode : "+chajongcode);
						Log.d("","getjbList deungrokno : "+deungrokno);
						Log.d("","getjbList gyugyeok : "+gyugyeok);
						Log.d("","getjbList pibuseomyeong : "+pibuseomyeong);
						Log.d("","getjbList buseocode : "+buseocode);
						Log.d("","getjbList buseoname : "+buseoname);
						//Log.d("","getjbList chajongmyeong : "+chajongmyeong);
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
			TextView item_drno = (TextView) view.findViewById(R.id.item_drno);  // 등록번호
			item_jangbi.setText(getItem(position).getjangbimyeong() );
			item_dogong.setText( getItem(position).getdogongcode());
			item_drno.setText(getItem(position).getdeungrokno());
			Log.d("","jbListAdapter getView - item_jangbi : "+position+":"+getItem(position).getjangbicode());
			Log.d("","jbListAdapter getView - item_dogong : "+position+":"+getItem(position).getdogongcode());
			Log.d("","jbListAdapter getView - item_drno : "+position+":"+getItem(position).getdeungrokno());
			
			Log.d("KyoRyangActivity","KyoRyangActivity jbListAdapter getView "+position+":"+getItem(position).getjangbicode());
		
			return view;
		}
	}
	
	
	/**
	 * 도공번호/장비명 선택 박스
	 */
	@SuppressLint("NewApi")
	private void setSpinnerJeongBi(){
		Log.d("","setSpinnerJeongBi()~!!");
		//Log.d("","setSpinnerJeongBi() - 지사코드 : "+jisacode);
		List<String> spinItems = new ArrayList<String>();
		Cursor cursor = db.select_jeongbi("","", jisacode);
		spinItems.add("도공번호");
		spinItems.add("장비명");
		//spinItems.add("구분");
		Log.d("","setSpinnerJangBi cursor count = " + cursor.getCount());

			sp_gubun = (Spinner) findViewById(R.id.sp_gubun);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(KyoRyangActivity.this, R.layout.spinner_item, spinItems){
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
//			adapter.setDropDownViewResource(R.layout.spinner_item);
			if(AndroidVersionSetMethod){
				sp_gubun.setDropDownVerticalOffset(8);	
			}
			
			sp_gubun.setAdapter(adapter);
			Log.d("","setSpinnerJeongBi - spinItems :" + spinItems);
			
			sp_gubun.setOnItemSelectedListener(new OnItemSelectedListener() {
				
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Log.d("","setSpinnerJeongBi() - onItemSelected");
				Log.d("","setSpinnerJeongBi() - position check ======= " + position);

				if("도공번호".equals(sp_gubun.getSelectedItem().toString())){
					ed_krname.setInputType(InputType.TYPE_CLASS_NUMBER);
				}else{
					ed_krname.setInputType(InputType.TYPE_CLASS_TEXT);	
				}
				
				setEdKrname();
				//setSpinnerJisaInfo(position);
				txt_krname.setText("");
				ed_krname.setText("");
				//getKrList(false);
				getjbList(false);
				txt_krname.performClick();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
	
	
	/** 정비구분 스피너 **/
	private void setSpinnerJeongBiGB(){
		Log.d("","setSpinnerJeongBiGB()");

		jeongbiGBList.clear();
		Cursor cursor = db.select_jeongbiGB("");
		jeongbiGB_List = new ArrayList<JeongbiGB>();
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			JeongbiGB item = new JeongbiGB();
			item.setCode(cursor.getString(0));
			item.setGubun(cursor.getString(1));
			jeongbiGB_List.add(item);
			jeongbiGBList.add(cursor.getString(1));
			
		}
		/*jeongbiGBList.add("자체");
		jeongbiGBList.add("외주");
		jeongbiGBList.add("정비구분");*/
		jeongbiGBList.add("정비구분");
		
		Log.d("","setSpinnerJeongBiGB - jeongbiGB_List ======= " + jeongbiGB_List.toString());
		Log.d("","setSpinnerJeongBiGB - jeongbiGBList ======= " + jeongbiGBList);
		sp_bjae1 = (Spinner) findViewById(R.id.sp_bjae1);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(KyoRyangActivity.this, android.R.layout.simple_spinner_item){
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
		//adapter.addAll(spinItems);
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_bjae1.setAdapter(adapter);
		sp_bjae1.setSelection(adapter.getCount()); 
		sp_bjae1.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
					Log.d("","setSpinnerJeongBiGB - position check ======= " + position +":"+jeongbiGBList.size());
					SELECTED_BJAE1 = jeongbiGBList.get(position).toString();
					SELECTED_JEONGBIGUBUN = jeongbiGBList.get(position).toString();
					Log.d("","setSpinnerJeongBiGB - SELECTED_BJAE1 :  " +SELECTED_BJAE1);
					Log.d("","setSpinnerJeongBiGB - SELECTED_JEONGBIGUBUN :  " +SELECTED_JEONGBIGUBUN);
					A(SELECTED_JEONGBIGUBUN);
					
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	public void A(String gubun){
		Log.d("Retrieve : ", gubun);
		if(!gubun.equals("정비구분")){
			Cursor cursor = db.select_jeongbiGB(gubun);
			
			Log.d("Retrieve : ", cursor.getString(0));
			
			SELECTED_JEONGBIGUBUNCODE = cursor.getString(0);
		}
		/*if(gubun.equals("자체")){
			SELECTED_JEONGBIGUBUNCODE = "1";
		}else if(gubun.equals("외주")){
			SELECTED_JEONGBIGUBUNCODE = "2";
		}else if(gubun.equals("하자")){
			SELECTED_JEONGBIGUBUNCODE = "3";
		}else if(gubun.equals("저장품")){
			SELECTED_JEONGBIGUBUNCODE = "4";
		}*/
		
		Log.d("","setSpinnerJeongBiGB - SELECTED_JEONGBIGUBUNCODE :  " +SELECTED_JEONGBIGUBUNCODE);
	}
	/* 수리항목 스피너 
	 * */
	private void setSpinnerItemGB(){
		Log.d("","setSpinnerItemGB()");
		itemGBList.clear();
		Cursor cursor = db.select_itemGB("");
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
		Log.d("","setSpinnerItemGB - itemGBList ======= " + itemGBList);
		Log.d("","setSpinnerItemGB - itemGB_List ======= " + itemGB_List);
		sp_bjae2 = (Spinner) findViewById(R.id.sp_bjae2);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(KyoRyangActivity.this,  android.R.layout.simple_spinner_item){
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
		
		Log.d("","itemGBList = " + itemGBList);
		adapter.addAll(itemGBList);
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_bjae2.setAdapter(adapter);
		sp_bjae2.setSelection(adapter.getCount()); 
		sp_bjae2.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				//if(position != 0){
					Log.d("","setSpinnerItemGB - position check ======= " + position +":"+itemGBList.size());
					SELECTED_BJAE2 = sp_bjae2.getSelectedItem().toString();
					SELECTED_SULIITEM = sp_bjae2.getSelectedItem().toString();
					Log.d("","setSpinnerItemGB - SELECTED_BJAE2" + SELECTED_BJAE2);
					setSpinnerDetailGB(sp_bjae2.getSelectedItem().toString());
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
		Log.d("","setSpinnerDetailGB() cccccc = "+suliitem);
		
		if(!suliitem.equals("수리항목")){
			Cursor cursor = db.select_itemGB(suliitem);
			SELECTED_SULIITEMCODE = cursor.getString(2);
			Log.d("", "setSpinnerDetailGB - SELECTED_SULIITEMCODE : "+SELECTED_SULIITEMCODE);
		}
		
		
		//1. 원동기
		if("원동기".equals(suliitem)){
			detailGBList.clear();
			Cursor cursor = db.select_detailGB("원동기");
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
			Cursor cursor = db.select_detailGB("동력전달장치");
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
			Cursor cursor = db.select_detailGB("조향장치");
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
			Cursor cursor = db.select_detailGB("제동장치");
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
			Cursor cursor = db.select_detailGB("주행장치");
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
			Cursor cursor = db.select_detailGB("완충장치");
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
			Cursor cursor = db.select_detailGB("전기전자장치");
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
			Cursor cursor = db.select_detailGB("유공압장치");
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
			Cursor cursor = db.select_detailGB("차체");
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
			Cursor cursor = db.select_detailGB("작업장치");
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
			Cursor cursor = db.select_detailGB("정비용품");
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
			Cursor cursor = db.select_detailGB("기타");
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
			Cursor cursor = db.select_detailGB("");
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
			Log.d("","setSpinnerDetailGB - detailGBList ======= " + detailGBList);
			Log.d("","setSpinnerDetailGB - detailGB_List ======= " + detailGB_List);
			sp_bjae3 = (Spinner) findViewById(R.id.sp_bjae3);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(KyoRyangActivity.this, android.R.layout.simple_spinner_item){
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
			sp_bjae3.setAdapter(adapter);
			sp_bjae3.setSelection(adapter.getCount()); 
		}else{
		
		detailGBList.add("세부항목");
		Log.d("","setSpinnerDetailGB - detailGBList ======= " + detailGBList);
		Log.d("","setSpinnerDetailGB - detailGB_List ======= " + detailGB_List);
		sp_bjae3 = (Spinner) findViewById(R.id.sp_bjae3);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(KyoRyangActivity.this, android.R.layout.simple_spinner_item){
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
		sp_bjae3.setAdapter(adapter);
		sp_bjae3.setSelection(adapter.getCount()); 
		sp_bjae3.setSelection(0);
		}
		sp_bjae3.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
					Log.d("","setSpinnerDetailGB - position check ======= " + position +":"+detailGB_List.size());
					SELECTED_BJAE3 = sp_bjae3.getSelectedItem().toString();
					SELECTED_DETAILITEM = sp_bjae3.getSelectedItem().toString();
					Log.d("","setSpinnerDetailGB - SELECTED_BJAE3 : " + SELECTED_BJAE3);
					
					SELECTED_BJAE3 = sp_bjae3.getSelectedItem().toString();
					if("기타".equals(SELECTED_BJAE3)){
						ed_write.setVisibility(View.VISIBLE);
						sp_bjae3B.setVisibility(View.GONE);
						ed_write.requestFocus();
						ed_write.setText("");
						ed_write.setSelection(ed_write.getText().length());
						SELECTED_DETAILITEM = ed_write.getText().toString();
						Log.d("","setSpinnerDetailGB - SELECTED_BJAE3 - ed_write : " + SELECTED_BJAE3);
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
	
	/**부재 선택 박스
	 * 1차부재 셋팅 
	 */
	public void spinSetBJae1(String jakup){
		bJae1.clear();
//		if("보수".equals(jakup)){//나중에 보수 바꿔달라 그럴까봐 남겨둠.
		if("".equals(jakup)){
			bJae1.add("부재 선택");
			Cursor cursor = db.selectBms_Code_Bjae("8");
			bjae1_list = new ArrayList<Bjae1>();
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				Bjae1 item = new Bjae1();
				item.setBjaecode(cursor.getString(0));
				item.setBjaename(cursor.getString(1));
				if(!"기타".equals(cursor.getString(1))){
					bjae1_list.add(item);
					bJae1.add(cursor.getString(1));
				}
				
			}
		}else{
			bJae1.add("부재 선택");
			Cursor cursor = db.selectBms_Code_Bjae("7");
			
			bjae1_list = new ArrayList<Bjae1>();
			
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				Bjae1 item = new Bjae1();
				String code = "";
				if(Common.nullCheck(cursor.getString(0)).length() == 1){
					code = "0"+cursor.getString(0);
				}else{
					code = cursor.getString(0);
				}
				item.setBjaecode(code);
				item.setBjaename(cursor.getString(1));
				if(!"기타".equals(cursor.getString(1))){
					bjae1_list.add(item);
					bJae1.add(cursor.getString(1));
				}
				
			}
//			bJae1.add("교면포장");//부재2 경간
//			bJae1.add("바닥판");//부재2 경간
//			bJae1.add("거더");//부재2 경간
//			bJae1.add("교량받침");//부재2 교대,교각
//			bJae1.add("하부구조");//부재2 교대,교각
//			bJae1.add("2차부재");//부재2 경간
//			bJae1.add("배수시설");//부재2 경간
//			bJae1.add("난간연석");//부재2 경간
//			bJae1.add("기초");//부재2 교대,교각
//			bJae1.add("기타");//부재2 경간,교대,교각
		}
		
		sp_bjae1 = (Spinner) findViewById(R.id.sp_bjae1);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(KyoRyangActivity.this, R.layout.spinner_item, bJae1){
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
		sp_bjae1.setAdapter(adapter);
		sp_bjae1.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				if(position != 0){
					Log.d("","position check ======= " + position +":"+bjae1_list.size());
					SELECTED_BJAE1_CODE = bjae1_list.get(position-1).getBjaecode();
					Log.d("","position check ======= " +SELECTED_BJAE1_CODE);
					SELECTED_BJAE1 = sp_bjae1.getSelectedItem().toString();
					spinSetBJae2(sp_bjae1.getSelectedItem().toString());
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
			
		});
		
	}
	
	
	
	
	/**
	 * 2차부재 셋팅 
	 */
	public void spinSetBJae2(final String selectedBjae1){
		
		if("공통".equals(selectedBjae1)){
			girdir_grbc_list.clear();
			bJae2.clear();
			bJae2.add("위치 선택");

			Cursor cursor = db.selectBms_Code_Bjae("7");
			
			bjae1_list = new ArrayList<Bjae1>();
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				if(!"기타".equals(cursor.getString(1))){
					bJae2.add(cursor.getString(1));
				}
				Girdir_GRBC item = new Girdir_GRBC();
				girdir_grbc_list.add(item);
				
			}
			
		}else{
			girdir_grbc_list.clear();	
			Log.d("","spinSetBJae2 parameter = " + selectedBjae1);
			Cursor cursor;
			Cursor cursor_girdir;
			bJae2.clear();
			bJae2.add("위치 선택");
			if("거더".equals(selectedBjae1)){
				cursor = db.selectBms_Krgb_CKGANSU(SELECTED_KRCODE ,SELECTED_KRSBBEONHO);//고매교
				cursor_girdir = db.selectBms_Krgb_GIRDIR(SELECTED_KRCODE ,SELECTED_KRSBBEONHO);//고매교
				List<Integer> ckgansu_list = new ArrayList<Integer>();
				int ckgansu = 0;
				while (cursor.moveToNext()) {
					ckgansu = Integer.parseInt(cursor.getString(0));
					ckgansu_list.add(ckgansu);
				}
				
				List<Integer> ckgirdir_list = new ArrayList<Integer>();
				Log.d("","cursor_girdir = " +cursor.getCount());
				Log.d("","cursor_girdir = " +cursor_girdir.getCount());
				int ckgirdir = 0;
				while (cursor_girdir.moveToNext()) {
					ckgirdir = Integer.parseInt(cursor_girdir.getString(0));
					ckgirdir_list.add(ckgirdir);
				}
				DecimalFormat decimalFormat_Three = new DecimalFormat("000");
				DecimalFormat decimalFormat_Two = new DecimalFormat("00");
				for (int i = 0; i < ckgansu_list.size(); i++) {
					if(ckgirdir_list.get(i)>0){
						for (int j = 0; j < ckgirdir_list.get(i); j++) {
							bJae2.add("경간"+(i+1) + "-거더"+(j+1));
							Girdir_GRBC item = new Girdir_GRBC();
							item.setJHBEONHO(decimalFormat_Two.format(j+1));
							item.setKGJJGUBUN(decimalFormat_Three.format(i+1));
							item.setKGJJBEONHO(decimalFormat_Two.format(j+1));
							
							girdir_grbc_list.add(item);
						}				
					}else{
						bJae2.add("경간"+(i+1));
						Girdir_GRBC item = new Girdir_GRBC();
						item.setKGJJGUBUN(decimalFormat_Three.format(i+1));
						item.setKGJJBEONHO(decimalFormat_Three.format(i+1));
						girdir_grbc_list.add(item);
					}
				}
			}else if("교면포장".equals(selectedBjae1)||"바닥판".equals(selectedBjae1)
					||"2차부재".equals(selectedBjae1)||"배수시설".equals(selectedBjae1)
					||"난간연석".equals(selectedBjae1)||"신축이음장치".equals(selectedBjae1)){//경간
				
				cursor = db.selectBms_Krgb_CKGANSU(SELECTED_KRCODE ,SELECTED_KRSBBEONHO);//고매교
				cursor_girdir = db.selectBms_Krgb_GIRDIR(SELECTED_KRCODE ,SELECTED_KRSBBEONHO);//고매교
				List<Integer> ckgansu_list = new ArrayList<Integer>();
				int ckgansu = 0;
				while (cursor.moveToNext()) {
					ckgansu = Integer.parseInt(cursor.getString(0));
					ckgansu_list.add(ckgansu);
				}
				
				List<Integer> ckgirdir_list = new ArrayList<Integer>();
				int ckgirdir = 0;
				while (cursor_girdir.moveToNext()) {
					ckgirdir = Integer.parseInt(cursor_girdir.getString(0));
					ckgirdir_list.add(ckgirdir);
				}
				
				DecimalFormat decimalFormat_Three = new DecimalFormat("000");
				DecimalFormat decimalFormat_Two = new DecimalFormat("00");
				for (int i = 0; i < ckgansu_list.size(); i++) {
					bJae2.add("경간"+(i+1));
					Girdir_GRBC item = new Girdir_GRBC();
					item.setKGJJBEONHO(decimalFormat_Three.format(i+1));
					girdir_grbc_list.add(item);
				}
				
			}else if("하부구조".equals(selectedBjae1)||"기초".equals(selectedBjae1)){//교대,교각
				cursor = db.selectBms_Krgb_JJGUBUN(SELECTED_KRCODE ,SELECTED_KRSBBEONHO);//고매교
				int kyogakCnt = 0;
				DecimalFormat decimalFormat_Three = new DecimalFormat("000");
				for (int i = 0; i < cursor.getCount(); i++) {
					Girdir_GRBC item = new Girdir_GRBC();
					
					cursor.moveToPosition(i);
					if("0".equals(cursor.getString(0))){
						bJae2.add("교대1");
						item.setKDKGGUBUN("0");
					}else if("1".equals(cursor.getString(0))){
						kyogakCnt++;
						bJae2.add("교각"+(kyogakCnt));
						item.setKDKGGUBUN("1");
					}else if("2".equals(cursor.getString(0))){
						bJae2.add("교대2");
						item.setKDKGGUBUN("2");
					}
					item.setKGJJBEONHO(cursor.getString(3));
					Log.d("","cursor하부구조 kgjjbeonho = " + cursor.getString(3));
					girdir_grbc_list.add(item);
				}
			}else if("교량받침".equals(selectedBjae1)){//교대,교각
				cursor = db.selectBms_Krgb_JJGUBUN(SELECTED_KRCODE ,SELECTED_KRSBBEONHO);//고매교
				int kyogakCnt = 0;
				DecimalFormat decimalFormat_Two = new DecimalFormat("00");
				DecimalFormat decimalFormat_Three = new DecimalFormat("000");
				
				String kdkggubun = "";
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					if("0".equals(cursor.getString(0))){
						kdkggubun = "0";
					}else if("1".equals(cursor.getString(0))){
						kyogakCnt++;
						kdkggubun = "1";
					}else if("2".equals(cursor.getString(0))){
						kdkggubun = "2";
					}
					
					int jjgubun = Integer.parseInt(cursor.getString(0));
					int krbcgaesu = Integer.parseInt(cursor.getString(1));
					int bigo = 0;
					try {
						bigo = Integer.parseInt(cursor.getString(2));
					} catch (Exception e) {
						e.printStackTrace();
						bigo = 2;
					}
					 
					String kgjjbeonho = cursor.getString(3);
					if(bigo == 1){//단방향
						if(0 == jjgubun){
							int bcbeonhoCnt = 1;
							for (int j = 0; j < krbcgaesu; j++) {
								
								if((j+1)%2 != 0){
									Girdir_GRBC item = new Girdir_GRBC();
									item.setKRBCBHGUBUN(bigo+"");
									item.setKGJJGUBUN(jjgubun+"");
									item.setKRBCIRBEONHO(decimalFormat_Three.format(j));
									item.setKRBCSBBEONHO(decimalFormat_Two.format(bcbeonhoCnt));
									item.setKDKGGUBUN(kdkggubun);
									item.setKGJJBEONHO(kgjjbeonho);

									girdir_grbc_list.add(item);
									
//									bJae2.add("교대001"+"-"+"지점번호"+kgjjbeonho+"-"+"교량받침"+decimalFormat_Three.format(j+1) +"-"+"방향구분A1"+"-"+"받침번호"+decimalFormat_Two.format(bcbeonhoCnt));
//									bJae2.add("교대001"+"-"+"지점번호"+kgjjbeonho+"-"+"교량받침"+decimalFormat_Three.format(j+1) +"-"+"받침번호"+decimalFormat_Two.format(bcbeonhoCnt));
									bJae2.add("교대1"+"-"+"교량받침"+(j+1) +"-"+"받침번호"+(bcbeonhoCnt));
								}else{
									Girdir_GRBC item = new Girdir_GRBC();
									item.setKRBCBHGUBUN(bigo+"");
									item.setKGJJGUBUN(jjgubun+"");
									item.setKRBCIRBEONHO(decimalFormat_Three.format(j));
									item.setKRBCSBBEONHO(decimalFormat_Two.format(bcbeonhoCnt));
									item.setKDKGGUBUN(kdkggubun);
									item.setKGJJBEONHO(kgjjbeonho);
									girdir_grbc_list.add(item);
									
//									bJae2.add("교대001"+"-"+"지점번호"+kgjjbeonho+"-"+"교량받침"+decimalFormat_Three.format(j+1) +"-"+"방향구분A2"+"-"+"받침번호"+decimalFormat_Two.format(bcbeonhoCnt));
//									bJae2.add("교대001"+"-"+"지점번호"+kgjjbeonho+"-"+"교량받침"+decimalFormat_Three.format(j+1) +"-"+"받침번호"+decimalFormat_Two.format(bcbeonhoCnt));
									bJae2.add("교대1"+"-"+"교량받침"+(j+1) +"-"+"받침번호"+(bcbeonhoCnt));
									bcbeonhoCnt++;
								}
							}
						}else if(1 == jjgubun){
							kyogakCnt++;
							int bcbeonhoCnt = 1;
							for (int j = 0; j < krbcgaesu; j++) {
								if((j+1)%2 != 0){
									Girdir_GRBC item = new Girdir_GRBC();
									item.setKRBCBHGUBUN(bigo+"");
									item.setKGJJGUBUN(jjgubun+"");
									item.setKRBCIRBEONHO(decimalFormat_Three.format(j));
									item.setKRBCSBBEONHO(decimalFormat_Two.format(bcbeonhoCnt));
									item.setKDKGGUBUN(kdkggubun);
									item.setKGJJBEONHO(kgjjbeonho);
									girdir_grbc_list.add(item);
//									bJae2.add("교각"+decimalFormat_Three.format(kyogakCnt)+"-"+"지점번호"+kgjjbeonho+"-"+"교량받침"+decimalFormat_Three.format(j+1)+"-"+"방향구분A1"+"-"+"받침번호"+decimalFormat_Two.format(bcbeonhoCnt));
//									bJae2.add("교각"+decimalFormat_Three.format(kyogakCnt)+"-"+"지점번호"+kgjjbeonho+"-"+"교량받침"+decimalFormat_Three.format(j+1)+"-"+"받침번호"+decimalFormat_Two.format(bcbeonhoCnt));
									bJae2.add("교각"+(kyogakCnt)+"-"+"교량받침"+(j+1)+"-"+"받침번호"+(bcbeonhoCnt));
								}else{
									Girdir_GRBC item = new Girdir_GRBC();
									item.setKRBCBHGUBUN(bigo+"");
									item.setKGJJGUBUN(jjgubun+"");
									item.setKRBCIRBEONHO(decimalFormat_Three.format(j));
									item.setKRBCSBBEONHO(decimalFormat_Two.format(bcbeonhoCnt));
									item.setKDKGGUBUN(kdkggubun);
									item.setKGJJBEONHO(kgjjbeonho);
									girdir_grbc_list.add(item);
//									bJae2.add("교각"+decimalFormat_Three.format(kyogakCnt)+"-"+"지점번호"+kgjjbeonho+"-"+"교량받침"+decimalFormat_Three.format(j+1)+"-"+"방향구분A2"+"-"+"받침번호"+decimalFormat_Two.format(bcbeonhoCnt));
//									bJae2.add("교각"+decimalFormat_Three.format(kyogakCnt)+"-"+"지점번호"+kgjjbeonho+"-"+"교량받침"+decimalFormat_Three.format(j+1)+"-"+"받침번호"+decimalFormat_Two.format(bcbeonhoCnt));
									bJae2.add("교각"+(kyogakCnt)+"-"+"교량받침"+(j+1)+"-"+"받침번호"+(bcbeonhoCnt));
									bcbeonhoCnt++;
								}
							}
						}else if(2 == jjgubun){
							int bcbeonhoCnt = 1;
							for (int j = 0; j < krbcgaesu; j++) {
								if((j+1)%2 != 0){
									Girdir_GRBC item = new Girdir_GRBC();
									item.setKRBCBHGUBUN(bigo+"");
									item.setKGJJGUBUN(jjgubun+"");
									item.setKRBCIRBEONHO(decimalFormat_Three.format(j));
									item.setKRBCSBBEONHO(decimalFormat_Two.format(bcbeonhoCnt));
									item.setKDKGGUBUN(kdkggubun);
									item.setKGJJBEONHO(kgjjbeonho);
									girdir_grbc_list.add(item);
//									bJae2.add("교대002"+"-"+"교량받침"+decimalFormat_Three.format(j+1)+"-"+"지점번호"+kgjjbeonho+"-"+"방향구분A1"+"-"+"받침번호"+decimalFormat_Two.format(bcbeonhoCnt));
//									bJae2.add("교대002"+"-"+"교량받침"+decimalFormat_Three.format(j+1)+"-"+"지점번호"+kgjjbeonho+"-"+"받침번호"+decimalFormat_Two.format(bcbeonhoCnt));
									bJae2.add("교대2"+"-"+"교량받침"+(j+1)+"-"+"받침번호"+(bcbeonhoCnt));
								}else{
									Girdir_GRBC item = new Girdir_GRBC();
									item.setKRBCBHGUBUN(bigo+"");
									item.setKGJJGUBUN(jjgubun+"");
									item.setKRBCIRBEONHO(decimalFormat_Three.format(j));
									item.setKRBCSBBEONHO(decimalFormat_Two.format(bcbeonhoCnt));
									item.setKDKGGUBUN(kdkggubun);
									item.setKGJJBEONHO(kgjjbeonho);
									girdir_grbc_list.add(item);
//									bJae2.add("교대002"+"-"+"교량받침"+decimalFormat_Three.format(j+1)+"-"+"지점번호"+kgjjbeonho+"-"+"방향구분A2"+"-"+"받침번호"+decimalFormat_Two.format(bcbeonhoCnt));
//									bJae2.add("교대002"+"-"+"교량받침"+decimalFormat_Three.format(j+1)+"-"+"지점번호"+kgjjbeonho+"-"+"받침번호"+decimalFormat_Two.format(bcbeonhoCnt));
									bJae2.add("교대2"+"-"+"교량받침"+(j+1)+"-"+"받침번호"+(bcbeonhoCnt));
									bcbeonhoCnt++;
								}
							}
						}
					}else if(bigo == 2){//양방향
						if(0 == jjgubun){
							for (int j = 0; j < krbcgaesu; j++) {
								Girdir_GRBC item = new Girdir_GRBC();
								item.setKRBCBHGUBUN(bigo+"");
								item.setKGJJGUBUN(jjgubun+"");
								item.setKRBCIRBEONHO(decimalFormat_Three.format(j));
								item.setKRBCSBBEONHO(decimalFormat_Two.format(j+1));
								item.setKDKGGUBUN(kdkggubun);
								item.setKGJJBEONHO(kgjjbeonho);
								girdir_grbc_list.add(item);
								
//								bJae2.add("교대001"+"-"+"지점번호"+kgjjbeonho+"-"+"교량받침"+decimalFormat_Three.format(j+1) +"-"+"방향구분A0"+"-"+"받침번호"+decimalFormat_Two.format(j+1));
								bJae2.add("교대1"+"-"+"교량받침"+(j+1) +"-"+"받침번호"+(j+1));
							}
						}else if(1 == jjgubun){
							kyogakCnt++;
							for (int j = 0; j < krbcgaesu; j++) {
								Girdir_GRBC item = new Girdir_GRBC();
								item.setKRBCBHGUBUN(bigo+"");
								item.setKGJJGUBUN(jjgubun+"");
								item.setKRBCIRBEONHO(decimalFormat_Three.format(j));
								item.setKRBCSBBEONHO(decimalFormat_Two.format(j+1));
								item.setKDKGGUBUN(kdkggubun);
								item.setKGJJBEONHO(kgjjbeonho);
								girdir_grbc_list.add(item);
								
//								bJae2.add("교각"+decimalFormat_Three.format(kyogakCnt)+"-"+"지점번호"+kgjjbeonho+"-"+"교량받침"+decimalFormat_Three.format(j+1)+"-"+"방향구분A0"+"-"+"받침번호"+decimalFormat_Two.format(j+1));
								bJae2.add("교각"+(kyogakCnt)+"-"+"교량받침"+(j+1)+"-"+"받침번호"+(j+1));
							}
						}else if(2 == jjgubun){
							for (int j = 0; j < krbcgaesu; j++) {
								Girdir_GRBC item = new Girdir_GRBC();
								item.setKRBCBHGUBUN(bigo+"");
								item.setKGJJGUBUN(jjgubun+"");
								item.setKRBCIRBEONHO(decimalFormat_Three.format(j));
								item.setKRBCSBBEONHO(decimalFormat_Two.format(j+1));
								item.setKDKGGUBUN(kdkggubun);
								item.setKGJJBEONHO(kgjjbeonho);
								girdir_grbc_list.add(item);
								
//								bJae2.add("교대002"+"-"+"교량받침"+decimalFormat_Three.format(j+1)+"-"+"지점번호"+kgjjbeonho+"-"+"방향구분A0"+"-"+"받침번호"+decimalFormat_Two.format(j+1));
								bJae2.add("교대2"+"-"+"교량받침"+(j+1)+"-"+"받침번호"+(j+1));
							}
						}
					}
				}
				
			}else if("기타".equals(selectedBjae1)){//경간,교대,교각
				bJae2.clear();
				cursor = db.selectBms_Krgb_CKGANSU(SELECTED_KRCODE ,SELECTED_KRSBBEONHO);//고매교
				cursor_girdir = db.selectBms_Krgb_GIRDIR(SELECTED_KRCODE ,SELECTED_KRSBBEONHO);//고매교
				List<Integer> ckgansu_list = new ArrayList<Integer>();
				int ckgansu = 0;
				while (cursor.moveToNext()) {
					ckgansu = Integer.parseInt(cursor.getString(0));
					ckgansu_list.add(ckgansu);
				}
				
				List<Integer> ckgirdir_list = new ArrayList<Integer>();
				int ckgirdir = 0;
				while (cursor_girdir.moveToNext()) {
					ckgirdir = Integer.parseInt(cursor_girdir.getString(0));
					ckgirdir_list.add(ckgirdir);
				}
				DecimalFormat decimalFormat_Three = new DecimalFormat("000");
				DecimalFormat decimalFormat_Two = new DecimalFormat("00");
				for (int i = 0; i < ckgansu_list.size(); i++) {
					if(ckgirdir_list.get(i)>0){
						bJae2.add("경간"+(i+1));
					}
				}
//				if(bJae2.size() == 0){
//					for (int i = 0; i < ckgansu_list.size(); i++) {
//						bJae2.add("경간"+decimalFormat_Three.format(i+1));
//					}
//				}
				
				cursor = db.selectBms_Krgb_JJGUBUN(SELECTED_KRCODE ,SELECTED_KRSBBEONHO);//고매교
				int kyogakCnt = 0;
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					if("0".equals(cursor.getString(0))){
						bJae2.add("교대0");
						Girdir_GRBC item = new Girdir_GRBC();
						girdir_grbc_list.add(item);
					}else if("1".equals(cursor.getString(0))){
						kyogakCnt++;
						bJae2.add("교각"+kyogakCnt);
						Girdir_GRBC item = new Girdir_GRBC();
						girdir_grbc_list.add(item);
					}else if("2".equals(cursor.getString(0))){
						bJae2.add("교대2");
						Girdir_GRBC item = new Girdir_GRBC();
						girdir_grbc_list.add(item);
					}
				}
			}
		}
		
		sp_bjae2 = (Spinner) findViewById(R.id.sp_bjae2);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(KyoRyangActivity.this, R.layout.spinner_item, bJae2){
			@SuppressLint("NewApi")
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				if(position == getCount()){
					((TextView)v.findViewById(android.R.id.text1)).setText("");;
					((TextView)v.findViewById(android.R.id.text1)).setText(getItem(getCount()));
					/*if("거더".equals(selectedBjae1)){
					}else if("교면포장".equals(selectedBjae1)||"바닥판".equals(selectedBjae1)
							||"2차부재".equals(selectedBjae1)||"배수시설".equals(selectedBjae1)
							||"난간연석".equals(selectedBjae1)){//경간
					}else if("하부구조".equals(selectedBjae1)||"기초".equals(selectedBjae1)){//교대,교각
					}else if("교량받침".equals(selectedBjae1)){//교대,교각
					}else if("기타".equals(selectedBjae1)){
					}*/
				}
				return v;
			}
			@Override
			public int getCount() {
				return super.getCount();
			}
		};
		adapter.setDropDownViewResource(R.layout.spinner_item);
		sp_bjae2.setAdapter(adapter);
		sp_bjae2.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Log.d("","ddddddddddddddd girdir_grbc_list = " + girdir_grbc_list.size() +":"+position);
				if(position != 0){
					SELECTED_BJAE2 = sp_bjae2.getSelectedItem().toString();
					spinSetBJae3(sp_bjae1.getSelectedItem().toString(), sp_bjae2.getSelectedItem().toString());
					
					Girdir_GRBC item = girdir_grbc_list.get(position-1);
					SELECTED_KGJJBEONHO = item.getKGJJBEONHO();
					Log.d("","ddddddddddddddd SELECTED_KGJJBEONHO = " + SELECTED_KGJJBEONHO);
					SELECTED_JHBEONHO = item.getJHBEONHO();
					SELECTED_KDKGGUBUN = item.getKDKGGUBUN();
					SELECTED_KRBCBHGUBUN = item.getKRBCBHGUBUN();
					SELECTED_KRBCIRBEONHO = item.getKRBCIRBEONHO();
					SELECTED_KRBCSBBEONHO = item.getKRBCSBBEONHO();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		
	}
	
	
	/**
	 * 3차부재 셋팅 
	 */
	public void spinSetBJae3(String selectedBjae1, String selectedBjae2){
		Log.d("","spinSetBJae3 = " + selectedBjae1+":"+selectedBjae2+""+SELECTED_JGUBUN);
		bJae3.clear();
		if("2".equals(SELECTED_JGUBUN)){//보수
			bJae3.add("내용 선택");
			bJae3.add("주입보수");
			bJae3.add("표면보수");
			bJae3.add("단면보수");
			bJae3.add("재도장");
			bJae3.add("토사채움");
			bJae3.add("배수구청소");
			bJae3.add("청소");
			bJae3.add("팻칭보수");
			bJae3.add("교체");
			bJae3.add("직접입력");
		}else{
			bJae3.add("내용 선택");
			if("교면포장".equals(selectedBjae1) || "거더".equals(selectedBjae1)){
				bJae3.add("균열");
				bJae3.add("박리");
				bJae3.add("파손");
				bJae3.add("백태");
				bJae3.add("도장탈리");
				bJae3.add("망상균열");
				bJae3.add("재료분리");
				bJae3.add("유간부족");
				bJae3.add("직접입력");
			}else if("바닥판".equals(selectedBjae1)){
				bJae3.add("균열");
				bJae3.add("박리");
				bJae3.add("파손");
				bJae3.add("백태");
				bJae3.add("도장탈리");
				bJae3.add("망상균열");
				bJae3.add("재료분리");
				bJae3.add("탄산화시험");
				bJae3.add("반발경도시험");
				bJae3.add("유간부족");
				bJae3.add("직접입력");
			}else if("교량받침".equals(selectedBjae1)||"신축이음장치".equals(selectedBjae1)){
				bJae3.add("방석균열");
				bJae3.add("파손");
				bJae3.add("부식");
				bJae3.add("몰탈파손");
				bJae3.add("편기");
				bJae3.add("몰탈균열");
				bJae3.add("재료분리");
				bJae3.add("직접입력");
			}else if("하부구조".equals(selectedBjae1)||"2차부재".equals(selectedBjae1)){
				bJae3.add("균열");
				bJae3.add("흉벽균열");
				bJae3.add("벽체균열");
				bJae3.add("코핑부균열");
				bJae3.add("기둥부균열");
				bJae3.add("박리");
				bJae3.add("파손");
				bJae3.add("백태");
				bJae3.add("백태");
				bJae3.add("망상균열");
				bJae3.add("재료분리");
				bJae3.add("탄산화시험");
				bJae3.add("반발경도시험");
				bJae3.add("직접입력");
			}else if("배수시설".equals(selectedBjae1)){
				bJae3.add("퇴적물");
				bJae3.add("막힘");
				bJae3.add("파손");
				bJae3.add("전경");
			}else if("난간연석".equals(selectedBjae1)){
				bJae3.add("균열");
				bJae3.add("박리");
				bJae3.add("파손");
				bJae3.add("백태");
				bJae3.add("부식");
				bJae3.add("망상균열");
				bJae3.add("재료분리");
				bJae3.add("직접입력");
			}else if("기초".equals(selectedBjae1)){
				bJae3.add("세굴");
				bJae3.add("침하");
				bJae3.add("파일손상");
				bJae3.add("침식");
				bJae3.add("재료분리");
				bJae3.add("직접입력");
			}else if("기타".equals(selectedBjae1)){
				bJae3.add("전경");
			}
		}
		
		sp_bjae3 = (Spinner) findViewById(R.id.sp_bjae3);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(KyoRyangActivity.this, R.layout.spinner_item, bJae3){
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
		sp_bjae3.setAdapter(adapter);
		sp_bjae3.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				SELECTED_BJAE3 = sp_bjae3.getSelectedItem().toString();
				if("직접입력".equals(SELECTED_BJAE3)){
					ed_write.setVisibility(View.VISIBLE);
					ed_write.requestFocus();
					ed_write.setText("");
					ed_write.setSelection(ed_write.getText().length());
				}else{
					ed_write.setText("");
					ed_write.setVisibility(View.GONE);
				}
				
/*				if("균열".equals(SELECTED_BJAE3) || "주입보수".equals(SELECTED_BJAE3) || "표면보수".equals(SELECTED_BJAE3)){
					txt_unit1.setText("mm x");
					txt_unit2.setText("mm");
				}else{
					txt_unit1.setText("m x");
					txt_unit2.setText("m");
				}
*/				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
			
		});
		
	}
	
	
	
	
	/**
	 * 종별 선택 박스
	 */
	@SuppressLint("NewApi")
	private void setSpinnerJongByul(){
		List<String> spinItems = new ArrayList<String>();
		Cursor cursor = db.selectBms_Krgb_JongByul();
		spinItems.add("전체");
		Log.d("","setSpinnerJongByul cursor count = " + cursor.getCount());
/*		while (cursor.moveToNext()) {
			if("02".equals(cursor.getString(0))){
				spinItems.add("1종");
			}else if("03".equals(cursor.getString(0))){
				spinItems.add("2종");
			}else if("99".equals(cursor.getString(0))){
				spinItems.add("기타");
			}else if("01".equals(cursor.getString(0))){
				//육교
			}
		}
*/		
		sp_gubun = (Spinner) findViewById(R.id.sp_gubun);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(KyoRyangActivity.this, R.layout.spinner_item, spinItems){
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
//		adapter.setDropDownViewResource(R.layout.spinner_item);
		if(AndroidVersionSetMethod){
			sp_gubun.setDropDownVerticalOffset(8);	
		}
		
		sp_gubun.setAdapter(adapter);
		sp_gubun.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Log.d("","sp_jongbyulsp_jongbyulsp_jongbyul");
				//setSpinnerJisaInfo(position);
				txt_krname.setText("");;
				ed_krname.setText("");
				//getKrList(false);
				txt_krname.performClick();
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
			
		});
		
	}

	/**
	 * 본부 선택 박스
	 */

	private void setSpinnerBonbuInfo(){
		
		List<String> spinItems = new ArrayList<String>();
		Cursor cursor = db.selectBms_BSINFO_BONBU();
		bonbuList.clear();
		Bsinfo bsinfo;
		String userBscode = Common.getPrefString(KyoRyangActivity.this, Configuration.SHARED_BSCODE);
		Log.d("","userBscode userJisacode  " + userBscode);
		
		int UserPosition = 0;
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			bsinfo = new Bsinfo();
			
			bsinfo.setBscode(cursor.getString(0));
			bsinfo.setBsname(cursor.getString(1));
			
			bonbuList.add(bsinfo);
			if(userBscode.equals(cursor.getString(0)) || i == 0){
				UserPosition = i;
				SELECTED_BSCODE = bonbuList.get(UserPosition).getBscode();
				SELECTED_BSNAME = bonbuList.get(UserPosition).getBsname();
			}
			
			spinItems.add(cursor.getString(1));
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(KyoRyangActivity.this, R.layout.spinner_item, spinItems){
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
				setSpinnerJisaInfo(position);
				SELECTED_BSCODE = bonbuList.get(position).getBscode();
				SELECTED_BSNAME = bonbuList.get(position).getBsname();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
			
		});
		sp_bonbu.setSelection(UserPosition);
		setSpinnerJisaInfo(UserPosition);
	}
	
	private void setSpinnerJisaInfo(int bonbuPosition){
		jisaList.clear();
		List<String> spinItems = new ArrayList<String>();
		Cursor cursor = db.selectBms_BSINFO_JISA(SELECTED_BSCODE);
		Bsinfo bsinfo;
		int UserPosition = 0;
		String userJisacode = Common.getPrefString(KyoRyangActivity.this, Configuration.SHARED_JISACODE);
		Log.d("","userBscode userJisacode  " + userJisacode);
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			bsinfo = new Bsinfo();
			
			bsinfo.setBscode(cursor.getString(0));
			bsinfo.setBsname(cursor.getString(1));
			
			jisaList.add(bsinfo);
			
			if(userJisacode.equals(cursor.getString(0)) || i ==0){
				UserPosition = i;
				SELECTED_JISACODE = jisaList.get(UserPosition).getBscode();
				SELECTED_JISANAME = jisaList.get(UserPosition).getBsname();
			}
			
			spinItems.add(cursor.getString(1));
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(KyoRyangActivity.this, R.layout.spinner_item, spinItems){
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
		sp_jisa.setAdapter(adapter);
		sp_jisa.setSelection(UserPosition);
		getKrList(false);
		sp_jisa.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				txt_krname.setText("");;
				ed_krname.setText("");
				getKrList(false);
//				txt_krname.performClick();
				SELECTED_JISACODE = jisaList.get(position).getBscode();
				SELECTED_JISANAME = jisaList.get(position).getBsname();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
			
		});
		
		
		
	}
	
	/**
	 * 점검 보수 선택 박스
	 */
	@SuppressLint("NewApi")
	private void setSpinner(){
		List<String> spinItems = new ArrayList<String>();
		spinItems.add("점검");
		spinItems.add("보수");
		sp_jakup = (Spinner) findViewById(R.id.sp_jakup);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(KyoRyangActivity.this, R.layout.spinner_item, spinItems){
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
		adapter.setDropDownViewResource(R.layout.spinner_item);
		if(AndroidVersionSetMethod){
			sp_jakup.setDropDownVerticalOffset(8);
		}
		sp_jakup.setAdapter(adapter);
		sp_jakup.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				//spinSetBJae1(sp_jakup.getSelectedItem().toString());//부재 선택 박스
				//spinSetBJae2("");
				//spinSetBJae3("", "");
				setEdKrname();//교량 선택 박스
				if("점검".equals(sp_jakup.getSelectedItem().toString())){
					SELECTED_JGUBUN = "1";
				}else if("보수".equals(sp_jakup.getSelectedItem().toString())){
					SELECTED_JGUBUN = "2";
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	//데이타 누적 관리
	static int  accrue_cnt_Level1 = 1;
	static int  accrue_cnt_Level2 = 1;
	
	//카메라 또는 앨범 실행
	@SuppressLint("NewApi")
	public void runCamOrAlbum_Dialog(){
		if(ed_write.getVisibility() == View.VISIBLE){
			SELECTED_BJAE3 = ed_write.getText().toString();
		}
		boolean showDialog = false;
		String message = "";
		if("".equals(Common.nullCheck(SELECTED_KRCODE))){
			showDialog = true;
			message = "교량을 선택해 주세요.";
		}else if("".equals(Common.nullCheck(SELECTED_KRSBBEONHO))){
			showDialog = true;
			message = "교량을 선택해 주세요.";
		}else if("".equals(Common.nullCheck(SELECTED_BJAE1)) || "부재 선택".equals(Common.nullCheck(SELECTED_BJAE1))){
			showDialog = true;
			message = "부재를 선택해 주세요.";
		}else if("".equals(Common.nullCheck(SELECTED_BJAE2)) || "위치 선택".equals(Common.nullCheck(SELECTED_BJAE2))){
			showDialog = true;
			message = "위치를 선택해 주세요.";
		}else if("".equals(Common.nullCheck(SELECTED_BJAE3)) || "내용 선택".equals(Common.nullCheck(SELECTED_BJAE3))){
			showDialog = true;
			message = "내용을 선택해 주세요.";
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
			/*int changedPoint = db.checkDataChanged_Kyoryang(  //1
					SELECTED_KRCODE, 
					SELECTED_KRSBBEONHO, 
					SELECTED_DOGONG,  //도공번호
					SELECTED_BHGUBUN, 
					SELECTED_KGJJGUBUN, 
					SELECTED_KGJJBEONHO,
					SELECTED_BIGO,
					SELECTED_CONTENT,
					"",
					SELECTED_JBMYEONG+"("+SELECTED_DRNO+")",   // 장비명
					SELECTED_BJAE2,
					SELECTED_SULIITEM+"/"+SELECTED_DETAILITEM+"("+SELECTED_BEFORE_AFTER+")",  // 내용
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
					SELECTED_SISUL
					);*/
			
			int changedPoint = db.checkDataChanged_Jeongbi(
					"["+SELECTED_JEONGBIGUBUNCODE+"]"+SELECTED_JEONGBIGUBUN,
					//SELECTED_JEONGBIGUBUN, 
					"["+SELECTED_SULIITEMCODE+"]"+SELECTED_SULIITEM, 
					SELECTED_DETAILITEM,	
					SELECTED_SULIITEM+"/"+SELECTED_DETAILITEM+"("+SELECTED_BEFORE_AFTER+")", 
					"",
					SELECTED_KRCODE, 
					SELECTED_JBMYEONG, 
					SELECTED_DRNO, 
					SELECTED_JBMYEONG+"("+SELECTED_DRNO+")", 
					SELECTED_DOGONG, 
					SELECTED_SWBEONHO, 
					SELECTED_BSCODE, 
					SELECTED_BUSEOCODE, 
					SELECTED_JGUBUN
					);

			if(changedPoint == 1){//위치만 고정
//				accrue_cnt_Level1++;
				accrue_cnt_Level2++;
				//SELECTED_CHECK = "("+accrue_cnt_Level2+")";
				SELECTED_CONTENT = "("+accrue_cnt_Level2+")";
//				SELECTED_BJAE2 = SELECTED_BJAE2.substring(0, SELECTED_BJAE2.indexOf("(")-1);
				
//			}else if(changedPoint == 2){//위치, 손상 고정
//				accrue_cnt_Level1++;
//				accrue_cnt_Level2++;
//				SELECTED_BJAE2 = SELECTED_BJAE2.replace("("+(accrue_cnt_Level1-1)+")", "")+"("+accrue_cnt_Level1+")";
				
//			}else{
//				accrue_cnt_Level1 = 1;
//				accrue_cnt_Level2 = 1;
//				SELECTED_BJAE2 = SELECTED_BJAE2.replace("("+(accrue_cnt_Level1-1)+")", "")+"("+accrue_cnt_Level1+")";
				
			}
			
			final CustomAlertDialog customAlertDialog = new CustomAlertDialog(KyoRyangActivity.this);
			customAlertDialog.show();
			customAlertDialog.get_btn_goCamera().setOnClickListener(new OnClickListener() {
				
				
				@Override
				public void onClick(View v) {
					customAlertDialog.dismiss();
					runCamera();		
				}
			});
			customAlertDialog.get_btn_goAlbum().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					customAlertDialog.dismiss();
					runAlbum();
				}
			});
			if(TAGYN == true){
//				customAlertDialog.get_title_Dialog().setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.title_boardcam, null));
				customAlertDialog.get_title_Dialog().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.title_boardcam, null));
			}else{
//				customAlertDialog.get_title_Dialog().setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.title_normalcam, null));
				customAlertDialog.get_title_Dialog().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.title_normalcam, null));
			}
		}
		
		
	}
	
	//카메라실행
	public void runCamera_Dialog(){
		Log.d("","runCamera_Dialog()");
		if(ed_write.getVisibility() == View.VISIBLE){
			Log.d("","runCamera_Dialog - S1111ELECTED_BJAE3 = " + SELECTED_BJAE3);
			SELECTED_BJAE3 = ed_write.getText().toString();
			SELECTED_DETAILITEM = ed_write.getText().toString();
		}
		
		boolean showDialog = false;
		String message = "";

		Log.d("","runCamera_Dialog - SELECTED_KRNAME = " + SELECTED_KRNAME);
		
		Log.d("","runCamera_Dialog - SELECTED_BJAE1 = " + SELECTED_BJAE1);
		Log.d("","runCamera_Dialog - SELECTED_BJAE2 = " + SELECTED_BJAE2);
		Log.d("","runCamera_Dialog - SELECTED_BJAE3 = " + SELECTED_BJAE3);
		
		Log.d("","runCamera_Dialog - SELECTED_JBCODE = " + SELECTED_JBCODE);
		Log.d("","runCamera_Dialog - SELECTED_JBMYEONG = " + SELECTED_JBMYEONG);
		Log.d("","runCamera_Dialog - SELECTED_DRNO = " + SELECTED_DRNO);
		Log.d("","runCamera_Dialog - SELECTED_DOGONG = " + SELECTED_DOGONG);
		Log.d("","runCamera_Dialog - SELECTED_JEONGBIGUBUN = " + SELECTED_JEONGBIGUBUN);
		Log.d("","runCamera_Dialog - SELECTED_JEONGBIGUBUNCODE = " + SELECTED_JEONGBIGUBUNCODE);
		Log.d("","runCamera_Dialog - SELECTED_SULIITEM = " + SELECTED_SULIITEM);
		Log.d("","runCamera_Dialog - SELECTED_SULIITEMCODE = " + SELECTED_SULIITEMCODE);
		Log.d("","runCamera_Dialog - SELECTED_DETAILITEM = " + SELECTED_DETAILITEM);
		
		Log.d("","runCamera_Dialog - ============== ");
		Log.d("","runCamera_Dialog - SELECTED_KRCODE = " + SELECTED_KRCODE);
		Log.d("","runCamera_Dialog - SELECTED_BSCODE = " + SELECTED_BSCODE);
		Log.d("","runCamera_Dialog - SELECTED_JGUBUN = " + SELECTED_JGUBUN);
		
		
		if("".equals(Common.nullCheck(SELECTED_KRCODE))){
			showDialog = true;
			message = "장비를 선택해 주세요.";
		}else if("".equals(Common.nullCheck(SELECTED_BJAE1)) || "정비구분".equals(Common.nullCheck(SELECTED_BJAE1))){
			showDialog = true;
			message = "정비구분을 선택해 주세요.";
		}else if("".equals(Common.nullCheck(SELECTED_BJAE2)) || "수리항목".equals(Common.nullCheck(SELECTED_BJAE2))){
			showDialog = true;
			message = "수리항목을 선택해 주세요.";
		}else if("".equals(Common.nullCheck(SELECTED_BJAE3)) || "세부항목".equals(Common.nullCheck(SELECTED_BJAE3))){
			showDialog = true;
			message = "세부항목을 선택해 주세요.";
		}
		
		Log.d("","runCamera_Dialog - showDialog1 = " + showDialog);
		
		if (showDialog) {
			Log.d("","runCamera_Dialog - showDialog2 = " + showDialog);
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
			Log.d("","runCamera_Dialog - showDialog3 = " + showDialog);

			Log.d("","runCamera_Dialog - SELECTED_BEFORE_AFTER = " + SELECTED_BEFORE_AFTER);
			
			
			/*int changedPoint = db.checkDataChanged_Kyoryang(  //2
					SELECTED_KRCODE, 
					SELECTED_KRSBBEONHO, 
					SELECTED_DOGONG,  //도공번호
					SELECTED_BHGUBUN, 
					SELECTED_KGJJGUBUN, 
					SELECTED_KGJJBEONHO,
					SELECTED_BIGO,
					SELECTED_CONTENT,
					"",
					SELECTED_JBMYEONG+"("+SELECTED_DRNO+")",   // 장비명
					SELECTED_BJAE2,
					SELECTED_SULIITEM+"/"+SELECTED_DETAILITEM+"("+SELECTED_BEFORE_AFTER+")",  // 내용
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
					SELECTED_SISUL
					);*/ 
			int changedPoint = db.checkDataChanged_Jeongbi(
					"["+SELECTED_JEONGBIGUBUNCODE+"]"+SELECTED_JEONGBIGUBUN, 
					//SELECTED_SULIITEM, 
					"["+SELECTED_SULIITEMCODE+"]"+SELECTED_SULIITEM,
					SELECTED_DETAILITEM,	
					SELECTED_SULIITEM+"/"+SELECTED_DETAILITEM+"("+SELECTED_BEFORE_AFTER+")", 
					"",
					SELECTED_KRCODE, 
					SELECTED_JBMYEONG, 
					SELECTED_DRNO, 
					SELECTED_JBMYEONG+"("+SELECTED_DRNO+")", 
					SELECTED_DOGONG, 
					SELECTED_SWBEONHO, 
					SELECTED_BSCODE, 
					SELECTED_BUSEOCODE, 
					SELECTED_JGUBUN
					);
			
			if(changedPoint == 1){//위치만 고정
//				accrue_cnt_Level2++;
//				SELECTED_CONTENT = "("+accrue_cnt_Level2+")";
				
			}

			final CameraDialogActivity cameraDialog = new CameraDialogActivity(KyoRyangActivity.this);
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
//				customAlertDialog.get_title_Dialog().setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.title_boardcam, null));
				//cameraDialog.get_title_Dialog().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.title_boardcam, null));
			}else{
//				customAlertDialog.get_title_Dialog().setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.title_normalcam, null));
				//cameraDialog.get_title_Dialog().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.title_normalcam, null));
			}
		}
		
		
	}
	
	//앨범실행
	public void runAlbum_Dialog(){
		Log.d("","runAlbum_Dialog()");
		if(ed_write.getVisibility() == View.VISIBLE){
			Log.d("","runAlbum_Dialog - S1111ELECTED_BJAE3 = " + SELECTED_BJAE3);
			SELECTED_BJAE3 = ed_write.getText().toString();
			SELECTED_DETAILITEM = ed_write.getText().toString();
		}

		boolean showDialog = false;
		String message = "";
		
		Log.d("","runAlbum_Dialog - SELECTED_KRNAME = " + SELECTED_KRNAME);
		
		Log.d("","runAlbum_Dialog - SELECTED_BJAE1 = " + SELECTED_BJAE1);
		Log.d("","runAlbum_Dialog - SELECTED_BJAE2 = " + SELECTED_BJAE2);
		Log.d("","runAlbum_Dialog - SELECTED_BJAE3 = " + SELECTED_BJAE3);
		
		Log.d("","runAlbum_Dialog - SELECTED_JBCODE = " + SELECTED_JBCODE);
		Log.d("","runAlbum_Dialog - SELECTED_JBMYEONG = " + SELECTED_JBMYEONG);
		Log.d("","runAlbum_Dialog - SELECTED_DRNO = " + SELECTED_DRNO);
		Log.d("","runAlbum_Dialog - SELECTED_DOGONG = " + SELECTED_DOGONG);
		Log.d("","runAlbum_Dialog - SELECTED_SULIITEM = " + SELECTED_SULIITEM);
		Log.d("","runAlbum_Dialog - SELECTED_DETAILITEM = " + SELECTED_DETAILITEM);
		
		if("".equals(Common.nullCheck(SELECTED_KRCODE))){
			showDialog = true;
			message = "장비를 선택해 주세요.";
		}else if("".equals(Common.nullCheck(SELECTED_BJAE1)) || "정비구분".equals(Common.nullCheck(SELECTED_BJAE1))){
			showDialog = true;
			message = "정비구분을 선택해 주세요.";
		}else if("".equals(Common.nullCheck(SELECTED_BJAE2)) || "수리항목".equals(Common.nullCheck(SELECTED_BJAE2))){
			showDialog = true;
			message = "수리항목을 선택해 주세요.";
		}else if("".equals(Common.nullCheck(SELECTED_BJAE3)) || "세부항목".equals(Common.nullCheck(SELECTED_BJAE3))){
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
			/*int changedPoint = db.checkDataChanged_Kyoryang(  //3
					SELECTED_KRCODE, 
					SELECTED_KRSBBEONHO, 
					SELECTED_DOGONG,  //도공번호
					SELECTED_BHGUBUN, 
					SELECTED_KGJJGUBUN, 
					SELECTED_KGJJBEONHO,
					SELECTED_BIGO,
					SELECTED_CONTENT,
					"",
					SELECTED_JBMYEONG+"("+SELECTED_DRNO+")",   // 장비명
					SELECTED_BJAE2,
					SELECTED_SULIITEM+"/"+SELECTED_DETAILITEM+"("+SELECTED_BEFORE_AFTER+")",  // 내용
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
					SELECTED_SISUL
					);*/ 
			int changedPoint = db.checkDataChanged_Jeongbi(
					"["+SELECTED_JEONGBIGUBUNCODE+"]"+SELECTED_JEONGBIGUBUN,
					//SELECTED_JEONGBIGUBUN, 
					//SELECTED_SULIITEM, 
					"["+SELECTED_SULIITEMCODE+"]"+SELECTED_SULIITEM,
					SELECTED_DETAILITEM,	
					SELECTED_SULIITEM+"/"+SELECTED_DETAILITEM+"("+SELECTED_BEFORE_AFTER+")", 
					"",
					SELECTED_KRCODE, 
					SELECTED_JBMYEONG, 
					SELECTED_DRNO, 
					SELECTED_JBMYEONG+"("+SELECTED_DRNO+")", 
					SELECTED_DOGONG, 
					SELECTED_SWBEONHO, 
					SELECTED_BSCODE, 
					SELECTED_BUSEOCODE, 
					SELECTED_JGUBUN
					);
			
			if(changedPoint == 1){//위치만 고정
//				accrue_cnt_Level2++;
//				SELECTED_CONTENT = "("+accrue_cnt_Level2+")";
				
			}

			
			final CameraDialogActivity cameraDialog = new CameraDialogActivity(KyoRyangActivity.this);
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
//				customAlertDialog.get_title_Dialog().setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.title_boardcam, null));
				//customAlertDialog.get_title_Dialog().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.title_boardcam, null));
			}else{
//				customAlertDialog.get_title_Dialog().setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.title_normalcam, null));
				//customAlertDialog.get_title_Dialog().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.title_normalcam, null));
			}
		}
		
		
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
		super.onActivityResult(requestCode, resultCode, data);
		boolean showDialog = false;
		String message = "";
		String dataDate = "";
		if(resultCode == RESULT_OK){
/*			if(requestCode == Configuration.REQUESTCODE_UNIT1){
				txt_unit1.setText(common.nullCheck(data.getStringExtra("unit"))+" X");
				
			}else if(requestCode == Configuration.REQUESTCODE_UNIT2){
				txt_unit2.setText(common.nullCheck(data.getStringExtra("unit")));
				
			}else if(requestCode == Configuration.REQUESTCODE_GETIMG){
*/			if(requestCode == Configuration.REQUESTCODE_GETIMG){
				Uri mUriSet = data.getData();
				File tempfile = new File(getRealPathFromURI(mUriSet)); 
				Log.d("","ddddddd getabsolutefile = " + tempfile.getAbsoluteFile().toString());
				Log.d("","ddddddd getname =" + tempfile.getName());
				
				ExifInterface exif;
				try {
					exif = new ExifInterface(tempfile.getAbsoluteFile().toString());
					dataDate = exif.getAttribute(ExifInterface.TAG_DATETIME);
					Log.d("","ddddddd dataDate =" + dataDate);
					if("".equals(common.nullCheck(dataDate))){
						Log.d("","ddddddd dataDate1 =" + dataDate);
						showDialog = true;
						message = "사진메타정보가 존재하지 않습니다.";
						dataDate = Common.getCalendarDateYMDHMS();
					}else{
						Log.d("","ddddddd dataDate2 =" + dataDate);
					}
					
					if (showDialog) {
						Log.d("","ddddddd dataDate3 =" + dataDate);
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
					Log.d("","ddddddd dataDate4 =" + dataDate);
					Log.d("","ddddddd dataDate5 =" + dataDate.substring(0, 10).replace(":", ".")+" "+dataDate.substring(11, 19));
					dataDate = dataDate.substring(0, 10).replace(":", ".")+" "+dataDate.substring(11, 19);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				if(TAGYN == true){
					SELECTED_TAGYN ="Y";
				}else{
					SELECTED_TAGYN ="N";
				}
				
				SELECTED_KRNAME = SELECTED_DOGONG;
				SELECTED_BJAE1 = SELECTED_JBMYEONG;
				SELECTED_BJAE2 = SELECTED_SULIITEM;
				//cameraIntent.putExtra("SELECTED_KRNAME", SELECTED_DOGONG);
				//cameraIntent.putExtra("SELECTED_BJAE1", SELECTED_JBMYEONG);
				//cameraIntent.putExtra("SELECTED_BJAE2", SELECTED_SULIITEM);
				
				/*db.insertBms_Krgb_JGINFO(
						SELECTED_KRCODE, 
						SELECTED_KRSBBEONHO, 
						SELECTED_DOGONG,  //도공번호
						SELECTED_BHGUBUN, 
						SELECTED_KGJJGUBUN, 
						SELECTED_KGJJBEONHO,
						SELECTED_BIGO,
						SELECTED_CONTENT,
						tempfile.getAbsolutePath().toString(),
						SELECTED_JBMYEONG+"("+SELECTED_DRNO+")",   // 장비명
						SELECTED_BJAE2,
						SELECTED_SULIITEM+"/"+SELECTED_DETAILITEM+"("+SELECTED_BEFORE_AFTER+")",  // 내용
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
						getUserNm(KyoRyangActivity.this)
						);*/
				
				db.insertSmart_DATAINFO(
						/* 2018.06.07.
						 * Camera 촬영 시 처리되는 정비구분, 수리항목 등에 선택인덱스 추가 부분이 누락됨
						 * Camera와 동일하게 처리 추가!
						 * */
						//SELECTED_JEONGBIGUBUN,
						"["+SELECTED_JEONGBIGUBUNCODE+"]"+SELECTED_JEONGBIGUBUN,						 
						//SELECTED_SULIITEM, 
						"["+SELECTED_SULIITEMCODE+"]"+SELECTED_SULIITEM, 
						SELECTED_DETAILITEM,	
						SELECTED_SULIITEM+"/"+SELECTED_DETAILITEM+"("+SELECTED_BEFORE_AFTER+")", 
						tempfile.getAbsolutePath().toString(),
						SELECTED_KRCODE, 
						SELECTED_JBMYEONG,
						SELECTED_DRNO, 
						SELECTED_JBMYEONG+"("+SELECTED_DRNO+")", 
						SELECTED_DOGONG,
						dataDate,
						SELECTED_SWBEONHO, 
						SELECTED_BSCODE, 
						SELECTED_BUSEOCODE, 
						SELECTED_JGUBUN, 
						SELECTED_TAGYN, 
						getUserNm(KyoRyangActivity.this)
						);
			}
		}
	}
	
	private void runAlbum(){
//		SELECTED_CONTENT = ed_content1.getText().toString() +" "+ed_content2.getText().toString()+txt_unit1.getText().toString()+ed_content3.getText().toString()+txt_unit2.getText().toString();
		if(ed_write.getVisibility() == View.VISIBLE){
			SELECTED_BJAE3 = ed_write.getText().toString();
		}
		
		Intent i = new Intent(Intent.ACTION_PICK);
		i.setType("image/*");
		startActivityForResult(i, Configuration.REQUESTCODE_GETIMG);
	}
	
	private void runCamera(){
		Log.d("", "runCamera()");
		
//		SELECTED_CONTENT = ed_content1.getText().toString() +" "+ed_content2.getText().toString()+txt_unit1.getText().toString()+ed_content3.getText().toString()+txt_unit2.getText().toString();
		Intent cameraIntent = new Intent(KyoRyangActivity.this, CameraActivity_Kyoryang.class);
		
		cameraIntent.putExtra("SELECTED_JBMYEONG", SELECTED_JBMYEONG);
		cameraIntent.putExtra("SELECTED_DRNO", SELECTED_DRNO);
		cameraIntent.putExtra("SELECTED_DOGONG", SELECTED_DOGONG);
		cameraIntent.putExtra("SELECTED_JEONGBIGUBUN", "["+SELECTED_JEONGBIGUBUNCODE+"]"+SELECTED_JEONGBIGUBUN);
		cameraIntent.putExtra("SELECTED_SULIITEM", SELECTED_SULIITEM);
		cameraIntent.putExtra("SELECTED_SULIITEMCODE", SELECTED_SULIITEMCODE);
		cameraIntent.putExtra("SELECTED_DETAILITEM", SELECTED_DETAILITEM);
		cameraIntent.putExtra("SELECTED_BEFORE_AFTER", SELECTED_BEFORE_AFTER);
		cameraIntent.putExtra("SELECTED_BUSEOCODE", jisacode);
		cameraIntent.putExtra("SELECTED_CHECK", SELECTED_CHECK);
		
		cameraIntent.putExtra("SELECTED_KRNAME", SELECTED_DOGONG);
		cameraIntent.putExtra("SELECTED_BJAE1", SELECTED_JBMYEONG);
		cameraIntent.putExtra("SELECTED_BJAE2", SELECTED_SULIITEM);
		
/*		cameraIntent.putExtra("SELECTED_KRNAME", SELECTED_DOGONG);
		cameraIntent.putExtra("SELECTED_BJAE1", SELECTED_JBMYEONG);
		cameraIntent.putExtra("SELECTED_BJAE2", SELECTED_SULIITEM);
		cameraIntent.putExtra("SELECTED_JBCODE", SELECTED_JBCODE);
		cameraIntent.putExtra("SELECTED_DRNO", SELECTED_DRNO);
*/		
		cameraIntent.putExtra("SELECTED_KRCODE", SELECTED_KRCODE);
		//cameraIntent.putExtra("SELECTED_KRNAME", SELECTED_KRNAME);
		cameraIntent.putExtra("SELECTED_KRSBBEONHO", SELECTED_KRSBBEONHO);
		cameraIntent.putExtra("SELECTED_BSCODE", SELECTED_BSCODE);
		cameraIntent.putExtra("SELECTED_JISACODE", SELECTED_JISACODE);
		cameraIntent.putExtra("SELECTED_CONTENT", SELECTED_CONTENT);
		cameraIntent.putExtra("SELECTED_BHGUBUN", SELECTED_BHGUBUN);
		cameraIntent.putExtra("SELECTED_KGJJGUBUN", SELECTED_KGJJGUBUN);
		cameraIntent.putExtra("SELECTED_KGJJBEONHO", SELECTED_KGJJBEONHO);
		cameraIntent.putExtra("SELECTED_BIGO", SELECTED_BIGO);
		//cameraIntent.putExtra("SELECTED_BJAE1", SELECTED_BJAE1);
		//cameraIntent.putExtra("SELECTED_BJAE2", SELECTED_BJAE2);
		
		cameraIntent.putExtra("SELECTED_BJAE3", SELECTED_BJAE3);
		
		cameraIntent.putExtra("SELECTED_BJAE1_CODE", SELECTED_BJAE1_CODE);
		cameraIntent.putExtra("SELECTED_JHBEONHO", SELECTED_JHBEONHO);
		cameraIntent.putExtra("SELECTED_KDKGGUBUN", SELECTED_KDKGGUBUN);
		cameraIntent.putExtra("SELECTED_KRBCBHGUBUN", SELECTED_KRBCBHGUBUN);
		cameraIntent.putExtra("SELECTED_KRBCIRBEONHO", SELECTED_KRBCIRBEONHO);
		cameraIntent.putExtra("SELECTED_KRBCSBBEONHO", SELECTED_KRBCSBBEONHO);
		cameraIntent.putExtra("SELECTED_SWBEONHO", SELECTED_SWBEONHO);
		
		cameraIntent.putExtra("SELECTED_JGUBUN", SELECTED_JGUBUN);
		cameraIntent.putExtra("SELECTED_SISUL", SELECTED_SISUL);
		
		cameraIntent.putExtra("nsname", ns_name);
		cameraIntent.putExtra("currentIjung", currentIjung);
		cameraIntent.putExtra("TAGYN", TAGYN);
		
		
		startActivity(cameraIntent);	
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		/*case R.id.txt_unit1:
			intent = new Intent(KyoRyangActivity.this, UnitChoiceActivity.class);
			startActivityForResult(intent, Configuration.REQUESTCODE_UNIT1);
			break;
		case R.id.txt_unit2:
			intent = new Intent(KyoRyangActivity.this, UnitChoiceActivity.class);
			startActivityForResult(intent, Configuration.REQUESTCODE_UNIT2);
			break;*/
		case R.id.btn_txt_krname:
			txt_krname.setText("");
			ed_krname.setText("");
			
			//txt_buseo.setText("");
			txt_jangbi.setText("");
			txt_drno.setText("");
			txt_chajong.setText("");
			txt_dogong.setText("");
			//txt_gyugyeok.setText("");
		//	txt_younjang.setText("");
		//	txt_kyogo.setText("");
		//	txt_ckgansu.setText("");
			SELECTED_KRCODE = "";
			SELECTED_KRNAME = "";
			break;
			
		case R.id.btn_ed_krname:
			txt_krname.setText("");
			ed_krname.setText("");
			break;
			
		case R.id.txt_frontjongbyul:
			sp_gubun.performClick();
			break;
			
		case R.id.btn_sendImage:
//			new FileUploadTest().execute("");
			Parameters params = new Parameters(Configuration.FILE_PRIMITIVE);
			
			String wifissid = "";
//			ConnectivityManager manager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
//			boolean isWifiAvailable = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isAvailable();
//			boolean isWifiConnect = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
//			if(!isWifiAvailable && !isWifiConnect){
//				WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
//				wifissid = wifiManager.getConnectionInfo().getSSID();
//			}
			WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
			wifissid = wifiManager.getConnectionInfo().getSSID();
			
			Log.d("", "btn_sendImage - params : "+params.toString());
			Log.d("", "btn_sendImage - wifissid : "+wifissid);
			
			if(null != wifissid && "<unknown ssid>".equals(wifissid)){
				Log.d("", "btn_sendImage - wifissid != null "+(wifissid.indexOf("Ex-Smobile")));
				if(!(wifissid.indexOf("Ex-Smobile")>-1) ){	
					Log.d("", "btn_sendImage - wifissid = -1 ");
					executeJob(params, KyoRyangActivity.this);
				}else{
					Log.d("", "btn_sendImage - wifissid = 1");
					Log.d("", "btn_sendImage - 서버전송-업무접속필요");

					AlertDialog.Builder adbLoc	= new AlertDialog.Builder(KyoRyangActivity.this);
					adbLoc.setCancelable(false);
					adbLoc.setTitle("스마트정비관리"); 
					adbLoc.setMessage("업무접속 연결 확인 후 사용하시기 바랍니다.");
					adbLoc.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					adbLoc.setNegativeButton("업무접속하기", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							//Intent intent = getPackageManager().getLaunchIntentForPackage("com.aircuve.mcuvic");
							Intent intent = getPackageManager().getLaunchIntentForPackage("com.ex.group.folder");
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			                startActivity(intent);
						}
					});
					adbLoc.show();
				}
			}else{
				Log.d("", "btn_sendImage - wifissid = null ");
				executeJob(params, KyoRyangActivity.this);
			}
			
//			executeJob(params, KyoRyangActivity.this);	
			
			break;
		case R.id.sp_gubunlB:
			sp_gubun.performClick();
			break;
		case R.id.sp_jisaB:
			sp_jisa.performClick();
			break;
		case R.id.sp_bonbuB:
			sp_bonbu.performClick();
			break;
		case R.id.btn_locationGiban:
			if(Common.nullCheck(ns_code).equals("")){
				AlertDialog.Builder adbLoc	= new AlertDialog.Builder(contextActivity);
				adbLoc.setCancelable(false);
				adbLoc.setTitle(R.string.app_name);
				adbLoc.setMessage(R.string.nolocation_kr);
				adbLoc.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				adbLoc.show();
			}else{
				//getKrList(true);
				//getjbList(true);
				ll_krlist.setVisibility(View.VISIBLE);
				Toast.makeText(KyoRyangActivity.this, ""+ns_name +" "+currentIjung +" Km", 2000).show();
			}
			
			break;
/*		case R.id.btn_delImage:
			AlertDialog.Builder adb	= new AlertDialog.Builder(contextActivity);
			adb.setCancelable(false);
			adb.setTitle("스마트원샷");
			adb.setMessage("휴대폰에 저장된 점검 사진을 삭제 하시겠습니까?\n※ 점검 데이타 또한 삭제 됩니다.");
			adb.setPositiveButton("예", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					db.deleteJgHistoryKyoRyang();
//					Common.DeleteDir(Configuration.dirRoot);
					Toast.makeText(KyoRyangActivity.this, "삭제 되었습니다.", Toast.LENGTH_SHORT).show();
				}
			});
			adb.setNegativeButton("아니오", null);
			adb.show(); 
			
			break;
*/		case R.id.sp_bjae1B:
			sp_bjae1.performClick();
			break;
		case R.id.sp_bjae2B:
			sp_bjae2.performClick();
			break;
		case R.id.sp_bjae3B:
//			ed_write.setVisibility(View.VISIBLE);
			sp_bjae3.performClick();
			break;
		case R.id.ll_krlist:
			ll_krlist.setVisibility(View.GONE);
			break;
		case R.id.txt_krname:
			ed_krname.setText(txt_krname.getText());
			ed_krname.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
			
			ll_krlist.setVisibility(View.VISIBLE);
			
			//getKrList(false);
			ed_krname.setSelection(ed_krname.getText().length());
			break;
		case R.id.btn_JgList:
			/*intent = new Intent(KyoRyangActivity.this, ListActivity_Kyoryang.class);
			startActivity(intent);*/
			break;			
		case R.id.btn_X:
			Common.setPrefString(KyoRyangActivity.this, Configuration.SHARED_BTNX, "Y");
			finish();
			break;
		case R.id.sp_jakupB:
			sp_jakup.performClick();
			break;
		case R.id.btn_BoardCamera:
			TAGYN = true;
			runCamera_Dialog();
			break;
		case R.id.btn_album:
			TAGYN = true;
			runAlbum_Dialog();
			break;
		case R.id.btn_NormalCamera:
			TAGYN = false;
			runCamOrAlbum_Dialog();
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		if(ll_krlist.getVisibility() == View.VISIBLE){
			ll_krlist.setVisibility(View.GONE);
		}else{
			Common.setPrefString(KyoRyangActivity.this, Configuration.SHARED_BTNX, "Y");
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
			mFileList = mList;
			this.params = params;
			this.mActivity = mActivity;
		}

		@Override
		protected void onPreExecute() {
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
			// --------------------------------------------------------------------------------------------
			// #region 실 첨부파일 전송, 전송화면 프로그레스 바 업데이트 함수 호출
			Log.d("","################### DoComplecatedJob doInBackground start" + mFileList.size());
			for (int i = 0; i < mFileList.size(); i++) {
				httpClient = new DefaultHttpClient();
				HttpContext httpContext = new BasicHttpContext();
				
				
				fileCnt ++;
				try {
					StringBuffer sb = new StringBuffer();
					sb.append(Configuration.FILE_UPLOAD_PATH);
					sb.append("?");
					sb.append(params.toString());
					Log.i("","URL = " + sb.toString());
					// URL url = new URL(new String(
					// Common.nullTrim(sb.toString()).getBytes("EUC-KR"), "8859_1"))
					// ;

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

					// String uriSet = mUriSet.toString();
					String absolutePath = "";

					// 파일 첨부 및 전송
					Log.d("", "before send ReadSdCardSize() size = "+ ReadSDCard().size());

					ImageJgInfo image = mFileList.get(i);
					absolutePath = image.getFilePath().toString();
					Log.d("", "send filepath name = " + absolutePath);
					multipartContent.addPart("uploaded_file" + i, new FileBody(new File(absolutePath)));

					totalSize = multipartContent.getContentLength();
					totalSizeKB = totalSize / 1024;
					// pb.setMax(Integer.parseInt(totalSize + ""));

					// Send it
					multipartContent.addPart("renamePrefix", new StringBody(""));
					
					
					//다음 형태로 태그 정보 넘기기 (사진태그)
//					String bridgeNm = multi.getParameter("bridgeNm");//태그1
//					String location = multi.getParameter("location");//태그2
//					String content = multi.getParameter("content");//태그3
//					String createDate = multi.getParameter("createDate");//태그4
					
 /*   				Log.d("","Multipartcontent bridgeNm = "+image.getInfo().getKrname1());
					Log.d("","Multipartcontent location = "+image.getInfo().getBjae2());
					Log.d("","Multipartcontent content = "+image.getInfo().getContent());
					Log.d("","Multipartcontent createDate = "+image.getInfo().getReg_date());
//					Log.d("","Multipartcontent beonho = "+"준비중");
					Log.d("","Multipartcontent bjgubun = "+image.getInfo().getBjae1_code());
					Log.d("","Multipartcontent jgubun = "+image.getInfo().getJgubun());
					Log.d("","Multipartcontent bonbucode = "+image.getInfo().getBscode());
					Log.d("","Multipartcontent jisacode = "+image.getInfo().getJisacode());
					Log.d("","Multipartcontent jhbeonho = "+image.getInfo().getJhbeonho());
					Log.d("","Multipartcontent jkhsahang = "+image.getInfo().getContent());
					Log.d("","Multipartcontent kdkggubun = "+image.getInfo().getKdkggubun());
					Log.d("","Multipartcontent kgbeonho = "+image.getInfo().getKgjjbeonho());
					Log.d("","Multipartcontent krbcbhgubun = "+image.getInfo().getKrbcbhgubun());
					Log.d("","Multipartcontent krbcirbeonho = "+image.getInfo().getKrbcirbeonho());
					Log.d("","Multipartcontent krbcsbbeonho = "+image.getInfo().getKrbcsbbeonho());
					Log.d("","Multipartcontent jjilja = "+image.getInfo().getReg_date());
					Log.d("","Multipartcontent bigo = "+image.getInfo().getBigo());
					Log.d("","Multipartcontent krcode = "+image.getInfo().getKrcode());
					Log.d("","Multipartcontent krsbbeonho = "+image.getInfo().getKrsbbeonho());
					Log.d("","Multipartcontent sjfilename = "+image.getFileName().toString());
					Log.d("","Multipartcontent sjnaeyong = "+image.getInfo().getContent());
					Log.d("","Multipartcontent swbeonho = "+image.getInfo().getSwbeonho());
					Log.d("","Multipartcontent tagYn = "+image.getInfo().getTagYn());
					Log.d("","Multipartcontent sisul = "+image.getInfo().getSisul());
					Log.d("","Multipartcontent swname = "+image.getInfo().getSwname());
*/					
					
					Log.d("","Multipartcontent SURIL!! = "+image.getInfo().getreg_date());     														// 수리일
					Log.d("","Multipartcontent SURIL = "+image.getInfo().getreg_date().replace(".", "").substring(0, 8));     						// 수리일
					Log.d("","Multipartcontent JANGBICODE = "+Common.nullCheck(image.getInfo().getjangbicode()));     								// 장비코드
					Log.d("","Multipartcontent SURIDATE = "+image.getInfo().getreg_date().substring(0, 16));										// 정비일시
					Log.d("","Multipartcontent JANGBIMYEONG = "+Common.nullCheck(image.getInfo().getjangbimyeong()));     							// 장비명
					Log.d("","Multipartcontent DOGONGBEONHO = "+Common.nullCheck(image.getInfo().getdogongcode()));     						    // 도공번호
					Log.d("","Multipartcontent CONTENT = "+image.getInfo().getsulicontent());  														// 수리내용
					Log.d("","Multipartcontent IMGAGE = "+image.getFileName().toString());															// 이미지명
					Log.d("","Multipartcontent BUSEOCODE = "+image.getInfo().getbuseocode());
					Log.d("","Multipartcontent GUBUN = "+image.getInfo().getGubun());
					Log.d("","Multipartcontent BUPUMCODE = "+image.getInfo().getBupumcode());
					Log.d("","Multipartcontent SWBEONHO = "+image.getInfo().getswbeonho());
					Log.d("","Multipartcontent SENDDATE = "+common.getCalendarDateYMDHM());
/*					String tempDate = image.getInfo().getReg_date();
					String tempDate = Common.getCalendarDateYMDHM();
					String year = tempDate.substring(0,4);
					String month = tempDate.substring(5,7);
					String day = tempDate.substring(8,10);
					String hh = tempDate.substring(8,10);
					String mm = tempDate.substring(11,13);
					//String ss = tempDate.substring(12,14);
*/					
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
							));
					
					httpPost.setEntity(multipartContent);
					
					// multipartContent.addPart("renamePrefix", new

					// 미디어 파일 전송
					sendCheckFile = "VPNFAIL";
					
					HttpResponse response = httpClient.execute(httpPost, httpContext);
					String serverResponse = EntityUtils.toString(response.getEntity());
					
					Log.d("","response : "+response);
					Log.d("","serverResponse : "+serverResponse.trim());
					
					sendCheckFile = serverResponse.trim();
					Log.d("","*****************************************************");
					Log.d("", "DoComplecatedJob doinbackground result " + response.toString());
					Log.d("", "DoComplecatedJob doinbackground result " + response.getParams().toString());
					Log.d("", "DoComplecatedJob doinbackground result " + response.getEntity());
					Log.d("", "DoComplecatedJob doinbackground result " + response.getStatusLine().getStatusCode());
					Log.d("", "DoComplecatedJob doinbackground " + sendCheckFile.trim());
					Log.d("","*****************************************************");
					
					if(response.getStatusLine().getStatusCode() == 200){
						//전송여부 저장
						sendCheckFile = "<resultcode>1000</resultcode>";
						//db.updateJgHistorySendYn(image.getFilePath().toString());
						db.updateJBHistorySendYn(image.getFilePath().toString());
					}else{
					}
				} catch (Exception e) {
					e.printStackTrace();
				}// end try~catch
				Log.d("", "################### DoComplecatedJob doInBackground end");	
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

			Log.d("ProgressUpdate", "onCancelled : " + "onCancelled");
			super.onCancelled();

			// #endregion
			// --------------------------------------------------------------------------------------------
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			// --------------------------------------------------------------------------------------------
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

//				tvTrans.setText(df.format(iProgress * totalSizeKB / 100) + " KB");
//				tvTot.setText(df.format(totalSizeKB) + " KB");
				tvTrans.setText(fileCnt+"개");
				tvTot.setText(mFileList.size()+"개");
			}// end if

			// #endregion
			// --------------------------------------------------------------------------------------------
		}

		@Override
		protected void onPostExecute(Long result) {
			// --------------------------------------------------------------------------------------------
			// #region 첨부파일 전송 완료 후 제보내용 전송 함수 호출

			progressDialog.dismiss();
			multipartContent = null;
			mDialog.dismiss();

			Log.d("ProgressUpdate", "onPostExecute() - transData : " + transData);
			Log.d("ProgressUpdate", "onPostExecute() - transData : " + result);
			Log.d("", "onPostExecute() - sendCheckFile : " + sendCheckFile);
			Log.d("", "onPostExecute() - multipartContent : " + multipartContent);
			Log.d("", "onPostExecute() - iPercent : " + iPercent);
			if (multipartContent != null || (iPercent < 100 && iPercent > 0)) {
//				openWarnDialog(mActivity, "파일 전송에 실패했습니다.");
				Toast.makeText(KyoRyangActivity.this, "파일 전송에 실패했습니다.\n message:percent="+iPercent, Toast.LENGTH_LONG).show();
			}else{
//				openWarnDialog(mActivity, "파일 전송 성공");
				if("VPNFAIL".equals(sendCheckFile)){
					Log.d("onPostExecute", "VPNFAIL - 업무접속 확인해주세요 ");
					AlertDialog.Builder adbLoc	= new AlertDialog.Builder(KyoRyangActivity.this);
					adbLoc.setCancelable(false); 
					adbLoc.setTitle("스마트정비관리"); 
					adbLoc.setMessage("업무접속 연결 확인 후 사용하시기 바랍니다."); 
					adbLoc.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					adbLoc.setNegativeButton("업무접속하기", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							//Intent intent = getPackageManager().getLaunchIntentForPackage("com.aircuve.mcuvic");
							Intent intent = getPackageManager().getLaunchIntentForPackage("com.ex.group.folder");
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			                startActivity(intent);
						}
					});
					adbLoc.show();
					
				}else if("FAIL".equals(sendCheckFile)){
					Toast.makeText(KyoRyangActivity.this, "파일 전송에 실패했습니다.\n message:Fail("+sendCheckFile+")", Toast.LENGTH_LONG).show();
				}else if("".equals(Common.nullCheck(sendCheckFile))){
					Toast.makeText(KyoRyangActivity.this, "파일 전송에 실패했습니다.\n message:null("+sendCheckFile+")", Toast.LENGTH_LONG).show();
				}else if("<resultcode>1000</resultcode>".equals(sendCheckFile)){
					Toast.makeText(KyoRyangActivity.this, "파일 전송 성공", Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(KyoRyangActivity.this, "파일 전송에 실패했습니다.\n message:etc("+sendCheckFile+")", Toast.LENGTH_LONG).show();
				}
				
			}
//			Common.DeleteDir(Common.FILE_DIR);
			// --------------------------------------------------------------------------------------------
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
		Log.d(tag, "******************** executeJob()~! ********************");
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
			Toast.makeText(KyoRyangActivity.this, R.string.no_data, Toast.LENGTH_SHORT).show();
		}
	}
	
	Dialog dialogWarn;

	public void openWarnDialog(Context ctx, String message) {
		Log.d(tag, "openWarnDialog()~!");
		dialogWarn = new Dialog(ctx, R.style.FullHeightDialog);

		dialogWarn.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogWarn.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		dialogWarn.setContentView(R.layout.cust_dialog_warn);
		dialogWarn.setCancelable(false);

		ImageButton button = (ImageButton) dialogWarn.findViewById(R.id.ibtnMovie);

		TextView tvPrompt = (TextView) dialogWarn.findViewById(R.id.tvPrompt);
		tvPrompt.setText(message);

		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				dialogWarn.dismiss();
			}
		});

		dialogWarn.show();
	}
	
	
//	Common.FILE_DIR
	public List<ImageJgInfo> ReadSDCard() {
		Log.d("", "ReadSDCard()~!!");
		//Cursor cursor = db.selectBms_Krgb_JGINFO_ONE();
		Cursor cursor = db.selectSmart_DATAINFO_ONE();
		File[] files = new File[cursor.getCount()];
		
		Log.d(tag, "ReadSDCard() - cursor.count : "+cursor.getCount());
		List<ImageJgInfo>  trnImageList = new ArrayList<ImageJgInfo>();

		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			ImageJgInfo item = new ImageJgInfo();
			JbInfo item_sub = new JbInfo();
			
/*			item_sub.setjangbicode(cursor.getString(0));  // 장비코드
			item_sub.setdogongcode(cursor.getString(2));  // 도공번호
			item_sub.setfilename(cursor.getString(8));  // 파일이름
			item_sub.setreg_date(cursor.getString(9));
			item_sub.setjangbimyeong(cursor.getString(10));  // 장비명
			item_sub.setsulicontent(cursor.getString(12));  // 내용
			item_sub.setbuseocode(cursor.getString(15));  // 부서코드
			item_sub.setswbeonho(cursor.getString(21));  // 사원번호
			item_sub.settagYn(cursor.getString(23));  // 태그여부
			item_sub.setswname(cursor.getString(26));  // 인천지사 안전순찰
*/			
			item_sub.setjangbicode(cursor.getString(5));  // 장비코드
			item_sub.setdogongcode(cursor.getString(9));  // 도공번호
			item_sub.setfilename(cursor.getString(4));  // 파일이름
			item_sub.setreg_date(cursor.getString(10));
			item_sub.setjangbimyeong(cursor.getString(8));  // 장비명 name
			item_sub.setsulicontent(cursor.getString(3));  // 내용  content
			item_sub.setbuseocode(cursor.getString(14));  // 부서코드
			item_sub.setswbeonho(cursor.getString(12));  // 사원번호
			item_sub.settagYn(cursor.getString(16));  // 태그여부
			item_sub.setswname(cursor.getString(17));  // 인천지사 안전순찰
			
			String jeongbiGB = cursor.getString(0);
			String itemGB = cursor.getString(1);
			Log.d(tag, "ReadSDCard() - jeongbiGB : "+jeongbiGB);
			Log.d(tag, "ReadSDCard() - itemGB : "+itemGB);
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
			
			Log.d("saveFile", "fileName ab : " + file.getAbsolutePath());
			Log.d("saveFile", "fileName : " + fileName[fileName.length - 1]);

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

		Log.d("", "ReadSdCard list size = " + trnImageList.size());
		return trnImageList;
	}
	
	
	/**___________________________________________________________________________________________________________________**/
	/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~**/
	
	/************************************************************************
	 * GPS 시작
	 * 	
	 ************************************************************************/
	public static LocationManager mLocMgr;
	static LocationListener mLocListener;
	private static Handler mHandler = new Handler();
	public static double longitude = 0.0;
	public static double latitude = 0.0;
	private double START_LAT = 37.200349;
	private double START_LNG = 127.094467;
	boolean mGpsSatStat = false;
	String nowprovider = "";
	public static boolean gpsDialogFlag = true;
	private static final int GPS_INTENT = 6;
	public static Activity contextActivity;
	public int gpsMinTime= 3000;
	
	public void startGps(){
		
		new Thread(new Runnable(){
			public void run() {
		    	try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				mHandler.post(new Runnable(){
					public void run() {
				    	mLocMgr = (LocationManager)getSystemService(Context.LOCATION_SERVICE);				
				    	mLocListener = new LocationListener(){
							public void onProviderDisabled(String provider) {

							}
							
							public void onStatusChanged(String provider, int status,Bundle extras) {

								switch (status) {
								case LocationProvider.OUT_OF_SERVICE:
									Log.v("GPSInfo", "Status Changed: Out of Service");
									latitude = START_LAT;
									longitude = START_LNG;

									break;
								case LocationProvider.TEMPORARILY_UNAVAILABLE:
									Log.v("GPSInfo", "Status Changed: Temporarily Unavailable");
									break;
								case LocationProvider.AVAILABLE:
									Log.v("GPSInfo", "Status Changed: Available");
									
									break;

								}								
							}
							public void onProviderEnabled(String provider) {}
							public void onLocationChanged(Location location) {
								
								Log.d("","locationchanged in send");
								latitude = location.getLatitude();// 위도
								longitude = location.getLongitude();// 경도
								//자릿수 소숫점 8째 자리까지
								latitude = Common.doubleCutToString(latitude);
								longitude = Common.doubleCutToString(longitude);
								
								Log.d("","locationChanged in="+latitude +" : "+longitude);
//								Toast.makeText(GpsService.this, latitude +" : "+longitude, 1000).show();
								
								//setCurrentPosition(getDBSearch(latitude, longitude));
							}
				    	};
						
				        Criteria criteria = new Criteria();
				        criteria.setAccuracy(Criteria.NO_REQUIREMENT);  		// 정확도//ACCURACY_FINE 일 경우 GPS 를 대부분 선택하게 된다. or NO_REQUIREMENT
				        criteria.setPowerRequirement(Criteria.POWER_LOW); 	// 전원 소비량
				        criteria.setAltitudeRequired(true);    			// 고도, 높이 값을 얻어 올지를 결정
				        criteria.setBearingRequired(true);
				        criteria.setSpeedRequired(true);    				//속도
				        criteria.setCostAllowed(false);      				//위치 정보를 얻어 오는데 들어가는 금전적 비용
				        
				        mLocMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				        mLocMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, gpsMinTime, 0, mLocListener);			
				        
				        GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() { 
				            public void onGpsStatusChanged(int event) { 
				                if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) { 
				                    GpsStatus status = mLocMgr.getGpsStatus(null); 
				                    Iterable<GpsSatellite> sats = status.getSatellites(); 				                    
				                    Iterator<GpsSatellite> iter = sats.iterator();
				                    
				                    GpsSatellite gpsS =  null;
				                    
				                    boolean checkStat = false;
				                    int checkStatCnt = 0;
				                    while(iter.hasNext()){
				                    	gpsS = iter.next();
				                    	
				                    	if(gpsS.usedInFix()){
//				                    		Log.d("GPSInfo", "Almanac : "+gpsS.hasAlmanac());
//				                    		Log.d("GPSInfo", "Ephemeris : "+gpsS.hasEphemeris());
				                    		checkStatCnt++;
				                    	}
				                    }
				                    
				                    if(checkStatCnt > 3){
				                    	checkStat = true;
				                    }else{
				                    	checkStat = false;
				                    }
				                    
				                    if(mGpsSatStat != checkStat){
				                    	mGpsSatStat = checkStat;
				                    	
				                    	try{
				                    		
				                    		if(mLocMgr != null){
						                    	Location l =  mLocMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				                    		}
				                    		
				                    	}catch(Exception ex){
				                    		ex.printStackTrace();
				                    	}
				                    }
				                } 
				            } 
				        };

				        mLocMgr.addGpsStatusListener(gpsStatusListener);
				        String provider = mLocMgr.getBestProvider(criteria, true);
				        
				        Log.d("GPSInfo", "bestProvider : "+provider);
				        nowprovider = provider;
				        
				        if(provider != null){
				        	mLocMgr.requestLocationUpdates(provider, gpsMinTime, 0, mLocListener);
				        }else{
				        	mLocMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, gpsMinTime, 0, mLocListener);
				        }
				        
				    	if(!mLocMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)){
				    		try {

				    			if(contextActivity.isFinishing() == false){
				    				if(gpsDialogFlag == true){
				    					AlertDialog.Builder adb	= new AlertDialog.Builder(contextActivity);
					    				adb.setCancelable(false);
					    				adb.setTitle("스마트정비관리");  
					    				adb.setMessage("GPS가 켜져있지 않습니다.\n지금 켜시겠습니까?");
					    				adb.setPositiveButton("예", new DialogInterface.OnClickListener() {
					    					public void onClick(DialogInterface dialog, int which) {
//					    						Intent i = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//					    						startActivityForResult(i, GPS_INTENT);
					    					}
					    				});
					    				adb.setNegativeButton("아니오", null);
					    				adb.show(); 
				    				}
				    				
				    			}
							} catch (Exception e) {
								e.printStackTrace();
							}
				    	}				    	
					}
		    	});		    			    	
			}
		}).start();
	}
	
	/************************************************************************
	 * GPS 중지
	 ************************************************************************/	
	public static void stopGPS(){
		if(mLocMgr != null)
			mLocMgr.removeUpdates(mLocListener);
	}
	
	// GPS 정보
		static String ns_code = ""; // 노선코드
		static String ns_name = ""; // 노선명
		static String banghyang = ""; // 방향
		static String bhCode = "S"; // 방향코드(S,E)
		static String currentIjung = ""; // 이정
		static String ns_code2 = "";//결과 2개일 경우 노선코드 2
		static String ns_name2 = "";//결과 2개일 경우 노선명 2
		static String currentIjung2 = "";//결과 2개일 경우 이정2
		

		/************************************************************************
		 * GPS 정보에 따른 DB검색 결과 값을 각 변수 대입하고 화면에 표시한다.
		 ************************************************************************/
		private void setCurrentPosition(String dbSearch) {
			// --------------------------------------------------------------------------------------------
			// #region GPS 정보에 따른 DB검색 결과 값을 각 변수 대입하고 화면에 표시한다.
			Log.d("GPSInfo", "setCurrentPosition");

			Log.d("GPSInfo", dbSearch);

			String[] GPSInfo = dbSearch.split("[|]");
			Log.d("GPSInfo", "setCurrentPosition" + GPSInfo.length);
			// GPS 정보가 정상인 경우
			if (GPSInfo.length >= 5) {

				// etLocation.setTextSize(20);

				Log.d("GPSInfo", "GPSInfo[0]" + GPSInfo[0]);
				Log.d("GPSInfo", "GPSInfo[1]" + GPSInfo[1]);
				Log.d("GPSInfo", "GPSInfo[2]" + GPSInfo[2]);

				// 관리 노선인 경우
				if (GPSInfo[0] != null && !GPSInfo[0].equals("")
						&& !GPSInfo[0].startsWith("노선외") && GPSInfo[2] != null) {

					// 이정에 소수점이 없으면 .0 추가
					if (GPSInfo[2].indexOf(".") <= -1) {
						GPSInfo[2] = GPSInfo[2] + ".0";
					} else {
						if (GPSInfo[2].indexOf(".") == GPSInfo[2].length() - 1) {
							GPSInfo[2] = GPSInfo[2] + "0";
						}// end if
					}// end if

					// 위치정보가 update 될 때마다 각 변수에 위치정보 설정함
					ns_name = GPSInfo[0]; // 노선명
					banghyang = GPSInfo[1]; // 방향
					currentIjung = GPSInfo[2]; // 이정
					ns_code = GPSInfo[3]; // 노선코드
				} else {
					ns_name = ""; // 노선명
					banghyang = ""; // 방향
					// currentIjung = "-999"; // 이정
					currentIjung = currentIjung;
					ns_code = ""; // 노선코드
				}// end if
			}// end if

		}
		
		Cursor dbCursor;
		double ppastLatitude;
		double ppastLongitude;
		double pastLatitude;
		double pastLongitude;
		String ppastIjung = "000";
		String pastIjung = "111";
		boolean isInNoseon = true;
		/************************************************************************
		 * GPS 정보에 따른 위치정보를 DB에서 조회 후 리턴 - 방향 기점, 종점 구분 방법 : 이전 좌표의 이정과 현재 이정을 비교
		 * 이전이정 > 현재 이정 = 기점 (경부선: 부산) 이전이정 < 현재 이정 = 종점 (경부선: 서울) : 정지상태에서 GPS 수신하여
		 * 이전이정이 없는 경우 현재 이정 기준에서 먼저 검색되는 방향을 표시 즉, 정지상태에서 비교할 이정이 없는 경우에는 정확한 방향표시
		 * 불가능 함.
		 * 
		 * @param latitude
		 * @param longitude
		 * @return
		 ************************************************************************/
		public String getDBSearch(double latitude, double longitude) {
			// --------------------------------------------------------------------------------------------
			// #region GPS 위경도 좌표에 따른 위치(노선, 방향, 이정, 부근 IC/JCT) 정보를 리턴한다.
			
			Log.v("GPSInfo", "getDBSearch");

			String rtnStr = "";
			// -> 현재 위치가 있다면 현재 위치에 과거 위치값으로 대입
			// -> 현재 위치가 없다면 현재 위치에 현재 위치값으로 대입
			if (!"".equals(currentIjung) && !"-999".equals(currentIjung)) {
				ppastIjung = pastIjung;
				pastIjung = Common.nullCheck(currentIjung);
				if (!pastIjung.equals("")) {
					ppastIjung = pastIjung;
				} else {
					pastIjung = "000";
				}// end if

			} else {
				// currentIjung = "111";
			}// end if

			Log.d("GPSInfo", "latitude : " + latitude);
			Log.d("GPSInfo", "longitude : " + longitude);
			Log.d("GPSInfo", "ns_code : " + ns_code);

			try {
				// private int maxNsEmptyTime = 30;
				// private int moveNsEmptyTime = 4;
				// private int emptyCnt = 0;//좌표정보없음 카운트 초기화
			
				dbCursor = db.fetchRange(latitude, longitude, ns_code);
				
				Log.d("GPSInfo", "getDBSearchCompleted...");
				if (dbCursor.getCount() > 0) {

					// cursor.getCount(); // 총 ROW 수
					// cursor.getColumnCount() // 컬럼 개수

					// 현재 GPS정보에 노선이 2개 이상인 경우 기존 노선 정보를 확인한다.
					String rowResults = "";

					if (dbCursor.getCount() > 1) {
						if (ns_code != null && ns_code.trim().length() > 0) {

							Log.d("GPSInfo", "GPSInfo 결과값 2개이상 기존 노선코드 존재");
							Log.d("GPSInfo", "ns_code : " + ns_code + "|");
							// Log.d("GPSInfo", "dbCursor.getString(0) : " +
							// dbCursor.getString(0) +"|" );

							dbCursor.moveToFirst();
							for (int i = 0; i < dbCursor.getCount(); i++) {

								dbCursor.moveToPosition(i);

								if (ns_code.equals(dbCursor.getString(0))) {

									ns_code = dbCursor.getString(0);
									currentIjung = dbCursor.getString(2);
									ns_name = dbCursor.getString(1);

								}else{
									ns_code2 = dbCursor.getString(0);
									currentIjung2 = dbCursor.getString(2);
									ns_name2 = dbCursor.getString(1);
								}
							}// end for
						} else {

							Log.d("GPSInfo", "GPSInfo 결과값 2개이상 기존 노선코드 없음");
							dbCursor.moveToFirst();

							for (int i = 0; i < dbCursor.getCount(); i++) {
								dbCursor.moveToPosition(i);
								if(i == dbCursor.getCount()-1){
									ns_code = dbCursor.getString(0);
									currentIjung = dbCursor.getString(2);
									ns_name = dbCursor.getString(1);
								}else{
									ns_code2 = dbCursor.getString(0);
									currentIjung2 = dbCursor.getString(2);
									ns_name2 = dbCursor.getString(1);
								}
							}// end for
						}// end if

					} else {
						Log.d("GPSInfo",
								"GPSInfo 결과값 1개 | count : " + dbCursor.getCount());

						// for(int i=0; i<dbCursor.getCount(); i++){

						dbCursor.moveToFirst();

						ns_code = dbCursor.getString(0);
						currentIjung = dbCursor.getString(2);
						ns_name = dbCursor.getString(1);
						ns_code2 = "";
						currentIjung2 = "";
						ns_name2 = "";

						// dbCursor.moveToNext();
						// }

					}// end if

					Log.d("GPSInfo", "GPSInfo ns_code : " + ns_code + "|");
					Log.d("GPSInfo", "GPSInfo currentIjung : " + currentIjung + "|");
					Log.d("GPSInfo", "GPSInfo ns_name : " + ns_name + "|");

					// 방향 목록
					Cursor cursorBangHyang = db.fetchBangHyang(ns_code);

					String gjMyeong = "";
					String jjMyeong = "";
					if (cursorBangHyang.getCount() > 0) {
						cursorBangHyang.moveToFirst();

						gjMyeong = cursorBangHyang.getString(2);
						jjMyeong = cursorBangHyang.getString(3);
					}// end if

					double temp0 = Double.parseDouble(ppastIjung);
					double temp1 = Double.parseDouble(pastIjung);
					double temp2 = Double.parseDouble(currentIjung);

					Log.d("GPSInfo", "GPSInfo temp0" + temp2 + ":" + temp1 + ":"
							+ temp0 + " ::::: " + ns_code);
					// Log.d("GPSInfo", "temp1" + temp1);
					// Log.d("GPSInfo", "temp2" + temp2);
					// 서울외곽순환선
					// <down>판교</down>
					// <upper>구리</upper>
					// <middle>일산</middle>
					if (ns_code.equals("1000")) {
						if (temp2 <= 33) {
							if (temp2 > temp1) {// 현재이정(currentIjung)이
												// 과거이정(pastIjung)보다
								// 크면
								if (temp1 >= temp0) {
									banghyang = common.UPPER_BANGHYANG;
									bhCode = "S";
								}// end if
							} else if (temp2 < temp1) {
								if (temp1 <= temp0) {
									banghyang = common.DOWN_BANGHYANG;
									bhCode = "E";
								}// end if
							}// end if
						} else if (temp2 > 33 && temp2 <= 70) {
							if (temp2 > temp1) {// 현재이정(currentIjung)이
												// 과거이정(pastIjung)보다
								// 크면
								if (temp1 >= temp0) {
									banghyang = common.MIDDLE_BANGHYANG;
									bhCode = "S";
								}// end if
							} else if (temp2 < temp1) {
								if (temp1 <= temp0) {
									banghyang = common.UPPER_BANGHYANG;
									bhCode = "E";
								}// end if
							}// end if
						} else {

							if (temp2 > temp1) {// 현재이정(currentIjung)이
												// 과거이정(pastIjung)보다
								// 크면
								if (temp1 >= temp0) {
									banghyang = common.DOWN_BANGHYANG;
									bhCode = "S";
								}// end if
							} else if (temp2 < temp1) {
								if (temp1 <= temp0) {
									banghyang = common.MIDDLE_BANGHYANG;
									bhCode = "E";
								}// end if
							}// end if
						}// end if
					} else {
						Log.d("", "GPSInfo temp2-1-0 " + temp2 + " : " + temp1
								+ " : " + temp0);
						if (temp2 > temp1) {// 현재이정(currentIjung)이 과거이정(pastIjung)보다
							// 크면
							if (temp1 >= temp0) {
								banghyang = jjMyeong;
								bhCode = "E";
							}// end if
						} else if (temp2 < temp1) {
							if (temp1 <= temp0) {
								bhCode = "S";
								banghyang = gjMyeong;
							}// end if
						}
						// else{
						//
						// Log.d("", "ddddddddddddddd " + bhCode);
						// bhCode = bhCode;
						// }
					}// end if(ns_code.equals("1000")){
					Log.d("", "GPSInfo bhcode = " + bhCode);
					rtnStr = ns_name + "|" + banghyang + "|" + currentIjung + "|"+ ns_code + "|0|" ;

					Log.d("GPSInfo", "Row : " + dbCursor.getPosition() + " => "+ rowResults);
					Log.d("GPSInfo", "rtnStr :  " + rtnStr);

					isInNoseon = true;
				} else {
					// 노선 내에 존재하지 않는 경우
					// 메시지 한 번만 표시
					if (isInNoseon) {
//						Toast.makeText(getApplicationContext(),"현재 위치는 도로공사 관리노선에 해당되지 않습니다.", 5000).show();
						if("".equals(Common.nullCheck(ns_code))){
//							setActivityViewEdit(contextActivity);
							Log.d("",tag+ " setActivityViewEdit 2");
						}
					}// end if

					Log.d("GPSInfo", "mGpsSatStat2:" + mGpsSatStat);

					if (mGpsSatStat) {
						rtnStr = "노선외|_|-999|_|0|0";
					} else {
						rtnStr = "노선외(위성GPS가 활성화되지 \n않아 위치정보가 부정확합니다)|_|-999|_|0|0";
					}// end if

					isInNoseon = false;
				}// end if (dbCursor.getCount() > 0) {

				// 방향코드조회
				 bhCode = db.getBanghyangCode(ns_code, banghyang);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				Log.d("", "GPSInfo rtnStr = " + rtnStr);
				if (dbCursor != null && !dbCursor.isClosed()) {
					dbCursor.close();
				}// end if
			}// end try~catch

			return rtnStr;

			// #endregion
			// --------------------------------------------------------------------------------------------
		}
}
