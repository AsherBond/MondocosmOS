/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2010, Sun Microsystems, Inc., All Rights Reserved
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
package org.jdesktop.wonderland.runner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.jdesktop.wonderland.utils.SystemPropertyUtil;

/**
 * Managed deployment properties.
 * @author jkaplan
 */
public class DeploymentManager {
    private static final Logger logger =
            Logger.getLogger(DeploymentManager.class.getName());

    // singleton instance
    private static DeploymentManager inst;
    
    // default place to read the plan from
    private static final String DEFAULT_PLAN = "/META-INF/deploy/DeploymentPlan.xml";

    // the system property for the plan URL
    private static final String REMOTE_PLAN_URL_PROP =
            "wonderland.deployment.plan.url";

    // the deployment plan
    private DeploymentPlan plan;

    // whether the plan has been set manually, or whether it was loaded
    // automatically
    private boolean planSet = false;

    /**
     * Constructor is private.  Use getInstance() instead.
     */
    private DeploymentManager() {
        try {
            plan = loadPlan();

            logger.info("Loaded deployment plan: " + plan);
        } catch (IOException ioe) {
            throw new IllegalStateException("Unable to load plan", ioe);
        }
    }
    
    /**
     * Get the default DeploymentManager instance
     * @return the deployment manager
     */
    public synchronized static DeploymentManager getInstance() {
        if (inst == null) {
            inst = new DeploymentManager();
        }
        
        return inst;
    }
    
    /**
     * Get the current deployment plan
     * @return the current deployment plan
     */
    public synchronized DeploymentPlan getPlan() {
        // if the plan was set manually, return the version that was
        // set.  Otherwise, reload the plan to get any changes since we
        // last started
        if (!planSet) {
            try {
                plan = loadPlan();
            } catch (IOException ioe) {
                logger.log(Level.WARNING, "Error loading plan", ioe);
            }
        }

        return plan;
    }
    
    /**
     * Set the plan to a different one.  Note this is not saved by default
     * until savePlan() is called.
     * @param plan the new plan
     */
    public synchronized void setPlan(DeploymentPlan plan) {
        this.plan = plan;
        this.planSet = true;
    }

    /**
     * Get a deployment entry by name
     * @param name the name of the deployment entry to get
     * @return the entry with the given name, or null if no entry
     * exists with the given name
     */
    public DeploymentEntry getEntry(String name) {
        return getPlan().getEntry(name);
    }
 
    /**
     * Load a plan from disk
     * @return the loaded plan
     */
    protected DeploymentPlan loadPlan() throws IOException {
        Reader planReader;

        if (getRemotePlanURL() != null) {
            logger.info("Loading deployment plan from URL: " + getRemotePlanURL());
            planReader = new InputStreamReader(getRemotePlanURL().openStream());
        } else if (getPlanFile().exists()) {
            logger.info("Loading deployment plan from file: " + getPlanFile());
            planReader = new FileReader(getPlanFile());
        } else {
            logger.info("Reading default deployment plan");
            planReader = new InputStreamReader(
                    DeploymentManager.class.getResourceAsStream(DEFAULT_PLAN));
        }

        // the plan was loaded
        this.planSet = false;

        try {
            return DeploymentPlan.decode(planReader);
        } catch (JAXBException je) {
            IOException ioe = new IOException("Error decoding plan file");
            ioe.initCause(je);
            throw ioe;
        }   
    }
    
    /**
     * Save the current deployment plan to disk.
     */
    public synchronized void savePlan() throws IOException {
        // remote plans can't be saved
        if (getRemotePlanURL() != null) {
            throw new IllegalStateException("Remote plans can't be saved");
        }

        try {
            FileWriter writer = new FileWriter(getPlanFile());
            getPlan().encode(writer);
            writer.close();
            
            // the plan has no longer been set manually, since it has now
            // been saved
            this.planSet = false;
        } catch (JAXBException je) {
            IOException ioe = new IOException("Error encoding plan file " + 
                                              getPlanFile());
            ioe.initCause(je);
            throw ioe;
        }
    }

    /**
     * Remove the current plan, restoring the defaults
     */
    public synchronized void removePlan() throws IOException {
        File planFile = getPlanFile();
        if (planFile.exists()) {
            planFile.delete();
        }

        // reload the default plan
        plan = loadPlan();
    }

    /**
     * Get the URL of the deployment plan from a system property
     * @return the deployment plan URL
     */
    protected URL getRemotePlanURL() {
        URL out = null;

        String remotePlanStr = SystemPropertyUtil.getProperty(REMOTE_PLAN_URL_PROP);
        if (remotePlanStr != null) {
            try {
                out = new URL(remotePlanStr);
            } catch (MalformedURLException mue) {
                logger.log(Level.WARNING, "Error reading remote plan URL " +
                           remotePlanStr, mue);
            }
        }

        return out;
    }

    /**
     * Get the directory to load or save deployment plans from
     * @return the deployment plan directory
     */
    protected File getPlanFile() {
        String deployDirName = SystemPropertyUtil.getProperty("wonderland.config.dir");
        File deployDir = new File(deployDirName);
        return new File(deployDir, "DeploymentPlan.xml");
    }   
}
