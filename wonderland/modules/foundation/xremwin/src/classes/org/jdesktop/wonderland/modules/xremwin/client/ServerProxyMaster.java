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
import java.io.EOFException;
import java.net.SocketTimeoutException;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.CreateWindowMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.DestroyWindowMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.ShowWindowMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.ConfigureWindowMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.PositionWindowMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.RestackWindowMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.WindowSetDecoratedMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.WindowSetUserDisplMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.WindowSetRotateYMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.WindowSetBorderWidthMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.DisplayPixelsMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.CopyAreaMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.ControllerStatusMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.SetWindowTitleMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.SlaveCloseWindowMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.ServerMessageType;
import java.util.LinkedList;
import com.jme.math.Vector3f;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.logging.Level;
import javax.crypto.SecretKey;
import org.jdesktop.wonderland.client.comms.ClientConnection;
import org.jdesktop.wonderland.client.comms.ConnectionFailureException;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.client.comms.WonderlandSession;

// TODO: 0.4 protocol: temporarily insert
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.DisplayCursorMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.MoveCursorMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.ShowCursorMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.common.XrwSecurityConnectionType;

/**
 * An implementation of a ServerProxy broadcasts to slave clients
 * every message received from the Xremwin server. In addition it
 * multiplexes events received by slave clients and sends these
 * to the Xremwin server.
 *
 * @author deronj
 */
@ExperimentalAPI
class ServerProxyMaster extends ServerProxyMasterSocket {

    static final int MASTER_CLIENT_ID = 0;
    private static final int CREATE_WINDOW_MESSAGE_SIZE = 20;
    private static final int DESTROY_WINDOW_MESSAGE_SIZE = 8;

    // TODO: 0.4 protocol: temporarily change
    //private static final int SHOW_WINDOW_MESSAGE_SIZE = 12;
    private static final int SHOW_WINDOW_MESSAGE_SIZE = 8;

    private static final int CONFIGURE_WINDOW_MESSAGE_SIZE = 28;
    private static final int POSITION_WINDOW_MESSAGE_SIZE = 16;
    private static final int RESTACK_WINDOW_MESSAGE_SIZE = 16;
    private static final int WINDOW_SET_DECORATED_MESSAGE_SIZE = 8;
    private static final int WINDOW_SET_BORDER_WIDTH_MESSAGE_SIZE = 8;
    private static final int WINDOW_SET_USER_DISPL_MESSAGE_SIZE = 24;
    private static final int WINDOW_SET_ROTATE_Y_MESSAGE_SIZE = 16;
    private static final int DISPLAY_PIXELS_MESSAGE_SIZE = 16;
    private static final int COPY_AREA_MESSAGE_SIZE = 32;
    private static final int CONTROLLER_STATUS_MESSAGE_SIZE = 8;
    private static final int SET_WINDOW_TITLE_MESSAGE_SIZE = 12;
    private static final int SLAVE_CLOSE_WINDOW_MESSAGE_SIZE = 12;
    private static final int BEEP_MESSAGE_SIZE = 1;
    private static final int INTEGER_SIZE = 4;

    // TODO: 0.4 protocol: temporarily insert
    private static final int DISPLAY_CURSOR_MESSAGE_SIZE = 12;
    private static final int MOVE_CURSOR_MESSAGE_SIZE = 16;
    private static final int SHOW_CURSOR_MESSAGE_SIZE = 2;
    private SlaveForwarder sf;
    private byte lastMsgCode;
    private DataBufferQueue.CurrentBuffer curBuf = new DataBufferQueue.CurrentBuffer();
    private byte[] createWindowBuf = new byte[CREATE_WINDOW_MESSAGE_SIZE];
    private byte[] destroyWindowBuf = new byte[DESTROY_WINDOW_MESSAGE_SIZE];
    private byte[] showWindowBuf = new byte[SHOW_WINDOW_MESSAGE_SIZE];
    private byte[] configureWindowBuf = new byte[CONFIGURE_WINDOW_MESSAGE_SIZE];
    private byte[] positionWindowBuf = new byte[POSITION_WINDOW_MESSAGE_SIZE];
    private byte[] restackWindowBuf = new byte[RESTACK_WINDOW_MESSAGE_SIZE];
    private byte[] windowSetDecoratedBuf = new byte[WINDOW_SET_DECORATED_MESSAGE_SIZE];
    private byte[] windowSetBorderWidthBuf = new byte[WINDOW_SET_BORDER_WIDTH_MESSAGE_SIZE];
    private byte[] winSetUserDisplBuf = new byte[Proto.WINDOW_SET_USER_DISPL_MESSAGE_SIZE];
    private byte[] winSetRotateYBuf = new byte[Proto.WINDOW_SET_ROTATE_Y_MESSAGE_SIZE];
    private byte[] displayPixelsBuf = new byte[DISPLAY_PIXELS_MESSAGE_SIZE];
    private byte[] copyAreaBuf = new byte[COPY_AREA_MESSAGE_SIZE];
    private byte[] controllerStatusBuf = new byte[CONTROLLER_STATUS_MESSAGE_SIZE];
    private byte[] setWindowTitleBuf = new byte[SET_WINDOW_TITLE_MESSAGE_SIZE];
    private byte[] slaveCloseWindowBuf = new byte[SLAVE_CLOSE_WINDOW_MESSAGE_SIZE];
    private byte[] beepBuf = new byte[BEEP_MESSAGE_SIZE];
    private byte[] integerBuf = new byte[INTEGER_SIZE];
    private byte[] userNameBuf = new byte[Proto.CONTROLLING_USER_NAME_MESSAGE_SIZE];
    private byte[] setPopupParentBuf = new byte[Proto.SET_POPUP_PARENT_MESSAGE_SIZE];

    // TODO: 0.4 protocol: temporarily insert
    private byte[] displayCursorBuf = new byte[DISPLAY_CURSOR_MESSAGE_SIZE];
    private byte[] moveCursorBuf = new byte[MOVE_CURSOR_MESSAGE_SIZE];
    private byte[] showCursorBuf = new byte[SHOW_CURSOR_MESSAGE_SIZE];

    // The client id and the user name of the last SLAVE_HELLO message received.
    private BigInteger connectingSlaveID;
    private String connectingUserName;

    /** The CellID this app is associated with */
    private CellID cellID;

    /** This app's Wonderland session. */
    protected WonderlandSession session;
    /** The server socket to which slaves should connect. */
    protected ServerSocket serverSocket;
    /** Which user is currently controlling the app */
    private String controllingUserName = null;

    private class SlaveHelloMessage {

        BigInteger slaveID;
        String userName;

        SlaveHelloMessage(BigInteger slaveID, String userName) {
            this.slaveID = slaveID;
            this.userName = userName;
        }
    }

    // The incoming message queue. A message can either come from 
    // the server or be a hello from a connecting slave.
    private final LinkedList<SlaveHelloMessage> slaveHellos = new LinkedList<SlaveHelloMessage>();

    /** The client to which this proxy belongs. */
    private ClientXrwMaster client;

    /** 
     * Create a new instance of ServerXrwMaster.
     * @param session This app's Wonderland session.
     * @param cellID The id of the cell this app is associated with.
     * @param masterHost The master host name (this host).
     * @param wsDisplayNum The X11 display number used by the window system for this app.
     * @param serverSocket The server socket to which slaves should connect.
     * @param client The client to which this proxy belongs.
     */
    public ServerProxyMaster(WonderlandSession session, CellID cellID, String masterHost,
                             int wsDisplayMaster, ServerSocket serverSocket, ClientXrwMaster client) {
        super(masterHost, wsDisplayMaster);
        this.session = session;
        this.serverSocket = serverSocket;
        this.client = client;
        this.cellID = cellID;
    }

    public void connect() throws IOException {
        setClientId(MASTER_CLIENT_ID);

        establishConnection();

        sf = new SlaveForwarder(this, session.getID(), serverSocket);
    }

    @Override
    public void disconnect() {
        super.disconnect();

        try {
            if (sf != null) {
                sf.disconnect();
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        AppXrw.logger.severe("ServerProxyMaster disconnected");
    }

    @Override
    public void cleanup() {
        disconnect();

        super.cleanup();

        if (sf != null) {
            sf.cleanup();
            sf = null;
        }

        curBuf = null;

        AppXrw.logger.severe("ServerProxyMaster cleaned up");
    }

    /**
     * Return the app of this proxy.
     */
    public AppXrw getApp () {
        return client.getApp();
    }

    /**
     * Used by the Client to read the next incoming message.
     */
    @Override
    public ServerMessageType getMessageType() {

        // Loop until we get a message from either server or a
        // connecting slave
        while (true) {

            // First poll for whether there are any slaves who want to connect.
            // Process these first. (Note: this is a poll, not a wait)
            // TODO: it probably is a good idea to put a limit on the number
            // of slave hellos we service here
            SlaveHelloMessage msg = null;
            synchronized (slaveHellos) {
                while (slaveHellos.size() > 0) {
                    msg = slaveHellos.getFirst();
                    slaveHellos.removeFirst();
                    connectingSlaveID = msg.slaveID;
                    connectingUserName = msg.userName;
                    return ServerMessageType.SLAVE_HELLO;
                }
            }

            // After servicing connectin slaves, try read a message from the server.
            try {
                ServerMessageType type = socketReadMessageType();
                return type;
            } catch (IOException e) {
                if (e instanceof SocketTimeoutException) {
                    // No message yet from server. Keep looping.
                } else {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Returns the session id of the connecting slave that sent
     * a SLAVE_HELLO message. Only call this when getMessageType
     * returns SLAVE_HELLO.
     */
    public BigInteger getConnectingSlaveID() {
        return connectingSlaveID;
    }

    /**
     * Returns the user name of the connecting slave that sent
     * a SLAVE_HELLO message. Only call this when getMessageType
     * returns SLAVE_HELLO.
     */
    public String getConnectingUserName() {
        return connectingUserName;
    }

    public ServerMessageType socketReadMessageType() throws IOException {
        try {
            lastMsgCode = (byte) in.readUnsignedByte();
            int msgCode = (int) lastMsgCode;
            return ServerMessageType.values()[msgCode];
        } catch (SocketException e) {
            // TODO: for now, ignore socket close errors. They occur
            // normally when the X server goes away on application death.
            // Later on we may want to find a better way to handle this.
            return ServerMessageType.SERVER_DISCONNECT;
        } catch (EOFException e) {
            return ServerMessageType.SERVER_DISCONNECT;
        }
    }

    public void addIncomingSlaveHelloMessage(BigInteger slaveID, String userName) {
        synchronized (slaveHellos) {
            slaveHellos.addLast(new SlaveHelloMessage(slaveID, userName));
        }
    }

    public void sendWelcomeMessageToSlave(BigInteger slaveID, String userName) {
        sf.unicastWelcomeMessage(slaveID, userName);
    }

    /**
     * Used by the slave forwarder to get the shared secret associated with
     * a given user.
     * @param clientID the id of the client to get a secret for
     * @return the client's secret, or null if the given client doesn't have
     * permission to access the app.
     */
    SecretKey getSecret(BigInteger clientID) {
        XrwSecurityConnection conn = session.getConnection(XrwSecurityConnectionType.TYPE,
                                                           XrwSecurityConnection.class);
        return conn.getSecret(clientID, cellID);
    }

    /**
     * Used by the slave forwarder to check if a user has permission to take
     * control
     * @param clientID the id of the client trying to take control
     * @return true if the client has permission to take control, or false if
     * not
     */
    boolean checkTakeControl(BigInteger clientID) {
        XrwSecurityConnection conn = session.getConnection(XrwSecurityConnectionType.TYPE,
                                                           XrwSecurityConnection.class);
        return conn.checkTakeControl(clientID, cellID);
    }

    @Override
    public void getData(CreateWindowMsgArgs msgArgs) {
        try {
            createWindowBuf[0] = lastMsgCode;
            in.readFully(createWindowBuf, 1, createWindowBuf.length - 1);
            sf.broadcastSend(createWindowBuf);

            curBuf.setBuffer(createWindowBuf, 1);
            msgArgs.decorated = curBuf.nextByte() != 0;
            msgArgs.borderWidth = curBuf.nextShort();
            msgArgs.wid = curBuf.nextInt();
            msgArgs.x = curBuf.nextShort();
            msgArgs.y = curBuf.nextShort();
            msgArgs.wAndBorder = curBuf.nextInt();
            msgArgs.hAndBorder = curBuf.nextInt();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void getData(DestroyWindowMsgArgs msgArgs) {
        try {
            destroyWindowBuf[0] = lastMsgCode;
            in.readFully(destroyWindowBuf, 1, destroyWindowBuf.length - 1);
            sf.broadcastSend(destroyWindowBuf);

            curBuf.setBuffer(destroyWindowBuf, 4);
            msgArgs.wid = curBuf.nextInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void getData(ShowWindowMsgArgs msgArgs) {
        try {
            showWindowBuf[0] = lastMsgCode;
            in.readFully(showWindowBuf, 1, showWindowBuf.length - 1);
            sf.broadcastSend(showWindowBuf);

            curBuf.setBuffer(showWindowBuf, 1);
            msgArgs.show = curBuf.nextByte() != 0;

            // TODO: 0.4 protocol: skip transient and pad (ignore transient for now)
            // TODO: 0.5 protocol: skip pad
            curBuf.skip(2);

            msgArgs.wid = curBuf.nextInt();

            // TODO: 0.5 protocol: not yet
            //msgArgs.transientFor = curBuf.nextInt();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void getData(ConfigureWindowMsgArgs msgArgs) {
        try {
            configureWindowBuf[0] = lastMsgCode;
            in.readFully(configureWindowBuf, 1, configureWindowBuf.length - 1);
            sf.broadcastSend(configureWindowBuf);

            curBuf.setBuffer(configureWindowBuf, 4);

            msgArgs.clientId = curBuf.nextInt();

            msgArgs.wid = curBuf.nextInt();

            msgArgs.x = curBuf.nextShort();
            msgArgs.y = curBuf.nextShort();

            msgArgs.wAndBorder = curBuf.nextInt();

            msgArgs.hAndBorder = curBuf.nextInt();

            msgArgs.sibid = curBuf.nextInt();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void getData(PositionWindowMsgArgs msgArgs) {
        try {
            positionWindowBuf[0] = lastMsgCode;
            in.readFully(positionWindowBuf, 1, positionWindowBuf.length - 1);
            sf.broadcastSend(positionWindowBuf);

            curBuf.setBuffer(positionWindowBuf, 4);
            msgArgs.clientId = curBuf.nextInt();
            msgArgs.wid = curBuf.nextInt();
            msgArgs.x = curBuf.nextShort();
            msgArgs.y = curBuf.nextShort();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void getData(RestackWindowMsgArgs msgArgs) {
        try {
            restackWindowBuf[0] = lastMsgCode;
            in.readFully(restackWindowBuf, 1, restackWindowBuf.length - 1);
            sf.broadcastSend(restackWindowBuf);

            curBuf.setBuffer(restackWindowBuf, 4);
            msgArgs.clientId = curBuf.nextInt();
            msgArgs.wid = curBuf.nextInt();
            msgArgs.sibid = curBuf.nextInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void getData(WindowSetDecoratedMsgArgs msgArgs) {
        try {
            windowSetDecoratedBuf[0] = lastMsgCode;
            in.readFully(windowSetDecoratedBuf, 1, windowSetDecoratedBuf.length - 1);
            sf.broadcastSend(windowSetDecoratedBuf);

            curBuf.setBuffer(windowSetDecoratedBuf, 1);
            msgArgs.decorated = (curBuf.nextByte() == 1) ? true : false;
            curBuf.skip(2);
            msgArgs.wid = curBuf.nextInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void getData(WindowSetBorderWidthMsgArgs msgArgs) {
        try {
            windowSetBorderWidthBuf[0] = lastMsgCode;
            in.readFully(windowSetBorderWidthBuf, 1, windowSetBorderWidthBuf.length - 1);
            sf.broadcastSend(windowSetBorderWidthBuf);

            curBuf.setBuffer(windowSetBorderWidthBuf, 2);
            msgArgs.borderWidth = curBuf.nextShort();
            msgArgs.wid = curBuf.nextInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void getData(WindowSetUserDisplMsgArgs msgArgs) {
        try {
            winSetUserDisplBuf[0] = lastMsgCode;
            in.readFully(winSetUserDisplBuf, 1, winSetUserDisplBuf.length - 1);
            sf.broadcastSend(winSetUserDisplBuf);

            curBuf.setBuffer(winSetUserDisplBuf, 4);
            msgArgs.clientId = curBuf.nextInt();
            msgArgs.wid = curBuf.nextInt();
            int ix = curBuf.nextInt();
            int iy = curBuf.nextInt();
            int iz = curBuf.nextInt();
            msgArgs.userDispl = new Vector3f(Float.intBitsToFloat(ix),
                    Float.intBitsToFloat(iy),
                    Float.intBitsToFloat(iz));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void getData(WindowSetRotateYMsgArgs msgArgs) {
        try {
            winSetRotateYBuf[0] = lastMsgCode;
            in.readFully(winSetRotateYBuf, 1, winSetRotateYBuf.length - 1);
            sf.broadcastSend(winSetRotateYBuf);

            curBuf.setBuffer(winSetRotateYBuf, 4);
            msgArgs.clientId = curBuf.nextInt();
            msgArgs.wid = curBuf.nextInt();
            int iroty = curBuf.nextInt();
            msgArgs.roty = Float.intBitsToFloat(iroty);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void getData(DisplayPixelsMsgArgs msgArgs) {
        try {
            displayPixelsBuf[0] = lastMsgCode;
            in.readFully(displayPixelsBuf, 1, displayPixelsBuf.length - 1);
            //System.err.print("DP: ");
            //print10bytes(displayPixelsBuf);

            curBuf.setBuffer(displayPixelsBuf, 1);
            int encodingCode = curBuf.nextByte();
            msgArgs.encoding = Proto.PixelEncoding.values()[encodingCode];

            msgArgs.x = curBuf.nextShort();
            msgArgs.wid = curBuf.nextInt();
            msgArgs.y = curBuf.nextShort();
            msgArgs.w = curBuf.nextShort();
            msgArgs.h = curBuf.nextShort();

            sf.broadcastSend(displayPixelsBuf);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void getData(CopyAreaMsgArgs msgArgs) {
        try {
            copyAreaBuf[0] = lastMsgCode;
            in.readFully(copyAreaBuf, 1, copyAreaBuf.length - 1);

            curBuf.setBuffer(copyAreaBuf, 4);
            msgArgs.wid = curBuf.nextInt();
            msgArgs.srcX = curBuf.nextInt();
            msgArgs.srcY = curBuf.nextInt();
            msgArgs.width = curBuf.nextInt();
            msgArgs.height = curBuf.nextInt();
            msgArgs.dstX = curBuf.nextInt();
            msgArgs.dstY = curBuf.nextInt();

            sf.broadcastSend(copyAreaBuf);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void getData(ControllerStatusMsgArgs msgArgs) {
        try {
            controllerStatusBuf[0] = lastMsgCode;
            in.readFully(controllerStatusBuf, 1, controllerStatusBuf.length - 1);
            sf.broadcastSend(controllerStatusBuf);

            curBuf.setBuffer(controllerStatusBuf, 1);
            int status = (int) curBuf.nextByte();
            msgArgs.status = Proto.ControllerStatus.values()[status];

            curBuf.skip(2);

            msgArgs.clientId = curBuf.nextInt();

            if (msgArgs.status == Proto.ControllerStatus.GAINED) {
                if (msgArgs.clientId == -1) {
                    // Nobody has control
                    controllingUserName = null;
                } else if (msgArgs.clientId == 0) {
                    // Master
                    controllingUserName = session.getUserID().getUsername();
                } else {
                    // Slaves
                    controllingUserName = sf.clientIdToUserName(msgArgs.clientId);
                }
                broadcastControllingUserNameToSlaves(controllingUserName);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Returns null if no user has control
    public String getControllingUser() {
        return controllingUserName;
    }

    // TODO: 0.4 protocol: temporarily insert
    public void getData(DisplayCursorMsgArgs msg) {
        try {
            displayCursorBuf[0] = lastMsgCode;
            in.readFully(displayCursorBuf, 1, displayCursorBuf.length - 1);
            // TODO: don't send cursor to slave: sf.broadcastMessage(displayCursorBuf);

            curBuf.setBuffer(displayCursorBuf, 4);
            msg.width = curBuf.nextShort();
            msg.height = curBuf.nextShort();
            msg.xhot = curBuf.nextShort();
            msg.yhot = curBuf.nextShort();

            int numPixels = msg.width * msg.height;
            int numBytes = numPixels * 4;
            msg.pixels = new int[numPixels];
            byte[] imageBuf = new byte[numBytes];

            in.readFully(imageBuf, 0, numBytes);
            curBuf.setBuffer(imageBuf, 0);
            for (int i : msg.pixels) {
                msg.pixels[i] = curBuf.nextInt();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO: 0.4 protocol: temporarily insert
    public void getData(MoveCursorMsgArgs msg) {
        try {
            moveCursorBuf[0] = lastMsgCode;
            in.readFully(moveCursorBuf, 1, moveCursorBuf.length - 1);
            // TODO: don't send cursor to slave: sf.broadcastMessage(moveCursorBuf);

            curBuf.setBuffer(moveCursorBuf, 4);
            msg.wid = curBuf.nextInt();
            msg.x = curBuf.nextInt();
            msg.y = curBuf.nextInt();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO: 0.4 protocol: temporarily insert
    public void getData(ShowCursorMsgArgs msg) {
        try {
            showCursorBuf[0] = lastMsgCode;
            in.readFully(showCursorBuf, 1, showCursorBuf.length - 1);
            // TODO: don't send cursor to slave: sf.broadcastMessage(showCursorBuf);

            curBuf.setBuffer(showCursorBuf, 1);
            msg.show = (curBuf.nextByte() == 1) ? true : false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void getData(SetWindowTitleMsgArgs msgArgs) {
        try {
            setWindowTitleBuf[0] = lastMsgCode;
            in.readFully(setWindowTitleBuf, 1, setWindowTitleBuf.length - 1);

            curBuf.setBuffer(setWindowTitleBuf, 4);
            msgArgs.wid = curBuf.nextInt();

            curBuf.setBuffer(setWindowTitleBuf, 8);
            int strLen = curBuf.nextInt();

            byte[] title = new byte[strLen];
            in.readFully(title);

            int hdrLen = setWindowTitleBuf.length;
            byte[] bytes = new byte[hdrLen + strLen];
            System.arraycopy(setWindowTitleBuf, 0, bytes, 0, hdrLen);
            System.arraycopy(title, 0, bytes, hdrLen, strLen);
            sf.broadcastSend(bytes);

            msgArgs.title = new String(title);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void getData(SlaveCloseWindowMsgArgs msgArgs) {
        try {
            slaveCloseWindowBuf[0] = lastMsgCode;
            in.readFully(slaveCloseWindowBuf, 1, slaveCloseWindowBuf.length - 1);
            sf.broadcastSend(slaveCloseWindowBuf);

            curBuf.setBuffer(slaveCloseWindowBuf, 4);
            msgArgs.clientId = curBuf.nextInt();
            msgArgs.wid = curBuf.nextInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void getData() {
        beepBuf[0] = lastMsgCode;
        sf.broadcastSend(beepBuf);
    }

    @Override
    public void setScanLineWidth(int width) {
        if (scanLineWidth != width) {
            scanLineBuf = new byte[width * 4];
        }
        scanLineWidth = width;
    }

    /* Debug 
    static int scanLinesRead = 0;
    static int bytesRead = 0;
     */
    @Override
    public byte[] readScanLine() {
        try {
            in.readFully(scanLineBuf);
            sf.broadcastSend(scanLineBuf);

            /* Debug
            scanLinesRead++;
            System.err.println("scanLinesRead = " + scanLinesRead);
            bytesRead += scanLineBuf.length;
            System.err.println("bytesRead = " + bytesRead);
             */

            return scanLineBuf;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int readRleInt() {
        try {
            in.readFully(integerBuf);
            //System.err.print("DPINT: ");
            //print10bytes(integerBuf);
            sf.broadcastSend(integerBuf);

            curBuf.setBuffer(integerBuf, 0);
            int value = curBuf.nextInt();
            return value;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void readRleChunk(byte[] chunkBuf) {
        try {
            in.readFully(chunkBuf);
            //System.err.print("DPCHUNK: ");
            //print10bytes(chunkBuf);
            sf.broadcastSend(chunkBuf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void readRleChunk(byte[] chunkBuf, int len) {
        try {
            in.readFully(chunkBuf, 0, len);
            //System.err.print("DPCHUNK: ");
            //print10bytes(chunkBuf);
            sf.broadcastSend(chunkBuf, len);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void broadcastControllingUserNameToSlaves(String userName) throws IOException {
        if (userName == null) {
            return;
        }

        int n = 0;
        int strLen = userName.length();

        // Compose the header
        userNameBuf[n++] = (byte) ServerMessageType.CONTROLLING_USER_NAME.ordinal();
        userNameBuf[n++] = 0; // Pad
        userNameBuf[n++] = (byte) ((strLen >> 8) & 0xff);
        userNameBuf[n++] = (byte) (strLen & 0xff);

        // Then send the string
        byte[] strBytes = userName.getBytes();
        int hdrLen = userNameBuf.length;
        byte[] bytes = new byte[hdrLen + strLen];
        System.arraycopy(userNameBuf, 0, bytes, 0, hdrLen);
        System.arraycopy(strBytes, 0, bytes, hdrLen, strLen);
        sf.broadcastSend(bytes);
    }

    public void setPopupParent(int wid, int parentWid) {
        int n = 0;

        setPopupParentBuf[n++] = (byte) ServerMessageType.SET_POPUP_PARENT.ordinal();
        setPopupParentBuf[n++] = 0; // Pad
        setPopupParentBuf[n++] = 0; // Pad
        setPopupParentBuf[n++] = 0; // Pad
        setPopupParentBuf[n++] = (byte) ((wid >> 24) & 0xff);
        setPopupParentBuf[n++] = (byte) ((wid >> 16) & 0xff);
        setPopupParentBuf[n++] = (byte) ((wid >> 8) & 0xff);
        setPopupParentBuf[n++] = (byte) (wid & 0xff);
        setPopupParentBuf[n++] = (byte) ((parentWid >> 24) & 0xff);
        setPopupParentBuf[n++] = (byte) ((parentWid >> 16) & 0xff);
        setPopupParentBuf[n++] = (byte) ((parentWid >> 8) & 0xff);
        setPopupParentBuf[n++] = (byte) (parentWid & 0xff);

        sf.broadcastSend(setPopupParentBuf);
    }

    public void writeSlaveSyncPixels(BigInteger slaveID, byte[] pixelBytes) {
        // NOTE: right now this is only used during syncing a slave.
        // So we must force the send to occur.
        sf.unicastSend(slaveID, pixelBytes, true);
    }

    /* For Debug
    private void debugPrintSlaveSyncPixels (byte[] pixelBuf) {
    int pixelCount = 0;
    for (int i = 0; i < pixelBuf.length; i += 4) {
    int pixel = pixelBuf[i]   << 24 |
    pixelBuf[i+1] << 16 |
    pixelBuf[i+2] <<  8 |
    pixelBuf[i+3];
    System.err.print(Integer.toHexString(pixel) + " ");
    pixelCount++;
    if ((pixelCount % 10) == 9) {
    System.err.println();
    }
    }
    }
     */

    // For Debug
    private static void print10bytes(byte[] bytes) {
        int n = (bytes.length > 10) ? 10 : bytes.length;
        for (int i = 0; i < n; i++) {
            System.err.print(Integer.toHexString(bytes[i] & 0xff) + " ");
        }
        System.err.println();
    }
}

