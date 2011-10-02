#ifndef WNT
// File:	WOKUnix_StatBuf.hxx
// Author:	Jean GAUTIER
//		<jga@cobrax>


#ifndef WOKUnix_StatBuf_HeaderFile
#define WOKUnix_StatBuf_HeaderFile

#ifndef WNT 

#include <sys/types.h>
#include <sys/stat.h>

typedef struct stat WOKUnix_StatBuf;

#else

#error "Type StatBuf does not exist on WNT"

#endif

#endif
#endif
