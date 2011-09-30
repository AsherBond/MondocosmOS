// Rep_Known3D_Express_X.h: interface for the Rep_Known3D_Express_X class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_REP_KNOWN3D_EXPRESS_X_H__5D373C30_CC8B_41CB_AF78_F2DA2BE11EDF__INCLUDED_)
#define AFX_REP_KNOWN3D_EXPRESS_X_H__5D373C30_CC8B_41CB_AF78_F2DA2BE11EDF__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "ZAnyScaleFunction.h"
#include "../utility/floattype.h"

//template <class T>
class Rep_Known3D_Express_X  : public ZAnyScaleFunction<SOLVER_FLOAT>
{
public:
	Rep_Known3D_Express_X();
	virtual ~Rep_Known3D_Express_X();

	virtual double Value (const Wml::GVectord& x);

    virtual void Gradient (const Wml::GVectord& x, Wml::GVectord& grad);

	virtual void Hesse (const Wml::GVectord& x, Wml::GMatrixd& hesse);

	void SetParam(double du1,double dv1,double dX,double dY,double dZ);

	void SetXVar(const Wml::GVectord& x);


protected:
	double u1,v1;
	double X,Y,Z;
	double P00,P01,P02,P10,P11,P12,P20,P21,P22,P03,P13;
};

#endif // !defined(AFX_REP_KNOWN3D_EXPRESS_X_H__5D373C30_CC8B_41CB_AF78_F2DA2BE11EDF__INCLUDED_)
