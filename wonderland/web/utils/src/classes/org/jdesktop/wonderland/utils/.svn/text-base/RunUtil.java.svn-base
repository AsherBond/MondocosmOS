/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * Sun designates this particular file as subject to the "Classpath" 
 * exception as provided by Sun in the License file that accompanied 
 * this code.
 */
package org.jdesktop.wonderland.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 * @author jkaplan
 */
public class RunUtil {
    /** a logger */
    private static final Logger logger = 
            Logger.getLogger(RunUtil.class.getName());
    
    /** the base directory */
    private static File baseDir;

    /** a thread to cleanup temp directories */
    private static CleanupThread cleanupThread;

    /**
     * Create a temporary directory in the run directory.  An attempt will
     * be made to clean up all temporary directories on exit.
     * 
     * @param prefix the first part of the directory name
     * @param suffix the last part of the name
     * @return the newly created directory
     */
    public static File createTempDir(String prefix, String suffix) {
        File out;
        
        do {
            String fileName = prefix + ((int) (Math.random() * 65536)) + suffix; 
            out = new File(getRunDir(), fileName);
        } while (out.exists());
        
        out.mkdir();
        out.deleteOnExit();
        scheduleForCleanup(out);
        return out;
    }
    
    /**
     * Get the run directory, creating it if necessary.  The default run
     * directory is a Java temp directory, but this can be overridden by
     * specifying a directory with the "wonderland.run.dir" property.
     * <p>
     * If the "wonderland.run.dir.cleanup" property is set, an attempt will
     * be made to clean up the run directory when the server quits.
     * 
     */
    public synchronized static File getRunDir() {
        if (baseDir == null) {
            // first try a property
            String baseDirProp = SystemPropertyUtil.getProperty(Constants.RUN_DIR_PROP);
            
            try {
               
                if (baseDirProp != null) {
                    baseDir = new File(baseDirProp);
                } else {
                    // create a new temp directory
                    baseDir = File.createTempFile("wonderlandweb", ".tmp");
                    
                    // clean up unless we are explicitly told not to
                    if (System.getProperty(Constants.RUN_DIR_CLEANUP_PROP) == null) {
                        System.setProperty(Constants.RUN_DIR_CLEANUP_PROP, "true");
                    }

                    // set the system property
                    System.setProperty(Constants.RUN_DIR_PROP, baseDir.getAbsolutePath());
                }
                    
                baseDir.delete();
                baseDir.mkdirs();
                
                if (Boolean.parseBoolean(System.getProperty(Constants.RUN_DIR_CLEANUP_PROP))) {
                    scheduleForCleanup(baseDir);
                }
            } catch (IOException ioe) {
                logger.warning("Unable to create run directory: " + baseDir);
                baseDir = new File(".");
            }
        }
        return baseDir;
    }

    /**
     * Get the content directory, create it if necessary. The default content
     * directory is found beneath the run directory, the subdirectory that is
     * defined by the wonderland.webserver.content.dir proeprty.
     */
    public synchronized static File getContentDir() {
        String contentSubdir = System.getProperty(Constants.WEBSERVER_CONTENT_DIR_PROP);
        if (contentSubdir == null) {
            contentSubdir = "content";
        }
        File contentDir = new File(RunUtil.getRunDir(), contentSubdir);
        contentDir.mkdirs();
        return contentDir;
    }
    
    /**
     * Extract the given file available as a resource in the current
     * classloader.
     * @param clazz the class file to load the reference relative to.  The
     * url will be loaded with a call to <code>clazz.getResourceAsStream()</code>.
     * @param srcUrl the URL of the source resource in the current
     * class' class loader
     * @param destDir the destination directory to extract to
     * @return the extracted file
     * @throws IOException if there is an error extracting the file
     */
    public static File extract(Class clazz, String srcURL, File destDir) 
            throws IOException 
    {
        // get input from .jar file
        InputStream is = clazz.getResourceAsStream(srcURL);
        
        // write to a file with the same name as the source
        String fileName = srcURL.substring(srcURL.lastIndexOf("/"));
        if (fileName.length() == 1) {
            // ignore directories
            return null;
        }
        
        File out = new File(destDir, fileName);    
        return writeToFile(is, out);
    }
   
    /**
     * Extract the given jar file available as a resource in the current
     * classloader.  This method extracts the contents of the jar file
     * to a directory, and returns that directory.  Currently identical to 
     * calling <code>extractZip()</code> with the same arguments.
     * 
     * @param srcUrl the URL of the source resource in the current class' class
     * loader
     * @param destDir the destination directory to extract to.  Note the
     * contents of the jar will be put in a subdirectory of this directory.
     * @return the directory where the contents of the jar have been 
     * extracted
     * @throws IOException if there is an error extracting the file
     */
    
    public static File extractJar(Class clazz, String srcURL, File destDir) 
            throws IOException 
    {
        return extractZip(clazz, srcURL, destDir);
    }
    
    /**
     * Extract the given zip file available as a resource in the current
     * classloader.  This method extracts the contents of the jar file
     * to a directory, and returns that directory.
     * 
     * @param srcUrl the URL of the source resource in the current class' class
     * loader
     * @param destDir the destination directory to extract to.  Note the
     * contents of the jar will be put in a subdirectory of this directory.
     * @return the directory where the contents of the jar have been 
     * extracted
     * @throws IOException if there is an error extracting the file
     */
    
    public static File extractZip(Class clazz, String srcURL, File destDir) 
            throws IOException 
    {
        // get input from .war file
        InputStream is = clazz.getResourceAsStream(srcURL);
        ZipInputStream zis = new ZipInputStream(is);

        // create the output directory
        String fileName = srcURL.substring(srcURL.lastIndexOf("/"));
        File outputDir = new File(destDir, fileName);
        outputDir.mkdir();
       
        return extractZip(zis, outputDir);
    }
    
    /**
     * Extract a zip input stream to the given directory.  This unzips
     * the entire contents of the input stream, creating any necessary
     * files and directories.
     * @param zis a zip input stream to read
     * @param outputDir the directory to write to
     * @return the output directory
     * @throws IOException if there is an error extracting the zip
     */
    public static File extractZip(ZipInputStream zis, File outputDir)
        throws IOException
    {   
        // write the contents of the jar there
        ZipEntry ze;
        while ((ze = zis.getNextEntry()) != null) {
            if (ze.isDirectory()) {
                // ignore directories
                continue;
            }
            
            File out = new File(outputDir, ze.getName());
            File dir = out.getParentFile();
            if (dir != null && !dir.exists()) {
                dir.mkdirs();
            }
            
            writeToFile(zis, out);
        }
        
        return outputDir;
    }
    
    /**
     * Extract the given input stream to the given output file. Read the 
     * stream completely, and return the newly-created file with the
     * contents of the stream.
     * @param in the input stream to read
     * @param out the file to write to
     * @return the output file (same as the out parameter)
     * @throws IOException if there is an error reading or writing
     */
    public static File writeToFile(InputStream in, File out) 
            throws IOException
    {
        FileOutputStream os = new FileOutputStream(out);
        if (in instanceof FileInputStream) {
            // optimized path for two files
            FileInputStream fis = (FileInputStream) in;
            copyFile(fis.getChannel(), os.getChannel());
            return out;
        }

        BufferedOutputStream bos = new BufferedOutputStream(os);
        BufferedInputStream bis = new BufferedInputStream(in);
        
        byte[] buffer = new byte[1024 * 64];
        int read;
        
        while ((read = bis.read(buffer)) > 0) {
            bos.write(buffer, 0, read);
        }
        
        bos.close();
        return out;
    }
    
    /** 
     * Optimized copy from one file to another
     * @param in the input file channel
     * @param out the output file channel
     * @param size the size of the file
     * @throws IOException if there is an error reading or writing
     */
    public static void copyFile(FileChannel in, FileChannel out)
        throws IOException
    {
        long position = 0;
        long read;

        while ((read = in.transferTo(position, 16 * 1024, out)) > 0) {
            position += read;
        }
    }

    /**
     * Delete a directory.  Dangerous!!
     */
    public static void deleteDir(File base) {
        if (base.isDirectory()) {
            for (File f : base.listFiles()) {
                if (f.isFile()) {
                    f.delete();
                } else if (f.isDirectory()) {
                    deleteDir(f);
                }
            }
        }
        
        base.delete();
    }

    /**
     * Closes a writer stream. Checks whether the writer is null, and does
     * nothing if so. Catching any exception and simply logs the error. This
     * is designed to be invoked from a finally clause.
     *
     * @param writer The writer to close
     */
    public static void close(Writer writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (java.io.IOException excp) {
                logger.log(Level.WARNING, "Failed to close writer", excp);
            }
        }
    }

    /**
     * Closes a reader stream. Checks whether the reader is null, and does
     * nothing if so. Catching any exception and simply logs the error. This
     * is designed to be invoked from a finally clause.
     *
     * @param reader The reader to close
     */
    public static void close(Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (java.io.IOException excp) {
                logger.log(Level.WARNING, "Failed to close reader", excp);
            }
        }
    }

    /**
     * Schedule a file to be cleaned up when the system exits.  If the file
     * does not exist at exit time, it will be ignored.  Directories will
     * be deleted using the deleteDir() method.
     * @param file the file to clean up
     */
    public synchronized static void scheduleForCleanup(File file) {
        if (cleanupThread == null) {
            cleanupThread = new CleanupThread();
            Runtime.getRuntime().addShutdownHook(cleanupThread);
        }

        cleanupThread.add(file);
    }

    static class CleanupThread extends Thread {
        private final List<File> cleanupList =
                new ArrayList<File>();

        public synchronized void add(File file) {
            cleanupList.add(file);
        }

        public synchronized void remove(File file) {
            cleanupList.remove(file);
        }

        @Override
        public void run() {
            synchronized (this) {
                for (File f : cleanupList) {
                    if (f.exists()) {
                        if (f.isDirectory()) {
                            deleteDir(f);
                        } else {
                            f.delete();
                        }
                    }
                }
            }
        }
    }
}
