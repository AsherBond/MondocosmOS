// ZRotOrthogonalWeightExpress.h: interface for the ZRotOrthogonalWeightExpress class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_ZROTORTHOGONALWEIGHTEXPRESS_H__3A5DD308_2844_408C_942C_BA4E811A49E7__INCLUDED_)
#define AFX_ZROTORTHOGONALWEIGHTEXPRESS_H__3A5DD308_2844_408C_942C_BA4E811A49E7__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "ZAnyScaleFunction.h"
#include "WmlMatrix3.h"

class ZRotOrthogonalWeightExpress : public ZAnyScaleFunction<double>  
{
public:
	ZRotOrthogonalWeightExpress();
	virtual ~ZRotOrthogonalWeightExpress();

	virtual double Value (const Wml::GVectord& x);
	
	virtual void Gradient (const Wml::GVectord& x, Wml::GVectord& grad);
	
	virtual void Hesse (const Wml::GVectord& x, Wml::GMatrixd& hesse);

	void SetXVar(const Wml::GVectord& x);

	void SetParam(const Wml::Matrix3d& R,double weight);

public:
	double r11,r12,r13,r21,r22,r23,r31,r32,r33;
	double e0,e1,e2,e3;
	double w;
	std::vector<int> m_LocalVariableIndex;
};

#endif // !defined(AFX_ZROTORTHOGONALWEIGHTEXPRESS_H__3A5DD308_2844_408C_942C_BA4E811A49E7__INCLUDED_)
