/***********************************************************************************************
 * File Info: $Id: BarValueGroup.java,v 1.1 2002/12/05 22:07:28 nathaniel_auvil Exp $
 * Copyright (C) 2000
 * Author: John Thomsen
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

package org.jCharts.chartText;


import org.jCharts.chartData.interfaces.IAxisChartDataSet;
import org.jCharts.properties.AxisProperties;
import org.jCharts.properties.BarChartProperties;
import org.jCharts.chartText.TextTag;
import org.jCharts.axisChart.AxisChart;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.ArrayList;


/**
 */
public class BarValueGroup
{

	/** Helper class to handle a barValue - wraps TextTag
	 * Meant to be called only by BarValueGroup class.
	 */
	protected class BarValue extends TextTag
	{
		private Rectangle2D.Float barRect;
		//private Rectangle2D.Float bounds;
		private int startPosition = 0;
		private boolean isNegative = false;
		private String text;


		public BarValue( String text,
		                 Font font,
		                 FontRenderContext fontRenderContext,
		                 Rectangle2D.Float barRect,
		                 int startPosition,
		                 boolean isNegative )
		{
			super( text, font, fontRenderContext );
			this.text = text;
			this.barRect = barRect;
			this.startPosition = startPosition;
			this.isNegative = isNegative;
		}


		public Rectangle2D.Float getBarRect()
		{
			return this.barRect;
		}


		public int getStartPosition()
		{
			return this.startPosition;
		}


		public boolean getIsNegative()
		{
			return this.isNegative;
		}


		public String getText()
		{
			return this.text;
		}

	}
	// -- End of inner class BarValue


	private AxisChart axisChart;
	//private IAxisChartDataSet iAxisChartDataSet;

	// Values to create or get once...
	private FontRenderContext fontRenderContext;
	private boolean isVertical;
	private BarChartProperties barChartProperties;
	private AxisProperties axisProperties;
	private NumberFormat numberFormat;

	private boolean showBarValues;
	private int barValuePosition;
	private int barValueItem;
	private Font barValueFont;
	private Paint barValueFontColor;

	private float horizontalPadding;
	private float verticalPadding;

	private double totalDataValue = 0.0d;

	private float centerChart = 0.0f;

	private ArrayList textTagList;


	public BarValueGroup( AxisChart axisChart, IAxisChartDataSet iAxisChartDataSet )
	{
/*
		this.barChartProperties = ( BarChartProperties ) iAxisChartDataSet.getChartTypeProperties();
		this.showBarValues = barChartProperties.getShowBarValues();

		//---NOTE: Aborting HERE if not needed.
		if( this.showBarValues == false ) return; // <--- Possible return.

		this.axisChart = axisChart;
		//this.iAxisChartDataSet = iAxisChartDataSet;

		this.axisProperties = axisChart.getAxisProperties();
		this.isVertical = ( axisProperties.getOrientation() == AxisProperties.VERTICAL );

		this.barValuePosition = barChartProperties.getShowBarValuesPosition();
		this.barValueItem = barChartProperties.getShowBarValuesItemToDisplay();
		this.barValueFont = barChartProperties.getShowBarValuesFont();
		this.barValueFontColor = barChartProperties.getShowBarValuesFontColor();

		for( int i = 0; i < iAxisChartDataSet.getNumberOfDataItems(); i++ )
		{
			double dataValue = iAxisChartDataSet.getValue( 0, i );
			totalDataValue += ( dataValue > 0 ) ? dataValue : -dataValue;
		}

		int precision = barChartProperties.getShowBarValuesPrecision();

		if( barChartProperties.getShowBarValuesAsMoney() )
		{
			numberFormat = NumberFormat.getCurrencyInstance();
		}

		if( precision > 0 || numberFormat != null )
		{
			if( numberFormat == null )
			{
				numberFormat = NumberFormat.getInstance();
			}

			numberFormat.setMaximumFractionDigits( precision );
			numberFormat.setMinimumFractionDigits( precision );
		}

		if( isVertical )
		{
			this.verticalPadding = barChartProperties.getShowBarValuesVerticalPadding();
		}
		else
		{
			this.horizontalPadding = barChartProperties.getShowBarValuesHorizontalPadding();
		}

		this.fontRenderContext = axisChart.getGraphics2D().getFontRenderContext();

		this.centerChart = ( isVertical ) ? axisChart.getVerticalAxisPixelLength() : axisChart.getHorizontalAxisPixelLength();
		this.centerChart /= 2.0f;

		textTagList = null;
*/
	}


	/*******************************************************************************************************
	 * Gets the text associated (by the user) for the given bar value.
	 * Meant only to be called by BarChart.render()
	 ********************************************************************************************************/
	private String getBarValueAt( int i, double dataValue )
	{
/*
		String str = "";

		if( barValueItem == BarChartProperties.SHOW_CUSTOM_STRINGS )
		{
			return barChartProperties.getBarValuesString( i );
		}

		if( barValueItem == BarChartProperties.SHOW_LABEL ||
		   barValueItem == BarChartProperties.SHOW_LABEL_AND_PERCENTAGE ||
		   barValueItem == BarChartProperties.SHOW_LABEL_AND_VALUE )
		{
			str = axisChart.getIDataSeries().getLabelAxisLabel( i );
		}

		if( barValueItem != BarChartProperties.SHOW_LABEL )
		{
			String pct = "";

			if( barValueItem == BarChartProperties.SHOW_LABEL_AND_PERCENTAGE ||
			   barValueItem == BarChartProperties.SHOW_LABEL_AND_VALUE )
			{
				str += ": ";
			}

			if( barValueItem == BarChartProperties.SHOW_PERCENTAGE ||
			   barValueItem == BarChartProperties.SHOW_LABEL_AND_PERCENTAGE )
			{
				dataValue = ( dataValue / totalDataValue ) * 100;
				pct = "%";
			}

			if( numberFormat == null )
			{
				str += ( int ) dataValue + pct;
			}
			else
			{
				str += numberFormat.format( dataValue ) + pct;
			}
		}

		return str;
*/
		return null;
	}


	/*******************************************************************************************************
	 * Gets the rectangle coords associated (by the user) for the given bar value.
	 * Meant only to be called by BarChart.render()
	 ********************************************************************************************************/
	private Rectangle2D.Float getBarValueRectangleCoordinates( int position, BarValue barValue )
	{
		//---Called by getBarValueRectangle

		//---Will this spacingwork for Vertical Text? Probably not.

/*
		float stringWidth = barValue.getWidth();
		float stringHeight = barValue.getHeight();
		Rectangle2D.Float barRect = barValue.getBarRect();

		Rectangle2D.Float displayPosition = new Rectangle2D.Float(
		   barRect.x,
		   barRect.y,
		   stringWidth + ( 2 * horizontalPadding ),
		   stringHeight + ( 2 * verticalPadding ) );

		float offset = 0.0f;

		if( position == BarChartProperties.BEYOND_BAR )
		{
			//System.out.println("BEYOND");
			offset = ( isVertical ) ? barRect.height : barRect.width;
			offset += ( isVertical ) ? verticalPadding : horizontalPadding;
		}
		else if( position == BarChartProperties.BEFORE_BAR )
		{
			//System.out.println("BEFORE");
			offset -= ( isVertical ) ? stringHeight : stringWidth;
			offset -= ( isVertical ) ? verticalPadding : horizontalPadding;
		}
		else if( position == BarChartProperties.END_OF_BAR )
		{
			//System.out.println("END");
			offset = ( isVertical ) ? barRect.height : barRect.width;
			offset -= ( isVertical ) ? verticalPadding : horizontalPadding;
			offset -= ( isVertical ) ? stringHeight : stringWidth;
		}
		else if( position == BarChartProperties.CENTER_OF_BAR )
		{
			//System.out.println("CENTER");
			offset = ( isVertical ) ? barRect.height / 2.0f : barRect.width / 2.0f;
			offset += ( isVertical ) ? verticalPadding : horizontalPadding;
			offset -= ( isVertical ) ? stringHeight / 2.0f : stringWidth / 2.0f;
		}
		else if( position == BarChartProperties.BASE_OF_BAR )
		{
			//System.out.println("BASE");
			offset += ( isVertical ) ? verticalPadding : horizontalPadding;
		}
		else if( position == BarChartProperties.CENTER_OF_CHART )
		{
			//System.out.println("CENTER_CHART");
			offset = centerChart;
			offset -= ( isVertical ) ? stringHeight / 2.0f : stringWidth / 2.0f;
		}
		else
		{
			return null;
		}

		if( isVertical )
		{
			displayPosition.y += barRect.height - offset;
			displayPosition.x += ( barRect.width - stringWidth ) / 2.0f;
		}
		else
		{
			displayPosition.x += offset;
			displayPosition.y += ( barRect.height ) / 2.0f;
			// Why does this need to be 3? 2 should work right.
			displayPosition.y += ( stringHeight ) / 2.0f;
		}

		return displayPosition;
*/
		return null;
	}


	/** Not sure why this was left out of Rectangle2D...
	 */
	private float getRight( Rectangle2D.Float r )
	{
		return r.x + r.width;
	}


	/** Not sure why this was left out of Rectangle2D...
	 */
	private float getBottom( Rectangle2D.Float r )
	{
		return r.y + r.height;
	}


	/*******************************************************************************************************
	 * Gets the rectangle associated (by the user) for the given bar value.
	 * Meant only to be called by BarChart.render()
	 ********************************************************************************************************/
	private void setBarValuePosition( BarValue barValue, Rectangle2D.Float bounds )
	{
/*
		if( barValue == null ) return;

		//boolean moved = false;

		//---Extra space needed above (or below) baseline
		float ySpace = ( barValue.getHeight() / 2 ) + verticalPadding;

		//---Holder to contain the coordinates for text display.
		Rectangle2D.Float displayPosition;

		//---The rectangle for the Bar on (in, around) which we display
		Rectangle2D.Float barRect = barValue.getBarRect();

		//---Position relative to the Bar
		int position = barValue.getStartPosition();

		//---Remap if in the negative area
		if( barValue.getIsNegative() )
		{
			if( barValuePosition == BarChartProperties.END_OF_BAR )
			{
				position = BarChartProperties.BASE_OF_BAR;
			}
			else if( barValuePosition == BarChartProperties.BEYOND_BAR )
			{
				position = BarChartProperties.BEFORE_BAR;
			}
			else if( barValuePosition == BarChartProperties.BASE_OF_BAR )
			{
				position = BarChartProperties.END_OF_BAR;
			}
		}

		//---The string to be displayed
		//String str = barValue.getText();

		//---Debug
		//System.out.println("Position:"+str+" at ("+position+")");

		//---Get default coordinates.
		displayPosition = getBarValueRectangleCoordinates( position, barValue );

		//---Special case, if before, then we move to base if too far left or down
		//---Should only happen on charts with negative values.
		if( position == BarChartProperties.BEFORE_BAR )
		{
			if( ( isVertical == false && displayPosition.x < bounds.x ) ||
			   ( isVertical && getBottom( displayPosition ) > getBottom( bounds ) ) )
			{
				position = BarChartProperties.BASE_OF_BAR;
				//moved = true;
				displayPosition = getBarValueRectangleCoordinates( position, barValue );
				//System.out.println("0:Moved "+str+" to BASE_OF_BAR");
			}
		}

		//---Do we have to shift it over a bit?
		//---Does it run outside the bars rectangle?
		if( position == BarChartProperties.BASE_OF_BAR ||
		   position == BarChartProperties.CENTER_OF_BAR )
		{
			// Ignore ones that are too wide in vertical chart, or tall in horizontal chart
			if( ( isVertical && ( displayPosition.y - ( ySpace ) ) < barRect.y ) ||
			   ( isVertical == false && getRight( displayPosition ) > getRight( barRect ) ) )
			{
				// Move needed
				position = BarChartProperties.END_OF_BAR;

				//moved = true;
				displayPosition = getBarValueRectangleCoordinates( position, barValue );
				//System.out.println("1:Moved "+str+" to END_OF_BAR");
			}
		}

		//---Does it run over left/bottom edge of chart (bounded area)?
		if( position == BarChartProperties.END_OF_BAR ||
		   position == BarChartProperties.CENTER_OF_BAR )
		{
			if( ( isVertical == false && displayPosition.x < bounds.x ) ||
			   ( isVertical && ( getBottom( displayPosition ) + ySpace ) > getBottom( bounds ) ) )
			{
				position = BarChartProperties.BEYOND_BAR;
				//moved = true;
				displayPosition = getBarValueRectangleCoordinates( position, barValue );
				//System.out.println("2:Moved "+str+" to BEYOND_BAR");
			}
		}

		//---Does it run over right/top edge of chart (bounds)?
		if( position == BarChartProperties.BEYOND_BAR )
		{
			boolean mustMove = false;

			if( isVertical == false )
			{
				if( getRight( displayPosition ) > getRight( bounds ) )
				{
					//System.out.println("Fail " + (displayPosition.x + displayPosition.width) + " > " + (bounds.x + bounds.width));
					mustMove = true;
				}
			}
			else
			{
				if( ( displayPosition.y + ySpace ) <= bounds.y )
				{
					mustMove = true;
				}
			}

			if( mustMove )
			{
				position = BarChartProperties.END_OF_BAR;
				//moved = true;
				displayPosition = getBarValueRectangleCoordinates( position, barValue );

				//System.out.println("3:Moved "+str+" to END_OF_BAR");
			}
		}

		barValue.setPosition( displayPosition.x, displayPosition.y );
*/
	}


	/** Adds a bar value for the given data item, if barValues are on
	 */
	public void addBarValue( int i, double dataValue, Rectangle2D.Float barRect )
	{
/*
		if( this.showBarValues == false )
		{
			return;
		}

		BarValue barValue = null;

		String str = getBarValueAt( i, dataValue );

		if( str != null )
		{

			float originX = axisChart.getXAxisOrigin();
			float originY = axisChart.getYAxisOrigin();
			float startingCoordinate = axisChart.getDataAxis().getZeroLineCoordinate();

			Rectangle2D.Float bounds = new Rectangle2D.Float();

			bounds.x = originX;
			bounds.y = originY;

			bounds.width = axisChart.getHorizontalAxis().getPixelLength();
			bounds.height = axisChart.getVerticalAxis().getPixelLength();

			if( dataValue < 0 )
			{
				if( isVertical )
				{
					//-- Bound on entire chart.
					bounds.y = originY - bounds.height;
				}
				else
				{
					//-- Bound on entire chart - already okay.
				}
			}
			else
			{
				if( isVertical )
				{
					//-- Bound from starting to top of chart.
					bounds.y -= bounds.height;
					bounds.height -= ( originY - startingCoordinate );
				}
				else
				{
					//-- Bound from starting to right side of chart
					bounds.x = startingCoordinate;
					bounds.width -= ( startingCoordinate - originX );
				}
			}

			barValue = new BarValue( str,
			   barValueFont,
			   fontRenderContext,
			   barRect,
			   barValuePosition,
			   ( dataValue < 0 ) );

			setBarValuePosition( barValue, bounds );

			if( textTagList == null )
			{
				textTagList = new ArrayList();
			}

			textTagList.add( barValue );
		}
*/
	}


	/** renders any barValues
	 */
	public void render( Graphics2D g2d )
	{
		if( textTagList == null || this.showBarValues == false )
		{
			return;
		}

		TextTag tag;
		Paint p = this.barValueFontColor;

		g2d.setFont( this.barValueFont );

		for( int i = 0; i < textTagList.size(); i++ )
		{
			tag = ( TextTag ) textTagList.get( i );
			tag.render( g2d, p );
			// Text tag sets the Paint every time if not null
			// So, we might save a small bit of time by changing
			// this to null after first time.
			p = null;
		}
	}
}
