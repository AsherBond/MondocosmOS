// File:	WOKUtils_Path_proto.hxx
// Created:	Fri Jan 31 19:37:37 1997
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>


#ifndef WOKUtils_Path_proto_HeaderFile
#define WOKUtils_Path_proto_HeaderFile

#include <Handle_WOKUtils_Path.hxx>

#ifdef WNT
#ifdef CreateFile
# undef CreateFile
#endif 

#ifdef CreateDirectory
# undef CreateDirectory
#endif
#include <WOKNT_Path.hxx>
#define  WOKUtils_Path WOKNT_Path 

#else

#include <WOKUnix_Path.hxx>
#define  WOKUtils_Path WOKUnix_Path

#endif

#include <WOKUtils_Extension.hxx>


#endif
