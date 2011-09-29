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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;


public class GCompositeIteratorTest {

   @Test
   public void testEmptyIterator() {
      final List<Iterator<Integer>> listsIterator = new ArrayList<Iterator<Integer>>();

      final Iterator<Integer> iterator = new GCompositeIterator<Integer>(listsIterator);

      assertFalse("Exhausted Iterator", iterator.hasNext());
   }


   @Test
   public void testEmptyIterator2() {
      final List<Integer> list1 = new ArrayList<Integer>();
      final List<Integer> list2 = new ArrayList<Integer>();
      final List<Integer> list3 = new ArrayList<Integer>();
      list3.add(1);
      list3.add(2);
      list3.add(3);
      list3.add(4);
      list3.add(5);

      final List<Iterator<Integer>> listsIterator = new ArrayList<Iterator<Integer>>();
      listsIterator.add(list1.iterator());
      listsIterator.add(list2.iterator());
      listsIterator.add(list3.iterator());

      final Iterator<Integer> iterator = new GCompositeIterator<Integer>(listsIterator);

      for (int i = 1; i <= 5; i++) {
         assertEquals("i " + i, Integer.valueOf(i), iterator.next());
      }

      assertFalse("Exhausted Iterator", iterator.hasNext());
   }


   @Test
   public void testEmptyIterator3() {
      final List<Integer> list1 = new ArrayList<Integer>();
      list1.add(1);
      list1.add(2);
      list1.add(3);
      list1.add(4);
      list1.add(5);
      final List<Integer> list2 = new ArrayList<Integer>();
      final List<Integer> list3 = new ArrayList<Integer>();

      final List<Iterator<Integer>> listsIterator = new ArrayList<Iterator<Integer>>();
      listsIterator.add(list1.iterator());
      listsIterator.add(list2.iterator());
      listsIterator.add(list3.iterator());

      final Iterator<Integer> iterator = new GCompositeIterator<Integer>(listsIterator);

      for (int i = 1; i <= 5; i++) {
         assertEquals("i " + i, Integer.valueOf(i), iterator.next());
      }

      assertFalse("Exhausted Iterator", iterator.hasNext());
   }


   @Test
   public void testIterator1() {
      final List<Integer> list1 = new ArrayList<Integer>();
      list1.add(1);
      list1.add(2);
      list1.add(3);
      list1.add(4);
      list1.add(5);

      final List<Iterator<Integer>> listsIterator = new ArrayList<Iterator<Integer>>();
      listsIterator.add(list1.iterator());

      final Iterator<Integer> iterator = new GCompositeIterator<Integer>(listsIterator);

      for (int i = 1; i <= 5; i++) {
         assertEquals("i " + i, Integer.valueOf(i), iterator.next());
      }

      assertFalse("Exhausted Iterator", iterator.hasNext());
   }


   @Test
   public void testIterator2() {
      final List<Integer> list1 = new ArrayList<Integer>();
      list1.add(1);
      list1.add(2);
      list1.add(3);

      final List<Integer> list2 = new ArrayList<Integer>();
      list2.add(4);
      list2.add(5);

      final List<Integer> list3 = new ArrayList<Integer>();

      final List<Integer> list4 = new ArrayList<Integer>();
      list2.add(6);

      final List<List<Integer>> lists = new ArrayList<List<Integer>>();
      lists.add(list1);
      lists.add(list2);
      lists.add(list3);
      lists.add(list4);

      final List<Iterator<Integer>> listsIterator = new ArrayList<Iterator<Integer>>();
      listsIterator.add(list1.iterator());
      listsIterator.add(list2.iterator());
      listsIterator.add(list3.iterator());
      listsIterator.add(list4.iterator());

      final Iterator<Integer> iterator = new GCompositeIterator<Integer>(listsIterator);

      for (int i = 1; i <= 6; i++) {
         assertEquals("i " + i, Integer.valueOf(i), iterator.next());
      }

      assertFalse("Exhausted Iterator", iterator.hasNext());
   }


   @Test
   public void testIterator3() {
      final List<Integer> list1 = new ArrayList<Integer>();
      list1.add(1);
      list1.add(2);
      list1.add(3);

      final List<Integer> list2 = new ArrayList<Integer>();
      list2.add(4);
      list2.add(5);

      final List<Integer> list3 = new ArrayList<Integer>();

      final List<Integer> list4 = new ArrayList<Integer>();
      list2.add(6);

      final List<List<Integer>> lists = new ArrayList<List<Integer>>();
      lists.add(list1);
      lists.add(list2);
      lists.add(list3);
      lists.add(list4);

      final List<Iterator<Integer>> listsIterator = new ArrayList<Iterator<Integer>>();
      listsIterator.add(list1.iterator());
      listsIterator.add(list2.iterator());
      listsIterator.add(list3.iterator());
      listsIterator.add(list4.iterator());

      final Iterator<Integer> iterator = new GCompositeIterator<Integer>(listsIterator);

      int i = 0;
      while (iterator.hasNext()) {
         i++;
         iterator.next();
      }

      assertEquals("i " + i, 6, i);
   }
}
