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
package org.jdesktop.wonderland.modules.webdav.web.content;

import com.sun.enterprise.security.web.integration.WebPrincipal;
import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.security.auth.Subject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.servlets.WebdavServlet;
import org.apache.naming.resources.FileDirContext;
import org.apache.naming.resources.ProxyDirContext;
import org.glassfish.security.common.Group;
import org.jdesktop.wonderland.modules.security.weblib.UserGroupPrincipal;
import org.jdesktop.wonderland.utils.RunUtil;

/**
 * Extension of default Catalina WebDav servlet to read from an alternate
 * directory and implement Wonderland-specific permissions.
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public class WonderlandWebdavServlet extends WebdavServlet {
    private static final Logger logger =
            Logger.getLogger(WonderlandWebdavServlet.class.getName());

    private static ThreadLocal<HttpServletRequest> requestLocal =
            new ThreadLocal<HttpServletRequest>();

    @Override
    public void init() throws ServletException {
        super.init();

        File contentDir = RunUtil.getContentDir();

        try {
            FileDirContext fdc = new PermissionsDirContext(contentDir);
            resources = new ProxyDirContext(new Hashtable(), fdc);

            /*{
                // workaround for issue where cache entries have null
                // attributes when permissions checks fail
                @Override
                public CacheEntry lookupCache(String name) {
                    CacheEntry ce = super.lookupCache(name);
                    if (ce != null && ce.attributes == null) {
                        ce.attributes = new ResourceAttributes();
                        ce.exists = false;
                    }
                    return ce;
                }

            };*/
        } catch (Exception ex) {
            throw new ServletException("Unable to initialize webdav servlet",
                                       ex);
        }
    }

    @Override
    protected void service(HttpServletRequest req,
                           HttpServletResponse resp)
        throws ServletException, IOException
    {
        try {
            // fill in the current servlet request as a thread-local variable
            requestLocal.set(req);

            // now do the rest of the processing
            super.service(req, resp);
        } finally {
            // remove the servlet request
            requestLocal.remove();
        }
    }

    /**
     * An extension of FileDirContext that takes permissions into account
     * when deciding whether or not to grant access to a file.
     */
    class PermissionsDirContext extends FileDirContext {
        private static final String USERS_DIR = "users";
        private static final String GROUPS_DIR = "groups";
        private static final String PRIVATE_DIR = "private";

        // the filesystem base of this directory
        private File baseFile;
        
        public PermissionsDirContext(File baseFile) throws IOException {
            this.baseFile = baseFile;
            
            setDocBase(baseFile.getPath());
        }
        

        @Override
        public void bind(String name, Object obj, Attributes attrs)
                throws NamingException
        {
            File file = new File(base, name);
            checkWrite(file);

            super.bind(name, obj, attrs);
        }

        @Override
        public DirContext createSubcontext(String name, Attributes attrs)
                throws NamingException
        {
            File file = new File(base, name);
            checkWrite(file);

            return super.createSubcontext(name, attrs);
        }

        @Override
        public void destroySubcontext(String name) throws NamingException {
            File file = file(name);
            checkWrite(file);

            super.destroySubcontext(name);
        }

        @Override
        public Attributes getAttributes(String name, String[] attrIds)
                throws NamingException
        {
            File file = file(name);
            checkRead(file);

            return super.getAttributes(name, attrIds);
        }

        @Override
        public NamingEnumeration list(String name) throws NamingException {
            final File listBase = file(name);
            checkRead(listBase);

            // return an enumeration that only includes files we have read
            // access to
            final NamingEnumeration res = super.list(name);
            return new NamingEnumeration() {
                private boolean finished = false;
                private Object next;

                public Object next() throws NamingException {
                    return nextElement();
                }

                public boolean hasMore() throws NamingException {
                    return hasMoreElements();
                }

                public void close() throws NamingException {
                    res.close();
                }

                public boolean hasMoreElements() {
                    if (finished) {
                        return false;
                    }

                    return findNext();
                }

                public Object nextElement() {
                    if (finished) {
                        throw new NoSuchElementException("No more elements");
                    }

                    if (next == null) {
                        findNext();
                    }

                    if (next == null) {
                        throw new NoSuchElementException("No more elements");
                    }

                    return next;
                }

                private boolean findNext() {
                    if (finished) {
                        return false;
                    }

                    while (res.hasMoreElements()) {
                        NameClassPair ncp = (NameClassPair) res.nextElement();
                        try {
                            File file = new File(listBase, ncp.getName());
                            checkRead(file);
                         
                            // if we got here, the file is valid
                            next = ncp;
                            return true;
                        } catch (NamingException ne) {
                            // ignore and go on
                        }
                    }

                    // no more elements
                    finished = true;
                    return false;
                }
            };
        }

        @Override
        public NamingEnumeration listBindings(String name)
                throws NamingException
        {
            // not used
            throw new UnsupportedOperationException("Not supported");
        }

        @Override
        public Object lookup(String name) throws NamingException {
            File file = file(name);
            checkRead(file);

            return super.lookup(name);
        }

        @Override
        public Object lookupLink(String name) throws NamingException {
            File file = file(name);
            checkRead(file);

            return super.lookupLink(name);
        }

        @Override
        public void modifyAttributes(String name, int mod_op, Attributes attrs)
                throws NamingException
        {
            File file = file(name);
            checkWrite(file);

            super.modifyAttributes(name, mod_op, attrs);
        }

        @Override
        public void modifyAttributes(String name, ModificationItem[] mods)
                throws NamingException
        {
            File file = file(name);
            checkWrite(file);

            super.modifyAttributes(name, mods);
        }

        @Override
        public void rebind(String name, Object obj, Attributes attrs)
                throws NamingException
        {
            File file = new File(base, name);
            checkWrite(file);

            super.rebind(name, obj, attrs);
        }

        @Override
        public void rename(String oldName, String newName)
                throws NamingException
        {
            File oldFile = file(oldName);
            checkWrite(oldFile);
            File newFile = new File(base, newName);;
            checkWrite(newFile);

            super.rename(oldName, newName);
        }

        @Override
        public void unbind(String name) throws NamingException {
            File file = file(name);
            checkWrite(file);

            super.unbind(name);
        }

        protected void checkRead(File file) throws NamingException {
            if (file == null) {
                throw new NamingException("Check for null file");
            }

            // administrators have access to all resources
            if (isAdmin()) {
                return;
            }

            // check the path to this file
            List<File> path = listPath(file);
            FileType type = new FileType(path);

            if (logger.isLoggable(Level.FINEST)) {
                logger.finest("[PermissionsDirContext] CheckRead User: " +
                    getUserName() + " " +
                    "Path: " + path + " Category: " + type.getType() +
                    " User: " + type.getUser() +
                    " Private " + type.isPrivate() +
                    " Owner " + isOwner(type));
            }

            if (type.isPrivate() && !isOwner(type)) {
                System.out.println("[WebdavServlet] Permission denied reading " + file +
                               " by " + getUserName());
                throw new NamingException("Permission denied");
            }
        }

        protected void checkWrite(File file) throws NamingException {
            if (file == null) {
                throw new NamingException("Check for null file");
            }

            // administrators have access to all resources
            if (isAdmin()) {
                return;
            }

            // check the path to this file
            List<File> path = listPath(file);
            FileType type = new FileType(path);

            if (logger.isLoggable(Level.FINEST)) {
                logger.finest("[PermissionsDirContext] CheckWrite User: " +
                    getUserName() + " " +
                    "Path: " + path + " Category: " + type.getType() +
                    " User: " + type.getUser() +
                    " Private " + type.isPrivate() +
                    " Owner " + isOwner(type));
            }

            if (!isOwner(type)) {
                System.out.println("[WebdavServlet] Permission denied writing " + file +
                               " by " + requestLocal.get().getUserPrincipal().getName());
                throw new NamingException("Permission denied");
            }
        }

        /**
         * Determine if this is a system administrator
         * @return true if this is an administrator, or false if not
         */
        protected boolean isAdmin() {
            return requestLocal.get().isUserInRole("admin");
        }

        /**
         * Determine if the given user is an owner of the given directory
         * @param type the file type to check
         * @return true if the current user is an owner, or false if not
         */
        protected boolean isOwner(FileType type) {
            HttpServletRequest req = requestLocal.get();
            Principal p = req.getUserPrincipal();

            if (type.getType() == null) {
                return false;
            } else if (type.getType().equalsIgnoreCase(USERS_DIR)) {
                return (type.getUser() != null) &&
                        type.getUser().equals(getUserName());
            } else if (type.getType().equalsIgnoreCase(GROUPS_DIR)) {
                // get the name of the group
                String target = type.getUser();
                
                // see if this user is a member of the given group
                if (p instanceof UserGroupPrincipal) {
                    for (String group : ((UserGroupPrincipal) p).getGroups()) {
                        if (group.equals(target)) {
                            return true;
                        }
                    }
                } else if (p instanceof WebPrincipal) {
                    // Glassfish sometimes wraps the actual principal in its
                    // own class
                    Subject s = ((WebPrincipal) p).getSecurityContext().getSubject();
                    for (Group group : s.getPrincipals(Group.class)) {
                        if (group.getName().equals(target)) {
                            return true;
                        }
                    }
                }
            }

            return false;
        }

        /**
         * Get the name of the current user.  If the current user is not
         * authenticated, return null as the user name.
         * @return the current user name, or null if the current user
         * does not have an authenticated principal.
         */
        protected String getUserName() {
            HttpServletRequest req = requestLocal.get();
            Principal p = req.getUserPrincipal();

            if (p == null) {
                return null;
            }

            return p.getName();
        }

        /**
         * List the path from the root to this file.  The result is a list
         * of File objects, the first of which is the child of the root
         * file that is first on the given files' path.
         *
         * @param file the file to start with
         * @return the path to the given file
         */
        protected List<File> listPath(File file)
            throws NamingException
        {
            File curFile = file;
            List<File> out = new ArrayList<File>();

            while (!curFile.equals(baseFile)) {
               out.add(0, curFile);
               curFile = curFile.getParentFile();

               if (curFile == null) {
                   throw new NamingException("Derived path for " + file +
                                             " not a child of " + baseFile);
               }
            }

            return out;
        }

        class FileType {
            private String type;
            private String user;
            private boolean priv = false;

            public FileType(List<File> path) {
                if (!path.isEmpty()) {
                    type = path.get(0).getName();
                }

                if (path.size() > 1) {
                    user = path.get(1).getName();
                }

                if (path.size() > 2) {
                    priv = path.get(2).getName().equalsIgnoreCase(PRIVATE_DIR);
                }
            }

            public String getType() {
                return type;
            }

            public String getUser() {
                return user;
            }

            public boolean isPrivate() {
                return priv;
            }
        }
    }
}
