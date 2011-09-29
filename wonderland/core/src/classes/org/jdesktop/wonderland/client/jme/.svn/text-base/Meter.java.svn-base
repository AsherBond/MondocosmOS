/**
 * Open Wonderland
 *
 * Copyright (c) 2010, Open Wonderland Foundation, All Rights Reserved
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
package org.jdesktop.wonderland.client.jme;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import javax.swing.JPanel;

/**
 *
 * @author nsimpson
 */
public class Meter extends JPanel {

    private String label;
    private double value;
    private double warning;
    private double max;
    private int leftIndent = 2;
    private int rightIndent = 2;
    private int labelGap = 2;
    private int bottomIndent = 5;
    private int barHeight = 10;
    private int barGap = 2;
    private int tickLength = 5;
    private int fontSize = 12;
    private String fontName = "SansSerif";
    private RoundRectangle2D.Double bar;
    private Color normalStartColor = new Color(89, 149, 37); // green
    private Color normalEndColor = new Color(219, 250, 203);
    private Color warningStartColor = new Color(255, 0, 0);  // red
    private Color warningEndColor = new Color(255, 84, 84);
    private DecimalFormat floatFormat = new DecimalFormat("###.0");
    private Font font;
    private FontMetrics fontMetrics;
    private int labelWidth = 0;
    private int labelHeight = 0;
    private int valueWidth = 0;
    private GradientPaint paint;

    public Meter(String label) {
        super();
        this.label = label;
        bar = new RoundRectangle2D.Double();
        font = new Font(fontName, Font.BOLD, fontSize);
    }

    public void setMaxValue(double max) {
        this.max = max;
    }

    public void setValue(double value) {
        this.value = value;
        if (value > max) {
            // value exceeded specified max, raise the max value to
            // accommodate the overage
            max = Math.ceil(value);
        }
        repaint();
    }

    public void setWarningValue(double warning) {
        this.warning = warning;
    }

    @Override
    public void paintComponent(Graphics g) {
        //Calendar then = Calendar.getInstance();
        Color startColor = ((warning > 0) && (value >= warning)) ? warningStartColor :
            normalStartColor;
        Color endColor = ((warning > 0) && (value >= warning)) ? warningEndColor :
            normalEndColor;

        Graphics2D g2 = (Graphics2D) g;

        // calculate the sizes of the label and value strings
        if (fontMetrics == null) {
            fontMetrics = g.getFontMetrics(font);
            labelWidth = fontMetrics.stringWidth(label);
            labelHeight = fontMetrics.getHeight();
            valueWidth = fontMetrics.stringWidth("000.0");
        }

        int w = this.getWidth();
        int availableWidth = w - leftIndent - rightIndent - labelWidth - valueWidth - 2 * labelGap;
        int h = this.getHeight();
        int y = h;

        // paint the background
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(0, 0, w, h);

        y -= bottomIndent;

        // paint the tick marks
        g2.setColor(Color.LIGHT_GRAY);
        double tickWidth = (double) availableWidth / 10d;
        double x = leftIndent + labelWidth + labelGap;
        for (int tick = 0; tick <= 10; tick++) {
            g2.drawLine((int) x, y, (int) x, y - tickLength);
            x += tickWidth;
        }

        y -= tickLength + barGap + barHeight;

        // draw the bar
        int barWidth = (int) ((value / max) * availableWidth);
        bar.setRoundRect(leftIndent + labelWidth + labelGap, y, barWidth, barHeight, 5, 5);
        g2.setClip(bar);
        paint = new GradientPaint(0, 0, startColor,
                availableWidth, getHeight(), endColor);
        g2.setPaint(paint);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setClip(0, 0, w, h);

        y += labelHeight;

        // draw the label
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.setFont(font);
        g2.drawString(label, leftIndent, y);

        // draw the value
        g2.drawString(floatFormat.format(value), w - rightIndent - valueWidth, y);

    //Calendar now = Calendar.getInstance();
    //System.err.println("cost: " + (now.getTimeInMillis() - then.getTimeInMillis()));
    }
}
