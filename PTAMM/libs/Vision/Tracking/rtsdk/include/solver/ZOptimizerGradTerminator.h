// ZOptimizerGradTerminator.h: interface for the ZOptimizerGradTerminator class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_ZOPTIMIZERGRADTERMINATOR_H__F6ADED00_544A_484F_BCD8_EC24E80F92FF__INCLUDED_)
#define AFX_ZOPTIMIZERGRADTERMINATOR_H__F6ADED00_544A_484F_BCD8_EC24E80F92FF__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
#include "ZOptimizerTerminator.h"

template <class T>
class ZOptimizerGradTerminator : public ZOptimizerTerminator<T>  
{
public:
	ZOptimizerGradTerminator();
	virtual ~ZOptimizerGradTerminator();
    virtual bool ShouldTerminate () const;
	void SetZeroFactor(double value)
	{
		m_ZeroFactor = value;
	}

protected:
	T m_ZeroFactor;
};

template <class T>
ZOptimizerGradTerminator<T>::ZOptimizerGradTerminator()
{
	m_ZeroFactor = 0.0001*DBL_EPSILON;
}

template <class T>
ZOptimizerGradTerminator<T>::~ZOptimizerGradTerminator()
{

}

template <class T>
bool ZOptimizerGradTerminator<T>::ShouldTerminate () const
{
	bool st=false;
	if(m_pDir->Length()<m_ZeroFactor)
		st=true;
	return st;
}

#endif // !defined(AFX_ZOPTIMIZERGRADTERMINATOR_H__F6ADED00_544A_484F_BCD8_EC24E80F92FF__INCLUDED_)
