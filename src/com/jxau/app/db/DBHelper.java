package com.jxau.app.db;

import com.jxau.app.bean.BusLineInfo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	
	private final static String DATABASE_NAME = "bus.db";
	private final static int DATABASE_VERSION = 1;
	private final static String TABLE_NAME = "BusInfo";
	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		String sql = " DROP TABLE IF EXISTS " + TABLE_NAME;
		db.execSQL(sql);
		onCreate(db);
	}
	
	public BusLineInfo select(String name) {
		BusLineInfo info = new BusLineInfo();
		SQLiteDatabase db = this.getReadableDatabase();
		System.out.println(db.toString());
		String sql = "select * from BusInfo where bus ="+"'"+name+"'";
		Cursor c = db.rawQuery(sql, null);
		while (c.moveToNext()) {
			info.setStartTime(c.getString(c.getColumnIndex("startTime")));
			info.setEndTime(c.getString(c.getColumnIndex("endTime")));
			info.setStartStation(c.getString(c.getColumnIndex("startStation")));
			info.setEndStation(c.getString(c.getColumnIndex("endStation")));
			info.setStations(c.getString(c.getColumnIndex("stations")));
			info.setName(c.getString(c.getColumnIndex("line")));
		}
		c.close();
		return info;
	}
}
