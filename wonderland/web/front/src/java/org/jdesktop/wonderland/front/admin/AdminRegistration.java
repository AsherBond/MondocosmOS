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
package org.jdesktop.wonderland.front.admin;

import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Used to register new entries on the Wonderland admin page
 * @author jkaplan
 */
public class AdminRegistration implements Comparable {
    public static final String ADMIN_REGISTRY_PROP = "AdminRegistry";
    private static final String ADMIN_CONTEXT = "/wonderland-web-front";
   
    private String shortName;
    private String displayName;
    private String url;
    private boolean absolute;
    private RegistrationFilter filter;
    private int position = Integer.MAX_VALUE / 2;

    /**
     * Register a new admin entry
     * @param reg the registration to register
     * @param context the servlet context
     */
    public static void register(AdminRegistration reg, ServletContext context) {
        List<AdminRegistration> registry = getRegistry(context);
        registry.add(reg);
    }
    
    /**
     * Unregister an admin entry
     * @param reg the registration to unregister
     * @param context the servlet context
     */
    public static void unregister(AdminRegistration reg, ServletContext context) {
        List<AdminRegistration> registry = getRegistry(context);
        registry.remove(reg);
    }
    
    public static List<AdminRegistration> getRegistry(ServletContext context) {
        ServletContext adminContext = context.getContext(ADMIN_CONTEXT);
        if (adminContext == null) {
            throw new IllegalStateException("Unable to find context " + 
                                            ADMIN_CONTEXT);
        }
        
        List<AdminRegistration> registry = (List<AdminRegistration>)
                adminContext.getAttribute(ADMIN_REGISTRY_PROP);
        if (registry == null) {
            throw new IllegalStateException("Unable to find property " +
                                            ADMIN_REGISTRY_PROP);
        }
        
        return registry;
    }
    
    /**
     * Create a new registration for the given name and URL
     * @param displayName the registration name to display on the admin page
     * @param url the URL to link to
     */
    public AdminRegistration(String displayName, String url) {
        this (null, displayName, url);
    }

    /**
     * Create a new registration for the given name and URL
     * @param shortName the short version of this registration's name
     * @param displayName the registration name to display on the admin page
     * @param url the URL to link to
     */
    public AdminRegistration(String shortName, String displayName, String url) {
        this.shortName = shortName;
        this.displayName = displayName;
        this.url = url;
    }
    
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isAbsolute() {
        return absolute;
    }

    public void setAbsolute(boolean absolute) {
        this.absolute = absolute;
    }

    public RegistrationFilter getFilter() {
        return filter;
    }

    public void setFilter(RegistrationFilter filter) {
        this.filter = filter;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int compareTo(Object o) {
        if (!(o instanceof AdminRegistration)) {
            return 0;
        }

        AdminRegistration ao = (AdminRegistration) o;

        // first compare positions
        int posComp = Integer.valueOf(getPosition()).compareTo(
                        Integer.valueOf(ao.getPosition()));
        if (posComp != 0) {
            return posComp;
        }

        // next, alphabetical
        return getDisplayName().compareTo(ao.getDisplayName());
    }

    /**
     * A filter that determines if the given menu item is visible for the
     * given request and response. The request will be for the admin
     * page, and will have username and group information if the user
     * is logged in.
     */
    public interface RegistrationFilter {
        /**
         * Return true if the menu item should be visible, or false if not.
         */
        public boolean isVisible(HttpServletRequest request,
                                 HttpServletResponse response);
    }

    /** a filter that is only visible to admins */
    public static final RegistrationFilter ADMIN_FILTER = new RegistrationFilter() {
        public boolean isVisible(HttpServletRequest request,
                                 HttpServletResponse response)
        {
            return request.isUserInRole("admin");
        }
    };

    /** a filter that is only visible to logged in users */
    public static final RegistrationFilter LOGGED_IN_FILTER = new RegistrationFilter() {
        public boolean isVisible(HttpServletRequest request,
                                 HttpServletResponse response)
        {
            return (request.getUserPrincipal() != null);
        }
    };
}
