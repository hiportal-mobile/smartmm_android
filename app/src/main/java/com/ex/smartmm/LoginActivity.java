package com.ex.smartmm;

import java.io.ByteArrayOutputStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ex.smartmm.common.Common;
import com.ex.smartmm.common.Configuration;
import com.ex.smartmm.common.DBAdapter_jangbi;
import com.ex.smartmm.net.Parameters;
import com.ex.smartmm.net.XMLData;
import com.skt.pe.common.data.AuthData;
import com.skt.pe.common.data.SKTUtil;
import com.skt.pe.common.exception.SKTException;
//import com.skt.pe.common.vpn.VPNConnection;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

//ChoiceJakupActivity -> KyoRyangActivity
//ChoiceJakupActivity -> TunnelActivity
//ChoiceJakupActivity -> AmGeoActivity
public class LoginActivity extends Activity implements OnClickListener{
	final String TAG = "ChoiceJakupActivity ";
	ImageView btn_login;
	Common common;
	//DBAdapter db;
	DBAdapter_jangbi jangbiDb;
	
	String mUserID = "";
	String authkey = "";
	String userType = "3";//1.본사, 2.본부, 3.지사
	String companycd = "";
	EditText ed_userid;
	ImageView btn_kr, btn_tn, btn_ag;
	LinearLayout ll_login, ll_choicejakup;
	TextView btn_logout;
	
	//기초코드 최종수신일
	//String lastUptDate = "20180730";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);//choicejakup
		//db = new DBAdapter();
		jangbiDb = new DBAdapter_jangbi();
		Common.setPrefString(LoginActivity.this, Configuration.SHARED_ISVPN, "N");
		
		File f1 = new File(com.ex.smartmm.common.Configuration.dirRoot);//디렉토리경로
		File f2 = new File(com.ex.smartmm.common.Configuration.dirRoot_kr);//디렉토리경로
		File f3 = new File(com.ex.smartmm.common.Configuration.dirRoot);//디렉토리경로
		File f4 = new File(com.ex.smartmm.common.Configuration.dirRoot);//디렉토리경로
		if(!f1.exists()){ f1.mkdir(); }
		if(!f2.exists()){ f2.mkdir(); }
		if(!f3.exists()){ f3.mkdir(); }
		if(!f4.exists()){ f4.mkdir(); }
		
		File f = new File(com.ex.smartmm.common.Configuration.dirRoot_kr);//디렉토리경로
		if(!f.exists()){
			//f.mkdir();
			f.mkdirs();
		}
		
		ed_userid = (EditText) findViewById(R.id.ed_userid);
		
		String mId = getIntent().getStringExtra("ID");
		String mEncPwd = getIntent().getStringExtra("PWD");
		String mLoginMethod = getIntent().getStringExtra("LOGIN_METHOD");
		
		ll_login = (LinearLayout) findViewById(R.id.ll_login);
		ll_choicejakup = (LinearLayout) findViewById(R.id.ll_choicejakup);
		
		Log.d(TAG, "onCreate() - mId = " + mId);
		Log.d(TAG, "onCreate() - mEncPwd = " + mEncPwd);
		Log.d(TAG, "onCreate() - mLoginMethod = " + mLoginMethod);
		
		common = new Common(LoginActivity.this);
		common.copyFile("smartmm.mp4");
		
		btn_login = (ImageView) findViewById(R.id.btn_login);
		btn_login.setOnClickListener(this);
		
		btn_kr = (ImageView) findViewById(R.id.btn_kr);
		btn_tn = (ImageView) findViewById(R.id.btn_tn);
		btn_ag = (ImageView) findViewById(R.id.btn_ag);
		btn_logout = (TextView) findViewById(R.id.btn_logout);
		
		btn_kr.setOnClickListener(this);
		btn_tn.setOnClickListener(this);
		btn_ag.setOnClickListener(this);
		btn_logout.setOnClickListener(this);
	}
	
	@Override
	protected void onPause() {
		//db.close();
		jangbiDb.close();
		super.onPause();
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		//db.init();
		jangbiDb.init();
		
		Log.d("","onResume() - Configuration.SHARED_BTNX : "+Common.getPrefString(LoginActivity.this, Configuration.SHARED_BTNX));
		
		if(!"Y".equals(Common.getPrefString(LoginActivity.this, Configuration.SHARED_BTNX))){//이미 로그인한 사용자일 경우 상태 Y else N
			WifiManager wifiManager = (WifiManager) LoginActivity.this.getSystemService(Context.WIFI_SERVICE);
    		WifiInfo wInfo = wifiManager.getConnectionInfo();
    		String macAddress = wInfo.getMacAddress(); 
    		int NetworkID = wInfo.getNetworkId();
    		String bssid = wInfo.getBSSID();
    		String bssid2 = wInfo.getSSID();
     		Log.d("", "bssid on wifinifo = " + bssid);
    		Log.d("", "bssid on macAddress = " + macAddress);
    		Log.d("", "bssid on NetworkID = " + NetworkID);
    		Log.d("", "bssid on bssid2 = " + bssid2);
    		
			try {
				getGmpAuth();	
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		Common.setPrefString(LoginActivity.this, Configuration.SHARED_BTNX, "N");
		
	}
	
	private SecuwayServiceConnection mConnection = new SecuwayServiceConnection();
    public SecuwayServiceConnection getConnection(){
    	return mConnection;
    }
    
    private class SecuwayServiceConnection implements ServiceConnection{

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			//서비스 바인더  멤버변수로 저장
			Log.d("", TAG+""+"isBindService Check onServiceConnected");
//			ll_login.setVisibility(View.GONE);
//			ll_choicejakup.setVisibility(View.VISIBLE);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d("", TAG+""+"isBindService Check onServiceDisconnected");
//			ll_login.setVisibility(View.VISIBLE);
//			ll_choicejakup.setVisibility(View.GONE);
		}
    }
	
	public void getGmpAuth() throws SKTException{
		Log.d(TAG, "getGmpAuth()~!");
		Map<String, String> map;
		mUserID = "";
		try {
			map = SKTUtil.getGMPAuth(this);
			mUserID = map.get(AuthData.ID_ID); // 운영용
			//mUserID = "20609710";
			//mUserID = "19714814";
			//mUserID = "18713511";
			//mUserID = "20731592";  // 공주지사 
			//mUserID = "19652213";  // 부산경남본부
			//mUserID = "19562255";  // 경주지사
			//mUserID = "18805012";  // 충청본부
			//mUserID = "21115710"; // 수원지사 기계화팀
			//mUserID = "21705223"; // 수원지사 도로안전팀
			//mUserID = "21521391"; // 총무처 총무팀
			//mUserID = "21817324"; // 도로교통연구원 ICT융합연구
			//mUserID = "19509932"; // 대구경북본부 기계화팀
			//mUserID = "20739897"; // 전북본부 기계화팀
			//mUserID = "21202912"; //수도권본부 기획팀
			//mUserID = "21801228"; //서울산지사 도로안전팀
			//mUserID = "21411227"; //전주지사 도로안전팀 최석현 계장
			//mUserID = "20108425"; //고성지사 도로안전팀 권중기 대리
			authkey = map.get(AuthData.ID_AUTH_KEY);
			companycd = map.get(AuthData.ID_COMPANY_CD);
			SKTUtil.getGMPAuthPwd(this);
			
			Log.i(TAG, "AuthData.ID_AUTH_KEY   : " + mUserID);
			Log.i(TAG, "AuthData.ID_AUTH_KEY   : " + map.get(AuthData.ID_AUTH_KEY));
			Log.i(TAG, "AuthData.ID_COMPANY_CD : " + map.get(AuthData.ID_COMPANY_CD));
			Log.i(TAG, "AuthData.ID_ENC_PWD : " + map.get(AuthData.ID_ENC_PWD));
			Log.i(TAG, "AuthData.ID_MDN : " + map.get(AuthData.ID_MDN));
			Log.i(TAG, "AuthData.ID_APP_ID : " + map.get(AuthData.ID_APP_ID));
			if(!"".equals(Common.nullCheck(mUserID))){
				Common.setPrefString(LoginActivity.this, Configuration.SHARED_USERID, mUserID);
				Log.i(TAG, "user id check-getGmpAuth() : " + common.getPrefString(LoginActivity.this, Configuration.SHARED_USERID));
			}
			
			//ed_userid.setText("");//운영용
			ed_userid.setText(mUserID);//운영용
		} catch (SKTException e) {
			e.printStackTrace();
		} finally {
			if(Common.nullCheck(mUserID).equals("")){
				getUserInfo(Common.getPrefString(LoginActivity.this, Configuration.SHARED_USERID), authkey, companycd);
			}else{
				getUserInfo(mUserID, authkey, companycd);
			}
		}
	}
	
	//사용자 정보 가져오기
	public XMLData getUserInfo(String userId, String authkey, String companycd) throws SKTException{
//		http://128.200.121.68:9000/emp_ex/service.pe?authKey=242ea0be477e3e998b4ae5fbb4ef84961093444e&companyCd=EX&encPwd=%2BjBrELHrgWksXVeX8HHSzg%3D%3D&mdn=01040532504&appId=MOCR000004&appVer=3.0.5&lang=ko&groupCd=EX&primitive=COMMON_APP_LAUNCHERMAIN
		Log.d(TAG, "getUserInfo()- userId : " + userId);
		Log.d(TAG, "getUserInfo()- authkey : " + authkey);
		Log.d(TAG, "getUserInfo()- companycd : " + companycd);
		try {
			Parameters params = new Parameters(Configuration.COMMON_SMARTMM_LOGIN);
			Log.d(TAG, "getUserInfo()- params : " + params);
			
			params.put("userId", userId);//운영용
			params.put("cmd", "getSmartOneShotUserInfo");
			//params.put("userId",		"19714814");//테스트 강원지사 이권찬차장
			//params.put("userId",		"20731592");//테스트 공주지사 오성택사원
			//params.put("cmd", "getSmartOneShotUserInfo");
//			params.put("cmd", "getSmartMMUserInfo");
			new Action(Configuration.COMMON_SMARTMM_LOGIN, params).execute("");
			Log.d(TAG, "getUserInfo- userId : " + userId);
			Log.d(TAG, "getUserInfo() - params : " + params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "******************** onActivityResult()~!");
		if(resultCode == RESULT_OK){
			if(requestCode == Configuration.REQUESTCODE_BSSEL){
				Log.d(TAG, TAG+"onActivityResult  =REQUESTCODE_BSSEL ");
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.btn_logout:
//			Common.setPrefString(ChoiceJakupActivity.this, Configuration.SHARED_USERID, "");
//			Common.setPrefString(ChoiceJakupActivity.this, Configuration.SHARED_BSCODE, "");
//			Common.setPrefString(ChoiceJakupActivity.this, Configuration.SHARED_JISACODE, "");
			ll_login.setVisibility(View.VISIBLE);
			ll_choicejakup.setVisibility(View.GONE);
			break;
		case R.id.btn_login:
			try {
				//마스터키 설정. Master Key
				if("555444".equals(ed_userid.getText().toString())){
					//ll_login.setVisibility(View.GONE);
					//ll_choicejakup.setVisibility(View.VISIBLE);
					//intent = new Intent(ChoiceJakupActivity.this, KyoRyangActivity.class);
					intent = new Intent(LoginActivity.this, MainActivity.class);
					startActivity(intent);
				}else{
					Log.d(TAG, "User Id Check = "+ed_userid.getText().toString()+" : "+Common.getPrefString(LoginActivity.this, Configuration.SHARED_USERID));
					if(!ed_userid.getText().toString().equals("")){
						
						if(ed_userid.getText().toString().equals(Common.getPrefString(LoginActivity.this, Configuration.SHARED_USERID))){
							Log.d(TAG, "User Id Check = "+ed_userid.getText().toString()+" == "+Common.getPrefString(LoginActivity.this, Configuration.SHARED_USERID));
							
							if(!"3".equals(Common.getPrefString(LoginActivity.this, Configuration.SHARED_USERTYPE))){
								// 본사, 본부 -> 부서선택 Dialog 띄우기
								Log.d(TAG, "User Id Check - usertype = !3 | "+Common.getPrefString(LoginActivity.this, Configuration.SHARED_USERTYPE));
								Log.d(TAG, "User Id Check - mUserID : "+mUserID);
								Log.d(TAG, "User Id Check - R.id.btn_kr : "+R.id.btn_kr);
								intent = new Intent(LoginActivity.this, DialogActivity_BsSel.class);
								intent.putExtra("btnType", R.id.btn_kr);
								intent.putExtra("userType", userType);
								intent.putExtra("mUserID", mUserID);
								PendingIntent pi = PendingIntent.getActivity(LoginActivity.this, Configuration.REQUESTCODE_BSSEL, intent, PendingIntent.FLAG_ONE_SHOT);
								try {
									pi.send();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}else{
								// 지사 -> 메인화면으로 바로 진입
								Log.d(TAG, "User Id Check - usertype = 3 | "+Common.getPrefString(LoginActivity.this, Configuration.SHARED_USERTYPE));
								//intent = new Intent(ChoiceJakupActivity.this, KyoRyangActivity.class);
								intent = new Intent(LoginActivity.this, MainActivity.class);
								intent.putExtra("mUserID", mUserID);
								startActivity(intent);	
							}
						}else{
							if(Common.getPrefString(LoginActivity.this, Configuration.SHARED_USERID).equals("")){
								AlertDialog.Builder adbLoc	= new AlertDialog.Builder(LoginActivity.this);
								adbLoc.setCancelable(false);
								adbLoc.setTitle(R.string.app_name);
								adbLoc.setMessage("업무접속 연결 후 앱을 재 실행 해주시기 바랍니다.");
								adbLoc.setPositiveButton("확인", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
									}
								});
								adbLoc.show();	
							}else{
								AlertDialog.Builder adbLoc	= new AlertDialog.Builder(LoginActivity.this);
								adbLoc.setCancelable(false);
								adbLoc.setTitle(R.string.app_name);
								adbLoc.setMessage("사용자 정보가 일치하지 않습니다.");
								adbLoc.setPositiveButton("확인", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
									}
								});
								adbLoc.show();
							}
						}
					}else{
						AlertDialog.Builder adbLoc = new AlertDialog.Builder(LoginActivity.this);
						adbLoc.setCancelable(false);
						adbLoc.setTitle(R.string.app_name);
						adbLoc.setMessage("사용자 정보를 입력해 주시기 바랍니다.");
						adbLoc.setPositiveButton("확인", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						});
						adbLoc.show();
					}
				}
				ed_userid.setText("");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			break;
			
		/*case R.id.btn_kr:
			if(!"3".equals(Common.getPrefString(ChoiceJakupActivity.this, Configuration.SHARED_USERTYPE))){
				intent = new Intent(ChoiceJakupActivity.this, DialogActivity_BsSel.class);
				intent.putExtra("btnType", R.id.btn_kr);
				intent.putExtra("userType", userType);
				intent.putExtra("mUserID", mUserID);
				PendingIntent pi = PendingIntent.getActivity(ChoiceJakupActivity.this, Configuration.REQUESTCODE_BSSEL, intent, PendingIntent.FLAG_ONE_SHOT);
				try {
					pi.send();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				intent = new Intent(ChoiceJakupActivity.this, KyoRyangActivity.class);
				intent.putExtra("mUserID", mUserID);
				startActivity(intent);	
			}
			
			break;
			
		case R.id.btn_tn:
			if(!"3".equals(Common.getPrefString(ChoiceJakupActivity.this, Configuration.SHARED_USERTYPE))){
				intent = new Intent(ChoiceJakupActivity.this, DialogActivity_BsSel.class);
				intent.putExtra("btnType", R.id.btn_tn);
				intent.putExtra("userType", userType);
				intent.putExtra("mUserID", mUserID);
				PendingIntent pi = PendingIntent.getActivity(ChoiceJakupActivity.this, Configuration.REQUESTCODE_BSSEL, intent, PendingIntent.FLAG_ONE_SHOT);
				try {
					pi.send();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				intent = new Intent(ChoiceJakupActivity.this, TunnelActivity.class); 
				intent.putExtra("mUserID", mUserID);
				startActivity(intent);				
			}
			break;
			
		case R.id.btn_ag:
			if(!"3".equals(Common.getPrefString(ChoiceJakupActivity.this, Configuration.SHARED_USERTYPE))){
				intent = new Intent(ChoiceJakupActivity.this, DialogActivity_BsSel.class);
				intent.putExtra("btnType", R.id.btn_ag);
				intent.putExtra("userType", userType);
				intent.putExtra("mUserID", mUserID);
				PendingIntent pi = PendingIntent.getActivity(ChoiceJakupActivity.this, Configuration.REQUESTCODE_BSSEL, intent, PendingIntent.FLAG_ONE_SHOT);
				try {
					pi.send();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				intent = new Intent(ChoiceJakupActivity.this, KyoRyangActivity.class); 
				intent.putExtra("mUserID", mUserID);
				startActivity(intent);				
			}
			break;*/
		default:
			break;
		}
	}
	
	public class Action extends AsyncTask<String, Void, JSONObject> {
		// --------------------------------------------------------------------------------------------
		// #region 공통코드 정보 수신
		// 진행 상태 Progressbar
		ProgressDialog progressDialog;
		int responseCode = 0;
		String primitive = "";
		Parameters params = null;

		JSONObject returnData = null;

		@Override
		protected void onPreExecute() {
			Log.d("", TAG+" onPreExecute()~!");
			//String dialogMessage = "";
			Log.d("", TAG+" onPreExecute() - primitive : " + primitive);
			
//			if(primitive.equals("COMMON_SMARTMM_LOGIN")){
			if(primitive.equals("COMMON_SMARTONESHOT_LOGIN")){
				//dialogMessage = "업무접속 연결 확인중...";
				progressDialog = ProgressDialog.show(LoginActivity.this, "", "업무접속 연결 확인중...", true);
			}else if(primitive.equals(Configuration.COMMON_EQUIPMENT_CARUPTDATA)){
				progressDialog = ProgressDialog.show(LoginActivity.this, "", "장비 데이터 정보 수신중...\n잠시만 기다려주세요.", true, false);
			}
			
			super.onPreExecute();
		}

		// primitive 에 따라 URL을 구분짓는다.
		public Action(String primitive, Parameters params) {
			Log.d("", TAG+" Action()~!");
			Log.d("", TAG+"Action() - primitive : " + primitive);
			Log.d("", TAG+"Action() - params : " + params);
			this.primitive = primitive;
			this.params = params;
		}

		@Override
		protected JSONObject doInBackground(String... arg0) {
			Log.d("", TAG+" doInBackground()~!");

			
			HttpURLConnection conn = null;
			JSONObject jsonObject= null;
			JSONArray jsonArray = null;

			OutputStream os = null;
			InputStream is = null;
			ByteArrayOutputStream baos = null;
			
			String date = "";
			try {
				Log.d("", TAG+" doInBackground() - primitive : " + primitive);
				StringBuffer body = new StringBuffer();
//				if(primitive.equals("COMMON_SMARTMM_LOGIN")){
				if(primitive.equals("COMMON_SMARTONESHOT_LOGIN")){
					body.append("http://mg.ex.co.kr/member/member.ex");
					body.append("?");
					body.append(params.toString());
				}else if(primitive.equals(Configuration.COMMON_EQUIPMENT_CARUPTDATA)){
					date = getLastDate();
					//date = "20180901";
					//date = commonMngManager();
					Log.d("", TAG+" doInBackground() - date : " + date);
					
					//params.put("date", "20171102"); // 테스트용
					params.put("date", date); //
					body.append("http://128.200.121.68:9000/emp_sf/service.pe");
					body.append("?");
					body.append(params.toString());
				}
				Log.d("", " url  = " + body.toString());				
				URL url = new URL(new String(Common.nullTrim(body.toString()).getBytes("UTF-8"), "UTF-8"));

				conn = (HttpURLConnection) url.openConnection();

				conn.setRequestMethod("GET");
				conn.setConnectTimeout(60000);
				conn.setReadTimeout(60000);
				conn.setRequestProperty("Cache-Control", "no-cache");
				// conn.setDoOutput(true);
				conn.setDoInput(true);
				Log.d(TAG, TAG+" doinBackground() - conn : "+conn);
				Log.d(TAG, TAG+" doinBackground() - conn.getURL() : "+conn.getURL());
				Log.d(TAG, TAG+" doinBackground() - conn.getErrorStream() : "+conn.getErrorStream());
				Log.d(TAG, TAG+" doinBackground() - conn.getRequestMethod() : "+conn.getRequestMethod());
				Log.d(TAG, TAG+" doinBackground() - conn.getContentType() : "+conn.getContentType());
				Log.d(TAG, TAG+" doinBackground() - conn.getResponseCode() : "+conn.getResponseCode());
				Log.d(TAG, TAG+" doinBackground() - conn.getResponseMessage() : "+conn.getResponseMessage());
				responseCode = conn.getResponseCode();
				Log.d(TAG, TAG+" doinBackground responseCode =  " +responseCode);

				Log.d(TAG, TAG + " ACTION responsecode  " + responseCode+ "----" + conn.getResponseMessage());

				

				Log.d(TAG, TAG + "doInBackground() - responseCode :  " +primitive+" = "+ responseCode);
				
				if (responseCode == HttpURLConnection.HTTP_OK) {
					
					is = conn.getInputStream();
					baos = new ByteArrayOutputStream();
					byte[] byteBuffer = new byte[1024];
					byte[] byteData = null;
					int nLength = 0;

					while ((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
						baos.write(byteBuffer, 0, nLength);
					}

					byteData = baos.toByteArray();
					String response = new String(byteData, "UTF-8");
					
					// String response = new String(byteData);
					//공지사항 인코딩이 다르다
					Log.d("", TAG+"responseData  = " + response);
					if (response == null || response.equals("")) {
						Log.e("", TAG + "Response is NULL!!");
					}
					Map<String, List<String>> headers = conn.getHeaderFields();
					Iterator<String> it = headers.keySet().iterator();
					while (it.hasNext()) {
						String key = it.next();
						List<String> values = headers.get(key);
						StringBuffer sb = new StringBuffer();
						for (int i = 0; i < values.size(); i++) {
							sb.append(";" + values.get(i));
						}
					}
					//db.setDataMngUpdate(date);
					
					try {
						
						if(!primitive.equals(Configuration.COMMON_EQUIPMENT_CARUPTDATA)){
							jsonObject = new JSONObject(response.trim());
							returnData = jsonObject;
						}else{
							jsonObject = new JSONObject();
							jsonObject.put("carList", response.trim());
							returnData = jsonObject;
						}
						return returnData;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			} catch (IOException e) {

			} finally {
				if (os != null) {
					try {
						os.close();
					} catch (Exception e) {
					}
				}

				if (is != null) {
					try {
						is.close();
					} catch (Exception e) {
					}
				}

				if (baos != null) {
					try {
						baos.close();
					} catch (Exception e) {
					}
				}

				if (conn != null) {
					conn.disconnect();
				}
			}
			Log.d(TAG, TAG + "doInBackground() - jsonObject :  " + jsonObject);
			Log.d(TAG, TAG + " doInBackground() - returnData :  " + returnData);
			return jsonObject;
//			return null;
		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(JSONObject jsonObject) {
			Log.d(TAG,"onPostExecute()~!");
			if(null != jsonObject){
				Log.d(TAG,"onPostExecute() = " + jsonObject.toString());
			}

			
			if(null != jsonObject){
				
				//로그인 확인
				if(primitive.equals("COMMON_SMARTONESHOT_LOGIN")){	
					try {
						String user_nm = Common.nullCheck(jsonObject.getString("user_nm"));
						Common.setPrefString(LoginActivity.this, Configuration.SHARED_USERNM, user_nm);
						Log.d(TAG,"xmlData is not null swname = " + user_nm);
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						String jisaCode = Common.nullCheck(jsonObject.getString("dept_cd"));
						String bscode = Common.nullCheck(jsonObject.getString("parent_dept_cd"));
						userType = Common.nullCheck(jsonObject.getString("type"));
						//userType="2";
						Common.setPrefString(LoginActivity.this, Configuration.SHARED_ISVPN, "Y");
						Common.setPrefString(LoginActivity.this, Configuration.SHARED_BSCODE, bscode);
						Common.setPrefString(LoginActivity.this, Configuration.SHARED_BONBUCODE, bscode);
						Common.setPrefString(LoginActivity.this, Configuration.SHARED_JISACODE, jisaCode);
						Common.setPrefString(LoginActivity.this, Configuration.SHARED_USERTYPE, userType);
						
						Log.d(TAG,"getUserInfo xmlData is not null- usertype1 : "+userType);
						Log.d(TAG,"getUserInfo xmlData is not null- usertype2 : "+Common.getPrefString(LoginActivity.this, Configuration.SHARED_USERTYPE));
						Log.d(TAG,"xmlData is not null bscode = " + bscode);
						Log.d(TAG,"xmlData is not null jisacode = " + jisaCode);
						
						Log.d(TAG, "##########@@@@@@@@@@");
						params = new Parameters(Configuration.COMMON_EQUIPMENT_CARUPTDATA);
//						new Action(Configuration.COMMON_EQUIPMENT_CARUPTDATA, params).execute("");
						
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				// 데이터 업데이트 확인
				}else if(primitive.equals(Configuration.COMMON_EQUIPMENT_CARUPTDATA)){
					JSONArray carArray = null;
					try {
						if(responseCode == 200){
							updateLastDate();
							carArray = new JSONArray(jsonObject.getString("carList"));
							new DbUpdater(carArray, progressDialog).execute();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				
			}else{//통신성공 데이타 없을경우
				Log.d(TAG," onPostExecute() xmlData is null");
				Log.d(TAG," onPostExecute() xmlData is null- usertype : "+Common.getPrefString(LoginActivity.this, Configuration.SHARED_USERTYPE));
				Log.d(TAG," onPostExecute() - responseCode : "+responseCode);
				Log.d(TAG," onPostExecute() - primitive : "+primitive);
				
				
				if(responseCode == 200){
					Log.d(TAG,"onPostExecute() - responseCode : "+responseCode);
					Common.setPrefString(LoginActivity.this, Configuration.SHARED_ISVPN, "Y");
					
					
					if("".equals(Common.getPrefString(LoginActivity.this, Configuration.SHARED_JISACODE))){	//인천지사로 default
						Log.d(TAG, "[onPostExecute() - responseCode==200] - jisacode is null");
						Common.setPrefString(LoginActivity.this, Configuration.SHARED_BSCODE, "N01795");//default 부서코드
						Common.setPrefString(LoginActivity.this, Configuration.SHARED_BSCODE, "N01795");//default 부서코드
						Common.setPrefString(LoginActivity.this, Configuration.SHARED_JISACODE, "N00218");//default 지사코드
					}else{
						
						String pfBscode = Common.getPrefString(LoginActivity.this, Configuration.SHARED_BSCODE);
						String pfJisacode = Common.getPrefString(LoginActivity.this, Configuration.SHARED_JISACODE);
						Log.d(TAG, "[onPostExecute() - responseCode==200] pfBscode : "+pfBscode+", pfJisacode : "+pfJisacode);
						Common.setPrefString(LoginActivity.this, Configuration.SHARED_BSCODE, pfBscode);//default 부서코드
						Common.setPrefString(LoginActivity.this, Configuration.SHARED_BONBUCODE, pfBscode);//default 부서코드
						Common.setPrefString(LoginActivity.this, Configuration.SHARED_JISACODE, pfJisacode);//default 부서코드
					}
					
//					ll_login.setVisibility(View.GONE);
//					ll_choicejakup.setVisibility(View.VISIBLE);
					
					//if(primitive.equals("COMMON_SMARTMM_LOGIN")){
/*    				if(primitive.equals("COMMON_SMARTONESHOT_LOGIN")){
						params = new Parameters(Configuration.COMMON_EQUIPMENT_CARUPTDATA);
						//params.put("date", "20171103");
						new Action(Configuration.COMMON_EQUIPMENT_CARUPTDATA, params).execute("");
					}else if(primitive.equals(Configuration.COMMON_EQUIPMENT_CARUPTDATA)){
						Intent intent = new Intent(ChoiceJakupActivity.this, DialogActivity_BsSel.class);
						startActivity(intent);
					}
*/					
					Intent intent = new Intent(LoginActivity.this, DialogActivity_BsSel.class);
					startActivity(intent);
					
					
				}else{//통신 실패시
					Log.d(TAG,"onPostExecute() - 통신실패");
					Common.setPrefString(LoginActivity.this, Configuration.SHARED_ISVPN, "N");
					
					if("".equals(Common.getPrefString(LoginActivity.this, Configuration.SHARED_JISACODE))){
						//Common.setPrefString(ChoiceJakupActivity.this, Configuration.SHARED_BSCODE, "N01759");//default 부서코드
						Common.setPrefString(LoginActivity.this, Configuration.SHARED_BSCODE, "N01795");//default 부서코드
						Common.setPrefString(LoginActivity.this, Configuration.SHARED_BONBUCODE, "N01795");//default 부서코드
						Common.setPrefString(LoginActivity.this, Configuration.SHARED_JISACODE, "N00218");//default 지사코드
					}else{
						String pfBscode = Common.getPrefString(LoginActivity.this, Configuration.SHARED_BSCODE);
						String pfJisacode = Common.getPrefString(LoginActivity.this, Configuration.SHARED_JISACODE);
						Log.d(TAG, "[onPostExecute() - 통신실패] pfBscode : "+pfBscode+", pfJisacode : +"+pfJisacode);
						Common.setPrefString(LoginActivity.this, Configuration.SHARED_BSCODE, pfBscode);//default 부서코드
						Common.setPrefString(LoginActivity.this, Configuration.SHARED_BONBUCODE, pfBscode);//default 부서코드
						Common.setPrefString(LoginActivity.this, Configuration.SHARED_JISACODE, pfJisacode);//default 부서코드
					}
					
    				/*if(primitive.equals("COMMON_SMARTONESHOT_LOGIN")){
						params = new Parameters(Configuration.COMMON_EQUIPMENT_CARUPTDATA);
						//params.put("date", "20171103");
						new Action(Configuration.COMMON_EQUIPMENT_CARUPTDATA, params).execute("");
					}else if(primitive.equals(Configuration.COMMON_EQUIPMENT_CARUPTDATA)){
						ll_login.setVisibility(View.VISIBLE);
						ll_choicejakup.setVisibility(View.GONE);
					}*/
	
					
					ll_login.setVisibility(View.VISIBLE);
					ll_choicejakup.setVisibility(View.GONE);	
				}
				
//				Intent intent = new Intent(ChoiceJakupActivity.this, ChoiceJakupActivity.class);
//				startActivity(intent);
			}

			 if(null != progressDialog){
				 progressDialog.dismiss();
			 }
				//commonMngManager();
		}

		private String commonMngManager(){
			Log.d("",TAG+" commonMngManager()~!");
			SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			long l = System.currentTimeMillis();
			Date localDate = new Date(l);
			String date = localSimpleDateFormat.format(localDate);
			Log.d("",TAG+" commonMngManager() - date : "+date);
			//db.setDataMngUpdate(date);
			jangbiDb.setDataMngUpdate(date);
			//String paramDate = db.getDataMngDate().substring(0, 10).replace("-", "");
			String paramDate = jangbiDb.getDataMngDate().substring(0, 10).replace("-", "");
			
			Log.d("",TAG+" commonMngManager() - paramDate : "+paramDate);
			
			return paramDate;
			
		}
		
		private String getLastDate(){
			Log.d("",TAG+" getLastDate()~!");
			//String paramDate1 = db.getDataMngDate();
			String paramDate1 = jangbiDb.getDataMngDate();
			Log.d("",TAG+" getLastDate() - paramDate1 : "+paramDate1);
			//String paramDate = db.getDataMngDate().substring(0, 10).replace("-", "");
			String paramDate = paramDate1.substring(0, 10).replace("-", "");
			Log.d("",TAG+" getLastDate() - paramDate : "+paramDate);
			Log.d("",TAG+" getLastDate() - date : "+paramDate1 +"//"+paramDate);			
			return paramDate;
			
		}
		
		private void updateLastDate(){
			Log.d("",TAG+" updateLastDate()~!");
			SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			long l = System.currentTimeMillis();
			Date localDate = new Date(l);
			String date = localSimpleDateFormat.format(localDate);
			Log.d("",TAG+" updateLastDate() - date : "+date);
			//db.setDataMngUpdate(date);
			jangbiDb.setDataMngUpdate(date);
		}
	}
	
	public class DbUpdater extends AsyncTask<String, Void, JSONObject> {
		JSONArray carArray;
		ProgressDialog progress;
		
		public DbUpdater(JSONArray carArray, ProgressDialog progress){
			Log.d(TAG, "******************** DbUpdater()~! ********************");
			this.carArray = carArray;
			this.progress = progress;
		}

		@Override
		protected void onPreExecute() {
			// TODO AUTO-GENERATED METHOD STUB
			super.onPreExecute();
			
			
		}
		
		@Override
		protected JSONObject doInBackground(String... arg0) {
			Log.d(TAG, " DbUpdater - doInBackground()");
			if(carArray.length() > 0){
				//db.update_jeongbi(carArray);	
				jangbiDb.update_jeongbi(carArray);
			}
			return null;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			progress.dismiss();
			
			Intent intent;
			// 본사사람 - Dialog 띄우기
			if(!"3".equals(Common.getPrefString(LoginActivity.this, Configuration.SHARED_USERTYPE))){
				Log.d("",TAG+"xmlData is not null- User Id Check4 - usertype =  "+Common.getPrefString(LoginActivity.this, Configuration.SHARED_USERTYPE));
				
				intent = new Intent(LoginActivity.this, DialogActivity_BsSel.class);
				intent.putExtra("btnType", R.id.btn_kr);
				intent.putExtra("userType", userType);
				intent.putExtra("mUserID", mUserID);
				Log.d("",TAG+"xmlData is not null- User Id Check5 - userType =  "+userType);
				Log.d("",TAG+"xmlData is not null- User Id Check5 - mUserID =  "+mUserID);
				PendingIntent pi = PendingIntent.getActivity(LoginActivity.this, Configuration.REQUESTCODE_BSSEL, intent, PendingIntent.FLAG_ONE_SHOT);
				
				try {
					pi.send();
				} catch (Exception e) {
					e.printStackTrace();
					
				}
			}else{ // 지사사람 - Activity 이동
				Log.d("",TAG+"xmlData is not null- User Id Check6 - usertype =  3 |"+Common.getPrefString(LoginActivity.this, Configuration.SHARED_USERTYPE));

				//intent = new Intent(ChoiceJakupActivity.this, KyoRyangActivity.class);
				intent = new Intent(LoginActivity.this, MainActivity.class);
				intent.putExtra("mUserID", mUserID);
				startActivity(intent);	
			}
			
		}
		
	}
	
}
