/***********************************************************************************************
 * File Info: $Id: JPEGEncoder13.java,v 1.6 2003/03/14 03:22:42 nathaniel_auvil Exp $
 * Copyright (C) 2002
 * Author: Nathaniel G. Auvil
 * Contributor(s):
 *
 * Copyright 2002 (C) Nathaniel G. Auvil. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation ("Software"), with or
 * without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and notices.
 * 	Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * 	conditions and the following disclaimer in the documentation and/or other materials
 * 	provided with the distribution.
 *
 * 3. The name "jCharts" or "Nathaniel G. Auvil" must not be used to endorse or promote
 * 	products derived from this Software without prior written permission of Nathaniel G.
 * 	Auvil.  For written permission, please contact nathaniel_auvil@users.sourceforge.net
 *
 * 4. Products derived from this Software may not be called "jCharts" nor may "jCharts" appear
 * 	in their names without prior written permission of Nathaniel G. Auvil. jCharts is a
 * 	registered trademark of Nathaniel G. Auvil.
 *
 * 5. Due credit should be given to the jCharts Project (http://jcharts.sourceforge.net/).
 *
 * THIS SOFTWARE IS PROVIDED BY Nathaniel G. Auvil AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * jCharts OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 ************************************************************************************************/

package org.jCharts.encoders;


import com.sun.image.codec.jpeg.*;
import org.jCharts.Chart;
import org.jCharts.chartData.ChartDataException;
import org.jCharts.properties.PropertyException;

import java.awt.image.*;
import java.io.IOException;
import java.io.OutputStream;


/*******************************************************************************************
 * Provided for backwards compatibility for jdk 1.3
 *
 *********************************************************************************************/
public final class JPEGEncoder13
{

	/******************************************************************************************
	 *
	 *
	 ******************************************************************************************/
	private JPEGEncoder13() throws Exception
	{
		throw new Exception( "No need to create an instance of this class!" );
	}


	/******************************************************************************************
	 * Encodes the chart to a JPEG format. If you are generating large dimension images, the file
	 *  size can get quite large.  You can try decreasing the quality to decrease the file size.
	 *
	 * @param outputStream
	 * @param quality float value from 0.0f(worst image quality) - 1.0f(best image quality)
	 * @throws ChartDataException
	 * @throws PropertyException
	 * @throws IOException
	 *******************************************************************************************/
	public static final void encode( Chart chart,
												float quality,
												OutputStream outputStream ) throws ChartDataException, PropertyException, IOException
	{
		BufferedImage bufferedImage = BinaryEncoderUtil.render( chart );

		float[] sharpKernel = {0.0f, -1.0f, 0.0f,
									  -1.0f, 5.0f, -1.0f,
									  0.0f, -1.0f, 0.0f};

		BufferedImageOp sharpen = new ConvolveOp( new Kernel( 3, 3, sharpKernel ), ConvolveOp.EDGE_NO_OP, null );
		BufferedImage sharp = sharpen.filter( bufferedImage, null );

		//---create an encoder object for the BufferedImage.
		JPEGEncodeParam jpegParam = JPEGCodec.getDefaultJPEGEncodeParam( sharp );
		jpegParam.setQuality( quality, false );

		JPEGImageEncoder jpeg = JPEGCodec.createJPEGEncoder( outputStream, jpegParam );

		//---encode the BufferedImage.
		jpeg.encode( bufferedImage );

	}
}



