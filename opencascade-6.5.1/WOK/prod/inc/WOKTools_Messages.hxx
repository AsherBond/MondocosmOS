// File:	WOKTools_Messages.hxx
// Created:	Wed Jun 28 19:08:33 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#ifndef WOKTools_Messages_HeaderFile
#define WOKTools_Messages_HeaderFile

#include <WOKTools_Info.hxx>
#include <WOKTools_Warning.hxx>
#include <WOKTools_Error.hxx>
#include <WOKTools_Verbose.hxx>

#ifndef __WOKTools_API
# if defined(WNT) && !defined(HAVE_NO_DLL)
#  ifdef __WOKTools_DLL
#   define __WOKTools_API __declspec( dllexport )
#  else
#   define __WOKTools_API __declspec( dllimport )
#  endif  // __WOKTools_DLL
# else
#  define __WOKTools_API
# endif  // WNT
#endif  // __WOKTools_API

extern __WOKTools_API WOKTools_Info&    InfoMsg();
extern __WOKTools_API WOKTools_Warning& WarningMsg();
extern __WOKTools_API WOKTools_Error&   ErrorMsg();
extern __WOKTools_API WOKTools_Verbose& VerboseMsg();

extern __WOKTools_API WOKTools_Message& endm(WOKTools_Message&);
extern __WOKTools_API WOKTools_Message& flushm(WOKTools_Message&);

#ifdef  DEB
#define WOK_VERBOSE 1
#endif

#define WOK_TRACE0(msg) if(VerboseMsg().IsSet()) { msg }

#define WOK_TRACE if(VerboseMsg().IsSet())


#define WOK_BEGIN_TRACE  if(VerboseMsg().IsSet()) {
#define WOK_END_TRACE    }
#endif
