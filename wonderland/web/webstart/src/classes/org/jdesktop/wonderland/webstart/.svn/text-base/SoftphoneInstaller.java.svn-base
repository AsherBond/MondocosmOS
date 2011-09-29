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
package org.jdesktop.wonderland.webstart;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.ClientContext;
import org.jdesktop.wonderland.client.jme.WebstartStartupListener;
import org.jdesktop.wonderland.client.softphone.SoftphoneControl;

/**
 * Install the softphone on webstart startup
 * @author jkaplan
 */
public class SoftphoneInstaller implements WebstartStartupListener {
    private static final Logger logger =
            Logger.getLogger(SoftphoneInstaller.class.getName());
    
    private static final String SOFTPHONE_JAR_NAME = "softphone.jar";
    private static final String SOFTPHONE_BUILD_DATE = "softphone-build-date";
   
    /** 
     * Creates a new instance of SoftphoneExtInstaller 
     */
    public SoftphoneInstaller() {
    }
    
    public void onStartup() {
        try {
            File softphoneDir = new File(ClientContext.getUserDirectory(),
                                           "softphone");
            File softphoneJar = checkCurrentSoftphone(getSoftphoneBuildDate(),
                                                      softphoneDir);
            if (softphoneJar != null && softphoneJar.exists()) {
                logger.info("Softphone is up-to-date");
                System.setProperty(SoftphoneControl.SOFTPHONE_PROP, 
                                   softphoneJar.getCanonicalPath());
                return;
            }
        
            logger.info("Installing new softphone");
            
            // extract the softphone
            softphoneJar = new File(softphoneDir, SOFTPHONE_JAR_NAME);
            InputStream softphoneIs = 
                getClass().getResourceAsStream("/resources/" + 
                                               SOFTPHONE_JAR_NAME);
            writeToFile(softphoneIs, softphoneJar);
        
            System.setProperty(SoftphoneControl.SOFTPHONE_PROP,
                               softphoneJar.getCanonicalPath());
        
        
            // extract the build date
            File dateFile = new File(softphoneDir, SOFTPHONE_BUILD_DATE);
            InputStream dateIs =
                getClass().getResourceAsStream("/resources/" + 
                                               SOFTPHONE_BUILD_DATE);
            writeToFile(dateIs, dateFile);
        } catch (IOException ioe) {
            logger.log(Level.WARNING, "Unable to extract softphone", ioe);
        }
    }
    
    public static URL getSoftphoneURL() {
	return SoftphoneInstaller.class.getResource(SOFTPHONE_JAR_NAME);
    }

    public static String getSoftphoneBuildDate() {
	InputStream is = SoftphoneInstaller.class.getResourceAsStream(
                "/resources/" + SOFTPHONE_BUILD_DATE);

	if (is == null) {
	    return null;
	}

	try {
	    byte[] buf = new byte[is.available()];

	    is.read(buf);
	    return new String(buf);
	} catch (IOException e) {
	    logger.log(Level.WARNING, "Unable to read softphone build date", e);
	    return null;
	}
    }

    /**
     * Determines if the date of the incoming jnlp bundle matches the date of 
     * the currently installed bundle.  If they match, this returns the existing
     * softphone jar file.  If not, it returns null, indicating the softphone
     * should be re-installed.
     *  
     * The date of the installed bundle is stored in the file:
     * ~/.wonderland/<version>/softphone/softphone-build-date.
     */
    private static File checkCurrentSoftphone(String buildDate,
                                              File softphoneDir)
    {
        if (!softphoneDir.exists()) {
            softphoneDir.mkdir();
            return null;
        }
        
        // This file contains the build date of the installed xremwin
	File instBuildDateFile = new File(softphoneDir, SOFTPHONE_BUILD_DATE);
	File instSoftphone = new File(softphoneDir, SOFTPHONE_JAR_NAME);
        if (!instBuildDateFile.exists() || !instSoftphone.exists()) {
	    return null;
	}

	String instBuildDate;
	try {
	    BufferedReader br = new BufferedReader(new FileReader(instBuildDateFile));
	    instBuildDate = br.readLine();
	    br.close();
	} catch (FileNotFoundException ex) {
	    return null;
	} catch (IOException ex) {
	    return null;
	}
        
        logger.fine("Installed date is " + instBuildDate);
        
	if (instBuildDate.equals(buildDate)) {
	    return instSoftphone;
	} else {
            return null;
        } 
    }
    
    private static void writeToFile(InputStream in, File out) 
        throws IOException
    {
        FileOutputStream os = new FileOutputStream(out);
        BufferedOutputStream bos = new BufferedOutputStream(os);
        BufferedInputStream bis = new BufferedInputStream(in);
        
        byte[] buffer = new byte[1024 * 64];
        int read;
        
        while ((read = bis.read(buffer)) > 0) {
            bos.write(buffer, 0, read);
        }
        
        bos.close();
    }
}
