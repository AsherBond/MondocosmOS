// ZAnyScaleFunction.h: interface for the ZAnyScaleFunction class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_ZANYSCALEFUNCTION_H__293EE8B4_8204_408B_B763_665883AE30F7__INCLUDED_)
#define AFX_ZANYSCALEFUNCTION_H__293EE8B4_8204_408B_B763_665883AE30F7__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
#include <VECTOR>
#include "math/basic/WmlMath/include/WmlLinearSystem.h"

//////////////////////////////////////////////////////////////////////////
/// \ingroup Solver
/// \brief Base class for any expression.
//////////////////////////////////////////////////////////////////////////
template < class T>
class ZAnyScaleFunction  
{
public:
	ZAnyScaleFunction();
	virtual ~ZAnyScaleFunction();

	virtual T Value (const Wml::GVector<T>& x) = 0;

    virtual void Gradient (const Wml::GVector<T>& x, Wml::GVector<T>& grad) = 0;

	virtual void Hesse (const Wml::GVector<T>& x, Wml::GMatrix<T>& hesse) = 0;
};

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////
template <class T>
ZAnyScaleFunction<T>::ZAnyScaleFunction()
{
	
}

template <class T>
ZAnyScaleFunction<T>::~ZAnyScaleFunction()
{
	
}
//////////////////////////////////////////////////////////////////////////
/// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
// typedef ZAnyScaleFunction<double> ZAnyScaleFunctionf;
//////////////////////////////////////////////////////////////////////////

//typedef std::vector<ZAnyScaleFunctionf*>	ZFunctionList;


#endif // !defined(AFX_ZANYSCALEFUNCTION_H__293EE8B4_8204_408B_B763_665883AE30F7__INCLUDED_)
