package com.ex.smartmm.common;

import android.graphics.Bitmap;

public class Image {
	
	private CharSequence fileName;
	private CharSequence filePath;
	private CharSequence imagePath;
	private CharSequence fileType;
	
	private CharSequence thumbnailPath;
	private Bitmap image;
	private Bitmap thumbnail;
	private String thumbnailId;
	private long fileSize;			
	
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
