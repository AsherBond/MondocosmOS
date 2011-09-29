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
package org.jdesktop.wonderland.comms.client;

import java.io.EOFException;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.comms.WonderlandObjectInputStream;
import org.jdesktop.wonderland.common.comms.WonderlandObjectOutputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests for the optimized serialization
 * 
 * @author paulby
 */
public class SerializationTest {

    private WonderlandObjectInputStream in;
    private WonderlandObjectOutputStream out;
    private PipedInputStream pipeIn;
    private CountingPipedOutputStream pipeOut;
    
    private Sender sender;
    private Receiver receiver;
    
    public SerializationTest() {
    }
    
    @Before
    public void setUp() {
        try {
            pipeIn = new PipedInputStream();
            pipeOut = new CountingPipedOutputStream();
            pipeIn.connect(pipeOut);
            sender = new Sender();
            receiver = new Receiver();
            sender.start();
            receiver.start();
            
        } catch (IOException ex) {
            Logger.getLogger(SerializationTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("Stream setup failed");
        }
    }

    @After
    public void tearDown() {
        try {
            receiver.setDone(true);
            out.close();
            pipeOut.close();
            pipeIn.close();
        } catch (IOException ex) {
            Logger.getLogger(SerializationTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    @Test
    public void simpleObjectTest() {
        checkSerialization(new SimpleObject(1));

        checkSerialization(new SimpleObject(2));
    }
    
    @Test
    public void complexObjectTest() {
        checkSerialization(new ComplexObject(3));
        checkSerialization(new ComplexObject(4));
    }
    
    @Test
    // Check that the refernce queue is working correctly and classes are
    // removed
    public void largeObjectTest() {
        try {
            sender.sendObject(new byte[1024 * 1024 * 10]);
            Object recv = receiver.getMessage();
            System.gc();
            sender.sendObject(new int[1024 * 1024 * 2]);
            recv = receiver.getMessage();
            System.gc();
            sender.sendObject(new long[1024 * 1024 * 1]);
            recv = receiver.getMessage();
            System.gc();
        } catch (InterruptedException ex) {
            Logger.getLogger(SerializationTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Send the object over the stream and check it arrives correctly
     * @param obj
     */
    private void checkSerialization(Serializable obj) {
        try {
            sender.sendObject(obj);
            Object recv = receiver.getMessage();

            if (!obj.equals(recv)) {
                fail("Failed with object " + obj.getClass().getName());
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(SerializationTest.class.getName()).log(Level.SEVERE, null, ex);
            fail("Interrupted Exception");
        }
    }
    
    class Sender extends Thread {
        private BlockingQueue<Serializable> queue = new LinkedBlockingQueue();;
        private boolean done = false;
        
        @Override
        public void run() {
            try {
                out = new WonderlandObjectOutputStream(pipeOut);
            } catch (IOException ex) {
                Logger.getLogger(SerializationTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            while(!done) {
                try {
                    Serializable o = queue.take();
                    System.err.print("Got data, writing to stream...");
                    pipeOut.resetCounter();
                    out.writeObject(o);
                    out.flush();
                    
                    System.err.println("Written object, size = "+pipeOut.getCounter());
                } catch (IOException ex) {
                    Logger.getLogger(SerializationTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    if (!done)
                        Logger.getLogger(SerializationTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        public void sendObject(Serializable object) {
            synchronized(queue) {
                queue.offer(object);
            }
        }
    }
    
    class Receiver extends Thread {
        private BlockingQueue receiveQueue = new LinkedBlockingQueue();
        private boolean done = false;
        
        @Override
        public void run() {
            try {
                in = new WonderlandObjectInputStream(pipeIn);
            } catch(IOException ex) {
                Logger.getLogger(SerializationTest.class.getName()).log(Level.SEVERE, null, ex);                
            }
            
            while(!done) {
                try {
                    Object o = in.readObject();
                    System.out.println("Read Object "+o);
                    receiveQueue.offer(o);
                } catch(EOFException ex) {
                    if (!done)
                        Logger.getLogger(SerializationTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(SerializationTest.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(SerializationTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            try {
                in.close();
                pipeIn.close();
            } catch(IOException e) {
                Logger.getLogger(SerializationTest.class.getName()).log(Level.SEVERE, "Error closing streams", e);                
            }
        }
        
        public void setDone(boolean done) {
            this.done = true;
            
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(SerializationTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public Object getMessage() throws InterruptedException {
            return receiveQueue.take();
        }
    }

    class CountingPipedOutputStream extends PipedOutputStream {
        int count = 0;
        
        @Override
        public void write(int b) throws IOException {
            super.write(b);
            count++;
        }
        
        @Override
        public void write(byte[] buf, int off, int len) throws IOException {
            super.write(buf, off,len);
            count+=len;
        }
        
        public int getCounter() {
            return count;
        }
        
        public void resetCounter() {
            count = 0;
        }
    }
}
