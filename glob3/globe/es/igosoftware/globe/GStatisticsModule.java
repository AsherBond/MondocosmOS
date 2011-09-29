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

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import es.igosoftware.util.GPair;
import gov.nasa.worldwind.util.StatisticsPanel;


public class GStatisticsModule
         extends
            GAbstractGlobeModule {


   public GStatisticsModule(final IGlobeRunningContext context) {
      super(context);
   }


   @Override
   public String getName() {
      return "Running Statistics";
   }


   @Override
   public String getDescription() {
      return "Statistics";
   }


   @Override
   public String getVersion() {
      return "0.1";
   }


   @Override
   public List<GPair<String, Component>> getPanels(final IGlobeRunningContext context) {
      final ArrayList<GPair<String, Component>> panels = new ArrayList<GPair<String, Component>>();

      panels.add(new GPair<String, Component>("Statistics", new StatisticsPanel(
               context.getWorldWindModel().getWorldWindowGLCanvas(), new Dimension(250, 500))));

      return panels;
   }


   @Override
   public void initializeTranslations(final IGlobeRunningContext context) {
      final IGlobeTranslator translator = context.getTranslator();
      translator.addTranslation("es", "Statistics", "Estadísticas");
      translator.addTranslation("de", "Statistics", "Statistik");
   }

}
