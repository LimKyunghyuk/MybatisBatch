package batch.common;

import java.util.HashMap;
import java.util.Map;

import batch.main.BatchProperty;

public class Sms extends _CommonQuery {

	final static String MYBATIS_ID = "sms";	// in mybatis-config.xml
	final static String NAME_SAPCE = "sms";	// in mapper.xml

	private static Sms instance;

	// constructor
	Sms() {
		super(MYBATIS_ID, NAME_SAPCE);
	}

	// singleton
	public static Sms getInstance() {
		
		if (instance == null) {
			synchronized (Sms.class) {
				instance = new Sms();
			}
		}
		return instance;
	}

	public void sendSms(String name) {
		// TODO Auto-generated method stub		
		
		BatchProperty ppt = BatchProperty.getInstrance();
				
		String smsPreMsg = ppt.getProperty("SMS_PRE_MSG");
		String smsPostMsg = ppt.getProperty("SMS_POST_MSG");
		String sender = ppt.getProperty("SENDER");
		
		// Get SMS receiver list
		for(String receiver : ppt.getPropertyList("RECEIVER")){
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("SMS_MSG", smsPreMsg + name + smsPostMsg);
			map.put("SENDER", sender);
			map.put("RECEIVER", receiver);
			
			// Send SMS
			getSession().insert("sms.sendSms", map);
		}
		
		getSession().commit();
	}
}