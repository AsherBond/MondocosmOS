// File:	WOKUtils_RemoteShell_proto.hxx
// Created:	Fri Jan 31 19:38:42 1997
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>


#ifndef WOKUtils_RemoteShell_proto_HeaderFile
#define WOKUtils_RemoteShell_proto_HeaderFile

#include <Handle_WOKUtils_RemoteShell.hxx>

#ifdef WNT


#include <WOKNT_Shell.hxx>

#define WOKUtils_RemoteShell WOKNT_Shell


#else

#include <WOKUnix_RemoteShell.hxx>

#define WOKUtils_RemoteShell WOKUnix_RemoteShell 

#endif


#endif
