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
package org.jdesktop.wonderland.modules.artimport.client.jme;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 *
 * @author paulby
 */
public class ModuleSourceManager {

    ModuleSourceManager() {

    }

    /**
     * Create a module with the given name in the specified directory
     * @param moduleName
     * @param parentDirectory
     */
    public void createModule(String moduleName, String moduleDescription, File parentDirectory, boolean includeArt, boolean includeClient, boolean includeServer) {
        File moduleDir = new File(parentDirectory.getAbsolutePath()+File.separatorChar+moduleName);
//        if (moduleDir.exists())
//            throw new RuntimeException("Module Directory already exists "+moduleDir.getAbsolutePath());
//
//        moduleDir.mkdir();

        String srcPkg = "org.jdesktop.wonderland.modules."+moduleName;

        String srcPkgDir = ("src.classes."+srcPkg).replaceAll("\\.", Matcher.quoteReplacement(File.separator));

        File clientSrc = new File(moduleDir.getAbsolutePath()+File.separatorChar+srcPkgDir+File.separatorChar+"client");
        clientSrc.mkdirs();

        File commonSrc = new File(moduleDir.getAbsolutePath()+File.separatorChar+srcPkgDir+File.separatorChar+"common");
        commonSrc.mkdirs();

        File serverSrc = new File(moduleDir.getAbsolutePath()+File.separatorChar+srcPkgDir+File.separatorChar+"server");
        serverSrc.mkdirs();

        if (includeArt) {
            File artDir = new File(moduleDir.getAbsolutePath()+File.separatorChar+srcPkgDir+File.separatorChar+"art");
            artDir.mkdir();
        }

        // Copy build.xml
        File myBuildProp = new File(moduleDir.getAbsolutePath()+File.separatorChar+"my.module.properties");
        File buildXML = new File(moduleDir.getAbsolutePath()+File.separatorChar+"build.xml");

        try {
            copyFile(ModuleSourceManager.class.getClassLoader().getResourceAsStream("org/jdesktop/wonderland/modules/artimport/client/jme/resources/module_build_template.xml"),
                    new FileOutputStream(buildXML),
                    new LineConditioner[] { new LineSubstituteConditioner("@ART@",
                                                includeArt ? "<art dir=\"\\${current.dir}/art\"/>" : "<!--<art dir=\"\\${current.dir}/art\"/>-->"),
                                            new LineSubstituteConditioner("@MODULE_NAME@", moduleName),
                                            new LineSubstituteConditioner("@MODULE_DESC@", moduleDescription),
                                            new LineSubstituteConditioner("@CLIENT_START@", includeClient ? "" : "<!--"),
                                            new LineSubstituteConditioner("@CLIENT_END@", includeClient ? "" : "-->"),
                                            new LineSubstituteConditioner("@SERVER_START@", includeServer ? "" : "<!--"),
                                            new LineSubstituteConditioner("@SERVER_END@", includeServer ? "" : "-->"),
                                            new LineSubstituteConditioner("@MODULE_PKG@", srcPkg.replaceAll("\\.", File.separator))}
            );

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ModuleSourceManager.class.getName()).log(Level.SEVERE, null, ex);
        }


        // Copy and update default nb project
        File nbProjDir = new File(moduleDir.getAbsolutePath()+File.separatorChar+"nbproject");
        nbProjDir.mkdirs();
        File nbProj = new File(nbProjDir.getAbsolutePath()+File.separatorChar+"project.xml");

        try {
            copyFile(ModuleSourceManager.class.getClassLoader().getResourceAsStream("org/jdesktop/wonderland/modules/artimport/client/jme/resources/module_nbproject_template.xml"),
                    new FileOutputStream(nbProj),
                    new LineConditioner[] { new LineSubstituteConditioner("@MODULE_NAME@",
                                            moduleName)}
            );

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ModuleSourceManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void copyFile(InputStream inFile, OutputStream outFile, LineConditioner[] conditioners) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inFile));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outFile));

        String line = null;

        try {
            do {
                line = reader.readLine();
                if (line != null) {
                    if (conditioners!=null) {
                        for(int i=0; i<conditioners.length; i++)
                            line = conditioners[i].conditionLine(line);
                    }

                    writer.write(line);
                    writer.newLine();
                }

            } while(line!=null);

            reader.close();
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(ModuleSourceManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    interface LineConditioner {
        public String conditionLine(String line);
    }

    /**
     * Replace the regular expression in each line with the replaceWith string
     */
    class LineSubstituteConditioner implements LineConditioner {
        private String srcRegEx;
        private String replaceWith;

        public LineSubstituteConditioner(String srcRegEx, String replaceWith) {
            this.srcRegEx = srcRegEx;
            this.replaceWith = replaceWith;
        }

        public String conditionLine(String line) {
//            System.err.println(line+"  srcEx "+srcRegEx+"   with "+replaceWith);
            return line.replaceAll(srcRegEx, replaceWith);
        }
    }


}
