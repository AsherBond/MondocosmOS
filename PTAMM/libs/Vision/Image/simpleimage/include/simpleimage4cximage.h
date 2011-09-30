// simpleimageconvert.h: interface for the CSimpleImage class.
//
//////////////////////////////////////////////////////////////////////

#pragma once

#include "simpleimage.h"
#include "vision/image/cximage/include/ximage.h"
//#include "z:/vision/image/cximage/lib/cximagelink.h"
#include <string>

namespace SimpleImage
{
	template<class T>
	bool convert(CxImage &cximage, CSimpleImage<T> &simage)
	{
		if (!cximage.IsValid())
		{
			return false;
		}

		int width = cximage.GetWidth();
		int height = cximage.GetHeight();

		if (cximage.GetBpp() == 24)
		{
			simage.set_size(width, height, 3);
			for (int y = 0; y < height; ++ y)
			{
				for (int x = 0; x < width; ++ x)
				{
					RGBQUAD &color = cximage.GetPixelColor(x, y);
					T *pixel = simage.at(x, y);

					pixel[2] = color.rgbRed;
					pixel[1] = color.rgbGreen;
					pixel[0] = color.rgbBlue;
				}
			}		
		}
		else if (cximage.IsGrayScale())
		{
			simage.set_size(width, height, 1);
			for (int y = 0; y < height; ++ y)
			{
				for (int x = 0; x < width; ++ x)
				{
					simage(x, y)[0] = cximage.GetPixelGray(x, y);
				}
			}		
		}
		else if (cximage.IsIndexed())
		{
			simage.set_size(width, height, 1);
			for (int y = 0; y < height; ++ y)
			{
				for (int x = 0; x < width; ++ x)
				{
					simage(x, y)[0] = cximage.GetPixelIndex(x, y);
				}
			}		
		}
		return true;
	}

	//bool convert(CxImage &cximage, CSimpleImageb &simage)
	//{
	//	if (!cximage.IsValid())
	//	{
	//		return false;
	//	}

	//	int width = cximage.GetWidth();
	//	int height = cximage.GetHeight();

	//	simage.set_data(cximage.GetBits(), width, height, cximage.GetBpp()/8);
	//	return true;
	//}

	template<class T>
	bool convert(CSimpleImage<T> &simage, CxImage &cximage)
	{
		if (!simage.is_valid())
		{
			return false;
		}

		int width = simage._width;
		int height = simage._height;

		if (simage._channel == 3)
		{
			cximage.Create(width, height, simage._channel*8);

			for (int y = 0; y < height; ++ y)
			{
				for (int x = 0; x < width; ++ x)
				{
					T *pixel = simage.at(x, y);

					RGBQUAD color;
					color.rgbRed = pixel[2];
					color.rgbGreen = pixel[1];
					color.rgbBlue = pixel[0];

					cximage.SetPixelColor(x, y, color);
				}
			}		
		}
		else if(simage._channel == 1)
		{
			cximage.Create(width, height, simage._channel*8);
			cximage.SetGrayPalette();
			for (int y = 0; y < height; ++ y)
			{
				for (int x = 0; x < width; ++ x)
				{
					cximage.SetPixelIndex(x, y, simage(x, y)[0]);
				}
			}		
		}

		return false;
	}

	//bool convert(CSimpleImageb &simage, CxImage &cximage)
	//{
	//	if (!simage.is_valid())
	//	{
	//		return false;
	//	}

	//	int width = simage._width;
	//	int height = simage._height;
	//	
	//	cximage.CreateFromArray(simage._data, width, height, simage._channel*8, simage._effwidth, false);
	//	return true;
	//}

	std::string extension(const std::string &name);

	int type(const std::string &ext);

	template <class T>
	bool save2ximage(CSimpleImage<T> &simage, std::string file)
	{
		CxImage image;
		convert(simage, image);
		return image.Save(file.c_str(), type(extension((file))));
	}

	template <class T>
	bool load2ximage(CSimpleImage<T> &simage, std::string file)
	{
		CxImage image;
		image.Load(file.c_str());
		return convert(image, simage);
	}
}
