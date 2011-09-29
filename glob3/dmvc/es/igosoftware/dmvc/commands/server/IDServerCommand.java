

package es.igosoftware.dmvc.commands.server;

import org.jboss.netty.channel.Channel;

import es.igosoftware.dmvc.commands.IDCommand;
import es.igosoftware.dmvc.server.GDServer;


public interface IDServerCommand
         extends
            IDCommand {


   public void evaluateInServer(final Channel channel,
                                final GDServer server);


}
