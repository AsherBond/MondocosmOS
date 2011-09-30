// feature.h: interface for the Feature class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_FEATURE_H__E8246FF9_0705_4D8A_853E_E01B817860F0__INCLUDED_)
#define AFX_FEATURE_H__E8246FF9_0705_4D8A_853E_E01B817860F0__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "utility/pointype.h"

enum {PATTERNSIZE = 64, BINSIZE = 72, FEATURESIZE = 64};
//enum {PATTERNWIDTH = 64, PATTERNHEIGHT = 64, BINSIZE = 72, FEATUREWIDTH = 64, FEATUREHEIGHT = 64};

#ifdef PI
#undef PI
#endif

/// \brief pi.
const float PI = 3.141593f;

namespace FeatureSpace
{
//////////////////////////////////////////////////////////////////////////
/// \ingroup Marker
/// \brief The state of detected features.
//////////////////////////////////////////////////////////////////////////
typedef enum
{
	UNCALIBRATED,
	CALIBRATED,
	INVALID,
} FEATURESTATE;

}
#endif // !defined(AFX_FEATURE_H__E8246FF9_0705_4D8A_853E_E01B817860F0__INCLUDED_)
