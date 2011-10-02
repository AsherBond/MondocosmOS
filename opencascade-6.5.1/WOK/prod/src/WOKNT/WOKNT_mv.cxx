#ifdef WNT
#define EXPORT
#define STRICT
#include <windows.h>
#include <Standard_Stream.hxx>
extern "C" __declspec( dllexport ) int wokMV ( int, char** );

int wokMV ( int argc, char** argv ) {

 if (  argc != 3 ) {
 
  cerr << "Usage: wokMV src dst\n" << flush;
  return 1;
 
 }  // end if

 if (  !MoveFileEx ( argv[ 1 ], argv[ 2 ], MOVEFILE_COPY_ALLOWED | MOVEFILE_REPLACE_EXISTING)  ) {
 
  char  buffer[ 2048 ];
  DWORD dwCode = GetLastError ();

  if (  !FormatMessage (
          FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_ARGUMENT_ARRAY,
          0, dwCode, MAKELANGID( LANG_NEUTRAL, SUBLANG_NEUTRAL ),
          buffer, 2048, NULL
         )
  ) wsprintf ( buffer, "Error code %d", dwCode );

  cerr << "wokMV: could not move - " << buffer << endl << flush;
  return 2;

 }  // end if

 return 0;

}  // end main
#endif
