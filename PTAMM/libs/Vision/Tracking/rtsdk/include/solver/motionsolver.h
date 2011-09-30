// motionsolver.h: interface for the CMotionSolver class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_MOTIONSOLVER_H__3813827F_FE50_4D3A_909F_4969DA0B5DE6__INCLUDED_)
#define AFX_MOTIONSOLVER_H__3813827F_FE50_4D3A_909F_4969DA0B5DE6__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "IRANSACModel.h"

//////////////////////////////////////////////////////////////////////////
#include "mtbuffer/videoframe.h"
#include "mtbuffer/framesample.h"
//////////////////////////////////////////////////////////////////////////

#include "others/misc/dllexp.h"

#include "math/Basic/WmlMath/include/WmlMatrix4.h"
#include "math/Basic/WmlMath/include/WmlMatrix3.h"

// for output of analysis data
#include <iostream>
#include <fstream>
//////////////////////////////////////////////////////////////////////////
/// \defgroup Solver Motion Solver
//////////////////////////////////////////////////////////////////////////

#include "../utility/floattype.h"

#include "solver.h"

//////////////////////////////////////////////////////////////////////////
/// \ingroup Solver
/// \brief The motion solver.
//////////////////////////////////////////////////////////////////////////
class DLL_EXPORT CMotionSolver: public videoreadwriter  
{
public:
	CMotionSolver(videobuffer* inbuf, videobuffer* outbuf,
		resampler_buffer* r_inbuf, resampler_buffer* r_outbuf);
	CMotionSolver();

	virtual ~CMotionSolver();

	CMotionSolver(const CMotionSolver& obj);
	
	CMotionSolver& operator = (const CMotionSolver& obj);

	//////////////////////////////////////////////////////////////////////////
	void operator()();

	/// \brief Set intrinsic parameters.
	bool SetIntrinsic(int width, int height, int focal);

	std::map<std::string, Wml::Matrix4<SOLVER_FLOAT> > lastPs;

	Wml::Matrix4<SOLVER_FLOAT> lastP;
	bool blast;

	bool outlier;
	//////////////////////////////////////////////////////////////////////////

	
	bool process(CVideoFrame*);

protected:
	//////////////////////////////////////////////////////////////////////////
	/// The intrinsic parameters of the camera.
	//////////////////////////////////////////////////////////////////////////
	Wml::Matrix3<SOLVER_FLOAT> K;

	virtual bool ComputePM(CVideoFrame &frame);

	virtual void Optimize_RT_Robust(Wml::Matrix4<SOLVER_FLOAT>& P, SingleList& singleList, 
		Wml::Matrix3<SOLVER_FLOAT>& K);

protected:
	//  For resampler.
	resampler_buffer* _resampler_outbuf;
	resampler_buffer * _resampler_inbuf;
};


class outdata
{
public:
	outdata():of("dataout.txt", std::ios::out)
	{
		of<<"frame "<<"sr0 "<<"sr1 "<<"sr2 "<<"st0 "<<"st1 "<<"st2 "<<"srmse "
			<<"r0 "<<"r1 "<<"r2 "<<"t0 "<<"t1 "<<"t2 "<<"rmse "<<std::endl;
		frame = -10;
	}
	~outdata()
	{
		of.close();
	}
	void out0(double r0, double r1, double r2, double t0, double t1, double t2, double rmse)
	{
		if(frame>=0)
			of<<frame++<<" "<<r0<<" "<<r1<<" "<<r2<<" "<<t0<<" "<<t1<<" "<<t2<<" "<<rmse;
	}
	void out1(double r0, double r1, double r2, double t0, double t1, double t2, double rmse)
	{
		if(frame>=0)
			of<<" "<<r0<<" "<<r1<<" "<<r2<<" "<<t0<<" "<<t1<<" "<<t2<<" "<<rmse<<" "<<std::endl;
		else
			frame ++;
	}
	int frame;
	std::ofstream of;
};

#endif // !defined(AFX_MOTIONSOLVER_H__3813827F_FE50_4D3A_909F_4969DA0B5DE6__INCLUDED_)
