

package es.igosoftware.dmvc.transferring;


import es.igosoftware.dmvc.model.IDAsynchronousExecutionListener;
import es.igosoftware.dmvc.model.IDModel;


public interface IDFileServer
         extends
            IDModel {

   public void getFile(final GDFileRequest request,
                       final IDAsynchronousExecutionListener<GDFileResponse, RuntimeException> listener);

}
