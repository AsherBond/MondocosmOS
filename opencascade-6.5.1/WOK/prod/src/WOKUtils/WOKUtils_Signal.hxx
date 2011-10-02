// File:	WOKUtils_Signal.hxx
// Created:	Fri Jan 31 19:35:33 1997
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>


#ifndef WOKUtils_Signal_HeaderFile
#define WOKUtils_Signal_HeaderFile


#include <WOKUtils_SigHandler.hxx>

#ifndef WNT


#include <WOKUnix_Signal.hxx>

typedef WOKUnix_Signal WOKUtils_Signal;

#define WOKUtils_SIGPIPE WOKUnix_SIGPIPE
#define WOKUtils_SIGHUP WOKUnix_SIGHUP
#define WOKUtils_SIGINT WOKUnix_SIGINT
#define WOKUtils_SIGQUIT WOKUnix_SIGQUIT
#define WOKUtils_SIGILL WOKUnix_SIGILL
#define WOKUtils_SIGKILL WOKUnix_SIGKILL
#define WOKUtils_SIGBUS WOKUnix_SIGBUS
#define WOKUtils_SIGSEGV WOKUnix_SIGSEGV
#define WOKUtils_SIGCHILD WOKUnix_SIGCHILD

#endif

#endif
