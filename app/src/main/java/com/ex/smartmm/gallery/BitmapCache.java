package com.ex.smartmm.gallery;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;

@SuppressLint("NewApi")
public class BitmapCache {
	
	private static BitmapCache cache = null;
	private LruCache<String, Bitmap> mCache;

	public static BitmapCache getInstance() {
		if (cache == null) {
			cache = new BitmapCache();
		}

		return cache;
	}

	public BitmapCache() {
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory());
		final int cacheSize = 20 * 1024 * 1024; //maxMemory / 10;


		mCache = new LruCache<String, Bitmap>(cacheSize) {

			@SuppressLint("NewApi")
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getByteCount();
			}

		};
	}

	public Bitmap getImage(String path)
	{
		Bitmap img = null;
		synchronized (mCache) 
		{
			img = mCache.get(path);
		}
		
		return img;
	}
	
	public Bitmap getHttpBitmap(String path) throws IOException
	{
		URL url = new URL(path);
		
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setReadTimeout(5000);

		conn.connect();
		InputStream is = conn.getInputStream();
		Bitmap img = BitmapFactory.decodeStream(is);
		if ( img != null )
		{
			synchronized (mCache) 
			{
				mCache.put(path, img);
			}
		}
		return img;
	}
	
	@SuppressLint("NewApi")
	public void putImage(String path, Bitmap img)
	{
		if ( path == null || path.isEmpty() || img == null) return;
		
		synchronized (mCache) 
		{
			mCache.remove(path);
			mCache.put(path, img);
		}
	}
	
	private Bitmap __getImageThumbnail(ContentResolver cr, String path)
	{
	    Cursor ca = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.MediaColumns._ID }, MediaStore.MediaColumns.DATA + "=?", new String[] {path}, null);
	    if (ca != null && ca.moveToFirst() && !ca.isClosed()) 
	    {
	        long id = ca.getLong(ca.getColumnIndex(MediaStore.MediaColumns._ID));

	        ca.close();
	        Bitmap img = MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MINI_KIND, null );
	        
	        return img;
	    }

	    ca.close();
	    return null;
	}
	
	private static Bitmap __rotate(Bitmap bitmap, int degrees) 
	{
		if (degrees != 0 && bitmap != null)
		{
			Matrix m = new Matrix();
			m.setRotate(degrees, (float) bitmap.getWidth() / 2,	(float) bitmap.getHeight() / 2);

			try
			{
				Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
				if (bitmap != converted) 
				{
					bitmap.recycle();
					bitmap = converted;
				}
			}
			catch (OutOfMemoryError ex)
			{
				ex.printStackTrace();
			}
		}
		return bitmap;
	}
	
	private static int __getExifOrientationToDegrees(int exifOrientation)
	{
		if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
		{
			return 90;
		}
		else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
		{
			return 180;
		}
		else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
		{
			return 270;
		}
		return 0;
	}
	
	private Bitmap __getRotatedBitmap(Bitmap img, String path)
	{
		int exifDegree = 0;
		try
		{
			ExifInterface exif = new ExifInterface(path);
			int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			exifDegree = __getExifOrientationToDegrees(exifOrientation);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		img = __rotate(img, exifDegree);
		
		return img;
	}
	
	public Bitmap getThumbnail(ContentResolver cr, String path)
	{
		Bitmap img = null;
		synchronized (mCache) 
		{
			img = mCache.get("T" + path);
		}
		
		if (img == null) {
			img = __getImageThumbnail(cr, path);
			if ( img != null )
			{
				img = __getRotatedBitmap(img, path);
				synchronized (mCache) 
				{
					mCache.put("T" + path, img);
				}
			}
		}
		
		return img;
	}

	public void removeBitmap(String path) {
		if ( path != null && !path.isEmpty() )
		{
			synchronized (mCache) 
			{
				mCache.remove(path);
			}
		}
	}

	public void clear() {
		synchronized (mCache) 
		{
			mCache.evictAll();
		}
		
		System.gc();
	}
}
