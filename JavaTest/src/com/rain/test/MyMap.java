package com.rain.test;

import java.util.HashMap;
import java.util.Map;

public class MyMap extends HashMap<String, String> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2702770377756810662L;

	public static String mapToString()
	{
		Map<String, String> map = new HashMap<String, String>();
		map.put("Key1", "value1");
		map.put("Key2", "value2");
		map.put("Key3", "value1");
		
		System.out.println("Map string: " + map.toString());
		
		return map.toString();
	}
	
	public static Map<String, String> stringToMap(String value)
	{
		Map<String, String> map = new HashMap<String, String>();

		if (value.length() < 2 || value.indexOf('{') != 0 || value.lastIndexOf('}') != value.length()-1)
		{
			System.out.println("Map string error");
			return null;
		}
		
		if (value.length() > 2)
		{
			value = value.substring(1, value.length()-1);
			String[] mapValueArray = value.split(",");
			for(String mapValue: mapValueArray)
			{
				String[] pare = mapValue.split("=");
				
				if (pare.length == 2)
				{
					map.put(pare[0].trim(), pare[1].trim());
				}
			}
		}
		
		System.out.println("Map string: " + map.toString());
		
		return map;
	}

}
