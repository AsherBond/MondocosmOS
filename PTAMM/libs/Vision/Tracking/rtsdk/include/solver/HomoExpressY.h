// HomoExpressY.h: interface for the HomoExpressY class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_HOMOEXPRESSY_H__A0D0867A_920B_460C_B481_3E2D6000B60D__INCLUDED_)
#define AFX_HOMOEXPRESSY_H__A0D0867A_920B_460C_B481_3E2D6000B60D__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "ZAnyScaleFunction.h"

class HomoExpressY : public ZAnyScaleFunction<double>  
{
public:
	HomoExpressY();
	virtual ~HomoExpressY();

	virtual double Value (const Wml::GVectord& x);

    virtual void Gradient (const Wml::GVectord& x, Wml::GVectord& grad);

	virtual void Hesse (const Wml::GVectord& x, Wml::GMatrixd& hesse);

	void SetLocalVar(double u1,double v1,double u2,double v2);
protected:
	double u1,v1,u2,v2;
};

#endif // !defined(AFX_HOMOEXPRESSY_H__A0D0867A_920B_460C_B481_3E2D6000B60D__INCLUDED_)
