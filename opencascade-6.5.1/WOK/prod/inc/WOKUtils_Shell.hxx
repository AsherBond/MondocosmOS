// File:	WOKUtils_Shell_proto.hxx
// Created:	Fri Jan 31 19:38:14 1997
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>


#ifndef WOKUtils_Shell_proto_HeaderFile
#define WOKUtils_Shell_proto_HeaderFile

#include <Handle_WOKUtils_Shell.hxx>

#ifdef WNT

#include <WOKNT_Shell.hxx>

#define WOKUtils_Shell WOKNT_Shell

#else

#include <WOKUnix_Shell.hxx>

#define WOKUtils_Shell WOKUnix_Shell 

#endif


#endif
