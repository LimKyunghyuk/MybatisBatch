package batch.common;

public interface BatchResultImpl {
	public void success(String strDate, String endDate, String name);
	public void fail(String strDate, String endDate, String name);
	public void sendSMS(String msg);
	
}
