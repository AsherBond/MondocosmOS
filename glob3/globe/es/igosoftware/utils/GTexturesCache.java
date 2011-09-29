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


package es.igosoftware.utils;

import java.net.URL;

import es.igosoftware.util.GPair;
import es.igosoftware.util.LRUCache;


public class GTexturesCache {


   private final LRUCache<GPair<URL, Boolean>, GTexture, RuntimeException> _cache;
   private final boolean                                                   _compressTextures;


   public GTexturesCache(final boolean compressTexture) {
      this(128, compressTexture);
   }


   public GTexturesCache(final int size,
                         final boolean compressTextures) {
      _compressTextures = compressTextures;
      _cache = initializeCache(size);
   }


   private LRUCache<GPair<URL, Boolean>, GTexture, RuntimeException> initializeCache(final int size) {
      return new LRUCache<GPair<URL, Boolean>, GTexture, RuntimeException>(
               new LRUCache.DefaultSizePolicy<GPair<URL, Boolean>, GTexture, RuntimeException>(size),
               new LRUCache.ValueFactory<GPair<URL, Boolean>, GTexture, RuntimeException>() {
                  @Override
                  public GTexture create(final GPair<URL, Boolean> key) {
                     final URL url = key._first;
                     final boolean mipmap = key._second;

                     return GTextureLoader.loadTexture(url, mipmap, _compressTextures);
                  }
               }, 0);
   }


   public GTexture getTexture(final URL url,
                              final boolean mipmap) {
      if (url == null) {
         return null;
      }

      final GPair<URL, Boolean> textureKey = new GPair<URL, Boolean>(url, mipmap);
      synchronized (_cache) {
         return _cache.get(textureKey);
      }
   }


   public synchronized void dispose() {
      synchronized (_cache) {
         _cache.visitValues(new LRUCache.ValueVisitor<GPair<URL, Boolean>, GTexture, RuntimeException>() {
            @Override
            public void visit(final GPair<URL, Boolean> key,
                              final GTexture texture,
                              final RuntimeException exception) {
               if (texture != null) {
                  texture.dispose();
               }
            }
         });

         _cache.clear();
      }
   }


}
