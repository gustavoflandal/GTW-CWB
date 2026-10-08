package com.consilux.lib;

import java.util.Calendar;
import java.util.Date;

public abstract class DateUtil {

	public static Date clearTime(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return clearTime(cal).getTime();
	}
	
	public static Calendar clearTime(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}
	
	public static Date addDays(Date date, int nDays) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);		
		cal.add(Calendar.DAY_OF_MONTH, nDays);
		return cal.getTime();
	}
	
}
