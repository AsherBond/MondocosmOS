

package es.igosoftware.dmvc.model;

public interface IDAsynchronousExecutionListener<ResultT, ExceptionT extends Exception> {

   public void evaluated(final ResultT result,
                         final ExceptionT exception);

}
