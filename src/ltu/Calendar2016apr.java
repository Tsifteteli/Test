package ltu;

import java.util.Calendar;
import java.util.Date;

public class Calendar2016apr  implements ICalendar {
  
   @Override
	public Date getDate() {
		Calendar cal = Calendar.getInstance();
      cal.set(Calendar.YEAR, 2016);
      cal.set (Calendar.MONTH, Calendar.APRIL);
		return cal.getTime();
	}
}
