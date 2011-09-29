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


package es.igosoftware.globe.modules;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.IGlobeApplication;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.IGlobeTranslator;
import es.igosoftware.globe.actions.GButtonGenericAction;
import es.igosoftware.globe.actions.IGenericAction;
import es.igosoftware.io.GFileName;
import es.igosoftware.util.GPair;


public class GHelpModule
         extends
            GAbstractGlobeModule {

   private static final String    DEFAULT_LABEL     = "Help";
   private static final GFileName DEFAULT_ICON_NAME = GFileName.relative("help.png");

   private final String           _label;
   private final GFileName        _iconName;
   protected String[][]           _languageHtmlURL;
   private final boolean          _autoOpen;
   private final boolean          _showInPanel;
   private boolean                _loadFromURL      = false;
   private final Color            _bgColor;


   public GHelpModule(final IGlobeRunningContext context,
                      final String htmlURL) {
      this(context, DEFAULT_LABEL, DEFAULT_ICON_NAME, htmlURL, false, false);
   }


   public GHelpModule(final IGlobeRunningContext context,
                      final String[][] languageHtmlURL) {
      this(context, DEFAULT_LABEL, DEFAULT_ICON_NAME, languageHtmlURL, false, false);
   }


   public GHelpModule(final IGlobeRunningContext context,
                      final String[][] languageHtmlURL,
                      final boolean loadFromURL) {
      this(context, DEFAULT_LABEL, DEFAULT_ICON_NAME, languageHtmlURL, false, false, loadFromURL);
   }


   public GHelpModule(final IGlobeRunningContext context,
                      final String label,
                      final GFileName iconName,
                      final String htmlURL,
                      final boolean autoOpen,
                      final boolean showInPanel) {
      this(context, label, iconName, htmlURL, autoOpen, showInPanel, null);
   }


   public GHelpModule(final IGlobeRunningContext context,
                      final String label,
                      final GFileName iconName,
                      final String[][] languageHtmlURL,
                      final boolean autoOpen,
                      final boolean showInPanel) {
      this(context, label, iconName, languageHtmlURL, autoOpen, showInPanel, null, false);
   }


   public GHelpModule(final IGlobeRunningContext context,
                      final String label,
                      final GFileName iconName,
                      final String[][] languageHtmlURL,
                      final boolean autoOpen,
                      final boolean showInPanel,
                      final boolean loadFromURL) {
      this(context, label, iconName, languageHtmlURL, autoOpen, showInPanel, null, loadFromURL);
   }


   public GHelpModule(final IGlobeRunningContext context,
                      final GFileName iconName,
                      final String[][] languageHtmlURL,
                      final boolean autoOpen,
                      final boolean showInPanel) {
      this(context, DEFAULT_LABEL, iconName, languageHtmlURL, autoOpen, showInPanel, null, false);
   }


   public GHelpModule(final IGlobeRunningContext context,
                      final String label,
                      final String htmlURL,
                      final boolean autoOpen,
                      final Color bgColor) {
      this(context, label, DEFAULT_ICON_NAME, htmlURL, autoOpen, false, bgColor);
   }


   public GHelpModule(final IGlobeRunningContext context,
                      final String label,
                      final GFileName iconName,
                      final String htmlURL,
                      final boolean autoOpen,
                      final boolean showInPanel,
                      final Color bgColor) {
      this(context, label, iconName, new String[][] { { null, htmlURL } }, autoOpen, showInPanel, bgColor, false);
   }


   public GHelpModule(final IGlobeRunningContext context,
                      final String label,
                      final GFileName iconName,
                      final String[][] languageHtmlURL,
                      final boolean autoOpen,
                      final boolean showInPanel,
                      final Color bgColor,
                      final boolean loadFromURL) {
      super(context);
      _label = label;
      _iconName = iconName;
      _languageHtmlURL = languageHtmlURL;
      _autoOpen = autoOpen;
      _showInPanel = showInPanel;
      _bgColor = bgColor;
      _loadFromURL = loadFromURL;
   }


   @Override
   public String getDescription() {
      return "Help Module";
   }


   @Override
   public List<? extends IGenericAction> getGenericActions(final IGlobeRunningContext context) {
      if (_showInPanel) {
         return Collections.emptyList();
      }

      final IGenericAction help = new GButtonGenericAction(_label, context.getBitmapFactory().getSmallIcon(_iconName),
               IGenericAction.MenuArea.HELP, true) {

         @Override
         public void execute() {
            showHelp(context);
         }
      };

      return Collections.singletonList(help);
   }


   public void setLanguageHtmlURLs(final String[][] languageHtmlURL) {
      _languageHtmlURL = languageHtmlURL;
   }


   private void showHelp(final IGlobeRunningContext context) {

      final IGlobeApplication application = context.getApplication();
      final IGlobeTranslator translator = context.getTranslator();

      try {
         final JScrollPane htmlPane = createHtmlPane(context, 5);

         final JPanel rootPanel = new JPanel();
         rootPanel.setLayout(new BorderLayout());
         rootPanel.setBorder(BorderFactory.createEmptyBorder());

         rootPanel.add(htmlPane, BorderLayout.CENTER);

         //final JFrame frame = application.getFrame();
         //         final JDialog dialog = new JDialog(frame, _label, true);
         final JDialog dialog = new JDialog(application.getFrame(), translator.getTranslation(_label), false);
         //         dialog.setResizable(false);
         dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
         //                  dialog.setIconImage(application.getImage(_iconName, 32, 32));

         dialog.getRootPane().registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
               dialog.dispose();
            }
         }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);


         dialog.getContentPane().add(rootPanel);

         if (application.getWidget().isApplet()) {


            dialog.setSize(Math.round(application.getWidget().getWidth() * 0.7f),
                     Math.round(application.getWidget().getHeight() * 0.85f));

            dialog.setLocationRelativeTo(application.getWidget());
         }
         else {
            dialog.setSize(Math.round(((Component) application).getWidth() * 0.7f),
                     Math.round(((Component) application).getHeight() * 0.85f));
            dialog.setLocationRelativeTo((Component) application);
         }


         dialog.setVisible(true);
      }
      catch (final IOException e) {
         context.getLogger().logSevere(e);
      }
   }


   private JScrollPane createHtmlPane(final IGlobeRunningContext context,
                                      final int border) throws IOException {
      final IGlobeTranslator translator = context.getTranslator();

      final JScrollPane scrollPane = new JScrollPane();
      scrollPane.setBorder(BorderFactory.createEmptyBorder(border, border, border, border));

      // hack to enable rendering of fonts with antialiasing on html rendering
      final JTextPane htmlPane = new JTextPane() {
         private static final long serialVersionUID = 1L;


         @Override
         public void paintComponent(final Graphics g) {
            final Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            super.paintComponent(g);
         }
      };
      htmlPane.setContentType("text/html");

      if (_bgColor != null) {
         htmlPane.setBackground(_bgColor);
         scrollPane.setBackground(_bgColor);
      }

      htmlPane.setEditable(false);
      htmlPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      //      textpane.setBorder(BorderFactory.createEmptyBorder());

      final String currentLanguage = translator.getCurrentLanguage();
      String urlByLanguage = null;
      for (final String[] languageAndURL : _languageHtmlURL) {
         final String language = languageAndURL[0];
         if (currentLanguage.equals(language)) {
            urlByLanguage = languageAndURL[1];
            break;
         }
      }

      if (urlByLanguage == null) {
         for (final String[] languageAndURL : _languageHtmlURL) {
            final String language = languageAndURL[0];
            if (language == null) {
               urlByLanguage = languageAndURL[1];
               break;
            }
         }
      }

      URL url;
      if (_loadFromURL) {
         url = new URL(urlByLanguage);
      }
      else {
         url = getClass().getClassLoader().getResource(urlByLanguage);
      }
      //final URL url = getClass().getClassLoader().getResource(urlByLanguage);
      //      final URL url = new URL(urlByLanguage);
      //      System.out.println(url);
      htmlPane.setPage(url);

      scrollPane.getViewport().add(htmlPane);

      // Move the focus to the html-pane
      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            scrollPane.requestFocus();
            scrollPane.requestFocusInWindow();
         }
      });

      //      final int TODO_Move_Scrollbar_to_top;
      //      scrollPane.getViewport().setViewPosition(new Point(0, 0));

      return scrollPane;
   }


   @Override
   public String getName() {
      return "Help Module";
   }


   @Override
   public List<GPair<String, Component>> getPanels(final IGlobeRunningContext context) {
      if (_showInPanel) {
         try {
            return Collections.singletonList(new GPair<String, Component>(_label, createHtmlPane(context, 0)));
         }
         catch (final IOException e) {
            context.getLogger().logSevere(e);
         }
      }

      return null;
   }


   @Override
   public String getVersion() {
      return "0.1";
   }


   @Override
   public void initialize(final IGlobeRunningContext context) {
      super.initialize(context);

      if (_autoOpen) {
         SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
               showHelp(context);
            }
         });
      }
   }


   @Override
   public void initializeTranslations(final IGlobeRunningContext context) {
      final IGlobeTranslator translator = context.getTranslator();
      translator.addTranslation("es", DEFAULT_LABEL, "Ayuda");
      translator.addTranslation("de", DEFAULT_LABEL, "Hilfe");
      translator.addTranslation("pt", DEFAULT_LABEL, "Ajuda");
   }

}
