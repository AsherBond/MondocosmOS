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
package org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat;

import org.jdesktop.wonderland.common.messages.Message;

/**
 *
 * @author jprovino
 */
public class VoiceChatMessage extends Message {

    private String group;

    public enum ChatType {
	EXCLUSIVE,
	SECRET,
	PRIVATE,
	PUBLIC
    }

    public static final String SECRET_DESCRIPTION =
	"Call cannot be seen or heard by others";

    public static final String PRIVATE_DESCRIPTION =
	"Call is visible, not audible to others";

    public static final String PUBLIC_DESCRIPTION =
	"Call is heard by everyone nearby";

    public VoiceChatMessage(String group) {
	this.group = group;
    }

    public String getGroup() {
	return group;
    }

}
