package com.jxau.app.bean;

import java.io.Serializable;
import java.util.List;

public class GoOutBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String location;
	private List<String> listLine;
	private boolean direction;
	
	public GoOutBean(String location,List<String> listLine,boolean direction){
		this.location=location;
		this.listLine=listLine;
		this.direction=direction;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
	public List<String> getListLine() {
		return listLine;
	}
	public void setListLine(List<String> listLine) {
		this.listLine = listLine;
	}
	public boolean isDirection() {
		return direction;
	}
	public void setDirection(boolean direction) {
		this.direction = direction;
	}
	
}
