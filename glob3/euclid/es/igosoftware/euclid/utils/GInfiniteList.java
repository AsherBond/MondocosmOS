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

import es.igosoftware.util.GMath;


@SuppressWarnings("unchecked")
public class GInfiniteList<T> {
   private static final int DEFAULT_BUCKET_SIZE = 1024; /* 2^10 */


   private int              _size;
   private int              _bucketSize;

   private T[][]            _buckets;


   public GInfiniteList(final int size) {
      this(size, DEFAULT_BUCKET_SIZE);
   }


   public GInfiniteList(final int size,
                        final int bucketSize) {
      _size = size;
      _bucketSize = bucketSize;

      final int numberOfBuckets = numberOfBucketsFor(_size);
      _buckets = (T[][]) new Object[numberOfBuckets][];
   }


   public void add(final T element) {
      final T[] bucket = ensureBucketsForSize(_size);

      final int indexInBucket = _size % _bucketSize;
      bucket[indexInBucket] = element;
      _size++;
   }


   private void checkIndex(final int i) {
      if (i > _size - 1) {
         throw new ArrayIndexOutOfBoundsException("index: " + i + ", size: " + _size);
      }
   }


   public void clear() {
      _size = 0;

      _buckets = (T[][]) new Object[0][];
   }


   private T[] ensureBucketsForSize(final int size) {
      final int currentMaxSize = _buckets.length * _bucketSize;
      if (currentMaxSize > size) {
         return getBucketForIndex(size);
      }

      final int numberOfBuckets = numberOfBucketsFor(size + 1);
      _buckets = Arrays.copyOf(_buckets, numberOfBuckets);
      return ensureBucketForIndex(size);
   }


   private int numberOfBucketsFor(final int size) {
      return (int) Math.ceil((double) size / _bucketSize);
   }


   private T[] ensureBucketForIndex(final int index) {
      final int bucketI = index / _bucketSize;

      T[] bucket = _buckets[bucketI];
      if (bucket == null) {
         bucket = (T[]) new Object[_bucketSize];
         _buckets[bucketI] = bucket;
      }
      return bucket;
   }


   public T get(final int index) {
      checkIndex(index);

      final T[] bucket = getBucketForIndex(index);
      if (bucket == null) {
         return null;
      }

      final int indexInBucket = index % _bucketSize;
      return bucket[indexInBucket];
   }


   private T[] getBucketForIndex(final int index) {
      final int bucketI = index / _bucketSize;
      return _buckets[bucketI];
   }


   public boolean isEmpty() {
      return (_size == 0);
   }


   public void set(final int index,
                   final T element) {
      checkIndex(index);

      final int indexInBucket = index % _bucketSize;
      ensureBucketForIndex(index)[indexInBucket] = element;
   }


   public int size() {
      return _size;
   }


   public void showStatistics() {
      System.out.println("-----------------------------------------------------------------------------");
      System.out.println("Infinite List");
      System.out.println("  Size: " + _size);


      final int bucketsCount = _buckets.length;
      System.out.println("  Buckets: " + bucketsCount);
      System.out.println("  Buckets Size: " + _bucketSize);

      int emptyBuckets = 0;
      int nullsInBuckets = 0;
      int usedSlotsInBuckets = 0;
      for (final T[] bucket : _buckets) {
         if (bucket == null) {
            emptyBuckets++;
         }
         else {
            for (final T element : bucket) {
               if (element == null) {
                  nullsInBuckets++;
               }
               else {
                  usedSlotsInBuckets++;
               }
            }
         }
      }
      System.out.println("  Empty Buckets: " + emptyBuckets + " (" + GMath.roundTo(100f * emptyBuckets / bucketsCount, 2) + "%)");
      System.out.println("  Used slots in Buckets: " + usedSlotsInBuckets);
      System.out.println("  Extra references: " + nullsInBuckets + " (" + GMath.roundTo(100f * nullsInBuckets / _size, 2) + "%)");

      final int plainReferences = _size;
      final int references = bucketsCount + (bucketsCount - emptyBuckets) * _bucketSize;
      System.out.println("  InfList References: " + references + " (" + GMath.roundTo(100f * references / plainReferences, 2)
                         + "%)");

      System.out.println("-----------------------------------------------------------------------------");
   }


   public static void main(final String[] args) {
      System.out.println("GInfiniteList 0.1");
      System.out.println("-----------------\n");

      final GInfiniteList<Integer> list = new GInfiniteList<Integer>(8, 5);


      list.set(0, 10);
      list.set(1, 11);
      list.set(2, 12);
      list.set(3, 13);
      list.set(4, 14);

      //      list.add(25);
      //      list.add(26);
      //      list.add(27);
      //      list.add(28);

      for (int i = 0; i < list.size(); i++) {
         System.out.println("#" + i + " " + list.get(i));
      }
      list.showStatistics();


      //      list.clear();
      //      list.add(1);
      //      list.add(2);
      //      list.add(3);
      //      list.add(4);
      //
      //      for (int i = 0; i < list.size(); i++) {
      //         System.out.println("#" + i + " " + list.get(i));
      //      }
      //      list.showStatistics();
   }

}
