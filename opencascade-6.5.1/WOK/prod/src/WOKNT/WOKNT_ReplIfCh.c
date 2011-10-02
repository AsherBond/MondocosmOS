#ifdef WNT
#define STRICT
#include <process.h>
#include <windows.h>

extern int wokCMP ( int, char** );

__declspec( dllexport ) int wokReplIfCh ( int argc, char** argv ) {

 int    status;
 char*  newArgs[ 3 ];

 if ( argc != 3 ) return 2;

 newArgs[ 0 ] = "wokCMP";
 newArgs[ 1 ] = argv[ 1 ];
 newArgs[ 2 ] = argv[ 2 ];

 status = wokCMP ( 3, newArgs );

 if ( status == 1 )

  status = MoveFileEx (
	        argv[ 1 ], argv[ 2 ], MOVEFILE_COPY_ALLOWED | MOVEFILE_REPLACE_EXISTING
		   ) ? 1 : 2;

 DeleteFile ( argv[ 1 ] ) ? 0 : 2;

 return status;

}  /* end main */
#endif
