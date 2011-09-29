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
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public final class LRUCache<KeyT, ValueT, ExceptionT extends Exception> {
   private static final long serialVersionUID = 1L;


   public static interface ValueFactory<KeyT, ValueT, ExceptionT extends Exception> {
      public ValueT create(final KeyT key) throws ExceptionT;
   }


   public static interface ValueVisitor<KeyT, ValueT, ExceptionT extends Exception> {
      public void visit(final KeyT key,
                        final ValueT value,
                        final ExceptionT exception);
   }


   public static interface ValuePredicate<KeyT, ValueT, ExceptionT extends Exception> {
      public boolean evaluate(final KeyT key,
                              final ValueT value,
                              final ExceptionT exception);
   }


   public static class Value<KeyT, ValueT, ExceptionT extends Exception> {

      public final KeyT       _key;
      public final ValueT     _value;
      public final ExceptionT _exception;


      private Value(final KeyT key,
                    final ValueT value,
                    final ExceptionT exception) {
         _key = key;
         _value = value;
         _exception = exception;
      }

   }


   public static class Entry<KeyT, ValueT, ExceptionT extends Exception> {
      private static final long serialVersionUID = 1L;

      private final KeyT        _key;
      private final int         _keyHashCode;
      private final ValueT      _value;
      private final ExceptionT  _exception;


      private Entry(final KeyT key,
                    final ValueT value,
                    final ExceptionT exception) {
         _key = key;
         _keyHashCode = key.hashCode();
         _value = value;
         _exception = exception;
      }


      @Override
      public String toString() {
         if (_exception != null) {
            return "[" + _key + " -> Exception: " + _exception + "]";
         }
         return "[" + _key + " -> " + _value + "]";
      }


      public KeyT getKey() {
         return _key;
      }


      public ValueT getValue() throws ExceptionT {
         if (_exception != null) {
            throw _exception;
         }
         return _value;
      }


      public ExceptionT getException() {
         return _exception;
      }
   }


   public static interface SizePolicy<KeyT, ValueT, ExceptionT extends Exception> {
      public boolean isOversized(final List<LRUCache.Entry<KeyT, ValueT, ExceptionT>> entries);
   }


   public final static class DefaultSizePolicy<KeyT, ValueT, ExceptionT extends Exception>
            implements
               SizePolicy<KeyT, ValueT, ExceptionT> {
      private static final long serialVersionUID = 1L;

      private final int         _maximumSize;


      public DefaultSizePolicy(final int maximumSize) {
         _maximumSize = maximumSize;
      }


      @Override
      public boolean isOversized(final List<Entry<KeyT, ValueT, ExceptionT>> entries) {
         return entries.size() > _maximumSize;
      }

   }


   private final SizePolicy<KeyT, ValueT, ExceptionT>                 _sizePolicy;
   private final LRUCache.ValueFactory<KeyT, ValueT, ExceptionT>      _factory;

   private final LinkedList<LRUCache.Entry<KeyT, ValueT, ExceptionT>> _entries;

   private final LRUCache.ValueVisitor<KeyT, ValueT, ExceptionT>      _entryRemovedVisitor;

   private int                                                        _hits       = 0;
   private int                                                        _misses     = 0;
   private int                                                        _callsCount = 0;
   private final int                                                  _statisticsInterval;


   public LRUCache(final int maximumSize,
                   final LRUCache.ValueFactory<KeyT, ValueT, ExceptionT> factory,
                   final int statisticsInterval) {
      this(new LRUCache.DefaultSizePolicy<KeyT, ValueT, ExceptionT>(maximumSize), factory, null, statisticsInterval);
   }


   public LRUCache(final int maximumSize,
                   final LRUCache.ValueFactory<KeyT, ValueT, ExceptionT> factory) {
      this(maximumSize, factory, null);
   }


   public LRUCache(final int maximumSize,
                   final LRUCache.ValueFactory<KeyT, ValueT, ExceptionT> factory,
                   final List<GTriplet<KeyT, ValueT, ExceptionT>> initialValues) {
      this(new LRUCache.DefaultSizePolicy<KeyT, ValueT, ExceptionT>(maximumSize), factory, initialValues);
   }


   public LRUCache(final LRUCache.SizePolicy<KeyT, ValueT, ExceptionT> sizePolicy,
                   final LRUCache.ValueFactory<KeyT, ValueT, ExceptionT> factory,
                   final LRUCache.ValueVisitor<KeyT, ValueT, ExceptionT> entryRemovedVisitor,
                   final int statisticsInterval) {
      this(sizePolicy, factory, entryRemovedVisitor, null, statisticsInterval);
   }


   public LRUCache(final LRUCache.SizePolicy<KeyT, ValueT, ExceptionT> sizePolicy,
                   final LRUCache.ValueFactory<KeyT, ValueT, ExceptionT> factory,
                   final int statisticsInterval) {
      this(sizePolicy, factory, null, null, statisticsInterval);
   }


   public LRUCache(final LRUCache.SizePolicy<KeyT, ValueT, ExceptionT> sizePolicy,
                   final LRUCache.ValueFactory<KeyT, ValueT, ExceptionT> factory) {
      this(sizePolicy, factory, null);
   }


   public LRUCache(final LRUCache.SizePolicy<KeyT, ValueT, ExceptionT> sizePolicy,
                   final LRUCache.ValueFactory<KeyT, ValueT, ExceptionT> factory,
                   final List<GTriplet<KeyT, ValueT, ExceptionT>> initialValues) {
      this(sizePolicy, factory, null, initialValues, 0);
   }


   public LRUCache(final LRUCache.SizePolicy<KeyT, ValueT, ExceptionT> sizePolicy,
                   final LRUCache.ValueFactory<KeyT, ValueT, ExceptionT> factory,
                   final LRUCache.ValueVisitor<KeyT, ValueT, ExceptionT> entryRemovedVisitor,
                   final List<GTriplet<KeyT, ValueT, ExceptionT>> initialValues,
                   final int statisticsInterval) {
      _sizePolicy = sizePolicy;
      _factory = factory;
      _entryRemovedVisitor = entryRemovedVisitor;

      if (initialValues == null) {
         _entries = new LinkedList<LRUCache.Entry<KeyT, ValueT, ExceptionT>>();
      }
      else {
         _entries = new LinkedList<Entry<KeyT, ValueT, ExceptionT>>();
         for (final GTriplet<KeyT, ValueT, ExceptionT> initialValue : initialValues) {
            _entries.add(new LRUCache.Entry<KeyT, ValueT, ExceptionT>(initialValue._first, initialValue._second,
                     initialValue._third));
         }
      }

      _statisticsInterval = statisticsInterval;
   }


   public synchronized boolean hasValue(final KeyT key) {
      final int keyHashCode = key.hashCode();
      for (final LRUCache.Entry<KeyT, ValueT, ExceptionT> entry : _entries) {
         if ((entry._keyHashCode == keyHashCode) && entry._key.equals(key)) {
            return true;
         }
      }

      return false;
   }


   public synchronized ValueT getValueOrNull(final KeyT key) throws ExceptionT {
      final int keyHashCode = key.hashCode();
      for (final LRUCache.Entry<KeyT, ValueT, ExceptionT> entry : _entries) {
         if ((entry._keyHashCode == keyHashCode) && entry._key.equals(key)) {
            return entry.getValue();
         }
      }

      return null;
   }


   public synchronized boolean moveUp(final KeyT key) {
      final int keyHashCode = key.hashCode();
      for (final LRUCache.Entry<KeyT, ValueT, ExceptionT> entry : _entries) {
         if ((entry._keyHashCode == keyHashCode) && entry._key.equals(key)) {
            _entries.remove(entry);
            _entries.addFirst(entry);
            return true;
         }
      }

      return false;
   }


   @SuppressWarnings("unchecked")
   public synchronized ValueT get(final KeyT key) throws ExceptionT {
      _callsCount++;

      final boolean showStatistics = (_statisticsInterval != 0) && (_callsCount % _statisticsInterval == 0);

      final int keyHashCode = key.hashCode();
      for (final LRUCache.Entry<KeyT, ValueT, ExceptionT> entry : _entries) {
         if ((entry._keyHashCode == keyHashCode) && entry._key.equals(key)) {
            _entries.remove(entry);
            _entries.addFirst(entry);

            _hits++;

            if (showStatistics) {
               showStatistics();
            }

            return entry.getValue();
         }
      }

      _misses++;

      ValueT value = null;
      ExceptionT exception = null;
      try {
         value = _factory.create(key);
      }
      catch (final Exception e) {
         exception = (ExceptionT) e;
      }

      final LRUCache.Entry<KeyT, ValueT, ExceptionT> newEntry = new LRUCache.Entry<KeyT, ValueT, ExceptionT>(key, value,
               exception);

      while (_sizePolicy.isOversized(Collections.unmodifiableList(_entries))) {
         entryRemoved(_entries.removeLast());
      }
      //      if (!_entries.isEmpty()) {
      //         entryRemoved(_entries.removeLast());
      //      }

      _entries.addFirst(newEntry);

      if (showStatistics) {
         showStatistics();
      }

      return newEntry.getValue();
   }


   private void entryRemoved(final Entry<KeyT, ValueT, ExceptionT> entry) {
      if (_entryRemovedVisitor != null) {
         _entryRemovedVisitor.visit(entry._key, entry._value, entry._exception);
      }
   }


   public void showStatistics() {
      System.out.println("-----------------------------------------------------------");
      System.out.println("Cache Statistics:");
      System.out.println("  Size: " + _entries.size());
      System.out.println("  Calls: " + getCallsCount());
      System.out.println("  Hits: " + getHitsCount() + " (" + GMath.roundTo(100 * getHitsRatio(), 2) + "%)");
      System.out.println("-----------------------------------------------------------");
   }


   public int getCallsCount() {
      return _callsCount;
   }


   public double getHitsRatio() {
      return (double) _hits / getCallsCount();
   }


   public int getHitsCount() {
      return _hits;
   }


   public synchronized List<LRUCache.Value<KeyT, ValueT, ExceptionT>> getValues() {
      final List<LRUCache.Value<KeyT, ValueT, ExceptionT>> result = new ArrayList<LRUCache.Value<KeyT, ValueT, ExceptionT>>(
               _entries.size());

      for (final LRUCache.Entry<KeyT, ValueT, ExceptionT> entry : _entries) {
         result.add(new LRUCache.Value<KeyT, ValueT, ExceptionT>(entry._key, entry._value, entry._exception));
      }

      return Collections.unmodifiableList(result);
   }


   public synchronized void visitValues(final LRUCache.ValueVisitor<KeyT, ValueT, ExceptionT> visitor) {
      for (final LRUCache.Entry<KeyT, ValueT, ExceptionT> entry : _entries) {
         visitor.visit(entry._key, entry._value, entry._exception);
      }
   }


   public synchronized void clear() {
      for (final Entry<KeyT, ValueT, ExceptionT> entry : _entries) {
         entryRemoved(entry);
      }

      _entries.clear();
   }


   public synchronized void clear(final LRUCache.ValuePredicate<KeyT, ValueT, ExceptionT> predicate) {
      final Iterator<Entry<KeyT, ValueT, ExceptionT>> iterator = _entries.iterator();

      while (iterator.hasNext()) {
         final LRUCache.Entry<KeyT, ValueT, ExceptionT> entry = iterator.next();
         if (predicate.evaluate(entry._key, entry._value, entry._exception)) {
            iterator.remove();
            entryRemoved(entry);
         }
      }
   }

}
