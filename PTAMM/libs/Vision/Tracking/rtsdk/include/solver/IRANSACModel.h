// IRANSACModel.h: interface for the IRANSACModel class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_IRANSACMODEL_H__EBB05838_4451_443E_B279_483F3A10E8DC__INCLUDED_)
#define AFX_IRANSACMODEL_H__EBB05838_4451_443E_B279_483F3A10E8DC__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include <VECTOR>
#include "math/Basic/WmlMath/include/WmlMathLib.h"

class MatchPoint{
public:
	Wml::Vector2d v_2d;
	Wml::Vector3d v_3d;
	int index;
};

typedef std::vector<MatchPoint*>	SingleList;

class IRANSACModel
{
public:
	IRANSACModel();
	virtual ~IRANSACModel();

	// Fit the model to the samples given. The number of samples is equal
	// to or larger than the smallest number of points required for a fit
	// ('n').
	// Return true if the fit can be done, false otherwise.
	virtual IRANSACModel* Clone() = 0;
	
	virtual bool FitModel (SingleList& singleList) = 0;
	// Return the fitting error of a single point against the current
	// model.	

	virtual double FittingErrorSingle (MatchPoint*) = 0;
	
	// Threshhold the given fit error of a point.
	// Return true if the fitting error is small enough and the point is
	//     fitting.
	// Return false if the point is not fitting.
	virtual bool ThreshholdPoint (double fitError) = 0;
	
	// The overall fitting error of all points in FittingGround. This
	// value is calculated by averaging all individual fitting errors of
	// the points in the FittingGround.
	double m_fittingErrorSum;

	SingleList* m_fittingGround;
};

#endif // !defined(AFX_IRANSACMODEL_H__EBB05838_4451_443E_B279_483F3A10E8DC__INCLUDED_)
