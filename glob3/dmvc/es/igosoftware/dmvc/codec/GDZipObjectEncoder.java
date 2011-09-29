

package es.igosoftware.dmvc.codec;

import java.io.ObjectOutputStream;
import java.util.zip.GZIPOutputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;


@ChannelPipelineCoverage("all")
public class GDZipObjectEncoder
         extends
            OneToOneEncoder {

   private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

   private final int           _estimatedLength;


   public GDZipObjectEncoder() {
      this(512);
   }


   public GDZipObjectEncoder(final int estimatedLength) {
      if (estimatedLength < 0) {
         throw new IllegalArgumentException("_estimatedLength: " + estimatedLength);
      }
      _estimatedLength = estimatedLength;
   }


   @Override
   protected Object encode(final ChannelHandlerContext ctx,
                           final Channel channel,
                           final Object msg) throws Exception {
      final ChannelBufferOutputStream bout = new ChannelBufferOutputStream(ChannelBuffers.dynamicBuffer(_estimatedLength,
               ctx.getChannel().getConfig().getBufferFactory()));
      bout.write(LENGTH_PLACEHOLDER);
      final ObjectOutputStream oout = new GCompactObjectOutputStream(new GZIPOutputStream(bout));
      oout.writeObject(msg);
      oout.flush();
      oout.close();

      final ChannelBuffer encoded = bout.buffer();
      encoded.setInt(0, encoded.writerIndex() - 4);
      return encoded;
   }

}
