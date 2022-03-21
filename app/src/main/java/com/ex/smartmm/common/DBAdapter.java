package com.ex.smartmm.common;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.*;*/

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

/** Asset Database 접근 관리 
 * @author JSJ
 * 2016-08-31
 */
public class DBAdapter {
	
	private String TAG = "DBAdapter";
	private static SQLiteDatabase sqliteDatabase;
	private final String package_name = "com.ex.smartmm";
	final String dbfile = "/data/data/"+package_name+"/databases/smartmm.db";
	/**
	 * 생성자 
	 */
	public DBAdapter() {
		init();
	}
	
	public void init(){
		File sdcard = Environment.getExternalStorageDirectory();
		File dbpath = new File(sdcard.getAbsolutePath() + File.separator);
		Log.d(TAG, "DB directory. " + dbpath.getAbsolutePath());

		Log.d("process", "db create");

		try {
			Log.d(TAG, "OPEN DB : " + dbfile);
			Log.d("process", "open db1");
			if(sqliteDatabase != null){
				sqliteDatabase.close();
				sqliteDatabase = null;
			}else{
				sqliteDatabase = SQLiteDatabase.openDatabase(dbfile, null, SQLiteDatabase.OPEN_READWRITE);
			}
			
		} catch (Exception e) {
			Log.d(TAG, "Exception : DB is not exist. ");
		}		
	}
	
	public boolean getSqliteStatus(){
		if(sqliteDatabase != null){
			return sqliteDatabase.isOpen();
		}else{
			return false;
		}
	}
	
	public SQLiteDatabase getSqlite(){
		if(sqliteDatabase != null){
			if(sqliteDatabase.isOpen()){
				return sqliteDatabase;
			}else{
				sqliteDatabase = SQLiteDatabase.openDatabase(dbfile, null, SQLiteDatabase.OPEN_READWRITE);
				return sqliteDatabase;
			}
		}else{
			sqliteDatabase = SQLiteDatabase.openDatabase(dbfile, null, SQLiteDatabase.OPEN_READWRITE);
			return sqliteDatabase;
		}
	}
	
	// ****************************************** Smart MM ********************************************************************************************//
	/** 정비관리 데이터 조회 **/
	public Cursor select_jeongbi(String dogongcode, String jangbimyeong, String buseocode){
		
		Log.d(TAG, "select_jeongbi()~!");
		Log.d(TAG, "select_jeongbi() - dogongcode : "+dogongcode);
		Log.d(TAG, "select_jeongbi() - jangbimyeong : "+jangbimyeong);
		Log.d(TAG, "select_jeongbi() - buseocode : "+buseocode);
		
		getSqlite().beginTransaction();
		Cursor cursor = null;
		
		try {
			StringBuffer sql = new StringBuffer();
			
	  		sql.append(" SELECT																	\n");
			sql.append(" 		'-' AS CODEMYEONG, 												\n");
			sql.append(" 		'50000001' AS JANGBICODE, 										\n");
			sql.append(" 		'공통' AS JANGBIMYEONG, 											\n");
			sql.append(" 		'-' AS DOGONGCODE, 												\n");
			sql.append(" 		'-' AS CHAJONGCODE, 											\n");
			sql.append(" 		'-' AS DEUNGROKNO, 												\n");
			sql.append(" 		'-' AS GYUGYEOK,  												\n");
			sql.append(" 		'' AS PIBUSEOMYEONG, 											\n");
			sql.append(" 		'"+buseocode+"' AS BUSEOCODE, 									\n");
			sql.append(" 		'-' AS CODE, 													\n");
			sql.append(" 		'-' AS CODEGUBUN, 												\n");
			sql.append(" 		'0' AS DOGONGSORT, 												\n");
			sql.append(" 		'Y' AS USE_YN, 													\n");
			sql.append(" 		'-' AS JSTIME 													\n");
			sql.append(" FROM BMCJBGBM 															\n");
			sql.append(" WHERE JANGBICODE='50000001' 											\n");
			sql.append(" UNION ALL 																\n");
			sql.append(" SELECT CODEMYEONG, JANGBICODE, JANGBIMYEONG, 							\n");
			sql.append(" 		DOGONGCODE, CHAJONGCODE,			 							\n");
			sql.append(" 		DEUNGROKNO, GYUGYEOK, 											\n");
			sql.append(" 		PIBUSEOMYEONG, BUSEOCODE,  										\n");
			sql.append(" 		CODE, CODEGUBUN, 												\n");
			sql.append(" 		DOGONGSORT, 													\n");
			sql.append(" 		USE_YN, JSTIME													\n");
			sql.append(" FROM (    											        	        \n");
			sql.append(" 	SELECT    											        	    \n");
			sql.append(" 		B.CMMN_CD_NM AS CODEMYEONG,    									\n");
			sql.append(" 		A.EQPM_CD AS JANGBICODE,    									\n");
			sql.append(" 		A.EQPM_NM AS JANGBIMYEONG,    									\n");
			sql.append(" 		A.EX_EQPM_IDNT_ID AS DOGONGCODE,    							\n");
			sql.append(" 		A.EQPM_KNCR_CD AS CHAJONGCODE,    								\n");
			sql.append(" 		A.EQPM_RGST_CTNT AS DEUNGROKNO,    								\n");
			sql.append(" 		A.STNDS_NM AS GYUGYEOK,    										\n");
			sql.append(" 		C.KOR_DPTNM AS PIBUSEOMYEONG,								    \n");
			sql.append(" 		A.MGMT_DPTCD AS BUSEOCODE,    									\n");
			sql.append(" 		B.CMMN_CD AS CODE,		    									\n");
			sql.append(" 		SUBSTR('0'||B.DATA_MG_VAL, -2, 2) AS CODEGUBUN,					\n");
			sql.append(" 		A. USE_YN,    													\n");
			sql.append(" 		SUBSTR(A.FSTTM_RGST_DTTM, 7, 4) || "
					+ "				SUBSTR(A.FSTTM_RGST_DTTM, 0, 3) || "
					+ "				SUBSTR(A.FSTTM_RGST_DTTM, 4, 2) AS JSTIME,    				\n");
			sql.append(" 		( CASE    											        	\n");
			sql.append(" 			WHEN A.EX_EQPM_IDNT_ID IS NULL THEN '후'     				\n");
			sql.append(" 			ELSE A.EX_EQPM_IDNT_ID END ) AS DOGONGSORT    				\n");
			sql.append(" 	 FROM    											        	    \n");
			sql.append(" 		T_EXMB_EQPM01M1 A,    											\n");
			sql.append(" 		( SELECT DATA_MG_VAL, CMMN_CD, CMMN_CD_NM     					\n");
			sql.append(" 		  FROM T_CNKC_CMMN_CD01C1   									\n");
			sql.append(" 		  WHERE CMMN_GRP_CD = 'EQPM_KNCR_CD') B,    					\n");
			sql.append(" 		V_CNKC_INTG_DPRT01C1 C    										\n");
			sql.append(" 	WHERE A.EQPM_KNCR_CD = B.CMMN_CD   									\n");
			sql.append(" 	AND A.MGMT_DPTCD = C.INTG_DPTCD    									\n");
			sql.append(" 	AND (FSTTM_RGST_DTTM > '20031012' OR SUBSTR(A.LSTTM_ALTR_DTTM, 7, 4) || "
					+ "			SUBSTR(A.LSTTM_ALTR_DTTM, 0, 3) || "
					+ "			SUBSTR(A.LSTTM_ALTR_DTTM, 4, 2) > '20031012')    				\n");
			sql.append(" )    											        	        	\n");
			sql.append(" WHERE BUSEOCODE = '"+buseocode+"'    									\n");
			sql.append(" and jangbimyeong like '%순찰%'		   									\n");
			
			if(!"".equals(dogongcode) && !"".equals(jangbimyeong)){
				sql.append(" AND ( DOGONGCODE like '%"+ dogongcode +"%' OR JANGBIMYEONG like '%"+ jangbimyeong +"%') 	\n");
			}else if(!"".equals(dogongcode)){
				sql.append(" AND DOGONGCODE like '%"+ dogongcode +"%'                           \n");
			}else if(!"".equals(jangbimyeong)){
				sql.append(" AND JANGBIMYEONG like '%"+ jangbimyeong +"%'                       \n");
			}
			sql.append(" ORDER BY DOGONGSORT    											    \n");
			
			Log.d("process", "sql.toString() : "+sql.toString());
			cursor = getSqlite().rawQuery(sql.toString(), null);
			Log.d("process", "cursor : "+cursor.toString());
			
		} catch (SQLException e) {
			Log.d("process", "exception");
			Log.e("selectData()Error! : ", e.toString());
			
		}finally{
			Log.d("process", "finish");
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		Log.d(TAG, "select_jeongbi()- cursor22 : "+cursor.getCount());
		 return cursor;
	}
	
	
	/** 정비관리 데이터에 '정비용 추가' **/
	public Cursor insertJeongbiYong(String buseocode){
		Log.d(TAG, "insertJeongbiYong()~!");
		getSqlite().beginTransaction();
		Cursor cursor = null;
		String date = Common.getCalendarDateMDYHMS();
		Log.d(TAG, "insertJeongbiYong()- date : "+date);
		try{
			Log.d(TAG, "insertJeongbiYong()- buseocode : "+buseocode);
			StringBuffer insertSql1 = new StringBuffer();
			
			insertSql1.append(" INSERT INTO T_EXMB_EQPM01M1																											\n");
			//insertSql1.append("(JANGBICODE, JANGBIMYEONG, DOGONGCODE, CHAJONGCODE, DEUNGROKNO, GYUGYEOK, BUSEOCODE, JSTIME)						\n");
			insertSql1.append("	 ( EQPM_CD, EQPM_NM, EX_EQPM_IDNT_ID, EQPM_KNCR_CD, EQPM_RGST_CTNT, STNDS_NM, MGMT_DPTCD, USE_YN, FSTTM_RGST_DTTM)		\n");
			insertSql1.append(" VALUES																																\n");
			insertSql1.append("	 ('50000001', '공통', '-', '-', '-', '-', '"+buseocode+"', 'Y', '"+date+"')														\n");

			getSqlite().execSQL(insertSql1.toString());
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		return cursor;
	}

	
	/** 정비관리 데이터 상세 조회 **/ //==@@
	public Cursor select_jeongbi_detail(String jangbicode) {
		Log.d(TAG, "select_jeongbi_Detail()");
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" SELECT    																\n");	
			sql.append("   B.CMMN_CD_NM AS CODEMYEONG,                            				\n"); //코드명 
			sql.append("   A.EQPM_CD AS JANGBICODE,                           					\n"); //장비코드
			sql.append("   A.EQPM_NM AS JANGBIMYEONG,                          					\n"); //장비명	
			sql.append("   A.EX_EQPM_IDNT_ID AS DOGONGCODE,                         			\n"); //도공코드
			sql.append("   A.EQPM_KNCR_CD AS CHAJONGCODE,                           			\n"); //차종코드
			sql.append("   A.EQPM_RGST_CTNT AS DEUNGROKNO,                        				\n"); //등록번호
			sql.append("   A.STNDS_NM AS GYUGYEOK,                           					\n"); //규격	
			sql.append("   C.KOR_DPTNM AS PIBUSEOMYEONG,                         				\n"); //피부서명	
			sql.append("   A.MGMT_DPTCD AS BUSEOCODE,                             				\n"); //부서코드
			sql.append("   C.KOR_DPTNM AS PIBUSEOMYEONG                          				\n"); //부서명
			sql.append(" FROM T_EXMB_EQPM01M1 A,  												\n");
			sql.append(" 	  ( SELECT DATA_MG_VAL, CMMN_CD, CMMN_CD_NM     					\n");
			sql.append(" 		FROM T_CNKC_CMMN_CD01C1   										\n");
			sql.append(" 		WHERE CMMN_GRP_CD = 'EQPM_KNCR_CD') B,    						\n");
			sql.append(" 	  V_CNKC_INTG_DPRT01C1 C    										\n");
			sql.append(" WHERE A.EQPM_KNCR_CD = B.CMMN_CD   									\n");
			sql.append(" AND A.MGMT_DPTCD = C.INTG_DPTCD    									\n");
			sql.append(" AND (FSTTM_RGST_DTTM > '20031012' OR SUBSTR(A.LSTTM_ALTR_DTTM, 7, 4) || "
					+ "		 SUBSTR(A.LSTTM_ALTR_DTTM, 0, 3) || "
					+ "		 SUBSTR(A.LSTTM_ALTR_DTTM, 4, 2) > '20031012')    					\n");
			sql.append(" AND JANGBICODE = '"+jangbicode+"'    									\n");
			sql.append("ORDER BY DOGONGCODE                       								\n");	
			
			Log.d("process", "select_jeongbi_Detail sql.toString() : "+sql.toString());
			System.out.println("select_jeongbi_Detail 조회 시작  "+System.currentTimeMillis());
			 cursor = getSqlite().rawQuery(sql.toString(), null);
			System.out.println("select_jeongbi_Detail 조회 시작  "+System.currentTimeMillis() +":"+ cursor.getCount());
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		Log.d(TAG, "select_jeongbi_Detail()- cursor : "+cursor.getCount());
		return cursor;
	}
	

	/** 정비구분 구하기 **/ 
	public Cursor select_jeongbiGB(String gubun) {
		Log.d(TAG,TAG+" select_jeongbiGB()");
		
		getSqlite().beginTransaction();
		String rtnStr = "";
		Cursor cursor = null;
		
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT  code, gubun   												\n");
			sql.append(" FROM SMART_JEONGBIGB                         		                \n");
			if(!"".equals(gubun)){
				sql.append("   WHERE gubun = '"+gubun+"'                         		    \n");
			}
			
			Log.d("process", "select_jeongbiGB sql.toString() : "+sql.toString());
			 cursor = getSqlite().rawQuery(sql.toString(), null);
			 
			 while (cursor.moveToNext()) {
				rtnStr = cursor.getString(0);
				break;
			}
			 
		} catch (SQLException e) {
			Log.d("process", "select_jeongbiGB exception ");
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			Log.d("process", "select_jeongbiGB finish ");
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		return cursor;
	}

	
	/** 수리항목 구하기 **/ //==@@
	public Cursor select_itemGB(String suliitem) {
		Log.d(TAG, "select_itemGB()");
		
		getSqlite().beginTransaction();
		String rtnStr = "";
		Cursor cursor = null;
		
		try {
			StringBuffer sql = new StringBuffer();
			
			sql.append(" SELECT  no, suliitem, code												\n");
			sql.append(" FROM SMART_ITEMGB3                         		                    \n");
			if(!"".equals(suliitem)){
				sql.append(" WHERE suliitem = '"+suliitem+"'                         		    \n");
			}
			sql.append(" GROUP BY no, suliitem                         		                	\n");
			
			Log.d("process", "select_itemGB sql.toString() : "+sql.toString());
			 cursor = getSqlite().rawQuery(sql.toString(), null);
			 
			 while (cursor.moveToNext()) {
				rtnStr = cursor.getString(0);
				break;
			}
			 
		} catch (SQLException e) {
			Log.d("process", "select_itemGB exception ");
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			Log.d("process", "select_itemGB finish ");
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		return cursor;
	}
	
	
	/** 세부항목 구하기 **/  //==@@
	public Cursor select_detailGB(String suliitem) {
		Log.d(TAG,"select_detailGB()~!");
		
		getSqlite().beginTransaction();
		String rtnStr = "";
		Cursor cursor = null;
		
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" SELECT  no, suliitem, detailitem   								\n");
			sql.append(" FROM SMART_ITEMGB3                            		                \n");
			sql.append(" WHERE  SULIITEM='"+suliitem+"'						    	        \n");
			
			Log.d("process", "select_detailGB sql.toString() : "+sql.toString());
			 cursor = getSqlite().rawQuery(sql.toString(), null);
			 
			 while (cursor.moveToNext()) {
				rtnStr = cursor.getString(0);
				break;
			}
			 
		} catch (SQLException e) {
			Log.d("process", "select_detailGB exception ");
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			Log.d("process", "select_detailGB finish ");
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		return cursor;
	}

	/** 내부 db 업데이트한 마지막 날짜 **/ 
	public String getDataMngDate(){
		Log.d(TAG, "getDataMngDate()~!");
		getSqlite().beginTransaction();
		Cursor cursor = null;
		String lastUptDate = "";
		String lastUptDate_ = "";

		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" SELECT lastUpdateDate   								\n");	
			sql.append(" FROM dataMng								  			\n");

			Log.d("process", "getDataMngDate sql.toString() : "+sql.toString());
			cursor = getSqlite().rawQuery(sql.toString(), null);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				Log.d("",TAG+" getDataMngDate() - cursor.getCount() : "+cursor.getCount());
				lastUptDate = cursor.getString(0);
				if(lastUptDate.equals("") || lastUptDate.equals(null) ){
					Log.d("",TAG+" getDataMngDate() - lastUptDate is null");
					lastUptDate = "2017-10-27 10:58:00";
				}
				
				Log.d("",TAG+" getDataMngDate() - lastUptDate : "+lastUptDate);
			}

		} catch (SQLException  e) {
			Log.d("process", "getDataMngDate exception ");
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			Log.d("process", "getDataMngDate finish ");
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();

		}
		Log.d("process", "getDataMngDate() - lastUptDate2 : "+lastUptDate);
		return lastUptDate;
	}
	
		
	//내부 db 가 업데이트 되면 최근업데이트 날짜를 바꿔준다 
	public String setDataMngUpdate(String date){
		Log.d(TAG, "setDataMngUpdate()~!");
		
		getSqlite().beginTransaction();
		
		String sql = "update dataMng set lastUpdateDate = '"+date+"' where pk_num = '1'";
		try {
			Log.d("process", "setDataMngUpdate() sql.toString() : "+sql.toString());
			getSqlite().execSQL(sql);
			Log.d("","setdataMngUpdate sql = " + sql);
		} catch (SQLException e) {
			Log.e("setdataMngUpdate() Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		return null;
	}
	
	
	/** 정비데이터 업데이트 **/
	public Cursor update_jeongbi(JSONArray items){
		Log.d(TAG, "uptJeongbi()~!");
		Log.d(TAG, "uptJeongbi() - items size : "+items.length());
		getSqlite().beginTransaction();
		Cursor cursor = null;

		try {
			for(int i=0; i<items.length(); i++){

				//Log.d("",TAG+" uptJeongbi() - items.get(" + i + ") : "+items.get(i));

				StringBuffer deleteSql1 = new StringBuffer();
				StringBuffer deleteSql2 = new StringBuffer();
				StringBuffer insertSql1 = new StringBuffer();
				StringBuffer insertSql2 = new StringBuffer();

				String gyugyeok = "";
				String chajongcode = "";
				String dogongcode = "";
				String jangbimyeong = "";
				String codegubun = "";
				String codemyeong = "";
				String buseocode = "";
				String hgbsmyeong = "";
				String deungrokno = "";
				String flagdate = "";
				String flag = "";
				String jangbicode = "";

				JSONObject value = items.getJSONObject(i);

				gyugyeok = value.getString("GYUGYEOK");
				chajongcode = value.getString("CHAJONGCODE");
				dogongcode = value.getString("DOGONGCODE");
				jangbimyeong = value.getString("JANGBIMYEONG");
				codegubun = value.getString("CODEGUBUN");
				codemyeong = value.getString("CODEMYEONG");
				buseocode = value.getString("BUSEOCODE");
				hgbsmyeong = value.getString("HGBSMYEONG");
				deungrokno = value.getString("DEUNGROKNO");
				flagdate = value.getString("FLAGDATE");
				//flagdate ==> 07-17-2017 00:00:00 으로 형태 변경
				flagdate = flagdate.substring(4, 6)+"-"+flagdate.substring(6, 8)+"-"+flagdate.substring(0, 4)+" 00:00:00";
				Log.d(TAG, "uptJeongbi() - flagdate : "+flagdate);
				flag = value.getString("FLAG");
				jangbicode = value.getString("JANGBICODE");

				deleteSql1.append(" delete from T_EXMB_EQPM01M1																																		\n");
				deleteSql1.append("	 WHERE EQPM_CD = '"+jangbicode+"' 																																\n");

				insertSql1.append(" INSERT INTO T_EXMB_EQPM01M1																																		\n");
				//insertSql1.append("	 (JANGBICODE, JANGBIMYEONG, DOGONGCODE, CHAJONGCODE, DEUNGROKNO, GYUGYEOK, BUSEOCODE, JSTIME)				\n");
				insertSql1.append("	 ( EQPM_CD, EQPM_NM, EX_EQPM_IDNT_ID, EQPM_KNCR_CD, EQPM_RGST_CTNT, STNDS_NM, MGMT_DPTCD, FSTTM_RGST_DTTM)											\n");
				insertSql1.append(" VALUES																																							\n");
				insertSql1.append("	 ('"+jangbicode+"','"+jangbimyeong+"','"+dogongcode+"','"+chajongcode+"','"+deungrokno+"','"+gyugyeok+"','"+buseocode+"','"+flagdate+"')		\n");

				getSqlite().execSQL(deleteSql1.toString());

				if(flag.equals("Y")){
					getSqlite().execSQL(insertSql1.toString());
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		return cursor;
	}
	

/*	public Cursor select_uptBMCJBGBM(String jangbicode){
		Log.d("",TAG+" select_uptBMCJBGBM()~!");
		getSqlite().beginTransaction();
		Cursor cursor = null;

		try {
			StringBuffer sql = new StringBuffer();

			SELECT * FROM BMCJBGBM 
			where JANGBICODE='30101628' 
			and JANGBIMYEONG='안전순찰차' 
			and DOGONGCODE='111'
			and CHAJONGCODE='143' 
			and BUSEOCODE='N00234' 
			and DEUNGROKNO='경기33루5832' 
			and GYUGYEOK='M7YA4-103' 
			and PIBUSEOMYEONG='화성지사' 

			sql.append(" SELECT *   												\n");	
			sql.append(" FROM BMCJBGBM								  				\n");
			sql.append(" WHERE JANGBICODE='"+jangbicode+"'							\n");

			Log.d("process", "select_uptBMCJBGBM() - sql.toString() : "+sql.toString());
			cursor = getSqlite().rawQuery(sql.toString(), null);
			
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		Log.d("",TAG+" select_uptBMCJBGBM() - cursor : "+cursor.getCount());
		return cursor;
	}

	
	public Cursor select_uptBMCCODEM(String codegubun, String code, String codemyeong){
		Log.d("",TAG+" select_uptBMCCODEM()");
		getSqlite().beginTransaction();
		Cursor cursor = null;
		
		try {
			StringBuffer sql = new StringBuffer();

			//SELECT * FROM BMCCODEM where CODEGUBUN='03' AND CODE= '143' AND CODEMYEONG='무쏘' 
			
			sql.append(" SELECT *   											\n");	
			sql.append(" FROM BMCCODEM								  			\n");
			sql.append(" WHERE CODEGUBUN='"+codegubun+"'						\n");
			sql.append("  AND CODE='"+code+"'       							\n");
			sql.append("  AND CODEMYEONG='"+codemyeong+"'       				\n");
			
			Log.d("process", "select_uptJeongbi sql.toString() : "+sql.toString());
			 cursor = getSqlite().rawQuery(sql.toString(), null);
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		Log.d("",TAG+" select_uptJeongbi()- cursor : "+cursor.getCount());
		return cursor;
	}*/
	
	
	 //checkDataChanged_Jeongbi@@
	/*checkDataChanged_Kyoryang*/
	public int checkDataChanged_Jeongbi(String jeongbi, String item, String detail,	String content, String filename,
										String jbcode, String jbmyeong, String drno, String name, 
										String dogong, String swbeonho, String bonbucode, String jisacode){

		Log.d(TAG, "checkDataChanged_Kyoryang()~!");

		Log.d(TAG, "checkDataChanged_Jeongbi() - jeongbi : "+jeongbi);
		Log.d(TAG, "checkDataChanged_Jeongbi() - item : "+item);
		Log.d(TAG, "checkDataChanged_Jeongbi() - detail : "+detail);
		Log.d(TAG, "checkDataChanged_Jeongbi() - content : "+content);
		Log.d(TAG, "checkDataChanged_Jeongbi() - filename : "+filename);
		Log.d(TAG, "checkDataChanged_Jeongbi() - jbcode : "+jbcode);
		Log.d(TAG, "checkDataChanged_Jeongbi() - jbmyeong : "+jbmyeong);
		Log.d(TAG, "checkDataChanged_Jeongbi() - drno : "+drno);
		Log.d(TAG, "checkDataChanged_Jeongbi() - name : "+name);
		Log.d(TAG, "checkDataChanged_Jeongbi() - dogong : "+dogong);
		Log.d(TAG, "checkDataChanged_Jeongbi() - swbeonho : "+swbeonho);
		Log.d(TAG, "checkDataChanged_Jeongbi() - bonbucode : "+bonbucode);
		Log.d(TAG, "checkDataChanged_Jeongbi() - jisacode : "+jisacode);

		int rntInt = 0;
		getSqlite().beginTransaction();
		String date = Common.getCalendarDateTime();
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();
			/*sql.append(" SELECT  														\n");
			sql.append(" 	JeongbiGB, ItemGB, DetailGB, CONTENT, FILENAME, "
							+ "JBCODE, JBMYEONG, DRNO, NAME, DOGONG, DATE, SENDYN, "
							+ "SWBEONHO, BONBUCODE, JISACODE, SWNAME, CHECKDATE			\n");*/
			sql.append(" SELECT * 														\n");
			sql.append(" FROM  SMART_DATAINFO											\n");
			sql.append(" ORDER BY DATE DESC 											\n");
			sql.append(" limit 1 														\n");
			
			Log.d("process", "sql.toString() : "+sql.toString());
			cursor = getSqlite().rawQuery(sql.toString(), null);
			
			String jeongbi_pre = "";
			String item_pre = "";
			String detail_pre = "";
			String content_pre = "";
			String jbcode_pre = "";
			String jbmyeong_pre = "";
			String drno_pre = "";
			String name_pre = "";
			String dogong_pre = "";
			
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				Log.d("", "checkDataChanged_Jeongbi() - ["+i+"]"+cursor.getColumnName(i)+" : "+cursor.getString(i));
				
				jeongbi_pre = cursor.getString(0);
				item_pre = cursor.getString(1);
				detail_pre = cursor.getString(2);
				content_pre = cursor.getString(3);
				jbcode_pre = cursor.getString(5);
				jbmyeong_pre = cursor.getString(6);
				drno_pre = cursor.getString(7);
				name_pre = cursor.getString(8);
				dogong_pre = cursor.getString(9);
				
			}
			
			Log.d("","checkDataChanged bjae2 = " + item_pre +":"+item);
			Log.d("","checkDataChanged bjae3 = " + content_pre +":"+content);

			if(jbcode.equals(jbcode_pre) && Common.nullCheck(dogong).equals(Common.nullCheck(dogong_pre))){
				if( content.equals(content_pre) ){
					Log.d("","rntInt = " + rntInt +", "+content_pre +":"+content);
					rntInt = 1;	
				}
			}
			
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		return rntInt;
	}
	
	/** 점검 정보 등록 **/
	public Cursor insertSmart_DATAINFO( String jeongbi, String item, String detail,	String content, String filename,
									  	String jbcode, String jbmyeong, String drno, String name, 
									  	String dogong, String dataDate, String swbeonho, String bonbucode, String jisacode, String tagyn, String swname){
	
		Log.d(TAG, "insertSmart_DATAINFO()~!");
		Log.d(TAG, "insertSmart_DATAINFO() - jeongbi : "+jeongbi);
		Log.d(TAG, "insertSmart_DATAINFO() - item : "+item);
		Log.d(TAG, "insertSmart_DATAINFO() - detail : "+detail);
		Log.d(TAG, "insertSmart_DATAINFO() - content : "+content);
		Log.d(TAG, "insertSmart_DATAINFO() - filename : "+filename);
		Log.d(TAG, "insertSmart_DATAINFO() - jbcode : "+jbcode);
		Log.d(TAG, "insertSmart_DATAINFO() - jbmyeong : "+jbmyeong);
		Log.d(TAG, "insertSmart_DATAINFO() - drno : "+drno);
		Log.d(TAG, "insertSmart_DATAINFO() - name : "+name);
		Log.d(TAG, "insertSmart_DATAINFO() - dogong : "+dogong);
		Log.d(TAG, "insertSmart_DATAINFO() - swbeonho : "+swbeonho);
		Log.d(TAG, "insertSmart_DATAINFO() - bonbucode : "+bonbucode);
		Log.d(TAG, "insertSmart_DATAINFO() - jisacode : "+jisacode);
		Log.d(TAG, "insertSmart_DATAINFO() - tagyn : "+tagyn);
		Log.d(TAG, "insertSmart_DATAINFO() - swname : "+swname);
		
		getSqlite().beginTransaction();
		//String date = Common.getCalendarDateTime();
		String currentDate = Common.getCalendarDateYMDHMS();
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();
		
			sql.append(" INSERT INTO SMART_DATAINFO 																		\n");
			sql.append("   (JeongbiGB, ItemGB, DetailGB, CONTENT, FILENAME, JBCODE, JBMYEONG, DRNO, NAME, DOGONG, "
							+ "DATE, SENDYN, SWBEONHO, BONBUCODE, JISACODE, TAGYN, SWNAME, CHECKDATE) 				\n");
			sql.append(" values										\n");
			sql.append("   ('"+jeongbi+"', '"+item+"', '"+detail+"', '"+content+"', '"+filename+"', '"
							  +jbcode+"', '"+jbmyeong+"', '"+drno+"', '"+name+"', '"
							  +dogong+"', '"+dataDate+"', 'N', '"
							  +swbeonho+"', '"+bonbucode+"', '"+jisacode+"', '"+tagyn+"', '"+swname+"', '"+currentDate+"')		\n");
			
			Log.d("process", "sql.toString() : "+sql.toString());
			getSqlite().execSQL(sql.toString());
			
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		return cursor;
	}

	
	/** 점검 조회 **/
	public Cursor selectSmart_DATAINFO() {
		Log.d(TAG, "selectSmart_DATAINFO~!");
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT  * 						\n");
			sql.append(" FROM SMART_DATAINFO        	\n");
			sql.append(" ORDER BY CHECKDATE  desc       \n"); //나중에 등록된 데이터가 위로가게 정렬
			
			Log.d("process", "sql.toString() : "+sql.toString());
			cursor = getSqlite().rawQuery(sql.toString(), null);
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		return cursor;
	}

	
	/** 점검 목록 조회-미전송 **/
	public Cursor selectSmart_DATAINFO_ONE() {
		Log.d(TAG, "selectSmart_DATAINFO_ONE()~!");
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * 							\n");
			sql.append(" FROM SMART_DATAINFO                \n");
			sql.append(" WHERE SENDYN='N'			        \n");
			sql.append(" ORDER BY CHECKDATE  desc           \n");
			Log.d("process", "sql.toString() : "+sql.toString());
			cursor = getSqlite().rawQuery(sql.toString(), null);
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		return cursor;
	}

	
	/** 점검목록 전송여부 수정 **/
	public Cursor updateJBHistorySendYn(String filename) {
		Log.d(TAG, "updateJBHistorySendYn()~!");
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" UPDATE SMART_DATAINFO SET sendyn = 'Y' where filename='"+filename+"'" );
			
			Log.d("process", "sql.toString() : "+sql.toString());
			getSqlite().execSQL(sql.toString());
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		return cursor;
	}	
	
	
	/** 점검목록 선택 삭제 **/
	public Cursor deleteJBHistoryOne(String date) {
		Log.d(TAG, "deleteJBHistoryOne()~!");
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" DELETE FROM SMART_DATAINFO WHERE date ='"+date+"'  \n");
			
			Log.d("process", "sql.toString() : "+sql.toString());
			getSqlite().execSQL(sql.toString());
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		return cursor;
	}
	
	
	/** 점검목록 전부 삭제  **/
	public Cursor deleteJBHistory() {
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" DELETE FROM SMART_DATAINFO WHERE 1=1  \n");
			
			Log.d("process", "sql.toString() : "+sql.toString());
			getSqlite().execSQL(sql.toString());
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		return cursor;
	}
	
	
	// ****************************************** Smart MM (KyoRyangActivity)*********************************************************************//
	
	 //checkDataChanged_Jeongbi@@
		/*checkDataChanged_Kyoryang*/
		public int checkDataChanged_Jeongbi(String jeongbi, String item, String detail,	String content, String filename,
											String jbcode, String jbmyeong, String drno, String name, 
											String dogong, String swbeonho, String bonbucode, String jisacode, String jgubun){

			Log.d(TAG, "checkDataChanged_Kyoryang()~!");

			Log.d(TAG, "checkDataChanged_Jeongbi() - jeongbi : "+jeongbi);
			Log.d(TAG, "checkDataChanged_Jeongbi() - item : "+item);
			Log.d(TAG, "checkDataChanged_Jeongbi() - detail : "+detail);
			Log.d(TAG, "checkDataChanged_Jeongbi() - content : "+content);
			Log.d(TAG, "checkDataChanged_Jeongbi() - filename : "+filename);
			Log.d(TAG, "checkDataChanged_Jeongbi() - jbcode : "+jbcode);
			Log.d(TAG, "checkDataChanged_Jeongbi() - jbmyeong : "+jbmyeong);
			Log.d(TAG, "checkDataChanged_Jeongbi() - drno : "+drno);
			Log.d(TAG, "checkDataChanged_Jeongbi() - name : "+name);
			Log.d(TAG, "checkDataChanged_Jeongbi() - dogong : "+dogong);
			Log.d(TAG, "checkDataChanged_Jeongbi() - swbeonho : "+swbeonho);
			Log.d(TAG, "checkDataChanged_Jeongbi() - bonbucode : "+bonbucode);
			Log.d(TAG, "checkDataChanged_Jeongbi() - jisacode : "+jisacode);
			Log.d(TAG, "checkDataChanged_Jeongbi() - jgubun : "+jgubun);

			int rntInt = 0;
			getSqlite().beginTransaction();
			String date = Common.getCalendarDateTime();
			Cursor cursor = null;
			try {
				StringBuffer sql = new StringBuffer();
				sql.append(" SELECT  														\n");
				sql.append(" 	JeongbiGB, ItemGB, DetailGB, CONTENT, FILENAME, "
								+ "JBCODE, JBMYEONG, DRNO, NAME, DOGONG, DATE, SENDYN, "
								+ "SWBEONHO, BONBUCODE, JISACODE, JGUBUN, SWNAME 			\n");
				sql.append(" FROM  SMART_DATAINFO											\n");
				sql.append(" ORDER BY DATE DESC 											\n");
				sql.append(" limit 1 														\n");
				
				Log.d("process", "sql.toString() : "+sql.toString());
				cursor = getSqlite().rawQuery(sql.toString(), null);
				
				String jeongbi_pre = "";
				String item_pre = "";
				String detail_pre = "";
				String content_pre = "";
				String jbcode_pre = "";
				String jbmyeong_pre = "";
				String drno_pre = "";
				String name_pre = "";
				String dogong_pre = "";
				
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					Log.d("", "checkDataChanged_Jeongbi() - "+cursor.getColumnName(i)+" : "+cursor.getString(i));
					
					jeongbi_pre = cursor.getString(0);
					item_pre = cursor.getString(1);
					detail_pre = cursor.getString(2);
					content_pre = cursor.getString(3);
					jbcode_pre = cursor.getString(5);
					jbmyeong_pre = cursor.getString(6);
					drno_pre = cursor.getString(7);
					name_pre = cursor.getString(8);
					dogong_pre = cursor.getString(9);
					
				}
				
				Log.d("","checkDataChanged bjae2 = " + item_pre +":"+item);
				Log.d("","checkDataChanged bjae3 = " + content_pre +":"+content);

				if(jbcode.equals(jbcode_pre) && Common.nullCheck(dogong).equals(Common.nullCheck(dogong_pre))){
					if( content.equals(content_pre) ){
						Log.d("","rntInt = " + rntInt +", "+content_pre +":"+content);
						rntInt = 1;	
					}
				}
				
			} catch (SQLException e) {
				Log.e("selectData()Error! : ", e.toString());
			}finally{
				getSqlite().setTransactionSuccessful();
				getSqlite().endTransaction();
			}
			return rntInt;
		}
		
		
		/** 점검 정보 등록 **/
		public Cursor insertSmart_DATAINFO( String jeongbi, String item, String detail,	String content, String filename,
										  	String jbcode, String jbmyeong, String drno, String name, 
										  	String dogong, String dataDate, String swbeonho, String bonbucode, String jisacode, String jgubun, String tagyn, String swname){
		
			Log.d(TAG, "insertSmart_DATAINFO()~!");
			Log.d(TAG, "insertSmart_DATAINFO() - jeongbi : "+jeongbi);
			Log.d(TAG, "insertSmart_DATAINFO() - item : "+item);
			Log.d(TAG, "insertSmart_DATAINFO() - detail : "+detail);
			Log.d(TAG, "insertSmart_DATAINFO() - content : "+content);
			Log.d(TAG, "insertSmart_DATAINFO() - filename : "+filename);
			Log.d(TAG, "insertSmart_DATAINFO() - jbcode : "+jbcode);
			Log.d(TAG, "insertSmart_DATAINFO() - jbmyeong : "+jbmyeong);
			Log.d(TAG, "insertSmart_DATAINFO() - drno : "+drno);
			Log.d(TAG, "insertSmart_DATAINFO() - name : "+name);
			Log.d(TAG, "insertSmart_DATAINFO() - dogong : "+dogong);
			Log.d(TAG, "insertSmart_DATAINFO() - swbeonho : "+swbeonho);
			Log.d(TAG, "insertSmart_DATAINFO() - bonbucode : "+bonbucode);
			Log.d(TAG, "insertSmart_DATAINFO() - jisacode : "+jisacode);
			Log.d(TAG, "insertSmart_DATAINFO() - jgubun : "+jgubun);
			Log.d(TAG, "insertSmart_DATAINFO() - tagyn : "+tagyn);
			Log.d(TAG, "insertSmart_DATAINFO() - swname : "+swname);
			
			getSqlite().beginTransaction();
			//String date = Common.getCalendarDateTime();
			String date = Common.getCalendarDateYMDHMS();
			Cursor cursor = null;
			try {
				StringBuffer sql = new StringBuffer();
			
				sql.append(" INSERT INTO SMART_DATAINFO 																		\n");
				sql.append("   (JeongbiGB, ItemGB, DetailGB, CONTENT, FILENAME, JBCODE, JBMYEONG, DRNO, NAME, DOGONG, "
								+ "DATE, SENDYN, SWBEONHO, BONBUCODE, JISACODE, JGUBUN, TAGYN, SWNAME) 							\n");
				sql.append(" values										\n");
				sql.append("   ('"+jeongbi+"', '"+item+"', '"+detail+"', '"+content+"', '"+filename+"', '"
								  +jbcode+"', '"+jbmyeong+"', '"+drno+"', '"+name+"', '"
								  +dogong+"', '"+dataDate+"', 'N', '"
								  +swbeonho+"', '"+bonbucode+"', '"+jisacode+"', '"+jgubun+"', '"+tagyn+"', '"+swname+"')		\n");
				
				Log.d("process", "sql.toString() : "+sql.toString());
				getSqlite().execSQL(sql.toString());
				
			} catch (SQLException e) {
				Log.e("selectData()Error! : ", e.toString());
			}finally{
				getSqlite().setTransactionSuccessful();
				getSqlite().endTransaction();
			}
			return cursor;
		}

	// ****************************************** Smart MM ********************************************************************************************//
	
	/**
	 * 노선 정보
	 * 
	 * @return Cursor
	 */
	public Cursor fetchRange(Double latitude, Double longitude, String ns_code) {
		
		StringBuffer sql = new StringBuffer();
		
		sql.append("   SELECT nscode,													\n");
		sql.append("          nsmyeong,                                                 \n");
		sql.append("          MIN (ijeong),                                             \n");
		sql.append("          MIN (xgap+xgap2) as xgap,                                 \n");
		sql.append("          '0'  icmyeong                                             \n");
		sql.append("     FROM (SELECT nscode,                                           \n");
		sql.append("                  nsmyeong,                            				\n");
		sql.append("                  ijeong,                                 			\n");
		sql.append("                  kyungdo,                                          \n");
		sql.append("                  wido,                                             \n");
		sql.append("                  ABS (kyungdo - " + longitude + ") xgap,           \n");
		sql.append("                  ABS (wido - " + latitude + ") xgap2             	\n");
		sql.append("             FROM nscode a                                        	\n");
		sql.append("            WHERE     1 = 1                                       	\n");
		sql.append("                  AND kyungdo + 0.0035 > " + longitude +           "\n");
		sql.append("                  AND kyungdo - 0.0035 < " + longitude +           "\n");
		sql.append("                  AND wido + 0.0035 > " + latitude +               "\n");
		sql.append("                  AND wido - 0.0035 < " + latitude + ")             \n");
		sql.append(" GROUP BY nscode, nsmyeong                                         	\n");
		sql.append(" ORDER BY MIN (xgap) DESC                                           \n");
		
		Cursor cursor = getSqlite().rawQuery(sql.toString(), null);
		Log.d("process", "query db1");
//		Log.d("process", "sql.toString() : "+sql.toString());
		return cursor;
	}
	
	//좌표 거리 계산
	public double calDistance(double lat1, double lon1, double lat2, double lon2){  
	    
	    double theta, dist;  
	    theta = lon1 - lon2;  
	    dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))   
	          * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));  
	    dist = Math.acos(dist);  
	    dist = rad2deg(dist);  
	      
	    dist = dist * 60 * 1.1515;   
	    dist = dist * 1.609344;    // 단위 mile 에서 km 변환.  
	    dist = dist * 1000.0;      // 단위  km 에서 m 로 변환  
	  
	    return dist;  
	}  
    // 주어진 도(degree) 값을 라디언으로 변환  
	private double deg2rad(double deg){  
	    return (double)(deg * Math.PI / (double)180d);  
	}  
	  
	// 주어진 라디언(radian) 값을 도(degree) 값으로 변환  
	private double rad2deg(double rad){  
	    return (double)(rad * (double)180d / Math.PI);  
	} 

	
	
	/**
	 * 방향 조회
	 * 
	 * @return Cursor
	 */
	public Cursor fetchBangHyang(String nsCode) {
		
		StringBuffer sql = new StringBuffer();

		sql.append(" SELECT nscode,      												\n");
		sql.append("        nsmyeong,                           							\n");
		sql.append("        gjmyeong,                            							\n");
		sql.append("        jjmyeong                             							\n");
		sql.append("   FROM banghyang                            						\n");
		sql.append("  WHERE nscode = '" + nsCode + "'    						\n");
		sql.append("  ORDER BY nscode					    							\n");
		
//		Log.d("process", "sql.toString() : "+sql.toString());
		
		Cursor cursor = getSqlite().rawQuery(sql.toString(), null);
		return cursor;
	}
	
	
	/** 
	 * @param nsCode
	 * @param bh_code
	 * @param bscode
	 * @return
	 */
	public String fetchBanghyang_sub(String nsCode, String bh_code, String rpt_bscode, String ijeong){
		
		String rtnStr = "";
		StringBuffer sql = new StringBuffer();

		sql.append("	SELECT code      					                      				");
		sql.append("	FROM banghyang_se                                         				");
		sql.append("	WHERE                                    								");
		sql.append("		ns_code = '"+nsCode+"'                                    			");
		sql.append("	AND bh_code = '"+bh_code+"'                                        		");
		sql.append("	AND bscode =                              								");
		sql.append("		(SELECT A.BSCODE                             						");
		sql.append("			FROM                                                     		");
		sql.append("				(SELECT  BSCODE, YEONDO                                    	");
		sql.append("					FROM JISAINFO                                           ");
		sql.append("					WHERE                                     				");
		sql.append("					NSCODE ='"+nsCode+"'                                    ");
		sql.append("					AND CAST('"+ijeong+"' AS DOUBLE)   						");
		sql.append("						BETWEEN CAST(MIN AS DOUBLE)+0.1						");
		sql.append("					AND CAST(MAX AS DOUBLE)            		                ");
		sql.append("					ORDER BY YEONDO ASC                                     ");
		sql.append("				) A                                                     	");
		sql.append("				GROUP BY A.BSCODE                                       	");
		sql.append("		)                                                         			");
		sql.append("		ORDER BY ns_code	                                      			");
		Cursor cursor = getSqlite().rawQuery(sql.toString(), null);
		if(cursor.getCount() > 0){
			while (cursor.moveToNext()) {
				rtnStr = cursor.getString(0);
				
			}
		}
		return rtnStr;
	}
	
	
	/**
	 * 공구 조회
	 * 
	 * @return Cursor
	 */
	public String fetchGonggu(String nsCode, String ijung) {
		String rtnStr = "";
		
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT gonggu      																					\n");
		sql.append("    FROM gonggu                            																\n");
		sql.append("   WHERE nscode = '"+nsCode+"' 																\n");
		sql.append("   AND CAST('"+ijung+"' AS DOUBLE) BETWEEN CAST(minkm AS DOUBLE) 	\n");
		sql.append("   AND CAST(maxkm AS DOUBLE)-0.1															\n");
		sql.append(" union      																									\n");
		sql.append(" SELECT gonggu      																					\n");
		sql.append("    FROM gonggu                            																\n");
		sql.append("   WHERE nscode = '"+nsCode+"' 																\n");
		sql.append("   AND CAST('"+ijung+"' AS DOUBLE) BETWEEN CAST(maxkm AS DOUBLE) \n");
		sql.append("   AND CAST(minkm AS DOUBLE)-0.1															\n");
		
		
//		Log.d("process", "fetchgonggu sql.toString() : "+sql.toString());
		
		Cursor cursor = getSqlite().rawQuery(sql.toString(), null);
		if(cursor.getCount() > 0 ){
			while (cursor.moveToNext()) {
				rtnStr = cursor.getString(0);
			}
		}
		
		return rtnStr;
		
	}	
	
	// Data Select
	public List<String> selectNoseonName() {
		// TODO Auto-generated method stub
		String sql = " select ns_code, ns_name from banghyang_se group by ns_code, ns_name order by ns_name ";

		List<String> list = new ArrayList<String>();
		//Log.e("selectData(): ", sql);
		try {
			Cursor cur = getSqlite().rawQuery(sql, null);
			list.add("노선외");
			while (cur.moveToNext()) {

				list.add(cur.getString(1));
			}

		} catch (SQLException se) {
			// TODO: handle exception
			Log.e("selectData()Error! : ", se.toString());
		}

		return list;
	}
		
	// Data Select
	public List<String> selectBanghyangName(String nsName, String nsCode) {
		// TODO Auto-generated method stub
		String sql = "";
		if (nsCode != null) {
			sql = " select bh_code, bh_name from banghyang_se where ns_code = '"
					+ nsCode + "'  group by bh_code order by bh_code desc";
		} else {
			sql = " select bh_code, bh_name from banghyang_se where ns_name = '"
					+ nsName + "' group by bh_code order by bh_code desc ";
		}

		List<String> list = new ArrayList<String>();
		try {
			Cursor cur = getSqlite().rawQuery(sql, null);
			while (cur.moveToNext()) {

				list.add(cur.getString(1));
			}

		} catch (SQLException se) {
			Log.e("selectData()Error! : ", se.toString());
		}

		return list;
	}
	
	/** 노선코드 조회
	 * @param nsName
	 * @return
	 */
	public String getNoseonCode(String nsName) {
		// TODO Auto-generated method stub
		String sql = "select ns_code from banghyang_se where ns_name = '" + nsName
				+ "'; ";
		String nsCode = "";
		//Log.e("selectData(): ", sql);

		// System.out.println("============================ sql : " + sql);

		try {
			Cursor cur = getSqlite().rawQuery(sql, null);
			cur.moveToFirst();
			if (cur.getCount() > 0) {
				nsCode = cur.getString(0);
			}
		} catch (SQLException se) {
			// TODO: handle exception
			Log.e("selectData()Error! : ", se.toString());
		}

		return nsCode;
	}
		
	
	/** 노선의 최대 이정값 구하기
	 * @param nsCode
	 * @return
	 */
	public String maxIjung(String nsCode){
		String maxIjung = "";
		String sql = "select max(cast( ijeong as double )) aa from nscode where nscode='"+nsCode+"'";
		Cursor cursor = getSqlite().rawQuery(sql.toString(),null);
		
		 cursor = getSqlite().rawQuery(sql, null);
		 cursor.moveToFirst();
		 if (cursor.getCount() > 0) {
		 	 maxIjung = cursor.getString(0);
		 }
//		 Log.d("","execsql maxIjung: " + sql.toString());
		return maxIjung;
	}
	

	//tm 좌표 구하기
	public Cursor fetchTMXY2(String nscode, String ijeong){
		StringBuffer sql = new StringBuffer();
		
		sql.append("   SELECT tm_x, tm_y													\n");
		sql.append("     FROM nscode               			       							\n");
		sql.append(" WHERE nscode='"+nscode+"'               						\n");
		sql.append(" AND CAST (ijeong as double) = CAST ('"+ijeong+"' as double)		            \n");
		
		Cursor cursor = getSqlite().rawQuery(sql.toString(), null);
		
//		Log.d("","execsql fetchGPSXY: " + sql.toString());
		return cursor;
	}
		

	/** 노선명 조회
	 * @param nsCode
	 * @return
	 */
	public String getNoseonName(String nsCode) {
		// TODO Auto-generated method stub
		String sql = "select ns_name from banghyang_se where ns_code = '" + nsCode
				+ "'; ";
		String nsName = "";
		//Log.e("selectData(): ", sql);

		// System.out.println("============================ sql : " + sql);

		try {
			Cursor cur = getSqlite().rawQuery(sql, null);
			cur.moveToFirst();
			if (cur.getCount() > 0) {
				nsName = cur.getString(0);
			}
		} catch (SQLException se) {
			// TODO: handle exception
			Log.e("selectData()Error! : ", se.toString());
		}

		return nsName;
	}
	
	//터널내 N초후 다음좌표값 가져오기.
	public String[] getNextLocGps(String nsCode, String ijeong, String bhCode){
		String[] arr = null;
		if(!"".equals(Common.nullCheck(nsCode))){
			double nextIjeong;
			if("E".equals(bhCode)){
				nextIjeong = Common.nullCheckDouble(ijeong) + 0.1;//이정 + 100미터 S E에 따라 다름
			}else{
				nextIjeong = Common.nullCheckDouble(ijeong) - 0.1;//이정 + 100미터 S E에 따라 다름
			}
			StringBuffer sql = new StringBuffer();
			
			sql.append(" SELECT kyungdo, wido                                        \n");
			sql.append(" FROM nscode                                                 \n");
			sql.append(" WHERE nscode ='"+nsCode+"'                                  \n");
			sql.append(" AND CAST(ijeong AS DOUBLE) = "+nextIjeong+"        		 \n");
			
			Cursor cursor = getSqlite().rawQuery(sql.toString(), null);
			if(cursor.getCount() > 0){
				while (cursor.moveToNext()) {
					arr[0] = cursor.getString(0);
					arr[1] = cursor.getString(1);
				}
				return arr;
			}
		}
		return null;
	}
	
	/** 방향코드 조회
	 * @param nsCode
	 * @param bhName
	 * @return
	 */
	public String getBanghyangCode(String nsCode, String bhName) {
		// TODO Auto-generated method stub
		String sql = "select bh_code from banghyang_se where ns_code = '" + nsCode
				+ "' and bh_name = '" + bhName + "'; ";
		String bhCode = "";
		// Log.e("selectData(): ", sql);

		// System.out.println("============================ sql : " + sql);

		try {
			Cursor cur = getSqlite().rawQuery(sql, null);
			cur.moveToFirst();
			if (cur.getCount() > 0) {
				bhCode = cur.getString(0);
			}
		} catch (SQLException se) {
			// TODO: handle exception
			Log.e("selectData()Error! : ", se.toString());
		}

		return bhCode;
	}
	
	
	
	/** bsinfo 테이블 정보 조회
	 * @return 목록 리턴
	 */
	public Cursor fetchBBBsCode(){
		String rtnStr = "";
		StringBuffer sql = new StringBuffer();
		
		sql.append("	SELECT bbcode, bbname, bscode, bsname, useyn from BSINFO      \n");
		
		Cursor cursor = getSqlite().rawQuery(sql.toString(), null);
		Log.d("process", "query fetchBBBsCode !");

		return cursor;
	}
	
	/** bsinfo 테이블 정보 조회
	 * @return 목록 리턴
	 */
	public String fetchBBBsCodeSelected(){
		String rtnStr = "";
		StringBuffer sql = new StringBuffer();
		
		sql.append("	SELECT bscode from BSINFO where useyn='Y'     \n");
		
		
		Cursor cursor = getSqlite().rawQuery(sql.toString(), null);
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			if(i==0){
				rtnStr+= cursor.getString(0);
			}else{
				rtnStr+= "|"+cursor.getString(0);
			}
		}
		Log.d("process", "query fetchBBBsCode !"+rtnStr);

		return rtnStr;
	}
	
	/** 부서코드 useyn update
	 * @param date
	 * @return
	 */
	public String updateBBBsCode(String bscode, String useyn){
		getSqlite().beginTransaction();
		
		String sql = "UPDATE BSINFO SET useyn = '"+useyn+"' where bscode='"+bscode+"'";
		
		try {
			getSqlite().execSQL(sql);
			Log.d("","setdataMngUpdate sql = " + sql);
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		return null;
	}
	
	/** 접보지사 선택 코드 초기화
	 * @return
	 */
	public String updateBBBsCodeClean(){
		getSqlite().beginTransaction();
		
		String sql = "UPDATE BSINFO SET useyn = 'N' where 1=1 ";
		
		try {
			getSqlite().execSQL(sql);
			Log.d("","setdataMngUpdate sql = " + sql);
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		return null;
	}
	/**부서코드 테이블 초기화
	 * @param bscode
	 * @param useyn
	 * @return
	 */
	public String updateBBBsCodeInit(){
		getSqlite().beginTransaction();
		
		String sql = "UPDATE BSINFO SET useyn = 'N' where 1=1";
		
		try {
			getSqlite().execSQL(sql);
			Log.d("","setdataMngUpdate sql = " + sql);
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		return null;
	}
	
	/** 사용자가 상세 데이타 확인한 값 저장.(알람용)
	 * @param rpt_id
	 * @param reg_date
	 */
	public void insertRptId(String rpt_id, String reg_date){
		getSqlite().beginTransaction();
		
		String sql = "INSERT INTO RPTID (rpt_id, reg_date) values('"+rpt_id+"','"+reg_date+"')";
		
		try {
			getSqlite().execSQL(sql);
			Log.d("","setdataMngUpdate sql = " + sql);
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
	}
	
	
	/** 사용자가 확인한 데이타 목록에 있는지 확인 (알람용) 
	 * @param rpt_id
	 * @return true=데이타 있음, false=데이타 없음
	 */
	public boolean selectRptId(String rpt_id){
		
		String rtnStr = "";
		StringBuffer sql = new StringBuffer();
		
		sql.append("	SELECT rpt_id               \n");
		sql.append("	FROM RPTID 					\n");
		sql.append("	WHERE rpt_id='"+rpt_id+"'	\n");
		
		
		Cursor cursor = getSqlite().rawQuery(sql.toString(), null);
		Log.d("process", "query selectRptId !");
		if(cursor.getCount() > 0 ){
			while (cursor.moveToNext()) {
				Log.d("",TAG+" isplay uhuh selectRptId = " + cursor.getString(0));
				return true;
			}
		}
		
		return false;
	}
	
	
	
	/** 일주일 전의 데이타 삭제
	 * @param ymdhm 년 월 일 시 분
	 */
	public void deleteRptId(){
		String rtnStr = "";
		StringBuffer sql = new StringBuffer();
		
		sql.append("delete from rptid WHERE DATE(STRFTIME('%Y-%m-%d', SUBSTR('now', 0, 11))) > DATE(reg_date, '+7 day')");
		
		getSqlite().execSQL(sql.toString());
		Log.d("process", "query selectRptId !");
	}
	
	/**
	 * DB 닫기
	 * 
	 * @return
	 */
	public void close(){
		if(sqliteDatabase != null){
			sqliteDatabase.close();
		}
		sqliteDatabase = null;
	}
	
	public boolean isClosed(){
		if(sqliteDatabase != null){
			return sqliteDatabase.isOpen();
		}
		
		return false;
	}
	
	
	//################# 스마트 원샷 전용 메소드############# 
	//교량 조회
	public Cursor selectBms_Krgb(String krname, String jbgubun, String jisaname) {
		Log.d("",TAG+" selectBms_Krgb()");
		 if(jbgubun.equals("1종")){
			 jbgubun = "02";
		 }else if(jbgubun.equals("2종")){
			 jbgubun = "03";
		 }else if(jbgubun.equals("기타")){
			 jbgubun = "99";
		 }
		
		 getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" SELECT code,   												        \n");
			sql.append("        codemyeong,                           							\n");
			sql.append("        jangbicode,                           							\n");
			sql.append("        jangbimyeong,                           						\n");
			sql.append("        dogongcode,                           							\n");
			sql.append("        chajongcode,                           							\n");
			sql.append("        deungrokno,                           							\n");
			sql.append("        gyugyeok,                           							\n");
			sql.append("        pibuseomyeong                           						\n");
			sql.append(" FROM SMART_JEONGBI                            						    \n");
			sql.append(" WHERE dogongcode='"+krname+"'                           						\n");
			sql.append(" ORDER BY dogongcode                            						\n");
			
//			sql.append(" SELECT  krcode,   												\n");
//			sql.append("        krname1,                           							\n");
//			sql.append("        krsbbeonho,                            							\n");
//			sql.append("        nsmyeong,                           							\n");
//			sql.append("        ejung                           							\n");
//			sql.append("   FROM bms_krgb                            						\n");
//			sql.append("  WHERE jisaname='"+jisaname+"'   						\n");
//			if(!"".equals(Common.nullCheck(krname))){
//				sql.append("  AND krname1 like '%"+ krname +"%'    						\n");
//			}
			
//			if(!jbgubun.equals("전체")){
//				sql.append("  AND jbgubun ='"+jbgubun+"'    						\n");
//			}
//			
//			sql.append("   group by krcode, krname1, krsbbeonho      						\n");
//			sql.append("   order by krname1      						\n");
			
			Log.d("process", "sql.toString() : "+sql.toString());
			System.out.println("조회 시작  "+System.currentTimeMillis());
			 cursor = getSqlite().rawQuery(sql.toString(), null);
			System.out.println("조회 시작  "+System.currentTimeMillis() +":"+ cursor.getCount());
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	//교량 조회-이정 기준
	public Cursor selectBms_Krgb_Ijeong(String jbgubun, String jisaname,String nscode, String ijung, String nscode2, String ijung2) {
		 if(jbgubun.equals("1종")){
			 jbgubun = "02";
		 }else if(jbgubun.equals("2종")){
			 jbgubun = "03";
		 }else if(jbgubun.equals("기타")){
			 jbgubun = "99";
		 }
		
		 getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

//			select * from(
//					select * from bms_krgb
//					where jisaname = '동서울지사'
//					and nscode = '1000'
//					)
//					where 4 > CAST(ejung AS DOUBLE)
//					or CAST(ejung AS DOUBLE) > 127
//					group by agcode
//					order by ijeong
			
			//순환선 처리
			boolean isCircleLine = false;
			boolean isCircleLine_over = false;
			double currentIjeong = -1;
			double cutlineIjeong = -1;
			if(null != ijung && !"".equals(ijung)){
				currentIjeong = Double.parseDouble(ijung); 
			}
			if(nscode.equals("1000") && currentIjeong  > 123){//ijeong max 128km
				double maxIjeong = 128;
				double varMinusValue = 128-currentIjeong;
				cutlineIjeong = varMinusValue;
				isCircleLine_over = true;
			}else if(nscode.equals("1000") && currentIjeong < 5){//ijeong max 128km
				double maxIjeong = 128;
				double varMinusValue = 5-currentIjeong;
				cutlineIjeong = maxIjeong - varMinusValue;
				isCircleLine = true;
			}
			
			//순환선 처리
			boolean isCircleLine2 = false;
			boolean isCircleLine_over2 = false;
			double currentIjeong2 = -1;
			double cutlineIjeong2 = -1;
			if(null != ijung2 && !"".equals(ijung2)){
				currentIjeong2 = Double.parseDouble(ijung2); 
			}
			if(nscode.equals("1000") && currentIjeong2  > 123){//ijeong max 128km
				double maxIjeong = 128;
				double varMinusValue = 128-currentIjeong2;
				cutlineIjeong2 = varMinusValue;
				isCircleLine_over2 = true;
			}else if(nscode.equals("1000") && currentIjeong2 < 5){//ijeong max 128km
				double maxIjeong = 128;
				double varMinusValue = 5-currentIjeong2;
				cutlineIjeong2 = maxIjeong - varMinusValue;
				isCircleLine2 = true;
			}
			
			
			sql.append(" SELECT  * from(  												\n");
			
				sql.append(" SELECT  krcode,   												\n");
				sql.append("        krname1,                           							\n");
				sql.append("        krsbbeonho,                            							\n");
				sql.append("        nsmyeong,                           							\n");
				sql.append("        ejung,                           							\n");
				sql.append("        jisaname                          							\n");
				sql.append("   FROM bms_krgb                            						\n");
				sql.append("  WHERE jisaname='"+jisaname+"'   						\n");
				sql.append("   AND nscode='"+nscode+"'															\n");
				//순환선 처리
				if(isCircleLine){
					sql.append("  AND CAST('"+ijung+"' AS DOUBLE) > CAST(ejung AS DOUBLE)   						\n");
					sql.append("  OR CAST(ejung AS DOUBLE) > "+cutlineIjeong+"						\n");
				}else if(isCircleLine_over){
					sql.append("  AND CAST('"+ijung+"' AS DOUBLE) < CAST(ejung AS DOUBLE)   						\n");
					sql.append("  OR CAST(ejung AS DOUBLE) < "+cutlineIjeong+"						\n");
				}else{
					sql.append("   AND nscode='"+nscode+"'															\n");
					sql.append("   AND CAST('"+ijung+"' AS DOUBLE) BETWEEN CAST(ejung AS DOUBLE)-5 	\n");
				}
				
				if(!jbgubun.equals("전체")){
					sql.append("  AND jbgubun ='"+jbgubun+"'    						\n");
				}
//				sql.append("   group by krcode, krname1, krsbbeonho      						\n");
//				sql.append("   order by krname1      						\n");
				
			sql.append("   union all      						\n");
				
				sql.append(" SELECT  krcode,   												\n");
				sql.append("        krname1,                           							\n");
				sql.append("        krsbbeonho,                            							\n");
				sql.append("        nsmyeong,                           							\n");
				sql.append("        ejung,                           							\n");
				sql.append("        jisaname                          							\n");
				sql.append("   FROM bms_krgb                            						\n");
				sql.append("  WHERE jisaname='"+jisaname+"'   						\n");
				sql.append("   AND nscode='"+nscode2+"'															\n");
				//순환선 처리
				if(isCircleLine2){
					sql.append("  AND CAST('"+ijung2+"' AS DOUBLE) > CAST(ejung AS DOUBLE)   						\n");
					sql.append("  OR CAST(ejung AS DOUBLE) > "+cutlineIjeong2+"						\n");
				}else if(isCircleLine_over2){
					sql.append("  AND CAST('"+ijung2+"' AS DOUBLE) < CAST(ejung AS DOUBLE)   						\n");
					sql.append("  OR CAST(ejung AS DOUBLE) < "+cutlineIjeong2+"						\n");
				}else{
					sql.append("   AND nscode='"+nscode2+"'															\n");
					sql.append("   AND CAST('"+ijung2+"' AS DOUBLE) BETWEEN CAST(ejung AS DOUBLE)-5 	\n");
				}
				if(!jbgubun.equals("전체")){
					sql.append("  AND jbgubun ='"+jbgubun+"'    						\n");
				}
//				sql.append("   group by krcode, krname1, krsbbeonho      						\n");
//				sql.append("   order by krname1      						\n");
			
			sql.append("   )WHERE jisaname='"+jisaname+"'      						\n");
			sql.append("   group by krcode, krname1, krsbbeonho, jisaname      						\n");
			sql.append("   order by krname1      						\n");
			
			
			
			
			
			Log.d("process", "sql.toString() : "+sql.toString());
			System.out.println("조회 시작  "+System.currentTimeMillis());
			 cursor = getSqlite().rawQuery(sql.toString(), null);
			System.out.println("조회 시작  "+System.currentTimeMillis() +":"+ cursor.getCount());
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	
	/** 교량 상세 조회
	 * @param krcode
	 * @param krsbbeonho
	 * @return
	 */
	public Cursor selectBms_Krgb_Detail(String krcode, String krsbbeonho) {
		
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" SELECT  krcode,   												\n");
			sql.append("        krname1,                           							\n");
			sql.append("        krsbbeonho,                           							\n");
			sql.append("        nscode,                           							\n");
			sql.append("        ejung,                           							\n");
			sql.append("        nsmyeong as nsname,                           							\n");
			sql.append("        kyogo,                           							\n");
			sql.append("        younjang,                           							\n");
			sql.append("        saddress,                           							\n");
			sql.append("        sbgjhyungsik1,                           							\n");
			sql.append("        scemhyungsikname,                           							\n");
			sql.append("        ckgansu                          							\n");
			sql.append("   FROM bms_krgb                            						\n");
			sql.append("  WHERE krcode = '"+krcode+"'    						\n");
			sql.append("  AND krsbbeonho = '"+krsbbeonho+"'    						\n");
			sql.append("   group by krcode, krname1, krsbbeonho      						\n");
			sql.append("   order by krname1      						\n");
			
			Log.d("process", "selectBms_Krgb_Detail sql.toString() : "+sql.toString());
			System.out.println("selectBms_Krgb_Detail 조회 시작  "+System.currentTimeMillis());
			 cursor = getSqlite().rawQuery(sql.toString(), null);
			System.out.println("selectBms_Krgb_Detail 조회 시작  "+System.currentTimeMillis() +":"+ cursor.getCount());
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	/** 상부구조형식 구하기
	 * @param sbgjhyungsikcode
	 * @return
	 */
	public String selectBms_Code_sbgjhyungsikName(String sbgjhyungsikcode) {
		
		getSqlite().beginTransaction();
		String rtnStr = "";
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" SELECT  codemyung   												\n");
			sql.append("   FROM BMS_CODE_C                            		\n");
			sql.append("  WHERE  code = '"+sbgjhyungsikcode+"'    	\n");
			sql.append("  AND CODEID='55'											\n");
			
			Log.d("process", "selectBms_Code_sbgjhyungsikName sql.toString() : "+sql.toString());
			 cursor = getSqlite().rawQuery(sql.toString(), null);
			 
			 while (cursor.moveToNext()) {
				rtnStr = cursor.getString(0);
				break;
			}
			 
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return rtnStr;
	}
	
	/** 부재코드 이름 구하기
	 * @param sbgjhyungsikcode
	 * @return
	 */
	public Cursor selectBms_Code_Bjae(String codeid) {
		
		getSqlite().beginTransaction();
		String rtnStr = "";
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" SELECT  code, codemyung   												\n");
			sql.append("   FROM BMS_CODE_C                            		\n");
			sql.append("  WHERE  CODEID='"+codeid+"'						    	\n");
			
			Log.d("process", "selectBms_Code_Bjae sql.toString() : "+sql.toString());
			 cursor = getSqlite().rawQuery(sql.toString(), null);
			 
			 while (cursor.moveToNext()) {
				rtnStr = cursor.getString(0);
				break;
			}
			 
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	/**지사코드를 이용한 본부 코드 조회 
	 * @return 본부코드
	 */
	public Cursor selectBms_BSCODE(String jisaCode) {
		Log.d("",TAG+" selectBms_BSCODE()");
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" SELECT  bonbucode   												\n");
			sql.append("   FROM bms_bsinfo                            						\n");
			sql.append("   WHERE jisacode !='본부코드'                            		\n");
			sql.append("   AND jisacode = '"+jisaCode+"'                            		\n");
			
			Log.d("process", "sql.toString() : "+sql.toString());
			 cursor = getSqlite().rawQuery(sql.toString(), null);
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	

	
	/**본부 목록 조회
	 * @return
	 */
	/*public Cursor selectBms_BSINFO_BONBU() {
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT  bbcode,   												\n");
			sql.append("        bbname                           							\n");
			sql.append("   FROM bsinfo_new                            						\n");
			sql.append("   WHERE bbcode !='본부코드'                            		\n");
			sql.append("   AND bbname like '%본부%'                           		\n");
			sql.append("   AND sort != ''                           		\n");
			sql.append("   group by bbcode,bbname                           		\n");
			sql.append("   order by sort                           		\n");
			
			Log.d("process", "sql.toString() : "+sql.toString());
			 cursor = getSqlite().rawQuery(sql.toString(), null);
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		return cursor;
	}*/
	
	
	/** 2018.06.22 변경 **/
	/**본부 목록 조회
	 * @return
	 */
	public Cursor selectBms_BSINFO_BONBU() {
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" SELECT  bbcode, bbname, sort   							\n");
			sql.append(" FROM bsinfo_new                            				\n");
			sql.append(" WHERE sort IS NOT NULL                 	           		\n");
			sql.append("   AND( bbname like '%본부%'                           		\n");
			sql.append("   OR bbname like '%사업단%'                           		\n");
			sql.append("   OR bbname like '%연구원%'                           		\n");
			sql.append("   OR bbname like '%센터%'                           			\n");
			sql.append("   OR bbname like '%개발원%'                           		\n");
			sql.append("   OR bbname like '%총무처%' )                          		\n");
			sql.append(" GROUP BY bbcode,bbname, sort                           	\n");
			sql.append(" ORDER BY CAST(sort as integer)                           \n");
			
			Log.d("process", "sql.toString() : "+sql.toString());
			 cursor = getSqlite().rawQuery(sql.toString(), null);
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}

	
	/**지사 목록 조회
	 * @return
	 */
	public Cursor selectBms_BSINFO_JISA(String bonbucode) {
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

/*			sql.append(" SELECT  jisacode,   												\n");
			sql.append("        jisaname                           							\n");
			sql.append("   FROM bms_bsinfo                            						\n");
			sql.append("  WHERE bonbucode = '"+bonbucode+"'    						\n");
			sql.append("  AND bonbucode !='본부코드'                            		\n");
*/			
			sql.append(" SELECT  bscode,   										\n");
			sql.append("         bsname                           				\n");
			sql.append(" FROM bsinfo_new                            			\n");
			sql.append(" WHERE sort is not null    								\n");
			sql.append(" AND bbcode = '"+bonbucode+"'    						\n");
			sql.append(" AND bbcode !='본부코드'                            		\n");
			//sql.append(" AND bsname not like '%본부%'                            		\n");
			    
		
			
			Log.d("process", "sql.toString() : "+sql.toString());
			cursor = getSqlite().rawQuery(sql.toString(), null);
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	/**
	 * @param bonbucode
	 * @return
	 */
	public String selectBms_BSINFO_JISANAME(String jisacode) {
		getSqlite().beginTransaction();
		
		String jisaname = "";
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" SELECT  jisacode,   												\n");
			sql.append("        jisaname                           							\n");
			sql.append("   FROM bms_bsinfo                            						\n");
			sql.append("  WHERE jisacode = '"+jisacode+"'    						\n");
			
			Log.d("process", "sql.toString() : "+sql.toString());
			cursor = getSqlite().rawQuery(sql.toString(), null);

			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				jisaname = cursor.getString(1);
			}
			
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return jisaname;
	}
	
	/** 종별 목록 조회
	 * @return
	 * jbgubun 02 (1종) 
	 * jbgubun 03 (2종) 
	 * jbgubun 99 (기타)
	 */
	public Cursor selectBms_Krgb_JongByul() {
		Log.d("",TAG+" selectBms_Krgb_JongByul()");
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" SELECT  jbgubun   												\n");
			sql.append("   FROM bms_krgb                            						\n");
			sql.append("  GROUP BY JBGUBUN                            		\n");
			
			Log.d("process", "sql.toString() : "+sql.toString());
			cursor = getSqlite().rawQuery(sql.toString(), null);
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	/**경간갯수 조회
	 * @return
	 * krcode = 교량코드
	 */
	public Cursor selectBms_Krgb_CKGANSU(String krcode, String krsbbeonho) {
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" SELECT  CKGANSU   												\n");
			sql.append("   FROM bms_krgb                            						\n");
			sql.append("   WHERE KRCODE='"+krcode+"'        						\n");
			sql.append("   AND krsbbeonho='"+krsbbeonho+"'        				\n");
			
			Log.d("process", "sql.toString() : "+sql.toString());
			cursor = getSqlite().rawQuery(sql.toString(), null);
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	/**거더갯수 조회
	 * @return
	 * krcode = 교량코드
	 */
	public Cursor selectBms_Krgb_GIRDIR(String krcode, String krsbbeonho) {
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" SELECT  kgjhgaesu  												\n");
			sql.append("   FROM bms_krgb                            						\n");
			sql.append("   WHERE KRCODE='"+krcode+"'        						\n");
			sql.append("   AND krsbbeonho='"+krsbbeonho+"'        				\n");
			
			Log.d("process", "sql.toString() : "+sql.toString());
			cursor = getSqlite().rawQuery(sql.toString(), null);
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	
	/**경간 정보 조회
	 * @return
	 * krcode = 교량코드
	 */
	public Cursor selectBms_Krgb_CKGANSUINFO(String krcode, String krsbbeonho) {
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" SELECT  CKGANSU   												\n");
			sql.append(" 				,KGJJIRBEONHO   												\n");
			sql.append(" 				,KGJJGUBUN   												\n");
			sql.append(" 				,KGJJBEONHO   												\n");
			sql.append("   FROM bms_krgb                            						\n");
			sql.append("   WHERE KRCODE='"+krcode+"'        						\n");
			sql.append("   AND krsbbeonho='"+krsbbeonho+"'        						\n");
			
			Log.d("process", "sql.toString() : "+sql.toString());
			cursor = getSqlite().rawQuery(sql.toString(), null);
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	
	
	/** 교대 교각 조회
	 * @param krcode
	 * @param krsbbeonho
	 * @return
	 */
	public Cursor selectBms_Krgb_JJGUBUN(String krcode, String krsbbeonho) {
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" SELECT  JJGUBUN, KRBCGAESU, BIGO, KGJJBEONHO	\n");
			sql.append("   FROM bms_krgb                            						\n");
			sql.append("   WHERE KRCODE='"+krcode+"'        						\n");
			sql.append("   AND krsbbeonho='"+krsbbeonho+"'        				\n");
			sql.append("   AND JJGUBUN IS NOT NULL				    				\n");
			sql.append("   ORDER BY JJGUBUN                            					\n");
			Log.d("process", "sql.toString() : "+sql.toString());
			cursor = getSqlite().rawQuery(sql.toString(), null);
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	/** 점검 정보 등록-교량
	 * @param krcode
	 * @param krsbbeonho
	 * @return
	 */
	public Cursor insertBms_Krgb_JGINFO(String krcode, String krsbbeonho, String krname1, String bhgubun, 
			String kgjjgubun, String kgjjbeonho, String bigo, String content, String filename, 
			String bjae1, String bjae2, String bjae3, String bjae1_code, String jhbeonho, String kdkggubun, 
			String krbcbhgubun, String krbcirbeonho, String krbcsbbeonho, String swbeonho, String bonbucode, 
			String jisacode, String jgubun, String tagYn, String sisul, String swname) {
		Log.d("", "insertBms_Krgb_JGINFO()~!");
		Log.d("", "insertBms_Krgb_JGINFO() - krcode : "+krcode);
		Log.d("", "insertBms_Krgb_JGINFO() - krsbbeonho : "+krsbbeonho);
		Log.d("", "insertBms_Krgb_JGINFO() - krname1 : "+krname1);
		Log.d("", "insertBms_Krgb_JGINFO() - bhgubun : "+bhgubun);
		Log.d("", "insertBms_Krgb_JGINFO() - kgjjgubun : "+kgjjgubun);
		Log.d("", "insertBms_Krgb_JGINFO() - kgjjbeonho : "+kgjjbeonho);
		Log.d("", "insertBms_Krgb_JGINFO() - bigo : "+bigo);
		Log.d("", "insertBms_Krgb_JGINFO() - content : "+content);
		Log.d("", "insertBms_Krgb_JGINFO() - filename : "+filename);
		//Log.d("", "insertBms_Krgb_JGINFO() - date : "+date);
		Log.d("", "insertBms_Krgb_JGINFO() - bjae1 : "+bjae1);
		Log.d("", "insertBms_Krgb_JGINFO() - bjae2 : "+bjae2);
		Log.d("", "insertBms_Krgb_JGINFO() - bjae3 : "+bjae3);
		Log.d("", "insertBms_Krgb_JGINFO() - bjae1_code : "+bjae1_code);
		Log.d("", "insertBms_Krgb_JGINFO() - jhbeonho : "+jhbeonho);
		Log.d("", "insertBms_Krgb_JGINFO() - kdkggubun : "+kdkggubun);
		Log.d("", "insertBms_Krgb_JGINFO() - krbcbhgubun : "+krbcbhgubun);
		Log.d("", "insertBms_Krgb_JGINFO() - krbcirbeonho : "+krbcirbeonho);
		Log.d("", "insertBms_Krgb_JGINFO() - krbcsbbeonho : "+krbcsbbeonho);
		Log.d("", "insertBms_Krgb_JGINFO() - swbeonho : "+swbeonho);
		Log.d("", "insertBms_Krgb_JGINFO() - bonbucode : "+bonbucode);
		Log.d("", "insertBms_Krgb_JGINFO() - jisacode : "+jisacode);
		Log.d("", "insertBms_Krgb_JGINFO() - jgubun : "+jgubun);
		Log.d("", "insertBms_Krgb_JGINFO() - tagYn : "+tagYn);
		Log.d("", "insertBms_Krgb_JGINFO() - sisul : "+sisul);
		Log.d("", "insertBms_Krgb_JGINFO() - swname : "+swname);
		
		
		getSqlite().beginTransaction();
		//String date = Common.getCalendarDateTime();
		String date = Common.getCalendarDateYMDHMS();
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" INSERT INTO BMS_KRGB_JGINFO 							\n");
			sql.append("   (krcode, krsbbeonho, krname1, bhgubun, kgjjgubun, kgjjbeonho, bigo, content, filename, date, bjae1, bjae2, bjae3, sendyn, bjae1_code, jhbeonho, kdkggubun, krbcbhgubun, krbcirbeonho, krbcsbbeonho, swbeonho, bonbucode, jisacode, jgubun, tagYn, sisul, swname) 	\n");
			sql.append("   values										\n");
			sql.append("   ('"+krcode+"', '"+krsbbeonho+"', '"+krname1+"' , '"+bhgubun+"' , '"+kgjjgubun+"' , '"+kgjjbeonho+"' , '"+bigo+"' , '"+content+"' , '"+filename+"' , '"+date+"' , '"+bjae1+"' , '"+bjae2+"' , '"+bjae3+"' ,'N' , '"+bjae1_code+"', '"+jhbeonho+"', '"+kdkggubun+"', '"+krbcbhgubun+"', '"+krbcirbeonho+"', '"+krbcsbbeonho+"', '"+swbeonho+"', '"+bonbucode+"', '"+jisacode+"', '"+jgubun+"', '"+tagYn+"', '"+sisul+"', '"+swname+"')	\n");
			
			Log.d("process", "sql.toString() : "+sql.toString());
			getSqlite().execSQL(sql.toString());
			
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	/** 이전 내용 일치 확인_교량
	 *  부재1, 부재2에 따라 설정됨.
	 * @return 0(변경됨), 1(부재2까지 고정), 2(부재3까지 고정)
	 */
	public int checkDataChanged_Kyoryang(String krcode, String krsbbeonho, String krname1, 
			String bhgubun, String kgjjgubun, String kgjjbeonho, String bigo, String content, String filename,  
			String bjae1, String bjae2, String bjae3, String bjae1_code, String jhbeonho, String kdkggubun, 
			String krbcbhgubun, String krbcirbeonho, String krbcsbbeonho, String swbeonho, String bonbucode, 
			String jisacode, String jgubun, String tagYn, String sisul){
		Log.d("", "checkDataChanged_Kyoryang()~!");

		
		Log.d("", "checkDataChanged_Kyoryang() - krcode : "+krcode);
		Log.d("", "checkDataChanged_Kyoryang() - krsbbeonho : "+krsbbeonho);
		Log.d("", "checkDataChanged_Kyoryang() - krname1 : "+krname1);
		Log.d("", "checkDataChanged_Kyoryang() - bhgubun : "+bhgubun);
		Log.d("", "checkDataChanged_Kyoryang() - kgjjgubun : "+kgjjgubun);
		Log.d("", "checkDataChanged_Kyoryang() - kgjjbeonho : "+kgjjbeonho);
		Log.d("", "checkDataChanged_Kyoryang() - bigo : "+bigo);
		Log.d("", "checkDataChanged_Kyoryang() - content : "+content);
		Log.d("", "checkDataChanged_Kyoryang() - filename : "+filename);
		//Log.d("", "insertBms_Krgb_JGINFO() - date : "+date);
		Log.d("", "checkDataChanged_Kyoryang() - bjae1 : "+bjae1);
		Log.d("", "checkDataChanged_Kyoryang() - bjae2 : "+bjae2);
		Log.d("", "checkDataChanged_Kyoryang() - bjae3 : "+bjae3);
		Log.d("", "checkDataChanged_Kyoryang() - bjae1_code : "+bjae1_code);
		Log.d("", "checkDataChanged_Kyoryang() - jhbeonho : "+jhbeonho);
		Log.d("", "checkDataChanged_Kyoryang() - kdkggubun : "+kdkggubun);
		Log.d("", "checkDataChanged_Kyoryang() - krbcbhgubun : "+krbcbhgubun);
		Log.d("", "checkDataChanged_Kyoryang() - krbcirbeonho : "+krbcirbeonho);
		Log.d("", "checkDataChanged_Kyoryang() - krbcsbbeonho : "+krbcsbbeonho);
		Log.d("", "checkDataChanged_Kyoryang() - swbeonho : "+swbeonho);
		Log.d("", "checkDataChanged_Kyoryang() - bonbucode : "+bonbucode);
		Log.d("", "checkDataChanged_Kyoryang() - jisacode : "+jisacode);
		Log.d("", "checkDataChanged_Kyoryang() - jgubun : "+jgubun);
		Log.d("", "checkDataChanged_Kyoryang() - tagYn : "+tagYn);
		Log.d("", "checkDataChanged_Kyoryang() - sisul : "+sisul);
		int rntInt = 0;
		getSqlite().beginTransaction();
		String date = Common.getCalendarDateTime();
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT  							\n");
			sql.append("   krcode, krsbbeonho, krname1, bhgubun, kgjjgubun, kgjjbeonho, bigo, content, filename, date, bjae1, bjae2, bjae3, sendyn, bjae1_code, jhbeonho, kdkggubun, krbcbhgubun, krbcirbeonho, krbcsbbeonho, swbeonho, bonbucode, jisacode, jgubun, tagYn, sisul 	\n");
			sql.append(" FROM  BMS_KRGB_JGINFO		\n");
			sql.append(" ORDER BY date DESC \n");
			sql.append(" limit 1 \n");
			
			
			Log.d("process", "sql.toString() : "+sql.toString());
			cursor = getSqlite().rawQuery(sql.toString(), null);
			String krcode_pre = "";
			String krsbbeonho_pre = "";
			String krname1_pre = "";
			String bhgubun_pre = "";
			String kgjjgubun_pre = "";
			String kgjjbeonho_pre = "";
			String bigo_pre = "";
			String content_pre = "";
			String bjae1_pre = "";
			String bjae2_pre = "";
			String bjae3_pre = "";
			
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				krcode_pre = cursor.getString(0);
				krsbbeonho_pre = cursor.getString(1);
				krname1_pre = cursor.getString(2);
				bhgubun_pre = cursor.getString(3);
				kgjjgubun_pre = cursor.getString(4);
				kgjjbeonho_pre = cursor.getString(5);
				bigo_pre = cursor.getString(6);
				content_pre = cursor.getString(7);
				bjae1_pre = cursor.getString(10);
				bjae2_pre = cursor.getString(11);
				bjae3_pre = cursor.getString(12);
			}
			
			Log.d("","checkDataChanged bjae2 = " + bjae2_pre +":"+bjae2);
			Log.d("","checkDataChanged bjae3 = " + bjae3_pre +":"+bjae3);
/*			if(krcode.equals(krcode_pre) && krsbbeonho.equals(krsbbeonho_pre)){
				if( bjae1.equals(bjae1_pre) ){
					if( bjae2.equals(bjae2_pre) ){
						rntInt = 1;
						if( bjae3.equals(bjae3_pre) ){
							rntInt = 2;	
						}
					}
				}
			}
*/			if(krcode.equals(krcode_pre) && Common.nullCheck(krname1).equals(Common.nullCheck(krname1_pre))){
				if( bjae3.equals(bjae3_pre) ){
					Log.d("","rntInt = " + rntInt +", "+bjae3_pre +":"+bjae3);
					rntInt = 1;	
				}
			}
			
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		return rntInt;
	}
	
	
	
	
	
	/** 점검목록 조회_교량.
	 * @param krcode
	 * @param krsbbeonho
	 * @return
	 */
	public Cursor selectBms_Krgb_JGINFO() {
		Log.d("", "selectBms_Krgb_JGINFO~!");
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();
			//                                  0              1               2             3              4                5           6         7            8          9     10        11      12
			sql.append(" SELECT  krcode, krsbbeonho, krname1, bhgubun, kgjjgubun, kgjjbeonho, bigo, content, filename, date, bjae1, bjae2, bjae3, sendyn, bjae1_code, jhbeonho, kdkggubun, krbcbhgubun, krbcirbeonho, krbcsbbeonho, swbeonho, jgubun \n");
			sql.append("   FROM BMS_KRGB_JGINFO                            		\n");
			sql.append("   ORDER BY DATE  desc                          				\n");
			Log.d("process", "sql.toString() : "+sql.toString());
			cursor = getSqlite().rawQuery(sql.toString(), null);
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	/** 점검목록 조회-미전송
	 * @param krcode
	 * @param krsbbeonho
	 * @return
	 */
	public Cursor selectBms_Krgb_JGINFO_ONE() {
		Log.d("", "selectBms_Krgb_JGINFO_ONE() ~!");
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();
			//                                  0              1               2             3              4                5           6         7            8          9     10        11      12      13            14              15        16          17            18                  19                  20              21              22              23           24           25     26
			sql.append(" SELECT  krcode, krsbbeonho, krname1, bhgubun, kgjjgubun, kgjjbeonho, bigo, content, filename, date, bjae1, bjae2, bjae3, sendyn, bonbucode, jisacode, jgubun, jhbeonho, kdkggubun, krbcbhgubun, krbcirbeonho, swbeonho, krbcsbbeonho, tagYn, bjae1_code, sisul, swname \n");
			sql.append("   FROM BMS_KRGB_JGINFO                            		\n");
			sql.append("   WHERE sendyn='N'			                            		\n");
			sql.append("   ORDER BY DATE  desc                          				\n");
			Log.d("process", "sql.toString() : "+sql.toString());
			cursor = getSqlite().rawQuery(sql.toString(), null);
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	
	/** 점검목록 전부 삭제-교량
	 * @param krcode
	 * @param krsbbeonho
	 * @return
	 */
	public Cursor deleteJgHistoryKyoRyang() {
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" DELETE FROM BMS_KRGB_JGINFO WHERE 1=1  \n");
			
			Log.d("process", "sql.toString() : "+sql.toString());
			getSqlite().execSQL(sql.toString());
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	
	
	
	
	/** 점검목록 선택 삭제-교량
	 * @param krcode
	 * @param krsbbeonho
	 * @return
	 */
	public Cursor deleteJgHistoryOne_Kyoryang(String date) {
		Log.d("", "deleteJgHistoryOne_Kyoryang()~!");
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" DELETE FROM BMS_KRGB_JGINFO WHERE date ='"+date+"'  \n");
			
			Log.d("process", "sql.toString() : "+sql.toString());
			getSqlite().execSQL(sql.toString());
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	/** 점검목록 전송여부 수정
	 * @param krcode
	 * @param krsbbeonho
	 * @return
	 */
	public Cursor updateJgHistorySendYn(String filename) {
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("UPDATE BMS_KRGB_JGINFO SET sendyn = 'Y' where filename='"+filename+"'");
			
			Log.d("process", "sql.toString() : "+sql.toString());
			getSqlite().execSQL(sql.toString());
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}	
	
	
	
	
	//****************************************** 터널 *******************************************************
	
	/** 터널 목록 조회
	 * @param tnname
	 * @param jisaname
	 * @return
	 */
	public Cursor selectBms_Tunnel(String tnname,  String jisaname) {
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" SELECT  tncode,   												\n");
			sql.append("        tnmyeong,                           							\n");
			sql.append("        nscode,                           							\n");
			sql.append("        nsmyeong,                            							\n");
			sql.append("        ijeong                           							\n");
			sql.append("   FROM bms_tunnel                            						\n");
			sql.append("  WHERE jisaname='"+jisaname+"'   						\n");
			if(!"".equals(Common.nullCheck(tnname))){
				sql.append("  AND tnmyeong like '%"+ tnname +"%'    						\n");
			}
			
			sql.append("   group by tncode, tnmyeong, nsmyeong, ijeong      						\n");
			sql.append("   order by tnmyeong      						\n");
			Log.d("process", "sql START --------------");
			Log.d("process", "sql.toString() : "+sql.toString());
			Log.d("process", "sql END --------------");
			System.out.println("조회 시작  "+System.currentTimeMillis());
			 cursor = getSqlite().rawQuery(sql.toString(), null);
			System.out.println("조회 시작  "+System.currentTimeMillis() +":"+ cursor.getCount());
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	/** 터널 목록 조회
	 * @param tnname
	 * @param jisaname
	 * @return
	 */
	public Cursor selectBms_Tunnel_ijeong(String jisaname, String nscode, String ijung, String nscode2, String ijung2) {
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			//순환선 처리
			boolean isCircleLine = false;
			boolean isCircleLine_over = false;
			double currentIjeong = -1;
			double cutlineIjeong = -1;
			if(null != ijung && !"".equals(ijung)){
				currentIjeong = Double.parseDouble(ijung); 
			}
			if(nscode.equals("1000") && currentIjeong  > 123){//ijeong max 128km
				double maxIjeong = 128;
				double varMinusValue = 128-currentIjeong;
				cutlineIjeong = varMinusValue;
				isCircleLine_over = true;
			}else if(nscode.equals("1000") && currentIjeong < 5){//ijeong max 128km
				double maxIjeong = 128;
				double varMinusValue = 5-currentIjeong;
				cutlineIjeong = maxIjeong - varMinusValue;
				isCircleLine = true;
			}
			
			//순환선 처리
			boolean isCircleLine2 = false;
			boolean isCircleLine_over2 = false;
			double currentIjeong2 = -1;
			double cutlineIjeong2 = -1;
			if(null != ijung2 && !"".equals(ijung2)){
				currentIjeong2 = Double.parseDouble(ijung2); 
			}
			if(nscode.equals("1000") && currentIjeong2  > 123){//ijeong max 128km
				double maxIjeong = 128;
				double varMinusValue = 128-currentIjeong2;
				cutlineIjeong2 = varMinusValue;
				isCircleLine_over2 = true;
			}else if(nscode.equals("1000") && currentIjeong2 < 5){//ijeong max 128km
				double maxIjeong = 128;
				double varMinusValue = 5-currentIjeong2;
				cutlineIjeong2 = maxIjeong - varMinusValue;
				isCircleLine2 = true;
			}
			
			StringBuffer sql = new StringBuffer();

			sql.append(" SELECT  * from (   												\n");
				sql.append(" SELECT  tncode,   												\n");
				sql.append("        tnmyeong,                           							\n");
				sql.append("        nscode,                           							\n");
				sql.append("        nsmyeong,                            							\n");
				sql.append("        ijeong,                           							\n");
				sql.append("        jisaname                           							\n");
				sql.append("   FROM bms_tunnel                            						\n");
				sql.append("  WHERE jisaname='"+jisaname+"'   						\n");
				sql.append("   AND nscode='"+nscode+"'															\n");
				//순환선 처리
				if(isCircleLine){
					sql.append("  AND CAST('"+ijung+"' AS DOUBLE) > CAST(ijeong AS DOUBLE)   						\n");
					sql.append("  OR CAST(ijeong AS DOUBLE) > "+cutlineIjeong+"						\n");
				}else if(isCircleLine_over){
					sql.append("  AND CAST('"+ijung+"' AS DOUBLE) < CAST(ijeong AS DOUBLE)   						\n");
					sql.append("  OR CAST(ijeong AS DOUBLE) < "+cutlineIjeong+"						\n");
				}else{
					sql.append("   AND CAST('"+ijung+"' AS DOUBLE) BETWEEN CAST(ijeong AS DOUBLE)-5 	\n");
					sql.append("   AND CAST(ijeong AS DOUBLE)+5															\n");
				}
				
				
				
				sql.append("   group by tncode, tnmyeong, nsmyeong, ijeong      						\n");
//				sql.append("   order by tnmyeong      						\n");
				
			sql.append("   union all      															\n");
				
				sql.append(" SELECT  tncode,   												\n");
				sql.append("        tnmyeong,                           							\n");
				sql.append("        nscode,                           							\n");
				sql.append("        nsmyeong,                            							\n");
				sql.append("        ijeong,                           							\n");
				sql.append("        jisaname                           							\n");
				sql.append("   FROM bms_tunnel                            						\n");
				sql.append("  WHERE jisaname='"+jisaname+"'   						\n");
				sql.append("   AND nscode='"+nscode2+"'															\n");
				//순환선 처리
				if(isCircleLine2){
					sql.append("  AND CAST('"+ijung2+"' AS DOUBLE) > CAST(ijeong AS DOUBLE)   						\n");
					sql.append("  OR CAST(ijeong AS DOUBLE) > "+cutlineIjeong2+"						\n");
				}else if(isCircleLine_over2){
					sql.append("  AND CAST('"+ijung2+"' AS DOUBLE) < CAST(ijeong AS DOUBLE)   						\n");
					sql.append("  OR CAST(ijeong AS DOUBLE) < "+cutlineIjeong2+"						\n");
				}else{
					sql.append("   AND CAST('"+ijung2+"' AS DOUBLE) BETWEEN CAST(ijeong AS DOUBLE)-5 	\n");
					sql.append("   AND CAST(ijeong AS DOUBLE)+5															\n");
				}
				sql.append("   group by tncode, tnmyeong, nsmyeong, ijeong      						\n");
//				sql.append("   order by tnmyeong      						\n");
			
			sql.append("   )where jisaname='"+jisaname+"'      						\n");
			sql.append("    group by tncode, tnmyeong, nsmyeong, ijeong       						\n");
			sql.append("   order by tnmyeong      						\n");
			
			Log.d("process", "sql START --------------");
			Log.d("process", "sql.toString() : "+sql.toString());
			Log.d("process", "sql END --------------");
			System.out.println("조회 시작  "+System.currentTimeMillis());
			 cursor = getSqlite().rawQuery(sql.toString(), null);
			System.out.println("조회 시작  "+System.currentTimeMillis() +":"+ cursor.getCount());
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	
	/** 터널 터널 부재1 조회
	 * @param tnname
	 * @param jisaname
	 * @return
	 */
	public Cursor selectBms_TnBuJae1(String tnname,  String jisaname) {
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" SELECT  bujaecode,   											\n");
			sql.append("        bujaename                           							\n");
			sql.append("   FROM bms_tunnel                            					\n");
			sql.append("  WHERE jisaname='"+jisaname+"'   						\n");
			if(!"".equals(Common.nullCheck(tnname))){
				sql.append("  AND tnmyeong like '%"+ tnname +"%'    			\n");
			}
			sql.append("   group by bujaecode, bujaename							\n");
			sql.append("   order by bujaename      										\n");
			Log.d("process", "sql START --------------");
			Log.d("process", "sql.toString() : "+sql.toString());
			Log.d("process", "sql END --------------");
			
			 cursor = getSqlite().rawQuery(sql.toString(), null);
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	
	/** 터널 터널 부재2 SPAN 번호조회
	 * @param tnname
	 * @param jisaname
	 * @return
	 */
	public Cursor selectBms_TnBuJae2_SPAN(String tnname,  String jisaname) {
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" SELECT  spanbeonho   											\n");
			sql.append("   FROM bms_tunnel                            					\n");
			sql.append("  WHERE jisaname='"+jisaname+"'   						\n");
			if(!"".equals(Common.nullCheck(tnname))){
				sql.append("  AND tnmyeong like '%"+ tnname +"%'    			\n");
			}
			sql.append("   group by spanbeonho											\n");
			sql.append("   order by cast(spanbeonho as integer)					\n");
			Log.d("process", "sql START --------------");
			Log.d("process", "sql.toString() : "+sql.toString());
			Log.d("process", "sql END --------------");
			
			 cursor = getSqlite().rawQuery(sql.toString(), null);
			 
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	/** 점검 정보 등록-터널
	 * @param krcode
	 * @param krsbbeonho
	 * @return
	 */
	public Cursor insertBms_Tunnel_JGINFO(String tncode, String tnname, String bjcode, String sbbeonho, 
																	String jgubun, String bonbucode, String jisacode, String swbeonho, 
																	String jjilja, String sjfilename, String jkhsahang, String tagyn, String sendyn, String bjae1, String bjae2, String bjae3, String swname) {
		getSqlite().beginTransaction();
		String date = Common.getCalendarDateTime();
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" INSERT INTO BMS_TUNNEL_JGINFO 							\n");
			sql.append("   (tncode, tnname, bjcode, sbbeonho, jgubun, bonbucode, jisacode, swbeonho, jjilja, sjfilename, jkhsahang, tagyn, sendyn, bjae1, bjae2, bjae3, swname ) 	\n");
			sql.append("   values										\n");
			sql.append("   ('"+tncode+"', '"+tnname+"', '"+bjcode+"', '"+sbbeonho+"' , '"+jgubun+"' , '"+bonbucode+"', '"+jisacode+"', '"+swbeonho+"', '"+jjilja+"', '"+sjfilename+"', '"+jkhsahang+"', '"+tagyn+"', '"+sendyn+"', '"+bjae1+"', '"+bjae2+"', '"+bjae3+"', '"+swname+"' )	\n");
			
			Log.d("process", "sql.toString() : "+sql.toString());
			getSqlite().execSQL(sql.toString());
			
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	/** 이전 내용 일치 확인_교량
	 *  부재1, 부재2에 따라 설정됨.
	 * @return 0(변경됨), 1(부재2까지 고정), 2(부재3까지 고정)
	 */
	public int checkDataChanged_Tunnel(String tncode, String tnname, String bjcode, String sbbeonho, 
			String jgubun, String bonbucode, String jisacode, String swbeonho, 
			String jjilja, String sjfilename, String jkhsahang, String tagyn, String sendyn, String bjae1, String bjae2, String bjae3){
		
		int rntInt = 0;
		getSqlite().beginTransaction();
		String date = Common.getCalendarDateTime();
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT  							\n");
			sql.append("   tncode, tnname, bjcode, sbbeonho, jgubun, bonbucode, jisacode, swbeonho, jjilja, sjfilename, jkhsahang, tagyn, sendyn, bjae1, bjae2, bjae3 	\n");
			sql.append(" FROM  BMS_TUNNEL_JGINFO		\n");
			sql.append(" ORDER BY jjilja DESC \n");
			sql.append(" limit 1 \n");
			
			
			Log.d("process", "sql.toString() : "+sql.toString());
			cursor = getSqlite().rawQuery(sql.toString(), null);
			String trcode_pre = "";
			String tnname_pre = "";
			String bjcode_pre = "";
			String sbbeonho_pre = "";
			String jgubun_pre = "";
			String bjae1_pre = "";
			String bjae2_pre = "";
			String bjae3_pre = "";
			String jkhsahang_pre = "";
			
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				trcode_pre = cursor.getString(0);
				tnname_pre = cursor.getString(1);
				bjcode_pre = cursor.getString(2);
				sbbeonho_pre = cursor.getString(3);
				jkhsahang_pre = cursor.getString(10);
				jgubun_pre = cursor.getString(5);
				bjae1_pre = cursor.getString(13);
				bjae2_pre = cursor.getString(14);
				bjae3_pre = cursor.getString(15);
			}
			Log.d("","checkDataChanged bjae2 = " + bjae2_pre +":"+bjae2);
			if(trcode_pre.equals(trcode_pre) ){
				if( bjae1.equals(bjae1_pre) ){
					if( bjae2.equals(bjae2_pre) ){
						rntInt = 1;
						if( bjae3.equals(bjae3_pre) ){
							rntInt = 2;	
						}
					}
				}
			}
			
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		return rntInt;
	}
	
	
	
	/** 점검목록 조회_터널.
	 * @param krcode
	 * @param krsbbeonho
	 * @return
	 */
	public Cursor selectBms_Tunnel_JGINFO() {
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();
//			                                    0            1           2            3            4            5               6              7          8            9             10          11        12      13        14      15        16
			sql.append(" SELECT  tncode, tnname, bjcode, sbbeonho, jgubun, bonbucode, jisacode, swbeonho, jjilja, sjfilename, jkhsahang, tagyn, sendyn, bjae1, bjae2, bjae3, swname \n");
			sql.append("   FROM BMS_TUNNEL_JGINFO                            		\n");
			sql.append("   ORDER BY JJILJA  desc                          				\n");
			Log.d("process", "sql.toString() : "+sql.toString());
			cursor = getSqlite().rawQuery(sql.toString(), null);
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	/** 점검목록 전부 삭제-터널
	 * @param krcode
	 * @param krsbbeonho
	 * @return
	 */
	public Cursor deleteJgHistoryTunnel() {
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" DELETE FROM BMS_TUNNEL_JGINFO WHERE 1=1  \n");
			
			Log.d("process", "sql.toString() : "+sql.toString());
			getSqlite().execSQL(sql.toString());
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	/** 점검목록 선택 삭제
	 * @param krcode
	 * @param krsbbeonho
	 * @return
	 */
	public Cursor deleteJgHistoryOne_Tunnel(String date) {
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" DELETE FROM BMS_TUNNEL_JGINFO WHERE jjilja ='"+date+"'  \n");
			
			Log.d("process", "sql.toString() : "+sql.toString());
			getSqlite().execSQL(sql.toString());
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	/** 점검목록 조회-미전송
	 * @param krcode
	 * @param krsbbeonho
	 * @return
	 */
	public Cursor selectBms_Tunnel_JGINFO_ONE() {
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();
			//                                   0         1            2            3             4               5             6            7          8              9            10      11         12
			sql.append(" SELECT  tncode, bjcode, sbbeonho, jgubun, bonbucode, jisacode, swbeonho, jjilja, sjfilename, jkhsahang, bjae2, bjae3, swname \n");
			sql.append("   FROM BMS_TUNNEL_JGINFO                            		\n");
			sql.append("   WHERE sendyn='N'			                            		\n");
			sql.append("   ORDER BY jjilja  desc                          				\n");
			Log.d("process", "sql.toString() : "+sql.toString());
			cursor = getSqlite().rawQuery(sql.toString(), null);
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	/** 터널 상세 조회
	 * @param krcode
	 * @param krsbbeonho
	 * @return
	 */
	public Cursor selectBms_Tunnel_Detail(String tncode) {
		
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" SELECT  nsmyeong,   									\n");
			sql.append("        ijeong,                           							\n");
			sql.append("        saddress,                           						\n");
			sql.append("        gongbeop,                           					\n");
			sql.append("        iggmhyeongsik,                           			\n");
			sql.append("        yeonjang,                           					\n");
			sql.append("        nopi                          								\n");
			sql.append("   FROM bms_tunnel                            			\n");
			sql.append("  WHERE tncode = '"+tncode+"'    					\n");
			sql.append("   group by tncode      									\n");
			
			Log.d("process", "selectBms_Tunnel_Detail sql.toString() : "+sql.toString());
			System.out.println("selectBms_Krgb_Detail 조회 시작  "+System.currentTimeMillis());
			 cursor = getSqlite().rawQuery(sql.toString(), null);
			System.out.println("selectBms_Tunnel_Detail 조회 종료  "+System.currentTimeMillis() +":"+ cursor.getCount());
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	/** 점검목록 전송여부 수정-터널
	 * @param krcode
	 * @param krsbbeonho
	 * @return
	 */
	public Cursor updateJgHistorySendYn_Tunnel(String filename) {
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("UPDATE BMS_TUNNEL_JGINFO SET sendyn = 'Y' where SJFILENAME='"+filename+"'");
			
			Log.d("process", "sql.toString() : "+sql.toString());
			getSqlite().execSQL(sql.toString());
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}	
	
	
	//****************************************** 터널 *******************************************************
	/** 점검 정보 등록-터널
	 * @param krcode
	 * @param krsbbeonho
	 * @return
	 *//*
	public Cursor insertBms_Amgeo_JGINFO(String agcode, String agname, String bjcode, String sbbeonho, 
																	String jgubun, String bonbucode, String jisacode, String swbeonho, 
																	String jjilja, String sjfilename, String jkhsahang) {
		getSqlite().beginTransaction();
		String date = Common.getCalendarDateTime();
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" INSERT INTO BMS_AMGEO_JGINFO 							\n");
			sql.append("   (agcode, agname, bjcode, sbbeonho, jgubun, bonbucode, jisacode, swbeonho, jjilja, sjfilename, jkhsahang) 	\n");
			sql.append("   values										\n");
			sql.append("   ('"+agcode+"', '"+agname+"', '"+bjcode+"', '"+sbbeonho+"' , '"+jgubun+"' , '"+bonbucode+"', '"+jisacode+"', '"+swbeonho+"', '"+jjilja+"', '"+sjfilename+"', '"+jkhsahang+"' )	\n");
			
			Log.d("process", "sql.toString() : "+sql.toString());
			getSqlite().execSQL(sql.toString());
			
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		return cursor;
	}*/

	/** 터널 목록 조회
	 * @param tnname
	 * @param jisaname
	 * @return
	 */
	public Cursor selectBms_Amgeo(String agname,  String jisaname) {
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" SELECT  agcode,   												\n");
			sql.append("        agmyeong,                           							\n");
			sql.append("        nscode,                           							\n");
			sql.append("        nsmyeong,                            							\n");
			sql.append("        ijeong                           							\n");
			sql.append("   FROM bms_amgeo                            						\n");
			sql.append("  WHERE jisaname='"+jisaname+"'   						\n");
			if(!"".equals(Common.nullCheck(agname))){
				sql.append("  AND agmyeong like '%"+ agname +"%'    						\n");
			}
			
			sql.append("   group by agcode, agmyeong, nsmyeong, ijeong      						\n");
			sql.append("   order by nscode,cast(ijeong as decimal)      						\n");
			Log.d("process", "sql START --------------");
			Log.d("process", "sql.toString() : "+sql.toString());
			Log.d("process", "sql END --------------");
			System.out.println("조회 시작  "+System.currentTimeMillis());
			 cursor = getSqlite().rawQuery(sql.toString(), null);
			System.out.println("조회 시작  "+System.currentTimeMillis() +":"+ cursor.getCount());
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	
	/** 터널 목록 조회
	 * @param tnname
	 * @param jisaname
	 * @return
	 */
	public Cursor selectBms_Amgeo_ijeong(String jisaname, String nscode, String ijung, String nscode2, String ijung2) {
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			
			//순환선 처리
			boolean isCircleLine = false;
			boolean isCircleLine_over = false;
			double currentIjeong = -1;
			double cutlineIjeong = -1;
			if(null != ijung && !"".equals(ijung)){
				currentIjeong = Double.parseDouble(ijung); 
			}
			if(nscode.equals("1000") && currentIjeong  > 123){//ijeong max 128km
				double maxIjeong = 128;
				double varMinusValue = 128-currentIjeong;
				cutlineIjeong = varMinusValue;
				isCircleLine_over = true;
			}else if(nscode.equals("1000") && currentIjeong < 5){//ijeong max 128km
				double maxIjeong = 128;
				double varMinusValue = 5-currentIjeong;
				cutlineIjeong = maxIjeong - varMinusValue;
				isCircleLine = true;
			}
			
			//순환선 처리
			boolean isCircleLine2 = false;
			boolean isCircleLine_over2 = false;
			double currentIjeong2 = -1;
			double cutlineIjeong2 = -1;
			if(null != ijung2 && !"".equals(ijung2)){
				currentIjeong2 = Double.parseDouble(ijung2); 
			}
			if(nscode.equals("1000") && currentIjeong2  > 123){//ijeong max 128km
				double maxIjeong = 128;
				double varMinusValue = 128-currentIjeong2;
				cutlineIjeong2 = varMinusValue;
				isCircleLine_over2 = true;
			}else if(nscode.equals("1000") && currentIjeong2 < 5){//ijeong max 128km
				double maxIjeong = 128;
				double varMinusValue = 5-currentIjeong2;
				cutlineIjeong2 = maxIjeong - varMinusValue;
				isCircleLine2 = true;
			}
			
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT  * from(   												\n");
				sql.append(" SELECT  agcode,   												\n");
				sql.append("        agmyeong,                           							\n");
				sql.append("        nscode,                           							\n");
				sql.append("        nsmyeong,                            							\n");
				sql.append("        ijeong,                           							\n");
				sql.append("        jisaname                           							\n");
				sql.append("   FROM bms_amgeo                            						\n");
				sql.append("  WHERE jisaname='"+jisaname+"'   						\n");
				sql.append("   AND nscode='"+nscode+"'															\n");
				//순환선 처리
				if(isCircleLine){
					sql.append("  AND CAST('"+ijung+"' AS DOUBLE) > CAST(ijeong AS DOUBLE)   						\n");
					sql.append("  OR CAST(ijeong AS DOUBLE) > "+cutlineIjeong+"						\n");
				}else if(isCircleLine_over){
					sql.append("  AND CAST('"+ijung+"' AS DOUBLE) < CAST(ijeong AS DOUBLE)   						\n");
					sql.append("  OR CAST(ijeong AS DOUBLE) < "+cutlineIjeong+"						\n");
				}else{
					sql.append("   AND CAST('"+ijung+"' AS DOUBLE) BETWEEN CAST(ijeong AS DOUBLE)-5 	\n");
					sql.append("   AND CAST(ijeong AS DOUBLE)+5															\n");
				}
				sql.append("   group by agcode, agmyeong, nsmyeong, ijeong      						\n");
//				sql.append("   order by nscode,cast(ijeong as decimal)      						\n");
			
			sql.append("   union all      						\n");
			
				sql.append(" SELECT  agcode,   												\n");
				sql.append("        agmyeong,                           							\n");
				sql.append("        nscode,                           							\n");
				sql.append("        nsmyeong,                            							\n");
				sql.append("        ijeong,                           							\n");
				sql.append("        jisaname                           							\n");
				sql.append("   FROM bms_amgeo                            						\n");
				sql.append("  WHERE jisaname='"+jisaname+"'   						\n");
				sql.append("   AND nscode='"+nscode2+"'															\n");
				//순환선 처리
				if(isCircleLine2){
					sql.append("  AND CAST('"+ijung2+"' AS DOUBLE) > CAST(ijeong AS DOUBLE)   						\n");
					sql.append("  OR CAST(ijeong AS DOUBLE) > "+cutlineIjeong2+"						\n");
				}else if(isCircleLine_over2){
					sql.append("  AND CAST('"+ijung2+"' AS DOUBLE) < CAST(ijeong AS DOUBLE)   						\n");
					sql.append("  OR CAST(ijeong AS DOUBLE) < "+cutlineIjeong2+"						\n");
				}else{
					sql.append("   AND CAST('"+ijung2+"' AS DOUBLE) BETWEEN CAST(ijeong AS DOUBLE)-5 	\n");
					sql.append("   AND CAST(ijeong AS DOUBLE)+5															\n");
				}
				sql.append("   group by agcode, agmyeong, nsmyeong, ijeong      						\n");
//				sql.append("   order by nscode,cast(ijeong as decimal)      						\n");

			sql.append("   )where jisaname='"+jisaname+"'      						\n");
			sql.append("    group by agcode, agmyeong, nsmyeong, ijeong      						\n");
			sql.append("   order by nscode,cast(ijeong as decimal)      						\n");
			
			
			
			Log.d("process", "sql START --------------");
			Log.d("process", "sql.toString() : "+sql.toString());
			Log.d("process", "sql END --------------");
			System.out.println("조회 시작  "+System.currentTimeMillis());
			 cursor = getSqlite().rawQuery(sql.toString(), null);
			System.out.println("조회 시작  "+System.currentTimeMillis() +":"+ cursor.getCount());
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	
	/** 암거 부재1 조회
	 * @param tnname
	 * @param jisaname
	 * @return
	 */
	public Cursor selectBms_AgBuJae1(String agname,  String jisaname) {
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" SELECT  bujaecode,   											\n");
			sql.append("        bujae                           							\n");
			sql.append("   FROM bms_amgeo                            					\n");
			sql.append("  WHERE jisaname='"+jisaname+"'   						\n");
			if(!"".equals(Common.nullCheck(agname))){
				sql.append("  AND agmyeong like '%"+ agname +"%'    			\n");
			}
			sql.append("   group by bujaecode, bujae							\n");
			sql.append("   order by bujae      										\n");
			Log.d("process", "sql START --------------");
			Log.d("process", "sql.toString() : "+sql.toString());
			Log.d("process", "sql END --------------");
			
			 cursor = getSqlite().rawQuery(sql.toString(), null);
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	/** 암거 부재2 SPAN 번호조회
	 * @param tnname
	 * @param jisaname
	 * @return
	 */
	public Cursor selectBms_AgBuJae2_SPAN(String agname,  String jisaname) {
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" SELECT  spanbeonho   											\n");
			sql.append("   FROM bms_amgeo                            					\n");
			sql.append("  WHERE jisaname='"+jisaname+"'   						\n");
			if(!"".equals(Common.nullCheck(agname))){
				sql.append("  AND agmyeong like '%"+ agname +"%'    			\n");
			}
			sql.append("   group by spanbeonho											\n");
			sql.append("   order by cast(spanbeonho as integer)					\n");
			Log.d("process", "sql START --------------");
			Log.d("process", "sql.toString() : "+sql.toString());
			Log.d("process", "sql END --------------");
			
			cursor = getSqlite().rawQuery(sql.toString(), null);
			 
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	/** 점검목록 전부 삭제-암거
	 * @param krcode
	 * @param krsbbeonho
	 * @return
	 */
	public Cursor deleteJgHistoryAmGeo() {
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" DELETE FROM BMS_AMGEO_JGINFO WHERE 1=1  \n");
			
			Log.d("process", "sql.toString() : "+sql.toString());
			getSqlite().execSQL(sql.toString());
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	/** 터널 상세 조회
	 * @param krcode
	 * @param krsbbeonho
	 * @return
	 */
	public Cursor selectBms_Amgeo_Detail(String agcode) {
		
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" SELECT  nsmyeong,   									\n");
			sql.append("        ijeong,                           							\n");
			sql.append("        address,                           						\n");
			sql.append("        aggubun,            	               					\n");
			sql.append("        agyuhyeong,  		                         			\n");
			sql.append("        sghtae,    		                       					\n");
			sql.append("        yeonjang,                  								\n");
			sql.append("        nopi	                       								\n");
			sql.append("   FROM bms_amgeo                            			\n");
			sql.append("  WHERE agcode = '"+agcode+"'    					\n");
			sql.append("   group by agcode      									\n");
			
			Log.d("process", "selectBms_Amgeo_Detail sql.toString() : "+sql.toString());
			System.out.println("selectBms_Krgb_Detail 조회 시작  "+System.currentTimeMillis());
			 cursor = getSqlite().rawQuery(sql.toString(), null);
			System.out.println("selectBms_Amgeo_Detail 조회 종료  "+System.currentTimeMillis() +":"+ cursor.getCount());
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	/** 점검 정보 등록-암거
	 * @param krcode
	 * @param krsbbeonho
	 * @return
	 */
	public Cursor insertBms_Amgeo_JGINFO(String agcode, String agname, String bjcode, String bjae1, String bjae2, String bjae3, String sbbeonho, String jgubun, String bonbucode, String jisacode, String swbeonho, String jjilja, String sjfilename, String jkhsahang, String sendyn, String tagyn, String swname) {
		getSqlite().beginTransaction();
		String date = Common.getCalendarDateTime();
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" INSERT INTO BMS_AMGEO_JGINFO 							\n");
			sql.append("   (agcode, agname, bjcode, bjae1, bjae2, bjae3, sbbeonho, jgubun, bonbucode, jisacode, swbeonho, jjilja, sjfilename, jkhsahang, sendyn, tagyn, swname) 	\n");
			sql.append("   values										\n");
			sql.append("   ('"+agcode+"', '"+agname+"', '"+bjcode+"', '"+bjae1+"' , '"+bjae2+"' , '"+bjae3+"', '"+sbbeonho+"', '"+jgubun+"', '"+bonbucode+"', '"+jisacode+"', '"+swbeonho+"', '"+jjilja+"', '"+sjfilename+"', '"+jkhsahang+"', '"+sendyn+"', '"+tagyn+"', '"+swname+"' )	\n");
			
			Log.d("process", "sql.toString() : "+sql.toString());
			getSqlite().execSQL(sql.toString());
			
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	/** 이전 내용 일치 확인_교량
	 *  부재1, 부재2에 따라 설정됨.
	 * @return 0(변경됨), 1(부재2까지 고정), 2(부재3까지 고정)
	 */
	public int checkDataChanged_Amgeo(String agcode, String agname, String bjcode, String bjae1, String bjae2, String bjae3, String sbbeonho, String jgubun, String bonbucode, String jisacode, String swbeonho, String jjilja, String sjfilename, String jkhsahang, String sendyn, String tagyn) {
		
		int rntInt = 0;
		getSqlite().beginTransaction();
		String date = Common.getCalendarDateTime();
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT  							\n");
			sql.append("   agcode, agname, bjcode, bjae1, bjae2, bjae3, sbbeonho, jgubun, bonbucode, jisacode, swbeonho, jjilja, sjfilename, jkhsahang, sendyn, tagyn 	\n");
			sql.append(" FROM  BMS_KRGB_JGINFO		\n");
			sql.append(" ORDER BY jjilja DESC \n");
			sql.append(" limit 1 \n");
			
			
			Log.d("process", "sql.toString() : "+sql.toString());
			cursor = getSqlite().rawQuery(sql.toString(), null);
			String agcode_pre = "";
			String agname_pre = "";
			String bjcode_pre = "";
			String bjae1_pre = "";
			String bjae2_pre = "";
			String bjae3_pre = "";
			String bigo_pre = "";
			String content_pre = "";
			
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				agcode_pre = cursor.getString(0);
				agname_pre = cursor.getString(1);
				bjae1_pre = cursor.getString(3);
				bjae2_pre = cursor.getString(4);
				bjae3_pre = cursor.getString(5);
			}
			
			Log.d("","checkDataChanged bjae2 = " + bjae2_pre +":"+bjae2);
			if(agcode.equals(agcode_pre) ){
				if( bjae1.equals(bjae1_pre) ){
					if( bjae2.equals(bjae2_pre) ){
						rntInt = 1;
						if( bjae3.equals(bjae3_pre) ){
							rntInt = 2;	
						}
					}
				}
			}
			
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		return rntInt;
	}
	
	
	/** 점검목록 조회_암거.
	 * @param krcode
	 * @param krsbbeonho
	 * @return
	 */
	public Cursor selectBms_Amgeo_JGINFO() {
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();
//												0           1            2            3             4               5            6              7            8          9                10       11        12       13        14     15         16
			sql.append(" SELECT  agcode, agname, bjcode, sbbeonho, jgubun, bonbucode, jisacode, swbeonho, jjilja, sjfilename, jkhsahang, tagyn, sendyn, bjae1, bjae2, bjae3, swname \n");
			sql.append("   FROM BMS_AMGEO_JGINFO                            		\n");
			sql.append("   ORDER BY JJILJA  desc                          				\n");
			Log.d("process", "sql.toString() : "+sql.toString());
			cursor = getSqlite().rawQuery(sql.toString(), null);
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	/** 점검목록 조회-미전송-암거
	 * @param krcode
	 * @param krsbbeonho
	 * @return
	 */
	public Cursor selectBms_Amgeo_JGINFO_ONE() {
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();
//			                                     0           1             2      3         4          5          6           7              8                9           10           11       12            13            14       15          16
			sql.append(" SELECT  agcode, agname, bjcode, bjae1, bjae2, bjae3, sbbeonho, jgubun, bonbucode, jisacode, swbeonho, jjilja, sjfilename, jkhsahang, sendyn, tagyn, swname \n");
			sql.append("   FROM BMS_AMGEO_JGINFO                            		\n");
			sql.append("   WHERE sendyn='N'			                            		\n");
			sql.append("   ORDER BY JJILJA  desc                          				\n");
			Log.d("process", "sql.toString() : "+sql.toString());
			cursor = getSqlite().rawQuery(sql.toString(), null);
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	/** 점검목록 전송여부 수정-암거
	 * @param krcode
	 * @param krsbbeonho
	 * @return
	 */
	public Cursor updateJgHistorySendYn_Amgeo(String filename) {
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("UPDATE BMS_Amgeo_JGINFO SET sendyn = 'Y' where SJFILENAME='"+filename+"'");
			
			Log.d("process", "sql.toString() : "+sql.toString());
			getSqlite().execSQL(sql.toString());
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}	
	
	/** 점검목록 선택 삭제-암거
	 * @param krcode
	 * @param krsbbeonho
	 * @return
	 */
	public Cursor deleteJgHistoryOne_Amgeo(String date) {
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" DELETE FROM BMS_AMGEO_JGINFO WHERE jjilja ='"+date+"'  \n");
			
			Log.d("process", "sql.toString() : "+sql.toString());
			getSqlite().execSQL(sql.toString());
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		return cursor;
	}
	
	
	
}
