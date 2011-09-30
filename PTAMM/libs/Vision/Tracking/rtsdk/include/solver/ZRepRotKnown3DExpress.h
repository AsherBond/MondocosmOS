// ZRepRotKnown3DExpress.h: interface for the ZRepRotKnown3DExpress class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_ZREPROTKNOWN3DEXPRESS_H__900F8EB8_C1C5_47E0_A933_46ABB5A256C2__INCLUDED_)
#define AFX_ZREPROTKNOWN3DEXPRESS_H__900F8EB8_C1C5_47E0_A933_46ABB5A256C2__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "ZAnyScaleFunction.h"
#include "IRANSACModel.h"
//#include "VideoFrame.h"

class ZRepRotKnown3DExpress  :public ZAnyScaleFunction<double>
{
public:
	ZRepRotKnown3DExpress();
	virtual ~ZRepRotKnown3DExpress();

	virtual double Value (const Wml::GVectord& x);
	
	virtual void Gradient (const Wml::GVectord& x, Wml::GVectord& grad);
	
	virtual void Hesse (const Wml::GVectord& x, Wml::GMatrixd& hesse);

	void SetXVar(const Wml::GVectord& x);

	int GetPointNumber(){return m_points.size();}

	void RemoveOutlier(const Wml::GVectord& x,int N=3,double percent=0.95);	

public:
	double alpha,beta,gama;
	double sinAlpha,cosAlpha,sinBeta,cosBeta,sinGama,cosGama;
	double X,Y,Z;
	double u1,v1;
	std::vector<int> m_LocalVariableIndex;
	std::vector<MatchPoint*>	m_points;
};

#endif // !defined(AFX_ZREPROTKNOWN3DEXPRESS_H__900F8EB8_C1C5_47E0_A933_46ABB5A256C2__INCLUDED_)
