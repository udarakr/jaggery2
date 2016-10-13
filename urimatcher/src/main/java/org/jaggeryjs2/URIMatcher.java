package org.jaggeryjs2;

import jdk.nashorn.internal.runtime.ScriptObject;

import java.util.HashMap;
import java.util.Map;
import org.wso2.uri.template.URITemplate;
import org.wso2.uri.template.URITemplateException;

public class URIMatcher {
    private String uriToBeMatched;
    private ScriptObject uriParts;

    public URIMatcher(String uri) {
        URIMatcher uriho = new URIMatcher();
        uriho.uriToBeMatched = uri;
    }

    public URIMatcher(){
    }

    public Boolean match(String pattern) {

        Map<String, String> urlParts = new HashMap<String, String>();
        boolean uriMatch = false;
        try {
            URITemplate uriTemplate = new URITemplate(pattern);
            uriMatch = uriTemplate.matches(uriToBeMatched, urlParts);
        } catch (URITemplateException e) {

        }

        ScriptObject nobj = null;
        for (Map.Entry<String, String> entry : urlParts.entrySet()) {
            nobj.put(entry.getKey(), nobj, true);
        }

        this.uriParts = nobj;
        return uriMatch;
    }

    public ScriptObject jsFunction_elements(){
        return this.uriParts;
    }
}
