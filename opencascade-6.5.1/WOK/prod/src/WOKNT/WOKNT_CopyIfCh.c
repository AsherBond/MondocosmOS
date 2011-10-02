#ifdef WNT
#define STRICT
#include <windows.h>

extern int wokCMP ( int, char** );

__declspec( dllexport ) int wokCopyIfCh ( int argc, char** argv ) {

 int    status;
 char*  newArgs[ 3 ];

 if ( argc != 3 ) return 2;

 newArgs[ 0 ] = "wokCMP";
 newArgs[ 1 ] = argv[ 1 ];
 newArgs[ 2 ] = argv[ 2 ];

 status = wokCMP ( 3, newArgs ); 

 if ( status )

  status = CopyFile ( argv[ 1 ], argv[ 2 ],FALSE ) ? 0 : 2;

 return status;

}  /* end main */
#endif
