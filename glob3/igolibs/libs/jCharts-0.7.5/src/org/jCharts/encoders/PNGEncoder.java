/***********************************************************************************************
 * File Info: $Id: PNGEncoder.java,v 1.6 2003/03/14 03:22:42 nathaniel_auvil Exp $
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


import org.jCharts.Chart;
import org.jCharts.chartData.ChartDataException;
import org.jCharts.properties.PropertyException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;


/********************************************************************************************
 * This class REQUIRES the jdk 1.4
 *
 **********************************************************************************************/
public final class PNGEncoder
{
	private static final String PNG = "png";


	static
	{
		//---do not use a file cache as hurts performance
		ImageIO.setUseCache( false );
	}


	/******************************************************************************************
	 *
	 *
	 ******************************************************************************************/
	private PNGEncoder() throws Exception
	{
		throw new Exception( "No need to create an instance of this class!" );
	}


	/******************************************************************************************
	 * Encodes the Chart to an OutputStream which can be a file or any other OutputStream
	 * implementation.
	 *
	 * @param chart
	 * @param outputStream
	 * @throws ChartDataException
	 * @throws PropertyException
	 * @throws IOException
	 *******************************************************************************************/
	public static final void encode( Chart chart, OutputStream outputStream ) throws ChartDataException, PropertyException, IOException
	{
		BufferedImage bufferedImage = BinaryEncoderUtil.render( chart );
		ImageIO.write( bufferedImage, PNG, outputStream );
	}
}
