package com.rain.jarfile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ReadJarFile1 {

	ReadJarFile1() {
		System.out.println("here can test 'resource/jarfile.txt' file");
	}
	
	public void readFile(final String filePath) {
		
		if (null == filePath || filePath.isEmpty()) {
			return;
		}

        if (filePath.startsWith("classpath:")) {
            String newPath = filePath.replaceFirst("classpath:", "/");
            
            InputStream is = this.getClass().getResourceAsStream(newPath);
            if (null != is) {
            	BufferedReader br = null;
	            try {
		        	br = new BufferedReader(new InputStreamReader(is));
		    		String s="";
		    		while((s=br.readLine())!=null)
		    			System.out.println(s);
	            } catch (Exception e) {
	            	e.printStackTrace();
	            } finally {
	            	try {
						is.close();
					} catch (IOException e) {
						// skip
					}
	            	if (null != br) {
	            		try {
							br.close();
						} catch (IOException e) {
							// skip
						}
	            	}
	            }
            } else {
            	System.out.println("Failed for this.getClass().getResourceAsStream('" + newPath + "')");
            }
        } else {
        	File file = new File(filePath);
	        BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(file));
				String tempString = null;
				while ((tempString = reader.readLine()) != null) {
					System.out.println(tempString);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (reader != null)
				{
					try {
						reader.close();
					} catch (IOException e) {
						// do nothing
					}
				}
			}
        }
	}
	
	public String getJarFile(final String filePath) {
		String newPath = null;
        if (filePath.startsWith("classpath:")) {
            String temp = filePath.replaceFirst("classpath:", "/");
            URL url = this.getClass().getResource(temp);
            if (null != url) {
            	System.out.println("url: " + url.toString());
	            try {
	                File file = new File(url.toURI());
	                newPath = file.toString();
	                System.out.println("new path name: " + newPath);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
            } else {
            	System.out.println("Failed for this.getClass().getResource('" + temp + "')");
            }
        }
        
        return newPath;
	}
	
	public void readJarFile() {
		this.readFile("classpath:resource/jarfile.txt");
	}
	
	public static ReadJarFile1 build() {
		return new ReadJarFile1();
	}
	
	public List<String> expandJarFiles(String filter) {
        String jar = ReadJarFile.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        filter = filter.toLowerCase();
        List<String> list = new LinkedList<String>();
        JarFile jf = null;
        try {
            jf = new JarFile(jar);
            Enumeration<JarEntry> es = jf.entries();
            while (es.hasMoreElements()) {
                String resname = es.nextElement().getName();
                if (resname.toLowerCase().lastIndexOf(filter) != -1) list.add(resname);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (jf != null) {
                try {
                    jf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        for (String s : list) {
            URL url = ReadJarFile1.class.getResource("/".concat(s));
            System.out.println(url.toString());
        }
		return list;
	}
}
