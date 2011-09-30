// MotionSmoother.h: interface for the CMotionSmoother class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_MOTIONSMOOTHER_H__43B2CDA3_AC15_482C_9701_E32DCA1DD269__INCLUDED_)
#define AFX_MOTIONSMOOTHER_H__43B2CDA3_AC15_482C_9701_E32DCA1DD269__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000


#include "../solver/IRANSACModel.h"

//////////////////////////////////////////////////////////////////////////
#include "../mtbuffer/videoframe.h"
//////////////////////////////////////////////////////////////////////////

#include "WmlMatrix4.h"
#include "WmlMatrix3.h"

// for output of analysis data
#include <iostream>
#include <fstream>
//////////////////////////////////////////////////////////////////////////
/// \defgroup Solver Motion Solver
//////////////////////////////////////////////////////////////////////////

#include "../utility/floattype.h"

#include "RT_3DKnown_SmoopthExpress.h"

#include "ZOptimizerLM.h"
#include "solver.h"

class DLL_EXPORT CMotionSmoother : public videoreadwriter  
{
public:
	CMotionSmoother(videobuffer* inbuf, videobuffer* outbuf);
	virtual ~CMotionSmoother();

	CMotionSmoother(const CMotionSmoother& obj);
	
	CMotionSmoother& operator = (const CMotionSmoother& obj);

	//////////////////////////////////////////////////////////////////////////
	void operator()();


protected:
	bool process(CVideoFrame*);

	bool SmoothMotion(CVideoFrame* frame);

	void _SmoothOptimize(Wml::GVector<SOLVER_FLOAT>& x,RT_3DKnown_SmoopthExpress<SOLVER_FLOAT>& m_fundMExpress,
		ZOptimizerLM<SOLVER_FLOAT>* m_pOptimizerLM, int iMaxIter, int iStep);

	void _Rectify(CVideoFrame* frame, SingleList& matchList);

	void _GetCam(CVideoFrame* frame, Wml::GVector<SOLVER_FLOAT>& x);

	void _UpdateCam(CVideoFrame* frame, Wml::GVector<SOLVER_FLOAT>& x);

	int _Filter(SingleList& matchList, Wml::Matrix4<SOLVER_FLOAT>& P, float threshold);
	//////////////////////////////////////////////////////////////////////////
	/// The intrinsic parameters of the camera.
	//////////////////////////////////////////////////////////////////////////
	Wml::Matrix3<SOLVER_FLOAT> K;

	// parameters of last frame.
	static Wml::Matrix4<SOLVER_FLOAT> lastP;
	
	static bool blast;
};

#endif // !defined(AFX_MOTIONSMOOTHER_H__43B2CDA3_AC15_482C_9701_E32DCA1DD269__INCLUDED_)
