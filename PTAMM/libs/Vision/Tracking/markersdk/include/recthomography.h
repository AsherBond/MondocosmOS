// recthomography.h: interface for the RectHomography class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(RECT_HOMOGRAPHY_INCLUDED_)
#define RECT_HOMOGRAPHY_INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "math/Basic/WmlMath/include/WmlLinearSystem.h"
#include "math/Basic/WmlMath/include/WmlMatrix3.h"

#include "solver/ZEquationOptimizer_LMApproach.h"
#include "Homography_XExpression.h"
#include "Homography_YExpression.h"

#include "utility/floattype.h"

#include <cmath>                    // for trigonometry functions

//////////////////////////////////////////////////////////////////////////
/// \ingroup Marker
/// \brief The homography of rectangles.
///
/// This will compute a homography of the detected rectangle, and build a map function.
/// This is used to build the frontal feature images that can be matched with the preprocessed key points.
//////////////////////////////////////////////////////////////////////////
namespace Pattern
{
template <class T>
class RectHomography  
{
public:
	//////////////////////////////////////////////////////////////////////////
	/// \brief Constructor.
	///
	/// Allocate memory for the homography mapping.
	//////////////////////////////////////////////////////////////////////////
	RectHomography():h(8),hi(9)
	{
		
	}

	//////////////////////////////////////////////////////////////////////////
	/// \brief Copy constructor.
	//////////////////////////////////////////////////////////////////////////
	RectHomography(const RectHomography& _object)
	{
		corners = _object.corners;
		neffcorner = _object.neffcorner;
		
		status = _object.status;
		
		h = _object.h;
		hi = _object.hi;
	}
	//////////////////////////////////////////////////////////////////////////
	/// \brief Assignment operation.
	//////////////////////////////////////////////////////////////////////////
	RectHomography& operator=(const RectHomography& _object)
	{
		if(this != &_object)
		{
			h = _object.h;
			hi = _object.hi;
			
			status = _object.status;
			
			corners = _object.corners;
			neffcorner = _object.neffcorner;
		}
		return *this;
	}
	~RectHomography()
	{

	}


	//////////////////////////////////////////////////////////////////////////
	/// \biref The homograpy matrix maps the circle coordinates to ellipse coordinates.
	//////////////////////////////////////////////////////////////////////////
	Wml::GVector<T> h;			// circle 2 ellipse homography

	//////////////////////////////////////////////////////////////////////////
	/// \biref The homograpy matrix maps the ellipse coordinates to circle coordinates. Inverse of #h.
	//////////////////////////////////////////////////////////////////////////
	Wml::GVector<T> hi;			// ellipse 2 cirle homography

	//////////////////////////////////////////////////////////////////////////
	/// \brief Corner coordinates of the bounding rectangles.
	//////////////////////////////////////////////////////////////////////////
	std::vector<std::pair<T, T> > corners;		// bounding rectangle corner as direction
	std::vector<bool> status;

	//////////////////////////////////////////////////////////////////////////
	/// \brief The best fitted corner index.
	//////////////////////////////////////////////////////////////////////////
	int neffcorner;

	//////////////////////////////////////////////////////////////////////////
	/// \brief Map the circle coordinates to the ellipse coordinates with the homography.
	///
	/// \param x [in|out] The X coordinate.
	/// \param y [in|out] The Y coordinate.
	//////////////////////////////////////////////////////////////////////////
	void Circle2Ellipse(T &x, T &y)
	{
		T tx, ty, tz;
		tx = x*h[0] + y*h[1] + h[2];
		ty = x*h[3] + y*h[4] + h[5];		
		tz = x*h[6] + y*h[7] + 1.0;		
		x = tx/tz;
		y = ty/tz;
	}

	//////////////////////////////////////////////////////////////////////////
	/// \brief Map the ellipse coordinates to the circle coordinates with the inverse homography.
	///
	/// \param x [in|out] The X coordinate.
	/// \param y [in|out] The Y coordinate.
	//////////////////////////////////////////////////////////////////////////
	void Ellipse2Circle(T &x, T &y)
	{
		T tx, ty, tz;
		tx = x*hi[0] + y*hi[1] + hi[2];
		ty = x*hi[3] + y*hi[4] + hi[5];		
		tz = x*hi[6] + y*hi[7] + hi[8];		
		x = tx/tz;
		y = ty/tz;
	}

	//////////////////////////////////////////////////////////////////////////
	/// \brief Compute the homography between the ellipse and a circle.
	///
	/// \param radius [in] The radius of the circle.
	/// \return Whether succeed.
	//////////////////////////////////////////////////////////////////////////
	bool Homography(T radius = 32.0)
	{
		if(corners.size() < 4)
			return false;
			
		// Homography circle to ellipse
		ZEquationOptimizer_LMApproach<T>	optmizer;
		optmizer.SetLamdaFactor(0.0);
		optmizer.SetMaxIteration(10);
		
		ZEquationOptimizer_LMApproach<T>::ZFunctionList	funList;

		T	dMin;

		Homography_XExpression<T> *phomography2dx;
		Homography_YExpression<T> *phomography2dy;
		// 
		
		phomography2dx = new Homography_XExpression<T> ; 
		phomography2dy = new Homography_YExpression<T> ;
		phomography2dx->SetParam(-radius, -radius, corners[0].first);
		phomography2dy->SetParam(-radius, -radius, corners[0].second);
		funList.push_back(phomography2dx);
		funList.push_back(phomography2dy);
		
		// 
		phomography2dx = new Homography_XExpression<T> ;
		phomography2dy = new Homography_YExpression<T> ;
		phomography2dx->SetParam(-radius, radius, corners[1].first);
		phomography2dy->SetParam(-radius, radius, corners[1].second);
		funList.push_back(phomography2dx);
		funList.push_back(phomography2dy);

		//
		phomography2dx = new Homography_XExpression<T> ;
		phomography2dy = new Homography_YExpression<T> ;
		phomography2dx->SetParam(radius, radius, corners[2].first);
		phomography2dy->SetParam(radius, radius, corners[2].second);
		funList.push_back(phomography2dx);
		funList.push_back(phomography2dy);
	
		//
		phomography2dx = new Homography_XExpression<T> ;
		phomography2dy = new Homography_YExpression<T> ;
		phomography2dx->SetParam(radius, -radius, corners[3].first);
		phomography2dy->SetParam(radius, -radius, corners[3].second);
		funList.push_back(phomography2dx);
		funList.push_back(phomography2dy);	
	

		h[0] = 1.0;
		h[1] = 0.0;
		h[2] = 0.0;
		h[3] = 0.0;
		h[4] = 1.0;
		h[5] = 0.0;
		h[6] = 0.0;
		h[7] = 0.0;
		optmizer.Optimize(&funList, h, dMin);

//		printf("\nmin error = %f\n\n", dMin);

		for (int i = 0; i < funList.size(); ++i)
		{
			delete funList[i];
		}
		funList.clear();
// 
// 		// ellipse 2 cirle homography
// 		for(i = 0; i < 8; ++i)
// 		{
// 				printf("h[%d] = %f, ", i, h[i]);
// 		}
		
		Wml::Matrix3<T> mat;
		mat(0 , 0) = h[0];
		mat(0 , 1) = h[1];
		mat(0 , 2) = h[2];
		mat(1 , 0) = h[3];
		mat(1 , 1) = h[4];
		mat(1 , 2) = h[5];
		mat(2 , 0) = h[6];
		mat(2 , 1) = h[7];
		mat(2 , 2) = 1.0;

		Wml::Matrix3<T> imat = mat.Inverse();
		
		hi[0] = imat(0 , 0);
		hi[1] = imat(0 , 1);
		hi[2] = imat(0 , 2);
		hi[3] = imat(1 , 0);
		hi[4] = imat(1 , 1);
		hi[5] = imat(1 , 2);
		hi[6] = imat(2 , 0);
		hi[7] = imat(2 , 1);
		hi[8] = imat(2 , 2);
		
// 		for(i = 0; i < 8; ++i)
// 		{
// 			printf("h[%d] = %f, ", i, h[i]);
// 		}
// 
// 		mat.InvertGaussJordan();
// 		T * data = mat.GetData();
// 		
// // #ifdef _DEBUG
// // 		for(i = 0; i < 8; ++i)
// // 		{
// // 			printf("h[%d] = %f, ", i, h[i]);
// // 		}
// // #endif
// 
// 		for(i = 0; i < 9; ++i)
// 		{
 //			hi[i] = data[i];
// // #ifdef _DEBUG
// // 			printf("hi[%d] = %f, ", i, hi[i]);
// // #endif
 //		}
		return true;
	}
};

}


#endif // !defined(AFX_ELLIPSEPARAMETER_H__A5356419_67C1_4AA3_900A_C2E41C172599__INCLUDED_)
