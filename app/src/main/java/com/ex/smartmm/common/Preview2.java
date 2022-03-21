package com.ex.smartmm.common;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;



public class Preview2 extends SurfaceView implements SurfaceHolder.Callback {

    SurfaceHolder mHolder;
    public int maxZoom = 0 ;
    public Camera mCamera = null;
    Context context;
	Parameters mParameters;
    public void close()
    {
    	if( mCamera != null){
    		mCamera.stopPreview();
    		mCamera.release();
    		mCamera = null;
    	}
	}
    
    public Preview2(Context context, AttributeSet attrs) { 
		super(context, attrs); 
	   this.context = context;
	  	try{
	  		if (mCamera == null){
		  		
	  			mCamera = Camera.open();
				Camera.Parameters params = mCamera.getParameters();
				maxZoom = params.getMaxZoom();

				List<Size> list = params.getSupportedPreviewSizes();
				int m_resWidth = 860;
				int m_resHeight = 480;

				if(list == null){//미리보기 지원목록이 없으면
					//표면의 크기로 미리보기 사이즈 지정
					m_resWidth = mCamera.getParameters().getPictureSize().width;
					m_resHeight = mCamera.getParameters().getPictureSize().height;

					params.setPictureSize(m_resWidth, m_resHeight);
				}else{//표면의 실제크기와 가장 근접한 사이즈 구하기
					int diff = 10000;
					Size size = null;

					for(Size s:list){
						if(Math.abs(s.width - m_resWidth) < diff){
							diff=Math.abs(s.width - m_resWidth);
							size=s;
						}
					}

					//(갤럭시s4)||갤럭시 노트3 사진 사이즈 조정 2013-11-14 JSJ
					if(Build.MODEL.contains("SHV-E330") || Build.MODEL.contains("SM-N900")
							|| (Build.MODEL.contains("SM-G9")|| Build.MODEL.contains("SM-N910")) ){
						params.setPictureSize(2048,1152);
					}else if(Build.MODEL.contains("SHV-E330")){//갤럭시 노트2
						params.setPictureSize(640,480);
					}else if(Build.MODEL.contains("SM-N916K")){
						params.setPictureSize(640,480);
					}else if(Build.MODEL.contains("SM-A700")){//갤럭시 a7
						params.setPictureSize(3264,1836);
					}else{
						params.setPictureSize(size.width,size.height);
					}

					List<Size> piclist = params.getSupportedPictureSizes();
					int width = 0;
					int height = 0;
					int nexWidth = 0;
					int nexheight = 0;
					for (int i = 0; i < piclist.size(); i++) {
						if(i == 0){
							width = piclist.get(i).width;
							height = piclist.get(i).height;
						}else{
							nexWidth = piclist.get(i).width;
							nexheight = piclist.get(i).height;

							if(width > nexheight){
								width = nexWidth;
								height = nexheight;
							}
						}
					}

					params.setPictureSize(width, height);

					Log.d("Camera", "width:" + size.width + "; height:" + size.height);
					Log.d("", "device Model info = " + Build.MODEL);
//					params.setFlashMode("torch");
				}
				mCamera.setParameters(params);
			}
//		  	mCamera.setDisplayOrientation(90);

	        mHolder = getHolder();
	        mHolder.addCallback(this);
	        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);      
			
	        setFocusable(true);
//	        sThread.start();
	  	}catch (Exception e) {
	  		e.printStackTrace();
	  	}
    }

	public void FlashOn(){
		if (mCamera == null ) {
			return;
		}
		Log.d("", "FlashMode = ON" );
		mParameters = mCamera.getParameters();
		mParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
		mCamera.setParameters(mParameters);

	}
	public void FlashOff(){

		if (mCamera == null ) {
			return;
		}
		Log.d("", "FlashMode = Off" );
		mParameters = mCamera.getParameters();
		mParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
		mCamera.setParameters(mParameters);
	}


    @Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
    
	public void surfaceCreated(SurfaceHolder holder) {

		try{

				if( mCamera == null){
					
						mCamera = Camera.open();
				}else{
					try {
				
						mCamera.setPreviewDisplay(holder);
						mCamera.startPreview();
					}

					catch (IOException exception) {
						Log.e("Error", "exception:surfaceCreated Camera Open ");
						mCamera.release();
						mCamera = null;
						// TODO: add more exception handling logic here
					}
				}
//			sThread.start();
//			new DragThread(mHolder, context).start();
				
		} catch (Exception e) {
			Log.e("camera open error","error");
		}
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
      	if(mCamera!=null ){
    		
    		mCamera.stopPreview();
    		mCamera.release(); 
    	    mCamera = null;
    	}
    }
    
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
//			sThread.x = (int)event.getX();
//			sThread.y = (int)event.getY();
			break;

		default:
			break;
		}
    	return super.onTouchEvent(event);
    }





}

