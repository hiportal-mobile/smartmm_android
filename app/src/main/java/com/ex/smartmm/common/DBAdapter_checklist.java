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
 * @author eunjy
 * 2018-08-16
 */
public class DBAdapter_checklist {
	
	private String TAG = "DBAdapter_checklist";
	private static SQLiteDatabase sqliteDatabase;
	private final String package_name = "com.ex.smartmm";
	final String dbfile = "/data/data/"+package_name+"/databases/smartmm_checklist.db";
	/**
	 * 생성자 
	 */
	public DBAdapter_checklist() {
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

	//DBAdapter_checklist - TEST
	public Cursor checkList(){
		Log.d(TAG, "checkList()~!");
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		
		try{
			StringBuffer sql = new StringBuffer();
			//sql.append(" SELECT * 						\n");
			sql.append(" SELECT SEQ, NAME				\n");
			sql.append(" FROM CHECKLIST        			\n");
			
			Log.d(TAG, "sql.toString() : "+sql.toString());
			cursor = getSqlite().rawQuery(sql.toString(), null);
		}catch (SQLException e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally {
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		return cursor;
	}
	
	
	public Cursor selectSmart_DATAINFO_Seq(){
		
		Log.d(TAG, "selectSmart_DATAINFO_Seq()~!");
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		
		try{
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT *				\n");
			sql.append(" FROM SMART_DATAINFO   	\n");
			Log.d(TAG, "sql.toString() : "+sql.toString());
			cursor = getSqlite().rawQuery(sql.toString(), null);
			
		}catch (Exception e) {
			Log.e("selectData()Error! : ", e.toString());
		}finally {
			getSqlite().setTransactionSuccessful();
			getSqlite().endTransaction();
		}
		return cursor;
	}
	
	
	/** 점검 정보 등록 **/
	public Cursor insertSmart_DATAINFO( String jeongbi, String item, String detail,	String content, String filename,
									  	String jbcode, String jbmyeong, String drno, String name, 
									  	String dogong, String dataDate, String swbeonho, String bonbucode, String jisacode, 
									  	String tagyn, String swname, String checkDate){
	
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
		Log.d(TAG, "insertSmart_DATAINFO() - dataDate : "+dataDate);
		Log.d(TAG, "insertSmart_DATAINFO() - swbeonho : "+swbeonho);
		Log.d(TAG, "insertSmart_DATAINFO() - bonbucode : "+bonbucode);
		Log.d(TAG, "insertSmart_DATAINFO() - jisacode : "+jisacode);
		Log.d(TAG, "insertSmart_DATAINFO() - tagyn : "+tagyn);
		Log.d(TAG, "insertSmart_DATAINFO() - swname : "+swname);
		Log.d(TAG, "insertSmart_DATAINFO() - checkDate : "+checkDate);
		getSqlite().beginTransaction();
		//String date = Common.getCalendarDateTime();
		
		String currentDate = Common.getCalendarDateYMDHMS();
		if(checkDate != null && checkDate.equals("")){
			Log.d(TAG, "checkDate is NULL");
			checkDate = currentDate;
		}else{
			Log.d(TAG, "checkDate is NOT NULL");
		}
		Log.d(TAG, "insertSmart_DATAINFO() - checkDate2 : "+checkDate);
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();
		
			sql.append(" INSERT INTO SMART_DATAINFO 																\n");
			sql.append("   (JeongbiGB, ItemGB, DetailGB, CONTENT, FILENAME, JBCODE, JBMYEONG, DRNO, NAME, DOGONG, "
							+ "DATE, SENDYN, SWBEONHO, BONBUCODE, JISACODE, TAGYN, SWNAME, CHECKDATE) 				\n");
			sql.append(" values										\n");
			sql.append("   ('"+jeongbi+"', '"+item+"', '"+detail+"', '"+content+"', '"+filename+"', '"
							  +jbcode+"', '"+jbmyeong+"', '"+drno+"', '"+name+"', '"
							  +dogong+"', '"+dataDate+"', 'N', '"
							  +swbeonho+"', '"+bonbucode+"', '"+jisacode+"', '"+tagyn+"', '"+swname+"', '"+checkDate+"')		\n");
			
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
	
	
	/** 카메라에서 점검 조회 **/
	public Cursor selectSmart_DATAINFO_forCamera(String checkDate) {
		Log.d(TAG, "selectSmart_DATAINFO_forCamera~!");
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT  * 						\n");
			sql.append(" FROM SMART_DATAINFO        	\n");
			
			sql.append(" WHERE CHECKDATE = '"+checkDate+"'       \n"); 
			
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

	
	
	/** 점검 조회 **/
	public Cursor selectSmart_DATAINFO() {
		Log.d(TAG, "selectSmart_DATAINFO~!");
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT  * 						\n");
			sql.append(" FROM SMART_DATAINFO        	\n");
			//sql.append(" GROUP BY         	\n");
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
	
	
	public Cursor selectSmart_DATAINFO_2() {
		Log.d(TAG, "selectSmart_DATAINFO~!");
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT  *, COUNT(*), MIN(DATE) 		\n");
			sql.append(" FROM SMART_DATAINFO        			\n");
			sql.append(" GROUP BY CHECKDATE       			 	\n");
			sql.append(" ORDER BY CHECKDATE  desc      			\n"); //나중에 등록된 데이터가 위로가게 정렬
			
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
	
	
	public Cursor selectSmart_DATAINFO_Image(String checkdate){
		Log.d(TAG, "selectSmart_DATAINFO_Image()~!");
		Log.d(TAG, "selectSmart_DATAINFO_Image() - checkdate : "+checkdate);
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT  FILENAME, DATE, CHECKDATE				\n");
			sql.append(" FROM SMART_DATAINFO        	\n");
			sql.append(" WHERE CHECKDATE = '"+checkdate+"'         	\n");
			sql.append(" ORDER BY DATE  	       		\n"); //나중에 등록된 데이터가 위로가게 정렬
			
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
	
	/** CheckListActivity2 **/
	/** 점검 목록 조회-미전송 **/
	public Cursor selectSmart_DATAINFO_ONE_2() {
		Log.d(TAG, "selectSmart_DATAINFO_ONE()~!");
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT *							\n");
			sql.append(" FROM SMART_DATAINFO                \n");
			sql.append(" WHERE SENDYN='N'			        \n");
			//sql.append(" GROUP BY CHECKDATE 		        \n");
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
	
	/** CheckListActivity2 **/
	/** 점검 목록 조회-미전송 **/
	public Cursor selectSmart_DATAINFO_SENDITEMS(String checkdate) {
		Log.d(TAG, "SENDITEMS()~!");
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT *							\n");
			sql.append(" FROM SMART_DATAINFO                \n");
			sql.append(" WHERE SENDYN='N'			        \n");
			//sql.append(" GROUP BY CHECKDATE 		        \n");
			sql.append(" AND CHECKDATE='"+checkdate+"'	    \n");
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

	
	/** 점검목록 전송여부 수정 **/
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
	
	
	/** 점검목록 선택 삭제 **/
	public Cursor deleteJBHistoryOne(String checkdate) {
		Log.d(TAG, "deleteJBHistoryOne()~!");
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" DELETE FROM SMART_DATAINFO WHERE checkdate ='"+checkdate+"'  \n");
			
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
	
	
	/** 점검목록 전부 삭제  **/
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
	}
	
	public Cursor deleteSmart_DATAINFO_ONE(String imgPath){
		Log.d(TAG, "deleteSmart_DATAINFO_ONE()~!");
		getSqlite().beginTransaction();
		
		Cursor cursor = null;
		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" DELETE FROM SMART_DATAINFO 	  \n");
			sql.append(" WHERE FILENAME = '"+imgPath+"'  \n");
			
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
	
	
	
	


	// ****************************************** Smart MM ********************************************************************************************//
	
	
}
