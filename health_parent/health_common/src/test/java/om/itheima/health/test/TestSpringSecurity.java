package om.itheima.health.test;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestSpringSecurity {

    @Test
    public void test(){

        //$2a$10$rVreeLO4ae0sRCJzVvRQUu4d0e5puIivjMLMyf2Qec9t.7e/qJHcS
        //$2a$10$mcckD9VA3ucgVl95wV1pMO39O2ROaTe91rb8s6C.66RhPwYrx0h9.
        BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
        String s=encoder.encode("admin");
        System.out.println(s);
        String s1=encoder.encode("abc");
        System.out.println(s1);
        //进行判断
        boolean b=encoder.matches("abc","$2a$10$mcckD9VA3ucgVl95wV1pMO39O2ROaTe91rb8s6C.66RhPwYrx0h9.");
        System.out.println(b);

    }


}
