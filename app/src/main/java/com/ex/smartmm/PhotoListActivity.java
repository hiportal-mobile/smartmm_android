package com.ex.smartmm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ex.smartmm.common.Common;
import com.ex.smartmm.common.Configuration;
import com.ex.smartmm.common.DBAdapter_checklist;
import com.ex.smartmm.common.ImageJgInfo;
import com.ex.smartmm.vo.AlbumVO;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class PhotoListActivity extends BaseActivity implements OnClickListener{
	
	public static final String TAG = "PhotoListActivity";

	public static Activity contextActivity;
	Common common = new Common(PhotoListActivity.this);
	ImageView btn_back;
	Button btn_ok;
	GridView imageGrid;
	ImageAdapter imageadapter;
	
	String ALBUM_NAME = "";
	String ALBUM_PATH = "";
	
	File file;
	File imgFile[];
	private boolean[] thumbnailsselection;
	List<AlbumVO> photoList;
	
	int imgCnt = 0;
	
	Set<String> fileNames = new HashSet<String>();
	
	boolean TAGYN = true; //이미지 태그 사용 여부 저장
	

	
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
	
	
	String SELECTED_TAGYN = "";//태그촬영 여부 Y, N
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onCreate()~!");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photolist);
		
		new ImageLoader().execute("");
		
		Intent acceptIntent = getIntent();
		ALBUM_NAME = acceptIntent.getStringExtra("ALBUM_NAME");
		ALBUM_PATH = acceptIntent.getStringExtra("ALBUM_PATH");
		
		Log.d(TAG, "onCreate() - ALBUM_NAME : "+ALBUM_NAME);
		Log.d(TAG, "onCreate() - ALBUM_PATH : "+ALBUM_PATH);
		
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
		
		//getImageInfo();
		
		
		btn_back = (ImageView)findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		btn_ok = (Button)findViewById(R.id.btn_ok);
		btn_ok.setOnClickListener(this);
		
		/*imageGrid = (GridView)findViewById(R.id.imageGrid);
		imageadapter = new ImageAdapter();
		imageGrid.setAdapter(imageadapter);*/
	}
	
	
	public void getImageInfo(){
		Log.d(TAG, "getImageInfo()~!");
		
		photoList = new ArrayList<AlbumVO>();
		
		file = new File(ALBUM_PATH);
		File list[] = file.listFiles();
		
		Log.d(TAG, "getImageInfo() - list Length : "+list.length);
		
		for(int i=0; i<list.length; i++){
			Log.d(TAG, "getImageInfo() - i : "+i);
			Log.d(TAG, "getImageInfo() - list getName : "+list[i].getName());
			Log.d(TAG, "getImageInfo() - list path : "+list[i].getPath());
			
			
			if(list[i].getName().toLowerCase().endsWith(".png") ||
					list[i].getName().toLowerCase().endsWith(".9.png") ||
					list[i].getName().toLowerCase().endsWith(".gif") ||
					list[i].getName().toLowerCase().endsWith(".jpg") ||
					list[i].getName().toLowerCase().endsWith(".jpeg") ){
				
				AlbumVO item = new AlbumVO();
				item.setName(list[i].getName());
				item.setPath(list[i].getPath());
				photoList.add(item);
				Log.d(TAG, "getImageInfo() - item name : "+item.getName());
			}
			
			
		}
		
		thumbnailsselection = new boolean[photoList.size()];
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_back:
			finish();
			break;
			
		case R.id.btn_ok:
			final int len = thumbnailsselection.length;
			int cnt = 0;
			ArrayList<String> selectedImg = new ArrayList<String>();
			//ArrayList<AlbumVO> selectedImg = new ArrayList<AlbumVO>();
			String selectImages = "";
			for(int i=0; i<len; i++){
				if(thumbnailsselection[i]){
					cnt++;
					selectedImg.add(photoList.get(i).getPath());
				}
				Log.d(TAG, "onClick(btn_ok) - selectedImg : "+selectedImg.toString());
				Log.d(TAG, "onClick(btn_ok) - selectedImg Size : "+selectedImg.size());
			}
			
			//new ImgSaveLowPixel("", "", selectedImg).execute("");
			new SaveImgData(selectedImg).execute("");
			break;
			
		default:
			break;
		}
	}
	
	
	
	
	/**====================================================================================================**/
	
	public class ImageLoader extends AsyncTask<String, Void, String>{
		ProgressDialog progressDialog;
		
		@Override
		protected void onPreExecute() {
			Log.d(TAG, "ImageLoader CLASS > onPreExecute()~! ");
			progressDialog = ProgressDialog.show(PhotoListActivity.this, "", "이미지 로딩중...", true);
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			getImageInfo();
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			Log.d(TAG, "ImageLoader CLASS > onPostExecute()~! ");
			super.onPostExecute(result);
			
			GridView gridView = (GridView)findViewById(R.id.imageGrid);
			ImageAdapter adapter = new ImageAdapter();
			gridView.setAdapter(adapter);
			
			progressDialog.dismiss();
		}
		
	}
	
	/**====================================================================================================**/
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	/** ******************************************************************************* **/
	class ViewHolder{
		ImageView thumbImage;
		CheckBox checkbox;
		int id;
	}
	
	
	public class ImageAdapter extends BaseAdapter{
		LayoutInflater inflater;
		
		public void ImageAdapter(){
			Log.d(TAG, "******************** ImageAdapter()~! ********************");
			inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return photoList.size();
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
			Bitmap myBitmap = BitmapFactory.decodeFile(photoList.get(position).getPath());
			
			Log.d(TAG, "getView() - position : "+position);
			Log.d(TAG, "getView() - thumbnailsselection : "+thumbnailsselection[position]);
			
			ViewHolder holder = null;
			if(convertView == null){
				holder = new ViewHolder();
				inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.item_photolist, null);

				holder.thumbImage = (ImageView)convertView.findViewById(R.id.thumbImage);
				holder.checkbox = (CheckBox)convertView.findViewById(R.id.checkbox);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder)convertView.getTag();
			}
			
			holder.thumbImage.setId(position);
			holder.checkbox.setId(position);
			
			holder.checkbox.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					CheckBox cb = (CheckBox) v;
					int id = cb.getId();
					if (thumbnailsselection[id]){
						imgCnt--;
						cb.setChecked(false);
						thumbnailsselection[id] = false;
					} else {
						if(imgCnt >= 5){
							makeDialog();
							cb.setChecked(false);
							thumbnailsselection[id] = false;
						}else{
							cb.setChecked(true);
							thumbnailsselection[id] = true;
							imgCnt ++;
						}
						
					}
				}
			});
			
			holder.thumbImage.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int id = v.getId();
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.parse("file://" + photoList.get(id).getPath()), "image/*");
					startActivity(intent);
				}
			});
			
			
	    	//holder.thumbImage.setImageBitmap(thumbnails[position]);
	    	holder.thumbImage.setImageBitmap(myBitmap);
	    	holder.checkbox.setChecked(thumbnailsselection[position]);
	    	holder.id = position;
			
			return convertView;
		}
	}
	
	
	/**====================================================================================================**/
	
	class ImgSaveLowPixel extends AsyncTask<String, String, String>{
		String path = "";
		String result = "";
		ArrayList<String> selectedImg;
		
		
		public ImgSaveLowPixel(String path, String result, ArrayList<String> selectedImg){
			Log.d(TAG, "******************** ImgSaveLowPixel()~! ********************");
			this.path = path;
			this.result = result;
			this.selectedImg = selectedImg;
			Log.d(TAG, "ImgSaveLowPixel() - selectedImg : "+selectedImg);
			
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
		
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			for(int i=0; i<selectedImg.size(); i++){
				File file = new File(selectedImg.get(i));
				String tempName = file.getName();
				String ext = tempName.substring(tempName.indexOf("."));
				tempName = result+"_"+i+".jpg";
				Log.d(TAG,"ImgSaveLowPixel > doInBackground() - path : " +path);
				
				BitmapFactory.Options opt = new BitmapFactory.Options();
				long megaByte = Math.round((Math.floor(file.length() * 100 / (1024 * 1024)))/100);
				if(megaByte> 2 ){
					opt.inSampleSize = (int)megaByte;
				}else{
					opt.inSampleSize = 1;
				}
				Log.d(TAG, "ImgSaveLowPixel > doInBackground() - megaByte = " + megaByte + ":" + (int)megaByte );
				
				Bitmap originFile = BitmapFactory.decodeFile(selectedImg.get(i), opt);
				
				double height = originFile.getHeight();
				double width = originFile.getWidth();
				double calcu_height = height*(1024/width);
				Log.d(TAG, "ImgSaveLowPixel > doInBackground() - originFile = " +height);
				Log.d(TAG, "ImgSaveLowPixel > doInBackground() - originFile = " +width);
				
				Log.d(TAG, "ImgSaveLowPixel > doInBackground() - originFile = " + (int)calcu_height);
				
				Bitmap resized = Bitmap.createScaledBitmap(originFile, 1024, (int)calcu_height, true);
				try {
			    	//FileOutputStream fos = new FileOutputStream(Configuration.directoryName +"/"+tempName);
			    	FileOutputStream fos = new FileOutputStream(Configuration.dirRoot_kr +"/"+tempName);
			    	resized.compress(CompressFormat.JPEG, 40, fos);
			    	fos.close();
			    	resized.recycle();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			refreshSD();
			ReadSDCard();
			
			new SaveImgData(selectedImg).execute("");
			
		}
		
	}
	
	/**====================================================================================================**/
	
	class SaveImgData extends AsyncTask<String, String, String>{
		ProgressDialog progressDialog;
		ArrayList<String> selectedImg;

		public SaveImgData(ArrayList<String> selectedImg){
			Log.d(TAG, "******************** SaveImgData()~! ********************");
			this.selectedImg = selectedImg;
			
		}
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			if(selectedImg.size() > 0){
				progressDialog = ProgressDialog.show(PhotoListActivity.this, "", "선택한 이미지를 저장중입니다...", true);
			}
			
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... param) {
			// TODO Auto-generated method stub
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			saveImg(selectedImg);
			
			if(selectedImg.size() > 0){
				progressDialog.dismiss();
				Toast.makeText(PhotoListActivity.this, "사진이 저장 되었습니다.", Toast.LENGTH_SHORT).show();
			}
			
			PhotoListActivity.this.finish();
			
		}
		
	}
	
	
	
	/**====================================================================================================**/
	
	public void refreshSD() {
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, 
				Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
		Log.d(TAG, "refreshSD() : "+"file://"+ Environment.getExternalStorageDirectory());
	}
	
	
	double totalFileSize =0;
	public List<ImageJgInfo> ReadSDCard() {
		Log.d(TAG, "ReadSDCard()~!");
		Log.d("","AndroidCustomGalleryActivity Step=4");
		ImageJgInfo currentItem = null;
		List<ImageJgInfo> trnImageList = null;

		trnImageList = new ArrayList<ImageJgInfo>();

		File fileDir = new File(Common.FILE_DIR);

		if (!fileDir.exists())
			fileDir.mkdir();

		File[] files = fileDir.listFiles();
		totalFileSize = 0;
		for (int i = 0; i < files.length; i++) {

			currentItem = new ImageJgInfo();
			File file = files[i];

			String fileName[] = Common.split(file.getPath().toString(), "/");
			String fileType[] = Common.split(file.getPath().toString(), ".");

			currentItem.setFilePath(file.getPath().toString());
			currentItem.setFileName(fileName[fileName.length - 1]);
			currentItem.setFileType(fileType[fileType.length - 1]);
			currentItem.setFileSize(file.length());

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

			// totalFileListSize.setText(totalFileSize+"MB");

			trnImageList.add(currentItem);

		}// end for

		Log.d("", "ReadSdCard list size = " + trnImageList.size());
		return trnImageList;
	}
	
	
	
	
	private void makeDialog(){
		AlertDialog.Builder ad = new AlertDialog.Builder(PhotoListActivity.this);
		ad.setMessage("")
				.setTitle("확인")
				.setMessage("* 이미지는 최대 5개까지 선택 가능합니다.")
				.setCancelable(false)
				.setPositiveButton("확인",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,int which) {
							}
						});
		ad.show();
	}
	
	
	public void saveImg(ArrayList<String> selectedImg){
		Log.d(TAG, "saveImg() - selectedImg size : "+selectedImg.size());
		
		for(int i=0; i<selectedImg.size(); i++){
			Log.d(TAG, "saveImg() - selectedImg : ["+i+"] "+selectedImg.get(i));
			
			ExifInterface exif;
			boolean showDialog = false;
			String message = "";
			String dataDate = "";
			try{
				
				
				exif = new ExifInterface(selectedImg.get(i));
				dataDate = exif.getAttribute(ExifInterface.TAG_DATETIME);
				Log.d(TAG, "saveImg() - dataDate1 : "+dataDate);
				if("".equals(common.nullCheck(dataDate))){
					showDialog = true;
					message = "사진메타정보가 존재하지않아 현재시간으로 저장합니다.";
					dataDate = common.getCalendarDateYMDHMS();
					Log.d(TAG, "saveImg() - No MetaDate dataDate : "+dataDate);
				}else{
					dataDate = dataDate.substring(0, 10).replace(":", ".")+" "+dataDate.substring(11, 19);
					Log.d(TAG, "saveImg() - Yes MetaDate dataDate : "+dataDate);
				}
				
				if(showDialog){
					Log.d(TAG, "saveImg() - dataDate2 : "+dataDate);
					/*AlertDialog.Builder adbLoc	= new AlertDialog.Builder(contextActivity);
					adbLoc.setCancelable(false);
					adbLoc.setTitle("스마트정비관리");  
					adbLoc.setMessage(message);
					adbLoc.setPositiveButton("예", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					adbLoc.show();*/
					
					Toast.makeText(PhotoListActivity.this, message, Toast.LENGTH_LONG).show();
				}
				Log.d(TAG, "saveImg() - dataDate3 : "+dataDate);
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			if(TAGYN == true){
				SELECTED_TAGYN ="Y";
			}else{
				SELECTED_TAGYN ="N";
			}
			
			SELECTED_CHECK = common.getCalendarDateYMDHMS();
			checkDb.insertSmart_DATAINFO(
					/* 2018.06.07.
					 * Camera 촬영 시 처리되는 정비구분, 수리항목 등에 선택인덱스 추가 부분이 누락됨
					 * Camera와 동일하게 처리 추가!
					 * */
					SELECTED_JEONGBIGUBUN,						 
					"["+SELECTED_SULIITEMCODE+"]"+SELECTED_SULIITEM, 
					SELECTED_DETAILITEM,	
					SELECTED_SULIITEM+"/"+SELECTED_DETAILITEM+"("+SELECTED_BEFORE_AFTER+")", 
					selectedImg.get(i),
					SELECTED_JBCODE, 
					SELECTED_JBMYEONG,
					SELECTED_DRNO, 
					SELECTED_JBMYEONG+"("+SELECTED_DRNO+")", 
					common.nullCheck(SELECTED_DOGONG), 
					dataDate,
					SELECTED_SWBEONHO, 
					SELECTED_BSCODE, 
					SELECTED_BUSEOCODE, 
					SELECTED_TAGYN, 
					getUserNm(PhotoListActivity.this),
					SELECTED_CHECK
					);
		}
	}
	
	
	public String getRealPathFromURI(Uri contentUri){
		String [] proj={MediaStore.Images.Media.DATA};
		Cursor cursor = managedQuery( contentUri, proj, null, null, null); 
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}


	
}
