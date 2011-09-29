

package es.igosoftware.dmvc.server;

import java.lang.reflect.Method;

import org.jboss.netty.channel.Channel;

import es.igosoftware.dmvc.model.GDModel;


public interface IDEvaluationListener {

   //   public String getMethodName();


   //   public Class<?> getMethodClass();


   public void evaluation(final Channel channel,
                          final GDModel model,
                          final Method method,
                          final Object[] materializedArgs,
                          final Object result,
                          final Exception exception);

}
