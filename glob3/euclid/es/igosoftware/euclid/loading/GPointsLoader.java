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


package es.igosoftware.euclid.loading;

import java.io.IOException;

import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;
import es.igosoftware.logging.GLoggerObject;
import es.igosoftware.util.GStringUtils;
import es.igosoftware.util.GUtils;


public abstract class GPointsLoader<VectorT extends IVector<VectorT, ?>, VertexT extends IVertexContainer.Vertex<VectorT>>
         extends
            GLoggerObject {

   public static final int DEFAULT_FLAGS = 0;
   public static final int VERBOSE       = 1;

   private final int       _flags;
   private boolean         _loaded       = false;


   protected GPointsLoader(final int flags) {
      _flags = flags;
   }


   protected final boolean isFlagged(final int flags) {
      return (_flags & flags) != 0;
   }


   @Override
   public final boolean logVerbose() {
      return isFlagged(GPointsLoader.VERBOSE) || GUtils.isDevelopment();
   }


   public final synchronized void load() throws IOException {
      if (_loaded) {
         throw new RuntimeException("Already loaded!");
      }
      _loaded = true;

      final long start = System.currentTimeMillis();
      rawLoad();
      final long elapsed = System.currentTimeMillis() - start;
      //logInfo("Read in " + GMath.roundTo(elapsed / 1000f, 2) + " seconds");
      logInfo("Read in " + elapsed + " ms. ( " + GStringUtils.getTimeMessage(elapsed) + " )");

   }


   protected abstract void rawLoad() throws IOException;


   //   public final synchronized IVertexContainer<VectorT, VertexT, ?> getVertices() {
   //      if (!_loaded) {
   //         throw new RuntimeException("Not yet loaded!");
   //      }
   //
   //      return getRawVertices();
   //   }

   public abstract IVertexContainer<VectorT, VertexT, ?> getVertices();


   protected abstract IVertexContainer<VectorT, VertexT, ?> getRawVertices();


   public final boolean isLoaded() {
      return _loaded;
   }

}
