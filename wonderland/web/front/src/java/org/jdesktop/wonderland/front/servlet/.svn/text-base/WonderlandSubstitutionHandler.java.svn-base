/**
 * Open Wonderland
 *
 * Copyright (c) 2010, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */

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
package org.jdesktop.wonderland.front.servlet;

import java.util.Enumeration;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriBuilder;
import org.jdesktop.deployment.jnlp.servlet.DefaultJnlpSubstitutionHandler;
import org.jdesktop.wonderland.utils.ServletPropertyUtil;

/**
 * Custom substitution handler for the Wonderland options to the JNLP download
 * servlet.
 * @author jkaplan
 */
public class WonderlandSubstitutionHandler 
        extends DefaultJnlpSubstitutionHandler 
{
    private static final Logger logger = 
            Logger.getLogger(WonderlandSubstitutionHandler.class.getName());
    
    @Override
    public String specializeJnlpTemplate(HttpServletRequest request, 
                                         String respath, String jnlpTemplate)
    {
        logger.finest("Wonderland Substitution handler");
        
        // apply default substitutions
        jnlpTemplate = super.specializeJnlpTemplate(request, respath, jnlpTemplate);

        // get the servlet context
        ServletContext context = request.getSession().getServletContext();
                
        // substitute $$sgs.server with the sgs server URL
        String serverURL = ServletPropertyUtil.getProperty("wonderland.server.url", context);
        if (serverURL == null) {
            UriBuilder builder = UriBuilder.fromUri(request.getRequestURL().toString());
            serverURL = builder.replacePath("/").build().toString();
            
            /*try {
                serverName = InetAddress.getLocalHost().getCanonicalHostName();
            } catch (UnknownHostException uhe) {
                logger.log(Level.WARNING, "Error getting local host", uhe);
                serverName = "localhost";
            }*/
        }
        jnlpTemplate = substitute(jnlpTemplate, "$$wonderland.server.url", serverURL);

        // substitute in the config directory
        String configDirURL = ServletPropertyUtil.getProperty("wonderland.client.config.dir", context);
        if (configDirURL == null) {
            UriBuilder builder = UriBuilder.fromUri(request.getRequestURL().toString());
            configDirURL = builder.replacePath("/wonderland-web-front/config/").build().toString();
        }
        jnlpTemplate = substitute(jnlpTemplate, "$$wonderland.client.config.dir", configDirURL);

        // add in additional properties
        StringBuilder props = new StringBuilder();
        for (Enumeration<String> e = request.getParameterNames(); e.hasMoreElements();) {
            String key = e.nextElement();
            String value = request.getParameter(key);
            
            if (isValid(key) && isValid(value)) {
                props.append("<property name=\"");
                props.append(key);
                props.append("\" value=\"");
                props.append(value);
                props.append("\"/>\n");
            }
        }
        jnlpTemplate = substitute(jnlpTemplate, "$$url.props", props.toString());

        // return the result
        return jnlpTemplate;
    }

    private static final String[] INVALID = new String[] { "\"", "'", "\n", "\r" };
    private boolean isValid(String prop) {
        for (String s : INVALID) {
            if (prop.contains(s)) {
                return false;
            }
        }

        return true;
    }
}
