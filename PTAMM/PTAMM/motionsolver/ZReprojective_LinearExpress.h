// ZReprojective_LinearExpress.h: interface for the ZReprojective_LinearExpress class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_ZREPROJECTIVE_LINEAREXPRESS_H__DD286E90_FEE2_43FC_B77E_72C9012955E4__INCLUDED_)
#define AFX_ZREPROJECTIVE_LINEAREXPRESS_H__DD286E90_FEE2_43FC_B77E_72C9012955E4__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "ZAnyScaleFunction.h"
#include "math/basic/WmlMath/include/WmlMatrix4.h"
#include "math/basic/WmlMath/include/Wmlvector2.h"

class ZReprojective_LinearExpress : public ZAnyScaleFunction<double>  
{
public:
	ZReprojective_LinearExpress();
	virtual ~ZReprojective_LinearExpress();

	virtual double Value (const Wml::GVectord& x);
	
	virtual void Gradient (const Wml::GVectord& x, Wml::GVectord& grad);
	
	virtual void Hesse (const Wml::GVectord& x, Wml::GMatrixd& hesse);

	void SetXVar(const Wml::GVectord& x);

	int GetPointNumber(){return m_points.size();}	

	void SetP(Wml::Matrix4d P);


public:	
	double X,Y,Z;
	double u1,v1;
	double P00, P01, P02, P10, P11, P12, P20, P21, P22, P03, P13, P23;
	double w;
	std::vector<int> m_varIndex;
	std::vector<double> wList;
	std::vector<Wml::Vector2d>	m_points;
	std::vector<Wml::Matrix4d> PList;
};

#endif // !defined(AFX_ZREPROJECTIVE_LINEAREXPRESS_H__DD286E90_FEE2_43FC_B77E_72C9012955E4__INCLUDED_)
