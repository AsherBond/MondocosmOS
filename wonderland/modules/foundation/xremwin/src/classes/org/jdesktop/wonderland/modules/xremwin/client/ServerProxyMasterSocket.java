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

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import com.jme.math.Vector3f;
import org.jdesktop.wonderland.common.ExperimentalAPI;
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

/**
 * An implementation ofa a ServerProxy which is used by the master to 
 * communicate with the Xremwin server using socket transport.
 *
 * @author deronj
 */
@ExperimentalAPI
abstract class ServerProxyMasterSocket implements ServerProxy {

    /** 
     * The Xremwin port number.
     * TODO: this is currently the same as RFB. This prevents
     * VNC and Xremwin being used on the same machine.
     */
    private static int portBase = 5900;

    // Make sure that when the server isn't sending us any messages that
    // we service slave hellos at least two times per second
    private static final int SOCKET_TIMEOUT = 500; // ms

    // TODO
    private static final int BUTTON4_MASK = 0x08;
    private static final int BUTTON5_MASK = 0x10;
    /** The name of the master host */
    protected String masterHost;
    /** The display number of the window system started by the master */
    protected int wsDisplayNum;
    /** The unique small integer ID of the client */
    protected int clientId;
    /** The connection's socket */
    protected Socket sock;
    /** The connection's input stream */
    protected DataInputStream in;
    /** The connection's output stream */
    protected OutputStream out;
    /** The width of the current scanline */
    protected int scanLineWidth;
    /** A buffer to hold the current scanline being read */
    protected byte[] scanLineBuf;

    // Buffers to hold various types of messages
    private byte[] keyEventBuf       = new byte[Proto.ClientMessageType.EVENT_KEY.size()];
    private byte[] pointerEventBuf   = new byte[Proto.ClientMessageType.EVENT_POINTER.size()];
    private byte[] takeControlBuf    = new byte[Proto.ClientMessageType.TAKE_CONTROL.size()];
    private byte[] releaseControlBuf = new byte[Proto.ClientMessageType.RELEASE_CONTROL.size()];
    private byte[] setWindowTitleBuf = new byte[Proto.ClientMessageType.SET_WINDOW_TITLE.size()];
    private byte[] setUserDisplBuf   = new byte[Proto.ClientMessageType.WINDOW_SET_USER_DISPLACEMENT.size()];
    private byte[] setSizeBuf        = new byte[Proto.ClientMessageType.WINDOW_SET_SIZE.size()];
    private byte[] setRotateYBuf     = new byte[Proto.ClientMessageType.WINDOW_SET_ROTATE_Y.size()];
    private byte[] toFrontBuf        = new byte[Proto.ClientMessageType.WINDOW_TO_FRONT.size()];
    private byte[] destroyWindowBuf  = new byte[Proto.ClientMessageType.DESTROY_WINDOW.size()];

    // We save the last pointer position in these so that we can supply
    // the current pointer position to the server when we send wheel events.
    private int lastPointerX = 0;
    private int lastPointerY = 0;

    /**
     * Create a new instance of ServerProxyMasterSocket.
     * @param masterHost The name of the master host (this host).
     * @param wsDisplayNum The X11 display number used by the window system
     * for this app.
     */
    protected ServerProxyMasterSocket(String masterHost, int wsDisplayNum) {
        this.masterHost = masterHost;
        this.wsDisplayNum = wsDisplayNum;
    }

    public void cleanup() {
        disconnect();
        scanLineBuf = null;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    protected synchronized void establishConnection() {
       try {
            sock = new Socket(masterHost, portBase + wsDisplayNum);
            sock.setSoTimeout(SOCKET_TIMEOUT);
            in = new DataInputStream(new BufferedInputStream(sock.getInputStream()));
            out = sock.getOutputStream();
        } catch (Exception ex) {
            AppXrw.logger.log(Level.WARNING, "Error connecting to " + masterHost + " port " +
                              (portBase + wsDisplayNum), ex);
            throw new RuntimeException(ex);
        }
    }

    public void disconnect() {
        try {
            // TODO: bug workaround: for some reason, if we try to close this
            // socket it causes  a massive slowdown in Wonderland!
            // I tried to not close it if the socket had taken an exception
            // but it didn't help.
            //sock.close();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        scanLineBuf = null;
    }

    public Proto.ServerMessageType getMessageType() {
        try {
            int msgCode = (int) in.readUnsignedByte();
            return Proto.ServerMessageType.values()[msgCode];
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getData(CreateWindowMsgArgs msgArgs) {
        try {
            msgArgs.decorated = in.readByte() != 0;
            msgArgs.borderWidth = in.readUnsignedShort();
            msgArgs.wid = in.readInt();
            msgArgs.x = in.readShort();
            msgArgs.y = in.readShort();
            msgArgs.wAndBorder = in.readInt();
            msgArgs.hAndBorder = in.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getData(DestroyWindowMsgArgs msgArgs) {
        try {
            // Skip 3 bytes of pad
            in.readByte();
            in.readUnsignedShort();
            msgArgs.wid = in.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getData(ShowWindowMsgArgs msgArgs) {
        try {
            msgArgs.show = in.readByte() != 0;
            in.readShort();            // Skip 2 bytes of pad
            msgArgs.wid = in.readInt();
            /* TODO: 0.4 protocol:
            msgArgs.transientFor = in.readInt();
             */
            in.readInt(); // Ignore
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getData(ConfigureWindowMsgArgs msgArgs) {
        try {

            // Skip 3 bytes of pad
            in.readByte();
            in.readUnsignedShort();

            msgArgs.clientId = in.readInt();
            msgArgs.wid = in.readInt();
            msgArgs.x = in.readShort();
            msgArgs.y = in.readShort();
            msgArgs.wAndBorder = in.readInt();
            msgArgs.hAndBorder = in.readInt();
            msgArgs.sibid = in.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getData(PositionWindowMsgArgs msgArgs) {
        try {

            // Skip 3 bytes of pad
            in.readByte();
            in.readUnsignedShort();

            msgArgs.clientId = in.readInt();
            msgArgs.wid = in.readInt();
            msgArgs.x = in.readShort();
            msgArgs.y = in.readShort();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getData(RestackWindowMsgArgs msgArgs) {
        try {

            // Skip 3 bytes of pad
            in.readByte();
            in.readUnsignedShort();

            msgArgs.clientId = in.readInt();
            msgArgs.wid = in.readInt();
            msgArgs.sibid = in.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getData(WindowSetDecoratedMsgArgs msgArgs) {
        try {
            msgArgs.decorated = (in.readByte() == 1) ? true : false;
            in.readUnsignedShort();  // skip pad
            msgArgs.wid = in.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void getData(WindowSetBorderWidthMsgArgs msgArgs) {
        try {
            in.readByte();           // skip pad
            msgArgs.borderWidth = in.readUnsignedShort();
            msgArgs.wid = in.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getData(WindowSetUserDisplMsgArgs msgArgs) {
        try {
            in.readByte();          // skip pad
            in.readUnsignedShort(); // skip pad
            msgArgs.clientId = in.readInt();
            msgArgs.wid = in.readInt();
            int ix = in.readInt();
            int iy = in.readInt();
            int iz = in.readInt();
            msgArgs.userDispl = new Vector3f(Float.intBitsToFloat(ix),
                    Float.intBitsToFloat(iy),
                    Float.intBitsToFloat(iz));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getData(WindowSetRotateYMsgArgs msgArgs) {
        try {
            in.readByte();          // skip pad
            in.readUnsignedShort(); // skip pad
            msgArgs.clientId = in.readInt();
            msgArgs.wid = in.readInt();
            int iroty = in.readInt();
            msgArgs.roty = Float.intBitsToFloat(iroty);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getData(DisplayPixelsMsgArgs msgArgs) {
        try {
            int encodingCode = in.readByte();
            msgArgs.encoding = Proto.PixelEncoding.values()[encodingCode];
            msgArgs.x = in.readUnsignedShort();
            msgArgs.wid = in.readInt();
            msgArgs.y = in.readUnsignedShort();
            msgArgs.w = in.readUnsignedShort();
            msgArgs.h = in.readUnsignedShort();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getData(CopyAreaMsgArgs msgArgs) {
        try {
            // Skip 3 bytes of pad
            in.readByte();
            in.readUnsignedShort();

            msgArgs.wid = in.readInt();
            msgArgs.srcX = in.readInt();
            msgArgs.srcY = in.readInt();
            msgArgs.width = in.readInt();
            msgArgs.height = in.readInt();
            msgArgs.dstX = in.readInt();
            msgArgs.dstY = in.readInt();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getData(ControllerStatusMsgArgs msgArgs) {
        try {
            int status = (int) in.readByte();
            msgArgs.status = Proto.ControllerStatus.values()[status];

            // Skip 2 bytes of pad
            in.readUnsignedShort();

            msgArgs.clientId = in.readInt();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Returns null if no user has control
    public abstract String getControllingUser();

    public void getData(SetWindowTitleMsgArgs msgArgs) {
        try {
            // Skip 1 byte of pad
            in.readByte();
            in.readShort();

            msgArgs.wid = in.readInt();
            int strLen = in.readInt();

            byte[] bytes = new byte[strLen];
            for (int i = 0; i < strLen; i++) {
                bytes[i] = in.readByte();
            }
            msgArgs.title = new String(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getData(SlaveCloseWindowMsgArgs msgArgs) {
        try {
            in.readByte();          // skip pad
            in.readUnsignedShort(); // skip pad
            msgArgs.clientId = in.readInt();
            msgArgs.wid = in.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getData() {
    }

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
    public byte[] readScanLine() {
        try {
            in.readFully(scanLineBuf);

            /* Debug
            scanLinesRead++;
            AppXrw.logger.finer("scanLinesRead = " + scanLinesRead);
            bytesRead += scanLineBuf.length;
            AppXrw.logger.finer("bytesRead = " + bytesRead);
             */

            return scanLineBuf;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int readRleInt() {
        try {
            int value = in.readInt();
            return value;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void readRleChunk(byte[] buf) {
        try {
            in.readFully(buf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void readRleChunk(byte[] buf, int len) {
        try {
            in.readFully(buf, 0, len);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected synchronized void write(byte[] buf, int i, int length) throws IOException {
        try {
            out.write(buf, i, length);
        } catch (SocketException ex) {
            // Ignore socket exceptions. They are caused by the server going away,
            // usually on application death. We detect this elsewhere.
        }
    }

    protected synchronized void write(byte[] buf) throws IOException {
        try {
            out.write(buf, 0, buf.length);
        } catch (SocketException ex) {
            // Ignore socket exceptions. They are caused by the server going away,
            // usually on application death. We detect this elsewhere.
        }
    }

    public void writeEvent(int wid, MouseEvent event) throws IOException {
        int mask = 0;
        int n = 0;

        int modifiers = event.getModifiersEx();
        if ((modifiers & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
            mask |= 0x01;
        }
        if ((modifiers & MouseEvent.BUTTON2_DOWN_MASK) != 0) {
            mask |= 0x02;
        }
        if ((modifiers & MouseEvent.BUTTON3_DOWN_MASK) != 0) {
            mask |= 0x04;
        }

        int x = event.getX();
        int y = event.getY();
        lastPointerX = x;
        lastPointerY = y;

        AppXrw.logger.fine("Send ptr: clientId = " + clientId + ", xy = " + x + ", " + y +
                ", mask = 0x" + Integer.toHexString(mask));

        pointerEventBuf[n++] = (byte) Proto.ClientMessageType.EVENT_POINTER.ordinal();
        pointerEventBuf[n++] = (byte) mask;
        pointerEventBuf[n++] = (byte) ((x >> 8) & 0xff);
        pointerEventBuf[n++] = (byte) (x & 0xff);
        pointerEventBuf[n++] = (byte) ((y >> 8) & 0xff);
        pointerEventBuf[n++] = (byte) (y & 0xff);
        pointerEventBuf[n++] = (byte) 0;
        pointerEventBuf[n++] = (byte) 0;
        pointerEventBuf[n++] = (byte) ((wid >> 24) & 0xff);
        pointerEventBuf[n++] = (byte) ((wid >> 16) & 0xff);
        pointerEventBuf[n++] = (byte) ((wid >> 8) & 0xff);
        pointerEventBuf[n++] = (byte) (wid & 0xff);
        pointerEventBuf[n++] = (byte) ((clientId >> 24) & 0xff);
        pointerEventBuf[n++] = (byte) ((clientId >> 16) & 0xff);
        pointerEventBuf[n++] = (byte) ((clientId >> 8) & 0xff);
        pointerEventBuf[n++] = (byte) (clientId & 0xff);

        write(pointerEventBuf, 0, n);
    }

    public void writeWheelEvent(int wid, MouseWheelEvent event) throws IOException {
        int wheelRotation = event.getWheelRotation();
        int mask = (wheelRotation == -1) ? BUTTON4_MASK : BUTTON5_MASK;
        int n;

        AppXrw.logger.fine("send MouseWheelEvent = " + event);

        /* First send button pressevent */
        n = 0;
        pointerEventBuf[n++] = (byte) Proto.ClientMessageType.EVENT_POINTER.ordinal();
        pointerEventBuf[n++] = (byte) mask;
        pointerEventBuf[n++] = (byte) ((lastPointerX >> 8) & 0xff);
        pointerEventBuf[n++] = (byte) (lastPointerX & 0xff);
        pointerEventBuf[n++] = (byte) ((lastPointerY >> 8) & 0xff);
        pointerEventBuf[n++] = (byte) (lastPointerY & 0xff);
        pointerEventBuf[n++] = (byte) 0;
        pointerEventBuf[n++] = (byte) 0;
        pointerEventBuf[n++] = (byte) ((wid >> 24) & 0xff);
        pointerEventBuf[n++] = (byte) ((wid >> 16) & 0xff);
        pointerEventBuf[n++] = (byte) ((wid >> 8) & 0xff);
        pointerEventBuf[n++] = (byte) (wid & 0xff);
        pointerEventBuf[n++] = (byte) ((clientId >> 24) & 0xff);
        pointerEventBuf[n++] = (byte) ((clientId >> 16) & 0xff);
        pointerEventBuf[n++] = (byte) ((clientId >> 8) & 0xff);
        pointerEventBuf[n++] = (byte) (clientId & 0xff);
        write(pointerEventBuf, 0, n);

        /* Then send button release event */
        mask = 0;
        n = 0;
        pointerEventBuf[n++] = (byte) Proto.ClientMessageType.EVENT_POINTER.ordinal();
        pointerEventBuf[n++] = (byte) mask;
        pointerEventBuf[n++] = (byte) ((lastPointerX >> 8) & 0xff);
        pointerEventBuf[n++] = (byte) (lastPointerX & 0xff);
        pointerEventBuf[n++] = (byte) ((lastPointerY >> 8) & 0xff);
        pointerEventBuf[n++] = (byte) (lastPointerY & 0xff);
        pointerEventBuf[n++] = (byte) 0;
        pointerEventBuf[n++] = (byte) 0;
        pointerEventBuf[n++] = (byte) ((wid >> 24) & 0xff);
        pointerEventBuf[n++] = (byte) ((wid >> 16) & 0xff);
        pointerEventBuf[n++] = (byte) ((wid >> 8) & 0xff);
        pointerEventBuf[n++] = (byte) (wid & 0xff);
        pointerEventBuf[n++] = (byte) ((clientId >> 24) & 0xff);
        pointerEventBuf[n++] = (byte) ((clientId >> 16) & 0xff);
        pointerEventBuf[n++] = (byte) ((clientId >> 8) & 0xff);
        pointerEventBuf[n++] = (byte) (clientId & 0xff);
        write(pointerEventBuf, 0, n);
    }

    public void writeEvent(KeyEvent event) throws IOException {
        char keyChar;
        int keyCode;
        int keySym;
        int n = 0;

        /* First, try to get the keysym from the keychar */
        keyChar = event.getKeyChar();
        keyCode = event.getKeyCode();
        keySym = UnicodeToKeysym.getKeysym(keyChar);

        /* Next, try to get it from the keycode */
        if (keySym == -1) {
            keySym = KeycodeToKeysym.getKeysym(keyCode);
        }

        /*
         ** If we still don't have it and the keychar is less than 0x20
         ** the control key is pressed and has already been sent. So
         ** just send the key and not its controlled version.
         */
        if (keySym == -1) {
            if (keyChar < 0x20) {
                keySym = keyChar + 0x60;
            }
        }

        if (keySym == -1) {
            AppXrw.logger.warning("Could not find keysym for key event = " + event);
            AppXrw.logger.warning("Key event not sent to remote window server");
            return;
        }

        AppXrw.logger.fine("Send keySym = " + keySym);

        keyEventBuf[n++] = (byte) Proto.ClientMessageType.EVENT_KEY.ordinal();
        keyEventBuf[n++] = (byte) ((event.getID() == KeyEvent.KEY_PRESSED) ? 1 : 0);
        boolean pressed = ((event.getID() == KeyEvent.KEY_PRESSED) ? true : false);
        keyEventBuf[n++] = (byte) 0;
        keyEventBuf[n++] = (byte) 0;
        keyEventBuf[n++] = (byte) ((keySym >> 24) & 0xff);
        keyEventBuf[n++] = (byte) ((keySym >> 16) & 0xff);
        keyEventBuf[n++] = (byte) ((keySym >> 8) & 0xff);
        keyEventBuf[n++] = (byte) (keySym & 0xff);
        keyEventBuf[n++] = (byte) ((clientId >> 24) & 0xff);
        keyEventBuf[n++] = (byte) ((clientId >> 16) & 0xff);
        keyEventBuf[n++] = (byte) ((clientId >> 8) & 0xff);
        keyEventBuf[n++] = (byte) (clientId & 0xff);

        write(keyEventBuf, 0, n);
    }

    public void writeTakeControl(boolean steal) throws IOException {
        int n = 0;

        AppXrw.logger.fine("Send take control, steal = " + steal);

        takeControlBuf[n++] = (byte) Proto.ClientMessageType.TAKE_CONTROL.ordinal();
        takeControlBuf[n++] = (byte) (steal ? 1 : 0);
        takeControlBuf[n++] = (byte) 0;
        takeControlBuf[n++] = (byte) 0;
        takeControlBuf[n++] = (byte) ((clientId >> 24) & 0xff);
        takeControlBuf[n++] = (byte) ((clientId >> 16) & 0xff);
        takeControlBuf[n++] = (byte) ((clientId >> 8) & 0xff);
        takeControlBuf[n++] = (byte) (clientId & 0xff);
        write(takeControlBuf, 0, n);
    }

    public void writeReleaseControl() throws IOException {
        int n = 0;

        AppXrw.logger.fine("Send release control = ");

        releaseControlBuf[n++] = (byte) Proto.ClientMessageType.RELEASE_CONTROL.ordinal();
        releaseControlBuf[n++] = (byte) 0;
        releaseControlBuf[n++] = (byte) 0;
        releaseControlBuf[n++] = (byte) 0;
        releaseControlBuf[n++] = (byte) ((clientId >> 24) & 0xff);
        releaseControlBuf[n++] = (byte) ((clientId >> 16) & 0xff);
        releaseControlBuf[n++] = (byte) ((clientId >> 8) & 0xff);
        releaseControlBuf[n++] = (byte) (clientId & 0xff);
        write(releaseControlBuf, 0, n);
    }

    public void writeSetWindowTitle(int wid, String title) throws IOException {
        int strLen = title.length();
        int n = 0;

        /* First send header */
        setWindowTitleBuf[n++] = (byte) Proto.ClientMessageType.SET_WINDOW_TITLE.ordinal();
        setWindowTitleBuf[n++] = 0; // Pad
        setWindowTitleBuf[n++] = 0; // Pad
        setWindowTitleBuf[n++] = 0; // Pad
        setWindowTitleBuf[n++] = (byte) ((wid >> 24) & 0xff);
        setWindowTitleBuf[n++] = (byte) ((wid >> 16) & 0xff);
        setWindowTitleBuf[n++] = (byte) ((wid >> 8) & 0xff);
        setWindowTitleBuf[n++] = (byte) (wid & 0xff);
        setWindowTitleBuf[n++] = (byte) ((strLen >> 24) & 0xff);
        setWindowTitleBuf[n++] = (byte) ((strLen >> 16) & 0xff);
        setWindowTitleBuf[n++] = (byte) ((strLen >> 8) & 0xff);
        setWindowTitleBuf[n++] = (byte) (strLen & 0xff);
        write(setWindowTitleBuf, 0, n);

        // Then send the string
        byte[] strBytes = title.getBytes();
        write(strBytes, 0, strLen);
    }

    public void windowSetUserDisplacement(int cid, int wid, Vector3f userDispl) throws IOException {
        int n = 0;
        int ix = Float.floatToRawIntBits(userDispl.x);
        int iy = Float.floatToRawIntBits(userDispl.y);
        int iz = Float.floatToRawIntBits(userDispl.z);

        setUserDisplBuf[n++] = (byte) Proto.ClientMessageType.WINDOW_SET_USER_DISPLACEMENT.ordinal();
        setUserDisplBuf[n++] = 0; // Pad
        setUserDisplBuf[n++] = 0; // Pad
        setUserDisplBuf[n++] = 0; // Pad
        setUserDisplBuf[n++] = (byte) ((cid >> 24) & 0xff);
        setUserDisplBuf[n++] = (byte) ((cid >> 16) & 0xff);
        setUserDisplBuf[n++] = (byte) ((cid >> 8) & 0xff);
        setUserDisplBuf[n++] = (byte) (cid & 0xff);
        setUserDisplBuf[n++] = (byte) ((wid >> 24) & 0xff);
        setUserDisplBuf[n++] = (byte) ((wid >> 16) & 0xff);
        setUserDisplBuf[n++] = (byte) ((wid >> 8) & 0xff);
        setUserDisplBuf[n++] = (byte) (wid & 0xff);
        setUserDisplBuf[n++] = (byte) ((ix >> 24) & 0xff);
        setUserDisplBuf[n++] = (byte) ((ix >> 16) & 0xff);
        setUserDisplBuf[n++] = (byte) ((ix >> 8) & 0xff);
        setUserDisplBuf[n++] = (byte) (ix & 0xff);
        setUserDisplBuf[n++] = (byte) ((iy >> 24) & 0xff);
        setUserDisplBuf[n++] = (byte) ((iy >> 16) & 0xff);
        setUserDisplBuf[n++] = (byte) ((iy >> 8) & 0xff);
        setUserDisplBuf[n++] = (byte) (iy & 0xff);
        setUserDisplBuf[n++] = (byte) ((iz >> 24) & 0xff);
        setUserDisplBuf[n++] = (byte) ((iz >> 16) & 0xff);
        setUserDisplBuf[n++] = (byte) ((iz >> 8) & 0xff);
        setUserDisplBuf[n++] = (byte) (iz & 0xff);

        write(setUserDisplBuf, 0, n);
    }

    public void windowSetSize(int cid, int wid, int w, int h) throws IOException {
        int n = 0;

        setSizeBuf[n++] = (byte) Proto.ClientMessageType.WINDOW_SET_SIZE.ordinal();
        setSizeBuf[n++] = 0; // Pad
        setSizeBuf[n++] = 0; // Pad
        setSizeBuf[n++] = 0; // Pad
        setSizeBuf[n++] = (byte) ((cid >> 24) & 0xff);
        setSizeBuf[n++] = (byte) ((cid >> 16) & 0xff);
        setSizeBuf[n++] = (byte) ((cid >> 8) & 0xff);
        setSizeBuf[n++] = (byte) (cid & 0xff);
        setSizeBuf[n++] = (byte) ((wid >> 24) & 0xff);
        setSizeBuf[n++] = (byte) ((wid >> 16) & 0xff);
        setSizeBuf[n++] = (byte) ((wid >> 8) & 0xff);
        setSizeBuf[n++] = (byte) (wid & 0xff);
        setSizeBuf[n++] = (byte) ((w >> 24) & 0xff);
        setSizeBuf[n++] = (byte) ((w >> 16) & 0xff);
        setSizeBuf[n++] = (byte) ((w >> 8) & 0xff);
        setSizeBuf[n++] = (byte) (w & 0xff);
        setSizeBuf[n++] = (byte) ((h >> 24) & 0xff);
        setSizeBuf[n++] = (byte) ((h >> 16) & 0xff);
        setSizeBuf[n++] = (byte) ((h >> 8) & 0xff);
        setSizeBuf[n++] = (byte) (h & 0xff);

        write(setSizeBuf, 0, n);
    }

    public void windowSetRotateY(int cid, int wid, float rotY) throws IOException {
        int n = 0;
        int irotY = Float.floatToRawIntBits(rotY);

        setRotateYBuf[n++] = (byte) Proto.ClientMessageType.WINDOW_SET_ROTATE_Y.ordinal();
        setRotateYBuf[n++] = 0; // Pad
        setRotateYBuf[n++] = 0; // Pad
        setRotateYBuf[n++] = 0; // Pad
        setRotateYBuf[n++] = (byte) ((cid >> 24) & 0xff);
        setRotateYBuf[n++] = (byte) ((cid >> 16) & 0xff);
        setRotateYBuf[n++] = (byte) ((cid >> 8) & 0xff);
        setRotateYBuf[n++] = (byte) (cid & 0xff);
        setRotateYBuf[n++] = (byte) ((wid >> 24) & 0xff);
        setRotateYBuf[n++] = (byte) ((wid >> 16) & 0xff);
        setRotateYBuf[n++] = (byte) ((wid >> 8) & 0xff);
        setRotateYBuf[n++] = (byte) (wid & 0xff);
        setRotateYBuf[n++] = (byte) ((irotY >> 24) & 0xff);
        setRotateYBuf[n++] = (byte) ((irotY >> 16) & 0xff);
        setRotateYBuf[n++] = (byte) ((irotY >> 8) & 0xff);
        setRotateYBuf[n++] = (byte) (irotY & 0xff);

        write(setRotateYBuf, 0, n);
    }

    public void windowToFront(int cid, int wid) throws IOException {
        int n = 0;

        toFrontBuf[n++] = (byte) Proto.ClientMessageType.WINDOW_TO_FRONT.ordinal();
        toFrontBuf[n++] = 0; // Pad
        toFrontBuf[n++] = 0; // Pad
        toFrontBuf[n++] = 0; // Pad
        toFrontBuf[n++] = (byte) ((cid >> 24) & 0xff);
        toFrontBuf[n++] = (byte) ((cid >> 16) & 0xff);
        toFrontBuf[n++] = (byte) ((cid >> 8) & 0xff);
        toFrontBuf[n++] = (byte) (cid & 0xff);
        toFrontBuf[n++] = (byte) ((wid >> 24) & 0xff);
        toFrontBuf[n++] = (byte) ((wid >> 16) & 0xff);
        toFrontBuf[n++] = (byte) ((wid >> 8) & 0xff);
        toFrontBuf[n++] = (byte) (wid & 0xff);

        write(toFrontBuf, 0, n);
    }

    public void destroyWindow(int wid) throws IOException {
        int n = 0;

        destroyWindowBuf[n++] = (byte) Proto.ClientMessageType.DESTROY_WINDOW.ordinal();
        destroyWindowBuf[n++] = 0; // Pad
        destroyWindowBuf[n++] = 0; // Pad
        destroyWindowBuf[n++] = 0; // Pad
        destroyWindowBuf[n++] = (byte) ((wid >> 24) & 0xff);
        destroyWindowBuf[n++] = (byte) ((wid >> 16) & 0xff);
        destroyWindowBuf[n++] = (byte) ((wid >> 8) & 0xff);
        destroyWindowBuf[n++] = (byte) (wid & 0xff);

        write(destroyWindowBuf, 0, n);
    }
}
