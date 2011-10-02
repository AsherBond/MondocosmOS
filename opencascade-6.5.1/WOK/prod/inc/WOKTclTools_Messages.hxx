// File:	WOKTCL_Messages.hxx
// Created:	Wed Oct 18 13:08:06 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#ifndef WOKTCL_Messages_HeaderFile
#define WOKTCL_Messages_HeaderFile

#include <WOKTclTools_Interpretor.hxx>

#ifndef __WOKTCLTOOLS_API
# if defined(WNT) && !defined(HAVE_NO_DLL)
#  ifdef __WOKTclTools_DLL
#   define __WOKTCLTOOLS_API __declspec( dllexport )
#  else
#   define __WOKTCLTOOLS_API __declspec( dllimport )
#  endif  // __WOKTclTools_DLL
# else
#   define __WOKTCLTOOLS_API
# endif  // WNT
#endif  // __WOKTCLTOOLS_API 

__WOKTCLTOOLS_API Standard_Integer WOKTclTools_MessageCmdSet(const Handle(WOKTclTools_Interpretor)& ,  Standard_Integer ,  WOKTools_ArgTable );
__WOKTCLTOOLS_API Standard_Integer WOKTclTools_MessageCmdUnSet(const Handle(WOKTclTools_Interpretor)& aninterp,  Standard_Integer ,  WOKTools_ArgTable );
__WOKTCLTOOLS_API Standard_Integer WOKTclTools_MessageCmdIsSet(const Handle(WOKTclTools_Interpretor)& aninterp,  Standard_Integer ,  WOKTools_ArgTable );


#endif
