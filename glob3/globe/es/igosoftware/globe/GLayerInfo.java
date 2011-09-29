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


package es.igosoftware.globe;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import es.igosoftware.util.GAssert;


public class GLayerInfo
         implements
            ILayerInfo {


   public static List<ILayerInfo> createFromNames(final Icon icon,
                                                  final String... names) {
      if (names == null) {
         return null;
      }

      final ArrayList<ILayerInfo> result = new ArrayList<ILayerInfo>(names.length);
      for (final String name : names) {
         result.add(new GLayerInfo(name, icon));
      }

      return result;
   }


   public static List<ILayerInfo> createFromNames(final Icon icon,
                                                  final List<String> names) {
      if (names == null) {
         return null;
      }

      final ArrayList<ILayerInfo> result = new ArrayList<ILayerInfo>(names.size());
      for (final String name : names) {
         result.add(new GLayerInfo(name, icon));
      }

      return result;
   }


   private final String _name;
   private final Icon   _icon;


   //   public GLayerInfo(final String name) {
   //      this(name, null);
   //   }


   public GLayerInfo(final String name,
                     final Icon icon) {
      GAssert.notNull(name, "_name");

      _name = name;
      _icon = icon;
   }


   @Override
   public String getName() {
      return _name;
   }


   @Override
   public Icon getIcon() {
      return _icon;
   }


}
