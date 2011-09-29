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


package es.igosoftware.util;

import java.util.Iterator;
import java.util.NoSuchElementException;


public final class GCompositeIterator<T>
         implements
            Iterator<T> {

   final private Iterator<Iterator<T>> _children;
   private Iterator<T>                 _currentIterator;


   public GCompositeIterator(final Iterable<Iterator<T>> children) {
      _children = children.iterator();
      advanceIterator();
   }


   private void advanceIterator() {
      _currentIterator = _children.hasNext() ? _children.next() : null;
   }


   @Override
   public boolean hasNext() {
      if (_currentIterator == null) {
         return false;
      }
      if (_currentIterator.hasNext()) {
         return true;
      }

      advanceIterator();
      return hasNext();
   }


   @Override
   public T next() {
      if (_currentIterator == null) {
         throw new NoSuchElementException();
      }
      if (_currentIterator.hasNext()) {
         return _currentIterator.next();
      }

      advanceIterator();
      return next();
   }


   @Override
   public void remove() {
      throw new RuntimeException("remove not supported");
   }
}
