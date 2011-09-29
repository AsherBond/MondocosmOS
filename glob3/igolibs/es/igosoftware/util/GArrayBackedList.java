

package es.igosoftware.util;

import java.util.AbstractList;


public class GArrayBackedList<T>
         extends
            AbstractList<T> {
   private final T[] _array;


   public GArrayBackedList(final T... array) {
      this._array = array;
   }


   @Override
   public int size() {
      return _array.length;
   }


   @Override
   public T get(final int index) {
      return _array[index];
   }

}
