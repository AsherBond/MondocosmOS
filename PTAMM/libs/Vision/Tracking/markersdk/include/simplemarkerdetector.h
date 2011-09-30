// simplemarkerdetector.h: interface for the CSimpleMarkerDetector class.
//
//////////////////////////////////////////////////////////////////////
#pragma once

#include "utility/floattype.h"

#include "others/misc/dllexp.h"

#include <vector>

#include "simpleimage.h"

#include "mtbuffer/patternparameter.h"

namespace Pattern
{
//////////////////////////////////////////////////////////////////////////
/// \ingroup Marker
/// \brief Find rectangle patterns in the scene with edge detection.
///
/// Detect circles in the scene, which are surrounded by black rectangles.
/// The method divides into 5 steps.
/// 
/// <ul>
///  <li> Threshold the image.
///     <ol>
///     <li> Compute the mean value of each channel.
///     <li> Set the pixel to white(1.0) if all the channels are greater than mean values, otherwise black (0.0).
///     </ol>
///  <li> Extract the contour with a border following method.
///     <ol>
///     <li> Extract all contours.
///     <li> Ignore contours too large or too small.
///     </ol>
///  <li>
///  <li> Ignore some error contour, and sort the remaining contours based on the liklihood to be a good circle.
///     <ol>
///     <li> For each conour, sample some pixels, fit a conic function, if it's a ellipse and a bouding rectangle can be found, \n
///          it's a good circle pattern.
///     <li> The liklihood is the distance from the center of the contour pixels to the center of the ellipse, the smaller, the better.
///     </ol>
///  <li>
///  <li> Fit the resulting ellipse.
///     <ol>
///     <li> Fit a conic function based on the contour pixels.
///     <li> Compute the ellipse parameters from the conic parameters.
///     <li> Refine the rectangle corners by Harris corner detector, which operates in a small window around the rough intersection.
///     </ol>
///  <li>
///  </ul>
//////////////////////////////////////////////////////////////////////////
class DLL_EXPORT CSimpleMarkerDetector
{
public:
	CSimpleMarkerDetector();
	virtual ~CSimpleMarkerDetector();

	/// \brief Public interface for running the method.
	virtual bool Fit(CSimpleImageb& simage, std::vector<pattern_parameter<MARKER_FLOAT> > &pats);
};
}