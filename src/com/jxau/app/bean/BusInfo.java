package com.jxau.app.bean;

import java.util.List;

/* ������Ϣ  */
/* station ��ʾǰ�����ߵ����վ������*/
/* number ��ʾǰ�����ߵ���Ĺ������� */
/* flag���� true ��ʾ���� �� false ��ʾǰ��*/
public class BusInfo {

	private List<SubBusInfo> listSubBusInfo;
	private String currentLine;
	private boolean dire;

	public List<SubBusInfo> getListSubBusInfo() {
		return listSubBusInfo;
	}

	public void setListSubBusInfo(List<SubBusInfo> listSubBusInfo) {
		this.listSubBusInfo = listSubBusInfo;
	}

	public String getCurrentLine() {
		return currentLine;
	}

	public void setCurrentLine(String currentLine) {
		this.currentLine = currentLine;
	}

	public boolean isDire() {
		return dire;
	}

	public void setDire(boolean dire) {
		this.dire = dire;
	}

}
