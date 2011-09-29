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

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;

import es.igosoftware.globe.IGlobeRunningContext;


public abstract class GCheckBoxGenericAction
         extends
            GGenericAction {
   private boolean _value;
   private Action  _action;


   protected GCheckBoxGenericAction(final String label,
                                    final Icon icon,
                                    final IGenericAction.MenuArea menuBarArea,
                                    final boolean showOnToolBar,
                                    final boolean initialValue) {
      this(label, ' ', icon, menuBarArea, showOnToolBar, initialValue);
   }


   protected GCheckBoxGenericAction(final String label,
                                    final char mnemonic,
                                    final Icon icon,
                                    final MenuArea menuBarArea,
                                    final boolean showOnToolBar,
                                    final boolean initialValue) {
      super(label, mnemonic, icon, menuBarArea, showOnToolBar);

      _value = initialValue;
   }


   private Action createAction(final String label,
                               final Icon icon,
                               final boolean initialValue) {
      final Action action = new AbstractAction(label, icon) {
         private static final long serialVersionUID = 1L;


         @Override
         public void actionPerformed(final ActionEvent e) {
            _value = !_value;
            putValue(Action.SELECTED_KEY, _value);

            execute();
         }
      };

      final char mnemonic = getMnemonic();
      if (mnemonic != ' ') {
         action.putValue(Action.MNEMONIC_KEY, Integer.valueOf(mnemonic));
      }

      //      action.setEnabled(isEnabled());
      action.putValue(Action.SELECTED_KEY, initialValue);
      return action;
   }


   private Action getAction(final IGlobeRunningContext context) {
      if (_action == null) {
         _action = createAction(context.getTranslator().getTranslation(getLabel()), getIcon(), _value);
      }
      return _action;
   }


   @Override
   public JMenuItem createMenuWidget(final IGlobeRunningContext context) {
      return new JCheckBoxMenuItem(getAction(context));
   }


   @Override
   public Component createToolbarWidget(final IGlobeRunningContext context) {
      return GSwingFactory.createToolbarCheckBox(getAction(context));
   }


   public boolean isSelected() {
      return _value;
   }


}
