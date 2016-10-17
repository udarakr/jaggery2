package org.jaggeryjs2.file;


import java.io.File;

public interface JavaScriptFileManager {

    public JavaScriptFile getJavaScriptFile(Object object) throws Exception;

    public File getFile(String path);

    public String getDirectoryPath(String path);

}
