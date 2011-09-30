// simpleimagethreshold.h: interface for the CSimpleImage class.
//
//////////////////////////////////////////////////////////////////////

#pragma once

#include "simpleimage.h"

namespace SimpleImage
{
	template<class T>
	bool threshold(CSimpleImage<T> &srcdest, std::vector<T> threshold)
	{
		if (!srcdest.is_valid())
		{
			return false;
		}

		int width = srcdest._width;
		int height = srcdest._height;


		if (srcdest._channel == 1 && threshold.size() >= 1)
		{
			for (int y = 0; y < height; ++ y)
			{
				for (int x = 0; x < width; ++ x)
				{
					T* pixel = srcdest(x,y);
					
					if (pixel[0] > threshold[0])
					{
						pixel[0] = 255;
					}
					else
					{
						pixel[0] = 0;
					}
				}
			}	
		}
		else if (srcdest._channel == 3 && threshold.size() >= 3)
		{
			for (int y = 0; y < height; ++ y)
			{
				for (int x = 0; x < width; ++ x)
				{
					T* pixel = srcdest(x,y);
					
					if (pixel[0] > threshold[0]
					&& pixel[1] > threshold[1]
					&& pixel[2] > threshold[2])
					{
						pixel[0] = 255;
						pixel[1] = 255;
						pixel[2] = 255;
					}
					else
					{
						pixel[0] = 0;
						pixel[1] = 0;
						pixel[2] = 0;
					}
				}
			}	
		}
		else if (srcdest._channel == 3 && threshold.size() >= 1)
		{
			for (int y = 0; y < height; ++ y)
			{
				for (int x = 0; x < width; ++ x)
				{
					T* pixel = srcdest(x,y);

					if (pixel[0] > threshold[0]
					&& pixel[0] > threshold[1]
					&& pixel[0] > threshold[2])
					{
						pixel[0] = 255;
						pixel[1] = 255;
						pixel[2] = 255;
					}
					else
					{
						pixel[0] = 0;
						pixel[1] = 0;
						pixel[2] = 0;
					}
				}
			}	
		}

		return true;
	}

	template<class T>
	bool threshold(const CSimpleImage<T> &src, CSimpleImage<T> &dest, T threshold)
	{
		if (!src.is_valid())
		{
			return false;
		}	

		dest = src;

		threshold(dest, threshold);

		return true;
	}
}
