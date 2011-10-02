// File:	WOKUnix_Dir.hxx
// Author:	Jean GAUTIER
//		<jga@cobrax>


#ifndef WOKUnix_Dir_HeaderFile
#define WOKUnix_Dir_HeaderFile

#ifndef WNT 

# include <dirent.h>

typedef DIR* WOKUnix_Dir;

#else

# error "Type Dir does not exist on WNT"

#endif

#endif
