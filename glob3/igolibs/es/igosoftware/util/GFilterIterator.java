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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;


public final class GFilterIterator<T>
         implements
            Iterator<T> {

   final private Iterator<? extends T> _iterator;
   final private List<IPredicate<T>>   _predicates   = new ArrayList<IPredicate<T>>();

   private T                           _currentValue;
   private boolean                     _finished     = false;
   private boolean                     _nextConsumed = true;


   @SuppressWarnings("unchecked")
   public GFilterIterator(final Iterator<? extends T> iterator,
                          final IPredicate<T> predicate) {
      this(iterator, (IPredicate<T>[]) new IPredicate<?>[] {
         predicate
      });
   }


   public GFilterIterator(final Iterator<? extends T> iterator,
                          final IPredicate<T>... predicates) {
      GAssert.notNull(iterator, "iterator");
      GAssert.notEmpty(predicates, "predicates");

      for (final IPredicate<T> predicate : predicates) {
         _predicates.add(predicate);
      }

      if (iterator instanceof GFilterIterator<?>) {
         @SuppressWarnings("unchecked")
         final GFilterIterator<T> predicateIterator = (GFilterIterator<T>) iterator;
         _iterator = predicateIterator._iterator;
         _predicates.addAll(predicateIterator._predicates);
      }
      else {
         _iterator = iterator;
      }
   }


   private boolean accept(final T value) {
      for (final IPredicate<T> predicate : _predicates) {
         if (!predicate.evaluate(value)) {
            return false;
         }
      }
      return true;
   }


   public boolean moveToNextValid() {
      boolean found = false;
      while (!found && _iterator.hasNext()) {
         final T currentValue1 = _iterator.next();
         if (accept(currentValue1)) {
            found = true;
            _currentValue = currentValue1;
            _nextConsumed = false;
         }
      }
      if (!found) {
         _finished = true;
      }
      return found;
   }


   @Override
   public T next() {
      if (!_nextConsumed) {
         _nextConsumed = true;
         return _currentValue;
      }

      if (!_finished) {
         if (moveToNextValid()) {
            _nextConsumed = true;
            return _currentValue;
         }
      }

      throw new NoSuchElementException();
   }


   @Override
   public boolean hasNext() {
      return !_finished && (!_nextConsumed || moveToNextValid());
   }


   @Override
   public void remove() {
      throw new RuntimeException("remove not supported");
   }

}
