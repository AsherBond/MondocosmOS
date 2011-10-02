// File:	Handle_WOKUtils_Path_proto.hxx
// Created:	Fri Jan 31 19:55:45 1997
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>


#ifndef Handle_WOKUtils_Path_HeaderFile
#define Handle_WOKUtils_Path_HeaderFile


#ifdef WNT

#include <Handle_WOKNT_Path.hxx>

typedef Handle_WOKNT_Path Handle_WOKUtils_Path;

#else

#include <Handle_WOKUnix_Path.hxx>

typedef Handle_WOKUnix_Path Handle_WOKUtils_Path;

#endif


#endif
