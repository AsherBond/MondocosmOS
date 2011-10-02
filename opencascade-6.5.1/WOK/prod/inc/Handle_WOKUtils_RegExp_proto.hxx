// File:	Handle_WOKUtils_RegExp_proto.hxx
// Created:	Mon Feb  3 19:26:55 1997
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>


#ifndef Handle_WOKUtils_RegExp_proto_HeaderFile
#define Handle_WOKUtils_RegExp_proto_HeaderFile


#ifdef WNT

#include <Handle_WOKNT_RegExp.hxx>

typedef Handle_WOKNT_RegExp Handle_WOKUtils_RegExp;

#else

#include <Handle_WOKUnix_RegExp.hxx>

typedef Handle_WOKUnix_RegExp Handle_WOKUtils_RegExp;

#endif


#endif
