package com.web.demo;

public class Person {

	private String name = null;
	private String[] books = new String[3];
	
	public Person()
	{
		books[0] = "English";
		books[1] = "Chinese";
		books[2] = "Spansh";
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public int getBookSize()
	{
		return books.length;
	}
	
	public String[] getBooks()
	{
		return books;
	}
}
