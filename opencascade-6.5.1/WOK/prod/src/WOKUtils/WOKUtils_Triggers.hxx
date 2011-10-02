// File:	WOKUtils_Triggers.hxx
// Created:	Thu Nov 14 14:48:10 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>


#ifndef WOKUtils_Triggers_HeaderFile
#define WOKUtils_Triggers_HeaderFile

#include <WOKUtils_Trigger.hxx>

#ifndef __WOKUTILS_API
# if defined(WNT) && !defined(HAVE_NO_DLL)
#  ifdef __WOKUtils_DLL
#   define __WOKUTILS_API __declspec( dllexport )
#  else
#   define __WOKUTILS_API __declspec( dllimport )
#  endif  // __WOKUtils_DLL
# else
#   define __WOKUTILS_API
# endif  // WNT
#endif  // __WOKUTILS_API

__WOKUTILS_API WOKUtils_Trigger& endt(WOKUtils_Trigger&);

#endif
