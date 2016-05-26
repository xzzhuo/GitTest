package com.rain.data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Person {
	
	private String name = "Lily";
	private int age = 10;
	private Date date = new Date();
	private Map<String, Object> map = new HashMap<String, Object>();
	
	public Person()
	{
		map.put("key1", "aa");
		map.put("key2", 10);
		map.put("key3", new Date());
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
