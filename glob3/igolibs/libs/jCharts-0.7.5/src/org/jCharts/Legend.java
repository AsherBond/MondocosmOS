/***********************************************************************************************
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


package org.jCharts;


import org.jCharts.chartData.interfaces.*;
import org.jCharts.chartData.processors.TextProcessor;
import org.jCharts.properties.*;
import org.jCharts.test.HTMLGenerator;
import org.jCharts.test.HTMLTestable;
import org.jCharts.types.ChartType;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;


/*************************************************************************************
 *
 * @author Nathaniel Auvil, Sandor Dornbush, Sundar Balasubramanian
 * @version $Id: Legend.java,v 1.17 2003/04/19 01:39:17 nathaniel_auvil Exp $
 ************************************************************************************/
final public class Legend implements HTMLTestable, Serializable
{
	private Chart chart;
	private LegendProperties legendProperties;

	private float iconSide;


	//---derived values
	private float widestLabelAndColumnPadding;
	private int numColumns;
	private int numRows;


	private TextProcessor textProcessor;

	private float x;
	private float y;
	private float width = 0;
	private float height = 0;


	//---used to extract the legendLabels and paints from the data set and make them easy to loop through
	private ArrayList labels;
	private ArrayList paints;
	private ArrayList shapes = new ArrayList();
	private ArrayList fillPointsFlags = new ArrayList();
	private ArrayList pointOutlinePaints = new ArrayList();


	/*********************************************************************************************
	 *
	 * @param chart
	 * @deprecated
	 **********************************************************************************************/
	public Legend( Chart chart )
	{
		this.chart = chart;
	}


	/*********************************************************************************************
	 *
	 * @param chart
	 * @param legendProperties
	 **********************************************************************************************/
	public Legend( Chart chart, LegendProperties legendProperties )
	{
		this.chart = chart;
		this.legendProperties = legendProperties;
	}


	public void setX( float x )
	{
		this.x = x;
	}


	public void setY( float y )
	{
		this.y = y;
	}


	/*****************************************************************************************
	 *
	 * @param iAxisDataSeries
	 * @param chartTitleHeight
	 ****************************************************************************************/
	public void computeLegendXY( IAxisDataSeries iAxisDataSeries, float chartTitleHeight )
	{
		//---PROCESS the size needed for drawing the legend.
		this.calculateDrawingValues( iAxisDataSeries );

		if( (this.getLegendProperties().getPlacement() == LegendAreaProperties.RIGHT)
			|| (this.getLegendProperties().getPlacement() == LegendAreaProperties.LEFT) )
		{
			if( this.getHeight() > this.chart.getImageHeight() - this.chart.getChartProperties().getEdgePadding() * 2 )
			{
				this.setY( this.chart.getChartProperties().getEdgePadding() );
			}
			else
			{
				this.setY( (this.chart.getImageHeight() / 2) - (this.getHeight() / 2) );
			}

			if( this.getLegendProperties().getPlacement() == LegendAreaProperties.RIGHT )
			{
				this.setX( this.chart.getImageWidth() - this.getWidth() - this.chart.getChartProperties().getEdgePadding() );
			}
			else //---else, LegendAreaProperties.LEFT
			{
				this.setX( this.chart.getChartProperties().getEdgePadding() );
			}
		}
		else //---LegendAreaProperties.BOTTOM, OR LegendAreaProperties.TOP
		{
			if( this.getWidth() + this.chart.getChartProperties().getEdgePadding() * 2 > this.chart.getImageWidth() )
			{
				this.setX( this.chart.getChartProperties().getEdgePadding() );
			}
			else
			{
				this.setX( (this.chart.getImageWidth() / 2) - (this.getWidth() / 2) );
			}

			if( this.getLegendProperties().getPlacement() == LegendAreaProperties.BOTTOM )
			{
				this.setY( this.chart.getImageHeight() - this.getHeight() - this.chart.getChartProperties().getEdgePadding() );
			}
			else //---else, LegendAreaProperties.TOP
			{
				this.setY( this.chart.getChartProperties().getEdgePadding() + chartTitleHeight );
			}
		}
	}


	/**********************************************************************************************
	 * Central method for processing data; try to minimize looping.
	 * 1) calculate the maximum height of labels
	 * 2) find the maximum label width
	 *
	 * @param iAxisDataSeries
	 **********************************************************************************************/
	private void processData( IAxisDataSeries iAxisDataSeries )
	{
		this.textProcessor = new TextProcessor();

		Iterator iterator = iAxisDataSeries.getIAxisPlotDataSetIterator();

		//LOOP
		while( iterator.hasNext() )
		{
			this.processLegendLabels( (IAxisPlotDataSet) iterator.next() );
		}
	}


	/**********************************************************************************************
	 * Central method for processing data; try to minimize looping.
	 * 1) calculate the maximum height of labels
	 * 2) find the maximum label width
	 *
	 * @param iPieChartDataSet
	 **********************************************************************************************/
	private void processData( IPieChartDataSet iPieChartDataSet )
	{
		this.textProcessor = new TextProcessor();
		this.processLegendLabels( iPieChartDataSet );
	}


	/**********************************************************************************************
	 * Method for processing data for AxisPlot datasets; try to minimize
	 * looping.
	 * 1) calculate the maximum height of labels
	 * 2) find the maximum label width
	 *
	 * @param iAxisPlotDataSet
	 * *********************************************************************************************/
	private void processLegendLabels( IAxisPlotDataSet iAxisPlotDataSet )
	{
		for( int i = 0; i < iAxisPlotDataSet.getNumberOfLegendLabels(); i++ )
		{
			//---StockChartDataSets could have NULLs depending on the data
			if( iAxisPlotDataSet.getLegendLabel( i ) != null )
			{
				this.textProcessor.addLabel( iAxisPlotDataSet.getLegendLabel( i ), this.legendProperties.getFont(), this.chart.getGraphics2D().getFontRenderContext() );

				//---pair labels with paints to get around ugly piechart vs axischart data structure mess
				this.labels.add( iAxisPlotDataSet.getLegendLabel( i ) );
				this.paints.add( iAxisPlotDataSet.getPaint( i ) );

				if( iAxisPlotDataSet.getChartType().equals( ChartType.POINT ) )
				{
					PointChartProperties pointChartProperties = (PointChartProperties) iAxisPlotDataSet.getChartTypeProperties();
					this.shapes.add( pointChartProperties.getShape( i ) );
					this.fillPointsFlags.add( new Boolean( pointChartProperties.getFillPointsFlag( i ) ) );
					this.pointOutlinePaints.add( pointChartProperties.getPointOutlinePaints( i ) );
				}
			}
		}
	}


	/**********************************************************************************************
	 * Method for processing data for PieCharts; try to minimize looping.
	 * 1) calculate the maximum height of labels
	 * 2) find the maximum label width
	 * @param iPieChartDataSet
	 * ********************************************************************************************/
	private void processLegendLabels( IPieChartDataSet iPieChartDataSet )
	{
		for( int i = 0; i < iPieChartDataSet.getNumberOfLegendLabels(); i++ )
		{
			//---StockChartDataSets could have NULLs depending on the data
			if( iPieChartDataSet.getLegendLabel( i ) != null )
			{
				this.textProcessor.addLabel( iPieChartDataSet.getLegendLabel( i ), this.legendProperties.getFont(), this.chart.getGraphics2D().getFontRenderContext() );

				//---pair labels with paints to get around ugly piechart vs axischart data structure mess
				this.labels.add( iPieChartDataSet.getLegendLabel( i ) );
				this.paints.add( iPieChartDataSet.getPaint( i ) );
			}
		}
	}


	/************************************************************************************************
	 *
	 *************************************************************************************************/
	public LegendProperties getLegendProperties()
	{
		return this.legendProperties;
	}


	/************************************************************************************************
	 * Calculates the width and height needed to display the Legend.  Use the getWidth() and
	 *  getHeight() methods to extract this information.
	 *
	 * @param iData can pass either the IPieChartDataSet or the IChartDataSeries to this.
	 ************************************************************************************************/
	public void calculateDrawingValues( IData iData )
	{
		int numberOfLabels;
		this.labels = new ArrayList();
		this.paints = new ArrayList();


		if( iData instanceof IAxisDataSeries )
		{
			IAxisDataSeries iAxisDataSeries = (IAxisDataSeries) iData;
			this.processData( iAxisDataSeries );
			numberOfLabels = iAxisDataSeries.getTotalNumberOfDataSets();
		}
		else
		{
			IPieChartDataSet iPieChartDataSet = (IPieChartDataSet) iData;
			this.processData( iPieChartDataSet );
			numberOfLabels = iPieChartDataSet.getNumberOfLegendLabels();
		}


		//---make the icon proportional to the Font being used.
		this.iconSide = (float) .50 * this.textProcessor.getTallestLabel();


		this.determineWidthAndHeight( numberOfLabels );
	}


	/********************************************************************************************
	 *
	 ********************************************************************************************/
	public float getWidth()
	{
		return this.width;
	}


	/********************************************************************************************
	 *
	 ********************************************************************************************/
	public int getHeight()
	{
//why not return a float here?
		return ((int) Math.ceil( this.height ));
	}


	/**********************************************************************************************
	 * Determines the dimensions needed for the Legend and creates the image for it.
	 *
	 **********************************************************************************************/
	private void determineWidthAndHeight( int numberOfLabels )
	{
		//---start with the padding no matter how many columns or specified width
		width = this.legendProperties.getEdgePadding() * 2;
		height = width;


		//---if don't care how many columns or the number of labels is less than num columns specified, all in one row.
		if( this.legendProperties.getNumColumns() == LegendAreaProperties.COLUMNS_AS_MANY_AS_NEEDED
			|| this.legendProperties.getNumColumns() >= numberOfLabels )
		{
			this.numColumns = numberOfLabels;
			width += this.textProcessor.getTotalLabelWidths();

			this.numRows = 1;
		}
		//---else, more than one row
		else
		{
			//---one less addition to do when looping.
			this.widestLabelAndColumnPadding = this.textProcessor.getWidestLabel() + this.legendProperties.getColumnPadding();

			if( legendProperties.getNumColumns() == LegendAreaProperties.COLUMNS_FIT_TO_IMAGE )
			{
				// calculate that the columns match exactly
				float actualWidth = legendProperties.getSize().width;

				float widestLabelColumnAndIcon =
					widestLabelAndColumnPadding +
					iconSide +
					legendProperties.getIconPadding() +
					legendProperties.getColumnPadding();

				numColumns = (int) (actualWidth / widestLabelColumnAndIcon);
				numColumns = Math.min( numColumns, numberOfLabels );
			}
			else
			{
				numColumns = this.legendProperties.getNumColumns();
			}

			width += this.textProcessor.getWidestLabel() * this.numColumns;

			this.numRows = (int) Math.ceil( (double) numberOfLabels / (double) this.numColumns );
		}


		//---account for icons
		width += (this.iconSide + this.legendProperties.getIconPadding()) * this.numColumns;

		//---account for space between each column
		width += this.legendProperties.getColumnPadding() * (this.numColumns - 1);

		//---account for each row
		height += (this.textProcessor.getTallestLabel() * this.numRows);

		//---account for each row padding
		height += (this.legendProperties.getRowPadding() * (this.numRows - 1));
	}


	/**********************************************************************************************
	 * Renders the legend.
	 *
	 **********************************************************************************************/
	public void render()
	{
		Graphics2D g2d = this.chart.getGraphics2D();

		//---get the bounds of the image
		Rectangle2D.Float rectangle = new Rectangle2D.Float( this.x, this.y, this.width - 1, this.height - 1 );

		//---fill the background of the Legend with the specified Paint
		if( this.legendProperties.getBackgroundPaint() != null )
		{
			g2d.setPaint( this.legendProperties.getBackgroundPaint() );
			g2d.fill( rectangle );
		}

		//---draw Legend border
		if( this.legendProperties.getBorderStroke() != null )
		{
			this.legendProperties.getBorderStroke().draw( g2d, rectangle );
		}

		//---dont think we want this so text will be clean but leave commented out.
		//g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );

		//---set the font and text color.
		g2d.setFont( this.legendProperties.getFont() );

		//---icon coordinates
		rectangle.y += this.legendProperties.getEdgePadding() + (this.textProcessor.getTallestLabel() / 2) - (this.iconSide / 2);
		rectangle.width = this.iconSide;
		rectangle.height = this.iconSide;


		float posX = this.x + this.legendProperties.getEdgePadding();
		float fontY = rectangle.y + rectangle.height;


		//---pre calculate utility values
		float yIncrement = this.textProcessor.getTallestLabel() + this.legendProperties.getRowPadding();
		float iconAndPaddingWidth = this.iconSide + this.legendProperties.getIconPadding();

		int labelIndex = 0;

		//LOOP
		for( int j = 0; j < this.numRows; j++ )
		{
			//LOOP
			for( int i = 0; i < this.numColumns; i++ )
			{
				rectangle.x = posX;

				//---display icon
				g2d.setPaint( (Paint) this.paints.get( labelIndex ) );

				//---get the original transform so can reset it.
				AffineTransform affineTransform = g2d.getTransform();
				if( this.shapes.size() > 0 )
				{
					//---translate the Shape into position.
					g2d.translate( rectangle.x, rectangle.y );

					if( ((Boolean) this.fillPointsFlags.get( labelIndex )).booleanValue() )
					{
						g2d.fill( (Shape) this.shapes.get( labelIndex ) );

						//---if we are filling the points, see if we should outline the Shape
						if( this.pointOutlinePaints.get( labelIndex ) != null )
						{
							g2d.setPaint( (Paint) this.pointOutlinePaints.get( labelIndex ) );
							g2d.draw( (Shape) this.shapes.get( labelIndex ) );
						}
					}
					else
					{
						g2d.draw( (Shape) this.shapes.get( labelIndex ) );
					}
					g2d.setTransform( affineTransform );
				}
				else
				{
					g2d.fill( rectangle );

					//---border around icon
					if( this.legendProperties.getIconBorderStroke() != null )
					{
						g2d.setStroke( this.legendProperties.getIconBorderStroke() );
						g2d.setPaint( this.legendProperties.getIconBorderPaint() );
						g2d.draw( rectangle );
					}
				}


				//---draw the label
				g2d.setPaint( this.legendProperties.getFontPaint() );
				posX += iconAndPaddingWidth;
				g2d.drawString( (String) this.labels.get( labelIndex ), posX, fontY );


				if( this.legendProperties.getNumColumns() == LegendAreaProperties.COLUMNS_AS_MANY_AS_NEEDED
					|| this.legendProperties.getNumColumns() >= this.labels.size() )
				{
					//---each column is as wide as it needs to be
					posX += this.textProcessor.getTextTag( labelIndex ).getWidth() + this.legendProperties.getColumnPadding();
				}
				else
				{
					//---all columns have the same width
					posX += this.widestLabelAndColumnPadding;
				}

				labelIndex++;

				//---if no more labels, we are done.
				if( labelIndex == this.labels.size() ) break;
			}

			posX = this.x + this.legendProperties.getEdgePadding();
			fontY += yIncrement;
			rectangle.y += yIncrement;
		}
	}


	/*********************************************************************************************
	 * Enables the testing routines to display the contents of this Object.
	 *
	 * @param htmlGenerator
	 **********************************************************************************************/
	public void toHTML( HTMLGenerator htmlGenerator )
	{
		htmlGenerator.legendTableStart();

		htmlGenerator.addTableRow( "Width", Float.toString( this.width ) );
		htmlGenerator.addTableRow( "Height", Float.toString( this.height ) );
		htmlGenerator.addTableRow( "Icon Side", Float.toString( this.iconSide ) );

		htmlGenerator.innerTableRowStart();
		this.legendProperties.toHTML( htmlGenerator );
		htmlGenerator.innerTableRowEnd();

		htmlGenerator.legendTableEnd();
	}

}
