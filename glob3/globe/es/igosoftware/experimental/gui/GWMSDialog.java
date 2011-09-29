

package es.igosoftware.experimental.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import es.igosoftware.experimental.wms.GImageFormat;
import es.igosoftware.experimental.wms.GWMSDefaultServers;
import es.igosoftware.experimental.wms.GWMSLayer;
import es.igosoftware.experimental.wms.GWMSLayerData;
import es.igosoftware.experimental.wms.GWMSServerData;
import es.igosoftware.experimental.wms.GWMSServersManager;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.io.GFileName;
import es.igosoftware.logging.GLogger;
import es.igosoftware.logging.ILogger;
import es.igosoftware.util.GSwingUtils;
import es.igosoftware.util.GUtils;
import gov.nasa.worldwind.ogc.wms.WMSCapabilities;


public class GWMSDialog
         extends
            JDialog
         implements
            ActionListener,
            PropertyChangeListener {

   /**
    * 
    */
   private static final long                 serialVersionUID            = 1L;

   private final static ILogger              logger                      = GLogger.instance();

   //   private final IGlobeApplication           _application;
   private final IGlobeRunningContext        _context;
   private final Container                   _content;

   private JTextField                        _layerNameText;
   private JComboBox                         _serversCombo;
   private JButton                           _defaultServersButton;
   private JButton                           _connectButton;
   private JButton                           _newButton;
   private JButton                           _editButton;
   private JButton                           _deleteButton;
   private GTablaIgoCusto                    _layersTable;
   private JButton                           _addButton;
   private JButton                           _closeButton;
   private JLabel                            _infoArea;

   private final int                         _returnValue                = -1;
   private WMSCapabilities                   _caps                       = null;
   private final Map<String, GWMSServerData> _serverMap                  = new HashMap<String, GWMSServerData>();
   private String                            _currentServer              = "";

   private Thread                            _connectWorker              = null;


   private static final String               closeButtonCommand          = "closeButton";
   private static final String               addButtonCommand            = "addButton";
   private static final String               connectButtonCommand        = "connectServerButton";
   private static final String               defaultServersButtonCommand = "defaultServersButton";
   private static final String               newServerButtonCommand      = "newServerButton";
   private static final String               editServerButtonCommand     = "editServerButton";
   private static final String               deleteServerButtonCommand   = "deleteServerButton";
   private static final String               comboBoxCommand             = "serverComboBox";

   private static final String               newServerMessage            = "New WMS server connection data";
   private static final String               editServerMessage           = "Update WMS server connection data";

   private static final Border               mainPadding                 = BorderFactory.createEmptyBorder(10, 10, 10, 10);
   private static final Border               labelPadding                = BorderFactory.createEmptyBorder(0, 0, 0, 20);
   private static final Border               verticalPadding             = BorderFactory.createEmptyBorder(0, 0, 10, 0);


   //private static final Border buttonPadding = BorderFactory.createEmptyBorder(0, 0, 0, 10);


   /**
    * @param application
    */
   //public GWMSDialog(final JFrame frame) {
   public GWMSDialog(final IGlobeRunningContext context) {
      super(context.getApplication().getFrame(), "Web Map Service", false);

      _context = context;
      _content = getContentPane();

      createWMSDialog();

   }


   public int showWMSDialog() {

      setVisible(true);
      return _returnValue;
   }


   private void createWMSDialog() {
      final JPanel p = new JPanel();
      p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
      p.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
      _content.add(p);

      //-- define top pane --
      final JPanel p1 = new JPanel();
      p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
      p1.setAlignmentX(CENTER_ALIGNMENT);
      p1.setBorder(mainPadding);

      //-- define top first panel --
      final JPanel p1a = new JPanel();
      p1a.setLayout(new BoxLayout(p1a, BoxLayout.LINE_AXIS));
      p1a.setAlignmentX(RIGHT_ALIGNMENT);
      p1a.setBorder(verticalPadding);

      final JLabel layerNameLabel = new JLabel("Layer Name:");
      layerNameLabel.setBorder(labelPadding);
      _layerNameText = new JTextField(20);
      layerNameLabel.setLabelFor(_layerNameText);

      p1a.add(layerNameLabel);
      p1a.add(_layerNameText);

      //-- define top second component --
      final JPanel p1b = new JPanel();
      p1b.setLayout(new BoxLayout(p1b, BoxLayout.LINE_AXIS));
      p1b.setAlignmentX(RIGHT_ALIGNMENT);
      p1b.setBorder(verticalPadding);

      final JLabel serverNameLabel = new JLabel("WMS Server:");
      serverNameLabel.setBorder(labelPadding);
      _serverMap.putAll(GWMSServersManager.getWMSAvailableServers());
      final String[] serversList = GWMSServersManager.getWMSServersNames(_serverMap);
      _serversCombo = new JComboBox(serversList);
      _serversCombo.setActionCommand(comboBoxCommand);
      _serversCombo.addActionListener(this);
      serverNameLabel.setLabelFor(_serversCombo);

      p1b.add(serverNameLabel);
      p1b.add(_serversCombo);

      //-- define top third component --
      final JPanel p1c = new JPanel();
      p1c.setLayout(new FlowLayout());
      p1c.setAlignmentX(RIGHT_ALIGNMENT);
      p1c.setBorder(verticalPadding);

      //-- define buttons for servers management
      _defaultServersButton = new JButton("Add default servers");
      _defaultServersButton.setActionCommand(defaultServersButtonCommand);
      _defaultServersButton.addActionListener(this);
      _connectButton = new JButton("Connect");
      _connectButton.setActionCommand(connectButtonCommand);
      _connectButton.addActionListener(this);
      _newButton = new JButton("New");
      _newButton.setActionCommand(newServerButtonCommand);
      _newButton.addActionListener(this);
      _editButton = new JButton("Edit");
      _editButton.setActionCommand(editServerButtonCommand);
      _editButton.addActionListener(this);
      _deleteButton = new JButton("Delete");
      _deleteButton.setActionCommand(deleteServerButtonCommand);
      _deleteButton.addActionListener(this);


      p1c.add(_connectButton);
      p1c.add(_newButton);
      p1c.add(_editButton);
      p1c.add(_deleteButton);
      p1c.add(_defaultServersButton);

      //-- define top last component --
      final JPanel p1d = new JPanel();
      p1d.setLayout(new BoxLayout(p1d, BoxLayout.LINE_AXIS));

      //      final GWMSLayerData prueba = new GWMSLayerData("1", "PNOITA", "Penoita bonita", "Mucho abstract eh!",
      //               GCollections.createList(new GWMSLayerStyleData("Patatera", "", "")));
      //      final List<GWMSLayerData> list = GCollections.createList(prueba);
      final List<GWMSLayerData> list = new ArrayList<GWMSLayerData>();
      final GDatosForTabla tableData = GDatosForTablaFactory.getWMSLayerData(list);
      _layersTable = new GTablaIgoCusto(tableData);
      _layersTable.getTable().getSelectionModel().addListSelectionListener(new RowListener());
      _layersTable.setShowVerticalLines(false);

      //      final SelectionListener listener = new SelectionListener();
      //      _layersTable.getTable().getSelectionModel().addListSelectionListener(listener);
      //      _layersTable.getTable().getColumnModel().getSelectionModel().addListSelectionListener(listener);

      //      final MousePressedListener mListener = new MousePressedListener();
      //      _layersTable.getTable().addMouseListener(mListener);

      p1d.add(_layersTable);

      // add components to top pane
      p1.add(p1a);
      p1.add(p1b);
      p1.add(p1c);
      p1.add(p1d);

      //-- use tabbed pane in order to be ready for adding new components
      final JTabbedPane tabbedP1 = new JTabbedPane();
      tabbedP1.addTab("Layers", null, p1, "Layers data selection");

      // define middel panel incluing save and cancel buttons
      final JPanel p2 = new JPanel();
      p2.setLayout(new BorderLayout());
      p2.setBorder(mainPadding);

      final JPanel p2a = new JPanel();
      p2a.setLayout(new FlowLayout());

      _addButton = new JButton("Add");
      _addButton.setActionCommand(addButtonCommand);
      _addButton.addActionListener(this);
      _closeButton = new JButton("Close");
      _closeButton.setActionCommand(closeButtonCommand);
      _closeButton.addActionListener(this);

      p2a.add(_addButton);
      p2a.add(_closeButton);

      p2.add(p2a, BorderLayout.EAST);

      // define bottom panel with only info area
      final JPanel p3 = new JPanel();
      p3.setLayout(new BorderLayout());

      _infoArea = new JLabel("WMS Information area", SwingConstants.LEADING);
      _infoArea.setHorizontalTextPosition(SwingConstants.LEFT);
      p3.add(_infoArea, BorderLayout.WEST);


      //-- Add to main panel
      p.add(tabbedP1);
      p.add(p2);
      p.add(p3);

      addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(final WindowEvent e) {
            GWMSServersManager.saveWMSServers(_serverMap);
         }
         //
         //         @Override
         //         public void windowIconified(final WindowEvent e) {
         //            System.out.println("Minimizing..");
         //         }
      });

      //setIconImage(GUtils.getImage("icons/earth.png"));
      final GFileName iconFileName = GFileName.relative("icons", "earth.png");
      setIconImage(GUtils.getImage(iconFileName.getName()));

      this.setSize(800, 500);
      setMaximumSize(new Dimension(900, 500));
      //setResizable(true);

      //this.setModal(false);
      setAlwaysOnTop(false);
      setModalityType(ModalityType.MODELESS);
      //this.setUndecorated(true);

      setLocationRelativeTo(_context.getApplication().getFrame());

      //this.setVisible(true);
   }


   private class RowListener
            implements
               ListSelectionListener {
      @Override
      public void valueChanged(final ListSelectionEvent event) {
         if (event.getValueIsAdjusting()) {
            return;
         }
         final int rowCount = _layersTable.getTable().getSelectedRowCount();
         _infoArea.setText(rowCount + " layers selected");
      }
   }


   @SuppressWarnings("deprecation")
   @Override
   public void actionPerformed(final ActionEvent e) {

      final String command = e.getActionCommand();

      if (command == closeButtonCommand) {
         if ((_connectWorker != null) && (_connectWorker.isAlive())) {
            _connectWorker.stop();
         }
         GWMSServersManager.saveWMSServers(_serverMap);
         dispose();
      }
      else if (command == addButtonCommand) {
         executeAddLayerCommand();
      }
      else if (command == connectButtonCommand) {
         if ((_connectWorker != null) && (_connectWorker.isAlive())) {
            _connectWorker.stop();
         }
         executeConnectServerCommand();
      }
      else if (command == defaultServersButtonCommand) {
         executeAddDefaultServersCommand();
      }
      else if (command == newServerButtonCommand) {
         final JDialog newDialog = createServerDataDialog(_content, newServerMessage, null);
         newDialog.setVisible(true);
      }
      else if (command == editServerButtonCommand) {
         final String serverName = (String) _serversCombo.getSelectedItem();
         final GWMSServerData server = _serverMap.get(serverName);
         final JDialog editDialog = createServerDataDialog(_content, editServerMessage, server);
         editDialog.setVisible(true);
      }
      else if (command == deleteServerButtonCommand) {
         executeDeleteServerCommand();
      }
      else if (command == comboBoxCommand) {
         _currentServer = (String) _serversCombo.getSelectedItem();
         _layersTable.removeAllRow();
         _infoArea.setText("Selected " + _currentServer + " server");
      }

   }


   private void executeDeleteServerCommand() {
      final int option = JOptionPane.showConfirmDialog(getContentPane(), "Are you sure you want to delete this server?",
               "Delete server", JOptionPane.YES_NO_OPTION);
      //System.out.println("OPTION: " + option);
      if (option == 0) {
         final String selectedServer = (String) _serversCombo.getSelectedItem();
         _serversCombo.removeItemAt(_serversCombo.getSelectedIndex());
         _serverMap.remove(selectedServer);
         if (selectedServer.equals(_currentServer)) {
            _layersTable.removeAllRow();
         }
         _infoArea.setText("Delete " + selectedServer + " server");
         GWMSServersManager.saveWMSServers(_serverMap);
      }
   }


   private void executeAddDefaultServersCommand() {
      final Set<GWMSServerData> defaultServers = GWMSDefaultServers.getDefaultServersSet();
      boolean modified = false;
      for (final GWMSServerData server : defaultServers) {
         if (!_serverMap.containsKey(server.getName())) {
            _serverMap.put(server.getName(), server);
            _serversCombo.addItem(server.getName());
            modified = true;
         }
      }
      if (modified) {
         _infoArea.setText("Added default WMS Servers");
         GWMSServersManager.saveWMSServers(_serverMap);
      }
   }


   private void executeConnectServerCommand() {

      _connectWorker = new Thread("connect WMS server") {
         @Override
         public void run() {
            _layersTable.removeAllRow();
            final String serverName = (String) _serversCombo.getSelectedItem();
            _infoArea.setText("Connecting to " + serverName + " server.. ");
            _infoArea.paintImmediately(_infoArea.getBounds());
            _caps = GWMSServersManager.getCapabilitiesforServer(_serverMap.get(serverName));
            if (_caps == null) {
               _infoArea.setText("Server " + serverName + " connection error");
            }
            else {
               final GWMSLayerData[] layerDataList = GWMSServersManager.getLayersForServer(_serverMap.get(serverName));
               if (layerDataList != null) {
                  _infoArea.setText("Connected to " + serverName + " server");
                  for (final GWMSLayerData layer : layerDataList) {
                     _layersTable.addRow(layer);
                  }
               }
               else {
                  _infoArea.setText("Server " + serverName + " connection error");
               }
            }
         }
      };

      _connectWorker.setDaemon(false);
      _connectWorker.setPriority(Thread.MAX_PRIORITY);
      _connectWorker.start();

      //=================================================================================

      //      _layersTable.removeAllRow();
      //      final String serverName = (String) _serversCombo.getSelectedItem();
      //      _infoArea.setText("Connecting to " + serverName + " server.. ");
      //      _infoArea.paintImmediately(_infoArea.getBounds());
      //      _caps = GWMSServersManager.getCapabilitiesforServer(_serverMap.get(serverName));
      //      if (_caps == null) {
      //         _infoArea.setText("Server " + serverName + " connection error");
      //      }
      //      else {
      //         final GWMSLayerData[] layerDataList = GWMSServersManager.getLayersForServer(_serverMap.get(serverName));
      //         if (layerDataList != null) {
      //            _infoArea.setText("Connected to " + serverName + " server");
      //            for (final GWMSLayerData layer : layerDataList) {
      //               _layersTable.addRow(layer);
      //            }
      //         }
      //         else {
      //            _infoArea.setText("Server " + serverName + " connection error");
      //         }
      //      }

   }


   private void executeAddLayerCommand() {
      //final GWMSLayerData[] layerList = (GWMSLayerData[]) _layersTable.getSelectedObjects();
      final Object[] layerList = _layersTable.getSelectedObjects();
      if (layerList.length > 0) {
         final StringBuffer layerNames = new StringBuffer();
         for (final Object objectData : layerList) {
            final GWMSLayerData layerData = (GWMSLayerData) objectData;
            layerNames.append(layerData.getName());
            layerNames.append(',');
         }
         layerNames.deleteCharAt(layerNames.lastIndexOf(","));

         final StringBuffer styleNames = new StringBuffer();
         final String[] stylesList = _layersTable.getSelectedStyles();
         for (final String style : stylesList) {
            if ((style != null) && (!style.equals(GDatosForTablaFactory.getUnselectedStyle()))) {
               styleNames.append(style);
            }
            styleNames.append(',');
         }
         styleNames.deleteCharAt(styleNames.lastIndexOf(","));

         final String globeLayerName = _layerNameText.getText();
         final GWMSLayer newLayer = new GWMSLayer(_caps, layerNames, styleNames, globeLayerName, GImageFormat.PNG);
         _context.getWorldWindModel().addLayer(newLayer);
         newLayer.doDefaultAction(_context);
         logger.logInfo("Added WMS layer !");
         _infoArea.setText("Added new WMS layer");
      }
   }


   @Override
   public void propertyChange(final PropertyChangeEvent evt) {

   }


   private JDialog createServerDataDialog(final Container cont,
                                          final String message,
                                          final GWMSServerData server) {

      final JDialog editDialog = new JDialog(_context.getApplication().getFrame(), message, true);

      final JPanel p = new JPanel();
      p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
      p.setBorder(BorderFactory.createEmptyBorder(20, 20, 5, 20));
      editDialog.add(p);

      final JPanel p0 = new JPanel();
      p0.setLayout(new BorderLayout());
      p0.setBorder(verticalPadding);
      final JLabel descriptionLabel = GSwingUtils.makeBold(new JLabel("Server connection details "));
      p0.add(descriptionLabel, BorderLayout.WEST);

      final JPanel p1 = new JPanel();
      p1.setLayout(new BorderLayout());
      p1.setBorder(verticalPadding);
      final JLabel serverNameLabel = new JLabel("Server Name");
      serverNameLabel.setBorder(labelPadding);
      final JTextField serverName = new JTextField(30);
      serverNameLabel.setLabelFor(serverName);

      p1.add(serverNameLabel, BorderLayout.WEST);
      p1.add(serverName, BorderLayout.CENTER);

      final JPanel p2 = new JPanel();
      p2.setLayout(new BorderLayout());
      final JLabel serverUrlLabel = new JLabel("Server URL");
      serverUrlLabel.setBorder(labelPadding);
      final JTextField serverUrl = new JTextField(60);
      serverUrlLabel.setLabelFor(serverUrl);

      p2.add(serverUrlLabel, BorderLayout.WEST);
      p2.add(serverUrl, BorderLayout.CENTER);

      final JPanel p3 = new JPanel();
      p3.setLayout(new BorderLayout());
      p3.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
      final JLabel advertisementLabel = new JLabel(" If WMS server require autentication, please, write user name and password ");

      p3.add(advertisementLabel, BorderLayout.WEST);

      final JPanel p4 = new JPanel();
      p4.setLayout(new BorderLayout());
      p4.setBorder(verticalPadding);

      final JPanel p41 = new JPanel();
      p41.setLayout(new BoxLayout(p41, BoxLayout.LINE_AXIS));
      p41.setAlignmentX(RIGHT_ALIGNMENT);
      p41.setBorder(verticalPadding);
      final JLabel userNameLabel = new JLabel("User Name");
      userNameLabel.setBorder(labelPadding);
      final JTextField userName = new JTextField(20);
      userNameLabel.setLabelFor(userName);

      p41.add(userNameLabel);
      p41.add(userName);

      final JPanel p42 = new JPanel();
      p42.setLayout(new BoxLayout(p42, BoxLayout.LINE_AXIS));
      p42.setAlignmentX(RIGHT_ALIGNMENT);
      p42.setBorder(verticalPadding);
      final JLabel passwordLabel = new JLabel("Password");
      passwordLabel.setBorder(labelPadding);
      final JTextField password = new JTextField(20);
      passwordLabel.setLabelFor(password);

      p42.add(passwordLabel);
      p42.add(password);

      p4.add(p41, BorderLayout.WEST);
      p4.add(p42, BorderLayout.EAST);

      final JPanel p5 = new JPanel();
      p5.setLayout(new BorderLayout());
      p5.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

      final JPanel p5a = new JPanel();
      p5a.setLayout(new FlowLayout());

      final JButton saveButton = new JButton("Save");
      saveButton.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent e) {
            if (serverName.getText().isEmpty()) {
               JOptionPane.showMessageDialog(editDialog, "Write a name for the sever !", "Save server data",
                        JOptionPane.WARNING_MESSAGE);

            }
            else if (serverUrl.getText().isEmpty()) {
               JOptionPane.showMessageDialog(editDialog, "Write a URL for the sever !", "Save server data",
                        JOptionPane.WARNING_MESSAGE);
            }
            else {

               final GWMSServerData serverData = new GWMSServerData(serverName.getText(), serverUrl.getText(),
                        userName.getText(), password.getText());

               if (message.equals(newServerMessage)) {
                  if (_serverMap.containsKey(serverName.getText())) {
                     JOptionPane.showMessageDialog(editDialog, "Server name already exists. Please, select a different name",
                              "Save server data", JOptionPane.WARNING_MESSAGE);
                     return;
                  }
                  _serverMap.put(serverData.getName(), serverData);
                  _serversCombo.addItem(serverData.getName());
                  _serversCombo.setSelectedIndex(_serversCombo.getItemCount() - 1);
                  _infoArea.setText("Added new WMS Server");

               }
               else {
                  _serverMap.remove(server.getName());
                  _serverMap.put(serverData.getName(), serverData);
                  _serversCombo.removeItemAt(_serversCombo.getSelectedIndex());
                  _serversCombo.addItem(serverData.getName());
                  _serversCombo.setSelectedIndex(_serversCombo.getItemCount() - 1);
                  _infoArea.setText("Updated " + serverData.getName() + " server");
               }

               GWMSServersManager.saveWMSServers(_serverMap);
               editDialog.dispose();
            }

         }
      });

      final JButton cancelButton = new JButton("Cancel");
      cancelButton.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(final ActionEvent e) {
            editDialog.dispose();
         }
      });

      p5a.add(cancelButton);
      p5a.add(saveButton);

      p5.add(p5a, BorderLayout.EAST);

      p.add(p0);
      p.add(p1);
      p.add(p2);
      p.add(p3);
      p.add(p4);
      p.add(p5);

      if (server != null) {
         serverName.setText(server.getName());
         serverUrl.setText(server.getURL());
      }

      editDialog.setSize(720, 280);
      editDialog.setMinimumSize(new Dimension(720, 280));
      editDialog.setMaximumSize(new Dimension(800, 280));
      editDialog.setLocationRelativeTo(cont);

      return editDialog;
   }
}
