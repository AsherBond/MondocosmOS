

package es.igosoftware.globe;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.fonts.FontPolicy;
import org.pushingpixels.substance.api.fonts.FontSet;
import org.pushingpixels.substance.api.skin.SubstanceMistAquaLookAndFeel;

import es.igosoftware.globe.actions.GSwingFactory;
import es.igosoftware.globe.actions.IGenericAction;
import es.igosoftware.globe.layers.hud.GHUDIcon;
import es.igosoftware.globe.layers.hud.GHUDLayer;
import es.igosoftware.io.GFileName;
import es.igosoftware.logging.ILogger;
import es.igosoftware.util.GPair;
import es.igosoftware.util.GSwingUtils;
import es.igosoftware.util.GUtils;
import es.igosoftware.utils.GWrapperFontSet;
import gov.nasa.worldwind.layers.Layer;


public abstract class GGlobeWidget<ApplicationT extends GGlobeApplication>
         extends
            JApplet {


   static {
      initializeGUI();
   }


   public static void initializeGUI() {
      if (GUtils.isWindows()) {
         System.setProperty("sun.awt.noerasebackground", "true"); // prevents flashing during window resizing
      }

      if (GUtils.isMac()) {
         // System.setProperty("com.apple.mrj.application.growbox.intrudes", "false");
         // System.setProperty("apple.laf.useScreenMenuBar", "true");
         // System.setProperty("com.apple.mrj.application.apple.menu.about.name", "glob3");
      }


      //      System.setProperty("sun.java2d.noddraw", "true");
      //      System.setProperty("sun.java2d.opengl", "true");


      UIManager.put("ClassLoader", GGlobeWidget.class.getClassLoader());

      JPopupMenu.setDefaultLightWeightPopupEnabled(false);
      ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);

      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            initializeLookAndFeel();
         }
      });
   }


   private static void initializeLookAndFeel() {
      final String useSubstance = System.getProperty("glob3.use_substance", "true");
      if (useSubstance.trim().equalsIgnoreCase("true")) {
         JFrame.setDefaultLookAndFeelDecorated(true);
         JDialog.setDefaultLookAndFeelDecorated(true);

         try {
            UIManager.setLookAndFeel(new SubstanceMistAquaLookAndFeel());

            SubstanceLookAndFeel.setToUseConstantThemesOnDialogs(true);
         }
         catch (final UnsupportedLookAndFeelException e) {
            e.printStackTrace();
         }

         initializeFonts();
      }
      else {
         try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
         }
         catch (final Exception e) {
            e.printStackTrace();
         }
      }
   }


   private static void initializeFonts() {
      if (GUtils.isWindows()) {
         return;
      }

      // reset the base font policy to null - this
      // restores the original font policy (default size).
      SubstanceLookAndFeel.setFontPolicy(null);

      // Get the default font set
      final FontSet substanceCoreFontSet = SubstanceLookAndFeel.getFontPolicy().getFontSet("Substance", null);

      // Create the wrapper font set
      final FontPolicy newFontPolicy = new FontPolicy() {
         @Override
         public FontSet getFontSet(final String lafName,
                                   final UIDefaults table) {
            final int delta;
            if (GUtils.isMac()) {
               delta = -3;
            }
            else {
               delta = -1;
            }
            return new GWrapperFontSet(substanceCoreFontSet, delta);
         }
      };

      // set the new font policy
      SubstanceLookAndFeel.setFontPolicy(newFontPolicy);
   }


   private static final long serialVersionUID = 1L;


   private ApplicationT      _application;

   // UI elements, they must be final
   private JSplitPane        _splitPane;
   private int               _splitPaneSavedPosition;

   private JMenuBar          _menubar;
   private JMenu             _fileMenu;
   private JMenu             _navigationMenu;
   private JMenu             _viewMenu;
   private JMenu             _editMenu;
   private JMenu             _analysisMenu;
   private JMenu             _helpMenu;

   private JToolBar          _toolbar;

   private JFrame            _frame;


   private boolean           _initialized     = false;
   private GHUDIcon          _downloadingHUDIcon;


   public GGlobeWidget() {
   }


   protected abstract ApplicationT createApplication();


   public ApplicationT getApplication() {
      if (_application == null) {
         _application = createApplication();
         _application.setWidget(this);
      }
      return _application;
   }


   private ILogger getLogger() {
      return getApplication().getLogger();
   }


   private IGlobeTranslator getTranslator() {
      return getRunningContext().getTranslator();
   }


   public IGlobeRunningContext getRunningContext() {
      return getApplication().getRunningContext();
   }


   private IGlobeBitmapFactory getBitmapFactory() {
      return getRunningContext().getBitmapFactory();
   }


   @Override
   public void init() {
      GSwingUtils.invokeInSwingThread(new Runnable() {
         @Override
         public void run() {
            getLogger().logInfo("Initializing...");


            getApplication().init();

            SwingUtilities.updateComponentTreeUI(GGlobeWidget.this);

            getLogger().logInfo("Initializing GUI...");
            initGUI();

            getLogger().logInfo("Initializing Modules GUI...");
            initModulesGUI();

            getLogger().logInfo("Final Initializing step...");
            postInitGUI();
         }
      });
   }


   protected void initModulesGUI() {
      IGlobeModule previousModule = null;
      for (final IGlobeModule module : getApplication().getModules()) {
         initModuleGUI(module, previousModule);
         previousModule = module;
      }
   }


   /**
    * Initialize the GUI layout, by default only the WorldWindowGLCanvas is visible on CENTER.
    * 
    * Overwrite to implements alternative layouts (don't forget to put the WorldWindowGLCanvas into the new layout)
    * 
    * @param frame
    *           the container frame (if any), null in applets
    */
   protected void initGUI() {
      final Container contentPane = getContentPane();

      _menubar = createMenuBar();

      _toolbar = createToolbar();

      final Component leftPane = createLeftPane();
      if (leftPane == null) {
         contentPane.add(getApplication().getWorldWindowGLCanvas(), BorderLayout.CENTER);
      }
      else {
         _splitPane = createSplitPane(leftPane);
         contentPane.add(_splitPane, BorderLayout.CENTER);
      }
   }


   private Set<IGenericAction.MenuArea> neededMenuAreas() {
      final HashSet<IGenericAction.MenuArea> result = new HashSet<IGenericAction.MenuArea>();

      if (!isApplet()) {
         result.add(IGenericAction.MenuArea.FILE);
      }

      for (final IGlobeModule module : getApplication().getModules()) {
         final List<? extends IGenericAction> actions = module.getGenericActions(getRunningContext());
         if (actions != null) {
            for (final IGenericAction action : actions) {
               if (action.isVisible()) {
                  result.add(action.getMenuBarArea());
               }
            }
         }
      }

      return result;
   }


   private JMenuBar createMenuBar() {
      final JMenuBar menubar = new JMenuBar();

      final Set<IGenericAction.MenuArea> neededMenuAreas = neededMenuAreas();

      if (neededMenuAreas.contains(IGenericAction.MenuArea.FILE)) {
         menubar.add(createFileMenu());
      }

      if (neededMenuAreas.contains(IGenericAction.MenuArea.NAVIGATION)) {
         menubar.add(createNavigationMenu());
      }

      if (neededMenuAreas.contains(IGenericAction.MenuArea.VIEW)) {
         menubar.add(createViewMenu());
      }

      if (neededMenuAreas.contains(IGenericAction.MenuArea.EDIT)) {
         menubar.add(createEditMenu());
      }

      if (neededMenuAreas.contains(IGenericAction.MenuArea.ANALYSIS)) {
         menubar.add(createAnalysisMenu());
      }

      if (neededMenuAreas.contains(IGenericAction.MenuArea.HELP)) {
         menubar.add(createHelpMenu());
      }

      return menubar;
   }


   private JMenu createFileMenu() {
      _fileMenu = new JMenu(getTranslator().getTranslation("File"));
      _fileMenu.setMnemonic('F');

      return _fileMenu;
   }


   private JMenu createViewMenu() {
      _viewMenu = new JMenu(getTranslator().getTranslation("View"));
      _viewMenu.setMnemonic('V');

      return _viewMenu;
   }


   private JMenu createEditMenu() {
      _editMenu = new JMenu(getTranslator().getTranslation("Edit"));
      _editMenu.setMnemonic('E');

      return _editMenu;
   }


   private JMenu createAnalysisMenu() {
      _analysisMenu = new JMenu(getTranslator().getTranslation("Analysis"));
      _analysisMenu.setMnemonic('A');

      return _analysisMenu;
   }


   private JMenu createNavigationMenu() {
      _navigationMenu = new JMenu(getTranslator().getTranslation("Navigation"));
      _navigationMenu.setMnemonic('N');

      return _navigationMenu;
   }


   private JMenu createHelpMenu() {
      _helpMenu = new JMenu(getTranslator().getTranslation("Help"));
      _helpMenu.setMnemonic('H');

      return _helpMenu;
   }


   private JToolBar createToolbar() {
      final JToolBar toolbar = new JToolBar();
      toolbar.setRollover(true);
      toolbar.setFloatable(false);

      return toolbar;
   }


   private JSplitPane createSplitPane(final Component leftPane) {
      final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
      splitPane.setContinuousLayout(true);
      splitPane.setOneTouchExpandable(true);

      final ApplicationT application = getApplication();

      splitPane.setDividerLocation(Math.round(application.initialDimension().width * application.getLeftPanelWidthRatio())
                                   + splitPane.getInsets().left);

      splitPane.setLeftComponent(leftPane);
      splitPane.setRightComponent(application.getWorldWindowGLCanvas());

      return splitPane;
   }


   protected void postInitGUI() {

      if (_menubar.getComponentCount() > 0) {
         setJMenuBar(_menubar);
      }

      if (_toolbar.getComponentCount() > 0) {
         getContentPane().add(_toolbar, BorderLayout.NORTH);
      }


      if (_fileMenu == null) {
         return;
      }

      if (!isApplet()) {
         final JMenuItem exitItem = GSwingFactory.createMenuItem(getTranslator().getTranslation("Exit"),
                  getBitmapFactory().getSmallIcon(GFileName.relative("quit.png")), 'x', new ActionListener() {
                     @Override
                     public void actionPerformed(final ActionEvent e) {
                        getApplication().exit();
                     }
                  });
         _fileMenu.add(exitItem);
      }
   }


   private Component createLeftPane() {
      final ArrayList<GPair<String, Component>> allPanels = new ArrayList<GPair<String, Component>>();

      final ApplicationT application = getApplication();

      for (final IGlobeModule module : application.getModules()) {
         final List<GPair<String, Component>> modulePanels = module.getPanels(getRunningContext());
         if (modulePanels != null) {
            allPanels.addAll(modulePanels);
         }
      }

      final Collection<? extends GPair<String, Component>> applicationPanels = application.getApplicationPanels(getRunningContext());
      if (applicationPanels != null) {
         allPanels.addAll(applicationPanels);
      }

      if (allPanels.isEmpty()) {
         return null;
      }

      if (allPanels.size() == 1) {
         return allPanels.get(0)._second;
      }

      final JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
      tabbedPane.setBorder(BorderFactory.createEmptyBorder());
      tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

      for (final GPair<String, Component> modulePanel : allPanels) {
         tabbedPane.addTab(getTranslator().getTranslation(modulePanel._first), modulePanel._second);
      }

      return tabbedPane;
   }


   private void initModuleGUI(final IGlobeModule module,
                              final IGlobeModule previousModule) {

      final List<? extends IGenericAction> genericActions = module.getGenericActions(getRunningContext());

      final Set<IGenericAction.MenuArea> firstUseFlags = new HashSet<IGenericAction.MenuArea>();

      boolean firstActionOnToolBar = true;
      if (genericActions != null) {
         for (final IGenericAction action : genericActions) {
            if (!action.isVisible()) {
               continue;
            }

            putActionOnMenuBar(action, firstUseFlags);

            if (action.isShowOnToolBar()) {
               if (firstActionOnToolBar) {
                  firstActionOnToolBar = false;
                  if (_toolbar.getComponents().length > 0) {
                     if ((previousModule != null) && (previousModule.getClass() != module.getClass())) {
                        _toolbar.addSeparator();
                     }
                  }
               }
               _toolbar.add(action.createToolbarWidget(getRunningContext()));
            }
         }
      }
   }


   private void putActionOnMenuBar(final IGenericAction action,
                                   final Set<IGenericAction.MenuArea> firstUseFlags) {
      final IGenericAction.MenuArea area = action.getMenuBarArea();

      final JMenu menu;
      switch (area) {
         case FILE:
            menu = _fileMenu;
            break;
         case NAVIGATION:
            menu = _navigationMenu;
            break;
         case HELP:
            menu = _helpMenu;
            break;
         case ANALYSIS:
            menu = _analysisMenu;
            break;
         case VIEW:
            menu = _viewMenu;
            break;
         case EDIT:
            menu = _editMenu;
            break;
         default:
            getLogger().logSevere("Invalid menu bar area: " + area);
            menu = null;
      }

      if (menu != null) {
         final boolean firstUse = !firstUseFlags.contains(area);
         if (firstUse) {
            firstUseFlags.add(area);
            if (menu.getComponents().length > 0) {
               menu.addSeparator();
            }
         }

         menu.add(action.createMenuWidget(getRunningContext()));
      }
   }


   public void prepareForFullScreen() {
      //      if (_menubar != null) {
      //         _menubar.setVisible(false);
      //      }

      if (_splitPane != null) {
         _splitPaneSavedPosition = _splitPane.getDividerLocation();
         _splitPane.setDividerLocation(0);
      }
   }


   public void prepareForNonFullScreen() {
      //     if (_menubar != null) {
      //         _menubar.setVisible(true);
      //      }

      if (_splitPane != null) {
         if (_splitPane.getDividerLocation() == 0) {
            // only restore to previous location if the pane is closed
            _splitPane.setDividerLocation(_splitPaneSavedPosition);
         }
      }
   }


   public void openInFrame() {
      if (_frame != null) {
         throw new RuntimeException("The widget already has a frame");
      }

      final ApplicationT application = getApplication();

      getLogger().logInfo("Opening frame...");
      _frame = new JFrame(application.getApplicationNameAndVersion());

      final Image imageIcon = application.getImageIcon();
      if (imageIcon != null) {
         _frame.setIconImage(imageIcon);
      }
      _frame.setSize(application.initialDimension());

      _frame.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(final WindowEvent event) {
            exit();
         }
      });

      _frame.setLocationRelativeTo(null);

      _frame.setContentPane(this);

      init();
      start();

      getLogger().logInfo("Opening frame...");
      _frame.setVisible(true);

      application.getWorldWindowGLCanvas().requestFocusInWindow();
   }


   public Frame getFrame() {
      return _frame;
   }


   public boolean isApplet() {
      return getFrame() == null;
   }


   protected void exit() {
      stop();
      destroy();

      getApplication().exit();
   }


   protected void dispose() {
      if (_frame != null) {
         _frame.dispose();
         _frame = null;

         System.exit(0);
      }
   }


   private GHUDIcon createDownloadingHUDIcon() {
      final IGlobeBitmapFactory factory = getRunningContext().getBitmapFactory();
      final BufferedImage image = factory.getImage(GFileName.relative("icons", "downloading.png"), 32, 32);

      final GHUDIcon icon = new GHUDIcon(image, GHUDIcon.Position.SOUTH_EAST);
      icon.setEnable(false);
      icon.setOpacity(0.7f);

      return icon;
   }


   private GHUDIcon getDownloadingHUDIcon() {
      if (_downloadingHUDIcon == null) {
         _downloadingHUDIcon = createDownloadingHUDIcon();
      }
      return _downloadingHUDIcon;
   }


   private boolean initializeHUDLayer() {
      final List<Layer> hudLayers = getRunningContext().getWorldWindModel().getLayerList().getLayersByClass(GHUDLayer.class);
      if (hudLayers.isEmpty()) {
         getLogger().logWarning("Can't find a " + GHUDLayer.class.getSimpleName() + " to hold the downloading-icon.");
         return false;
      }

      if (hudLayers.size() > 1) {
         getLogger().logWarning("There are more than one " + GHUDLayer.class.getSimpleName() + ", selecting the first one");
      }

      final GHUDLayer hudLayer = (GHUDLayer) hudLayers.get(0);

      final GHUDIcon downloadingHUDIcon = getDownloadingHUDIcon();
      if (!hudLayer.hasElement(downloadingHUDIcon)) {
         hudLayer.addElement(downloadingHUDIcon);
      }

      return true;
   }


   boolean changedRunningTasksCount(final long runningTasksCount) {
      if (!_initialized) {
         if (initializeHUDLayer()) {
            _initialized = true;
         }
      }

      final GHUDIcon icon = getDownloadingHUDIcon();
      if (icon == null) {
         return false;
      }

      boolean redraw = false;
      final boolean isDownloading = (runningTasksCount > 0);
      if (isDownloading) {
         icon.setLabel(Long.toString(runningTasksCount));
         redraw = true;
      }
      if (isDownloading != icon.isEnable()) {
         icon.setEnable(isDownloading);
         redraw = true;
      }

      return redraw;
   }


}
