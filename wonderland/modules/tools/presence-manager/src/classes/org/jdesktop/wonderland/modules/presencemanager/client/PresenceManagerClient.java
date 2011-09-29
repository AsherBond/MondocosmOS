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
package org.jdesktop.wonderland.modules.presencemanager.client;

import com.jme.math.Vector3f;
import org.jdesktop.wonderland.client.ClientContext;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellCache;
import org.jdesktop.wonderland.client.cell.view.LocalAvatar;
import org.jdesktop.wonderland.client.cell.view.LocalAvatar.ViewCellConfiguredListener;
import org.jdesktop.wonderland.client.comms.BaseConnection;
import org.jdesktop.wonderland.client.comms.CellClientSession;
import org.jdesktop.wonderland.client.comms.ConnectionFailureException;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.input.InputManager;
import org.jdesktop.wonderland.client.softphone.SoftphoneControlImpl;
import org.jdesktop.wonderland.common.comms.ConnectionType;
import org.jdesktop.wonderland.common.cell.CallID;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellStatus;
import org.jdesktop.wonderland.common.messages.Message;
import org.jdesktop.wonderland.modules.presencemanager.common.PresenceInfo;
import org.jdesktop.wonderland.modules.presencemanager.common.PresenceManagerConnectionType;
import org.jdesktop.wonderland.modules.presencemanager.common.messages.ClientConnectMessage;
import org.jdesktop.wonderland.modules.presencemanager.common.messages.ClientConnectResponseMessage;
import org.jdesktop.wonderland.modules.presencemanager.common.messages.PlayerInRangeMessage;
import org.jdesktop.wonderland.modules.presencemanager.common.messages.PresenceInfoAddedMessage;
import org.jdesktop.wonderland.modules.presencemanager.common.messages.PresenceInfoChangedMessage;
import org.jdesktop.wonderland.modules.presencemanager.common.messages.PresenceInfoRemovedMessage;

import java.util.ArrayList;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.messages.ErrorMessage;
import org.jdesktop.wonderland.common.messages.ResponseMessage;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.NameTagComponent;
import org.jdesktop.wonderland.modules.presencemanager.common.messages.CellLocationRequestMessage;
import org.jdesktop.wonderland.modules.presencemanager.common.messages.CellLocationResponseMessage;
import org.jdesktop.wonderland.modules.presencemanager.common.messages.PresenceInfoChangedAliasMessage;

/**
 *
 * @author jprovino
 */
public class PresenceManagerClient extends BaseConnection implements
        ViewCellConfiguredListener {

    private static final Logger logger =
            Logger.getLogger(PresenceManagerClient.class.getName());
    private WonderlandSession session;
    private CellID cellID;
    private PresenceManagerImpl pm;
    private PresenceInfo presenceInfo;
    private static PresenceManagerClient client;
    private boolean connectionComplete = false;

    public static PresenceManagerClient getInstance() {
	return client;
    }

    /** 
     * Create a new PresenceManagerClient
     * @param session the session to connect to, guaranteed to be in
     * the CONNECTED state
     * @throws org.jdesktop.wonderland.client.comms.ConnectionFailureException
     */
    public PresenceManagerClient() {
        logger.fine("Starting PresenceManagerClient");
	client = this;
    }

    public synchronized void execute(final Runnable r) {
    }

    @Override
    public void connect(WonderlandSession session)
            throws ConnectionFailureException
    {
        super.connect(session);
        this.session = session;

	/*
	 * Depending on timing, we may or may not have been called at viewConfigured().
	 * We create and add the presence info for the client when viewConfigured() is called.
	 */
        pm = (PresenceManagerImpl) PresenceManagerFactory.getPresenceManager(session);

        LocalAvatar avatar = ((CellClientSession) session).getLocalAvatar();
        avatar.addViewCellConfiguredListener(this);
        if (avatar.getViewCell() != null) {
            // if the view is already configured, fake an event
            viewConfigured(avatar);
        }
    }

    @Override
    public void disconnect() {
        // LocalAvatar avatar = ((CellClientSession)session).getLocalAvatar();
        // avatar.removeViewCellConfiguredListener(this);
        super.disconnect();

	PresenceManagerFactory.reset();

        // connection information is no longer valid
        setConnectionComplete(false);
    }

    public void viewConfigured(LocalAvatar localAvatar) {
        if (localAvatar==null || localAvatar.getViewCell()==null) {
            logger.severe("TODO - implement PresenceManagerClient.viewConfigured for a null view cell");
        } else {
            cellID = localAvatar.getViewCell().getCellID();

            String callID = CallID.getCallID(cellID);

            SoftphoneControlImpl.getInstance().setCallID(callID);

            // get the list of all presence information
            ResponseMessage rm;
            try {
                rm = sendAndWait(new ClientConnectMessage(cellID));
                
                if (rm instanceof ClientConnectResponseMessage) {
                    handleMessage(rm);
                } else if (rm instanceof ErrorMessage) {
                    ErrorMessage em = (ErrorMessage) rm;
                    logger.log(Level.WARNING, "Error getting presence info " +
                               em.getErrorMessage(), em.getErrorCause());
                }
            } catch (InterruptedException ie) {
                logger.log(Level.WARNING, "Error reading presence info", ie);
            }



            logger.fine("[PresenceManagerClient] view configured fpr " + cellID + " in " + pm);
        }
    }

    public Vector3f getCellPosition(CellID cellID) {
        Message request = new CellLocationRequestMessage(cellID);

        try {
            ResponseMessage rm = sendAndWait(request);
            if (rm instanceof CellLocationResponseMessage) {
                return ((CellLocationResponseMessage) rm).getLocation();
            } else if (rm instanceof ErrorMessage) {
                logger.log(Level.WARNING, "Error getting location of " +
                        cellID + ": " + ((ErrorMessage) rm).getErrorMessage());
            }
        } catch (InterruptedException ie) {
            // ignore
        }

        // if we get here, there was an error getting the value
        return null;
    }

    @Override
    public void handleMessage(Message message) {
        logger.fine("got a message... " + message);

	if (message instanceof ClientConnectResponseMessage) {
	    ClientConnectResponseMessage msg = (ClientConnectResponseMessage) message;

	    CellCache cellCache = ClientContext.getCellCache(session);

	    if (cellCache == null) {
		logger.warning("Can't find cellCache for session " + session);
		return;
	    }

	    ArrayList<String> nameTagList = new ArrayList();

	    PresenceInfo[] presenceInfoList = msg.getPresenceInfoList();

	    for (int i = 0; i < presenceInfoList.length; i++) {
		PresenceInfo presenceInfo = presenceInfoList[i];

		logger.fine("Client connected: " + presenceInfo);

		logger.fine("Got ClientConnectResponse:  adding pi " + presenceInfo);
		pm.presenceInfoAdded(presenceInfo);

		String username = presenceInfo.getUserID().getUsername();

		if (presenceInfo.getCellID() == null) {
		    logger.warning("CellID is null for " + presenceInfo);
		    continue;
		}

		Cell cell = cellCache.getCell(presenceInfo.getCellID());

		if (cell == null) {
		    logger.warning("Unable to find cell for " + presenceInfo.getCellID());
		    continue;
		}

		NameTagComponent nameTag = cell.getComponent(NameTagComponent.class);

		if (presenceInfo.getUsernameAlias().equals(username) == false) {
 		    pm.changeUsernameAlias(presenceInfo, presenceInfo.getUsernameAlias());
 		}

		if (nameTag == null) {
		    continue;
		}

		nameTag.updateLabel(presenceInfo.getUsernameAlias(),
                                    presenceInfo.isInConeOfSilence(),
                                    presenceInfo.isSpeaking(),
                                    presenceInfo.isMuted());
	    }

//	    if (nameTagList.size() > 0) {
//		new NameTagUpdater(nameTagList);
//	    }

            // connection is complete
            setConnectionComplete(true);

	    return;
	}

        // Issue #1113: if the connection is not complete, ignore messages
        if (!isConnectionComplete()) {
            return;
        }

        if (message instanceof PlayerInRangeMessage) {
	    PlayerInRangeMessage msg = (PlayerInRangeMessage) message;

	    PresenceInfo info = pm.getPresenceInfo(msg.getCallID());

	    if (info == null) {
		logger.fine("no presence info for callID " + msg.getCallID());
		return;
	    }

	    pm.playerInRange(info, msg.isInRange());
	    return;
	}

        if (message instanceof PresenceInfoAddedMessage) {
            PresenceInfoAddedMessage m = (PresenceInfoAddedMessage) message;

            logger.fine("GOT PresenceInfoAddedMessage for " + m.getPresenceInfo());

            pm.presenceInfoAdded(m.getPresenceInfo());
            return;
        }

        if (message instanceof PresenceInfoRemovedMessage) {
            PresenceInfoRemovedMessage m = (PresenceInfoRemovedMessage) message;
            PresenceInfo pi = pm.getPresenceInfo(m.getCellID());
            if (pi == null) {
                logger.warning("No presence info found for " + m.getCellID());
                return;
            }

            logger.fine("GOT PresenceInfoRemovedMessage for " + pi);
            pm.presenceInfoRemoved(pi);
            return;
        }

        if (message instanceof PresenceInfoChangedMessage) {
            PresenceInfoChangedMessage m = (PresenceInfoChangedMessage) message;
            PresenceInfo pi = pm.getPresenceInfo(m.getCellID());
            if (pi == null) {
                logger.warning("No presence info found for " + m.getCellID());
                return;
            }

            logger.fine("GOT PresenceInfoChangeMessage for " + pi);
	    
            switch (m.getChange()) {
                case SPEAKING:
                    pm.setSpeaking(pi, m.getValue());
                    break;
                case MUTED:
                    pm.setMute(pi, m.getValue());
                    break;
                case SECRET_CHAT:
                    pm.setInSecretChat(pi, m.getValue());
                    break;
                case CONE_OF_SILENCE:
                    pm.setEnteredConeOfSilence(pi, m.getValue());
                    break;
            }
            return;
        }

        if (message instanceof PresenceInfoChangedAliasMessage) {
            PresenceInfoChangedAliasMessage m = (PresenceInfoChangedAliasMessage) message;
            PresenceInfo pi = pm.getPresenceInfo(m.getCellID());
            if (pi == null) {
                logger.warning("No presence info found for " + m.getCellID());
                return;
            }

            pm.changeUsernameAlias(pi, m.getAlias());
            return;
        }

        throw new UnsupportedOperationException("Unknown message:  " + message);
    }

    private synchronized boolean isConnectionComplete() {
        return connectionComplete;
    }

    private synchronized void setConnectionComplete(boolean connectionComplete) {
        this.connectionComplete = connectionComplete;
    }

    /*
     * There is no way to know when other avatar names have been initialized.
     * When we connect, if we can't update the names with mute and alias info
     * because a name tag doesn't yet exist, we shedule the update for later.
     */
//    class NameTagUpdater extends Thread {
//
//	private ArrayList<String> nameTagList;
//
//	public NameTagUpdater(ArrayList<String> nameTagList) {
//	    this.nameTagList = nameTagList;
//
//	    start();
//	}
//
//	public void run() {
//	    while (true) {
//		String[] names = nameTagList.toArray(new String[0]);
//
//		for (int i = 0; i < names.length; i++) {
//		    String name = names[i];
//
//		    nameTagList.remove(name);
//
//		    PresenceInfo info = pm.getUserPresenceInfo(name);
//
//		    if (info == null) {
//			logger.info("No presence info for " + name);
//			continue;
//		    }
//
//		    CellCache cellCache = ClientContext.getCellCache(session);
//
//		    Cell cell = cellCache.getCell(info.cellID);
//
//		    if (cell == null) {
//		  	logger.warning("No cell for " + name);
//			continue;
//		    }
//
////		    NameTagComponent nameTag = new NameTagComponent(cell, name, (float) .17);
//
//		    nameTag.updateLabel(info.usernameAlias, info.inConeOfSilence,
//			info.isSpeaking, info.isMuted);
//		}
//
//		if (nameTagList.size() == 0) {
//		    break;
//		}
//
//	        try {
//		    Thread.sleep(200);
//		} catch (InterruptedException e) {
//		}
//	    }
// 	}
//    }

    public ConnectionType getConnectionType() {
        return PresenceManagerConnectionType.CONNECTION_TYPE;
    }

}
