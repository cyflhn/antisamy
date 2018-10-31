package org.owasp.validator.html.test;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.owasp.validator.html.AntiSamyExt;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;

/**
 * Created by chuyifan on 2018/10/31.
 */
public class AntiSamyAttrTest {

    @Test
    public void testXss1() throws Exception {
        // String content = "<img//onerror='alert(1' src='/adss'> <TABLE> && ,: # %23 SD SSDADA";
        // String content="<img /onerror=alert(1' > ?=+ adad & sda %6d a ";
        String content = "<z a=\"\"<script></script> <a onclick='aa' >";
        Assert.assertTrue(testXss(content));

        content = "<z a=<assa\" <script   onclick='aa' >";
        Assert.assertTrue(testXss(content));
    }

    @Test
    public void testXss2() throws Exception {
        // String content = "<img//onerror='alert(1' src='/adss'> <TABLE> && ,: # %23 SD SSDADA";
        // String content="<img /onerror=alert(1' > ?=+ adad & sda %6d a ";
        String content = "<z a=\"<\"<script></script> >";
        Assert.assertFalse(testXss(content));
    }

    private boolean testXss(String content) throws PolicyException {
        Policy policy = Policy.getInstance(this.getClass().getResourceAsStream("/antixss_attr.xml"));
        Locale defaultLocal = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
        AntiSamyExt as = new AntiSamyExt();
        try {
            CleanResults cr = as.scan(content, policy);
            System.out.println(cr.getNumberOfErrors());
            System.out.println(cr.getCleanHTML());
            return cr.getNumberOfErrors() > 0;
        }
        catch (Exception ex) {
        }
        finally {
            Locale.setDefault(defaultLocal);
        }
        return false;
    }
}
