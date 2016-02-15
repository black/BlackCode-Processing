package utility;

import java.util.Calendar;
import java.text.SimpleDateFormat;

public class DateUtils {
	public static final String DATE_FORMAT_USA = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FORMAT_EUROPE = "dd/MM/yyyy HH:mm:ss";
	
	public static String nowUsa() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_USA);
		return sdf.format(cal.getTime());
	}

	public static String nowEurope() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_EUROPE);
		return sdf.format(cal.getTime());
	}

}