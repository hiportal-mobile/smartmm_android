package com.ex.smartmm.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Bitmap.CompressFormat;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.widget.Toast;

/** 파일, SharePreference, String 관련
 * @author JSJ
 *
 */
@SuppressLint("NewApi")
public class Common {
	public static final String TAG = "Common";
	public final static String					PREF_smartmm  				= "com.ex.smartmm";
	
	public	static String FILE_DIR	=  Configuration.dirRoot_kr;
	public static String FILE_TAG = "";
    //2016-08-31 db파일 버전 관리
    public final String DB_VERSION = "2.0.5"; // 사용안함 (2018.08.24)
    //2018-08-17 db파일 버전 관리
    //jangbi DB 와 checklist DB 분리
  	public final String JANGBI_DB_VERSION = "0.1.4";
  	public final String CHECKLIST_DB_VERSION = "0.0.0";
    static SharedPreferences pref ;//DB용
    
    Editor ed;

    public final String UPPER_BANGHYANG = "구리";
	public final String MIDDLE_BANGHYANG = "일산";
	public final String DOWN_BANGHYANG = "판교";
    Context context;

    // kbr 2022.04.08
	DBAdapter_checklist db;
    
	public Common(){
		Log.d(TAG, "******************** Common()~! 1 ********************");
    }

    public Common(Context context){
    	Log.d(TAG, "******************** Common()~! 2 ********************");
        this.context = context;
    }
    
    
    public void copyFile(String fileName){
		Log.d(TAG, "******************** copyFile()~! ********************");
		Log.d(TAG, "copyFile() - fileName : "+fileName);

		// smartmm_checklist.mp4 -> smartmm_checklist.db
		if(fileName.contains("checklist")){
			Log.d(TAG, "copyFile() - smartmm_checklist.db **********");
			File file = new File("/data/data/"+PREF_smartmm+"/databases/smartmm_checklist.db");
			pref = context.getSharedPreferences(""+PREF_smartmm+"", context.MODE_PRIVATE);
	        ed = pref.edit();
	        
	        Log.d(TAG,"copyFile() - @@@@@ CHECKLIST_DB_VERSION : "+CHECKLIST_DB_VERSION);
	        Log.d(TAG,"copyFile() - @@@@@ pref CHECKLIST_DB_VERSION : "+pref.getString("CHECKLIST_DB_VERSION", ""));
	        
			if (!file.exists()){
				Log.d(TAG, "copyFile() - smartmm_checklist.db >> no exist");
				Log.d(TAG, "copyFile() - file : "+file.toString());
				InputStream is = null;
				FileOutputStream fos = null;
				File outDir = new File("/data/data/"+PREF_smartmm+"/databases/");
				outDir.mkdirs();
					 
				try {
					//is = context.getResources().getAssets().open("smartmm_checklist.mp4");
					is = context.getResources().getAssets().open(fileName);
					int size = 2048;
					byte[] buffer = new byte[size];
					File outfile = new File(outDir + "/" + "smartmm_checklist.db");
					fos = new FileOutputStream(outfile);
					
					for (int c = is.read(buffer); c != -1; c = is.read(buffer)){
						fos.write(buffer, 0, c);
					}
					
					is.close();
					fos.close();
					Log.i("","copyFile() - file copy complete");
				} catch (IOException e) {
					//TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					//2014-01-13
					Log.d(TAG, "copyFile() - CHECKLIST_DB_VERSION : "+CHECKLIST_DB_VERSION);
					ed.putString("CHECKLIST_DB_VERSION", CHECKLIST_DB_VERSION);
					ed.commit();
				}
			}else{
				//이미 존재함
				Log.d(TAG, "copyFile() - smartmm_checklist.db >> already exist");
	        }
			
		// smartmm.mp4 -> smartmm.db
		}else{
			Log.d(TAG, "copyFile() - smartmm.db **********");
	    	File file = new File("/data/data/"+PREF_smartmm+"/databases/smartmm.db");
			pref = context.getSharedPreferences(""+PREF_smartmm+"", context.MODE_PRIVATE);
	        ed = pref.edit();
	        
	        Log.d(TAG,"copyFile() - @@@@@ JANGBI_DB_VERSION : "+JANGBI_DB_VERSION);
	        Log.d(TAG,"copyFile() - @@@@@ pref JANGBI_DB_VERSION : "+pref.getString("JANGBI_DB_VERSION", ""));
	        
	        //DBFile이 존재하지 않을때
	        if (!file.exists()){
				Log.d(TAG, "copyFile() - smartmm.db >> no exist");
				Log.d(TAG, "copyFile() - file : "+file.toString());
				InputStream is = null;
				FileOutputStream fos = null;
				File outDir = new File("/data/data/"+PREF_smartmm+"/databases/");
				outDir.mkdirs();
					 
				try {
					//is = context.getResources().getAssets().open("smartmm.mp4");
					is = context.getResources().getAssets().open(fileName);
					int size = 2048;
					byte[] buffer = new byte[size];
					File outfile = new File(outDir + "/" + "smartmm.db");
					fos = new FileOutputStream(outfile);
					
					for (int c = is.read(buffer); c != -1; c = is.read(buffer)){
						fos.write(buffer, 0, c);
					}
					
					is.close();
					fos.close();
					Log.d(TAG,"copyFile() - file copy complete");
				} catch (IOException e) {
					//TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					//2014-01-13
					Log.d(TAG, "copyFile() - JANGBI_DB_VERSION : "+JANGBI_DB_VERSION);
					ed.putString("JANGBI_DB_VERSION", JANGBI_DB_VERSION);
					ed.commit();
				}
				
			//DBFile 이미 존재할 때
	        }else{
				//이미 존재함
				Log.d(TAG, "copyFile() - smartmm.db >> already exist");
				Log.d(TAG,"copyFile() - DbFile exist update status check start ##########################");
				Log.d(TAG, "copyFile() - pref.getString : "+pref.getString("JANGBI_DB_VERSION", ""));

				boolean isUpdated = false;
				
				if(pref.contains("JANGBI_DB_VERSION") && pref.getString("JANGBI_DB_VERSION", "").equals(JANGBI_DB_VERSION)){
					isUpdated = false;
					Log.d(TAG,"copyFile() - DBFILE not update");	
					Log.d(TAG,"copyFile() - DBFILE not update > pref.getString : " + pref.getString("JANGBI_DB_VERSION", ""));
				}else{
					isUpdated = true;
					Log.d(TAG,"copyFile() - DBFILE update");
					Log.d(TAG,"copyFile() - DBFILE update > pref.getString : " + pref.getString("JANGBI_DB_VERSION", ""));
				}
				Log.d(TAG, "copyFile() - isUpdated : "+isUpdated);

				//DBFile 업데이트 시키기
				if(isUpdated){
					File uptDbFile  = new File("/data/data/"+PREF_smartmm+"/databases/smartmm.db");
					
					//파일삭제후 Asset 폴더의 DB-> database 에 밀어넣기
					uptDbFile.delete();
					InputStream is = null;
					FileOutputStream fos = null;
					File outDir = new File("/data/data/"+PREF_smartmm+"/databases/");
					outDir.mkdirs();
					
					try {
						//is = context.getResources().getAssets().open("smartmm.mp4");
						is = context.getResources().getAssets().open(fileName);
						int size = is.available();
						byte[] buffer = new byte[size];
						File outfile = new File(outDir + "/" + "smartmm.db");
						//File outfile = new File(DB_FILE);
						fos = new FileOutputStream(outfile);
						
						for (int c = is.read(buffer); c != -1; c = is.read(buffer)){
							fos.write(buffer, 0, c);
						}
						
						is.close();
						fos.close();
					} catch (IOException e) {
						//TODO Auto-generated catch block
						e.printStackTrace();
					}finally{
						ed.putString("JANGBI_DB_VERSION", JANGBI_DB_VERSION);
						ed.commit();
					}
					ed.putString("JANGBI_DB_VERSION", JANGBI_DB_VERSION);
					ed.commit();	
	            }
	        }
		}
    }

    
    /*
	public void copyFile(String fileName){
		Log.d(TAG, "******************** copyFile()~! ********************");
		Log.d(TAG, "copyFile() - fileName : "+fileName);
    	File file = new File("/data/data/"+PREF_smartmm+"/databases/smartmm.db");
		pref = context.getSharedPreferences(""+PREF_smartmm+"", context.MODE_PRIVATE);
        ed = pref.edit();
        
        Log.d(TAG,"copyFile() - @@@@@ DB_VERSION : "+DB_VERSION);
        Log.d(TAG,"copyFile() - @@@@@ pref DB_VERSION : "+pref.getString("DB_VERSION", ""));
        
		if (!file.exists()){
			Log.d(TAG, "copyFile() - file : "+file.toString());
			InputStream is = null;
			FileOutputStream fos = null;
			File outDir = new File("/data/data/"+PREF_smartmm+"/databases/");
			outDir.mkdirs();
				 
			try {
				//is = context.getResources().getAssets().open("smartmm.mp4");
				is = context.getResources().getAssets().open(fileName);
				Log.d(TAG, "copyFile() - fileName : "+fileName);
				Log.d(TAG, "copyFile() - is : "+is.toString());
//				int size = is.available();
				int size = 2048;
				byte[] buffer = new byte[size];
				File outfile = new File(outDir + "/" + "smartmm.db");
				//File outfile = new File(DB_FILE);
				fos = new FileOutputStream(outfile);
				
				for (int c = is.read(buffer); c != -1; c = is.read(buffer)){
					fos.write(buffer, 0, c);
				}
				
				is.close();
				fos.close();
				Log.i("","copyFile file copy complete");
			} catch (IOException e) {
				//TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				//2014-01-13
				Log.d(TAG, "copyFile() - DB_VERSION : "+DB_VERSION);
				ed.putString("DB_VERSION", DB_VERSION);
				ed.commit();
			}
			//2014-01-13 db파일이 존재할때
		}else{
			Log.i("","copyFile() - DbFile exist update status check start ##########################");
			boolean isUpdated = false;
	        
			if(pref.contains("DB_VERSION") && pref.getString("DB_VERSION", "").equals(DB_VERSION)){
				Log.i(TAG,"copyFile() - #####"+pref.getString("DB_VERSION", ""));
				isUpdated = false;
				Log.i("","DbFile exist update status check DBFILE not update");	
				Log.i("","DbFile exist update status check DBFILE not update" + pref.getString("DB_VERSION", ""));
			}else{
				isUpdated = true;
				Log.i("","DbFile exist update status check DBFILE update");
				Log.i("","DbFile exist update status check DBFILE update" + pref.getString("DB_VERSION", ""));
			}
			Log.d(TAG, "copyFile() - isUpdated : "+isUpdated);
			//*******************
			//isUpdated = true;
			if(isUpdated){
				File uptDbFile  = new File("/data/data/"+PREF_smartmm+"/databases/smartmm.db");
				
				//파일삭제후 Asset 폴더의 DB-> database 에 밀어넣기
				uptDbFile.delete();
				InputStream is = null;
				FileOutputStream fos = null;
				File outDir = new File("/data/data/"+PREF_smartmm+"/databases/");
				outDir.mkdirs();
				
				try {
					//is = context.getResources().getAssets().open("smartmm.mp4");
					is = context.getResources().getAssets().open(fileName);
					Log.d(TAG, "copyFile() - fileName2 : "+fileName);
					Log.d(TAG, "copyFile() - is2 : "+is.toString());
					int size = is.available();
					byte[] buffer = new byte[size];
					File outfile = new File(outDir + "/" + "smartmm.db");
					//File outfile = new File(DB_FILE);
					fos = new FileOutputStream(outfile);
					
					for (int c = is.read(buffer); c != -1; c = is.read(buffer)){
						fos.write(buffer, 0, c);
					}
					
					is.close();
					fos.close();
				} catch (IOException e) {
					//TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					//2014-01-13
					ed.putString("DB_VERSION", DB_VERSION);
					ed.commit();
				}
				
				ed.putString("DB_VERSION", DB_VERSION);
				ed.commit();	
            }
        }
    }
	
*/
	
	//테스트 용도  필요한 로그파일은 권한이 없어 루팅이 필요하다.
	public void copyFileLogs(){
		InputStream is = null;
		FileOutputStream fos = null;
		File[] files = new File("/data/log").listFiles();
		
		File outDir = new File(Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera/EX-BOARD/logs");
		outDir.mkdirs();
		for (int i = 0; i < files.length; i++) {
			Log.d("", "Common copyFileLogs= "+files[i].getName());
			try {
				is = new FileInputStream(files[i]);
				int size = is.available();	
				byte[] buffer = new byte[size];
				File outfile = new File(outDir + "/" + files[i].getName());
				//File outfile = new File(DB_FILE);
				fos = new FileOutputStream(outfile);
				for (int c = is.read(buffer); c != -1; c = is.read(buffer)){
					fos.write(buffer, 0, c);
				}
				is.close();
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/** 파일 복사하기
	 * @param targetFileFullPath 복사 대상 파일 전체 경로
	 * @param destinationFileFullPath 복사될 위치 전체 경로
	 */
	public void copyFile(String targetFileFullPath, String destinationFileFullPath)
    {
		InputStream is = null;
		FileOutputStream fos = null;
		File TargetDir = new File(targetFileFullPath);
		File outDir = new File(destinationFileFullPath);
				 
		try {
			is = new FileInputStream(TargetDir);
			
			int size = 2048;
			byte[] buffer = new byte[size];
			File outfile = new File(destinationFileFullPath);
			
			fos = new FileOutputStream(outfile);
			
			for (int c = is.read(buffer); c != -1; c = is.read(buffer)){
				fos.write(buffer, 0, c);
			}
			
			is.close();
			fos.close();
			Log.i("","copyFile file copy complete");
		} catch (IOException e) {
			//TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
    
	public void makeDirectory(){
		File root = new File(Configuration.dirRoot_kr);
		if(!root.exists()) root.mkdir();
	}
    
    public static final String nullZeroCheck(String nullStr){
    	if (nullStr != null && !nullStr.equals("")) {
			if("null".equals(nullStr)){
				return "";
			}else if("0".equals(nullStr)){
				return "";
			}
			return nullStr;
		}
		return "";
    }
    //String 값 null값 처리.
    public static final String nullCheck(String nullStr) {
		if (nullStr != null && !nullStr.equals("")) {
			if("null".equals(nullStr)){
				return "";
			}
			return nullStr;
		}
		return "";
	}
    public static final String nullCheckTextZero(String nullStr) {
		if (nullStr != null && !nullStr.equals("")) {
			if("null".equals(nullStr)){
				return "0";
			}
			return nullStr;
		}
		return "0";
	}
    //String to Double + null 체크
    public static final double nullCheckDouble(String nullStr) {
    	if (nullStr != null && !nullStr.equals("")) {
    		try {
				
    			return Double.parseDouble(nullStr);
			} catch (Exception e) {
				return 0.0;
			}
    	}
    	return 0.0;
    }
    
    public static final int nullCheckDelayY(String nullStr) {
    	if (nullStr != null && !nullStr.equals("")) {
    		try {
				
    			return Integer.parseInt(nullStr);
			} catch (Exception e) {
				return 4000;
			}
    	}
    	return 4000;
    }
    
    public static final double nullCheckDelayN(String nullStr) {
    	if (nullStr != null && !nullStr.equals("")) {
    		try {
				
    			return Integer.parseInt(nullStr);
			} catch (Exception e) {
				return 60000;
			}
    	}
    	return 60000;
    }
    
    public static final String nullTrim(String nullStr){
    	if (nullStr != null && !nullStr.equals("")) {
			if("null".equals(nullStr)){
				return "";
			}
			return nullStr.replace(" ","");
		}
		return "";
    }
    
    
    
  //---------------------------------------------------------------------4.SharedPreference---------------------------------------------------------------------	
    public final static String					PREF_smartmm_UTIL  				= "com.ex.smartmm.util";
    private static SharedPreferences mPref ;//UTIL용
    
  		/**SharedPreference 생성
  		 * @param context
  		 */
  		public static void createPreferences(Context context) {
  			if(mPref == null) {
  				
  				File f = new File("/data/data/" + context.getPackageName() + "/shared_prefs/");
  				if(f!=null && f.exists()) {
  					File[] ff = f.listFiles();
  				}
  			}
  		}
  		/** SharedPreference 작성
  		 * @param context
  		 * @param id
  		 * @param value
  		 */
  		public static void setPrefString(Context context, String key, String value) {
  			if(mPref == null) {
  				mPref = PreferenceManager.getDefaultSharedPreferences(context);
  			}
  			//Log.d("","User Id Check : common3 =  "+value);
  			Editor editor = mPref.edit();
  			editor.putString(key, value);
  			editor.commit();
  		}
  		
  		/** SharedPreference 값 가져오기
  		 * @param context
  		 * @param key
  		 * @return
  		 */
  		public static String getPrefString(Context context,String key) {
  			if(mPref == null) {
  				mPref = PreferenceManager.getDefaultSharedPreferences(context);
  			}
  			//Log.d("","User Id Check : common1 =  "+mPref);
  			//Log.d("","User Id Check : common2 =  "+mPref.getString(key, ""));
  			return mPref.getString(key, "");
  		}
  		

  		 public static String[] split(String sString, String sDelim) {

  	        if (isEmpty(sString) || sString.indexOf(sDelim) < -1) {
  	            return null;
  	        }
  	        
  	        StringTokenizer stringtokenizer = new StringTokenizer(sString, sDelim);
  	        int iTokenCount = stringtokenizer.countTokens();

  	        if (iTokenCount <= 0) {
  	            return null;
  	        }

  	        String sResults[] = new String[iTokenCount];
  	        int i = 0;

  	        while (stringtokenizer.hasMoreTokens()) {
  	        	sResults[i++] = stringtokenizer.nextToken();
  	        }

  		        return sResults;
  		 }
  		 
  		/**
  		 * 해당 문자열에 값이 있는지 확인한다.
  		 * @param str 문자열
  		 * @return
  		 */
  		public static boolean isEmpty(String sStr) {
  			boolean bResult = false;
  			
  			if (sStr == null || sStr.trim().equals("")) {
  				bResult = true;
  			}
  			
  			return bResult;
  		}
  		
  		/** 하위파일 삭제
  		 * @param path
  		 * @return
  		 */
  		public static boolean DeleteDir(String path) {

  			boolean rtn = false;
  			
  			File file = new File(path);
  			if(file.exists()){
  			
  				File[] childFileList = file.listFiles();
  				
  				for (File childFile : childFileList) {
  					
  					if (childFile.isDirectory()) {
  						DeleteDir(childFile.getAbsolutePath()); // 하위 디렉토리 루프
  					} else {
  						childFile.delete(); // 하위 파일삭제
  						
  					}
  				}
//  				if(file.delete()){ // root 삭제
  					rtn = true;
//  				}
  			}
  			return rtn;
  		}
  		
  		

  		
  		/**사진 - 앨범에서 선택*/
  		public void cameraPicRequestSelect(Activity activity, Intent data){
  			String orgFile	= getFilePath(data.getData(), activity);
  			File temp = new File(orgFile);
  			String tempName = temp.getName();
  			String ext = tempName.substring(tempName.indexOf("."));
  			
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
  			String result = Common.FILE_TAG+decimalFormat.format(year)
  					+ decimalFormat.format(month) + decimalFormat.format(date)
  					+ decimalFormat.format(hour) + decimalFormat.format(minute)
  					+ decimalFormat.format(second) + decimalFormat.format(rnd);
  			tempName = result+ext;
  			Log.d("","cameraPicRequestSelect = 5");
  			fileCopy2(orgFile.toString(), tempName ,"N");
  			fileCopyToGallery(orgFile.toString(), tempName ,"N");
  			
//  			StatFs staFs = new StatFs(System.getenv("SECONDARY_STORAGE"));
//   	        long totalSize = (long)staFs.getBlockSizeLong() * staFs.getBlockCountLong();
//   	        System.out.println("secondary_storage path size = " + totalSize);
//   	        if(totalSize > 1024){
//   	        	fileCopyToSdcard(orgFile.toString(), tempName ,"N");
//   	        }
   	        
  			
  			
  			
  			// MediaStore 미디어 삽입
  			ContentValues newValues = new ContentValues(2);
  			newValues.put(MediaColumns.MIME_TYPE, "image/jpeg");
  			newValues.put(MediaColumns.DATA, FILE_DIR+"/"+FILE_TAG+tempName);
  			ContentResolver contentResolver = activity.getContentResolver();
  			Uri newUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, newValues);
  			activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
  		}
  		
  		/**동영상 - 앨범에서 선택*/
  		public void cameraVicRequestSelect(Activity activity, Intent data){
  			
  			String orgFile	= getFilePath(data.getData(), activity);
  			
  			File temp = new File(orgFile);
  			fileCopy2(orgFile, FILE_TAG+temp.getName(), "N");

  			// MediaStore 미디어 삽입
  			ContentValues newValues = new ContentValues(5);
  			newValues.put(MediaColumns.TITLE, "RecordedVideo");
  			newValues.put(MediaStore.Video.Media.DISPLAY_NAME, FILE_TAG+temp.getName());
  			newValues.put(MediaColumns.DATE_ADDED, System.currentTimeMillis() / 1000);
  			newValues.put(MediaColumns.MIME_TYPE, "video/mp4");
  			newValues.put(MediaColumns.DATA, FILE_DIR+"/"+FILE_TAG+temp.getName());
  			
  			ContentResolver contentResolver = activity.getContentResolver();
  			Uri newUri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, newValues);
  			
  			activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
  		}
  		
  		/**사진 - 촬영*/
  		public void cameraPicRequest(Activity activity, Intent data){
  			
  			String saveFile = data.getExtras().getString("imgName");
  			
//  			File temp = new File(saveFile);
//  			fileCopy2(temp.getName(),temp.getName(),"Y");
  			
  			// MediaStore 미디어 삽입
  			ContentValues newValues = new ContentValues(2);
  			newValues.put(MediaColumns.MIME_TYPE, "image/jpeg");
  			newValues.put(MediaColumns.DATA, "/mnt"+saveFile);
  			
  			ContentResolver contentResolver = activity.getContentResolver();
  			Uri newUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, newValues);
  			
  			//activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
  			
  			activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"+FILE_DIR))); 
  		}
  		
  		/**동영상 - 촬영*/
  		public void cameraVicRequest(Activity activity, Intent data){
  			
  			Image image	= Common.getVideoInfo(data.getData(), activity, "");
  			
  			fileCopy2(""+image.getFilePath(), FILE_TAG+image.getFileName().toString(), "Y");
  			
  			// MediaStore 미디어 삽입
  			ContentValues newValues = new ContentValues(5);
  			newValues.put(MediaColumns.TITLE, "RecordedVideo");
  			newValues.put(MediaStore.Video.Media.DISPLAY_NAME, FILE_TAG+image.getFileName().toString());
  			newValues.put(MediaColumns.DATE_ADDED, System.currentTimeMillis() / 1000);
  			newValues.put(MediaColumns.MIME_TYPE, "video/mp4");
  			newValues.put(MediaColumns.DATA, FILE_DIR+"/"+FILE_TAG+image.getFileName().toString());
  			
  			ContentResolver contentResolver = activity.getContentResolver();
  			Uri newUri = contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, newValues);
  			
  			activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
  		}
  		
  		public  String getFilePath(Uri contentUri, Context context){
  			// can post image
  			String[]  proj = {	MediaStore.Images.Media.DATA,
  								MediaStore.Video.Media.DATA };
  			
  			Cursor cursor = ((Activity) context).managedQuery(contentUri, proj,null,null,null);
  			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
  			cursor.moveToFirst();
  			
  			String fileName = cursor.getString(column_index);
  			//ManagedQuery Close 제외
//  			cursor.close();

  			return fileName;
  		}
  		
  		
  		/** 파일 복사2
  		 * @param realFile(실제 파일명)-경로포함
  		 * @param saveFile(복사될 파일명)
  		 * @param delete(실제파일 삭제여부)
  		 */
  		public void fileCopy2(String realFile, String saveFile, String delete){

  			File file = new File(realFile);
  			File saveFullPath =  new File(FILE_DIR +"/"+saveFile);

  			Log.d("","filecopy2 = "+ realFile);
  			Log.d("","filecopy2 = "+ saveFile);
  			
  			String filePath = FILE_DIR +"/"+saveFile;
  			try {
  				File fileDirectory = new File(filePath.substring(0, filePath.lastIndexOf("/") + 1));
  				if(!fileDirectory.exists()){
  					fileDirectory.mkdirs();
  				}

  				FileInputStream inputStream = new FileInputStream(file);
  				FileOutputStream outputStream = new FileOutputStream(saveFullPath);
  				
  				FileChannel fcin = inputStream.getChannel();
  				FileChannel fcout = outputStream.getChannel();
  				
  				long size = fcin.size();
  				fcin.transferTo(0, size, fcout);
  				
  				fcout.close();
  				fcin.close();
  				outputStream.close();
  				inputStream.close();
  				
  				if(delete=="Y"){
  					file.delete();
  				}
  				
  			} catch (Exception e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  				Log.e("","filecopy2 = "+e.toString());
  			}
  		}
  		
  		/** 파일 복사 촬영된 사진을 갤러리로 저장.
  		 * @param realFile(실제 파일명)-경로포함
  		 * @param saveFile(복사될 파일명)
  		 * @param delete(실제파일 삭제여부)
  		 */
  		public void fileCopyToGallery(String realFile, String saveFile, String delete){
  			
  			File file = new File(realFile);
  			File saveFullPath =  new File(FILE_DIR+"/"+saveFile);
  			
  			Log.d("","fileCopyToGallery = "+ realFile);
  			Log.d("","fileCopyToGallery = "+ saveFile);
  			
  			String filePath = FILE_DIR+"/"+saveFile;
  			try {
  				File fileDirectory = new File(filePath.substring(0, filePath.lastIndexOf("/") + 1));
  				if(!fileDirectory.exists()){
  					fileDirectory.mkdirs();
  				}
  				
  				FileInputStream inputStream = new FileInputStream(file);
  				FileOutputStream outputStream = new FileOutputStream(saveFullPath);
  				
  				FileChannel fcin = inputStream.getChannel();
  				FileChannel fcout = outputStream.getChannel();
  				
  				long size = fcin.size();
  				fcin.transferTo(0, size, fcout);
  				
  				fcout.close();
  				fcin.close();
  				outputStream.close();
  				inputStream.close();
  				
  				if(delete=="Y"){
  					file.delete();
  				}
  				
  			} catch (Exception e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  				Log.e("","fileCopyToGallery = "+e.toString());
  			}
  		}
  		
  		/** 파일 복사 촬영된 사진을 원클릭 폴더로 저장.
  		 * @param realFile(실제 파일명)-경로포함
  		 * @param saveFile(복사될 파일명)
  		 * @param delete(실제파일 삭제여부)
  		 */
  		public void fileCopyToSmartOneShot(String realFile, String saveName, String delete){
  			
  			File file = new File(realFile);
  			File saveFullPath =  new File(FILE_DIR +"/"+saveName);
  			
  			Log.d("","fileCopyToSituation = "+ realFile);
  			Log.d("","fileCopyToSituation = "+ saveName);
  			
  			String filePath = FILE_DIR +"/"+saveName;
  			try {
  				File fileDirectory = new File(filePath.substring(0, filePath.lastIndexOf("/") + 1));
  				if(!fileDirectory.exists()){
  					fileDirectory.mkdirs();
  				}
  				
  				FileInputStream inputStream = new FileInputStream(file);
  				FileOutputStream outputStream = new FileOutputStream(saveFullPath);
  				
  				FileChannel fcin = inputStream.getChannel();
  				FileChannel fcout = outputStream.getChannel();
  				
  				long size = fcin.size();
  				fcin.transferTo(0, size, fcout);
  				
  				fcout.close();
  				fcin.close();
  				outputStream.close();
  				inputStream.close();
  				
  				if(delete=="Y"){
  					file.delete();
  				}
  				
  			} catch (Exception e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  				Log.e("","fileCopyToGallery = "+e.toString());
  			}
  		}
  		
  		
  		//외장 sd카드에 저장.
  		public void fileCopyToSdcard(String realFile, String saveFile, String delete){
  			
  			File file = new File(realFile);
  			File saveFullPath =  new File( System.getenv("SECONDARY_STORAGE") +"/"+saveFile );
  			
  			Log.d("","fileCopyToGallery = "+ realFile);
  			Log.d("","fileCopyToGallery = "+ saveFile);
  			
  			String filePath = FILE_DIR+"/"+saveFile;
  			try {
  				File fileDirectory = new File(filePath.substring(0, filePath.lastIndexOf("/") + 1));
  				if(!fileDirectory.exists()){
  					fileDirectory.mkdirs();
  				}
  				
  				FileInputStream inputStream = new FileInputStream(file);
  				FileOutputStream outputStream = new FileOutputStream(saveFullPath);
  				
  				FileChannel fcin = inputStream.getChannel();
  				FileChannel fcout = outputStream.getChannel();
  				
  				long size = fcin.size();
  				fcin.transferTo(0, size, fcout);
  				
  				fcout.close();
  				fcin.close();
  				outputStream.close();
  				inputStream.close();
  				
  				if(delete=="Y"){
  					file.delete();
  				}
  				
  			} catch (Exception e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  				Log.e("","fileCopyToGallery = "+e.toString());
  			}
  			
  			
  			
  		}
  		
  		public static Image getVideoInfo(Uri uri, Context context, String query) {
  			
  			Image image = null;
  			Cursor thumbCursor = null;
  			Cursor c = null;
  			
  			try{
  			
  				// Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
  				image = new Image();
  		
  				String[] proj = { MediaStore.Video.VideoColumns._ID,
  						MediaStore.Video.VideoColumns.DATA,
  						MediaStore.Video.VideoColumns.DISPLAY_NAME };
  		
  				long videoId = 0;
  				String videoName = "";
  				String videoPath = "";
  		
  				
  		
  				if (query.equals("")) {
  					thumbCursor = ((Activity) context).managedQuery(uri, proj, null, null, null);
  				} else {
  					String selection = MediaStore.Video.VideoColumns.DISPLAY_NAME+ "='" + query + "'";
  					thumbCursor = ((Activity) context).managedQuery(uri, proj, selection, null, null);
  					
  				}
  		
  				if (thumbCursor != null && thumbCursor.moveToFirst()) { // 처음부터
  					// 저장된.
  					int id = thumbCursor
  							.getColumnIndex(MediaStore.Video.VideoColumns._ID);
  					int pathId = thumbCursor
  							.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
  					int nameId = thumbCursor
  							.getColumnIndex(MediaStore.Video.VideoColumns.DISPLAY_NAME);
  		
  					videoId = thumbCursor.getLong(id);
  					videoPath = thumbCursor.getString(pathId);
  					videoName = thumbCursor.getString(nameId);
  		
  					image.setFilePath(videoPath);
  					image.setFileName(videoName);
  		
  				}
  				//thumbCursor.close();
  		
  				if (videoName != "" && videoPath != "") {
  					// TODO
  				}
  		
  				String[] THUMB_PROJECTION = new String[] {MediaStore.Video.Thumbnails._ID,MediaStore.Video.Thumbnails.DATA };
  		
  				Uri thumbUri = MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI;
  				ContentResolver cr = context.getContentResolver();
  				c = cr.query(thumbUri, THUMB_PROJECTION,MediaStore.Video.Thumbnails.VIDEO_ID + "=?",new String[] { String.valueOf(videoId) }, null);

  				BitmapFactory.Options options1 = new BitmapFactory.Options();
  				options1.inSampleSize = 6;
  				
  				Bitmap bmp1 = MediaStore.Video.Thumbnails.getThumbnail(cr, videoId, MediaStore.Images.Thumbnails.MICRO_KIND, options1);
  				
  				Log.d("timer", "bmp1"+bmp1.getHeight());				
  				
  				if (c.moveToNext()) {
  					long id = c.getLong(0);

  					int dataId = c.getColumnIndex(MediaStore.Video.Thumbnails.DATA);
  					String strThumPath = c.getString(dataId);

  					ContentResolver crThumb = context.getContentResolver();
  					BitmapFactory.Options options = new BitmapFactory.Options();
  					options.inSampleSize = 6;
  					Bitmap bmp = MediaStore.Video.Thumbnails.getThumbnail(crThumb,
  							videoId, MediaStore.Video.Thumbnails.MICRO_KIND,
  							options);
  					image.setThumbnail(bmp);
  					
  					//Log.d("KIM", "videoThumId = " + id );
  					//Log.d("KIM", "videoThumPath = " + strThumPath );
  					//Log.d("KIM", "bmp = " + bmp );
  				}
  			} catch (Exception e) {
  				Log.d("KIM", "Exception : "+e.toString());			
  			} finally {
  				if (c != null){
  					c.close();
  				}
//  				if (thumbCursor != null){
//  					thumbCursor.close();
//  				}			
  			}

  			//Log.d("KIM", "Ret image = " + image.toString());
  			return image;
  		}
  		
  		/** 전화번호 가공
		 * @param phoneNum
		 * @return
		 */
		public static final String[] phoneNumCut(String phoneNum) {

			String mPhoneNo = nullCheck(phoneNum);
			String[] phoneNo = new String[3];
			if(!"".equals(mPhoneNo)){
				if (mPhoneNo.startsWith("+")) {

					if (mPhoneNo.length() > 12) {
						phoneNo[0] = "0" + mPhoneNo.substring(3, 5);
						phoneNo[1] = mPhoneNo.substring(5, 9);
						phoneNo[2] = mPhoneNo.substring(9);
					} else {
						phoneNo[0] = "0" + mPhoneNo.substring(3, 5);
						phoneNo[1] = mPhoneNo.substring(5, 8);
						phoneNo[2] = mPhoneNo.substring(8);
					}
				} else {

					if (mPhoneNo.length() > 10) {
						phoneNo[0] = mPhoneNo.substring(0, 3);
						phoneNo[1] = mPhoneNo.substring(3, 7);
						phoneNo[2] = mPhoneNo.substring(7);
					} else {
						phoneNo[0] = mPhoneNo.substring(0, 3);
						phoneNo[1]= mPhoneNo.substring(3, 6);
						phoneNo[2] = mPhoneNo.substring(6);
					}
				}

				return phoneNo;
			}else{
				return phoneNo;
			}
			
		}
		
		public static Double  doubleCutToString(double gpsxy){
			String rtnStr = "0";
			double rtnDouble = 0.0;
			if(gpsxy > 0){
				rtnStr = Double.toString(gpsxy);
				if(rtnStr.length() > 12){
					rtnStr = rtnStr.substring(0, 12);
					rtnDouble = Double.valueOf(rtnStr).doubleValue();
					return rtnDouble;
				}else{
					return gpsxy;		
				}
			}
			return rtnDouble;
		}

		public static String getCalendarDateTime(){
			DecimalFormat decimalFormat = new DecimalFormat("00");// decimalformat
  			DecimalFormat NumFormat = new DecimalFormat("0000");// 4
  			Calendar rightNow = Calendar.getInstance();// 
  			int year = rightNow.get(Calendar.YEAR);
  			int month = rightNow.get(Calendar.MONTH) + 1;
  			int date = rightNow.get(Calendar.DATE);//
  			int hour = rightNow.get(Calendar.HOUR_OF_DAY);
  			int minute = rightNow.get(Calendar.MINUTE);
  			int second = rightNow.get(Calendar.SECOND);
  			String result = NumFormat.format(year)+""+ decimalFormat.format(month)+"" + decimalFormat.format(date)+""+decimalFormat.format(hour)+""+decimalFormat.format(minute)+""+decimalFormat.format(second);
  			return result;
		}
		
		/** 날짜 조회
		 * @return ex 02-19-2018 19:11:53
		 */
		public static String getCalendarDateMDYHMS(){
			DecimalFormat decimalFormat = new DecimalFormat("00");// decimalformat
  			DecimalFormat NumFormat = new DecimalFormat("0000");// 4
  			Calendar rightNow = Calendar.getInstance();// 
  			int year = rightNow.get(Calendar.YEAR);
  			int month = rightNow.get(Calendar.MONTH) + 1;
  			int date = rightNow.get(Calendar.DATE);//
  			int hour = rightNow.get(Calendar.HOUR_OF_DAY);
  			int minute = rightNow.get(Calendar.MINUTE);
  			int second = rightNow.get(Calendar.SECOND);
  			String result = NumFormat.format(month)+"-"+ decimalFormat.format(date)+"" + decimalFormat.format(year)+":"+decimalFormat.format(hour)+":"+decimalFormat.format(minute)+":"+decimalFormat.format(second);
  			return result;
		}
		
		/** 날짜 조회
		 * @return ex 2000년01월01일
		 */
		public static String getCalendarDateYMD(){
			DecimalFormat decimalFormat = new DecimalFormat("00");// decimalformat
  			DecimalFormat NumFormat = new DecimalFormat("0000");// 4
  			Calendar rightNow = Calendar.getInstance();// 
  			int year = rightNow.get(Calendar.YEAR);
  			int month = rightNow.get(Calendar.MONTH) + 1;
  			int date = rightNow.get(Calendar.DATE);//
  			String result = NumFormat.format(year)+"년"+ decimalFormat.format(month)+"월" + decimalFormat.format(date)+"일";
  			return result;
		}
		
		/** 날짜 조회
		 * @return ex 2000.01.01 01:01
		 */
		public static String getCalendarDateYMDHM(){
			DecimalFormat decimalFormat = new DecimalFormat("00");// decimalformat
  			DecimalFormat NumFormat = new DecimalFormat("0000");// 4
  			Calendar rightNow = Calendar.getInstance();// 
  			int year = rightNow.get(Calendar.YEAR);
  			int month = rightNow.get(Calendar.MONTH) + 1;
  			int date = rightNow.get(Calendar.DATE);//
  			int hour = rightNow.get(Calendar.HOUR_OF_DAY);
  			int minute = rightNow.get(Calendar.MINUTE);
  			int second = rightNow.get(Calendar.SECOND);
  			//String result = NumFormat.format(year)+"년"+ decimalFormat.format(month)+"월" + decimalFormat.format(date)+"일 "+decimalFormat.format(hour)+"시"+decimalFormat.format(minute)+"분"+decimalFormat.format(second)+"초";
  			String result = NumFormat.format(year)+"."+ decimalFormat.format(month)+"." + decimalFormat.format(date)+" "+decimalFormat.format(hour)+":"+decimalFormat.format(minute);
  			return result;
		}
		
		/** 날짜 조회
		 * @return ex 2000년01월01일01시01분01초
		 */
		public static String getCalendarDateYMDHMS(){
			DecimalFormat decimalFormat = new DecimalFormat("00");// decimalformat
  			DecimalFormat NumFormat = new DecimalFormat("0000");// 4
  			Calendar rightNow = Calendar.getInstance();// 
  			int year = rightNow.get(Calendar.YEAR);
  			int month = rightNow.get(Calendar.MONTH) + 1;
  			int date = rightNow.get(Calendar.DATE);//
  			int hour = rightNow.get(Calendar.HOUR_OF_DAY);
  			int minute = rightNow.get(Calendar.MINUTE);
  			int second = rightNow.get(Calendar.SECOND);
  			//String result = NumFormat.format(year)+"년"+ decimalFormat.format(month)+"월" + decimalFormat.format(date)+"일 "+decimalFormat.format(hour)+"시"+decimalFormat.format(minute)+"분"+decimalFormat.format(second)+"초";
  			String result = NumFormat.format(year)+"."+ decimalFormat.format(month)+"." + decimalFormat.format(date)+" "+decimalFormat.format(hour)+":"+decimalFormat.format(minute)+":"+decimalFormat.format(second);
  			return result;
		}
		
		/** 앱 버전 코드
		 * @param context
		 * @return
		 */
		public static int getAppVersionCode(Context context) {
			try {
				PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
				return packageInfo.versionCode;
			} catch (NameNotFoundException e) {
				
				throw new RuntimeException("패키지 이름을 알 수 없습니다. " + e);
			}
		}
	
		/** 앱 버전 이름
		 * @param context
		 * @return
		 */
		public static String getAppVersionName(Context context) {
			try {
				PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
				return packageInfo.versionName;
			} catch (NameNotFoundException e) {
				
				throw new RuntimeException("패키지 이름을 알 수 없습니다. " + e);
			}
		}
		
		/**SDCard Refresh file list
		 * @param activity
		 */
		public void refreshSD(Activity activity) {
			activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, 
					Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
//			activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, 
//					Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
		}
		
		public static String getCurrentYMD(){
			DecimalFormat decimalFormat = new DecimalFormat("00");// decimalformat
  			Calendar rightNow = Calendar.getInstance();//
  			int year = rightNow.get(Calendar.YEAR);
  			int month = rightNow.get(Calendar.MONTH)+1;
  			int date = rightNow.get(Calendar.DATE);//
  			String result = decimalFormat.format(year)+"/"+decimalFormat.format(month) +"/"+ decimalFormat.format(date);
			return result;
		}
		public static String getYear(){
			DecimalFormat decimalFormat = new DecimalFormat("00");// decimalformat
			Calendar rightNow = Calendar.getInstance();//
			int year = rightNow.get(Calendar.YEAR);
			return decimalFormat.format(year);
		}
		public static String getMonth(){
			DecimalFormat decimalFormat = new DecimalFormat("00");// decimalformat
			Calendar rightNow = Calendar.getInstance();//
			int month = rightNow.get(Calendar.MONTH)+1;
			return decimalFormat.format(month);
		}
		public static String getDay(){
			DecimalFormat decimalFormat = new DecimalFormat("00");// decimalformat
			Calendar rightNow = Calendar.getInstance();//
			int date = rightNow.get(Calendar.DATE);//
			return decimalFormat.format(date);
		}

		
		public static String getCurrentHMS(){
			DecimalFormat decimalFormat = new DecimalFormat("00");// decimalformat
  			Calendar rightNow = Calendar.getInstance();// 
  			int hour = rightNow.get(Calendar.HOUR_OF_DAY);
  			int minute = rightNow.get(Calendar.MINUTE);
  			int second = rightNow.get(Calendar.SECOND);
  			String result = decimalFormat.format(hour) +":"+ decimalFormat.format(minute);
  			
			return result;
		}
		
		
		
		public Bitmap overlayMark(Context con, Bitmap bmp1, Bitmap bmp2, String savePath){
			int margin = 5;
			if(bmp1.getWidth() < 1000){
				margin = 5;
			}else if(bmp1.getWidth() < 2000){
				margin = 10;
			}else{
				margin = 15;
			}
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());

			Canvas canvas = new Canvas(bmOverlay);
			canvas.drawBitmap(bmp1, 0, 0, null);

			canvas.drawBitmap(bmp2, margin+0,bmp1.getHeight()-bmp2.getHeight()-margin, null);
			canvas.drawBitmap(bmp2, bmp1.getWidth()-bmp2.getWidth()-margin, bmp1.getHeight()-bmp2.getHeight()-margin, null);
			
			try {
				FileOutputStream fos = new FileOutputStream(savePath);
				bmOverlay.compress(CompressFormat.PNG, 100, fos);
				fos.close();
				bmOverlay.recycle();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return bmOverlay;
		}
	public Bitmap readImageWithSampling(String imagePath, int targetWidth, int targetHeight, Bitmap.Config bmConfig) {
		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath, bmOptions);

		int photoWidth  = bmOptions.outWidth;
		int photoHeight = bmOptions.outHeight;

		// Determine how much to scale down the image
		int scaleFactor = Math.min(photoWidth / targetWidth, photoHeight / targetHeight);

		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inPreferredConfig = bmConfig;
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		Bitmap  orgImage = BitmapFactory.decodeFile(imagePath, bmOptions);

		return orgImage;
	}

	public void makeDialog(Context con, String title, String message, String btn){
		AlertDialog.Builder ad = new AlertDialog.Builder(con);
		ad.setMessage("")
				.setTitle(title)
				.setMessage(message)
				.setCancelable(false)
				.setPositiveButton(btn,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						});
		ad.show();
	}

	/**
	 * 위도,경도로 주소취득
	 * @param lat
	 * @param lng
	 * @return 주소
	 */
	public String findAddress(Context context, double lat, double lng) {
		String currentLocationAddress = "";
		StringBuffer bf = new StringBuffer();
		Geocoder geocoder = new Geocoder(context, Locale.KOREA);
		List<Address> address;
		try {
			if (geocoder != null) {
				// 세번째 인수는 최대결과값인데 하나만 리턴받도록 설정했다
				address = geocoder.getFromLocation(lat, lng, 1);
				// 설정한 데이터로 주소가 리턴된 데이터가 있으면
				if (address != null && address.size() > 0) {
					// 주소
					currentLocationAddress = address.get(0).getAddressLine(0).toString();

					// 전송할 주소 데이터 (위도/경도 포함 편집)
					bf.append(currentLocationAddress).append("#");
					bf.append(lat).append("#");
					bf.append(lng);
				}

			}

		} catch (IOException e) {
			Toast.makeText(context, "주소취득 실패", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
		return bf.toString();
	}

	// kbr 2022.03.30

	/**
	 * 앱 폴더 내 DB 파일 체크
	 * true : 유
	 * false : 무
	 * @return
	 */
	public boolean checkOriginFile() {
		File originDir = new File(Configuration.DBFILE_ORIGIN_PATH);
		// 앱 폴더가 존재하지 않을 경우
		if ( !originDir.exists() ) {
			return false;
		}
		// 앱 폴더가 존재할 경우
		File originFile = new File(Configuration.DBFILE_ORIGIN_PATH + "smartmm_checklist.db");

		// DB 파일이 존재하지 않을 경우
		if ( !originFile.exists() ) {
			return false;
		}

		// DB 파일이 존재할 경우
		db = new DBAdapter_checklist();
		Cursor cursor = db.selectSmart_DATAINFO_Seq();

		// 목록이 존재하지 않을 경우
		if (cursor.getCount() == 0) {
			return false;
		}

		// 목록이 존재할 경우
		return true;
	}

	public boolean moveFile(String fileName, String inputPath, String outputPath) {

		Log.d(TAG, " - moveFile() : start!!!!!!!!!!!!!!!!");

		Log.d(TAG, "fileName - " + fileName);
		Log.d(TAG, "inputPath - " + inputPath);
		Log.d(TAG, "outputPath - " + outputPath);

		// backup 파일이 없을 경우 그냥 진행
		if (inputPath.equals(Configuration.DBFILE_BACKUP_PATH)) {
			File backupChk = new File(inputPath + fileName);
			Log.d(TAG, "backupChk - " + backupChk.getPath());
			if (!backupChk.exists()) {
				Log.d(TAG, " - backup file not exist");
				return true;
			}
			Log.d(TAG, " - backup file exist");
		}

		InputStream is = null;
		OutputStream os = null;
		boolean checkResult = false;

		try {
			File dir = new File(outputPath);

			Log.d(TAG, "dir - " + dir.getPath());

			if (!dir.exists()) {
				dir.mkdirs();
			}

			Log.d(TAG, "move file path - " + outputPath + fileName);

			is = new FileInputStream(inputPath + fileName);
			os = new FileOutputStream(outputPath + fileName);

			byte[] buffer = new byte[1024];
			int read;
			while((read = is.read(buffer)) != -1) {
				os.write(buffer, 0, read);
			}
			os.flush();

			is.close();
			os.close();


			// 백업 경로에서 가지고 오는 경우 DB 에 데이터 저장 / 파일 확인
			if (inputPath.equals(Configuration.DBFILE_BACKUP_PATH)) {
				// DB 데이터 저장
				pref = context.getSharedPreferences(""+PREF_smartmm+"", context.MODE_PRIVATE);
				ed = pref.edit();
				Log.d(TAG, "moveFile() - CHECKLIST_DB_VERSION : "+ CHECKLIST_DB_VERSION);
				ed.putString("CHECKLIST_DB_VERSION", CHECKLIST_DB_VERSION);
				ed.commit();
				Log.d(TAG, " - move file complete > origin file check");
				// origin path 에서 백업하는 경우 백업 경로에 파일이 있는지 확인
			} else {
				Log.d(TAG, " - move file complete > backup file check");
			}
			// output 경로에 파일 존재 확인
			checkResult = new File(outputPath + fileName).exists();
			Log.d(TAG, String.valueOf(checkResult));
		} catch (IOException e) {
			Log.d(TAG, "move file path - IOException");
		}

		// 파일 조회를 위해 열었던 DB close
		if (db != null && !db.isClosed()) {
			db.close();
		}

		return checkResult;

	}

}


