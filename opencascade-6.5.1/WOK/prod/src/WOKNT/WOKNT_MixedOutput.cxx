#ifdef WNT
#define STRICT
#include <windows.h>

#include <WOKNT_MixedOutput.ixx>

#include <TCollection_HAsciiString.hxx>
#include <OSD_WNT.hxx>
#include <OSD_WNT_1.hxx>

#define MAX_LINE_LENGTH 2048

void                                      __fastcall _WOKNT_clear_pipe ( HANDLE );
DWORD                                     __fastcall _WOKNT_nb_to_read ( HANDLE );
Handle( TColStd_HSequenceOfHAsciiString ) __fastcall _WOKNT_read_pipe ( OSD_File*, HANDLE );
void                                      __fastcall _WOKNT_create_pipe (
                                                      Standard_Integer*, Standard_Integer*
                                                     );

WOKNT_MixedOutput :: WOKNT_MixedOutput () {

}  // end constructor

void WOKNT_MixedOutput :: Cleanup () {

 CloseStdOut ();
 WOKNT_ShellOutput :: Cleanup ();

}  // end WOKNT_MixedOutput :: Cleanup

Standard_Integer WOKNT_MixedOutput :: OpenStdOut () {

 _WOKNT_create_pipe ( &myFileChannel, &myOutHandle );
 myIO |= ( FLAG_PIPE | FLAG_READ_PIPE );

 return myOutHandle;

}  // end WOKNT_MixedOutput :: OpenStdOut

void WOKNT_MixedOutput :: CloseStdOut () {

 if (  ( HANDLE )myOutHandle != INVALID_HANDLE_VALUE  ) {
 
  CloseHandle (  ( HANDLE )myOutHandle  );
  myOutHandle = ( Standard_Integer )INVALID_HANDLE_VALUE;

 }  // end if

}  // end WOKNT_MixedOutput :: CloseStdOut

Standard_Integer WOKNT_MixedOutput :: OpenStdErr () {

 return (  ( HANDLE )myOutHandle == INVALID_HANDLE_VALUE  ) ?
         OpenStdOut () : myOutHandle;

}  // end WOKNT_MixedOutput :: OpenStdErr

void WOKNT_MixedOutput :: CloseStdErr () {

 CloseStdOut ();

}  // end WOKNT_MixedOutput :: CloseStdErr

void WOKNT_MixedOutput :: Clear () {

 _WOKNT_clear_pipe (  ( HANDLE )myFileChannel  );

}  // end WOKNT_MixedOutput :: Clear

Handle( TColStd_HSequenceOfHAsciiString ) WOKNT_MixedOutput :: Echo () {

 return _WOKNT_read_pipe (  this, ( HANDLE )myFileChannel  );

}  // end WOKNT_MixedOutput :: Echo

Handle( TColStd_HSequenceOfHAsciiString ) WOKNT_MixedOutput :: Errors () {

 return Echo ();

}  // end WOKNT_MixedOutput :: Errors

Handle( TColStd_HSequenceOfHAsciiString ) WOKNT_MixedOutput :: SyncStdOut () {

 DWORD                                     dummy;
 Handle( TColStd_HSequenceOfHAsciiString ) retVal;

 if (  ( HANDLE )myFileChannel != INVALID_HANDLE_VALUE  ) {
 
  while (  ReadFile (  ( HANDLE )myFileChannel, NULL, 0, ( LPDWORD )&dummy, NULL )  ) {
  
   if (  retVal.IsNull ()  )

    retVal = new TColStd_HSequenceOfHAsciiString ();

   retVal -> Append (  Echo ()  );

  }  // end while

  CloseStdOut ();
 
 }  // end if

 return retVal;

}  // end WOKNT_MixedOutput :: SyncStdOut

Handle( TColStd_HSequenceOfHAsciiString ) WOKNT_MixedOutput :: SyncStdErr () {

 return SyncStdOut ();

}  // end WOKNT_MixedOutput :: SyncStdErr

DWORD __fastcall _WOKNT_nb_to_read ( HANDLE hPipe ) {

 DWORD retVal = 0;

 PeekNamedPipe ( hPipe, NULL, 0, NULL, &retVal, NULL );

 return retVal;

}  // end _WOKNT_nb_to_read

void __fastcall _WOKNT_clear_pipe ( HANDLE hPipe ) {

 PBYTE buffer = NULL;
 DWORD dwBytesToRead;
 DWORD dwBuffSize = 0;

 while (  dwBytesToRead = _WOKNT_nb_to_read ( hPipe )  ) {
 
  if ( dwBytesToRead > dwBuffSize ) {
  
   if ( buffer != NULL ) delete [] buffer;

   buffer = new BYTE[ dwBuffSize = dwBytesToRead ];
  
  }  // end if

  ReadFile ( hPipe, buffer, dwBuffSize, &dwBytesToRead, NULL );
 
 }  // end while

 if ( buffer != NULL ) delete [] buffer;

}  // end _WOKNT_clear_pipe

Handle( TColStd_HSequenceOfHAsciiString ) __fastcall _WOKNT_read_pipe (
                                                      OSD_File* aFile, HANDLE hPipe
                                                     ) {

 DWORD                                     dwBytes;
 DWORD                                     dwBytesRead = 0;
 Standard_Integer                          dummy;
 TCollection_AsciiString                   aLine;
 Handle( TColStd_HSequenceOfHAsciiString ) retVal =
  new TColStd_HSequenceOfHAsciiString ();

 while (  dwBytes = _WOKNT_nb_to_read ( hPipe )  ) {
 
  dwBytesRead += dwBytes;

  aFile -> ReadLine ( aLine, MAX_LINE_LENGTH, dummy );

  retVal -> Append (  new TCollection_HAsciiString ( aLine )  );

 }  // end while

 if ( dwBytesRead == 0 ) retVal.Nullify ();

 return retVal;

}  // end _WOKNT_read_pipe

void __fastcall _WOKNT_create_pipe (
                 Standard_Integer* readPipe, Standard_Integer* writePipe
                ) {
                
 SECURITY_ATTRIBUTES sa;

 sa.nLength              = sizeof ( SECURITY_DESCRIPTOR );  // structure size
 sa.lpSecurityDescriptor = NULL;                            // default protection
 sa.bInheritHandle       = TRUE;                            // inheritable handle

 if (  !CreatePipe (
         ( PHANDLE )readPipe,   // read end of the pipe
         ( PHANDLE )writePipe,  // write end of the pipe
         &sa,                   // protection/inheritance
         4096                   // buffer size
        )
 ) *writePipe = ( Standard_Integer )INVALID_HANDLE_VALUE;
                
}  // end _WOKNT_create_pipe
#endif
