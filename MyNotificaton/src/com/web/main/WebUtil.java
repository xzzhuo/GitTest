package com.web.main;

import io.netty.util.internal.SystemPropertyUtil;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import bdx.net.netty.NetProcess;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class WebUtil {

	private NetProcess mProcess = null;
	private Map<String, Object> mParam = new HashMap<String, Object>();
	private String mTemplatePath = ".";
	
	public WebUtil(NetProcess process)
	{
		mProcess = process;
		mTemplatePath = SystemPropertyUtil.get("user.dir");
	}
	
	public void setTemplatePath(String path)
	{
		mTemplatePath = path;
	}
	
	public void assign(String key, Object value)
	{
		mParam.put(key, value);
	}
	
	public void display(String name)
	{
		try {
			Configuration cfg = new Configuration();
			//cfg.setEncoding(locale, encoding);
			cfg.setDirectoryForTemplateLoading(new File(mTemplatePath));
			Template template = cfg.getTemplate(name, "utf-8");
			Map<String, String> root = new HashMap<String, String>();
			root.put("name", "cxl");
			root.put("age", "25");
			
			StringWriter sw = new StringWriter();
			template.process(mParam, sw);
			sw.flush();
			mProcess.setResponseText(new StringBuilder(sw.getBuffer()));
			//mProcess.setResponseText(new StringBuilder("中国"));
		} catch (TemplateException e) {
			mProcess.setResponseText(new StringBuilder(e.getMessage()));
			e.printStackTrace();
		} catch (IOException e) {
			mProcess.setResponseText(new StringBuilder(e.getMessage()));
			e.printStackTrace();
		}
	}
}
