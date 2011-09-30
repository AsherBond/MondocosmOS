// HomoExpressX.h: interface for the HomoExpressX class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_HOMOEXPRESSX_H__B8B67ED8_439F_482B_B78F_78F23278AABA__INCLUDED_)
#define AFX_HOMOEXPRESSX_H__B8B67ED8_439F_482B_B78F_78F23278AABA__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "ZAnyScaleFunction.h"

class HomoExpressX : public ZAnyScaleFunction<double>  
{
public:
	HomoExpressX();
	virtual ~HomoExpressX();

	virtual double Value (const Wml::GVectord& x);

    virtual void Gradient (const Wml::GVectord& x, Wml::GVectord& grad);

	virtual void Hesse (const Wml::GVectord& x, Wml::GMatrixd& hesse);

	void SetLocalVar(double u1,double v1,double u2,double v2);
protected:
	double u1,v1,u2,v2;
};

#endif // !defined(AFX_HOMOEXPRESSX_H__B8B67ED8_439F_482B_B78F_78F23278AABA__INCLUDED_)
