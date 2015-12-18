package com.web.demo;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.web.data.NotificationItem;
import com.web.data.NotificationTable;
import com.web.function.MyUtil;
import com.web.main.Config;
import com.web.main.JsonDateValueProcessor;
import com.web.main.WebUtil;

import bdx.net.database.DatabaseParam;
import bdx.net.database.NetDatabase;
import bdx.net.log.NetLog;
import bdx.net.netty.NetProcess;
import bdx.net.utils.DbUtils;
import bdx.net.utils.ImportExcel;

public class DemoProcess extends NetProcess {

	private WebUtil mWebUtil = null;
	
	public DemoProcess()
	{
		mWebUtil = new WebUtil(this);
	}
	
	public Pages calculatePages(Map<String, String> request)
	{
		Pages pages = new Pages();

		int rows = pages.getRows();
		int offsets = pages.getOffsets();
		
		if (this.getCookie("rows") != null)
		{
			rows = Integer.parseInt(this.getCookie("rows"));
		}
		
		if (this.getCookie("offsets") != null)
		{
			offsets = Integer.parseInt(this.getCookie("offsets"));
		}
		
		if (request.containsKey("page_act"))
		{
			String page_act = request.get("page_act");
			
			if (page_act.equals("page_to_first"))
			{
				rows = 0;
			}
			else if (page_act.equals("page_to_previous"))
			{
				rows -= offsets;
				if (rows < 0)
				{
					rows = 0;
				}
			}
			else if (page_act.equals("page_to_next"))
			{
				rows += offsets;
			}
			else if (page_act.equals("page_to_last"))
			{
				NetLog.error("Pages", "Not implement");
			}
			
		}
		
		this.setCookie("rows", String.format("%d", rows));
		this.setCookie("offsets", String.format("%d", offsets));
		
		NetLog.error("Pages", String.format("rows = %d, offsets = %d", rows, offsets));
		
		pages.setRows(rows);
		pages.setOffsets(offsets);
		
		return pages;
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
			if (path.indexOf("Cienet\\tmp\\notification") > 0)
			{
				act = "act_sync_notification";
			}
			else
			{
				NetLog.error("debug", path);
				act = "main";
			}
		}
		
		NetLog.debug(address, "act=" + act);
		
		if (act.equals("test"))
		{
			Person person = new Person();
			person.setName("test");
			
			mWebUtil.assign("name", "name");
			mWebUtil.assign("age", "25");
			mWebUtil.assign("person", person);
			mWebUtil.display(tempFile.getName());
		}
		else if (act.equals("main") ||
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
				
				Pages pages = calculatePages(request);
				
				DatabaseParam param = new DatabaseParam();
				param.setUrl(Config.getDBUrl());
				param.setPort(Config.getDBPort());
				param.setDatabaseName(Config.getDBName());
				param.setUser(Config.getDBUser());
				param.setPassword(Config.getDBPassword());
				NotificationTable table = new NotificationTable(param);
				List<NotificationItem> list = table.queryPages(pages.getRows(), pages.getOffsets());
				
				mWebUtil.assign("title", "My Notification");
				//mWebUtil.assign("name", "test");
				mWebUtil.assign("pages", pages);
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
				
				if (map.containsKey("severity"))
				{
					String value = map.get("severity").toString();
					if (!value.toLowerCase().equals("critical"))
					{
						map.replace("severity", "Optional");
					}
				}
				
				DatabaseParam param = new DatabaseParam();
				param.setUrl(Config.getDBUrl());
				param.setPort(Config.getDBPort());
				param.setDatabaseName(Config.getDBName());
				param.setUser(Config.getDBUser());
				param.setPassword(Config.getDBPassword());
				
				NotificationTable table = new NotificationTable(param);
				table.insert(map);
				
				this.location("index.html?act=main");
			}
		}
		else if (act.equals("menu_show_notification_list"))
		{
			mWebUtil.assign("title", "My Notification");
			
			mWebUtil.display(tempFile.getName());
		}
		else if (act.equals("act_sync_notification"))
		{
			DatabaseParam param = new DatabaseParam();
			param.setUrl(Config.getDBUrl());
			param.setPort(Config.getDBPort());
			param.setDatabaseName(Config.getDBName());
			param.setUser(Config.getDBUser());
			param.setPassword(Config.getDBPassword());
			NotificationTable table = new NotificationTable(param);
			List<NotificationItem> newList = table.queryState(0,0,10);
			List<NotificationItem> delList = table.queryState(2,0,10);
			
			try
			{
				JSONObject jsonObject= new JSONObject();
				JSONArray jsonNewArray = new JSONArray();
				
				for(NotificationItem item : newList)
				{
					try
					{
						JsonConfig jsonConfig = new JsonConfig();  
						jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor());  

						
						JSONObject jsonItem = JSONObject.fromObject(item, jsonConfig);
						jsonNewArray.add(jsonItem);
					}
					catch(Exception e)
					{
						this.print(e.getMessage());
						NetLog.error("JSON error", e.getMessage());
					}
				}
				
				JSONArray jsonDelArray = new JSONArray();
				for(NotificationItem item : delList)
				{
					try
					{
						jsonDelArray.add(item.getId());
					}
					catch(Exception e)
					{
						this.print(e.getMessage());
						NetLog.error("JSON error", e.getMessage());
					}
				}
				
				jsonObject.put("Notifications", jsonNewArray);
				jsonObject.put("ArchivedIds", jsonDelArray);
				this.print(jsonObject.toString());
			}
			catch(Exception e)
			{
				this.print(e.getMessage());
				NetLog.error("JSON error", e.getMessage());
			}
		}
		else if (act.equals("db"))
		{
			DatabaseParam param = new DatabaseParam();
			param.setUrl(Config.getDBUrl());
			param.setPort(Config.getDBPort());
			param.setDatabaseName(Config.getDBName());
			param.setUser(Config.getDBUser());
			param.setPassword(Config.getDBPassword());
			
			NetDatabase mydb = new NetDatabase();
			mydb.connect(param);
			
		}
		else if (act.equals("map"))
		{
			PersonBean person = new PersonBean();
			Map<String, Object> mp = new HashMap<String, Object>();
			mp.put("name", "Mike");
			mp.put("age", 25);
			mp.put("mN", "male");

			DbUtils.transferMap2Bean2(mp, person);
			this.print("--- transMap2Bean Map Info: <br>");
			for (Map.Entry<String, Object> entry : mp.entrySet()) {
				this.print(entry.getKey() + ": " + entry.getValue()+"<br>");
			}
			
			this.print("--- Bean Info: <br>");
			this.print("name: " + person.getName()+"<br>");
			this.print("age: " + person.getAge()+"<br>");
			this.print("mN: " + person.getmN()+"<br>");

			Map<String, Object> map = DbUtils.transferBean2Map(person);
			
			this.print("--- transBean2Map Map Info: <br>");
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				this.print(entry.getKey() + ": " + entry.getValue()+"<br>");
			}
		}
		else if (act.equals("map1"))
		{
			NotificationItem notfi = new NotificationItem();
			Map<String, Object> mp = new HashMap<String, Object>();  
			mp.put("nt_id", "1");
			mp.put("nt_title", "Title1");
			mp.put("nt_description", "Description1");
			
			mp.put("nt_action1_text", "nt_action1_text");
			mp.put("nt_action1_uri", "nt_action1_uri");
			mp.put("nt_state", "Active");
			mp.put("nt_category", "sw");
			//mp.put("nt_end_date", "2015-11-16");
			//mp.put("nt_archive", "0");
			//mp.put("nt_disable", "1");

			transMap2Bean2(mp, notfi);
			System.out.println("--- transMap2Bean Map Info: ");  
			for (Map.Entry<String, Object> entry : mp.entrySet()) {  
				System.out.println(entry.getKey() + ": " + entry.getValue());  
			}  
			
			System.out.println("--- Bean Info: ");  
			System.out.println("id: " + notfi.getId());  
			System.out.println("title: " + notfi.getTitle());  
			System.out.println("description: " + notfi.getDescription());

			Map<String, Object> map = transBean2Map(notfi);  
			
			System.out.println("--- transBean2Map Map Info: ");  
			for (Map.Entry<String, Object> entry : map.entrySet()) {  
				System.out.println(entry.getKey() + ": " + entry.getValue());
				this.print(entry.getKey() + ": " + entry.getValue() + "<br>");
			}  
			
		}
		else if (act.equals("box"))
		{
			Object val = 10;
			
			String a = (String)val.toString();
			
			System.out.println(a);
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("Field1", 10);
			map.put("Field2", "name");
			setResponseText(new StringBuilder(MyUtil.getInsertSql("table", map)));
		}
		else if (act.equals("excel1"))
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
		else if (act.equals("excel2"))
		{
			DatabaseParam param = new DatabaseParam();
			param.setUrl(Config.getDBUrl());
			param.setPort(Config.getDBPort());
			param.setDatabaseName(Config.getDBName());
			param.setUser(Config.getDBUser());
			param.setPassword(Config.getDBPassword());
			NotificationTable table = new NotificationTable(param);
			List<NotificationItem> list = table.queryAll();
			
			// 第一步，创建一个webbook，对应一个Excel文件  
			HSSFWorkbook wb = new HSSFWorkbook();  
			// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet  
			HSSFSheet sheet = wb.createSheet("个人信息");  
			// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short  
			HSSFRow row = sheet.createRow((int) 0);  
			// 第四步，创建单元格，并设置值表头 设置表头居中  
			HSSFCellStyle style = wb.createCellStyle();  
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式  

			HSSFCell cell = row.createCell(0);  
			cell.setCellValue("id");  
			cell.setCellStyle(style);  
			cell = row.createCell(1);  
			cell.setCellValue("title");  
			cell.setCellStyle(style);
			cell = row.createCell(2);  
			cell.setCellValue("description");  
			cell.setCellStyle(style);  
			cell = row.createCell(3);  
			cell.setCellValue("start date");
			cell = row.createCell(4);  
			cell.setCellValue("end date");
			cell.setCellStyle(style);  
			
			int i = 1;
			for(NotificationItem item: list)
			{
				row = sheet.createRow((int) i++);
				// 第四步，创建单元格，并设置值  
				row.createCell(0).setCellValue((double) item.getId());  
				row.createCell(1).setCellValue(item.getTitle());  
				row.createCell(2).setCellValue(item.getDescription());  
				cell = row.createCell(3);  
				cell.setCellValue(new SimpleDateFormat("yyyy-mm-dd").format(item.getStartdate()));
				cell = row.createCell(4);  
				cell.setCellValue(new SimpleDateFormat("yyyy-mm-dd").format(item.getEnddate()));
			}
			
			// 第六步，将文件存到指定位置  
			try  
			{  
				FileOutputStream fout = new FileOutputStream("person2.xls");  
				wb.write(fout);  
				fout.close();
				wb.close();
			}  
			catch (Exception e)  
			{  
				e.printStackTrace();  
			}
		}
		else
		{
			setResponseText(new StringBuilder("Not implement"));
		}

		NetLog.debug(address, "Leave JavaWebProcess - Process()");
	}

	public static void transMap2Bean2(Map<String, Object> map, Object obj) {  
		if (map == null || obj == null) {  
			return;  
		}
		try {  
			BeanUtils.populate(obj, map);  
		} catch (Exception e) {  
			System.out.println("transMap2Bean2 Error " + e);  
		}  
	}

	public static void transMap2Bean(Map<String, Object> map, Object obj) {
		
			try {  
				BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());  
				PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();  
			
				for (PropertyDescriptor property : propertyDescriptors) {  
					String key = property.getName();  
			
					if (map.containsKey(key)) {  
						Object value = map.get(key);  
		
						Method setter = property.getWriteMethod();  
						setter.invoke(obj, value);  
					}
				}
			} catch (Exception e) {  
				System.out.println("transMap2Bean Error " + e);  
			}  
			
			 return;
		}  

	public static Map<String, Object> transBean2Map(Object obj) {  
		
		if(obj == null){  
			return null;  
		}          
		Map<String, Object> map = new HashMap<String, Object>();  
		try {  
			BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());  
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();  
			for (PropertyDescriptor property : propertyDescriptors) {  
				String key = property.getName();  
				if (!key.equals("class")) {  

					Method getter = property.getReadMethod();  
					Object value = getter.invoke(obj);  
					map.put(key, value);  
				}  
			}  
		} catch (Exception e) {  
			System.out.println("transBean2Map Error " + e);  
		}  
		return map;  
	}  

}
