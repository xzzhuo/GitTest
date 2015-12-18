package com.web.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bdx.net.database.DatabaseParam;
import bdx.net.database.NetDatabase;
import bdx.net.database.NetTable;
import bdx.net.utils.DbUtils;

public class NotificationTable extends NetTable {

	private static final int mVersion = 1; 
	private static final String mTableName = "notification11";
	
	public NotificationTable(DatabaseParam param) {
		super(param, mVersion);
	}

	@Override
	public String getTableName() {
		return mTableName;
	}

	@Override
	public void onCreateTable(NetDatabase db) {
		
		String sql = String.format("create table %s (", getTableName());
		sql += "id INT NOT NULL AUTO_INCREMENT,";
		sql += "PRIMARY KEY (id),";

		sql += "title VARCHAR(100) NOT NULL,";
		sql += "description VARCHAR(40),";
		sql += "severity VARCHAR(40),";
		sql += "action1text VARCHAR(40),";
		sql += "action1uri VARCHAR(40),";
		sql += "startdate DATE,";
		sql += "enddate DATE,";
		sql += "state INT DEFAULT '0',";
		sql += "targetappname VARCHAR(60)";
		
		
		sql += ")";
		
		db.createTable(sql);
	}

	public List<Map<String, Object>> query()
	{
		String sql = String.format("SELECT * FROM %s ", this.getTableName());
		return super.query(sql);
	}
	
	public List<NotificationItem> queryAll()
	{
		//String sql = DbUtils.
		String sql = "SELECT * FROM " + this.getTableName();
		List<Map<String, Object>> maps = super.query(sql);
		
		List<NotificationItem> lists = new ArrayList<NotificationItem>();
		for(Map<String, Object> map: maps)
		{
			NotificationItem item = new NotificationItem();
			DbUtils.transferMap2Bean(map, item);
			lists.add(item);
		}
		
		return lists;
	}
	
	public List<NotificationItem> queryPages(int limit, int offset)
	{
		String sql = String.format("SELECT * FROM %s limit %d,%d", this.getTableName(), limit, offset);
		List<Map<String, Object>> maps = super.query(sql);
		
		List<NotificationItem> lists = new ArrayList<NotificationItem>();
		for(Map<String, Object> map: maps)
		{
			NotificationItem item = new NotificationItem();
			DbUtils.transferMap2Bean(map, item);
			lists.add(item);
		}
		
		return lists;
	}
	
	public List<NotificationItem> queryState(int state, int limit, int offset)
	{
		String sql = String.format("SELECT * FROM %s WHERE state='%d' limit %d,%d", this.getTableName(), state, limit, offset);
		List<Map<String, Object>> maps = super.query(sql);
		
		List<NotificationItem> lists = new ArrayList<NotificationItem>();
		for(Map<String, Object> map: maps)
		{
			NotificationItem item = new NotificationItem();
			DbUtils.transferMap2Bean(map, item);
			lists.add(item);
		}
		
		return lists;
	}

	public boolean insert(Map<String, Object> map) {
		String sql = DbUtils.getInsertSql(this.getTableName(), map);
		
		return this.insert(sql);
	}
}
