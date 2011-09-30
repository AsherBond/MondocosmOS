// HomoTransform.h: interface for the HomoTransform class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_HOMOTRANSFORM_H__8FD90C81_54E8_4035_90BE_D5D7157B8FBA__INCLUDED_)
#define AFX_HOMOTRANSFORM_H__8FD90C81_54E8_4035_90BE_D5D7157B8FBA__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "WmlMathLib.h"
#include "IRANSACModel.h"

class HomoTransform  : public Wml::Matrix3d
{
public:
	HomoTransform();
	virtual ~HomoTransform();

	static HomoTransform BuildTransform(SingleList& singleList);
	static void RefineTransform(SingleList& singleList,HomoTransform& trans);
};

#endif // !defined(AFX_HOMOTRANSFORM_H__8FD90C81_54E8_4035_90BE_D5D7157B8FBA__INCLUDED_)
