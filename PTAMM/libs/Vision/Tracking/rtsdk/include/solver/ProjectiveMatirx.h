// ProjectiveMatirx.h: interface for the ProjectiveMatirx class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_PROJECTIVEMATIRX_H__66DB268C_1693_4BF0_AA8F_49AAACA9FB1C__INCLUDED_)
#define AFX_PROJECTIVEMATIRX_H__66DB268C_1693_4BF0_AA8F_49AAACA9FB1C__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "math/Basic/WmlMath/lib/wmlmathlink.h"
#include "IRANSACModel.h"

class ProjectiveMatirx  : public Wml::Matrix4d
{
public:
	ProjectiveMatirx();
	virtual ~ProjectiveMatirx();

	Wml::Vector3d Multiply(const Wml::Vector3d v);

	static ProjectiveMatirx BuildTransform(SingleList& singleList);
	static void RefineTransform(SingleList& singleList,ProjectiveMatirx& trans);
};

#endif // !defined(AFX_PROJECTIVEMATIRX_H__66DB268C_1693_4BF0_AA8F_49AAACA9FB1C__INCLUDED_)
