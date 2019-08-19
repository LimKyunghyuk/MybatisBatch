package batch.common;

import java.util.HashMap;
import java.util.Map;

public class StorageMap{

	private Map<String, Storage> map;
	
	StorageMap(){
		map = new HashMap<String, Storage>();
	}

	public Storage get(Object key) throws Exception {
	
		Storage storage = map.get(key);
		
		if(null == storage) {
			throw new Exception("Can not find def storage");
		}
		
		return storage;
	}

	
	
	public void put(Storage storage){
		// TODO Auto-generated method stub
		map.put(storage.getName(), storage);
	}
	

	public void connection(){
		
		for( String key : map.keySet() ){
			map.get(key).open();
		}
	}
	
	
	public void close(){
		
		for( String key : map.keySet() ){
			map.get(key).close();
		}
	}

	public void commit(){
		for( String key : map.keySet() ){
			map.get(key).commit();
		}
	}
	
	public void rollback(){
		for( String key : map.keySet() ){
			map.get(key).rollback();
		}
	}
	
}
