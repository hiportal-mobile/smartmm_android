package com.ex.smartmm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ex.smartmm.common.Common;
import com.ex.smartmm.common.DBAdapter_checklist;
import com.ex.smartmm.vo.AlbumVO;
import com.ex.smartmm.vo.JbDataInfo;
import com.ex.smartmm.vo.JbInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CheckImageActivity extends Activity implements OnClickListener{
	
	public static final String TAG = "CheckImageActivity";
	
	Common common;
	DBAdapter_checklist db;
	
	ImageView btn_back;
	ListView listView;
	ImageAdapter adapter;
	
	String ITEM_CHECKDATE = "";
	
	//List<JbInfo> items = new ArrayList<JbInfo>();
	List<JbDataInfo> items = new ArrayList<JbDataInfo>();
	List<AlbumVO> imageList;
	File file;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onCreate()~!");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_checkimage);
		
		common = new Common(CheckImageActivity.this);
		db = new DBAdapter_checklist();
		db.close();
		db.init();
		
		Intent acceptIntent = getIntent();
		ITEM_CHECKDATE = acceptIntent.getStringExtra("ITEM_CHECKDATE");
		Log.d(TAG, "onCreate() - ITEM_CHECKDATE : "+ITEM_CHECKDATE);
		
		btn_back = (ImageView)findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		
		selectImageList();
		
		
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
	
	
	public void selectImageList(){
		Log.d(TAG, "selectImageList()~!");
		items.clear();
		Cursor cursor = db.selectSmart_DATAINFO_Image(ITEM_CHECKDATE);
		
		while(cursor.moveToNext()){
			int i = cursor.getPosition();
			Log.d(TAG, "selectImageList() - i : "+i);
			
			//JbInfo item = new JbInfo();
			JbDataInfo item = new JbDataInfo();
			Log.d(TAG,"selectImageList() - FILENAME : "+cursor.getString(0));
			Log.d(TAG,"selectImageList() - SURIDATE : "+cursor.getString(1));
			Log.d(TAG,"selectImageList() - CHECKDATE : "+cursor.getString(2));
			
			item.setImgPath(cursor.getString(0));
			item.setDate(cursor.getString(1));
			item.setCheckdate(cursor.getString(2));

			file = new File(item.getImgPath());
			Log.d(TAG, "selectImageList() - file Name : "+file.getName());
			Log.d(TAG, "selectImageList() - file Path : "+file.getPath());
			//item.setfilename(file.getName());
			item.setImgName(file.getName());
			item.setImgPath(file.getPath());
			items.add(item);
			
			
			
		}
		
		Log.d(TAG, "selectImageList() - items Size : "+items.size());
		
		showImageList();
	}
	
	/*public void getImageInfo(){
		Log.d(TAG, "getImageInfo()~!");
		
		imageList = new ArrayList<AlbumVO>();
		
		file = new File(ITEM_CHECKDATE);
		
		Log.d(TAG, "getImageInfo()");
		file.getPath()
		
		
	}*/
	
	public void showImageList(){
		Log.d(TAG, "showImageList()~!");
		
		listView = (ListView)findViewById(R.id.listView);
		adapter = new ImageAdapter();
		listView.setAdapter(adapter);
	}
	
	public void deleteItem(String imgPath){
		Log.d(TAG, "deleteItem()~!");
		Log.d(TAG, "deleteItem() - imgPath : "+imgPath);
		db.deleteSmart_DATAINFO_ONE(imgPath);
		selectImageList();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;

		default:
			break;
		}
	}
	
	
	/** *************************************************************************** **/
	class ViewHolder{
		ImageView imageView;
		TextView textView;
		ImageView btn_del;
		int id;
	}
	public class ImageAdapter extends BaseAdapter{
		LayoutInflater inflater;
		
		public ImageAdapter(){
			Log.d(TAG, "******************** ImageAdapter CLASS ********************");
			inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
			
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
			Log.d(TAG, "getView() - position : "+position);
			Log.d(TAG, "getView() - position : "+items.get(position).getImgPath());
			Log.d(TAG, "getView() - position : "+items.get(position).getImgName());
			Bitmap myBitmap = BitmapFactory.decodeFile(items.get(position).getImgPath());
			
			ViewHolder holder = null;
			if(convertView == null){
				holder = new ViewHolder();
				inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.item_checkimage, null);
				
				holder.imageView = (ImageView)convertView.findViewById(R.id.imageView);	
				holder.textView = (TextView)convertView.findViewById(R.id.textView);
				holder.btn_del = (ImageView)convertView.findViewById(R.id.btn_del);
				convertView.setTag(holder);
				
			}else{
				holder = (ViewHolder)convertView.getTag();
			}
			
			holder.imageView.setId(position);
			holder.textView.setId(position);
			holder.btn_del.setId(position);
			
			holder.imageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int id = v.getId();
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.parse("file://"+items.get(id).getImgPath()), "image/*");
					startActivity(intent);
				}
			});
			
			holder.btn_del.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int id = v.getId();
					final String imgPath = items.get(id).getImgPath();
					Log.d(TAG, "onClick(bnt_del) - imgPath : "+imgPath);
					AlertDialog.Builder adbLoc = new AlertDialog.Builder(CheckImageActivity.this);
					adbLoc.setCancelable(false);
					adbLoc.setTitle("스마트정비관리");
					adbLoc.setMessage("삭제 하시겠습니까?");
					adbLoc.setPositiveButton("예", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							
							deleteItem(imgPath);
						}
					});
					adbLoc.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
						}
					});
					adbLoc.show();
					
					
					
				}
			});
			
			holder.imageView.setImageBitmap(myBitmap);
			holder.textView.setText(items.get(position).getImgName());
			return convertView;
		}
		
	}

}
