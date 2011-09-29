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
package org.jdesktop.wonderland.client.jme.login;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 *
 * @author nsimpson
 */
public class GradientPanel extends JPanel {
    private Color gradientStartColor = new Color(255, 0, 0);
    private Color gradientEndColor = new Color(0, 0, 0);
    
    public void setGradientStartColor(Color gradientStartColor) {
        this.gradientStartColor = gradientStartColor;
    }
    
    public Color getGradientStartColor() {
        return gradientStartColor;
    }

    public void setGradientEndColor(Color gradientEndColor) {
        this.gradientEndColor = gradientEndColor;
    }
    
    public Color getGradientEndColor() {
        return gradientEndColor;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        
        GradientPaint paint = new GradientPaint(0, 0, gradientStartColor,
                0, getHeight(), gradientEndColor);
        g2.setPaint(paint);
        g2.fill(g2.getClip());
    }
}
