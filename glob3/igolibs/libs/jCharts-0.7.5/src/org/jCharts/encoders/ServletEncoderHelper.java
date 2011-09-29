/***********************************************************************************************
 * File Info: $Id: ServletEncoderHelper.java,v 1.1 2003/03/14 03:22:42 nathaniel_auvil Exp $
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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class ServletEncoderHelper
{
	public static final String SVG_MIME_TYPE = "image/svg+xml";
	public static final String PNG_MIME_TYPE = "image/png";
	public static final String JPEG_MIME_TYPE = "image/jpeg";


	/******************************************************************************************
	 * Convenience method to call from a Servlet or JSP.  This method will set the appropriate
	 *  mime type and then export the chart as the response.
	 *
	 * We cannot overload encode(...) as it will create a compile time dependency with the
	 * 	HttpServletResponse Class which will require the J2EE libraries.
	 *
	 * @param chart
	 * @param httpServletResponse
	 * @throws ChartDataException
	 * @throws PropertyException
	 * @throws IOException
	 * @since 0.7
	 ******************************************************************************************/
	public static final void encodeServlet( Chart chart, HttpServletResponse httpServletResponse ) throws ChartDataException, PropertyException, IOException
	{
		httpServletResponse.setContentType( SVG_MIME_TYPE );
		SVGEncoder.encode( chart, httpServletResponse.getOutputStream() );
	}


	/******************************************************************************************
	 * Convenience method to call from a Servlet or JSP.  This method will set the appropriate
	 *  mime type and then export the chart as the response.
	 *
	 * We cannot overload encode(...) as it will create a compile time dependency with the
	 * 	HttpServletResponse Class which will require the J2EE libraries.
	 *
	 * @param chart
	 * @param quality float value from 0.0f(worst image quality) - 1.0f(best image quality)
	 * @param httpServletResponse
	 * @throws ChartDataException
	 * @throws PropertyException
	 * @throws IOException
	 * @since 0.7
	 ******************************************************************************************/
	public static final void encodeJPEG13( Chart chart,
														float quality,
														HttpServletResponse httpServletResponse ) throws ChartDataException, PropertyException, IOException
	{
		httpServletResponse.setContentType( JPEG_MIME_TYPE );
		JPEGEncoder13.encode( chart, quality, httpServletResponse.getOutputStream() );
	}


	/******************************************************************************************
	 * Convenience method to call from a Servlet or JSP.  This method will set the appropriate
	 *  mime type and then export the chart as the response.
	 *
	 * @param chart
	 * @param quality float value from 0.0f(worst image quality) - 1.0f(best image quality)
	 * @param httpServletResponse
	 * @throws ChartDataException
	 * @throws PropertyException
	 * @throws IOException
	 ******************************************************************************************/
	public static final void encodeJPEG( Chart chart,
													 float quality,
													 HttpServletResponse httpServletResponse ) throws ChartDataException, PropertyException, IOException
	{
		httpServletResponse.setContentType( JPEG_MIME_TYPE );
		JPEGEncoder.encode( chart, quality, httpServletResponse.getOutputStream() );
	}


	/******************************************************************************************
	 * Convenience method to call from a Servlet or JSP.  This method will set the appropriate
	 *  mime type and then export the chart as the response.
	 *
	 * @param chart
	 * @param httpServletResponse
	 * @throws org.jCharts.chartData.ChartDataException
	 * @throws org.jCharts.properties.PropertyException
	 * @throws java.io.IOException
	 ******************************************************************************************/
	public static final void encodePNG( Chart chart, HttpServletResponse httpServletResponse ) throws ChartDataException, PropertyException, IOException
	{
		httpServletResponse.setContentType( PNG_MIME_TYPE );
		PNGEncoder.encode( chart, httpServletResponse.getOutputStream() );
	}


}
