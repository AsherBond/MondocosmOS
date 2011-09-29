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
package org.jdesktop.wonderland.modules.sas.provider;

import java.util.List;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.comms.BaseConnection;
import org.jdesktop.wonderland.common.comms.ConnectionType;
import org.jdesktop.wonderland.common.messages.Message;
import org.jdesktop.wonderland.common.messages.MessageList;
import org.jdesktop.wonderland.modules.sas.common.SasProviderConnectionType;
import org.jdesktop.wonderland.modules.sas.common.SasProviderLaunchMessage;
import org.jdesktop.wonderland.modules.sas.common.SasProviderLaunchStatusMessage;
import org.jdesktop.wonderland.modules.sas.common.SasProviderAppStopMessage;
import org.jdesktop.wonderland.modules.sas.common.SasProviderAppExittedMessage;
import org.jdesktop.wonderland.common.messages.MessageID;
import org.jdesktop.wonderland.modules.sas.common.SasProviderReadyMessage;

/**
 * The SAS provider client.
 *
 * @author deronj
 */

public class SasProviderConnection extends BaseConnection {

    private static final Logger logger = Logger.getLogger(SasProviderConnection.class.getName());
    
    /** The listener for messages from the server over the SAS Provider connection. */
    private SasProviderConnectionListener listener;

    /**
     * Create an instance of SasProviderConnection.
     * @param listener The listener for messages from the server over the SAS Provider connection.
     */
    public SasProviderConnection (SasProviderConnectionListener listener) {
        this.listener = listener;
    }
    
    /**
     * Get the type of client
     * @return CellClientType.CELL_CLIENT_TYPE
     */
    public ConnectionType getConnectionType() {
        return SasProviderConnectionType.CLIENT_TYPE;
    }

    /**
     * Handle a message from the server
     * @param message the message to handle
     */
    public void handleMessage(Message message) {
        if (message instanceof MessageList) {
            List<Message> list = ((MessageList)message).getMessages();
            for(Message m : list)
                handleMessage(m);
            return;
        }
        
        logger.warning("Received message from server: " + message);

        if (message instanceof SasProviderLaunchMessage) {
            SasProviderLaunchMessage msg = (SasProviderLaunchMessage) message;

            logger.warning("Received launch message from server");
            logger.warning("execCap = " + msg.getExecutionCapability());
            logger.warning("appName = " + msg.getAppName());
            logger.warning("command = " + msg.getCommand());

            if (listener == null) {
                logger.warning("No provider listener is registered.");
                sendResponse(msg.getMessageID(), null);
            }

            logger.warning("Attempting to launch X app");
            String connInfo = listener.launch(msg.getAppName(), msg.getCommand(), 
                                              this, msg.getMessageID(), msg.getCellID());
            logger.warning("connInfo = " + connInfo);
            sendResponse(msg.getMessageID(), connInfo);

        } else if (message instanceof SasProviderAppStopMessage) {
            SasProviderAppStopMessage msg = (SasProviderAppStopMessage) message;

            logger.warning("Received app stop message from server");
            logger.warning("launchMsgID= " + msg.getLaunchMessageID());

            listener.appStop(this, msg.getLaunchMessageID());

        } else {
            throw new RuntimeException("Unexpected message type "+message.getClass().getName());
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnected() {
        listener = null;
    }
    
    /**
     * Notify the connection handler that this provider is ready to receive
     * messages.
     */
    public void notifyProviderReady() {
        send(new SasProviderReadyMessage());
    }

    /**
     * Respond to a launch request message.
     */
    private void sendResponse (MessageID launchMessageID, String connInfo) {
        
        SasProviderLaunchStatusMessage.LaunchStatus status;
        if (connInfo == null) {
            status = SasProviderLaunchStatusMessage.LaunchStatus.FAIL;
        } else {
            status = SasProviderLaunchStatusMessage.LaunchStatus.SUCCESS;
        }

        logger.info("Respond with status message = " + status);
        logger.info("status = " + status);
        logger.info("launchMessageID = " + launchMessageID);
        logger.info("connInfo = " + connInfo);

        try {
            SasProviderLaunchStatusMessage msg = 
                new SasProviderLaunchStatusMessage(status, launchMessageID, connInfo);
            send(msg);
            logger.info("sent success message");
        } catch (Exception ex) {
            logger.warning("Message send error while responding to launch message, msgID = " + 
                           launchMessageID);
        }
    }
    
    /**
     * Called when an app exits.
     * @param launchMessageID The message ID of the message that launched the app.
     * @param exitValue The exit value of the app.
     */
    public void appExitted (MessageID launchMessageID, int exitValue) {
        logger.info("App exitted.");
        logger.info("launchMessageID = " + launchMessageID);
        logger.info("exitValue = " + exitValue);

        try {
            SasProviderAppExittedMessage msg = 
                new SasProviderAppExittedMessage(launchMessageID, exitValue);
            send(msg);
        } catch (Exception ex) {
            logger.warning("Message send error sending app exitted message, msgID = " + 
                           launchMessageID);
        }
    }
}
