#pragma  once

#include "others/misc/dllexp.h"

//////////////////////////////////////////////////////////////////////////
/// \ingroup Buffer
/// \brief The camera parameters.
///
/// Storing the final result.
//////////////////////////////////////////////////////////////////////////
template <class T>
class DLL_DLL_EXPORT camera_parameter
{
public:
	/// constructor.
	camera_parameter()
	{
		int i = 0, j = 0;

		for( i = 0; i < 3; ++i)
		{
			for( j = 0; j < 3; ++j)
			{
				K[i][j] = 0.0;
				r[i][j] = 0.0;
			}			
			r[i][i] = 1.0;
			trans[i] = 0.0;
		}
		K[0][0] = 1000.0;
		K[1][1] = 1000.0;
		K[2][2] = 1.0;
		K[0][2] = (640-1)/2.0;
		K[1][2] = (480-1)/2.0;		
	}

	/// deconstructor.
	~camera_parameter()
	{

	}

	/// intrinsic parameters
	T K[3][3];
	
	/// extrinsic parameters
	/// rotation.
	T r[3][3];
	
	/// transition.
	T trans[3];
};
