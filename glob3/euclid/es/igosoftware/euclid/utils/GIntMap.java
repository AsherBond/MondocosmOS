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


package es.igosoftware.euclid.utils;

import java.util.Arrays;


public class GIntMap<V> {
   private static final int UNUSED_KEY = -1;

   private final int        _bucketSize;
   private int[][]          _keys;
   private V[][]            _buckets;


   public GIntMap(final int initialCapacity,
                  final int bucketSize) {
      if (initialCapacity < 0) {
         throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
      }

      //      if (initialCapacity > MAXIMUM_CAPACITY) {
      //         initialCapacity = MAXIMUM_CAPACITY;
      //      }

      //      // Find a power of 2 >= initialCapacity
      //      int capacity = 1;
      //      while (capacity < initialCapacity) {
      //         capacity <<= 1;
      //      }

      //      _size = 0;
      //      final int bucketsCount = (int) Math.ceil(initialCapacity / BUCKET_SIZE);
      //      _buckets = (V[][]) new Object[bucketsCount][BUCKET_SIZE];
      //      for (final V[] bucket : _buckets) {
      //         Arrays.fill(bucket, UNUSED_KEY);
      //      }
      _bucketSize = bucketSize;
      final int capacity = (int) Math.ceil((float) initialCapacity / _bucketSize) * _bucketSize;
      clear(capacity);
   }


   @SuppressWarnings("unchecked")
   private void clear(final int capacity) {
      final int bucketsCount = (int) Math.ceil((double) capacity / _bucketSize);

      _buckets = (V[][]) new Object[bucketsCount][_bucketSize];
      for (int i = 0; i < bucketsCount; i++) {
         _buckets[i] = null;
      }

      _keys = new int[bucketsCount][_bucketSize];
      for (final int[] key : _keys) {
         Arrays.fill(key, UNUSED_KEY);
      }
   }


   public boolean containsKey(final int key) {
      final int bucketIndex = bucketIndexFor(key);
      final int[] keys = _keys[bucketIndex];
      if (keys == null) {
         return false;
      }

      for (final int eachKey : keys) {
         if (eachKey == key) {
            return true;
         }
      }
      return false;
   }


   public boolean containsValue(final V value) {
      if (value == null) {
         return containsNullValue();
      }

      for (final V[] bucket : _buckets) {
         if (bucket == null) {
            continue;
         }

         for (final V element : bucket) {
            if (value.equals(element)) {
               return true;
            }
         }
      }

      return false;
   }


   private boolean containsNullValue() {
      for (final V[] bucket : _buckets) {
         if (bucket == null) {
            continue;
         }
         for (final V element : bucket) {
            if (element == null) {
               return true;
            }
         }
      }
      return false;
   }


   public V get(final int key) {
      final int bucketIndex = bucketIndexFor(key);
      final V[] bucket = _buckets[bucketIndex];
      if (bucket == null) {
         return null;
      }

      final int[] keys = _keys[bucketIndex];
      for (int i = 0; i < keys.length; i++) {
         if (keys[i] == key) {
            return bucket[i];
         }
      }

      return null;
   }


   //   private static int hash(int hash) {
   //      // This function ensures that hashCodes that differ only by
   //      // constant multiples at each bit position have a bounded
   //      // number of collisions (approximately 8 at default load factor).
   //      hash ^= (hash >>> 20) ^ (hash >>> 12);
   //      return hash ^ (hash >>> 7) ^ (hash >>> 4);
   //   }


   //   public boolean isEmpty() {
   //      return _size == 0;
   //   }


   @SuppressWarnings("unchecked")
   public void put(final int key,
                   final V value) {
      final int bucketIndex = bucketIndexFor(key);

      V[] bucket = _buckets[bucketIndex];
      if (bucket == null) {
         bucket = (V[]) new Object[_bucketSize];
         _buckets[bucketIndex] = bucket;
      }

      final int[] keys = _keys[bucketIndex];
      for (int i = 0; i < keys.length; i++) {
         if (keys[i] == UNUSED_KEY) {
            keys[i] = key;
            bucket[i] = value;
            return;
         }
      }

      throw new RuntimeException("Can't find a free position for key=" + key + " on bucket #" + bucketIndex + " (bucket="
                                 + Arrays.toString(bucket) + ", keys=" + Arrays.toString(keys) + ")");
   }


   private int bucketIndexFor(final int key) {
      return key % _buckets.length;
   }


   public void showStatistics() {
      int bucketsElementsCount = 0;
      for (final int[] keys : _keys) {
         for (final int key : keys) {
            if (key != UNUSED_KEY) {
               bucketsElementsCount++;
            }
         }
      }

      int emptyBuckets = 0;
      int garbageCount = 0;
      for (int i = 0; i < _buckets.length; i++) {
         if (_buckets[i] == null) {
            emptyBuckets++;
         }
         else {
            final int[] keys = _keys[i];
            for (final int key : keys) {
               if (key == UNUSED_KEY) {
                  garbageCount++;
               }
            }
         }
      }


      final int bucketsCount = _buckets.length;
      System.out.println("-----------------------------------------------------");
      System.out.println("GIntMap");
      //      System.out.println("  size: " + _size);
      System.out.println("  bucket size: " + _bucketSize);
      System.out.println("  buckets: " + bucketsCount);
      System.out.println("  empty buckets: " + emptyBuckets);
      System.out.println("  buckets elements count: " + bucketsElementsCount);
      System.out.println("  garbage: " + garbageCount);

      final int referencesCount = (bucketsCount * 2) + ((bucketsCount - emptyBuckets) * 2 * _bucketSize);
      System.out.println("  References Count: " + referencesCount);
      System.out.println("-----------------------------------------------------");
   }


   public static void main(final String[] args) {
      System.out.println("GIntMap 0.1");
      System.out.println("-----------\n");

      final int capacity = 8000000;
      //final GIntMap<String> map = new GIntMap<String>(capacity, 65536);
      final GIntMap<String> map = new GIntMap<String>(capacity, 512);

      for (int i = 0; i < capacity; i++) {
         map.put(i, "\"" + i + "\"");
      }
      //      map.put(1, "A");

      map.showStatistics();

      System.out.println(map.get(0));
      //System.out.println(map.get(1000));
      System.out.println(map.get(capacity - 1));
   }


}
