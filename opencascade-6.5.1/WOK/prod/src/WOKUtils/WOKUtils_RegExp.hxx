// File:	WOKUtils_RegExp_proto.hxx
// Created:	Mon Feb  3 19:27:30 1997
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>


#ifndef WOKUtils_RegExp_proto_HeaderFile
#define WOKUtils_RegExp_proto_HeaderFile


#include <Handle_WOKUtils_RegExp.hxx>

#ifdef WNT

#include <WOKNT_RegExp.hxx>

#define  WOKUtils_RegExp WOKNT_RegExp 

#else

#include <WOKUnix_RegExp.hxx>

#define  WOKUtils_RegExp WOKUnix_RegExp

#endif


#endif
