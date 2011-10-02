#ifdef WNT
#define STRICT
#include <windows.h>

#include <WOKNT_ShellOutput.ixx>

WOKNT_ShellOutput :: WOKNT_ShellOutput () {

}  // end constructor

void WOKNT_ShellOutput :: Cleanup () {

 if (  ( HANDLE )myFileChannel != INVALID_HANDLE_VALUE  ) Close ();

}  // end WOKNT_ShellOutput :: Cleanup
#endif
