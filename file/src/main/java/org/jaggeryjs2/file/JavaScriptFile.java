package org.jaggeryjs2.file;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public interface JavaScriptFile {

    public void construct() throws Exception;

    public void open(String mode) throws Exception;

    public void close() throws Exception;

    public String read(long count) throws Exception;

    public void write(String data) throws Exception;

    public void write(InputStream data) throws Exception;

    public String readAll() throws Exception;

    public boolean move(String dest) throws Exception;

    public boolean del() throws Exception;

    public long getLength() throws Exception;

    public long getLastModified() throws Exception;

    public String getName() throws Exception;

    public boolean isExist() throws Exception;

    public InputStream getInputStream() throws Exception;

    public OutputStream getOutputStream() throws Exception;

    public String getContentType() throws Exception;

    public boolean saveAs(String dest) throws Exception;

    public boolean mkdir() throws Exception;

    public boolean isDirectory();

    public String getPath();

    public String getURI();

    public ArrayList<String> listFiles();
}
