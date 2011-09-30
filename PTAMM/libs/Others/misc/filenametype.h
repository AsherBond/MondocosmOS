#ifndef _FILENAMETYPE_H
#define _FILENAMETYPE_H
#include "vision/image/CxImage/include/ximage.h"
#include <string>

namespace FileName{
	inline std::string FindExtension(const std::string &name)
	{
		int len = name.size();
		int i;
		for (i = len-1; i >= 0; i--)
		{
			if (name[i] == '.')
			{
				return name.substr(i+1, name.size() - i - 1);
			}
		}
		return std::string("");
	}

	inline int FindType(const std::string &ext)
	{
		int type = 0;
		if (ext == "bmp")					type = CXIMAGE_FORMAT_BMP;
	#if CXIMAGE_SUPPORT_JPG
		else if (ext=="jpg"||ext=="jpeg")	type = CXIMAGE_FORMAT_JPG;
	#endif
	#if CXIMAGE_SUPPORT_GIF
		else if (ext == "gif")				type = CXIMAGE_FORMAT_GIF;
	#endif
	#if CXIMAGE_SUPPORT_PNG
		else if (ext == "png")				type = CXIMAGE_FORMAT_PNG;
	#endif
	#if CXIMAGE_SUPPORT_MNG
		else if (ext=="mng"||ext=="jng")	type = CXIMAGE_FORMAT_MNG;
	#endif
	#if CXIMAGE_SUPPORT_ICO
		else if (ext == "ico")				type = CXIMAGE_FORMAT_ICO;
	#endif
	#if CXIMAGE_SUPPORT_TIF
		else if (ext=="tiff"||ext=="tif")	type = CXIMAGE_FORMAT_TIF;
	#endif
	#if CXIMAGE_SUPPORT_TGA
		else if (ext=="tga")				type = CXIMAGE_FORMAT_TGA;
	#endif
	#if CXIMAGE_SUPPORT_PCX
		else if (ext=="pcx")				type = CXIMAGE_FORMAT_PCX;
	#endif
	#if CXIMAGE_SUPPORT_WBMP
		else if (ext=="wbmp")				type = CXIMAGE_FORMAT_WBMP;
	#endif
	#if CXIMAGE_SUPPORT_WMF
		else if (ext=="wmf"||ext=="emf")	type = CXIMAGE_FORMAT_WMF;
	#endif
	#if CXIMAGE_SUPPORT_J2K
		else if (ext=="j2k"||ext=="jp2")	type = CXIMAGE_FORMAT_J2K;
	#endif
	#if CXIMAGE_SUPPORT_JBG
		else if (ext=="jbg")				type = CXIMAGE_FORMAT_JBG;
	#endif
	#if CXIMAGE_SUPPORT_JP2
		else if (ext=="jp2"||ext=="j2k")	type = CXIMAGE_FORMAT_JP2;
	#endif
	#if CXIMAGE_SUPPORT_JPC
		else if (ext=="jpc"||ext=="j2c")	type = CXIMAGE_FORMAT_JPC;
	#endif
	#if CXIMAGE_SUPPORT_PGX
		else if (ext=="pgx")				type = CXIMAGE_FORMAT_PGX;
	#endif
	#if CXIMAGE_SUPPORT_RAS
		else if (ext=="ras")				type = CXIMAGE_FORMAT_RAS;
	#endif
	#if CXIMAGE_SUPPORT_PNM
		else if (ext=="pnm"||ext=="pgm"||ext=="ppm") type = CXIMAGE_FORMAT_PNM;
	#endif
		else type = CXIMAGE_FORMAT_UNKNOWN;

		return type;
	}

	inline int FindCxImageType(const std::string &filename)
	{
		return FindType(FindExtension(filename.c_str()));
	}
}
#endif