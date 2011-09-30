// timestamp.h: interface for the time_stamp class.
//
//////////////////////////////////////////////////////////////////////

#if !defined(AFX_TIMESTAMP_H__9D494403_86BE_4797_ACB3_836DC7215FA1__INCLUDED_)
#define AFX_TIMESTAMP_H__9D494403_86BE_4797_ACB3_836DC7215FA1__INCLUDED_

#if _MSC_VER > 1000
#pragma once
#endif // _MSC_VER > 1000

#include "others/misc/dllexp.h"

//////////////////////////////////////////////////////////////////////////
/// \ingroup Buffer
/// \brief Base class for ordering the data.
///
/// The class contains a timestamp value, which identify the time.
//////////////////////////////////////////////////////////////////////////
class DLL_DLL_EXPORT time_stamp
{
public:

	/// \brief Default constructor.
	time_stamp()
	{
	//	_timestamp = stimestamp;
	//	++ stimestamp;
	}
	
	/// \brief Initialize the time stamp.
	void init_time(int tickcount);

	/// \brief Set the time stamp manually.
	void set_time(int timestamp, int tickcount);

	//////////////////////////////////////////////////////////////////////////
	/// \brief The timestamp.
	///
	/// For buffer timestamp.
	//////////////////////////////////////////////////////////////////////////
	unsigned int _timestamp;

	static unsigned int s_timestamp;

	//////////////////////////////////////////////////////////////////////////
	/// \brief The system tick when the timestamp was created.
	//////////////////////////////////////////////////////////////////////////
	int _tickcount;
};
#endif // !defined(AFX_TIMESTAMP_H__9D494403_86BE_4797_ACB3_836DC7215FA1__INCLUDED_)
