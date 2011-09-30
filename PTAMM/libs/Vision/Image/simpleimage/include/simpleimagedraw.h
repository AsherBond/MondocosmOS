// simpleimageconvert.h: interface for the CSimpleImage class.
//
//////////////////////////////////////////////////////////////////////

#pragma once

#include "simpleimage.h"
#include <limits>
#undef max
#undef min

namespace SimpleImage
{
	template<class T>
	void line(CSimpleImage<T> &src, int x1, int x2, int y1, int y2, byte r, byte g, byte b)
	{
		//////////////////////////////////////////////////////
		// Draws a line using the Bresenham line algorithm
		// Thanks to Jordan DeLozier <JDL>
		//////////////////////////////////////////////////////
		int xinc1,xinc2,yinc1,yinc2;      // Increasing values
		int den, num, numadd,numpixels;   
		int deltax = abs(x2 - x1);        // The difference between the x's
		int deltay = abs(y2 - y1);        // The difference between the y's
		int x = x1;
		int y = y1;

		// Get Increasing Values
		if (x2 >= x1) {                // The x-values are increasing
			xinc1 = 1;
			xinc2 = 1;
		} else {                         // The x-values are decreasing
			xinc1 = -1;
			xinc2 = -1;
		}

		if (y2 >= y1) {                // The y-values are increasing
			yinc1 = 1;
			yinc2 = 1;
		} else {                         // The y-values are decreasing
			yinc1 = -1;
			yinc2 = -1;
		}

		// Actually draw the line
		if (deltax >= deltay)         // There is at least one x-value for every y-value
		{
			xinc1 = 0;                  // Don't change the x when numerator >= denominator
			yinc2 = 0;                  // Don't change the y for every iteration
			den = deltax;
			num = deltax / 2;
			numadd = deltay;
			numpixels = deltax;         // There are more x-values than y-values
		}
		else                          // There is at least one y-value for every x-value
		{
			xinc2 = 0;                  // Don't change the x for every iteration
			yinc1 = 0;                  // Don't change the y when numerator >= denominator
			den = deltay;
			num = deltay / 2;
			numadd = deltax;
			numpixels = deltay;         // There are more y-values than x-values
		}

		for (int curpixel = 0; curpixel <= numpixels; curpixel++)
		{
			// Draw the current pixel
			//SetPixelColor(x,y,color,bSetAlpha);
			if (x >= 0 && x < src._width
				&& y >= 0 && y < src._height)
			{
				T *pixel = src.at(x, y);

				pixel[0] = b;
				pixel[1] = g;
				pixel[2] = r;
			}

			num += numadd;              // Increase the numerator by the top of the fraction
			if (num >= den)             // Check if numerator >= denominator
			{
				num -= den;               // Calculate the new numerator value
				x += xinc1;               // Change the x as appropriate
				y += yinc1;               // Change the y as appropriate
			}
			x += xinc2;                 // Change the x as appropriate
			y += yinc2;                 // Change the y as appropriate
		}
	}

	template<class T>
	void cross(CSimpleImage<T>& src,int x, int y, byte r, byte g, byte b, int radius)
	{
		int i;
		if( y >= 0 && y < src._height)
		{
			for(i=std::max(x-radius, 0);i<=std::min(x+radius, src._width-1);i++)
			{
				T *pixel = src.at(i, y);

				pixel[0] = b;
				pixel[1] = g;
				pixel[2] = r;
			}

		}
		if (x >= 0 && x < src._width)
		{
			for(i=std::max(y-radius, 0);i<=std::min(y+radius, src._height-1);i++)
			{
				T *pixel = src.at(x, i);

				pixel[0] = b;
				pixel[1] = g;
				pixel[2] = r;
			}
		}
	}

}
