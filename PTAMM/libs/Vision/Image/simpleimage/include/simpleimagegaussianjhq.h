#pragma once

#include "simpleimage.h"

namespace SimpleImage
{

class CGaussianConvolutorJHQ 
{

public:
	CGaussianConvolutorJHQ(float sigma);
	virtual ~CGaussianConvolutorJHQ(){}

	// Apply the gaussian filter.
	bool Convolve(CSimpleImagef & src);

protected:

	void Gaussian_conv(CSimpleImagef &src, float sigma);

	void convolve(float* dst_pt, const float* src_pt, int M, int N, const float* filter_pt, int W);

	void econvolve(float* dst_pt, const float* src_pt, int M, int N, const float* filter_pt, int W);
protected:
	float m_sigma;
};

} // namespace SIFT