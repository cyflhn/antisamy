package org.owasp.validator.html;

import java.util.*;

/**
 * Created by chuyifan on 2018/9/26.
 */
public class MultiCleanResults extends CleanResults {
    private Map<String, ArrayList<String>> cleanHTMLMap = new HashMap<String, ArrayList<String>>();

    public void addErrorMessages(List<String> errorMsgs) {
        this.getErrorMessages().addAll(errorMsgs);
    }

    public void addErrorMessage(String errorMsg) {
        this.getErrorMessages().add(errorMsg);
    }

    public void addCleanHtml(String name, String value) {
        ArrayList<String> vallist = cleanHTMLMap.get(name);
        if (vallist == null) {
            vallist = new ArrayList<String>();
            cleanHTMLMap.put(name, vallist);
        }
        vallist.add(value);
    }

    public void removeCleanHtml(String name) {
        if (cleanHTMLMap != null && cleanHTMLMap.size() > 0) {
            cleanHTMLMap.remove(name);
        }
    }

    public Map<String, String[]> getCleanHtmlMap() {
        if (cleanHTMLMap.size() > 0) {
            Map<String, String[]> cleanHTMLArrayMap = new HashMap<String, String[]>(cleanHTMLMap.size());
            Iterator<Map.Entry<String, ArrayList<String>>> iterator = cleanHTMLMap.entrySet().iterator();
            for (; iterator.hasNext();) {
                Map.Entry<String, ArrayList<String>> entry = iterator.next();
                cleanHTMLArrayMap.put(entry.getKey(), entry.getValue() == null ? new String[]{""}
                        : entry.getValue().toArray(new String[entry.getValue().size()]));
            }
            return cleanHTMLArrayMap;
        }
        return null;
    }
}
