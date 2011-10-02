// File:	Handle_WOKUtils_RemoteShell.hxx
// Created:	Fri Jan 31 20:07:44 1997
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>


#ifndef Handle_WOKUtils_RemoteShell_HeaderFile
#define Handle_WOKUtils_RemoteShell_HeaderFile



#ifdef WNT

#include <Handle_WOKNT_Shell.hxx>

typedef Handle_WOKNT_Shell Handle_WOKUtils_RemoteShell;

#else

#include <Handle_WOKUnix_RemoteShell.hxx>

typedef Handle_WOKUnix_RemoteShell Handle_WOKUtils_RemoteShell;

#endif


#endif
