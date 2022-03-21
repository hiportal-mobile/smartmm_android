package com.ex.smartmm;


import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import com.ex.smartmm.common.Configuration;
import com.ex.smartmm.vo.AlbumVO;
import com.ex.smartmm.vo.JbInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AlbumListActivity_bak extends Activity implements OnClickListener, OnItemClickListener {
	
	public static final String TAG = "AlbumListActivity";
	
	ListView listView;
	ImageView btn_back;
	ImageAdapter adapter;
	ArrayList<String> thumbsDataList = new ArrayList<String>();
    ArrayList<String> thumbsIDList = new ArrayList<String>();
    
    File file, file1, file2;
	ArrayList myList;
	
	List<AlbumVO> albumList = new ArrayList<AlbumVO>();
	
	String SELECTED_JBCODE = "";
	String SELECTED_JBMYEONG = "";
	String SELECTED_DRNO = "";
	String SELECTED_DOGONG = "";
	String SELECTED_SWBEONHO = "";
	String SELECTED_BSCODE = "";
	String SELECTED_BUSEOCODE = "";
	String SELECTED_JEONGBIGUBUNCODE = "";
	String SELECTED_JEONGBIGUBUN = "";
	String SELECTED_SULIITEMCODE = "";
	String SELECTED_SULIITEM = "";
	String SELECTED_DETAILITEM = "";
	String SELECTED_BEFORE_AFTER = "";
	String SELECTED_CHECK = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_albumlist);
		
		Intent acceptIntent = getIntent();
		SELECTED_JBCODE = acceptIntent.getStringExtra("SELECTED_JBCODE"); 
		SELECTED_JBMYEONG = acceptIntent.getStringExtra("SELECTED_JBMYEONG"); 
		SELECTED_JEONGBIGUBUN = acceptIntent.getStringExtra("SELECTED_JEONGBIGUBUN"); 
		SELECTED_SULIITEMCODE = acceptIntent.getStringExtra("SELECTED_SULIITEMCODE"); 
		SELECTED_SULIITEM = acceptIntent.getStringExtra("SELECTED_SULIITEM"); 
		SELECTED_DETAILITEM = acceptIntent.getStringExtra("SELECTED_DETAILITEM"); 
		SELECTED_BEFORE_AFTER = acceptIntent.getStringExtra("SELECTED_BEFORE_AFTER"); 
		SELECTED_DRNO = acceptIntent.getStringExtra("SELECTED_DRNO"); 
		SELECTED_DOGONG = acceptIntent.getStringExtra("SELECTED_DOGONG"); 
		SELECTED_SWBEONHO = acceptIntent.getStringExtra("SELECTED_SWBEONHO"); 
		SELECTED_BSCODE = acceptIntent.getStringExtra("SELECTED_BSCODE"); 
		SELECTED_BUSEOCODE = acceptIntent.getStringExtra("SELECTED_BUSEOCODE"); 
		SELECTED_CHECK  = acceptIntent.getStringExtra("SELECTED_CHECK"); 
		
		Log.d(TAG, "onCreate() - SELECTED_JBCODE : "+SELECTED_JBCODE);
		Log.d(TAG, "onCreate() - SELECTED_JBMYEONG : "+SELECTED_JBMYEONG);
		Log.d(TAG, "onCreate() - SELECTED_JEONGBIGUBUN : "+SELECTED_JEONGBIGUBUN);
		Log.d(TAG, "onCreate() - SELECTED_SULIITEMCODE : "+SELECTED_SULIITEMCODE);
		Log.d(TAG, "onCreate() - SELECTED_SULIITEM : "+SELECTED_SULIITEM);
		Log.d(TAG, "onCreate() - SELECTED_DETAILITEM : "+SELECTED_DETAILITEM);
		Log.d(TAG, "onCreate() - SELECTED_BEFORE_AFTER : "+SELECTED_BEFORE_AFTER);
		Log.d(TAG, "onCreate() - SELECTED_DRNO : "+SELECTED_DRNO);
		Log.d(TAG, "onCreate() - SELECTED_DOGONG : "+SELECTED_DOGONG);
		Log.d(TAG, "onCreate() - SELECTED_SWBEONHO : "+SELECTED_SWBEONHO);
		Log.d(TAG, "onCreate() - SELECTED_BSCODE : "+SELECTED_BSCODE);
		Log.d(TAG, "onCreate() - SELECTED_BUSEOCODE : "+SELECTED_BUSEOCODE);
		Log.d(TAG, "onCreate() - SELECTED_CHECK : "+SELECTED_CHECK);
		
		getFileListInfo();
		
		btn_back = (ImageView)findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		
		listView = (ListView)findViewById(R.id.listView);
		adapter = new ImageAdapter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), position+" 클릭함", Toast.LENGTH_LONG).show();
				
				Intent intent = new Intent(AlbumListActivity_bak.this, PhotoListActivity.class);
				intent.putExtra("ALBUM_NAME", albumList.get(position).getName());
				intent.putExtra("ALBUM_PATH", albumList.get(position).getPath());
				
				intent.putExtra("SELECTED_JBCODE", SELECTED_JBCODE);
				intent.putExtra("SELECTED_JBMYEONG", SELECTED_JBMYEONG);
				intent.putExtra("SELECTED_DRNO", SELECTED_DRNO);
				intent.putExtra("SELECTED_DOGONG", SELECTED_DOGONG);
				intent.putExtra("SELECTED_JEONGBIGUBUN", SELECTED_JEONGBIGUBUN);
				intent.putExtra("SELECTED_SULIITEM", SELECTED_SULIITEM);
				intent.putExtra("SELECTED_SULIITEMCODE", SELECTED_SULIITEMCODE);
				intent.putExtra("SELECTED_DETAILITEM", SELECTED_DETAILITEM);
				intent.putExtra("SELECTED_BEFORE_AFTER", SELECTED_BEFORE_AFTER);
				intent.putExtra("SELECTED_BUSEOCODE", SELECTED_BUSEOCODE);
				intent.putExtra("SELECTED_BSCODE", SELECTED_BSCODE);
				intent.putExtra("SELECTED_JEONGBIGUBUNCODE", SELECTED_JEONGBIGUBUNCODE);
				intent.putExtra("SELECTED_SWBEONHO", SELECTED_SWBEONHO);
				
				//startActivity(intent);
				startActivityForResult(intent, PHOTO_REQUEST_CODE);
			}
		});
	}
	
	public static int PHOTO_REQUEST_CODE = 1000;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode==PHOTO_REQUEST_CODE){
			AlbumListActivity_bak.this.finish();
		}
	}
	
	public void getFileListInfo(){
		myList = new ArrayList();
		albumList = new ArrayList<AlbumVO>();
		
		String rootSD = Environment.getExternalStorageDirectory().toString();
		Log.d(TAG, "getFileListInfo() - rootSD : "+rootSD);
		file = new File(rootSD);
		File fileList[] = file.listFiles();
		
		Log.d(TAG, "getFileListInfo() - fileList.length : "+fileList.length);
		for(int i = 0; i<fileList.length; i++){
			Log.d(TAG, "getFileListInfo() - i : "+i);
			Log.d(TAG, "getFileListInfo() - list getName : "+fileList[i].getName());
			Log.d(TAG, "getFileListInfo() - list path : "+fileList[i].getPath());
			file1 = new File(fileList[i].getPath());
			
			String[] imgList = file1.list(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String filename) {
					// TODO Auto-generated method stub
					Boolean bOk = false;
					if(filename.toLowerCase().endsWith(".png")){
						bOk = true;
					}else if(filename.toLowerCase().endsWith(".9.png")){
						bOk = true;
					}else if(filename.toLowerCase().endsWith(".gif")){
						bOk = true;
					}else if(filename.toLowerCase().endsWith(".jpg")){
						bOk = true;
					}else{
						bOk = false;
					}
					
					return bOk;
				}
			});
			
			if(imgList.length > 0){
				AlbumVO item = new AlbumVO();
				Log.d(TAG, "getFileListInfo() - imgList : "+imgList.toString());
				Log.d(TAG, "getFileListInfo() - ### fileList getName : "+fileList[i].getName());
				Log.d(TAG, "getFileListInfo() - ### fileList getPath : "+fileList[i].getPath());
				item.setName(fileList[i].getName());
				item.setPath(fileList[i].getPath());
				albumList.add(item);
				//Log.d(TAG, "albumList path : ["+i+"] "+albumList.get(i).getPath());
			}
			if(fileList[i].getName().equals("SMARTMM")){
				AlbumVO item = new AlbumVO();
				item.setName("JEONGBI");
				item.setPath(Configuration.dirRoot_kr);
				albumList.add(item);
			}else if(fileList[i].getName().equals("Pictures")){
				AlbumVO item = new AlbumVO();
				item.setName(fileList[i].getName());
				item.setPath(fileList[i].getPath());
				
				albumList.add(item);
			}else if(fileList[i].getName().equals("DCIM")){
				AlbumVO item = new AlbumVO();
				//item.setName(fileList[i].getName());
				//item.setPath(fileList[i].getPath());
				item.setName("Camera");
				item.setPath(fileList[i].getPath()+"/Camera");
				albumList.add(item);
				AlbumVO item2 = new AlbumVO();
				item2.setName("Screenshots");
				item2.setPath(fileList[i].getPath()+"/Screenshots");
				albumList.add(item2);
			}
		}
	}
	
	
	/*public void getImageExistYN(){
		
	}*/

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btn_back:
			finish();
			break;
		}
	}
	
	
	/** ************************************************************************************************** **/
	
	
	/** ************************************************************************************************** **/
	
	class ViewHolder{
		ImageView imageView;
		TextView textView;
		TextView imgTotal;
		int id;
	}
	
	public class ImageAdapter extends BaseAdapter {
		LayoutInflater inflater;
		
		public void ImageAdapter(){
			inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			//return items.length;
			//return myList.size();
			return albumList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			holder = new ViewHolder();
			if (convertView == null) {
	            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            convertView = inflater.inflate(R.layout.item_albumlist, null);
	            
	            holder.imageView = (ImageView)convertView.findViewById(R.id.imageView);
	            holder.textView = (TextView)convertView.findViewById(R.id.textView);
	            holder.imgTotal = (TextView)convertView.findViewById(R.id.imgTotal);
	            
	            convertView.setTag(holder);
			
			}else{
				holder = (ViewHolder)convertView.getTag();
			}
			//holder.textView.setId(position);
			//holder.itemCheckBox.setId(position);
			//holder.textView.setText(items[position]);
			//holder.textView.setText(myList.get(position).toString());
			holder.textView.setText(albumList.get(position).getName());
			return convertView;
		}
		
		
	}

}
