#ifdef WNT
#define STRICT
#include <windows.h>

#include <WOKNT_OutErrOutput.ixx>

#include <OSD_WNT.hxx>
#include <OSD_WNT_1.hxx>

void                                      __fastcall _WOKNT_clear_pipe ( HANDLE );
DWORD                                     __fastcall _WOKNT_nb_to_read ( HANDLE );
Handle( TColStd_HSequenceOfHAsciiString ) __fastcall _WOKNT_read_pipe ( OSD_File*, HANDLE );
void                                      __fastcall _WOKNT_create_pipe (
                                                      Standard_Integer*, Standard_Integer*
                                                     );

WOKNT_OutErrOutput :: WOKNT_OutErrOutput () {

}  // end constructor

void WOKNT_OutErrOutput :: Cleanup () {

 CloseStdOut ();
 CloseStdErr ();

 if (  ( HANDLE )myErrHandleR != INVALID_HANDLE_VALUE  ) {
 
  CloseHandle (  ( HANDLE )myErrHandleR  );
  myErrHandleR = ( Standard_Integer )INVALID_HANDLE_VALUE;

 }  // end if

 WOKNT_ShellOutput :: Cleanup ();

}  // end WOKNT_OutErrOutput :: Cleanup

Standard_Integer WOKNT_OutErrOutput :: OpenStdErr () {

 _WOKNT_create_pipe ( &myErrHandleR, &myErrHandleW );
 myIO |= ( FLAG_PIPE | FLAG_READ_PIPE );

 return myErrHandleW;

}  // end WOKNT_OutErrOutput :: OpenStdErr

void WOKNT_OutErrOutput :: CloseStdErr () {

 if (  ( HANDLE )myErrHandleW != INVALID_HANDLE_VALUE  ) {
 
  CloseHandle (  ( HANDLE )myErrHandleW  );
  myErrHandleW = ( Standard_Integer )INVALID_HANDLE_VALUE;

 }  // end if

}  // end WOKNT_OutErrOutput :: CloseStdErr

void WOKNT_OutErrOutput :: Clear () {

 WOKNT_MixedOutput :: Clear ();

 _WOKNT_clear_pipe (  ( HANDLE )myErrHandleR  );

}  // end WOKNT_OutErrOutput :: Clear

Handle( TColStd_HSequenceOfHAsciiString ) WOKNT_OutErrOutput :: Errors () {

 Handle( TColStd_HSequenceOfHAsciiString ) retVal;
 Standard_Integer                          handle;

 handle        = myFileChannel;
 myFileChannel = myErrHandleR;

 retVal = _WOKNT_read_pipe (  this, ( HANDLE )myFileChannel  );

 myFileChannel = handle;

 return retVal;

}  // end WOKNT_OutErrOutput :: Errors

Handle( TColStd_HSequenceOfHAsciiString ) WOKNT_OutErrOutput :: SyncStdErr () {

 DWORD                                     dummy;
 Handle( TColStd_HSequenceOfHAsciiString ) retVal;

 if (  ( HANDLE )myErrHandleR != INVALID_HANDLE_VALUE  ) {
 
  while (  ReadFile (  ( HANDLE )myErrHandleR, NULL, 0, ( LPDWORD )&dummy, NULL )  ) {
  
   if (  retVal.IsNull ()  )

    retVal = new TColStd_HSequenceOfHAsciiString ();

   retVal -> Append (  Errors ()  );

  }  // end while

  CloseStdErr ();
  CloseHandle (  ( HANDLE )myErrHandleR  );
 
 }  // end if

 return retVal;

}  // end WOKNT_OutErrOutput :: SyncStdErr
#endif
