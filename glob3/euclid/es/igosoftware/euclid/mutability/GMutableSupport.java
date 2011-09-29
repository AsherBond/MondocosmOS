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


package es.igosoftware.euclid.mutability;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class GMutableSupport<MutableT extends IMutable<MutableT>>
         implements
            IMutable<MutableT> {


   private boolean                                      _isMutable = true;
   private List<WeakReference<IMutable.ChangeListener>> _listeners = null;


   public GMutableSupport() {
   }


   @Override
   public void addChangeListener(final IMutable.ChangeListener listener) {
      if (_listeners == null) {
         _listeners = new ArrayList<WeakReference<IMutable.ChangeListener>>(1);
      }
      _listeners.add(new WeakReference<IMutable.ChangeListener>(listener));
   }


   @Override
   public void changed() {
      checkMutable();

      notifyListeners();
   }


   @Override
   public void checkMutable() {
      if (!isMutable()) {
         throw new RuntimeException("The receiver is immutable");
      }
   }


   @Override
   public boolean isMutable() {
      return _isMutable;
   }


   @Override
   public void makeImmutable() {
      checkMutable();

      _isMutable = false;
      notifyListeners();
      removeAllChangeListener();
   }


   private void notifyListeners() {
      if (_listeners == null) {
         return;
      }

      final List<WeakReference<IMutable.ChangeListener>> listenersCopy = new ArrayList<WeakReference<IMutable.ChangeListener>>(
               _listeners);
      for (final WeakReference<IMutable.ChangeListener> listenerWR : listenersCopy) {
         final IMutable.ChangeListener listener = listenerWR.get();
         if (listener != null) {
            listener.mutableChanged();
         }
      }
   }


   @Override
   public void removeAllChangeListener() {
      _listeners = null;
   }


   @Override
   public void removeChangeListener(final IMutable.ChangeListener listener) {
      if (_listeners == null) {
         return;
      }

      final List<WeakReference<IMutable.ChangeListener>> toRemove = new ArrayList<WeakReference<IMutable.ChangeListener>>(1);

      for (final WeakReference<IMutable.ChangeListener> currentListenerWR : _listeners) {
         final IMutable.ChangeListener currentListener = currentListenerWR.get();
         if ((currentListener == null) || currentListener.equals(listener)) {
            toRemove.add(currentListenerWR);
         }
      }

      if (!toRemove.isEmpty()) {
         _listeners.removeAll(toRemove);
      }

   }

}
