package com.beiluoshimen.securityguard.market;

public class Character {
	private String name;
	private int no;//e.g., 100 101 << the character num 
	private int price;
	
	private String pic; //html://...xxx.ooo.100.jpg
	
	//client can use this to setup up socket to fetch data from data server.
	private String dataIP; // e.g.,127.0.0.1
	private String dataPort; //e.g., port num = 10001
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDataIP() {
		return dataIP;
	}
	public void setDataIP(String dataIP) {
		this.dataIP = dataIP;
	}

	public String getDataPort() {
		return dataPort;
	}
	public void setDataPort(String dataPort) {
		this.dataPort = dataPort;
	}
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	public Character(String name, int no, int price, String pic , String dataIP, String dataPort) {
		super();
		this.name = name;
		this.no = no;
		this.price = price;
		this.pic = pic;
		this.dataIP = dataIP;
		this.dataPort = dataPort;
	}
	
	
}

