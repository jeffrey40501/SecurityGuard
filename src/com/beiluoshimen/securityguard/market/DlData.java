package com.beiluoshimen.securityguard.market;



//this Dldata(download data) contains one specific character data.
public class DlData {
	// this no. indicate which character this zip contains ....
	private int no;
	// this define the length of zipData.
	private int size;
	//pointer to zipData
	private String  zipData;
	public DlData(){
		
	}
	
	public DlData(int size,int no, String zipData){
		this.size = size;
		this.no = no;
		this.zipData = zipData;
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getZipData() {
		return zipData;
	}

	public void setZipData(String zipData) {
		this.zipData = zipData;
	}
	

}
