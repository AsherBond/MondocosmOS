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


package es.igosoftware.euclid.projection;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.DoubleByReference;


public class GProj4Library
         implements
            Library {


   static {
      // Native.setProtected(true);

      //Native.register(Platform.isWindows() ? "msvcrt" : "proj");
      Native.register("proj");
   }


   /**
    * Original signature : <code>int pj_transform(projPJ, projPJ, long, int, double*, double*, double*)</code><br>
    */
   //   public static native int pj_transform(final Pointer src,
   //                                         final Pointer dst,
   //                                         final NativeLong point_count,
   //                                         final int point_offset,
   //                                         final DoubleBuffer x,
   //                                         final DoubleBuffer y,
   //                                         final DoubleBuffer z);

   //   /**
   //    * Original signature : <code>int pj_transform(projPJ, projPJ, long, int, double*, double*, double*)</code><br>
   //    * 
   //    * pj_transform()</br> </br> int pj_transform( projPJ srcdefn, projPJ dstdefn, long point_count, int point_offset, double *x,
   //    * double *y, double *z );</br> </br> srcdefn: source (input) coordinate system.</br> </br> dstdefn: destination (output)
   //    * coordinate system.</br> </br> point_count: the number of points to be processed (the size of the x/y/z arrays).</br> </br>
   //    * point_offset: the step size from value to value (measured in doubles) within the x/y/z arrays - normally 1 for a packed
   //    * array. May be used to operate on xyz interleaved point arrays.</br> </br> x/y/z: The array of X, Y and Z coordinate values
   //    * passed as input, and modified in place for output. The Z may optionally be NULL.</br> </br> return: The return is zero on
   //    * success, or a PROJ.4 error code.</br> </br> The pj_transform() function transforms the passed in list of points from the
   //    * source coordinate system to the destination coordinate system. Note that geographic locations need to be passed in radians,
   //    * not decimal degrees, and will be returned similarly. The "z" array may be passed as NULL if Z values are not available.</br>
   //    * </br> If there is an overall failure, an error code will be returned from the function. If individual points fail to
   //    * transform - for instance due to being over the horizon - then those x/y/z values will be set to HUGE_VAL on return. Input
   //    * values that are HUGE_VAL will not be transformed.
   //    */
   //   public static native int pj_transform(final Pointer srcdefn,
   //                                         final Pointer dstdefn,
   //                                         final int point_count,
   //                                         final int point_offset,
   //                                         final double[] x,
   //                                         final double[] y,
   //                                         final double[] z);


   /**
    * Original signature : <code>int pj_transform(projPJ, projPJ, long, int, double*, double*, double*)</code><br>
    * 
    * pj_transform()</br> </br> int pj_transform( projPJ srcdefn, projPJ dstdefn, long point_count, int point_offset, double *x,
    * double *y, double *z );</br> </br> srcdefn: source (input) coordinate system.</br> </br> dstdefn: destination (output)
    * coordinate system.</br> </br> point_count: the number of points to be processed (the size of the x/y/z arrays).</br> </br>
    * point_offset: the step size from value to value (measured in doubles) within the x/y/z arrays - normally 1 for a packed
    * array. May be used to operate on xyz interleaved point arrays.</br> </br> x/y/z: The array of X, Y and Z coordinate values
    * passed as input, and modified in place for output. The Z may optionally be NULL.</br> </br> return: The return is zero on
    * success, or a PROJ.4 error code.</br> </br> The pj_transform() function transforms the passed in list of points from the
    * source coordinate system to the destination coordinate system. Note that geographic locations need to be passed in radians,
    * not decimal degrees, and will be returned similarly. The "z" array may be passed as NULL if Z values are not available.</br>
    * </br> If there is an overall failure, an error code will be returned from the function. If individual points fail to
    * transform - for instance due to being over the horizon - then those x/y/z values will be set to HUGE_VAL on return. Input
    * values that are HUGE_VAL will not be transformed.
    */
   public static native int pj_transform(final Pointer srcdefn,
                                         final Pointer dstdefn,
                                         final int point_count,
                                         final int point_offset,
                                         final DoubleByReference x,
                                         final DoubleByReference y,
                                         final DoubleByReference z);


   /**
    * Original signature: <code>void pj_free(projPJ)</code><br>
    */
   public static native void pj_free(final Pointer projPJ1);


   /**
    * Original signature: <code>projPJ pj_init_plus(const char*)</code><br>
    */
   public static native Pointer pj_init_plus(final String charPtr1);


   /**
    * Original signature: <code>char* pj_strerrno( int );</code><br/>
    * <br/>
    * Returns the error text associated with the passed in error code.
    */
   public static native String pj_strerrno(final int errorCode);


   public static native boolean pj_is_latlong(final Pointer proj);


   public static native boolean pj_is_geocent(final Pointer proj);


   public static void main(final String[] args) {
      System.out.println("GProj4Library 0.1");
      System.out.println("-----------------\n");

      final Pointer pj_src = pj_init_plus("+proj=utm +zone=29 +ellps=intl +units=m +no_defs");

      final Pointer pj_dest = pj_init_plus("+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs");

      final DoubleByReference x = new DoubleByReference(698590);
      final DoubleByReference y = new DoubleByReference(4374720);
      final DoubleByReference z = new DoubleByReference(315.75);

      final int error = pj_transform(pj_src, pj_dest, 1, 1, x, y, z);
      if (error < 0) {
         System.out.println("Error " + error + " " + pj_strerrno(error));
      }

      System.out.println(Math.toDegrees(y.getValue()));
      System.out.println(Math.toDegrees(x.getValue()));
      System.out.println(z.getValue());

      pj_free(pj_dest);
      pj_free(pj_src);
   }

}
