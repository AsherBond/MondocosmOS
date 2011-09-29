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


package es.igosoftware.statistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import es.igosoftware.logging.GLogger;
import es.igosoftware.logging.ILogger;
import es.igosoftware.util.GArrayListInt;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GMath;
import es.igosoftware.util.GProgress;
import es.igosoftware.util.GStringUtils;
import es.igosoftware.util.GUnmodifiableListInt;
import es.igosoftware.util.IComparatorInt;
import es.igosoftware.util.IEvaluator;
import es.igosoftware.util.IEvaluatorInt;
import es.igosoftware.util.IListInt;
import es.igosoftware.util.IRangeEvaluator;


public class GKMeansPlusPlusClusterer {

   private static final ILogger logger = GLogger.instance();


   public static interface Policy<PointT, CentroidT> {

      public double pointQuantity(final PointT point);


      public double centroidQuantity(final CentroidT center);


      public CentroidT centroidOf(final List<PointT> points);


      public double distance(final PointT point,
                             final CentroidT center);

   }


   public static interface PolicyInt<CentroidT> {

      public double pointQuantity(final int point);


      public double centroidQuantity(final CentroidT center);


      public CentroidT centroidOf(final IListInt points);


      public double distance(final int point,
                             final CentroidT center);

   }


   private static class GOperations<PointT, CentroidT> {

      final GKMeansPlusPlusClusterer.Policy<PointT, CentroidT> _policy;


      private GOperations(final GKMeansPlusPlusClusterer.Policy<PointT, CentroidT> policy) {
         _policy = policy;
      }


      private boolean closeTo(final CentroidT center0,
                              final CentroidT center1) {

         return GMath.closeTo(_policy.centroidQuantity(center0), _policy.centroidQuantity(center1));
      }


      private int compareCentroids(final CentroidT center0,
                                   final CentroidT center1) {

         return Double.compare(_policy.centroidQuantity(center0), _policy.centroidQuantity(center1));
      }


      private int comparePoints(final PointT point0,
                                final PointT point1) {

         return Double.compare(_policy.pointQuantity(point0), _policy.pointQuantity(point1));

      }


      private CentroidT centroidOf(final List<PointT> points) {

         return _policy.centroidOf(points);
      }


      private double distance(final PointT point,
                              final CentroidT center) {

         return _policy.distance(point, center);
      }

   }


   private static class GOperationsInt<CentroidT> {

      final GKMeansPlusPlusClusterer.PolicyInt<CentroidT> _policy;


      private GOperationsInt(final PolicyInt<CentroidT> policy) {
         _policy = policy;
      }


      private boolean closeTo(final CentroidT center0,
                              final CentroidT center1) {

         return GMath.closeTo(_policy.centroidQuantity(center0), _policy.centroidQuantity(center1));
      }


      private int compareCentroids(final CentroidT center0,
                                   final CentroidT center1) {

         return Double.compare(_policy.centroidQuantity(center0), _policy.centroidQuantity(center1));
      }


      private int comparePoints(final int point0,
                                final int point1) {

         return Double.compare(_policy.pointQuantity(point0), _policy.pointQuantity(point1));
      }


      private CentroidT centroidOf(final IListInt points) {

         return _policy.centroidOf(points);
      }


      private double distance(final int point,
                              final CentroidT center) {

         return _policy.distance(point, center);
      }

   }


   public static class GCluster<PointT, CentroidT> {
      /** Center of the cluster. */
      private final CentroidT    _center;

      /** The points contained in this cluster. */
      private final List<PointT> _points = new ArrayList<PointT>();


      /**
       * Build a cluster centered at a specified point.
       * 
       * @param _center
       *           the point which is to be the _center of this cluster
       */
      private GCluster(final CentroidT center) {
         _center = center;
      }


      /**
       * Add a point to this cluster.
       * 
       * @param point
       *           point to add
       */
      private void addPoint(final PointT point) {
         _points.add(point);
      }


      /**
       * Get the points contained in the cluster.
       * 
       * @return _points contained in the cluster
       */
      public List<PointT> getPoints() {
         return Collections.unmodifiableList(_points);
      }


      /**
       * Get the point chosen to be the center of this cluster.
       * 
       * @return chosen cluster center
       */
      public CentroidT getCenter() {
         return _center;
      }


      @Override
      public String toString() {
         return "  GCluster [center=" + _center.toString() + ", points=" + _points.size() + "]";
      }
   }


   public static class GClusterInt<CentroidT> {
      /** Center of the cluster. */
      private final CentroidT _center;

      /** The points contained in this cluster. */
      private final IListInt  _points = new GArrayListInt();


      /**
       * Build a cluster centered at a specified point.
       * 
       * @param _center
       *           the point which is to be the _center of this cluster
       */
      private GClusterInt(final CentroidT center) {
         _center = center;
      }


      /**
       * Add a point to this cluster.
       * 
       * @param point
       *           point to add
       */
      private void addPoint(final int point) {
         _points.add(point);
      }


      /**
       * Get the points contained in the cluster.
       * 
       * @return _points contained in the cluster
       */
      public IListInt getPoints() {
         return new GUnmodifiableListInt(_points);
      }


      /**
       * Get the point chosen to be the center of this cluster.
       * 
       * @return chosen cluster center
       */
      public CentroidT getCenter() {
         return _center;
      }


      @Override
      public String toString() {
         return "  GCluster [center=" + _center + ", points=" + _points.size() + "]";
      }
   }


   private GKMeansPlusPlusClusterer() {
   }


   /**
    * Runs the K-means++ clustering algorithm.
    * 
    * @param _points
    *           the points to cluster
    * @param k
    *           the number of clusters to split the data into
    * @param maxIterations
    *           the maximum number of iterations to run the algorithm for. If negative, no maximum will be used
    * @return a list of clusters containing the points
    */
   public static <PointT, CentroidT> Collection<GKMeansPlusPlusClusterer.GCluster<PointT, CentroidT>> cluster(final GKMeansPlusPlusClusterer.Policy<PointT, CentroidT> policy,
                                                                                                              final List<PointT> points,
                                                                                                              final int k,
                                                                                                              final int maxIterations) {
      if (points.size() < k) {
         throw new RuntimeException("Insufficient points (" + points.size() + ") to create " + k + " clusters");
      }

      final long start = System.currentTimeMillis();

      final GOperations<PointT, CentroidT> operations = new GOperations<PointT, CentroidT>(policy);

      final int max = (maxIterations < 0) ? Integer.MAX_VALUE : maxIterations;

      final GProgress progress = new GProgress(max) {
         @Override
         public void informProgress(final long stepsDone,
                                    final double percent,
                                    final long elapsed,
                                    final long estimatedMsToFinish) {
            logger.logInfo("  K-Means++: " + progressString(stepsDone, percent, elapsed, estimatedMsToFinish));
         }
      };

      // create the initial clusters
      List<GKMeansPlusPlusClusterer.GCluster<PointT, CentroidT>> clusters = chooseInitialClusters(operations, points, k);
      assignPointsToClusters(operations, clusters, points);

      // iterate through updating the centers until we're done
      for (int count = 0; count < max; count++) {
         final AtomicBoolean clusteringChanged = new AtomicBoolean(false);
         final List<GKMeansPlusPlusClusterer.GCluster<PointT, CentroidT>> newClusters = new ArrayList<GKMeansPlusPlusClusterer.GCluster<PointT, CentroidT>>(
                  k);

         GCollections.concurrentEvaluate(clusters, new IEvaluator<GKMeansPlusPlusClusterer.GCluster<PointT, CentroidT>>() {
            @Override
            public void evaluate(final GCluster<PointT, CentroidT> cluster) {
               //               final List<PointT> clusterPoints = cluster.getPoints();
               //               if (clusterPoints.isEmpty()) {
               //                  throw new RuntimeException("Empty cluster points");
               //               }
               final CentroidT newCenter = operations.centroidOf(cluster.getPoints());
               //if (!newCenter.equals(cluster.getCenter())) {
               if (!operations.closeTo(newCenter, cluster.getCenter())) {
                  clusteringChanged.set(true);
               }
               synchronized (newClusters) {
                  newClusters.add(new GKMeansPlusPlusClusterer.GCluster<PointT, CentroidT>(newCenter));
               }
            }
         });

         if (!clusteringChanged.get()) {

            progress.stepsDone(max - count);

            Collections.sort(clusters, new Comparator<GKMeansPlusPlusClusterer.GCluster<PointT, CentroidT>>() {
               @Override
               public int compare(final GCluster<PointT, CentroidT> o1,
                                  final GCluster<PointT, CentroidT> o2) {
                  return operations.compareCentroids(o1._center, o2._center);
               }
            });

            final long ellapsed = System.currentTimeMillis() - start;
            logger.logInfo("  K-Means++: Converged after " + (count + 1) + " (max: " + max + ") iterations in "
                           + GStringUtils.getTimeMessage(ellapsed));

            showStatistics(clusters, policy);

            return clusters;
         }

         assignPointsToClusters(operations, newClusters, points);
         clusters = newClusters;

         progress.stepDone();
      }

      Collections.sort(clusters, new Comparator<GKMeansPlusPlusClusterer.GCluster<PointT, CentroidT>>() {
         @Override
         public int compare(final GCluster<PointT, CentroidT> o1,
                            final GCluster<PointT, CentroidT> o2) {
            return operations.compareCentroids(o1._center, o2._center);
         }
      });

      final long ellapsed = System.currentTimeMillis() - start;
      logger.logInfo("  K-Means++: Converged after max (" + max + ") iterations in " + GStringUtils.getTimeMessage(ellapsed));

      showStatistics(clusters, policy);

      return clusters;
   }


   /**
    * Use K-means++ to choose the initial centers.
    * 
    * @param <PointT>
    *           type of the points to cluster
    * @param _points
    *           the points to choose the initial centers from
    * @param k
    *           the number of centers to choose
    * @param random
    *           random generator to use
    * @return the initial centers
    */
   private static <PointT, CentroidT> List<GKMeansPlusPlusClusterer.GCluster<PointT, CentroidT>> chooseInitialClusters(final GOperations<PointT, CentroidT> operations,
                                                                                                                       final List<PointT> points,
                                                                                                                       final int k) {

      //      logger.info("K-Means++: Sorting points...");
      final ArrayList<PointT> sortedPoints = new ArrayList<PointT>(points);
      Collections.sort(sortedPoints, new Comparator<PointT>() {
         @Override
         public int compare(final PointT value0,
                            final PointT value1) {
            return operations.comparePoints(value0, value1);
         }
      });
      //      logger.info("K-Means++: Points sorted!");

      final List<GKMeansPlusPlusClusterer.GCluster<PointT, CentroidT>> result = new ArrayList<GKMeansPlusPlusClusterer.GCluster<PointT, CentroidT>>(
               k);
      GCollections.concurrentEvaluate(sortedPoints, new IRangeEvaluator() {
         @Override
         public void evaluate(final int from,
                              final int to) {
            //            final List<PointT> rangePoints = points.subList(from, to + 1);
            //            if (rangePoints.isEmpty()) {
            //               throw new RuntimeException("Empty points");
            //            }
            final CentroidT centroid = operations.centroidOf(sortedPoints.subList(from, to + 1));
            final GCluster<PointT, CentroidT> cluster = new GKMeansPlusPlusClusterer.GCluster<PointT, CentroidT>(centroid);

            synchronized (result) {
               result.add(cluster);
            }

         }
      }, k /* use k as number of threads, to split the input in K groups */);

      return result;
   }


   /**
    * Use K-means++ to choose the initial centers.
    * 
    * @param <PointT>
    *           type of the points to cluster
    * @param _points
    *           the points to choose the initial centers from
    * @param k
    *           the number of centers to choose
    * @param random
    *           random generator to use
    * @return the initial centers
    */
   private static <CentroidT> List<GKMeansPlusPlusClusterer.GClusterInt<CentroidT>> chooseInitialClusters(final GOperationsInt<CentroidT> operations,
                                                                                                          final IListInt points,
                                                                                                          final int k) {

      //      logger.info("K-Means++: Sorting points...");
      final IListInt sortedPoints = new GArrayListInt(points);
      GCollections.quickSort(sortedPoints, new IComparatorInt() {
         @Override
         public int compare(final int value0,
                            final int value1) {
            return operations.comparePoints(value0, value1);
         }
      });


      //      logger.info("K-Means++: Points sorted!");

      final List<GKMeansPlusPlusClusterer.GClusterInt<CentroidT>> result = new ArrayList<GKMeansPlusPlusClusterer.GClusterInt<CentroidT>>(
               k);
      GCollections.concurrentEvaluate(sortedPoints, new IRangeEvaluator() {
         @Override
         public void evaluate(final int from,
                              final int to) {

            //            final IListInt rangePoints = points.subList(from, to + 1);
            //            if (rangePoints.isEmpty()) {
            //               throw new RuntimeException("Empty points");
            //            }
            final CentroidT centroid = operations.centroidOf(sortedPoints.subList(from, to + 1));
            final GClusterInt<CentroidT> cluster = new GKMeansPlusPlusClusterer.GClusterInt<CentroidT>(centroid);

            synchronized (result) {
               result.add(cluster);
            }

         }
      }, k /* use k as number of threads, to split the input in K groups */);

      return result;
   }


   /**
    * Adds the given points to the closest {@link Cluster}.
    * 
    * @param <PointT>
    *           type of the points to cluster
    * @param clusters
    *           the {@link Cluster}s to add the points to
    * @param points
    *           the points to add to the given {@link Cluster}s
    * @param policy
    */
   private static <PointT, CentroidT> void assignPointsToClusters(final GOperations<PointT, CentroidT> operations,
                                                                  final Collection<GKMeansPlusPlusClusterer.GCluster<PointT, CentroidT>> clusters,
                                                                  final List<PointT> points) {
      GCollections.concurrentEvaluate(points, new IEvaluator<PointT>() {
         @Override
         public void evaluate(final PointT point) {
            final GKMeansPlusPlusClusterer.GCluster<PointT, CentroidT> cluster = getNearestCluster(operations, clusters, point);
            synchronized (cluster) {
               cluster.addPoint(point);
            }
         }
      });

      //      for (final GCluster<PointT, CentroidT> cluster : clusters) {
      //         if (cluster.getPoints().isEmpty()) {
      //            throw new RuntimeException("Invalid cluster: " + cluster);
      //         }
      //      }
   }


   private static <CentroidT> void assignPointsToClusters(final GOperationsInt<CentroidT> operations,
                                                          final Collection<GKMeansPlusPlusClusterer.GClusterInt<CentroidT>> clusters,
                                                          final IListInt points) {
      GCollections.concurrentEvaluate(points, new IEvaluatorInt() {
         @Override
         public void evaluate(final int point) {
            final GKMeansPlusPlusClusterer.GClusterInt<CentroidT> cluster = getNearestCluster(operations, clusters, point);
            synchronized (cluster) {
               cluster.addPoint(point);
            }
         }
      });

      //      for (final GClusterInt<CentroidT> cluster : clusters) {
      //         if (cluster.getPoints().isEmpty()) {
      //            throw new RuntimeException("Invalid cluster: " + cluster);
      //         }
      //      }
   }


   /**
    * Returns the nearest {@link Cluster} to the given point
    * 
    * @param <PointT>
    *           type of the points to cluster
    * @param clusters
    *           the {@link Cluster}s to search
    * @param point
    *           the point to find the nearest {@link Cluster} for
    * @return the nearest {@link Cluster} to the given point
    */
   private static <PointT, CentroidT> GKMeansPlusPlusClusterer.GCluster<PointT, CentroidT> getNearestCluster(final GOperations<PointT, CentroidT> operations,
                                                                                                             final Collection<GKMeansPlusPlusClusterer.GCluster<PointT, CentroidT>> clusters,
                                                                                                             final PointT point) {
      double minDistance = Double.POSITIVE_INFINITY;
      GKMeansPlusPlusClusterer.GCluster<PointT, CentroidT> nearestCluster = null;
      for (final GKMeansPlusPlusClusterer.GCluster<PointT, CentroidT> cluster : clusters) {

         final double currentDistance = operations.distance(point, cluster.getCenter());
         if (currentDistance < minDistance) {
            minDistance = currentDistance;
            nearestCluster = cluster;
         }
      }

      //      if (nearestCluster == null) {
      //         for (final GKMeansPlusPlusClusterer.GCluster<PointT, CentroidT> cluster : clusters) {
      //            final double currentDistance = policy.distance(point, cluster.getCenter());
      //            System.out.println("distance=" + currentDistance + ", point=" + point + ", center=" + cluster.getCenter());
      //         }
      //      }

      return nearestCluster;
   }


   private static <CentroidT> GKMeansPlusPlusClusterer.GClusterInt<CentroidT> getNearestCluster(final GOperationsInt<CentroidT> operations,
                                                                                                final Collection<GKMeansPlusPlusClusterer.GClusterInt<CentroidT>> clusters,
                                                                                                final int point) {
      double minDistance = Double.POSITIVE_INFINITY;
      GKMeansPlusPlusClusterer.GClusterInt<CentroidT> nearestCluster = null;
      for (final GKMeansPlusPlusClusterer.GClusterInt<CentroidT> cluster : clusters) {
         final double currentDistance = operations.distance(point, cluster.getCenter());
         if (currentDistance < minDistance) {
            minDistance = currentDistance;
            nearestCluster = cluster;
         }
      }

      //      if (nearestCluster == null) {
      //         for (final GClusterInt<CentroidT> cluster : clusters) {
      //            final double currentDistance = policy.distance(point, cluster.getCenter());
      //            System.out.println("distance=" + currentDistance + ", point=" + point + ", center=" + cluster.getCenter());
      //         }
      //      }

      return nearestCluster;
   }


   /**
    * Runs the K-means++ clustering algorithm.
    * 
    * @param _points
    *           the points to cluster
    * @param k
    *           the number of clusters to split the data into
    * @param maxIterations
    *           the maximum number of iterations to run the algorithm for. If negative, no maximum will be used
    * @return a list of clusters containing the points
    */
   public static <CentroidT> Collection<GKMeansPlusPlusClusterer.GClusterInt<CentroidT>> cluster(final GKMeansPlusPlusClusterer.PolicyInt<CentroidT> policy,
                                                                                                 final IListInt points,
                                                                                                 final int k,
                                                                                                 final int maxIterations) {
      if (points.size() < k) {
         throw new RuntimeException("Insufficient points (" + points.size() + ") to create " + k + " clusters");
      }

      final long start = System.currentTimeMillis();

      final GOperationsInt<CentroidT> operations = new GOperationsInt<CentroidT>(policy);

      final int max = (maxIterations < 0) ? Integer.MAX_VALUE : maxIterations;

      final GProgress progress = new GProgress(max) {
         @Override
         public void informProgress(final long stepsDone,
                                    final double percent,
                                    final long elapsed,
                                    final long estimatedMsToFinish) {
            logger.logInfo("  K-Means++: " + progressString(stepsDone, percent, elapsed, estimatedMsToFinish));
         }
      };

      // create the initial clusters
      List<GKMeansPlusPlusClusterer.GClusterInt<CentroidT>> clusters = chooseInitialClusters(operations, points, k);
      assignPointsToClusters(operations, clusters, points);

      // iterate through updating the centers until we're done
      for (int count = 0; count < max; count++) {
         final AtomicBoolean clusteringChanged = new AtomicBoolean(false);
         final List<GKMeansPlusPlusClusterer.GClusterInt<CentroidT>> newClusters = new ArrayList<GKMeansPlusPlusClusterer.GClusterInt<CentroidT>>(
                  k);

         GCollections.concurrentEvaluate(clusters, new IEvaluator<GKMeansPlusPlusClusterer.GClusterInt<CentroidT>>() {
            @Override
            public void evaluate(final GClusterInt<CentroidT> cluster) {
               final CentroidT newCenter = operations.centroidOf(cluster.getPoints());
               //if (!newCenter.equals(cluster.getCenter())) {
               if (!operations.closeTo(newCenter, cluster.getCenter())) {
                  clusteringChanged.set(true);
               }
               synchronized (newClusters) {
                  newClusters.add(new GKMeansPlusPlusClusterer.GClusterInt<CentroidT>(newCenter));
               }
            }
         });

         if (!clusteringChanged.get()) {

            progress.stepsDone(max - count);

            Collections.sort(clusters, new Comparator<GKMeansPlusPlusClusterer.GClusterInt<CentroidT>>() {
               @Override
               public int compare(final GClusterInt<CentroidT> o1,
                                  final GClusterInt<CentroidT> o2) {
                  return operations.compareCentroids(o1._center, o2._center);
               }
            });

            final long ellapsed = System.currentTimeMillis() - start;
            logger.logInfo("  K-Means++: Converged after " + (count + 1) + " (max: " + max + ") iterations in "
                           + GStringUtils.getTimeMessage(ellapsed));

            showStatisticsInt(clusters, policy);

            return clusters;
         }

         assignPointsToClusters(operations, newClusters, points);
         clusters = newClusters;

         progress.stepDone();
      }

      Collections.sort(clusters, new Comparator<GKMeansPlusPlusClusterer.GClusterInt<CentroidT>>() {
         @Override
         public int compare(final GClusterInt<CentroidT> o1,
                            final GClusterInt<CentroidT> o2) {
            return operations.compareCentroids(o1._center, o2._center);
         }
      });

      final long ellapsed = System.currentTimeMillis() - start;
      logger.logInfo("  K-Means++: Converged after max (" + max + ") iterations in " + GStringUtils.getTimeMessage(ellapsed));

      showStatisticsInt(clusters, policy);

      return clusters;
   }


   private static <PointT, CentroidT> void showStatistics(final Collection<GKMeansPlusPlusClusterer.GCluster<PointT, CentroidT>> clusters,
                                                          final Policy<PointT, CentroidT> policy) {

      final GStatisticsUnit clusterStatUnit = new GStatisticsUnit("Clustering statistics ");
      for (int i = 1; i <= clusters.size(); i++) {
         clusterStatUnit.addVariable(new GStatisticsVariableD("Cluster-" + Integer.toString(i),
                  GStatisticsVariableAbstract.MAX | GStatisticsVariableAbstract.MIN | GStatisticsVariableAbstract.AVERAGE
                           | GStatisticsVariableAbstract.STANDARD_DEVIATION));

      }

      int clusterIndex = 1;
      for (final GKMeansPlusPlusClusterer.GCluster<PointT, CentroidT> cluster : clusters) {
         logger.logInfo(cluster.toString());
         final List<PointT> pointList = cluster.getPoints();
         for (int i = 0; i < pointList.size(); i++) {
            final PointT point = pointList.get(i);
            clusterStatUnit.sample("Cluster-" + Integer.toString(clusterIndex), policy.pointQuantity(point));
         }
         clusterIndex++;
      }
      clusterStatUnit.show();
   }


   private static <CentroidT> void showStatisticsInt(final Collection<GKMeansPlusPlusClusterer.GClusterInt<CentroidT>> clusters,
                                                     final PolicyInt<CentroidT> policy) {

      final GStatisticsUnit clusterStatUnit = new GStatisticsUnit("Clustering statistics ");
      for (int i = 1; i <= clusters.size(); i++) {
         clusterStatUnit.addVariable(new GStatisticsVariableD("Cluster-" + Integer.toString(i),
                  GStatisticsVariableAbstract.MAX | GStatisticsVariableAbstract.MIN | GStatisticsVariableAbstract.AVERAGE
                           | GStatisticsVariableAbstract.STANDARD_DEVIATION));

      }

      int clusterIndex = 1;
      for (final GKMeansPlusPlusClusterer.GClusterInt<CentroidT> cluster : clusters) {
         logger.logInfo(cluster.toString());
         final IListInt pointList = cluster.getPoints();
         for (int i = 0; i < pointList.size(); i++) {
            final int point = pointList.get(i);
            clusterStatUnit.sample("Cluster-" + Integer.toString(clusterIndex), policy.pointQuantity(point));
         }
         clusterIndex++;
      }
      clusterStatUnit.show();
   }


   public static void main(final String[] args) {
      System.out.println("GKMeansPlusPlusClusterer 0.1");
      System.out.println("----------------------------\n");


      final GKMeansPlusPlusClusterer.Policy<Double, Double> policy = new GKMeansPlusPlusClusterer.Policy<Double, Double>() {

         @Override
         public double centroidQuantity(final Double center) {

            return center.doubleValue();
         }


         @Override
         public double pointQuantity(final Double point) {

            return point.doubleValue();
         }


         @Override
         public Double centroidOf(final List<Double> points) {
            double sum = 0;

            for (final Double point : points) {
               sum += point.doubleValue();
            }

            return sum / points.size();
         }


         @Override
         public double distance(final Double value0,
                                final Double value1) {
            return Math.abs(value0 - value1);
         }

      };


      final Random random = new Random();
      //      final Collection<Double> points = Arrays.asList(10.0, 10.0, 20.0, 50.0, 50.0, 0.0, 1.0, 2.0, 1000.0, 100.0, 500.0);
      final int pointsCount = 1000000;
      final List<Double> points = new ArrayList<Double>(pointsCount);
      for (int i = 0; i < pointsCount; i++) {
         //points.add(Double.valueOf(i));
         points.add(Double.valueOf(random.nextDouble()));
         //         points.add(Double.valueOf(random.nextDouble() * i));
      }

      final int k = 5;
      final int maxIterations = 500;

      //      Utils.delay(30000);

      createClusters(policy, points, k, maxIterations);
      //      createClusters(policy, points, k, maxIterations);
   }


   private static void createClusters(final GKMeansPlusPlusClusterer.Policy<Double, Double> policy,
                                      final List<Double> points,
                                      final int k,
                                      final int maxIterations) {
      final Collection<GKMeansPlusPlusClusterer.GCluster<Double, Double>> clusters = GKMeansPlusPlusClusterer.cluster(policy,
               points, k, maxIterations);

      System.out.println();
      for (final GKMeansPlusPlusClusterer.GCluster<Double, Double> cluster : clusters) {
         System.out.println(cluster);
      }
   }


}
