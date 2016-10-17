package org.jaggeryjs2.file;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public interface JavaScriptFile {

    public void construct();

    public void open(String mode);

    public void close();

    public String read(long count);

    public void write(String data);

    public void write(InputStream data);

    public String readAll();

    public boolean move(String dest);

    public boolean del();

    public long getLength();

    public long getLastModified();

    public String getName();

    public boolean isExist();

    public InputStream getInputStream();

    public OutputStream getOutputStream();

    public String getContentType();

    public boolean saveAs(String dest);

    public boolean mkdir();

    public boolean isDirectory();

    public String getPath();

    public String getURI();

    public ArrayList<String> listFiles();
}
