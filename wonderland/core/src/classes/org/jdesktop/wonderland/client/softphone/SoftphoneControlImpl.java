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
 * Copyright (c) 2004-2010, Sun Microsystems, Inc., All Rights Reserved
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
package org.jdesktop.wonderland.client.softphone;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.ThreadManager;

public class SoftphoneControlImpl implements SoftphoneControl {
    private static final Logger logger =
            Logger.getLogger(SoftphoneControlImpl.class.getName());
 
    private Process softphoneProcess;
    private OutputStream softphoneOutputStream;
    private ProcOutputListener stdOutListener;
    private ProcOutputListener stdErrListener;
    private Pinger pinger;
    
    private String username;
    private String registrar;
    private int registrarTimeout;
    private String localHost;
    
    private String softphoneAddress;

    private boolean connected;
    
    private boolean exitNotificationSent;

    private boolean isTooLoud;

    private static SoftphoneControlImpl softphoneControlImpl;

    private String callID;

    private String problem;

    private SoftphoneControlImpl() {
    }

    /**
     * State of the softphone
     */
    enum State { VISIBLE, INVISIBLE, MUTED, UNMUTED, CONNECTED, 
	DISCONNECTED, EXITED, TOO_LOUD, PROBLEM
    }
    
    /**
     * Gets the one instance of SoftphoneControlImpl
     */
    public static SoftphoneControlImpl getInstance() {
        if (softphoneControlImpl == null) {
            softphoneControlImpl = new SoftphoneControlImpl();
        }
        return softphoneControlImpl;
    }

    /**
     * Start up the softphone
     */
    public String startSoftphone(String username, String registrar,
	    int registrarTimeout, String localHost) throws IOException {
    
	this.username = username.replaceAll("\\p{Punct}", "_");

	String previousRegistrar = this.registrar;

	this.registrar = registrar;
	this.registrarTimeout = registrarTimeout;
	this.localHost = localHost;

	if (localHost != null && localHost.equalsIgnoreCase("default")) {
	    localHost = null;
	}
        
        // if it's already running, send it a command to re-register
        if (isRunning()) {
	    if (previousRegistrar == null ||
		    previousRegistrar.equals(registrar) == false) {

	        register(registrar);
	    } else {
		logger.fine(
		    "startSoftphone:  new registrar same as previous "
		    + " registrar");
	    }

	    return softphoneAddress;
        }

        // is this a valid JVM in which to run?
        String javaVersion = System.getProperty("java.version");
        if (!JavaVersion.isMacOSX() &&
                (JavaVersion.compareVersions(javaVersion, "1.5.0") < 0))
        {
	    logger.warning("java.version is " + javaVersion);
            logger.warning("Softphone needs 1.5.0 or later to run");
	    throw new IOException("Softphone needs java 1.5.0 or later to run");
        }
        
	quiet = false;

	exitNotificationSent = false;

        // launch the sucker!
        String[] command = getSoftphoneCommand(username, registrar,
	    registrarTimeout, localHost, quality);

	if (command == null) {
	    logger.warning("Unable to find softphone.jar.  "
		+ "You cannot use the softphone!");

	    softphoneProcess = null;

	    throw new IOException("Unable to find softphone.jar");
	}

	String s = "";

        for (int i = 0; i < command.length; i++) {
	    s += " " + command[i];
        }

        logger.warning("Launching communicator: " + s);

        softphoneProcess = Runtime.getRuntime().exec(command);
            
	// open communication channels to the new process
        softphoneOutputStream =
            new BufferedOutputStream(softphoneProcess.getOutputStream());
        stdOutListener = new ProcOutputListener(softphoneProcess.getInputStream());
        stdErrListener = new ProcOutputListener(softphoneProcess.getErrorStream());
        stdOutListener.start();
        stdErrListener.start();
        pinger = new Pinger();
        pinger.start();

	if (isVisible) {
	    setVisible(true);
	}

        return waitForAddress();
    }
    
    /**
     * Attempts to wait for the softphone to register itself and get
     * a sip address.  Displays a waiting dialog after a time, and allows
     * the user to cancel the wait.
     * <p>This method SHOULD NOT be called from the AWT event thread.
     * @return the sip address of the softphone, or null if the softphone
     * could not be launched or was canceled by the user.
     */
    private String waitForAddress() throws IOException {
	long start = System.currentTimeMillis();

	synchronized (this) {
	    while (softphoneAddress == null && System.currentTimeMillis() - start < 60000) {
	        try {
                    wait(60000);
	        } catch (InterruptedException e) {
		    System.out.println("INTERRUPTEDEXCEPTION!");
		    e.printStackTrace();
	        }
	    }

	    if (softphoneAddress == null) {
                logger.warning("Softphone failed to start!");
	    }
	}

	return softphoneAddress;
    }

    /**
     * Get the location of softphone.jar.  The location is determined as
     * follows:
     *     <li>look for the file specified by the system property
     *         com.sun.mc.softphone.jar
     *     <li>Search the classpath for softphone.jar
     *
     * @return a path that points to softphone.jar, or null if the path
     * cannot be found
     */
    private String getJarPath() {
        // try the system property
        String jarPath = System.getProperty(SoftphoneControl.SOFTPHONE_PROP);

        if (jarPath != null && checkPath(jarPath)) {
            return jarPath;
        }

        // try the classpath
        String paths[] = System.getProperty("java.class.path").split(
                System.getProperty("path.separator"));

        for (int i=0; i<paths.length; i++) {
	    String path = paths[i];

            if (path.endsWith("softphone.jar")) {
		if (checkPath(path)) {
                    return path;
		}
            }
	
	    path += File.separator + "softphone.jar";

	    if (checkPath(path)) {
                return path;
	    }
        }

        // no luck 
        return null;
    }

    /**
     * Check if the given path is a valid jar file
     * @param path the path
     * @return true if this is a valid jar file, or false if not
     */
    private boolean checkPath(String jarPath) {
        return new File(jarPath).exists();
    }
    
    /**
     * Gets a String array suitable for System.exec() that will launch the
     * softphone program on behalf of a particular user
     * @param username the name of the user to display in the softphone
     */
    private String[] getSoftphoneCommand(String username,
	    String registrar, int registrarTimeout, String localHost,
            AudioQuality quality) {

        String javaHome = System.getProperty("java.home");
        
        String softphonePath = getJarPath();
	if (softphonePath == null) {
	    return null;
	}

        String audioFile = System.getProperty("softphone.audio.file");
        boolean silent = Boolean.parseBoolean(System.getProperty("softphone.silent"));
        boolean debug = Boolean.parseBoolean(System.getProperty("softphone.debug"));

        // list of string to turn into a command
        List<String> command = new ArrayList<String>(11);
       
        command.add(javaHome + File.separator + "bin" + File.separator + "java");
	command.add("-Dsun.java2d.noddraw=true");
        
        // java options
        if (debug) {
            command.add("-Xdebug");
            command.add("-Xrunjdwp:transport=dt_socket,server=y,address=8895,suspend=n");
        }
        
        // the jar to run
        command.add("-jar");
        command.add(softphonePath);
        
        // everything below are arguments to that jar
        command.add("-mc");
        command.add("-u");
        command.add(username);
	command.add("-r");
	command.add(registrar);
	command.add("-stun");

	String[] tokens = registrar.split(":");
        String stun = tokens[0];
	int ix = stun.indexOf(";");
	if (ix >= 0) {
            stun = stun.substring(0, ix);
	}

	if (tokens.length >= 2) {
	    stun += ":" + tokens[1];
	}
        command.add(stun);

	if (registrarTimeout != 0) {
	    command.add("-t");
	    command.add(String.valueOf(registrarTimeout));
	}

	if (localHost != null) {
            command.add("-l");
	    command.add(localHost);
	}
              
        if (quality != null) {
            command.add("-sampleRate");
            command.add(String.valueOf(quality.sampleRate()));
            command.add("-channels");
            command.add(String.valueOf(quality.channels()));
            command.add("-transmitSampleRate");
            command.add(String.valueOf(quality.transmitSampleRate()));
            command.add("-transmitChannels");
            command.add(String.valueOf(quality.transmitChannels()));
        }
        
	if (isMuted == true) {
	    command.add("-mute");
	}

        if (audioFile != null) {
            command.add("-playTreatment");
            command.add(audioFile);
        }

        if (silent) {
            command.add("-silent");
        }

        return command.toArray(new String[command.size()]);
    }

    public void stopSoftphone() {
        close(null);

	synchronized (this) {
	    notifyAll();
	}
    }
    
    public void setCallID(String callID) {
	this.callID = callID;
    }

    public String getCallID() {
	return callID;
    }

    public void register(String registrarAddress) {
	try {
	    sendCommandToSoftphone("ReRegister=" + registrarAddress);
	} catch (IOException e) {
	}
    }

    public boolean isRunning() {
	if (softphoneProcess == null) {
	    return false;
	}

	try {
	    int exitValue = softphoneProcess.exitValue();	// softphone exited

	    logger.warning("Softphone exited with status " + exitValue);
            close(null); // Software phone was closed.

	    synchronized (this) {
	        notifyAll();
	    }

	    return false;
	} catch (IllegalThreadStateException e) {
	    return true;		// still running
	}
    }

    public boolean isConnected() throws IOException {
	if (isRunning() == false) {
	    throw new IOException("Softphone is not running");
	}

	return connected;
    }

    public boolean isTooLoud() {
	boolean isTooLoud = this.isTooLoud;

	this.isTooLoud = false;

	return isTooLoud;
    }

    private void restartSoftphone() {
	close(null);
	notifyListeners(State.EXITED);
    }
    
    private boolean isVisible;

    public boolean isVisible() {
	return isRunning() && isVisible;
    }

    public void setVisible(boolean isVisible) {
	if (isRunning() == false) {
	    notifyListeners(State.INVISIBLE);
	    return;
	}

	try {
	    if (isVisible) {
	        sendCommandToSoftphone("Show");
	    } else {
	        sendCommandToSoftphone("Hide");
	    }
	} catch (IOException e) {
	}
    }

    private boolean isMuted;

    public void mute(boolean isMuted) {
        try {
            if (isMuted) {
                sendCommandToSoftphone("Mute");
            } else {
                sendCommandToSoftphone("Unmute");
            }
	} catch (IOException e) {
	}
    }
    
    public boolean isMuted() {
	return isMuted;
    }

    private AudioQuality quality;

    public AudioQuality getAudioQuality() {
	return quality;
    }

    public void setAudioQuality(AudioQuality quality) {
	this.quality = quality;

	if (isRunning() == false) {
	    return;
	}

	try {
            sendCommandToSoftphone("sampleRate=" + quality.sampleRate());
            sendCommandToSoftphone("channels=" + quality.channels());
            sendCommandToSoftphone("transmitSampleRate=" + quality.transmitSampleRate());
            sendCommandToSoftphone("transmitChannels=" + quality.transmitChannels());
	} catch (IOException e) {
	}
    }

    public void recordReceivedAudio(String recordingPath)
            throws IOException {

        sendCommandToSoftphone("recordReceivedAudio="
            + recordingPath);
    }

    public void pauseRecordingReceivedAudio() throws IOException {
        sendCommandToSoftphone("pauseRecordingReceivedAudio");
    }

    public void resumeRecordingReceivedAudio() throws IOException {
        sendCommandToSoftphone("resumeRecordingReceivedAudio");
    }

    public void stopRecordingReceivedAudio() throws IOException {
        sendCommandToSoftphone("stopRecordingReceivedAudio");
    }

    public void sendCommandToSoftphone(String cmd) throws IOException {
        if (softphoneOutputStream == null) {
	    logger.warning(
		"Unable to send command to softphone, output stream is null "
		+ cmd);
	    throw new IOException(
		"Unable to send command to softphone, output stream is null "
		+ cmd);
        }

        synchronized(softphoneOutputStream) {
            logger.finest("SoftphoneControl sending command to softphone:  " + cmd);

	    try {
	        if (cmd.equals("Shutdown")) {
	    	    shuttingDown = true;
	        }

                byte bytes[] = (cmd+"\n").getBytes();
                softphoneOutputStream.write(bytes);
                softphoneOutputStream.flush();
            } catch (IOException e) {
		if (shuttingDown == false) {
                    e.printStackTrace();
                    //softphoneOutputStream = null;

		    logger.warning("SoftphoneControl exception:  " + e.getMessage());
		}

		//close(
                //    "There was an error trying to use the software phone.  "
                //    + "Please check your system's audio settings and try again.");
		throw e;
            }
        }
    }
    
    public void runLineTest() throws IOException {
        sendCommandToSoftphone("linetest");
    }

    public void logAudioProblem() throws IOException {
        sendCommandToSoftphone("stack");
    }

    private ArrayList<SoftphoneListener> listeners = 
	new ArrayList<SoftphoneListener>();

    public void addSoftphoneListener(SoftphoneListener listener) {
        synchronized(listeners) {
	    if (listeners.contains(listener)) {
		logger.warning("Duplicate listener!!!");
		return;
	    }

            listeners.add(listener);
        }
    }

    public void removeSoftphoneListener(SoftphoneListener listener) {
        synchronized(listeners) {
            listeners.remove(listener);
        }
    }

    public void startMicVuMeter(boolean startVuMeter) throws IOException {
	sendCommandToSoftphone("StartMicVuMeter=" + startVuMeter);
    }

    private ArrayList<MicrophoneInfoListener> microphoneInfoListeners = 
	new ArrayList<MicrophoneInfoListener>();

    public void addMicrophoneInfoListener(MicrophoneInfoListener listener) {
        synchronized(microphoneInfoListeners) {
	    if (microphoneInfoListeners.contains(listener)) {
		logger.warning("Duplicate listener!!!");
		return;
	    }

            microphoneInfoListeners.add(listener);
        }
    }

    public void removeMicrophoneInfoListener(MicrophoneInfoListener listener) {
        synchronized(microphoneInfoListeners) {
            microphoneInfoListeners.remove(listener);
        }
    }

    private void notifyMicrophoneInfoListeners(String data, boolean isVuMeterData) {
        ArrayList<MicrophoneInfoListener> listeners = new ArrayList<MicrophoneInfoListener>();

        synchronized(microphoneInfoListeners) {
            for (MicrophoneInfoListener listener : microphoneInfoListeners) {
                listeners.add(listener);
            }
	}

	for (MicrophoneInfoListener listener : listeners) {
	    if (isVuMeterData) {
	        listener.microphoneVuMeterValue(data);
	    } else {
		listener.microphoneVolume(data);
	    }
	}
    }

    public void startSpeakerVuMeter(boolean startSpeakerVuMeter) throws IOException {
	sendCommandToSoftphone("StartSpeakerVuMeter=" + startSpeakerVuMeter);
    }

    private ArrayList<SpeakerInfoListener> speakerInfoListeners = 
	new ArrayList<SpeakerInfoListener>();

    public void addSpeakerInfoListener(SpeakerInfoListener listener) {
        synchronized(speakerInfoListeners) {
	    if (speakerInfoListeners.contains(listener)) {
		logger.warning("Duplicate listener!!!");
		return;
	    }

            speakerInfoListeners.add(listener);
        }
    }

    public void removeSpeakerInfoListener(SpeakerInfoListener listener) {
        synchronized(speakerInfoListeners) {
            speakerInfoListeners.remove(listener);
        }
    }

    private void notifySpeakerInfoListeners(String data, boolean isVuMeterData) {
        ArrayList<SpeakerInfoListener> listeners = new ArrayList<SpeakerInfoListener>();

        synchronized(speakerInfoListeners) {
            for (SpeakerInfoListener listener : speakerInfoListeners) {
                listeners.add(listener);
            }
	}

	for (SpeakerInfoListener listener : listeners) {
	    if (isVuMeterData) {
	        listener.speakerVuMeterValue(data);
	    } else {
		listener.speakerVolume(data);
	    }
	}
    }

    private boolean quiet = false;

    private void lineReceived(ProcOutputListener source, String line) {
        if (source == stdErrListener) {
            logger.warning(line);
        } else if (source == stdOutListener) {
	    if (quiet == false) {	
                logger.info(line);
	    }
      
	    if (line.indexOf("Softphone Connected") >= 0) {
	    	quiet = true;
	    }

	    if (line.indexOf("TestUDPPort") >= 0) {
		notifyListenersTestUDPPort(line);
	 	return;
	    }

	    if (line.indexOf("Softphone Problem:") >= 0) {
		problem = line;
		notifyListeners(State.PROBLEM);
		return;
	    }

	    if (line.indexOf("MicVuMeterData:") >= 0) {
		String[] tokens = line.split(":");
                notifyMicrophoneInfoListeners(tokens[1], true);
	  	return;
	    }

	    if (line.indexOf("MicrophoneVolume:") >= 0) {
		String[] tokens = line.split(":");
                notifyMicrophoneInfoListeners(tokens[1], false);
	  	return;
	    }

	    if (line.indexOf("SpeakerVuMeterData:") >= 0) {
		String[] tokens = line.split(":");
                notifySpeakerInfoListeners(tokens[1], true);
	  	return;
	    }

	    if (line.indexOf("SpeakerVolume:") >= 0) {
		String[] tokens = line.split(":");
                notifySpeakerInfoListeners(tokens[1], false);
	  	return;
	    }

	    if (line.indexOf("Softphone Connected") >= 0) {
		connected = true;
		mute(isMuted);
                notifyListeners(State.CONNECTED);
	    } else if (line.indexOf("Softphone Disconnected") >= 0) {
		connected = false;
                notifyListeners(State.DISCONNECTED);
	    }

            // WARNING:  The Sip Communicator generates this exact
            // message.  If the communicator is changed,
            // you'll need to change searchString.
            String searchString = "SipCommunicator Public Address is '";
            int idx = line.indexOf(searchString);
            if (idx >= 0) {
                String addr = line.substring(idx + searchString.length());
                int ixEnd = addr.indexOf("'");
                if (ixEnd>0) {
                    addr = addr.substring(0, ixEnd);
                }

		synchronized (this) {
                    softphoneAddress = addr;

		    notifyAll();
		}

                logger.info("softphone address is '" + addr + "'");
            } else if (line.indexOf("Softphone is visible") >= 0) {
                isVisible = true;
                notifyListeners(State.VISIBLE);
            } else if (line.indexOf("Softphone is hidden") >= 0) {
                isVisible = false;
                notifyListeners(State.INVISIBLE);
            } else if (line.indexOf("Softphone Muted") >= 0) {
		isMuted = true;
                notifyListeners(State.MUTED);
            } else if (line.indexOf("Softphone Unmuted") >= 0) {
		isMuted = false;
                notifyListeners(State.UNMUTED);
            } else if (line.indexOf("Restart softphone now") >= 0) {
                restartSoftphone();
            } else if (line.indexOf("TOO LOUD") >= 0) {
		isTooLoud = true;
            }
        }
    }

    /**
     * Notify listeners of a change
     * @param visible if true, the change is visibility, if false, the
     * change is to the mute state
     * @param state the mute or visibility state
     */
    private void notifyListeners(State state) {
        synchronized(this.listeners) {
	    ArrayList<SoftphoneListener> listeners = new ArrayList<SoftphoneListener>();

	    for (SoftphoneListener listener : this.listeners) {
		listeners.add(listener);
	    }

	    if (state == State.EXITED) {
		if (exitNotificationSent) {
		    return;
		}

		exitNotificationSent = true;
	    }

	    for (SoftphoneListener listener : listeners) {
                switch (state) {
                case VISIBLE:
                    listener.softphoneVisible(true);
                    break;
                case INVISIBLE:
                    listener.softphoneVisible(false);
                    break;
                case MUTED:
                    listener.softphoneMuted(true);
                    break;
                case UNMUTED:
                    listener.softphoneMuted(false);
                    break;
                case CONNECTED:
                    listener.softphoneConnected(true);
                    break;
                case DISCONNECTED:
                    listener.softphoneConnected(false);
                    break;
                case EXITED:
                    listener.softphoneExited();
                    break;
		case PROBLEM:
		    listener.softphoneProblem(problem);
		    break;
                }
            }
	}
    }

    private void notifyListenersTestUDPPort(String line) {
	String[] tokens = line.split(":");

	if (tokens.length != 3) {
	    System.err.println("Missing parameters:  " + line);
	    return;
	}

	int port;

	try {
	    port = Integer.parseInt(tokens[1]);
	} catch (NumberFormatException e) {
	    System.err.println("Invalid port:  " + line);
	    return;
	}

	int duration;

	try {
	    duration = Integer.parseInt(tokens[2]);
	} catch (NumberFormatException e) {
	    System.err.println("Invalid duration:  " + line);
	    return;
	}

	for (SoftphoneListener listener : listeners) {
	    listener.softphoneTestUDPPort(port, duration);
	}
    }

    /**
     * Tickle the softphone every 5 seconds.  If it doesn't hear from us,
     * it will quit.
     */
    class Pinger extends Thread {
        public Pinger() {
            super(ThreadManager.getThreadGroup(), "Softphone pinger");
        }
        
        public void run() {
            try {
		logger.info("About to ping softphone for the first time...");

                while (true) {
		    try {
                        sendCommandToSoftphone("ping");
		    } catch (IOException e) {
		    }

                    Thread.sleep(5000);
                }
            } catch (InterruptedException ie) {}
        }
    }

    /**
     * Listens for lines from an input stream.
     */
    class ProcOutputListener implements Runnable {
        BufferedReader reader;
        Thread anim;

        public ProcOutputListener(InputStream stream) {
            this.reader = new BufferedReader(new InputStreamReader(stream));
        }
        
        public void start() {
            anim = new Thread(ThreadManager.getThreadGroup(), this);
            anim.start();
        }
        
        public void stop() {
            Thread hold = anim;
            anim = null;
            try {
		logger.finer("SipStarter closing input stream");
                reader.close();
            } catch (IOException ioe) {}
        }
        
        public void run() {
            try {
                while (anim == Thread.currentThread()) {
                    String line = reader.readLine();
                    if (line == null) {
			logger.info("readLine returned null!");
                        anim = null;
                    } else {
                        lineReceived(this, line);
                    }
                }
            } catch (IOException ioe) {
                if (anim != null) {
                    ioe.printStackTrace();
                }
            } finally {
		close("Lost connection to the software phone unexpectedly.");
            }
        }
        
    }

    private boolean shuttingDown;
    private boolean closed;

    private void close(final String failureMessage) {
	if (closed) {
	    return;
	}

	closed = true;

	if (failureMessage != null) {
	    logger.info("SipStarter close:  " + failureMessage);
	}

        synchronized(this) {
            if (softphoneOutputStream != null) {
		logger.finer("SipStarter sending Shutdown to softphone");

		try {
                    sendCommandToSoftphone("Shutdown");
		} catch (IOException e) {
		}
            }

	    softphoneAddress = null;

            if (softphoneOutputStream != null) {
                try {
                    softphoneOutputStream.close();
                } catch (IOException ioe) {
		}
            }

	    logger.finer("SipStarter setting output stream to null");

            softphoneOutputStream = null;
            if (stdOutListener != null) {
                stdOutListener.stop();
                stdOutListener = null;
            }
            if (stdErrListener != null) {
                stdErrListener.stop();
                stdErrListener = null;
            }
            if (pinger != null) {
                pinger.interrupt();
                pinger = null;
            }
            softphoneProcess = null;
        }

	notifyListeners(State.EXITED);
    }
    
    /**
     * Utilities for determining Java version, platform, etc.
     * @author jkaplan
     */
    static class JavaVersion {
        /** 
         * Determine if this platform is Mac OS X.  
         * @return true for Mac OX, or false if not.
         */
        public static boolean isMacOSX() {
            String osName = System.getProperty("os.name");
            return osName.equalsIgnoreCase("Mac OS X");
        }
    
        /**
         * Compare the two given java versions.  The results are -1 if the
         * first version is earlier than the second version, 0 if the versions
         * are equal, or 1 if the first version is later than the second
         * version.  
         * <p>
         * The format of a Java version is major.minor.micro-qualifier.  Only
         * the version numbers (major, minor and micro) are compared.  Any
         * unspecified values are not compared, so 1.4.x is equivalent to
         * 1.4.
         * <p>
         * @param first the first version to compare, as a String (e.g. "1.4.2")
         * @param second the second version to compare, as a String
         * @return -1, 0, or 1 as appropriate
         */ 
        public static int compareVersions(String first, String second) {
            String[] firstSplits = first.split("\\D");
            String[] secondSplits = second.split("\\D");
        
            // start out thinking they are equal
            int res = 0;
        
            // compare as many digits as the shorter string
            int size = Math.min(firstSplits.length, secondSplits.length);   
            for (int i = 0; i < size; i++) {
                int f = Integer.parseInt(firstSplits[i]);
                int s = Integer.parseInt(secondSplits[i]);
            
                // see if we've found a number where they differ
                if (f < s) {
                    res = -1;
                    break;
                } else if (f > s) {
                    res = 1;
                    break;
                }
            }
        
            return res;
        }

        /** test method */
        public static void main(String[] args) {
            System.out.println("IsMacOSX: " + isMacOSX());
            System.out.println("Compare 1.5 to 1.4.2: " + 
                    compareVersions("1.5", "1.4.2"));
            System.out.println("Compare 1.5.0 to 1.5.1: " + 
                    compareVersions("1.5.0", "1.5.1"));
            System.out.println("Compare 1.5 to 1.5.3: " + 
                    compareVersions("1.5", "1.5.3"));
            System.out.println("Compare 1.4.3 to 1.5.0: " + 
                    compareVersions("1.4.3", "1.5.0"));
            System.out.println("Compare 1.6.0-beta2 to 1.5.0: " +
    		    compareVersions("1.6.0-beta2", "1.5.0"));
        }
    }

    public static void main(String args[]) {
	SoftphoneControlImpl softphoneControlImpl = SoftphoneControlImpl.getInstance();

	try {
	    String address = softphoneControlImpl.startSoftphone(
	        System.getProperty("user.name"), "swbridge.east.sun.com:5060", 0, null);

	    logger.warning("Softphone address is " + address);

	    softphoneControlImpl.sendCommandToSoftphone(
		"PlaceCall=conferenceId:xxx,callee=20315");
	} catch (IOException e) {
	    logger.warning(e.getMessage());
	}
    }

}
