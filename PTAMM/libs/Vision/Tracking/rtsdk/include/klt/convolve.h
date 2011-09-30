/*********************************************************************
 * convolve.h
 *********************************************************************/

#ifndef _CONVOLVE_H_
#define _CONVOLVE_H_

#include "klt.h"
#include "klt_util.h"

void _KLTToFloatImage(
  KLT_PixelType *img,
  int ncols, int nrows,
  _KLT_FloatImage floatimg);

void _KLTComputeGradients(
  _KLT_FloatImage img,
  float sigma,
  _KLT_FloatImage gradx,
  _KLT_FloatImage grady);

void _KLTComputeGradients_Direct(
  _KLT_FloatImage img,
  float sigma,
  _KLT_FloatImage gradx,
  _KLT_FloatImage grady);


void _KLTGetKernelWidths(
  float sigma,
  int *gauss_width,
  int *gaussderiv_width);

void _KLTComputeSmoothedImage(
  _KLT_FloatImage img,
  float sigma,
  _KLT_FloatImage smooth);

#define MAX_KERNEL_WIDTH 	71

typedef struct  {
	int width;
	float data[MAX_KERNEL_WIDTH];
}  ConvolutionKernel;

void _computeKernels(
					 float sigma,
					ConvolutionKernel *gauss,
  ConvolutionKernel *gaussderiv);

void _convolveSeparate(
							  _KLT_FloatImage imgin,
							  ConvolutionKernel horiz_kernel,
							  ConvolutionKernel vert_kernel,
  _KLT_FloatImage imgout);

#endif
