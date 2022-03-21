package com.ex.smartmm.common;

import com.ex.smartmm.vo.AmgeoInfo;
import com.ex.smartmm.vo.ItemGB;
import com.ex.smartmm.vo.JbDataInfo;
import com.ex.smartmm.vo.JbInfo;
import com.ex.smartmm.vo.KrInfo;

import android.graphics.Bitmap;

public class ImageJgInfo {
	
	private CharSequence fileName;
	private CharSequence filePath;
	private CharSequence imagePath;
	private CharSequence fileType;
	
	private CharSequence thumbnailPath;
	private Bitmap image;
	private Bitmap thumbnail;
	private String thumbnailId;
	private long fileSize;			
//	private KrInfo info;
	private JbInfo info;
	private JbDataInfo jbDataInfo;
	private ItemGB infoItem;

/*	public KrInfo getInfo() {
		return info;
	}

	public void setInfo(KrInfo info) {
		this.info = info;
	}
*/
	public JbInfo getInfo() {
		return info;
	}
	public void setInfo(JbInfo info) {
		this.info = info;
	}

	public JbDataInfo getJbDataInfo() {
		return jbDataInfo;
	}
	public void setJbDataInfo(JbDataInfo jbDataInfo) {
		this.jbDataInfo = jbDataInfo;
	}
	
	public ItemGB getInfoItem() {
		return infoItem;
	}

	public void setInfoItem(ItemGB infoItem) {
		this.infoItem = infoItem;
	}
 	

	
	public long getFileSize() {
		return fileSize;
	}


	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}


	public CharSequence getFileName() {
		return fileName;
	}


	public void setFileName(CharSequence fileName) {
		this.fileName = fileName;
	}


	public CharSequence getFilePath() {
		return filePath;
	}


	public void setFilePath(CharSequence filePath) {
		this.filePath = filePath;
	}


	public CharSequence getImagePath() {
		return imagePath;
	}


	public void setImagePath(CharSequence imagePath) {
		this.imagePath = imagePath;
	}


	public CharSequence getFileType() {
		return fileType;
	}


	public void setFileType(CharSequence fileType) {
		this.fileType = fileType;
	}


	public CharSequence getThumbnailPath() {
		return thumbnailPath;
	}


	public void setThumbnailPath(CharSequence thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}


	public Bitmap getImage() {
		return image;
	}


	public void setImage(Bitmap image) {
		this.image = image;
	}


	public Bitmap getThumbnail() {
		return thumbnail;
	}


	public void setThumbnail(Bitmap thumbnail) {
		this.thumbnail = thumbnail;
	}


	public String getThumbnailId() {
		return thumbnailId;
	}


	public void setThumbnailId(String thumbnailId) {
		this.thumbnailId = thumbnailId;
	}



	
}
