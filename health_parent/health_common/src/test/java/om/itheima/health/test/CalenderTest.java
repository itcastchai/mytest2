package om.itheima.health.test;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CalenderTest {

    @Test
    public void Test1(){
        Calendar calendar=Calendar.getInstance();
        String s = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        System.out.println(s);
    }
}
