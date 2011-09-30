// Homography_YExpression.h: interface for the Homography_YExpression class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_HOMOGRAPHY_YEXPRESSION_H__89697F0F_CF11_4094_B483_89B2F4FEFA28__INCLUDED_)
#define AFX_HOMOGRAPHY_YEXPRESSION_H__89697F0F_CF11_4094_B483_89B2F4FEFA28__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "solver/ZAnyScaleFunction.h"

//////////////////////////////////////////////////////////////////////////
/// \ingroup Marker
/// \brief Expression for homography.
//////////////////////////////////////////////////////////////////////////
template <class T>
class Homography_YExpression : public ZAnyScaleFunction<T>  
{
public:
	Homography_YExpression();
	virtual ~Homography_YExpression();

	virtual T Value(const Wml::GVector<T>& x);
 
	virtual void Gradient (const Wml::GVector<T>& x, Wml::GVector<T>& grad);

	virtual void Hesse (const Wml::GVector<T>& x, Wml::GMatrix<T>& hesse);

	void SetParam(T pt1x, T pt1y, T pt2y);

	T Validation(const Wml::GVector<T> &x);
protected:
	T u1;
	T v1;
	T v2;

	bool linear;
};


//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////
template <class T>
Homography_YExpression<T>::Homography_YExpression()
{
	linear = true;
}

template <class T>
Homography_YExpression<T>::~Homography_YExpression()
{
	
}

template <class T>
T Homography_YExpression<T>::Value(const Wml::GVector<T> &x)
{
	if(linear)
	{
		return x[3]*u1+x[4]*v1+x[5]-(x[6]*u1+x[7]*v1+1.0)*v2;
	}
	else
	{
		T v = x[3]*u1+x[4]*v1+x[5];
		
		v/=(x[6]*u1+x[7]*v1+1.0);
		
		return v-v2;
	}
}

template <class T>
void Homography_YExpression<T>::Gradient (const Wml::GVector<T>& x, Wml::GVector<T>& grad)
{
	grad.SetSize(x.GetSize());
	
	if(linear)
	{
		grad[0] = 0.0;
		grad[1] = 0.0;
		grad[2] = 0.0;
		
		grad[3] = u1;
		grad[4] = v1;
		grad[5] = 1.0;
		
		grad[6] = -v2*u1;
		grad[7] = -v2*v1;
	}
	else
	{
		T numerator = x[3]*u1+x[4]*v1+x[5];
		T denominator = x[6]*u1+x[7]*v1+1.0;
		T squareddeno = denominator*denominator;
		
		grad[0] = 0.0;
		grad[1] = 0.0;
		grad[2] = 0.0;
		
		grad[3] = u1/denominator;
		grad[4] = v1/denominator;
		grad[5] = 1.0/denominator;
		
		grad[6] = numerator*u1/squareddeno;
		grad[7] = numerator*v1/squareddeno;
	}
}

template <class T>
void Homography_YExpression<T>::Hesse (const Wml::GVector<T>& x, Wml::GMatrix<T>& hesse)
{
	
}

template <class T>
void Homography_YExpression<T>::SetParam(T pt1x, T pt1y, T pt2y)
{
	u1 = pt1x;
	v1 = pt1y;
	v2 = pt2y;
}

template <class T>
T Homography_YExpression<T>::Validation(const Wml::GVector<T> &x)
{
	T v = x[3]*u1+x[4]*v1+x[5];
	
	v/=(x[6]*u1+x[7]*v1+1.0);
	
	return v-v2;
}
#endif // !defined(AFX_HOMOGRAPHY_YEXPRESSION_H__89697F0F_CF11_4094_B483_89B2F4FEFA28__INCLUDED_)
