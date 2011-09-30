#pragma once

#include <vector>
#include "framematch.h"

//////////////////////////////////////////////////////////////////////////
/// \ingroup Buffer
/// \brief Marker information structure.
///
/// The detected marker information.
//////////////////////////////////////////////////////////////////////////
class siftmarker
{
public:
	siftmarker()
	{
		
	}
	/// the matched points.
	/// For each marker, the 1st is the center, and the other 4
	/// correspond to the 4 corners. This is for render.	
	std::vector<frame_match> _matches;
	std::vector<frame_match> _outlier;

	/// the solved camera parameters.
	camera_parameter<SOLVER_FLOAT> _markercamera;


	//////////////////////////////////////////////////////////////////////////
	/// define the local frame.
	std::vector<frame_match> _bounding;

	SOLVER_FLOAT _homography[3][3];
};
