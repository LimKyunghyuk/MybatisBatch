package batch.common;

import org.apache.log4j.Logger;

import batch.main.BatchProperty;
import util.Timer;


public class BatchManager {
	
protected static Logger logger = Logger.getLogger(BatchManager.class.getName());
	
	private String batchName;
	private StorageMap storageMap;
	private BatchResultImpl batchResult;
	
	public BatchManager(){
		this(null);
	}
	
	public BatchManager(String name){
		
		this.batchName = name;
		storageMap = new StorageMap();
	}

	public String getName() {
		return batchName;
	}

	public void setName(String name) {
		this.batchName = name;
	}


	public interface Transaction{
		boolean onTransaction(StorageMap storageMap) throws Exception;
	}
	
	public void putStorage(Storage storage){
		
		// 적합성 검사
		storageMap.put(storage);
	}
	
	public void setTransaction(Transaction callback) {
		logger.debug("setTransaction");
		
		if(batchResult == null){
			logger.error("Could not call setBatchResult() in main.");
			return;
		}
		
		String strDate = Timer.getTime();
		String endDate = strDate;
		
		storageMap.connection();
		
		try{

			boolean success = callback.onTransaction(storageMap);
			
			if(success){
				
				storageMap.commit();
				endDate = Timer.getTime();
				
				batchResult.success(strDate, endDate, batchName);
				
			}else{
				throw new Exception();
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
			storageMap.rollback();
			
			// save batch result 
			endDate = Timer.getTime();
			batchResult.fail(strDate, endDate, batchName);
			
			// send SMS
			if(BatchProperty.isReal()){
				
				logger.info("오류 문자 발송");
				batchResult.sendSMS(batchName);	
			}
			
		}finally{
			
			storageMap.close();
		}
	}
	
	
	public void setBatchResult(BatchResultImpl batchResult){
		this.batchResult = batchResult;
	}
	
}
