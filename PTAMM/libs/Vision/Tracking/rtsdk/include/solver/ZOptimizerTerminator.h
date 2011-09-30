// ZOptimizerTerminator.h: interface for the ZOptimizerTerminator class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_ZOPTIMIZERTERMINATOR_H__79FB6643_187B_424D_B5D2_C2104473D09D__INCLUDED_)
#define AFX_ZOPTIMIZERTERMINATOR_H__79FB6643_187B_424D_B5D2_C2104473D09D__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
#include "math/Basic/WmlMath/include/WmlLinearSystem.h"

//////////////////////////////////////////////////////////////////////////
/// \ingroup Solver
/// \brief The termination condition of Levenberg-Marquardt Optimization.
//////////////////////////////////////////////////////////////////////////
template <class T>
class ZOptimizerTerminator  
{
public:
	ZOptimizerTerminator();
	virtual ~ZOptimizerTerminator();

public:
	/// \brief Set the parameters of the optimization process.
	void RegisterParameters(Wml::GVector<T>* pCurrx, T* pCurrFun, T* pNextFun,Wml::GVector<T>* pDir);

	/// \brief Interface for termination condition.
	virtual bool ShouldTerminate () const = 0;


protected:
	/// \brief Current expression value.
	const T *m_pCurrFuncValue;

	/// \brief Next expression value.
	const T *m_pNextFuncValue;

	/// \brief Current optimization result.
	Wml::GVector<T>* m_pCurrX;

	/// \brief Descending direction.
	Wml::GVector<T>* m_pDir;
};


//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

template <class T>
ZOptimizerTerminator<T>::ZOptimizerTerminator()
{
	
}

template <class T>
ZOptimizerTerminator<T>::~ZOptimizerTerminator()
{
	
}

//////////////////////////////////////////////////////////////////////////
/// Tell the parameters of the optimization process.
///
/// \param pCurrX	[in] Pointer to the current variable values.
/// \param pCurrFun [in] Pointer to the current expression value.
/// \param pNextFun [in] Pointer to the next expression value.
/// \param pGrad	[in] Pointer to the Jacobian matrix.
//////////////////////////////////////////////////////////////////////////
template <class T>
void ZOptimizerTerminator<T>::RegisterParameters(Wml::GVector<T>* pCurrX, T* pCurrFun, T* pNextFun,Wml::GVector<T>* pGrad)
{
	m_pCurrX = pCurrX;
	m_pCurrFuncValue = pCurrFun;
	m_pNextFuncValue = pNextFun;
	m_pDir = pGrad;
}
#endif // !defined(AFX_ZOPTIMIZERTERMINATOR_H__79FB6643_187B_424D_B5D2_C2104473D09D__INCLUDED_)
