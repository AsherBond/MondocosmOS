

package es.igosoftware.experimental.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import net.miginfocom.swing.MigLayout;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.colors.GColorF;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.experimental.vectorial.rendering.coloring.GColorBrewerColorSchemeSet;
import es.igosoftware.euclid.experimental.vectorial.rendering.coloring.GColorScheme;
import es.igosoftware.euclid.experimental.vectorial.rendering.coloring.GUniqueValuesDataSet;
import es.igosoftware.euclid.features.GField;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.globe.IGlobeBitmapFactory;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.IGlobeVector2Layer;
import es.igosoftware.globe.attributes.IEventsHandler;
import es.igosoftware.io.GFileName;
import gov.nasa.worldwind.layers.Layer;


public class GUniqueValuesPanel
         extends
            JPanel
         implements
            ActionListener,
            ChangeListener,
            ListSelectionListener {


   /**
    * 
    */
   private static final long                                                                                   serialVersionUID     = -4213389135681915663L;

   private static final Border                                                                                 commonPadding        = BorderFactory.createEmptyBorder(
                                                                                                                                             10,
                                                                                                                                             10,
                                                                                                                                             10,
                                                                                                                                             10);
   private static final Border                                                                                 mainPadding          = BorderFactory.createEmptyBorder(
                                                                                                                                             5,
                                                                                                                                             20,
                                                                                                                                             5,
                                                                                                                                             20);
   //   private static final Border                                                                                 topPadding           = BorderFactory.createEmptyBorder(
   //                                                                                                                                             2,
   //                                                                                                                                             0,
   //                                                                                                                                             0,
   //                                                                                                                                             0);
   private static final Border                                                                                 labelPadding         = BorderFactory.createEmptyBorder(
                                                                                                                                             0,
                                                                                                                                             0,
                                                                                                                                             0,
                                                                                                                                             3);
   //   private static final Border                                                                                 bottomPadding      = BorderFactory.createEmptyBorder(
   //                                                                                                                                           0,
   //                                                                                                                                           0,
   //                                                                                                                                           2,
   //                                                                                                                                           0);
   private static final Border                                                                                 componentPadding     = BorderFactory.createEmptyBorder(
                                                                                                                                             0,
                                                                                                                                             4,
                                                                                                                                             0,
                                                                                                                                             3);
   private static final String                                                                                 allDefault           = "<All default>";
   //private static final String                                                                                 noneValue          = "<None value>";
   private static final String                                                                                 fieldNameCommand     = "fieldNameCommand";
   //  private static final String                                                                                 fieldValueCommand  = "fieldValueCommand";
   private static final String                                                                                 dataTypeCommand      = "dataTypeCommand";
   private static final String                                                                                 colorSchemeCommand   = "colorSchemeCommand";
   //private static final String                                                                                 applyCommand         = "applyCommand";
   private static final String                                                                                 optionChooserCommand = "optionChooserCommand";
   private static final String                                                                                 defaultColorCommand  = "defaultColorCommand";

   private static final String                                                                                 addCommand           = "addCommand";
   private static final String                                                                                 addAllCommand        = "addAllCommand";
   private static final String                                                                                 delCommand           = "delCommand";
   private static final String                                                                                 delAllCommand        = "delAllCommand";
   private static final String                                                                                 okCommand            = "okCommand";
   private static final String                                                                                 cancelCommand        = "cancelCommand";

   private static final int                                                                                    initialValue         = 2;
   private static final int                                                                                    minValue             = 2;
   private static final int                                                                                    maxValue             = 12;
   private static final int                                                                                    stepValue            = 1;

   //private static final JLabel                                                                                 paddingLabel         = new JLabel();


   private final IGlobeRunningContext                                                                          _context;
   private final IEventsHandler                                                                                _eventHandler;
   //private final IGlobeLayer       _layer;
   private final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> _features;

   private final GColorBrewerColorSchemeSet                                                                    _colorBrewerSchemeInstance;

   private JComboBox                                                                                           _fieldNameCombo;
   //private JComboBox                                                                                           _fieldValueCombo;
   private JList                                                                                               _fieldValuesList;
   private JComboBox                                                                                           _dataTypeCombo;
   private JComboBox                                                                                           _colorSchemeCombo;
   //private JTable                                                                                              _fieldValuesTable;

   //private JButton                                                                                             _okButton;
   //private JButton                                                                                             _applyButton;
   //private JButton                                                                                             _cancelButton;
   private JButton                                                                                             _addButton;
   private JButton                                                                                             _addAllButton;
   private JButton                                                                                             _delButton;
   private JButton                                                                                             _delAllButton;
   private JButton                                                                                             _defaultColorButton;

   private JSpinner                                                                                            _classesNumberSpinner;
   //private JPanel                                                                                              _colorsPanel;
   private JPanel                                                                                              _currentColorsPanel;
   private JComboBox                                                                                           _optionChooserCombo;
   private SchemeChooserType                                                                                   _selectedChooser;
   private Color                                                                                               _defaultColor;

   private final DefaultListModel                                                                              _listModel;
   private final HashMap<String, IColor>                                                                       _colorsAssociation;


   public static enum SchemeChooserType {
      Brewer,
      Ramp,
      Others
   }


   public GUniqueValuesPanel(final IGlobeRunningContext context,
                             final Layer layer,
                             final IEventsHandler eventHandler,
                             final Color defaultColor) {


      super();

      _context = context;
      _features = ((IGlobeVector2Layer) layer).getFeaturesCollection();
      _eventHandler = eventHandler;
      _defaultColor = defaultColor;

      //_content = getContentPane();
      _colorBrewerSchemeInstance = GColorBrewerColorSchemeSet.INSTANCE;
      _listModel = new DefaultListModel();

      _selectedChooser = SchemeChooserType.Brewer;
      _colorsAssociation = new HashMap<String, IColor>();

      createPanel();

   }


   private void createPanel() {

      setLayout(new MigLayout("fillx, insets 0 0 0 0, gap 0 3"));
      setBackground(Color.WHITE);
      //paddingLabel.setBackground(Color.WHITE);

      add(createCommonPanel(), "growx, wrap");

      _currentColorsPanel = createColorsPanel();

      add(_currentColorsPanel, "growx");

      applyChanges();

   }


   private class ColorIcon
            implements
               Icon {
      private final static int defaultSize = 14;

      private final Color      _color;
      private final int        _size;


      public ColorIcon(final Color color) {
         this(color, defaultSize);
      }


      public ColorIcon(final Color color,
                       final int size) {
         _color = color;
         _size = size;
      }


      @Override
      public int getIconHeight() {
         return _size;
      }


      @Override
      public int getIconWidth() {
         return _size;
      }


      @Override
      public void paintIcon(final Component c,
                            final Graphics g,
                            final int x,
                            final int y) {
         g.setColor(_color);
         g.fillRect(x, y, _size - 1, _size - 1);

         g.setColor(Color.black);
         g.drawRect(x, y, _size - 1, _size - 1);
      }
   }


   private class CellColorRenderer
            extends
               DefaultListCellRenderer {

      private static final long serialVersionUID = 1L;


      @Override
      public Component getListCellRendererComponent(final JList list,
                                                    final Object value,
                                                    final int index,
                                                    final boolean isSelected,
                                                    final boolean hasFocus) {

         final JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);
         Color color = _defaultColor;

         if (_listModel.contains(allDefault)) {
            final Icon colorIcon = new ColorIcon(color);
            label.setIcon(colorIcon);
            return label;
         }
         //         else if (colorScheme != null) {
         //            final List<IColor> colorList = colorScheme.getColors();
         //            if (index < colorList.size()) {
         //               _colorsAssociation.put((String) value, colorList.get(index));
         //               color = colorList.get(index).asAWTColor();
         //            }
         //         }

         switch (_selectedChooser) {
            case Brewer:
               final GColorScheme colorScheme = (GColorScheme) _colorSchemeCombo.getSelectedItem();
               if (colorScheme != null) {
                  final List<IColor> colorList = colorScheme.getColors();
                  if (index < colorList.size()) {
                     _colorsAssociation.put((String) value, colorList.get(index));
                     color = colorList.get(index).asAWTColor();
                  }
               }
               break;
            case Ramp:

               break;
            case Others:

               break;
         }

         //color = _colorsAssociation.get(value).asAWTColor();
         final Icon colorIcon = new ColorIcon(color);
         label.setIcon(colorIcon);
         return label;

      }
   }


   private JPanel createCommonPanel() {

      final JPanel p = new JPanel(new MigLayout("fillx, insets 2 2 2 2, gap 0 3"));
      p.setBackground(Color.WHITE);

      final JLabel optionChooserLabel = new JLabel("Option");
      optionChooserLabel.setBorder(labelPadding);

      final SchemeChooserType[] optionChooserList = SchemeChooserType.values();
      _optionChooserCombo = new JComboBox(optionChooserList);
      _optionChooserCombo.setActionCommand(optionChooserCommand);
      _optionChooserCombo.addActionListener(this);
      optionChooserLabel.setLabelFor(_optionChooserCombo);

      final JLabel defaultColorLabel = new JLabel("Default");
      defaultColorLabel.setBorder(componentPadding);

      _defaultColorButton = new JButton("..");
      _defaultColorButton.setBackground(_defaultColor);

      _defaultColorButton.putClientProperty(SubstanceLookAndFeel.COLORIZATION_FACTOR, Double.valueOf(1));
      _defaultColorButton.putClientProperty(SubstanceLookAndFeel.BUTTON_NO_MIN_SIZE_PROPERTY, Boolean.TRUE);
      _defaultColorButton.putClientProperty(SubstanceLookAndFeel.CORNER_RADIUS, Float.valueOf(1)); // currently is ignored
      _defaultColorButton.setActionCommand(defaultColorCommand);
      _defaultColorButton.addActionListener(this);
      _defaultColorButton.setToolTipText("Default color chooser");

      p.add(optionChooserLabel, "cell 0 0");
      p.add(_optionChooserCombo, "cell 1 0, gapleft 3");
      p.add(defaultColorLabel, "cell 2 0, gapleft 5");
      p.add(_defaultColorButton, "cell 3 0, gapleft 3");
      //p.add(new JPanel(), "cell 4 0, growx");

      //------------------------------------------------------------------------
      final JLabel fieldNameLabel = new JLabel("Field");
      //fieldNameLabel.setBorder(labelPadding);
      final String[] fieldNameList = getFieldNames();
      _fieldNameCombo = new JComboBox(fieldNameList);
      _fieldNameCombo.setActionCommand(fieldNameCommand);
      _fieldNameCombo.addActionListener(this);
      fieldNameLabel.setLabelFor(_fieldNameCombo);

      p.add(fieldNameLabel, "cell 0 2");
      p.add(_fieldNameCombo, "cell 1 2, gapleft 3, span 2");

      _fieldValuesList = new JList(_listModel);
      _listModel.addElement(allDefault);
      _fieldValuesList.setVisibleRowCount(4);
      _fieldValuesList.addListSelectionListener(this);
      _fieldValuesList.setCellRenderer(new CellColorRenderer());

      final JScrollPane fieldValuesScrollPane = new JScrollPane(_fieldValuesList);

      final JPanel dataBottons = createDataBotonsPanel();

      p.add(fieldValuesScrollPane, "cell 0 3, span 3, wmax 200px, growx");
      p.add(dataBottons, "cell 3 2, spany 2");
      //------------------------------------------------------------------------

      return p;
   }


   private JPanel createRampPanel() {

      final JPanel p2Ramp = new JPanel();
      p2Ramp.setLayout(new BorderLayout());
      //p2Ramp.setAlignmentX(CENTER_ALIGNMENT);
      //p2Ramp.setBorder(topPadding);
      p2Ramp.setBackground(Color.WHITE);

      final JPanel p2 = new JPanel(new FlowLayout());
      //p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
      p2.setBackground(Color.WHITE);


      final JLabel underConstructionLabel = new JLabel("Option under construction..");
      underConstructionLabel.setForeground(Color.RED);
      p2.add(underConstructionLabel);

      p2Ramp.add(p2, BorderLayout.CENTER);

      return p2Ramp;
   }


   private JPanel createWheelPanel() {

      final JPanel p2Wheel = new JPanel();
      p2Wheel.setLayout(new BorderLayout());
      //p2Wheel.setAlignmentX(CENTER_ALIGNMENT);
      //p2Wheel.setBorder(topPadding);
      p2Wheel.setBackground(Color.WHITE);

      final JPanel p2 = new JPanel(new FlowLayout());
      //p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
      p2.setBackground(Color.WHITE);

      final JLabel underConstructionLabel = new JLabel("Option under construction..");
      underConstructionLabel.setForeground(Color.RED);
      p2.add(underConstructionLabel);

      p2Wheel.add(p2, BorderLayout.CENTER);

      return p2Wheel;
   }


   private JPanel createDataBotonsPanel() {
      final IGlobeBitmapFactory bitmapFactory = _context.getBitmapFactory();

      final JPanel p3 = new JPanel(new MigLayout("fillx, flowy, insets 0 0 0 0, gap 0 1"));
      p3.setBackground(Color.WHITE);

      final Icon addIcon = bitmapFactory.getIcon(GFileName.relative("add.png"), 12, 12);
      _addButton = new JButton(addIcon);
      _addButton.setActionCommand(addCommand);
      _addButton.addActionListener(this);
      _addButton.setBackground(getBackground());
      _addButton.setToolTipText("Add value");

      final Icon addAllIcon = bitmapFactory.getIcon(GFileName.relative("add-all.png"), 12, 12);
      _addAllButton = new JButton(addAllIcon);
      _addAllButton.setActionCommand(addAllCommand);
      _addAllButton.addActionListener(this);
      _addAllButton.setBackground(p3.getBackground());
      _addAllButton.setToolTipText("Add all values");

      final Icon delIcon = bitmapFactory.getIcon(GFileName.relative("remove2.png"), 12, 12);
      _delButton = new JButton(delIcon);
      _delButton.setActionCommand(delCommand);
      _delButton.addActionListener(this);
      _delButton.setBackground(p3.getBackground());
      _delButton.setToolTipText("Remove value");

      final Icon delAllIcon = bitmapFactory.getIcon(GFileName.relative("remove-all.png"), 12, 12);
      _delAllButton = new JButton(delAllIcon);
      _delAllButton.setActionCommand(delAllCommand);
      _delAllButton.addActionListener(this);
      _delAllButton.setBackground(p3.getBackground());
      _delAllButton.setToolTipText("Remove all values");

      p3.add(_addButton, "gapleft 10, gaptop 15");
      p3.add(_addAllButton, "gapleft 10, gaptop 2");
      p3.add(_delButton, "gapleft 10, gaptop 2");
      p3.add(_delAllButton, "gapleft 10, gaptop 2");

      return p3;
   }


   private String[] getFieldNames() {

      final List<GField> fieldList = _features.getFields();

      final String[] fieldNameList = new String[fieldList.size()];
      for (int index = 0; index < fieldList.size(); index++) {
         fieldNameList[index] = fieldList.get(index).getName();
      }

      return fieldNameList;
   }


   private String[] getFieldValues(final String field) {

      final Set<String> valuesSet = new HashSet<String>();

      for (int index = 0; index < _features.size(); index++) {
         final Object value = _features.get(index).getAttribute(field);
         if (value != null) {
            valuesSet.add(value.toString());
         }
      }

      final List<String> sortedValues = new ArrayList<String>(valuesSet);
      Collections.sort(sortedValues);
      return sortedValues.toArray(new String[] {});
      //return valuesSet.toArray(new String[] {});
   }


   private JPanel createColorsPanel() {

      JPanel p = null;

      switch (_selectedChooser) {
         case Brewer:
            p = createBrewerPanel();
            break;
         case Ramp:
            p = createRampPanel();
            break;
         case Others:
            p = createWheelPanel();
            break;
      }

      return p;

   }


   private JPanel createBrewerPanel() {

      final JPanel p2 = new JPanel(new MigLayout("fillx, insets 0 0 0 0, gap 0 2"));
      p2.setBackground(Color.WHITE);

      //-- define top first component --
      final JPanel p2a = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));

      p2a.setBackground(Color.WHITE);

      //-- spinner for number of classes in the user data
      final JLabel classesNumberLabel = new JLabel("Classes");
      classesNumberLabel.setBorder(labelPadding);
      _classesNumberSpinner = new JSpinner(new SpinnerNumberModel(initialValue, minValue, maxValue, stepValue));
      _classesNumberSpinner.addChangeListener(this);
      classesNumberLabel.setLabelFor(_classesNumberSpinner);

      p2a.add(classesNumberLabel);
      p2a.add(_classesNumberSpinner);

      //-- type of user data under color brewer categories
      final JLabel dataTypeLabel = new JLabel("Type");
      dataTypeLabel.setBorder(componentPadding);

      final GColorScheme.Type[] dataTypeList = GColorScheme.Type.values();
      _dataTypeCombo = new JComboBox(dataTypeList);
      _dataTypeCombo.setActionCommand(dataTypeCommand);
      _dataTypeCombo.addActionListener(this);
      dataTypeLabel.setLabelFor(_dataTypeCombo);

      p2a.add(dataTypeLabel);
      p2a.add(_dataTypeCombo);

      //-- new panel for color scheme selection
      final JPanel p2b = new JPanel();
      p2b.setLayout(new BoxLayout(p2b, BoxLayout.LINE_AXIS));
      //final JPanel p2b = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));
      p2b.setAlignmentX(RIGHT_ALIGNMENT);
      //p2b.setBorder(verticalPadding);
      p2b.setBackground(Color.WHITE);

      final JLabel colorSchemeLabel = new JLabel("Scheme");
      colorSchemeLabel.setBorder(labelPadding);
      final List<GColorScheme> colorSchemeList = _colorBrewerSchemeInstance.getSchemes(
               (Integer) _classesNumberSpinner.getValue(), (GColorScheme.Type) _dataTypeCombo.getSelectedItem());

      _colorSchemeCombo = new JComboBox(colorSchemeList.toArray());
      _colorSchemeCombo.setRenderer(new ColorSchemeRenderer());
      _colorSchemeCombo.setActionCommand(colorSchemeCommand);
      _colorSchemeCombo.addActionListener(this);

      colorSchemeLabel.setLabelFor(_colorSchemeCombo);

      p2b.add(colorSchemeLabel);
      p2b.add(_colorSchemeCombo);

      p2.add(p2a, "left, wrap");
      p2.add(p2b, "left, growx");

      return p2;
   }


   public class ColorSchemeRenderer
            extends
               JPanel
            implements
               ListCellRenderer {

      private static final long serialVersionUID = 1L;


      @Override
      public Component getListCellRendererComponent(final JList list,
                                                    final Object value,
                                                    final int index,
                                                    final boolean isSelected,
                                                    final boolean cellHasFocus) {

         if (value != null) {

            if (value instanceof GColorScheme) {

               final GColorScheme colorScheme = (GColorScheme) value;
               final JTable renderer = new JTable();
               renderer.setShowGrid(false);
               //renderer.setPreferredSize(new Dimension(150, 20)); 
               renderer.setPreferredSize(new Dimension(160, 18));

               class MyTableModel
                        extends
                           AbstractTableModel {

                  private static final long serialVersionUID = 1L;


                  @Override
                  public int getRowCount() {
                     return 1;
                  }


                  @Override
                  public int getColumnCount() {
                     //return columnNames.length;
                     return colorScheme.getDimensions();
                  }


                  @Override
                  public Object getValueAt(final int rowIndex,
                                           final int columnIndex) {
                     return null;
                  }

               }

               renderer.setModel(new MyTableModel());

               class MyTableCellRenderer
                        extends
                           DefaultTableCellRenderer {

                  private static final long serialVersionUID = 1L;


                  public MyTableCellRenderer(final IColor color) {
                     super.setBackground(color.asAWTColor());
                  }
               }

               int ind = 0;
               final int columnWidth = _colorSchemeCombo.getSize().width / colorScheme.getDimensions();
               for (final IColor color : colorScheme.getColors()) {
                  renderer.getTableHeader().getColumnModel().getColumn(ind).setCellRenderer(new MyTableCellRenderer(color));
                  renderer.getTableHeader().getColumnModel().getColumn(ind).setWidth(columnWidth);
                  ind++;
               }

               return renderer;
            }
         }

         return new DefaultListCellRenderer();
      }
   }


   final JDialog createAddValuesDialog(final String fieldName) {

      final JDialog valuesDialog = new JDialog(_context.getApplication().getFrame(), "Unique values selection", true);

      final JPanel p = new JPanel();
      p.setLayout(new BorderLayout());
      p.setBorder(mainPadding);

      final JLabel selectValueLabel = new JLabel("Select values for " + fieldName + " from the list ");
      selectValueLabel.setBorder(commonPadding);

      final String[] fieldValues = getFieldValues(_fieldNameCombo.getSelectedItem().toString());
      final DefaultListModel listModel = new DefaultListModel();

      if (fieldValues.length > 0) {
         for (final String value : fieldValues) {
            listModel.addElement(value);
         }
      }
      else {
         listModel.addElement(allDefault);
      }

      final JList fieldValuesList = new JList(listModel);
      fieldValuesList.setVisibleRowCount(5);
      fieldValuesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      fieldValuesList.addListSelectionListener(new ListSelectionListener() {

         @Override
         public void valueChanged(final ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
               return;
            }
         }

      });

      final JScrollPane scrollPane = new JScrollPane(fieldValuesList);

      //final JPanel pButtons = new JPanel(new BorderLayout());
      final JPanel pButtons = new JPanel(new MigLayout("fillx, insets 0 0 0 0, gap 0 3"));
      pButtons.setBorder(commonPadding);
      final JButton okButton = new JButton("OK");
      final JButton cancelButton = new JButton("Cancel");

      final ActionListener dialogActionListener = new ActionListener() {
         @Override
         public void actionPerformed(final ActionEvent e) {

            final String command = e.getActionCommand();
            if (command == okCommand) {
               final Object[] selectedValues = fieldValuesList.getSelectedValues();
               if (selectedValues.length > 0) {
                  if (_listModel.contains(allDefault)) {
                     _listModel.removeElement(allDefault);
                  }
                  for (final Object value : selectedValues) {
                     //if (!_listModel.contains(value)) {
                     //_listModel.addElement(value);
                     addValueToList(value);
                     //}
                  }
                  repaintValuesList(_listModel.size() - 1);
                  valuesDialog.dispose();
               }
            }
            else if (command == cancelCommand) {
               valuesDialog.dispose();
            }
         }
      };

      okButton.setActionCommand(okCommand);
      cancelButton.setActionCommand(cancelCommand);
      okButton.addActionListener(dialogActionListener);
      cancelButton.addActionListener(dialogActionListener);

      pButtons.add(okButton, "cell 6 0, split 2");
      pButtons.add(cancelButton, "cell 6 0");

      p.add(selectValueLabel, BorderLayout.NORTH);
      p.add(scrollPane, BorderLayout.CENTER);
      p.add(pButtons, BorderLayout.SOUTH);

      valuesDialog.add(p);

      valuesDialog.setSize(350, 220);
      valuesDialog.setLocationRelativeTo(_context.getApplication().getFrame());

      return valuesDialog;
   }


   @Override
   public void actionPerformed(final ActionEvent e) {

      final String command = e.getActionCommand();

      if (command == fieldNameCommand) {

         _listModel.removeAllElements();
         _listModel.addElement(allDefault);
         //_fieldValuesList.setModel(_listModel);
         cleanColorsAssociation();
         repaintValuesList(0);

         applyChanges();
      }
      else if (command == dataTypeCommand) {
         final List<GColorScheme> colorSchemeList = _colorBrewerSchemeInstance.getSchemes(
                  (Integer) _classesNumberSpinner.getValue(), (GColorScheme.Type) _dataTypeCombo.getSelectedItem());
         _colorSchemeCombo.removeAllItems();
         for (final GColorScheme colorScheme : colorSchemeList) {
            _colorSchemeCombo.addItem(colorScheme);
         }
         _colorSchemeCombo.paintImmediately(_colorSchemeCombo.getBounds());

         //updateColorAssociation();
         _fieldValuesList.repaint();
         applyChanges();
      }
      else if (command == optionChooserCommand) {
         final SchemeChooserType selected = (SchemeChooserType) _optionChooserCombo.getSelectedItem();
         if (selected.equals(_selectedChooser)) {
            return;
         }

         _currentColorsPanel.setVisible(false);
         remove(_currentColorsPanel);
         _selectedChooser = selected;

         switch (_selectedChooser) {
            case Brewer:
               _currentColorsPanel = createBrewerPanel();
               break;
            case Ramp:
               _currentColorsPanel = createRampPanel();
               break;
            case Others:
               _currentColorsPanel = createWheelPanel();
               break;
         }

         add(_currentColorsPanel);
         //updateColorAssociation();
         _currentColorsPanel.setVisible(true);

         applyChanges();
      }
      else if (command == defaultColorCommand) {

         final Color newColor = JColorChooser.showDialog(_context.getApplication().getFrame(), "Default color", _defaultColor);

         if (newColor != null) {
            _defaultColor = newColor;
            _defaultColorButton.setBackground(_defaultColor);
         }

         //updateColorAssociation();
         _fieldValuesList.repaint();
         applyChangesAnyway();

      }
      else if (command == colorSchemeCommand) {

         //updateColorAssociation();
         _fieldValuesList.repaint();
         applyChanges();
      }
      else if (command == addCommand) {
         final JDialog addDialog = createAddValuesDialog(_fieldNameCombo.getSelectedItem().toString());
         addDialog.setVisible(true);
         //_fieldValuesList.setSelectedIndex(_listModel.getSize() - 1);
         applyChanges();
      }
      else if (command == addAllCommand) {
         final String[] fieldValueList = getFieldValues(_fieldNameCombo.getSelectedItem().toString());
         if (fieldValueList.length == 0) {
            return;
         }
         //_listModel.removeAllElements();
         while (!_listModel.isEmpty()) {
            removeValueFromList(0);
         }
         for (final String value : fieldValueList) {
            //_listModel.addElement(value);
            addValueToList(value);
         }
         repaintValuesList(0);

         applyChanges();
      }
      else if (command == delCommand) {

         final int[] selectedIndex = _fieldValuesList.getSelectedIndices();
         if (selectedIndex.length == 0) {
            return;
         }

         //list already empty
         if (_listModel.contains(allDefault)) {
            return;
         }

         while (!_fieldValuesList.isSelectionEmpty()) {
            final int index = _fieldValuesList.getSelectedIndex();
            //_listModel.remove(index);
            removeValueFromList(index);
         }

         if (_listModel.size() == 0) {
            _listModel.addElement(allDefault);
         }
         repaintValuesList(selectedIndex[0]);

         applyChangesAnyway();

      }
      else if (command == delAllCommand) {
         //_listModel.removeAllElements();

         //list already empty
         if (_listModel.contains(allDefault)) {
            return;
         }

         while (!_listModel.isEmpty()) {
            removeValueFromList(0);
         }

         _listModel.addElement(allDefault);
         repaintValuesList(0);

         applyChangesAnyway();
      }

      //applyChanges();

   }


   private void applyChangesAnyway() {
      applyDataChanges();
   }


   private void applyChanges() {

      if (!_listModel.contains(allDefault)) {
         applyDataChanges();
      }
   }


   private void applyDataChanges() {

      final IColor defaultColor = GColorF.fromAWTColor(_defaultColor);
      GColorScheme colorScheme = null;
      HashMap<String, IColor> colorAssociation = null;
      final String fieldName = (String) _fieldNameCombo.getSelectedItem();

      updateColorsAssociation();

      switch (_selectedChooser) {
         case Brewer:
            if (_listModel.contains(allDefault)) {
               break;
            }
            colorScheme = (GColorScheme) _colorSchemeCombo.getSelectedItem();
            colorAssociation = _colorsAssociation;
            break;
         case Ramp:

            break;
         case Others:

            break;
      }

      final GUniqueValuesDataSet setValue = new GUniqueValuesDataSet(defaultColor, colorScheme, fieldName, colorAssociation);
      _eventHandler.setAction(setValue);

   }


   private void repaintValuesList(final int index) {
      _fieldValuesList.setModel(_listModel);
      _fieldValuesList.validate();
      _fieldValuesList.setSelectedIndex(index);
      _fieldValuesList.paintImmediately(_fieldValuesList.getBounds());
   }


   private void addValueToList(final Object element) {

      if (!_listModel.contains(element)) {
         _listModel.addElement(element);

         switch (_selectedChooser) {
            case Brewer:
               final List<IColor> colorList = ((GColorScheme) _colorSchemeCombo.getSelectedItem()).getColors();
               IColor color = GColorF.fromAWTColor(_defaultColor);
               final int index = _listModel.getSize() - 1;
               if (index < colorList.size()) {
                  color = colorList.get(index);
               }
               _colorsAssociation.put((String) element, color);
               break;
            case Ramp:

               break;
            case Others:

               break;
         }
      }

   }


   private void removeValueFromList(final int index) {

      switch (_selectedChooser) {
         case Brewer:
            final String element = (String) _listModel.get(index);
            _listModel.remove(index);
            _colorsAssociation.remove(element);
            break;
         case Ramp:
            _listModel.remove(index);
            break;
         case Others:
            _listModel.remove(index);
            break;
      }

   }


   private void updateColorsAssociation() {

      switch (_selectedChooser) {
         case Brewer:

            if (_listModel.contains(allDefault)) {
               break;
            }

            final GColorScheme colorScheme = (GColorScheme) _colorSchemeCombo.getSelectedItem();

            if (colorScheme == null) {
               for (int index = 0; index < _listModel.size(); index++) {
                  _colorsAssociation.put((String) _listModel.get(index), GColorF.fromAWTColor(_defaultColor));
               }
               break;
            }

            final List<IColor> colorList = colorScheme.getColors();
            for (int index = 0; index < _listModel.size(); index++) {
               final String element = (String) _listModel.get(index);
               IColor color = GColorF.fromAWTColor(_defaultColor);
               if (index < colorList.size()) {
                  color = colorList.get(index);
               }
               _colorsAssociation.put(element, color);
            }

            break;
         case Ramp:

            break;
         case Others:

            break;
      }

   }


   private void cleanColorsAssociation() {
      _colorsAssociation.clear();
   }


   // spinner changes events
   @Override
   public void stateChanged(final ChangeEvent e) {

      if (e.getSource().equals(_classesNumberSpinner)) {

         final List<GColorScheme> colorSchemeList = _colorBrewerSchemeInstance.getSchemes(
                  (Integer) _classesNumberSpinner.getValue(), (GColorScheme.Type) _dataTypeCombo.getSelectedItem());
         _colorSchemeCombo.removeAllItems();
         for (final GColorScheme colorScheme : colorSchemeList) {
            _colorSchemeCombo.addItem(colorScheme);
         }
         _colorSchemeCombo.paintImmediately(_colorSchemeCombo.getBounds());

         //updateColorAssociation();
         _fieldValuesList.repaint();

         applyChanges();
      }
   }


   // field values list selection events
   @Override
   public void valueChanged(final ListSelectionEvent e) {

      if (e.getValueIsAdjusting()) {
         return;
      }
      //System.out.println("NUMBER OF SELECTED: " + _fieldValuesList.getSelectedIndices().length);
   }


}
