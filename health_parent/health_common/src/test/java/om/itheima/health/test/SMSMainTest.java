package om.itheima.health.test;

import com.aliyuncs.exceptions.ClientException;
import com.itheima.health.utils.SMSUtils;
import org.junit.Test;

public class SMSMainTest {
    @Test
    public void test1() throws ClientException {
        SMSUtils.sendShortMessage("15012579600","6666");
    }
}
