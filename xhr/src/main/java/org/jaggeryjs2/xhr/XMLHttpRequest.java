/*
 *  Copyright (c), WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.jaggeryjs2.xhr;

import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.internal.runtime.Undefined;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class XMLHttpRequest {

    private static final Log log = LogFactory.getLog(XMLHttpRequest.class);

    private final int HTTPS_DEFAULT_PORT = 443;
    private HttpClient httpClient = null;
    private XMLHttpRequest xhr;
    private static final String HEADER_JOINER = ", ";

    private String methodName = null;
    private String url = null;
    private boolean async = false;
    private String username = null;
    private String password = null;
    private List<Header> requestHeaders = new ArrayList<Header>();

    /**
     * XHR constants
     */
    private static final short UNSENT = 0;
    private static final short OPENED = 1;
    private static final short HEADERS_RECEIVED = 2;
    private static final short LOADING = 3;
    private static final short DONE = 4;

    /**
     * XHR properties
     */
    private short readyState;
    private StatusLine statusLine;
    private String responseText;
    private AbstractJSObject onreadystatechange;

    private HttpMethodBase method = null;
    private Header[] responseHeaders = null;
    private String responseType = null;
    private boolean error;
    private AbstractJSObject responseXML;

    public XMLHttpRequest(){
        try {
            // To ignore the hostname verification in http client side when org.wso2.ignoreHostnameVerification is set
            // This system variable should not be set in product environment due to security reasons
            String ignoreHostnameVerification = System.getProperty("org.wso2.ignoreHostnameVerification");
            if (ignoreHostnameVerification != null && "true".equalsIgnoreCase(ignoreHostnameVerification)) {
                Protocol protocolWithoutHostNameVerification = new Protocol("https",
                        (ProtocolSocketFactory) new EasySSLProtocolSocketFactory(), HTTPS_DEFAULT_PORT);
                Protocol.registerProtocol("https", protocolWithoutHostNameVerification);
            }
            httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
            ProxyHost proxyConfig = getProxyConfig();
            if (proxyConfig != null) {
                httpClient.getHostConfiguration().setProxyHost(proxyConfig);
            }
        } catch(IOException e){
            log.error(e);
        } catch(GeneralSecurityException e) {
            log.error(e);
        }

        xhr = new XMLHttpRequest(httpClient);

    }

    private XMLHttpRequest(HttpClient httpClient){
        this.httpClient = httpClient;
    }

    public void init(){

    }

    public int status() {
        return xhr.statusLine.getStatusCode();
    }

    public short readyState()  {
        return xhr.readyState;
    }

    public String statusText() {
        return xhr.statusLine.getReasonPhrase();
    }

    public String responseText() {
        if (xhr.readyState == LOADING || xhr.readyState == DONE) {
            return xhr.responseText;
        } else {
            return "";
        }
    }

    //TODO implement responseXML method

    public void open(String method, String url){
        setMethod( xhr, method);
        setURL(xhr, url);

        updateReadyState(xhr, OPENED);
    }

    public void open(String method, String url, String async){
        setMethod( xhr, method);
        setURL(xhr, url);
        setAsync(xhr, async);

        updateReadyState(xhr, OPENED);
    }

    public void open(String method, String url, String userName, String password){
        setMethod( xhr, method);
        setURL(xhr, url);
        setUsername(xhr, userName);
        setPassword(xhr, password);

        updateReadyState(xhr, OPENED);
    }

    public void open(String method, String url, String async, String userName, String password){
        setMethod( xhr, method);
        setURL(xhr, url);
        setAsync(xhr, async);
        setUsername(xhr, userName);
        setPassword(xhr, password);

        updateReadyState(xhr, OPENED);
    }

    public void setRequestHeader(String name, String value){
        xhr.requestHeaders.add(new Header(name, value));
    }

    public void send() throws Exception {
        xhr.sendRequest(null);
    }

    public void send(String request) throws Exception {
        xhr.sendRequest(request);
    }

    public void abort(){
        if (xhr.async) {
            xhr.method.abort();
        }
    }

    public String getResponseHeader(String header) {
        if (xhr.readyState == UNSENT || xhr.readyState == OPENED) {
            return null;
        }
        if (xhr.error) {
            return null;
        }
        if (xhr.responseHeaders == null) {
            return null;
        }
        StringBuffer value = null;
        for (Header h : xhr.responseHeaders) {
            if (h.getName().equalsIgnoreCase(header)) {
                if (value == null) {
                    value = new StringBuffer(h.getValue());
                    continue;
                }
                value.append(HEADER_JOINER).append(h.getValue());
            }
        }
        return value != null ? value.toString() : null;
    }

    public String getAllResponseHeaders(){
        if (xhr.readyState == UNSENT || xhr.readyState == OPENED) {
            return null;
        }
        if (xhr.error) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        String headers = "";
        if (xhr.responseHeaders == null) {
            return headers;
        }
        for (Header h : xhr.responseHeaders) {
            String header = h.getName();
            builder.append(h.getName() + ": " + h.getValue() + "\r\n");
        }
        headers = builder.toString();
        return headers;
    }

    private static void updateReadyState(XMLHttpRequest xhr, short readyState) {
        xhr.readyState = readyState;
        if (xhr.async && xhr.onreadystatechange != null) {
            try {
                xhr.onreadystatechange.call(xhr);
            } catch (Exception e) {
                //TODO handle error
            }
        }
    }

    private static void setURL(XMLHttpRequest xhr, Object arg){
        if (arg instanceof String) {
            String url = (String) arg;
            String formattedUrl = url.toLowerCase();
            if (!(formattedUrl.startsWith("http://") || formattedUrl.startsWith("https://"))) {
                //TODO handle error
            }
            int lastIndex = url.indexOf("#");
            lastIndex = (lastIndex == -1) ? url.length() : lastIndex;
            xhr.url = url.substring(0, lastIndex);
        } else {
            //TODO handle error
        }
    }

    //TODO add other HTTP methods too
    private static void setMethod(XMLHttpRequest xhr, Object arg) {
        if (arg instanceof String) {
            String methodName = ((String) arg).toUpperCase();
            if ("GET".equals(methodName) || "HEAD".equals(methodName) || "POST".equals(methodName) ||
                    "PUT".equals(methodName) || "DELETE".equals(methodName) || "TRACE".equals(methodName) ||
                    "OPTIONS".equals(methodName)) {
                xhr.methodName = methodName;
            } else {
                //TODO handle error
            }
        } else {
            //TODO handle error
        }
    }

    private static void setAsync(XMLHttpRequest xhr, Object arg) {
        if (arg instanceof Boolean) {
            xhr.async = (Boolean) arg;
        } else {
            //TODO handle error
        }
    }

    private static void setUsername(XMLHttpRequest xhr, Object arg) {
        if (arg instanceof String) {
            xhr.username = (String) arg;
        } else {
            //TODO handle error
        }
    }

    private static void setPassword(XMLHttpRequest xhr, Object arg) {
        if (arg instanceof String) {
            xhr.password = (String) arg;
        } else {
            //TODO handle error
        }
    }

    private ProxyHost getProxyConfig() {

        ProxyHost proxyConfig = null;

        String proxyHost = System.getProperty("http.proxyHost");
        String proxyPortStr = System.getProperty("http.proxyPort");

        int proxyPort = -1;
        if (proxyHost != null) {
            proxyHost = proxyHost.trim();
        }

        if (proxyPortStr != null) {
            try {
                proxyPort = Integer.parseInt(proxyPortStr);

                //TODO test this properly
                if (proxyHost.length() > 0) {
                    proxyConfig = new ProxyHost(proxyHost, proxyPort);
                }
            } catch (NumberFormatException e) {
                log.error(e.getMessage(), e);
            }
        }

        return proxyConfig;
    }

    private void sendRequest(Object obj) throws Exception {
        final HttpMethodBase method;
        if ("GET".equalsIgnoreCase(methodName)) {
            method = new GetMethod(this.url);
        } else if ("HEAD".equalsIgnoreCase(methodName)) {
            method = new HeadMethod(this.url);
        } else if ("POST".equalsIgnoreCase(methodName)) {
            PostMethod post = new PostMethod(this.url);
            if(obj instanceof FormData){
                FormData fd = ((FormData) obj);
                List<Part> parts = new ArrayList<Part>();
                for (Map.Entry<String, String> entry : fd) {
                    parts.add(new StringPart(entry.getKey(),entry.getValue()));
                }
                post.setRequestEntity(
                        new MultipartRequestEntity(parts.toArray(new Part[parts.size()]), post.getParams())
                );
            } else {
                String content = getRequestContent(obj);
                if (content != null) {
                    post.setRequestEntity(new InputStreamRequestEntity(new ByteArrayInputStream(content.getBytes())));
                }
            }
            method = post;
        } else if ("PUT".equalsIgnoreCase(methodName)) {
            PutMethod put = new PutMethod(this.url);
            String content = getRequestContent(obj);
            if (content != null) {
                put.setRequestEntity(new InputStreamRequestEntity(new ByteArrayInputStream(content.getBytes())));
            }
            method = put;
        } else if ("DELETE".equalsIgnoreCase(methodName)) {
            method = new DeleteMethod(this.url);
        } else if ("TRACE".equalsIgnoreCase(methodName)) {
            method = new TraceMethod(this.url);
        } else if ("OPTIONS".equalsIgnoreCase(methodName)) {
            method = new OptionsMethod(this.url);
        } else {
            throw new Exception("Unknown HTTP method : " + methodName);
        }
        for (Header header : requestHeaders) {
            method.addRequestHeader(header);
        }
        if (username != null) {
            httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        }
        this.method = method;
        final XMLHttpRequest xhr = this;
        if (async) {
            updateReadyState(xhr, LOADING);
            final ExecutorService es = Executors.newSingleThreadExecutor();
            es.submit(new Callable() {
                public Object call() throws Exception {
                    try {
                        executeRequest(xhr);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    } finally {
                        es.shutdown();
                    }
                    return null;
                }
            });
        } else {
            executeRequest(xhr);
        }
    }

    private static void executeRequest(XMLHttpRequest xhr)  {
        System.out.println("XHR httpclient " + xhr.httpClient);
        try {
            xhr.httpClient.executeMethod(xhr.method);
            xhr.statusLine = xhr.method.getStatusLine();
            xhr.responseHeaders = xhr.method.getResponseHeaders();
            updateReadyState(xhr, HEADERS_RECEIVED);

            byte[] response = xhr.method.getResponseBody();
            if (response != null) {
                if (response.length > 0) {
                    xhr.responseText = new String(response);
                }
            }
            Header contentType = xhr.method.getResponseHeader("Content-Type");
            if (contentType != null) {
                xhr.responseType = contentType.getValue();
            }
            updateReadyState(xhr, DONE);
        } catch (IOException e) {
            log.error(e.getMessage(), e);

        } finally {
            xhr.method.releaseConnection();
        }
    }

    private static String getRequestContent(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Undefined) {
            return null;
        }
        return serializeObject(obj);
    }

    //TODO start moving to a util
    public static String serializeObject(Object obj) {

        if (obj instanceof String ||
                obj instanceof Integer ||
                obj instanceof Long ||
                obj instanceof Float ||
                obj instanceof Double ||
                obj instanceof Short ||
                obj instanceof BigInteger ||
                obj instanceof BigDecimal ||
                obj instanceof Boolean) {
            return obj.toString();
        }

        //TODO serialize XML

        return serializeJSON(obj);
    }

    public static String serializeJSON(Object obj) {

        if (obj == null) {
            return "null";
        }
        if (obj instanceof Undefined) {
            return "null";
        }
        if (obj instanceof Boolean) {
            return Boolean.toString((Boolean) obj);
        }
        if (obj instanceof String) {
            return serializeString((String) obj);
        }

        if (obj instanceof Number) {
            return obj.toString();
        }

        if (obj instanceof Object[]) {
            return serializeObjectArray((Object[]) obj);
        }


        return "{}";
    }

    private static String serializeString(String obj) {
        return "\"" + obj.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n")
                .replace("\t", "\\t").replace("\f", "\\f").replace("\b", "\\b").replace("\u2028", "\\u2028")
                .replace("\u2029", "\\u2029") + "\"";
    }

    private static String serializeObjectArray(Object[] obj) {
        StringWriter json = new StringWriter();
        json.append("[");
        boolean first = true;
        for (Object value : obj) {
            if (!first) {
                json.append(", ");
            } else {
                first = false;
            }
            json.append(serializeJSON(value));
        }
        json.append("]");
        return json.toString();
    }

    //TODO end moving to a util


}
