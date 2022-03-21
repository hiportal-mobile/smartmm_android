package com.ex.smartmm.vo;

//점검정보 
public class JGItemVO {

	String id = "";
	String krcode = "";
	String krname = "";
	String imgPath = "";
	String content = "";
	String date = "";
	String sendyn = "";
	
	
	
	public String getSendyn() {
		return sendyn;
	}
	public void setSendyn(String sendyn) {
		this.sendyn = sendyn;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getKrcode() {
		return krcode;
	}
	public void setKrcode(String krcode) {
		this.krcode = krcode;
	}
	public String getKrname() {
		return krname;
	}
	public void setKrname(String krname) {
		this.krname = krname;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getImgPath() {
		return imgPath;
	}
	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}
	
	
}
