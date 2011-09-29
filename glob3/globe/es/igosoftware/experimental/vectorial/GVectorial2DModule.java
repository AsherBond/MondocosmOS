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


package es.igosoftware.experimental.vectorial;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import es.igosoftware.euclid.IBoundedGeometry2D;
import es.igosoftware.euclid.bounding.IFinite2DBounds;
import es.igosoftware.euclid.features.IGlobeFeatureCollection;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.globe.GAbstractGlobeModule;
import es.igosoftware.globe.GLayerInfo;
import es.igosoftware.globe.IGlobeRunningContext;
import es.igosoftware.globe.ILayerFactoryModule;
import es.igosoftware.globe.ILayerInfo;
import es.igosoftware.io.GFileName;
import es.igosoftware.io.GGenericFileFilter;
import es.igosoftware.io.GIOUtils;


public class GVectorial2DModule
         extends
            GAbstractGlobeModule
         implements
            ILayerFactoryModule {


   private final boolean _verbose;


   public GVectorial2DModule(final IGlobeRunningContext context,
                             final boolean verbose) {
      super(context);
      _verbose = verbose;
   }


   @Override
   public String getName() {
      return "Polygons 2D Module";
   }


   @Override
   public String getVersion() {
      return "experimental";
   }


   @Override
   public String getDescription() {
      return "Module for handling of vectorial layers";
   }


   @Override
   public List<? extends ILayerInfo> getAvailableLayers(final IGlobeRunningContext context) {
      return GLayerInfo.createFromNames(context.getBitmapFactory().getSmallIcon(GFileName.relative("vectorial.png")),
               "Vectorial file...");
   }


   @Override
   public GVectorial2DLayer addNewLayer(final IGlobeRunningContext context,
                                        final ILayerInfo layerInfo) {

      final JFileChooser fileChooser = createFileChooser(context);

      final int returnVal = fileChooser.showOpenDialog(context.getApplication().getFrame());
      if (returnVal == JFileChooser.APPROVE_OPTION) {
         final File selectedFile = fileChooser.getSelectedFile();
         if (selectedFile != null) {
            GIOUtils.setCurrentDirectory(selectedFile.getParentFile());
            openFile(selectedFile, context);
         }
      }

      return null;
   }


   private void openFile(final File file,
                         final IGlobeRunningContext context) {
      final Thread worker = new Thread("Vectorial layer loader") {

         @Override
         public void run() {
            // TODO: read projection or ask user
            final GProjection projection = GProjection.EPSG_4326;

            try {
               final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features = GShapeLoader.readFeatures(
                        file, projection);

               final GVectorial2DLayer layer = new GVectorial2DLayer(file.getName(), features, _verbose);
               //               layer.setShowExtents(true);
               context.getWorldWindModel().addLayer(layer);

               layer.doDefaultAction(context);
            }
            catch (final IOException e) {
               SwingUtilities.invokeLater(new Runnable() {
                  @Override
                  public void run() {
                     JOptionPane.showMessageDialog(context.getApplication().getFrame(), "Error opening " + file.getAbsolutePath()
                                                                                        + "\n\n " + e.getLocalizedMessage(),
                              "Error", JOptionPane.ERROR_MESSAGE);
                  }
               });
            }
         }
      };

      worker.setDaemon(true);
      worker.setPriority(Thread.MIN_PRIORITY);
      worker.start();
   }


   private JFileChooser createFileChooser(final IGlobeRunningContext context) {

      final File currentDirectory = GIOUtils.getCurrentDirectory();

      final JFileChooser fileChooser = new JFileChooser(currentDirectory) {
         private static final long serialVersionUID = 1L;


         @Override
         public String getTypeDescription(final File f) {
            final String extension = GIOUtils.getExtension(f);

            if ((extension != null) && extension.toLowerCase().equals("shp")) {
               return "SHP File";
            }

            return super.getDescription(f);
         }


         @Override
         public Icon getIcon(final File f) {
            if (f.isDirectory()) {
               return super.getIcon(f);
            }

            final String extension = GIOUtils.getExtension(f);

            if ((extension != null) && extension.toLowerCase().equals("shp")) {
               return context.getBitmapFactory().getSmallIcon(GFileName.relative("vectorial.png"));
            }

            return super.getIcon(f);
         }
      };

      fileChooser.setMultiSelectionEnabled(false);
      fileChooser.setAcceptAllFileFilterUsed(false);
      fileChooser.setFileFilter(new GGenericFileFilter("shp", "SHP files (*.shp)"));
      //      fileChooser.setSelectedFile(new File(new File(fileName).getName()));

      return fileChooser;
   }


   //   private static String getLayerName(final IGlobeApplication application) {
   //
   //      final String answer = (String) JOptionPane.showInputDialog(application.getFrame(),
   //               application.getTranslation("Enter the name for the new layer"), application.getTranslation("Vectorial Layer"),
   //               JOptionPane.PLAIN_MESSAGE, application.getSmallIcon(GFileName.relative("new-vectorial.png")), null,
   //               application.getTranslation(DEFAULT_LAYER_NAME));
   //
   //      if ((answer != null) && !answer.trim().isEmpty()) {
   //         return answer.trim();
   //      }
   //
   //      return application.getTranslation(DEFAULT_LAYER_NAME);
   //   }


   //   private void stopEditionOfLayer(final IGlobeVector2Layer layer) {
   //      System.out.println("Stopping edition of: " + layer);
   //   }
   //
   //
   //   private void startEditionOfLayer(final IGlobeVector2Layer layer) {
   //      System.out.println("Starting edition of: " + layer);
   //
   //      //      final IGlobeFeatureCollection<IVector2, ? extends IBoundedGeometry2D<? extends IFinite2DBounds<?>>> features = layer.getFeaturesCollection();
   //
   //      //      final int ______Diego_at_work;
   //   }


}
