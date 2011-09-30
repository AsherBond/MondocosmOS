#include "math/basic/wmlmath/include/wmlmatrix.h"
#include "IRANSACModel.h"

class RotateCameraTracker
{
public:
	RotateCameraTracker(){}
	~RotateCameraTracker(){}

	Wml::Matrix3d RobustOptimizeR_Known3D(SingleList matchlist, Wml::Matrix3d &K, int width, int height, Wml::Matrix3d &homography);
};