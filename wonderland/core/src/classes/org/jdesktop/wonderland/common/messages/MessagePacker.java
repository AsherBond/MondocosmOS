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
package org.jdesktop.wonderland.common.messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.comms.SessionInternalConnectionType;
import org.jdesktop.wonderland.common.comms.WonderlandObjectInputStream;
import org.jdesktop.wonderland.common.comms.WonderlandObjectOutputStream;

/**
 *
 * Utility class to pack and unpack messages to/from ByteBuffers.
 * 
 * 
 * @author paulby
 */
public class MessagePacker {
    
    // TODO - would it be more performant to have a pool of streams and
    // have each call to the serialization pack/unpack use a pre created
    // stream from the pool.
    
    private static MessageMonitor messageMonitor = null;
    
    private static HashMap<Class, Short> packClassIds;
    
    private static short NOT_PACKED = Short.MIN_VALUE;
    
    private static Logger logger = Logger.getLogger(MessagePacker.class.getName());
    
    static {
        short id = Short.MIN_VALUE+1;
        packClassIds = new HashMap();
        packClassIds.put(CellID.class, id++);
    }

    /**
     * Return the id used to identify the class obj in the packed messages
     * 
     * @param aThis
     * @return
     */
    public static short getPackClassId(Object obj) {
        Short ret = packClassIds.get(obj.getClass());
        if (ret==null)
            return NOT_PACKED;
        return ret;
    }

    /**
     * Pack the given message into a ByteBuffer ready
     * for transmission over the network
     * @param message
     * @return
     */
    public static ByteBuffer pack(Message message, short clientID) throws PackerException {
        ObjectOutputStream out = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            out = new WonderlandObjectOutputStream(baos);

            serializationPack(message, clientID, out);

            ByteBuffer buf = ByteBuffer.wrap(baos.toByteArray());
            if (messageMonitor != null) {
                messageMonitor.sending(message, buf.capacity());
            }
            
            // TODO Remove - this is should be in messageMonitor
            if (logger.isLoggable(Level.FINEST)) {
                logger.finest("Packed " + message.getClass().getName() + "   size " + buf.capacity());
            }
            return buf;
        } catch (IOException ex) {
            Logger.getLogger(MessagePacker.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (out!=null)
                    out.close();
            } catch (IOException ex) {
                Logger.getLogger(MessagePacker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        throw new PackerException();
    }
    
    private static void serializationPack(Message message, short clientID, ObjectOutputStream out) throws PackerException {
        try {
            // first write the message ID
            out.writeObject(message.getMessageID());

            // now write the client ID
            out.writeShort(clientID);

            // finally, write the serialized message
            out.writeObject(message);

        } catch (IOException ex) {
            Logger.getLogger(MessagePacker.class.getName()).log(Level.SEVERE, null, ex);
            throw new PackerException();
        } 
    }
    
    /**
     * Give a ByteBuffer unpack it's data and return
     * the message it represents
     * @param buf
     * @return
     */
    public static ReceivedMessage unpack(ByteBuffer buf) throws PackerException {
        return unpack(buf, null);
    }
    
    /**
     * Give a ByteBuffer unpack it's data and return the message it represents.
     * Use the provided classloader to resolve the message class.
     * @param buf the buffer to unpack
     * @param classLoader the classloader to resolve the class with
     * @return the unpacked message
     * @throws org.jdesktop.wonderland.common.messages.MessagePacker.PackerException
     */
    public static ReceivedMessage unpack(ByteBuffer buf, ClassLoader classLoader) 
            throws PackerException
    {
        ObjectInputStream in = null;
        try {
            in = new WonderlandObjectInputStream(getInputStream(buf), classLoader);
            ReceivedMessage msg = serializationUnpack(buf, in);

            in.close();
            if (messageMonitor != null) {
                messageMonitor.received(msg.getMessage(), buf.capacity());
            }
            return msg;
        } catch (IOException ex) {
            Logger.getLogger(MessagePacker.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(MessagePacker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        throw new PackerException();
    }
        
    private static ReceivedMessage serializationUnpack(ByteBuffer buf, ObjectInputStream in) {            
        
        try {
            
            // first read the message ID
            MessageID messageID = (MessageID) in.readObject();

            // next the client ID
            short clientID = in.readShort();

            // finally the message
            Message message = (Message)in.readObject();

            // now put it all together
            message.setMessageID(messageID);
            return new ReceivedMessage(message, clientID);
        } catch (IOException ex) {
            Logger.getLogger(MessagePacker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MessagePacker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * Convert a ByteBuffer into an input stream
     * @param data the byte array
     * @return an input stream for reading from the ByteBuffer
     */
    protected static InputStream getInputStream(ByteBuffer data) {
         // get a byte buffer from the data
         ByteArrayInputStream bais;
         if (data.hasArray()) {
            // optimized method uses the backing array for this
            // ByteBuffer directly
            bais = new ByteArrayInputStream(data.array(),
                                            data.arrayOffset(),
                                            data.remaining());
         } else {
             // copy contents of ByteBuffer into a byte array
             byte[] arr = new byte[data.remaining()];
             data.get(arr);
             bais = new ByteArrayInputStream(arr);
         }
         
         return bais;
    }
    
    /**
     * Set the message monitor.
     * @param monitor
     */
    public static void setMessageMonitor(MessageMonitor monitor) {
        messageMonitor = monitor;
    }
    
    /**
     * A description of a received message.  This includes both the clientID 
     * the message was sent with and the contents of the message.
     */
    public static class ReceivedMessage {
        /** the message */
        private final Message message;
        
        /** the clientID sent with the message */
        private final short clientID;
    
        /**
         * Create a new ReceivedMessage 
         */
        private ReceivedMessage(Message message, short clientID) {
            this.message = message;
            this.clientID = clientID;
        }
        
        /**
         * Get the message
         */
        public Message getMessage() {
            return message;
        }
        
        /**
         * Get the client ID
         */
        public short getClientID() {
            return clientID;
        }
    }
    
    /**
     * Thrown when there is a problem packing or unpacking a Message
     */
    public static class PackerException extends Exception {
        
        /**
         * Return the message id for the message that failed, if known. Otherwise
         * return null
         * @return messageID, or null
         */
        public MessageID getMessageID() {
            return null;
        }
        
        public short getClientID() {
            return Short.MIN_VALUE;
        }
    }

}
