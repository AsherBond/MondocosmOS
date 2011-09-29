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
package org.jdesktop.wonderland.modules.xremwin.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.HashMap;
import java.math.BigInteger;
import java.net.ServerSocket;
import org.jdesktop.wonderland.modules.appbase.client.utils.clientsocket.ClientSocketListener;
import org.jdesktop.wonderland.modules.appbase.client.utils.clientsocket.MasterSocketSet;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellTransform;
import com.jme.math.Vector3f;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.SecretKey;

/**
 * The module in the master which broadcasts xremwin messages to slaves.
 *
 * @author deronj
 */
@ExperimentalAPI
class SlaveForwarder {

    private ServerProxyMaster serverProxy;
    private MasterSocketSet socketSet;
    private byte[] welcomeBuf = new byte[Proto.WELCOME_MESSAGE_SIZE];
    private byte[] controlRefusedBuf = new byte[Proto.CONTROLLER_STATUS_MESSAGE_SIZE];
    static boolean perfTestEnabled = false;
    static boolean perfTestExitOnCompletion = true;

    private final Map<Integer, String> clientIdToUserName = new HashMap<Integer, String>();
    private final Map<Integer, BigInteger> clientIdToSlaveId = new HashMap<Integer, BigInteger>();
    private final Map<BigInteger, Mac> slaveIdToSecret = new HashMap<BigInteger, Mac>();
    private final Map<BigInteger, Integer> slaveIdToCounter = new HashMap<BigInteger, Integer>();

    private int nextNewClientId = 1;

    public SlaveForwarder(ServerProxyMaster serverProxy, BigInteger sessionID, ServerSocket serverSocket)
            throws IOException {
        this.serverProxy = serverProxy;
        socketSet = new MasterSocketSet(sessionID, serverSocket, new MyListener());
        socketSet.start();
    }

    public void cleanup() {
        disconnect();
        serverProxy = null;
        AppXrw.logger.severe("SlaveForwarder cleaned up");
    }

    void disconnect() {
        if (socketSet != null) {
            socketSet.close();
            socketSet = null;
        }
        AppXrw.logger.severe("SlaveForwarder disconnected");
    }

    public void unicastSend(BigInteger slaveID, byte[] buf) {
        unicastSend(slaveID, buf, buf.length, false);
    }

    public void unicastSend(BigInteger slaveID, byte[] buf, boolean force) {
        unicastSend(slaveID, buf, buf.length, force);
    }

    public void unicastSend(BigInteger slaveID, byte[] buf, int len, boolean force) {
        try {
            socketSet.send(slaveID, buf, len, force);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void broadcastSend(byte[] buf) {
        broadcastSend(buf, buf.length);
    }

    public void broadcastSend(byte[] buf, int len) {
        try {
            socketSet.send(buf, len);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private int allocateClientId(String userName) {

        // Is there already a client id for this user name? If so, the user has
        // disconnected and is reconnecting. So reuse the already assigned id.

        synchronized (clientIdToUserName) {
            for (int clientId : clientIdToUserName.keySet()) {
                if (clientIdToUserName.get(clientId).equals(userName)) {
                    return clientId;
                }
            }
        }

        return nextNewClientId++;
    }

    public String clientIdToUserName(int clientId) {
        synchronized (clientIdToUserName) {
            return clientIdToUserName.get(clientId);
        }
    }

    void unicastWelcomeMessage(BigInteger slaveID, String userName) {
        new Welcomer(slaveID, userName).run();
    }

    private class Welcomer implements Runnable {

        private BigInteger slaveID;
        private String userName;

        private Welcomer(BigInteger slaveID, String userName) {
            this.slaveID = slaveID;
            this.userName = userName;
        }

        public void run() {
            int clientId = allocateClientId(userName);

            int n = 0;
            welcomeBuf[n++] = (byte) Proto.ServerMessageType.WELCOME.ordinal();
            welcomeBuf[n++] = 0;
            welcomeBuf[n++] = 0;
            welcomeBuf[n++] = 0;
            welcomeBuf[n++] = (byte) ((clientId >> 24) & 0xff);
            welcomeBuf[n++] = (byte) ((clientId >> 16) & 0xff);
            welcomeBuf[n++] = (byte) ((clientId >> 8) & 0xff);
            welcomeBuf[n++] = (byte) (clientId & 0xff);

            unicastSend(slaveID, welcomeBuf, true);

            syncSlaveWindowStateAll(slaveID);

            // Remember that we assigned this client id to this user
            synchronized (clientIdToUserName) {
                clientIdToUserName.put(clientId, userName);
            }

            // Remember that we assigned this client id to this slave ID
            // TODO: how does these get cleaned up?
            synchronized (clientIdToSlaveId) {
                clientIdToSlaveId.put(clientId, slaveID);
            }
        }

        // Enough to hold approximately ten lines
        private int syncPixelsBufMax = 1280 * 10 * 4;
        private byte[] syncBuf = new byte[62];
        private byte[] syncPixelsBuf = new byte[syncPixelsBufMax];

        // Send all known client window state to a slave
        private void syncSlaveWindowStateAll(BigInteger slaveID) {
            AppXrw appx = serverProxy.getApp();
            synchronized (appx.widToWindow) {
                Iterator it;

                //  First count the number of windows
                int numWins = 0;
                it = appx.widToWindow.values().iterator();
                while (it.hasNext()) {
                    WindowXrw win = (WindowXrw) it.next();
                    numWins++;
                }

                // Send the window count to the slave
                byte[] buf = new byte[4];
                encode(buf, 0, numWins);
                unicastSend(slaveID, buf, true);
                AppXrw.logger.info("numWins = " + numWins);

                // Then send the individual window states
                it = appx.widToWindow.values().iterator();
                while (it.hasNext()) {
                    WindowXrw win = (WindowXrw) it.next();
                    syncSlaveWindowState(slaveID, win);
                }

                // It is safe to write to a slave socket only when the master has enqueued 
                // all welcome message buffers. Note: this was a bug in 0.4: it was 
                // enabling the socket to accept other messages after the first window.
                // (Found my inspection, not testing).
                socketSet.setEnable(slaveID, true);
            }
        }

        private void syncSlaveWindowState(BigInteger slaveID, WindowXrw win) {
            AppXrw.logger.info("Enter syncSlaveWindowState: win = " + win.getWid());

            String controllingUser = win.getControllingUser();
            int controllingUserLen = (controllingUser != null)
                    ? controllingUser.length() : 0;

            AppXrw.logger.info("wid = " + win.getWid());
            AppXrw.logger.info("xy = " + win.getOffsetX() + " " + win.getOffsetY());
            AppXrw.logger.info("wh = " + win.getWidth() + " " + win.getHeight());
            AppXrw.logger.info("bw = " + win.getBorderWidth());
            AppXrw.logger.info("decorated = " + win.isDecorated());
            AppXrw.logger.info("showing = " + win.isVisibleApp());
            AppXrw.logger.info("controlling user = " + controllingUser);
            AppXrw.logger.info("zOrder = " + win.getZOrder());
            
            WindowXrw.Type type = win.getType();
            AppXrw.logger.info("type = " + type);
            WindowXrw parentWindow = (WindowXrw) win.getParent();
            int parentWid = WindowXrw.INVALID_WID; 
            if (parentWindow != null) {
                parentWid = parentWindow.getWid();
            }
            AppXrw.logger.info("parent wid = " + parentWid);

            // Get the user transform from the cell and extract the translation
            // from it and send this translation in the sync info. 
            // TODO: someday: We can get away with this only because we currently don't
            // support secondary rotations. When we finally do support secondary
            // rotations we'll need to modify the sync protocol to send the entire 
            // transform.
            CellTransform userTransformCell = win.getUserTransformCell();
            Vector3f userTranslation = userTransformCell.getTranslation(null);
            AppXrw.logger.info("userTranslation = " + userTranslation);

            // Send basic window attributes
            encode(syncBuf, 0, win.getWid());
            encode(syncBuf, 4, win.getOffsetX());
            encode(syncBuf, 8, win.getOffsetY());
            encode(syncBuf, 12, win.getWidth());
            encode(syncBuf, 16, win.getHeight());
            encode(syncBuf, 20, win.getBorderWidth());
            encode(syncBuf, 24, controllingUserLen);
            encode(syncBuf, 28, win.getZOrder());

            // Placeholder: this used to be the rotation around y axis
            encode(syncBuf, 32, 0f);

            encode(syncBuf, 36, userTranslation.x);
            encode(syncBuf, 40, userTranslation.y);
            encode(syncBuf, 44, userTranslation.z);

            /* TODO: 0.4 protocol:
            encode(syncBuf, 48, win.getTransientFor().getWid());
             */
            encode(syncBuf, 48, 0);
            encode(syncBuf, 52, type.ordinal());
            encode(syncBuf, 56, parentWid);

            syncBuf[60] = (byte) (win.isDecorated() ? 1 : 0);
            syncBuf[61] = (byte) (win.isVisibleApp() ? 1 : 0);

            unicastSend(slaveID, syncBuf, true);
            //AppXrw.logger.warning("Call unicastMessage with " + syncBuf.length + " bytes");
            //print10bytes(syncBuf);

            if (controllingUserLen > 0) {
                unicastSend(slaveID, controllingUser.getBytes(), true);
            }

            // Send window contents. Note that if there are pending writes to
            // the window this may block until the next frame tick.
            win.syncSlavePixels(slaveID);
        }
    }

    private static void encode(byte[] buf, int startIdx, short value) {
        buf[startIdx + 0] = (byte) ((value >> 8) & 0xff);
        buf[startIdx + 1] = (byte) ((value) & 0xff);
    }

    private static void encode(byte[] buf, int startIdx, int value) {
        buf[startIdx] = (byte) ((value >> 24) & 0xff);
        buf[startIdx + 1] = (byte) ((value >> 16) & 0xff);
        buf[startIdx + 2] = (byte) ((value >> 8) & 0xff);
        buf[startIdx + 3] = (byte) ((value) & 0xff);
    }

    private static void encode(byte[] buf, int startIdx, float value) {
        ByteBuffer byteBuf = ByteBuffer.allocate(4);
        byteBuf.putFloat(value);
        byteBuf.rewind();
        byte[] bytes = new byte[4];
        byteBuf.get(bytes);
        System.arraycopy(bytes, 0, buf, startIdx, 4);
    }

    private class MyListener implements ClientSocketListener {

        public void receivedMessage(BigInteger otherClientID, byte[] message) {
            int msgCode = (int) message[0];
            Proto.ClientMessageType msgType = Proto.ClientMessageType.values()[msgCode];

            // See this is the hello message from the slave
            if (msgType == Proto.ClientMessageType.HELLO) {
                // set up security for this user
                if (!addUser(otherClientID, message)) {
                    AppXrw.logger.warning("Hello reject from " + otherClientID);
                    return;
                }

                int strLen = (int)(message[2] << 8) | (int)message[3];
                if (strLen <= 0) {
                    AppXrw.logger.warning("Invalid slave user name string length");
                    return;
                }
                byte[] userNameBuf = new byte[strLen];
                System.arraycopy(message, 4, userNameBuf, 0, strLen);
                String userName = new String(userNameBuf);

                AppXrw.logger.info("Received hello message from slave " + otherClientID +
                               ", userName = " + userName);
                serverProxy.addIncomingSlaveHelloMessage(otherClientID, userName);
                return;
            }

            // verify the message to make sure it is valid before forwarding
            // it along
            if (!verify(otherClientID, msgType, message)) {
                AppXrw.logger.warning("Message reject from " + otherClientID);
                socketSet.close(otherClientID);
            }

            // if this is a take control message, perform a security
            // check before allowing it.
            if (msgType == Proto.ClientMessageType.TAKE_CONTROL &&
                    !serverProxy.checkTakeControl(otherClientID))
            {
                AppXrw.logger.warning("Blocked take control request from " +
                        "unauthorized user " + otherClientID);

                // get the client id
                int clientID = readInt(message, Proto.ClientMessageType.TAKE_CONTROL.clientIdIndex());
                sendTakeControlFailure(otherClientID, clientID);
                return;
            }

            // XXX HACK XXX we also do a take control permission check when
            // the user attempts to destroy a window.  This is because for
            // some reason the X server doesn't check that only a user in
            // control can destroy a window.  This check does not guarantee
            // that the user has control, but at least guarantees the user
            // could potentially take control.  This should be fixed in the
            // X server.
            if (msgType == Proto.ClientMessageType.DESTROY_WINDOW &&
                    !serverProxy.checkTakeControl(otherClientID))
            {
                AppXrw.logger.warning("Blocked destroy window request from " +
                        "unauthorized user " + otherClientID);
                return;
            }
          
            // Forward event to the xremwin server, minus the signature
            try {
                serverProxy.write(message, 0, message.length - Proto.SIGNATURE_SIZE);
            } catch (IOException ex) {
                AppXrw.logger.warning("IOException during write to xremwin server");
            }
        }

        public void otherClientHasLeft(BigInteger otherClientID) {
            AppXrw.logger.info("Slave has disconnected: " + otherClientID);
        
            // clean up
            synchronized (slaveIdToCounter) {
                slaveIdToCounter.remove(otherClientID);
            }
            synchronized (slaveIdToSecret) {
                slaveIdToSecret.remove(otherClientID);
            }
        }
    }

    /**
     * Send a take control failure message
     * @param slaveID the id of the client to send to
     * @param clientID the clientID
     */
    private void sendTakeControlFailure(BigInteger slaveID, int clientId) {
        int n = 0;
        controlRefusedBuf[n++] = (byte) Proto.ServerMessageType.CONTROLLER_STATUS.ordinal();
        controlRefusedBuf[n++] = (byte) Proto.ControllerStatus.REFUSED.ordinal();
        controlRefusedBuf[n++] = 0;
        controlRefusedBuf[n++] = 0;
        controlRefusedBuf[n++] = (byte) ((clientId >> 24) & 0xff);
        controlRefusedBuf[n++] = (byte) ((clientId >> 16) & 0xff);
        controlRefusedBuf[n++] = (byte) ((clientId >> 8) & 0xff);
        controlRefusedBuf[n++] = (byte) (clientId & 0xff);

        unicastSend(slaveID, controlRefusedBuf, true);
    }

    /**
     * Add a record for a new user.
     * @param slaveID the id of the slave that connected
     * @param message the initial message from this user
     * @return true if the message is a valid message from the given
     * Wonderland client, or false if not.
     */
    private boolean addUser(BigInteger slaveID, byte[] message) {
        // get the secret for this user
        SecretKey secret = serverProxy.getSecret(slaveID);
        if (secret == null) {
            return false;
        }

        // create a Mac based on that secret
        Mac mac;
        try {
            mac = Mac.getInstance("HmacSHA1");
            mac.init(secret);
        } catch (NoSuchAlgorithmException nsae) {
            // shouldn't happen
            throw new IllegalStateException(nsae);
        } catch (InvalidKeyException ike) {
            // shouldn't happen
            throw new IllegalStateException(ike);
        }

        // remember the secret
        synchronized (slaveIdToSecret) {
            slaveIdToSecret.put(slaveID, mac);
        }

        // get the initial counter for this user
        int counterIdx = message.length - 24;
        int counter = readInt(message, counterIdx);
        synchronized (slaveIdToCounter) {
            slaveIdToCounter.put(slaveID, counter);
        }

        // now that we have populated those tables, go ahead and verify the
        // message
        return verify(slaveID, Proto.ClientMessageType.HELLO, message);
    }

    /**
     * Verify that a message's signature matches.  Also verify that the
     * any embedded client IDs are valid.
     * @param slaveID the id of the slave that sent this message
     * @param msgType the type of message
     * @param message the message to verify
     * @return true if the signature matches, or false if not
     */
    private boolean verify(BigInteger slaveID, Proto.ClientMessageType msgType, 
                           byte[] message)
    {
        // first, verify the signature matches the given slave ID
        Mac mac;
        synchronized (slaveIdToSecret) {
            mac = slaveIdToSecret.get(slaveID);
        }
        if (mac == null) {
            AppXrw.logger.warning("Rejected message from " + slaveID + 
                                  ".  MAC not found.");
            return false;
        }
        mac.update(message, 0, message.length - 20);

        // copy the signature into its own array for processing
        byte[] signature = new byte[20];
        System.arraycopy(message, message.length - 20, signature, 0, 20);

        // compare the signatures
        if (!Arrays.equals(signature, mac.doFinal())) {
            AppXrw.logger.warning("Rejected message from " + slaveID + 
                                  ".  Signature mismatch.");
            return false;
        }

        // now extract the client ID (if any) and compare that to the map
        // from slave ids to client IDs
        int idx = msgType.clientIdIndex();
        if (idx != -1) {
            int clientID = readInt(message, idx);
            
            BigInteger correctID;
            synchronized (clientIdToSlaveId) {
                correctID = clientIdToSlaveId.get(clientID);
            }

            if (correctID == null || !correctID.equals(slaveID)) {
                AppXrw.logger.warning("Rejected message from " + slaveID + 
                                  " type " + msgType + ". Client ID mismatch.");
                return false;
            }
        }

        // finally, check the counter for this message, to make sure it
        // is the next counter based on the last one we received from this
        // slave.  This prevents replay attacks.
        int counterIdx = message.length - 24;
        int counter = readInt(message, counterIdx);
        
        Integer correctCounter;
        synchronized (slaveIdToCounter) {
            correctCounter = slaveIdToCounter.get(slaveID);
            
            // update the counter while we have the lock.  If we reject the 
            // counter below, the client will be disconnected anyway.
            slaveIdToCounter.put(slaveID, correctCounter + 1);
        }

        if (correctCounter == null || (correctCounter.intValue() != counter)) {
            AppXrw.logger.warning("Rejected message from " + slaveID + 
                                  " type " + msgType + ". Counter mismatch.");
            return false;
        }
        
        // everything checked out!
        return true;
    }

    private int readInt(byte[] buffer, int offset) {
        return ((buffer[offset++] & 0xff) << 24) |
               ((buffer[offset++] & 0xff) << 16) |
               ((buffer[offset++] & 0xff) << 8) |
               (buffer[offset++] & 0xff);
    }

    // For Debug
    private static void print10bytes(byte[] bytes) {
        int n = (bytes.length > 10) ? 10 : bytes.length;
        for (int i = 0; i < n; i++) {
            System.err.print(Integer.toHexString(bytes[i] & 0xff) + " ");
        }
        System.err.println();
    }
}
