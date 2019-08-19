package batch.main;

import util.PropertyManager;

public class BatchProperty extends PropertyManager {
	
	final static String MODE = System.getProperty("mode");
	
	final static String PATH_DEV  = "config/dev/";
	final static String PATH_REAL = "config/real/";
	
	final static String FILE_CONFIG = "config.properties";
	final static String FILE_LOG4J = "log4j.properties";
	
	static BatchProperty batchProperty;
	
	public static BatchProperty getInstrance() {

		if (batchProperty == null) {
			synchronized (BatchProperty.class) {
				batchProperty = new BatchProperty();
			}
		}

		return batchProperty;
	}
	
	
	
	BatchProperty(){
		
		if(MODE == null || "".equals(MODE)) throw new RuntimeException("Could not find property '-Dmode'");
		
		String path = getPath(FILE_CONFIG);
		
		PropertyLoad(path);
	}
	
	public static boolean isReal(){
		
		return "real".equals(MODE.toLowerCase());
	}
	
	public String getPath(String fileName){
		
		String path = null;
		
		if("dev".equals(MODE.toLowerCase())){
			path = PATH_DEV + fileName;
		}else if("real".equals(MODE.toLowerCase())){
			path = PATH_REAL + fileName;
		}
	
		return super.getPath(path); 
	}
	
	public static String getLog4jPath(){
		
		BatchProperty bp = BatchProperty.getInstrance();
		
		return bp.getPath(FILE_LOG4J);
	}
	

	public void show(){
		
		logger.info("##############################################################");

		logger.info("##");
		logger.info("## [" + MODE + "]");
		logger.info("## " + FILE_CONFIG + " file path : " + getPath(FILE_CONFIG));
		logger.info("## " + FILE_LOG4J + " file Path : " + getPath(FILE_LOG4J));
		logger.info("##");
		
		logger.info("##############################################################");
	}
	
}
