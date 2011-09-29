

package es.igosoftware.dmvc.model;

import java.io.Serializable;
import java.lang.reflect.Method;

import es.igosoftware.dmvc.IDSerializable;


public interface IDProperty
         extends
            IDSerializable,
            Serializable {


   public Object get();


   public void set(final Object value) throws Exception;


   public void validate(final GDModel model);


   public void justMaterializedInClient(final GDRemoteModel remoteModel);


   public Method getPropertyMethod(final String methodName,
                                   final Object[] args);


   public String getName();

}
