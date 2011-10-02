// File:	WOKUtils_RESyntax_proto.hxx
// Created:	Mon Feb  3 20:07:30 1997
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>


#ifndef WOKUtils_RESyntax_proto_HeaderFile
#define WOKUtils_RESyntax_proto_HeaderFile



#ifdef WNT

#include <WOKNT_RESyntax.hxx>

typedef  WOKNT_RESyntax WOKUtils_RESyntax;

#define WOKUtils_RESyntaxAWK   WOKNT_RESyntaxAWK
#define WOKUtils_RESyntaxEGREP WOKNT_RESyntaxEGREP
#define WOKUtils_RESyntaxGREP  WOKNT_RESyntaxGREP
#define WOKUtils_RESyntaxEMACS WOKNT_RESyntaxEMACS

#else
 
#include <WOKUnix_RESyntax.hxx>

typedef  WOKUnix_RESyntax WOKUtils_RESyntax;

#define WOKUtils_RESyntaxAWK   WOKUnix_RESyntaxAWK
#define WOKUtils_RESyntaxEGREP WOKUnix_RESyntaxEGREP
#define WOKUtils_RESyntaxGREP  WOKUnix_RESyntaxGREP
#define WOKUtils_RESyntaxEMACS WOKUnix_RESyntaxEMACS

#endif

#endif

