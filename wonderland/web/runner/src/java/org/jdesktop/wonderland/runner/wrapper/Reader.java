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
package org.jdesktop.wonderland.runner.wrapper;

import java.io.InputStreamReader;
import java.net.URL;

/**
 *
 * @author jkaplan
 */
public class Reader {
    public static void main(String[] args) {
        try {
            /* Open an HTTP connection to the Jersey RESTful service */
            URL url = new URL("http://localhost:8080/wonderland-web-runner/services/list");
            System.out.println("url = " + url.toExternalForm());
            RunnerListWrapper w = RunnerListWrapper.decode(new InputStreamReader(url.openStream()));
        
            for (RunnerWrapper rw : w.getRunners()) {
                System.out.println(rw);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
