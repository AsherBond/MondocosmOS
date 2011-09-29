

package es.igosoftware.dmvc.commands.client;

import org.jboss.netty.channel.Channel;

import es.igosoftware.dmvc.client.GDClient;
import es.igosoftware.dmvc.commands.IDCommand;


public interface IDClientCommand
         extends
            IDCommand {


   public void evaluateInClient(final Channel channel,
                                final GDClient client);


   public boolean isSynchronousEvaluationAnswer();


}
