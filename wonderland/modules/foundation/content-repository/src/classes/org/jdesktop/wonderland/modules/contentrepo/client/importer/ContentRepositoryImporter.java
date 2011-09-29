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
package org.jdesktop.wonderland.modules.contentrepo.client.importer;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.BaseClientPlugin;
import org.jdesktop.wonderland.client.ClientPlugin;
import org.jdesktop.wonderland.client.content.ContentImportManager;
import org.jdesktop.wonderland.client.jme.content.AbstractContentImporter;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.client.login.PrimaryServerListener;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.annotation.Plugin;
import org.jdesktop.wonderland.modules.contentrepo.client.ContentRepository;
import org.jdesktop.wonderland.modules.contentrepo.client.ContentRepositoryRegistry;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentCollection;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode.Type;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentRepositoryException;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentResource;

/**
 * A default content importer to import content into the default content
 * repository for the system. Is also a plugin that registers itself with the
 * content import manager.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@Plugin
public class ContentRepositoryImporter extends AbstractContentImporter 
        implements ClientPlugin
{
    private static Logger logger = Logger.getLogger(ContentRepositoryImporter.class.getName());

    /* The current session to the server, needed to fetch the content repo */
    private ServerSessionManager loginInfo = null;

    /** An internal BaseClientPlugin that handles activate and deactivate by
     *  delegating to the superclass methods
     */
    private BaseClientPlugin plugin;

    /**
     * @inheritDoc()
     */
    public void initialize(ServerSessionManager loginInfo) {
        this.loginInfo = loginInfo;
        this.plugin = new BaseClientPlugin() {
            @Override
            protected void activate() {
                ContentRepositoryImporter.this.register();
            }

            @Override
            protected void deactivate() {
                ContentRepositoryImporter.this.unregister();
            }

        };

        // initialize our plugin
        plugin.initialize(loginInfo);
    }

    public void cleanup() {
        // clean up the plugin
        plugin.cleanup();
    }

    /**
     * Called when our session manager becomes primary. Set the default
     * importer to this one.
     */
    public void register() {
        ContentImportManager cim = ContentImportManager.getContentImportManager();
        cim.setDefaultContentImporter(this);
    }

    /**
     * Called when our session manager is no longer primary.  Reset the
     * default importer back to null
     */
    public void unregister() {
        ContentImportManager cim = ContentImportManager.getContentImportManager();
        if (cim.getDefaultContentImporter() == this) {
            cim.setDefaultContentImporter(null);
        }
    }

    /**
     * @inheritDoc()
     */
    public String[] getExtensions() {
        // Don't need to return any extensions since this is a default handler
        return new String[] {};
    }

    /**
     * @inheritDoc()
     */
    @Override
    public String isContentExists(File file) {
        String fileName = file.getName();
        ContentRepositoryRegistry registry = ContentRepositoryRegistry.getInstance();
        ContentRepository repo = registry.getRepository(loginInfo);
        try {
            ContentCollection userRoot = repo.getUserRoot();
            if (userRoot.getChild(fileName) != null) {
                return "wlcontent://users/" + loginInfo.getUsername() + "/" + fileName;
            }
            return null;
        } catch (ContentRepositoryException excp) {
            logger.log(Level.WARNING, "Error while try to find " + fileName +
                    " in content repository", excp);
            return null;
        }
    }

    /**
     * @inheritDoc()
     */
    @Override
    public String uploadContent(File file) throws IOException {
        String fileName = file.getName();
        ContentRepositoryRegistry registry = ContentRepositoryRegistry.getInstance();
        ContentRepository repo = registry.getRepository(loginInfo);
        ContentCollection userRoot;

        // First try to find the resource, if it exists, then simply upload the
        // new bits. Otherwise create the resource and upload the new bits
        try {
            userRoot = repo.getUserRoot();
            ContentNode node = (ContentNode)userRoot.getChild(fileName);
            if (node == null) {
                node = (ContentNode)userRoot.createChild(fileName, Type.RESOURCE);
            }
            ((ContentResource)node).put(file);
        } catch (ContentRepositoryException excp) {
            logger.log(Level.WARNING, "Error while trying to find " + fileName +
                    " in content repository", excp);
            throw new IOException("Error while trying to find " + fileName +
                    " in content repository");
        }

        // If we have reached here, then we have successfully uploaded the bits
        // so we return a valid URI to the content.
        return "wlcontent://users/" + loginInfo.getUsername() + "/" + fileName;
    }
}
