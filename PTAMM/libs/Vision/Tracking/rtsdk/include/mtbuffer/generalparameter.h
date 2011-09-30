// GeneralParameter.h: interface for the GeneralParameter class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_GENERALPARAMETER_H__ED6D4CFF_48F3_4675_BC4B_8584537CB8D5__INCLUDED_)
#define AFX_GENERALPARAMETER_H__ED6D4CFF_48F3_4675_BC4B_8584537CB8D5__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include <cstdlib>
#include <map>
#include <string>
#include <sstream>
#include <fstream>
#include <iostream>
#include <ios>

#pragma warning(disable: 4786)
#pragma warning(disable: 168)

#ifndef DWORD
#define DWORD unsigned long
#endif


#include "others/misc/dllexp.h"


//////////////////////////////////////////////////////////////////////////
/// \ingroup MTBUFFER
/// \brief This is for read/write configuration file.
//////////////////////////////////////////////////////////////////////////
class DLL_DLL_EXPORT GeneralParameter  
{
	typedef std::map<std::string, std::string> PARAMMAP;
public:
	GeneralParameter(std::string strDelimeter = "=");
	
	virtual ~GeneralParameter();

	void Resotre(std::ostream& strm);

	void Redirect(std::ostream& strm);

	bool Load(const std::string &strParamFile);
	
	void Save();

	void Save(const std::string &strParamFile);

protected:
	std::string     m_strDelimeter;

	std::streambuf *m_sbOldStreamBuffer;

	std::ofstream	m_oflogfile;

	std::string		m_strParamFile;			// file storing the parameters

	PARAMMAP		m_mapParams;			// memory map storing the parameters

    std::string     m_strNaturalMarkerData;

public:
	void Get(const std::string& name, float& value) const;

	void Get(const std::string& name, int& value);

	void Get(const std::string& name, bool& value);

	void Get(const std::string& name, DWORD& value);

	void Get(const std::string& name, std::string& value) const;
	
	void Put(const std::string &name, const float &value);

	void Put(const std::string& name, const int& value);

	void Put(const std::string& name, const bool& value);

	void Put(const std::string& name, const DWORD& value);
		
	void Put(const std::string& name, const char* value);
	
	void Put(const std::string& name, const std::string& value);

	void Erase(const std::string& name);

	void Clear();
};

#endif // !defined(AFX_GENERALPARAMETER_H__ED6D4CFF_48F3_4675_BC4B_8584537CB8D5__INCLUDED_)
