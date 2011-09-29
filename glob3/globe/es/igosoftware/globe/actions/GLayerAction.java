

package es.igosoftware.globe.actions;

import javax.swing.Icon;

import es.igosoftware.globe.GGlobeComponent;


public abstract class GLayerAction
         extends
            GGlobeComponent
         implements
            ILayerAction {


   private final String  _label;
   private final char    _mnemonic;
   private final Icon    _icon;
   private final boolean _showOnToolBar;


   protected GLayerAction(final String label,
                          final Icon icon,
                          final boolean showOnToolBar) {
      this(label, ' ', icon, showOnToolBar);
   }


   protected GLayerAction(final String label,
                          final char mnemonic,
                          final Icon icon,
                          final boolean showOnToolBar) {
      super();
      _label = label;
      _mnemonic = mnemonic;
      _icon = icon;
      _showOnToolBar = showOnToolBar;
   }


   @Override
   public Icon getIcon() {
      return _icon;
   }


   @Override
   public String getLabel() {
      return _label;
   }


   @Override
   public char getMnemonic() {
      return _mnemonic;
   }


   @Override
   public boolean isEnabled() {
      return true;
   }


   @Override
   public boolean isShowOnToolBar() {
      return _showOnToolBar;
   }

}
