#pragma once

#include "simpleimage.h"
namespace SimpleImage
{

class CGaussianConvolutorOpenCV  
{

public:
	CGaussianConvolutorOpenCV(float sigma);
	virtual ~CGaussianConvolutorOpenCV(){}

	// Apply the gaussian filter.
	bool Convolve(CSimpleImagef & src);

	bool Convolve(CSimpleImageb & src);

protected:
	float m_sigma;
};

} // namespace SIFT