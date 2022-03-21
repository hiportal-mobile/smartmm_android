package com.ex.smartmm;

import java.io.File;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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

import com.ex.smartmm.CheckListActivity_bak.DoComplecatedJob;
import com.ex.smartmm.common.Configuration;
import com.ex.smartmm.common.Common;
//import com.ex.smartmm.common.DBAdapter;
import com.ex.smartmm.common.DBAdapter_checklist;
//import com.ex.smartmm.common.DBCommon;
import com.ex.smartmm.common.ImageJgInfo;
import com.ex.smartmm.net.CustomMultiPartEntity;
import com.ex.smartmm.net.Parameters;
import com.ex.smartmm.net.CustomMultiPartEntity.ProgressListener;
import com.ex.smartmm.vo.JGItemVO;
import com.ex.smartmm.vo.JbInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class CheckListActivity_bak extends Activity implements OnClickListener, OnItemClickListener{
	
	public static final String TAG = "CheckListActivity";
	
	Common common;
	//DBCommon dbcommon;
	//DBAdapter db;
	DBAdapter_checklist db;
	
	ListView listView;
	ImageView btn_back, btn_send, btn_del;

	List<JbInfo> items = new ArrayList<JbInfo>();
	
	ArrayList<JbInfo> listViewItemList = new ArrayList<JbInfo>() ;
	List<JbInfo> sendItems = new ArrayList<JbInfo>();
	
	
	//CustomChoiceListViewAdapter 어뎁터
	final CustomChoiceListViewAdapter adapter = new CustomChoiceListViewAdapter();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()~!!");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.checklist);
		
		//dbcommon = new DBCommon(CheckListActivity.this);
		//dbcommon.copyFile("smartmm_checklist.mp4");
		//db = new DBAdapter();
		common = new Common(CheckListActivity_bak.this);
		common.copyFile("smartmm_checklist.mp4");
		db = new DBAdapter_checklist();
		db.close();
		db.init();
		
		btn_back = (ImageView) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		
		listView = (ListView) findViewById(R.id.listView);
		listView.setAdapter(adapter);
		
		checkList();
		
		saveData();
		
		Log.d(TAG, "onCreate() - items Size : "+items.size());
		for(int i=0; i<items.size(); i++){
			Log.d(TAG, "onCreate() - items Size : "+items.get(i).getcontent());
		}

		btn_send = (ImageView) findViewById(R.id.btn_send);
		btn_send.setOnClickListener(this);
		
		btn_del = (ImageView) findViewById(R.id.btn_del);
		btn_del.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//db = new DBAdapter();
		db = new DBAdapter_checklist();
		db.close();
		db.init();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		db.close();
	}
	
	@Override
	protected void onDestroy() {
		if(null == db ){
			db.close();
		}
		super.onDestroy();
	}
	
	
	public void checkList(){
		Log.d(TAG, "checkList()~!");
		Cursor cursor = db.checkList();
		Log.d(TAG, "checkList() - 111");
		while(cursor.moveToNext()){
			int i = cursor.getPosition();
			
			Log.d(TAG, "checkList() - ["+cursor.getString(0)+"] "+cursor.getString(1));
		}
	}
	
	/** 데이터 저장 **/
	public void saveData(){
		items.clear();
		listViewItemList.clear();
		/*listViewItem = null;*/
		
		Cursor cursor = db.selectSmart_DATAINFO();
		while (cursor.moveToNext()) {
			
			int i = cursor.getPosition();
			Log.d(TAG, "saveData() - cursor getPosition : "+i);
			JbInfo item = new JbInfo();

			Log.d(TAG,"saveData() - JeongbiGB : "+cursor.getString(0));		//[2]외주
			Log.d(TAG,"saveData() - ItemGB : "+cursor.getString(1));		//[6]완충장치
			Log.d(TAG,"saveData() - DetailGB : "+cursor.getString(2));		//테스트
			Log.d(TAG,"saveData() - CONTENT : "+cursor.getString(3));		//완충장치/테스트(정비 전)
			Log.d(TAG,"saveData() - FILENAME : "+cursor.getString(4));		//파일이름
			Log.d(TAG,"saveData() - JBCODE : "+cursor.getString(5));		//장비코드
			Log.d(TAG,"saveData() - JBMYEONG : "+cursor.getString(6));		//장비명
			Log.d(TAG,"saveData() - DRNO : "+cursor.getString(7));			//등록번호
			Log.d(TAG,"saveData() - NAME : "+cursor.getString(8));			// 안전순찰차(인천34거3522)
			Log.d(TAG,"saveData() - DOGONG : "+cursor.getString(9));		//도공번호
			Log.d(TAG,"saveData() - DATE : "+cursor.getString(10));			//수리일자
			Log.d(TAG,"saveData() - SENDYN : "+cursor.getString(11));		//서버전송 여부(Y, N)
			Log.d(TAG,"saveData() - SWBEONHO : "+cursor.getString(12));		//사원번호
			Log.d(TAG,"saveData() - BONBUCODE : "+cursor.getString(13));	
			Log.d(TAG,"saveData() - JISACODE : "+cursor.getString(14));		//지사코드
			Log.d(TAG,"saveData() - TAGYN : "+cursor.getString(15));		//Y
			Log.d(TAG,"saveData() - SWNAME : "+cursor.getString(16));		//이름(이은경)
			Log.d(TAG,"saveData() - CHECKDATE : "+cursor.getString(17));	//체크일자(SMART_DATAINFO에 데이터 insert 한 시간)
			
			item.setId(cursor.getString(5)); 			//장비코드
			item.setjangbimyeong(cursor.getString(6)); 	//장비명
			item.setdogongcode(cursor.getString(9));	//도공번호
			item.setImgPath(cursor.getString(4));		//filename
			item.setcontent(cursor.getString(8)+"_"+cursor.getString(3));	//content::안전순찰차(인천34거3522)
			item.setDate(cursor.getString(10));  		//수리일자
			item.setsendyn(cursor.getString(11));		//sendYN
			item.setCheckdate(cursor.getString(17));		//체크일자
			items.add(item);
			
			showData(i);
			
		}
		
		Log.d(TAG, "saveData() - items Size : "+items.size());
		//Log.d(TAG, "saveData() - items 0 : "+items.get(0).getcontent());
		//Log.d(TAG, "saveData() - items 1 : "+items.get(1).getcontent());
	}
	
	/** 화면에 데이터 보여주기 **/
	public void showData(int i){
		Log.d(TAG, "showData()~!!");
		Log.d(TAG, "showData() - items dogong : "+items.get(i).getdogongcode());
		Log.d(TAG, "showData() - items checkDate : "+items.get(i).getCheckdate());
		Log.d(TAG, "showData() - items sendyn : "+items.get(i).getsendyn());
		adapter.addItem(i);
	}


	public void deleteItem(JbInfo item){
		Log.d(TAG, "deleteItem()~!");
		Log.d(TAG, "deleteItem() - dogong : "+item.getdogongcode());
		Log.d(TAG, "deleteItem() - date : "+item.getDate());
		db.deleteJBHistoryOne(item.getDate());
		saveData();
	}
	
	
	public void checkConnectStatus(){
		Log.d(TAG, "checkConnectStatus()~!");
		Log.d(TAG, "checkConnectStatus() - sendItemsSize : "+sendItems.size());
		
		for(int i=0; i<sendItems.size(); i++){
			Log.d(TAG, "checkConnectStatus() - sendItems Content : "+sendItems.get(i).getcontent());
			Log.d(TAG, "checkConnectStatus() - sendItems JangbiCode : "+sendItems.get(i).getjangbicode());
			Log.d(TAG, "checkConnectStatus() - sendItems ImgPath : "+sendItems.get(i).getImgPath());
			Log.d(TAG, "checkConnectStatus() - sendItems CheckDate : "+sendItems.get(i).getCheckdate());
		}
    	Parameters params = new Parameters(Configuration.FILE_PRIMITIVE);
    	String wifissid = "";
		WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		wifissid = wifiManager.getConnectionInfo().getSSID();
		
		Log.d(TAG, "checkConnectStatus() - params : "+params.toString());
		Log.d(TAG, "checkConnectStatus() - wifissid : "+wifissid);
		Log.d(TAG, "checkConnectStatus() - wifissidIndexOf : "+wifissid.indexOf("StarBobs"));
		
		//Wifi X, LTE O
		if(wifissid.equals("") || wifissid.equals(null) || wifissid.equals("<unknown ssid>")){
			Log.d(TAG, "checkConnectStatus() - 11111");
			executeJob(params, CheckListActivity_bak.this);

		//Wifi O
		}else{
			Log.d(TAG, "checkConnectStatus() - 22222");
			//사내 Wifi
			if(!(wifissid.indexOf("StarBobs")>-1) ){
				Log.d(TAG, "checkConnectStatus() - 33333");
				executeJob(params, CheckListActivity_bak.this);
			//일반 Wifi
			}else{
				Log.d(TAG, "checkConnectStatus() - 44444");
				AlertDialog.Builder adbLoc	= new AlertDialog.Builder(CheckListActivity_bak.this);
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
		}
		
		/*if(null != wifissid && "<unknown ssid>".equals(wifissid)){
			//
			Log.d("", "checkConnectStatus() - 33333"); //
			executeJob(params, CheckListActivity.this);
		}else{
			//wifi X
			Log.d("", "checkConnectStatus() - 44444"); //
			if(!(wifissid.indexOf("StarBobs")>-1) ){
				//다른Wifi
				Log.d("", "checkConnectStatus() - 55555");  //
				AlertDialog.Builder adbLoc	= new AlertDialog.Builder(CheckListActivity.this);
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
			}else{
				//사내Wifi
				Log.d("", "checkConnectStatus() - 666666"); 
				executeJob(params, CheckListActivity.this);
			}
		}*/
	}
	
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onItemClick()~!");
	}

	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
		
		case R.id.btn_back:
			finish();
			break;
			
		case R.id.btn_send:
			sendItems.clear();
			SparseBooleanArray checkedItems = listView.getCheckedItemPositions();
			int count = adapter.getCount();
			
			Log.d(TAG, "onClick(btn_send) - sendItems Size1 : "+sendItems.size());
			Log.d(TAG, "onClick(btn_send) - getCheckedItemCount : "+listView.getCheckedItemCount());
            Log.d(TAG, "onClick(btn_send) - count: "+count);
            Log.d(TAG, "onClick(btn_send) - checkedItems : "+checkedItems.toString());
            Log.d(TAG, "onClick(btn_send) - checkedItems size: "+checkedItems.size());  //체크된 갯수

            
            for (int i = count-1; i >= 0; i--) {
                if (checkedItems.get(i)) {
                	Log.d(TAG, "onClick(btn_send) - i: "+i);
                	Log.d(TAG, "onClick(btn_send) - items.get(i).getdogongcode() : "+items.get(i).getdogongcode());
                	Log.d(TAG, "onClick(btn_send) - items.get(i).getdogongcode() : "+items.get(i).getcontent());

                	adapter.sendItem(i);
                }
                Log.d(TAG, "onClick(btn_send) - item : "+items.size());
            	//Log.d(TAG, "onClick(btn_send) - item.getdogongcode() : "+item.getcontent());
                
            }
            // 모든 선택 상태 초기화.
            /*listView.clearChoices() ;
            adapter.notifyDataSetChanged();*/
            
            checkConnectStatus();
            
            Log.d(TAG, "onClick(btn_send) - END ***** ");
			break;
			
		case R.id.btn_del:
			AlertDialog.Builder adbLoc = new AlertDialog.Builder(CheckListActivity_bak.this);
			adbLoc.setCancelable(false);
			adbLoc.setTitle("스마트정비관리");
			adbLoc.setMessage("삭제 하시겠습니까?");
			adbLoc.setPositiveButton("예", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					/*
					deleteItem(item);*/
					SparseBooleanArray checkedItems = listView.getCheckedItemPositions();
					int count = adapter.getCount();
					
					Log.d(TAG, "onClick(btn_del) - getCheckedItemCount : "+listView.getCheckedItemCount());
		            Log.d(TAG, "onClick(btn_del) - count: "+count);
		            Log.d(TAG, "onClick(btn_del) - checkedItems : "+checkedItems.toString());
		            Log.d(TAG, "onClick(btn_del) - checkedItems size: "+checkedItems.size());
		            
		            for (int i = count-1; i >= 0; i--) {
		                if (checkedItems.get(i)) {
		                	Log.d(TAG, "onClick(btn_del) - i: "+i);
		                	//Log.d(TAG, "onClick(btn_del) - items.get(i).getdogongcode() : "+items.get(i).getdogongcode());
		                	//Log.d(TAG, "onClick(btn_del) - items.get(i).getdogongcode() : "+items.get(i).getcontent());
		                    items.remove(i);
		                    adapter.delItem(i);
		                }
		            }
		            // 모든 선택 상태 초기화.
		            listView.clearChoices() ;
		            adapter.notifyDataSetChanged();
				}
			});
			adbLoc.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
				}
			});
			adbLoc.show();
			break;
			
		default:
			break;
		}
	}

	
	//==========================================================================================================
	public class CustomChoiceListViewAdapter extends BaseAdapter {
		
	    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
	    //private ArrayList<JbInfo> listViewItemList = new ArrayList<JbInfo>() ;

	    private Bitmap placeHolderBitmap;
	    
	    // ListViewAdapter의 생성자
	    public CustomChoiceListViewAdapter() {
	    	Log.d(TAG, "******************** CustomChoiceListViewAdapter CLASS ********************");
	    }
	    
	    public class AsyncDrawable extends BitmapDrawable{
			final WeakReference<BitmapWorkerTask> taskReference;
			public AsyncDrawable(Resources resources, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask){
				super(resources, bitmap);
				taskReference = new WeakReference(bitmapWorkerTask);
			}
			public BitmapWorkerTask getBitmapWorkerTask(){
				return taskReference.get();
			}
		}
	    
		// Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
	    @Override
	    public int getCount() {
	    	Log.d(TAG, "getCount() : "+listViewItemList.size());
	        return listViewItemList.size() ;
	    }

	    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	Log.d(TAG, "CustomChoiceListViewAdapter > getView()~!");
	    	
	        final int pos = position;
	        final Context context = parent.getContext();

	        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
	        if (convertView == null) {
	            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            convertView = inflater.inflate(R.layout.item_checklist_bak, parent, false);
	            //convertView = inflater.inflate(R.id.layout_checkbox, parent, false);
	        }

	        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
	        ImageView imageview = (ImageView) convertView.findViewById(R.id.item_img);
			TextView item_dogong = (TextView) convertView.findViewById(R.id.item_dogong);
			TextView item_content = (TextView) convertView.findViewById(R.id.item_content);
			
			// Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
			JbInfo listViewItem = new JbInfo();
			listViewItem = listViewItemList.get(position);
			File imageFile = new File(listViewItem.getImgPath());
			
			if(checkBitmapWorkerTask(imageFile, imageview)){
				BitmapWorkerTask bitmapWorkerTask = new BitmapWorkerTask(imageview);
				AsyncDrawable asyncDrawable = new AsyncDrawable(imageview.getResources(), placeHolderBitmap, bitmapWorkerTask);
				imageview.setImageDrawable(asyncDrawable);

				bitmapWorkerTask.execute(imageFile);
			}
			
	        // 아이템 내 각 위젯에 데이터 반영
			item_dogong.setText(listViewItem.getdogongcode());
			item_content.setText(listViewItem.getcontent());
			Log.d(TAG, "CustomChoiceListViewAdapter() - listViewItemList Size2 : "+listViewItemList.size());
			Log.d(TAG, "getView() - listViewItem.getdogongcode() : "+listViewItem.getdogongcode());
			Log.d(TAG, "getView() - listViewItem.getsendyn() : "+listViewItem.getsendyn());
			if("Y".equals(listViewItem.getsendyn())){
				Log.d(TAG, "getView() - YYYYY");
				item_dogong.setTextColor(Color.parseColor("#0054FF"));//전송완료
				item_content.setTextColor(Color.parseColor("#0054FF"));//전송완료
			}else if("N".equals(listViewItem.getsendyn())){
				Log.d(TAG, "getView() - NNNNN");
				item_dogong.setTextColor(Color.parseColor("#ff0000"));//미전송
				item_content.setTextColor(Color.parseColor("#ff0000"));//미전송
			}
			
	        return convertView;
	    }

	    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
	    @Override
	    public long getItemId(int position) {
	        return position ;
	    }

	    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
	    @Override
	    public Object getItem(int position) {
	        return listViewItemList.get(position) ;
	    }

	    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
	    public void addItem(int i) {
	    	Log.d(TAG, "addItem()~!");
	    	
	    	JbInfo item = new JbInfo(); 
	    	item.setjangbicode(items.get(i).getjangbicode());
	    	item.setjangbimyeong(items.get(i).getjangbimyeong());
	    	item.setdogongcode(items.get(i).getdogongcode());
	    	item.setImgPath(items.get(i).getImgPath());
	    	item.setcontent(items.get(i).getcontent());
	    	item.setDate(items.get(i).getDate());
	    	item.setsendyn(items.get(i).getsendyn());
	    	item.setCheckdate(items.get(i).getCheckdate());
	        listViewItemList.add(item);
	    }
	    
	    public void delItem(int i){
	    	JbInfo item = new JbInfo();
	    	Log.d(TAG, "delItem()~!");
	    	Log.d(TAG, "delItem() - listViewItemList.get(i).getdogongcode() : "+listViewItemList.get(i).getdogongcode());
	    	
	    	item = listViewItemList.get(i);
	    	Log.d(TAG, "delItem() - item.getdogongcode() : "+item.getdogongcode());
	    	
	    	listViewItemList.remove(i);
	    	listViewItemList.clear();
	    	deleteItem(item);
	    	
	    	
	    }
	    
	    public void sendItem(int i){
	    	JbInfo item = new JbInfo();
	    	Log.d(TAG, "sendItem()~!");
	    	Log.d(TAG, "sendItem() - listViewItemList.get(i).getdogongcode() : "+listViewItemList.get(i).getdogongcode());
	    	Log.d(TAG, "sendItem() - listViewItemList.get(i).getCheckdate() : "+listViewItemList.get(i).getCheckdate());
	    	item = listViewItemList.get(i);
	    	Log.d(TAG, "sendItem() - item.getdogongcode() : "+item.getdogongcode());
	    	
	    	sendItems.add(item);
	    	Log.d(TAG, "sendItem() - sendItems.size() : "+sendItems.size());
	    }
	    
	    public boolean checkBitmapWorkerTask(File imageFile, ImageView imageView){
			BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
			if(bitmapWorkerTask != null){
				final File workerFile = bitmapWorkerTask.getmImageFile();
				if(workerFile != null){
					if(workerFile != imageFile){
						bitmapWorkerTask.cancel(true);
					}else{
						return false;
					}
				}
			}
			return true;
		}
		
		public BitmapWorkerTask getBitmapWorkerTask(ImageView imageView){
			Drawable drawable = imageView.getDrawable();
			if(drawable instanceof AsyncDrawable){
				AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
			return null;
		}
	    
	}
	//==========================================================================================================
	public class BitmapWorkerTask extends AsyncTask<File, Void, Bitmap> {

		WeakReference<ImageView> imageViewReferences;
		final static int TARGET_IMAGE_VIEW_WIDTH = 90;
		final static int TARGET_IMAGE_VIEW_HEIGHT = 90;
		private File mImageFile;

		public BitmapWorkerTask(ImageView imageView) {
			Log.d(TAG, "******************** BitmapWorkerTask CLASS > BitmapWorkerTask()~! ********************");
			imageViewReferences = new WeakReference<ImageView>(imageView);
		}

		@Override
		protected Bitmap doInBackground(File... params) {
			mImageFile = params[0];
			return decodeBitmapFromFile(params[0]);
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			Log.d(TAG, "BitmapWorkerTask > onPostExecute()~!");
	        
			if(bitmap != null && imageViewReferences != null){
	            ImageView viewImage = imageViewReferences.get();
	            if(viewImage != null){
	                viewImage.setImageBitmap(bitmap);
	            }
	        }
			if (isCancelled()) {
				bitmap = null;
			}
			if (bitmap != null && imageViewReferences != null) {
				ImageView imageView = imageViewReferences.get();
				BitmapWorkerTask bitmapWorkerTask = adapter.getBitmapWorkerTask(imageView);

				if (this == bitmapWorkerTask && imageView != null) {
					imageView.setImageBitmap(bitmap);
					Log.d(TAG, "onPostExecute() - bitmap width: height = " + bitmap.getWidth() +":" + bitmap.getHeight());
				}
			}
		}

		private int calcalateInSampleSize(BitmapFactory.Options bmOptions) {
			final int photoWidth = bmOptions.outWidth;
			final int photoHeight = bmOptions.outHeight;
			int scaleFactor = 1;

			if (photoWidth > TARGET_IMAGE_VIEW_WIDTH || photoHeight > TARGET_IMAGE_VIEW_HEIGHT) {
				final int halfPhotoWidth = photoWidth / 2;
				final int halfPhotoHeight = photoHeight / 2;

				while (halfPhotoWidth / scaleFactor > TARGET_IMAGE_VIEW_WIDTH || halfPhotoHeight / scaleFactor > TARGET_IMAGE_VIEW_HEIGHT) {
					scaleFactor *= 2;
				}
			}
			return scaleFactor;
		}

		private Bitmap decodeBitmapFromFile(File imageFile) {
			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			bmOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bmOptions);
			bmOptions.inSampleSize = calcalateInSampleSize(bmOptions);
			bmOptions.inJustDecodeBounds = false;
			return BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bmOptions);
		}

		public File getmImageFile() {
			return mImageFile;
		}
	}
	

	
	//==========================================================================================================
	
	Handler fileHandler;
	//+++++++++++++++++++++++++++++++++START
	public void executeJob(final Parameters params, Activity mActivity) {
		Log.d(TAG, "******************** executeJob()~! ********************");
		Log.d("", "executeJob()");
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
			Toast.makeText(CheckListActivity_bak.this, R.string.no_data, Toast.LENGTH_SHORT).show();
		}
		
		Log.d(TAG, "executeJob() - END *****");
	}
	
	
	public List<ImageJgInfo> ReadSDCard() {
		Log.d(TAG, "******************** ReadSDCard()~! ********************");
		Cursor cursor = db.selectSmart_DATAINFO_ONE();
		File[] files = new File[cursor.getCount()];
		
		Log.d(TAG, "ReadSDCard() - cursor.count : "+cursor.getCount());
		List<ImageJgInfo>  trnImageList = new ArrayList<ImageJgInfo>();

		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			ImageJgInfo item = new ImageJgInfo();
			JbInfo item_sub = new JbInfo();
			
			Log.d(TAG,"ReadSDCard() - JeongbiGB : "+cursor.getString(0));		//[2]외주
			Log.d(TAG,"ReadSDCard() - ItemGB : "+cursor.getString(1));			//[6]완충장치
			Log.d(TAG,"ReadSDCard() - DetailGB : "+cursor.getString(2));		//테스트
			Log.d(TAG,"ReadSDCard() - CONTENT : "+cursor.getString(3));		//완충장치/테스트(정비 전)
			Log.d(TAG,"ReadSDCard() - FILENAME : "+cursor.getString(4));		//파일이름
			Log.d(TAG,"ReadSDCard() - JBCODE : "+cursor.getString(5));			//장비코드
			Log.d(TAG,"ReadSDCard() - JBMYEONG : "+cursor.getString(6));		//장비명
			Log.d(TAG,"ReadSDCard() - DRNO : "+cursor.getString(7));			//등록번호
			Log.d(TAG,"ReadSDCard() - NAME : "+cursor.getString(8));			// 안전순찰차(인천34거3522)
			Log.d(TAG,"ReadSDCard() - DOGONG : "+cursor.getString(9));			//도공번호
			Log.d(TAG,"ReadSDCard() - DATE : "+cursor.getString(10));			//수리일자
			Log.d(TAG,"ReadSDCard() - SENDYN : "+cursor.getString(11));			//서버전송 여부(Y, N)
			Log.d(TAG,"ReadSDCard() - SWBEONHO : "+cursor.getString(12));		//사원번호
			Log.d(TAG,"ReadSDCard() - BONBUCODE : "+cursor.getString(13));	
			Log.d(TAG,"ReadSDCard() - JISACODE : "+cursor.getString(14));		//지사코드
			Log.d(TAG,"ReadSDCard() - TAGYN : "+cursor.getString(15));			//Y
			Log.d(TAG,"ReadSDCard() - SWNAME : "+cursor.getString(16));			//이름(이은경)
			Log.d(TAG,"ReadSDCard() - CHECKDATE : "+cursor.getString(17));		//체크일자(SMART_DATAINFO에 데이터 insert 한 시간)
			
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
			item_sub.setCheckdate(cursor.getString(17));  // 날짜
			
			String jeongbiGB = cursor.getString(0);
			String itemGB = cursor.getString(1);
			Log.d(TAG, "ReadSDCard() - jeongbiGB : "+jeongbiGB);
			Log.d(TAG, "ReadSDCard() - itemGB : "+itemGB);
			Log.d(TAG, "ReadSDCard() - content : "+item_sub.getsulicontent());
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
			
			for(int j=0; j<sendItems.size(); j++){
				Log.d(TAG, "ReadSdCard() - sendItems ImgPath : "+sendItems.get(j).getImgPath());
				Log.d(TAG, "ReadSdCard() - sendItems CheckDate : "+sendItems.get(j).getCheckdate());
				Log.d(TAG, "ReadSdCard() - sendItems Dogong : "+sendItems.get(j).getdogongcode());
				Log.d(TAG, "ReadSdCard() - checkdate : "+cursor.getString(17));
				if(sendItems.get(j).getCheckdate().equals(cursor.getString(17))){
					Log.d(TAG, "ReadSdCard() - ImgPath : "+cursor.getString(4));
					Log.d(TAG, "ReadSdCard() - Dogong : "+cursor.getString(9));
					trnImageList.add(item);
				}
			}
			
			//trnImageList.add(item);
		}

		Log.d(TAG, "ReadSdCard() - list Size : " + trnImageList.size());
		return trnImageList;
	}
	//+++++++++++++++++++++++++++++++++END
	
	double totalFileSize = 0.0;
	public List<ImageJgInfo> ReadSDCardItem(JbInfo info) {
		Log.d(TAG, "******************** ReadSDCardItem()~! ********************");
		//Cursor cursor = db.selectBms_Krgb_JGINFO_ONE();
		Cursor cursor = db.selectSmart_DATAINFO_ONE();
		
		File[] files = new File[cursor.getCount()];
		
		List<ImageJgInfo>  trnImageList = new ArrayList<ImageJgInfo>();
		
		Log.d(TAG, "ReadSDCard() - cursor.count : "+cursor.getCount());
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			ImageJgInfo item = new ImageJgInfo();
			JbInfo item_sub = new JbInfo();
			item_sub.setjangbicode(cursor.getString(5));  // 장비코드
			item_sub.setdogongcode(cursor.getString(9));  // 도공번호
			item_sub.setfilename(cursor.getString(4));  // 파일이름
			item_sub.setreg_date(cursor.getString(10));	// 정비일시
			item_sub.setjangbimyeong(cursor.getString(8));  // 장비명 name
			item_sub.setsulicontent(cursor.getString(3));  // 내용  content
			item_sub.setbuseocode(cursor.getString(14));  // 부서코드
			item_sub.setswbeonho(cursor.getString(12));  // 사원번호
			item_sub.settagYn(cursor.getString(15));  // 태그여부
			item_sub.setswname(cursor.getString(16));  // 이름
			item_sub.setCheckdate(cursor.getString(17));  // 이름
			
			String jeongbiGB = cursor.getString(0);
			String itemGB = cursor.getString(1);
			item_sub.setGubun(jeongbiGB.substring(jeongbiGB.indexOf("[")+1, jeongbiGB.indexOf("]")));	//정비구분 gubun 추가
			item_sub.setBupumcode(itemGB.substring(itemGB.indexOf("[")+1, itemGB.indexOf("]")));	//수리항목 bupumcode 추가
			
			item.setInfo(item_sub);
			Log.d(TAG, "ReadSDCardOneItem() - item : "+item.getInfo());
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
			Log.d("","asdfasdfasdfasdfasd " + info.getImgPath());
			Log.d("","asdfasdfasdfasdfasd " + cursor.getString(4));
			if(info.getImgPath().equals(cursor.getString(4))){
				Log.d("","asdfasdfasdfasdfasd in " + info.getImgPath());
				Log.d("","asdfasdfasdfasdfasd in " + cursor.getString(4));
				trnImageList.add(item);	
			}
		}
		
		

		Log.d("", "ReadSdCard list size = " + trnImageList.size());
		Log.d("", "ReadSdCard trnImageList = " + trnImageList);
		return trnImageList;
	}
	//==========================================================================================================
	
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
			mFileList = mList;
			this.params = params;
			this.mActivity = mActivity;
		}

		@Override
		protected void onPreExecute() {
			Log.d(TAG, "DoComplecatedJob > onPreExecute()~!");
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
			Log.d(TAG, "doInBackground()~!");
			Log.d(TAG, "doInBackground() - strData : "+strData);
			// --------------------------------------------------------------------------------------------
			// #region 실 첨부파일 전송, 전송화면 프로그레스 바 업데이트 함수 호출
			Log.d("","################### DoComplecatedJob doInBackground start!!!     listsize = " + mFileList.size());
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

					ImageJgInfo image = mFileList.get(i);
					absolutePath = image.getFilePath().toString();
					Log.d("", "send filepath name = " + absolutePath);
					multipartContent.addPart("uploaded_file" + i, new FileBody(new File(absolutePath)));

					totalSize = multipartContent.getContentLength();
					totalSizeKB = totalSize / 1024;
					// pb.setMax(Integer.parseInt(totalSize + ""));

					// Send it
					multipartContent.addPart("renamePrefix", new StringBody(""));
					
					
					Log.d("","Multipartcontent SURIL!! = "+image.getInfo().getreg_date());     														// 수리일
					Log.d("","Multipartcontent SURIL = "+image.getInfo().getreg_date().replace(".", "").substring(0, 8));     						// 수리일
					Log.d("","Multipartcontent JANGBICODE = "+Common.nullCheck(image.getInfo().getjangbicode()));									// 장비코드
					Log.d("","Multipartcontent SURIDATE = "+image.getInfo().getreg_date().substring(0, 16));										// 정비일시
					Log.d("","Multipartcontent JANGBIMYEONG = "+Common.nullCheck(image.getInfo().getjangbimyeong())); 								// 장비명
					Log.d("","Multipartcontent DOGONGBEONHO = "+Common.nullCheck(image.getInfo().getdogongcode()));   								// 도공번호
					Log.d("","Multipartcontent CONTENT = "+image.getInfo().getsulicontent());  														// 수리내용
					Log.d("","Multipartcontent IMGAGE = "+image.getFileName().toString());															// 이미지명
					Log.d("","Multipartcontent BUSEOCODE = "+image.getInfo().getbuseocode());
					Log.d("","Multipartcontent GUBUN = "+image.getInfo().getGubun());
					Log.d("","Multipartcontent BUPUMCODE = "+image.getInfo().getBupumcode());
					Log.d(TAG,"Multipartcontent SWBEONHO = "+image.getInfo().getswbeonho());
					Log.d(TAG,"Multipartcontent SENDDATE = "+Common.getCalendarDateYMDHM());
					
					String tempDate = image.getInfo().getreg_date();
					String year = tempDate.substring(0,4);
					String month = tempDate.substring(4,6);
					String day = tempDate.substring(6,8);
					String hh = tempDate.substring(8,10);
					String mm = tempDate.substring(10,12);
					String ss = tempDate.substring(12,14);
					
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
					multipartContent.addPart("SENDDATE",new StringBody(Common.getCalendarDateYMDHM(), Charset.forName("UTF-8")));
					
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
					
					Log.d(TAG, "multipartContent===================="+multipartContent.toString());
					
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
					Log.d("", "DoComplecatedJob doinbackground sendCheckFile " + sendCheckFile);
					Log.d("", "DoComplecatedJob doinbackground result " + response.toString());
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
		protected void onProgressUpdate(Integer... progress) {
			Log.d(TAG, "onProgressUpdate()~!");	
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
			Log.d(TAG, "onPostExecute()~!");
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
				Toast.makeText(CheckListActivity_bak.this, "파일 전송에 실패했습니다.", Toast.LENGTH_LONG).show();
			}else{
//				openWarnDialog(mActivity, "파일 전송 성공");
				if("VPNFAIL".equals(sendCheckFile)){
					Log.d("onPostExecute", "VPNFAIL - 업무접속 확인해주세요 ");
					AlertDialog.Builder adbLoc	= new AlertDialog.Builder(CheckListActivity_bak.this);
					adbLoc.setCancelable(false);
					adbLoc.setTitle("스마트정비관리"); 
					adbLoc.setMessage("업무접속 연결 확인 후 사용하시기 바랍니다.");
					adbLoc.setPositiveButton("확인", new DialogInterface.OnClickListener() {
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
					Toast.makeText(CheckListActivity_bak.this, "파일 전송에 실패했습니다.", Toast.LENGTH_LONG).show();
				}else if("".equals(Common.nullCheck(sendCheckFile))){
					Toast.makeText(CheckListActivity_bak.this, "파일 전송에 실패했습니다.", Toast.LENGTH_LONG).show();
				}else if("<resultcode>1000</resultcode>".equals(sendCheckFile)){
					Toast.makeText(CheckListActivity_bak.this, "파일 전송 성공", Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(CheckListActivity_bak.this, "파일 전송에 실패했습니다.", Toast.LENGTH_LONG).show();
				}
				
			}
			
			// 모든 선택 상태 초기화.
			listView.clearChoices() ;
            adapter.notifyDataSetChanged();
			saveData();
			
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
}
