// ZDefOptimizerTerminator.h: interface for the ZDefOptimizerTerminator class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_ZDEFOPTIMIZERTERMINATOR_H__7B95134F_904E_4C55_8946_F41CF158D65E__INCLUDED_)
#define AFX_ZDEFOPTIMIZERTERMINATOR_H__7B95134F_904E_4C55_8946_F41CF158D65E__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "ZOptimizerTerminator.h"

//////////////////////////////////////////////////////////////////////////
/// \ingroup Solver
/// \brief The default termination condition terminates the optimization
/// while 2 consecutive optimization steps change the result little.
//////////////////////////////////////////////////////////////////////////
template <class T>
class ZDefOptimizerTerminator : public ZOptimizerTerminator<T>  
{
public:
	ZDefOptimizerTerminator();
	virtual ~ZDefOptimizerTerminator();

public:
	virtual bool ShouldTerminate() const;

	virtual void SetZero(const T espilon);

protected:
	T m_Espilon;
};

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

template <class T>
ZDefOptimizerTerminator<T>::ZDefOptimizerTerminator()
{
	m_Espilon = 1.0e-10;
}

template <class T>
ZDefOptimizerTerminator<T>::~ZDefOptimizerTerminator()
{

}

template <class T>
void ZDefOptimizerTerminator<T>::SetZero(const T espilon)
{
	m_Espilon = espilon;
}

template <class T>
bool ZDefOptimizerTerminator<T>::ShouldTerminate() const
{
	return false;
	if ((*m_pNextFuncValue) < (*m_pCurrFuncValue))
	{
		return ((*m_pCurrFuncValue)-(*m_pNextFuncValue)) <= m_Espilon;

		//return (fabs((*m_pCurrFuncValue)-(*m_pNextFuncValue)) / (*m_pCurrFuncValue)) <m_Espilon;
	}
	else
	{
		return false;
	}
}

#endif // !defined(AFX_ZDEFOPTIMIZERTERMINATOR_H__7B95134F_904E_4C55_8946_F41CF158D65E__INCLUDED_)
