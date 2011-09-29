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


package es.igosoftware.scenegraph;

import es.igosoftware.euclid.GAngle;
import es.igosoftware.euclid.mutability.GMutableSupport;
import es.igosoftware.euclid.mutability.IMutable;
import es.igosoftware.euclid.vector.GVector3D;
import es.igosoftware.euclid.vector.IVector3;


public abstract class GMutableNodeAbstract<MutableT extends GMutableNodeAbstract<MutableT>>
         extends
            GNodeAbstract
         implements
            IMutableNode<MutableT> {

   private final GMutableSupport<MutableT> _mutableSupport;


   protected GMutableNodeAbstract(final String name,
                                  final GTransformationOrder order) {
      super(name, order);
      _mutableSupport = new GMutableSupport<MutableT>();
   }


   @Override
   public void addChangeListener(final IMutable.ChangeListener listener) {
      _mutableSupport.addChangeListener(listener);
   }


   @Override
   public void changed() {
      calculateLocalTransformMatrix();
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


   @Override
   public void setHeading(final GAngle heading) {
      checkMutable();

      if (_heading.equals(heading)) {
         return;
      }

      _heading = heading;
      cleanCaches();
      changed();
   }


   @Override
   public void setPitch(final GAngle pitch) {
      checkMutable();

      if (_pitch.equals(pitch)) {
         return;
      }

      _pitch = pitch;
      cleanCaches();
      changed();
   }


   @Override
   public void setRoll(final GAngle roll) {
      checkMutable();

      if (_roll.equals(roll)) {
         return;
      }

      _roll = roll;
      cleanCaches();
      changed();
   }


   @Override
   public void setScale(final double scale) {
      setScale(scale, scale, scale);
   }


   public void setScale(final double scaleX,
                        final double scaleY,
                        final double scaleZ) {
      checkMutable();

      final GVector3D newScale = new GVector3D(scaleX, scaleY, scaleZ);
      if (newScale.equals(_scale)) {
         return;
      }

      _scale = newScale;
      cleanCaches();
      changed();
   }


   @Override
   public void setTranslation(final IVector3 translation) {
      checkMutable();

      _translation = translation;
      cleanCaches();
      changed();
   }


   @SuppressWarnings("unchecked")
   @Override
   public void reparentTo(final GGroupNode parent) {
      final GGroupNode previousParent = getParent();
      if (previousParent != null) {
         previousParent.removeChild((MutableT) this);
      }

      if (parent != null) {
         parent.addChild((MutableT) this);
      }
   }

}
