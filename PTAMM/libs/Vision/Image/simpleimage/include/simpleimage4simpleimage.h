// simpleimage4simpleimage.h: interface for the CSimpleImage class.
//
//////////////////////////////////////////////////////////////////////

#pragma once

#include "simpleimage.h"

namespace SimpleImage
{
	template<class T, class T1>
	bool convert(CSimpleImage<T> &simage, CSimpleImage<T1> &simage1)
	{
		if (!simage.is_valid())
		{
			return false;
		}
		int width = simage._width;
		int height = simage._height;


		if (simage._channel == 1)
		{
			simage1.set_size(width, height, 1);

			for (int y = 0; y < height; ++ y)
			{
				for (int x = 0; x < width; ++ x)
				{
					simage1(x, y)[0] = simage(x, y)[0];
				}
			}		
		}
		else
		{
			simage1.set_size(width, height, 3);

			for (int y = 0; y < height; ++ y)
			{
				for (int x = 0; x < width; ++ x)
				{
					T1 *pixel1 = simage1.at(x, y);

					T *pixel = simage.at(x, y);

					pixel1[0] = pixel[0];
					pixel1[1] = pixel[1];
					pixel1[2] = pixel[2];
				}
			}		
		}
		return true;
	}
}
