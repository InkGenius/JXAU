package com.jxau.app.bean;

/*
 * ��װͨ����·������/�����״̬����վ�����Ƶ���Ϣ�� 
 */
public class LineBean{
	
	private String currentLine;
	private boolean status;// true ��Ϊ������
	
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
