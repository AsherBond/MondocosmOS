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


public class GMutableAbstract<MutableT extends GMutableAbstract<MutableT>>
         implements
            IMutable<MutableT> {

   //   private final boolean                                      _isMutable = true;
   //   private final List<WeakReference<IMutable.ChangeListener>> _listeners = null;
   private final GMutableSupport<MutableT> _mutableSupport;


   protected GMutableAbstract() {
      _mutableSupport = new GMutableSupport<MutableT>();
   }


   @Override
   public void addChangeListener(final IMutable.ChangeListener listener) {
      _mutableSupport.addChangeListener(listener);
   }


   @Override
   public void changed() {
      _mutableSupport.changed();
   }


   @Override
   public boolean isMutable() {
      return _mutableSupport.isMutable();
   }


   @Override
   public void checkMutable() {
      _mutableSupport.checkMutable();
   }


   @Override
   public void makeImmutable() {
      _mutableSupport.makeImmutable();
   }


   @Override
   public void removeAllChangeListener() {
      _mutableSupport.removeAllChangeListener();
   }


   @Override
   public void removeChangeListener(final IMutable.ChangeListener listener) {
      _mutableSupport.removeChangeListener(listener);
   }

   //   public static void main(final String[] args) {
   //      class Mutable
   //               extends
   //                  GMutableAbstract<Mutable> {
   //
   //      }
   //
   //      final Mutable mutable = new Mutable();
   //      System.out.println(mutable.isMutable());
   //
   //      mutable.addChangeListener(new IMutable.ChangeListener<Mutable>() {
   //         @Override
   //         public void changed(final Mutable mutable1) {
   //            System.out.println(mutable1 + " has changed (listener 1)");
   //         }
   //      });
   //
   //      mutable.addChangeListener(new IMutable.ChangeListener<Mutable>() {
   //         @Override
   //         public void changed(final Mutable mutable1) {
   //            System.out.println(mutable1 + " has changed (listener 2)");
   //         }
   //      });
   //
   //      mutable.changed();
   //      System.out.println(mutable.isMutable());
   //
   //      mutable.makeImmutable();
   //      System.out.println(mutable.isMutable());
   //      //      mutable.changed();
   //
   //      // System.out.println(mutable._listeners);
   //   }

}
