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
package org.jdesktop.wonderland.modules.appbase.client.utils.net;

import java.net.InetAddress;
import java.net.Inet6Address;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Logger;

/**
 * A class that tries to figure out the IP address of the local machine that can
 * preferably route to the outside world.
 * 
 * It does this by first building up a list of possible IP addresses and then picking the most
 * logical one. First all the network interfaces and their IP addresses are scanned and
 * those interfaces that are up (active) and not loopback and addresses that are not
 * link-local are added to a list of possible candidates.  It then adds "localhost"
 * to this list (least preferred).
 *
 * It then tries to auto-detect the IP address by opening a socket to the SGS server/port
 * and looking at the local address on that socket connection. If a valid non-loopback
 * address is found, then this is initially preferred.
 * 
 * It then looks to see if a IP address/interface has been manually set using the
 * "wonderland.local.hostAddress" system property. If there is one and is valid, this
 * becomes the preferred
 *
 * If no preferred address is found, it just picks the first one in the list that was built.
 *
 * @author kkg
 */
public class NetworkAddress {

    private static Logger logger = Logger.getLogger("wonderland.util");

    private static final String LOCALHOST = "localhost";

    private static ArrayList<NetworkAddress> networkAddressList = null;
    private static NetworkAddress defaultNetworkAddress = null;

    public synchronized static NetworkAddress[] getNetworkAddresses() {
	if (networkAddressList == null)
	    setupNetworkAddresses();

	return networkAddressList.toArray(new NetworkAddress[0]);
    }

    private static void setupNetworkAddresses() {
	networkAddressList = new ArrayList<NetworkAddress>();

	// First add network interfaces which are up and arent loopback
	// (since we will add localhost separately)
	try {
	    Enumeration<NetworkInterface> eni = NetworkInterface.getNetworkInterfaces();

	    while (eni.hasMoreElements()) {
		NetworkInterface ni = eni.nextElement();

		/* Java 5 backport: these two methods don't exist in Java 5
		   This means that the list of interfaces may contain some
		   invalid interfaces that aren't in the Java 6 list
		if (!ni.isLoopback() && ni.isUp()) {
		*/
		    Enumeration<InetAddress> eia = ni.getInetAddresses();
		    while (eia.hasMoreElements()) {
			InetAddress ia = eia.nextElement();

                        // ignore link local addresses
                        // issue #524: also ignore IPv6 addresses until xremwin properly
                        // supports IPv6
			if (!ia.isLinkLocalAddress() && !(ia instanceof Inet6Address)) {
			    networkAddressList.add(
				new NetworkAddress(ni.getName(), ia.getHostAddress()));
			    break;
			}
		    }
		/* Java 5 backport
		}
		*/
	    }
	} catch (Exception e) {}

	// Then add the LOCALHOST entry.
	try {
	    InetAddress ia = InetAddress.getByName(LOCALHOST);

	    if (ia != null)
		networkAddressList.add(
		    new NetworkAddress(LOCALHOST, ia.getHostAddress()));

	} catch (Exception e) {}

	addAddress("auto", detectLocalAddress(), true);
	addAddress("manual", System.getProperty("wonderland.local.hostAddress",""), true);

	if ((defaultNetworkAddress == null) && (networkAddressList.size() > 0))
	    defaultNetworkAddress = networkAddressList.get(0);
    }

    public synchronized static NetworkAddress addAddress(String name, String address, boolean setDefault) {
	if ((address == null) || "".equals(address))
	    return null;

	NetworkAddress na = null;

	String hostAddress = null;

	// See if it a hostname or IP address.
	try {
	    InetAddress ia = InetAddress.getByName(address);

	    if (ia != null)
		hostAddress = ia.getHostAddress();

	} catch (Exception e) {}

	for (int i = 0; i < networkAddressList.size(); i++) {
	    na = networkAddressList.get(i);

	    // If the address is specified as a network interface or a tag
	    // then just use that...
	    if (na.getName().equals(address)) {
		if (setDefault) defaultNetworkAddress = na;
		return na;
	    }

	    // If the host address matches that of an existing entry just use that...
	    if ((hostAddress != null) && (na.getHostAddress().equals(hostAddress))) {
		if (setDefault) defaultNetworkAddress = na;
		return na;
	    }

	    // If the tag name matches, then override this entries host address
	    if (na.getName().equals(name))
		break;

	    na = null;
	}

	if (hostAddress == null)
	    return null;

	if (na == null) {
	    na = new NetworkAddress(name, hostAddress);
	    networkAddressList.add(na);
	} else {
	    na.setHostAddress(hostAddress);
	}

	if (setDefault) defaultNetworkAddress = na;
	return na;
    }

    public synchronized static void setDefaultNetworkAddress(NetworkAddress na) {
	defaultNetworkAddress = na;
    }

    public synchronized static NetworkAddress getDefaultNetworkAddress() {
	if (defaultNetworkAddress == null)
	    getNetworkAddresses();

	return defaultNetworkAddress;
    }

    public static String getDefaultHostAddress() {
	NetworkAddress na = getDefaultNetworkAddress();

	if (na == null)
	    return LOCALHOST;

	return na.getHostAddress();
    }

    public static InetAddress getDefaultInetAddress() {
	try {
	    return InetAddress.getByName(getDefaultHostAddress());
	} catch (Exception e) {}

	return null;
    }

    public static String detectLocalAddress(String server, String port) {
	if ((server == null) || (port == null))
	    return null;

	String localAddress = null;
	Socket s = null;

        try {
	    if ((s = new Socket(server, Integer.decode(port))) != null) {
		InetAddress ia = s.getLocalAddress();

		if ((ia != null) && !ia.isLoopbackAddress())
		    localAddress = ia.getHostAddress();
	    }

        } catch (Exception e) {}

	if (s != null)
	    try { s.close(); } catch (Exception e) {}

	if (localAddress != null)
	    logger.warning("Auto-detected LOCAL host address = " + localAddress);

	return localAddress;
    }

    public static String detectLocalAddress() {
	return detectLocalAddress(System.getProperty("sgs.server"),
				  System.getProperty("sgs.port"));
    }

    /** Creates a new instance of NetworkAddress */
    private String name;
    private String hostAddress;

    private NetworkAddress(String name, String hostAddress) {
	setName(name);
	setHostAddress(hostAddress);
    }

    @Override
    public String toString() {
	if (getName().equals(getHostAddress()))
	    return getName();

	if ("".equals(getName()))
	    return getHostAddress();

	return getHostAddress() + " [" + getName() + "]";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }
}
