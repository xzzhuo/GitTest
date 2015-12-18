package com.web.main;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import bdx.net.constant.NetConstant;
import bdx.net.interface1.INetConfig;
import bdx.net.interface1.LogLevel;
import bdx.net.interface1.NetCharset;
import bdx.net.log.NetLog;

public class Config implements INetConfig {

	private static final String mConfigPath = "conf";
	private static final String mConfigFile = "server.conf";
	
	private static int mPort = 8080;
	
	private static String DB_User 		= "root";
	private static String DB_Password 	= "";
	private static String DB_Url 		= "localhost";
	private static int DB_Port 			= 3306;
	private static String DB_Name 		= "notification";
	
	public static void readConfig()
	{
		File inFile = new File(mConfigPath, mConfigFile);
		if (!inFile.exists())
		{
			NetLog.error(NetConstant.System, "Config file is not exist, use default configure paramters", true);
			return;
		}
		
		try  
		{
			FileInputStream fstream = new FileInputStream(inFile);  
			DataInputStream in = new DataInputStream(fstream);  
			BufferedReader br = new BufferedReader(new InputStreamReader(in));  
			String strLine;  
			while ((strLine = br.readLine()) != null)  
			{
				if (!strLine.isEmpty())
				{
					// System.out.println(strLine);
					try
					{
						String[] split = strLine.split("=");
						if (split.length == 2)
						{
							String key = split[0].trim().toLowerCase();
							String value = split[1].trim();
							
							if (key.equals("port".toLowerCase()))
							{
								mPort = Integer.parseInt(value);
							}
							else if (key.equals("DB_User".toLowerCase()))
							{
								DB_User = value;
							}
							else if (key.equals("DB_User".toLowerCase()))
							{
								DB_User = value;
							}
							else if (key.equals("DB_Password".toLowerCase()))
							{
								DB_Password = value;
							}
							else if (key.equals("DB_Url".toLowerCase()))
							{
								DB_Url = value;
							}
							else if (key.equals("DB_Port".toLowerCase()))
							{
								DB_Port = Integer.parseInt(value);
							}
							else if (key.equals("DB_Name".toLowerCase()))
							{
								DB_Name = value;
							}
						}
						else if (split.length > 0)
						{
							
						}
					}
					catch(Exception e)  
					{  
						NetLog.error(NetConstant.System, "Parse parameter Error: " + e.getStackTrace(), true);  
					}
				}
			}  
			in.close();  
		}  
		catch(Exception e)  
		{  
			NetLog.error(NetConstant.System, "Error: " + e.getStackTrace(), true);  
		}
	}
	
	public static String  getDBUser()
	{
		return DB_User;
	}
	
	public static String  getDBPassword()
	{
		return DB_Password;
	}
	
	public static String  getDBUrl()
	{
		return DB_Url;
	}
	
	public static int getDBPort()
	{
		return DB_Port;
	}
	
	public static String  getDBName()
	{
		return DB_Name;
	}

	@Override
	public NetCharset getCharset() {
		return NetCharset.UTF_8;
	}

	@Override
	public String getRootPath() {
		return "Cienet";
	}

	@Override
	public int getServerPort() {
		return mPort;
	}

	@Override
	public LogLevel getLogLevel() {
		return LogLevel.Error;
	}
}
