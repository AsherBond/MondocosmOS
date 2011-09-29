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


package es.igosoftware.globe.weather.aemet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;


public class GClusterer {

   private GClusterer() {
   }

   public interface NeighborhoodCalculator<T> {
      public Collection<T> getNeighborhood(final T value);
   }


   public static <T> Collection<Set<T>> getClusters(final Collection<T> values,
                                                    final NeighborhoodCalculator<T> neighborhoodCalculator) {

      final Collection<Set<T>> clusters = new ArrayList<Set<T>>();

      final Set<T> processed = new HashSet<T>(values.size());
      for (final T value : values) {
         if (processed.contains(value)) {
            continue;
         }

         // data not processed, create a new cluster starting from this
         final Set<T> cluster = new HashSet<T>();
         clusters.add(cluster);

         final LinkedList<T> toProcess = new LinkedList<T>();
         toProcess.add(value);

         while (!toProcess.isEmpty()) {
            final T valueInProcess = toProcess.removeFirst();
            if (processed.contains(valueInProcess)) {
               continue;
            }

            processed.add(valueInProcess);

            cluster.add(valueInProcess);

            toProcess.addAll(neighborhoodCalculator.getNeighborhood(valueInProcess));
         }

      }

      return clusters;
   }
}
