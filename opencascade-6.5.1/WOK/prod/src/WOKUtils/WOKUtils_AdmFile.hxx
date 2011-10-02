// File:	WOKUtils_AdmFile.hxx
// Created:	Fri Jan 31 19:36:31 1997
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>


#ifndef WOKUtils_AdmFile_HeaderFile
#define WOKUtils_AdmFile_HeaderFile


#ifdef WNT

#include <WOKNT_AdmFile.hxx>

typedef WOKNT_AdmFile WOKUtils_AdmFile;

#else

#include <WOKUnix_AdmFile.hxx>

typedef WOKUnix_AdmFile WOKUtils_AdmFile;

#endif


#endif
