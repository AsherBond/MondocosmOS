// FrameRate.h: interface for the CFrameRate class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_FRAMERATE_H__E3F416D4_2D58_4F5A_9B13_CAFAD28595B3__INCLUDED_)
#define AFX_FRAMERATE_H__E3F416D4_2D58_4F5A_9B13_CAFAD28595B3__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000
#include <windows.h>

class CFrameRate  
{
public:
	inline CFrameRate();
	inline virtual ~CFrameRate();
	
	inline void			Reset();
	inline void			RenderFrame();
	inline const float		FrameRate();

	inline long			GetCount()
	{
		return m_dwFrameCount;
	}
protected:
	long		m_dwLastTick;
	long		m_dwFrameCount;
};

#include <iostream>

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

CFrameRate::CFrameRate():m_dwFrameCount(0)
{
	m_dwLastTick = GetTickCount();
}

CFrameRate::~CFrameRate()
{

}

void CFrameRate::Reset()
{
	m_dwFrameCount = 0;
	m_dwLastTick = GetTickCount();
}

void CFrameRate::RenderFrame()
{
	++m_dwFrameCount;
//	printf("%d frames\n", m_dwFrameCount);
}

const float CFrameRate::FrameRate() 
{
	RenderFrame();
	
	long dwCurrentTick = GetTickCount();
	if(dwCurrentTick == m_dwLastTick)
		dwCurrentTick = m_dwLastTick + 1;
	float f = float(m_dwFrameCount*1000)/(dwCurrentTick - m_dwLastTick);
	if(dwCurrentTick - m_dwLastTick > 1000)
	{
		Reset();
	}

	return f;
}
#endif // !defined(AFX_FRAMERATE_H__E3F416D4_2D58_4F5A_9B13_CAFAD28595B3__INCLUDED_)
