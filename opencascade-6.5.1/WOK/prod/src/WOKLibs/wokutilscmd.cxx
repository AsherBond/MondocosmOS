// File:	wokutilscmd.cxx
// Created:	Thu Feb 27 19:57:33 1997
// Author:	Jean GAUTIER
//		<jga@hourax.paris1.matra-dtv.fr>


#include <tcl.h>

#include <WOKTclTools_Interpretor.hxx>
#include <WOKTclUtils_Path.hxx>
#include <WOKTclTools_Package.hxx>

#ifdef WNT
# ifdef _DEBUG
extern "C" void _debug_break ( char* );
# endif  // _DEBUG
# define WOKUTILS_EXPORT __declspec( dllexport )
#else
# define WOKUTILS_EXPORT
#endif  // WNT

extern "C" int WOKUTILS_EXPORT Wokutils_Init(WOKTclTools_PInterp);

int Wokutils_Init(WOKTclTools_PInterp interp)
{
  Handle(WOKTclTools_Interpretor)& CurrentInterp = WOKTclTools_Interpretor::Current();

  if(WOKTclTools_Interpretor::Current().IsNull())
    {
      CurrentInterp = new WOKTclTools_Interpretor(interp);
    }

  CurrentInterp->Add("wokcmp",       "compares two files",            WOKTclUtils_Path::FileCompare,     "WOK COMMAND\n");
  CurrentInterp->Add("wokfind",      "lists a fiel tree",             WOKTclUtils_Path::DirectorySearch, "WOK COMMAND\n");

  WOKTclTools_Package woktools(CurrentInterp, "wokutils", "2.0");

  woktools.Provide();
  
  return TCL_OK;
}


