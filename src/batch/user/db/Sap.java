package batch.user.db;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.ext.DestinationDataProvider;

import batch.common.Storage;
import batch.main.BatchProperty;

public class Sap implements Storage{
	
	protected static Logger logger = Logger.getLogger(Sap.class.getName());
	
	private String name;
	static String ABAP_AS = "ABAP_AS_WITHOUT_POOL"; // sap 연결명(연결파일명으로 사용됨)
	static String ABAP_MS = "ABAP_MS_WITHOUT_POOL";
	
	JCoDestination destination;
	
	public interface SapParameter{
		void setSapParameter(JCoTable table);
	}
	
	public Sap(String name){
		setName(name);
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	private void init() {
		Properties connectProperties = new Properties();
		
		BatchProperty batchProperty = BatchProperty.getInstrance();
		
		connectProperties.setProperty(DestinationDataProvider.JCO_MSHOST, batchProperty.getProperty("SAP_MSHOST"));
		connectProperties.setProperty(DestinationDataProvider.JCO_R3NAME, batchProperty.getProperty("SAP_R3NAME"));
		connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, batchProperty.getProperty("SAP_CLIENT"));
		connectProperties.setProperty(DestinationDataProvider.JCO_USER,   batchProperty.getProperty("SAP_USER"));
		connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, batchProperty.getProperty("SAP_PASSWD"));
		connectProperties.setProperty(DestinationDataProvider.JCO_GROUP,  batchProperty.getProperty("SAP_GROUP"));
		connectProperties.setProperty(DestinationDataProvider.JCO_LANG,   batchProperty.getProperty("SAP_LANG"));
		
		
		createDestinationDataFile(ABAP_MS, connectProperties);
	}

	void createDestinationDataFile(String destinationName, Properties connectProperties){

		File destCfg = new File(destinationName + ".jcoDestination");

		if(destCfg.exists()){
			if(destCfg.delete()){
				logger.debug("delete file success!");
			}else{
				logger.debug("delete file fail!");
			}
		}
		
		if (!destCfg.exists()) {

			try

			{

				FileOutputStream fos = new FileOutputStream(destCfg, false);

				connectProperties.store(fos, "for tests only !");

				fos.close();

			}

			catch (Exception e)

			{

				throw new RuntimeException("Unable to create the destination files", e);

			}

		}
	}
	
	@Override
	public void open() {
		init();

		try {
			destination = JCoDestinationManager.getDestination(ABAP_MS);
		} catch (JCoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void commit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rollback() {
		// TODO Auto-generated method stub
		
	}
	
	
	public List<Map<String, Object>> select(String rfcName, Map<String, Object> map2, String tableName){
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		try {
			logger.debug(destination.getAttributes());
			
			JCoFunction function = destination.getRepository().getFunction(rfcName);
			JCoParameterList paramList = function.getImportParameterList();

			Iterator iterator = map2.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry entry = (Entry) iterator.next();
				paramList.setValue((String) entry.getKey(), (String) entry.getValue());
			}

			function.execute(destination);

			// 펑션에서 테이블 호출

			JCoTable codes = function.getTableParameterList().getTable(tableName);

			JCoFieldIterator field = codes.getFieldIterator();

			String[] fieldList = new String[codes.getFieldCount()];

			int index = 0;
			while (field.hasNextField()) {
				fieldList[index++] = field.nextField().getName();
			}

			Map<String, Object> map;
			for (int i = 0; i < codes.getNumRows(); i++) {

				map = new HashMap<String, Object>();
				codes.setRow(i);

				for (int j = 0; j < index; j++) {
					map.put(fieldList[j], codes.getString(fieldList[j]));
				}
				list.add(map);
			}

		} catch (JCoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		return list;
		
	}
	
	public JCoParameterList select2(String rfcName, Map<String, Object> map2){
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		JCoParameterList exportPrams = null;
		try {
			logger.debug(destination.getAttributes());
			
			JCoFunction function = destination.getRepository().getFunction(rfcName);
			JCoParameterList paramList = function.getImportParameterList();

			Iterator iterator = map2.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry entry = (Entry) iterator.next();
				paramList.setValue((String) entry.getKey(), (String) entry.getValue());
			}

			function.execute(destination);

			// 펑션에서 테이블 호출

//			JCoTable codes = function.getTableParameterList().getTable(tableName);
			exportPrams = function.getExportParameterList();
			

		} catch (JCoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		return exportPrams;
		
	}
	
	
	
	
	public String insert(String rfcName, String tableName, SapParameter params, String returnValueName){

		// logger.debug(destination.getAttributes());
		String rtnMsg = null;
		
		try{
			JCoFunction function = destination.getRepository().getFunction(rfcName);
			
			JCoTable sapTable = function.getTableParameterList().getTable(tableName);
			
			params.setSapParameter(sapTable);
			
			function.execute(destination);
		
			rtnMsg = (String)function.getExportParameterList().getValue(returnValueName);
			
		}catch(Exception e){
		
			e.printStackTrace();
		}
		
		return rtnMsg; 
	}
}