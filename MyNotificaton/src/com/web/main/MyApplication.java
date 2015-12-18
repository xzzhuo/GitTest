package com.web.main;

import com.web.demo.DemoProcess;

import bdx.net.interface1.INetConfig;
import bdx.net.netty.NetApplication;
import bdx.net.netty.NetProcess;

public class MyApplication extends NetApplication {

	private static int processIndex = 0; 
	
	public static void main(String[] args)
	{
		
		processIndex = Integer.parseInt(args[0]);
		new MyApplication().run(args);
	}

	@Override
	public INetConfig onGetConfig() {
		return new Config();
	}

	@Override
	public NetProcess onGetProcess() {
		if (processIndex == 1)
		{
			return new DemoProcess();
		}
		else if (processIndex == 2)
		{
			return new NotificationProcess();
		}
		else
		{
			return null;
		}
	}
	
}
