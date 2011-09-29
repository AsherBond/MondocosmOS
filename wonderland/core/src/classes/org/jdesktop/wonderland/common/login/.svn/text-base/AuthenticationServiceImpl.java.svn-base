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
package org.jdesktop.wonderland.common.login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Logger;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

/**
 * An implementation of an authentication service
 * @author jkaplan
 */
class AuthenticationServiceImpl implements AuthenticationService {
    private static final Logger logger =
            Logger.getLogger(AuthenticationServiceImpl.class.getName());
    
    private String authUrl;
    private String username;
    private String token;
    private String cookieName;

    public AuthenticationServiceImpl(String authUrl, String username)
        throws AuthenticationException
    {
        this.authUrl = authUrl;
        this.username = username;

        // assume the cookie name doesn't change
        cookieName = getCookieName();
    }
    
    public String getAuthenticationURL() {
        return authUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthenticationToken() {
        return token;
    }

    public void setAuthenticationToken(String token) {
        this.token = token;
    }

    public void secureURLConnection(HttpURLConnection conn) {
        // add the cookie to the request
        StringBuffer cookieBuf;

        // make sure we're not overwriting other cookies
        String existingCookies = conn.getRequestProperty("Cookie");
        if (existingCookies == null) {
            cookieBuf = new StringBuffer();
        } else {
            cookieBuf = new StringBuffer(existingCookies);
        }
        
        // add our cookie
        try {
            cookieBuf.append(cookieName);
            cookieBuf.append("=");
            cookieBuf.append(URLEncoder.encode(getAuthenticationToken(), 
                                               "UTF-8"));
            cookieBuf.append(";");
        } catch (UnsupportedEncodingException uee) {
             throw new IllegalStateException(uee);
        }
        
        conn.setRequestProperty("Cookie", cookieBuf.toString());
    }

    public boolean isTokenValid() throws AuthenticationException {
        return isTokenValid(getAuthenticationToken());
    }

    public boolean isTokenValid(String token) throws AuthenticationException {
        try {
            URL u = new URL(getAuthenticationURL() + "/isTokenValid");
            HttpURLConnection uc = (HttpURLConnection) u.openConnection();
            uc.setRequestMethod("POST");
            uc.setRequestProperty("Content-Type",
                                  "application/x-www-form-urlencoded");

            uc.setDoOutput(true);
            uc.setDoInput(true);

            PrintWriter pr = new PrintWriter(
                    new OutputStreamWriter(uc.getOutputStream()));
            pr.append("tokenid=" + URLEncoder.encode(token, "UTF-8"));
            pr.close();

            logger.fine("IsTokenValid response: " + uc.getResponseCode() +
                           ": " + uc.getResponseMessage());

            BufferedReader br = new BufferedReader(
                                    new InputStreamReader(uc.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                logger.finest("IsTokenValid response line: " + line);

                if (line.startsWith("boolean=")) {
                    return Boolean.parseBoolean(line.substring("boolean=".length()));
                }
            }
        } catch (IOException ioe) {
            throw new AuthenticationException(ioe);
        }

        throw new AuthenticationException("No validation in response");
    }

    public String getCookieName() throws AuthenticationException {
        try {
            URL u = new URL(getAuthenticationURL() + "/getCookieNameForToken");
            HttpURLConnection uc = (HttpURLConnection) u.openConnection();
            uc.setDoInput(true);

            logger.fine("GetCookieName response: " + uc.getResponseCode() +
                        " : " + uc.getResponseMessage());

            BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                logger.finest("GetCookieName response line: " + line);

                if (line.startsWith("string=")) {
                    return line.substring("string=".length());
                }
            }
        } catch (IOException ioe) {
            throw new AuthenticationException(ioe);
        }

        throw new AuthenticationException("No cookie name in service output");
    }

    public Attributes getAttributes(String token, String... attributeNames)
            throws AuthenticationException
    {
        BasicAttributes out = new BasicAttributes();

        try {
            URL u = new URL(getAuthenticationURL() + "/attributes");
            HttpURLConnection uc = (HttpURLConnection) u.openConnection();
            uc.setRequestMethod("POST");
            uc.setRequestProperty("Content-Type",
                                  "application/x-www-form-urlencoded");

            uc.setDoOutput(true);
            uc.setDoInput(true);

            PrintWriter pr = new PrintWriter(
                                  new OutputStreamWriter(uc.getOutputStream()));
            pr.append("subjectid=" + URLEncoder.encode(token, "UTF-8"));
            if (attributeNames != null && attributeNames.length > 0) {
                for (String name : attributeNames) {
                    pr.append("&attribute_names=" + name);
                }
            }
            pr.close();

            logger.fine("GetAttributes Response: " + uc.getResponseCode() +
                        ": " + uc.getResponseMessage());

            BufferedReader br = new BufferedReader(
                                    new InputStreamReader(uc.getInputStream()));
            parseAttributes(br, "userdetails", out);
        } catch (IOException ioe) {
            throw new AuthenticationException(ioe);
        }

        return out;
    }

    public Attributes read(String userId, String... attributeNames)
        throws AuthenticationException
    {
        BasicAttributes out = new BasicAttributes();

        try {
            URL u = new URL(getAuthenticationURL() + "/read");
            HttpURLConnection uc = (HttpURLConnection) u.openConnection();
            uc.setRequestMethod("POST");
            uc.setRequestProperty("Content-Type",
                                  "application/x-www-form-urlencoded");

            uc.setDoOutput(true);
            uc.setDoInput(true);

            PrintWriter pr = new PrintWriter(
                                  new OutputStreamWriter(uc.getOutputStream()));
            pr.append("name=" + userId);
            pr.append("&admin=" + URLEncoder.encode(getAuthenticationToken(),
                                                    "UTF-8"));
            if (attributeNames != null && attributeNames.length > 0) {
                for (String name : attributeNames) {
                    pr.append("&attribute_names=" + name);
                }
            }
            pr.close();

            logger.fine("Read Response: " + uc.getResponseCode() +
                        ": " + uc.getResponseMessage());

            BufferedReader br = new BufferedReader(
                                    new InputStreamReader(uc.getInputStream()));
            parseAttributes(br, "identitydetails", out);
        } catch (IOException ioe) {
            throw new AuthenticationException(ioe);
        }

        return out;
    }

    public void logout() throws AuthenticationException {
        try {
            URL u = new URL(getAuthenticationURL() + "/logout");
            HttpURLConnection uc = (HttpURLConnection) u.openConnection();
            uc.setRequestMethod("POST");
            uc.setRequestProperty("Content-Type",
                                  "application/x-www-form-urlencoded");

            uc.setDoOutput(true);
            uc.setDoInput(true);

            PrintWriter pr = new PrintWriter(
                    new OutputStreamWriter(uc.getOutputStream()));
            pr.append("subjectid=" + URLEncoder.encode(getAuthenticationToken(),
                                                       "UTF-8"));
            pr.close();

            logger.fine("Logout response: " + uc.getResponseCode() +
                           ": " + uc.getResponseMessage());
        } catch (IOException ioe) {
            throw new AuthenticationException(ioe);
        }
    }

    private void parseAttributes(BufferedReader reader, String prefix,
                                 BasicAttributes attrs)
            throws IOException
    {
        String namePrefix = prefix + ".attribute.name=";
        String valuePrefix = prefix + ".attribute.value=";

        String line;
        Attribute curAttr = null;

        while ((line = reader.readLine()) != null) {
            logger.finest("ParseAttributes response line: " + line);
            if (line.startsWith(namePrefix)) {
                String name = line.substring(namePrefix.length());
                curAttr = new BasicAttribute(name);
                attrs.put(curAttr);
            } else if (line.startsWith(valuePrefix)) {
                String value = line.substring(valuePrefix.length());
                curAttr.add(value);
            }
        }
    }
}
