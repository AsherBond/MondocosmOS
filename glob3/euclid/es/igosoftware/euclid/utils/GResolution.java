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


package es.igosoftware.euclid.utils;

import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.IVector3;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;
import es.igosoftware.logging.GLoggerObject;
import es.igosoftware.util.GCollections;
import es.igosoftware.util.GMath;
import es.igosoftware.util.IComparatorDouble;


public final class GResolution
         extends
            GLoggerObject {


   private final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> _vertices;
   private final boolean                                                          _verbose;


   private GVector3D                                                              _maxCoordenates;
   private GVector3D                                                              _minCoordenates;
   private GVector3D                                                              _maxDeltaResolutions;
   private GVector3D                                                              _minDeltaResolutions;
   private GVector3D                                                              _averageResolutions;
   private GVector3D                                                              _standardDeviations;


   public GResolution(final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices,
                      final boolean verbose) {
      super();
      _vertices = vertices;
      _verbose = verbose;

      calculateResolutions(_vertices);
   }


   private static class StatisticsData {

      double _maxCoordenate;
      double _minCoordenate;
      double _maxDelta;
      double _minDelta;
      double _average;
      double _standardDeviation;


      public StatisticsData(final double maxCoordenate,
                            final double minCoordenate,
                            final double maxDelta,
                            final double minDelta,
                            final double average,
                            final double standardDeviation) {
         super();
         _maxCoordenate = maxCoordenate;
         _minCoordenate = minCoordenate;
         _maxDelta = maxDelta;
         _minDelta = minDelta;
         _average = average;
         _standardDeviation = standardDeviation;
      }


      private double getMaxCoordenate() {
         return _maxCoordenate;
      }


      private double getMinCoordenate() {
         return _minCoordenate;
      }


      private double getMaxDelta() {
         return _maxDelta;
      }


      private double getMinDelta() {
         return _minDelta;
      }


      private double getAverage() {
         return _average;
      }


      private double getStandardDeviation() {
         return _standardDeviation;
      }

   }


   @Override
   public boolean logVerbose() {
      return _verbose;
   }


   private void calculateResolutions(final IVertexContainer<IVector3, IVertexContainer.Vertex<IVector3>, ?> vertices) {

      final long start = System.currentTimeMillis();

      final double[] xs = new double[vertices.size()];
      final double[] ys = new double[vertices.size()];
      final double[] zs = new double[vertices.size()];

      for (int i = 0; i < vertices.size(); i++) {
         final IVector3 point = vertices.getPoint(i);
         xs[i] = point.x();
         ys[i] = point.y();
         zs[i] = point.z();
      }

      final StatisticsData statsX = getStatistics(xs);
      final StatisticsData statsY = getStatistics(ys);
      final StatisticsData statsZ = getStatistics(zs);

      _maxCoordenates = new GVector3D(statsX.getMaxCoordenate(), statsY.getMaxCoordenate(), statsZ.getMaxCoordenate());
      _minCoordenates = new GVector3D(statsX.getMinCoordenate(), statsY.getMinCoordenate(), statsZ.getMinCoordenate());
      _maxDeltaResolutions = new GVector3D(statsX.getMaxDelta(), statsY.getMaxDelta(), statsZ.getMaxDelta());
      _minDeltaResolutions = new GVector3D(statsX.getMinDelta(), statsY.getMinDelta(), statsZ.getMinDelta());
      _averageResolutions = new GVector3D(statsX.getAverage(), statsY.getAverage(), statsZ.getAverage());
      _standardDeviations = new GVector3D(statsX.getStandardDeviation(), statsY.getStandardDeviation(),
               statsZ.getStandardDeviation());

      if (logVerbose()) {
         logInfo("-----------------------------------------------------------------------------------------");
         logInfo("RESOLUTION STATISTICS:");
         logInfo(toString());
         final long elapsed = System.currentTimeMillis() - start;
         logInfo("Processed in " + GMath.roundTo(elapsed / 1000f, 1) + " secs");
         logInfo("-----------------------------------------------------------------------------------------");
      }

   }


   private StatisticsData getStatistics(final double[] numbers) {


      GCollections.quickSort(numbers, new IComparatorDouble() {

         @Override
         public int compare(final double d1,
                            final double d2) {

            if (d1 < d2) {
               return -1;
            }
            else if (d1 > d2) {
               return 1;
            }
            return 0;
         }

      });

      double deltaSum = 0.0;
      int deltaCounter = 0;

      double previous = numbers[0];
      // first loop only to compute the average, necessary for typical deviation computation
      for (int i = 1; i < numbers.length; i++) {
         final double current = numbers[i];
         final double delta = current - previous;
         if (delta > Double.MIN_VALUE) {
            deltaSum += delta;
            deltaCounter++;
         }
         previous = current;
      }

      final double average = deltaSum / deltaCounter;

      // Initialize again for second loop
      previous = numbers[0];

      double maxDelta = 0.0;
      double minDelta = Double.MAX_VALUE;
      double maxCoordenate = previous;
      double minCoordenate = previous;
      double sumatorio = 0.0;
      // second loop to compute the other statistics
      for (int i = 1; i < numbers.length; i++) {
         final double current = numbers[i];
         final double delta = current - previous;

         if (current > maxCoordenate) {
            maxCoordenate = current;
         }
         if (current < minCoordenate) {
            minCoordenate = current;
         }

         if (delta > maxDelta) {
            maxDelta = delta;
         }
         if (delta < minDelta) {
            minDelta = delta;
         }

         final double difference = (delta - average);
         sumatorio += (difference * difference);

         previous = current;
      }

      final double standardDeviation = GMath.sqrt(sumatorio / numbers.length);

      return new StatisticsData(maxCoordenate, minCoordenate, maxDelta, minDelta, average, standardDeviation);

   }


   public GVector3D getMaxCoordenates() {
      return _maxCoordenates;
   }


   public GVector3D getMinCoordenates() {
      return _minCoordenates;
   }


   public GVector3D getMaxDeltaResolutions() {
      return _maxDeltaResolutions;
   }


   public GVector3D getMinDeltaResolutions() {
      return _minDeltaResolutions;
   }


   public GVector3D getAverageResolutions() {
      return _averageResolutions;
   }


   public GVector3D getStandardDeviations() {
      return _standardDeviations;
   }


   @Override
   public String toString() {
      return "GResolution [AverageResolutions=" + _averageResolutions + ", StandardDeviations=" + _standardDeviations + "\n"
             + ", MaxCoordenates=" + _maxCoordenates + ", MinCoordenates=" + _minCoordenates + "\n" + ", MaxDeltaResolutions="
             + _maxDeltaResolutions + ", MinDeltaResolutions=" + _minDeltaResolutions + "\n" + ", Verbose=" + _verbose + "]";
   }

}
