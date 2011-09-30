// Estimate3DStructure.h: interface for the Estimate3DStructure class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_ESTIMATE3DSTRUCTURE_H__C0C44F2D_C3FA_4569_BDE9_A6CCDD155D1B__INCLUDED_)
#define AFX_ESTIMATE3DSTRUCTURE_H__C0C44F2D_C3FA_4569_BDE9_A6CCDD155D1B__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include <vector>

#include "math/basic/WmlMath/include/WmlMatrix3.h"
#include "math/basic/WmlMath/include/WmlMatrix4.h"
#include "math/basic/WmlMath/include/Wmlvector2.h"
#include "math/basic/WmlMath/include/Wmlvector3.h"

double Estimate3D(std::vector<Wml::Vector2d> &points_2d, 
				  std::vector<Wml::Matrix4d> &cameras,
				  std::vector<Wml::Matrix3d> &Ks,
				  Wml::Vector3d &initx);

double Estimate3D(std::vector<Wml::Vector2d> &points_2d,
				  std::vector<Wml::Matrix4d> &cameras, 
				  std::vector<Wml::Matrix3d> &Ks,
				  Wml::Vector3d &initx,
				  std::vector<int>& keylist);

double Track_RMSE(Wml::Vector2d &point_2d, Wml::Matrix4d &P, 
				  Wml::Matrix3d &K,
				  Wml::Vector3d &v_3d);

#endif // !defined(AFX_ESTIMATE3DSTRUCTURE_H__C0C44F2D_C3FA_4569_BDE9_A6CCDD155D1B__INCLUDED_)
