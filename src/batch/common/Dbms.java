package batch.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

import batch.main.BatchProperty;

public class Dbms implements Storage {

	protected static Logger logger = Logger.getLogger(Dbms.class.getName());

	private String mybatisId;
	private String nameSpace;

	private SqlSessionFactory sf;
	private SqlSession session;
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return mybatisId;
	}
	
	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		mybatisId = name;
		nameSpace = name;
	}
	
	@Override
	public void open() {
		
		BatchProperty ppt = BatchProperty.getInstrance();
		String configXML = ppt.getProperty("CONFIG_XML");

	
		InputStream is = null;
		
		try {

			is = Resources.getResourceAsStream(configXML);
			sf = new SqlSessionFactoryBuilder().build(is, mybatisId);
			session = sf.openSession();
			
		} catch (IOException e) {
			e.printStackTrace();
			
		}finally{ 
			
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		
	}

	@Override
	public void close() {
		session.close();
	}
	
	@Override
	public void commit() {
		session.commit();
	}

	@Override
	public void rollback() {
		session.rollback();
	}

	public List<HashMap<String, Object>> select(String id) {

		List<HashMap<String, Object>> list = session.selectList(nameSpace + "." + id);
		
		return list;
	}

	public void insert(String id, Map<String, Object> map) {
		session.insert(nameSpace + "." + id, map);
	}

	public void update(String id, Map<String, Object> map) {
		session.update(nameSpace + "." + id, map);
	}
	
	public void delete(String id) {
		session.delete(nameSpace + "." + id);
	}
	

	public void delete(String id, Map<String, Object> map) {
		session.delete(nameSpace + "." + id, map);
	}
	
	



	
//	public SqlSession getSession() {
//		return session;
//	}
	
}
