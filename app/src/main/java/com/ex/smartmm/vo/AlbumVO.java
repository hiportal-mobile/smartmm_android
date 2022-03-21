package com.ex.smartmm.vo;

import java.util.List;

public class AlbumVO {
	
	String name;
	String path;
	String img;
	String imgTotal;
	List<String> imgInfo;


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	
	public String getImgTotal() {
		return imgTotal;
	}
	public void setImgTotal(String imgTotal) {
		this.imgTotal = imgTotal;
	}
	
	public List<String> getImgInfo() {
		return imgInfo;
	}
	public void setImgInfo(List<String> imgInfo) {
		this.imgInfo = imgInfo;
	}
	
}
