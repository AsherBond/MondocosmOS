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
package org.jdesktop.wonderland.modules.securitysession.noauth.weblib;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.naming.directory.BasicAttribute;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.util.Base64;
import org.jdesktop.wonderland.modules.securitysession.weblib.SessionLoginException;
import org.jdesktop.wonderland.modules.securitysession.weblib.SessionManager;
import org.jdesktop.wonderland.modules.securitysession.weblib.UserRecord;

/**
 *
 * @author jkaplan
 */
public class NoAuthSessionManagerImpl implements SessionManager {
    private final Map<String, UserRecord> byUserId = 
            new LinkedHashMap<String, UserRecord>();
    private final Map<String, UserRecord> byToken =
            new LinkedHashMap<String, UserRecord>();

    public void initialize(Map opts) {
        // nothing to do
    }

    public synchronized UserRecord login(String userId, Object... credentials)
            throws SessionLoginException
    {
        if (byUserId.containsKey(userId)) {
            throw new SessionLoginException("Duplicate login not allowed");
        }
        
        // decode the credentials
        String fullname = null;
        String email = null;
        
        if (credentials.length > 0) {
            fullname = (String) credentials[0];
        }
        
        if (credentials.length > 1) {
            email = (String) credentials[1];
        }
        
        // create the userrecord
        UserRecord rec = new UserRecord(userId, newToken(userId));
        
        // set values in the record
        rec.getAttributes().put(new BasicAttribute("uid", userId));
        rec.getAttributes().put(new BasicAttribute("cn", fullname));
        rec.getAttributes().put(new BasicAttribute("mail", email));

        // add to our internal maps
        byUserId.put(userId, rec);
        byToken.put(rec.getToken(), rec);
     
        return rec;
    }

    public synchronized UserRecord get(String userId) {
        // get the existing value for this user
        return byUserId.get(userId);
    }

    public synchronized UserRecord getByToken(String token) {
        return byToken.get(token);
    }

    public String getUserId(String token) {
        String out = null;

        UserRecord rec = getByToken(token);
        if (rec != null) {
            out = rec.getUserId();
        }

        return out;
    }

    public synchronized UserRecord logout(String token) {
        UserRecord rec = byToken.remove(token);
        if (rec != null) {
            byUserId.remove(rec.getUserId());
        }

        return rec;
    }

    public String handleUnauthenticated(HttpServletRequest request,
                                        boolean mandatory,
                                        HttpServletResponse response)
        throws IOException
    {
        // unauthenticated users are treated as admin so the web UI works
        return "admin";
    }

    /**
     * Create a token for the given user id.  This will produce a unique
     * token for this user.  Tokens are non-deterministic, so calling this
     * method multiple times for the same token should result in different
     * tokens every time.
     * @param userId the user id to create a token for
     * @return a token for the user
     */
    private static String newToken(String userId) {
        MessageDigest md;

        try {
            md = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException nsae) {
            throw new IllegalStateException("Unable to find SHA", nsae);
        }

        md.update(userId.getBytes());

        // add some random data to the message to make it unique
        SecureRandom sr = new SecureRandom();
        byte[] buffer = new byte[128];
        sr.nextBytes(buffer);
        md.update(buffer);

        byte[] res = md.digest();
        return new String(Base64.encode(res));
    }
}
