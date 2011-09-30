// projectivefilter.h: interface for the CProjectiveFilter class.
//
//////////////////////////////////////////////////////////////////////
#pragma once

//////////////////////////////////////////////////////////////////////////
#include "others/misc/dllexp.h"
#include "../mtbuffer/videoframe.h"
//////////////////////////////////////////////////////////////////////////

#include "../mtbuffer/framesample.h"

//////////////////////////////////////////////////////////////////////////
/// \ingroup SIFT
/// \brief SIFT Feature Match.
//////////////////////////////////////////////////////////////////////////
class DLL_EXPORT CProjectiveFilter : public videoreadwriter
{
public:
	CProjectiveFilter(videobuffer* inbuf, videobuffer* outbuf,
		resampler_buffer* r_outbuf, resampler_pool* r_pool);

	CProjectiveFilter();

	virtual ~CProjectiveFilter();

	CProjectiveFilter(const CProjectiveFilter& obj);

	CProjectiveFilter& operator = (const CProjectiveFilter& obj);
	
	//////////////////////////////////////////////////////////////////////////
	void operator()();
	
	bool process(CVideoFrame*);

protected:
	//  For resampler.
	resampler_buffer* _resampler_outbuf;
	resampler_pool * _resampler_pool;
};