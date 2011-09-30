// MarkerlessMotionSolver.h: interface for the CMarkerlessMotionSolver class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_MARKERLESSMOTIONSOLVER_H__B110B7AB_6C83_45C7_BDE1_310C5308DE59__INCLUDED_)
#define AFX_MARKERLESSMOTIONSOLVER_H__B110B7AB_6C83_45C7_BDE1_310C5308DE59__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "motionsolver.h"

//////////////////////////////////////////////////////////////////////////
/// \ingroup Solver
/// \brief The motion solver.
//////////////////////////////////////////////////////////////////////////
class DLL_EXPORT CMarkerlessMotionSolver : public CMotionSolver
{
public:
	CMarkerlessMotionSolver(videobuffer* inbuf, videobuffer* outbuf, resampler_buffer* r_inbuf, resampler_buffer* r_outbuf);
	CMarkerlessMotionSolver();

	virtual ~CMarkerlessMotionSolver();


	bool ComputePM(CVideoFrame &frame);

		void _UpdateCam(CVideoFrame* frame, Wml::Matrix4<SOLVER_FLOAT>& P);

	int _CheckByLastCam(CVideoFrame* pFrame, double threshold);

	void Optimize_RT_Robust(camera_parameter<SOLVER_FLOAT> &camera, Wml::Matrix4<SOLVER_FLOAT>& P, SingleList& singleList, Wml::Matrix3<SOLVER_FLOAT>& K);

	void SetDefaultCam(CVideoFrame& frame);

	double GoodPoints(CVideoFrame* pFrame, Wml::Matrix4<SOLVER_FLOAT>& P, SingleList& matchList, 
		Wml::Matrix3<SOLVER_FLOAT>& K, double sigma=5.0);
};

#endif // !defined(AFX_MARKERLESSMOTIONSOLVER_H__B110B7AB_6C83_45C7_BDE1_310C5308DE59__INCLUDED_)
