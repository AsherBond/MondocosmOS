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


package es.igosoftware.util;


public class GKahanSummer {
   private double _sum          = 0;
   private double _compensation = 0;


   /**
    * Return the current corrected value of the running sum.
    * 
    * @return the running sum's value
    */
   public double value() {
      return _sum + _compensation;
   }


   /**
    * Add the value of an addend to the running sum.
    * 
    * @param the
    *           addend value
    */
   public void add(final double addend) {
      // Correct the addend value and add it to the running sum.
      final double correctedAddend = addend + _compensation;
      final double tempSum = _sum + correctedAddend;

      // Compute the next compensation and set the running sum.
      // The parentheses are necessary to compute the high-order
      // bits of the addend.
      _compensation = correctedAddend - (tempSum - _sum);
      _sum = tempSum;
   }


   //   public static void main(final String[] args) {
   //      System.out.println("GKahanSummer 0.1");
   //      System.out.println("----------------\n");
   //
   //      final Random random = new Random();
   //      final double[] values = new double[127000000];
   //      for (int i = 0; i < values.length; i++) {
   //         values[i] = 4000000d + (random.nextDouble() * 60000);
   //      }
   //
   //      System.out.println(GMath.plainSum(values) / values.length);
   //
   //      System.out.println(GMath.kahanSum(values) / values.length);
   //
   //      final GKahanSummer summer = new GKahanSummer();
   //      for (final double value : values) {
   //         summer.add(value);
   //      }
   //      System.out.println(summer.value() / values.length);
   //   }

}
