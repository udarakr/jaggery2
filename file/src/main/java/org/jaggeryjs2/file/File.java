package org.jaggeryjs2.file;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jaggeryjs2.stream.Stream;

import javax.activation.FileTypeMap;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.wso2.carbon.utils.CarbonUtils;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

public class File {

    private static final Log log = LogFactory.getLog(File.class);

    private JavaScriptFile file;
    private JavaScriptFileManagerImpl manager;
    private static File fileObj;

    private static boolean mimeMapLoaded = false;
    private static final String RESOURCE_MEDIA_TYPE_MAPPINGS_FILE = "mime.types";

    public File (){}

    public File(String path) throws Exception {

        fileObj = new File();
        fileObj.manager = new JavaScriptFileManagerImpl();

        fileObj.file = fileObj.manager.getJavaScriptFile(path);
        fileObj.file.construct();
    }

    public static void open(String mode){
        fileObj.file.open(mode);
    }

    public static void write(Object obj) {
        if (obj instanceof InputStream) {
            fileObj.file.write((InputStream) obj);
        } else if (obj instanceof Stream) {
           // fileObj.file.write(((Stream) obj).getStream());
        } else {
            //TODO implement Util
            //fileObj.file.write(Util.serializeObject(obj));
        }
    }

    public static String read(int numberOfCharacters) {
        return fileObj.file.read(numberOfCharacters);
    }

    public static String readAll() {
        return fileObj.file.readAll();
    }

    public static void close() {
        fileObj.file.close();
    }

    public static boolean move(String target) throws Exception {
        return fileObj.file.move(fileObj.manager.getJavaScriptFile(target).getURI());
    }

    public static boolean saveAs(String target) throws Exception {
        return fileObj.file.saveAs(fileObj.manager.getJavaScriptFile(target).getURI());
    }

    public static boolean del() {
        return fileObj.file.del();
    }

    public static long getLength() {
        return fileObj.file.getLength();
    }

    public static long getLastModified() {
        return fileObj.file.getLastModified();
    }

    public static String getName() {
        return fileObj.file.getName();
    }

    public static boolean isExists() {
        return fileObj.file.isExist();
    }

    public static String getContentType() throws Exception{


        if (!mimeMapLoaded) {
            FileTypeMap.setDefaultFileTypeMap(loadMimeMap());
            mimeMapLoaded = true;
        }

        return fileObj.file.getContentType();
    }

    //TODO implement getStream method

    public static boolean isDirectory() {
        return fileObj.file.isDirectory();
    }

    public static String getPath() {
        return fileObj.file.getURI();
    }

    public static boolean mkdir() {
        return fileObj.file.mkdir();
    }

    //TODO implement listfiles

    /**
     * To zip a folder
     *
     * @param dest Zip file path to zip the folder
     * @throws Exception
     */
    @SuppressFBWarnings({"PATH_TRAVERSAL_IN", "PATH_TRAVERSAL_OUT", "PATH_TRAVERSAL_IN"})
    public static boolean zip(String dest) throws IOException {

        ZipOutputStream zip = null;

        if (fileObj.file.isExist()) {

            String destinationPath = fileObj.manager.getFile(dest).getAbsolutePath();
            String sourcePath = fileObj.manager.getDirectoryPath(fileObj.file.getPath());
            java.io.File destinationFile = new java.io.File(destinationPath);
            if (destinationFile.getParentFile().exists() || destinationFile.getParentFile().mkdirs()) {
                try {
                    zip = new ZipOutputStream(new FileOutputStream(destinationPath));
                    java.io.File folder = new java.io.File(sourcePath);
                    if (folder.list() != null) {
                        for (String fileName : folder.list()) {
                            addFileToZip("", sourcePath + java.io.File.separator + fileName, zip);
                        }
                    }
                    return true;
                } catch (IOException ex) {
                    log.error("Cannot zip the folder. " + ex);
                    throw new IOException(ex);
                } finally {
                    if (zip != null) {
                        try {
                            zip.flush();
                            zip.close();
                        } catch (IOException er) {
                            log.error("Unable to close the zip output stream " + er);
                        }
                    }
                }
            } else {
                log.error("Unable to create the directory path for file : "+ fileObj.file.getName());
            }
        }else {
            log.error("Zip operation cannot be done. Folder not found");
        }
        return false;
    }

    /**
     * To unzip a zip file
     *

     * @param dest Path to unzip the zip file
     * @throws Exception
     */
    @SuppressFBWarnings({"PATH_TRAVERSAL_IN", "PATH_TRAVERSAL_IN", "PATH_TRAVERSAL_IN"})
    public static boolean jsFunction_unZip(String dest) throws IOException {

        ZipInputStream zin = null;
        BufferedOutputStream out = null;
        if (fileObj.file.isExist()) {

            java.io.File zipfile = new java.io.File(fileObj.manager.getFile(fileObj.file.getPath()).getAbsolutePath());
            java.io.File outdir = new java.io.File(fileObj.manager.getDirectoryPath(dest));
            if (outdir.getParentFile().exists() || outdir.getParentFile().mkdirs()) {
                if (outdir.exists() || outdir.mkdir()) {
                    try {
                        zin = new ZipInputStream(new FileInputStream(zipfile));
                        ZipEntry entry;
                        String name, dir;
                        byte[] buffer = new byte[1024];
                        while ((entry = zin.getNextEntry()) != null) {
                            name = entry.getName();
                            if (entry.isDirectory()) {
                                mkdirs(outdir, name);
                                continue;
                            }
                            int hasParentDirs = name.lastIndexOf(java.io.File.separatorChar);
                            dir = (hasParentDirs == -1) ? null : name.substring(0, hasParentDirs);
                            if (dir != null) {
                                mkdirs(outdir, dir);
                            }
                            try {
                                out = new BufferedOutputStream(new FileOutputStream(new java.io.File(outdir, name)));
                                int count;
                                while ((count = zin.read(buffer)) != -1) {
                                    out.write(buffer, 0, count);
                                }
                            } catch (Exception ex){
                                log.error("Unable to perform unZip operation for file : "+ fileObj.file.getName(), ex);
                                return false;
                            } finally {
                                if (out != null) {
                                    try {
                                        out.close();
                                    } catch (IOException er) {
                                        log.error("Unable to close the output stream " + er);
                                    }
                                }
                            }
                        }
                        return true;
                    } catch (IOException ex) {
                        log.error("Cannot unzip the file " + ex);
                        throw new IOException(ex);
                    } finally {
                        if (zin != null) {
                            try {
                                zin.close();
                            } catch (IOException er) {
                                log.error("Unable to close the zip input stream " + er);
                            }
                        }
                    }
                } else {
                    log.error("Unable to create directories to handle file : "+  fileObj.file.getName());
                }
            } else {
                log.error("Unable to create directories to handle file : "+  fileObj.file.getName());
            }
        } else {
            log.error("Zip file not exists");
        }
        return false;
    }

    //TODO move to util if needed
    @SuppressFBWarnings("PATH_TRAVERSAL_IN")
    private static FileTypeMap loadMimeMap() throws Exception {
        String configDirPath = CarbonUtils.getCarbonConfigDirPath();
        java.io.File configFile = new java.io.File(configDirPath, RESOURCE_MEDIA_TYPE_MAPPINGS_FILE);
        if (!configFile.exists()) {
            String msg = "Resource media type definitions file (mime.types) file does " +
                    "not exist in the path " + configDirPath;
            throw new Exception(msg);
        }


        final Map<String, String> mimeMappings = new HashMap<String, String>();

        final String mappings;
        try {
            mappings = FileUtils.readFileToString(configFile, "UTF-8");
        } catch (IOException e) {
            String msg = "Error opening resource media type definitions file " +
                    "(mime.types) : " + e.getMessage();
            throw new Exception(msg, e);
        }
        String[] lines = mappings.split("[\\r\\n]+");
        for (String line : lines) {
            if (!line.startsWith("#")) {
                String[] parts = line.split("\\s+");
                for (int i = 1; i < parts.length; i++) {
                    mimeMappings.put(parts[i], parts[0]);
                }
            }
        }

        return new FileTypeMap() {
            @Override
            public String getContentType(java.io.File file) {
                return getContentType(file.getName());
            }

            @Override
            public String getContentType(String fileName) {
                int i = fileName.lastIndexOf('.');
                if (i > 0) {
                    String mimeType = mimeMappings.get(fileName.substring(i + 1));
                    if (mimeType != null) {
                        return mimeType;
                    }
                }
                return "application/octet-stream";
            }
        };
    }

    /**
     * To add a file to zip
     *
     * @param path    Root path name
     * @param srcFile Source File that need to be added to zip
     * @param zip     ZipOutputStream
     * @throws IOException
     */
    @SuppressFBWarnings({"PATH_TRAVERSAL_IN", "PATH_TRAVERSAL_IN"})
    private static void addFileToZip(String path, String srcFile, ZipOutputStream zip) throws IOException {
        FileInputStream in = null;
        try {
            java.io.File folder = new java.io.File(srcFile);
            if (folder.isDirectory()) {
                addFolderToZip(path, srcFile, zip);
            } else {
                byte[] buf = new byte[1024];
                int len;
                in = new FileInputStream(srcFile);
                zip.putNextEntry(new ZipEntry(path + java.io.File.separator + folder.getName()));
                while ((len = in.read(buf)) > 0) {
                    zip.write(buf, 0, len);
                }
            }
        } catch (IOException er) {
            throw new IOException(er);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    /**
     * To add a folder to zip
     *
     * @param path      Path of the file or folder from root directory of zip
     * @param srcFolder Source folder to be made as zip
     * @param zip       ZipOutputStream
     */
    @SuppressFBWarnings("PATH_TRAVERSAL_IN")
    private static void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) throws IOException {
        java.io.File folder = new java.io.File(srcFolder);
        if (path.isEmpty()) {
            zip.putNextEntry(new ZipEntry(folder.getName() + java.io.File.separator));
        } else {
            zip.putNextEntry(new ZipEntry(path + java.io.File.separator + folder.getName() + java.io.File.separator));
        }
        for (String fileName : folder.list()) {
            if (path.isEmpty()) {
                addFileToZip(folder.getName(), srcFolder + java.io.File.separator + fileName, zip);
            } else {
                addFileToZip(path + java.io.File.separator + folder.getName(), srcFolder + java.io.File.separator + fileName, zip);
            }
        }
    }

    /**
     * To create the recursive directories in a specific path
     *
     * @param parentDirectory Parent of the directory
     * @param path            Path of the the child directory to be created inside
     */
    @SuppressFBWarnings("PATH_TRAVERSAL_IN")
    private static boolean mkdirs(java.io.File parentDirectory, String path) {
        java.io.File dir = new java.io.File(parentDirectory, path);
        return dir.exists() || dir.mkdirs();
    }
}
