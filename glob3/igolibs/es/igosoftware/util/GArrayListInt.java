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

import java.util.Arrays;


public class GArrayListInt
         implements
            IListInt {


   private static final long serialVersionUID = 1L;

   private int[]             _elementData;
   private int               _size;


   public GArrayListInt() {
      this(10);
   }


   public GArrayListInt(final int initialCapacity) {
      if (initialCapacity < 0) {
         throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
      }
      _elementData = new int[initialCapacity];
   }


   public GArrayListInt(final int[] elements) {
      _size = elements.length;
      _elementData = Arrays.copyOf(elements, _size);
   }


   public GArrayListInt(final IListInt elements) {
      _size = elements.size();
      _elementData = elements.toArray();
   }


   @Override
   public boolean add(final int element) {
      ensureCapacity(_size + 1); // Increments modCount!!
      _elementData[_size++] = element;
      return true;
   }


   @Override
   public boolean addAll(final IListInt elements) {
      ensureCapacity(_size + elements.size());

      for (int i = 0; i < elements.size(); i++) {
         _elementData[_size++] = elements.get(i);
      }

      return true;
   }


   @Override
   public boolean addAll(final int[] elements) {
      ensureCapacity(_size + elements.length);

      for (final int element : elements) {
         _elementData[_size++] = element;
      }

      return true;
   }


   @Override
   public void clear() {
      _size = 0;
   }


   @Override
   public boolean contains(final int element) {
      for (int i = 0; i < _size; i++) {
         if (_elementData[i] == element) {
            return true;
         }
      }

      return false;
   }


   @Override
   public boolean containsAll(final IListInt elements) {
      for (int i = 0; i < elements.size(); i++) {
         final int element = elements.get(i);
         if (!contains(element)) {
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


   @Override
   public void ensureCapacity(final int minCapacity) {
      final int oldCapacity = _elementData.length;
      if (minCapacity > oldCapacity) {
         int newCapacity = (oldCapacity * 3) / 2 + 1;
         if (newCapacity < minCapacity) {
            newCapacity = minCapacity;
         }
         // minCapacity is usually close to size, so this is a win:
         _elementData = Arrays.copyOf(_elementData, newCapacity);
      }
   }


   @Override
   public void trimToSize() {
      final int oldCapacity = _elementData.length;
      if (_size < oldCapacity) {
         _elementData = Arrays.copyOf(_elementData, _size);
      }
   }


   private void rangeCheck(final int index) {
      if (index >= _size) {
         throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + _size);
      }
   }


   @Override
   public int get(final int index) {
      rangeCheck(index);

      return _elementData[index];
   }


   @Override
   public int indexOf(final int element) {
      for (int i = 0; i < _size; i++) {
         if (_elementData[i] == element) {
            return i;
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
      for (int i = _size - 1; i >= 0; i--) {
         if (_elementData[i] == element) {
            return i;
         }
      }

      return -1;
   }


   @Override
   public boolean removeByValue(final int element) {
      for (int index = 0; index < _size; index++) {
         if (element == _elementData[index]) {
            fastRemove(index);
            return true;
         }
      }

      return false;
   }


   private void fastRemove(final int index) {
      final int numMoved = _size - index - 1;
      if (numMoved > 0) {
         System.arraycopy(_elementData, index + 1, _elementData, index, numMoved);
      }
      _size--;
   }


   @Override
   public int removeByIndex(final int index) {
      rangeCheck(index);

      final int oldValue = _elementData[index];

      final int numMoved = _size - index - 1;
      if (numMoved > 0) {
         System.arraycopy(_elementData, index + 1, _elementData, index, numMoved);
      }
      _size--;

      return oldValue;
   }


   @Override
   public boolean removeAll(final IListInt elements) {
      boolean modified = false;

      for (int i = 0; i < elements.size(); i++) {
         final int element = elements.get(i);
         if (contains(element)) {
            removeByValue(element);
            modified = true;
         }
      }

      return modified;
   }


   @Override
   public boolean removeAll(final int[] elements) {
      boolean modified = false;

      for (final int element : elements) {
         if (contains(element)) {
            removeByValue(element);
            modified = true;
         }
      }

      return modified;
   }


   @Override
   public int set(final int index,
                  final int element) {
      rangeCheck(index);

      final int oldValue = _elementData[index];
      _elementData[index] = element;
      return oldValue;
   }


   @Override
   public int size() {
      return _size;
   }


   @Override
   public int[] toArray() {
      return Arrays.copyOf(_elementData, _size);
   }


   @Override
   public String toString() {
      return "GArrayListInt [size=" + size() + ", _elementData=" + Arrays.toString(Arrays.copyOf(_elementData, _size)) + "]";
   }


   public static void main(final String[] args) {
      System.out.println("GArrayListInt 0.1");
      System.out.println("-----------------\n");

      final IListInt list = new GArrayListInt();


      list.add(1);
      list.addAll(new int[] {
                        2,
                        3,
                        4,
                        5
      });
      list.addAll(new int[] {
                        6,
                        7,
                        8,
                        9,
                        10,
                        11,
                        12
      }); // force grow

      list.removeByValue(11);
      list.removeByIndex(4);

      System.out.println(list);
      System.out.println(list.get(8));

      System.out.println(Arrays.toString(list.toArray()));

      list.clear();
      list.add(10);
      System.out.println(list.get(0));
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
         if (_elementData[i] != other.get(i)) {
            return false;
         }
      }

      return true;
   }


   @Override
   public int hashCode() {
      int hashCode = 1;
      for (int i = 0; i < _size; i++) {
         hashCode = 31 * hashCode + _elementData[i];
      }
      return hashCode;
   }


   @Override
   public IListInt subList(final int fromIndex,
                           final int toIndex) {
      return new GSubListInt(this, fromIndex, toIndex);
   }


}
