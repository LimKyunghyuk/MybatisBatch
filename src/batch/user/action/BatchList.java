package batch.user.action;

public interface BatchList {
	
	// 2018.12.04 개선
	public void insertGpOrganization();

	public void insertGpCommonCode();

	public void insertGpUser();

	public void syncCompanyDept();

	public void syncCompanyDuty();

	public void syncRetireUser();

	public void syncUpdateUser();
	
	public void insertVwSfaDept();
	
	public void insertVwSfaOrg();
	
	public void updateProductMst();

	public void sendSmsForBoard();
	
	// 
	public void downloadTmsPlan();

	public void downloadTmsDeli();
	
	public void w21Cust(String[] args);
	
	public void w21Day(String[] args);

	public void deleteLog();
	
}
