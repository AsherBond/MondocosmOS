

package es.igosoftware.globe.modules;

import java.util.Collections;
import java.util.List;

import javax.swing.Icon;

import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.actions.GCheckBoxGenericAction;
import es.igosoftware.globe.actions.IGenericAction;
import es.igosoftware.util.GAssert;
import gov.nasa.worldwind.layers.Layer;


public class GEnableDisableLayerModule
         extends
            GAbstractGlobeModule {

   public static final String MS_Virtual_Earth_Aerial = "MS Virtual Earth Aerial";
   public static final String Political_Boundaries    = "Political Boundaries";

   private final Layer        _layer;
   private final String       _label;
   private final Icon         _icon;
   private final boolean      _showOnToolBar;
   private final boolean      _setEnableOnInit;


   public GEnableDisableLayerModule(final IGlobeRunningContext context,
                                    final Layer layer,
                                    final boolean setEnableOnInit) {
      this(context, layer, "Layer: " + layer.getName(), setEnableOnInit);
   }


   public GEnableDisableLayerModule(final IGlobeRunningContext context,
                                    final Layer layer,
                                    final String label,
                                    final boolean setEnableOnInit) {
      this(context, layer, label, null, false, setEnableOnInit);
   }


   public GEnableDisableLayerModule(final IGlobeRunningContext context,
                                    final Layer layer,
                                    final String label,
                                    final Icon icon,
                                    final boolean showOnToolBar,
                                    final boolean setEnableOnInit) {
      super(context);
      GAssert.notNull(layer, "layer");
      GAssert.notNull(label, "label");

      _layer = layer;

      _label = label;
      _icon = icon;
      _showOnToolBar = showOnToolBar;
      _setEnableOnInit = setEnableOnInit;
   }


   @Override
   public void initialize(final IGlobeRunningContext context) {
      super.initialize(context);

      if (_setEnableOnInit) {
         _layer.setEnabled(true);
      }
   }


   @Override
   public String getName() {
      return "Module for enable/disable Layer: \"" + _layer.getName() + "\"";
   }


   @Override
   public String getVersion() {
      return "0.1";
   }


   @Override
   public String getDescription() {
      return null;
   }


   @Override
   public List<? extends IGenericAction> getGenericActions(final IGlobeRunningContext context) {
      final IGenericAction switchLayer = new GCheckBoxGenericAction(_label, ' ', _icon, IGenericAction.MenuArea.VIEW,
               _showOnToolBar, _layer.isEnabled()) {
         @Override
         public void execute() {
            _layer.setEnabled(isSelected());
         }
      };

      // _layer.addPropertyChangeListener(new PropertyChangeListener() {
      //    @Override
      //    public void propertyChange(final PropertyChangeEvent evt) {
      //       switchLayer.setValue(switchLayer.isSelected());
      //    }
      // });

      return Collections.singletonList(switchLayer);
   }


}
