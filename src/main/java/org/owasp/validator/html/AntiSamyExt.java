package org.owasp.validator.html;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.owasp.validator.html.scan.AntisamyDOMScannerExt;

/**
 * Created by chuyifan on 2018/9/21.
 */
public class AntiSamyExt extends AntiSamy {
    public CleanResults scan(String taintedHTML, Policy policy) throws ScanException, PolicyException {
        return new AntisamyDOMScannerExt(policy).scan(taintedHTML);
    }

    public MultiCleanResults scan(Map<String, String[]> taintedHTML, Policy policy)
            throws ScanException, PolicyException {
        Iterator<Map.Entry<String, String[]>> iterator = taintedHTML.entrySet().iterator();
        MultiCleanResults results = new MultiCleanResults();
        for (; iterator.hasNext();) {
            Map.Entry<String, String[]> entry = iterator.next();
            if (entry.getValue() != null) {
                boolean xssInjected = false;
                for (String value : entry.getValue()) {
                    AntisamyDOMScannerExt antisamyDOMScannerExt = new AntisamyDOMScannerExt(policy);
                    if (StringUtils.isNotEmpty(value)) {
                        CleanResults cleanResults = antisamyDOMScannerExt.scan(value);
                        results.addCleanHtml(entry.getKey(), cleanResults.getCleanHTML().length() <= value.length()
                                ? cleanResults.getCleanHTML() : " ");
                        if (cleanResults.getNumberOfErrors() > 0) {
                            xssInjected = true;
                            results.addErrorMessages(cleanResults.getErrorMessages());
                        }
                    }
                    else {
                        results.addCleanHtml(entry.getKey(), "");
                    }
                }
                if (!xssInjected) {
                    results.removeCleanHtml(entry.getKey());
                }
            }
        }
        return results;
    }
}
