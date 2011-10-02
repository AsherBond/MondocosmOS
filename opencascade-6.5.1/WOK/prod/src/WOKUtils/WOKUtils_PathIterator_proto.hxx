// File:	WOKUtils_PathIterator.hxx
// Created:	Fri Jan 31 19:34:18 1997
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>


#ifndef WOKUtils_PathIterator_HeaderFile
#define WOKUtils_PathIterator_HeaderFile

#ifdef WNT

#include <WOKNT_PathIterator.hxx>

typedef WOKNT_PathIterator WOKUtils_PathIterator;

#else

#include <WOKUnix_PathIterator.hxx>

typedef WOKUnix_PathIterator WOKUtils_PathIterator;

#endif



#endif
