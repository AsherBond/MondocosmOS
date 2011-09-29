/**
 * Open Wonderland
 *
 * Copyright (c) 2010 - 2011, Open Wonderland Foundation, All Rights Reserved
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
package org.jdesktop.wonderland.server.cell;

import com.jme.math.Vector3f;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.kernel.ComponentRegistry;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.auth.WonderlandIdentity;
import org.jdesktop.wonderland.common.cell.CellEditConnectionType;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.MultipleParentException;
import org.jdesktop.wonderland.common.cell.messages.CellCreateMessage;
import org.jdesktop.wonderland.common.cell.messages.CellCreatedMessage;
import org.jdesktop.wonderland.common.cell.messages.CellDeleteMessage;
import org.jdesktop.wonderland.common.cell.messages.CellDuplicateMessage;
import org.jdesktop.wonderland.common.cell.messages.CellEditMessage;
import org.jdesktop.wonderland.common.cell.messages.CellEditMessage.EditType;
import org.jdesktop.wonderland.common.cell.messages.CellReparentMessage;
import org.jdesktop.wonderland.common.cell.security.ChildrenAction;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.common.cell.state.PositionComponentServerState;
import org.jdesktop.wonderland.common.comms.ConnectionType;
import org.jdesktop.wonderland.common.messages.ErrorMessage;
import org.jdesktop.wonderland.common.messages.Message;
import org.jdesktop.wonderland.common.security.Action;
import org.jdesktop.wonderland.server.WonderlandContext;
import org.jdesktop.wonderland.server.comms.SecureClientConnectionHandler;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;
import org.jdesktop.wonderland.server.security.Resource;

/**
 * Handles CellEditMessages sent by the Wonderland client
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
class CellEditConnectionHandler implements SecureClientConnectionHandler, Serializable {

    public ConnectionType getConnectionType() {
        return CellEditConnectionType.CLIENT_TYPE;
    }

    public void registered(WonderlandClientSender sender) {
        // ignore
    }

    public Resource checkConnect(WonderlandClientID clientID, Properties properties) {
        return null;
    }

    public void clientConnected(WonderlandClientSender sender,
            WonderlandClientID clientID, Properties properties) {
        // ignore
    }

    public void connectionRejected(WonderlandClientID clientID) {
        // ignore
    }

    public void clientDisconnected(WonderlandClientSender sender,
            WonderlandClientID clientID) {
        // ignore
    }

    public Resource checkMessage(WonderlandClientID clientID, Message message) {
        CellResourceManager crm = AppContext.getManager(CellResourceManager.class);
        Resource out = null;

        // for each cell being modified, check that the caller has
        // permissions to modify the children of the parent cell
        CellEditMessage editMessage = (CellEditMessage) message;
        switch (editMessage.getEditType()) {
            case CREATE_CELL:
                CellCreateMessage ccm = (CellCreateMessage) editMessage;
                if (ccm.getParentCellID() != null) {
                    out = crm.getCellResource(ccm.getParentCellID());
                }
                break;
            case DELETE_CELL:
                {
                    // delete requires permission from both the cell being
                    // deleted and the parent cell
                    CellDeleteMessage cdm = (CellDeleteMessage) editMessage;
                    CellMO deleteMO = CellManagerMO.getCell(cdm.getCellID());
                    if (deleteMO == null || 
                            deleteMO.getCellID().equals(CellID.getEnvironmentCellID()))
                    {
                        break;
                    }
                    Resource child = crm.getCellResource(cdm.getCellID());
                    Resource parent = null;

                    // get the cell's parent, if any
                    CellMO parentMO = deleteMO.getParent();
                    if (parentMO != null) {
                        parent = crm.getCellResource(parentMO.getCellID());
                    }

                    // now create a delete resource with child & parent
                    if (child != null || parent != null) {
                        out = new DeleteCellResource(cdm.getCellID().toString(),
                                                     child, parent);
                    }
                }
                break;
            case DUPLICATE_CELL:
                CellDuplicateMessage cnm = (CellDuplicateMessage) editMessage;
                CellMO dupMO = CellManagerMO.getCell(cnm.getCellID());
                if (dupMO != null && dupMO.getParent() != null) {
                    out = crm.getCellResource(dupMO.getParent().getCellID());
                }
                break;

            case REPARENT_CELL:
                {
                    CellReparentMessage msg = (CellReparentMessage) editMessage;

                    CellMO childMO = CellManagerMO.getCell(msg.getCellID());
                    if (childMO==null)
                        break;

                    Resource child = crm.getCellResource(msg.getCellID());
                    Resource oldParent = null;
                    Resource newParent = null;

                    CellMO oldParentMO = childMO.getParent();
                    if (oldParentMO!=null)
                        oldParent = crm.getCellResource(oldParentMO.getCellID());

                    CellMO newParentMO = CellManagerMO.getCell(msg.getParentCellID());
                    if (newParentMO!=null)
                        newParent = crm.getCellResource(msg.getParentCellID());

                    if (child!=null || oldParent!=null || newParent!=null)
                        out = new ReparentCellResource(msg.getCellID().toString(), child, oldParent, newParent);
                }
                break;

        }

        return out;
    }

    public void messageReceived(WonderlandClientSender sender, WonderlandClientID clientID, Message message) {
        
        Logger logger = Logger.getLogger(CellEditConnectionHandler.class.getName());

        CellEditMessage editMessage = (CellEditMessage)message;
        if (editMessage.getEditType() == EditType.CREATE_CELL) {

            // The create message contains a setup class of the cell setup
            // information. Simply parse this stream, which will result in a
            // setup class of the property type.
            CellServerState setup = ((CellCreateMessage)editMessage).getCellSetup();
            
            // Fetch the server-side cell class name and create the cell
            String className = setup.getServerClassName();
            logger.fine("Attempting to load cell mo: " + className);
            CellMO cellMO = CellMOFactory.loadCellMO(className);
            if (cellMO == null) {
                /* Log a warning and move onto the next cell */
                logger.warning("Unable to load cell MO: " + className );
                sender.send(clientID, new ErrorMessage(editMessage.getMessageID(),
                        "Unable to load cell MO: " + className));
                return;
            }

            // Find the parent cell (if any)
            CellMO parent = null;
            CellID parentCellID = ((CellCreateMessage)editMessage).getParentCellID();
            if (parentCellID != null) {
                parent = CellManagerMO.getCell(parentCellID);
            }

            /* Call the cell's setup method */
            try {
                cellMO.setServerState(setup);

                if (parent == null) {
                    WonderlandContext.getCellManager().insertCellInWorld(cellMO);
                } else {
                    parent.addChild(cellMO);
                }
                //everything worked okay. send response message
                sender.send(clientID, new CellCreatedMessage(editMessage.getMessageID(), cellMO.getCellID()));

            } catch (ClassCastException cce) {
                logger.log(Level.WARNING, "Error setting up new cell " +
                        cellMO.getName() + " of type " +
                        cellMO.getClass() + ", it does not implement " +
                        "BeanSetupMO.", cce);
                sender.send(clientID,
                        new ErrorMessage(editMessage.getMessageID(),cce));
                return;
            } catch (MultipleParentException excp) {
                logger.log(Level.WARNING, "Error adding new cell " + cellMO.getName()
                        + " of type " + cellMO.getClass() + ", has multiple parents", excp);
                sender.send(clientID,
                        new ErrorMessage(editMessage.getMessageID(), excp));
                return;
            }
        }
        else if (editMessage.getEditType() == EditType.DELETE_CELL) {
            // Find the cell object given the ID of the cell. If the ID is
            // invalid, we just log an error and return.
            CellID cellID = ((CellDeleteMessage)editMessage).getCellID();
            CellMO cellMO = CellManagerMO.getCell(cellID);
            if (cellMO == null) {
                logger.warning("No cell found to delete with cell id " + cellID);
                return;
            }

            // Find out the parent of the cell. This may be null if the cell is
            // at the world root. This determines from where to remove the cell
            CellMO parentMO = cellMO.getParent();
            if (parentMO != null) {
                parentMO.removeChild(cellMO);
            }
            else {
                CellManagerMO.getCellManager().removeCellFromWorld(cellMO);
            }
        }
        else if (editMessage.getEditType() == EditType.DUPLICATE_CELL) {
            // Find the cell object given the ID of the cell. If the ID is
            // invalid, we just log an error and return.
            CellID cellID = ((CellDuplicateMessage)editMessage).getCellID();
            CellMO cellMO = CellManagerMO.getCell(cellID);
            if (cellMO == null) {
                logger.warning("No cell found to duplicate with cell id " + cellID);
                sender.send(clientID, new ErrorMessage(editMessage.getMessageID(),
                        "No cell found to duplicate with cell id " + cellID));
                return;
            }
            CellMO parentCellMO = cellMO.getParent();

            // We need to fetch the current state of the cell from the cell we
            // wish to duplicate. We also need the name of the server-side cell
            // class
            CellServerState state = cellMO.getServerState(null);
            String className = state.getServerClassName();

            // Attempt to create the cell using the cell factory and the class
            // name of the server-side cell.
            CellMO newCellMO = CellMOFactory.loadCellMO(className);
            if (newCellMO == null) {
                /* Log a warning and move onto the next cell */
                logger.warning("Unable to duplicate cell MO: " + className);
                sender.send(clientID, new ErrorMessage(editMessage.getMessageID(),
                        "Unable to duplicate cell MO: " + className));
                return;
            }

            // We want to modify the position of the new cell slight, so we
            // offset the position by (1, 1, 1).
            PositionComponentServerState position = (PositionComponentServerState)state.getComponentServerState(PositionComponentServerState.class);
            if (position == null) {
                logger.warning("Unable to determine the position of the cell " +
                        "to duplicate with id " + cellID);
                sender.send(clientID, new ErrorMessage(editMessage.getMessageID(),
                        "Unable to determine the position of the cell " +
                        "to duplicate with id " + cellID));
                return;
            }
            Vector3f offset = new Vector3f(1, 0, 1);
            Vector3f origin = position.getTranslation();
            position.setTranslation(offset.add(origin));
            state.addComponentServerState(position);

            // Set the desired name of the cell contained within the message
            state.setName(((CellDuplicateMessage)editMessage).getCellName());
            
            // Set the state of the new cell and add it to the same parent as
            // the old cell. If the old parent cell is null, we just insert it
            // as root.
            newCellMO.setServerState(state);
            try {
                if (parentCellMO == null) {
                    WonderlandContext.getCellManager().insertCellInWorld(newCellMO);
                }
                else {
                    parentCellMO.addChild(newCellMO);
                }
                sender.send(clientID, new CellCreatedMessage(editMessage.getMessageID(), newCellMO.getCellID()));
            } catch (MultipleParentException excp) {
                logger.log(Level.WARNING, "Error duplicating cell " +
                        newCellMO.getName() + " of type " + newCellMO.getClass() +
                        ", has multiple parents", excp);
                sender.send(clientID, new ErrorMessage(editMessage.getMessageID(),
                        excp));
                return;
            }
        }
        else if (editMessage.getEditType() == EditType.REPARENT_CELL) {
            // Find the cell id to move and the new parent id
            CellID cellID = ((CellReparentMessage)editMessage).getCellID();
            CellID newParentID = ((CellReparentMessage)editMessage).getParentCellID();

            // Figure out the new local coordinates of the cell wrt the new
            // parent

            // Change the parent cell
            CellMO child = CellManagerMO.getCell(cellID);
            CellMO oldParent = child.getParent();
            CellMO newParent = CellManagerMO.getCell(newParentID);

            if (oldParent == null) {
                CellManagerMO.getCellManager().removeCellFromWorld(child);
            } else {
                oldParent.removeChild(child);
            }

            CellTransform childTransform = ((CellReparentMessage) editMessage).getChildCellTransform();
            if (childTransform != null)
                child.setLocalTransform(childTransform);

            if (newParent == null) {
                try {
                    CellManagerMO.getCellManager().insertCellInWorld(child);
                } catch (MultipleParentException ex) {
                    Logger.getLogger(CellEditConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                try {
                    newParent.addChild(child);
                } catch (MultipleParentException ex) {
                    Logger.getLogger(CellEditConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public boolean messageRejected(WonderlandClientSender sender,
                                   WonderlandClientID clientID, Message message,
                                   Set<Action> requested, Set<Action> granted)
    {
        Logger logger = Logger.getLogger(CellEditConnectionHandler.class.getName());
        logger.warning("Message " + message + " rejected from " + clientID);

        return true;
    }

    /**
     * Returns the base URL of the web server.
     */
    private static URL getWebServerURL() throws MalformedURLException {
        return new URL(System.getProperty("wonderland.web.server.url"));
    }
    
    /**
     * Given a base URL of the server (e.g. http://localhost:8080) returns
     * the server name and port as a string (e.g. localhost:8080). Returns null
     * if the host name is not present.
     * 
     * @return <server name>:<port>
     * @throw MalformedURLException If the given string URL is invalid
     */
    private static String getServerFromURL(URL serverURL) {
        String host = serverURL.getHost();
        int port = serverURL.getPort();
        
        if (host == null) {
            return null;
        }
        else if (port == -1) {
            return host;
        }
        return host + ":" + port;
    }

    /**
     * Deleting requires permission to modify the cell being deleted as well
     * as permission to modify the children of the parent cell, if any.  This
     * resource provides that mapping.  All request are mapped to the child
     * except requests for the ModifyChildren which are mapped to the parent.
     */
    private static class DeleteCellResource implements Resource {
        private String cellID;
        private Resource child;
        private Resource parent;

        public DeleteCellResource(String cellID, Resource child,
                                  Resource parent)
        {
            this.cellID = cellID;
            this.child = child;
            this.parent = parent;
        }

        public String getId() {
            return "DeleteCell_" + cellID;
        }

        public Result request(WonderlandIdentity identity, Action action) {
            if (action instanceof ChildrenAction && parent != null) {
                // route to parent (if any)
                return parent.request(identity, action);
            } else if (!(action instanceof ChildrenAction) && child != null) {
                // route to child (if any)
                return child.request(identity, action);
            }

            // if we got here, there is no-one to route to -- just grant the
            // request
            return Result.GRANT;
        }

        public boolean request(WonderlandIdentity identity, Action action,
                               ComponentRegistry registry)
        {
            if (action instanceof ChildrenAction && parent != null) {
                // route to parent (if any)
                return parent.request(identity, action, registry);
            } else if (!(action instanceof ChildrenAction) && child != null) {
                // route to child (if any)
                return child.request(identity, action, registry);
            }

            // if we got here, there is no-one to route to -- just grant the
            // request
            return true;
        }
    }

    private static class ReparentCellResource implements Resource {
        private String cellID;
        private Resource child;
        private Resource oldParent;
        private Resource newParent;

        public ReparentCellResource(String cellID, Resource child,
                                  Resource oldParent,
                                  Resource newParent)
        {
            this.cellID = cellID;
            this.child = child;
            this.oldParent = oldParent;
            this.newParent = newParent;
        }

        public String getId() {
            return "ReparentCell_" + cellID;
        }

        public Result request(WonderlandIdentity identity, Action action) {
            if (action instanceof ChildrenAction && oldParent != null) {
                Result tmp = Result.GRANT;
                if (oldParent!=null )
                    tmp = oldParent.request(identity, action);
                Result tmp2 = Result.GRANT;
                if (newParent!=null)
                    tmp2 = newParent.request(identity, action);

                return Result.combine(tmp, tmp2);
            } else if (!(action instanceof ChildrenAction) && child != null) {
                // route to child (if any)
                return child.request(identity, action);
            }

            // if we got here, there is no-one to route to -- just grant the
            // request
            return Result.GRANT;
        }

        public boolean request(WonderlandIdentity identity, Action action,
                               ComponentRegistry registry)
        {
            if (action instanceof ChildrenAction) {
                boolean tmp = true;
                if (oldParent!=null)
                    tmp = oldParent.request(identity, action, registry);
                boolean tmp2 = true;
                if (newParent!=null)
                    tmp2 = newParent.request(identity, action, registry);
                return (tmp && tmp2);
            } else if (!(action instanceof ChildrenAction) && child != null) {
                // route to child (if any)
                return child.request(identity, action, registry);
            }

            // if we got here, there is no-one to route to -- just grant the
            // request
            return true;
        }
    }
}
