package org.owasp.validator.html.test;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.junit.Test;

import java.util.List;

/**
 * Created by chuyifan on 2018/9/26.
 */
public class ParseTest {
    @Test
    public void testParse() {
        // String html = "adadaf< 1> < dada@da.com> \r\n 1\n2 & %25 <table> sda ^%45 652 <img/onerror=alert(1111)
        // src='1'>";
        String html = "<table>";
        List<Node> nodes = Parser.parseFragment(html, null, "");

//        Cleaner cleaner = new Cleaner(Whitelist.relaxed());
//        Document clean = cleaner.clean(dirty);
//
//        clean.outputSettings().prettyPrint(false).escapeMode(Entities.EscapeMode.xhtml);
//        System.out.println(clean.body().html());;

    }
}
