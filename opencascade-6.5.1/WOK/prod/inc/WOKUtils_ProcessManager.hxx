// File:	WOKUtils_ProcessManager.hxx
// Created:	Fri Jan 31 19:32:38 1997
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>


#ifndef WOKUtils_ProcessManager_HeaderFile
#define WOKUtils_ProcessManager_HeaderFile

#ifdef WNT

#include <WOKNT_ShellManager.hxx>

typedef WOKNT_ShellManager WOKUtils_ProcessManager;

#else

#include <WOKUnix_ProcessManager.hxx>

typedef WOKUnix_ProcessManager WOKUtils_ProcessManager;

#endif

#endif
