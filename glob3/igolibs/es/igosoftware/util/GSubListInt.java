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

public class GSubListInt
         implements
            IListInt {

   private static final long serialVersionUID = 1L;

   private final IListInt    _list;
   private final int         _offset;
   private final int         _size;


   public GSubListInt(final IListInt list,
                      final int fromIndex,
                      final int toIndex) {
      if (fromIndex < 0) {
         throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
      }
      if (toIndex > list.size()) {
         throw new IndexOutOfBoundsException("toIndex = " + toIndex);
      }
      if (fromIndex > toIndex) {
         throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
      }

      _list = list;
      _offset = fromIndex;
      _size = toIndex - fromIndex;
   }


   @Override
   public boolean add(final int element) {
      throw new UnsupportedOperationException();
   }


   @Override
   public boolean addAll(final IListInt elements) {
      throw new UnsupportedOperationException();
   }


   @Override
   public boolean addAll(final int[] elements) {
      throw new UnsupportedOperationException();
   }


   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }


   @Override
   public boolean contains(final int element) {
      return (indexOf(element) >= 0);
   }


   @Override
   public boolean containsAll(final IListInt elements) {
      for (int i = 0; i < elements.size(); i++) {
         if (!contains(elements.get(i))) {
            return false;
         }
      }

      return true;
   }


   @Override
   public boolean containsAll(final int[] elements) {
      for (final int element : elements) {
         if (!contains(element)) {
            return false;
         }
      }

      return true;
   }


   private void rangeCheck(final int index) {
      if ((index < 0) || (index >= _size)) {
         throw new IndexOutOfBoundsException("Index: " + index + ",Size: " + _size);
      }
   }


   @Override
   public int get(final int index) {
      rangeCheck(index);
      return _list.get(index + _offset);
   }


   @Override
   public int indexOf(final int element) {
      final int to = _offset + _size;
      for (int i = _offset; i < to; i++) {
         if (_list.get(i) == element) {
            return i - _offset;
         }
      }
      return -1;
   }


   @Override
   public boolean isEmpty() {
      return (_size == 0);
   }


   @Override
   public int lastIndexOf(final int element) {
      final int to = _offset + _size;
      for (int i = to - 1; i >= 0; i--) {
         if (_list.get(i) == element) {
            return i - _offset;
         }
      }
      return -1;
   }


   @Override
   public boolean removeAll(final IListInt elements) {
      throw new UnsupportedOperationException();
   }


   @Override
   public boolean removeAll(final int[] elements) {
      throw new UnsupportedOperationException();
   }


   @Override
   public int removeByIndex(final int index) {
      throw new UnsupportedOperationException();
   }


   @Override
   public boolean removeByValue(final int element) {
      throw new UnsupportedOperationException();
   }


   @Override
   public int set(final int index,
                  final int element) {
      rangeCheck(index);
      return _list.set(index + _offset, element);
   }


   @Override
   public int size() {
      return _size;
   }


   @Override
   public IListInt subList(final int fromIndex,
                           final int toIndex) {
      return new GSubListInt(this, fromIndex, toIndex);
   }


   @Override
   public int[] toArray() {
      final int[] result = new int[_size];
      for (int i = 0; i < _size; i++) {
         result[i] = get(i);
      }
      return result;
   }


   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      }
      if (!(o instanceof IListInt)) {
         return false;
      }

      final IListInt other = (IListInt) o;

      if (_size != other.size()) {
         return false;
      }

      for (int i = 0; i < _size; i++) {
         if (get(i) != other.get(i)) {
            return false;
         }
      }

      return true;
   }


   @Override
   public int hashCode() {
      int hashCode = 1;
      for (int i = 0; i < _size; i++) {
         hashCode = 31 * hashCode + get(i);
      }
      return hashCode;
   }


   @Override
   public String toString() {
      return "GSubListInt [size=" + _size + ", offset=" + _offset + ", list=" + _list + "]";
   }


   @Override
   public void ensureCapacity(final int minCapacity) {

      //_list.ensureCapacity(minCapacity);
      throw new UnsupportedOperationException();
   }


   @Override
   public void trimToSize() {

      //_list.trimToSize();
      throw new UnsupportedOperationException();
   }

}
