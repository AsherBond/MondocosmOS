/**
 * Open Wonderland
 *
 * Copyright (c) 2010, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */

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
package org.jdesktop.wonderland.modules.service;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.modules.spi.ModuleDeployerSPI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jdesktop.wonderland.common.modules.ModuleInfo;
import org.jdesktop.wonderland.modules.Module;
import org.jdesktop.wonderland.modules.ModulePart;
import sun.misc.Service;

/**
 * The DeployManager is responsible for deploying modules during installation.
 * It loads the a list of deployers (each implement ModuleDeployerSPI) and
 * deploys and undeploys modules to/from them.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class DeployManager {
    /** The instantiated deployers, mapped by class */
    private final Map<Class, ModuleDeployerSPI> deployers =
            new LinkedHashMap<Class, ModuleDeployerSPI>();

    /* The error logger */
    private Logger logger = Logger.getLogger(DeployManager.class.getName());
    
    /**
     * Constructor
     */
    public DeployManager() {
    }
    
    /**
     * Deploys a given module to the proper deployers. The contract between the
     * module manager and the deployers is that once the deployers say that a
     * module may be deployed to it, the deployment should not fail. If it does
     * then DeployerException is thrown.
     * 
     * @param module The module to deploy
     * @throw DeployerException If the module cannot be deployed
     */
    public void deploy(Module module) throws DeployerException {
        logger.info("[DEPLOY] Deploying module " + module.getName());
        
        /*
         * If the deployment fails for one deployer, then keep an exception
         * around for it.
         */
        DeployerException deployException = null;
        
        /*
         * Fetch all of the deployers and iterate through them. For each of
         * the parts they support, find whether the module contains such a
         * part. If so, deploy the module.
         */
        Map<String, ModulePart> parts = module.getParts();
        Iterator<ModuleDeployerSPI> it = getDeployers().iterator();
        logger.info("[DEPLOY] Module has parts " + parts.keySet().toString());
        logger.info("[DEPLOY] Number of Deployers " + this.deployers.size());
        for (Class clazz : deployers.keySet()) {
            logger.info("[DEPLOY] Deployer Class " + clazz.getName() + " " +
                    deployers.get(clazz).getName());
        }
        
        while (it.hasNext() == true) {
            /*
             * Fetch the module part types that the deployer supports. If none,
             * just continue to the next module.
             */
            ModuleDeployerSPI deployer = it.next();
            String[] partTypes = deployer.getTypes();
            if (partTypes == null) {
                continue;
            }
            
            /*
             * Loop through each part type and see if there is a module part
             */
            for (String partType : partTypes) {
                if (parts.containsKey(partType) == true) {
                    try {
                        logger.info("[DEPOY] Deploying " + module.getName() + " to " + deployer.getName());
                        deployer.deploy(partType, module, parts.get(partType));
                    } catch (java.lang.Exception excp) {
                        /*
                         * Catch all exceptions here. Report them and pass them
                         * up as DeployerException, but continue to
                         * the remainder of the deployers.
                         */
                        logger.log(Level.WARNING, "[DEPLOY] Failed", excp);
                        deployException = new DeployerException(deployer.getName(), module);
                    }
                }
            }
        }
        
        /* If there is an exception then throw it, otherwise just return */
        if (deployException != null) {
            throw deployException;
        }
    }
    
    /**
     * Undeploys a given module to the proper deployers. The contract between the
     * module manager and the deployers is that once the deployers say that a
     * module may be undeployed to it, the undeployment should not fail. If it does
     * then DeployerException is thrown.
     * 
     * @param module The module to undeploy
     * @throw DeployerException If the module cannot be deployed
     */
    public void undeploy(Module module) throws DeployerException {
        /*
         * If the deployment fails for one deployer, then keep an exception
         * around for it.
         */
        DeployerException undeployException = null;
        
        /*
         * Fetch all of the deployers and iterate through them. For each of
         * the parts they support, find whether the module contains such a
         * part. If so, deploy the module.
         */
        Map<String, ModulePart> parts = module.getParts();
        Iterator<ModuleDeployerSPI> it = getDeployers().iterator();
        while (it.hasNext() == true) {
            /*
             * Fetch the module part types that the deployer supports. If none,
             * just continue to the next module.
             */
            ModuleDeployerSPI deployer = it.next();
            String[] partTypes = deployer.getTypes();
            if (partTypes == null) {
                continue;
            }
            
            /*
             * Loop through each part type and see if there is a module part
             */
            for (String partType : partTypes) {
                if (parts.containsKey(partType) == true) {
                    try {
                        deployer.undeploy(partType, module, parts.get(partType));
                    } catch (java.lang.Exception excp) {
                        /*
                         * Catch all exceptions here. Report them and pass them
                         * up as DeployerException, but continue to
                         * the remainder of the deployers.
                         */
                        logger.log(Level.WARNING, "[UNDEPLOY] Failed", excp);
                        undeployException = new DeployerException(deployer.getName(), module);
                    }
                }
            }
        }
        
        /* If there is an exception then throw it, otherwise just return */
        if (undeployException != null) {
            throw undeployException;
        }
    }
    
    /**
     * Returns true if all of the deployers can deploy the parts of the module,
     * false if not
     */
    public DeploymentQueryResult canDeploy(Module module) {
        DeploymentQueryResult res = new DeploymentQueryResult();

        /*
         * Fetch all of the deployers and iterate through them. For each of
         * the parts they support, find whether the module contains such a
         * part. If so, ask the deployer whether it is ready to deploy
         */
        Map<String, ModulePart> parts = module.getParts();
        Iterator<ModuleDeployerSPI> it = getDeployers().iterator();
        while (it.hasNext() == true) {
            ModuleDeployerSPI deployer = it.next();
            String[] partTypes = deployer.getTypes();
            if (partTypes == null) {
                continue;
            }
            
            /* Loop through each part type and see if there is a module part */
            for (String partType : partTypes) {
                if (parts.containsKey(partType) == true) {
                    if (deployer.isDeployable(partType, module, parts.get(partType)) == false) {
                        res.addReason("Unable to deploy part " + partType +
                                      " with deployer " + deployer.getName() + 
                                      " class " + deployer.getClass().getName());
                        res.setResult(false);
                        return res;
                    }
                }
            }
        }

        res.setResult(true);
        return res;
    }
    
    /**
     * Returns true if all of the deployers can undeploy the parts of the module,
     * false if not
     */
    public DeploymentQueryResult canUndeploy(Module module) {
        DeploymentQueryResult res = new DeploymentQueryResult();

        /*
         * Fetch all of the deployers and iterate through them. For each of
         * the parts they support, find whether the module contains such a
         * part. If so, ask the deployer whether it is ready to deploy
         */
        Map<String, ModulePart> parts = module.getParts();
        Iterator<ModuleDeployerSPI> it = getDeployers().iterator();
        while (it.hasNext() == true) {
            ModuleDeployerSPI deployer = it.next();
            String[] partTypes = deployer.getTypes();
            if (partTypes == null) {
                continue;
            }
            
            /* Loop through each part type and see if there is a module part */
            for (String partType : partTypes) {
                if (parts.containsKey(partType) == true) {
                    logger.warning("For module " + module.getName() + " can " +
                            "undeploy part " + partType + " with deployer " +
                            deployer.getName() + " class " + deployer.getClass().getName());
                    if (deployer.isUndeployable(partType, module, parts.get(partType)) == false) {
                        res.addReason("Unable to undeploy part " + partType +
                                      " with deployer " + deployer.getName() +
                                      " class " + deployer.getClass().getName());
                        res.setResult(false);
                        return res;
                    }
                }
            }
        }

        res.setResult(true);
        return res;
    }

    /**
     * Calculate a valid deploy order for a group of modules. A valid
     * deploy order is one where for every module to be deployed,
     * all of that module's dependecies have already been deployed before
     * that module is deployed.  That way, at deploy time a module can
     * be sure that any resource it needs are already deployed properly.
     * <p>
     * The result of this method only defines that all dependencies will
     * be met before any module is listed. There are no other guarantees about
     * the ordering of modules relative to each other.  Modules will not
     * necessarily deploy in the same order from run to run.
     * <p>
     * Note this only compares module dependencies within the given map.
     * If a module depends on a module that is not in the given map,
     * it is assumed that the module has already been installed, so no special
     * care needs to be taken.
     * <p>
     * @param modules a map containing module names and modules to
     * deploy
     * @return a list of module names in dependency order
     * @throws IllegalArgumentException if a valid deployment order cannot
     * be determined due to circular dependencies
     */
    public static List<String> getDeploymentOrder(Map<String, Module> modules) {
        List<String> out = new ArrayList<String>();

        // make a copy of the module map, so we don't remove things from
        // the original
        Map<String, Module> deploy = new HashMap<String, Module>(modules);

        int changeCount;
        do {
            // reset the change count
            changeCount = 0;

            // go through each remaining module, to see if its dependencies
            // are all met
            Iterator<Map.Entry<String, Module>> i = deploy.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry<String, Module> e = i.next();
                String name = e.getKey();
                Module module = e.getValue();

                // scan all the dependencies to see if they are met
                boolean ready = true;
                for (ModuleInfo info : module.getRequires().getRequires()) {
                    // check if the dependency is still in the map
                    // of modules to install.  If it is, it means we cannot
                    // install this module yet.  If the dependency isn't in
                    // the map, it's already installed or already on the list,
                    // so we should be all set
                    if (deploy.containsKey(info.getName())) {
                        ready = false;
                        break;
                    }
                }

                // if all the dependecies are met, move this module out of
                // the modules map, and into the install list
                if (ready) {
                    out.add(name);
                    i.remove();
                    changeCount++;
                }
            }

        } while (changeCount > 0);

        // at this point, all modules remaining in the modules map were
        // examined, and all had unresolved dependencies.  If the map is
        // empty, that is fine, it just means we are done.

        // If the map is not empty, it means there were circular dependencies
        // we could not resolve.  In that case, throw an exception with the
        // remaining modules
        if (!deploy.isEmpty()) {
            String moduleList = "";
            for (String name : deploy.keySet()) {
                moduleList += name + ", ";
            }

            throw new IllegalArgumentException("Could not determine " +
                    "module installation order.  The following modules had" +
                    "unresolvable dependencies: " + moduleList);
        }

        return out;
    }

    /**
     * Calculate the set of deployers.  This checks to make sure no new
     * deployers have been deployed, so always returns an up-to-date set
     * of deployers.
     * @return the current set of deployers 
     */
    private Collection<ModuleDeployerSPI> getDeployers() {
        /*
         * Initialize the list of deployers. For each found, create a deployer
         * object and put into the set of deployer objects
         */
        Class[] clazzes = this.getClasses();
        for (Class clazz : clazzes) {
            // do nothing if we have already instantiated this class
            if (deployers.containsKey(clazz)) {
                continue;
            }

            // this is a new deployer.  Add an instance.
            try {
                ModuleDeployerSPI deployer = (ModuleDeployerSPI) clazz.newInstance();
                deployers.put(clazz, deployer);
            } catch (InstantiationException ex) {
                logger.log(Level.WARNING, "[DEPLOY] INSTANTIATE", ex);
            } catch (IllegalAccessException ex) {
                logger.log(Level.WARNING, "[DEPLOY] INSTANTIATE", ex);
            }
        }

        return deployers.values();
    }

    /**
     * Find and return all the classes that implement the ModuleDeployerSPI
     * inteface
     * 
     * @return
     */
    private Class[] getClasses() {
        Iterator<ModuleDeployerSPI> it = Service.providers(ModuleDeployerSPI.class);
        
        // use a linked hash set to preserve a static ordering
        Collection<Class> names = new LinkedHashSet<Class>();
        while (it.hasNext() == true) {
            names.add(it.next().getClass());
        }

        return names.toArray(new Class[]{} );
    }

    /**
     * The result of a deployment query about whether a module can be
     * deployed or undeployed, along with the reason it can or cannot
     * be deployed.
     */
    public static class DeploymentQueryResult {
        private boolean result;
        private final List<String> reasons =
                new ArrayList<String>();

        public boolean getResult() {
            return result;
        }

        public void setResult(boolean result) {
            this.result = result;
        }

        public void addReason(String reason) {
            this.reasons.add(reason);
        }

        public List<String> getReasons() {
            return reasons;
        }
    }
}
