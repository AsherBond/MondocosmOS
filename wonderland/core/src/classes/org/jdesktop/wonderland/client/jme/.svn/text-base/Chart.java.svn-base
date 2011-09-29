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
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import javax.swing.JPanel;

/**
 *
 * @author nsimpson
 */
public class Chart extends JPanel {

    private static final int DEFAULT_SAMPLE_SIZE = 200;
    private int sampleSize = DEFAULT_SAMPLE_SIZE;
    private String label;
    private double value;
    private double max;
    private int leftIndent = 2;
    private int rightIndent = 2;
    private int labelGap = 2;
    private int topIndent = 4;
    private int bottomIndent = 1;
    private int fontSize = 12;
    private String fontName = "SansSerif";
    private Color bgGradientStartColor = new Color(20, 25, 32); // dark gray
    private Color bgGradientEndColor = new Color(53, 63, 81);
    private Color gradientStartColor = new Color(89, 149, 37); // green
    private Color gradientEndColor = new Color(219, 250, 203);
    private DecimalFormat floatFormat = new DecimalFormat("###.0");
    private Font font;
    private FontMetrics fontMetrics;
    private int currentWidth = 0;
    private int currentHeight = 0;
    private int chartWidth = 0;
    private int chartHeight = 0;
    private int labelWidth = 0;
    private int labelHeight = 0;
    private int valueWidth = 0;
    private GradientPaint paint;
    private GradientPaint bgPaint;
    private BufferedImage chartImage;
    private BufferedImage copyImage;
    private int lineX = 0;
    private int sum = 0;
    private int samples = 0;
    private int average = 0;
    private double[] values = new double[DEFAULT_SAMPLE_SIZE];
    private int valueIndex = 0;

    public Chart(String label) {
        super();
        this.label = label;
        font = new Font(fontName, Font.BOLD, fontSize);
    }

    /**
     * Sets the maximum value of the chart
     * @param max the maximum value
     */
    public void setMaxValue(double max) {
        this.max = max;
    }

    /**
     * Adds a new value to the chart
     * @param value the current value
     */
    public void setValue(double value) {
        // store the current value and compute the rolling average
        this.value = value;
        if (valueIndex >= values.length) {
            valueIndex = 0;
        }
        sum -= values[valueIndex];
        sum += value;
        values[valueIndex] = value;
        valueIndex++;
        samples = (samples >= values.length) ? values.length : samples + 1;
        average = sum / samples;
        repaint();
    }

    /**
     * Sets the sample size to be displayed in the chart
     * @param sampleSize the number of samples to be displayed
     */
    public void setSampleSize(int sampleSize) {
        this.sampleSize = sampleSize;
        values = new double[sampleSize];
        sum = 0;
        samples = 0;
        average = 0;
        valueIndex = 0;
    }

    @Override
    public void paintComponent(Graphics g) {
        paintComponentImage(g);
    }

    public void paintComponentImage(Graphics g) {
//        Calendar then = Calendar.getInstance();
        Graphics2D g2 = (Graphics2D) g;

        // calculate the sizes of the label and value strings
        if (fontMetrics == null) {
            fontMetrics = g.getFontMetrics(font);
            labelWidth = fontMetrics.stringWidth(label);
            labelHeight = fontMetrics.getHeight();
            valueWidth = fontMetrics.stringWidth("000.0");
        }

        // handle resize
        if ((currentWidth != getWidth()) || (currentHeight != getHeight())) {
            currentWidth = getWidth();
            currentHeight = getHeight();
            chartWidth = currentWidth - leftIndent - valueWidth - labelGap - rightIndent;
            chartHeight = currentHeight - 2;
            if (chartWidth <= 0) {
                chartWidth = 10;
            }
            if (chartHeight <= 0) {
                chartHeight = 10;
            }
            chartImage = null;
            chartImage = new BufferedImage(chartWidth, chartHeight, BufferedImage.TYPE_INT_ARGB);
            copyImage = null;
            copyImage = new BufferedImage(chartWidth, chartHeight, BufferedImage.TYPE_INT_ARGB);
        }

        int x = 0;
        int y = 0;
        int lineHeight = (int) ((value / max) * chartHeight);

        // paint the background
        if (bgPaint == null) {
            bgPaint = new GradientPaint(0, 0, bgGradientEndColor,
                    0, chartHeight, bgGradientStartColor);
        }
        g2.setPaint(bgPaint);
        g2.fillRect(0, 0, currentWidth, currentHeight);

        // draw the text label
        x += leftIndent;
        y += topIndent + labelHeight / 2;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.setFont(font);
        g2.drawString(label, x, y);

        // draw the value
        y += topIndent + labelHeight;
        g2.drawString(floatFormat.format(value), x, y);

        Graphics2D cg2 = (Graphics2D) chartImage.getGraphics();

        if (lineX > chartWidth) {
            Graphics2D sg2 = (Graphics2D) copyImage.getGraphics();
            sg2.drawImage(chartImage, null, -1, 0);
            cg2.setPaint(bgPaint);
            cg2.fillRect(0, 0, chartWidth, chartHeight);
            cg2.drawImage(copyImage, null, 0, 0);
            sg2.dispose();
            lineX = chartWidth - 1;
        }

        // draw the current value as a line
        if (paint == null) {
            paint = new GradientPaint(0, 0, gradientEndColor,
                    0, chartHeight, gradientStartColor);
        }
        cg2.setPaint(paint);
        cg2.fillRect(lineX, chartHeight - lineHeight, 1, lineHeight);
        lineX++;

        // render the image onto the panel
        x += valueWidth + labelGap;
        g2.drawImage(chartImage, null, x, bottomIndent);

        // draw the average value line
        int averageHeight = (int) ((average / max) * chartHeight);
        g2.setColor(Color.WHITE);
        g2.drawLine(x, chartHeight - averageHeight, currentWidth, chartHeight - averageHeight);

//        Calendar now = Calendar.getInstance();
//        System.err.println("cost: " + (now.getTimeInMillis() - then.getTimeInMillis()));
//        now = null;
//        then = null;
        cg2.dispose();
        g2.dispose();
    }
}
