package com.rain.transfer;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BeanTransef {

	public static <T> T transMap2Bean(Map<String, Object> map, Class<T> classOfT, DateFormat format) {
		
		T obj = null;
		try {
			obj = classOfT.newInstance();
			BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());  
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();  
		
			for (PropertyDescriptor property : propertyDescriptors) {  
				String key = property.getName();
		
				if (map.containsKey(key)) {
					Object value = map.get(key);
					Method setter = property.getWriteMethod();

					if (property.getPropertyType().equals(value.getClass()))
					{
						setter.invoke(obj, value);
					}
					else
					{
						String str = value.toString();
						if (property.getPropertyType().equals(String.class))
						{
							setter.invoke(obj, str);
						}
						else if (property.getPropertyType().equals(Date.class))
						{
							setter.invoke(obj, format.parse(str));
						}
						else if (property.getPropertyType().equals(char.class) || property.getPropertyType().equals(Character.class))
						{
							setter.invoke(obj, str.charAt(0));
						}
						else if (property.getPropertyType().equals(int.class) || property.getPropertyType().equals(Integer.class))
						{
							setter.invoke(obj, Integer.parseInt(str));
						}
						else if (property.getPropertyType().equals(double.class) || property.getPropertyType().equals(Double.class))
						{
							setter.invoke(obj, Double.parseDouble(str));
						}
						else if (property.getPropertyType().equals(long.class) || property.getPropertyType().equals(Long.class))
						{
							setter.invoke(obj, Long.parseLong(str));
						}
						else if (property.getPropertyType().equals(short.class) || property.getPropertyType().equals(Short.class))
						{
							setter.invoke(obj, Short.parseShort(str));
						}
						else if (property.getPropertyType().equals(byte.class) || property.getPropertyType().equals(Byte.class))
						{
							setter.invoke(obj, Byte.parseByte(str));
						}
						else if (property.getPropertyType().equals(float.class) || property.getPropertyType().equals(Float.class))
						{
							setter.invoke(obj, Float.parseFloat(str));
						}
						else if (property.getPropertyType().equals(boolean.class) || property.getPropertyType().equals(Boolean.class))
						{
							setter.invoke(obj, Boolean.parseBoolean(str));
						}
						else
						{
							throw new Exception(property.getPropertyType().toString());
						}
					}
				}
				else
				{
					System.err.println(String.format("transMap2Bean Error: '%s' is not match", key));  
				}
			}
		} catch (Exception e) {  
			System.out.println("transMap2Bean Error " + e);  
		}
		
		return obj;
	}
	
	public static <T> T transMap2Bean(Map<String, Object> map, Class<T> classOfT) {
		
		return transMap2Bean(map, classOfT, DateFormat.getDateInstance(DateFormat.DEFAULT));
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
