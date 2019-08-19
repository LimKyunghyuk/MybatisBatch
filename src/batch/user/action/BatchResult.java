package batch.user.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import batch.common.BatchResultImpl;
import batch.main.BatchMain;
import batch.main.BatchProperty;
import batch.user.db.Mssql;
import batch.user.db.Tibero;
import util.PropertyManager;

public class BatchResult implements BatchResultImpl{

	protected static Logger logger = Logger.getLogger(BatchResult.class.getName());
	
	@Override
	public void success(String strDate, String endDate, String name) {
		
		Tibero tibero = new Tibero("msfamobile");
		tibero.open();
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("STR_DATE", strDate);
		map.put("END_DATE", endDate);
		map.put("BATCH_NAME", name);
		map.put("RESULT", "S");
		
		tibero.insert("insertBatchResult", map);
		tibero.commit();
		tibero.close();
		
		logger.info(name + " batch success!");
	}

	@Override
	public void fail(String strDate, String endDate, String name) {
		
		Tibero tibero = new Tibero("msfamobile");
		tibero.open();
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("STR_DATE", strDate);
		map.put("END_DATE", endDate);
		map.put("BATCH_NAME", name);
		map.put("RESULT", "F");
		
		tibero.insert("insertBatchResult", map);
		tibero.commit();
		tibero.close();
		
		logger.info(name + " batch fail!");
	}
	
	@Override
	public void sendSMS(String msg) {
		
		
		BatchProperty ppt = BatchProperty.getInstrance();
		
		Mssql mssql = new Mssql("sms");
		mssql.open();
		
		String smsPreMsg = ppt.getProperty("SMS_PRE_MSG");
		String smsPostMsg = ppt.getProperty("SMS_POST_MSG");
		String sender = ppt.getProperty("SENDER");
		
		// Get SMS receiver list
		for(String receiver : ppt.getPropertyList("RECEIVER")){
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("SMS_MSG", smsPreMsg + msg + smsPostMsg);
			map.put("SENDER", sender);
			map.put("RECEIVER", receiver);
			
			// Send SMS
			mssql.insert("sendSms", map);
		}
		
		mssql.commit();
		mssql.close();
		
	}

}
