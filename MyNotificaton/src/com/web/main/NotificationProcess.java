package com.web.main;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.web.data.NotificationItem;
import com.web.data.NotificationTable;
import com.web.demo.Pages;

import bdx.net.database.DatabaseParam;
import bdx.net.log.NetLog;
import bdx.net.netty.NetProcess;
import bdx.net.utils.DbUtils;
import bdx.net.utils.ImportExcel;

public class NotificationProcess extends NetProcess {

	private WebUtil mWebUtil = null;
	
	public NotificationProcess()
	{
		mWebUtil = new WebUtil(this);
	}
	
	@Override
	public void Process(String address, String path, Map<String, String> request) {
		NetLog.debug(address, "Enter JavaWebProcess - Process()");
		NetLog.debug(address, path);

		File tempFile = new File(path);
		
		NetLog.debug(address, "Parent path = " + tempFile.getParent());
		mWebUtil.setTemplatePath(tempFile.getParent());
		
		String act = "";
		if (request.containsKey("act"))
		{
			act = request.get("act");
		}
		
		if (act.isEmpty())
		{
			act = "main";
		}
		
		NetLog.debug(address, "act=" + act);
		
		if (act.equals("main") ||
				act.equals("notification_item_insert") ||
				act.equals("act_notification_item_insert") ||
				act.equals("notification_delete"))
		{
			if (act.equals("main"))
			{
				String dateStart = "";
				String dateEnd = "";
				
				if (request.containsKey("date_start"))
				{
					dateStart = request.get("date_start");
				}
				if (request.containsKey("date_end"))
				{
					dateEnd = request.get("date_end");
				}
				DatabaseParam param = new DatabaseParam();
				param.setUrl(Config.getDBUrl());
				param.setPort(Config.getDBPort());
				param.setDatabaseName(Config.getDBName());
				param.setUser(Config.getDBUser());
				param.setPassword(Config.getDBPassword());
				NotificationTable table = new NotificationTable(param);
				List<NotificationItem> list = table.queryPages(0, 10);
				
				mWebUtil.assign("title", "My Notification");
				//mWebUtil.assign("name", "test");
				mWebUtil.assign("pages", new Pages());
				mWebUtil.assign("list", list);
				mWebUtil.assign("date_start", dateStart);
				mWebUtil.assign("date_end", dateEnd);
				mWebUtil.display(tempFile.getName());
			}
			else if (act.equals("act_notification_item_insert"))
			{
				NotificationItem item = new NotificationItem();
				Map<String, Object> map = DbUtils.transferBean2Map(item);
				for (Map.Entry<String, String> entry : request.entrySet()) {
					if (map.containsKey(entry.getKey()))
					{
						map.replace(entry.getKey(), entry.getValue());
					}
				}
				map.remove("id");
				
				DatabaseParam param = new DatabaseParam();
				param.setUrl(Config.getDBUrl());
				param.setPort(Config.getDBPort());
				param.setDatabaseName(Config.getDBName());
				param.setUser(Config.getDBUser());
				param.setPassword(Config.getDBPassword());
				
				NotificationTable table = new NotificationTable(param);
				for(int i=0;i<200;i++)
				{
					table.insert(map);
				}
				this.location("index.html?act=main");
			}
		}
		else if (act.equals("menu_show_notification_list"))
		{
			mWebUtil.assign("title", "My Notification");

			mWebUtil.display(tempFile.getName());
		}
		else if (act.equals("excel"))
		{
			File file = new File("person.xls");
			
			System.out.print("文件名： person.xls\t\t");
			
			String text = "";
			String[][] result;
			try {
				result = ImportExcel.getData(file, 0);
				int rowLength = result.length;
				for(int i=0;i<rowLength;i++) {
					for(int j=0;j<result[i].length;j++) {
						System.out.print(result[i][j]+"\t\t");
						text += result[i][j]+",";
					}
					System.out.println();
					text += "<br>";
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			this.print(text + "<br>");
		}
		else
		{
			setResponseText(new StringBuilder("Not implement"));
		}

		NetLog.debug(address, "Leave JavaWebProcess - Process()");
	}

}
