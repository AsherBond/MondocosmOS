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

import java.io.File;
import java.io.FileWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.modules.Module;
import org.jdesktop.wonderland.common.modules.ModuleInfo;
import org.jdesktop.wonderland.common.modules.ModuleRequires;
import org.jdesktop.wonderland.modules.service.DeployManager.DeploymentQueryResult;
import org.jdesktop.wonderland.modules.service.ModuleOverwriteUtils.OverwriteQueryResult;
import org.jdesktop.wonderland.utils.RunUtil;


/**
 * The ModuleManager class manages the modules on the Wonderland server. It
 * enumerates the collection of installed modules on the system. During each
 * restart (or when a management 'reload' message is received), it scans the
 * module installation directory for modules to be installed or removed and
 * updates the system.
 * <p>
 * TBD - Description of module state in order here
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ModuleManager {

    /* The base module directory, and for the add/, pending/, and installed/ */
    private File root = null;
    
    /* Manages all of the deployers in the system */
    private DeployManager deployManager = null;
    
    /* Manages all of the modules that are pending for installation */
    private PendingManager pendingMananger = null;

    /* Manages all of the modules that are installed */
    private InstallManager installedMananger = null;
    
    /* Manages all of the modules to be uninstalled */
    private UninstallManager uninstallManager = null;
    
    /* The logger for the module manager */
    private static final Logger logger = Logger.getLogger(ModuleManager.class.getName());
    
    /**
     * The TaggedModule is an immutable class that represents a module to be
     * installed that is 'tagged' with a certain set of key-value pairs.
     */
    public static class TaggedModule {
        private File file;
        private Map<String, String> attributes;
        
        public TaggedModule(File file, Map<String, String> attributes) {
            this.file = file;
            this.attributes = attributes;
        }
        
        public File getFile() { return this.file; }
        public Map<String, String> getAttributes() { return this.attributes; }
    }
    
    /** Constructor */
    private ModuleManager() {
        // Put the "modules/" directory underneath the content directory
        File contentDir = RunUtil.getContentDir();
        root = new File(contentDir, "modules");
        logger.info("Wonderland modules base directory: " + root.getAbsolutePath());
        
        /* Set the base directory for the module system, create it if necessary */
        try {
            if (this.root.exists() == false) {
                ModuleManagerUtils.makeDirectory(this.root);
            }
        } catch (java.io.IOException excp) {
            logger.severe("[MODULES] Failed to create root " + this.root.getAbsolutePath());
            logger.severe("[MODULES] " + excp.toString());
            System.exit(1);
        }
        
        /* Create the managers to handle the whole module lifecycle */
        this.deployManager = new DeployManager();
        this.pendingMananger = new PendingManager(root);
        this.installedMananger = new InstallManager(root);
        this.uninstallManager = new UninstallManager(root);
    }
    
    /**
     * Singleton to hold instance of ModuleManager. This holder class is loaded
     * on the first execution of ModuleManager.getModuleManager().
     */
    private static class ModuleManagerHolder {
        private final static ModuleManager moduleManager = new ModuleManager();
    }
    
    /**
     * Returns a single instance of this class
     * <p>
     * @return Single instance of this class.
     */
    public static final ModuleManager getModuleManager() {
        return ModuleManagerHolder.moduleManager;
    }
    
    /**
     * Returns the error logger associated with this class.
     * 
     * @return The error logger
     */
    public static Logger getLogger() {
        return ModuleManager.logger;
    }
    
    /**
     * Redeploys all installed modules. This method does not check whether
     * modules can or cannot be deployed, it assumes this method is called when
     * the system is in an appropriate state for deployment
     * <p>
     * Before redeploying, this method installs all pending modules. No modules
     * are deployed until all the pending modules are installed.
     */
    public void redeployAll() {
        // remove all modules pending for removal
        uninstallAll();

        // install all modules pending for install
        installAll(false);

        // deploy all modules
        Map<String, Module> modules = this.installedMananger.getModules();

        // calculate a valid deployment order
        List<String> order = DeployManager.getDeploymentOrder(modules);
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("[MODULES] Calculated deployment order: " + order.toString());
        }

        logger.warning("[MODULES] deploying " + order.size() + " modules.");

        for (String name : order) {
            Module module = modules.get(name);

            try {
                this.deployManager.deploy(module);
            } catch (DeployerException excp) {
                /* Log a warning message and continue */
                logger.log(Level.WARNING, "[MODULE] REDEPLOY FAILED FOR " +
                        excp.getModule().getName() + " BY " +
                        excp.getDeployerName(), excp);
            }
        }
    }

    /**
     * Attempts to add a collection of modules to install. Takes the Files of
     * Jar files containing the modules.
     */
    public Collection<Module> addToInstall(Collection<File> moduleFiles) {
        /* Returns a collection of modules added */
        Collection<Module> added = new LinkedList<Module>();
        
        /* Iterate through each module URL and make it a pending module */
        Iterator<File> it = moduleFiles.iterator();
        while (it.hasNext() == true) {
            File file = it.next();
            Module module = this.pendingMananger.add(file);
            if (module == null) {
                logger.warning("[MODULES] INSTALL Failed to add " + file);
                continue;
            }
            added.add(module);
        }
        return added;
    }

    /**
     * Attempts to add a collection of modules to install. Takes a collection
     * of TaggedModules as an argument.
     * 
     * @param taggedModules
     * @return
     */
    public Collection<Module> addTaggedToInstall(Collection<TaggedModule> taggedModules) {
        /* Returns a collection of modules added */
        Collection<Module> added = new LinkedList<Module>();
        
        /*
         * Iterate through each module URL and make it a pending module
         */
        Iterator<TaggedModule> it = taggedModules.iterator();
        while (it.hasNext() == true) {
            /* Fetch the next tagged module in the collection */
            TaggedModule tm = it.next();
            File file = tm.getFile();
            Map<String, String> attributes = tm.getAttributes();
            
            /* Give the File to the manager of pending modules to add */
            Module module = this.pendingMananger.add(file);
            if (module == null) {
                logger.warning("[MODULES] INSTALL Failed to add " + file);
                continue;
            }
            
            /* See if we want to set attributes on the module and add if so */
            if (attributes != null) {
                ModuleInfo info = module.getInfo();
                info.putAttibutes(attributes);
                File infoFile = module.getFile(Module.MODULE_INFO);
                try {
                    FileWriter writer = new FileWriter(infoFile);
                    info.encode(writer);
                    writer.close();
                } catch (Exception ex) {
                    logger.log(Level.WARNING, "[MODULES] INSTALL Failed to update module.xml", ex);
                }
            }
            
            /* Add to the list of modules waiting to be installed */
            added.add(module);
        }
        return added;
    }
    
    /**
     * Attempts to remove a collection of modules. Returns a new collection of
     * all of the module names that were successfully removed and now pending
     * for un installation (during the next commit()). Note that a "removed"
     * module is still present in the system until the next commit(), during
     * which it is "uninstalled".
     * 
     * @param removedModules A collection of modules to remove
     * @return The names of the successfully removed modules
     */
    public Collection<String> addToUninstall(Collection<String> moduleNames) {
        /* Returns a collection of module names removed */
        Collection<String> removed = new LinkedList<String>();
        
        /*
         * Make a copy of the map of removed modules so that we do not modify
         * the argument. We first need to make sure that these modules are
         * actually installed. If not, log a message and remove it from the
         * map.
         */
        Iterator<String> it = moduleNames.iterator();
        Map<String, Module> installed = this.getInstalledModules();
        while (it.hasNext() == true) {
            String moduleName = it.next();
            Module module = installed.get(moduleName);
            logger.warning("ADD TO UNINSTALL " + moduleName + " " + module);
            if (module != null) {
                this.uninstallManager.add(moduleName, module.getInfo());
                removed.add(moduleName);
            }
        }
        return removed;
    }

    /**
     * Installs all of the pending modules. This checks for all of the modules
     * that can be installed given dependency checks and whether the server
     * is started and stopped.
     */
    public void installAll() {
        installAll(true);
    }

    /**
     * Installs all pending modules.  The value of the deploy boolean tells
     * whether or not to deploy modules after they are installed.  This is
     * used at startup so that all modules are deployed at the same time,
     * after they are installed.
     * @param deploy true to deploy modules, or false not to
     */
    protected void installAll(boolean deploy) {
        Map<String, Module> installed = new HashMap(this.installedMananger.getModules());
       
        /*
         * Make a copy of the list of modules to be installed. Check whether
         * they can be deployed and remove from the list if not.
         */
        Map<String, Module> pending = new HashMap(this.pendingMananger.getModules());
        Iterator<Map.Entry<String, Module>> it = pending.entrySet().iterator();
        while (it.hasNext() == true) {
            Map.Entry<String, Module> entry = it.next();
            Module module = entry.getValue();
            DeploymentQueryResult res = this.deployManager.canDeploy(module);
            if (res.getResult() == false) {
                StringBuffer message = new StringBuffer("[MODULES] Unable to install " +
                                                        module.getName() + ":\n");
                for (String reason : res.getReasons()) {
                    message.append(reason + "\n");
                }
                logger.warning(message.toString());
                it.remove();
            }
        }
        
        /*
         * Check to see that the module can be safely installed by making sure
         * that first its dependencies are met.
         */
        Map<Module, List<ModuleInfo>> failures =
                new LinkedHashMap<Module, List<ModuleInfo>>();
        Map<String, Module> passed = this.checkDependencies(pending, failures);

        /* Format a message to describe any failures */
        if (failures.isEmpty() == false) {
            StringBuffer failureMessage = new StringBuffer("[MODULES] Module dependency failures: \n");
            for (Map.Entry<Module, List<ModuleInfo>> e : failures.entrySet()) {
                failureMessage.append("Module " + e.getKey().getName() +
                                      " depends on ");
                for (ModuleInfo depend : e.getValue()) {
                    failureMessage.append(depend.getName() + " ");
                    failureMessage.append(" v. " + depend.getMajor() + ".");
                    failureMessage.append(depend.getMinor() + ".");
                    failureMessage.append(depend.getMini() + " ");
                }
                failureMessage.append("\n");
            }
            logger.warning(failureMessage.toString());
        }


        /*
         * Next check whether the module is overwriting an existing installed
         * module. Make sure that the new version of the module does not
         * violate the dependencies of other modules.
         */
        Iterator<Map.Entry<String, Module>> it1 = passed.entrySet().iterator();
        while (it1.hasNext() == true) {
            Map.Entry<String, Module> entry = it1.next();
            ModuleInfo info = entry.getValue().getInfo();
            
            OverwriteQueryResult res = ModuleOverwriteUtils.canOverwrite(info);
            if (res.getResult() == false) {
                StringBuffer message = new StringBuffer("[MODULES] Unable to replace module " +
                                                        info.getName() + ":\n");
                for (String reason : res.getReasons()) {
                    message.append(reason + "\n");
                }
                logger.warning(message.toString());

                it1.remove();
            }
        }
        
        /*
         * Go ahead and install the module and deploy.  Make sure to do
         * this in a valid deploy order
         */
        List<String> ordered = DeployManager.getDeploymentOrder(passed);
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("[MODULES] Calculated deployment order: " + ordered.toString());
        }
        
        for (String moduleName : ordered) {
            Module module = passed.get(moduleName);
            
            /*
             * Check to see if the module is already installed. If we have
             * reached here it means that we can safely uninstall the module.
             * But we first have to undeploy it.
             */
            if (installed.containsKey(moduleName) == true) {
                try {
                    this.deployManager.undeploy(module);
                } catch (DeployerException excp) {
                    logger.log(Level.WARNING, "[MODULES] INSTALL ALL Unable to undeploy " + moduleName, excp);
                }
            }
            
            /*
             * Install the module into the installed/ directory. Fetch the
             * newly installed module (which differs from the pending Module)
             */
            File file = module.getFile();
            module = this.installedMananger.add(moduleName,file);
            if (module == null) {
                logger.warning("[MODULES] INSTALL ALL Failed on " + moduleName);
                continue;
            }
            
            /* Remove from the pending/ directory */
            this.pendingMananger.remove(moduleName);
            
            /* Deploy the module */
            if (deploy) {
                try {
                    this.deployManager.deploy(module);
                } catch (DeployerException excp) {
                    logger.log(Level.WARNING, "[MODULES] INSTALL ALL Unable to deploy " + moduleName, excp);
                }
            }
        }
    }
 
    /**
     * Uninstalls all of the modules waiting to be uninstalled if possible.
     * Checks for the modules that can be uninstalled and removes them.
     */
    public void uninstallAll() {
        /*
         * Make a copy of the list of modules to be uninstalled. Check whether
         * they require the server to be stopped.
         */
        Map<String, ModuleInfo> uninstall = new HashMap(this.uninstallManager.getModules());
        Iterator<Map.Entry<String, ModuleInfo>> it = uninstall.entrySet().iterator();
        Map<String, Module> installed = this.installedMananger.getModules();
        while (it.hasNext() == true) {
            Map.Entry<String, ModuleInfo> entry = it.next();
            String moduleName = entry.getKey();
            Module module = installed.get(moduleName);

            // OWL issue #165: if the module doesn't exist, remove it from
            // the uninstall list and move on. The reference to the file
            // should be removed the next time the uninstall list is
            // written
            if (module == null) {
                logger.warning("Module " + moduleName + " no longer installed. "
                        + "Skipping.");
                
                it.remove();
                continue;
            }

            DeploymentQueryResult res = this.deployManager.canUndeploy(module);
            if (res.getResult() == false) {
                StringBuffer message = new StringBuffer("[MODULES] Unable to uninstall " +
                                                        module.getName() + ":\n");
                for (String reason : res.getReasons()) {
                    message.append(reason + "\n");
                }
                logger.warning(message.toString());
                it.remove();
            }
        }
        
        /*
         * Check to see that the module can be safely removed by making sure that
         * first it is no longer required
         */
        Map<String, Set<ModuleInfo>> failures = new LinkedHashMap<String, Set<ModuleInfo>>();
        Map<String, ModuleInfo> checked = this.checkRequired(uninstall, failures);

        /* Format a message to describe any failures */
        if (failures.isEmpty() == false) {
            StringBuffer failureMessage = new StringBuffer("[MODULES] Module dependency failures: \n");
            for (Map.Entry<String, Set<ModuleInfo>> e : failures.entrySet()) {
                failureMessage.append("Modules that depend on " + e.getKey() + " ");
                for (ModuleInfo depend : e.getValue()) {
                    failureMessage.append(depend.getName() + " ");
                }
                failureMessage.append("\n");
            }
            logger.warning(failureMessage.toString());
        }

        /*
         * For all of the modules that can be uninstalled, undeploy them and
         * uninstall them
         */
        Iterator<Map.Entry<String, ModuleInfo>> it2 = checked.entrySet().iterator();
        while (it2.hasNext() == true) {
            Map.Entry<String, ModuleInfo> entry = it2.next();
            String moduleName = entry.getKey();
            
            /* Undeploy the module, log the error if it happens (should be rare) */
            Module module = installed.get(moduleName);
            try {
                this.deployManager.undeploy(module);
            } catch (DeployerException excp) {
                logger.log(Level.WARNING, "[MODULES] UNINSTALL Failed to Undeploy", excp);
            }
            this.installedMananger.remove(moduleName);
            this.uninstallManager.remove(moduleName);
        }

        // OWL issue #165: make sure the uninstall list gets written here,
        // to guarantee that any failed uninstalls will be cleaned up
        this.uninstallManager.write();
    }
    
    /**
     * Returns a map of module names and objects currently installed in the
     * system. If no modules are installed, this method returns an empty map.
     * 
     * @return A map of unique installed module names and their Module objects
     */
    public Map<String, Module> getInstalledModules() {
        return new HashMap(this.installedMananger.getModules());
    }

    /**
     * Returns a map of module names and objects currently pending installation
     * in the system. If no modules are pending installation, this method
     * returns an empty map.
     * 
     * @return A map of unique pending module names and their Module objects
     */
    public Map<String, Module> getPendingModules() {
        return new HashMap(this.pendingMananger.getModules());
    }
    
    /**
     * Returns a map of moduel names and info objects currently waiting for
     * uninstall in the system. If no modules are pending uninstall, this
     * method returns an empty map.
     * 
     * @return A map of unique uninstall module names and their info objects
     */
    public Map<String, ModuleInfo> getUninstallModuleInfos() {
        return new HashMap(this.uninstallManager.getModules());
    }
    
    /**
     * Returns a map of installed modules that posses a property with a certain
     * key
     */
    public Map<String, Module> getInstalledModulesByKey(String key) {
        Map<String, Module> keyed = new HashMap();
        Map<String, Module> installed = this.installedMananger.getModules();
        Iterator<Map.Entry<String, Module>> it = installed.entrySet().iterator();
        while (it.hasNext() == true) {
            Map.Entry<String, Module> entry = it.next();
            String moduleName = entry.getKey();
            Module module = entry.getValue();
            if (module.getInfo().getAttribute(key) != null) {
                keyed.put(moduleName, module);
            }
        }
        return keyed;
    }
    
    /**
     * Checks whether a collection of modules asked to be removed are still
     * required and returns a collection of modules that are no longer required.
     * This method iteratres until it can find no more modules that are no
     * longer required.
     * 
     * @param removedModules A collection of module infos to check
     * @param failures if non-null, this list will be filled in with
     * a list of modules for which the dependency check fails and the
     * unsatisfied dependencies
     * @return A collection of modules that are no longer required
     */
    private Map<String, ModuleInfo> checkRequired(Map<String, ModuleInfo> removedModules,
                                                  Map<String, Set<ModuleInfo>> failures)
    {
        Map<String, ModuleInfo> satisfied = new HashMap<String, ModuleInfo>();
        
        /*
         * Create a map of ModuleRequireCheck classes for each of the modules
         * we wish to check for removal
         */
        HashMap<String, ModuleRequiredCheck> required = new HashMap();
        Iterator<Map.Entry<String, ModuleInfo>> it = removedModules.entrySet().iterator();
        while (it.hasNext() == true) {
            Map.Entry<String, ModuleInfo> entry = it.next();
            ModuleInfo info = entry.getValue();
            required.put(info.getName(), new ModuleRequiredCheck(info));
        }
        
        /*
         * Fetch a map of installed modules. Loop through each and add as
         * requirements to the modules if they are being asked to be removed.
         */
        Map<String, Module> present = this.getInstalledModules();
        Iterator<Map.Entry<String, Module>> it2 = present.entrySet().iterator();
        while (it2.hasNext() == true) {
            /*
             * Fetch the map of modules that this module requires
             */
            Map.Entry<String, Module> entry = it2.next();
            String moduleName = entry.getKey();
            Module module = entry.getValue();
            ModuleInfo info = module.getInfo();
            ModuleRequires requirements = module.getRequires();
            
            /*
             * Loop through each of the requirements of the module and add it
             * to the ModuleRequiredCheck, if it exists. (If it does exist, it
             * means we are checking to see if the module is still required and
             * we want to flag it with this module).
             */
            for (ModuleInfo infoRequires : requirements.getRequires()) {
                ModuleRequiredCheck check = required.get(infoRequires.getName());
                if (check != null) {
                    check.addRequiresModuleInfo(info);
                }
            }
        }
        
        /*
         * Next we need to loop through and see which modules are no longer
         * required. When a module is no longer required, we should add it to
         * the map of satisfied modules and also remove it from all of the
         * other module requirement checks. We continue checking until we can
         * find no more additional modules that are no longer requires.
         */
        boolean found = true;
        while (found == true) {
            found = false;
            Iterator<Map.Entry<String, ModuleRequiredCheck>> it4 = required.entrySet().iterator();
            while (it4.hasNext() == true) {
                Map.Entry<String, ModuleRequiredCheck> entry = it4.next();
                
                /*
                 * If the module is no longer required, then...
                 */
                if (entry.getValue().isRequired() == false) {
                    /* Add it to the 'satified map' */
                    ModuleInfo moduleInfo = entry.getValue().getCheckedModuleInfo();
                    satisfied.put(moduleInfo.getName(), moduleInfo);
                    
                    /* Remove it from the dependency map using the iterator */
                    it4.remove();
                    
                    /*
                     * Iterator over the remaining required check objects. This
                     * part assumes the following works properly in Java: nested
                     * iterations where we just removed an entry from the original
                     * map using the Iterator.remove() method
                     */
                    Iterator<Map.Entry<String, ModuleRequiredCheck>> it5 = required.entrySet().iterator();
                    while (it5.hasNext() == true) {
                        Map.Entry<String, ModuleRequiredCheck> check = it5.next();
                        check.getValue().checkRequired(moduleInfo);
                    }
                    
                    /* Indicate we have found more satified modules */
                    found = true;
                }
            }
            
            /* If there are no more modules left, then we are done */
            if (required.isEmpty() == true) {
                break;
            }
        }

        if (failures != null) {
            for (Map.Entry<String, ModuleRequiredCheck> e : required.entrySet()) {
                failures.put(e.getKey(), e.getValue().getRequires());
            }
        }

        return satisfied;
    }
    
    /**
     * Checks the dependencies for a collection of modules and returns a
     * collection of modules whose requirements have been met. This method
     * iterates until it can satify the requirements of added modules no longer. 
     *
     * @param modules A collection of modules to check dependencies
     * @param failures if non-null, this map will be filled in with
     * a list of modules for which the dependency check fails and the
     * unsatisfied dependencies
     * @return A collection of moduels with satified dependencies
     */
    private Map<String, Module> checkDependencies(Map<String, Module> modules,
                                        Map<Module, List<ModuleInfo>> failures)
    {
        Map<String, Module> satisfied = new HashMap<String, Module>();
        
        /*
         * Create a map of ModuleDependencyCheck classes for each of the 
         * modules we wish to add.
         */
        HashMap<Module, ModuleDependencyCheck> dependencies = new HashMap();
        Iterator<Map.Entry<String, Module>> it = modules.entrySet().iterator();
        while (it.hasNext() == true) {
            Map.Entry<String, Module> entry = it.next();
            Module module = entry.getValue();
            dependencies.put(module, new ModuleDependencyCheck(module));
        }
        
        /*
         * Fetch a map of installed. Loop through each and see if any of the
         * added modules depends upon the installed module. If so, mark the
         * dependency as met.
         */
        Map<String, Module> present = this.getInstalledModules();
        Iterator<Map.Entry<String, Module>> it2 = present.entrySet().iterator();
        while (it2.hasNext() == true) {
            ModuleInfo potentialDependency = it2.next().getValue().getInfo();
            Iterator<Map.Entry<Module, ModuleDependencyCheck>> it3 = dependencies.entrySet().iterator();
            while (it3.hasNext() == true) {
                ModuleDependencyCheck check = it3.next().getValue();
                check.checkDependency(potentialDependency);
            }
        }
        
        /*
         * Next we need to loop through and see which modules have had their
         * requirements met. When a module has all of its requirements met, then
         * we should add it to the map of satified modules and also remove it
         * from all of the other module dependency checks. We continue checking
         * until we can find no more additional modules requirements met.
         */
        boolean found = true;
        while (found == true) {
            found = false;
            Iterator<Map.Entry<Module, ModuleDependencyCheck>> it4 = dependencies.entrySet().iterator();
            while (it4.hasNext() == true) {
                Map.Entry<Module, ModuleDependencyCheck> entry = it4.next();
                Module module = entry.getKey();
                String moduleName = module.getName();
                ModuleDependencyCheck mdc = entry.getValue();
                
                /*
                 * If the module has all of its requirements met and it is not
                 * already in the map of modules who have had their requirements
                 * met (meaning, this is the first time we see it has had its
                 * requirements met), then...
                 */
                if (mdc.isDependenciesMet() == true && satisfied.containsKey(moduleName) == false) {
                    /* Add it to the 'satified map' */
                    satisfied.put(moduleName, module);
                    
                    /* Remove it from the dependency map using the iterator */
                    it4.remove();
                    
                    /*
                     * Iterator over the remaining dependency check objects. This
                     * part assumes the following works properly in Java: nested
                     * iterations where we just removed an entry from the original
                     * map using the Iterator.remove() method
                     */
                    ModuleInfo moduleInfo = module.getInfo();
                    Iterator<Map.Entry<Module, ModuleDependencyCheck>> it5 = dependencies.entrySet().iterator();
                    while (it5.hasNext() == true) {
                        Map.Entry<Module, ModuleDependencyCheck> check = it5.next();
                        check.getValue().checkDependency(moduleInfo);
                    }
                    
                    /* Indicate we have found more satified modules */
                    found = true;
                }
            }
            
            /* If there are no more modules left, then we are done */
            if (dependencies.isEmpty() == true) {
                break;
            }
        }

        /* Populate the list of failed modules */
        if (failures != null) {
            for (Map.Entry<Module, ModuleDependencyCheck> e : dependencies.entrySet()) {
                failures.put(e.getKey(), e.getValue().getUnmetDependencies());
            }
        }
        
        return satisfied;
    }
}
