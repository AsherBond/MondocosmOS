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
package org.jdesktop.wonderland.utils.ant;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Workaround for Windows issue with task shutdown.  Instead of running
 * the web server directly from any, launch it from this wrapper.  When
 * the wrapper exits, the kill-switch connection to the web server
 * closes, and the web server shuts down gracefully.
 *
 * @author kaplanj
 */
public class RunWrapper {
    private Socket socket;

    public void execute(File jar, String[] args)
            throws IOException, InterruptedException
    {
        String javaHome = System.getProperty("java.home");
        String fileSep = System.getProperty("file.separator");

        // create the command to execute as a list of strings.  We will
        // convert this to a string array later on in order to execute it.
        // Command will be of approximately the form:
        //
        // $java -cp ${antdir}/ant-launcher.jar -Dant.home=${antdir} \
        //       org.apache.tools.ant.launch.Launcher -Dxxx=yyy \
        //       -f ${rundir}/run.xml
        //
        List<String> cmd = new ArrayList<String>();
        cmd.add(javaHome + fileSep + "bin" + fileSep + "java");

        // copy the system properties, ignoring the built-in ones
        for (Object propNameObj : System.getProperties().keySet()) {
            String propName = (String) propNameObj;

            if (!propName.startsWith("java.") &&
                !propName.startsWith("os.") &&
                !propName.startsWith("file.") &&
                !propName.startsWith("path.") &&
                !propName.startsWith("line.") &&
                !propName.startsWith("user."))
            {
                cmd.add("-D" + propName + "=" +
                        System.getProperty((String) propName));
            }
        }

        // setup the killswitch port
        int killSwitchPort = 20000 + new Random().nextInt(10000);
        cmd.add("-Dwonderland.webserver.killswitch=" + killSwitchPort);

        // add the jar file
        cmd.add("-jar");
        cmd.add(jar.getCanonicalPath());

        // add in any arguments
        for (String arg : args) {
            cmd.add(arg);
        }

        System.out.println("RunWrapper: executing command " + cmd);

        // execute
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);

        final Process proc = pb.start();
        new Thread("Output reader") {
            @Override
            public void run() {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(proc.getInputStream()));
                String line;
                try {
                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException ioe) {
                    // oh well
                }
            }
        }.start();

        // wait a bit, then start the kill switch
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ie) {
            // ignore
        }

        // start the killswitch -- hopefully this wait was long
        // enough
        InetAddress localAddr = InetAddress.getLocalHost();
        socket = new Socket();
        socket.setKeepAlive(true);
        socket.connect(new InetSocketAddress(localAddr, killSwitchPort));

        // now wait for the underlying process to exit
        int res = proc.waitFor();
        System.exit(res);
    }

    public Socket getSocket() {
        return socket;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            usage();
        }

        File f = new File(args[0]);
        String[] newArgs = new String[args.length - 1];
        if (newArgs.length > 0) {
            System.arraycopy(args, 1, newArgs, 0, newArgs.length);
        }

        RunWrapper wrapper = new RunWrapper();
        try {
            wrapper.execute(f, newArgs);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (InterruptedException ie) {
            // ignore
        } finally {
            if (wrapper.getSocket() != null &&
                wrapper.getSocket().isConnected())
            {
                try {
                    wrapper.getSocket().close();
                } catch (IOException ioe) {
                    // ignore
                }
            }
        }
    }

    public static void usage() {
        System.err.println("Usage: RunWrapper <jar> [properties_file]");
        System.exit(-1);
    }
}
