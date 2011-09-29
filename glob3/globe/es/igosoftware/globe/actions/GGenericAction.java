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


package es.igosoftware.globe.actions;

import javax.swing.Icon;

import es.igosoftware.globe.GGlobeComponent;


public abstract class GGenericAction
         extends
            GGlobeComponent
         implements
            IGenericAction {


   private final String                  _label;
   private final char                    _mnemonic;
   private final Icon                    _icon;
   private final IGenericAction.MenuArea _menuBarArea;
   private final boolean                 _showOnToolBar;


   protected GGenericAction(final String label,
                            final Icon icon,
                            final IGenericAction.MenuArea menuBarArea,
                            final boolean showOnToolBar) {
      this(label, ' ', icon, menuBarArea, showOnToolBar);
   }


   protected GGenericAction(final String label,
                            final char mnemonic,
                            final Icon icon,
                            final IGenericAction.MenuArea menuBarArea,
                            final boolean showOnToolBar) {
      super();
      if ((label == null) && (icon == null)) {
         throw new IllegalArgumentException("Label and/or icon are needed");
      }

      _label = label;
      _mnemonic = mnemonic;
      _icon = icon;
      _menuBarArea = menuBarArea;
      _showOnToolBar = showOnToolBar;
   }


   @Override
   public MenuArea getMenuBarArea() {
      return _menuBarArea;
   }


   @Override
   public boolean isShowOnToolBar() {
      return _showOnToolBar;
   }


   @Override
   public Icon getIcon() {
      return _icon;
   }


   @Override
   public String getLabel() {
      return _label;
   }


   @Override
   public char getMnemonic() {
      return _mnemonic;
   }


   @Override
   public boolean isVisible() {
      return true;
   }


}
