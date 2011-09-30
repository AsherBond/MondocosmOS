// SequenceParserImpl.h: interface for the CSequenceParserImpl class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_SEQUENCEPARSERIMPL_H__AA4C1B4C_FC8B_47D1_A773_DE811480EF09__INCLUDED_)
#define AFX_SEQUENCEPARSERIMPL_H__AA4C1B4C_FC8B_47D1_A773_DE811480EF09__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "FileNameParserImpl.h"
namespace FileName
{
	class CSequenceParserImpl : public CParserImpl  
	{
	public:
		CSequenceParserImpl(int);
		virtual ~CSequenceParserImpl();

		virtual bool GetNextFile(std::string &);
		virtual bool GetPrevFile(std::string &);
		virtual bool GetFile(std::string &, int);
		virtual int GetCount(std::string&);

		virtual int SetCount(int, int, int);
	protected:
		int			m_nStep;
		int			m_nEnd;
		int			m_nStart;
	};
}

#endif // !defined(AFX_SEQUENCEPARSERIMPL_H__AA4C1B4C_FC8B_47D1_A773_DE811480EF09__INCLUDED_)
