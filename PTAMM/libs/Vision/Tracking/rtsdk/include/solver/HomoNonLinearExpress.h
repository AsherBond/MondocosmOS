// HomoNonLinearExpress.h: interface for the HomoNonLinearExpress class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_HOMONONLINEAREXPRESS_H__8F7CC22E_F337_44FE_B249_06406A9B56F7__INCLUDED_)
#define AFX_HOMONONLINEAREXPRESS_H__8F7CC22E_F337_44FE_B249_06406A9B56F7__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "ZAnyScaleFunction.h"
#include "WmlMathLib.h"

class HomoNonLinearExpress : public ZAnyScaleFunction<double>  
{
public:
	HomoNonLinearExpress();
	virtual ~HomoNonLinearExpress();

	virtual double Value (const Wml::GVectord& x);

    virtual void Gradient (const Wml::GVectord& x, Wml::GVectord& grad);

	virtual void Hesse (const Wml::GVectord& x, Wml::GMatrixd& hesse);
	
	void SetXVar(const Wml::GVectord& x);

public:
	double u1,v1;
	double X,Y,Z;
	double H00, H01, H02, H10, H11, H12, H20, H21;
	std::vector<Wml::Vector2d>	m_pt2dList;
	std::vector<Wml::Vector3d>	m_pt3dList;
	std::vector<int> m_LocalVariableIndex;
};

#endif // !defined(AFX_HOMONONLINEAREXPRESS_H__8F7CC22E_F337_44FE_B249_06406A9B56F7__INCLUDED_)
