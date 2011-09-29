

package es.igosoftware.dmvc;

import org.jboss.netty.channel.Channel;

import es.igosoftware.dmvc.client.GDClient;
import es.igosoftware.dmvc.server.GDServer;


public interface IDSerializable {

   public Object objectToSerialize(final GDServer server);


   public Object objectToSerialize(final GDClient client);


   public IDSerializable materializeInServer(final Channel channel,
                                             final GDServer server);


   public IDSerializable materializeInClient(final Channel channel,
                                             final GDClient client);


}
