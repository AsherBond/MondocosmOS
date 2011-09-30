// FNParserImpl.h: interface for the CFNParserImpl class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_FNPARSERIMPL_H__7B1C7AF1_A28C_4D5A_B270_529156753BD6__INCLUDED_)
#define AFX_FNPARSERIMPL_H__7B1C7AF1_A28C_4D5A_B270_529156753BD6__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include <string>
namespace FileName
{
	class CParserImpl  
	{
	public:
		CParserImpl();
		virtual ~CParserImpl();
		virtual bool GetNextFile(std::string &) = 0;
		virtual bool GetPrevFile(std::string &) = 0;
		virtual bool GetFile(std::string &, int) = 0;
		virtual int GetCount(std::string&) = 0;
		virtual int SetCount(int, int, int) = 0;
	};
}
#endif // !defined(AFX_FNPARSERIMPL_H__7B1C7AF1_A28C_4D5A_B270_529156753BD6__INCLUDED_)
