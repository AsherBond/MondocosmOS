/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2010, Sun Microsystems, Inc., All Rights Reserved
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
package org.jdesktop.wonderland.webserver;

import java.util.Map;
import java.util.logging.Logger;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.module.ServerAuthModule;
import javax.security.auth.message.config.ServerAuthContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Default server auth module that is set up in the domain.xml for the
 * web server.  This SAM rejects all requests until a real SAM is registered
 * by a security module implementation.
 *
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public class DefaultSAM implements ServerAuthModule, ServerAuthContext {
    private static final Logger logger =
            Logger.getLogger(DefaultSAM.class.getName());

    private static Class<? extends ServerAuthModule> delegateClass;
    private static boolean started = false;

    private ServerAuthModule delegate;

    public void initialize(MessagePolicy mp, MessagePolicy mp1,
                           CallbackHandler ch, Map map)
        throws AuthException
    {
        getDelegate().initialize(mp, mp1, ch, map);
    }

    public Class[] getSupportedMessageTypes() {
        return getDelegate().getSupportedMessageTypes();
    }

    public AuthStatus validateRequest(MessageInfo mi, Subject sbjct,
                                      Subject sbjct1)
        throws AuthException
    {
        return getDelegate().validateRequest(mi, sbjct, sbjct1);
    }

    public AuthStatus secureResponse(MessageInfo mi, Subject sbjct)
        throws AuthException
    {
        return getDelegate().secureResponse(mi, sbjct);
    }

    public void cleanSubject(MessageInfo mi, Subject sbjct)
        throws AuthException
    {
        getDelegate().cleanSubject(mi, sbjct);
    }
    
    private synchronized ServerAuthModule getDelegate() {
        // nothing registered, just return the default class
        if (!isStarted() || (delegate == null && delegateClass == null)) {
            return new DefaultDelegate();
        }

        // if the delegate has not yet been created, go ahead and create
        // an instance now
        if (delegate == null) {
            try {
                delegate = delegateClass.newInstance();
            } catch (InstantiationException ex) {
                throw new IllegalStateException(ex);
            } catch (IllegalAccessException ex) {
                throw new IllegalStateException(ex);
            }
        }
        
        return delegate;
    }

    /**
     * Set the class of delegate to use
     * @param delegateClass the class of delegate to use
     */
    public synchronized static void setDelegateClass(
            Class<? extends ServerAuthModule> delegateClass)
    {
        DefaultSAM.delegateClass = delegateClass;
    }

    /**
     * Get the currently setup delegate class
     * @return the class of delegate to use
     */
    public synchronized static Class<? extends ServerAuthModule> getDelegateClass() {
        return DefaultSAM.delegateClass;
    }

    /**
     * Set whether the SAM should start delegating connections to the
     * delegate.
     * @param started true if the SAM has started delegating connections
     */
    public synchronized static void setStarted(boolean started) {
        DefaultSAM.started = started;
    }

    /**
     * Get whether the SAM should start delegating connections to the
     * delegate.
     * @return true if the SAM has started delegating connections
     */
    public synchronized static boolean isStarted() {
        return DefaultSAM.started;
    }

    /**
     * The default delegate will be used when no other delegate can be
     * found.  It simply rejects all messages.
     */
    private final class DefaultDelegate implements ServerAuthModule {

        public void initialize(MessagePolicy mp, MessagePolicy mp1,
                               CallbackHandler ch, Map map)
            throws AuthException
        {
            // do nothing
        }

        public Class[] getSupportedMessageTypes() {
            return new Class[] { HttpServletRequest.class,
                                 HttpServletResponse.class };
        }

        public AuthStatus validateRequest(MessageInfo mi, Subject sbjct,
                                          Subject sbjct1)
            throws AuthException
        {
            logger.warning("Rejecting request to default delegate.");

            // all attempts to validate are failures until a real delegate
            // is setup
            return AuthStatus.SEND_FAILURE;
        }

        public AuthStatus secureResponse(MessageInfo mi, Subject sbjct)
            throws AuthException
        {
            // all attempts to validate are failures until a real delegate
            // is setup
            return AuthStatus.SEND_FAILURE;
        }

        public void cleanSubject(MessageInfo mi, Subject sbjct)
            throws AuthException
        {
            sbjct.getPrincipals().clear();
        }
    }
}
