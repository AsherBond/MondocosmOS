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
import java.awt.Font;
import java.awt.Paint;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import org.jCharts.axisChart.AxisChart;
import org.jCharts.chartData.AxisChartDataSet;
import org.jCharts.chartData.ChartDataException;
import org.jCharts.chartData.DataSeries;
import org.jCharts.encoders.PNGEncoder;
import org.jCharts.properties.AxisProperties;
import org.jCharts.properties.BarChartProperties;
import org.jCharts.properties.ChartProperties;
import org.jCharts.properties.ChartTypeProperties;
import org.jCharts.properties.LegendProperties;
import org.jCharts.properties.PropertyException;
import org.jCharts.properties.util.ChartFont;
import org.jCharts.types.ChartType;

import es.igosoftware.io.GFileName;
import es.igosoftware.logging.GLoggerObject;
import es.igosoftware.util.GMath;


public class GHistogram<T>
         extends
            GLoggerObject {

   public static interface DataQuantityCalculator<T> {
      public double quantity(final T sample);
   }


   //final private Collection<T>          _data;
   //final private int                    _intervals;
   //final private DataQuantityCalculator _calculator;
   final private int             _dataSize;


   final private Collection<T>[] _categories;
   final private double[]        _averages;
   final private double          _average;
   final private double          _standardDeviation;
   final private boolean         _verbose;
   final private Comparator<T>   _comparator;


   @SuppressWarnings("unchecked")
   public GHistogram(final Iterable<T> data,
                     final int intervals,
                     final boolean verbose,
                     final DataQuantityCalculator<T> calculator,
                     final Comparator<T> comparator) {
      //      _data = data;
      //      _intervals = intervals;
      //      _calculator = calculator;

      _verbose = verbose;
      _comparator = comparator;

      _categories = new Collection[intervals];
      for (int i = 0; i < _categories.length; i++) {
         _categories[i] = new ArrayList<T>();
         //_categories[i] = new TreeSet<T>();
      }


      double maxQuantity = Double.NEGATIVE_INFINITY;
      double minQuantity = Double.POSITIVE_INFINITY;
      int dataCounter = 0;
      double total = 0;
      for (final T eachData : data) {
         final double eachQuantity = calculator.quantity(eachData);

         total += eachQuantity;

         if (eachQuantity > maxQuantity) {
            maxQuantity = eachQuantity;
         }

         if (eachQuantity < minQuantity) {
            minQuantity = eachQuantity;
         }

         dataCounter++;
      }
      _average = total / dataCounter;

      _dataSize = dataCounter - 1;

      double standardDeviationAccumulator = 0;
      for (final T eachData : data) {
         final double eachQuantity = calculator.quantity(eachData);
         final double delta = eachQuantity - _average;
         standardDeviationAccumulator += delta * delta;
      }
      _standardDeviation = GMath.sqrt(standardDeviationAccumulator / _dataSize);

      final double categoryStep = (maxQuantity - minQuantity) / intervals;

      _averages = new double[intervals];

      for (final T eachData : data) {
         final double eachQuantity = calculator.quantity(eachData);
         final int eachCategory = Math.min((int) ((eachQuantity - minQuantity) / categoryStep), intervals - 1);

         _categories[eachCategory].add(eachData);
         _averages[eachCategory] += eachQuantity;
      }

      double accumulatedPercent = 0;
      for (int i = 0; i < _categories.length; i++) {
         accumulatedPercent += getPercent(i);
      }

      assert (accumulatedPercent == 1) : accumulatedPercent;


      for (int i = 0; i < _categories.length; i++) {
         _averages[i] /= _categories[i].size();
      }
   }


   @SuppressWarnings("unchecked")
   public GHistogram(final Iterable<T> data,
                     final T[] intervalsList,
                     final boolean verbose,
                     final DataQuantityCalculator<T> calculator,
                     final Comparator<T> comparator) {

      _verbose = verbose;
      _comparator = comparator;

      final int intervals = intervalsList.length + 1;
      _categories = new Collection[intervals];
      for (int i = 0; i < _categories.length; i++) {
         _categories[i] = new ArrayList<T>();
      }


      double maxQuantity = Double.NEGATIVE_INFINITY;
      double minQuantity = Double.POSITIVE_INFINITY;
      int dataCounter = 0;
      double total = 0;
      for (final T eachData : data) {
         final double eachQuantity = calculator.quantity(eachData);

         total += eachQuantity;

         if (eachQuantity > maxQuantity) {
            maxQuantity = eachQuantity;
         }

         if (eachQuantity < minQuantity) {
            minQuantity = eachQuantity;
         }

         dataCounter++;
      }
      _average = total / dataCounter;

      _dataSize = dataCounter - 1;

      double standardDeviationAccumulator = 0;
      for (final T eachData : data) {
         final double eachQuantity = calculator.quantity(eachData);
         final double delta = eachQuantity - _average;
         standardDeviationAccumulator += delta * delta;
      }
      _standardDeviation = GMath.sqrt(standardDeviationAccumulator / _dataSize);

      _averages = new double[intervals];

      for (final T eachData : data) {
         final double eachQuantity = calculator.quantity(eachData);
         final int eachCategory = getEachCategory(eachData, intervalsList);

         _categories[eachCategory].add(eachData);
         _averages[eachCategory] += eachQuantity;
      }

      double accumulatedPercent = 0;
      for (int i = 0; i < _categories.length; i++) {
         accumulatedPercent += getPercent(i);
      }

      assert (accumulatedPercent == 1) : accumulatedPercent;


      for (int i = 0; i < _categories.length; i++) {
         _averages[i] /= _categories[i].size();
      }
   }


   private int getEachCategory(final T item,
                               final T[] list) {
      int i;
      for (i = 0; i < list.length; i++) {
         final int compareResult = _comparator.compare(item, list[i]);
         if (compareResult < 0) {
            return i;
         }
      }

      return i;
   }


   @Override
   public boolean logVerbose() {
      return _verbose;
   }


   public double getAverage() {
      return _average;
   }


   public double getStandardDeviation() {
      return _standardDeviation;
   }


   public int getIntervals() {
      return _categories.length;
   }


   public Collection<T> getInterval(final int i) {
      return Collections.unmodifiableCollection(_categories[i]);
   }


   public int getFrequency(final int i) {
      return _categories[i].size();
   }


   public double getPercent(final int i) {
      return (double) getFrequency(i) / (_dataSize + 1);
   }


   public double getToPercent(final int i) {
      int frequencySum = 0;
      for (int j = 0; j <= i; j++) {
         frequencySum += getFrequency(j);
      }
      return (double) frequencySum / (_dataSize + 1);
   }


   public double getFromPercent(final int i) {
      if (i > 0) {
         return getToPercent(i - 1);
      }
      return 0;
   }


   public Collection<T> getData(final double fromPercent,
                                final double toPercent) {
      assert (fromPercent >= 0) : fromPercent;
      assert (toPercent <= 1) : toPercent;
      assert (toPercent <= fromPercent) : toPercent;

      final Collection<T> result = new ArrayList<T>();

      for (int i = 0; i < _categories.length; i++) {
         if ((getFromPercent(i) < toPercent) && (getToPercent(i) >= fromPercent)) {
            result.addAll(_categories[i]);
         }
      }

      return Collections.unmodifiableCollection(result);
   }


   public double getAverage(final int i) {
      return _averages[i];
   }


   public int getDataSize() {
      return _dataSize;
   }


   @Override
   public String toString() {
      return "GHistogram dataSize= " + getDataSize() + ", intervals= " + getIntervals();
   }


   public void showStatistics() {
      //System.out.println(this);
      logInfo(toString());
      showCategories();
   }


   public void showCategories() {
      for (int i = 0; i < getIntervals(); i++) {
         //final double intervalPercent = GMath.roundTo(100f * getPercent(i), 2);
         final String intervalPercent = NumberFormat.getInstance().format(GMath.roundTo(100f * getPercent(i), 2));
         //         System.out.println("  Interval #" + i + ": " + getFrequency(i) + " items (" + intervalPercent + "%)" /*+ ", data="+ _categories[i]*/);

         //final LinkedList<T> result = (LinkedList<T>) getInterval(i);
         final LinkedList<T> sortedResult = new LinkedList<T>(_categories[i]);

         //Collections.sort(sortedResult);
         Collections.sort(sortedResult, new Comparator<T>() {
            @Override
            public int compare(final T value0,
                               final T value1) {
               return _comparator.compare(value0, value1);
            }
         });

         if (sortedResult.size() >= 2) {
            final String intervalFirst = sortedResult.getFirst().toString();
            final String intervalLast = sortedResult.getLast().toString();

            logInfo("  Interval #" + i + " [" + intervalFirst + ".." + intervalLast + "]: " + getFrequency(i) + " items ("
                    + intervalPercent + "%)" /*+ ", data="+ _categories[i]*/);
         }
         else if (sortedResult.size() == 1) {
            final String intervalFirst = sortedResult.getFirst().toString();

            logInfo("  Interval #" + i + " [" + intervalFirst + "]: " + getFrequency(i) + " item (" + intervalPercent + "%)");
         }
         else {
            logInfo("  Interval #" + i + " [..]: 0 items (0%)");
         }

      }
   }


   public void savePNG(final GFileName fileName,
                       final String chartTitle,
                       final String axisXTitle,
                       final Color color,
                       final int decimals,
                       final String unitName) {

      final int intervals = getIntervals();

      final String[] dataLabels = new String[intervals];
      final double[][] dataValues = new double[1][intervals];

      for (int i = 0; i < intervals; i++) {
         //dataLabels[i] = "#" + i;
         dataLabels[i] = "" + NumberFormat.getInstance().format(GMath.roundTo((float) getAverage(i), decimals)) + " "
                         + NumberFormat.getInstance().format(GMath.roundTo(100f * (float) getToPercent(i), 0)) + "%";
         dataValues[0][i] = getFrequency(i);
      }

      final String axisYTitle = "Frequency";
      //      final String averageStr = NumberFormat.getInstance().format(GMath.roundTo(getAverage(), decimals)) + unitName;
      //      final String standardDeviationString = NumberFormat.getInstance().format(GMath.roundTo(getStandardDeviation(), decimals))
      //                                             + unitName;
      final String averageStr = NumberFormat.getInstance().format(GMath.roundTo(getAverage(), decimals));
      final String standardDeviationString = NumberFormat.getInstance().format(GMath.roundTo(getStandardDeviation(), decimals));
      final String title = "GHistogram " + unitName + ": " + chartTitle + "  (Average: " + averageStr + " , Standard Deviation: "
                           + standardDeviationString + ")";
      plot(fileName, title, axisXTitle, axisYTitle, new String[] {
         axisYTitle
      }, new Paint[] {
         color
      }, dataLabels, dataValues);
   }


   private static void plot(final GFileName fileName,
                            final String chartTitle,
                            final String axisXTitle,
                            final String axisYTitle,
                            final String[] legendLabels,
                            final Paint[] legendPaints,
                            final String[] labels,
                            final double[][] values) {

      final DataSeries dataSeries = new DataSeries(labels, axisXTitle, axisYTitle, chartTitle);

      final ChartProperties chartProperties = new ChartProperties();
      chartProperties.setTitleFont(new ChartFont(new Font("SansSerif", Font.PLAIN, 25), new Color(0, 0, 0)));

      final AxisProperties axisProperties = new AxisProperties();
      //axisProperties.getXAxisProperties().setShowAxisLabels(false);
      //axisProperties.getYAxisProperties().setAxisTickMarkPixelLength(1);
      axisProperties.getYAxisProperties().setShowGridLines(10);

      final LegendProperties legendProperties = new LegendProperties();

      final ChartTypeProperties barChartProperties = new BarChartProperties();


      try {
         //         DataAxisProperties yAxis = (DataAxisProperties) axisProperties.getYAxisProperties();
         //         yAxis.setUserDefinedScale(0, 25);
         //         yAxis.setNumItems(5);


         final AxisChartDataSet axisPlotDataSet = new AxisChartDataSet(values, legendLabels, legendPaints, ChartType.BAR,
                  barChartProperties);
         dataSeries.addIAxisPlotDataSet(axisPlotDataSet);

         final AxisChart chart = new AxisChart(dataSeries, chartProperties, axisProperties, legendProperties, 1600, 1024);

         PNGEncoder.encode(chart, new FileOutputStream(fileName.asFile()));
      }
      catch (final ChartDataException e) {
         e.printStackTrace();
      }
      catch (final PropertyException e) {
         e.printStackTrace();
      }
      catch (final IOException e) {
         e.printStackTrace();
      }

   }


   public static void main(final String[] args) {
      System.out.println("GHistogram 0.1");
      System.out.println("-------------\n");


      //      final Random random = new Random();

      final Collection<Double> data = new ArrayList<Double>();
      //      for (int i = 0; i < 5000; i++) {
      //         data.add(random.nextDouble());
      //      }
      data.add(4.0);
      data.add(1.0);
      data.add(11.0);
      data.add(13.0);
      data.add(2.0);
      data.add(7.0);

      final GHistogram<Double> histogram = new GHistogram<Double>(data, 3, true, new DataQuantityCalculator<Double>() {
         @Override
         public double quantity(final Double sample) {
            return sample.doubleValue();
         }
      }, new Comparator<Double>() {
         @Override
         public int compare(final Double value0,
                            final Double value1) {
            return Double.compare(value0, value1);
         }
      });

      histogram.showStatistics();

      histogram.savePNG(GFileName.relative("test.png"), "Random", "Random Values", new Color(0.8f, 0.1f, 0), 10, " years");

      System.out.println("done!");
   }

}
