/*
 * Copyright 2009 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */


package es.igosoftware.dmvc.server;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;

import es.igosoftware.dmvc.GDHandler;
import es.igosoftware.dmvc.commands.IDCommand;
import es.igosoftware.dmvc.commands.client.GDInitializeClientCommand;
import es.igosoftware.dmvc.commands.server.GDCompositeServerCommand;
import es.igosoftware.dmvc.commands.server.IDServerCommand;


@ChannelPipelineCoverage("all")
public class GDServerHandler
         extends
            GDHandler {

   private final GDServer _server;


   public GDServerHandler(final GDServer server) {
      _server = server;
   }


   @Override
   public void channelConnected(final ChannelHandlerContext ctx,
                                final ChannelStateEvent e) {
      super.channelConnected(ctx, e);

      final Channel channel = e.getChannel();
      final int sessionID = channel.getId().intValue();


      _server.channelConnected(channel, sessionID);

      channel.getCloseFuture().addListener(new ChannelFutureListener() {
         @Override
         public void operationComplete(final ChannelFuture future) throws Exception {
            //            remove(future.getChannel());
            _server.channelClosed(channel, sessionID);
         }
      });

      final IDCommand initializeClientCommand = new GDInitializeClientCommand(sessionID, _server);
      sendCommand(channel, initializeClientCommand);
   }


   @Override
   public void messageReceived(final ChannelHandlerContext ctx,
                               final MessageEvent e) {
      super.messageReceived(ctx, e);

      final IDServerCommand command = (IDServerCommand) e.getMessage();

      if (command.isComposite()) {
         final GDCompositeServerCommand composite = (GDCompositeServerCommand) command;

         for (final IDServerCommand child : composite.getChildren()) {
            processCommand(e, child);
         }
      }
      else {
         processCommand(e, command);
      }
   }


   private void processCommand(final MessageEvent e,
                               final IDServerCommand command) {
      command.evaluateInServer(e.getChannel(), _server);
   }


}
