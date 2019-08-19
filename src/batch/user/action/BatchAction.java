package batch.user.action;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sap.conn.jco.JCoTable;

import batch.common.BatchManager;
import batch.common.StorageMap;
import batch.main.BatchSetting;
import batch.user.db.Mssql;
import batch.user.db.Sap;
import batch.user.db.Sftp;
import batch.user.db.Tibero;

public class BatchAction extends BatchSetting implements BatchList {

	protected static Logger logger = Logger.getLogger(BatchAction.class.getName());

	@Override
	public void insertGpOrganization() {
		logger.debug("start insertGpOrganization()");

		BatchManager batch = new BatchManager("부서 정보 업데이트");

		batch.setBatchResult(new BatchResult());
		batch.putStorage(new Tibero("msfamobile"));

		batch.setTransaction(new BatchManager.Transaction() {

			@Override
			public boolean onTransaction(StorageMap storageMap) throws Exception {

				Tibero msfamobile = (Tibero) storageMap.get("msfamobile");

				List<HashMap<String, Object>> list = msfamobile.select("getGpOrganization");

				msfamobile.delete("deleteGpAdditionalJob");

				int ordId = 0;

				for (HashMap<String, Object> map : list) {

					map.put("ORG_ID", ++ordId);
					msfamobile.insert("addGpOrganization", map);
				}
				return true;
			}
		});
	}

	@Override
	public void insertGpCommonCode() {
		logger.debug("start insertGpCommonCode()");

		BatchManager batch = new BatchManager("공통코드 업데이트");

		batch.setBatchResult(new BatchResult());
		batch.putStorage(new Tibero("lcpcweb"));
		batch.putStorage(new Tibero("msfamobile"));

		batch.setTransaction(new BatchManager.Transaction() {

			@Override
			public boolean onTransaction(StorageMap storageMap) throws Exception {

				Tibero lcpcweb = (Tibero) storageMap.get("lcpcweb");
				Tibero msfamobile = (Tibero) storageMap.get("msfamobile");

				List<HashMap<String, Object>> list = lcpcweb.select("getGpCommonCode");

				msfamobile.delete("deleteGpCommonCode");

				int cdId = 0;

				for (HashMap<String, Object> map : list) {

					map.put("CD_ID", ++cdId);
					msfamobile.insert("addGpCommonCode", map);
				}

				return true;
			}
		});

	}

	@Override
	public void insertGpUser() {
		logger.debug("start insertGpUser()");

		BatchManager batch = new BatchManager("사용자 정보 업데이트");

		batch.setBatchResult(new BatchResult());
		batch.putStorage(new Tibero("lcpcweb"));
		batch.putStorage(new Tibero("msfamobile"));

		batch.setTransaction(new BatchManager.Transaction() {

			@Override
			public boolean onTransaction(StorageMap storageMap) throws Exception {

				Tibero lcpcweb = (Tibero) storageMap.get("lcpcweb");
				Tibero msfamobile = (Tibero) storageMap.get("msfamobile");

				msfamobile.delete("deleteGpUser");									// 기존 사용자를 모두 삭제한다.
				
				List<HashMap<String, Object>> list = lcpcweb.select("getGpUser");	// 새로운 사용자 리스트를 가져온다.

				for (HashMap<String, Object> map : list) {

					msfamobile.insert("addGpUser", map);							// 가져온 사용자의 정보를 추가 한다.
				}

				return true;
			}
		});

	}

	@Override
	public void syncRetireUser() {
		logger.debug("start syncRetireUser()");

		BatchManager batch = new BatchManager("퇴직자 처리 동기화");
		batch.setBatchResult(new BatchResult());
		batch.putStorage(new Tibero("msfamobile"));

		batch.setTransaction(new BatchManager.Transaction() {

			@Override
			public boolean onTransaction(StorageMap storageMap) throws Exception {

				Tibero msfamobile = (Tibero) storageMap.get("msfamobile");

				List<HashMap<String, Object>> list = msfamobile.select("getRetireUser");

				for (HashMap<String, Object> map : list) {
					msfamobile.update("updateRetireUser", map);
					msfamobile.insert("addRetireLog", map);
					msfamobile.delete("deleteRetireUserMdn", map);
					msfamobile.delete("deleteRetireAdditionalJob", map);
				}

				return true;
			}
		});

	}

	@Override
	public void syncCompanyDept() {
		logger.debug("start syncCompanyDept()");

		BatchManager batch = new BatchManager("사용자 정보 업데이트");

		batch.setBatchResult(new BatchResult());
		batch.putStorage(new Tibero("msfamobile"));

		batch.setTransaction(new BatchManager.Transaction() {

			@Override
			public boolean onTransaction(StorageMap storageMap) throws Exception {

				Tibero msfamobile = (Tibero) storageMap.get("msfamobile");

				msfamobile.delete("deleteCompanyDept");
				msfamobile.insert("addCompanyDept", null);

				return true;
			}
		});
	}

	@Override
	public void syncCompanyDuty() {
		logger.debug("start syncCompanyDuty()");

		BatchManager batch = new BatchManager("보직 정보 동기화");
		batch.setBatchResult(new BatchResult());
		batch.putStorage(new Tibero("msfamobile"));

		batch.setTransaction(new BatchManager.Transaction() {

			@Override
			public boolean onTransaction(StorageMap storageMap) throws Exception {

				Tibero msfamobile = (Tibero) storageMap.get("msfamobile");

				msfamobile.delete("deleteCompanyDuty");
				msfamobile.insert("addCompanyDuty", null);

				return true;
			}
		});
	}
	
	@Override
	public void syncUpdateUser() {
		logger.debug("start syncUpdateUser()");

		BatchManager batch = new BatchManager("사용자 정보 동기화");
		batch.setBatchResult(new BatchResult());
		batch.putStorage(new Tibero("msfamobile"));

		batch.setTransaction(new BatchManager.Transaction() {

			@Override
			public boolean onTransaction(StorageMap storageMap) throws Exception {

				Tibero msfamobile = (Tibero) storageMap.get("msfamobile");

				List<HashMap<String, Object>> list = msfamobile.select("getModifyUser");

				for (HashMap<String, Object> map : list) {

					if ("Y".equals((String) map.get("RETIRE_YN"))) {
						msfamobile.update("updateRetireUser", map);
						msfamobile.insert("addRetireLog", map);
						msfamobile.delete("deleteRetireUserMdn", map);
						msfamobile.delete("deleteRetireAdditionalJob", map);
					} else {
						msfamobile.update("updateUser", map);
					}
				}

				return true;
			}
		});
	}

	@Override
	public void insertVwSfaDept() {
		logger.debug("start insertVwSfaDept()");

		BatchManager batch = new BatchManager("IM 부서 정보");
		batch.setBatchResult(new BatchResult());

		batch.putStorage(new Mssql("im"));
		batch.putStorage(new Tibero("lcpcweb"));

		batch.setTransaction(new BatchManager.Transaction() {

			@Override
			public boolean onTransaction(StorageMap storageMap) throws Exception {

				Mssql im = (Mssql) storageMap.get("im");
				Tibero lcpcweb = (Tibero) storageMap.get("lcpcweb");

				List<HashMap<String, Object>> list = im.select("getVwSFADept");

				lcpcweb.delete("deleteVwSFADept");

				for (HashMap<String, Object> map : list) {

					lcpcweb.insert("addVwSFADept", map);
				}

				return true;
			}
		});
	}

	@Override
	public void insertVwSfaOrg() {
		logger.debug("start insertVwSfaOrg()");

		BatchManager batch = new BatchManager("IM 조직 레벨");
		batch.setBatchResult(new BatchResult());

		batch.putStorage(new Mssql("im"));
		batch.putStorage(new Tibero("lcpcweb"));

		batch.setTransaction(new BatchManager.Transaction() {

			@Override
			public boolean onTransaction(StorageMap storageMap) throws Exception {

				Mssql im = (Mssql) storageMap.get("im");
				Tibero lcpcweb = (Tibero) storageMap.get("lcpcweb");

				List<HashMap<String, Object>> list = im.select("getVwSFAOrg");

				lcpcweb.delete("deleteVwSFAOrg");

				for (HashMap<String, Object> map : list) {

					lcpcweb.insert("addVwSFAOrg", map);
				}

				return true;
			}
		});
	}

	@Override
	public void sendSmsForBoard() {

		BatchManager batch = new BatchManager("게시판 업로드 알림(kakao)");

		batch.setBatchResult(new BatchResult());
		batch.putStorage(new Tibero("lcpcweb"));
		batch.putStorage(new Mssql("kakao"));

		batch.setTransaction(new BatchManager.Transaction() {

			@Override
			public boolean onTransaction(StorageMap storageMap) throws Exception {

				Tibero lcpcweb = (Tibero) storageMap.get("lcpcweb");
				Mssql kakao = (Mssql) storageMap.get("kakao");

				List<HashMap<String, Object>> list = lcpcweb.select("selectNewPostInBoard");

				if (list.size() <= 0)
					return true; // 새로운 글 없음

				String msg = (String) (list.get(0).get("SMS_MSG"));
				Map<String, Object> kakaoMap = new HashMap<String, Object>();

				kakaoMap.put("BBS_SEQ", list.get(0).get("BBS_SEQ"));
				if (msg == null) {

					// error 처리
					lcpcweb.update("updateAllNewPostInBoard", kakaoMap);
					return true;
				}

				if (true == msg.contains("판매지원 게시판에 새로운 글을 등록하였습니다.")) {

					kakaoMap.put("DEST_PHONE", list.get(0).get("RECVER"));
					kakaoMap.put("SEND_PHONE", "-");
					kakaoMap.put("MSG_BODY", msg);
					kakaoMap.put("TEMPLATE_CODE", "-");
					kakaoMap.put("RE_TYPE", "SMS");
					kakaoMap.put("RE_BODY", null);

					kakao.insert("sendKakao", kakaoMap);

					kakaoMap.put("BBS_SEQ", list.get(0).get("BBS_SEQ"));
					lcpcweb.update("updateNewPostInBoard", kakaoMap);
				} else {
					// error 처리
					lcpcweb.update("updateAllNewPostInBoard", kakaoMap);
				}

				return true;
			}
		});
	}

	@Override
	public void downloadTmsPlan() {
		logger.debug("downloadTmsPlan()");
		
		BatchManager batch = new BatchManager("배송 예정 정보");
		batch.setBatchResult(new BatchResult());
		
		batch.putStorage(new Sftp("TMS"));
		batch.putStorage(new Sap("SAP"));
		
		batch.setTransaction(new BatchManager.Transaction() {
			
			@Override
			public boolean onTransaction(StorageMap storageMap) throws Exception {
				
				Sftp tms = (Sftp)storageMap.get("TMS");
				Sap sap = (Sap)storageMap.get("SAP");
				
				Map<String, List<String>> files = tms.select("/SEND/PLAN", "B2BTMS");

				for(String fileName : files.keySet()){

					// 파일 이름 출력
					logger.debug("<" + fileName + ">");
					final List<String> contents = files.get(fileName);
					
					// 사전 검증
					boolean errflag = false;
					
					for(String line : contents){
						
						String[] elements = line.split("\\|", -1);

						for (String str : elements) {
							logger.debug("[" + str + "]");
						}
						
						if(12 != elements.length)  {
							errflag = true;
							break;
						}
						
						logger.debug("");
					}
					
					// Set SAP parameter 
					Sap.SapParameter param = new Sap.SapParameter() {
						
						@Override
						public void setSapParameter(JCoTable sapTable){
							
							for(String line : contents){
								
								String[] elements = line.split("\\|", -1);

								sapTable.appendRow();
								
								sapTable.setValue("IF_SCD", 		elements[0]);
								sapTable.setValue("CLIENT_CD",		elements[1]);
								sapTable.setValue("CLIENT_NM",		elements[2]);
								sapTable.setValue("ORG_ORD_NO",		elements[3]);
								sapTable.setValue("CENTER_CD",		elements[4]);

								sapTable.setValue("STOP_CD", 		elements[5]);
								sapTable.setValue("STOP_NM", 		elements[6]);
								sapTable.setValue("DELI_DY", 		elements[7]);
								sapTable.setValue("VHCL_NO", 		elements[8]);

								sapTable.setValue("DRV_NM", 		elements[9]);
								sapTable.setValue("DRV_TEL_NO",		elements[10]);
								sapTable.setValue("TO_STOP_DATE",	elements[11]);
								
							}
						}
					};
					
					
					
					logger.debug("errflag : " + errflag);
					if(errflag){
						logger.error("error! The number of elements is different in : <" + fileName + "> file!");
						tms.move("/SEND/DELI", "/SEND/PLAN/backup/error", fileName);
						
					}else{
					
						String resultCode = sap.insert("ZSD_TMS_DELIPLAN", "GT_ETAB", param , "RESULTCODE");
						
						if("S".equals(resultCode)){
							logger.info("success!  <" + fileName + "> file!");
							tms.move("/SEND/PLAN", "/SEND/PLAN/backup/success", fileName);	
						}else{
							logger.error("error!  The SAP failed to update <" + fileName + "> file!");
							tms.move("/SEND/PLAN", "/SEND/PLAN/backup/error", fileName);
						}
					}
				}

				return true;
			}
		});
		
	}
	
	
	@Override
	public void downloadTmsDeli() {
		logger.debug("downloadTmsDeli()");
		
		BatchManager batch = new BatchManager("배송 도착 정보");
		batch.setBatchResult(new BatchResult());
		
		batch.putStorage(new Sftp("TMS"));
		batch.putStorage(new Sap("SAP"));
		
		batch.setTransaction(new BatchManager.Transaction() {
			
			@Override
			public boolean onTransaction(StorageMap storageMap) throws Exception {
				
				Sftp tms = (Sftp)storageMap.get("TMS");
				Sap sap = (Sap)storageMap.get("SAP");
				
				Map<String, List<String>> files = tms.select("/SEND/DELI", "B2BTMS");

				for(String fileName : files.keySet()){

					// 파일 이름 출력
					logger.debug("<" + fileName + ">");
					final List<String> contents = files.get(fileName);

					// 사전 검증
					boolean errflag = false;
					
					for(String line : contents){
						
						String[] elements = line.split("\\|", -1);

						for (String str : elements) {
							logger.debug("[" + str + "]");
						}
						
						if(15 != elements.length)  {
							errflag = true;
							break;
						}
						
						logger.debug("");
					}
					
					// Set SAP parameter 
					Sap.SapParameter param = new Sap.SapParameter() {
						
						@Override
						public void setSapParameter(JCoTable sapTable){
							
							for(String line : contents){
								
								String[] elements = line.split("\\|", -1);

								sapTable.appendRow();
								
								sapTable.setValue("IF_SCD", 		elements[0]);
								sapTable.setValue("CLIENT_CD",		elements[1]);
								sapTable.setValue("CLIENT_NM",		elements[2]);
								sapTable.setValue("ORG_ORD_NO",		elements[3]);
								sapTable.setValue("CENTER_CD",		elements[4]);

								sapTable.setValue("STOP_CD", 		elements[5]);
								sapTable.setValue("STOP_NM", 		elements[6]);
								sapTable.setValue("DELI_DY", 		elements[7]);
								sapTable.setValue("VHCL_NO", 		elements[8]);

								sapTable.setValue("DRV_NM", 		elements[9]);
								sapTable.setValue("DRV_TEL_NO",		elements[10]);
								sapTable.setValue("TO_STOP_DATE",	elements[11]);
								
								// 2019.06.26 추가
								sapTable.setValue("DRV_EMP_NO1",		elements[12]);
								sapTable.setValue("SUB_DRV_EMP_NO1",	elements[13]);
								sapTable.setValue("SUB_DRV_EMP_NO2",	elements[14]);
								
							}
						}
					};
					
					
					
					logger.debug("errflag : " + errflag);
					if(errflag){
						logger.error("error! The number of elements is different in : <" + fileName + "> file!");
						tms.move("/SEND/DELI", "/SEND/DELI/backup/error", fileName);
						
					}else{
					
						String resultCode = sap.insert("ZSD_TMS_DELISTOP", "GT_ETAB", param , "RESULTCODE");
						
						if("S".equals(resultCode)){
							tms.move("/SEND/DELI", "/SEND/DELI/backup/success", fileName);	
						}else{
							logger.error("error!  The SAP failed to update <" + fileName + "> file!");
							tms.move("/SEND/DELI", "/SEND/DELI/backup/error", fileName);
						}
					}
				}

				return true;
			}
		});
		
	}

	@Override
	public void updateProductMst() {
		logger.debug("start updateProductMst()");

		BatchManager batch = new BatchManager("미사용 제품 코드 처리");
		batch.setBatchResult(new BatchResult());

		batch.putStorage(new Sap("SAP"));
		batch.putStorage(new Tibero("lcpcweb"));

		batch.setTransaction(new BatchManager.Transaction() {

			@Override
			public boolean onTransaction(StorageMap storageMap) throws Exception {

				Sap sap = (Sap) storageMap.get("SAP");
				Tibero lcpcweb = (Tibero) storageMap.get("lcpcweb");

				Map<String, Object> sapParamMap = new HashMap<String, Object>();
				sapParamMap.put("I_VKORG", "1000");
				List<Map<String, Object>> list = sap.select("ZSD_WEB_MATNR_SEND", sapParamMap, "E_RESULT");

				Map<String, Object> paramMap = new HashMap<String, Object>();

				for (int i = 0; i < list.size(); i++) {

					paramMap.put("ITEM_CD", list.get(i).get("ITEM_CD"));

					if ("20".equals(list.get(i).get("USE_CD"))) {
						paramMap.put("DISCOUNT_YN", "Y");
					} else {
						paramMap.put("DISCOUNT_YN", "N");
					}

					lcpcweb.update("updateBarcodeMst", paramMap);

				}

				return true;
			}
		});

	}

	@Override
	public void w21Cust(final String[] args) {
		logger.debug("w21Cust");

		BatchManager batch = new BatchManager("w21");

		batch.setBatchResult(new BatchResult());
		batch.putStorage(new Tibero("wision21"));
		batch.putStorage(new Sap("sap"));

		batch.setTransaction(new BatchManager.Transaction() {

			@Override
			public boolean onTransaction(StorageMap storageMap) throws Exception {

				Tibero wision21 = (Tibero) storageMap.get("wision21");
				Sap sap = (Sap) storageMap.get("sap");

				
//				long strCust = 10000000L;
//				long endCust = 20000000L;
//				long interval = 10000L;

				if(args.length != 4) {
					logger.debug("파라메터 부족 : " + args.length);
					for(String arg : args){
						logger.debug("파라메터  : " + arg);
					}
					
					return false;
				}
				
				if(null == args[1] || "".equals(args[1])) {
					logger.debug("첫 번째 파라메터 없음 : " + args[1]);
					return false;
				}
				
				if(null == args[2] || "".equals(args[2])) {
					logger.debug("두 번째 파라메터 없음 : " + args[2]);
					return false;
				}
				
				if(null == args[3] || "".equals(args[3])) {
					logger.debug("세 번째 파라메터 없음 : " + args[3]);
					return false;
				}
				
				long strCust = Long.parseLong(args[1]);
				long endCust = Long.parseLong(args[2]);
				long interval = Long.parseLong(args[3]);
				
				
				// long strCust = 1009L;
				// long endCust = 1009L;
				// long interval = 1L;

				long normalCnt = 0L;
				long abnormalCnt = 0L;
				List<Map<String, Object>> custList = null;
				Map<String, Object> importParamMap = new HashMap<String, Object>();
				for (long i = strCust; i <= endCust; i += interval) {

					logger.debug("");
					logger.debug("I_KUNNR : " + i);
					logger.debug("I_KUNNR_T : " + (i + interval));
					logger.debug("endCust : " + endCust);
					logger.debug("interval : " + interval);
					logger.debug("[index] : " + i);

					if (0 != strCust)
						importParamMap.put("I_KUNNR", "00" + Long.toString(i));
					if (0 != endCust)
						importParamMap.put("I_KUNNR_T", "00" + Long.toString(i + interval));

					logger.debug("importParamMap>" + importParamMap);

					// get custMst in SAP
					custList = sap.select("ZSD_UPSO_CUST_SEND", importParamMap, "E_TAB");

					// insert custMst into wision21
					for (Map<String, Object> custMst : custList) {

						// Classify normal custMst and abnormal custcustMst
						if (isNormal(custMst)) {
							normalCnt++;
							wision21.insert("insertCustMstSus", custMst);

						} else {
							abnormalCnt++;
							wision21.insert("insertCustMstErr", custMst);
						}
					}

					wision21.commit();

				} // end for

				logger.debug("normalCnt :" + normalCnt + "건");
				logger.debug("abnormalCnt :" + abnormalCnt + "건");

				return true;
			}
		});

	}

	@Override
	public void w21Day(final String[] args) {
		logger.debug("w21Day");

		BatchManager batch = new BatchManager("w21");

		batch.setBatchResult(new BatchResult());
		batch.putStorage(new Tibero("wision21"));
		batch.putStorage(new Sap("sap"));

		batch.setTransaction(new BatchManager.Transaction() {

			@Override
			public boolean onTransaction(StorageMap storageMap) throws Exception {

				Tibero wision21 = (Tibero) storageMap.get("wision21");
				Sap sap = (Sap) storageMap.get("sap");


				logger.debug("첫 번째 파라메터 : " + args[1]);
				logger.debug("두 번째 파라메터 : " + args[2]);
				
				if(null == args[1] || "".equals(args[1])) return false;
				if(null == args[2] || "".equals(args[2])) return false;

				DateFormat df = new SimpleDateFormat("yyyyMMdd");
				
				Date strDt = df.parse(args[1]);
				Date endDt = df.parse(args[2]);
				
				logger.debug("시작일 : " + args[1]);
				logger.debug("종료일 : " + args[2]);
				
				long day = endDt.getTime() - strDt.getTime();
				day = day / (24 * 60 * 60 * 1000);

				day = Math.abs(day);
				
				logger.debug("차이 : " + day);

				Calendar cal = Calendar.getInstance();
				cal.setTime(strDt);
				
				List<Map<String, Object>> custList = null;
				Map<String, Object> importParamMap = new HashMap<String, Object>();

				long normalCnt = 0L;
				long abnormalCnt = 0L;
				
				String dayStr;
				
				for (int i = 0; i <= day; i++) {

					dayStr = df.format(cal.getTime());
					logger.debug("대상 날짜 : " + dayStr);
					
					
					importParamMap.put("I_DATE", dayStr);
					importParamMap.put("I_DATE_T", dayStr);
					importParamMap.put("I_KUNNR", "0000000000");
					importParamMap.put("I_KUNNR_T", "0000000000");
					
					logger.debug("importParamMap>" + importParamMap);
					
					// get custMst in SAP
					custList = sap.select("ZSD_UPSO_CUST_SEND", importParamMap, "E_TAB");
					
					logger.debug("거래처 수 : " + custList.size());
					
					for (Map<String, Object> custMst : custList) {

						// Classify normal custMst and abnormal custcustMst
						if (isNormal(custMst)) {
							normalCnt++;
							wision21.insert("insertCustMstSus", custMst);

						} else {
							abnormalCnt++;
							wision21.insert("insertCustMstErr", custMst);
						}
					}
					
					cal.add(Calendar.DATE, 1);
					wision21.commit();

				} // end for

				logger.debug("normalCnt :" + normalCnt + "건");
				logger.debug("abnormalCnt :" + abnormalCnt + "건");
				
			return true;
				
				
			}
				
		
		});

	}
	
	private final boolean isNormal(Map<String, Object> custMst) {

		if (null == custMst.get("CUST_CD") || "".equals(custMst.get("CUST_CD")))
			return false;
		if (null == custMst.get("CHANNEL_CD") || "".equals(custMst.get("CHANNEL_CD")))
			return false;

		return true;
	}

	@Override
	public void deleteLog() {
		logger.debug("deleteLog");
		
		
		
		BatchManager batch = new BatchManager("로그 삭제 배치");
		
		batch.setBatchResult(new BatchResult());
		
		batch.putStorage(new Tibero("lcpcweb"));
		batch.putStorage(new Tibero("msfamobile"));
		batch.setTransaction(new BatchManager.Transaction() {
			
			@Override
			public boolean onTransaction(StorageMap storageMap) throws Exception {

				Tibero lcpcweb = (Tibero) storageMap.get("lcpcweb");
				Tibero msfamobile = (Tibero) storageMap.get("msfamobile");
				
				
				lcpcweb.delete("deleteLog");
				msfamobile.delete("deleteLog");
				
				
				return true;
			}
		
		});
		
		
		
	}
}
