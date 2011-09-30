// FilenameParser.h: interface for the CFilenameParser class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_FILENAMEPARSER_H__A31C437B_18D1_4C3E_A477_E5AAE707B038__INCLUDED_)
#define AFX_FILENAMEPARSER_H__A31C437B_18D1_4C3E_A477_E5AAE707B038__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include <string>
#include <fstream>
namespace FileName
{
	class CParserImpl;
	class CParser  
	{
	public:
		CParser(int = 1);
		virtual ~CParser();
		
		bool GetNextFile(std::string &);
		bool GetPrevFile(std::string &);
		bool GetFile(std::string &, int);

		int GetCount(std::string&);

		int SetCount(int, int, int);

		CParserImpl	*m_pImpl;
	};

	bool file_exists(std::string file);
}

#endif // !defined(AFX_FILENAMEPARSER_H__A31C437B_18D1_4C3E_A477_E5AAE707B038__INCLUDED_)
