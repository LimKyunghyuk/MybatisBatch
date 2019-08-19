package util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Timer {
	
	public static String getTime(){
		Calendar calendar = Calendar.getInstance();
        java.util.Date date = calendar.getTime();	
        return (new SimpleDateFormat("yyyyMMddHHmmss").format(date));
	}
}