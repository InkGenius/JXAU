package com.jxau.app.bean;


import java.util.List;

public class BusTrackResult {
	
	private List<BusTrackDetail> listSubBusInfo;
	private String currentLine;
	
	public BusTrackResult(){}
	public BusTrackResult(List<BusTrackDetail> listSubBusInfo, String currentLine) {
		this.listSubBusInfo = listSubBusInfo;
		this.currentLine = currentLine;
	}

	public List<BusTrackDetail> getListSubBusInfo() {
		return listSubBusInfo;
	}

	public void setListSubBusInfo(List<BusTrackDetail> listSubBusInfo) {
		this.listSubBusInfo = listSubBusInfo;
	}

	public String getCurrentLine() {
		return currentLine;
	}

	public void setCurrentLine(String currentLine) {
		this.currentLine = currentLine;
	}


}
