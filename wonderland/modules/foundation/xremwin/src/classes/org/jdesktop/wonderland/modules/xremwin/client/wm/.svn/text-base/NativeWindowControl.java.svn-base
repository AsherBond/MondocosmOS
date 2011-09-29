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
package org.jdesktop.wonderland.modules.xremwin.client.wm;

import org.jdesktop.wonderland.common.StableAPI;

@StableAPI
public interface NativeWindowControl {
    public long getWID();
    public void setVisible(boolean visible);
    public void destroy();
    public boolean isMaximized();
    public void setMaximized(boolean maximize);
    public String getName();
    public void setSize(int w, int h);
    public int getWidth();
    public int getHeight();
    public void setLocation(int x, int y);
    public int getX();
    public int getY();
    public void moveToTop();
    public void setInputFocus();
    public boolean isCloseable();
    public boolean isMaximizable();
    public boolean isMinimizable();
    public boolean isResizable();
    public boolean isNormalWindow();
    public boolean isDecorated();
    public boolean isDockable();
    public long getRootWID();
    public void setWindowAssociation(String subWinCls, String subWinName, String subWinTitlePattern);
    public void setWindowAssociation(
            String targetWinResCls, String targetWinResName, String targetWinTitlePattern, 
            String subWinResCls, String subWinResName, String subWinTitlePattern);
}
