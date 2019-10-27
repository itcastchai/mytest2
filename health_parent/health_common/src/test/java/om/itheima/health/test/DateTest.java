package om.itheima.health.test;

import com.itheima.health.utils.DateUtils;
import org.junit.Test;

import java.util.Date;

public class DateTest {
    @Test
    public void test1() throws Exception {
        //1.获取当前日期
        String today= DateUtils.parseDate2String(new Date());
        //2.获取本周一

        String firstWeekMonday = DateUtils.parseDate2String(DateUtils.getThisWeekMonday());

        //3.获取本周最日
        String  lastWeekMonday=DateUtils.parseDate2String(DateUtils.getSundayOfThisWeek());
        //4.获取本月第一天
        String  firMonthDay=DateUtils.parseDate2String(DateUtils.getFirstDay4ThisMonth());

        //5.获取本月最后一天
        String lastMonthDay=DateUtils.parseDate2String(DateUtils.getLastDay4ThisMonth());
    }
}
