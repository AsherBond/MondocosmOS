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
public class VMeter extends JPanel {

    private String label;
    private double value;
    private double warning;
    private double max;
    private int leftIndent = 2;
    private int rightIndent = 2;
    private int labelGap = 2;
    private int topIndent = 5;
    private int bottomIndent = 5;
    private int barHeight = 0;
    private int barWidth = 0;
    private int barGap = 2;
    private int tickLength = 5;
    private int fontSize = 12;
    private boolean showValue = true;
    private boolean showTicks = true;
    private String fontName = "SansSerif";
    private RoundRectangle2D.Double bar;
    private Color normalStartColor = new Color(89, 149, 37); // green
    private Color normalEndColor = new Color(219, 250, 203);
    private Color warningStartColor = new Color(255, 0, 0);  // red
    private Color warningEndColor = new Color(255, 84, 84);
    private DecimalFormat floatFormat = new DecimalFormat("##0.0");
    private Font font;
    private FontMetrics fontMetrics;
    private int labelWidth = 0;
    private int labelHeight = 0;
    private int valueWidth = 0;
    private int valueHeight = 0;
    private GradientPaint paint;

    public VMeter(String label) {
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

    public void setShowValue(boolean showValue) {
        this.showValue = showValue;
    }

    public boolean getShowValue() {
        return showValue;
    }

    public void setShowTicks(boolean showTicks) {
        this.showTicks = showTicks;
    }

    public boolean getShowTicks() {
        return showTicks;
    }

    public void setWarningValue(double warning) {
        this.warning = warning;
    }

    @Override
    public void paintComponent(Graphics g) {
        //Calendar then = Calendar.getInstance();
        Color startColor = warningEndColor; //((warning > 0) && (value >= warning)) ? warningStartColor : normalStartColor;
        Color endColor = normalStartColor; //((warning > 0) && (value >= warning)) ? warningEndColor : normalEndColor;
        String valueString = floatFormat.format(value);

        Graphics2D g2 = (Graphics2D) g;

        if (fontMetrics == null) {
            fontMetrics = g.getFontMetrics(font);
        }

        // configure meter to show or hide current numeric value
        if (showValue) {
            valueWidth = fontMetrics.stringWidth(valueString);
            valueHeight = fontMetrics.getHeight();
        } else {
            valueWidth = 0;
            valueHeight = 0;
            labelGap = 0;
        }

        // configure meter to show or hide the meter label (at bottom of meter)
        if (label.length() > 0) {
            labelWidth = fontMetrics.stringWidth(label);
            labelHeight = fontMetrics.getHeight();
        } else {
            labelWidth = 0;
            labelHeight = 0;
            labelGap = 0;
        }

        // calculate space available for meter
        int w = this.getWidth();
        int h = this.getHeight();
        int availableHeight = h - topIndent - bottomIndent - labelHeight - valueHeight - 2 * labelGap;
        int availableWidth = w - leftIndent - rightIndent;
        int y;

        // paint the background
        g2.setColor(this.getBackground());
        g2.fillRect(0, 0, w, h);

        y = h - bottomIndent - labelHeight - labelGap;

        if (showTicks) {
            // paint the tick marks
            g2.setColor(Color.LIGHT_GRAY);
            double tickSpace = (double) availableHeight / 10d;
            double x = w - rightIndent - tickLength;
            for (int tick = 0;
                    tick <=
                    10; tick++) {
                g2.drawLine((int) x, y, (int) (x + tickLength), y);
                y -= tickSpace;
            }
        }

        y = h;

        // determine bar dimensions
        barHeight = (int) ((value / max) * availableHeight);
        barWidth = availableWidth - tickLength;
        
        // set the bar's clip rect
        bar.setFrame(leftIndent + 5, h - bottomIndent - labelHeight - labelGap - barHeight,
                barWidth - 5, barHeight);
        g2.setClip(bar);

        // paint gradient clipped by the bar's clip rect
        paint = new GradientPaint(leftIndent, topIndent + valueHeight + labelGap, startColor,
                barWidth, availableHeight, endColor);
        g2.setPaint(paint);
        g2.fillRect(leftIndent, topIndent + valueHeight + labelGap, availableWidth, availableHeight);

        // reset clip rect
        g2.setClip(0, 0, w, h);

        if ((label.length() > 0) || showValue) {
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(this.getForeground());
            g2.setFont(font);
        }

        if (label.length() > 0) {
            // draw the label
            g2.drawString(label, (w - labelWidth) / 2, h - bottomIndent);
        }

        if (showValue) {
            // draw the value
            g2.drawString(valueString, (w - valueWidth) / 2, topIndent + valueHeight);
        }
        //Calendar now = Calendar.getInstance();
        //System.err.println("cost: " + (now.getTimeInMillis() - then.getTimeInMillis()));
    }
}
