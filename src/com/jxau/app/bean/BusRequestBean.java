package com.jxau.app.bean;

import java.io.Serializable;

public class BusRequestBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String quaryNum;
	private String line;
	private boolean direction;

	public BusRequestBean(String line, boolean direction) {
		this.line = line;
		this.direction = direction;
		this.quaryNum = direction ? line+"_0" : line+"_1";
		
	}

	public String getLine() {
		return line;
	}

	public void setLine(String listLine) {
		this.line = listLine;
	}

	public boolean isDirection() {
		return direction;
	}

	public void setDirection(boolean direction) {
		this.direction = direction;
	}

	public String getQuaryNum() {
		return quaryNum;
	}

	public void setQuaryNum(String quaryNum) {
		this.quaryNum = quaryNum;
	}
}
