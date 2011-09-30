// sifrmarkermotionsolver.h: interface for the CMotionSolver class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_SIFTMARKERMOTIONSOLVER_H__3813827F_FE50_4D3A_909F_4969DA0B5DE6__INCLUDED_)
#define AFX_SIFTMARKERMOTIONSOLVER_H__3813827F_FE50_4D3A_909F_4969DA0B5DE6__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "motionsolver.h"

//////////////////////////////////////////////////////////////////////////
/// \ingroup Solver
/// \brief The motion solver.
//////////////////////////////////////////////////////////////////////////
class DLL_EXPORT CSIFTMarkerMotionSolver: public CMotionSolver  
{
public:
	CSIFTMarkerMotionSolver(videobuffer* inbuf, videobuffer* outbuf,
		resampler_buffer* r_inbuf, resampler_buffer* r_outbuf);
	virtual ~CSIFTMarkerMotionSolver();

	//////////////////////////////////////////////////////////////////////////
	virtual bool ComputePM(CVideoFrame &frame);
};


#endif // !defined(AFX_MOTIONSOLVER_H__3813827F_FE50_4D3A_909F_4969DA0B5DE6__INCLUDED_)
