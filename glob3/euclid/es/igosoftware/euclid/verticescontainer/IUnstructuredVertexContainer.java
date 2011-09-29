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


package es.igosoftware.euclid.verticescontainer;

import es.igosoftware.euclid.projection.GProjection;
import es.igosoftware.euclid.vector.IVector;
import es.igosoftware.util.IPredicate;


public interface IUnstructuredVertexContainer<

VectorT extends IVector<VectorT, ?>,

VertexT extends IVertexContainer.Vertex<VectorT>,

//MutableT extends IMutable<MutableT>
MutableT extends IUnstructuredVertexContainer<VectorT, VertexT, MutableT>

>
         extends
            IVertexContainer<VectorT, VertexT, MutableT> {


   public VectorT getReferencePoint();


   //   public IVertexContainer<VectorT, VertexT, ?> composedWith(final IUnstructuredVertexContainer<VectorT, VertexT, ?> container);
   //
   //
   //   public IVertexContainer<VectorT, VertexT, ?> select(final IPredicate<VertexT> predicate);
   //
   //
   //   public IVertexContainer<VectorT, VertexT, ?> collect(final ITransformer<VertexT, VertexT> predicate,
   //                                                        final VectorT referencePoint);
   //
   //
   //   public IVertexContainer<VectorT, VertexT, ?> newEmptyContainer(final int initialCapacity,
   //                                                                  final VectorT referencePoint);


   public IUnstructuredVertexContainer<VectorT, VertexT, ?> composedWith(final IUnstructuredVertexContainer<VectorT, VertexT, ?> container);


   public MutableT newEmptyContainer(final int initialCapacity,
                                     final VectorT referencePoint);


   public MutableT newEmptyContainer(final int initialCapacity,
                                     final GProjection projection,
                                     final VectorT referencePoint);


   public IUnstructuredVertexContainer<VectorT, VertexT, ?> selectAsSubContainer(final IPredicate<VertexT> predicate);


   //   @Override
   //   public IUnstructuredVertexContainer<VectorT, VertexT, ?> newEmptyContainer(final int initialCapacity);


   //   @Override
   //   public IVertexContainer<VectorT, VertexT, ?> asSubContainer(final int[] subIndices);

   //    @Override
   //   public GSubVertexContainer<VectorT> asSubContainer(final int[] subIndices);

   // @Override
   //public GSubVertexContainer<VectorT> asSortedSubContainer(final Comparator<VertexT> comparator);

   //   @Override
   //   public IUnstructuredVertexContainer<VectorT, VertexT, ?> asMutableCopy();

}
