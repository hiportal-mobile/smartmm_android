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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class AlbumListActivity extends Activity implements OnClickListener, OnItemClickListener {
	
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
				//Toast.makeText(getApplicationContext(), position+" 클릭함", Toast.LENGTH_LONG).show();
				
				Intent intent = new Intent(AlbumListActivity.this, PhotoListActivity.class);
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
			AlbumListActivity.this.finish();
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
		
		for(int i=0; i<fileList.length; i++){
			Log.d(TAG, "getFileListInfo() - i : "+i);
			Log.d(TAG, "getFileListInfo() - list getName : "+fileList[i].getName());
			Log.d(TAG, "getFileListInfo() - list path : "+fileList[i].getPath());
			
			if(fileList[i].getName().equals("DCIM")){
				AlbumVO item = new AlbumVO();
				AlbumVO imgInfo = getImageExistYN(fileList[i].getPath()+"/Camera");
				item.setImg(imgInfo.getImg());
				item.setImgTotal(imgInfo.getImgTotal());
				item.setName("Camera");
				item.setPath(fileList[i].getPath()+"/Camera");
				
				albumList.add(item);
				
				//screenshot 존재여부
				File file3 = new File(fileList[i].getPath());
				File dcim_list[] = file3.listFiles();
				for(int j=0; j<dcim_list.length; j++){
					Log.d(TAG, "getFileListInfo() - dcim_list path : "+dcim_list[j].getPath());
					if(dcim_list[j].getName().contains("Screenshot")){
						AlbumVO item2 = new AlbumVO();
						AlbumVO Screenshots_imgInfo = getImageExistYN(fileList[i].getPath()+"/Screenshots");
						item2.setImg(Screenshots_imgInfo.getImg());
						item2.setImgTotal(Screenshots_imgInfo.getImgTotal());
						item2.setName("Screenshots");
						item2.setPath(fileList[i].getPath()+"/Screenshots");
						albumList.add(item2);
					}
				}
				
			}else if(fileList[i].getName().equals("Download")){
				AlbumVO item = new AlbumVO();
				AlbumVO imgInfo = getImageExistYN(fileList[i].getPath());
				item.setImg(imgInfo.getImg());
				item.setImgTotal(imgInfo.getImgTotal());
				item.setName("Download");
				item.setPath(fileList[i].getPath());
				
				albumList.add(item);
				
			}else if(fileList[i].getName().equals("Pictures")){
				AlbumVO item = new AlbumVO();
				AlbumVO imgInfo = getImageExistYN(fileList[i].getPath());
				item.setImg(imgInfo.getImg());
				item.setImgTotal(imgInfo.getImgTotal());
				item.setName("Pictures");
				item.setPath(fileList[i].getPath());
				
				albumList.add(item);
				
				//screenshot 존재여부
				File file3 = new File(fileList[i].getPath());
				File dcim_list[] = file3.listFiles();
				for(int j=0; j<dcim_list.length; j++){
					Log.d(TAG, "getFileListInfo() - dcim_list path : "+dcim_list[j].getPath());
					if(dcim_list[j].getName().contains("Screenshot")){
						AlbumVO item2 = new AlbumVO();
						AlbumVO Screenshots_imgInfo = getImageExistYN(fileList[i].getPath()+"/Screenshots");
						item2.setImg(Screenshots_imgInfo.getImg());
						item2.setImgTotal(Screenshots_imgInfo.getImgTotal());
						item2.setName("Screenshots");
						item2.setPath(fileList[i].getPath()+"/Screenshots");
						albumList.add(item2);
					}
				}
				
			}else if(fileList[i].getName().equals("SMARTMM")){
				AlbumVO item = new AlbumVO();
				AlbumVO imgInfo = getImageExistYN(Configuration.dirRoot_kr);
				item.setImg(imgInfo.getImg());
				item.setImgTotal(imgInfo.getImgTotal());
				item.setName("JEONGBI");
				item.setPath(Configuration.dirRoot_kr);
				
				albumList.add(item);
			}else{
				Log.d(TAG, "getFileListInfo() - ELSE ++++++++++++++++++++");
				Log.d(TAG, "getFileListInfo() - file :: "+fileList[i].getPath());
				file1 = new File(fileList[i].getPath());
				String[] imgList = file1.list(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String filename) {
						// TODO Auto-generated method stub
						Boolean bOk = false;
						if(filename.toLowerCase().endsWith(".png")){ bOk = true;
						}else if(filename.toLowerCase().endsWith(".9.png")){ bOk = true;
						}else if(filename.toLowerCase().endsWith(".gif")){ bOk = true;
						}else if(filename.toLowerCase().endsWith(".jpg")){ bOk = true;
						}else if(filename.toLowerCase().endsWith(".jpeg")){ bOk = true;
						}else{ bOk = false;
						}
						return bOk;
					}
				});
				Log.d(TAG, "getFileListInfo() - imgList Length : "+imgList.length);
				if(imgList.length > 0){
					AlbumVO item = new AlbumVO();
					Log.d(TAG, "getFileListInfo() - imgList : "+imgList.toString());
					Log.d(TAG, "getFileListInfo() - ### fileList getName : "+fileList[i].getName());
					Log.d(TAG, "getFileListInfo() - ### fileList getPath : "+fileList[i].getPath());

					AlbumVO imgInfo = getImageExistYN(fileList[i].getPath());
					item.setImg(imgInfo.getImg());
					item.setImgTotal(imgInfo.getImgTotal());
					item.setName(fileList[i].getName());
					item.setPath(fileList[i].getPath());

					Log.d(TAG, "getFileListInfo() - %%% getImg : "+item.getImg());
					Log.d(TAG, "getFileListInfo() - %%% getImgTotal : "+item.getImgTotal());
					albumList.add(item);

				}
			}
		}
	}
	
	
	public AlbumVO getImageExistYN(String path){
		Log.d(TAG, "getImageExistYN()~!");
		Log.d(TAG, "getImageExistYN() - path : "+path);
		List<String> imgDetailList = new ArrayList<String>();
		AlbumVO imgListInfo = new AlbumVO();
		
		file2 = new File(path);
		File imgDetailFileList[] = file2.listFiles();
		
		Log.d(TAG, "getImageExistYN() - imgDetailList Length : "+imgDetailFileList.length);
		
		//해당경로에 폴더 및 이미지가 존재하는 경우
		if(imgDetailFileList.length > 0){
			Log.d(TAG, "getImageExistYN() - imgDetailList Length Exist ");
			for(int j=0; j<imgDetailFileList.length; j++){
				Log.d(TAG, "getImageExistYN() - imgDetailList Length Exist222 ");
				if(imgDetailFileList[j].getName().toLowerCase().endsWith(".png") ||
						imgDetailFileList[j].getName().toLowerCase().endsWith(".9.png") ||
						imgDetailFileList[j].getName().toLowerCase().endsWith(".gif") ||
						imgDetailFileList[j].getName().toLowerCase().endsWith(".jpg") ||
						imgDetailFileList[j].getName().toLowerCase().endsWith(".jpeg")){
					AlbumVO item = new AlbumVO();
					Log.d(TAG, "getImageExistYN() - imgDetailList Path : ["+j+"] "+imgDetailFileList[j].getPath());
					Log.d(TAG, "getImageExistYN() - imgDetailList Name : ["+j+"] "+imgDetailFileList[j].getName());

					imgDetailList.add(imgDetailFileList[j].getPath());
				}else{
				}
			}
			
			//해당경로에 이미지가 존재
			if(imgDetailList.size() > 0){
				Log.d(TAG, "getImageExistYN() - 해당경로에 이미지가 존재 imgDetailList Size : "+imgDetailList.size());
				imgListInfo.setImg(imgDetailList.get(0));
				imgListInfo.setImgTotal(String.valueOf(imgDetailList.size()));
			
			//해당폴더에 이미지 존재X
			}else{
				Log.d(TAG, "getImageExistYN() - 해당폴더에 이미지 존재X imgDetailList Size : "+imgDetailList.size());
				imgListInfo.setImg("");
				imgListInfo.setImgTotal(String.valueOf(imgDetailList.size()));
			}
			
		//해당경로에 아무것도 존재 X 
		}else{
			Log.d(TAG, "getImageExistYN() - 해당경로에 아무것도 존재 X imgDetailList Size : "+imgDetailList.size());
			imgListInfo.setImg("");
			imgListInfo.setImgTotal(String.valueOf(imgDetailList.size()));
		}
		
		
		
		//imgListInfo.setImgInfo(imgDetailList);
		
		return imgListInfo;
	}

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
			
			Log.d(TAG, "getView() - position : "+position);
			Log.d(TAG, "getView() - albumList Path : "+albumList.get(position).getPath());
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
			
			Log.d(TAG, "getView() - albumList Img : "+albumList.get(position).getImg());
			if(!"0".equals(albumList.get(position).getImgTotal())){
				Log.d(TAG, "getView() - albumList mgTotal : "+albumList.get(position).getImgTotal());
				Bitmap myBitmap = BitmapFactory.decodeFile(albumList.get(position).getImg());
				
				holder.imageView.setImageBitmap(myBitmap);
				
			}
			
			holder.textView.setText(albumList.get(position).getName());
			holder.imgTotal.setText(albumList.get(position).getImgTotal());
			
			return convertView;
		}
		
		
	}

}
