package org.owasp.validator.html.test;

import java.io.StringReader;

import org.apache.xerces.dom.DocumentImpl;
import org.cyberneko.html.parsers.DOMFragmentParser;
import org.junit.Test;
import org.w3c.dom.DocumentFragment;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * Created by chuyifan on 2018/9/26.
 */
public class ParseTest {
    @Test
    public void parseUnRegular() throws Exception {
        String html = "Theis is <new> prldadsdas</new><body/onhashchange=alert(1)><a href=#>clickit";
        DOMFragmentParser parser = getDomParser();
        org.w3c.dom.Document document = new DocumentImpl();
        DocumentFragment dom = document.createDocumentFragment();
        try {
            parser.parse(new InputSource(new StringReader(html)), dom);
        }
        catch (Exception e) {
        }
    }

    static DOMFragmentParser getDomParser() throws SAXNotRecognizedException, SAXNotSupportedException {
        DOMFragmentParser parser = new DOMFragmentParser();
        parser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");

        parser.setFeature("http://cyberneko.org/html/features/scanner/style/strip-cdata-delims", false);
        parser.setFeature("http://cyberneko.org/html/features/scanner/cdata-sections", true);

        try {
            parser.setFeature("http://cyberneko.org/html/features/enforce-strict-attribute-names", true);
        }
        catch (SAXNotRecognizedException se) {
            // this indicates that the patched nekohtml is not on the
            // classpath
        }
        return parser;
    }

}
