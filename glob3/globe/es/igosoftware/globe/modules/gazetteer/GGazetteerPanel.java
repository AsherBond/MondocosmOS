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


package es.igosoftware.globe.modules.gazetteer;

import gov.nasa.worldwind.View;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.exception.NoItemException;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.poi.BasicPointOfInterest;
import gov.nasa.worldwind.poi.Gazetteer;
import gov.nasa.worldwind.poi.PointOfInterest;
import gov.nasa.worldwind.poi.YahooGazetteer;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstants;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;


public class GGazetteerPanel
         extends
            JPanel {
   private static final long serialVersionUID = 1L;

   private final WorldWindow _wwd;
   private final Gazetteer   _gazeteer;
   private JList             _jList;
   private JButton           _jButtonSearch;
   private JTextField        _jField;
   private JScrollPane       _jScrollPane;


   public GGazetteerPanel(final WorldWindow wwd,
                          final String gazetteerClassName) throws IllegalAccessException, InstantiationException,
            ClassNotFoundException {
      super(new BorderLayout());

      if (gazetteerClassName != null) {
         _gazeteer = constructGazetteer(gazetteerClassName);
      }
      else {
         _gazeteer = new YahooGazetteer();
      }

      _wwd = wwd;

      final TableLayout thisLayout = new TableLayout(new double[][] {
                        {
                                          7.0,
                                          TableLayoutConstants.FILL,
                                          TableLayoutConstants.FILL,
                                          TableLayoutConstants.FILL,
                                          7.0
                        },
                        {
                                          7.0,
                                          TableLayoutConstants.MINIMUM,
                                          TableLayoutConstants.FILL,
                                          6.0
                        }
      });
      thisLayout.setHGap(5);
      thisLayout.setVGap(5);
      setLayout(thisLayout);

      {
         _jButtonSearch = new JButton();
         _jButtonSearch.setText("Search");
         _jButtonSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent evt) {
               search();
            }
         });
         this.add(_jButtonSearch, "3, 1");

         _jField = new JTextField("Name or Lat,Lon?");
         this.add(_jField, "1, 1, 2, 1");

         final ListModel jListModel = new DefaultComboBoxModel();
         _jList = new JList();
         _jList.setModel(jListModel);
         _jList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
               if (e.getClickCount() == 2) {
                  final int iIndex = _jList.locationToIndex(e.getPoint());
                  final ListModel dlm = _jList.getModel();
                  final Object item = dlm.getElementAt(iIndex);
                  _jList.ensureIndexIsVisible(iIndex);
                  final PointOfInterest poi = (PointOfInterest) item;
                  moveToLocation(poi);
               }
            }

         });
         _jScrollPane = new JScrollPane();
         _jScrollPane.setViewportView(_jList);
         this.add(_jScrollPane, "1, 2, 3, 2");

         _jField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
               search();
            }
         });
      }
   }


   protected void search() {

      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            try {
               handleEntryAction();
            }
            catch (final NoItemException e) {
               JOptionPane.showMessageDialog(GGazetteerPanel.this, "Location not available \""
                                                                   + (_jField.getText() != null ? _jField.getText() : "")
                                                                   + "\"\n" + "(" + e.getMessage() + ")",
                        "Location Not Available", JOptionPane.ERROR_MESSAGE);
            }
            catch (final IllegalArgumentException e) {
               JOptionPane.showMessageDialog(GGazetteerPanel.this, "Error parsing input \""
                                                                   + (_jField.getText() != null ? _jField.getText() : "")
                                                                   + "\"\n" + e.getMessage(), "Lookup Failure",
                        JOptionPane.ERROR_MESSAGE);
            }
            catch (final Exception e) {
               e.printStackTrace();
               JOptionPane.showMessageDialog(GGazetteerPanel.this,
                        "Error looking up \"" + (_jField.getText() != null ? _jField.getText() : "") + "\"\n" + e.getMessage(),
                        "Lookup Failure", JOptionPane.ERROR_MESSAGE);
            }
         }
      });

   }


   private Gazetteer constructGazetteer(final String className) throws ClassNotFoundException, IllegalAccessException,
                                                               InstantiationException {
      if ((className == null) || (className.length() == 0)) {
         throw new IllegalArgumentException("Gazetteer class _name is null");
      }

      final Class<?> c = Class.forName(className.trim());
      final Object o = c.newInstance();

      if (!(o instanceof Gazetteer)) {
         throw new IllegalArgumentException("Gazetteer class _name is null");
      }

      return (Gazetteer) o;
   }


   private void handleEntryAction() throws NoItemException, IllegalArgumentException {

      final DefaultComboBoxModel model = (DefaultComboBoxModel) _jList.getModel();
      model.removeAllElements();

      final String lookupString = _jField.getText();

      if ((lookupString == null) || (lookupString.length() < 1)) {
         return;
      }

      final List<PointOfInterest> poi = parseSearchValues(lookupString);

      if (poi != null) {
         if (poi.size() == 1) {
            this.moveToLocation(poi.get(0));
         }
         else {
            for (final PointOfInterest p : poi) {
               model.addElement(p);
            }
         }
      }
   }


   /*
   Sample imputs
   Coordinate formats:
   39.53, -119.816  (Reno, NV)
   21 10 14 N, 86 51 0 W (Cancun)
   -31� 59' 43", 115� 45' 32" (Perth)
    */
   private List<PointOfInterest> parseSearchValues(String searchStr) {
      final String sepRegex = "[,]"; //other seperators??
      searchStr = searchStr.trim();
      final String[] searchValues = searchStr.split(sepRegex);
      if (searchValues.length == 1) {
         return queryService(searchValues[0].trim());
      }
      else if (searchValues.length == 2) //possible coordinates
      {
         //any numbers at all?
         final String regex = "[0-9]";
         final Pattern pattern = Pattern.compile(regex);
         final Matcher matcher = pattern.matcher(searchValues[1]); //Street Address may have numbers in first field so use 2nd
         if (matcher.find()) {
            final List<PointOfInterest> list = new ArrayList<PointOfInterest>();
            list.add(parseCoordinates(searchValues));
            return list;
         }
         return queryService(searchValues[0].trim() + "+" + searchValues[1].trim());
      }
      else {
         //build search string and send to service
         final StringBuilder sb = new StringBuilder();
         for (int i = 0; i < searchValues.length; i++) {
            sb.append(searchValues[i].trim());
            if (i < searchValues.length - 1) {
               sb.append("+");
            }

         }

         return queryService(sb.toString());
      }
   }


   private List<PointOfInterest> queryService(final String queryString) {
      final List<PointOfInterest> results = _gazeteer.findPlaces(queryString);
      if ((results == null) || (results.size() == 0)) {
         return null;
      }
      return results;
   }


   //throws IllegalArgumentException
   private PointOfInterest parseCoordinates(final String coords[]) {
      if (isDecimalDegrees(coords)) {
         final Double d1 = Double.parseDouble(coords[0].trim());
         final Double d2 = Double.parseDouble(coords[1].trim());

         return new BasicPointOfInterest(LatLon.fromDegrees(d1, d2));
      }

      final Angle aLat = Angle.fromDMS(coords[0].trim());
      final Angle aLon = Angle.fromDMS(coords[1].trim());

      return new BasicPointOfInterest(LatLon.fromDegrees(aLat.getDegrees(), aLon.getDegrees()));
   }


   private boolean isDecimalDegrees(final String[] coords) {
      try {
         Double.parseDouble(coords[0].trim());
         Double.parseDouble(coords[1].trim());
      }
      catch (final NumberFormatException nfe) {
         return false;
      }

      return true;
   }


   public void moveToLocation(final PointOfInterest location) {
      // Use a PanToIterator to iterate view to target _position
      _wwd.getView().goTo(new Position(location.getLatlon(), 0), 25e3);
   }


   public void moveToLocation(final Sector sector,
                              Double altitude) {

      if ((altitude == null) || (altitude == 0)) {
         final double t = sector.getDeltaLonRadians() > sector.getDeltaLonRadians() ? sector.getDeltaLonRadians()
                                                                                   : sector.getDeltaLonRadians();
         final double w = 0.5 * t * 6378137.0;
         altitude = w / _wwd.getView().getFieldOfView().tanHalfAngle();
      }

      final View view = _wwd.getView();
      final Globe globe = _wwd.getModel().getGlobe();
      if ((globe != null) && (view != null)) {
         view.goTo(new Position(sector.getCentroid(), 0), altitude);
      }
   }


}
