// File:	WOKUtils_ShellManager.hxx
// Created:	Fri Jan 31 19:34:18 1997
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>


#ifndef WOKUtils_ShellManager_HeaderFile
#define WOKUtils_ShellManager_HeaderFile

#ifdef WNT

#include <WOKNT_ShellManager.hxx>

typedef WOKNT_ShellManager WOKUtils_ShellManager;

#else

#include <WOKUnix_ShellManager.hxx>

typedef WOKUnix_ShellManager WOKUtils_ShellManager;

#endif



#endif
