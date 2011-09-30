// FilenameParser.cpp: implementation of the CFilenameParser class.
//
//////////////////////////////////////////////////////////////////////

#include "FileNameParser.h"
#include "SequenceParserImpl.h"
#include <cassert>

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

FileName::CParser::CParser(int step)
{
	m_pImpl = new CSequenceParserImpl(step); 
}

FileName::CParser::~CParser()
{
	if (m_pImpl)
	{
		delete m_pImpl;
	}
}

bool FileName::CParser::GetPrevFile(std::string &strFilename)
{
	assert(m_pImpl);
	return m_pImpl->GetPrevFile(strFilename);
}

bool FileName::CParser::GetNextFile(std::string &strFilename)
{
	assert(m_pImpl);
	return m_pImpl->GetNextFile(strFilename);
}

bool FileName::CParser::GetFile(std::string &strFilename, int nFileNumber)
{
	assert(m_pImpl);
	return m_pImpl->GetFile(strFilename, nFileNumber);
}

int FileName::CParser::GetCount(std::string &strFilename)
{
	assert(m_pImpl);
	return m_pImpl->GetCount(strFilename);
}

int FileName::CParser::SetCount(int start, int end, int step)
{
	assert(m_pImpl);
	return m_pImpl->SetCount(start, end, step);
}

bool FileName::file_exists(std::string file)
{
	std::ifstream ifs;
	ifs.open(file.c_str());

	return ifs.is_open();
}