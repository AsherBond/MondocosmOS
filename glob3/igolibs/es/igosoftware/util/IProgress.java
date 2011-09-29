

package es.igosoftware.util;


public interface IProgress {


   public void finish();


   public void stepDone();


   public void stepsDone(final long steps);


}
