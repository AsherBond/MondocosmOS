

package es.igosoftware.util;

import java.util.Iterator;

import junit.framework.TestCase;


public class GArrayBackedListTest
         extends
            TestCase {

   public void testIterator() throws Exception {
      final GArrayBackedList<String> list = new GArrayBackedList<String>("a");
      final Iterator<String> it = list.iterator();
      String testValue = "a";
      while (it.hasNext()) {
         final String n = it.next();
         assertTrue(n.equals(testValue));
         testValue = null;
      }
   }


   public void testGet() throws Exception {
      final GArrayBackedList<String> list = new GArrayBackedList<String>("a", "b");
      assertTrue(list.get(0).equals("a"));
      assertTrue(list.get(1).equals("b"));
   }


   public void testContains() throws Exception {
      final GArrayBackedList<String> list = new GArrayBackedList<String>("a", "b");
      assertTrue(list.contains("a"));
      assertTrue(list.contains("b"));
      assertTrue(!list.contains("c"));

      assertTrue(list.containsAll(new GArrayBackedList<String>("a", "b")));
      assertTrue(list.containsAll(new GArrayBackedList<String>("a")));
   }


   public void testIsEmpty() throws Exception {
      assertTrue(new GArrayBackedList<String>().isEmpty());
      assertTrue(!new GArrayBackedList<String>("a").isEmpty());
   }


   public void testIndexOf() throws Exception {
      final GArrayBackedList<String> list = new GArrayBackedList<String>("a", "b");
      assertTrue(list.indexOf("a") == 0);
      assertTrue(list.indexOf("b") == 1);
   }


   public void testSize() throws Exception {
      assertTrue(new GArrayBackedList<String>("a", "b").size() == 2);
   }


   public void testModifyingOperationsUnsupported() throws Exception {
      final GArrayBackedList<String> list = new GArrayBackedList<String>("a");

      try {
         list.add("b");
         fail();
      }
      catch (final UnsupportedOperationException e) {}
      try {
         list.add(0, "b");
         fail();
      }
      catch (final UnsupportedOperationException e) {}
      try {
         list.addAll(list);
         fail();
      }
      catch (final UnsupportedOperationException e) {}
      try {
         list.addAll(0, list);
         fail();
      }
      catch (final UnsupportedOperationException e) {}
      try {
         list.clear();
         fail();
      }
      catch (final UnsupportedOperationException e) {}
      try {
         list.remove(0);
         fail();
      }
      catch (final UnsupportedOperationException e) {}
      try {
         list.remove("a");
         fail();
      }
      catch (final UnsupportedOperationException e) {}
      try {
         list.removeAll(list);
         fail();
      }
      catch (final UnsupportedOperationException e) {}
      try {
         list.retainAll(new GArrayBackedList<String>());
         fail();
      }
      catch (final UnsupportedOperationException e) {}
   }
}
