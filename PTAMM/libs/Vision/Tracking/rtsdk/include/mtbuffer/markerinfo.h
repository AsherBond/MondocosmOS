#pragma once

#include <vector>
#include "cameraparameter.h"
#include "framematch.h"
#include "../utility/floattype.h"

//////////////////////////////////////////////////////////////////////////
/// \ingroup Buffer
/// \brief Marker information structure.
///
/// The detected marker information.
//////////////////////////////////////////////////////////////////////////
class marker_info
{
public:
	marker_info()
	{
		memset(_lines, 0, sizeof(double) * 4 * 3);
		_flag = 0;
	}
	/// four lines of the artificial marker. 
	double _lines[4][3];

	SOLVER_FLOAT _homography[3][3];

	/// flag to indicate the type of the marker, artificial or natural. 0 for artificial, 1 for natural.
	int _flag;

	/// the matched points.
	/// For each marker, the 1st is the center, and the other 4
	/// correspond to the 4 corners. This is for render.	
	std::vector<frame_match> _matches;
	std::vector<frame_match> _bounding;

	/// the solved camera parameters.
	camera_parameter<SOLVER_FLOAT> _markercamera;
};