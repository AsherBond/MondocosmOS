// simpleimageconvert.h: interface for the CSimpleImage class.
//
//////////////////////////////////////////////////////////////////////

#pragma once

#include "simpleimage.h"

#include <cmath>

namespace SimpleImage
{
	template<class T>
	void gradient(CSimpleImage<T> &src, CSimpleImage<T> &gradient)
	{
		//if (!src.is_valid())
		//{
		//	return false;
		//}

		int width = src._width;
		int height = src._height;

		gradient.set_size(width, height, 2);

		if (src._channel == 1)
		{

			for (int y = 1; y < height-1; ++ y)
			{
				for (int x = 1; x < width-1; ++ x)
				{
					T *pixel = gradient.at(x, y);

					pixel[0] = src(x + 1, y)[0] - src(x - 1, y)[0];
					pixel[1] = src(x, y + 1)[0] - src(x, y - 1)[0];
				}
			}	

			// top.
			for (int x = 0; x < width; ++ x)
			{
				T *pixel = gradient.at(x, 0);

				pixel[0] = 0;
				pixel[1] = 0;
			}

			// bottom.
			for (int x = 0; x < width; ++ x)
			{
				T *pixel = gradient.at(x, height - 1);

				pixel[0] = 0;
				pixel[1] = 0;
			}

			// left.
			for (int y = 1; y < height-1; ++ y)
			{
				T *pixel = gradient.at(0, y);

				pixel[0] = 0;
				pixel[1] = 0;
			}

			// right.
			for (int y = 1; y < height-1; ++ y)
			{
				T *pixel = gradient.at(width - 1, 0);

				pixel[0] = 0;
				pixel[1] = 0;
			}
		}
		//else
		//{
		//	return false;
		//}
	
		//return true;
	}

	template<class T>
	void magnitude(CSimpleImage<T> &src, CSimpleImage<T> &magnitude)
	{
		//if (!src.is_valid())
		//{
		//	return false;
		//}

		int width = src._width;
		int height = src._height;

		magnitude.set_size(width, height, 1);

		if (src._channel == 2)
		{

			for (int y = 0; y < height; ++ y)
			{
				for (int x = 0; x < width; ++ x)
				{
					T *pixel = src.at(x, y);

					T gx = pixel[0];
					T gy = pixel[1];
					magnitude(x, y)[0] = std::sqrt(gx*gx + gy*gy);
				}
			}	
		}
		//else
		//{
		//	return false;
		//}

		//return true;
	}

	template<class T>
	void direction(CSimpleImage<T> &src, CSimpleImage<T> &direction)
	{
		//if (!src.is_valid())
		//{
		//	return false;
		//}

		int width = src._width;
		int height = src._height;

		direction.set_size(width, height, 1);

		if (src._channel == 2)
		{

			for (int y = 0; y < height; ++ y)
			{
				for (int x = 0; x < width; ++ x)
				{
					T *pixel = src.at(x, y);

					T gx = pixel[0];
					T gy = pixel[1];

					direction(x, y)[0] = std::atan2(gy, gx);
				}
			}	
		}
		//else
		//{
		//	return false;
		//}

		//return true;
	}

	template<class T>
	inline T magnitude_sqr(const CSimpleImage<T> &src, int x, int y, int c)
	{
		T gx = src(x + 1, y)[c] - src(x - 1, y)[c];
		T gy = src(x, y + 1)[c] - src(x, y - 1)[c];

		return gx*gx + gy*gy;
	}

	template<class T>
	void magnitude(CSimpleImage<T> &src, CSimpleImage<T> &magnitude, int startx, int starty, int endx, int endy)
	{
		//if (!src.is_valid())
		//{
		//	return false;
		//}

		int width = src._width;
		int height = src._height;

		if (src._channel == 2)
		{

			for (int y = starty; y < endy; ++ y)
			{
				for (int x = startx; x < endx; ++ x)
				{
					T *pixel = src.at(x, y);

					T gx = pixel[0];
					T gy = pixel[1];

					magnitude(x, y)[0] = std::sqrt(gx*gx + gy*gy);
				}
			}	
		}
		//else
		//{
		//	return false;
		//}

		//return true;
	}

	template<class T>
	void direction(CSimpleImage<T> &src, CSimpleImage<T> &direction, int startx, int starty, int endx, int endy)
	{
		//if (!src.is_valid())
		//{
		//	return false;
		//}

		int width = src._width;
		int height = src._height;

		if (src._channel == 2)
		{

			for (int y = starty; y < endy; ++ y)
			{
				for (int x = startx; x < endx; ++ x)
				{
					T *pixel = src.at(x, y);

					T gx = pixel[0];
					T gy = pixel[1];

					direction(x, y)[0] = std::atan2(gy, gx);
				}
			}	
		}
		//else
		//{
		//	return false;
		//}

		//return true;
	}
}
