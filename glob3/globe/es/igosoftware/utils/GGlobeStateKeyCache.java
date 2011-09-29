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


package es.igosoftware.utils;

import gov.nasa.worldwind.render.DrawContext;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * An utility class to implements cache per globe.getStateKey() changes.
 * 
 * @param <KeyT>
 * @param <ValueT>
 */
public class GGlobeStateKeyCache<KeyT, ValueT> {

   private static class Entry<ValueT> {
      private final Object                _stateKey;
      private final WeakReference<ValueT> _value;


      private Entry(final DrawContext dc,
                    final ValueT value) {
         _stateKey = dc.getGlobe().getStateKey(dc);
         _value = new WeakReference<ValueT>(value);
      }
   }


   public static interface Factory<KeyT, ValueT> {
      public ValueT create(final DrawContext dc,
                           final KeyT key);
   }


   private final GGlobeStateKeyCache.Factory<KeyT, ValueT>    _factory;
   private final Map<KeyT, GGlobeStateKeyCache.Entry<ValueT>> _values;


   public GGlobeStateKeyCache(final GGlobeStateKeyCache.Factory<KeyT, ValueT> factory) {
      _factory = factory;
      _values = new HashMap<KeyT, GGlobeStateKeyCache.Entry<ValueT>>();
   }

   private int _callCounter = 0;


   public synchronized ValueT get(final DrawContext dc,
                                  final KeyT key) {

      if (++_callCounter % 125 == 0) {
         cleanGarbage();
         _callCounter = 0;
      }


      final GGlobeStateKeyCache.Entry<ValueT> entry = _values.get(key);

      final Object stateKey = dc.getGlobe().getStateKey(dc);

      if ((entry != null) && entry._stateKey.equals(stateKey)) {
         // cache hit
         final ValueT value = entry._value.get();
         if (value != null) {
            return value;
         }
      }

      final ValueT newValue = _factory.create(dc, key);
      final GGlobeStateKeyCache.Entry<ValueT> newEntry = new GGlobeStateKeyCache.Entry<ValueT>(dc, newValue);
      _values.put(key, newEntry);
      return newValue;
   }


   private void cleanGarbage() {
      final ArrayList<KeyT> keysToRemove = new ArrayList<KeyT>();

      for (final Map.Entry<KeyT, Entry<ValueT>> entry : _values.entrySet()) {
         if (entry.getValue()._value.get() == null) {
            keysToRemove.add(entry.getKey());
         }
      }

      for (final KeyT keyToRemove : keysToRemove) {
         _values.remove(keyToRemove);
      }
   }

}
