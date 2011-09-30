// RANSAC.h: interface for the CRANSAC class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_RANSACHOMOGRAPHY_H__3212B64C_ACD6_4418_8436_F0C85D279FD2__INCLUDED_)
#define AFX_RANSACHOMOGRAPHY_H__3212B64C_ACD6_4418_8436_F0C85D279FD2__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "imagematchhomomodel.h"
#include <algorithm>

class CRANSACHomography
{
public:
	CRANSACHomography();
	virtual ~CRANSACHomography();

	// Smallest number of points to be able to fit the model.
	int n;

	// The number of iterations required.
	int k;


	// n: Smallest number of points to be able to fit the model.
	// k: The number of iterations required.
	CRANSACHomography (int n, int k)
	{
		this->n = n;
		this->k = k;
	}

	// ArrayList of Model's, sorted by summed fitting error.
	// model: Model to fit
	// points: List of point data to fit
	// d: Number of nearby points required for a model to be accepted

	std::vector<int> FindBestModel(ImageMatchHomoModel& model, Point2DPairVector& points);

	// Calculate the expected number of draws required when a fraction of
	// 'goodFraction' of the sample points is good and at least 'n' points are
	// required to fit the model. Add 'sdM' times the standard deviation to be
	// sure.
	// n: > 0
	// goodFraction: > 0.0 and <= 1.0
	// sdM: >= 0
	// return the guess for k, the expected number of draws.
	static int GetKFromGoodfraction (int n, double goodFraction, int sdM)
	{
		double result;

		result = pow (goodFraction, -n);
		if (sdM > 0)
			result += sdM * sqrt (1.0 - pow (goodFraction, n));

		return ((int) (result + 0.5));
	}
};

#endif // !defined(AFX_RANSAC_H__3212B64C_ACD6_4418_8436_F0C85D279FD2__INCLUDED_)
