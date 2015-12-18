package com.web.function;

import java.util.Map;

public class MyUtil {

	public static String getInsertSql(String table, Map<String, Object> map)
	{
		String field = "";
		String value = "";
		
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			field += entry.getKey() + ",";
			value += "'" + entry.getValue().toString() + "',";
		}
		
		if (field.endsWith(","))
		{
			field = field.substring(0, field.length()-1);
		}
		
		if (value.endsWith(","))
		{
			value = value.substring(0, value.length()-1);
		}
		
		String returnValue = "INSERT INTO " + table + "  (" + field + ") VALUES (" + value + ")";
		
		return returnValue;
	}
}
