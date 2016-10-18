package org.jaggeryjs2;

import jdk.nashorn.api.scripting.JSObject;

import java.util.HashMap;
import java.util.Map;
import org.wso2.uri.template.URITemplate;
import org.wso2.uri.template.URITemplateException;

import javax.script.*;

public class URIMatcher {

    private String uriToBeMatched;
    private JSObject uriParts;
    private URIMatcher urimatcher;

    public URIMatcher(String uri) {
        urimatcher = new URIMatcher();
        urimatcher.uriToBeMatched = uri;
    }

    public URIMatcher(){
    }

    public JSObject match(String pattern) throws ScriptException {

        Map<String, String> urlParts = new HashMap<String, String>();
        try {
            URITemplate uriTemplate = new URITemplate(pattern);
            boolean uriMatch = uriTemplate.matches(urimatcher.uriToBeMatched, urlParts);
            if (!uriMatch) {
                return null;
            }

        } catch (URITemplateException e) {

        }

        ScriptEngineManager m = new ScriptEngineManager();
        ScriptEngine e = m.getEngineByName("nashorn");
        JSObject objConstructor = (JSObject)e.eval("Object");
        JSObject jsObj = (JSObject) objConstructor.newObject();

        for (Map.Entry<String, String> entry : urlParts.entrySet()) {
            jsObj.setMember(entry.getKey(), entry.getValue());

        }

        urimatcher.uriParts = jsObj;
        return jsObj;
    }

    public JSObject elements() throws ScriptException, NoSuchMethodException {
        return urimatcher.uriParts;
    }
}
