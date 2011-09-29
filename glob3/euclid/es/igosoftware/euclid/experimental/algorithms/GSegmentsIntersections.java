

package es.igosoftware.euclid.experimental.algorithms;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;

import es.igosoftware.euclid.IGeometry2D;
import es.igosoftware.euclid.bounding.GAxisAlignedRectangle;
import es.igosoftware.euclid.shape.GSegment2D;
import es.igosoftware.euclid.utils.GGeometry2DRenderer;
import es.igosoftware.euclid.vector.GVector2D;
import es.igosoftware.euclid.vector.GVector2I;
import es.igosoftware.euclid.vector.IVector2;
import es.igosoftware.euclid.vector.IVectorI2;
import es.igosoftware.util.GStringUtils;


public class GSegmentsIntersections {

   private GSegmentsIntersections() {

   }


   private static IVector2 getSegmentSegmentIntersection(final GSegment2D segment1,
                                                         final GSegment2D segment2) {

      final IVector2 begin = segment1._from;
      final IVector2 end = segment1._to;
      final IVector2 anotherBegin = segment2._from;
      final IVector2 anotherEnd = segment2._to;

      final double denominator = ((anotherEnd.y() - anotherBegin.y()) * (end.x() - begin.x()))
                                 - ((anotherEnd.x() - anotherBegin.x()) * (end.y() - begin.y()));

      final double numeratorA = ((anotherEnd.x() - anotherBegin.x()) * (begin.y() - anotherBegin.y()))
                                - ((anotherEnd.y() - anotherBegin.y()) * (begin.x() - anotherBegin.x()));

      final double numeratorB = ((end.x() - begin.x()) * (begin.y() - anotherBegin.y()))
                                - ((end.y() - begin.y()) * (begin.x() - anotherBegin.x()));

      if (denominator == 0.0) {
         if ((numeratorA == 0.0) && (numeratorB == 0.0)) {
            return null;
         }

         return null;
      }

      final double ua = numeratorA / denominator;
      final double ub = numeratorB / denominator;

      if ((ua >= 0.0) && (ua <= 1.0) && (ub >= 0.0) && (ub <= 1.0)) {
         // Get the intersection point.
         final double x = begin.x() + ua * (end.x() - begin.x());
         final double y = begin.y() + ua * (end.y() - begin.y());

         return new GVector2D(x, y);
      }

      return null;
   }


   //   private static IVector2 getSegmentSegmentIntersection2(final GSegment2D segment1,
   //                                                          final GSegment2D segment2) {
   //
   //      final IVector2 begin = segment1._from;
   //      final IVector2 end = segment1._to;
   //      final IVector2 anotherBegin = segment2._from;
   //      final IVector2 anotherEnd = segment2._to;
   //
   //      final double denominator = ((anotherEnd.y() - anotherBegin.y()) * (end.x() - begin.x()))
   //                                 - ((anotherEnd.x() - anotherBegin.x()) * (end.y() - begin.y()));
   //
   //      final double numeratorA = ((anotherEnd.x() - anotherBegin.x()) * (begin.y() - anotherBegin.y()))
   //                                - ((anotherEnd.y() - anotherBegin.y()) * (begin.x() - anotherBegin.x()));
   //
   //      final double numeratorB = ((end.x() - begin.x()) * (begin.y() - anotherBegin.y()))
   //                                - ((end.y() - begin.y()) * (begin.x() - anotherBegin.x()));
   //
   //      if (GMath.closeToZero(denominator)) {
   //
   //         return null;
   //      }
   //
   //      final double ua = numeratorA / denominator;
   //      final double ub = numeratorB / denominator;
   //      //      final double ua = GMath.clamp(numeratorA / denominator, 0, 1);
   //      //      final double ub = GMath.clamp(numeratorB / denominator, 0, 1);
   //
   //      final double precision = GMath.maxD(segment1.precision(), segment2.precision());
   //      if (GMath.between(ua, 0, 1, precision) && GMath.between(ub, 0, 1, precision)) {
   //         // Get the intersection point.
   //         final double x = begin.x() + ua * (end.x() - begin.x());
   //         final double y = begin.y() + ua * (end.y() - begin.y());
   //
   //         return new GVector2D(x, y);
   //      }
   //
   //      return null;
   //   }


   public static Map<IVector2, Set<GSegment2D>> getIntersections(final List<GSegment2D> segments) {
      System.out.println("- finding intersections...");
      final long start = System.currentTimeMillis();

      final Map<IVector2, Set<GSegment2D>> result = getIntersectionsBruteForce(segments);

      System.out.println("- found " + result.size() + " intersections, in " + segments.size() + " segments, in "
                         + GStringUtils.getTimeMessage(System.currentTimeMillis() - start, false));

      return result;
   }


   private static Map<IVector2, Set<GSegment2D>> getIntersectionsBruteForce(final List<GSegment2D> segments) {
      final Map<IVector2, Set<GSegment2D>> result = new HashMap<IVector2, Set<GSegment2D>>();

      final int segmentsCount = segments.size();

      for (int i = 0; i < segmentsCount; i++) {
         final GSegment2D segmentI = segments.get(i);

         for (int j = i + 1; j < segmentsCount; j++) {
            final GSegment2D segmentJ = segments.get(j);

            final IVector2 intersectionPoint = getSegmentSegmentIntersection(segmentI, segmentJ);
            //final IVector2 intersectionPoint = getSegmentSegmentIntersection2(segmentI, segmentJ);
            if (intersectionPoint != null) {
               Set<GSegment2D> intersections = result.get(intersectionPoint);
               if (intersections == null) {
                  intersections = new HashSet<GSegment2D>();
                  result.put(intersectionPoint, intersections);
               }
               intersections.add(segmentI);
               intersections.add(segmentJ);
            }
         }
      }

      return result;
   }


   //   private static Map<IVector2, Set<GSegment2D>> getIntersectionsBruteForce(final List<GSegment2D> segments) {
   //      final Map<IVector2, Set<GSegment2D>> result = new HashMap<IVector2, Set<GSegment2D>>();
   //
   //      final int segmentsCount = segments.size();
   //
   //      for (int i = 0; i < segmentsCount; i++) {
   //         final GSegment2D segmentI = segments.get(i);
   //
   //         for (int j = i + 1; j < segmentsCount; j++) {
   //            final GSegment2D segmentJ = segments.get(j);
   //
   //            //final IVector2 intersectionPoint = getSegmentSegmentIntersection(segmentI, segmentJ);
   //            //final IVector2 intersectionPoint = getSegmentSegmentIntersection2(segmentI, segmentJ);
   //
   //            final GLineIntersectionResult<IVector2> intersectionResult = segmentI.getIntersection(segmentJ);
   //            if (intersectionResult != null) {
   //               if (intersectionResult.getType() == GLineIntersectionResult.Type.INTERSECTING) {
   //                  final IVector2 intersectionPoint = intersectionResult.getPoint();
   //
   //                  if (intersectionPoint != null) {
   //                     System.out.println("INTERSECTAN: " + intersectionPoint.toString());
   //                     Set<GSegment2D> intersections = result.get(intersectionPoint);
   //                     if (intersections == null) {
   //                        intersections = new HashSet<GSegment2D>();
   //                        result.put(intersectionPoint, intersections);
   //                     }
   //                     intersections.add(segmentI);
   //                     intersections.add(segmentJ);
   //                  }
   //               }
   //               else if (intersectionResult.getType() == GLineIntersectionResult.Type.COINCIDENT) {
   //                  final GSegment2D intersectionSegment = (GSegment2D) intersectionResult.getSegment();
   //                  System.out.println("COINCIDENTES: " + intersectionSegment.toString());
   //               }
   //               else if (intersectionResult.getType() == GLineIntersectionResult.Type.PARALLEL) {
   //                  System.out.println("POS SON PARALELOS !");
   //               }
   //               else if (intersectionResult.getType() == GLineIntersectionResult.Type.NOT_INTERSECTING) {
   //                  System.out.println("POS NO INTERSECTAN");
   //               }
   //            }
   //            else {
   //               System.out.println("Not Intersecting !");
   //            }
   //
   //         }
   //      }
   //
   //      return result;
   //   }


   private static void drawResults(final IVectorI2 extent,
                                   final List<GSegment2D> segments,
                                   final Map<IVector2, Set<GSegment2D>> intersections) throws IOException {
      final Collection<IGeometry2D> allGeometries = new ArrayList<IGeometry2D>(segments.size() + intersections.size());
      allGeometries.addAll(segments);
      allGeometries.addAll(intersections.keySet());

      // release some memory
      segments.clear();
      intersections.clear();

      System.out.println("- drawing...");

      final GGeometry2DRenderer renderer = new GGeometry2DRenderer(new GAxisAlignedRectangle(GVector2D.ZERO, new GVector2D(
               extent.x(), extent.y())), extent);

      renderer.drawGeometries(allGeometries, true);

      ImageIO.write(renderer.getImage(), "png", new File("/home/fpulido/Escritorio/GSegmentsIntersections.png"));
   }


   private static List<GSegment2D> createRandomSegments(final Random random,
                                                        final IVectorI2 extent,
                                                        final int segmentsCount) {
      System.out.println("- creating " + segmentsCount + " random segments...");

      final List<GSegment2D> segments = new ArrayList<GSegment2D>();

      for (int i = 0; i < segmentsCount; i++) {
         final GVector2D randomFrom = new GVector2D(random.nextDouble() * extent.x(), random.nextDouble() * extent.y());
         final GVector2D randomTo = new GVector2D(random.nextDouble() * extent.x(), random.nextDouble() * extent.y());

         final GSegment2D randomSegment = new GSegment2D(randomFrom, randomTo);
         segments.add(randomSegment);
      }

      return segments;
   }


   //   private static List<GSegment2D> createTestSegments(final IVectorI2 extent) {
   //
   //      System.out.println("- creating test segments...");
   //
   //      final List<GSegment2D> segments = new ArrayList<GSegment2D>();
   //
   //      final GVector2D aFrom = new GVector2D(0.01 * extent.x(), 0.1 * extent.y());
   //      final GVector2D aTo = new GVector2D(0.01 * extent.x(), 0.6 * extent.y());
   //      final GVector2D bFrom = new GVector2D(0.01 * extent.x(), 0.5 * extent.y());
   //      final GVector2D bTo = new GVector2D(0.01 * extent.x(), 0.9 * extent.y());
   //      final GVector2D cFrom = new GVector2D(0.2 * extent.x(), 0.01 * extent.y());
   //      final GVector2D cTo = new GVector2D(0.7 * extent.x(), 0.01 * extent.y());
   //      final GVector2D dFrom = new GVector2D(0.7 * extent.x(), 0.01 * extent.y());
   //      final GVector2D dTo = new GVector2D(0.9 * extent.x(), 0.01 * extent.y());
   //
   //      final GVector2D eFrom = new GVector2D(0.3 * extent.x(), 0.3 * extent.y());
   //      final GVector2D eTo = new GVector2D(0.8 * extent.x(), 0.3 * extent.y());
   //      final GVector2D fFrom = new GVector2D(0.5 * extent.x(), 0.2 * extent.y());
   //      final GVector2D fTo = new GVector2D(0.5 * extent.x(), 0.6 * extent.y());
   //      //      final GVector2D gFrom = new GVector2D(2 * extent.x(), 0 * extent.y());
   //      //      final GVector2D gTo = new GVector2D(7 * extent.x(), 0 * extent.y());
   //      //      final GVector2D hFrom = new GVector2D(7 * extent.x(), 0 * extent.y());
   //      //      final GVector2D hTo = new GVector2D(11 * extent.x(), 0 * extent.y());
   //
   //      final GSegment2D segmentA = new GSegment2D(aFrom, aTo);
   //      final GSegment2D segmentB = new GSegment2D(bFrom, bTo);
   //      final GSegment2D segmentC = new GSegment2D(cFrom, cTo);
   //      final GSegment2D segmentD = new GSegment2D(dFrom, dTo);
   //
   //      final GSegment2D segmentE = new GSegment2D(eFrom, eTo);
   //      final GSegment2D segmentF = new GSegment2D(fFrom, fTo);
   //
   //      segments.add(segmentA);
   //      segments.add(segmentB);
   //      segments.add(segmentC);
   //      segments.add(segmentD);
   //
   //      segments.add(segmentE);
   //      segments.add(segmentF);
   //
   //      return segments;
   //   }


   public static void main(final String[] args) throws IOException {
      System.out.println("GSegmentsIntersections 0.1");
      System.out.println("--------------------------\n");


      final boolean drawResults = true;
      final int segmentsCount = 200;
      final IVectorI2 extent = new GVector2I(1024 * 2, 768 * 2);


      final Random random = new Random(0);
      final List<GSegment2D> segments = createRandomSegments(random, extent, segmentsCount);
      //final List<GSegment2D> segments = createTestSegments(extent);

      final Map<IVector2, Set<GSegment2D>> intersections = GSegmentsIntersections.getIntersections(segments);

      System.out.println("Number of intersections: " + intersections.size());

      if (drawResults) {
         drawResults(extent, segments, intersections);
      }

      System.out.println("- done!");
   }


}
