/***********************************************************************************************
 * File Info: $Id: BarTestDriver.java,v 1.20 2003/11/02 13:22:31 nathaniel_auvil Exp $
 * Copyright (C) 2000
 * Author: Nathaniel G. Auvil
 * Contributor(s):
 *
 * Copyright 2002 (C) Nathaniel G. Auvil. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright
 *    statements and notices.  Redistributions must also contain a
 *    copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the
 *    above copyright notice, this list of conditions and the
 *    following disclaimer in the documentation and/or other
 *    materials provided with the distribution.
 *
 * 3. The name "jCharts" or "Nathaniel G. Auvil" must not be used to
 * 	  endorse or promote products derived from this Software without
 * 	  prior written permission of Nathaniel G. Auvil.  For written
 *    permission, please contact nathaniel_auvil@users.sourceforge.net
 *
 * 4. Products derived from this Software may not be called "jCharts"
 *    nor may "jCharts" appear in their names without prior written
 *    permission of Nathaniel G. Auvil. jCharts is a registered
 *    trademark of Nathaniel G. Auvil.
 *
 * 5. Due credit should be given to the jCharts Project
 *    (http://jcharts.sourceforge.net/).
 *
 * THIS SOFTWARE IS PROVIDED BY Nathaniel G. Auvil AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * jCharts OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 ************************************************************************************************/

package org.jCharts.test;


import org.jCharts.axisChart.AxisChart;
import org.jCharts.axisChart.customRenderers.axisValue.renderers.*;
import org.jCharts.chartData.*;
import org.jCharts.chartData.interfaces.IAxisDataSeries;
import org.jCharts.properties.*;
import org.jCharts.properties.util.ChartStroke;
import org.jCharts.types.ChartType;

import java.awt.*;
import java.util.Locale;
import java.util.Collection;
import java.util.Iterator;
import java.text.MessageFormat;


/******************************************************************************************
 * This file provides examples of how to create all the different chart types provided by
 *  this package.
 *
 *******************************************************************************************/
public final class BarTestDriver extends AxisChartTestBase
{
	boolean supportsImageMap()
	{
		return true;
	}


	/******************************************************************************************
	 * Separate this so can use for combo chart test
	 *
	 ******************************************************************************************/
	static ChartTypeProperties getChartTypeProperties( int numberOfDataSets )
	{
		BarChartProperties barChartProperties = new BarChartProperties();
		barChartProperties.setWidthPercentage( 1f );

		return barChartProperties;
	}


	/******************************************************************************************
	 *
	 *
	 ******************************************************************************************/
	DataSeries getDataSeries() throws ChartDataException
	{
		int dataSize = (int) TestDataGenerator.getRandomNumber( 2, 5 );
		int numberOfDataSets = 1; //(int) TestDataGenerator.getRandomNumber( 1, 3 );


		AxisChartDataSet axisChartDataSet;


		DataSeries dataSeries = super.createDataSeries( dataSize );

		axisChartDataSet = super.createAxisChartDataSet( ChartType.BAR,
																		 getChartTypeProperties( numberOfDataSets ),
																		 numberOfDataSets,
																		 dataSize,
																		 -2000,
																		 2000 );

		dataSeries.addIAxisPlotDataSet( axisChartDataSet );

		return dataSeries;
	}


	/*****************************************************************************************
	 *
	 * @param args
	 * @throws PropertyException
	 * @throws ChartDataException
	 *****************************************************************************************/
	public static void main( String[] args ) throws PropertyException, ChartDataException
	{
		BarChartProperties barChartProperties = new BarChartProperties();

		//BackgroundRenderer backgroundRenderer = new BackgroundRenderer( new Color( 20, 20, 20, 50 ) );
		//barChartProperties.addPreRenderEventListener( backgroundRenderer );

/*
		ValueLabelRenderer valueLabelRenderer = new ValueLabelRenderer( false, true, -1 );
		valueLabelRenderer.setValueLabelPosition( ValueLabelPosition.ON_TOP );
		valueLabelRenderer.useVerticalLabels( false );
		barChartProperties.addPostRenderEventListener( valueLabelRenderer );
*/


/*
		double[][] data = {{280, 16, -150, 160, 90, 60, 150, 11, -23, 50, 89}};
		Paint[] paints = {Color.green};
		String[] legendLabels = {"Test Legend Label"};
		AxisChartDataSet axisChartDataSet = new AxisChartDataSet( data, legendLabels, paints, ChartType.BAR, barChartProperties );

		String[] axisLabels = {"1900", "195555510", "1920", "1935555555555550", "1940", "19555555550", "5555551960", "19755555550", "19855550", "19905555", "20005555"};
		//String[] axisLabels = {"1900", "1910", "1920", "1930", "1940", "1950", "1960", "1970", "1980", "1990", "2000" };
		IAxisDataSeries dataSeries = new DataSeries( axisLabels, "Wonka Bars", "Years", "Oompa Loompa Productivity" );
		dataSeries.addIAxisPlotDataSet( axisChartDataSet );


		ChartProperties chartProperties = new ChartProperties();
		AxisProperties axisProperties = new AxisProperties( true );

		axisProperties.getYAxisProperties().setShowGridLines( AxisTypeProperties.GRID_LINES_ALL );
		axisProperties.getYAxisProperties().setGridLineChartStroke( new ChartStroke( new BasicStroke( 1.5f ), Color.red ) );

		//axisProperties.setXAxisLabelsAreVertical( true );

		DataAxisProperties yAxis = (DataAxisProperties) axisProperties.getXAxisProperties();
		yAxis.setRoundToNearest( 1 );
		yAxis.setUserDefinedScale( -300, 200 );

		LegendProperties legendProperties = null; //new LegendProperties();

		AxisChart axisChart = new AxisChart( dataSeries, chartProperties, axisProperties, legendProperties, 500, 400 );

		ChartTestDriver.exportImage( axisChart, "BarChartTest.png" );
*/

		bug2();
	}


	public static void bug() throws ChartDataException, PropertyException
	{
		String[] xAxisLabels = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII"};
		//out = response.getOutputStream();

		//java.util.ResourceBundle txts = java.util.ResourceBundle.getBundle( "charts", new Locale( language ) );

		//String year = getParameter( request, "Year" );
		//if( year == null ) year = "2002";

		//Collection v = user.getNumbersByYear( kid, new Long( year ) );
		//Iterator it = v.iterator();

		double[][] data = new double[1][12];
		for( int i = 0; i < 12; i++ )
		{
			double d = 51;
			data[0][i] = d;
		}
		String xAxisTitle = "CHART1_XAXIS";
		String yAxisTitle = "CHART1_YAXIS";

		//Object[] arg = {year};
		String title = "CHART1_TITLE hhhhhhhhhhhhhhhhhhhhh hhhhhhhhh hhhhhhhhhhh hhhhhhhhhhh hhhhhhhh hhhhhhhh hhhh  hhhhh hhhh hhhh hhhh  hhhhh hhhhhhhh";

		DataSeries dataSeries = new DataSeries( xAxisLabels, xAxisTitle, yAxisTitle, title );

		String[] legendLabels = {"CHART1_LEGEND"};

		Paint[] paints = {new GradientPaint( 0, 0, new Color( 255, 213, 83, 150 ), 0, 350, new Color( 243, 116, 0, 200 ) )
		};
		BarChartProperties barChartProperties = new BarChartProperties();

		ValueLabelRenderer valueLabelRenderer = new ValueLabelRenderer( false, false, false, -1 );
		valueLabelRenderer.setValueLabelPosition( ValueLabelPosition.ON_TOP );
		valueLabelRenderer.useVerticalLabels( false );
		barChartProperties.addPostRenderEventListener( valueLabelRenderer );

		AxisChartDataSet axisChartDataSet = new AxisChartDataSet( data, legendLabels, paints, ChartType.BAR, barChartProperties );
		dataSeries.addIAxisPlotDataSet( axisChartDataSet );
		ChartProperties chartProperties = new ChartProperties();

		LabelAxisProperties xAxisProperties = new LabelAxisProperties();
		//xAxisProperties.setAxisTitleChartFont( ft_axis );
		//xAxisProperties.setScaleChartFont( ft_labels );

		DataAxisProperties yAxisProperties = new DataAxisProperties();
		//yAxisProperties.setAxisTitleChartFont( ft_axis );
		//yAxisProperties.setScaleChartFont( ft_labels );
		yAxisProperties.setRoundToNearest( 0 );

		AxisProperties axisProperties = new AxisProperties( xAxisProperties, yAxisProperties );
		LegendProperties legendProperties = new LegendProperties();

		//chartProperties.setTitleFont( ft_title );
		//legendProperties.setFont( ft_base_ );

		axisProperties.setBackgroundPaint( new GradientPaint( 0, 0, new Color( 255, 255, 255 ), 0, 300, new
			Color( 167, 213, 255 ) ) );
		AxisChart axisChart = new AxisChart(
			dataSeries,
			chartProperties,
			axisProperties,
			null, //legendProperties,
			548, 350 ); // wymiary wykresu

		ChartTestDriver.exportImage( axisChart, "Bug_BarChartTest.png" );

	}


	private static void bug2()
	{
		BarChartProperties barChartProperties = null;
		LegendProperties legendProperties = null;
		AxisProperties axisProperties = null;
		ChartProperties chartProperties =null;// = new ChartProperties();
		int width = 550;
		int height = 360;

		try
		{
			String[] xAxisLabels = {"1995", "1996", "1997", "1998", "1999", "2000","2001", "2002", "2003", "2004"};
			String xAxisTitle = "Years";
			String yAxisTitle = "Problems";
			String title = "Micro$oft At Work";
			IAxisDataSeries dataSeries = new DataSeries( xAxisLabels, xAxisTitle,yAxisTitle, title );

			double[][] data = new double[][]{{1500, 6880, 4510, 2600, 1200, 1580,8000, 4555, 4000, 6120}};
			String[] legendLabels = {"Bugs"};
			Paint[] paints = new Paint[]{Color.blue.darker()};
			dataSeries.addIAxisPlotDataSet( new AxisChartDataSet( data,legendLabels, paints, ChartType.BAR, barChartProperties ) );
			AxisChart axisChart = new AxisChart( dataSeries, chartProperties, axisProperties, legendProperties, width, height );

			ChartTestDriver.exportImage( axisChart, "Bug222_BarChartTest.png" );
		}
		catch( ChartDataException chartDataException )
		{
			chartDataException.printStackTrace();
		}
		catch( PropertyException propertyException ) {
			propertyException.printStackTrace();
		}
	}
}
