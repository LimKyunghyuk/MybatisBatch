package batch.main;

import org.apache.log4j.Logger;

import batch.user.action.BatchAction;
import batch.user.action.BatchList;

public class BatchMain{

	protected static Logger logger = Logger.getLogger(BatchMain.class.getName());
	
	public static void main(String[] args) {

		BatchList batchAction = new BatchAction();

		 args = new String[1];
		 args[0] = "DOWNLOAD_TMS_DELI";

		if(args == null || args.length == 0){
			throw new RuntimeException("Could not find parameter 'args[]'");
		}
		
		if("INSERT_GP_ORGANIZATION".equals(args[0])){
			logger.info("INSERT_GP_ORGANIZATION!");
			batchAction.insertGpOrganization();
			
		}else if("INSERT_GP_COMMON_CODE".equals(args[0])){
			logger.info("INSERT_GP_COMMON_CODE!");
			batchAction.insertGpCommonCode();
			
		}else if("INSERT_GP_USER".equals(args[0])){
			logger.info("INSERT_GP_USER!");
			batchAction.insertGpUser();
			
		}else if("SYNC_COMPANY_DEPT".equals(args[0])){
			logger.info("SYNC_COMPANY_DEPT!");
			batchAction.syncCompanyDept();
			
		}else if("SYNC_COMPANY_DUTY".equals(args[0])){
			logger.info("SYNC_COMPANY_DUTY!");
			batchAction.syncCompanyDuty();
			
		}else if("SYNC_RETIRE_USER".equals(args[0])){
			logger.info("SYNC_RETIRE_USER!");
			batchAction.syncRetireUser();
			
		}else if("SYNC_UPDATE_USER".equals(args[0])){
			logger.info("SYNC_UPDATE_USER!");
			batchAction.syncUpdateUser();
			
		}else if("INSERT_VW_SFA_DEPT".equals(args[0])){ 			// IM => SFA
			logger.info("INSERT_VW_SFA_DEPT!");
			batchAction.insertVwSfaDept();
			
		}else if("INSERT_VW_SFA_ORG".equals(args[0])){				// IM => SFA
			logger.info("INSERT_VW_SFA_ORG");
			batchAction.insertVwSfaOrg();
			
		}else if("SAP_UPDATE_PRODUCT_MST".equals(args[0])){ 		// SAP => SFA (unused product)  
			logger.info("SAP_UPDATE_PRODUCT_MST");
			batchAction.updateProductMst();
			
		}else if("SEND_SMS_FOR_BOARD".equals(args[0])){				// If you write a new post on the bulletin board, send SMS message.   
			logger.info("SEND_SMS_FOR_BOARD");
			batchAction.sendSmsForBoard();
			
		}else if("DOWNLOAD_TMS_PLAN".equals(args[0])){				// [2018.11.19] TMS(PLAN) => SFA   
			logger.info("DOWNLOAD_TMS_PLAN");
			batchAction.downloadTmsPlan();
			
		}else if("DOWNLOAD_TMS_DELI".equals(args[0])){  			// [2018.11.19] TMS(DELI) => SFA 
			logger.debug("DOWNLOAD_TMS_DELI");
			batchAction.downloadTmsDeli();
		
		}else if("W21_CUST".equals(args[0])){  						// 통합웹 거래처 동기화 - 거래처 코드 기준
			logger.info("W21_CUST");
			batchAction.w21Cust(args);
			
		}else if("W21_DAY".equals(args[0])){  						//  통합웹 거래처 동기화 - 날짜 기준
			logger.info("W21_DAY");
			batchAction.w21Day(args);
		
		}else if("DELETE_LOG".equals(args[0])){  					//  스마트 SFA 모바일, PC웹 로그 삭제
			logger.info("DELETE_LOG");
			batchAction.deleteLog();
			
		}
		
		logger.info("done.");
	}
}
