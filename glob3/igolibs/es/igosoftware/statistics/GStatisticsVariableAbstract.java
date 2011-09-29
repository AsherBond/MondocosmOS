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

import java.util.List;

import es.igosoftware.io.GFileName;
import es.igosoftware.logging.ILogger;
import es.igosoftware.util.GAssert;


public abstract class GStatisticsVariableAbstract<TypeT extends Number> {

   public static final int MAX                = 1;
   public static final int MIN                = 2;
   public static final int AVERAGE            = 4;
   public static final int TOTAL              = 8;
   public static final int STANDARD_DEVIATION = 16;
   public static final int HISTOGRAM          = 32;


   protected final String  _name;
   protected final int     _flags;
   protected long          _counter           = 0;
   protected String        _unitName          = "";
   protected GFileName     _targetDirectory   = GFileName.CURRENT_DIRECTORY;


   public GStatisticsVariableAbstract(final String name,
                                      final int flags) {
      GAssert.notNull(name, "name");

      _name = name;
      _flags = flags;
   }


   public GStatisticsVariableAbstract(final String name) {

      this(name, 0);
   }


   public abstract void show(final ILogger logger);


   public String getName() {
      return _name;
   }


   public long getCounter() {
      return _counter;
   }


   public synchronized void setUnitName(final String unitName) {
      _unitName = unitName;
   }


   public synchronized void setTargetDirectory(final GFileName targetDirectory) {
      _targetDirectory = targetDirectory;
   }


   public abstract List<TypeT> getVarList();


   public abstract Number getMax();


   public abstract Number getMin();


   public abstract Double getAverage();


   public abstract Double getStandardDeviation();


   protected boolean isFlaged(final int reference) {
      return (_flags & reference) != 0;
   }


   public abstract void sample(final TypeT delta);


}
