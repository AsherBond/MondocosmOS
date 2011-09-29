

package es.unex.s3xtante.modules.sextante.bindings;

import java.awt.geom.Rectangle2D;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.media.jai.RasterFactory;

import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.globe.IGlobeRasterLayer;
import es.igosoftware.globe.layers.GESRIAsciiFileTools;
import es.igosoftware.globe.layers.GGlobeRasterLayer;
import es.igosoftware.globe.layers.GRasterGeodata;
import es.unex.s3xtante.utils.ProjectionUtils;
import es.unex.sextante.core.AnalysisExtent;
import es.unex.sextante.dataObjects.AbstractRasterLayer;
import es.unex.sextante.outputs.FileOutputChannel;
import es.unex.sextante.outputs.IOutputChannel;


public class GlobeRasterLayer
         extends
            AbstractRasterLayer {

   private static final double DEFAULT_NO_DATA_VALUE = -99999.;

   private GProjection         _projection;
   private String              _filename;
   private Raster              _raster;

   private AnalysisExtent      _layerExtent;

   private double              _noDataValue;
   private String              _name;

   private IGlobeRasterLayer   _baseDataObject;


   public void create(final String name,
                      final String filename,
                      final AnalysisExtent ae,
                      int dataType,
                      final int numBands,
                      final GProjection projection) {

      if (projection == null) {
         _projection = ProjectionUtils.getDefaultProjection();
      }
      else {
         _projection = projection;
      }

      if (dataType == DataBuffer.TYPE_DOUBLE) {
         dataType = DataBuffer.TYPE_FLOAT;
      }

      System.out.println(ae.getNX());
      System.out.println(ae.getNY());
      _raster = RasterFactory.createBandedRaster(dataType, ae.getNX(), ae.getNY(), numBands, null);

      _filename = filename;
      _name = name;
      _layerExtent = ae;
      _noDataValue = DEFAULT_NO_DATA_VALUE;

   }


   public void create(final IGlobeRasterLayer layer) {

      _baseDataObject = layer;
      _projection = layer.getRasterGeodata()._projection;
      _raster = layer.getRaster();
      final GRasterGeodata extent = layer.getRasterGeodata();
      _layerExtent = new AnalysisExtent();

      _layerExtent.setCellSize(extent._cellsize);
      _layerExtent.setXRange(extent._xllcorner, extent._xllcorner + extent._cols * extent._cellsize, true);
      _layerExtent.setYRange(extent._yllcorner, extent._yllcorner + extent._rows * extent._cellsize, true);


      _name = layer.getName();

      _noDataValue = layer.getNoDataValue();

   }


   @Override
   public int getBandsCount() {

      return _raster.getNumBands();

   }


   @Override
   public double getCellValueInLayerCoords(final int x,
                                           final int y,
                                           final int band) {

      if (_raster != null) {
         return _raster.getSampleDouble(x, y, band);
      }
      return getNoDataValue();

   }


   @Override
   public int getDataType() {

      if (_raster != null) {
         return _raster.getDataBuffer().getDataType();
      }
      return DataBuffer.TYPE_DOUBLE;

   }


   @Override
   public double getLayerCellSize() {

      if (_layerExtent != null) {
         return _layerExtent.getCellSize();
      }
      return 0;

   }


   @Override
   public AnalysisExtent getLayerGridExtent() {

      return _layerExtent;

   }


   @Override
   public double getNoDataValue() {

      return _noDataValue;

   }


   @Override
   public void setCellValue(final int x,
                            final int y,
                            final int band,
                            final double value) {

      if (isInWindow(x, y)) {
         ((WritableRaster) _raster).setSample(x, y, band, value);
      }

   }


   @Override
   public void setNoDataValue(final double noDataValue) {

      _noDataValue = noDataValue;

   }


   @Override
   public Object getCRS() {

      return _projection;

   }


   @Override
   public Rectangle2D getFullExtent() {

      return _layerExtent.getAsRectangle2D();

   }


   @Override
   public void open() {

   }


   @Override
   public void close() {

   }


   @Override
   public void postProcess() {

      try {

         // BufferedImage bi = new
         // BufferedImage(RasterRenderer.getDefaultColorModel(
         // (WritableRaster)m_Raster), (WritableRaster)m_Raster, false,
         // null);
         // Sector sector = Sector.fromDegrees(m_LayerExtent.getYMin(),
         // m_LayerExtent.getYMax(),
         // m_LayerExtent.getXMin(), m_LayerExtent.getXMax());
         // GeotiffWriter writer;
         // File file = new File(m_sFilename);
         // writer = new GeotiffWriter(file);
         // writer.write(bi);
         // TODO:Sector???????????
         final File file = new File(_filename);
         final GRasterGeodata geodata = new GRasterGeodata(_layerExtent.getXMin(), _layerExtent.getYMin(),
                  _layerExtent.getCellSize(), _layerExtent.getNY(), _layerExtent.getNX(), _projection);

         final GGlobeRasterLayer layer = new GGlobeRasterLayer(_raster, geodata);
         layer.setNoDataValue(_noDataValue);
         layer.setName(_name);
         GESRIAsciiFileTools.writeFile(layer, file);
         create(layer);

      }
      catch (final Exception e) {
         e.printStackTrace();
      }

   }


   @Override
   public IOutputChannel getOutputChannel() {

      return new FileOutputChannel(_filename);

   }


   @Override
   public String getName() {

      return _name;

   }


   @Override
   public void setName(final String sName) {

      _name = sName;
      if (_baseDataObject != null) {
         ((GGlobeRasterLayer) _baseDataObject).setName(sName);
      }

   }


   @Override
   public void free() {
   }


   @Override
   public Object getBaseDataObject() {

      return _baseDataObject;

   }


}
