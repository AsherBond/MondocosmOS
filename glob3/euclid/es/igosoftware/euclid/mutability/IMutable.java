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

/**
 * 
 * The Mutable objects supports change notification, and support to switch to immutable state.
 * 
 * When the receiver switch to immutable state, all the listeners will be automatically removed.
 * 
 * @author dgd
 * 
 */
public interface IMutable<MutableT extends IMutable<MutableT>> {

   public static interface ChangeListener {
      public void mutableChanged();
   }


   /**
    * Answer if the receiver is in mutable state
    * 
    * @return boolean
    */
   public boolean isMutable();


   /**
    * Throws an RunTimeException if the receiver is not in mutable state
    */
   public void checkMutable();


   /**
    * Makes the receiver to switch to immutable state.<br/>
    * <br/>
    * All the change listeners will be notified, and then removed (as it make no sense to keep change listener for an object that
    * will not change anymore)
    */
   public void makeImmutable();


   /**
    * The object (that is still in mutable state) was changed, notify all the change-listeners.<br/>
    * <br/>
    * Fires an {@link RuntimeException} if called in immutable state.
    */
   public void changed();


   /**
    * Add a new change-listener to the receiver.
    * 
    * @param listener
    *           The callback interface
    */
   public void addChangeListener(final IMutable.ChangeListener listener);


   /**
    * Removes the given listener (if present) from the notification list.
    * 
    * @param listener
    */
   public void removeChangeListener(final IMutable.ChangeListener listener);


   /**
    * Removes all the change listeners in a shot.
    */
   public void removeAllChangeListener();

}
