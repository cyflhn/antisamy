package org.owasp.validator.html.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by chuyifan on 2018/10/20.
 */
public class MatcherTest {
    @Test
    public void testMatch() {
        String a = "//s";
        String b = "^(?![\\p{L}\\p{N}\\\\\\.\\#@\\$%\\+&amp;;\\-_~,\\?=/!]*(&amp;colon))[\\p{L}\\p{N}\\\\\\.\\#@\\$%\\+&amp;;\\-_~,\\?=/!]*";
        String c = "(\\w+?)(?=[\\p{N}]*)";
        Pattern pattern = Pattern.compile(b);
        Matcher macher = pattern.matcher(a);
        Assert.assertTrue(macher.matches());
        System.out.println(macher.group(1));
    }
}
