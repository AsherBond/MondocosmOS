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
package org.jdesktop.wonderland.common.modules.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import javax.xml.bind.JAXBException;
import org.jdesktop.wonderland.common.FileUtils;
import org.jdesktop.wonderland.common.modules.ModuleInfo;
import org.jdesktop.wonderland.common.modules.ModuleRepository;
import org.jdesktop.wonderland.common.modules.ModuleRequires;

/**
 * Write the elements of a module out to a jar file programatically.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ModuleJarWriter {

    private ModuleInfo moduleInfo = null;
    private ModuleRequires moduleRequires = null;
    private ModuleRepository moduleRepository = null;
    private Map<String, File> moduleArt = null;
    private Set<File> directories = null;

    public ModuleJarWriter() {
    }

    public ModuleInfo getModuleInfo() {
        return moduleInfo;
    }

    public void setModuleInfo(ModuleInfo moduleInfo) {
        this.moduleInfo = moduleInfo;
    }

    public ModuleRepository getModuleRepository() {
        return moduleRepository;
    }

    public void setModuleRepository(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    public ModuleRequires getModuleRequires() {
        return moduleRequires;
    }

    public void setModuleRequires(ModuleRequires moduleRequires) {
        this.moduleRequires = moduleRequires;
    }
    
    public void addArtFile(String path, File file) {
        if (moduleArt == null) {
            moduleArt = new HashMap<String, File>();
        }

        moduleArt.put(fixPath(path), file);
    }
    
    public Map<String, File> getArtFiles() {
        return moduleArt;
    }

    /**
     * Add a directory and all it's children (recursively) to the module
     * 
     * @param dir
     */
    public void addDirectory(File dir) {
        if (directories==null)
            directories = new HashSet();
        directories.add(dir);
    }

    /**
     * Remove the directory and all it's children
     *
     * @param dir
     */
    public void removeDirectory(File dir) {
        directories.remove(dir);
    }
    
    public void removeArtFile(String path) {
        if (moduleArt != null) {
            moduleArt.remove(fixPath(path));
        }
    }
    
    public void writeToJar(File file) throws IOException, JAXBException {
        // A set of paths already written to the jar
        Set<String> writtenPaths = new HashSet<String>();
        
        // Open the output stream as a jar output stream. Write out the XML
        // information files if they exist.
        JarOutputStream jos = new JarOutputStream(new FileOutputStream(file));
        writeManifest(jos);
        
        if (moduleInfo != null) {
            JarEntry entry = new JarEntry("module.xml");
            jos.putNextEntry(entry);
            moduleInfo.encode(jos);
        }
        
        if (moduleRequires != null) {
            JarEntry entry = new JarEntry("requires.xml");
            jos.putNextEntry(entry);
            moduleRequires.encode(jos);
        }
        
        if (moduleRepository != null) {
            JarEntry entry = new JarEntry("repository.xml");
            jos.putNextEntry(entry);
            moduleRepository.encode(jos);
        }
        
        // Write out an entry for the art/ directory
        if (moduleArt != null) {
            Iterator<Map.Entry<String, File>> it = moduleArt.entrySet().iterator();
            while (it.hasNext() == true) {
                Map.Entry<String, File> mapEntry = it.next();
                String path = "art/" + mapEntry.getKey();
                writeDirectory(jos, path, writtenPaths);
                JarEntry entry = new JarEntry(fixPath(path));
                jos.putNextEntry(entry);
                FileUtils.copyFile(new FileInputStream(mapEntry.getValue()), jos);
            }
        }

        if (directories!=null) {
            for(File dir : directories) {
                writeDirectoryTree(jos, dir, dir.getParent().toString().length()+1, writtenPaths);
            }
        }

        jos.close();
    }

    private void writeDirectoryTree(JarOutputStream jos, File dir, int parentTrimStart, Set<String> written) throws IOException {
        String dirName = dir.getAbsolutePath().substring(parentTrimStart, dir.getAbsolutePath().length());
        JarEntry entry = new JarEntry(fixPath(dirName + "/"));
        jos.putNextEntry(entry);

        File[] files = dir.listFiles();
        if (files==null)
            return;
        for(File f : files) {

            if (f.isDirectory()) {
                writeDirectoryTree(jos, f, parentTrimStart, written);
            } else {
                String path = f.getAbsolutePath().substring(parentTrimStart, f.getAbsolutePath().length()); 
                JarEntry fileEntry = new JarEntry(fixPath(path));                
                jos.putNextEntry(fileEntry);
                FileUtils.copyFile(new FileInputStream(f), jos);
            }
        }
    }
    
    private void writeDirectory(JarOutputStream jos, String path, Set<String> written) throws IOException {
        // Creates directory entries for each subpath of the given path,
        // excluding the final token (assumed to be the file name)
        String tokens[] = fixPath(path).split("/");
        if (tokens == null) {
            return;
        }
        
        // Loop through and create each directory in turn
        String directory = "";
        for (int i = 0; i < tokens.length - 1; i++) {
            directory = directory + tokens[i] + "/";
            if (written.contains(directory) == false) {
                JarEntry entry = new JarEntry(directory);
                jos.putNextEntry(entry);
                written.add(directory);
            }
        }
    }
    
    private void writeManifest(JarOutputStream jos) throws IOException {
        // Write the META-INF/ directory
        JarEntry entry1 = new JarEntry("META-INF/");
        jos.putNextEntry(entry1);
        
        // Write a default MANIFEST.MF entry
        Manifest mf = new Manifest();
        JarEntry entry2 = new JarEntry("META-INF/MANIFEST.MF");
        jos.putNextEntry(entry2);
        mf.write(jos);
    }
    
    public static void main(String[] args) throws IOException, JAXBException {
        ModuleJarWriter mjw = new ModuleJarWriter();
        ModuleInfo info = new ModuleInfo("Fubar", 1, 0, 0, "Fubar module");
        mjw.setModuleInfo(info);
        mjw.addArtFile("models/castle.tiff", new File("/Users/jordanslott/Desktop/yuval.tiff"));
        mjw.addArtFile("models/castle/mystuff.tiff", new File("/Users/jordanslott/Desktop/yuval.tiff"));
        mjw.writeToJar(new File("mymodule.jar"));
    }

    /**
     * Fix issues with paths on Windows
     * @param path the path to fix
     * @return the fixed path
     */
    private String fixPath(String path) {
        // make sure to replace '\' with '/' on Windows
        if (File.separatorChar == '\\') {
            path = path.replace('\\', '/');
        }

        return path;
    }
}
