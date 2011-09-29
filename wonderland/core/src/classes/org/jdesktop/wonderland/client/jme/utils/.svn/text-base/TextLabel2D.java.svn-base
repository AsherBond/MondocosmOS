/*
 * Sample code form JME wiki, http://jmonkeyengine.com/wiki/doku.php?id=billboard_awt_label
 */
package org.jdesktop.wonderland.client.jme.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.Arrays;

import com.jme.image.Texture;
import com.jme.image.Texture.MagnificationFilter;
import com.jme.image.Texture.MinificationFilter;
import com.jme.math.FastMath;
import com.jme.math.Vector2f;
import com.jme.scene.BillboardNode;
import com.jme.scene.Node;
import com.jme.scene.Spatial.LightCombineMode;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.RenderState.StateType;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;

public class TextLabel2D extends Node {

    private String text;
    private float blurIntensity = 0.5f;
    private int kernelSize = 5;
    private ConvolveOp blur;
    private Color foreground = new Color(1f, 1f, 1f);
    private Color background = new Color(0f, 0f, 0f);
    private float fontResolution = 20f;
    private float shadowOffsetX = 1.5f;
    private float shadowOffsetY = 1.5f;
    private Font font;
    private Font drawFont;
    private float height = 1f;
    private FontRenderContext fontRenderContext = null;
    private Quad quad;
    private float imgWidth = 0f;
    private float imgHeight = 0f;
    private float imgFactor = 0f;

    public TextLabel2D(String text) {
        this(text, new Color(1f, 1f, 1f), new Color(0f, 0f, 0f), 0.3f, false, null);
    }

    public TextLabel2D(String text, Color foreground, Color background,
            float height, boolean billboard, Font font) {
        super();
        this.text = text;
        this.foreground = foreground;
        this.background = background;
        this.height = height;
        updateKernel();
        if (font == null) {
            font = Font.decode("Sans PLAIN");
        }
        setFont(font);
        attachChild(getBillboard());
    }

    public void setFont(Font font) {
        if (this.font==font)
            return;
        
        this.font = font;
        BufferedImage tmp0 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) tmp0.getGraphics();
        drawFont = font.deriveFont(fontResolution);

        fontRenderContext = g2d.getFontRenderContext();
    }

    public void setText(String text, Color foreground, Color background) {
        this.text = text;
        this.foreground = foreground;
        this.background = background;

        Quad oldQuad = quad;
        Quad updatedQuad = getQuad();

        if (updatedQuad!=oldQuad) {
            Node tmpParent = oldQuad.getParent();
            oldQuad.removeFromParent();
            TextureState texState = (TextureState) oldQuad.getRenderState(StateType.Texture);
            Texture tex = texState.getTexture();
            TextureManager.releaseTexture(tex);
            tmpParent.attachChild(updatedQuad);
        }
    }

    public void setShadowOffsetX(int offsetPixelX) {
        shadowOffsetX = offsetPixelX;
    }

    public void setShadowOffsetY(int offsetPixelY) {
        shadowOffsetY = offsetPixelY;
    }

    public void setBlurSize(int kernelSize) {
        this.kernelSize = kernelSize;
        updateKernel();
    }

    public void setBlurStrength(float strength) {
        this.blurIntensity = strength;
        updateKernel();
    }

    public void setFontResolution(float fontResolution) {
        if (this.fontResolution==fontResolution)
            return;

        this.fontResolution = fontResolution;
        BufferedImage tmp0 = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) tmp0.getGraphics();
        drawFont = font.deriveFont(fontResolution);

        fontRenderContext = g2d.getFontRenderContext();
    }

    /**
     * Set the height of the quad onto which the label image is applied.
     * @param height
     */
    public void setHeight(float height) {
        this.height = height;
    }

    private void updateKernel() {
        float[] kernel = new float[kernelSize * kernelSize];
        Arrays.fill(kernel, blurIntensity);
        blur = new ConvolveOp(new Kernel(kernelSize, kernelSize, kernel));
    }

    /**
     * Generate an image of the label
     *
     * @param scaleFactors is set to the factors needed to adjust texture coords
     * to the next power-of-two-sized resulting image
     */
    private BufferedImage getImage(Vector2f scaleFactors) {

        // calculate the size of the label text rendered with the specified font
        TextLayout layout = new TextLayout(text, font, fontRenderContext);
        Rectangle2D b = layout.getBounds();

        // calculate the width of the label with shadow and blur
        int actualWidth = (int) (b.getWidth() + kernelSize + 1 + shadowOffsetX);

        // calculate the maximum height of the text including the ascents and
        // descents of the characters
        int actualHeight = (int) (layout.getAscent() + layout.getDescent() + kernelSize + 1 + shadowOffsetY);

        // determine the closest power of two bounding box
        //
        // NOTE: we scale the text height to fit the nearest power or two, and
        // then scale the text width equally to maintain the correct aspect
        // ratio:
        int desiredHeight = FastMath.nearestPowerOfTwo(actualHeight);
        int desiredWidth = FastMath.nearestPowerOfTwo((int) (((float) desiredHeight / (float) actualHeight) * actualWidth));

        // set the scale factors for scaling the text to fit the nearest power
        // of two bounding box:
        if (scaleFactors != null) {
            // scale the text vertically to fit the height
            scaleFactors.y = (float) desiredHeight / actualHeight;
            // scale the text an equal amount horizontally to maintain aspect ratio
            scaleFactors.x = scaleFactors.y;
        }

        // create an image to render the text onto
        BufferedImage tmp0 = new BufferedImage(desiredWidth, desiredHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) tmp0.getGraphics();
        g2d.setFont(drawFont);

//        // draw debugging text alignment lines
//        g2d.setColor(Color.YELLOW);
//        g2d.drawLine(0, desiredHeight / 2, desiredWidth, desiredHeight / 2);
//        g2d.drawLine(desiredWidth / 2, 0, desiredWidth / 2, desiredHeight);

        // center the text on the label
        int scaledWidth = (int) (actualWidth * scaleFactors.x);
        int textX = desiredWidth / 2 - scaledWidth / 2;// + kernelSize / 2;
        int textY = desiredHeight / 2;

//        // draw debugging text left and right bounds lines
//        g2d.setColor(Color.RED);
//        g2d.drawLine(textX, 0, textX, desiredHeight);
//        g2d.drawLine(desiredWidth / 2 + scaledWidth / 2, 0, desiredWidth / 2 + scaledWidth / 2, desiredHeight);

        // draw the shadow of the text
        g2d.setFont(drawFont);
//        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setColor(background);
        g2d.drawString(text, textX + shadowOffsetX, textY + shadowOffsetY);

        // blur the text
        BufferedImage ret = blur.filter(tmp0, null);

        // draw the blurred text over the shadow
        g2d = (Graphics2D) ret.getGraphics();
        g2d.setFont(drawFont);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setColor(foreground);
        g2d.drawString(text, textX, textY);

        return ret;
    }

    private Quad getQuad() {
        Vector2f scales = new Vector2f();
        BufferedImage img = getImage(scales);

        float w = img.getWidth();
        float h = img.getHeight();
        float factor = height / h;
        Quad ret;

        if (imgWidth==w && imgHeight==h && imgFactor==factor) {
            // Reuse quad and texture
            ret = quad;
            TextureState texState = (TextureState) quad.getRenderState(StateType.Texture);
            Texture oldtex = texState.getTexture();
            // Not sure why this does not work, instead release the current texture and create a new one.
//            oldtex.setImage(TextureManager.loadImage(img, true));
//            texState.setTexture(oldtex);
            TextureManager.releaseTexture(oldtex);

            Texture tex = TextureManager.loadTexture(img, MinificationFilter.BilinearNoMipMaps, MagnificationFilter.Bilinear, true);

            texState.setTexture(tex);
            //end workaround
        } else {
            ret = new Quad("textLabel2d", w * factor, h * factor);
            TextureState ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
            Texture tex = TextureManager.loadTexture(img, MinificationFilter.BilinearNoMipMaps, MagnificationFilter.Bilinear, true);

            ts.setTexture(tex);
            ts.setEnabled(true);
            ret.setRenderState(ts);

            BlendState as = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
            as.setBlendEnabled(false);
            as.setReference(0.5f);
            as.setTestFunction(BlendState.TestFunction.GreaterThan);
            as.setTestEnabled(true);
            ret.setRenderState(as);

            ret.setLightCombineMode(LightCombineMode.Off);
            ret.updateRenderState();
            this.quad = ret;
            imgWidth = w;
            imgHeight = h;
            imgFactor = factor;
        }

        return ret;
    }

    private BillboardNode getBillboard() {
        BillboardNode bb = new BillboardNode("bb");
        bb.attachChild(getQuad());
        return bb;
    }
}