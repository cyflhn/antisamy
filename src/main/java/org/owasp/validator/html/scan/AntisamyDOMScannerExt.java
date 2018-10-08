package org.owasp.validator.html.scan;

import java.io.Writer;

import org.apache.xml.serialize.OutputFormat;
import org.owasp.validator.html.Policy;

/**
 * Created by chuyifan on 2018/9/21.
 */
public class AntisamyDOMScannerExt extends AntiSamyDOMScanner {
    public AntisamyDOMScannerExt(Policy policy) {
        super(policy);
    }

    protected org.apache.xml.serialize.HTMLSerializer getHTMLSerializer(Writer w, OutputFormat format){
        return new HtmlExtSerializer(w);
    }
}
