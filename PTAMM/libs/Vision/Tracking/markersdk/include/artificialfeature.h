// artificialfeature.h: interface for the Feature class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(ARTIFICIALFEATURE_H_INCLUDED_)
#define ARTIFICIALFEATURE_H_INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include <vector>
#include <string>
#include <fstream>

#include "others/misc/dllexp.h"
#include "feature.h"
#include "mtbuffer/framematch.h"

namespace FeatureSpace
{
	//////////////////////////////////////////////////////////////////////////
	/// \ingroup Marker
	/// \brief The state of detected features.
	//////////////////////////////////////////////////////////////////////////
	// typedef enum
	// {
	// 	UNCALIBRATED,
	// 	CALIBRATED,
	// 	INVALID,
	// } FEATURESTATE;

	//////////////////////////////////////////////////////////////////////////
	/// \ingroup Marker
	/// \brief A feature is a nomalized image of the detected marker pattern in the scene.
	///
	/// The feature may be converted to keypoints during preprocess, or may be matched with the keypoints
	/// and used for solving.
	//////////////////////////////////////////////////////////////////////////
	class DLL_EXPORT ArtificialFeature  
	{
	public:
		/// \brief Constructor.
		ArtificialFeature();

		ArtificialFeature(ArtificialFeature const& obj);

		ArtificialFeature& operator=(ArtificialFeature const& obj);

		/// \brief Destructor.
		~ArtificialFeature();

		//////////////////////////////////////////////////////////////////////////
		/// \brief The pattern image.
		///
		/// Using the rectangle homography, the pattern image is built
		/// pixel by pixel from the scene image.
		//////////////////////////////////////////////////////////////////////////
		unsigned char *_data;

		//////////////////////////////////////////////////////////////////////////
		/// \brief Matched to which keypoint.
		///
		/// Index to the keypoint base, otherwise -1.
		//////////////////////////////////////////////////////////////////////////
		matchkey   _matchkeypoint;

		//////////////////////////////////////////////////////////////////////////
		/// \biref Ignored percentage area of the rectangle.
		///
		//////////////////////////////////////////////////////////////////////////
		float _ignore;

		//////////////////////////////////////////////////////////////////////////
		/// \brief The detected center and the corners.
		///
		/// The corners of the rectangle. If it's converted to keypoints
		/// during preprocess, it must have all the four corners detected.
		//////////////////////////////////////////////////////////////////////////
		std::vector<d5Point> _corners;
		std::vector<d2Point> _oricorners;

		//////////////////////////////////////////////////////////////////////////
		/// \brief State of the feature.
		///
		///	During preprocess, UNCALIBRATED means the 3D structure cannot be recovered,
		/// CALIBRATED means the 3D structure of the points are recovered.
		/// During realtime tracking, UNCALIBRATED means the feacture cannot be found
		/// in the keypoint base, CALIBRATED it's matched to some keypoint and contains
		/// the 3D structur of the points.
		//////////////////////////////////////////////////////////////////////////
		FEATURESTATE	_state;

	};
}
#endif // !defined(AFX_FEATURE_H__E8246FF9_0705_4D8A_853E_E01B817860F0__INCLUDED_)
