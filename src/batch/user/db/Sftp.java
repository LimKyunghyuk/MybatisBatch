package batch.user.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import batch.common.Storage;
import batch.main.BatchProperty;

public class Sftp implements Storage{

	protected static Logger logger = Logger.getLogger(Sftp.class.getName());
	private String name;
	
	private JSch jsch;
	
	private Session dwSession;
	
	private ChannelSftp dwChannel;
	
	private final String HOST;
	private final int PORT;
	private final String ID;
	private final String PW;
	
	private int TMS_SLICE;
	
	
	public Sftp(String name){
		
		setName(name);
		
		BatchProperty ppt = BatchProperty.getInstrance();
		
		HOST = ppt.getProperty("TMS_HOST");
		PORT = Integer.parseInt(ppt.getProperty("TMS_PORT"));
		ID = ppt.getProperty("TMS_ID");
		PW = ppt.getProperty("TMS_PW");
		
		try{
			TMS_SLICE = Integer.parseInt(ppt.getProperty("TMS_SLICE"));	
		}catch (Exception e) {
			TMS_SLICE = 0;
		}
		
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void open() {
		
		jsch = new JSch();
		
		try {
			
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			
			dwSession = jsch.getSession(ID, HOST, PORT);
			dwSession.setPassword(PW);
			dwSession.setConfig(config);
			dwSession.connect();
			dwChannel = (ChannelSftp)dwSession.openChannel("sftp");
			dwChannel.connect();
			
			logger.debug("Connected to " + HOST);
			
		} catch (JSchException e) {
			logger.error(e.toString());
			
			try {
				
				dwChannel.disconnect();
				dwSession.disconnect();
				
			} catch (Exception e1) {
				logger.error(e1.toString());
			}
		}
	}

	@Override
	public void close() {
		
		try {
		
			if(dwSession.isConnected()){
				
				dwChannel.disconnect();
				dwSession.disconnect();	
			}
			
		} catch (Exception e) {
			logger.error(e.toString());
		}
		
	}

	@Override
	public void commit() {}

	@Override
	public void rollback() {}
	
	public Map<String, List<String>> select(String serverPath, String fileName) {
		
		Map<String, List<String>> returnList = new HashMap<String, List<String>>();
		
		if(!serverPath.endsWith("/")){
			serverPath += "/";
		}
		
		try {
			
			Vector<LsEntry> fileList = dwChannel.ls(serverPath);
			
			String serverFileName;
			
			logger.debug("fileList : " + fileList.size());
			
			int cnt = 0;
			
			for (LsEntry entry : fileList) {

				serverFileName = entry.getFilename();

				if (serverFileName != null && serverFileName.contains(fileName)) {

					
					if(TMS_SLICE != 0){
						if(TMS_SLICE == cnt++) break; // TMS_SLICE 만큼 잘라서 read	
					}

					List<String> file = new ArrayList<String>();

					InputStream is = dwChannel.get(serverPath + serverFileName);
					BufferedReader br = new BufferedReader(new InputStreamReader(is));
					
					String line;
					
					while ((line = br.readLine()) != null) {
						
						//logger.debug("line :" + line);
						file.add(line);
						
					}
					returnList.put(entry.getFilename(), file);
				}

			}
			
			
		} catch (SftpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return returnList;
	}
	
	public void move(String src, String des, String name){
		
		if(!src.endsWith("/")){
			src += "/";
		}
				
		if(!des.endsWith("/")){
			des += "/";
		}		
		
		String srcPath = src + name;
		String desPath = des + name;

		try {
			
			dwChannel.rename(srcPath, desPath);
		
		} catch (SftpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void move(String src, String des, String name, String error){
		
		if(!src.endsWith("/")){
			src += "/";
		}
				
		if(!des.endsWith("/")){
			des += "/";
		}		
		
		String srcPath = src + name;
		String desPath = des + name + "_" + error;

		try {
			
			dwChannel.rename(srcPath, desPath);
		
		} catch (SftpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
