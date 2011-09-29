

package es.igosoftware.dmvc.transferring;

import java.io.File;

import es.igosoftware.io.GFileName;
import es.igosoftware.util.GProcessor;


public interface IDFileClient {


   /**
    * Get a file reference (or null) to a local copy of the file named fileName. If the local cache has not a copy of the file,
    * enqueue a download request que answer null.
    * 
    * @param fileName
    * @return
    */
   public File getFile(final GFileName fileName);


   /**
    * Evaluate processor with a file reference (nor null in case of errors) to a local copy of the file named fileName. If the
    * file named fileName is already cached, the processor will be executed inmediatly, otherwise a download request is enqued and
    * the processos will be evaluated asynchronously.
    * 
    * @param fileName
    * @param processor
    */
   public void getFile(final GFileName fileName,
                       final GProcessor<File> processor);


}
