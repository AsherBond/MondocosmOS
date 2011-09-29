

package es.unex.s3xtante.modules.sextante.bindings;

import es.unex.sextante.core.GeoAlgorithm;
import es.unex.sextante.gui.core.IPostProcessTaskFactory;


public class GlobePostProcessTaskFactory
         implements
            IPostProcessTaskFactory {

   @Override
   public Runnable getPostProcessTask(final GeoAlgorithm alg,
                                      final boolean bShowResultsDialog) {

      return new GlobePostProcessTask(alg, bShowResultsDialog);

   }

}
