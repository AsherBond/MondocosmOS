#pragma once

#include "simpleimage.h"
#include <memory.h>

namespace SimpleImage{

class CConvolveMask{

    int m_Dim;
    int m_Middle;
	float m_MaskSum;

	friend class CGaussianConvolutor;

public:	
	CConvolveMask():m_pMask(0), m_Dim(0),m_Middle(0),m_MaskSum(0)
	{};
    CConvolveMask(int dim){
	   CreateMask(dim);
	}
    virtual ~CConvolveMask(){
		if(m_pMask)
			delete []m_pMask;
	}

	void CreateMask(int dim){
	   m_Dim = dim;
	   m_Middle = dim / 2;
	   if(m_pMask)
		   delete []m_pMask;
	   m_pMask = new float[dim];
	   memset(m_pMask, 0, dim * sizeof(float));
	   m_MaskSum = 1;
	}
    
	inline float & At(int idx){
    //   XASSERT(m_pMask);
	   //XASSERT(idx >=0 && idx < m_Dim);
	   return m_pMask[idx];
	}

	inline int Count() const{
		return m_Dim;
	}

protected:
	float * m_pMask;
};

class CGaussianConvolutor  
{
public:
	CGaussianConvolutor(float sigma);
	CGaussianConvolutor(float sigma, int dim);
	virtual ~CGaussianConvolutor(){}

	// Apply the gaussian filter.
	bool Convolve(CSimpleImagef & src);

protected:
	CConvolveMask m_Mask;

	int m_iBoxSize;
	int m_iHalfSize;

	void Convolve1D_Horizontal(CSimpleImagef & Src, CSimpleImagef &Dst);
	void Convolve1D_Vertical(CSimpleImagef & Src,CSimpleImagef &Dst);
};

} // namespace SIFT