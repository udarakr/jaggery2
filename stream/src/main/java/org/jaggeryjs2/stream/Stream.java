package org.jaggeryjs2.stream;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class Stream {

    private static InputStream stream;
    private static Stream streamObj;

    public Stream(){}

    public Stream(Object obj) {

        streamObj = new Stream();

        if (obj instanceof String) {
            streamObj.stream = new ByteArrayInputStream(((String) obj).getBytes());

        } else if (obj instanceof InputStream) {
            streamObj.stream = (InputStream) obj;
        } 
    }

    //TODO implemnet toString method
    public String toString() {
        return null;
    }

    public static Object getStream() {
        return stream;
    }

}
