// cctformatlabparser.h: interface for the CCTforMatlabParser class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_CCTFORMATLABPARSER_H__B4AC9DC4_59A4_46DE_B003_F94DFF6D7639__INCLUDED_)
#define AFX_CCTFORMATLABPARSER_H__B4AC9DC4_59A4_46DE_B003_F94DFF6D7639__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include <string>
#include <vector>

//////////////////////////////////////////////////////////////////////////
/// \ingroup Solver
/// \brief Camera Calibration Toolbox for Matlab.
///
/// Parse the intrinsic parameters from the Camera Calibration Toolbox for Matlab.
//////////////////////////////////////////////////////////////////////////

//Camera Calibration Toolbox for Matlab
class CCTforMatlabParser  
{
public:
	CCTforMatlabParser();
	virtual ~CCTforMatlabParser();

	/// \brief Parse the data file.
	static bool ParseDataFile(const std::string&, double K[3][3]);
	
	/// \brief Find a substring in another string.
	static std::string::size_type FindToken(std::string &, std::string &);

	/// \brief Find next token between two black spaces.
	static void NextToken(std::string &, std::string &, int &);

	/// \brief Whether the string is digital.
	static bool IsDigits(std::string &);

	/// \brief Whether the character is digital.
	static bool IsDigit(char);

};

#endif // !defined(AFX_CCTFORMATLABPARSER_H__B4AC9DC4_59A4_46DE_B003_F94DFF6D7639__INCLUDED_)
