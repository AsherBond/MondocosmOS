/**
 * Open Wonderland
 *
 * Copyright (c) 2011, Open Wonderland Foundation, All Rights Reserved
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
package org.jdesktop.wonderland.client.assetmgr;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.InternalAPI;

/**
 * Track the IO and notify the listeners of progress
 *
 * @author paulby
 */
@InternalAPI
public class TrackingInputStream extends FilterInputStream {
    private static final Logger LOGGER =
            Logger.getLogger(TrackingInputStream.class.getName());
    
    private int byteCount = 0;
    private InputStream steam;
    private ArrayList<ProgressListener> listeners = null;
    private int totalSize;
    private int notifySize;
    private int nextNotify;
    
    /**
     * Notify the user every time the number of byte downloaded is a multiple
     * of notifySize.
     */
    public TrackingInputStream(InputStream stream) {
        super(stream);
    }
    
    @Override
    public int read() throws IOException {
        int ret = in.read();
        if (ret == -1) {
            byteCount = totalSize;
        } else
            byteCount++;

        if (byteCount > nextNotify || byteCount == totalSize)
            notifyListeners();
        return ret;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int ret = in.read(b);
        if (ret == -1) {
            byteCount = totalSize;
        } else
            byteCount += ret;

        if (byteCount > nextNotify || byteCount == totalSize)
            notifyListeners();
        return ret;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int ret = in.read(b, off, len);
        if (ret == -1) {
            byteCount = totalSize;
        } else
            byteCount += ret;

        if (byteCount > nextNotify || byteCount == totalSize)
            notifyListeners();

        return ret;
    }

    private void notifyListeners() {
        int percentage;

        if (listeners != null) {
            if (totalSize == -1) {
                percentage = -1;
            }
            else if (byteCount == totalSize) {
                percentage = 100;
            }
            else {
                percentage = (int) (byteCount / (float) totalSize * 100f);
            }
            
            if (listeners != null)
                for (ProgressListener listener : listeners) {
                    if (listener != null) {
                        try {
                            listener.downloadProgress(byteCount, percentage);
                        } catch (Exception ex) {
                            // ignore errors in the listener
                            LOGGER.log(Level.WARNING, "Error in listener", ex);
                        }
                    }
                }

            while (nextNotify < byteCount)
                nextNotify += notifySize;

            if (percentage == 100 && listeners != null)
                listeners.clear();      // Make sure we dont send two 100 perc. This would happen if we read all the bytes and then get a close
        }
    }

    @Override
    public void close() throws IOException {
        byteCount = totalSize;
        notifyListeners();
        super.close();
        if (listeners != null)
            listeners.clear();
    }

    public void setListener(ProgressListener listener, int notifySize, int totalSize) {
        if (listener == null)
            return;
        this.totalSize = totalSize;
        this.notifySize = notifySize;
        while (nextNotify < byteCount)
            nextNotify += notifySize;
        if (listeners == null)
            listeners = new ArrayList();
        listeners.add(listener);
    }

    public boolean removeListener(ProgressListener listener) {
        return listeners.remove(listener);
    }

    public interface ProgressListener {
        public void downloadProgress(int readBytes, int percentage);
    }
}
