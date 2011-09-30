// simpleimagecolor.h: interface for the CSimpleImage class.
//
//////////////////////////////////////////////////////////////////////

#pragma once

#include "simpleimage.h"
#include <vector>
namespace SimpleImage
{
	template<class T, class T1>
	bool grayscale(CSimpleImage<T> &src, CSimpleImage<T1> &dest)
	{
		static const float rw = 0.299f, gw = 0.587f, bw = 0.114f;

		if (!src.is_valid())
		{
			return false;
		}

		int width = src._width;
		int height = src._height;

		dest.set_size(width, height, 1);

		if (src._channel == 1)
		{
			for (int y = 0; y < height; ++ y)
			{
				for (int x = 0; x < width; ++ x)
				{
					dest(x, y)[0] = src(x, y)[0];
				}
			}	
		}
		else if (src._channel == 3)
		{
			for (int y = 0; y < height; ++ y)
			{
				for (int x = 0; x < width; ++ x)
				{
					T *pixel = src.at(x, y);

					dest(x, y)[0] = rw * pixel[2] + gw * pixel[1] + bw * pixel[0];
				}
			}	
		}
		return true;
	}

	template<class T, class T1>
	bool gray2rgb(CSimpleImage<T> &src, CSimpleImage<T1> &dst)
	{
		if (!src.is_valid())
		{
			return false;
		}

		int width = src._width;
		int height = src._height;


		if (src._channel == 1)
		{
			dst.set_size(width, height, 3);
			for (int y = 0; y < height; ++ y)
			{
				for (int x = 0; x < width; ++ x)
				{
					T *dstpixel = dst.at(x, y);
					T *srcpixel = src.at(x, y);

					dstpixel[0] = srcpixel[0];
					dstpixel[1] = srcpixel[0];
					dstpixel[2] = srcpixel[0];
				}
			}	
		}
		else if (src._channel == 3)
		{
			dst = src;
		}
		return true;
	}

	template<class T>
	bool grayscale(CSimpleImage<T> &srcdest)
	{
		static const float rw = 0.299f, gw = 0.587f, bw = 0.114f;
		if (!srcdest.is_valid())
		{
			return false;
		}

		int width = srcdest._width;
		int height = srcdest._height;

		if (srcdest._channel == 1)
		{
			return true;
		}
		else if (srcdest._channel == 3)
		{
			CSimpleImage<T> dest;
			dest.set_size(width, height, 1);

			for (int y = 0; y < height; ++ y)
			{
				for (int x = 0; x < width; ++ x)
				{
					T *pixel = srcdest.at(x, y);

					dest(x, y)[0] = rw * pixel[2] + gw * pixel[1] + bw * pixel[0];
				}
			}	

			srcdest = dest;
		}
		return true;
	}

	template<class T>
	bool split(CSimpleImage<T> &srcdest, std::vector<CSimpleImage<T> > &channels)
	{
		if (!srcdest.is_valid())
		{
			return false;
		}

		int width = srcdest._width;
		int height = srcdest._height;

		channels.resize(srcdest._channel);

		for (int c = 0; c < srcdest._channel; ++ c)
		{
			CSimpleImage<T> &dest = channels[c];
			dest.set_size(width, height, 1);

			for (int y = 0; y < height; ++ y)
			{
				for (int x = 0; x < width; ++ x)
				{
					dest.at(x, y)[0] = srcdest.at(x, y)[c];
				}
			}	
		}
		return true;
	}

	template<class T>
	bool merge(std::vector<CSimpleImage<T> > &channels, CSimpleImage<T> &srcdest)
	{
		if (channels.size() == 0)
		{
			return false;
		}

		int width = channels[0]._width;
		int height = channels[0]._height;

		srcdest.set_size(width, height, channels.size());

		for (int c = 0; c < srcdest._channel; ++ c)
		{
			CSimpleImage<T> &src = channels[c];

			for (int y = 0; y < height; ++ y)
			{
				for (int x = 0; x < width; ++ x)
				{
					srcdest.at(x, y)[c] = src.at(x, y)[0];
				}
			}	
		}
		return true;
	}
}
