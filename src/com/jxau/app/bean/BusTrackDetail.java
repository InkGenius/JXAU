/**
 * 
 */
package com.jxau.app.bean;

public class BusTrackDetail {
	
	private String station;
	private int number;
	private String arrived;
	
	public BusTrackDetail(){}
	public BusTrackDetail(String line,int number,String station){
		this.arrived=line;
		this.number=number;
		this.station=station;
	}
	public String getStation() {
		return station;
	}
	public void setStation(String station) {
		this.station = station;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	
	public String getArrived() {
		return arrived;
	}
	public void setArrived(String arrived) {
		this.arrived = arrived;
	}
}
