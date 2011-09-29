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


package es.igosoftware.euclid.colors;


public interface IColor {

   /**
    * @return the red component of this color, a float in the range [0.0..1.0]
    */
   public float getRed();


   /**
    * @return the green component of this color, a float in the range [0.0..1.0]
    */
   public float getGreen();


   /**
    * @return the blue component of this color, a float in the range [0.0..1.0]
    */
   public float getBlue();


   /**
    * @return the brightness of this color, a float in the range [0.0..1.0]
    */
   public float getBrightness();


   /**
    * @return the hue of this color, an angle in the range [0.0..2PI]
    */
   public double getHue();


   /**
    * @return the saturation of this color, a float in the range [0.0..1.0]
    */
   public float getSaturation();


   /**
    * @return the luminance of this color, a brightness value weighted by the human eye's color sensitivity, a float in the range
    *         [0.0..1.0]
    */
   public float getLuminance();


   public IColor add(final float delta);


   public IColor add(final IColor that);


   public IColor mul(final float delta);


   public IColor div(final float delta);


   public boolean between(final IColor min,
                          final IColor max);


   public IColor clamp(final IColor lower,
                       final IColor upper);


   public boolean closeTo(final IColor that);


   public IColor mixedWidth(final IColor that,
                            final float alpha);


   public IColor max(final IColor that);


   public IColor min(final IColor that);


   public IColor sub(final float delta);


   public IColor sub(final IColor that);


   public IColor adjustSaturation(final float saturation);


   public IColor adjustBrightness(final float brightness);


   public IColor adjustSaturationBrightness(final float saturation,
                                            final float brightness);


   public float precision();


   /**
    * An array of thisMany colors around the color wheel starting at the receiver and ending all the way around the hue space just
    * before self. Very useful for displaying color based on a variable in your program.
    */
   public IColor[] wheel(final int thisMany);


   /**
    * Return an array of thisMany colors from the receiver to otherColor. Very useful for displaying color based on a variable in
    * your program.
    */
   public IColor[] mix(final IColor otherColor,
                       final int thisMany);


   public IColor darker();


   public IColor twiceDarker();


   public IColor muchDarker();


   public IColor lighter();


   public IColor twiceLighter();


   public IColor muchLighter();


   public java.awt.Color asAWTColor();


   public java.awt.Color asAWTColor(final float opacity);


   public String toHexString();


   public String toInt256String();


}
