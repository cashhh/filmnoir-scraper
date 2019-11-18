package scraper;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class Main {

    public static void main(String[] args) throws ParseException, IOException {
        //new Scraper();
        Calendar c = new GregorianCalendar(2019,Calendar.NOVEMBER, 15, 15, 40);
        Date d = c.getTime();
        System.out.println(d);
        /*String a = "Sunday, Nov 3, 7:00 PM";
        String b = "Thursday, November 14, 1:30 PM";

        String monthName = "February";

        Date date = new SimpleDateFormat("MMMM").parse(monthName);
        SimpleDateFormat sdf = new SimpleDateFormat("M");
        System.out.println(sdf.format(date));*/
    }
}
