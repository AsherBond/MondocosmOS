// File:	WOKNT_Handle.hxx
// Author:	Jean GAUTIER
//		<jga@cobrax>


#ifndef WOKNT_Handle_HeaderFile
#define WOKNT_Handle_HeaderFile

#ifdef WNT
#include <windows.h>

typedef HANDLE WOKNT_Handle ;

#ifdef CreateFile
# undef CreateFile
#endif  // CreateFile

#ifdef CreateDirectory
# undef CreateDirectory
#endif  // CreateFile

#ifdef RemoveDirectory
# undef RemoveDirectory
#endif  // CreateFile


#else


typedef int WOKNT_Handle ;


#endif

#endif
