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
package org.jdesktop.wonderland.modules.ant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.bind.JAXBException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.types.spi.Service;
import org.apache.tools.ant.types.ZipFileSet;
import org.apache.tools.zip.ZipOutputStream;
import org.jdesktop.wonderland.modules.Module;
import org.jdesktop.wonderland.common.modules.ModuleInfo;
import org.jdesktop.wonderland.common.modules.ModuleRequires;

/**
 * @author jon
 */
public class ModuleTask extends Jar {
    
    // attributed
    private String name;
    private int majorVersion = ModuleInfo.VERSION_UNSET;
    private int minorVersion = ModuleInfo.VERSION_UNSET;
    private int miniVersion = ModuleInfo.VERSION_UNSET;
    private String moduleDescription;
    
    private List<Requires> requires = new ArrayList<Requires>();
    private List<ModulePart> parts = new ArrayList<ModulePart>();
   
    private File buildDir;
    private boolean overwrite = false;
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }
    
    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }
    
    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }

    public void setMiniVersion(int miniVersion) {
        this.miniVersion = miniVersion;
    }

    public void setModuleDescription(String moduleDescription) {
        this.moduleDescription = moduleDescription;
    }
    
    public void setBuildDir(File buildDir) {
        this.buildDir = buildDir;
    }
    
    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }
    
    public Requires createRequires() {
        Requires r = new Requires();
        requires.add(r);
        return r;
    }
    
    public void addConfiguredPart(ModulePart part) {
        part.validate();
        
        // add the fileset portion of the part as a normal fileset
        if (part.getDir() != null) {
            part.setPrefixInternal(part.getName());
            super.addFileset(part);
        }
        
        // add to our list to process any additional jars at execute()
        // time
        parts.add(part);
    }
    
    public void addConfiguredArt(ArtPart art) {
        addConfiguredPart(art);
    }

    public void addConfiguredAudio(AudioPart audio) {
        addConfiguredPart(audio);
    }
        
    public void addConfiguredHelp(HelpPart help) {
        addConfiguredPart(help);
    }
        
    public void addConfiguredWfs(WFSPart wfs) {
        addConfiguredPart(wfs);
    }
    
    public void addConfiguredServer(ServerPart server) {
        addConfiguredPart(server);
    }
    
    public void addConfiguredCommon(CommonPart common) {
        addConfiguredPart(common);
    }
    
    public void addConfiguredClient(ClientPart client) {
        addConfiguredPart(client);
    }
    
    @Override
    public void execute() throws BuildException {
        // make sure there are no obvious errors before we write anything
        validate();
        
        // remember the context classloader
        ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
        
        // now write the relevant xml files, by creating temp files
        // and adding those temp files to parent .jar
        try {
            
            // workaround for JAXB issue.  The JAXB ContextFinder uses the
            // context classloader to load the correct JAXBContext instance.
            // Make sure the context classloader is the one that loaded
            // this task (which has the JAXB classpath).  Otherwsise,
            // the default ant classloader will be used, which doesn't
            // have the JAXB classes.  Also make sure to set the
            // context classloader back after this try block.            
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            
            // first write the module info
            writeModuleInfo();
            
            // next write required files
            writeRequires();
            
            // write parts
            for (ModulePart p : parts) {
                writePart(p);
            }
        } catch (IOException ioe) {
            throw new BuildException(ioe);
        } catch (JAXBException je) {
            throw new BuildException(je);
        } finally {
            // reset the classloader
            Thread.currentThread().setContextClassLoader(contextCL);
        }
        
        // TODO calculate checksums?        
        super.execute();
    }
    
    private void writeModuleInfo() throws IOException, JAXBException {
        ModuleInfo mi = new ModuleInfo(name, majorVersion, minorVersion,
                miniVersion, moduleDescription);
        
        File moduleInfoFile;
        if (buildDir == null) {
            moduleInfoFile = File.createTempFile("moduleInfo", "xml");
            moduleInfoFile.deleteOnExit();
        } else {
            moduleInfoFile = new File(buildDir, "moduleInfo.xml");
        }
        
        if (overwrite || !compareModuleInfo(mi, moduleInfoFile)) {
            log("Rewriting moduleInfo file", Project.MSG_VERBOSE);
            FileWriter writer = new FileWriter(moduleInfoFile);
            mi.encode(writer);
            writer.close();
        }
        
        ZipFileSet zfs = new ZipFileSet();
        zfs.setFile(moduleInfoFile);
        zfs.setFullpath(Module.MODULE_INFO);
        
        super.addFileset(zfs);
    }
    
    /**
     * Return if the given new ModuleInfo object is the same as the 
     * module info contained in the file oldMIFile.  Returns false if
     * the old file doesn't exist.  Note this relies on more than just the 
     * equals() method of the ModuleInfo object -- it also compares the 
     * description.
     * 
     * @param newMI the new module info object
     * @param oldMIFile the file containing the old module info object
     * @return true if the files are the same, or false if they are different
     * @throws IOException if there is a problem reading the file
     */
    private boolean compareModuleInfo(ModuleInfo newMI, File oldMIFile) 
        throws IOException
    {
        log("Comparing module info " + oldMIFile.getCanonicalPath() + 
            " exists: " + oldMIFile.exists(), Project.MSG_VERBOSE);
        
        if (!oldMIFile.exists()) {
            return false;
        }

        FileReader reader = null;
        try {
            reader = new FileReader(oldMIFile);
            ModuleInfo oldMI = ModuleInfo.decode(reader);
            
            log("New desc:|" + newMI.getDescription() + "|Old desc:|" + oldMI.getDescription() + "|", Project.MSG_VERBOSE);
            
            // ModuleInfo.equals() doesn't check the description field, 
            // but we want to re-write the file if the description has
            // changed.
            boolean descChanged = (newMI.getDescription() == null) ?
                (oldMI.getDescription() != null) :
                (!newMI.getDescription().equals(oldMI.getDescription()));
            
            log("ModuleInfo: descChanged: " + descChanged + " " +
                "equals: " + newMI.equals(oldMI), Project.MSG_VERBOSE);
            
            return (!descChanged && newMI.equals(oldMI));
        } catch (JAXBException je) {
            // problem reading file
        } finally {
            reader.close();
        }
        
        return false;
    }
    
    private void writeRequires() throws IOException, JAXBException {
        Set<ModuleInfo> mis = new HashSet<ModuleInfo>();
        for (Requires r : requires) {
            mis.add(new ModuleInfo(r.name, r.majorVersion, r.minorVersion,
                    r.miniVersion));
        }
        
        ModuleRequires mr = new ModuleRequires(mis.toArray(new ModuleInfo[0]));
        
        File moduleRequiresFile;
        if (buildDir == null) {
            moduleRequiresFile = File.createTempFile("moduleRequires", ".xml");
            moduleRequiresFile.deleteOnExit();
        } else {
            moduleRequiresFile = new File(buildDir, "moduleRequires.xml");
        }
        
        if (overwrite || !compareModuleRequires(mr, moduleRequiresFile)) {
            log("Rewriting moduleRequires file", Project.MSG_VERBOSE);
            FileOutputStream fos = new FileOutputStream(moduleRequiresFile);
            mr.encode(fos);
            fos.close();
        }
        
        ZipFileSet zfs = new ZipFileSet();
        zfs.setFile(moduleRequiresFile);
        zfs.setFullpath(Module.MODULE_REQUIRES);
        
        super.addFileset(zfs);
    }
    
    /**
     * Return if the given new ModuleRequires object is the same as the 
     * module requires contained in the file oldMRFile.  Returns false if
     * the old file doesn't exist. This task compares the ModuleInfo[] objects
     * in each requires file.
     * 
     * @param newMR the new module requires object
     * @param oldMRFile the file containing the old module requires object
     * @return true if the files are the same, or false if they are different
     * @throws IOException if there is a problem reading the file
     */
    private boolean compareModuleRequires(ModuleRequires newMR, File oldMRFile) 
        throws IOException
    {
        if (!oldMRFile.exists()) {
            return false;
        }

        FileReader reader = null;
        try {
            reader = new FileReader(oldMRFile);
            ModuleRequires oldMR = ModuleRequires.decode(reader);
           
            return Arrays.deepEquals(newMR.getRequires(), oldMR.getRequires());
        } catch (JAXBException je) {
            // problem reading file
        } finally {
           reader.close();
        }
        
        return false;
    }
    
    private void writePart(ModulePart p) throws IOException {
        for (ModuleJar jar : p.jars) {
            writeModuleJar(p.getName(), jar);
        }
    }
    
    private void writeModuleJar(String partName, ModuleJar jar) 
        throws IOException
    {
        String jarname = jar.getName();
        if (jarname.indexOf(".") == -1) {
            jarname += ".jar";
        }
        
        File jarFile;
        if (buildDir == null) {
            jarFile = File.createTempFile(jar.getName(), ".jar");
            jarFile.delete();
            jarFile.deleteOnExit();
        } else {
            File jarBuildDir = new File(buildDir, partName);            
            jarBuildDir.mkdirs();
            
            jarFile = new File(jarBuildDir, jarname);
        }
        
        jar.setInternalDestFile(jarFile);
        jar.execute();
            
        ZipFileSet zfs = new ZipFileSet();
        zfs.setFile(jarFile);
        zfs.setFullpath(partName + "/" + jarname);
        
        super.addFileset(zfs);
    }
    
    /**
     * Once this task is completely assembled, this method can be used
     * to check for any errors.  If it returns normally, there are no
     * errors.
     * @throws BuildException if there are errors with this tasks
     */
    private void validate() throws BuildException {
        // make sure we have a name and version
        if (name == null) {
            throw new BuildException("Name is required.");
        }
        
        if (majorVersion == ModuleInfo.VERSION_UNSET) {
            throw new BuildException("Major version is required.");
        }
        
        // force the minor version to be 0 if it is unset
        if (minorVersion == ModuleInfo.VERSION_UNSET) {
            minorVersion = 0;
        }

        // force the mini version to be 0 if it is unset
        if (miniVersion == ModuleInfo.VERSION_UNSET) {
            miniVersion = 0;
        }
        
        // check any included requirements
        for (Requires r : requires) {
            r.validate();
        }
    }
    
    public static class Requires {
        private String name;
        private int majorVersion = ModuleInfo.VERSION_UNSET;
        private int minorVersion = ModuleInfo.VERSION_UNSET;
        private int miniVersion = ModuleInfo.VERSION_UNSET;
        
        public void setName(String name) {
            this.name = name;
        }
    
        public void setVersion(int majorVersion) {
            this.majorVersion = majorVersion;
        }

        public void setMajorVersion(int majorVersion) {
            this.majorVersion = majorVersion;
        }

        public void setMinorVersion(int minorVersion) {
            this.minorVersion = minorVersion;
        }

        public void setMiniVersion(int miniVersion) {
            this.miniVersion = miniVersion;
        }

        private void validate() throws BuildException {
            if (name == null) {
                throw new BuildException("Requires without name.");
            }
            
            if (majorVersion == ModuleInfo.VERSION_UNSET) {
                throw new BuildException("Requires without major version.");
            }
        }
    }
    
    public static class ModulePart extends ZipFileSet {
        private String name;
        private List<ModuleJar> jars = new ArrayList<ModuleJar>();
        
        public ModulePart() {
        }
        
        protected ModulePart(String name) {
            this.name = name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        public void addConfiguredJar(ModuleJar jar) {
            // make sure it is OK
            jar.validate();
            
            // add it to our list
            jars.add(jar);
        }

        @Override
        public void setPrefix(String prefix) {
            throw new BuildException("Cannot set prefix for module part");
        }
        
        protected void setPrefixInternal(String prefix) {
            super.setPrefix(prefix);
        }
        
        public void validate() {
            if (name == null) {
                throw new BuildException("Module parts requires name");
            }
        }
    }
    
    public static class ArtPart extends ModulePart {
        public ArtPart() {
            super ("art");
        }
    }
 
    public static class AudioPart extends ModulePart {
        public AudioPart() {
            super ("audio");
        }
    }
        
    public static class WFSPart extends ModulePart {
        public WFSPart() {
            super ("wfs");
        }
    }
 
    public static class HelpPart extends ModulePart {
        public HelpPart() {
            super ("help");
        }
    }
        
    public static class ServerPart extends ModulePart {
        public ServerPart() {
            super ("server");
        }
        
        public void addConfiguredServerJar(ServerJar serverJar) {
            addConfiguredJar(serverJar);
        }
    }
    
    public static class CommonPart extends ModulePart {
        public CommonPart() {
            super ("common");
        }
        
        public void addConfiguredCommonJar(ModuleJar commonJar) {
            addConfiguredJar(commonJar);
        }
    }
    
    public static class ClientPart extends ModulePart {
        public ClientPart() {
            super ("client");
        }
        
        public void addConfiguredClientJar(ClientJar clientJar) {
            addConfiguredJar(clientJar);
        }
    }
    
    public static class ModuleJar extends Jar {
        private String name;
        private List<Service> services = new ArrayList<Service>();
        
        public ModuleJar() {
        }
        
        protected ModuleJar(String name) {
            this.name = name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        @Override
        public void setDestFile(File file) {
            throw new BuildException("Cannot change destination file of " +
                                     " ModuleJar.");
        }
        
        void setInternalDestFile(File file) {
            super.setDestFile(file);
        }
        
        /**
         * A nested SPI service element.  Workaround for ant 1.7 issue
         * writing services to the wrong directory.
         * @param service the nested element.
         */
        @Override
        public void addConfiguredService(Service service) {
            // Check if the service is configured correctly
            service.check();
            services.add(service);
        }

        /**
         * Initialize the zip output stream.
         * @param zOut the zip output stream
         * @throws IOException on I/O errors
         * @throws BuildException on other errors
         */
        @Override
        protected void initZipOutputStream(ZipOutputStream zOut)
                throws IOException, BuildException 
        {
            super.initZipOutputStream(zOut);
            
            if (!skipWriting) {
                writeServices(zOut);
            }
        }

        
        /**
         * Write SPI Information to JAR. Workaround for ant 1.7 issue
         * writing service to the wrong directory.
         */
        private void writeServices(ZipOutputStream zOut) throws IOException {
            Iterator serviceIterator;
            Service service;

            serviceIterator = services.iterator();
            while (serviceIterator.hasNext()) {
                service = (Service) serviceIterator.next();
                //stolen from writeManifest
                super.zipFile(service.getAsStream(), zOut,
                        "META-INF/services/" + service.getType(),
                        System.currentTimeMillis(), null,
                        ZipFileSet.DEFAULT_FILE_MODE);
            }
        }
        
        public void validate() {
            if (name == null) {
                throw new BuildException("ModuleJar requires a name");
            }
        }
    }
    
    public static class ServerJar extends ModuleJar {
        public void addConfiguredServerPlugin(ServerPlugin serverPlugin) {
            addConfiguredService(serverPlugin);
        }
        
        public void addConfiguredCellSetup(CellSetup cellSetup) {
            addConfiguredService(cellSetup);
        }
        
        public void addConfiguredCellExtensionType(CellExtensionType cellExtensionType) {
            addConfiguredService(cellExtensionType);
        }
    }
    
    public static class ServerPlugin extends Service {
        public ServerPlugin() {
            setType("org.jdesktop.wonderland.server.ServerPlugin");
        }
    }
    
    public static class CellSetup extends Service {
        public CellSetup() {
            setType("org.jdesktop.wonderland.common.cell.state.spi.CellServerStateSPI");
        }
    }
 
    public static class CellExtensionType extends Service {
        public CellExtensionType() {
            setType("org.jdesktop.wonderland.common.cell.setup.spi.CellExtensionTypeSPI");
        }
    }
        
    public static class ClientJar extends ModuleJar {
        public void addConfiguredClientPlugin(ClientPlugin clientPlugin) {
            addConfiguredService(clientPlugin);
        }

        public void addConfiguredCellFactory(CellFactory cellFactory) {
            addConfiguredService(cellFactory);
        }

        public void addConfiguredCellProperties(CellProperties cellProperties) {
            addConfiguredService(cellProperties);
        }

        public void addConfiguredComponentProperties(ComponentProperties componentProperties) {
            addConfiguredService(componentProperties);
        }
    }
    
    public static class ClientPlugin extends Service {
        public ClientPlugin() {
            setType("org.jdesktop.wonderland.client.ClientPlugin");
        }
    }

    public static class CellFactory extends Service {
        public CellFactory() {
            setType("org.jdesktop.wonderland.client.cell.registry.spi.CellFactorySPI");
        }
    }

    public static class CellProperties extends Service {
        public CellProperties() {
            setType("org.jdesktop.wonderland.client.cell.properties.spi.CellPropertiesSPI");
        }
    }

    public static class ComponentProperties extends Service {
        public ComponentProperties() {
            setType("org.jdesktop.wonderland.client.cell.properties.spi.CellComponentPropertiesSPI");
        }
    }
}
