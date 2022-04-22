package com.ex.smartmm.common;

import android.os.Environment;


public class Configuration {
	
	//primitive
//	public static String COMMON_SMARTMM_LOGIN = "COMMON_SMARTONESHOT_LOGIN";//사용자 정보 조회.
	public static String COMMON_SMARTMM_LOGIN = "COMMON_EQUIPMENT_USERINFO";//사용자 정보 조회.

	public static String COMMON_EQUIPMENT_CARUPTDATA = "COMMON_EQUIPMENT_CARUPTDATA";//장비데이터 조회.
	public static String COMMON_EQUIPMENT_DEPTINFO = "COMMON_EQUIPMENT_DEPTINFO";//부서정보 조회
	
	public static String dirRoot = Environment.getExternalStorageDirectory().toString()+"/SMARTMM/";//사진 촬영 원본
	public static String dirRoot_kr = Environment.getExternalStorageDirectory().toString()+"/SMARTMM/JEONGBI/";//사진 촬영 원본 교량
	
	public static String IMAGE_SAVE = "IMAGE_SAVE";//키값 value(1024 or 2048)
	public static String Flash_key = "CameraFlash";//Y or N
	public static String TAG_Key = "TAG_Key";//Y or N
	public static String FILE_PRIMITIVE = "COMMON_EQUIPMENT_UPLOADPROC";
	
	public static String FILE_UPLOAD_PATH = "http://128.200.121.68:9000/emp_sf/service.pe";
	
	
	public static String SHARED_ISVPN = "SHARED_ISVPN";//VPN 상태 체크
	public static String SHARED_USERID="SHARED_USERID";//SharedPreference 키 값 사용자 아이디
	public static String SHARED_USERNM="SHARED_USERNM";//SharedPreference 키 값 사용자 이름
	public static String SHARED_BSCODE="SHARED_BSCODE";//SharedPreference 키 값 사용자 부서코드
	public static String SHARED_BONBUCODE="SHARED_BONBUCODE";//SharedPreference 키 값 사용자 본부코드
	public static String SHARED_JISACODE="SHARED_JISACODE";//SharedPreference 키 값 사용자 지사코드
	public static String SHARED_USERTYPE="SHARED_USERTYPE";//사용자 타입 1.본사, 2.본부, 3.지사
	public static String SHARED_DEPTCD5="SHARED_DEPTCD5";//부서하위 팀 코드(기계화팀 필터로 인해 추가)
	public static String SHARED_DEPTNM5="SHARED_DEPTNM5";//부서하위 팀 명(기계화팀 필터로 인해 추가)

	public static String SHARED_BSSEL_BSCODE="SHARED_BSSEL_BSCODE";//DialogActivity_BsSel.java 에서 선택된 부서코드
	public static String SHARED_BSSEL_BSNAME="SHARED_BSSEL_BSNAME";//DialogActivity_BsSel.java 에서 선택된 부서코드
	public static String SHARED_BSSEL_JISACODE="SHARED_BSSEL_JISACODE";//DialogActivity_BsSel.java 에서 선택된 지사코드
	public static String SHARED_BSSEL_JISANAME="SHARED_BSSEL_JISANAME";//DialogActivity_BsSel.java 에서 선택된 지사코드




	public static String SHARED_MECHANICTEAM = "SHARED_MECHANICTEAM";//기계화팀 여부 Y or N

	public static String SHARED_ORI_BBCODE = "SHARED_ORI_BSCODE";//로그인 사용자 부서 코드
	public static String SHARED_ORI_BBNAME = "SHARED_ORI_BSNAME";//로그인 사용자 부서 명
	public static String SHARED_ORI_BSCODE = "SHARED_ORI_JSCODE";//로그인 사용자 지사 코드
	public static String SHARED_ORI_BSNAME = "SHARED_ORI_JSNAME";//로그인 사용자 부서 명
	public static String SHARED_ORI_DEPTCODE5 = "SHARED_ORI_DEPTCODE5";//로그인 사용자 지사 코드
	public static String SHARED_ORI_DEPTNAME5 = "SHARED_ORI_DEPTNAME5";//로그인 사용자 부서 명

	public static String SHARED_DEPTVERSION = "SHARED_DEPTVERSION";//sqlite > bsinfo_new 테이블 버전 정보



	public static String SHARED_BTNX = "SHARED_BTNX";//취소, X 버튼 동작 저장
	
	public static String SHARED_PERMISSION_CALL="SHARED_PERMISSION_CALL";//Y, or N


	public static int REQUESTCODE_MYLOC = 1001;//다이얼로그 내위치 확인 requestcode
	public static int REQUESTCODE_BSSEL = 1002;//다이얼로그 부서선택 requestcode
	public static int REQUESTCODE_GETIMG = 1003;//카메라 촬영 또는 앨범 선택시 requestcode
	
	public static int REQUESTCODE_UNIT1 = 2001;//단위수정 앞자리
	public static int REQUESTCODE_UNIT2 = 2002;//단위수정 뒷자리
	
	/** 2018.09.05 추가 **/
	public static String CHECKLIST_SEQ = "CHECKLIST_SEQ";// checkList 순서

	/* kbr 2022.03.29 */
	public static String COMMON_SMARTMM_CHECKVERSION = "COMMON_EQUIPMENT_CHECKVERSION"; // APP VERSION 조회
	public static String DBFILE_BACKUP_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/SMARTMMDB/";	// DB 파일 백업 경로 (외부)
	public static String DBFILE_ORIGIN_PATH = "/data/data/com.ex.smartmm/databases/";	// 원본 DB 파일 경로

}


