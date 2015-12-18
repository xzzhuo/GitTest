package com.rain.netty;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

// process debug data
public class ProcessDebug {

	private static String FILE_NAME = "JsonData.txt";
	private static Map<String, String> responseMap = new HashMap<String, String>();

	public static void loadDefaultDebugData()
	{
		loadDebugData(FILE_NAME);
	}
	
	public static void loadDebugData(String fileName)
	{
		responseMap.clear();
		responseMap.put("ERROR", "{\"Error\":\"00001\",\"Data\":\"No Command\"}");
		
		File inFile = new File(fileName);
		if (!inFile.exists())
		{
			return;
		}
		
		try  
		{
			FileInputStream fstream = new FileInputStream(fileName);  
			DataInputStream in = new DataInputStream(fstream);  
			BufferedReader br = new BufferedReader(new InputStreamReader(in));  
			String strLine;  
			while ((strLine = br.readLine()) != null)  
			{
				if (!strLine.isEmpty())
				{
					System.err.println(strLine);  
				}
				
				if (!strLine.startsWith("//"))
				{
					String[] split = strLine.split("==");
					if (split.length == 2)
					{
						responseMap.put(split[0], split[1]);
					}
				}
			}  
			br.close();  
		}  
		catch(Exception e)  
		{  
			System.err.println("Error: " + e.getMessage());  
		}
		
	}
	
	public static String processDebugData(String url)
	{
		if (responseMap.containsKey(url))
		{
			return responseMap.get(url);
		}
		else
		{
			return responseMap.get("ERROR");
		}
	}
}
