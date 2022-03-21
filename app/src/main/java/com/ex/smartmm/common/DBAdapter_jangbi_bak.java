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
public class DBAdapter_jangbi_bak {
	
	private String TAG = "DBAdapter_jangbi";
	private static SQLiteDatabase sqliteDatabase;
	private final String package_name = "com.ex.smartmm";
	final String dbfile = "/data/data/"+package_name+"/databases/smartmm.db";
	/**
	 * 생성자 
	 */
	public DBAdapter_jangbi_bak() {
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
			sql.append(" FROM T_EXMB_EQPM01M1 													\n");
			sql.append(" WHERE EQPM_CD='50000001' 												\n");
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
			//sql.append(" and jangbimyeong like '%순찰%'		   									\n");
			if(!"".equals(dogongcode) && !"".equals(jangbimyeong)){
				sql.append(" AND ( DOGONGCODE like '%"+ dogongcode +"%' OR JANGBIMYEONG like '%"+ jangbimyeong +"%') 	\n");
			}else if(!"".equals(dogongcode)){
				sql.append(" AND DOGONGCODE like '%"+ dogongcode +"%'                           \n");
			}else if(!"".equals(jangbimyeong)){
				sql.append(" AND JANGBIMYEONG like '%"+ jangbimyeong +"%'                       \n");
			}
			sql.append(" ORDER BY DOGONGSORT    											    \n");
			
			Log.d(TAG, "sql.toString() : "+sql.toString());
			cursor = getSqlite().rawQuery(sql.toString(), null);
			Log.d(TAG, "cursor : "+cursor.toString());
			
		} catch (SQLException e) {
			Log.d("process", "exception");
			Log.e("selectData()Error! : ", e.toString());
			
		}finally{
			Log.d("process", "finish");
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		
		Log.d(TAG, "select_jeongbi()- cursor : "+cursor.getCount());
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
			
			Log.d(TAG, "sql.toString() : "+insertSql1.toString());
			getSqlite().execSQL(insertSql1.toString());
			
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		return cursor;
	}

	
	/** 정비관리 데이터 상세 조회 **/ 
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
			
			Log.d(TAG, "sql.toString() : "+sql.toString());
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
		Log.d(TAG," select_jeongbiGB()");
		
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
			
			Log.d(TAG, "sql.toString() : "+sql.toString());
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

	
	/** 수리항목 구하기 **/ 
	public Cursor select_itemGB(String suliitem) {
		Log.d(TAG, "select_itemGB()~!");
		
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
			
			Log.d(TAG, "sql.toString() : "+sql.toString());
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
	
	
	/** 세부항목 구하기 **/  
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
			
			Log.d(TAG, "sql.toString() : "+sql.toString());
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

			Log.d(TAG, "sql.toString() : "+sql.toString());
			cursor = getSqlite().rawQuery(sql.toString(), null);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				Log.d(TAG,"getDataMngDate() - cursor.getCount() : "+cursor.getCount());
				lastUptDate = cursor.getString(0);
				if(lastUptDate.equals("") || lastUptDate.equals(null) ){
					Log.d(TAG, "getDataMngDate() - lastUptDate is null");
					lastUptDate = "2017-10-27 10:58:00";
				}
				
				Log.d(TAG, "getDataMngDate() - lastUptDate : "+lastUptDate);
			}

		} catch (SQLException  e) {
			Log.d("process", "getDataMngDate exception ");
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			Log.d("process", "getDataMngDate finish ");
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();

		}
		Log.d(TAG, "getDataMngDate() - lastUptDate2 : "+lastUptDate);
		return lastUptDate;
	}
	
		
	/** 내부 db 가 업데이트 되면 최근업데이트 날짜를 바꿔준다 **/ 
	public String setDataMngUpdate(String date){
		Log.d(TAG, "setDataMngUpdate()~!");
		
		getSqlite().beginTransaction();
		
		String sql = "update dataMng set lastUpdateDate = '"+date+"' where pk_num = '1'";
		try {
			Log.d(TAG, "sql.toString() : "+sql.toString());
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

				Log.d("",TAG+" uptJeongbi() - items.get(" + i + ") : "+items.get(i));

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

				Log.d(TAG, "sql.toString() : "+deleteSql1.toString());
				getSqlite().execSQL(deleteSql1.toString());

				if(flag.equals("Y")){
					Log.d(TAG, "sql.toString() : "+insertSql1.toString());
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
			
			Log.d(TAG, "sql.toString() : "+sql.toString());
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
			    
			Log.d(TAG, "sql.toString() : "+sql.toString());
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
	/** 지사이름 조회 **/
	public String selectBms_BSINFO_JISANAME(String jisacode) {
		Log.d(TAG, "selectBms_BSINFO_JISANAME()~!");
		Log.d(TAG, "selectBms_BSINFO_JISANAME() - jisacode : "+jisacode);
		getSqlite().beginTransaction();
		
		String jisaname = "";
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" SELECT  jisacode,   												\n");
			sql.append("        jisaname                           							\n");
			sql.append("   FROM bms_bsinfo                            						\n");
			sql.append("  WHERE jisacode = '"+jisacode+"'    						\n");
			
			Log.d(TAG, "sql.toString() : "+sql.toString());
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
	
	
	
	
	
	/********************************************************************************/
	
	
	 //checkDataChanged_Jeongbi@@
	/*checkDataChanged_Kyoryang*/
/*	public int checkDataChanged_Jeongbi(String jeongbi, String item, String detail,	String content, String filename,
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
			sql.append(" SELECT  														\n");
			sql.append(" 	JeongbiGB, ItemGB, DetailGB, CONTENT, FILENAME, "
							+ "JBCODE, JBMYEONG, DRNO, NAME, DOGONG, DATE, SENDYN, "
							+ "SWBEONHO, BONBUCODE, JISACODE, SWNAME, CHECKDATE			\n");
			sql.append(" SELECT * 														\n");
			sql.append(" FROM  SMART_DATAINFO											\n");
			sql.append(" ORDER BY DATE DESC 											\n");
			sql.append(" limit 1 														\n");
			
			Log.d(TAG, "sql.toString() : "+sql.toString());
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
				Log.d(TAG, "checkDataChanged_Jeongbi() - ["+i+"]"+cursor.getColumnName(i)+" : "+cursor.getString(i));
				
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
	
	*//** 점검 정보 등록 **//*
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
			
			Log.d(TAG, "sql.toString() : "+sql.toString());
			getSqlite().execSQL(sql.toString());
			
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		return cursor;
	}

	
	*//** 점검 조회 **//*
	public Cursor selectSmart_DATAINFO() {
		Log.d(TAG, "selectSmart_DATAINFO~!");
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT  * 						\n");
			sql.append(" FROM SMART_DATAINFO        	\n");
			sql.append(" ORDER BY CHECKDATE  desc       \n"); //나중에 등록된 데이터가 위로가게 정렬
			
			Log.d(TAG, "sql.toString() : "+sql.toString());
			cursor = getSqlite().rawQuery(sql.toString(), null);
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		return cursor;
	}

	
	*//** 점검 목록 조회-미전송 **//*
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
			Log.d(TAG, "sql.toString() : "+sql.toString());
			cursor = getSqlite().rawQuery(sql.toString(), null);
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		return cursor;
	}

	
	*//** 점검목록 전송여부 수정 **//*
	public Cursor updateJBHistorySendYn(String filename) {
		Log.d(TAG, "updateJBHistorySendYn()~!");
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" UPDATE SMART_DATAINFO SET sendyn = 'Y' where filename='"+filename+"'" );
			
			Log.d(TAG, "sql.toString() : "+sql.toString());
			getSqlite().execSQL(sql.toString());
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		return cursor;
	}	
	
	
	*//** 점검목록 선택 삭제 **//*
	public Cursor deleteJBHistoryOne(String date) {
		Log.d(TAG, "deleteJBHistoryOne()~!");
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" DELETE FROM SMART_DATAINFO WHERE date ='"+date+"'  \n");
			
			Log.d(TAG, "sql.toString() : "+sql.toString());
			getSqlite().execSQL(sql.toString());
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		return cursor;
	}
	
	
	*//** 점검목록 전부 삭제  **//*
	public Cursor deleteJBHistory() {
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" DELETE FROM SMART_DATAINFO WHERE 1=1  \n");
			
			Log.d(TAG, "sql.toString() : "+sql.toString());
			getSqlite().execSQL(sql.toString());
		} catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally{
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		return cursor;
	}*/
	
	
	// ****************************************** Smart MM (KyoRyangActivity)*********************************************************************//
	
	 //checkDataChanged_Jeongbi@@
		/*checkDataChanged_Kyoryang*/
/*		public int checkDataChanged_Jeongbi(String jeongbi, String item, String detail,	String content, String filename,
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
				
				Log.d(TAG, "sql.toString() : "+sql.toString());
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
		
		
		*//** 점검 정보 등록 **//*
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
				
				Log.d(TAG, "sql.toString() : "+sql.toString());
				getSqlite().execSQL(sql.toString());
				
			} catch (SQLException e) {
				Log.e("selectData()Error! : ", e.toString());
			}finally{
				getSqlite().setTransactionSuccessful();
				getSqlite().endTransaction();
			}
			return cursor;
		}
*/
	// ****************************************** Smart MM ********************************************************************************************//
	
	
	
}
