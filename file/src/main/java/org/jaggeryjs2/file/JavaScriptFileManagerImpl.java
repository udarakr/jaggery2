package org.jaggeryjs2.file;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class JavaScriptFileManagerImpl implements JavaScriptFileManager {

    private static final Log log = LogFactory.getLog(JavaScriptFileManagerImpl.class);

    public JavaScriptFile getJavaScriptFile(Object object) throws Exception {
        if (object instanceof String) {
            String uri = (String) object;
            return new JavaScriptFileImpl(uri, getFile(uri).getAbsolutePath());
        } else {
            String msg = "Unsupported parameter to the File constructor : " + object.getClass();
            log.error(msg);
            //TODO throw proper exception from core
            throw new Exception();
        }
    }

    @SuppressFBWarnings("PATH_TRAVERSAL_IN")
    public File getFile(String uri) {
        File file = null;
        if (uri.startsWith("file://")) {
            try {
                file = FileUtils.toFile(new URL(uri));
            } catch (MalformedURLException e) {
                log.error(e.getMessage(), e);
                //TODO throw exception from core
            }
        } else {
            String oldPath = uri;
            uri = FilenameUtils.normalizeNoEndSeparator(uri);
            if (uri == null) {
                String msg = "Invalid file URI : " + oldPath;
                log.error(msg);
                //TODO throw exception from core
            }
            file = new File(uri);
        }

        return file;
    }

    public String getDirectoryPath(String path) {
        return getFile(path).getAbsolutePath();
    }
}
