#include "StdAfx.h"

void ShowDib256(CDC *pDC, byte* pimage, int x, int y, int width, int height, bool flip)
{
	// һ��DIB����һ��BITMAPINFO��bitmap����
	// ���ȹ���BITMAPINFO
	// �ж��ַ�ʽ����BITMAPINFO��Ҫ�Ŀռ䣬����ͨ��һ������
	// ��256�ֻҶ���ɫ���������ʼ����
	char buf[sizeof(BITMAPINFOHEADER) + sizeof(RGBQUAD)*256];
	BITMAPINFO*  pbmpinfo = (BITMAPINFO*)buf;

	// ���DIB��Ϣ
	pbmpinfo->bmiHeader.biSize = sizeof(BITMAPINFOHEADER);
	pbmpinfo->bmiHeader.biHeight = flip?-height:height;
	pbmpinfo->bmiHeader.biWidth = width;
	pbmpinfo->bmiHeader.biPlanes = 1;
	pbmpinfo->bmiHeader.biBitCount = 8;
	pbmpinfo->bmiHeader.biCompression = BI_RGB;
	pbmpinfo->bmiHeader.biSizeImage = 0; // 256*256;
	pbmpinfo->bmiHeader.biClrUsed = 256;

	// ���Ҷ���ɫ��Ϣ�����Ƶ�ɫ�壬����ʹ������ɫ
	// �������ĻҶ���ɫ����bitmap��Ҫ�ģ�����������ʾΪ��ɫ
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
	// �����ǽ�����õ�DIB���Ƶ�DC�У����ַ���
	// 1. CreateCompatibleBitmap  �� SetDIBits
    // 2. SetDIBitsToDevice
 	bmp.CreateCompatibleBitmap(pDC, width, height);
	memDC.SelectObject(&bmp);
 	SetDIBits(memDC.GetSafeHdc(), bmp, 0, height, pimage, pbmpinfo, DIB_RGB_COLORS);

//  SetDIBitsToDevice(pDC->GetSafeHdc(), 0, 0, 256, 256, 0, 0, 0, 256, 
// 		pb, pbmpinfo, DIB_RGB_COLORS);

	// ���Ƶ���ʾ��
	pDC->BitBlt(x, y, width, height, &memDC, 0, 0, SRCCOPY);

}