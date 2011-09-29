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
package org.jdesktop.wonderland.webstart.ant;

// IMPORTANT! You need to compile this class against ant.jar.
// The easiest way to do this is to add ${ant.core.lib} to your project's classpath.
// For example, for a plain Java project with no other dependencies, set in project.properties:
// javac.classpath=${ant.core.lib}

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * @author jkaplan
 */
public class FileDateTask extends Task {
    private File file;
    private String prop;
    
    public void setFile(File file) {
        this.file = file;
    }
 
    public void setProp(String prop) {
        this.prop = prop;
    }
    
    @Override
    public void execute() throws BuildException {
        if (file == null) {
            throw new BuildException("File required");
        }
        if (prop == null) {
            throw new BuildException("Prop required");
        }
        
        getProject().setProperty(prop, String.valueOf(file.lastModified()));
    }
}
