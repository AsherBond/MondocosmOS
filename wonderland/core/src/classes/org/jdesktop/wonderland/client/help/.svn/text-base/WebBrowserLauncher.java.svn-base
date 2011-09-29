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
package org.jdesktop.wonderland.client.help;

import java.lang.reflect.Method;

/**
 * Launches a browser external to Wonderland. This code was obtained on the
 * Internet and carried the following header:
 * 
 * /////////////////////////////////////////////////////////
 * //  Bare Bones Browser Launch                          //
 * //  Version 1.5                                        //
 * //  December 10, 2005                                  //
 * //  Supports: Mac OS X, GNU/Linux, Unix, Windows XP    //
 * //  Example Usage:                                     //
 * //     String url = "http://www.centerkey.com/";       //
 * //     BareBonesBrowserLaunch.openURL(url);            //
 * //  Public Domain Software -- Free to Use as You Like  //
 * /////////////////////////////////////////////////////////
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class WebBrowserLauncher {

    /**
     * Opens a URL in an external web browser. Throws java.lang.Exception upon
     * error.
     * 
     * @param url The URL to open in an external web browser
     * @throw Exception Upon error launching the web browser
     */
    public static void openURL(String url) throws java.lang.Exception {
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Mac OS") == true) {
            Class fileMgr = Class.forName("com.apple.eio.FileManager");
            Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[]{String.class});
            openURL.invoke(null, new Object[] { url });
        }
        else if (osName.startsWith("Windows") == true) {
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
        }
        else { //assume Unix or Linux
            String[] browsers = {
                "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape"
            };
            String browser = null;
            for (int count = 0; count < browsers.length && browser == null; count++) {
                if (Runtime.getRuntime().exec(new String[]{ "which", browsers[count] }).waitFor() == 0) {
                    browser = browsers[count];
                }
            }
            if (browser == null) {
                throw new Exception("Could not find web browser");
            }
            else {
                Runtime.getRuntime().exec(new String[]{ browser, url });
            }
        }
    }
}
