/*

 IGO Software SL  -  info@igosoftware.es

 http://www.glob3.org

-------------------------------------------------------------------------------
 Copyright (c) 2010, IGO Software SL
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
     * Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.
     * Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.
     * Neither the name of the IGO Software SL nor the
       names of its contributors may be used to endorse or promote products
       derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL IGO Software SL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-------------------------------------------------------------------------------

*/


package es.igosoftware.globe.layers.hud;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.media.opengl.GL;

import com.sun.opengl.util.texture.TextureIO;

import es.igosoftware.util.GAssert;
import es.igosoftware.util.GMath;
import es.igosoftware.util.GUtils;
import es.igosoftware.utils.GGLUtils;
import es.igosoftware.utils.GTexture;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.render.DrawContext;


public class GHUDIcon
         implements
            IHUDElement {


   public static enum Position {
      SOUTH,
      NORTH,
      NORTH_WEST,
      SOUTH_WEST,
      NORTH_EAST,
      SOUTH_EAST;
   }


   private static final Font       DEFAULT_LABEL_FONT         = new Font("Serif", Font.BOLD, 12);
   private static final Color      DEFAULT_LABEL_COLOR        = new Color(0f, 0f, 0f, 1f);
   private static final Color      DEFAULT_LABEL_SHADOW_COLOR = new Color(1f, 1f, 1f, 0.9f);


   private BufferedImage           _image;

   private GTexture                _texture;
   private int                     _textureWidth;
   private int                     _textureHeight;

   private final GHUDIcon.Position _position;
   private int                     _borderWidth               = 20;
   private int                     _borderHeight              = 20;
   private float                   _opacity                   = 0.65f;

   private boolean                 _isEnable                  = true;
   private double                  _distanceFromEye           = 0;

   private Rectangle               _lastScreenBounds;
   private boolean                 _highlighted;

   private List<ActionListener>    _actionListeners;


   private String                  _label;
   private Font                    _labelFont;
   private Color                   _labelColor;
   private Color                   _labelShadowColor;
   //   private DrawContext             _lastDC;
   private final List<Runnable>    _frameWorkers              = new LinkedList<Runnable>();


   public GHUDIcon(final BufferedImage image,
                   final GHUDIcon.Position position) {
      this(image, null, position);
   }


   public GHUDIcon(final BufferedImage image,
                   final String labelOrNull,
                   final GHUDIcon.Position position) {
      GAssert.notNull(image, "image");
      GAssert.notNull(position, "position");

      _image = image;
      _label = labelOrNull;
      _position = position;
   }


   @Override
   public double getDistanceFromEye() {
      return _distanceFromEye;
   }


   @Override
   public void pick(final DrawContext dc,
                    final Point pickPoint) {
      // do nothing on pick
   }


   @Override
   public void render(final DrawContext dc) {
      //      _lastDC = dc;

      runFrameWorkers();

      final GTexture texture = getTexture();
      if ((texture == null) || !texture.hasGLTexture()) {
         return;
      }

      final GL gl = dc.getGL();

      gl.glPushAttrib(GL.GL_DEPTH_BUFFER_BIT | GL.GL_COLOR_BUFFER_BIT | GL.GL_ENABLE_BIT | GL.GL_TEXTURE_BIT
                      | GL.GL_TRANSFORM_BIT | GL.GL_VIEWPORT_BIT | GL.GL_CURRENT_BIT);

      gl.glEnable(GL.GL_BLEND);
      gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
      gl.glDisable(GL.GL_DEPTH_TEST);


      // Load a parallel projection with xy dimensions (viewportWidth, viewportHeight)
      // into the GL projection matrix.
      final Rectangle viewport = dc.getView().getViewport();
      gl.glMatrixMode(GL.GL_PROJECTION);
      gl.glPushMatrix();
      gl.glLoadIdentity();
      final double maxwh = Math.max(_textureWidth, _textureHeight);
      gl.glOrtho(0d, viewport.width, 0d, viewport.height, -0.6 * maxwh, 0.6 * maxwh);


      gl.glMatrixMode(GL.GL_MODELVIEW);
      gl.glPushMatrix();
      gl.glLoadIdentity();

      // Translate and scale
      final float scale = computeScale(viewport);
      final Vec4 locationSW = computeLocation(viewport, scale);
      gl.glTranslated(locationSW.x(), locationSW.y(), locationSW.z());
      // Scale to 0..1 space
      gl.glScalef(scale, scale, 1f);
      gl.glScaled(_textureWidth, _textureHeight, 1d);


      _lastScreenBounds = calculateScreenBounds(viewport, locationSW, scale);


      texture.enable();
      texture.bind();
      gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);

      gl.glColor4f(1, 1, 1, calculateOpacity());
      dc.drawUnitQuad(texture.getImageTexCoords());

      texture.disable();


      gl.glMatrixMode(GL.GL_PROJECTION);
      gl.glPopMatrix();

      gl.glMatrixMode(GL.GL_MODELVIEW);
      gl.glPopMatrix();

      gl.glPopAttrib();
   }


   private GTexture getTexture() {
      if (_texture == null) {
         _texture = initializeTexture();
         if ((_texture == null) || !_texture.hasGLTexture()) {
            return null;
         }

         _textureWidth = _texture.getWidth();
         _textureHeight = _texture.getHeight();
      }

      return _texture;
   }


   private static final void drawString(final Graphics2D g2d,
                                        final String str,
                                        final double x,
                                        final double y,
                                        final Paint paint) {
      g2d.setPaint(paint);

      g2d.drawString(str, (float) x, (float) y);
   }


   private static final void drawShadowedStringCentered(final Graphics2D g2d,
                                                        final String str,
                                                        final double x,
                                                        final double y,
                                                        final Font font,
                                                        final Paint paint,
                                                        final double shadowOffset,
                                                        final Paint shadowPaint) {

      g2d.setFont(font);

      final FontRenderContext frc = g2d.getFontRenderContext();
      final Rectangle2D bounds = font.getStringBounds(str, frc);
      final LineMetrics metrics = font.getLineMetrics(str, frc);
      final double width = bounds.getWidth(); // The width of our text
      final float lineHeight = metrics.getHeight(); // Total line height
      final float ascent = metrics.getAscent(); // Top of text to baseline

      final double cx = (x + (0 - width) / 2);
      final double cy = (y + (0 - lineHeight) / 2 + ascent);

      if (shadowOffset > 0) {
         drawString(g2d, str, cx + shadowOffset, cy - shadowOffset, shadowPaint);
         drawString(g2d, str, cx + shadowOffset, cy + shadowOffset, shadowPaint);
         drawString(g2d, str, cx - shadowOffset, cy - shadowOffset, shadowPaint);
         drawString(g2d, str, cx - shadowOffset, cy + shadowOffset, shadowPaint);
      }
      drawString(g2d, str, cx, cy, paint);


   }


   private float computeScale(final Rectangle viewport) {
      return Math.min(1, (float) viewport.width / _textureWidth) * (_highlighted ? 1.15f : 1f);
   }


   private Rectangle calculateScreenBounds(final Rectangle viewport,
                                           final Vec4 position,
                                           final float scale) {
      final int iWidth = toInt(_textureWidth * scale);
      final int iHeight = toInt(_textureHeight * scale);
      final int iX = toInt(position.x);
      final int iY = viewport.height - iHeight - toInt(position.y);
      return new Rectangle(iX, iY, iWidth, iHeight);
   }


   private static int toInt(final double value) {
      return GMath.toInt(Math.round(value));
   }


   private Vec4 computeLocation(final Rectangle viewport,
                                final double scale) {
      final double width = _textureWidth * scale;
      final double height = _textureHeight * scale;

      final double vpWidth = viewport.getWidth();
      final double vpHeight = viewport.getHeight();
      double x = 0;
      double y = 0;

      switch (_position) {
         case SOUTH:
            x = (vpWidth / 2) - (width / 2);
            y = _borderHeight;
            break;

         case NORTH:
            x = (vpWidth / 2) - (width / 2);
            y = vpHeight - height - _borderHeight;
            break;

         case NORTH_EAST:
            x = vpWidth - width - _borderWidth;
            y = vpHeight - height - _borderHeight;
            break;

         case SOUTH_EAST:
            x = vpWidth - width - _borderWidth;
            y = _borderHeight;
            break;

         case NORTH_WEST:
            x = _borderWidth;
            y = vpHeight - height - _borderHeight;
            break;

         case SOUTH_WEST:
            x = _borderWidth;
            y = _borderHeight;
            break;

      }

      return new Vec4(x, y, 0);
   }


   public float getOpacity() {
      return _opacity;
   }


   private float calculateOpacity() {
      if (_highlighted) {
         return 1.0f;
      }
      return _opacity;
   }


   public void setOpacity(final float opacity) {
      _opacity = opacity;
   }


   public int getBorderWidth() {
      return _borderWidth;
   }


   public void setBorderWidth(final int borderWidth) {
      _borderWidth = borderWidth;
   }


   public int getBorderHeight() {
      return _borderHeight;
   }


   public void setBorderHeight(final int borderHeight) {
      _borderHeight = borderHeight;
   }


   @Override
   public boolean isEnable() {
      return _isEnable;
   }


   public void setEnable(final boolean isEnable) {
      if (_isEnable == isEnable) {
         return;
      }

      _isEnable = isEnable;
   }


   public void setDistanceFromEye(final double distanceFromEye) {
      _distanceFromEye = distanceFromEye;
   }


   @Override
   public String toString() {
      return "GHUDIcon [texture=" + _texture + ", position=" + _position + "]";
   }


   @Override
   public Rectangle getLastScreenBounds() {
      return _lastScreenBounds;
   }


   @Override
   public void setHighlighted(final boolean highlighted) {
      _highlighted = highlighted;
   }


   @Override
   public boolean hasActionListeners() {
      return (_actionListeners != null) && !_actionListeners.isEmpty();
   }


   @Override
   public void mouseClicked(final MouseEvent evt) {
      if ((_actionListeners == null) || _actionListeners.isEmpty()) {
         return;
      }

      notifyListenersInOpenGLThread(evt);

      //      for (final ActionListener listener : _actionListeners) {
      //         final ActionEvent actionEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null, evt.getWhen(), 0);
      //         listener.actionPerformed(actionEvent);
      //      }
   }


   protected void notifyListenersInOpenGLThread(final MouseEvent evt) {
      //      _lastDC.getModel().getLayers().add(new GOnFirstRenderLayer() {
      //         @Override
      //         protected void execute(final DrawContext dc) {
      //            for (final ActionListener listener : _actionListeners) {
      //               final ActionEvent actionEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null, evt.getWhen(), 0);
      //               listener.actionPerformed(actionEvent);
      //            }
      //         }
      //      });

      GGLUtils.invokeOnOpenGLThread(new Runnable() {
         @Override
         public void run() {
            for (final ActionListener listener : _actionListeners) {
               final ActionEvent actionEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null, evt.getWhen(), 0);
               listener.actionPerformed(actionEvent);
            }
         }
      });
   }


   public void removeActionListener(final ActionListener listener) {
      if (_actionListeners == null) {
         return;
      }

      _actionListeners.remove(listener);

      if (_actionListeners.isEmpty()) {
         _actionListeners = null;
      }
   }


   public void addActionListener(final ActionListener listener) {
      if (_actionListeners == null) {
         _actionListeners = new ArrayList<ActionListener>(2);
      }
      _actionListeners.add(listener);
   }


   public void setImage(final BufferedImage image) {
      if (GUtils.equals(_image, image)) {
         return;
      }

      _image = image;
      clearTexture();
   }


   private void runFrameWorkers() {
      synchronized (_frameWorkers) {
         if (_frameWorkers.isEmpty()) {
            return;
         }

         for (final Runnable frameWorker : _frameWorkers) {
            frameWorker.run();
         }
         _frameWorkers.clear();
      }
   }


   private void clearTexture() {
      if (_texture == null) {
         return;
      }

      final GTexture texture = _texture;
      if (texture != null) {
         addFrameWorker(new Runnable() {
            @Override
            public void run() {
               GGLUtils.disposeTexture(texture);
            }
         });
         _texture = null;
      }
   }


   private void addFrameWorker(final Runnable worker) {
      synchronized (_frameWorkers) {
         _frameWorkers.add(worker);
      }
   }


   private GTexture initializeTexture() {
      final BufferedImage image;
      if ((_label == null) || _label.isEmpty()) {
         image = _image;
      }
      else {
         image = new BufferedImage(_image.getWidth(), _image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);

         final Graphics2D g2d = image.createGraphics();

         g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
         //         g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
         //         g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
         //         g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
         g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


         g2d.drawImage(_image, 0, 0, null);

         final Color shadowColor = getLabelShadowColor();
         final int shadowOffset = (shadowColor == null) ? 0 : 1;
         drawShadowedStringCentered(g2d, _label, _image.getWidth() / 2d, _image.getHeight() / 2d, getLabelFont(),
                  getLabelColor(), shadowOffset, shadowColor);

         g2d.dispose();
      }

      return new GTexture(TextureIO.newTexture(image, true));
   }


   public Color getLabelShadowColor() {
      return (_labelShadowColor == null) ? DEFAULT_LABEL_SHADOW_COLOR : _labelShadowColor;
   }


   public Color getLabelColor() {
      return (_labelColor == null) ? DEFAULT_LABEL_COLOR : _labelColor;
   }


   public Font getLabelFont() {
      return (_labelFont == null) ? DEFAULT_LABEL_FONT : _labelFont;
   }


   public String getLabel() {
      return _label;
   }


   public void setLabel(final String label) {
      if (GUtils.equals(_label, label)) {
         return;
      }

      _label = label;
      clearTexture();
   }


   public void setLabelColor(final Color labelColor) {
      if (GUtils.equals(_labelColor, labelColor)) {
         return;
      }

      _labelColor = labelColor;
      clearTexture();
   }


   public void setLabelShadowColor(final Color labelShadowColor) {
      if (GUtils.equals(_labelShadowColor, labelShadowColor)) {
         return;
      }

      _labelShadowColor = labelShadowColor;
      clearTexture();
   }


   public void setLabelFont(final Font font) {
      if (GUtils.equals(_labelFont, font)) {
         return;
      }

      _labelFont = font;
      clearTexture();
   }


}
