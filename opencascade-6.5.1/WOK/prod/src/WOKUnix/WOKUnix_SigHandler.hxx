#ifndef WNT
// File:	WOKUnix_SigHandler.hxx
// Created:	Wed May 24 18:35:36 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#ifndef WOKUnix_SigHandler_HeaderFile
#define WOKUnix_SigHandler_HeaderFile

#if defined (__sun ) || defined ( SOLARIS )
# include <floatingpoint.h>
# include <sys/machsig.h>

# define FPE_FLTDIV_TRAP FPE_FLTDIV 
# define FPE_INTDIV_TRAP FPE_INTDIV 
# define FPE_FLTOVF_TRAP FPE_FLTOVF 
# define FPE_INTOVF_TRAP FPE_INTOVF
# define FPE_FLTUND_TRAP FPE_FLTUND 
 extern "C" {int ieee_handler(char *,char *, sigfpe_handler_type&);}
#endif


#if defined (__irix) || defined ( IRIX )
# include <sys/siginfo.h>
# define FPE_FLTDIV_TRAP FPE_FLTDIV 
# define FPE_INTDIV_TRAP FPE_INTDIV 
# define FPE_FLTOVF_TRAP FPE_FLTOVF 
# define FPE_INTOVF_TRAP FPE_INTOVF
# define FPE_FLTUND_TRAP FPE_FLTUND 
#endif 

typedef void (* WOKUnix_SigHandler) (int);

#endif
#endif
