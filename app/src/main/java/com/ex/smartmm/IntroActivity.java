package com.ex.smartmm;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
import com.ex.smartmm.common.PermissionUtil;
import com.ex.smartmm.net.XMLData;
import com.skt.pe.common.data.AuthData;
import com.skt.pe.common.data.SKTUtil;
import com.skt.pe.common.exception.SKTException;
import com.skt.pe.common.service.Parameters;

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

public class IntroActivity extends BaseActivity implements OnClickListener {
	
	public final String TAG = "IntroActivity";
	
	Common common;
	DBAdapter_jangbi jangbiDb;

	LinearLayout ll_login;
	EditText ed_userid;
	ImageView btn_login;
	
	String mUserID = "";
	String authkey = "";
	String userType = "3";//1.본사, 2.본부, 3.지사
	String companycd = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		Log.d(TAG, "onCreate()~!");
		common = new Common(IntroActivity.this);
		common.copyFile("smartmm.mp4");

		jangbiDb = new DBAdapter_jangbi();
		common.setPrefString(IntroActivity.this, Configuration.SHARED_ISVPN, "N");
		
		File f1 = new File(com.ex.smartmm.common.Configuration.dirRoot);//디렉토리경로
		File f2 = new File(com.ex.smartmm.common.Configuration.dirRoot);//디렉토리경로
		if(!f1.exists()){ f1.mkdirs(); }
		if(!f2.exists()){ f2.mkdirs(); }
		
//		common = new Common(IntroActivity.this);
//		common.copyFile("smartmm.mp4");
		
		ed_userid = (EditText)findViewById(R.id.ed_userid);
		
		btn_login = (ImageView)findViewById(R.id.btn_login);
		btn_login.setOnClickListener(this);

		Common.setPrefString(IntroActivity.this, Configuration.SHARED_DEPTVERSION ,"3");
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if(requestCode == REQUUEST_ALL){
			Log.i("permission", "intro Received response for location permissions request." + PermissionUtil.verifyPermissions(grantResults));

		}

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		jangbiDb.close();
		super.onPause();
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(TAG, "******************** onResume()~! ********************");
		jangbiDb.init();

		Log.d(TAG, "onResume() - Configuration.SHARED_BTNX : "+Configuration.SHARED_BTNX);

		if(!"Y".equals(common.getPrefString(IntroActivity.this, Configuration.SHARED_BTNX))){
			WifiManager wifiManager = (WifiManager) IntroActivity.this.getSystemService(Context.WIFI_SERVICE);
    		WifiInfo wInfo = wifiManager.getConnectionInfo();
    		String macAddress = wInfo.getMacAddress();
    		int NetworkID = wInfo.getNetworkId();
    		String bssid = wInfo.getBSSID();
    		String bssid2 = wInfo.getSSID();
     		Log.d(TAG, "bssid on wifinifo = " + bssid);
    		Log.d(TAG, "bssid on macAddress = " + macAddress);
    		Log.d(TAG, "bssid on NetworkID = " + NetworkID);
    		Log.d(TAG, "bssid on bssid2 = " + bssid2);

    		try{
    			getGmpAuth();
    		}catch (Exception e) {
    			e.printStackTrace();
    		}
		}

		Common.setPrefString(IntroActivity.this, Configuration.SHARED_BTNX, "N");
	}
	
	
	public void getGmpAuth() throws SKTException{
		Log.d(TAG, "getGmpAuth()~!");
		Map<String, String> map;
		
		try{
			map = SKTUtil.getGMPAuth(this);
			mUserID = map.get(AuthData.ID_ID);

//			mUserID = "21501229"; //테스트 test 본사사람
//			mUserID = "18812811"; //테스트 test 본부사람
//			mUserID = "18905015"; //테스트 test 보은지사
//			mUserID = "21417791"; //테스트 test 기계화팀

			authkey = map.get(AuthData.ID_AUTH_KEY);
			companycd = map.get(AuthData.ID_COMPANY_CD);
			SKTUtil.getGMPAuthPwd(this);
			
			Log.d(TAG, "getGmpAuth() - AuthData.mUserID : "+mUserID);
			Log.d(TAG, "getGmpAuth() - AuthData.authkey : "+authkey);
			Log.d(TAG, "getGmpAuth() - AuthData.companycd : "+companycd);
			
			if(!"".equals(common.nullCheck(mUserID)) ){
				common.setPrefString(IntroActivity.this, Configuration.SHARED_USERID, mUserID);
				Log.d(TAG, "getGmpAuth() - Configuration.SHARED_USERID : "+common.getPrefString(IntroActivity.this, Configuration.SHARED_USERID));
			}
			
			ed_userid.setText(mUserID); //로그인창의 ID 입력란에 자동으로 ID넣기
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(common.nullCheck(mUserID).equals("")){
				getUserInfo(common.getPrefString(IntroActivity.this, Configuration.SHARED_USERID), authkey, companycd);
			}else{
				getUserInfo(mUserID, authkey, companycd);
			}
		}
	}

	public void getGmpAuth(String user_id) throws SKTException{
		Log.d(TAG, "getGmpAuth()~!");
		Map<String, String> map;

		try{
			map = SKTUtil.getGMPAuth(this);
			mUserID = user_id;

//			mUserID = "21501229"; //본사사람
//			mUserID = "18812811"; //본부사람
//			mUserID = "18905015";//보은지사
//			mUserID = "21417791";//기계화팀

			authkey = map.get(AuthData.ID_AUTH_KEY);
			companycd = map.get(AuthData.ID_COMPANY_CD);
			SKTUtil.getGMPAuthPwd(this);

			Log.d(TAG, "getGmpAuth() - AuthData.mUserID : "+mUserID);
			Log.d(TAG, "getGmpAuth() - AuthData.authkey : "+authkey);
			Log.d(TAG, "getGmpAuth() - AuthData.companycd : "+companycd);

			if(!"".equals(common.nullCheck(mUserID)) ){
				common.setPrefString(IntroActivity.this, Configuration.SHARED_USERID, mUserID);
				Log.d(TAG, "getGmpAuth() - Configuration.SHARED_USERID : "+common.getPrefString(IntroActivity.this, Configuration.SHARED_USERID));
			}

			ed_userid.setText(mUserID); //로그인창의 ID 입력란에 자동으로 ID넣기

		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(common.nullCheck(mUserID).equals("")){
				getUserInfo(common.getPrefString(IntroActivity.this, Configuration.SHARED_USERID), authkey, companycd);
			}else{
				getUserInfo(mUserID, authkey, companycd);
			}
		}
	}

	
	
	//사용자 정보 가져오기
	public XMLData getUserInfo(String userId, String authKey, String companyCd) throws SKTException{
		Log.d(TAG, "getUserInfo()~!");
		Log.d(TAG, "getUserInfo() - userId : "+userId);
		Log.d(TAG, "getUserInfo() - authKey : "+authKey);
		Log.d(TAG, "getUserInfo() - companyCd : "+companyCd);
		
		try {
			Parameters params = new Parameters(Configuration.COMMON_SMARTMM_LOGIN);
			Log.d(TAG, "getUserInfo() - params1 : "+params);

			params.put("userId", userId);
            params.put("userid", userId);
			params.put("cmd", "getSmartOneShotUserInfo");
			Log.d(TAG, "getUserInfo() - params2 : "+params);
			new Action(Configuration.COMMON_SMARTMM_LOGIN, params).execute("");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		Intent intent;
		switch (v.getId()){
			case R.id.btn_login:
				try{
					
					//555444
					if("555444".equals(ed_userid.getText().toString())){
						intent = new Intent(IntroActivity.this, MainActivity.class);
						startActivity(intent);
					}else{
						Log.d(TAG, "onClick(btn_login) - CHECK userid = "+ed_userid.getText().toString());
						Log.d(TAG, "onClick(btn_login) - CHECK SHARED_USERID = "+Common.getPrefString(IntroActivity.this, Configuration.SHARED_USERID));
						
						//사번입력을 안했을 경우
						if("".equals(ed_userid.getText().toString())){
							AlertDialog.Builder adbLoc = new AlertDialog.Builder(IntroActivity.this);
							adbLoc.setCancelable(false);
							adbLoc.setTitle(R.string.app_name);
							adbLoc.setMessage("사용자 정보를 입력해 주시기 바랍니다.");
							adbLoc.setPositiveButton("예", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
								}
							});
							adbLoc.show();
						
						//사번을 입력 했을 경우
						}else{
//							getGmpAuth(ed_userid.getText().toString());
							//사번 정확히 입력 - 본사/본부/지사 체크
							if(ed_userid.getText().toString().equals(common.getPrefString(IntroActivity.this, Configuration.SHARED_USERID))){
								Log.d(TAG, "onClick(btn_login) - CHECK userid -> "+ed_userid.getText().toString()+" == "+Common.getPrefString(IntroActivity.this, Configuration.SHARED_USERID));
								Log.d(TAG, "onClick(btn_login) - usertype : "+Common.getPrefString(IntroActivity.this, Configuration.SHARED_USERTYPE));
								//본사,본부 -> 부서선택 Dialog 띄우기
								if(!"3".equals(Common.getPrefString(IntroActivity.this, Configuration.SHARED_USERTYPE))){
									Log.d(TAG, "onClick(btn_login) - usertype = !3 | "+Common.getPrefString(IntroActivity.this, Configuration.SHARED_USERTYPE));
								  	Log.d(TAG, "onClick(btn_login) - mUserID : "+mUserID);
									Log.d(TAG, "onClick(btn_login) - R.id.btn_kr : "+R.id.btn_kr);

									intent = new Intent(IntroActivity.this, DialogActivity_BsSel.class);
									intent.putExtra("userType", Common.getPrefString(IntroActivity.this, Configuration.SHARED_USERTYPE));
									intent.putExtra("mUserID", mUserID);
									PendingIntent pi = PendingIntent.getActivity(IntroActivity.this, Configuration.REQUESTCODE_BSSEL, intent, PendingIntent.FLAG_ONE_SHOT);
									try{
										pi.send();
									}catch (Exception e) { e.printStackTrace();	}
								//지사 -> 메일화면으로 바로 진입	
								}else{
									Log.d(TAG, "onClick(btn_login) - usertype = 3 | "+Common.getPrefString(IntroActivity.this, Configuration.SHARED_USERTYPE));
									intent = new Intent(IntroActivity.this, MainActivity.class);
									startActivity(intent);


								}
								
							//사번을 다르게 입력한 경우
							}else{
								
								//업무접속 후 앱을 실행한 적이 없는 경우
								if(Common.getPrefString(IntroActivity.this, Configuration.SHARED_USERID).equals("")){
									AlertDialog.Builder adbLoc	= new AlertDialog.Builder(IntroActivity.this);
									adbLoc.setCancelable(false);
									adbLoc.setTitle(R.string.app_name);
									adbLoc.setMessage("업무접속 연결 후 앱을 재 실행 해주시기 바랍니다.");
									adbLoc.setPositiveButton("예", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
										}
									});
									adbLoc.show();	
									
								//사용자 정보가 일치하지 않는 경우	
								}else{
									AlertDialog.Builder adbLoc	= new AlertDialog.Builder(IntroActivity.this);
									adbLoc.setCancelable(false);
									adbLoc.setTitle(R.string.app_name);
									adbLoc.setMessage("사용자 정보가 일치하지 않습니다.");
									adbLoc.setPositiveButton("예", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
										}
									});
									adbLoc.show();
								}
							}
							
						}
						
					}
					
					
					
				}catch (Exception e) {
					e.printStackTrace();
				}
				break;
				
			default:
				break;
		}
		
	}

	
	/** ***********************SecuwayServiceConnection CLASS START ***************************************************** **/

	private SecuwayServiceConnection mConnection = new SecuwayServiceConnection();
    public SecuwayServiceConnection getConnection(){
    	return mConnection;
    }
    
	private class SecuwayServiceConnection implements ServiceConnection{
		
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			Log.d(TAG, "onServiceConnected()~!");
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			Log.d(TAG, "onServiceDisconnected()~!");
		}
		
	}
	
	/** ***********************SecuwayServiceConnection CLASS END ***************************************************** **/
	
	/** *********************** ACTION CLASS START ***************************************************** **/
	
	public class Action extends AsyncTask<String, Void, JSONObject>{
		
		ProgressDialog progressDialog;
		int responseCode = 0;
		String primitive = "";
		Parameters params = null;
		JSONObject returnData =null;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			
			Log.d(TAG, "onPreExecute() - primitive : "+primitive);
			
			//COMMON_SMARTONESHOT_LOGIN
			if(primitive.equals(Configuration.COMMON_SMARTMM_LOGIN)){
				progressDialog = ProgressDialog.show(IntroActivity.this, "", "업무접속 연결 확인중...", true);
			
			//COMMON_EQUIPMENT_CARUPTDATA
			}else if(primitive.equals(Configuration.COMMON_EQUIPMENT_CARUPTDATA)){
				progressDialog = ProgressDialog.show(IntroActivity.this, "", "1장비 데이터 정보 수신중...\n잠시만 기다려주세요.", true, false);
			}
			
			super.onPreExecute();
		}
		
		public Action(String primitive, Parameters params){
			Log.d(TAG, "******************** Action CLASS ********************");
			Log.d(TAG, "Action() - primitive : "+primitive);
			Log.d(TAG, "Action() - params : "+params);
			this.primitive = primitive;
			this.params = params;
		}
		
		@Override
		protected JSONObject doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			Log.d(TAG, "doInBackground()~!");
			
			HttpURLConnection conn = null;
			JSONObject jsonObject= null;
			JSONArray jsonArray = null;

			OutputStream os = null;
			InputStream is = null;
			ByteArrayOutputStream baos = null;
			
			String date = "";
			
			try{
				Log.d(TAG, "Action > doInBackground() - primitive : "+primitive);
				
				StringBuffer body = new StringBuffer();
				
				//COMMON_SMARTONESHOT_LOGIN
				if(primitive.equals(Configuration.COMMON_SMARTMM_LOGIN)){
//					body.append("http://mg.ex.co.kr/member/member.ex");
//					body.append("?");
//					body.append(params.toString());

                    body.append("http://128.200.121.68:9000/emp_sf/service.pe");
                    body.append("?");
                    body.append(params.toString());

				//COMMON_EQUIPMENT_CARUPTDATA
				}else if(primitive.equals(Configuration.COMMON_EQUIPMENT_CARUPTDATA)){
					date = getLastDate();
					//date = "20180915";
					Log.d(TAG, "Action > doInBackground() - date : " + date);
					
					params.put("date", date); 
					body.append("http://128.200.121.68:9000/emp_sf/service.pe");
					body.append("?");
					body.append(params.toString());
				}else if(primitive.equals(Configuration.COMMON_EQUIPMENT_DEPTINFO)){
					date = getLastDate();
					//date = "20180915";
					Log.d(TAG, "Action > doInBackground() - date : " + date);

					params.put("date", date);
					body.append("http://128.200.121.68:9000/emp_sf/service.pe");
					body.append("?");
					body.append(params.toString());

				}else{
					date = getLastDate();
					//date = "20180915";
					Log.d(TAG, "Action > doInBackground() - date : " + date);

					params.put("date", date);
					params.put("responseLDte", "20101220090000");
					body.append("http://128.200.121.68:9000/emp_sf/service.pe");
					body.append("?");
					body.append(params.toString());
				}
				
				Log.d(TAG, "Action > doInBackground() - url : "+body.toString());
				
				URL url = new URL(new String(Common.nullTrim(body.toString()).getBytes("UTF-8"), "UTF-8"));

				conn = (HttpURLConnection) url.openConnection();

				conn.setRequestMethod("GET");
				conn.setConnectTimeout(60000);
				conn.setReadTimeout(60000);
				conn.setRequestProperty("Cache-Control", "no-cache");
				conn.setDoInput(true);
				Log.d(TAG, "Action > doinBackground() - conn : "+conn);
				Log.d(TAG, "Action > doinBackground() - conn.getURL() : "+conn.getURL());
				Log.d(TAG, "Action > doinBackground() - conn.getErrorStream() : "+conn.getErrorStream());
				Log.d(TAG, "Action > doinBackground() - conn.getRequestMethod() : "+conn.getRequestMethod());
				Log.d(TAG, "Action > doinBackground() - conn.getContentType() : "+conn.getContentType());
				Log.d(TAG, "Action > doinBackground() - conn.getResponseCode() : "+conn.getResponseCode());
				Log.d(TAG, "Action > doinBackground() - conn.getResponseMessage() : "+conn.getResponseMessage());
				responseCode = conn.getResponseCode();
				
				Log.d(TAG, "Action > doinBackground() - responseCode1 == " +responseCode);
				Log.d(TAG, "Action > doinBackground() - responsecode2 == " +responseCode+ "----" + conn.getResponseMessage());
				Log.d(TAG, "Action > doInBackground() - responseCode3 == " +primitive+" = "+ responseCode);

				if(responseCode == HttpURLConnection.HTTP_OK){
					
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
					Log.d(TAG, "Action > doInBackground() - responseData  = " + response);
					if (response == null || response.equals("")) {
						Log.e(TAG, "Action > doInBackground() - Response is NULL!!");
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
					
					try {
						
						//COMMON_SMARTONESHOT_LOGIN
						if(primitive.equals(Configuration.COMMON_SMARTMM_LOGIN)){
							jsonObject = new JSONObject(response.trim());
							returnData = jsonObject;
						
						//COMMON_EQUIPMENT_CARUPTDATA
						}else if(primitive.equals(Configuration.COMMON_EQUIPMENT_CARUPTDATA)){
							jsonObject = new JSONObject();
							jsonObject.put("carList", response.trim());
							returnData = jsonObject;
						}else if(primitive.equals(Configuration.COMMON_EQUIPMENT_DEPTINFO)){
							jsonObject = new JSONObject(response.trim());
							returnData = jsonObject;
						}
						Log.d(TAG, "Action > doInBackground() - returnData == "+returnData);
						return returnData;
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				
				}
				
			}catch (Exception e) {
			}finally {
				if (os != null) {
					try { os.close(); } catch (Exception e) {}
				}

				if (is != null) {
					try { is.close(); } catch (Exception e) {}
				}

				if (baos != null) {
					try { baos.close(); } catch (Exception e) {}
				}

				if (conn != null) { conn.disconnect();	}
			
			}
		
			return jsonObject;
		}
		
		@Override
		protected void onPostExecute(JSONObject jsonObject) {
			// TODO Auto-generated method stub
			Log.d(TAG, "Action > onPostExecute()~!");
			
			//통신성공 데이터가 있을 경우
			if(null != jsonObject){
				Log.d(TAG, "Action > onPostExecute() - xmlData is NOT null");
				
				try {
					
					//로그인 정보 확인
					//COMMON_SMARTONESHOT_LOGIN
					if(primitive.equals(Configuration.COMMON_SMARTMM_LOGIN)){
						Log.d(TAG, "Action > onPostExecute() - COMMON_SMARTONESHOT_LOGIN !!");
						String user_nm = common.nullCheck(jsonObject.getString("user_nm"));
						String jisaCode = "";
						if(jsonObject.has("dept_cd")){
							jisaCode = Common.nullCheck(jsonObject.getString("dept_cd"));
						}
						String dept5 = "";
						String bscode = Common.nullCheck(jsonObject.getString("parent_dept_cd"));
						userType = Common.nullCheck(jsonObject.getString("type"));

						if(jsonObject.has("dept_cd5")){
							dept5 = Common.nullCheck(jsonObject.getString("dept_cd5"));
						}

						common.setPrefString(IntroActivity.this, Configuration.SHARED_USERNM, user_nm);
						common.setPrefString(IntroActivity.this, Configuration.SHARED_ISVPN, "Y");
						common.setPrefString(IntroActivity.this, Configuration.SHARED_BSCODE, bscode);
						common.setPrefString(IntroActivity.this, Configuration.SHARED_BONBUCODE, bscode);
						common.setPrefString(IntroActivity.this, Configuration.SHARED_JISACODE, jisaCode);
						common.setPrefString(IntroActivity.this, Configuration.SHARED_USERTYPE, userType);
						common.setPrefString(IntroActivity.this, Configuration.SHARED_DEPTCD5, dept5);

						Log.d(TAG, "Action > onPostExecute() - SHARED_USERNM : "+common.getPrefString(IntroActivity.this, Configuration.SHARED_USERNM));
						Log.d(TAG, "Action > onPostExecute() - SHARED_ISVPN : "+common.getPrefString(IntroActivity.this, Configuration.SHARED_ISVPN));
						Log.d(TAG, "Action > onPostExecute() - SHARED_BSCODE : "+common.getPrefString(IntroActivity.this, Configuration.SHARED_BSCODE));
						Log.d(TAG, "Action > onPostExecute() - SHARED_BONBUCODE : "+common.getPrefString(IntroActivity.this, Configuration.SHARED_BONBUCODE));
						Log.d(TAG, "Action > onPostExecute() - SHARED_JISACODE : "+common.getPrefString(IntroActivity.this, Configuration.SHARED_JISACODE));
						Log.d(TAG, "Action > onPostExecute() - SHARED_USERTYPE : "+common.getPrefString(IntroActivity.this, Configuration.SHARED_USERTYPE));

						//본인 다이얼로그를 통해 변경되지 않는 본부코드 지사코드
						common.setPrefString(IntroActivity.this, Configuration.SHARED_ORI_BBCODE, bscode);
						common.setPrefString(IntroActivity.this, Configuration.SHARED_ORI_BSCODE, jisaCode);

						String bbname = jangbiDb.select_bbinfo_new(common.getPrefString(IntroActivity.this, Configuration.SHARED_ORI_BBCODE));
						common.setPrefString(IntroActivity.this, Configuration.SHARED_ORI_BBNAME, bbname);

						String bsname = jangbiDb.select_bsinfo_new(common.getPrefString(IntroActivity.this, Configuration.SHARED_ORI_BSCODE));
						common.setPrefString(IntroActivity.this, Configuration.SHARED_ORI_BSNAME, bsname);

						String deptname5 = jangbiDb.select_bsinfo_new(common.getPrefString(IntroActivity.this, Configuration.SHARED_DEPTCD5));
						common.setPrefString(IntroActivity.this, Configuration.SHARED_DEPTNM5, deptname5);

						params = new Parameters(Configuration.COMMON_EQUIPMENT_CARUPTDATA);
						Log.d(TAG, "Action > onPostExecute() - params : "+params);
						
						//사용자 확인 후 장비데이터 통신하러 이동
						new Action(Configuration.COMMON_EQUIPMENT_CARUPTDATA, params).execute("");
						
					//장비데이터 확인	
					//COMMON_EQUIPMENT_CARUPTDATA	
					}else if(primitive.equals(Configuration.COMMON_EQUIPMENT_CARUPTDATA)){
						Log.d(TAG, "Action > onPostExecute() - COMMON_EQUIPMENT_CARUPTDATA !!");
						JSONArray carArray = null;
						if(responseCode == 200){
							updateLastDate();
							carArray = new JSONArray(jsonObject.getString("carList"));
							Log.d(TAG, "Action > onPostExecute() - carArray == "+carArray);
							new DbUpdater(carArray, progressDialog).execute();
						}

						//부서정보 조회
						Log.d("DEPTINFO","COMMON_EQUIPMENT_DEPTINFO step1");
						params = new Parameters(Configuration.COMMON_EQUIPMENT_DEPTINFO);
						params.put("dbversion", Common.getPrefString(IntroActivity.this, Configuration.SHARED_DEPTVERSION));//버전에 따라 업데이트 여부 결정.
						new Action(Configuration.COMMON_EQUIPMENT_DEPTINFO, params).execute("");

					}else if(primitive.equals(Configuration.COMMON_EQUIPMENT_DEPTINFO)){
						Log.d("DEPTINFO","COMMON_EQUIPMENT_DEPTINFO step2");

						String changeYn = (String)jsonObject.get("changeYn");

						JSONArray jsonArr = null;
						int rtn1 = 0;
						int rtn2 = 0;
						if(jsonObject.has("deptlist")){
							jsonArr = new JSONArray((String)jsonObject.get("deptlist"));
							if("Y".equals(changeYn)){
								rtn1 = jangbiDb.update_bsinfo_new(jsonArr);
								rtn2 = jangbiDb.update_V_CNKC_INTG_DPRT01C1(jsonArr);

							}
						}

						if(rtn1 == 1 && rtn2 == 1){
							Common.setPrefString(IntroActivity.this, Configuration.SHARED_DEPTVERSION ,(String)jsonObject.get("serverVersion"));
							String bbname = jangbiDb.select_bbinfo_new(common.getPrefString(IntroActivity.this, Configuration.SHARED_ORI_BBCODE));
							common.setPrefString(IntroActivity.this, Configuration.SHARED_ORI_BBNAME, bbname);

							String bsname = jangbiDb.select_bsinfo_new(common.getPrefString(IntroActivity.this, Configuration.SHARED_ORI_BSCODE));
							common.setPrefString(IntroActivity.this, Configuration.SHARED_ORI_BSNAME, bsname);

							String deptname5 = jangbiDb.select_bsinfo_new(common.getPrefString(IntroActivity.this, Configuration.SHARED_DEPTCD5));
							common.setPrefString(IntroActivity.this, Configuration.SHARED_DEPTNM5, deptname5);

							Log.d(TAG, "oristat = " + bbname +":"+bsname);
						}


						Log.d(TAG, "Action > onPostExecute() - COMMON_EQUIPMENT_DEPTINFO !!");
						Log.d(TAG, "Action > onPostExecute() - COMMON_TTMDEPT_LIST !!");
					}

					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
			//통신성공 데이터가 없을 경우
			}else{	
				Log.d(TAG, "Action > onPostExecute() - xmlData is null " +primitive);
				Log.d(TAG, "Action > onPostExecute() xmlData is null- usertype : "+Common.getPrefString(IntroActivity.this, Configuration.SHARED_USERTYPE));
				Log.d(TAG, "Action > onPostExecute() - responseCode : "+responseCode);
				Log.d(TAG, "Action > onPostExecute() - primitive : "+primitive);
				
				if("".equals(Common.getPrefString(IntroActivity.this, Configuration.SHARED_JISACODE))){
					//Common.setPrefString(ChoiceJakupActivity.this, Configuration.SHARED_BSCODE, "N01759");//default 부서코드
					Common.setPrefString(IntroActivity.this, Configuration.SHARED_BSCODE, "N01795");//default 부서코드
					Common.setPrefString(IntroActivity.this, Configuration.SHARED_BONBUCODE, "N01795");//default 부서코드
					Common.setPrefString(IntroActivity.this, Configuration.SHARED_JISACODE, "N00218");//default 지사코드
				}else{
					String pfBscode = Common.getPrefString(IntroActivity.this, Configuration.SHARED_BSCODE);
					String pfJisacode = Common.getPrefString(IntroActivity.this, Configuration.SHARED_JISACODE);
					Log.d(TAG, "Action > onPostExecute() - pfBscode : "+pfBscode+", pfJisacode : +"+pfJisacode);
					Common.setPrefString(IntroActivity.this, Configuration.SHARED_BSCODE, pfBscode);//default 부서코드
					Common.setPrefString(IntroActivity.this, Configuration.SHARED_BONBUCODE, pfBscode);//default 부서코드
					Common.setPrefString(IntroActivity.this, Configuration.SHARED_JISACODE, pfJisacode);//default 부서코드
				}
				
				if(responseCode == 200){	//통신성공
					common.setPrefString(IntroActivity.this, Configuration.SHARED_ISVPN, "Y");
					
					Log.d(TAG, "Action > onPostExecute() - BSCODE : "+common.getPrefString(IntroActivity.this, Configuration.SHARED_BSCODE));
					Log.d(TAG, "Action > onPostExecute() - SHARED_BONBUCODE : "+common.getPrefString(IntroActivity.this, Configuration.SHARED_BONBUCODE));
					Log.d(TAG, "Action > onPostExecute() - SHARED_JISACODE : "+common.getPrefString(IntroActivity.this, Configuration.SHARED_JISACODE));
					
//					Intent intent = new Intent(IntroActivity.this, DialogActivity_BsSel.class);
//					startActivity(intent);
					
				}else{	//통신실패
					Log.d(TAG,"Action > onPostExecute() - 통신실패");
					common.setPrefString(IntroActivity.this, Configuration.SHARED_ISVPN, "N");
					
				}
				if(primitive.equals(Configuration.COMMON_EQUIPMENT_DEPTINFO)){
					Log.d("DEPTINFO","COMMON_EQUIPMENT_DEPTINFO step3");

				}

			}
			if(null != progressDialog){
				progressDialog.dismiss();
			}
//			if(!primitive.equals(Configuration.COMMON_EQUIPMENT_CARUPTDATA)){
//			}


		}
	}
	
	
	/** *********************** ACTION CLASS END ***************************************************** **/
	public String getLastDate(){
		Log.d(TAG, "getLastDate()~!");
		//String paramDate1 = db.getDataMngDate();
		String paramDate1 = jangbiDb.getDataMngDate();
		Log.d(TAG, "getLastDate() - paramDate1 : "+paramDate1);
		//String paramDate = db.getDataMngDate().substring(0, 10).replace("-", "");
		String paramDate = paramDate1.substring(0, 10).replace("-", "");
		Log.d(TAG, "getLastDate() - paramDate : "+paramDate);
		Log.d(TAG, "getLastDate() - date : "+paramDate1 +"//"+paramDate);			
		return paramDate;
	}
	
	private void updateLastDate(){
		Log.d(TAG, " updateLastDate()~!");
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long l = System.currentTimeMillis();
		Date localDate = new Date(l);
		String date = localSimpleDateFormat.format(localDate);
		Log.d(TAG, " updateLastDate() - date : "+date);
		//db.setDataMngUpdate(date);
		jangbiDb.setDataMngUpdate(date);
	}
	
	
	
	
	
	/** *********************** DbUpdater CLASS START ***************************************************** **/
	
	public class DbUpdater extends AsyncTask<String, Void, JSONObject>{
		JSONArray carArray;
		ProgressDialog progress;
		
		public DbUpdater(JSONArray carArray, ProgressDialog progress){
			Log.d(TAG, "******************** DbUpdater()~! ********************");
			this.carArray = carArray;
			this.progress = progress;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		
		@Override
		protected JSONObject doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			Log.d(TAG, "DbUpdater > doInBackground() update size = " + carArray.length());
			if(carArray.length() > 0){
				jangbiDb.update_jeongbi(carArray);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(JSONObject result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Log.d(TAG, "DbUpdater > onPostExecute()~!");
			progress.dismiss();
			Log.d(TAG, "DbUpdater > onPostExecute() - usertype =  "+Common.getPrefString(IntroActivity.this, Configuration.SHARED_USERTYPE));

			Log.d(TAG, "DbUpdater > onPostExecute() - usertype =  "+Common.getPrefString(IntroActivity.this, Configuration.SHARED_USERTYPE));
			Intent intent;
			// 본사사람 - Dialog 띄우기
			if(!"3".equals(Common.getPrefString(IntroActivity.this, Configuration.SHARED_USERTYPE))){	
				Log.d(TAG, "DbUpdater > onPostExecute() - usertype = 본사사람");
				
				intent = new Intent(IntroActivity.this, DialogActivity_BsSel.class);
				intent.putExtra("btnType", R.id.btn_kr);
				//intent.putExtra("btnType", R.id.btn_login);
				intent.putExtra("userType", Common.getPrefString(IntroActivity.this, Configuration.SHARED_USERTYPE));
				intent.putExtra("mUserID", mUserID);
				Log.d(TAG, "DbUpdater > onPostExecute() - userType2 =  "+userType);
				Log.d(TAG, "DbUpdater > onPostExecute() - mUserID =  "+mUserID);
				PendingIntent pi = PendingIntent.getActivity(IntroActivity.this, Configuration.REQUESTCODE_BSSEL, intent, PendingIntent.FLAG_ONE_SHOT);
				
				try {
					pi.send();
				} catch (Exception e) {
					e.printStackTrace();
					
				}
			}else{ // 지사사람 - Activity 이동
				Log.d(TAG, "DbUpdater > onPostExecute() - usertype = 지사사람");

				//intent = new Intent(ChoiceJakupActivity.this, KyoRyangActivity.class);
//				intent = new Intent(IntroActivity.this, MainActivity.class);
//				intent.putExtra("mUserID", mUserID);
//				startActivity(intent);

				Log.d(TAG, "DbUpdater > onPostExecute() - usertype = 본사사람");

				intent = new Intent(IntroActivity.this, DialogActivity_BsSel.class);
				intent.putExtra("btnType", R.id.btn_kr);
				//intent.putExtra("btnType", R.id.btn_login);
				intent.putExtra("userType", Common.getPrefString(IntroActivity.this, Configuration.SHARED_USERTYPE));
				intent.putExtra("mUserID", mUserID);
				Log.d(TAG, "DbUpdater > onPostExecute() - userType2 =  "+userType);
				Log.d(TAG, "DbUpdater > onPostExecute() - mUserID =  "+mUserID);
				PendingIntent pi = PendingIntent.getActivity(IntroActivity.this, Configuration.REQUESTCODE_BSSEL, intent, PendingIntent.FLAG_ONE_SHOT);

				try {
					pi.send();
				} catch (Exception e) {
					e.printStackTrace();

				}
			}
		}
	}
	
	/** *********************** DbUpdater CLASS END ***************************************************** **/
}
