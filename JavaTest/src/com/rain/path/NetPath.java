package com.rain.path;

import java.io.File;
import java.io.IOException;

public class NetPath {

	public static String combine(String path1, String path2)
	{
		File p1 = new File(path1);
		File p2 = new File(path2);
		
		return String.format("%s%s%s", p1, File.separatorChar, p2); 
	}
	
	public static String abslote(String path)
	{
		File p = new File(path);
		
		return p.getAbsolutePath();
	}
	
	public static String canonical(String path) throws IOException
	{
		File p = new File(path);
		
		String s = p.getCanonicalPath();

		return s;
	}
	
	public static String canonicalCombine(String path1, String path2) throws IOException
	{
		File p = new File(NetPath.combine(path1, path2));
		
		String s = p.getCanonicalPath();

		return s;
	}
	
	public static String relative(String workPath, String targetPath) throws IOException
	{
		File p1 = new File(workPath);
		File p2 = new File(targetPath);
		
		String workCanonicalPath = p1.getCanonicalPath();
		String targetCanonicalPath = p2.getCanonicalPath();

		String separator = String.format("\\%c", File.separatorChar);
		String[] workArray = workCanonicalPath.split(separator);
		String[] targetArray = targetCanonicalPath.split(separator);
		
		String relative = "";
		int min = workArray.length < targetArray.length ? workArray.length : targetArray.length;
		int k = min;
		for (int i=0; i<min; i++)
		{
			if (!workArray[i].equalsIgnoreCase(targetArray[i]))
			{
				k = i;
				break;
			}
		}
		
		for(int i=0; i<workArray.length-k;i++)
		{
			relative += String.format("..%c", File.separatorChar);
		}
		
		for(int i=k; i<targetArray.length;i++)
		{
			relative += String.format("%s%c", targetArray[i], File.separatorChar);
		}
		
		return relative;
	}
}
