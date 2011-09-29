

package es.igosoftware.globe.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.util.GAssert;


public class GGroupGenericAction
         extends
            GGenericAction {


   private final IGenericAction[] _children;


   public GGroupGenericAction(final String label,
                              final char mnemonic,
                              final Icon icon,
                              final IGenericAction.MenuArea menuBarArea,
                              final boolean showOnToolBar,
                              final IGenericAction... children) {
      super(label, mnemonic, icon, menuBarArea, showOnToolBar);

      GAssert.notEmpty(children, "children");

      _children = Arrays.copyOf(children, children.length); // copy to prevent external modifications
   }


   @Override
   public JMenuItem createMenuWidget(final IGlobeRunningContext context) {
      final JMenu menu = new JMenu(getLabel());

      final Icon icon = getIcon();
      if (icon != null) {
         menu.setIcon(icon);
      }

      for (final IGenericAction child : _children) {
         menu.add(child.createMenuWidget(context));
      }

      return menu;
   }


   @Override
   public Component createToolbarWidget(final IGlobeRunningContext context) {
      final JButton button = GSwingFactory.createToolbarButton(getIcon(), context.getTranslator().getTranslation(getLabel()));

      button.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent event) {
            final JPopupMenu menu = new JPopupMenu(getLabel());
            menu.setLightWeightPopupEnabled(false);

            for (final IGenericAction child : _children) {
               menu.add(child.createMenuWidget(context));
            }

            menu.show(button, 0, button.getHeight());
         }
      });

      return button;
   }


   @Override
   public final void execute() {
      // do nothing
   }


}
