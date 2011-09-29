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
package org.jdesktop.wonderland.client.jme;

import com.jme.renderer.jogl.JOGLContextCapabilities;
import com.jme.system.DisplaySystem;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;
import org.jdesktop.mtgame.RenderManager;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.mtgame.processor.WorkProcessor.WorkCommit;

/**
 * Singleton data store for viewing logs
 * @author Jonathan Kaplan <jonathankap@gmail.com>
 */
public enum LogViewer {
    INSTANCE;
    
    /** the log handler */
    private LogViewerHandler handler;

    /** the queue of work to process */
    private List<LogRecord> workQueue = new LinkedList<LogRecord>();

    /** the list of currently displayed records */
    private final List<LogEntry> entries = new LinkedList<LogEntry>();
    
    /** the maximum number of entries */
    private int maxEntries = 1000;

    /** whether the viewer is visible on startup */
    private boolean visibleOnStartup = false;
    
    /** the actual log viewer frame */
    private LogViewerFrame frame;
    
    LogViewer() {
        // restore default values
        restore();
    }
    
    private class LogEntry {
        LogRecord record;
        int length;
    }
    
    private void restore() {
        Preferences prefs = Preferences.userNodeForPackage(LogViewer.class);

        maxEntries = prefs.getInt("maxEntries", maxEntries);

        Logger root = LogManager.getLogManager().getLogger("");
        Level levelVal = Level.parse(prefs.get("rootLevel", root.getLevel().getName()));
        root.setLevel(levelVal);

        visibleOnStartup = prefs.getBoolean("visibleOnStartup", visibleOnStartup);
    }
    
    public void setVisible(final boolean visible) {
        if (SwingUtilities.isEventDispatchThread()) {
            doSetVisible(visible);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                   doSetVisible(visible);
               } 
            });
        }
    }
    
    /**
     * Must be called on AWT event thread
     */
    private void doSetVisible(boolean visible) {
        LogViewerFrame lvf;
        
        if (visible) {
            lvf = getFrame(true);
            lvf.setVisible(true);
            lvf.toFront();
        } else {
            lvf = getFrame(false);
            if (lvf != null) {
                lvf.setVisible(false);
            }
        }
    }
    
    public LogViewerHandler getHandler() {
        return handler;
    }

    public void setHandler(LogViewerHandler handler) {
        this.handler = handler;
    }

    public int getMaxEntries() {
        return maxEntries;
    }

    public void setMaxEntries(int maxEntries) {
        if (maxEntries <= 0) {
            throw new IllegalArgumentException("Maximum entries must be " +
                                               "more than 0");
        }

        this.maxEntries = maxEntries;

        // save a preference
        Preferences prefs = Preferences.userNodeForPackage(LogViewer.class);
        prefs.putInt("maxEntries", maxEntries);

        // processing records now will correctly remove any records over
        // the new limit
        processRecords();
    }

    public Level getRootLogLevel() {
        return Logger.getLogger("").getLevel();
    }

    public void setRootLogLevel(Level rootLevel) {
        Logger.getLogger("").setLevel(rootLevel);

        // save a preference
        Preferences prefs = Preferences.userNodeForPackage(LogViewer.class);
        prefs.put("rootLevel", rootLevel.getName());
    }

    public boolean isVisibleOnStartup() {
        return visibleOnStartup;
    }

    public void setVisibleOnStartup(boolean visibleOnStartup) {
        this.visibleOnStartup = visibleOnStartup;

        // save a preference
        Preferences prefs = Preferences.userNodeForPackage(LogViewer.class);
        prefs.putBoolean("visibleOnStartup", visibleOnStartup);
    }
    
    /**
     * Get the frame associated with this viewer. Must be called on AWT
     * event thread, since the frame is created if it doesn't exist.
     * @param create if true, create the frame if it doesn't exist
     * @return the frame, or null if the frame doesn't exist and create is
     * false
     */
    protected LogViewerFrame getFrame(boolean create) {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("Must be on AWT event thread");
        }
        
        if (frame == null && create) {
            frame = new LogViewerFrame();
            
            // initialize with existing records
            StringBuilder str = new StringBuilder();
            for (LogEntry entry : entries) {
                format(entry.record, str);
            }
            frame.addRecord(str.toString(), 0);
        }
        
        return frame;
    }

    /**
     * Called by the handler to add a new record to the log. This method queues
     * the record and schedules the actual update to happen on the AWT
     * event thread.
     * @param record the record to process
     */
    protected synchronized void addRecord(LogRecord record) {
        // OWL issue #160: queue up multiple events to improve performance

        // if the queue is empty, then we need to schedule a task
        // to clear the list. If the list is not empty, it means a task
        // has been scheduled but has not yet run. In that case, we can
        // just add our elements to the list and they will be processed
        // by the eventual task
        boolean schedule = workQueue.isEmpty();

        // add our record
        workQueue.add(record);

        // schedule a task if necessary
        if (schedule) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    processRecords();
                }
            });
        }
    }

    /**
     * Called on the AWT event thread to process all outstanding records
     */
    protected void processRecords() {
        // OWL issue #160: handle multiple events to improve performance

        // first, get the set of records to process, making sure we have the
        // lock to prevent additions
        Collection<LogRecord> records;
        synchronized (this) {
            // copy the records into a new collection
            records = new ArrayList<LogRecord>(workQueue);

            // clear the work queue. This ensures that the next time an element
            // is added, a new task will be scheduled
            workQueue.clear();
        }

        // process each record
        StringBuilder str = new StringBuilder();
        for (LogRecord record : records) {
            // format the record and add it to the strig builder
            int length = format(record, str);

            // add it to the list of records
            LogEntry entry = new LogEntry();
            entry.record = record;
            entry.length = length;
            entries.add(entries.size(), entry);
        }

        // if we are now over the maximum number of entries, remove as many
        // as we need
        int removeLen = 0;
        while (entries.size() > getMaxEntries()) {
            LogEntry entry = entries.remove(0);
            removeLen += entry.length;
        }

        // add data to the frame, if it exists
        LogViewerFrame lvf = getFrame(false);
        if (lvf != null) {
            lvf.addRecord(str.toString(), removeLen);
        }
    }
    
    protected String generateErrorReport() {
        final StringBuilder out = new StringBuilder();
        out.append("Error report generated ").
                append(DateFormat.getDateTimeInstance().format(new Date())).
                append("\n");
        out.append("\n");

        // java version, etc
        out.append("-------- System Information --------\n");
        out.append("Java version: ").append(System.getProperty("java.version")).append("\n");
        out.append("Java vendor:").append(System.getProperty("java.vendor")).append("\n");
        out.append("OS:").append(System.getProperty("os.name")).append("\n");
        out.append("OS version: ").append(System.getProperty("os.version")).append("\n");
        out.append("OS architecture: ").append(System.getProperty("os.arch")).append("\n");
        out.append("\n");
        out.append("Max memory: ").append(Runtime.getRuntime().maxMemory()).append("\n");
        out.append("Total memory: ").append(Runtime.getRuntime().totalMemory()).append("\n");
        out.append("Free memory: ").append(Runtime.getRuntime().freeMemory()).append("\n");
        out.append("\n");

        // graphics
        out.append("-------- Graphics Information --------\n");

        // grab display information from the renderer
        final Semaphore gs = new Semaphore(0);        
        SceneWorker.addWorker(new WorkCommit() {
            public void commit() {
                try {
                    DisplaySystem ds = DisplaySystem.getDisplaySystem("JOGL");
                    out.append("Display adapter:  ").append(ds.getAdapter()).append("\n");
                    out.append("Display vendor:   ").append(ds.getDisplayVendor()).append("\n");
                    out.append("Driver version:   ").append(ds.getDriverVersion()).append("\n");
                    out.append("Display renderer: ").append(ds.getDisplayRenderer()).append("\n");
                    out.append("API Version:      ").append(ds.getDisplayAPIVersion()).append("\n");
                    out.append("\n\n");
                } finally {
                    gs.release();
                } 
            }
        });
        
        // wait for the renderer to run the worker
        try {
            gs.acquire();
        } catch (InterruptedException ie) {
            // ignore
        }
        
        RenderManager rm = WorldManager.getDefaultWorldManager().getRenderManager();
        JOGLContextCapabilities cap = rm.getContextCaps();
        out.append("GL_ARB_fragment_program...").append(cap.GL_ARB_fragment_program).append("\n");
        out.append("GL_ARB_fragment_shader...").append(cap.GL_ARB_fragment_shader).append("\n");
        out.append("GL_ARB_shader_objects...").append(cap.GL_ARB_shader_objects).append("\n");
        out.append("GL_ARB_texture_non_power_of_two...").append(cap.GL_ARB_texture_non_power_of_two).append("\n");
        out.append("GL_ARB_vertex_buffer_object...").append(cap.GL_ARB_vertex_buffer_object).append("\n");
        out.append("GL_ARB_vertex_program...").append(cap.GL_ARB_vertex_program).append("\n");
        out.append("GL_ARB_vertex_shader...").append(cap.GL_ARB_vertex_shader).append("\n");
        out.append("GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS_ARB...").append(cap.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS_ARB).append("\n");
        out.append("GL_MAX_FRAGMENT_UNIFORM_COMPONENTS_ARB...").append(cap.GL_MAX_FRAGMENT_UNIFORM_COMPONENTS_ARB).append("\n");
        out.append("GL_MAX_TEXTURE_COORDS_ARB...").append(cap.GL_MAX_TEXTURE_COORDS_ARB).append("\n");
        out.append("GL_MAX_TEXTURE_IMAGE_UNITS_ARB...").append(cap.GL_MAX_TEXTURE_IMAGE_UNITS_ARB).append("\n");
        out.append("GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT...").append(cap.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT).append("\n");
        out.append("GL_MAX_TEXTURE_UNITS...").append(cap.GL_MAX_TEXTURE_UNITS).append("\n");
        out.append("GL_MAX_VARYING_FLOATS_ARB...").append(cap.GL_MAX_VARYING_FLOATS_ARB).append("\n");
        out.append("GL_MAX_VERTEX_ATTRIBS_ARB...").append(cap.GL_MAX_VERTEX_ATTRIBS_ARB).append("\n");
        out.append("GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS_ARB...").append(cap.GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS_ARB).append("\n");
        out.append("GL_MAX_VERTEX_UNIFORM_COMPONENTS_ARB...").append(cap.GL_MAX_VERTEX_UNIFORM_COMPONENTS_ARB).append("\n");
        out.append("GL_SGIS_generate_mipmap...").append(cap.GL_SGIS_generate_mipmap).append("\n");
        out.append("GL_SHADING_LANGUAGE_VERSION_ARB...").append(cap.GL_SHADING_LANGUAGE_VERSION_ARB).append("\n");
        out.append("GL_VERSION_1_2...").append(cap.GL_VERSION_1_2).append("\n");
        out.append("GL_VERSION_2_0...").append(cap.GL_VERSION_2_0).append("\n");
        out.append("GL_VERSION_2_1...").append(cap.GL_VERSION_2_1).append("\n");
        out.append("GL_VERSION_3_0...").append(cap.GL_VERSION_3_0).append("\n");
        out.append("\n");

        // error log
        out.append("-------- Error Log --------\n");
        for (LogEntry entry : entries) {
            format(entry.record, out);
        }
        out.append("\n");

        // thread dump
        out.append("-------- Threads --------\n");
        for (Map.Entry<Thread, StackTraceElement[]> e : Thread.getAllStackTraces().entrySet()) {
            out.append(e.getKey().getName()).append(" ").append(e.getKey().getState()).append("\n");
            for (StackTraceElement ste : e.getValue()) {
                out.append("    ").append(ste.getClassName());
                out.append(".").append(ste.getMethodName());
                if (ste.isNativeMethod()) {
                    out.append("(native)");
                } else {
                    out.append("(").append(ste.getFileName()).append(":");
                    out.append(ste.getLineNumber()).append(")");
                }
                out.append("\n");

            }
            out.append("\n");

        }
        out.append("\n");

        return out.toString();
    }
    
    /**
     * Format the given record, and add it to the given string builder. Return
     * the length of text added to the builder.
     */
    static int format(LogRecord record, StringBuilder builder) {
        int startLen = builder.length();
        
        builder.append(record.getLevel());
        builder.append(" ");
        builder.append(DateFormat.getTimeInstance().format(new Date(record.getMillis())));
        builder.append(" ");
        builder.append(record.getSourceClassName());
        builder.append(" ");
        builder.append(record.getSourceMethodName());
        builder.append("\n");

        if (record.getMessage() != null) {
            builder.append(record.getMessage());
            builder.append("\n");
        }

        if (record.getThrown() != null) {
            builder.append(formatThrowable(record.getThrown()));
        }

        // return the difference in length from when we started
        return builder.length() - startLen;
    }

    static String formatThrowable(Throwable t) {
        StringWriter out = new StringWriter();
        t.printStackTrace(new PrintWriter(out));
        return out.toString();
    }

}
