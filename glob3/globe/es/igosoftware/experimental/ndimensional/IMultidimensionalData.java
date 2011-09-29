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


package es.igosoftware.experimental.ndimensional;


import java.nio.FloatBuffer;
import java.util.List;
import java.util.Map;

import es.igosoftware.euclid.colors.GColorI;
import es.igosoftware.euclid.colors.IColor;
import es.igosoftware.util.GRange;
import es.igosoftware.utils.GPositionBox;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.Globe;


public interface IMultidimensionalData {


   public static enum VectorColorization {
      WHITE("White", GColorI.WHITE),
      BLACK("Black", GColorI.BLACK),
      BLUE("Blue", GColorI.BLUE),
      CYAN("Cyan", GColorI.CYAN),
      GREEN("Green", GColorI.GREEN),
      YELLOW("Yellow", GColorI.YELLOW),
      RED("Red", GColorI.RED),
      RAMP_BY_ANGLE("Ramp by angle", null);


      private final String _label;
      private final IColor _color;


      private VectorColorization(final String label,
                                 final IColor color) {
         _label = label;
         _color = color;
      }


      public IColor getColor() {
         return _color;
      }


      @Override
      public String toString() {
         return _label;
      }
   }


   public static class PointsCloud {

      public final FloatBuffer _pointsBuffer;
      public final FloatBuffer _colorsBuffer;


      public PointsCloud(final FloatBuffer pointsBuffer,
                         final FloatBuffer colorsBuffer) {
         _pointsBuffer = pointsBuffer;
         _colorsBuffer = colorsBuffer;
      }


      //      public void dispose() {
      //         if (_pointsBuffer != null) {
      //            _pointsBuffer.clear();
      //         }
      //         if (_colorsBuffer != null) {
      //            _colorsBuffer.clear();
      //         }
      //      }

   }


   public static class VectorsCloud {

      public final FloatBuffer _pointsBuffer;
      public final FloatBuffer _colorsBuffer;


      public VectorsCloud(final FloatBuffer pointsBuffer,
                          final FloatBuffer colorsBuffer) {
         _pointsBuffer = pointsBuffer;
         _colorsBuffer = colorsBuffer;
      }


      //      public void dispose() {
      //         if (_pointsBuffer != null) {
      //            _pointsBuffer.clear();
      //         }
      //         if (_colorsBuffer != null) {
      //            _colorsBuffer.clear();
      //         }
      //      }

   }


   public String getName();


   public String getTimeDimensionName();


   public List<String> getDimensionsNames();


   public List<String> getNonTimeDimensionsNames();


   public int getTimeDimensionLength();


   public List<String> getAvailableValueVariablesNames();


   public List<String> getAvailableVectorVariablesNames();


   public GPositionBox getBox();


   public IMultidimensionalData.PointsCloud calculateValuePointsCloud(final String variableName,
                                                                      final int time,
                                                                      final Globe globe,
                                                                      final double verticalExaggeration,
                                                                      final Vec4 referencePoint,
                                                                      final Map<String, GRange<Integer>> dimensionsRanges,
                                                                      final float alpha);


   public IMultidimensionalData.VectorsCloud calculateVectorsCloud(final String variableName,
                                                                   final int time,
                                                                   final Globe globe,
                                                                   final double verticalExaggeration,
                                                                   final Vec4 referencePoint,
                                                                   final float factor,
                                                                   final IMultidimensionalData.VectorColorization colorization,
                                                                   final Map<String, GRange<Integer>> dimensionsRanges);


   public int getDimensionLength(final String dimensionName);


}
