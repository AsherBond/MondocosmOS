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
package org.jdesktop.wonderland.client.jme;

import java.net.URL;
import java.security.Permission;
import java.util.Iterator;
import java.util.logging.Logger;
import sun.misc.Service;

/**
 *
 * @author jkaplan
 */
public class Webstart {
    // logger
    private static final Logger logger = 
            Logger.getLogger(Webstart.class.getName());
    
    // check for Java Web Start
    private static boolean webstartChecked = false;
    private static boolean isWebstart = false;
    
    public synchronized static boolean isWebstart() {
        if (!webstartChecked) {
             // Check for webstart/jnlp
            try {
                Class.forName("javax.jnlp.BasicService");
                isWebstart = true;
            } catch (Exception ex) {
                isWebstart = false;
            } finally {
                webstartChecked = true;
            }
        } 
        
        return isWebstart;
    }
    
    public static void webstartSetup() {
        logger.warning("Running from Java Web Start. Performing setup.");

        // setup a URL stream handler for the Wonderland protocols.  This
        // works around the fact that URL will only find handlers
        // loaded in the system classloader, and in webstart the
        // handlers we need (wonderland.protocol.*) are in the jnlp classloader.
        //
        //
        // Note if we return null here, it will go on to try the normal
        // mechanisms defined in URL
        URL.setURLStreamHandlerFactory(new WonderlandURLStreamHandlerFactory());
        
        // set our own security manager
        logger.info("Setting security manager");
        System.setSecurityManager(new JnlpSecurityManager());
        
        // discover listeners using service loader mechanism
        Iterator<WebstartStartupListener> it = 
                Service.providers(WebstartStartupListener.class);
        while (it.hasNext()) {
            WebstartStartupListener wsl = it.next();
            wsl.onStartup();
        }
    }
       
    /**
     *
     * Simple Security Manager for JNLP deployment.
     */
    public static class JnlpSecurityManager extends SecurityManager {

        private boolean readAccessGranted = false;
        private boolean readDontAskAgain = false;
        private boolean writeAccessGranted = false;
        private boolean writeDontAskAgain = false;

        /** Creates a new instance of JnlpSecurityManager */
        public JnlpSecurityManager() {
        }

        @Override
        public void checkPermission(Permission perm) {
        }

        @Override
        public void checkPermission(Permission perm, Object context) {
        }

        /**
         * The Wonderland client should never exit, unless when specifically closed.
         * There are, however, a slew of System.exit() calls throughout the code upon
         * error conditions. Here, we catch when these are called and print out a stack
         * track for debugging purposes.
         */
        @Override
        public void checkExit(int status) {
            logger.info("Wonderland client is either exiting or checking whether it can exit (just FYI).");

            /* Print out the stack trace (don't know how to do it other than manually) */
            StackTraceElement[] els = Thread.currentThread().getStackTrace();
            StringBuilder str = new StringBuilder("Information Stack trace from checkExit():\n");
            for (StackTraceElement el : els) {
                str.append("    at " + el.getClassName() + "." + el.getMethodName() +
                        "(" + el.getFileName() + ":" + el.getLineNumber() + ")\n");
            }
            logger.info(str.toString());
            super.checkExit(status);
        }  
        
//      private synchronized void askUser(boolean readRequest, String filename) {
//        if (readRequest && readDontAskAgain) 
//            if (!readAccessGranted)
//                throw new SecurityException("User Denied Access to file");
//            else
//                return;
//        
//        if (!readRequest && writeDontAskAgain)
//            if (!writeAccessGranted)
//                throw new SecurityException("User Denied Access to file");
//            else
//                return;
//                        
//        JnlpFileAccessDialog d = new JnlpFileAccessDialog(new JFrame(), true, readRequest, filename);
//        d.setVisible(true);
//        if (readRequest) {
//            readAccessGranted = d.isAccessGranted();
//            readDontAskAgain = d.dontAskAgain();
//        } else {
//            writeAccessGranted = d.isAccessGranted();
//            writeDontAskAgain = d.dontAskAgain();  
//            logger.severe("ANSWER "+writeAccessGranted+" "+writeDontAskAgain);
//        }
//        
//        if (!d.isAccessGranted())
//            throw new SecurityException("User Denied Access to file");        
//    }
    }
}
