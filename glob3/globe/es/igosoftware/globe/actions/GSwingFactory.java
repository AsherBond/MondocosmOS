

package es.igosoftware.globe.actions;

import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;


public class GSwingFactory {

   private GSwingFactory() {
      // private, only static methods
   }


   public static JMenuItem createMenuItem(final String label,
                                          final Icon icon,
                                          final char mnemonic,
                                          final ActionListener actionListener) {
      final JMenuItem widget = new JMenuItem(label, icon);
      if (mnemonic != ' ') {
         widget.setMnemonic(mnemonic);
      }
      widget.addActionListener(actionListener);
      return widget;
   }


   public static JButton createToolbarButton(final Icon icon,
                                             final String label,
                                             final ActionListener actionListener) {
      final JButton widget = createToolbarButton(icon, label);

      if (actionListener != null) {
         widget.addActionListener(actionListener);
      }

      return widget;
   }


   public static JButton createToolbarButton(final Icon icon,
                                             final String label) {

      final JButton widget;
      if (icon == null) {
         widget = new JButton(label);
      }
      else {
         widget = new JButton(icon);
         widget.setToolTipText(label);
      }

      widget.putClientProperty(SubstanceLookAndFeel.FLAT_PROPERTY, Boolean.TRUE);
      widget.putClientProperty(SubstanceLookAndFeel.BUTTON_NO_MIN_SIZE_PROPERTY, Boolean.TRUE);

      return widget;
   }


   public static JToggleButton createToolbarCheckBox(final Action action) {
      final JToggleButton widget = new JToggleButton(action);

      widget.putClientProperty(SubstanceLookAndFeel.FLAT_PROPERTY, Boolean.TRUE);
      widget.putClientProperty(SubstanceLookAndFeel.BUTTON_NO_MIN_SIZE_PROPERTY, Boolean.TRUE);

      return widget;
   }


   public static JToggleButton createToolbarCheckBox(final Icon icon,
                                                     final String label,
                                                     final boolean initState,
                                                     final ActionListener actionListener) {

      final JToggleButton widget;
      if (icon == null) {
         widget = new JToggleButton(label);
      }
      else {
         widget = new JToggleButton(icon);
         widget.setToolTipText(label);
      }

      widget.putClientProperty(SubstanceLookAndFeel.FLAT_PROPERTY, Boolean.TRUE);
      widget.putClientProperty(SubstanceLookAndFeel.BUTTON_NO_MIN_SIZE_PROPERTY, Boolean.TRUE);

      if (actionListener != null) {
         widget.addActionListener(actionListener);
      }

      widget.setSelected(initState);

      return widget;
   }


}
