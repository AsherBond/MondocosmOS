#ifndef WNT
// File:	WOKUnix_FDSet.hxx
// Created:	Tue May  9 15:25:38 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#ifndef WOKUnix_FDSet_HeaderFile
#define WOKUnix_FDSet_HeaderFile

#if (!defined(__hpux))  && (!defined(HPUX) )
#include <sys/select.h>
#define WOKUnix_FDSet_CAST fd_set *
#endif

#if defined(__hpux)  || defined(HPUX) 
#include <sys/param.h>
#include <sys/types.h>
#include <sys/time.h>
#define WOKUnix_FDSet_CAST fd_set *
#endif

typedef fd_set WOKUnix_FDSet;

#endif

#endif
