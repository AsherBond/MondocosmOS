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


package es.igosoftware.loading;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import es.igosoftware.io.GFileName;


public class GResourceRetriever {
   public static URL getResourceAsUrl(final GFileName fileName) throws MalformedURLException {
      if (fileName.isAbsolute()) {
         return new URL("file://" + File.separatorChar + fileName.buildPath());
      }

      return new URL("file://." + File.separatorChar + fileName.buildPath());
   }


   //   public static URL getResourceAsUrl(final String filename) throws MalformedURLException {
   //      URL result;
   //
   //      try {
   //         result = new URL(filename);
   //      }
   //      catch (final MalformedURLException e) {
   //         // When the string was not a valid URL, try to load it as a resource using
   //         // an anonymous class in the tree.
   //         final Object object = new Object() {
   //         };
   //         result = object.getClass().getClassLoader().getResource(filename);
   //
   //         if (result == null) {
   //            result = new URL("file", "localhost", filename);
   //         }
   //      }
   //
   //      return result;
   //   }


   //   public static InputStream getResourceAsInputStream(final String filename) throws IOException {
   //      URL result;
   //
   //      try {
   //         result = getResourceAsUrl(filename);
   //      }
   //      catch (final MalformedURLException e) {
   //         return new FileInputStream(filename);
   //      }
   //
   //      if (result == null) {
   //         return new FileInputStream(filename);
   //      }
   //      return result.openStream();
   //   }


}
