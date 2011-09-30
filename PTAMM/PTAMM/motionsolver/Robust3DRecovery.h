#pragma once

#include "math/basic/wmlmath/include/wmlmatrix4.h"
#include "math/basic/wmlmath/include/wmlvector3.h"
#include "math/basic/wmlmath/include/wmlvector2.h"
#include "math/basic/wmlmath/include/wmlgvector.h"

#include <vector>
#include "math/basic/WmlMath/include/WmlMatrix3.h"

class CRobust3DRecovery
{
public:
	CRobust3DRecovery(void);
	~CRobust3DRecovery(void);

	/// \brief Robust estimate the R and t and scale.
	std::vector<int> generate(std::vector<Wml::Vector2d> &points_2d, std::vector<Wml::Matrix4d> &cameras,
		std::vector<Wml::Matrix3d> &Ks,
		Wml::Vector3d& point_3d, int min_sample, double threshold, int iteration);
};
