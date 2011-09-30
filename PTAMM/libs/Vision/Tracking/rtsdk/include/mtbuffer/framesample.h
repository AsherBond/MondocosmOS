// framesample.h: interface for the frame_sample class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_FRAMESAMPLE_H__017B5DEA_C403_49C5_B458_3E314B200A6E__INCLUDED_)
#define AFX_FRAMESAMPLE_H__017B5DEA_C403_49C5_B458_3E314B200A6E__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "others/misc/dllexp.h"

#include "../utility/floattype.h"

#include "mtbuffer.h"
#include "objectpool.h"
#include "cameraparameter.h"
#include "timestamp.h"

#include "Math/Basic/WmlMath/include/WmlGMatrix.h"

namespace SIFT
{
	class CKeypoint;
}


//////////////////////////////////////////////////////////////////////////
/// \ingroup MTBUFFER
/// \brief This is for resampling the keypoint, to handle the unprepared area with the known structure.
//////////////////////////////////////////////////////////////////////////
class DLL_DLL_EXPORT frame_sample: public time_stamp
{
public:
	frame_sample();
	~frame_sample();

	camera_parameter<SOLVER_FLOAT> _camera;
	std::vector<SIFT::CKeypoint *> _keypoint;
};

typedef mtbuffer<frame_sample> resampler_buffer;
typedef object_pool<frame_sample> resampler_pool;

#endif // !defined(AFX_FRAMESAMPLE_H__017B5DEA_C403_49C5_B458_3E314B200A6E__INCLUDED_)
