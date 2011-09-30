// SequenceParserImpl.cpp: implementation of the CSequenceParserImpl class.
//
//////////////////////////////////////////////////////////////////////

#include "SequenceParserImpl.h"
#include <algorithm>
#include <fstream>

#ifdef _DEBUG
#undef THIS_FILE
static char THIS_FILE[]=__FILE__;
#define new DEBUG_NEW
#endif

using namespace std;
//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

FileName::CSequenceParserImpl::CSequenceParserImpl(int step)
{
	m_nStep = step;
	m_nStart = 0;
	m_nEnd = 100000;
}

FileName::CSequenceParserImpl::~CSequenceParserImpl()
{

}

bool FileName::CSequenceParserImpl::GetNextFile(string &strFilename)
{
	// parse file number
	string::size_type nEnd	 = strFilename.rfind('.');
	if(nEnd == string::npos)
	{
		nEnd = strFilename.length();
	}

	string::size_type nBegin = strFilename.find_last_of("0123456789");
	if(nBegin == string::npos)
		nBegin = nEnd;
	else
	{
		while(nBegin>0)
		{
			if(strFilename[nBegin-1]<'0' || strFilename[nBegin-1]>'9')
				break;
			else
				--nBegin;
		}
	}
	string strNumber
		= strFilename.substr(nBegin, nEnd - nBegin);

	// increment the file number for next file
	int nNumber = ::atoi(strNumber.c_str()) + m_nStep;
	char buf[128];
	::itoa(nNumber, buf, 10);
	string strNextNumber(buf);
	
	// get new file name
	if(strNextNumber.length() >= strNumber.length())
	{
		strFilename.replace(nBegin, strNumber.length(), strNextNumber);
	}
	else
	{
		strFilename.replace(nEnd - strNextNumber.length(), strNextNumber.length(), strNextNumber);
	}
	return true;
}

bool FileName::CSequenceParserImpl::GetPrevFile(string &strFilename)
{
	// parse file number
	string::size_type nEnd	 = strFilename.rfind('.');
	if(nEnd == string::npos)
	{
		nEnd = strFilename.length();
	}

	string::size_type nBegin = strFilename.find_last_of("0123456789");
	if(nBegin == string::npos)
		nBegin = nEnd;
	else
	{
		while(nBegin>0)
		{
			if(strFilename[nBegin-1]<'0' || strFilename[nBegin-1]>'9')
				break;
			else
				--nBegin;
		}
	}
	string strNumber
		= strFilename.substr(nBegin, nEnd - nBegin);

	// increment the file number for next file
	int nNumber = ::atoi(strNumber.c_str()) - m_nStep;
	char buf[128];
	::itoa(nNumber, buf, 10);
	string strNextNumber(buf);

	// get new file name
	if(strNextNumber.length() >= strNumber.length())
	{
		strFilename.replace(nBegin, strNumber.length(), strNextNumber);
	}
	else
	{
		strFilename.replace(nEnd - strNextNumber.length(), strNextNumber.length(), strNextNumber);
	}
	return true;
}

bool FileName::CSequenceParserImpl::GetFile(std::string &strFilename, int num)
{
	num = m_nStart + num*m_nStep;

	// parse file number
	string::size_type nEnd	 = strFilename.rfind('.');
	if(nEnd == string::npos)
	{
		nEnd = strFilename.length();
	}

	string::size_type nBegin = strFilename.find_last_of("0123456789");
	if(nBegin == string::npos)
		nBegin = nEnd;
	else
	{
		while(nBegin>0)
		{
			if(strFilename[nBegin-1]<'0' || strFilename[nBegin-1]>'9')
				break;
			else
				--nBegin;
		}
	}
	string strNumber
		= strFilename.substr(nBegin, nEnd - nBegin);

	// increment the file number for next file
	int nNumber = ::atoi(strNumber.c_str()) + num;
	char buf[128];
	::itoa(nNumber, buf, 10);
	string strNextNumber(buf);

	// get new file name
	if(strNextNumber.length() >= strNumber.length())
	{
		strFilename.replace(nBegin, strNumber.length(), strNextNumber);
	}
	else
	{
		strFilename.replace(nEnd - strNextNumber.length(), strNextNumber.length(), strNextNumber);
	}
	return true;
}

int FileName::CSequenceParserImpl::SetCount(int start, int end, int step)
{
	m_nStart = start;
	m_nEnd = end;
	m_nStep = step;

	return (end - start)/step + 1;
}

int FileName::CSequenceParserImpl::GetCount(std::string &strFilename)
{
	// parse file number
	string::size_type nEnd	 = strFilename.rfind('.');
	if(nEnd == string::npos)
	{
		nEnd = strFilename.length();
	}

	string::size_type nBegin = strFilename.find_last_of("0123456789");
	if(nBegin == string::npos)
		nBegin = nEnd;
	else
	{
		while(nBegin>0)
		{
			if(strFilename[nBegin-1]<'0' || strFilename[nBegin-1]>'9')
				break;
			else
				--nBegin;
		}
	}
	string strNumber
		= strFilename.substr(nBegin, nEnd - nBegin);

	// increment the file number for next file
	std::ifstream ifs;

	int count = 0;
	int nNumber = ::atoi(strNumber.c_str());
	char buf[128];
	bool exist = true;
	do 
	{
		::itoa(nNumber, buf, 10);
		string strNextNumber(buf);

		// get new file name
		std::string nextfile = strFilename;

		if(strNextNumber.length() >= strNumber.length())
		{
			nextfile.replace(nBegin, strNumber.length(), strNextNumber);
		}
		else
		{
			nextfile.replace(nEnd - strNextNumber.length(), strNextNumber.length(), strNextNumber);
		}
		ifs.open(nextfile.c_str());
		if (ifs.is_open())
		{
			++ count;
			nNumber += m_nStep;
			ifs.close();
		}
		else
		{
			exist = false;
		}
	} while(exist);

	if (count == 0)
	{
		count = 1;
	}
	return count;
}