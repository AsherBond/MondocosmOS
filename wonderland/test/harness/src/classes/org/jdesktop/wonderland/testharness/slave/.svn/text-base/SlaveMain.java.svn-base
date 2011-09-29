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
package org.jdesktop.wonderland.testharness.slave;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.jdesktop.wonderland.testharness.common.ClientLoginRequest;
import org.jdesktop.wonderland.testharness.common.ClientLogoutRequest;
import org.jdesktop.wonderland.testharness.common.TestReply;
import org.jdesktop.wonderland.testharness.common.TestRequest;

/**
 *
 * @author paulby
 */
public class SlaveMain {

    private static final Logger logger =
            Logger.getLogger(SlaveMain.class.getName());
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private URL[] jarUrls;
    private HashMap<String, RequestProcessor> processors =
            new HashMap<String, RequestProcessor>();
    private boolean done = false;
    private ReplySender replyHandler;

//    static {
//        new LogControl(SlaveMain.class, "/org/jdesktop/wonderland/testharness/slave/resources/logging.properties");
//    }
    public SlaveMain(String[] args) {

        String masterHostname;
        int masterPort;

        if (args.length < 2) {
            System.err.println("Usage: SlaveMain <master hostname> <master port>");
            System.exit(1);
        }

        masterHostname = args[0];
        masterPort = Integer.parseInt(args[1]);

        try {
//            initJars();

            Socket s = null;
            
            while(s==null) {
                try {
                    s = new Socket(masterHostname, masterPort);
                } catch(ConnectException ce) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(SlaveMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            System.out.println("Opening streams");
            out = new ObjectOutputStream(s.getOutputStream());
            in = new ObjectInputStream(s.getInputStream());

            replyHandler = new ReplySender() {
                public void sendReply(TestReply reply) {
                    try {
                        out.writeObject(reply);
                    } catch (IOException ex) {
                        Logger.getLogger(SlaveMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };

            // Register the output stream with the log handler
            SlaveLogHandler.setOutputStream(out);

            do {
                try {
                    System.out.println("Waiting for request...");
                    TestRequest request = (TestRequest) in.readObject();
                    System.out.println("Slave got request " + request);
                    processRequest(request);
                } catch (IOException ex) {
                    done = true;
                    logger.log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    logger.log(Level.SEVERE, null, ex);
                } catch (ProcessingException pe) {
                    logger.log(Level.WARNING, "Unable to process request", pe);
                }
            } while (!done);
        } catch (UnknownHostException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
//        } catch (ProcessingException ex) {
//            logger.log(Level.SEVERE, null, ex);
        } finally {
            // close down connections when the master disconnects
            for (RequestProcessor rp : processors.values()) {
                rp.destroy();
            }
        }
    }

    private void initJars() throws ProcessingException {
        InputStream processor = getClass().getResourceAsStream("/testharness-slave-processors.jar");
        InputStream client = getClass().getResourceAsStream("/wonderland-client.jar");

        try {
            jarUrls = new URL[]{writeJar(processor), writeJar(client)};

            // Set up the URL handler based on the WonderlandURLStreamHandler.
            // We do this here because we only want to do it once per VM.
            //
            // Note that this means that all instances of the test will share
            // the same asset database, because the URL handlers for asset
            // URLs will always map to this classloader.  For the purpose of
            // most scalability tests, this should be fine.
            ClassLoader loader = new URLClassLoader(jarUrls, getClass().getClassLoader());
            Class c = loader.loadClass("org.jdesktop.wonderland.client.jme.WonderlandURLStreamHandlerFactory");
            URLStreamHandlerFactory factory = (URLStreamHandlerFactory) c.newInstance();
            URL.setURLStreamHandlerFactory(factory);
        } catch (ClassNotFoundException cnfe) {
            throw new ProcessingException(cnfe);
        } catch (InstantiationException ie) {
            throw new ProcessingException(ie);
        } catch (IllegalAccessException iae) {
            throw new ProcessingException(iae);
        } catch (IOException ioe) {
            throw new ProcessingException(ioe);
        }
    }

    private void processRequest(TestRequest request) throws ProcessingException {
        if (request instanceof ClientLoginRequest) {
            ClientLoginRequest lr = (ClientLoginRequest) request;
            RequestProcessor rp = createProcessor(lr.getProcessorName());
            rp.initialize(lr.getUsername(), lr.getProps(), replyHandler);

            processors.put(request.getUsername(), rp);

        } else if (request instanceof ClientLogoutRequest) {
            RequestProcessor rp = processors.remove(request.getUsername());
            if (rp != null) {
                rp.destroy();
            }
        } else {
            RequestProcessor rp = processors.get(request.getUsername());
            if (rp == null) {
                logger.severe("No processor found for " + request.getUsername());
                return;
            }

            rp.processRequest(request);
        }

    }

    private RequestProcessor createProcessor(String processorName) throws ProcessingException {
        String pkgName = getClass().getPackage().getName();
        String clazzName = pkgName + "." + processorName;

        InputStream processor = getClass().getResourceAsStream("/testharness-slave-processors.jar");
        InputStream client = getClass().getResourceAsStream("/wonderland-client.jar");

        try {
            URL[] urls = new URL[]{writeJar(processor), writeJar(client)};
            ClassLoader loader = new URLClassLoader(urls, getClass().getClassLoader());

            Class rpClazz = loader.loadClass(clazzName);
            return (RequestProcessor) rpClazz.newInstance();
        } catch (ClassNotFoundException cnfe) {
            throw new ProcessingException(cnfe);
        } catch (InstantiationException ie) {
            throw new ProcessingException(ie);
        } catch (IllegalAccessException iae) {
            throw new ProcessingException(iae);
        } catch (IOException ioe) {
            throw new ProcessingException(ioe);
        }
    }

    private URL writeJar(InputStream is)
            throws IOException {
        File outFile = File.createTempFile("testharness", "jar");
        outFile.deleteOnExit();
        FileOutputStream outWriter = new FileOutputStream(outFile);

        BufferedInputStream bis = new BufferedInputStream(is);
        byte[] buffer = new byte[16 * 1024];

        int read;
        while ((read = bis.read(buffer)) != -1) {
            outWriter.write(buffer, 0, read);
        }

        outWriter.close();
        return outFile.toURI().toURL();
    }

    public static void main(String[] args) {
        new SlaveMain(args);
    }

    public interface ReplySender {
        public void sendReply(TestReply request);
    }
}
