// artificialfeaturematch.h: interface for the CArtificialFeatureMatch class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(ARTIFICIAL_FEATUREMATCH_H_INCLUDED)
#define ARTIFICIAL_FEATUREMATCH_H_INCLUDED

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
#include <map>

#include "others/misc/dllexp.h"
#include "utility/floattype.h"

//////////////////////////////////////////////////////////////////////////
#include "mtbuffer/videoframe.h"
#include "mtbuffer/thread.h"
//////////////////////////////////////////////////////////////////////////

#include "recthomography.h"
#include "markerkeypointbase.h"

//////////////////////////////////////////////////////////////////////////
/// \ingroup Marker
/// \brief Features match with keypoints.
///
/// Convert markers to features, and match them with the keypoints.
//////////////////////////////////////////////////////////////////////////
class DLL_EXPORT CArtificialFeatureMatch  : public videoreadwriter
{
public:
	CArtificialFeatureMatch(videobuffer* inbuf, videobuffer* outbuf);
	virtual ~CArtificialFeatureMatch();

	CArtificialFeatureMatch(const CArtificialFeatureMatch& obj);
	
	CArtificialFeatureMatch& operator = (const CArtificialFeatureMatch& obj);

	/// \brief The thread interface.
	void operator()();
	
	/// \brief Process the buffer item.
	bool process(CVideoFrame*);

	/// \brief Convert the ellipse patter to feature.
	static void Pattern2Feature(CSimpleImagef& image,
		std::vector<Pattern::RectHomography<MARKER_FLOAT> >& patterns, 
		std::vector<FeatureSpace::ArtificialFeature>& features );
	
	
	/// \brief Search for the detected feature pattern in the keypoint base.
	static void SearchForFeature(std::vector<FeatureSpace::ArtificialFeature>& features,
		FeatureSpace::MarkerKeypointBase& KeyBase,
		std::map<int, int> &featurekey);
	
protected:

	//////////////////////////////////////////////////////////////////////////
	/// \brief Generated features.
	//////////////////////////////////////////////////////////////////////////
	std::vector<FeatureSpace::ArtificialFeature> _features;
	
	//////////////////////////////////////////////////////////////////////////
	/// \brief Found, <feature index, key index>
	//////////////////////////////////////////////////////////////////////////
	std::map<int, int> _featurekey;

	CSimpleImagef _grey_image;
};

#endif // !defined(AFX_FEATUREMATCH_H__952CEDDA_8FC6_4B16_AE25_718C821A77D3__INCLUDED_)
