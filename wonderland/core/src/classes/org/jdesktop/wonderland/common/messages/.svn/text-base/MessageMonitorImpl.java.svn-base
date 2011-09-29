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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A simple static implementation of MessageMonitor. This should
 * work on any client and on a single node DS server. 
 * 
 * @author paulby
 */
public class MessageMonitorImpl implements MessageMonitor {

    // Totals since monitor was attached to MessagePacker
    private Map<String, MonitorRecord> totalData = new LinkedHashMap(50, 0.75f, true);
    
    public void sending(Message msg, int size) {
        
        MonitorRecord rec = totalData.get(msg.getClass().getName());
        if (rec==null) {
            rec = new MonitorRecord();
            totalData.put(msg.getClass().getName(), rec);
        }
        rec.sending(size);
    }

    public void received(Message msg, int size) {
        MonitorRecord rec = totalData.get(msg.getClass().getName());
        if (rec==null) {
            rec = new MonitorRecord();
            totalData.put(msg.getClass().getName(), rec);
        }
        rec.receiving(size);
    }

    class MonitorRecord {
        int sendMsgCount=0;
        int recvMsgCount=0;
        int sendTotalSize=0;
        int recvTotalSize=0;
        
        public void sending(int size) {
            sendMsgCount++;
            sendTotalSize+=size;
        }
        
        public void receiving(int size) {
            recvMsgCount++;
            recvTotalSize+=size;
        }
    }
}
