#include "generalparameter.h"

void strtrimright(std::string & str, char c)
{
	int trimindex = str.length();
	for (int i = str.length() - 1; i >= 0; -- i)
	{
		if (str[i] == c)
		{
			trimindex = i;
		}
		else
		{
			break;
		}
	}

	str = str.substr(0, trimindex);
}

void strtrimleft(std::string & str, char c)
{
	int trimindex = -1;
	for (int i = 0; i < str.length() - 1; ++ i)
	{
		if (str[i] == c)
		{
			trimindex = i;
		}
		else
		{
			break;
		}
	}

	str = str.substr(trimindex + 1, str.length() - ( trimindex + 1));
}

GeneralParameter::GeneralParameter(std::string strDelimeter):m_strDelimeter(strDelimeter)
{
//		Redirect(std::cerr);
//		char t[1024];
//		GetCurrentDirectory(1024, t);
//		m_strParamFile += t;
//		m_strParamFile += "\\";
}
GeneralParameter::~GeneralParameter()
{
//		Save(m_strParamFile);
//		Resotre(std::cerr);
}

void GeneralParameter::Resotre(std::ostream& strm)
{
	strm.rdbuf(m_sbOldStreamBuffer);
}

void GeneralParameter::Redirect(std::ostream& strm)
{
	m_oflogfile.open("cerr.txt");

	m_sbOldStreamBuffer = strm.rdbuf();

	strm.rdbuf (m_oflogfile.rdbuf());
}

bool GeneralParameter::Load(const std::string &strParamFile)
{
	if (strParamFile.empty())
	{
		return false;
	}

	bool ret = false;

	printf("loading configuration file %s \n", strParamFile.c_str());  
	m_strParamFile = strParamFile;

	using std::ios;
	std::ifstream infile;
	ios::iostate oldExceptions = infile.exceptions();
	try
	{
		std::string name, value;
		m_mapParams.clear();
		infile.open(strParamFile.c_str(), ios::in);
		if(infile)
		{
			std::string line;
			while(std::getline(infile, line))
			{
				int index = line.find(m_strDelimeter);
				name = line.substr(0, index);
				strtrimright(name, ' ');
				value = line.substr(index+m_strDelimeter.size(), line.size());
				strtrimleft(value, ' ');
				m_mapParams.insert(std::make_pair(name, value));
			}
			infile.close();

			ret = true;
		}
	}
	catch (const std::ios_base::failure& e)
	{
		if(!infile.eof())
		{
			infile.exceptions (oldExceptions);
			std::cerr<<e.what()<<std::endl;
		}
	}
	infile.exceptions(oldExceptions);

	return ret;
}

void GeneralParameter::Save()
{
	Save(m_strParamFile);
}

void GeneralParameter::Save(const std::string &strParamFile)
{
	using std::ios;
	std::ofstream outfile;
	ios::iostate oldExceptions = outfile.exceptions();
	try
	{
		std::string name, value;
		outfile.open(strParamFile.c_str(), ios::out|ios::trunc);
		
		if(outfile)
		{
			PARAMMAP::iterator paramit = m_mapParams.begin();

			while(paramit != m_mapParams.end())
			{
				outfile<<paramit->first<<m_strDelimeter<<paramit->second<<"\n";
				++paramit;
			}
			outfile.close();
		}

	}
	catch (const std::ios_base::failure& e)
	{
		if(!outfile.eof())
		{
			outfile.exceptions (oldExceptions);
			std::cerr<<e.what()<<std::endl;
		}
	}
	outfile.exceptions(oldExceptions);
}

void GeneralParameter::Get(const std::string& name, float& value) const
{
	PARAMMAP::const_iterator paramit =  m_mapParams.find(name);
	if(paramit == m_mapParams.end())
	{
		//value = 0.0f;
	}
	else
	{
		value = ::atof(paramit->second.c_str());
	}
}
void GeneralParameter::Get(const std::string& name, int& value)
{
	PARAMMAP::const_iterator paramit =  m_mapParams.find(name);
	if(paramit == m_mapParams.end())
	{
		//value = 0;
	}
	else
	{
		value = ::atoi(paramit->second.c_str());
	}
}
void GeneralParameter::Get(const std::string& name, bool& value)
{
	int temp = 0;
	Get(name, temp);
	value = bool(temp);
}
void GeneralParameter::Get(const std::string& name, DWORD& value)
{
	PARAMMAP::const_iterator paramit =  m_mapParams.find(name);
	if(paramit == m_mapParams.end())
	{
		//value = 0;
	}
	else
	{
		value = ::atoi(paramit->second.c_str());
	}
}
void GeneralParameter::Get(const std::string& name, std::string& value) const
{
//	printf("reading string parameter\n");

	PARAMMAP::const_iterator paramit =  m_mapParams.find(name);
	if(paramit == m_mapParams.end())
	{
		//value = "";
//		printf("failed: reading string parameter\n");
	}
	else
	{
		value = paramit->second;
//		printf("reading string parameter = %s \n", value.c_str());
	}
}

void GeneralParameter::Put(const std::string &name, const float &value)
{
	std::stringstream newvalue;
	newvalue<<value;
	
	Put(name, newvalue.str());
}

void GeneralParameter::Put(const std::string& name, const int& value)
{
	std::stringstream newvalue;
	newvalue<<value;
	
	Put(name, newvalue.str());
}
void GeneralParameter::Put(const std::string& name, const bool& value)
{
	int temp = int(value);
	Put(name, temp);
}
void GeneralParameter::Put(const std::string& name, const DWORD& value)
{
	std::stringstream newvalue;
	newvalue<<value;
	
	Put(name, newvalue.str());
}
	
void GeneralParameter::Put(const std::string& name, const char* value)
{
	std::stringstream newvalue;
	newvalue<<value;
	Put(name, newvalue.str());
}

void GeneralParameter::Put(const std::string& name, const std::string& value)
{
	PARAMMAP::iterator paramit =  m_mapParams.find(name);

	if(paramit == m_mapParams.end())
	{
		m_mapParams.insert(std::make_pair(name, value));
	}
	else
	{
		paramit->second = value;
	}
}

void GeneralParameter::Erase(const std::string& name)
{
	PARAMMAP::iterator paramit =  m_mapParams.find(name);

	if(paramit != m_mapParams.end())
	{
		m_mapParams.erase(paramit);
	}
}

void GeneralParameter::Clear()
{
	m_mapParams.clear();
}