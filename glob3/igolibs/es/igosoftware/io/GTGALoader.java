/*

 IGO Software SL  -  info@igosoftware.es

 http://www.glob3.org

-------------------------------------------------------------------------------
 Copyright (c) 2010, IGO Software SL
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
     * Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.
     * Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.
     * Neither the name of the IGO Software SL nor the
       names of its contributors may be used to endorse or promote products
       derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL IGO Software SL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-------------------------------------------------------------------------------

*/


package es.igosoftware.io;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;


public class GTGALoader {
   private static final byte[] uTGAcompare = new byte[] {
                     0,
                     0,
                     2,
                     0,
                     0,
                     0,
                     0,
                     0,
                     0,
                     0,
                     0,
                     0
                                           };
   private static final byte[] cTGAcompare = new byte[] {
                     0,
                     0,
                     10,
                     0,
                     0,
                     0,
                     0,
                     0,
                     0,
                     0,
                     0,
                     0
                                           };


   private static int unsignedByteToInt(final byte b) {
      return (b & 0xFF);
   }


   private void readBuffer(final InputStream in,
                           final byte[] buffer) throws IOException {
      int bytesRead = 0;
      int bytesToRead = buffer.length;
      while (bytesToRead > 0) {
         final int read = in.read(buffer, bytesRead, bytesToRead);
         bytesRead += read;
         bytesToRead -= read;
      }
   }


   private BufferedImage loadUncompressedTGA(final InputStream in) throws IOException {
      final byte[] header = new byte[6];
      readBuffer(in, header);

      final int imageHeight = (unsignedByteToInt(header[3]) << 8) + unsignedByteToInt(header[2]);
      final int imageWidth = (unsignedByteToInt(header[1]) << 8) + unsignedByteToInt(header[0]);
      final int bpp = unsignedByteToInt(header[4]);

      if ((imageWidth <= 0) || (imageHeight <= 0) || ((bpp != 24) && (bpp != 32))) {
         throw new IOException("Invalid texture information");
      }

      final int bytesPerPixel = (bpp / 8);
      final int imageSize = (bytesPerPixel * imageWidth * imageHeight);
      final byte imageData[] = new byte[imageSize];

      readBuffer(in, imageData);

      final BufferedImage bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

      for (int j = 0; j < imageHeight; j++) {
         for (int i = 0; i < imageWidth; i++) {
            final int index = ((imageHeight - 1 - j) * imageWidth + i) * bytesPerPixel;

            final int alpha = (imageData[index + 3] & 0xFF) << 24;
            final int red = (imageData[index + 0] & 0xFF) << 16;
            final int green = (imageData[index + 1] & 0xFF) << 8;
            final int blue = (imageData[index + 2] & 0xFF);
            final int value = alpha | red | green | blue;

            bufferedImage.setRGB(i, j, value);
         }
      }

      return (bufferedImage);
   }


   private BufferedImage loadCompressedTGA(final InputStream fTGA) throws IOException {
      final byte[] header = new byte[6];
      readBuffer(fTGA, header);

      final int imageHeight = (unsignedByteToInt(header[3]) << 8) + unsignedByteToInt(header[2]);
      final int imageWidth = (unsignedByteToInt(header[1]) << 8) + unsignedByteToInt(header[0]);
      final int bpp = unsignedByteToInt(header[4]);

      if ((imageWidth <= 0) || (imageHeight <= 0) || ((bpp != 24) && (bpp != 32))) {
         throw new IOException("Invalid texture information");
      }

      final int bytesPerPixel = (bpp / 8);
      final int imageSize = (bytesPerPixel * imageWidth * imageHeight);
      final byte imageData[] = new byte[imageSize];
      final int pixelCount = imageHeight * imageWidth;

      int currentByte = 0;
      int currentPixel = 0;
      final byte[] colorBuffer = new byte[bytesPerPixel];

      do {
         int chunkheader = 0;
         try {
            chunkheader = unsignedByteToInt((byte) fTGA.read());
         }
         catch (final IOException e) {
            throw new IOException("Could not read RLE header");
         }

         // If the ehader is < 128, it means the that is the number of RAW
         // color packets minus 1
         if (chunkheader < 128) {
            // add 1 to get number of following color values
            chunkheader++;

            // Read RAW color values
            for (short counter = 0; counter < chunkheader; counter++) {
               readBuffer(fTGA, colorBuffer);

               // write to memory

               // Flip R and B vcolor values around in the process
               imageData[currentByte] = colorBuffer[2];

               imageData[currentByte + 1] = colorBuffer[1];
               imageData[currentByte + 2] = colorBuffer[0];

               // if its a 32 bpp image
               if (bytesPerPixel == 4) {
                  // copy the 4th byte
                  imageData[currentByte + 3] = colorBuffer[3];
               }

               currentByte += bytesPerPixel;

               currentPixel++;

               if (currentPixel > pixelCount) {
                  throw new IOException("Too many pixels read");
               }
            }
         }
         // chunkheader > 128 RLE data, next color reapeated chunkheader -
         // 127 times
         else {
            // Subteact 127 to get rid of the ID bit
            chunkheader -= 127;

            readBuffer(fTGA, colorBuffer);

            // copy the color into the image data as many times as dictated
            for (short counter = 0; counter < chunkheader; counter++) {
               // switch R and B bytes areound while copying
               imageData[currentByte] = colorBuffer[2];

               imageData[currentByte + 1] = colorBuffer[1];
               imageData[currentByte + 2] = colorBuffer[0];

               // If TGA images is 32 bpp
               if (bytesPerPixel == 4) {
                  // Copy 4th byte
                  imageData[currentByte + 3] = colorBuffer[3];
               }

               currentByte += bytesPerPixel;

               currentPixel++;

               // Make sure we haven't written too many pixels
               if (currentPixel > pixelCount) {
                  // if there is too many... Display an error!
                  throw new IOException("Too many pixels read");
               }
            }
         }
      }
      while (currentPixel < pixelCount); // Loop while there are still pixels left

      final BufferedImage bufferedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

      for (int j = 0; j < imageHeight; j++) {
         for (int i = 0; i < imageWidth; i++) {
            final int index = ((imageHeight - 1 - j) * imageWidth + i) * bytesPerPixel;

            final int alpha = (imageData[index + 3] & 0xFF) << 24;
            final int red = (imageData[index + 0] & 0xFF) << 16;
            final int green = (imageData[index + 1] & 0xFF) << 8;
            final int blue = (imageData[index + 2] & 0xFF);
            final int value = alpha | red | green | blue;

            bufferedImage.setRGB(i, j, value);
         }
      }

      return (bufferedImage);
   }


   public BufferedImage loadImage(final URL url) throws IOException {
      final InputStream is = new BufferedInputStream(url.openStream());
      final BufferedImage image = loadImage(is);
      is.close();
      return image;
   }


   public BufferedImage loadImage(final String url) throws IOException {
      final InputStream is = new BufferedInputStream(new FileInputStream(url));
      final BufferedImage image = loadImage(is);
      is.close();
      return image;
   }


   public BufferedImage loadImage(final InputStream in) throws IOException {
      final byte[] header = new byte[12];

      readBuffer(in, header);

      if (Arrays.equals(uTGAcompare, header)) {
         return loadUncompressedTGA(in);
      }

      if (Arrays.equals(cTGAcompare, header)) {
         return loadCompressedTGA(in);
      }

      throw new IOException("TGA must be type 2 or type 10 ");
   }


   public GTGALoader() {
   }
}
