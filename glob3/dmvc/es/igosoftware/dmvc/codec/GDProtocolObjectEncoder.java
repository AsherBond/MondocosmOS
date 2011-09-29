

package es.igosoftware.dmvc.codec;


import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import es.igosoftware.io.GIOUtils;
import es.igosoftware.protocol.GProtocolMultiplexor;
import es.igosoftware.protocol.IProtocolObject;


@ChannelPipelineCoverage("all")
public class GDProtocolObjectEncoder
         extends
            OneToOneEncoder {

   private static final byte[]        LENGTH_PLACEHOLDER = new byte[4];

   private final int                  _estimatedLength;
   private final GProtocolMultiplexor _multiplexor;


   public GDProtocolObjectEncoder(final GProtocolMultiplexor multiplexor) {
      this(512, multiplexor);
   }


   public GDProtocolObjectEncoder(final int estimatedLength,
                                  final GProtocolMultiplexor multiplexor) {
      if (estimatedLength < 0) {
         throw new IllegalArgumentException("_estimatedLength: " + estimatedLength);
      }
      _estimatedLength = estimatedLength;
      _multiplexor = multiplexor;
   }


   @Override
   protected Object encode(final ChannelHandlerContext ctx,
                           final Channel channel,
                           final Object msg) throws Exception {

      final IProtocolObject object = (IProtocolObject) msg;

      ChannelBufferOutputStream bout = null;
      try {
         bout = new ChannelBufferOutputStream(ChannelBuffers.dynamicBuffer(_estimatedLength,
                  ctx.getChannel().getConfig().getBufferFactory()));
         bout.write(LENGTH_PLACEHOLDER);

         final byte[] rawBytes = _multiplexor.getProtocolBytes(object);
         final byte[] compressedBytes = GIOUtils.compress(rawBytes);

         //      final int TODO_Remove_Print;
         //      System.out.println("-----> sending message, raw " + rawBytes.length + ", compressed " + compressedBytes.length);

         final int compressedSign; // the message length is in negative to flag the contents is compressed 
         if (compressedBytes.length < rawBytes.length) {
            compressedSign = -1;
            bout.write(compressedBytes);
         }
         else {
            compressedSign = 1;
            bout.write(rawBytes);
         }

         final ChannelBuffer encoded = bout.buffer();
         encoded.setInt(0, (encoded.writerIndex() - 4) * compressedSign);
         return encoded;
      }
      finally {
         GIOUtils.gentlyClose(bout);
      }
   }


}
