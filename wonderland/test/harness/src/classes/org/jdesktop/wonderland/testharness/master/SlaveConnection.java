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
package org.jdesktop.wonderland.testharness.master;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.jdesktop.wonderland.testharness.common.TestReply;
import org.jdesktop.wonderland.testharness.common.TestRequest;

/**
 * 
 * @author paulby
 */
public class SlaveConnection extends Thread {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private TestDirector director;
    private boolean done = false;
    private int slaveID;
    
    private static final Logger logger = Logger.getLogger(SlaveConnection.class.getName());

    List<SlaveConnectionListener> listeners = new CopyOnWriteArrayList();
    private List<TestReplyListener> replyListeners = new CopyOnWriteArrayList();


    SlaveConnection(Socket socket, int slaveID) {
        super("SlaveConnection");
        this.slaveID = slaveID;
        
        System.out.println("New Slave Controller "+slaveID);
        this.socket = socket;
        try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(MasterMain.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        start();
    }
    
    void setDirector(TestDirector director) {
        this.director = director;
    }
    
    TestDirector getDirector() {
        return director;
    }

    @Override
    public void run() {
        try {
            while(!done) {
                try {
                    Object msg = in.readObject();
                    if (msg instanceof LogRecord) {
                        LogRecord logR = (LogRecord)msg;
                        logR.setLoggerName("slave"+slaveID+":"+logR.getLoggerName());
                        logger.log(logR);
                    } else if (msg instanceof TestReply) {
                        System.err.println("GOT REPLY "+msg);
                        for(TestReplyListener l : replyListeners)
                            l.received((TestReply)msg);
                    }
                } catch(OptionalDataException e) {
                    Logger.getLogger(SlaveConnection.class.getName()).log(Level.SEVERE, "Exception length "+((OptionalDataException)e).length, e);
                } catch(EOFException eof) {
                    done=true;
                } catch (IOException ex) {
                    Logger.getLogger(SlaveConnection.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(SlaveConnection.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } finally {
            for (SlaveConnectionListener listener : listeners) {
                listener.disconnected(this);
            }
        }
    }

    public void send(TestRequest request) {
        System.out.println("Sending Request "+request);
               
        try {
            out.writeObject(request);
        } catch (IOException ex) {
            Logger.getLogger(MasterMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addReplyListener(TestReplyListener listener) {
        replyListeners.add(listener);
    }

    public void addConnectionListener(SlaveConnectionListener listener) {
        listeners.add(listener);
    }

    public void removeConnectionListener(SlaveConnectionListener listener) {
        listeners.remove(listener);
    }

    public interface SlaveConnectionListener {
        public void disconnected(SlaveConnection connection);
    }

    public interface TestReplyListener {
        public void received(TestReply reply);
    }
}
