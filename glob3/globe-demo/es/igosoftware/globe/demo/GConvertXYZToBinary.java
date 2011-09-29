

package es.igosoftware.globe.demo;

import java.io.IOException;

import es.igosoftware.euclid.GAngle;
import es.igosoftware.euclid.colors.GColorPrecision;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.euclid.loading.GBinaryPoints3Loader;
import es.igosoftware.euclid.loading.GPointsLoader;
import es.igosoftware.euclid.loading.GXYZLoader;
import es.igosoftware.euclid.matrix.GMatrix44D;
import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.GVectorPrecision;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.verticescontainer.GVertex3Container;
import es.igosoftware.euclid.verticescontainer.IUnstructuredVertexContainer;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;
import es.igosoftware.experimental.pointscloud.GPointsCloudLODGenerator;
import es.igosoftware.io.GFileName;


public class GConvertXYZToBinary {


   public static void main(final String[] args) throws IOException {
      System.out.println("LOD Generator 0.1");
      System.out.println("-----------------\n");


      final GProjection projection = GProjection.EPSG_23029;
      final boolean forceConvertion = false;
      final boolean saveColors = false;
      final IVector3 translation = new GVector3D(Math.toRadians(-15.5232), Math.toRadians(28.1186), 261).reproject(
               GProjection.EPSG_4326, projection);
      final GFileName inputFileNamePTX = GFileName.absolute("home", "dgd", "Desktop", "igo.asc");
      final GFileName inputFileName = GFileName.absolute("home", "dgd", "Desktop", "iglesia.bp");
      final GFileName outputDirectoryName = GFileName.absolute("home", "dgd", "Desktop", "LOD",
               "Parroquia de San Juan Bautista de Aruca");
      final double heading = 0;
      final boolean intensitiesFromColor = true;


      if (forceConvertion) {
         convertXYZ(inputFileNamePTX, projection, translation, heading, inputFileName, saveColors, intensitiesFromColor);
      }

      generateLOD(inputFileName, outputDirectoryName);
   }


   private static void generateLOD(final GFileName inputFileName,
                                   final GFileName outputDirectoryName) throws IOException {
      final double maxLeafSideLength = Double.POSITIVE_INFINITY;
      final int maxLeafVertices = 1024 * 32;

      final GPointsCloudLODGenerator lodGenerator = new GPointsCloudLODGenerator(inputFileName, maxLeafSideLength,
               maxLeafVertices, outputDirectoryName);
      lodGenerator.process();
   }


   private static void convertXYZ(final GFileName inputFileName,
                                  final GProjection projection,
                                  final IVector3 translation,
                                  final double headingInDegrees,
                                  final GFileName outputFileName,
                                  final boolean saveColors,
                                  final boolean intensitiesFromColor) throws IOException {

      final GXYZLoader loader = new GXYZLoader(GVectorPrecision.DOUBLE, GColorPrecision.INT, projection, intensitiesFromColor,
               GPointsLoader.DEFAULT_FLAGS | GPointsLoader.VERBOSE, inputFileName);
      // final GXYZLoader loader = new GXYZLoader(GVectorPrecision.FLOAT, GColorPrecision.INT, projection, intensitiesFromColor,
      //          GXYZLoader.DEFAULT_FLAGS | GXYZLoader.VERBOSE, inputFileName);


      loader.load();


      final IVector3 referencePoint;
      if (translation == null) {
         referencePoint = loader.getVertices().getAverage()._point;
      }
      else {
         referencePoint = translation.add(loader.getVertices().getAverage()._point);
      }

      final IUnstructuredVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> originalVertices = loader.getVertices();

      final IUnstructuredVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> transformedVertices = new GVertex3Container(
               GVectorPrecision.FLOAT, originalVertices.colorPrecision(), projection, referencePoint, originalVertices.size(),
               originalVertices.hasIntensities(), saveColors && originalVertices.hasColors(), originalVertices.hasNormals());

      final GMatrix44D matrix;
      if (headingInDegrees == 0) {
         matrix = null;
      }
      else {
         matrix = GMatrix44D.createRotationMatrix(new GVector3D(0, 1, 0), GAngle.fromDegrees(headingInDegrees));
      }


      for (int i = 0; i < originalVertices.size(); i++) {
         final IVector3 originalVertex = originalVertices.getPoint(i);

         final IVector3 rotatedVertex;
         if (matrix == null) {
            rotatedVertex = originalVertex;
         }
         else {
            rotatedVertex = originalVertex.transformedBy(matrix);
         }

         final IVector3 point;
         if (translation == null) {
            point = rotatedVertex;
         }
         else {
            point = translation.add(rotatedVertex);
         }

         final IColor color = originalVertices.getColor(i);
         final float intensity = originalVertices.getIntensity(i);
         final IVector3 normal = originalVertices.getNormal(i);

         transformedVertices.addPoint(point, intensity, normal, color);
      }

      GBinaryPoints3Loader.save(transformedVertices, projection, outputFileName, true);
   }


}
