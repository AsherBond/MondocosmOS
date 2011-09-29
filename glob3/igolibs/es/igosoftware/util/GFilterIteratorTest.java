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


public class GFilterIteratorTest {

   @Test
   public void testEmptyIterator() {
      final List<Integer> list = new ArrayList<Integer>();

      final Iterator<Integer> iterator = new GFilterIterator<Integer>(list.iterator(), new GPredicate<Integer>() {
         @Override
         public boolean evaluate(final Integer object) {
            return false;
         }
      });

      assertFalse("Exhausted Iterator", iterator.hasNext());
   }


   @Test
   public void testFullyFilteredIterator() {
      final List<Integer> list = new ArrayList<Integer>();
      list.add(1);
      list.add(2);
      list.add(3);

      final Iterator<Integer> iterator = new GFilterIterator<Integer>(list.iterator(), new GPredicate<Integer>() {
         @Override
         public boolean evaluate(final Integer integer) {
            return false;
         }
      });

      assertFalse("Exhausted Iterator", iterator.hasNext());
   }


   @Test
   public void testOddFilteredIterator() {
      final List<Integer> list = new ArrayList<Integer>();
      list.add(1);
      list.add(2);
      list.add(3);
      list.add(4);

      final Iterator<Integer> iterator = new GFilterIterator<Integer>(list.iterator(), new GPredicate<Integer>() {
         @Override
         public boolean evaluate(final Integer integer) {
            return (integer % 2) != 0;
         }
      });

      final List<Integer> result = new ArrayList<Integer>();
      while (iterator.hasNext()) {
         result.add(iterator.next());
      }

      final List<Integer> expected = new ArrayList<Integer>();
      expected.add(1);
      expected.add(3);

      assertEquals(expected, result);
   }


   @Test
   public void testEvenFilteredIterator() {
      final List<Integer> list = new ArrayList<Integer>();
      list.add(1);
      list.add(2);
      list.add(3);
      list.add(4);

      final Iterator<Integer> iterator = new GFilterIterator<Integer>(list.iterator(), new GPredicate<Integer>() {
         @Override
         public boolean evaluate(final Integer integer) {
            return (integer % 2) == 0;
         }
      });

      final List<Integer> result = new ArrayList<Integer>();
      while (iterator.hasNext()) {
         result.add(iterator.next());
      }

      final List<Integer> expected = new ArrayList<Integer>();
      expected.add(2);
      expected.add(4);

      assertEquals(expected, result);
   }
}
