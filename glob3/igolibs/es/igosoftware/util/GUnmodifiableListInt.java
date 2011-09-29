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

public class GUnmodifiableListInt
         implements
            IListInt {

   private static final long serialVersionUID = 1L;

   private final IListInt    _list;


   public GUnmodifiableListInt(final IListInt list) {
      GAssert.notNull(list, "list");
      _list = list;
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
      return _list.contains(element);
   }


   @Override
   public boolean containsAll(final IListInt elements) {
      return _list.containsAll(elements);
   }


   @Override
   public boolean containsAll(final int[] elements) {
      return _list.containsAll(elements);
   }


   @Override
   public int get(final int index) {
      return _list.get(index);
   }


   @Override
   public int indexOf(final int element) {
      return _list.indexOf(element);
   }


   @Override
   public boolean isEmpty() {
      return _list.isEmpty();
   }


   @Override
   public int lastIndexOf(final int element) {
      return _list.lastIndexOf(element);
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
      throw new UnsupportedOperationException();
   }


   @Override
   public int size() {
      return _list.size();
   }


   @Override
   public int[] toArray() {
      return _list.toArray();
   }


   @Override
   public boolean equals(final Object o) {
      return (this == o) || _list.equals(o);
   }


   @Override
   public int hashCode() {
      return _list.hashCode();
   }


   @Override
   public IListInt subList(final int fromIndex,
                           final int toIndex) {
      return new GSubListInt(this, fromIndex, toIndex);
   }


   @Override
   public void ensureCapacity(final int minCapacity) {

      throw new UnsupportedOperationException();
   }


   @Override
   public void trimToSize() {

      throw new UnsupportedOperationException();
   }


}
