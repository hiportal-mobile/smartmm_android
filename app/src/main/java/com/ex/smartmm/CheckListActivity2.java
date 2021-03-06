package com.ex.smartmm;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
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

import com.ex.smartmm.CheckListActivity.DoComplecatedJob;
import com.ex.smartmm.common.Common;
import com.ex.smartmm.common.Configuration;
import com.ex.smartmm.common.DBAdapter_checklist;
import com.ex.smartmm.common.ImageJgInfo;
import com.ex.smartmm.net.CustomMultiPartEntity;
import com.ex.smartmm.net.Parameters;
import com.ex.smartmm.net.CustomMultiPartEntity.ProgressListener;
import com.ex.smartmm.vo.JbDataInfo;
import com.ex.smartmm.vo.JbInfo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Layout;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class CheckListActivity2 extends Activity implements OnClickListener, OnItemClickListener{
	
	public static final String TAG = "CheckListActivity2";

	Common common;
	DBAdapter_checklist db;
	
	ImageView btn_back, btn_send, btn_del;
	ListView listView;
	ImageAdapter adapter;
	
	//List<JbInfo> items = new ArrayList<JbInfo>();
	List<JbDataInfo> items = new ArrayList<JbDataInfo>();
	List<JbDataInfo> sendItems;
	
	private boolean[] thumbnailsselection;
	int imgCnt = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.checklist);
		
		common = new Common(CheckListActivity2.this);
		db = new DBAdapter_checklist();
		db.close();
		db.init();
		
		btn_back = (ImageView)findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		
		btn_send = (ImageView)findViewById(R.id.btn_send);
		btn_send.setOnClickListener(this);
		
		btn_del = (ImageView)findViewById(R.id.btn_del);
		btn_del.setOnClickListener(this);
		
		
		
		saveData();
		
	}
	
	@Override
	protected void onResume() {
		Log.d(TAG, "onResume()~!");
		super.onResume();
		//db = new DBAdapter();
		db = new DBAdapter_checklist();
		db.close();
		db.init();
	}
	
	@Override
	protected void onPause() {
		Log.d(TAG, "onPause()~!");
		super.onPause();
		db.close();
	}
	
	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy()~!");
		if(null == db ){
			db.close();
		}
		super.onDestroy();
	}
	
	
	public void saveData(){
		items.clear();
		Cursor cursor = db.selectSmart_DATAINFO_2();
		
		while(cursor.moveToNext()){
			int i = cursor.getPosition();
			Log.d(TAG, "saveData() - cursor getPosition : "+i);
			//JbInfo item = new JbInfo();
			JbDataInfo item = new JbDataInfo();
			
			Log.d(TAG,"saveData() - JeongbiGB : "+cursor.getString(0));		//[2]??????
			Log.d(TAG,"saveData() - ItemGB : "+cursor.getString(1));		//[6]????????????
			Log.d(TAG,"saveData() - DetailGB : "+cursor.getString(2));		//?????????
			Log.d(TAG,"saveData() - CONTENT : "+cursor.getString(3));		//????????????/?????????(?????? ???)
			Log.d(TAG,"saveData() - FILENAME : "+cursor.getString(4));		//????????????
			Log.d(TAG,"saveData() - JBCODE : "+cursor.getString(5));		//????????????
			Log.d(TAG,"saveData() - JBMYEONG : "+cursor.getString(6));		//?????????
			Log.d(TAG,"saveData() - DRNO : "+cursor.getString(7));			//????????????
			Log.d(TAG,"saveData() - NAME : "+cursor.getString(8));			// ???????????????(??????34???3522)
			Log.d(TAG,"saveData() - DOGONG : "+cursor.getString(9));		//????????????
			Log.d(TAG,"saveData() - DATE : "+cursor.getString(10));			//????????????
			Log.d(TAG,"saveData() - SENDYN : "+cursor.getString(11));		//???????????? ??????(Y, N)
			Log.d(TAG,"saveData() - SWBEONHO : "+cursor.getString(12));		//????????????
			Log.d(TAG,"saveData() - BONBUCODE : "+cursor.getString(13));	
			Log.d(TAG,"saveData() - JISACODE : "+cursor.getString(14));		//????????????
			Log.d(TAG,"saveData() - TAGYN : "+cursor.getString(15));		//Y
			Log.d(TAG,"saveData() - SWNAME : "+cursor.getString(16));		//??????(?????????)
			Log.d(TAG,"saveData() - CHECKDATE : "+cursor.getString(17));	//????????????(SMART_DATAINFO??? ????????? insert ??? ??????)
			
			Log.d(TAG,"saveData() - COUNT : "+cursor.getString(18));		//COUNT
			Log.d(TAG,"saveData() - MIN(DATE) : "+cursor.getString(19));	//
			
			item.setJangbicode(cursor.getString(5)); //????????????
			item.setJangbimyeong(cursor.getString(6)); //?????????
			item.setDogong(cursor.getString(9)); 	//????????????
			item.setImgPath(cursor.getString(4));		//filename
			item.setContent(cursor.getString(8)+"_"+cursor.getString(3));	//content::???????????????(??????34???3522)
			item.setDate(cursor.getString(10));  		//????????????
			item.setSendyn(cursor.getString(11));		//sendYN
			item.setCheckdate(cursor.getString(17));		//????????????
			item.setCount(cursor.getString(18));		//????????????
			
			items.add(item);
			
			//showData(i);
		}
		Log.d(TAG, "saveData() items++++++++++++++++++");
		for(int i=0; i<items.size(); i++){
			
			Log.d(TAG, "saveData() items : ["+i+"] "+items.get(i));
		}
		
		thumbnailsselection = new boolean[items.size()];
		
		showData();
	}
	
	public void showData(){
		Log.d(TAG, "showData()~!");
		listView = (ListView)findViewById(R.id.listView);
		adapter = new ImageAdapter();
		listView.setAdapter(adapter);
	}
	
	/*public void deleteItem(int i){
		Log.d(TAG, "deleteItem()~!");
		Log.d(TAG, "deleteItem() - checkdate : "+items.get(i).getCheckdate());
		db.deleteJBHistoryOne(items.get(i).getCheckdate());
		//items.remove(i);
		
		Cursor cursor = db.selectSmart_DATAINFO();
		Log.d(TAG, "cursor count : "+cursor.getCount());
		saveData();
	}*/
	
	
	public void deleteItem(ArrayList<JbDataInfo> selectedItem){
		Log.d(TAG, "deleteItem()~!");
		Log.d(TAG, "deleteItem() - selectedItem Size : "+selectedItem.size());
		
		for(int i=0; i<selectedItem.size(); i++){
			Log.d(TAG, "deleteItem() - i : "+i);
			Log.d(TAG, "deleteItem() - selectedItem Content : "+selectedItem.get(i).getContent());
			Cursor cursor = db.deleteJBHistoryOne(selectedItem.get(i).getCheckdate());
		}

		saveData();

	}
	
	public void checkConnectStatus(ArrayList<JbDataInfo> selectedItem){
		Log.d(TAG, "checkConnectStatus()~!");
		Log.d(TAG, "checkConnectStatus() - selectedItem Size : "+selectedItem.size());
		sendItems = new ArrayList<JbDataInfo>();
		for(int i=0; i<selectedItem.size(); i++){
			Log.d(TAG, "checkConnectStatus() - selectedItem Content : ["+i+"] "+selectedItem.get(i).getContent());
			Log.d(TAG, "checkConnectStatus() - selectedItem Content : ["+i+"] "+selectedItem.get(i).getCheckdate());
			//sendItems.get(i).setCheckdate(selectedItem.get(i).getCheckdate());
			
			getSendItems(selectedItem.get(i).getCheckdate());
			Log.d(TAG, "checkConnectStatus() ++++++++++++++++++++");
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
			executeJob(params, CheckListActivity2.this);

		//Wifi O
		}else{
			Log.d(TAG, "checkConnectStatus() - 22222");
			//?????? Wifi
			if(!(wifissid.indexOf("StarBobs")>-1) ){
				Log.d(TAG, "checkConnectStatus() - 33333");
				executeJob(params, CheckListActivity2.this);
			//?????? Wifi
			}else{
				Log.d(TAG, "checkConnectStatus() - 44444");
				AlertDialog.Builder adbLoc	= new AlertDialog.Builder(CheckListActivity2.this);
				adbLoc.setCancelable(false);
				adbLoc.setTitle("?????????????????????"); 
				adbLoc.setMessage("???????????? ?????? ?????? ??? ??????????????? ????????????.");
				adbLoc.setPositiveButton("??????", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				});
				adbLoc.setNegativeButton("??????????????????", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = getPackageManager().getLaunchIntentForPackage("com.ex.group.folder");
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		                startActivity(intent);
					}
				});
				adbLoc.show();
			}
		}
	}
	
	
	public void getSendItems(String checkdate){
		Cursor cursor = db.selectSmart_DATAINFO_SENDITEMS(checkdate);
		Log.d(TAG, "getSendItems()~!");
		Log.d(TAG, "getSendItems() - cursor count : "+cursor.getCount());
		for(int i=0; i<cursor.getCount(); i++){
			cursor.moveToPosition(i);
			JbDataInfo item_sub = new JbDataInfo();
			
			Log.d(TAG,"getSendItems() - JeongbiGB : "+cursor.getString(0));		//[2]??????
			Log.d(TAG,"getSendItems() - ItemGB : "+cursor.getString(1));			//[6]????????????
			Log.d(TAG,"getSendItems() - DetailGB : "+cursor.getString(2));		//?????????
			Log.d(TAG,"getSendItems() - CONTENT : "+cursor.getString(3));		//????????????/?????????(?????? ???)
			Log.d(TAG,"getSendItems() - FILENAME : "+cursor.getString(4));		//????????????
			Log.d(TAG,"getSendItems() - JBCODE : "+cursor.getString(5));			//????????????
			Log.d(TAG,"getSendItems() - JBMYEONG : "+cursor.getString(6));		//?????????
			Log.d(TAG,"getSendItems() - DRNO : "+cursor.getString(7));			//????????????
			Log.d(TAG,"getSendItems() - NAME : "+cursor.getString(8));			// ???????????????(??????34???3522)
			Log.d(TAG,"getSendItems() - DOGONG : "+cursor.getString(9));			//????????????
			Log.d(TAG,"getSendItems() - DATE : "+cursor.getString(10));			//????????????
			Log.d(TAG,"getSendItems() - SENDYN : "+cursor.getString(11));			//???????????? ??????(Y, N)
			Log.d(TAG,"getSendItems() - SWBEONHO : "+cursor.getString(12));		//????????????
			Log.d(TAG,"getSendItems() - BONBUCODE : "+cursor.getString(13));	
			Log.d(TAG,"getSendItems() - JISACODE : "+cursor.getString(14));		//????????????
			Log.d(TAG,"getSendItems() - TAGYN : "+cursor.getString(15));			//Y
			Log.d(TAG,"getSendItems() - SWNAME : "+cursor.getString(16));			//??????(?????????)
			Log.d(TAG,"getSendItems() - CHECKDATE : "+cursor.getString(17));		//????????????(SMART_DATAINFO??? ????????? insert ??? ??????)

			item_sub.setJangbicode(cursor.getString(5));  // ????????????
			item_sub.setDogong(cursor.getString(9));  // ????????????
			//item_sub.setfilename(cursor.getString(4));  // ????????????
			item_sub.setImgPath(cursor.getString(4)); //????????????
			item_sub.setDate(cursor.getString(10));	//????????????
			item_sub.setJangbimyeong(cursor.getString(8));  // ????????? name
			item_sub.setContent(cursor.getString(3));  // ??????  content
			item_sub.setJisacode(cursor.getString(14));  // ????????????
			item_sub.setSwbeonho(cursor.getString(12));  // ????????????
			item_sub.setTagyn(cursor.getString(15));  // ????????????
			item_sub.setSwname(cursor.getString(16));  // ??????
			item_sub.setCheckdate(cursor.getString(17));  // ??????
			
			sendItems.add(item_sub);
		}
		
	}
	
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		AlertDialog.Builder adbLoc = new AlertDialog.Builder(CheckListActivity2.this);

		switch (v.getId()){
		case R.id.btn_back:
			finish();
			break;
		
		case R.id.btn_send:
			Log.d(TAG, "onClick(btn_send)~!");
			adbLoc = new AlertDialog.Builder(CheckListActivity2.this);

			adbLoc.setCancelable(false);
			adbLoc.setTitle("?????????????????????");
			adbLoc.setMessage("??????????????? ???????????????????");
			adbLoc.setPositiveButton("???", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					
					int len = thumbnailsselection.length;
					int cnt = 0;
					Log.d(TAG, "onClick(btn_send) - thumbnailsselection Length : "+thumbnailsselection.length);
					ArrayList<JbDataInfo> selectedItem = new ArrayList<JbDataInfo>();
					for(int i=0; i<len; i++){
						if(thumbnailsselection[i]){
							//cnt++;
							Log.d(TAG, "onClick(btn_send) - thumbnailsselection : ["+i+"] "+thumbnailsselection[i]);
							selectedItem.add(items.get(i));
						}
					}
					checkConnectStatus(selectedItem);
					// ?????? ?????? ?????? ?????????.
		            /*listView.clearChoices() ;
		            adapter.notifyDataSetChanged();*/
				}
				
			});
			adbLoc.setNegativeButton("?????????", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
				}
			});
//			adbLoc.show();

			Dialog d = adbLoc.create();

			d.show();
			d.getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);

			break;
			
		case R.id.btn_del:
			Log.d(TAG, "onClick(btn_del)~!");
			adbLoc = new AlertDialog.Builder(CheckListActivity2.this);

			adbLoc.setCancelable(false);
			adbLoc.setTitle("?????????????????????");
			adbLoc.setMessage("?????? ???????????????????");
			adbLoc.setPositiveButton("???", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					
					int len = thumbnailsselection.length;
					int cnt = 0;
					Log.d(TAG, "onClick(btn_del) - thumbnailsselection Length : "+thumbnailsselection.length);
					ArrayList<JbDataInfo> selectedItem = new ArrayList<JbDataInfo>();
					for(int i=0; i<len; i++){
						if(thumbnailsselection[i]){
							//cnt++;
							Log.d(TAG, "onClick(btn_del) - thumbnailsselection : ["+i+"] "+thumbnailsselection[i]);
							selectedItem.add(items.get(i));
							//deleteItem(i);
							//deleteItem(selectedItem);
						}
					}
					deleteItem(selectedItem);
					// ?????? ?????? ?????? ?????????.
		            listView.clearChoices() ;
		            adapter.notifyDataSetChanged();

					// kbr 2022.04.08
					// ?????? ?????? ?????? ??? DB ?????? ??????
					Common commonF = new Common(CheckListActivity2.this);
					try {
						commonF.moveFile("smartmm_checklist.db", Configuration.DBFILE_ORIGIN_PATH, Configuration.DBFILE_BACKUP_PATH);
						commonF.moveFile("smartmm.db", Configuration.DBFILE_ORIGIN_PATH, Configuration.DBFILE_BACKUP_PATH);
					} catch (Exception e) {
						Log.e(TAG, "File backup Exception!!!!!!!!!!!!!!!");
					}
				}
				
			});
			adbLoc.setNegativeButton("?????????", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
				}
			});
//			adbLoc.show();

			d = adbLoc.create();
			d.show();
			d.getWindow().setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			
			break;
			
		default:
			break;
		}
	}
	
	class ViewHolder{
		CheckBox item_select;
		ImageView item_img;
		TextView item_count;
		TextView item_dogong;
		TextView item_content;
		int id;
	}
	
	
	/** ******************************************************************************* **/
	
	public class ImageAdapter extends BaseAdapter{
		LayoutInflater inflater;
		
		public ImageAdapter(){
			Log.d(TAG, "******************** ImageAdapter CLASS ********************");
			inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
			Log.d(TAG, "ImageAdapter() - items size : "+items.size());
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Log.d(TAG, "getView()~!");
			Log.d(TAG, "getView() - items size : "+items.size());
			Log.d(TAG, "getView() - position111 : "+position);
			Bitmap myBitmap = BitmapFactory.decodeFile(items.get(position).getImgPath());

			Log.d(TAG, "getView() - position222 : "+position);
			Log.d(TAG, "getView() - thumbnailsselection : "+thumbnailsselection[position]);
			ViewHolder holder = null;
			if(convertView == null){
				holder = new ViewHolder();
				inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.item_checklist, null);
				
				holder.item_select = (CheckBox)convertView.findViewById(R.id.item_select);
				holder.item_img = (ImageView)convertView.findViewById(R.id.item_img);
				holder.item_count = (TextView)convertView.findViewById(R.id.item_count);
				holder.item_dogong = (TextView)convertView.findViewById(R.id.item_dogong);
				holder.item_content = (TextView)convertView.findViewById(R.id.item_content);
				
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder)convertView.getTag();
			}
			
			holder.item_select.setId(position);
			holder.item_img.setId(position);
			holder.item_count.setId(position);
			holder.item_dogong.setId(position);
			holder.item_content.setId(position);
			
			holder.item_select.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					CheckBox cb = (CheckBox) v;
					int id = cb.getId();
					if (thumbnailsselection[id]){
						imgCnt--;
						cb.setChecked(false);
						thumbnailsselection[id] = false;
					} else {
						
						cb.setChecked(true);
						thumbnailsselection[id] = true;
						imgCnt ++;
						
						/*if(imgCnt >= 5){
							//makeDialog();
							cb.setChecked(false);
							thumbnailsselection[id] = false;
						}else{
							cb.setChecked(true);
							thumbnailsselection[id] = true;
							imgCnt ++;
						}*/
						
					} 
				}
			});
			
			holder.item_img.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					
					int id = v.getId();
					Log.d(TAG, "onClick(item_img) - items Checkdate : ["+id+"] "+items.get(id).getCheckdate());
					
					
					Intent intent;
					intent = new Intent(CheckListActivity2.this, CheckImageActivity.class);
					intent.putExtra("ITEM_CHECKDATE", items.get(id).getCheckdate());
					//startActivity(intent);
					startActivityForResult(intent, CHECKIMAGE_REQUEST_CODE);
					
				}
			});
			
			holder.item_select.setChecked(thumbnailsselection[position]);
			holder.item_img.setImageBitmap(myBitmap);
			holder.item_count.setText(items.get(position).getCount());
			holder.item_dogong.setText(items.get(position).getDogong());
			holder.item_content.setText(items.get(position).getContent());
			if("Y".equals(items.get(position).getSendyn())){
				holder.item_dogong.setTextColor(Color.parseColor("#0054FF"));//????????????
				holder.item_content.setTextColor(Color.parseColor("#0054FF"));//????????????
			}else if("N".equals(items.get(position).getSendyn())){
				holder.item_dogong.setTextColor(Color.parseColor("#ff0000"));//?????????
				holder.item_content.setTextColor(Color.parseColor("#ff0000"));//?????????
			}
			return convertView;
		}
	}

	
	
	/** ******************************************************************************* **/
	
	
	public static int CHECKIMAGE_REQUEST_CODE = 1000;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == CHECKIMAGE_REQUEST_CODE){
			saveData();
		}
	}
	private void makeDialog(){
		AlertDialog.Builder ad = new AlertDialog.Builder(CheckListActivity2.this);
		ad.setMessage("")
				.setTitle("??????")
				.setMessage("* ???????????? ?????? 5????????? ?????? ???????????????.")
				.setCancelable(false)
				.setPositiveButton("??????",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,int which) {
							}
						});
		ad.show();
	}

	
	/** ******************************************************************************* **/
	Handler fileHandler;
	public void executeJob(final Parameters params, Activity mActivity){
		Log.d(TAG, "******************** executeJob()~! ********************");
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
			Toast.makeText(CheckListActivity2.this, R.string.no_data, Toast.LENGTH_SHORT).show();
		}
		
		Log.d(TAG, "executeJob() - END *****");
	}
	
	
	double totalFileSize = 0.0;
	public List<ImageJgInfo> ReadSDCard(){
		Log.d(TAG, "******************** ReadSDCard()~! ********************");
		Cursor cursor = db.selectSmart_DATAINFO_ONE();
		File[] files = new File[cursor.getCount()];
		
		Log.d(TAG,  "ReadSDCard() - cursor.count : "+cursor.getCount());
		List<ImageJgInfo> trnImageList = new ArrayList<ImageJgInfo>();
		
		for(int i=0; i<cursor.getCount(); i++){
			cursor.moveToPosition(i);
			ImageJgInfo item = new ImageJgInfo();
			JbDataInfo item_sub = new JbDataInfo();
			
			Log.d(TAG, "ReadSDCard() - i : "+i);
			Log.d(TAG,"ReadSDCard() - JeongbiGB : "+cursor.getString(0));		//[2]??????
			Log.d(TAG,"ReadSDCard() - ItemGB : "+cursor.getString(1));			//[6]????????????
			Log.d(TAG,"ReadSDCard() - DetailGB : "+cursor.getString(2));		//?????????
			Log.d(TAG,"ReadSDCard() - CONTENT : "+cursor.getString(3));		//????????????/?????????(?????? ???)
			Log.d(TAG,"ReadSDCard() - FILENAME : "+cursor.getString(4));		//????????????
			Log.d(TAG,"ReadSDCard() - JBCODE : "+cursor.getString(5));			//????????????
			Log.d(TAG,"ReadSDCard() - JBMYEONG : "+cursor.getString(6));		//?????????
			Log.d(TAG,"ReadSDCard() - DRNO : "+cursor.getString(7));			//????????????
			Log.d(TAG,"ReadSDCard() - NAME : "+cursor.getString(8));			// ???????????????(??????34???3522)
			Log.d(TAG,"ReadSDCard() - DOGONG : "+cursor.getString(9));			//????????????
			Log.d(TAG,"ReadSDCard() - DATE : "+cursor.getString(10));			//????????????
			Log.d(TAG,"ReadSDCard() - SENDYN : "+cursor.getString(11));			//???????????? ??????(Y, N)
			Log.d(TAG,"ReadSDCard() - SWBEONHO : "+cursor.getString(12));		//????????????
			Log.d(TAG,"ReadSDCard() - BONBUCODE : "+cursor.getString(13));	
			Log.d(TAG,"ReadSDCard() - JISACODE : "+cursor.getString(14));		//????????????
			Log.d(TAG,"ReadSDCard() - TAGYN : "+cursor.getString(15));			//Y
			Log.d(TAG,"ReadSDCard() - SWNAME : "+cursor.getString(16));			//??????(?????????)
			Log.d(TAG,"ReadSDCard() - CHECKDATE : "+cursor.getString(17));		//????????????(SMART_DATAINFO??? ????????? insert ??? ??????)

			item_sub.setJangbicode(cursor.getString(5));  // ????????????
			item_sub.setDogong(cursor.getString(9));  // ????????????
			//item_sub.setfilename(cursor.getString(4));  // ????????????
			item_sub.setImgPath(cursor.getString(4)); //????????????
			item_sub.setDate(cursor.getString(10));	//????????????
			item_sub.setJangbimyeong(cursor.getString(8));  // ????????? name
			item_sub.setContent(cursor.getString(3));  // ??????  content
			item_sub.setJisacode(cursor.getString(14));  // ????????????
			item_sub.setSwbeonho(cursor.getString(12));  // ????????????
			item_sub.setTagyn(cursor.getString(15));  // ????????????
			item_sub.setSwname(cursor.getString(16));  // ??????
			item_sub.setCheckdate(cursor.getString(17));  // ??????
			
			String jeongbiGB = cursor.getString(0);
			String itemGB = cursor.getString(1);
			Log.d(TAG, "ReadSDCard() - jeongbiGB : "+jeongbiGB);
			Log.d(TAG, "ReadSDCard() - itemGB : "+itemGB);
			item_sub.setGubun(jeongbiGB.substring(jeongbiGB.indexOf("[")+1, jeongbiGB.indexOf("]")));	//???????????? gubun ??????
			item_sub.setBupumcode(itemGB.substring(itemGB.indexOf("[")+1, itemGB.indexOf("]")));	//???????????? bupumcode ??????
			
			item.setJbDataInfo(item_sub);
			
			
			File file = new File(cursor.getString(4));
			
			String fileName[] = Common.split(file.getPath().toString(), "/");
			String fileType[] = Common.split(file.getPath().toString(), ".");

			item.setFilePath(file.getPath().toString());
			item.setFileName(fileName[fileName.length - 1]);
			item.setFileType(fileType[fileType.length - 1]);
			item.setFileSize(file.length());
			
			Log.d(TAG, "ReadSDCard() - getAbsolutePath : " + file.getAbsolutePath());
			Log.d(TAG, "ReadSDCard() - fileName : " + fileName[fileName.length - 1]);
			// ??????????????? ??? ?????? ???????????? ????????? 1??????, ?????????, MB ??????

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
				Log.d(TAG, "ReadSdCard() - ##sendItems ImgPath : "+sendItems.get(j).getImgPath());
				Log.d(TAG, "ReadSdCard() - ##sendItems CheckDate : "+sendItems.get(j).getCheckdate());
				//Log.d(TAG, "ReadSdCard() - sendItems Dogong : "+sendItems.get(j).getdogongcode());
				if(sendItems.get(j).getImgPath().equals(cursor.getString(4))){
					if(sendItems.get(j).getCheckdate().equals(cursor.getString(17))){
						Log.d(TAG, "ReadSdCard() - &&ImgPath : "+cursor.getString(4));
						Log.d(TAG, "ReadSdCard() - &&Dogong : "+cursor.getString(9));
						trnImageList.add(item);
					}
				}
				Log.d(TAG, "ReadSdCard() - sendItems ----------------------");
			}
			
			Log.d(TAG, "ReadSDCard() - $$$$$ item getJbDataInfo Dogong : " + item.getJbDataInfo().getDogong());
			Log.d(TAG, "ReadSDCard() - $$$$$ item getJbDataInfo CheckDate : " + item.getJbDataInfo().getCheckdate());
		}
		
		Log.d("", "ReadSdCard list size = " + trnImageList.size());
		for(int k=0; k<trnImageList.size(); k++){
			Log.d(TAG, "ReadSdCard() - trnImageList DATE ::::::: "+trnImageList.get(k).getJbDataInfo().getDate());
			Log.d(TAG, "ReadSdCard() - trnImageList DATE ::::::: "+trnImageList.get(k).getJbDataInfo().getDate());
			Log.d(TAG, "ReadSdCard() - trnImageList ::::::: "+trnImageList.get(k).getJbDataInfo().getCheckdate());
		}
		
		Log.d("", "ReadSdCard trnImageList = " + trnImageList.toString());
		return trnImageList;
	}
	
	
	
	
	/** ******************************************************************************* **/
	
	//==========================================================================================================
	
		/************************************************************************
		 * ?????? ?????? ?????? - Progressbar, AsyncTask??? ???????????? ?????? ?????? - ???????????? ??? ???????????? ?????? ??????
		 ************************************************************************/
		String sendCheckFile;
		String transData = "Y";
		CustomMultiPartEntity multipartContent;

		public int iPercent;

		public class DoComplecatedJob extends AsyncTask<String, Integer, Long> {
			long totalSize;
			long totalSizeKB;

			// ?????? ?????? Progressbar
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
				// #region ???????????? ??????

				progressDialog = new ProgressDialog(mActivity);
				progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				progressDialog.setMessage("?????? ?????? ???...");
				progressDialog.setCancelable(true);

				progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "????????????",
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
				tvPromptProgress.setText("????????? ????????? ?????????.");
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

				// ?????? ?????? ?????????
				tv.setText("0 %");
				tvTrans.setText("0 KB");
				tvTot.setText("0 KB");

				// ?????? ?????? ?????????
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
				// #region ??? ???????????? ??????, ???????????? ??????????????? ??? ???????????? ?????? ??????
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

						// ?????? ?????? ??? ??????

						ImageJgInfo image = mFileList.get(i);
						Log.d(TAG, "doInBackground() - image JbDataInfo : "+mFileList.get(i).getJbDataInfo().getCheckdate());
						absolutePath = image.getFilePath().toString();
						Log.d("", "send filepath name = " + absolutePath);
						multipartContent.addPart("uploaded_file" + i, new FileBody(new File(absolutePath)));
						Log.d("", "send filepath name !!!!! 1111111111");
						totalSize = multipartContent.getContentLength();
						Log.d("", "send filepath name !!!!! 2222222222");
						totalSizeKB = totalSize / 1024;
						// pb.setMax(Integer.parseInt(totalSize + ""));
						Log.d("", "send filepath name !!!!! 3333333333");
						// Send it
						multipartContent.addPart("renamePrefix", new StringBody(""));
						Log.d("", "send filepath name !!!!! 4444444444");
						
						Log.d(TAG,"Multipartcontent SURIL!! = "+image.getJbDataInfo().getDate());     							// ?????????
						Log.d(TAG,"Multipartcontent SURIL = "+image.getJbDataInfo().getDate().replace(".", "").substring(0, 8));	// ?????????
						Log.d(TAG,"Multipartcontent JANGBICODE = "+Common.nullCheck(image.getJbDataInfo().getJangbicode()));			// ????????????
						Log.d(TAG,"Multipartcontent SURIDATE = "+image.getJbDataInfo().getDate().substring(0, 16));				// ????????????
						Log.d(TAG,"Multipartcontent JANGBIMYEONG = "+Common.nullCheck(image.getJbDataInfo().getJangbimyeong())); 		// ?????????
						Log.d(TAG,"Multipartcontent DOGONGBEONHO = "+Common.nullCheck(image.getJbDataInfo().getDogong()));   		// ????????????
						Log.d(TAG,"Multipartcontent CONTENT = "+image.getJbDataInfo().getContent());  								// ????????????
						Log.d(TAG,"Multipartcontent IMGAGE = "+image.getFileName().toString());									// ????????????
						Log.d(TAG,"Multipartcontent BUSEOCODE = "+image.getJbDataInfo().getJisacode());
						Log.d(TAG,"Multipartcontent GUBUN = "+image.getJbDataInfo().getGubun());
						Log.d(TAG,"Multipartcontent BUPUMCODE = "+image.getJbDataInfo().getBupumcode());
						Log.d(TAG,"Multipartcontent SWBEONHO = "+image.getJbDataInfo().getSwbeonho());
						Log.d(TAG,"Multipartcontent CHECKDATE = "+image.getJbDataInfo().getCheckdate());
						Log.d(TAG,"Multipartcontent SENDDATE = "+Common.getCalendarDateYMDHM());
						
						String tempDate = image.getJbDataInfo().getDate();
						String year = tempDate.substring(0,4);
						String month = tempDate.substring(4,6);
						String day = tempDate.substring(6,8);
						String hh = tempDate.substring(8,10);
						String mm = tempDate.substring(10,12);
						String ss = tempDate.substring(12,14);
						
						multipartContent.addPart("SURIL",new StringBody(image.getJbDataInfo().getDate().replace(".", "").substring(0, 8), Charset.forName("UTF-8")));
						multipartContent.addPart("JANGBICODE",new StringBody(Common.nullCheck(image.getJbDataInfo().getJangbicode()), Charset.forName("UTF-8")));
						multipartContent.addPart("SURIDATE",new StringBody(image.getJbDataInfo().getDate().substring(0, 16), Charset.forName("UTF-8")));
						multipartContent.addPart("JANGBIMYEONG",new StringBody(Common.nullCheck(image.getJbDataInfo().getJangbimyeong()), Charset.forName("UTF-8")));
						multipartContent.addPart("DOGONGBEONHO",new StringBody(Common.nullCheck(image.getJbDataInfo().getDogong()), Charset.forName("UTF-8")));
						multipartContent.addPart("CONTENT",new StringBody(image.getJbDataInfo().getContent(), Charset.forName("UTF-8")));
						multipartContent.addPart("IMGAGE",new StringBody(image.getFileName().toString(), Charset.forName("UTF-8")));
						multipartContent.addPart("BUSEOCODE",new StringBody(image.getJbDataInfo().getJisacode(), Charset.forName("UTF-8")));
						multipartContent.addPart("GUBUN",new StringBody(image.getJbDataInfo().getGubun(), Charset.forName("UTF-8")));
						multipartContent.addPart("BUPUMCODE",new StringBody(image.getJbDataInfo().getBupumcode(), Charset.forName("UTF-8")));
						multipartContent.addPart("SWBEONHO",new StringBody(image.getJbDataInfo().getSwbeonho(), Charset.forName("UTF-8")));
						multipartContent.addPart("CHECKDATE",new StringBody(image.getJbDataInfo().getCheckdate(), Charset.forName("UTF-8")));
						multipartContent.addPart("SENDDATE",new StringBody(Common.getCalendarDateYMDHM(), Charset.forName("UTF-8")));
						
						multipartContent.addPart("legacyParam", new StringBody(
								"?SURIL="+URLEncoder.encode(image.getJbDataInfo().getDate().replace(".", "").substring(0, 8),"UTF-8")+
								"&JANGBICODE="+URLEncoder.encode(Common.nullCheck(image.getJbDataInfo().getJangbicode()),"UTF-8")+
								"&SURIDATE="+URLEncoder.encode(image.getJbDataInfo().getDate().substring(0, 16),"UTF-8")+
								"&JANGBIMYEONG="+URLEncoder.encode(Common.nullCheck(image.getJbDataInfo().getJangbimyeong()), "UTF-8")+
								"&DOGONGBEONHO="+URLEncoder.encode(Common.nullCheck(image.getJbDataInfo().getDogong()),"UTF-8")+
								"&CONTENT="+URLEncoder.encode(image.getJbDataInfo().getContent(),"UTF-8")+
								"&IMGAGE="+URLEncoder.encode(image.getFileName().toString(),"UTF-8")+
								"&BUSEOCODE="+URLEncoder.encode(image.getJbDataInfo().getJisacode(),"UTF-8")+
								"&GUBUN="+URLEncoder.encode(image.getJbDataInfo().getGubun(),"UTF-8")+
								"&BUPUMCODE="+URLEncoder.encode(image.getJbDataInfo().getBupumcode(),"UTF-8")
								//"&CHECKDATE="+URLEncoder.encode(image.getJbDataInfo().getCheckdate(),"UTF-8")
								
								));
						
						Log.d(TAG, "multipartContent===================="+multipartContent.toString());
						
						httpPost.setEntity(multipartContent);
						
						// multipartContent.addPart("renamePrefix", new

						// ????????? ?????? ??????
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
							//???????????? ??????
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
				// #region ???????????? ?????????????????? ????????????

				pb.setProgress((int) (progress[0]));

				// thread ??????
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

//					tvTrans.setText(df.format(iProgress * totalSizeKB / 100) + " KB");
//					tvTot.setText(df.format(totalSizeKB) + " KB");
					tvTrans.setText(fileCnt+"???");
					tvTot.setText(mFileList.size()+"???");
				}// end if

				// #endregion
				// --------------------------------------------------------------------------------------------
			}
			
			@Override
			protected void onPostExecute(Long result) {
				Log.d(TAG, "onPostExecute()~!");
				// --------------------------------------------------------------------------------------------
				// #region ???????????? ?????? ?????? ??? ???????????? ?????? ?????? ??????

				progressDialog.dismiss();
				multipartContent = null;
				mDialog.dismiss();

				Log.d("ProgressUpdate", "onPostExecute() - transData : " + transData);
				Log.d("ProgressUpdate", "onPostExecute() - transData : " + result);
				Log.d("", "onPostExecute() - sendCheckFile : " + sendCheckFile);
				Log.d("", "onPostExecute() - multipartContent : " + multipartContent);
				Log.d("", "onPostExecute() - iPercent : " + iPercent);
				if (multipartContent != null || (iPercent < 100 && iPercent > 0)) {
//					openWarnDialog(mActivity, "?????? ????????? ??????????????????.");
					Toast.makeText(CheckListActivity2.this, "?????? ????????? ??????????????????.", Toast.LENGTH_LONG).show();
				}else{
//					openWarnDialog(mActivity, "?????? ?????? ??????");
					if("VPNFAIL".equals(sendCheckFile)){
						Log.d("onPostExecute", "VPNFAIL - ???????????? ?????????????????? ");
						AlertDialog.Builder adbLoc	= new AlertDialog.Builder(CheckListActivity2.this);
						adbLoc.setCancelable(false);
						adbLoc.setTitle("?????????????????????"); 
						adbLoc.setMessage("???????????? ?????? ?????? ??? ??????????????? ????????????.");
						adbLoc.setPositiveButton("??????", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						});
						adbLoc.setNegativeButton("??????????????????", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								//Intent intent = getPackageManager().getLaunchIntentForPackage("com.aircuve.mcuvic");
								Intent intent = getPackageManager().getLaunchIntentForPackage("com.ex.group.folder");
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				                startActivity(intent);
							}
						});
						adbLoc.show();
					}else if("FAIL".equals(sendCheckFile)){
						Toast.makeText(CheckListActivity2.this, "?????? ????????? ??????????????????.\n message:Fail("+sendCheckFile+")", Toast.LENGTH_LONG).show();
					}else if("".equals(Common.nullCheck(sendCheckFile))){
						Toast.makeText(CheckListActivity2.this, "?????? ????????? ??????????????????.\n message:null("+sendCheckFile+")", Toast.LENGTH_LONG).show();
					}else if("<resultcode>1000</resultcode>".equals(sendCheckFile)){
						Toast.makeText(CheckListActivity2.this, "?????? ?????? ??????", Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(CheckListActivity2.this, "?????? ????????? ??????????????????.\n message:etc("+sendCheckFile+")", Toast.LENGTH_LONG).show();
					}

				}
				
				// ?????? ?????? ?????? ?????????.
				listView.clearChoices() ;
	            adapter.notifyDataSetChanged();
				saveData();
				
//				Common.DeleteDir(Common.FILE_DIR);
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
