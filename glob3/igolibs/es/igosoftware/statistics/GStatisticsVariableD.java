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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import es.igosoftware.io.GFileName;
import es.igosoftware.logging.ILogger;
import es.igosoftware.statistics.GHistogram.DataQuantityCalculator;
import es.igosoftware.util.GMath;


public class GStatisticsVariableD
         extends
            GStatisticsVariableAbstract<Double> {

   private double             _total             = 0;
   private double             _min               = Long.MAX_VALUE;
   private double             _max               = 0;
   private double             _average           = 0;
   private double             _standardDeviation = 0;
   private final List<Double> _values            = new ArrayList<Double>();

   private GHistogram<Double> _histogram         = null;
   private int                _numIntervals      = 0;
   private Double[]           _intervalList      = null;


   public GStatisticsVariableD(final String name,
                               final int flags) {
      this(name, flags, 10);
   }


   public GStatisticsVariableD(final String name,
                               final int flags,
                               final Double[] intervalList) {
      super(name, flags);
      _intervalList = intervalList;
      _numIntervals = _intervalList.length;
   }


   public GStatisticsVariableD(final String name,
                               final int flags,
                               final int numIntervals) {
      super(name, flags);
      _numIntervals = numIntervals;
   }


   public GStatisticsVariableD(final String name) {

      super(name);
   }


   @Override
   public synchronized void show(final ILogger logger) {

      String output = "  " + _name + " [counter: " + _counter;


      if (isFlaged(TOTAL)) {
         output += "; total: " + _total;
      }

      if (isFlaged(MIN)) {
         output += "; min: " + _min;
      }

      if (isFlaged(MAX)) {
         output += "; max: " + _max;
      }

      if (isFlaged(AVERAGE)) {
         _average = _total / _counter;
         output += "; average: " + _average;
      }

      if (isFlaged(STANDARD_DEVIATION)) {

         double summatory = 0.0;
         final Iterator<Double> iterator = _values.iterator();
         while (iterator.hasNext()) {
            final double value = iterator.next();
            summatory += (value - _average) * (value - _average);
         }

         _standardDeviation = GMath.sqrt(summatory / _counter);
         output += "; standard deviation: " + _standardDeviation;
      }

      output += " ]";
      logger.logInfo(output);

      if (isFlaged(HISTOGRAM)) {

         final DataQuantityCalculator<Double> dataQCalculator = new DataQuantityCalculator<Double>() {
            @Override
            public double quantity(final Double sample) {
               return sample;
            }
         };

         final Comparator<Double> comparator = new Comparator<Double>() {
            @Override
            public int compare(final Double value0,
                               final Double value1) {
               return Double.compare(value0, value1);
            }
         };


         if (_intervalList == null) {
            _histogram = new GHistogram<Double>(_values, _numIntervals, true, dataQCalculator, comparator);
         }
         else {
            _histogram = new GHistogram<Double>(_values, _intervalList, true, dataQCalculator, comparator);
         }


         _histogram.showStatistics();
         final GFileName targetFile = GFileName.fromParentAndParts(_targetDirectory, _unitName + "-" + _name + ".png");
         _histogram.savePNG(targetFile, _name, _name + " Distribution", new Color(0.8f, 0.1f, 0), 3, _unitName);
      }

   }


   @Override
   public void sample(final Double delta) {

      sample(delta.doubleValue());

   }


   public synchronized void sample(final double delta) {
      _counter += 1;
      _total += delta;

      if (isFlaged(MIN)) {
         if (delta < _min) {
            _min = delta;
         }
      }

      if (isFlaged(MAX)) {
         if (delta > _max) {
            _max = delta;
         }
      }

      if (isFlaged(STANDARD_DEVIATION) || isFlaged(HISTOGRAM)) {
         _values.add(delta);
      }

   }


   @Override
   public List<Double> getVarList() {
      return _values;
   }


   @Override
   public Double getAverage() {
      if (isFlaged(AVERAGE)) {
         return _average;
      }
      return null;
   }


   @Override
   public Double getMax() {
      if (isFlaged(MAX)) {
         return _max;
      }
      return null;
   }


   @Override
   public Double getMin() {
      if (isFlaged(MIN)) {
         return _min;
      }
      return null;
   }


   @Override
   public Double getStandardDeviation() {
      if (isFlaged(STANDARD_DEVIATION)) {
         return _standardDeviation;
      }
      return null;
   }


}
