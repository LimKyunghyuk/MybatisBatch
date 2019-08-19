package util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertyManager {

	protected static Logger logger = Logger.getLogger(PropertyManager.class.getName());

//	final static String PATH_DEV  = "config/dev/";
//	final static String PATH_REAL = "config/real/";
//	final static String FILE_CONFIG = "config.properties";
	
	private Properties properties;
	
	/*
	
	static PropertyManager propertyManager;
	
	public static PropertyManager getInstrance() {

		if (propertyManager == null) {
			synchronized (PropertyManager.class) {
				propertyManager = new PropertyManager();
			}
		}

		return propertyManager;
	}
	*/
	
	public String getPath(String fileName){
		
		if(fileName == null) throw new RuntimeException("Could not find parameter 'fileName'");
		
		ClassLoader cl;
		cl = Thread.currentThread().getContextClassLoader();
		if(cl == null){
			cl = ClassLoader.getSystemClassLoader();
		}
		
		return cl.getResource(fileName).getFile().replaceAll("%20", " "); 
	}
	
	/*
	public static String getPath(String fileName){
		
		String path = null;
		String mode = System.getProperty("mode");
		
		if(fileName == null) throw new RuntimeException("Could not find parameter 'fileName'");
		
		if(mode == null) throw new RuntimeException("Could not find property '-Dmode'");	
		
		if("dev".equals(mode.toLowerCase())){
			path = PATH_DEV + fileName;
		}else if("real".equals(mode.toLowerCase())){
			path = PATH_REAL + fileName;
		}
		
		ClassLoader cl;
		cl = Thread.currentThread().getContextClassLoader();
		if(cl == null){
			cl = ClassLoader.getSystemClassLoader();
		}
		
		return cl.getResource(path).getFile().replaceAll("%20", " "); 
	}
	*/
	
	/*
	public PropertyManager() {
		
		properties = new Properties();
		String path = getPath(FILE_CONFIG);
		logger.debug("path : " + path);
		
		try {
			
			FileInputStream fileInputStream = new FileInputStream(path);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
			properties.load(inputStreamReader);
					
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	*/
	
	public PropertyManager(){
		properties = new Properties();
	}
	
	public void PropertyLoad(String path) {
		
		try {
			
			FileInputStream fileInputStream = new FileInputStream(path);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
			properties.load(inputStreamReader);
					
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public String getProperty(String key){
		
		String rtnStr = properties.getProperty(key);
		logger.debug(key + ":" + rtnStr);
	
		return rtnStr;
	}
	
	public List<String> getPropertyList(String key){
		
		if(properties == null) return null;
		
		String temp = properties.getProperty(key);
		String[] list = temp.split(",");
		
		List<String> rtnList = new ArrayList<String>();
				
		for(String itme : list){
			rtnList.add(itme);
		}
		
		return rtnList;
	}
	
	
	Properties getProperty(){
		return properties;
	}
	
}
