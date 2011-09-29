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

import java.util.StringTokenizer;

/**
 * Provides support for the test harness. This support is required in core
 * so changes can be made before the test harness module has been loaded
 *
 * @author Paul
 */
public class TestHarnessSupport {

    /**
     * Process the TestHarness command line args, because of the way javaws -open
     * passes the args they all appear in a single string.
     * @param args
     */
    static void processCommandLineArgs(String args) {
        // Strip off -b
        String arg = args.substring(3);

        // Test Harness.
        // Have to parse the argument as javaws passes all the args
        // as a single string
        StringTokenizer st = new StringTokenizer(arg);
        while(st.hasMoreTokens()) {
            String tok = st.nextToken();
            if (tok.equalsIgnoreCase("-username")) {
                String name = st.nextToken();
                System.setProperty("auth.username", name);
                System.setProperty("auth.fullname", name);
            } else if (tok.equalsIgnoreCase("-actorPort")) {
                String portStr = st.nextToken();
                System.setProperty("testharness.port", portStr);
            } else if (tok.equalsIgnoreCase("-userdir")) {
                System.setProperty("wonderland.user.dir", st.nextToken());
            } else if (tok.equalsIgnoreCase("-audiofile")) {
                System.setProperty("softphone.audio.file", st.nextToken());
            } else {
                System.err.println("Unknown option in TestHarnessSupport "+tok);
            }
        }

        System.setProperty("testharness.enabled", "true");
        System.setProperty("softphone.silent", "true");
    }
    
}
