// File:	WOKUnix_MaxPipeSize.hxx
// Created:	Fri May 12 10:09:01 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>
#ifndef WNT

#ifndef WOKUnix_MaxPipeSize_HeaderFile
#define WOKUnix_MaxPipeSize_HeaderFile

#include <sys/param.h>

#ifdef HAVE_CONFIG_H
# include <config.h>
#endif

#ifdef HAVE_LIMITS
# include <limits>
#elif defined (HAVE_LIMITS_H)
# include <limits.h>
#endif

#define MAX_PIPE_SIZE PIPE_BUF

#endif
#endif
