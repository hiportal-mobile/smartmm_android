package com.ex.smartmm.vo;

public class ItemGB {

	//CREATE TABLE "SMART_ITEMGB" ("NO" , "SULIITEM" , "DETAILITEM" )

	String no = "";//순번
	String suliitem = "";//수리항목
	String detailitem = "";//상세항목
	
	
	public String getno() {
		return no;
	}
	public void setno(String no) {
		this.no = no;
	}
	
	public String getsuliitem() {
		return suliitem;
	}
	public void setsuliitem(String suliitem) {
		this.suliitem = suliitem;
	}
	
	public String getdetailitem() {
		return detailitem;
	}
	public void setdetailitem(String detailitem) {
		this.detailitem = detailitem;
	}
}
