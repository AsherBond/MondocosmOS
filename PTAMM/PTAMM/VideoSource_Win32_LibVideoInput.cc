// Copyright 2010 Isis Innovation Limited
// This VideoSource for Win32 uses the videoInput library 0.1995 by Theodore Watson
// available at 
// http://muonics.net/school/spring05/videoInput/


#include "VideoSource.h"
#include <videoInput.h> // External lib
#include <gvars3/instances.h>
#include <cvd/utility.h>
#define WIN32_MEAN_AND_LEAN
#include <windows.h>

using namespace std;
using namespace CVD;
using namespace GVars3;

namespace PTAMM {

struct VideoInputInfo
{
	videoInput *pVideoInput;
	int nDevice;
};

VideoSource::VideoSource()
{
	VideoInputInfo *pInfo = new VideoInputInfo;
	mptr = (void*) pInfo;

	pInfo->pVideoInput =  new videoInput;
	pInfo->nDevice = GV3::get<int>("VideoInput.DeviceNumber", 0, HIDDEN);;
	int nIdealFrameRate = GV3::get<int>("VideoInput.IdealFrameRate", 30, HIDDEN);
	ImageRef irIdealSize = GV3::get<ImageRef>("VideoInput.IdealSize", ImageRef(640,480), HIDDEN);

	pInfo->pVideoInput->setIdealFramerate(pInfo->nDevice, nIdealFrameRate);
	pInfo->pVideoInput->setupDevice(pInfo->nDevice, irIdealSize.x, irIdealSize.y);

	mirSize.x = pInfo->pVideoInput->getWidth(pInfo->nDevice);
	mirSize.y = pInfo->pVideoInput->getHeight(pInfo->nDevice);
}

void VideoSource::GetAndFillFrameBWandRGB(Image<CVD::byte> &imBW, Image<CVD::Rgb<CVD::byte> > &imRGB)
{
	imRGB.resize(mirSize);
	imBW.resize(mirSize);

	VideoInputInfo *pInfo = (VideoInputInfo*) mptr;
	while(!pInfo->pVideoInput->isFrameNew(pInfo->nDevice))
		Sleep(1);

	pInfo->pVideoInput->getPixels(pInfo->nDevice, (CVD::byte*) imRGB.data(), true, true);
	copy(imRGB, imBW);
}

ImageRef VideoSource::Size()
{
	return mirSize;
}

}
