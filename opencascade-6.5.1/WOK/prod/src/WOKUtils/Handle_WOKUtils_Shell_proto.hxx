// File:	Handle_WOKUtils_Shell_proto.hxx
// Created:	Fri Jan 31 20:07:10 1997
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>


#ifndef Handle_WOKUtils_Shell_proto_HeaderFile
#define Handle_WOKUtils_Shell_proto_HeaderFile




#ifdef WNT

#include <Handle_WOKNT_Shell.hxx>

typedef Handle_WOKNT_Shell Handle_WOKUtils_Shell;

#else

#include <Handle_WOKUnix_Shell.hxx>

typedef Handle_WOKUnix_Shell Handle_WOKUtils_Shell;

#endif

#endif
