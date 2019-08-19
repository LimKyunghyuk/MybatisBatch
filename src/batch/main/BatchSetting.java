package batch.main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

public class BatchSetting {

	protected BatchSetting() {

		setLog4j();
		
		BatchProperty.getInstrance().show();
	}
	
	void setLog4j(){
		
		Properties props = new Properties();
		String path = BatchProperty.getLog4jPath();

		try {

			props.load(new FileInputStream(path));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PropertyConfigurator.configure(props);
	}

}
