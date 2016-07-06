package com.rain.test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import com.google.gson.Gson;
import com.rain.data.Person;
import com.rain.data.TextDate;
import com.rain.path.NetPath;
import com.rain.transfer.BeanTransef;

import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

class UploadHolder
{
	String Type;
	String pathName;
}

class StringTest
{
	public StringTest(String value)
	{
		this.value = value;
	}
	public String value;
}

public class JavaTest {
	
	public static void main(String[] args) {
		
		System.out.println("\n1. test testMapToArray()");
		testMapToArray();
		
		System.out.println("\n2. test testUUID()");
		testUUID();
		
		System.out.println("\n3. test testDate()");
		testDate();
		
		System.out.println("\n4. test testIntValueCopy()");
		testIntValueCopy();
		
		System.out.println("\n5. test testStringValueCopy()");
		testStringValueCopy();
		
		System.out.println("\n6. test testBinaryBuffer()");
		testBinaryBuffer();
		
		System.out.println("\n7. test TestMapToString()");
		TestMapToString();
		
		System.out.println("\n8. test TestMapToString()");
		TestStringToMap("{Key1=value1, Key2=value2, Key3=value3}");
		
		System.out.println("\n9. test TestRealPath()");
		TestRealPath("b\\ain1\\eclipse\\workspace\\JavaTest\\a\\index.html", "\\b\\c");
		
		System.out.println("\n10. test TestJsonData()");
		TestJsonData();
		
		System.out.println("\n11. test zip file()");
		TestZipFile();
		
		System.out.println("\n12. test path");
		try {
			TestPath();
		} catch (IOException e) {
			System.out.println("\ntest path failed: " + e.getMessage());
		}
	}
	
	private static void TestPath() throws IOException {
		
		System.out.println("NetPath.combine(\"Path1\", \"Path2\")");
		System.out.println(NetPath.combine("Path1", "Path2"));
		System.out.println();
		
		String a = "root/Path1\\a";
		String b = "root/Path2\\b";
		System.out.println(String.format("NetPath.combine(\"%s\", \"%s\")", a, b));
		System.out.println(NetPath.combine(a, b));
		System.out.println();
		
		a = "root/Path1\\a";
		b = "root/Path2\\b";
		System.out.println(String.format("NetPath.combine(\"%s\", \"%s\")", a, b));
		String c = NetPath.combine(a, b);
		System.out.println(c);
		System.out.println(String.format("NetPath.canonical(\"%s\")", c));
		c = NetPath.canonical(c);
		System.out.println(c);
		System.out.println(String.format("NetPath.relative(\"%s\", \"%s\")", a, c));
		System.out.println("result:" + NetPath.relative(a, c));
		System.out.println(String.format("NetPath.relative(\"%s\", \"%s\")", c, a));
		String b1 = NetPath.relative(c, a);
		System.out.println("result:" + b1);
		System.out.println(String.format("NetPath.canonicalCombine(\"%s\", \"%s\")", a, c));
		String a1 = NetPath.canonicalCombine(c, b1);
		System.out.println("result:" + a1);
		System.out.println();
		
		System.out.println(String.format("NetPath.abslote(\"%s\")", a));
		System.out.println(NetPath.abslote(a));
		System.out.println();
		
		System.out.println(String.format("NetPath.canonical(\"%s\")", a));
		System.out.println(NetPath.canonical(a));
		System.out.println();
		
		System.out.println(String.format("NetPath.abslote(\"%s\")", b));
		System.out.println(NetPath.abslote(b));
		System.out.println();
		
		System.out.println(String.format("NetPath.canonical(\"%s\")", b));
		System.out.println(NetPath.canonical(b));
		System.out.println();
		
		System.out.println(String.format("NetPath.canonicalCombine(\"%s\", \"%s\")", a, b));
		System.out.println(NetPath.canonicalCombine(a, b));
		System.out.println();
	}

	private static void TestZipFile() {
		
		String file = "temp/zipfile.zip";
		
		try {
			ZipFile zf = new ZipFile(file);
			InputStream in = new BufferedInputStream(new FileInputStream(file));  
			ZipInputStream zin = new ZipInputStream(in);  
			ZipEntry ze;  
			while ((ze = zin.getNextEntry()) != null) {  
				if (ze.isDirectory()) {
				} else {
					System.err.println("file - " + ze.getName() + " : "  
	                           + ze.getSize() + " bytes");  
                   long size = ze.getSize();
                   if (size > 0) {  
                       BufferedReader br = new BufferedReader(  
                               new InputStreamReader(zf.getInputStream(ze)));  
                       String line;  
                       while ((line = br.readLine()) != null) {  
                           System.out.println(line);  
                       }
                       br.close();  
                   }  
                   System.out.println();
				}
			}
			zin.close();
			zf.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private static void TestJsonData() {
		
		Person person = new Person();
		Gson gson = new Gson();
		String text = null;
		
		// 1. Bean object to Json string
		System.out.println("\n <1> Bean object to Json string\n");
		try {
			text = gson.getAdapter(Person.class).toJson(person);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(text);

		// 2. Json string to Bean object
		System.out.println("\n <2> Json string to Bean object\n");
		Person data = gson.fromJson(text, Person.class);
		System.out.println("name = " + data.getName());
		System.out.println("age = " + data.getAge());
		System.out.println("date = " + data.getDate());
		
		// 3. Map to Json string
		System.out.println("\n <3> Map to Json string\n");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("key1", "aa");
		map.put("key2", 10);
		map.put("key3", new Date());
		map.put("key4", "bb");
		try {
			text = gson.getAdapter(Map.class).toJson(map);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(text);

		// 4. Map to Bean object
		System.out.println("\n <4> Map to Bean object\n");
		TextDate data2 = BeanTransef.transMap2Bean(map, TextDate.class);
		System.out.println("key1 = " + data2.getKey1());
		System.out.println("key2 = " + data2.getKey2());
		System.out.println("key3 = " + data2.getKey3());
		System.out.println("key4 = " + data2.getKey4());
		
		// 5. Map to Bean object
		System.out.println("\n <5> Map to Bean object\n");
		Map<String, Object> map3 = new HashMap<String, Object>();
		map3.put("key1", "aa");
		map3.put("key2", "10");
		map3.put("key3", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		map3.put("key4", "bb");
		map3.put("key5", "10000");
		map3.put("key6", "10.34");
		map3.put("key7", "12");
		map3.put("key8", "127");
		map3.put("key9", "11.56");
		//map3.put("key10", "true");
		
		System.out.println("map3: " + map3.toString());
		
		TextDate data3 = BeanTransef.transMap2Bean(map3, TextDate.class, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		System.out.println("key1 = " + data3.getKey1());
		System.out.println("key2 = " + data3.getKey2());
		System.out.println("key3 = " + data3.getKey3());
		System.out.println("key4 = " + data3.getKey4());
		System.out.println("key5 = " + data3.getKey5());
		System.out.println("key6 = " + data3.getKey6());
		System.out.println("key7 = " + data3.getKey7());
		System.out.println("key8 = " + data3.getKey8());
		System.out.println("key9 = " + data3.getKey9());
		System.out.println("key10 = " + data3.isKey10());
		
		// 6. get file name
		System.out.println("\n <6> get file name\n");
		
		File orgFile = new File("D:/Temp/temp.dat");
		File tagPath = new File("D:/Temp/");
		File tagFile = null;
		try {
			tagFile = File.createTempFile("temp_", "_"+orgFile.getName(), tagPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (tagFile != null)
		{
			System.out.println("Temp file = " + tagFile.getAbsolutePath());
		}
	}

	private static void testIntValueCopy() {
		Integer a = 100;
		Integer b = 200;
		Integer c = a.intValue();
		a = b;
		
		System.out.println("a="+a);
		System.out.println("b="+b);
		System.out.println("c="+c);
		
	}
	
	private static void testStringValueCopy() {
		String a = "100";
		String b = "200";
		String c = a;
		a = "500";
		
		System.out.println("a="+a);
		System.out.println("b="+b);
		System.out.println("c="+c);
		
		StringTest aa = new StringTest("100");
		StringTest bb = new StringTest("200");
		StringTest cc = aa;
		aa = bb;
		
		System.out.println("aa="+aa.value);
		System.out.println("bb="+bb.value);
		System.out.println("cc="+cc.value);
	}

	private static void testMapToArray()
	{
		// TODO Auto-generated method stub
		Map<String,UploadHolder> uploadMap = new HashMap<String,UploadHolder>();
		
		UploadHolder holder1 = new UploadHolder();
		holder1.Type = "type1";
		holder1.pathName = "path1";
		
		UploadHolder holder2 = new UploadHolder();
		holder2.Type = "type2";
		holder2.pathName = "path2";
		
		uploadMap.put("holder1.Type", holder1);
		uploadMap.put("holder2.Type", holder2);
		
		UploadHolder holder3 = uploadMap.get("holder2.Type");
		if (holder3 == null)
		{
			
		}
		else
		{
			System.out.println(holder3.pathName);
		}
		
		Object[] objArray = uploadMap.values().toArray();
		for(Object obj:objArray)
		{
			System.out.println(obj.toString());
			if (obj instanceof UploadHolder)
			{
				UploadHolder holder = (UploadHolder)obj;
				
				System.out.println(holder.Type);
				System.out.println(holder.pathName);
			}
			
		}
	}

	private static void testUUID()
	{
		UUID id = UUID.randomUUID();
		long uuid1 = id.getMostSignificantBits();
		long uuid2 = id.getLeastSignificantBits();
		
		String id1 = String.format("%x", uuid1);
		String id2 = String.format("%x", uuid2);
		
		System.out.println(uuid1);
		System.out.println(uuid2);
		
		System.out.println(id1);
		System.out.println(id2);
		
		String uuid = id. toString();
		System.out.println(uuid);
	}
	
	private static String dateToString(Date date)
	{
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formater.setTimeZone(TimeZone.getTimeZone("GMT"));
		return formater.format(date);
	}
	
	private static String dateToLocalString(Date date)
	{
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formater.format(date);
	}
	
	private static Date stringToDate(String date) throws ParseException
	{
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formater.setTimeZone(TimeZone.getTimeZone("GMT"));
		return formater.parse(date);
	}
	
	private static void testDate()
	{
		try
		{
			Date date = new Date();
			
			System.out.println("date = "+date.toString()); 
			
			String str1 = dateToString(date);
			System.out.println("date1 = "+str1);
			
			Date date1 = stringToDate(str1);
			Date date0 = stringToDate(str1);
			
			if (date1.compareTo(date0) == 0)
			{
				System.out.println("date1 equals date");
			}
			
			String str2 = dateToLocalString(date1);
			System.out.println("date2 = "+str2);
			
			long ddd = (new Date()).getTime();
			System.out.println("ddd = "+ddd);
			Date ddd1 = new Date(2423470423283l);
			System.out.println("ddd1 = "+dateToLocalString(ddd1));
		}
		catch(Exception ex)
		{
			
		}
		
	}
	
	private static void testBinaryBuffer()
	{
		ByteBuffer buffer = ByteBuffer.allocate(100);
		int a=100;
		buffer.order(ByteOrder.LITTLE_ENDIAN).putInt(a);
		//buffer.flip();
		
		long b=45;
		buffer.putLong(b);
		//buffer.flip();
		
		double c=9.6;
		buffer.putDouble(c);
		
		String value = "abcdefg";
		buffer.put(value.getBytes(), 0, 7);		
		buffer.flip();

		System.out.println("buffer.limit() = "+buffer.limit());
		byte [] content = new byte[buffer.limit()];
		buffer.get(content);
		
		for (int i=0;i<buffer.limit();i++)
		{
			System.out.println("content = "+(char)content[i]);
		}
		
		buffer.flip();
		int a1 = buffer.order(ByteOrder.LITTLE_ENDIAN).getInt();
		
		System.out.println("a1 = "+a1);
	}
	
	public static String TestMapToString()
	{
		Map<String, String> map = new HashMap<String, String>();
		map.put("Key1", "value1");
		map.put("Key2", "value2");
		map.put("Key3", "value1");
		
		System.out.println("Map string: " + map.toString());
		
		return map.toString();
	}
	
	public static void TestStringToMap(String value)
	{
		Map<String, String> map = new HashMap<String, String>();

		if (value.length() < 2 || value.indexOf('{') != 0 || value.lastIndexOf('}') != value.length()-1)
		{
			System.out.println("Map string error");
			return;
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
	}
	
	public static void TestRealPath(String file, String path)
	{
		File file1 = new File(file);
		if (!file1.isAbsolute())
		{
			file1 = new File(System.getProperty("user.dir"),file);
		}
		
		File file2 = new File(path);
		if (!file2.isAbsolute())
		{
			file2 = new File(System.getProperty("user.dir"),path);
		}
		
		System.out.println("file1 absolute path: " + file1.getAbsolutePath());
		System.out.println("file2 absolute path: " + file2.getAbsolutePath());
		
		//File file3 = new File(file2.getAbsolutePath(), file1.getAbsolutePath());
		//System.out.println("file3 path: " + file3.getPath());
		
		int a = file2.getAbsolutePath().compareToIgnoreCase(file1.getAbsolutePath());
		System.out.println("a = " + String.format("%d", a));
		
		int b = "abcdeft".indexOf("cbc");
		System.out.println("b = " + String.format("%d", b));
	}
}
