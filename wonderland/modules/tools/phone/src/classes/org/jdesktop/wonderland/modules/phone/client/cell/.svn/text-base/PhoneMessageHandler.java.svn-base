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
package org.jdesktop.wonderland.modules.phone.client.cell;

//import org.jdesktop.wonderland.avatarorb.client.cell.AvatarOrbCell;

import com.sun.sgs.client.ClientChannel;
import java.util.logging.Logger;
import java.util.ResourceBundle;
import javax.swing.SwingUtilities;
import org.jdesktop.wonderland.client.cell.ChannelComponent;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.softphone.SoftphoneControlImpl;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;
import org.jdesktop.wonderland.modules.phone.common.CallListing;
import org.jdesktop.wonderland.modules.phone.common.messages.CallEndedResponseMessage;
import org.jdesktop.wonderland.modules.phone.common.messages.CallEstablishedResponseMessage;
import org.jdesktop.wonderland.modules.phone.common.messages.CallInvitedResponseMessage;
import org.jdesktop.wonderland.modules.phone.common.messages.EndCallMessage;
import org.jdesktop.wonderland.modules.phone.common.messages.EndCallResponseMessage;
import org.jdesktop.wonderland.modules.phone.common.messages.JoinCallMessage;
import org.jdesktop.wonderland.modules.phone.common.messages.JoinCallResponseMessage;
import org.jdesktop.wonderland.modules.phone.common.messages.LockUnlockResponseMessage;
import org.jdesktop.wonderland.modules.phone.common.messages.PhoneResponseMessage;
import org.jdesktop.wonderland.modules.phone.common.messages.PlaceCallMessage;
import org.jdesktop.wonderland.modules.phone.common.messages.PlaceCallResponseMessage;
import org.jdesktop.wonderland.modules.phone.common.messages.PlayTreatmentMessage;
import org.jdesktop.wonderland.modules.phone.common.PhoneInfo;

/**
 *
 * @author jprovino
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class PhoneMessageHandler {

    private static final Logger LOGGER =
            Logger.getLogger(PhoneMessageHandler.class.getName());
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/phone/client/cell/resources/Bundle");

    private static final float HOVERSCALE = 1.5f;
    private static final float NORMALSCALE = 1.25f;
    
    private CallListing mostRecentCallListing;
         
    private boolean projectorState;
    
    private ProjectorStateUpdater projectorStateUpdater;

    private String name;

    private PhoneCell phoneCell;

    private PhoneForm phoneForm;

    private ChannelComponent channelComp;

    public PhoneMessageHandler(PhoneCell phoneCell) {
	this.phoneCell = phoneCell;

	channelComp = phoneCell.getComponent(ChannelComponent.class);

        ChannelComponent.ComponentMessageReceiver msgReceiver =
	    new ChannelComponent.ComponentMessageReceiver() {
                public void messageReceived(CellMessage message) {
		    processMessage((PhoneResponseMessage) message);
                }
            };

        channelComp.addMessageReceiver(CallEndedResponseMessage.class, msgReceiver);
        channelComp.addMessageReceiver(CallEstablishedResponseMessage.class, msgReceiver);
        channelComp.addMessageReceiver(CallInvitedResponseMessage.class, msgReceiver);
        channelComp.addMessageReceiver(EndCallResponseMessage.class, msgReceiver);
        channelComp.addMessageReceiver(JoinCallResponseMessage.class, msgReceiver);
        channelComp.addMessageReceiver(LockUnlockResponseMessage.class, msgReceiver);
        channelComp.addMessageReceiver(PhoneResponseMessage.class, msgReceiver);
        channelComp.addMessageReceiver(PlaceCallResponseMessage.class, msgReceiver);
    }

    public void done() {
	channelComp.removeMessageReceiver(CallEndedResponseMessage.class);
	channelComp.removeMessageReceiver(CallEstablishedResponseMessage.class);
	channelComp.removeMessageReceiver(CallInvitedResponseMessage.class);
	channelComp.removeMessageReceiver(EndCallResponseMessage.class);
	channelComp.removeMessageReceiver(JoinCallResponseMessage.class);
	channelComp.removeMessageReceiver(LockUnlockResponseMessage.class);
	channelComp.removeMessageReceiver(PhoneResponseMessage.class);
	channelComp.removeMessageReceiver(PlaceCallResponseMessage.class);
    }

    public void phoneSelected() {
	if (phoneForm == null) {
	    final PhoneInfo phoneInfo = phoneCell.getPhoneInfo();

	    boolean locked = phoneInfo.locked;

	    boolean passwordProtected = true;

	    if (phoneInfo.password == null || phoneInfo.password.length() == 0) {
		locked = false;
		passwordProtected = false;
	    }

	    final WonderlandSession session = phoneCell.getCellCache().getSession();

	    final boolean isLocked = locked;
	    final boolean isPasswordProtected = passwordProtected;

	    java.awt.EventQueue.invokeLater(new Runnable() {
		public void run() {
	            phoneForm = new PhoneForm(session, phoneCell.getCellID(), channelComp,
		        PhoneMessageHandler.this, isLocked, phoneInfo.phoneNumber, isPasswordProtected);
		}
	    });
	}

	java.awt.EventQueue.invokeLater(new Runnable() {
	    public void run() {
		phoneForm.setVisible(true);
	    }
	});
    }

    public void processMessage(final PhoneResponseMessage message) {
	if (message instanceof CallEndedResponseMessage) {
	    final CallEndedResponseMessage msg = (CallEndedResponseMessage) message;

            if (msg.wasSuccessful() == false) {    
                LOGGER.warning("Failed:  " + msg.getReasonCallEnded());
	    }

            CallListing listing = msg.getCallListing();
        
            if (mostRecentCallListing == null ||
		    listing.equals(mostRecentCallListing) == false) {

		return;
	    }

            if (mostRecentCallListing.isPrivate()) {
		//This was a private call...
                //ChannelController.getController().getLocalUser().getAvatarCell().setUserWhispering(false); 
            }

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
		    if (phoneForm != null) {
                        phoneForm.setCallEnded(msg.getReasonCallEnded());
		    }
                }
            });

	    return;
	}

	if (message instanceof LockUnlockResponseMessage) {
	    LockUnlockResponseMessage msg = (LockUnlockResponseMessage) message;

	    if (phoneForm != null) {
	        phoneForm.changeLocked(msg.getLocked(), msg.wasSuccessful());
	    }
	    return;
	}

	if (message instanceof PhoneResponseMessage == false) {
	    LOGGER.warning("Invalid message:  " + message);
	    return;
	}

	PhoneResponseMessage msg = (PhoneResponseMessage) message;

        CallListing listing = msg.getCallListing();

	if (msg instanceof PlaceCallResponseMessage) {
	    LOGGER.fine("Got place call response...");

            if (msg.wasSuccessful() == false) {
                LOGGER.warning("Failed PLACE_CALL!");
		return;
	    }

            if (mostRecentCallListing == null ||
		    listing.equals(mostRecentCallListing) == false) {

		LOGGER.warning("Didn't find listing...");
		return;
	    }

	    /*
	     * Make sure the most recent listing has the right private 
	     * client name.
	     */
	    mostRecentCallListing.setPrivateClientName(listing.getPrivateClientName());

	    /*
	     * Set the call ID used by the server.
	     */
	    LOGGER.fine("Updating listing with " + listing.getExternalCallID());

	    mostRecentCallListing.setExternalCallID(listing.getExternalCallID());

            /*
	     * This is a confirmation msg for OUR call. 
	     * Update the form's selection.                        
	     */
            if (listing.isPrivate()) {
                //ChannelController.getController().getLocalUser().getAvatarCell().setUserWhispering(true);
            }
	    return;
	}

	if (msg instanceof JoinCallResponseMessage)  {
            //Hearing back from the server means this call has joined the world.
            if (msg.wasSuccessful() == false) {
                LOGGER.warning("Failed JOIN_CALL");
		return;
	    }

	    if (mostRecentCallListing == null || 
		    listing.equals(mostRecentCallListing) == false) {

		return;
	    }

            //This is a JOIN confirmation msg for OUR call. So we should no longer be whispering...
            //ChannelController.getController().getLocalUser().getAvatarCell().setUserWhispering(false);                       
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
	    	    if (phoneForm != null) {
                        phoneForm.setCallEstablished(false);
		    }
                }
            });
	    return;
	}
            
	if (msg instanceof CallInvitedResponseMessage) {
            if (mostRecentCallListing == null ||
		    listing.equals(mostRecentCallListing) == false) {

		return;  // we didn't start this call
	    }

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
		    if (phoneForm != null) {
                        phoneForm.setStatusMessage(BUNDLE.getString("Dialing"));
		    }
                }
            });
            return;
	}
            
	if (msg instanceof CallEstablishedResponseMessage) {
	    LOGGER.fine("Got est resp");

            if (mostRecentCallListing == null ||
		    listing.equals(mostRecentCallListing) == false) {

		LOGGER.warning("no listing " + mostRecentCallListing
		    + " listing " + listing);

		return;  // we didn't start this call
	    }

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
		    if (phoneForm != null) {
		        synchronized (phoneForm) {
                            phoneForm.setCallEstablished(
			        mostRecentCallListing.isPrivate());
		        }
		    }
                }
            });
            
            return;
        }
    }
    
    public void leftChannel(ClientChannel arg0) {
        // ignore
    }
    
    public void placeCall(CallListing listing) {
	WonderlandSession session = phoneCell.getCellCache().getSession();

	SoftphoneControlImpl sc = SoftphoneControlImpl.getInstance();

	listing.setSoftphoneCallID(sc.getCallID());

        PlaceCallMessage msg = new PlaceCallMessage(phoneCell.getCellID(), 
	    listing);

	LOGGER.fine("Sending place call message " + phoneCell.getCellID() + " "
	    + " softphoneCallID " + sc.getCallID() + " " + listing);

	synchronized (phoneForm) {
            mostRecentCallListing = listing;      

            channelComp.send(msg);    
	}
    }
    
    public void joinCall() {
	WonderlandSession session = phoneCell.getCellCache().getSession();

        JoinCallMessage msg = new JoinCallMessage(phoneCell.getCellID(), 
	    mostRecentCallListing);

        channelComp.send(msg);
    }
    
    public void endCall() {        
	LOGGER.fine("call id is " + mostRecentCallListing.getExternalCallID());

        EndCallMessage msg = new EndCallMessage(phoneCell.getCellID(), 
	    mostRecentCallListing);

        channelComp.send(msg); 
    }
    
    public void dtmf(char c) {
        String treatment = "dtmf:" + c;

        PlayTreatmentMessage msg = new PlayTreatmentMessage(phoneCell.getCellID(), 
	    mostRecentCallListing, treatment, true);

	channelComp.send(msg);
    }
    
    public void processEvent() {
        // react to mouse enter/exit events
    }

}
    
    class ProjectorStateUpdater extends Thread {

        private boolean running = true;
        
	public ProjectorStateUpdater() {
	    start();
	}

        @Override
	public void run() {
	    while (running) {
		updateProjectorState();

		try {
		    Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	    }
	}
        
        public void kill() {
            running = false;
        }

        private void updateProjectorState() {
        
            //boolean targetState = !callListingMap.isEmpty();
            boolean targetState = false;

            //ArrayList<Cell> childList = new ArrayList<Cell>();
            //getAllContainedCells(childList);
            //Iterator<Cell> iter = childList.iterator();
            //while(iter.hasNext()) {
            //    Cell c = iter.next();
                //if (c instanceof AvatarOrbCell) {
                //    targetState = true;
                //    break;
                //}
            //}
        
            //Are we switching states?
            //if (projectorState == targetState){
	    //    return;
	    //}

	    if (targetState){
                //Turn on        
		//cellLocal.addChild(projectorBG);
            } else {
                //Turn off
                //projectorBG.detach();
            }
            
            //projectorState = targetState;
	}
    
    }
