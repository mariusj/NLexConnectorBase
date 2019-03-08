package eu.europa.eurlex.nlex.soap.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * A wrapper for a request that lets you modify headers 
 * and read content multiple times.
 * @author Mariusz Jakubowski
 *
 */
public class HeaderMapRequestWrapper extends HttpServletRequestWrapper {
    
    private Map<String, String> headerMap = new HashMap<String, String>();

    private String body;

    private ResettableServletInputStream stream;
    
    
    /**
     * Construct a wrapper for this request.
     * 
     * @param request
     */
    public HeaderMapRequestWrapper(HttpServletRequest request) {
        super(request);
        try {
            this.stream = new ResettableServletInputStream(request.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a header with a given name and a value.
     * 
     * @param name
     * @param value
     */
    public void addHeader(String name, String value) {
        headerMap.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        String headerValue = super.getHeader(name);
        if (headerMap.containsKey(name)) {
            headerValue = headerMap.get(name);
        }
        return headerValue;
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        List<String> names = Collections.list(super.getHeaderNames());
        for (String name : headerMap.keySet()) {
            names.add(name);
        }
        return Collections.enumeration(names);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        List<String> values = Collections.list(super.getHeaders(name));
        if (headerMap.containsKey(name)) {
            values.clear();
            values.add(headerMap.get(name));
        }
        return Collections.enumeration(values);
    }

    /**
     * Returns request body as string.
     * @return
     */
    public String getBody() {
        if (body == null) {
            body = new String(stream.getBytes(), StandardCharsets.UTF_8);
        }
        return body;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }
    
    @Override
    public ServletInputStream getInputStream() throws IOException {
        return stream;
    }
    

}
