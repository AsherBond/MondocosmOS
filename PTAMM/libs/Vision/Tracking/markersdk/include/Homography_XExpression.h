// Homography_XExpression.h: interface for the Homography_XExpression class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_HOMOGRAPHY_XEXPRESSION_H__2830B4AA_24FF_4E0B_A300_C2E38A8E6B1A__INCLUDED_)
#define AFX_HOMOGRAPHY_XEXPRESSION_H__2830B4AA_24FF_4E0B_A300_C2E38A8E6B1A__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "solver/ZAnyScaleFunction.h"

//////////////////////////////////////////////////////////////////////////
/// \ingroup Marker
/// \brief Expression for homography.
//////////////////////////////////////////////////////////////////////////
template <class T>
class Homography_XExpression : public ZAnyScaleFunction<T>  
{
public:
	Homography_XExpression();
	virtual ~Homography_XExpression();

	virtual T Value(const Wml::GVector<T>& x);
 
	virtual void Gradient (const Wml::GVector<T>& x, Wml::GVector<T>& grad);

	virtual void Hesse (const Wml::GVector<T>& x, Wml::GMatrix<T>& hesse);

	void SetParam(T pt1x, T pt1y, T pt2x);

	virtual T Validation(const Wml::GVector<T> &x);

protected:
	T u1;
	T v1;
	T u2;

	bool linear;
};

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

template <class T>
Homography_XExpression<T>::Homography_XExpression()
{
	linear = true;
}

template <class T>
Homography_XExpression<T>::~Homography_XExpression()
{
	
}

template <class T>
T Homography_XExpression<T>::Value(const Wml::GVector<T> &x)
{
	
	if(linear)
	{
		return x[0]*u1+x[1]*v1+x[2]-(x[6]*u1+x[7]*v1+1.0)*u2;
	}
	else
	{
		T v = x[0]*u1+x[1]*v1+x[2];
		
		v/=(x[6]*u1+x[7]*v1+1.0);
		return v-u2;
	}
}

template <class T>
void Homography_XExpression<T>::Gradient (const Wml::GVector<T>& x, Wml::GVector<T>& grad)
{
	grad.SetSize(x.GetSize());
	if(linear)
	{
		
		grad[0] = u1;
		grad[1] = v1;
		grad[2] = 1.0;
		
		grad[3] = 0.0;
		grad[4] = 0.0;
		grad[5] = 0.0;
		
		grad[6] = -u2*u1;
		grad[7] = -u2*v1;
	}
	else
	{
		T numerator = x[0]*u1+x[1]*v1+x[2];
		T denominator = x[6]*u1+x[7]*v1+1.0;
		T squareddeno = denominator*denominator;
		
		grad[0] = u1/denominator;
		grad[1] = v1/denominator;
		grad[2] = 1.0/denominator;
		
		grad[3] = 0.0;
		grad[4] = 0.0;
		grad[5] = 0.0;
		
		grad[6] = numerator*u1/squareddeno;
		grad[7] = numerator*v1/squareddeno;
	}
}

template <class T>
void Homography_XExpression<T>::Hesse (const Wml::GVector<T>& x, Wml::GMatrix<T>& hesse)
{
	
}

template <class T>
void Homography_XExpression<T>::SetParam(T pt1x, T pt1y, T pt2x)
{
	u1 = pt1x;
	v1 = pt1y;
	u2 = pt2x;
}


template <class T>
T Homography_XExpression<T>::Validation(const Wml::GVector<T> &x)
{
	T v = x[0]*u1+x[1]*v1+x[2];
	
	v/=(x[6]*u1+x[7]*v1+1.0);
	return v-u2;
}

#endif // !defined(AFX_HOMOGRAPHY_XEXPRESSION_H__2830B4AA_24FF_4E0B_A300_C2E38A8E6B1A__INCLUDED_)
