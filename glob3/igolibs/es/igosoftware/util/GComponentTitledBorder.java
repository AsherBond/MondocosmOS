

package es.igosoftware.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;


public class GComponentTitledBorder
         implements
            Border,
            MouseListener,
            SwingConstants {

   private final int        _offset = 5;

   private final Component  _comp;
   private final JComponent _container;
   private final Border     _border;
   private Rectangle        _rect;


   public GComponentTitledBorder(final Component comp,
                                 final JComponent container,
                                 final Border border) {
      _comp = comp;
      _container = container;
      _border = border;
      container.addMouseListener(this);
   }


   @Override
   public boolean isBorderOpaque() {
      return true;
   }


   @Override
   public void paintBorder(final Component c,
                           final Graphics g,
                           final int x,
                           final int y,
                           final int width,
                           final int height) {
      if (g instanceof Graphics2D) {
         final Graphics2D g2d = (Graphics2D) g;
         g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      }

      final Insets borderInsets = _border.getBorderInsets(c);
      final Insets insets = getBorderInsets(c);
      final int temp = (insets.top - borderInsets.top) / 2;
      //      final Shape lastClip = g.getClip();
      //      g.setClip(_rect);
      _border.paintBorder(c, g, x, y + temp, width, height - temp);
      final Dimension size = _comp.getPreferredSize();
      _rect = new Rectangle(_offset, 0, size.width, size.height);
      SwingUtilities.paintComponent(g, _comp, (Container) c, _rect);
      //      g.setClip(lastClip);
   }


   @Override
   public Insets getBorderInsets(final Component c) {
      final Dimension size = _comp.getPreferredSize();
      final Insets insets = _border.getBorderInsets(c);
      insets.top = Math.max(insets.top, size.height);
      return insets;
   }


   private void dispatchEvent(final MouseEvent evt) {
      if ((_rect != null) && _rect.contains(evt.getX(), evt.getY())) {
         final Point pt = evt.getPoint();
         pt.translate(-_offset, 0);
         _comp.setBounds(_rect);
         _comp.dispatchEvent(new MouseEvent(_comp, evt.getID(), evt.getWhen(), evt.getModifiers(), pt.x, pt.y,
                  evt.getClickCount(), evt.isPopupTrigger(), evt.getButton()));
         if (!_comp.isValid()) {
            _container.repaint();
         }
      }
   }


   @Override
   public void mouseClicked(final MouseEvent evt) {
      dispatchEvent(evt);
   }


   @Override
   public void mouseEntered(final MouseEvent evt) {
      dispatchEvent(evt);
   }


   @Override
   public void mouseExited(final MouseEvent evt) {
      dispatchEvent(evt);
   }


   @Override
   public void mousePressed(final MouseEvent evt) {
      dispatchEvent(evt);
   }


   @Override
   public void mouseReleased(final MouseEvent evt) {
      dispatchEvent(evt);
   }


}
