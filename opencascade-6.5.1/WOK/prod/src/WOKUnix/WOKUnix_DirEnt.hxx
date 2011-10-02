// File:	WOKUnix_Dir.hxx
// Author:	Jean GAUTIER
//		<jga@cobrax>


#ifndef WOKUnix_DirEnt_HeaderFile
#define WOKUnix_DirEnt_HeaderFile

#ifndef WNT 

# include <dirent.h>

typedef struct dirent* WOKUnix_DirEnt;

#else

#error "Type DirEnt does not exist on WNT"

#endif

#endif
