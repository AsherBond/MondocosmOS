

package es.igosoftware.dmvc.codec;

import java.io.StreamCorruptedException;
import java.util.zip.GZIPInputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;


public class GDZipObjectDecoder
         extends
            FrameDecoder {

   private final int _maxObjectSize;


   public GDZipObjectDecoder() {
      this(1048576);
   }


   public GDZipObjectDecoder(final int maxObjectSize) {
      if (maxObjectSize <= 0) {
         throw new IllegalArgumentException("_maxObjectSize: " + maxObjectSize);
      }

      _maxObjectSize = maxObjectSize;
   }


   @Override
   protected Object decode(final ChannelHandlerContext ctx,
                           final Channel channel,
                           final ChannelBuffer buffer) throws Exception {
      if (buffer.readableBytes() < 4) {
         return null;
      }

      final int dataLen = buffer.getInt(buffer.readerIndex());
      if (dataLen <= 0) {
         throw new StreamCorruptedException("invalid data length: " + dataLen);
      }
      if (dataLen > _maxObjectSize) {
         throw new StreamCorruptedException("data length too big: " + dataLen + " (max: " + _maxObjectSize + ')');
      }

      if (buffer.readableBytes() < dataLen + 4) {
         return null;
      }

      buffer.skipBytes(4);
      return new GCompactObjectInputStream(new GZIPInputStream(new ChannelBufferInputStream(buffer, dataLen))).readObject();
   }

}
