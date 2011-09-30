#include "StdAfx.h"

void ShowDib256(CDC *pDC, byte* pimage, int x, int y, int width, int height, bool flip)
{
	// 一个DIB包括一个BITMAPINFO和bitmap数据
	// 首先构造BITMAPINFO
	// 有多种方式申请BITMAPINFO需要的空间，这里通过一个数组
	// 有256种灰度颜色，在下面初始化化
	char buf[sizeof(BITMAPINFOHEADER) + sizeof(RGBQUAD)*256];
	BITMAPINFO*  pbmpinfo = (BITMAPINFO*)buf;

	// 填充DIB信息
	pbmpinfo->bmiHeader.biSize = sizeof(BITMAPINFOHEADER);
	pbmpinfo->bmiHeader.biHeight = flip?-height:height;
	pbmpinfo->bmiHeader.biWidth = width;
	pbmpinfo->bmiHeader.biPlanes = 1;
	pbmpinfo->bmiHeader.biBitCount = 8;
	pbmpinfo->bmiHeader.biCompression = BI_RGB;
	pbmpinfo->bmiHeader.biSizeImage = 0; // 256*256;
	pbmpinfo->bmiHeader.biClrUsed = 256;

	// 填充灰度颜色信息，类似调色板，可以使任意颜色
	// 如果这里的灰度颜色少于bitmap需要的，超出部分显示为黑色
	for(int t = 0; t < 256; ++t)
	{
		pbmpinfo->bmiColors[t].rgbBlue = t;
		pbmpinfo->bmiColors[t].rgbGreen = t;
		pbmpinfo->bmiColors[t].rgbRed = t;
		pbmpinfo->bmiColors[t].rgbReserved = 0;
	}

	CDC memDC;
	memDC.CreateCompatibleDC(pDC);
	CBitmap bmp;
	// 以下是将构造好的DIB绘制到DC中，两种方法
	// 1. CreateCompatibleBitmap  和 SetDIBits
    // 2. SetDIBitsToDevice
 	bmp.CreateCompatibleBitmap(pDC, width, height);
	memDC.SelectObject(&bmp);
 	SetDIBits(memDC.GetSafeHdc(), bmp, 0, height, pimage, pbmpinfo, DIB_RGB_COLORS);

//  SetDIBitsToDevice(pDC->GetSafeHdc(), 0, 0, 256, 256, 0, 0, 0, 256, 
// 		pb, pbmpinfo, DIB_RGB_COLORS);

	// 复制到显示器
	pDC->BitBlt(x, y, width, height, &memDC, 0, 0, SRCCOPY);

}