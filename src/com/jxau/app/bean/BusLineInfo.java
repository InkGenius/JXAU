package com.jxau.app.bean;

public class BusLineInfo {
	
	private String name;
	private String stations;
	private String startStation;
	private String endStation;
	private String startTime;
	private String endTime;
	public BusLineInfo(){}
	
	public BusLineInfo(String name,String stations,String startStation,
			String endStation,String startTime,String endTime){
		
		this.name=name;
		this.stations=stations;
		this.startStation=startStation;
		this.endStation=endStation;
		this.startTime=startTime;
		this.endTime=endTime;
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStations() {
		return stations;
	}

	public void setStations(String stations) {
		this.stations = stations;
	}

	public String getStartStation() {
		return startStation;
	}

	public void setStartStation(String startStation) {
		this.startStation = startStation;
	}

	public String getEndStation() {
		return endStation;
	}

	public void setEndStation(String endStation) {
		this.endStation = endStation;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
}
