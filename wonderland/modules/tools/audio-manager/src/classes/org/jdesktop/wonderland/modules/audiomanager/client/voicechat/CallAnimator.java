/*
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
package org.jdesktop.wonderland.modules.audiomanager.client.voicechat;

import org.jdesktop.wonderland.modules.audiomanager.client.AudioManagerClient;

import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.WlAvatarCharacter;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.WlAvatarContext;

import java.util.logging.Logger;

import imi.character.avatar.AvatarContext.TriggerNames;

/**
 *
 * @author jprovino
 */
public class CallAnimator {

    private static final Logger LOGGER =
        Logger.getLogger(CallAnimator.class.getName());

    private static String animationName;

    private static boolean isPlaying;

    public static void animateCallAnswer(AudioManagerClient client) {
	if (isPlaying) {
	    return;
	}

        WlAvatarCharacter avatar = client.getWlAvatarCharacter();

        if (avatar == null) {
	    LOGGER.info("Unable to get avatar for animation");
	    return;
        }

        for (String action : avatar.getAnimationNames()) {
            if (action.indexOf("_AnswerCell") > 0) {
                animationName = action;
		break;
            }
        }

	if (animationName == null) {
	    LOGGER.info("Unable to get call answer animation name");
	    return;
	}

	avatar.playAnimation(animationName);
	isPlaying = true;
   }

   public static void stopCallAnswerAnimation(AudioManagerClient client) {
	if (isPlaying == false) {
	    return;
	}

	isPlaying = false;

        WlAvatarCharacter avatar = client.getWlAvatarCharacter();
	
        if (avatar == null) {
	    LOGGER.info("Unable to get avatar for animation");
            return;
        }

	if (animationName == null) {
	    LOGGER.info("Unable to get call answer animation name");
	    return;
	}

	//trigger(client, TriggerNames.Move_Forward);
	trigger(client, TriggerNames.Move_Back);
   }

   private static void trigger(AudioManagerClient client, 
	    final TriggerNames trigger) {

        final WlAvatarCharacter avatar = client.getWlAvatarCharacter();

        final Runnable r = new Runnable() {
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    LOGGER.warning("Sleep failed.");
                }
                avatar.triggerActionStop(trigger);
            }
        };

        // Spawn a thread to start the animation, which then spawns a thread
        // to stop the animation after a small sleep.
        new Thread() {
            @Override
            public void run() {
                avatar.triggerActionStart(trigger);
                new Thread(r).start();
            }
        }.start();
   }

}

