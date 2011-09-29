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
package org.jdesktop.wonderland.modules.presencemanager.server;

import com.sun.mpk20.voicelib.app.AudioGroup;
import com.sun.mpk20.voicelib.app.AudioGroupPlayerInfo;
import com.sun.mpk20.voicelib.app.ManagedCallStatusListener;
import com.sun.mpk20.voicelib.app.Player;
import com.sun.mpk20.voicelib.app.PlayerInRangeListener;
import com.sun.mpk20.voicelib.app.VoiceManager;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedObjectRemoval;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.NameNotBoundException;
import com.sun.sgs.app.ObjectNotFoundException;
import com.sun.sgs.app.util.ScalableHashMap;
import com.sun.voip.client.connector.CallStatus;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.auth.WonderlandIdentity;
import org.jdesktop.wonderland.common.cell.CallID;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.messages.Message;
import org.jdesktop.wonderland.modules.presencemanager.common.PresenceInfo;
import org.jdesktop.wonderland.modules.presencemanager.common.PresenceManagerConnectionType;
import org.jdesktop.wonderland.modules.presencemanager.common.messages.PlayerInRangeMessage;
import org.jdesktop.wonderland.modules.presencemanager.common.messages.PresenceInfoAddedMessage;
import org.jdesktop.wonderland.modules.presencemanager.common.messages.PresenceInfoChangedAliasMessage;
import org.jdesktop.wonderland.modules.presencemanager.common.messages.PresenceInfoChangedMessage;
import org.jdesktop.wonderland.modules.presencemanager.common.messages.PresenceInfoChangedMessage.Change;
import org.jdesktop.wonderland.modules.presencemanager.common.messages.PresenceInfoRemovedMessage;
import org.jdesktop.wonderland.server.WonderlandContext;
import org.jdesktop.wonderland.server.auth.ClientIdentityManager;
import org.jdesktop.wonderland.server.comms.CommsManager;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;

/**
 * Server-side presence manager implementation.
 * <p>
 * This object is designed to be a singleton.  It is safe for use
 * in Darkstar because the object itself is stateless.  All state is stored
 * in separate managed objects, which are referred to by binding name.
 *
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public class PresenceManagerSrvImpl implements PresenceManagerSrv {
    private static final Logger logger =
            Logger.getLogger(PresenceManagerSrvImpl.class.getName());

    private static final String PRESENCE_BINDING =
            PresenceManagerSrvImpl.class.getName() + ".PRESENCE_BINDING";
    private static final String LISTENER_BINDING =
            PresenceManagerSrvImpl.class.getName() + ".LISTENER_BINDING";

    protected PresenceManagerSrvImpl() {
    }
    
    public PresenceInfo addPresenceInfo(BigInteger clientID, CellID cellID) {
        return addPresenceInfo(null, null, clientID, cellID);
    }
    
    public PresenceInfo addPresenceInfo(WonderlandClientSender sender,
                                        WonderlandIdentity wlID, 
                                        BigInteger clientID,
                                        CellID cellID) 
    {
        // if the sender is null, find the presence manager sender
        if (sender == null) {
            sender = getPresenceSender();
        }
        
        // if the given ID is null, find the ID of the current player
        if (wlID == null) {
            wlID = AppContext.getManager(ClientIdentityManager.class).getClientID();
        }
        
        // first, generate a new PresenceInfo object for this client
        String callID = CallID.getCallID(cellID);
        PresenceInfo pi = new PresenceInfo(cellID, clientID, wlID, callID);

        // add the presence info object to our map
        getPresenceInfo().put(clientID, pi);

        // notify all clients of the new presence info
        sender.send(new PresenceInfoAddedMessage(pi));

        // create a listener for this player
        PlayerListener listener = new PlayerListener(sender, clientID,
                callID, cellID, getPresenceInfo());

        // remember the listener
        getListeners().put(clientID, listener);
        
        return pi;
    }

    public void enableInRangeNotification(BigInteger clientID,
                                          WonderlandClientID wlClientID)
    {
        PlayerListener listener = getListeners().get(clientID);
        if (listener == null) {
            logger.warning("No player listener for " + clientID);
            return;
        }

        listener.enableInRangeListener(wlClientID);
    }

    public void setUsernameAlias(BigInteger clientID, String alias) {
        PresenceInfo pi = getPresenceInfo(clientID);
        if (pi == null) {
            logger.warning("No presence info for " + clientID);
            return;
        }

        // set the alias
        pi.setUsernameAlias(alias);

        // send a message to the connection
        getPresenceSender().send(new PresenceInfoChangedAliasMessage(pi.getCellID(), alias));
    }

    public void setInConeOfSilence(BigInteger clientID, boolean inCOS) {
        PresenceInfo pi = getPresenceInfo(clientID);
        if (pi == null) {
            logger.warning("No presence info for " + clientID);
            return;
        }

        // set the alias
        pi.setInConeOfSilence(inCOS);

        // send a message to the connection
        Message m = new PresenceInfoChangedMessage(pi.getCellID(),
                                                   Change.CONE_OF_SILENCE,
                                                   inCOS);
        getPresenceSender().send(m);
    }

    public void setSpeakingInGroup(WonderlandClientID targetID,
                                   BigInteger speakingID,
                                   boolean speaking)
    {
        PresenceInfo pi = getPresenceInfo(speakingID);
        if (pi == null) {
            logger.warning("No presence info for " + speakingID);
            return;
        }

        Message m = new PresenceInfoChangedMessage(pi.getCellID(),
                                                   Change.SPEAKING, speaking);
        getPresenceSender().send(m);
    }

    public PresenceInfo getPresenceInfo(BigInteger clientID) {
        return getPresenceInfo().get(clientID);
    }

    public PresenceInfo removePresenceInfo(BigInteger clientID) {
        // clean up this player's listener
        PlayerListener listener = getListeners().remove(clientID);
        if (listener == null) {
            logger.warning("[PresenceManagerConnectionHandler] No presence " +
                           "listener found for " + clientID);
        } else {
            listener.done();
            AppContext.getDataManager().removeObject(listener);
        }

        // remove the presence info for this user
        PresenceInfo pi = getPresenceInfo().remove(clientID);

        // notify listeners
        if (pi == null) {
            logger.warning("[PresenceManagerConnectionHandler] No presence " +
                           "info found for " + clientID);
        } else {
            getPresenceSender().send(new PresenceInfoRemovedMessage(pi.getCellID()));
        }

        return pi;
    }

    public Collection<PresenceInfo> getAllPresenceInfo() {
        return getPresenceInfo().values();
    }
    
    private Map<BigInteger, PresenceInfo> getPresenceInfo() {
        Map<BigInteger, PresenceInfo> presenceMap;

        try {
            presenceMap = (Map<BigInteger, PresenceInfo>)
                    AppContext.getDataManager().getBinding(PRESENCE_BINDING);
        } catch (NameNotBoundException nnbe) {
            // issue #1069: lazily create map if it doesn't exist
            presenceMap = new ScalableHashMap<BigInteger, PresenceInfo>();
            AppContext.getDataManager().setBinding(PRESENCE_BINDING, presenceMap);
        }
        
        return presenceMap;
    }

    private Map<BigInteger, PlayerListener> getListeners() {
        Map<BigInteger, PlayerListener> listenerMap;

        try {
            listenerMap = (Map<BigInteger, PlayerListener>)
                AppContext.getDataManager().getBinding(LISTENER_BINDING);
        } catch (NameNotBoundException nnbe) {
            // issue #1069: lazily create map if it doesn't exist
            listenerMap = new ScalableHashMap<BigInteger, PlayerListener>();
            AppContext.getDataManager().setBinding(LISTENER_BINDING, listenerMap);
        }
        
        return listenerMap;
    }

    private WonderlandClientSender getPresenceSender() {
        CommsManager cm = WonderlandContext.getCommsManager();
        return cm.getSender(PresenceManagerConnectionType.CONNECTION_TYPE);
    }

    static class PlayerListener implements ManagedObject, 
            PlayerInRangeListener, ManagedCallStatusListener,
            ManagedObjectRemoval, Serializable
    {
	private final WonderlandClientSender sender;
	private final BigInteger clientID;
        private final String callID;
        private final CellID cellID;
        private final ManagedReference<Map<BigInteger, PresenceInfo>> presenceMapRef;
        private final String bindingName;

        private RangeListenerImpl inRangeListener;
        private WonderlandClientID wlClientID;

  	private boolean done = false;

	public PlayerListener(WonderlandClientSender sender,
                              BigInteger clientID, String callID, CellID cellID,
                              Map<BigInteger, PresenceInfo> presenceMap)
        {
            this.sender = sender;
            this.clientID = clientID;
            this.callID = callID;
            this.cellID = cellID;
            
            this.presenceMapRef = AppContext.getDataManager().createReference(presenceMap);

            // create a binding so that the player in range listener can resolve
            // this managed object
            bindingName = PlayerListener.class.getName() + "." + clientID.toString();
            AppContext.getDataManager().setBinding(bindingName, this);

            // add a call status listener for this call
            VoiceManager vm = AppContext.getManager(VoiceManager.class);
            vm.addCallStatusListener(this, callID.toString());
	}

        public void enableInRangeListener(WonderlandClientID wlClientID) {
            this.wlClientID = wlClientID;

            // create the listener
            inRangeListener = new RangeListenerImpl(bindingName);

            // mark ourself for update
            AppContext.getDataManager().markForUpdate(this);

            // find the player associated with this call
            VoiceManager vm = AppContext.getManager(VoiceManager.class);
	    Player player = vm.getPlayer(callID);
	    if (player == null) {
                // if the call is not yet established, just return here.
                // when the call is established, the listener will be added
                // below
	        return;
            }

            // add listners
            player.addPlayerInRangeListener(inRangeListener);
        }

	public void done() {
	    done = true;
            
            // mark ourself for update
            AppContext.getDataManager().markForUpdate(this);

            // clean up listeners
            VoiceManager vm = AppContext.getManager(VoiceManager.class);
            vm.removeCallStatusListener(this, callID);

            if (inRangeListener != null) {
                Player player = vm.getPlayer(callID);
                if (player == null) {
                    logger.warning("[PlayerListener] No player for " + callID);
                    return;
                }
            
                player.removePlayerInRangeListener(inRangeListener);
            }
        }
	
        public void playerInRange(Player player, Player playerInRange, boolean isInRange) {
	    logger.fine("[PlayerListener] player in range");

            if (done) {
		return;
            }

	    /*
	     * When the client disconnects, there is a race between this thread and the disconnect thread.
	     */
	    try {
                sender.send(wlClientID, new PlayerInRangeMessage(playerInRange.getId(), isInRange));
            } catch (ObjectNotFoundException e) {
	    }
        }

        public void callStatusChanged(CallStatus status) {
            // find the player associated with this call
            VoiceManager vm = AppContext.getManager(VoiceManager.class);
	    Player player = vm.getPlayer(callID);
	    if (player == null) {
	        logger.warning("[PlayerListener] No player for " + callID);
                return;
            }

            PresenceInfo pi = presenceMapRef.get().get(clientID);
            if (pi == null) {
                logger.warning("[PlayerListener] No presence info for " + clientID);
                return;
            }

            switch (status.getCode()) {
                case CallStatus.ESTABLISHED:
                    if (inRangeListener != null) {
                        player.addPlayerInRangeListener(inRangeListener);
                    }
                    break;

                case CallStatus.MUTED:
                    pi.setMuted(true);
                    sender.send(new PresenceInfoChangedMessage(cellID, Change.MUTED, true));
                    break;

                case CallStatus.UNMUTED:
                    pi.setMuted(false);
                    sender.send(new PresenceInfoChangedMessage(cellID, Change.MUTED, false));
                    break;

                case CallStatus.STARTEDSPEAKING:
                    // ignore this indicator if the player is in a secret
                    // audio group
                    if (isInSecretAudioGroup(player)) {
                        return;
                    }

                    sender.send(new PresenceInfoChangedMessage(cellID, Change.SPEAKING, true));
                    break;

                case CallStatus.STOPPEDSPEAKING:
                    // ignore this indicator if the player is in a secret
                    // audio group
                    if (isInSecretAudioGroup(player)) {
                        return;
                    }

                    sender.send(new PresenceInfoChangedMessage(cellID, Change.SPEAKING, false));
                    break;
            }
        }

        private boolean isInSecretAudioGroup(Player player) {
            AudioGroup[] audioGroups = player.getAudioGroups();

            for (int i = 0; i < audioGroups.length; i++) {
                AudioGroupPlayerInfo info = audioGroups[i].getPlayerInfo(player);

                if (info.chatType == AudioGroupPlayerInfo.ChatType.SECRET) {
                    return true;
                }
            }

            return false;
        }

        public void removingObject() {
            AppContext.getDataManager().removeBinding(bindingName);
        }
    }

    // wrapper for PlayerInRangeListener that passes the listener on to
    // the real listener reference by a binding
    static class RangeListenerImpl implements PlayerInRangeListener, Serializable {
        private final String bindingName;

        public RangeListenerImpl(String bindingName) {
            this.bindingName = bindingName;
        }

        public void playerInRange(Player player, Player playerInRange,
                                  boolean isInRange)
        {
            // look up the binding in the data store
            try {
                PlayerInRangeListener listener =
                        (PlayerInRangeListener) AppContext.getDataManager().getBinding(bindingName);
                listener.playerInRange(player, playerInRange, isInRange);
            } catch (ObjectNotFoundException onfe) {
                logger.log(Level.WARNING, "Object not found for binding " +
                           bindingName);
                player.removePlayerInRangeListener(this);
            } catch (NameNotBoundException nnbe) {
                logger.log(Level.WARNING, "Object not found for binding " +
                           bindingName);
                player.removePlayerInRangeListener(this);
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final RangeListenerImpl other = (RangeListenerImpl) obj;
            if ((this.bindingName == null) ? (other.bindingName != null) :
                !this.bindingName.equals(other.bindingName))
            {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 89 * hash + (this.bindingName != null ? this.bindingName.hashCode() : 0);
            return hash;
        }
    }
}
