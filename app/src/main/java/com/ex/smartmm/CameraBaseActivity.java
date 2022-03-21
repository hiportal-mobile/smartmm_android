package com.ex.smartmm;

import com.ex.smartmm.common.Common;
import com.ex.smartmm.common.Configuration;
import com.ex.smartmm.common.DBAdapter;
import com.ex.smartmm.common.DBAdapter_checklist;
import com.ex.smartmm.common.DBAdapter_jangbi;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class CameraBaseActivity extends Activity{
	public static final String TAG = "CameraBaseActivity";
	DBAdapter db;
	DBAdapter_jangbi jangbiDb;
	DBAdapter_checklist checkDb;
	
	/** 사용자 이름 만들기
	 * @param context
	 * @return
	 */
	public String getUserNm(Context context){
		String user_nm = Common.getPrefString(context, Configuration.SHARED_USERNM);
		Log.d(TAG, "getUserNm() - user_nm : "+user_nm);
		if("".equals(user_nm)){
			Log.d(TAG, "getUserNm() - jisacode : "+Common.getPrefString(context, Configuration.SHARED_JISACODE));
			user_nm = jangbiDb.selectBms_BSINFO_JISANAME(Common.getPrefString(context, Configuration.SHARED_JISACODE));
			user_nm = user_nm+" 안전순찰";
			Log.d(TAG, "getUserNm() - user_nm2 : "+user_nm);
		}
		return user_nm;
	}
	
}
