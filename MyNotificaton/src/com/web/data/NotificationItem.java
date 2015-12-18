package com.web.data;

import java.util.Date;

public class NotificationItem {

	private int id = 0;
	private String title = "1";
	private String description = "1";
	private String action1text = "1";
	private String action1uri = "1";
	private String severity = "1";
	private Date startdate;
	private Date enddate;
	private String targetappname = "1";
	private int state = 0;		// 0-new, 1-burn, 2-delete
	
	public NotificationItem()
	{
		startdate = new Date();
		enddate = new Date();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAction1text() {
		return action1text;
	}

	public void setAction1text(String action1_text) {
		this.action1text = action1_text;
	}

	public String getAction1uri() {
		return action1uri;
	}

	public void setAction1uri(String action1_uri) {
		this.action1uri = action1_uri;
	}

	public Date getStartdate() {
		return startdate;
	}

	public void setStartdate(Date start_date) {
		this.startdate = start_date;
	}

	public Date getEnddate() {
		return enddate;
	}

	public void setEnddate(Date end_date) {
		this.enddate = end_date;
	}

	public String getTargetappname() {
		return targetappname;
	}

	public void setTargetappname(String app_id) {
		this.targetappname = app_id;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
}
