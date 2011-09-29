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
package org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer;

import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;

import org.jdesktop.mtgame.processor.WorkProcessor.WorkCommit;

import org.jdesktop.wonderland.client.jme.SceneWorker;
import org.jdesktop.wonderland.client.jme.utils.TextLabel2D;

import org.jdesktop.wonderland.client.jme.ClientContextJME;

import java.awt.Color;
import java.awt.Font;

import java.util.logging.Logger;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.AvatarNameEvent.EventType;

/**
 * @author jprovino
 * @author nsimpson
 */
public class NameTagNode extends Node {

    private static final Logger logger = Logger.getLogger(NameTagNode.class.getName());

    // colors
    public static final Color SPEAKING_COLOR = Color.RED;
    public static final Color NOT_SPEAKING_COLOR = Color.WHITE;
    public static final Color CONE_OF_SILENCE_COLOR = Color.LIGHT_GRAY;

    private Color foregroundColor = NOT_SPEAKING_COLOR;
    private Color backgroundColor = new Color(0f, 0f, 0f);

    // fonts
    public static final String DEFAULT_FONT_NAME = "SANS_SERIF";
    public static final String DEFAULT_FONT_NAME_TYPE = "PLAIN";
    public static final String DEFAULT_FONT_ALIAS_TYPE = "ITALIC";

    public static final int DEFAULT_FONT_SIZE = 20;

    public static final Font REAL_NAME_FONT =
            fontDecode(DEFAULT_FONT_NAME, DEFAULT_FONT_NAME_TYPE, DEFAULT_FONT_SIZE);

    public static final Font ALIAS_NAME_FONT =
            fontDecode(DEFAULT_FONT_NAME, DEFAULT_FONT_ALIAS_TYPE, DEFAULT_FONT_SIZE);

    private int fontSize = DEFAULT_FONT_SIZE;

    private Font font = REAL_NAME_FONT;

    // name tag heights
    public static final float SMALL_SIZE = 0.2f;
    public static final float REGULAR_SIZE = 0.3f;
    public static final float LARGE_SIZE = 0.5f;

    private float height = REGULAR_SIZE;

    // status indicators
    public static final String LEFT_MUTE = "[";
    public static final String RIGHT_MUTE = "]";
    public static final String SPEAKING = "...";

    private boolean inConeOfSilence;
    private boolean isSpeaking;
    private boolean isMuted;
    private boolean labelHidden;

    private boolean done;
    private TextLabel2D label = null;
    private final float heightAbove;
    private String name;
    private Spatial q;
    private String usernameAlias;
    private boolean visible;

    private static Font fontDecode(String fontName, String fontType, int fontSize) {
        return Font.decode(fontName + " " + fontType + " " + fontSize);
    }

    public NameTagNode(String name, float heightAbove, boolean inConeOfSilence, boolean isSpeaking,
	    boolean isMuted) {
        this.name = name;
        this.usernameAlias = name;
        this.heightAbove = heightAbove;
        this.inConeOfSilence = inConeOfSilence;
        this.isSpeaking = isSpeaking;
        this.isMuted = isMuted;
        visible = true;

	setNameTag(EventType.REGULAR_FONT, name, usernameAlias);
    }

    public void done() {
        if (done) {
            return;
        }

        done = true;

        detachChild(q);
    }

    public static String getDisplayName(String name, boolean isSpeaking, boolean isMuted) {
        if (isMuted) {
            return LEFT_MUTE + name + RIGHT_MUTE;
        } else {
            if (isSpeaking) {
                return name + SPEAKING;
            }
	}

        return name;
    }

    public static String getUsername(String name) {
        String s = name.replaceAll("\\" + LEFT_MUTE, "");

        s = s.replaceAll("\\" + RIGHT_MUTE, "");

        return s.replaceAll("\\" + SPEAKING, "");
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        if (visible) {
            updateLabel();
        } else {
            removeLabel();
        }
    }

    /**
     * Returns whether the name tag is visible. 
     */
    public boolean isVisible() {
        return visible;
    }

    public void setNameTag(EventType eventType, String username, String alias) {
        logger.info("set name tag: " + eventType + ", username: " + username
	    + ", alias: " + alias); 

        switch (eventType) {
            case HIDE:
                labelHidden = true;
		break;

            case SMALL_FONT:
                labelHidden = false;
                height = SMALL_SIZE;
                break;

            case REGULAR_FONT:
                labelHidden = false;
                height = REGULAR_SIZE;
                break;

            case LARGE_FONT:
                labelHidden = false;
                height = LARGE_SIZE;
                break;

            case ENTERED_CONE_OF_SILENCE:
                inConeOfSilence = true;
                break;

            case EXITED_CONE_OF_SILENCE:
                inConeOfSilence = false;
                break;

            case STARTED_SPEAKING:
                isSpeaking = true;
                break;

            case STOPPED_SPEAKING:
                isSpeaking = false;
                break;

            case MUTE:
                isMuted = true;
		isSpeaking = false;
//                removeLabel();
                break;

            case UNMUTE:
                isMuted = false;
                break;

            case CHANGE_NAME:
//                removeLabel();
                usernameAlias = alias;
                break;

            default:
                logger.warning("unhandled name tag event type: " + eventType);
                break;
        }

	updateLabel();
    }

    private void removeLabel() {
        if (label != null) {
            detachChild(label);
            label = null;
        }
    }

    public void updateLabel(String usernameAlias, boolean inConeOfSilence, boolean isSpeaking,
	    boolean isMuted) {

	//System.out.println("UPDATE LABEL:  name " + name + " alias " + alias + " isMuted " + isMuted);

	this.usernameAlias = usernameAlias;
	this.inConeOfSilence = inConeOfSilence;
	this.isSpeaking = isSpeaking;
	this.isMuted = isMuted;

	updateLabel();
    }

    private void updateLabel() {
        if (name==null)
            return;

        if (labelHidden) {
            removeLabel();
            return;
        }

	if (usernameAlias!=null && name.equals(usernameAlias) == false) {
            font = ALIAS_NAME_FONT;
	} else {
            font = REAL_NAME_FONT;
	}

	if (inConeOfSilence) {
	    foregroundColor = CONE_OF_SILENCE_COLOR;
	} else {
	    if (isSpeaking) {
	        foregroundColor = SPEAKING_COLOR;
	    } else {
	        foregroundColor = NOT_SPEAKING_COLOR;
	    }
	}

	final String displayName = getDisplayName(usernameAlias, isSpeaking, isMuted);

        SceneWorker.addWorker(new WorkCommit() {
            public void commit() {
                if (visible) {
                    if (label == null) {
                        label = new TextLabel2D(displayName, foregroundColor, backgroundColor, 
			    height, true, font);

                        label.setLocalTranslation(0, heightAbove, 0);

                        Matrix3f rot = new Matrix3f();
                        rot.fromAngleAxis((float) Math.PI, new Vector3f(0f, 1f, 0f));
                        label.setLocalRotation(rot);

                        attachChild(label);
                    } else {
                        label.setFont(font);
                        label.setHeight(height);
                        label.setText(displayName, foregroundColor, backgroundColor);
                    }
                    ClientContextJME.getWorldManager().addToUpdateList(NameTagNode.this);
                }
            }
        });
    }

}
