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
package org.jdesktop.wonderland.common;

import java.io.IOException;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import java.util.logging.Logger;

import com.sun.stun.NetworkAddressManager;

public class NetworkAddress {

    private static Logger logger = Logger.getLogger(NetworkAddress.class.getName());

    public static InetAddress getPrivateLocalAddress() throws UnknownHostException {
	return NetworkAddressManager.getPrivateLocalAddress();
    } 

    public static InetAddress getPrivateLocalAddress(String s) throws UnknownHostException {
	return NetworkAddressManager.getPrivateLocalAddress(s);
    }

    /*
     * Ask stunServer to resolve socket.getAddress().
     */
    public static InetSocketAddress getPublicAddressFor(
            InetSocketAddress stunServer, DatagramSocket socket)
            throws IOException {

	return NetworkAddressManager.getPublicAddressFor(stunServer, socket);
    }

}
