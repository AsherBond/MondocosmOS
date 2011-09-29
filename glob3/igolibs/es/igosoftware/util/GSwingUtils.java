

package es.igosoftware.util;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JList;
import javax.swing.SwingUtilities;

import es.igosoftware.logging.GLogger;


public class GSwingUtils {


   private GSwingUtils() {
      // private, only static methods
   }


   public static Font makeBold(final Font font) {
      return font.deriveFont(font.getStyle() ^ Font.BOLD);
   }


   public static <ComponentT extends Component> ComponentT makeBold(final ComponentT component) {
      component.setFont(makeBold(component.getFont()));
      return component;
   }


   public static Font makeBigger(final Font font,
                                 final float delta) {
      return font.deriveFont(font.getSize() + delta);
   }


   public static void repaint(final Component component) {
      if (component == null) {
         return;
      }

      if (component instanceof JList) {
         repaint((JList) component);
         return;
      }

      component.invalidate();
      component.doLayout();
      component.repaint();
   }


   public static void repaint(final JList list) {
      if (list == null) {
         return;
      }

      list.updateUI();
   }


   public static void invokeInSwingThread(final Runnable runnable) {
      if (EventQueue.isDispatchThread()) {
         runnable.run();
      }
      else {
         try {
            SwingUtilities.invokeAndWait(runnable);
         }
         catch (final InterruptedException e) {
            GLogger.instance().logSevere(e);
         }
         catch (final InvocationTargetException e) {
            GLogger.instance().logSevere(e);
         }
      }
   }


}
