package com.jxau.app.bean;

/*
 * 封装通过线路，出发/到达的状态，拿站点名称的信息类 
 */
public class LineBean{
	
	private String currentLine;
	private boolean status;// true 即为正方向
	
	public String getLine() {
		return currentLine;
	}
	public void setLine(String line) {
		this.currentLine = line;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
}
