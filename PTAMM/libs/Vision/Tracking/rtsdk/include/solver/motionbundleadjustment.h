// MotionBundleAdjustment.h: interface for the CMotionBundleAdjustment class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(MOTIONBUNDLEADJUSTMENT_INCLUDED_)
#define MOTIONBUNDLEADJUSTMENT_INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000


//////////////////////////////////////////////////////////////////////////
#include "../mtbuffer/videoframe.h"
//////////////////////////////////////////////////////////////////////////

#include "WmlMatrix4.h"
#include "WmlMatrix3.h"

#include <list>

/////////////////////////////////////////////////////////////////////////
/// \ingroup Solver Motion Solver
//////////////////////////////////////////////////////////////////////////
class DLL_EXPORT CMotionBundleAdjustment : public videoreadwriter  
{
public:
	CMotionBundleAdjustment(videobuffer* inbuf, videobuffer* outbuf);
	virtual ~CMotionBundleAdjustment();

	CMotionBundleAdjustment(const CMotionBundleAdjustment& obj);
	
	CMotionBundleAdjustment& operator = (const CMotionBundleAdjustment& obj);

	//////////////////////////////////////////////////////////////////////////
	void operator()();


protected:
	bool process(CVideoFrame*);

	void save_history(CVideoFrame* frame);

	void bundle_adjustment_sba();

	//////////////////////////////////////////////////////////////////////////
	/// The intrinsic parameters of the camera.
	//////////////////////////////////////////////////////////////////////////
	Wml::Matrix3d _K;

	/// rotations of previous frames.
	std::list<Wml::Matrix3d>_Rs;


	/// translations of previous frames.
	std::list<Wml::Vector3d> _ts;

	/// matches.
	std::list<std::vector<frame_match> > _framematches;
	
	struct match
	{
	public:
		match(double dx, double dy, int fn):x(dx),y(dy),frameno(fn)
		{

		}
		double x;
		double y;
		int frameno; 
	};

	int _maxframe;

};

#endif // !defined(MOTIONBUNDLEADJUSTMENT_INCLUDED_)
