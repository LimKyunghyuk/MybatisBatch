package batch.common;

public interface Storage{
	
	
	public void setName(String name);
	
	public String getName();
	
	void open();
	void close();
	
	
	// TCL
	public void commit();
	public void rollback();
	
}
