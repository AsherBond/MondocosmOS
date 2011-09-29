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
package org.jdesktop.wonderland.web.wfs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.jdesktop.wonderland.tools.wfs.WFS;

/**
 * @author Jordan Slott <jslott@dev.java.net>
 * @author kaplanj
 * @author Bernard Horan
 */
@XmlRootElement(name="wfs-recording")
public class WFSRecording extends WFSRoot {
    /* The logger for the WFSRecording */
    private static final Logger logger = Logger.getLogger(WFSRecording.class.getName());
    /**
     * The name of the WFS recording description file relative to the
     * recording root directory.
     */
    private static final String RECORDING_DESC = "recording.xml";

    private static final String POSITION_DESC = "position.xml";

    /**
     * The name of the WFS recording changes file relative to the
     * recording root directory.
     */
    private static final String CHANGES_DESC = "changes.xml";

    /** The name of the WFS directory relative to the recording root directory */
    public static final String RECORDING_WFS = "world-wfs";

    /* The location (beneath the wfs root) of the wfs recording directories */
    public static final String RECORDINGS_DIR = "recordings";

    /* the encoding for the changes xml file */
    final private static String ENCODING = "ISO-8859-1";

    /* the date formatter for writing the timestamp in the changes file */
    final private static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

    /* the date formatter for writing the dublin core date in the changes file */
    final private static SimpleDateFormat DC_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

    /* The JAXB context for later use */
    private static JAXBContext context = null;

    /** The timestamp for this recording */
    private Date timestamp;

    /** The description of this recording */
    private String description;

    @XmlTransient
    /* The writer on which the changes are written */
    private PrintWriter changesWriter;

    /* Create the XML marshaller and unmarshaller once for all ModuleInfos */
    static {
        try {
            context = JAXBContext.newInstance(WFSRecording.class);
        } catch (javax.xml.bind.JAXBException excp) {
            Logger.getLogger(WFSRecording.class.getName()).log(Level.WARNING,
                    "[WFS] Unable to create JAXBContext", excp);
        }
    }

    /**
     * No-arg constructor.  Use getInstance() instead.
     */
    public WFSRecording() {
    }

    /**
     * Get the name of this recording
     * @return name the name of this recording
     */
    @XmlTransient
    @Override
    public String getName() {
        return super.getName();
    }

    /**
     * Set the name of this recording.  This will rename the directory, so
     * be careful
     * @param newName the new name for this recording
     * @throws IOException if there is an error renaming
     */
    public void setName(String newName) throws IOException {
        // get the current name
        String oldName = getName();
        String oldPath = getRootPath();

        File parent = getDirectory().getParentFile();
        File newDir = new File(parent, newName);

        if (newDir.exists()) {
            throw new IOException("New directory " + newDir + " exists");
        }

        if (!getDirectory().renameTo(newDir)) {
            throw new IOException("Unable to rename " + getDirectory() +
                                  " to " + newDir);
        }

        // update our internal state
        setDirectory(newDir);
        setWfs(newDir);

        // notify the manager
        WFSManager.getWFSManager().renameRecording(oldName, oldPath, this);
    }

    /**
     * Get the description of this recording
     * @return the user-provided description of this recording, or null if
     * no description was provided
     */
    @XmlTransient
    public String getDescription() {
        return getDescriptionInternal();
    }

    /**
     * Set the description of this recording
     * @param description the description to set
     * @throws IOException if there is an error saving the updated description
     */
    public void setDescription(String description) throws IOException {
        setDescriptionInternal(description);
        save();
    }

    /**
     * Get the description if this recording.  Used internally by jaxb.
     */
    @XmlElement(name="description")
    protected String getDescriptionInternal() {
        return description;
    }

    /**
     * Set the description of this recording but don't rewrite the value.  Used
     * internally by jaxb
     * @param description the description to set
     */
    protected void setDescriptionInternal(String description) {
        this.description = description;
    }


    /**
     * Get the top-level directory for this recording
     * @return the recording directory
     */
    @XmlTransient
    @Override
    public File getDirectory() {
        return super.getDirectory();
    }
 
    /**
     * Get the full path to this recording from the root
     * @return the root path
     */
    @XmlTransient
    @Override
    public String getRootPath() {
        return RECORDINGS_DIR + "/" + getName() + "/" + RECORDING_WFS;
    }

    /**
     * Get the timestamp for this recording
     * @return the timestamp
     */
    @XmlTransient
    public Date getTimestamp() {
        return getTimestampInternal();
    }

    /**
     * Set the timestamp for this recording
     * @param timestamp the timestamp to set
     * @throws IOException if there is an error setting the timestamp
     */
    public void setTimestamp(Date timestamp) throws IOException {
        setTimestampInternal(timestamp);
        save();
    }

     /**
     * Get the timestamp for this recording.  Used internally by jaxb
     * @return the timestamp
     */
    @XmlElement(name="timestamp")
    protected Date getTimestampInternal() {
        return timestamp;
    }

    /**
     * Set the timestamp for this recording.  Used internally by jaxb.
     * @param timestamp the timestamp to set
     */
    public void setTimestampInternal(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Get the WFS associated with this recording
     * @return the WFS object associated with this recording
     */
    @XmlTransient
    @Override
    public WFS getWfs() {
        return super.getWfs();
    }

    /**
     * Set the WFS associated with this recording.  Only done at restore time.
     * @param dir the base directory to read the WFS from
     */
    @Override
    protected void setWfs(File dir) throws IOException {
        // decode the WFS if it exists
        File wfsDir = new File(dir, RECORDING_WFS);
        super.setWfs(wfsDir);
    }

    /**
     * Create a new changes file.
     * @param timestamp the starttime at which changes are written
     * @throws IOException 
     */
    public void createChangesFile(long timestamp) throws IOException {
        // read the changes file if it exists
        File cFile = getChangesFile();
        if (cFile.exists()) {
            logger.warning("changes file for " + getName() + " already exists, deleting");
            cFile.delete();
        }

        changesWriter = new PrintWriter(new FileOutputStream(cFile), true);
        changesWriter.println("<?xml version=\"1.0\" encoding=\"" + ENCODING + "\"?>");
        changesWriter.println("<Wonderland_Recorder date=\"" + DATE_FORMATTER.format(new Date()) + "\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\">");
         changesWriter.println("<dublinCore>");
        changesWriter.println("<dc:title>");
        changesWriter.println(this.getName());
        changesWriter.println("</dc:title>");
        if (this.getDescription() != null) {
            changesWriter.println("<dc:description>");
            changesWriter.println(this.getDescription());
            changesWriter.println("</dc:description>");
        }
        changesWriter.println("<dc:date>");
        changesWriter.println(DC_DATE_FORMATTER.format(new Date()));
        changesWriter.println("</dc:date>");
        changesWriter.println("</dublinCore>");
        changesWriter.println("<Wonderland_Changes timestamp=\"" + timestamp + "\">");
    }

    /**
     * Record the position info on the position file
     * @param positionInfo an xml-ised description of the position of a cell
     * @throws java.io.FileNotFoundException if we can't find the position file
     */
    public void recordPositionInfo(String positionInfo) throws FileNotFoundException {
        // read the changes file if it exists
        File pFile = getPositionFile();
        if (pFile.exists()) {
            logger.warning("position file for " + getName() + " already exists, deleting");
            pFile.delete();
        }
        PrintWriter positionWriter = new PrintWriter(new FileOutputStream(pFile), true);
        positionWriter.append(positionInfo);
        positionWriter.close();
    }
    

    /**
     * Record a change on the changesWriter.<br>
     * Delegate this to the argument
     * @param writer an object responsible for writing the change on the changesWriter
     */
    public void recordChange(WFSRecordingWriter writer) {
        writer.recordChange(changesWriter); 
    }

    /**
     * Write out the footer for the printWriter and then close the writer
     * (and indirectly the underlying file)
     * @param timestamp the timestamp for the message
     */
    public void closeChangesFile(long timestamp) {
        //Add a message to indicate that we've reached the end of the recording
        //This is read by the playback mechanism to indicate to the user that
        //playback has completed
        changesWriter.println("<EndMessage timestamp=\"" + timestamp + "\"/>");
        changesWriter.println("</Wonderland_Changes>");
        changesWriter.println("</Wonderland_Recorder>");
        changesWriter.close();
    }

    /**
     * Public accessor for the changes file for this recording
     * @return the changes file, may be null or non-existent file
     */
    public File getChangesFile() {
        return new File(getDirectory(), CHANGES_DESC);
    }

    /**
     * Public accessor for the position file for this recording
     * @return the position file, may be null or non-existent file
     */
    public File getPositionFile() {
        return new File(getDirectory(), POSITION_DESC);
    }


    /**
     * Gets an instance of WFSRecording for the given directory.  If there
     * is no WFS recording in the directory, it will be created.
     *
     * @param dir the directory to get a recording for
     * @throws IOException if there is an error reading the directory
     */
    public static WFSRecording getInstance(File dir) throws IOException {
        WFSRecording recording;

        // read the description file if it exists
        File recordingDesc = new File(dir, RECORDING_DESC);
        if (recordingDesc.exists()) {
            // if the recording description file exists, read it in
            FileReader r = new FileReader(recordingDesc);
            try {
                Unmarshaller unmarshaller = context.createUnmarshaller();
                recording = (WFSRecording) unmarshaller.unmarshal(r);
            } catch (JAXBException je) {
                IOException ioe = new IOException("Error reading recording " +
                        " description from " + recordingDesc);
                ioe.initCause(je);
                throw ioe;
            }
        } else {
            // otherwise, just start with an empty one
            recording = new WFSRecording();
        }

        // fill in the root directory & wfs
        recording.setDirectory(dir);
        recording.setWfs(dir);

        // all set
        return recording;
    }

    /**
     * Save the XML description
     * @throw IOException Upon error writing the XML file
     */
    public void save() throws IOException {
        File recordingDesc = new File(getDirectory(), RECORDING_DESC);
        FileWriter fw = new FileWriter(recordingDesc);

        try {
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", true);
            marshaller.marshal(this, fw);
        } catch (JAXBException je) {
            IOException ioe = new IOException("Error writing recording to " +
                                              recordingDesc);
            ioe.initCause(je);
            throw ioe;
        }
    }

    
}
