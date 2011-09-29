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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.igosoftware.io.GFileName;
import es.igosoftware.logging.GLogger;
import es.igosoftware.logging.ILogger;


public class GStatisticsUnit {

   private static final ILogger                                             LOGGER        = GLogger.instance();
   private static final String                                              LINE          = "-------------------------------------------------------------------------------";

   private final GFileName                                                  _targetDirectory;
   private final String                                                     _name;
   //private final GStatisticsVariableAbstract<? extends Number>[]            _variables;
   private final List<GStatisticsVariableAbstract<? extends Number>>        _variables    = new ArrayList<GStatisticsVariableAbstract<? extends Number>>();
   private final Map<String, GStatisticsVariableAbstract<? extends Number>> _variablesMap = new HashMap<String, GStatisticsVariableAbstract<? extends Number>>();


   public GStatisticsUnit(final String name) {
      //_name = name;
      this(name, GFileName.CURRENT_DIRECTORY);
   }


   public GStatisticsUnit(final String name,
                          final GFileName directory) {
      _name = name;
      _targetDirectory = directory;
   }


   //   public GStatisticsUnit(final String name,
   //                          final GStatisticsVariableAbstract<? extends Number>... variables) {
   //      _name = name;
   //      _variables = variables;
   //
   //      _variablesMap = new HashMap<String, GStatisticsVariableAbstract<? extends Number>>(_variables.length);
   //      for (final GStatisticsVariableAbstract<? extends Number> variable : _variables) {
   //         _variablesMap.put(variable.getName(), variable);
   //      }
   //
   //   }


   public void addVariable(final GStatisticsVariableAbstract<? extends Number> var) {
      var.setUnitName(_name);
      var.setTargetDirectory(_targetDirectory);
      _variables.add(var);
      _variablesMap.put(var.getName(), var);
   }


   public void show() {
      show(LOGGER);
   }


   public void show(final ILogger logger) {
      logger.logInfo(LINE);
      if (_name != null) {
         logger.logInfo(_name + ":");
      }

      for (final GStatisticsVariableAbstract<?> variable : _variables) {
         variable.show(logger);
         //logger.info("\n");
      }

      logger.logInfo(LINE);
   }


   public void sample(final String varName,
                      final long delta) {

      final GStatisticsVariableL var = (GStatisticsVariableL) _variablesMap.get(varName);
      if (var == null) {
         LOGGER.logWarning(varName + " variable not found");
      }
      else {
         var.sample(delta);
      }
   }


   //   public void sample(final String varName,
   //                      final int delta) {
   //      sample(varName, delta);
   //   }


   public void sample(final String varName) {
      sample(varName, 1);
   }


   public void sample(final String varName,
                      final double delta) {
      final GStatisticsVariableD var = (GStatisticsVariableD) _variablesMap.get(varName);
      if (var == null) {
         LOGGER.logWarning(varName + " variable not found");
      }
      else {
         var.sample(delta);
      }
   }


   public long getCounter(final String varName) {
      final GStatisticsVariableAbstract<?> var = _variablesMap.get(varName);
      if (var == null) {
         LOGGER.logWarning(varName + " variable not found");
         return -1;
      }

      return var.getCounter();
   }


   public List<? extends Number> getVariableList(final String varName) {
      final GStatisticsVariableAbstract<? extends Number> var = _variablesMap.get(varName);
      if (var == null) {
         LOGGER.logWarning(varName + " variable not found");
         return null;
      }

      return var.getVarList();
   }


   public Number getMax(final String varName) {
      final GStatisticsVariableAbstract<?> var = _variablesMap.get(varName);
      if (var == null) {
         LOGGER.logWarning(varName + " variable not found");
         return null;
      }

      return var.getMax();
   }


   public Number getMin(final String varName) {
      final GStatisticsVariableAbstract<?> var = _variablesMap.get(varName);
      if (var == null) {
         LOGGER.logWarning(varName + " variable not found");
         return null;
      }

      return var.getMin();
   }


   public double getAverage(final String varName) {
      final GStatisticsVariableAbstract<?> var = _variablesMap.get(varName);
      if (var == null) {
         LOGGER.logWarning(varName + " variable not found");
         return Double.NaN;
      }

      return var.getAverage().doubleValue();
   }


   public double getStandarDeviation(final String varName) {
      final GStatisticsVariableAbstract<?> var = _variablesMap.get(varName);
      if (var == null) {
         LOGGER.logWarning(varName + " variable not found");
         return Double.NaN;
      }

      return var.getStandardDeviation().doubleValue();
   }


   public static void main(final String[] args) {
      System.out.println("GStatisticsUnit 0.1");
      System.out.println("-------------------\n");

      final GStatisticsUnit unit = new GStatisticsUnit("Test");

      unit.addVariable(new GStatisticsVariableL("var1", GStatisticsVariableAbstract.MAX | GStatisticsVariableAbstract.MIN
                                                        | GStatisticsVariableAbstract.AVERAGE
                                                        | GStatisticsVariableAbstract.STANDARD_DEVIATION));

      unit.addVariable(new GStatisticsVariableD("var2", GStatisticsVariableAbstract.AVERAGE | GStatisticsVariableAbstract.TOTAL));

      unit.sample("var1", 1);
      unit.sample("var1", 5);
      unit.sample("var1", 9);

      unit.sample("var2", 0.2);
      unit.sample("var2", 8.5);
      unit.sample("var2", 5.7);

      unit.show();
   }

}
