package org.jCharts.axisChart.axis.scale;import org.jCharts.properties.AxisProperties;import org.jCharts.properties.PropertyException;public class ChartScale{	/**	 * The largest number in the dataset.	 */	double yMax;	/**	 * The smallest number in the dataset.	 */	double yMin;	/**	 * The difference between two points on adjacent grid lines.	 */	double yDelta = 0.0;	/**	 * The value which the first grid line represents.	 */	double yStart = 0.0;	/**	 * The value which the last grid line represents.	 */	double yEnd = 0.0;	/**	 * The suggested value for the rounding of the data.	 */	int rounding = 0;	/**	 * The number of segments that the suggested yStart, yEnd and yDelta produce.	 */	int segments = 20;	public static void main( String[] args )	{		ChartScale c = new ChartScale();		c.yMin = -30;		c.yMax = 200;		c.calculate();		System.out.println( "yStart= " + c.yStart + "  yEnd= " + c.yEnd + "  segments= " + c.segments + "  rounding= " + c.rounding );	}	public ChartScale()	{	}	/**	 * Constructor.  Creates a ChartScale object and initializes all of its properties as appropriate for the given	 * data's minimum and maximum values.	 *	 * @param data double[] the data for which you would like suggested graph values	 */	public ChartScale( double[][] data )	{		this.yMax = getMax( data );		this.yMin = getMin( data );		calculate();	}	/**	 * Compute yDelta, yStart, yEnd, segments and rounding.	 */	private void calculate()	{		// In the following line, note that Math.log is actually Natural Logarithm.		// log base a of b = ln b / ln a => log base 10 of x = ln x / ln 10		// yDelta is a nice, even, power-of-ten difference between yMax and yMin		yDelta = Math.pow( 10.0, Math.round( Math.log( yMax - yMin ) / Math.log( 10 ) ) );		yStart = yMin - (yMin % yDelta); // the first point on an even power of ten on or below yMin		yEnd = yMax - (yMax % yDelta) + yDelta; // the first point on an even power of ten on or above yMax		// At this point, we have a yStart, yEnd and yDelta that are guaranteed to include all points in the graph.		// However, there are probably too few segments.  Perhaps only two.  Reduce the delta according to the		// number of segments that we already have.  Note that we will reduce delta only in a way that produces some		// nice round numbers.  yDelta is a power of ten, and therefore will alway divide nicely by 10, 4, and 2.		// We will need to recalculate yStart and yEnd to get rid of any unneeded blank segments.		// Count the number of segments this gives us and use this number to determine the factor by which to reduce		// yDelta.  Note: to get larger numbers of segments, fiddle with the if statements below.		segments = (int) ((yEnd - yStart) / yDelta);		if( segments <= 2 )		{			// we need 10 times as many segments (before recalculating start and end, which may reduce the number)			yDelta = yDelta / 10.0;		}		else if( segments <= 5 )		{			// we need 4 times as many segments (before recalculating start and end, which may reduce the number)			yDelta = yDelta / 4.0;		}		else if( segments <= 10 )		{			// we need twice as many segments (before recalculating start and end, which may reduce the number)			yDelta = yDelta / 2.0;		}		// Recalc yStart and yEnd to match with new delta.		yStart = yMin - (yMin % yDelta);		yEnd = yMax - (yMax % yDelta) + yDelta;		segments = (int) ((yEnd - yStart) / yDelta);		rounding = (int) (Math.round( Math.log( yDelta ) / Math.log( 10 ) ) - 3);	}	/**	 * Helper method that finds the largest double in the 2D array of doubles.	 *	 * @param data double[][] to look into for the max	 * @return double the largest value found	 */	private double getMax( double[][] data )	{		double max = Double.MIN_VALUE;		for( int i = 0; i < data.length; i++ )		{			double[] doubles = data[ i ];			for( int j = 0; j < doubles.length; j++ )			{				double aDouble = doubles[ j ];				if( aDouble > max )				{					max = aDouble;				}			}		}		return max;	}	/**	 * Helper method that finds the smallest double in the 2D array of doubles.	 *	 * @param data double[][] to look into for the min	 * @return double the smallest value found	 */	private double getMin( double[][] data )	{		double min = Double.MAX_VALUE;		for( int i = 0; i < data.length; i++ )		{			double[] doubles = data[ i ];			for( int j = 0; j < doubles.length; j++ )			{				double aDouble = doubles[ j ];				if( aDouble < min )				{					min = aDouble;				}			}		}		return min;	}	/**	 * Accessor for the yMax property.  This property represents the largest value in the dataset.	 *	 * @return double largest value in the associated dataset	 */	public double getYMax()	{		return yMax;	}	/**	 * Accessor for the yMin property.  This property represents the smallest value in the dataset.	 *	 * @return double smallest value in the associated dataset	 */	public double getYMin()	{		return yMin;	}	/**	 * Accessor for the yDelta property.  The difference between any two points on adjacent grid lines.	 *	 * @return double grid line spacing	 */	public double getYDelta()	{		return yDelta;	}	/**	 * Accessor for the yStart property.  The value which the first grid line represents.	 *	 * @return double y axis value for the bottom horizontal grid line.	 */	public double getYStart()	{		return yStart;	}	/**	 * Accessor for the yEnd property.  The value which the last grid line represents.	 *	 * @return double y axis value for the top horizontal grid line.	 */	public double getYEnd()	{		return yEnd;	}	/**	 * Accessor for the rounding property.  The suggested value for the rounding of the data.	 *	 * @return double rounding value suggestion.	 */	public int getRounding()	{		return rounding;	}	/**	 * Accessor for the segments property.  The number of segments that the suggested yStart, yEnd and yDelta produce.	 *	 * @return double segments in the graph as suggested.	 */	public int getSegments()	{		return segments;	}	/**	 * Creates and returns a new AxisProperties object based on the internally calculated values of yStart, yDelta,	 * segments and rounding.	 *	 * @return AxisProperties with yStart, yDelta, segments and rounding set.	 */	public AxisProperties getAxisProperties() throws PropertyException	{/*        AxisProperties axisProperties = new AxisProperties(yStart, yDelta);        axisProperties.setYAxisRoundValuesToNearest(rounding);        axisProperties.setYAxisNumItems(segments);        return axisProperties;*/		return null;	}}