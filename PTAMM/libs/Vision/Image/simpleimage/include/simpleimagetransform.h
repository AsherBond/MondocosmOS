#pragma once

#include "simpleimage.h"
#include <cmath>
namespace SimpleImage
{	
	float pi = 3.1416;

	template<class T>
	// in angle.
	bool shearx(CSimpleImage<T> &src, CSimpleImage<T> &dst, float xshear)
	{
		xshear = xshear/180.0f*pi;
		float tan_angle_x = std::tan(xshear);

		int newwidth = src._width + src._height*std::fabs(tan_angle_x) + 0.5;
		int newheight = src._height;

		dst.set_size(newwidth, newheight, src._channel);

		float centerx = dst._width / 2.0f;
		float centery = dst._height / 2.0f;

		float oldcenterx = src._width / 2.0f;

		for (int y = 0; y < dst._height; ++ y)
		{
			for (int x = 0; x < dst._width; ++ x)
			{
				int originx = (x-centerx) + tan_angle_x*(y-centery) + 0.5f + oldcenterx;
				
				if (originx >= 0 && originx < src._width)
				{
					for (int c = 0; c < dst._channel; ++ c )
					{
						dst(x, y, c) = src(originx, y, c);
					}
				}
				else
				{
					for (int c = 0; c < dst._channel; ++ c )
					{
						dst(x, y, c) = 255.0f;
					}					
				}
			}
		}

		return true;
	}

	template<class T>
	// in angle.
	bool sheary(CSimpleImage<T> &src, CSimpleImage<T> &dst, float yshear)
	{
		yshear = yshear/180.0f*pi;
		float tan_angle_y = std::tan(yshear);

		int newwidth = src._width;
		int newheight = src._height + src._width*std::fabs(tan_angle_y) + 0.5;

		dst.set_size(newwidth, newheight, src._channel);

		float centerx = dst._width / 2.0f;
		float centery = dst._height / 2.0f;

		float oldcentery = src._height / 2.0f;

		for (int y = 0; y < dst._height; ++ y)
		{
			for (int x = 0; x < dst._width; ++ x)
			{
				int originy = tan_angle_y*(x-centerx) + (y-centery) + 0.5f + oldcentery;

				if (originy >= 0 && originy < src._height)
				{
					for (int c = 0; c < dst._channel; ++ c )
					{
						dst(x, y, c) = src(x, originy, c);
					}
				}
				else
				{
					for (int c = 0; c < dst._channel; ++ c )
					{
						dst(x, y, c) = 255.0f;
					}					
				}
			}
		}

		return true;
	}

	template<class T>
	// in angle.
	bool shear(CSimpleImage<T> &src, CSimpleImage<T> &dst, float xshear, float yshear)
	{
		xshear = xshear/180.0f*pi;
		yshear = yshear/180.0f*pi;

		float tan_angle_x = std::tan(xshear);
		float tan_angle_y = std::tan(yshear);

		float newwidth = src._width + src._height*std::fabs(tan_angle_x) + 0.5;
		float newheight = src._height + src._width*std::fabs(tan_angle_y) + 0.5;

		dst.set_size((int)newwidth, (int)newheight, src._channel);

		float centerx = dst._width / 2.0f;
		float centery = dst._height / 2.0f;

		float oldcenterx = src._width / 2.0f;
		float oldcentery = src._height / 2.0f;

		float xscale = newwidth/src._width;
		float yscale = newheight/src._height;

		for (int y = 0; y < dst._height; ++ y)
		{
			for (int x = 0; x < dst._width; ++ x)
			{
				T* dstpixel = dst.at(x, y);

				int originx = ((x-centerx) + tan_angle_x*(y-centery)) + oldcenterx + 0.5f;
				int originy = (tan_angle_y*(x-centerx) + (y-centery)) + oldcentery + 0.5f;
				if (originx >= 0 && originx < src._width && originy >= 0 && originy < src._height)
				{ 
					T* srcpixel = src.at(originx, originy);
					
					for (int c = 0; c < dst._channel; ++ c )
					{
						dstpixel[c] = srcpixel[c];
					}
				}
				else
				{
					for (int c = 0; c < dst._channel; ++ c )
					{
						dstpixel[c] = 255.0f;
					}					
				}
			}
			break;
		}

		return true;
	}
}