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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import es.igosoftware.concurrent.GConcurrent;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.euclid.verticescontainer.GStructuredCompositeVertexContainer;
import es.igosoftware.euclid.verticescontainer.IStructuredVertexContainer;
import es.igosoftware.euclid.verticescontainer.IVertexContainer;
import es.igosoftware.io.GFileName;


public abstract class GStructuredFilePointsLoader<VectorT extends IVector<VectorT, ?>,

GroupT extends IStructuredVertexContainer.IVertexGroup<VectorT, IVertexContainer.Vertex<VectorT>, GroupT>>
         extends
            GFilePointsLoader<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>> {


   private final GStructuredCompositeVertexContainer<VectorT, GroupT> _verticesComposite = new GStructuredCompositeVertexContainer<VectorT, GroupT>();


   protected GStructuredFilePointsLoader(final int flags,
                                         final GFileName... fileNames) {
      super(flags, fileNames);
   }


   @Override
   protected final void rawLoad() throws IOException {
      final List<GFileName> fileNames = getFileNames();
      final int filesCount = fileNames.size();

      startLoad(filesCount);

      if (filesCount == 1) {
         final GFileName fileName = fileNames.get(0);
         logInfo("Reading vertices from \"" + fileName.buildPath() + "\"...");

         final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> vertices = loadVerticesFromFile(fileName);
         _verticesComposite.addChild(vertices);
      }
      else {
         final ExecutorService executor = GConcurrent.createExecutor(Runtime.getRuntime().availableProcessors() * 8);
         //final ExecutorService executor = GConcurrent.getDefaultExecutor();

         final List<Future<IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?>>> futuresVertices = new ArrayList<Future<IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?>>>(
                  filesCount);

         for (int i = 0; i < filesCount; i++) {
            final int finalI = i;

            final Future<IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?>> futureVertices = executor.submit(new Callable<IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?>>() {
               @Override
               public IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> call()
                                                                                                                                         throws IOException {
                  final GFileName fileName = fileNames.get(finalI);
                  logInfo("Reading vertices from \"" + fileName.buildPath() + "\" (" + (finalI + 1) + "/" + filesCount + ")...");

                  final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> vertices = loadVerticesFromFile(fileName);

                  logInfo("Read " + vertices.size() + " vertices from \"" + fileName.buildPath() + "\" (" + (finalI + 1) + "/"
                          + filesCount + ")...");

                  return vertices;
               }
            });

            futuresVertices.add(futureVertices);
         }


         for (final Future<IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?>> futureVertices : futuresVertices) {
            try {
               final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> vertices = futureVertices.get();
               _verticesComposite.addChild(vertices);
            }
            catch (final InterruptedException e) {
               throw new IOException(e);
            }
            catch (final ExecutionException e) {
               throw new IOException(e);
            }
         }
      }

      endLoad();
      _verticesComposite.makeImmutable();

      logInfo("Read " + _verticesComposite.size() + " vertices from " + filesCount + " files");
   }


   @Override
   protected final IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> getRawVertices() {
      return (_verticesComposite.childrenCount() == 1) ? _verticesComposite.getChild(0) : _verticesComposite;
   }


   @Override
   protected abstract IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> loadVerticesFromFile(final GFileName fileName)
                                                                                                                                                                                 throws IOException;


   @Override
   public final synchronized IStructuredVertexContainer<VectorT, IStructuredVertexContainer.StructuredVertex<VectorT, GroupT>, GroupT, ?> getVertices() {
      if (!isLoaded()) {
         throw new RuntimeException("Not yet loaded!");
      }

      return getRawVertices();
   }

}
