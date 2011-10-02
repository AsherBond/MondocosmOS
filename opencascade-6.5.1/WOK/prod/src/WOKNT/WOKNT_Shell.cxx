#ifdef WNT
#define STRICT
#include <windows.h>
#include <tchar.h>

#ifdef CreateFile
# undef CreateFile
#endif  // CreateFile

#ifdef CreateDirectory
# undef CreateDirectory
#endif  // CreateFile

#ifdef RemoveDirectory
# undef RemoveDirectory
#endif  // CreateFile

#include <WOKNT_Shell.ixx>

#include <WOKNT_OutErrOutput.hxx>
#include <WOKTools_Messages.hxx>
#include <OSD_Environment.hxx>
#include <WOKNT_Array1OfString.hxx>
#include <WOKNT_CompareOfString.hxx>
#include <WOKNT_QuickSortOfString.hxx>

#include <OSD_Exception_CTRL_BREAK.hxx>

#pragma comment( lib, "user32.lib" )

Standard_CString _WOKNT_get_last_error_text ( void );

Standard_Integer __fastcall _WOKNT_find_environment_variable (
                             const Handle( TColStd_HSequenceOfHAsciiString )&,
                             const Handle( TCollection_HAsciiString )&
                            );

static void _WOKNT_get_env (  Handle( TColStd_HSequenceOfHAsciiString )&  );

WOKNT_Shell :: WOKNT_Shell (
                   const WOKNT_ExecutionMode anExecMode,
                   const WOKNT_OutputMode    anOutMode
                  ) {

 char aDir[1024];

 GetCurrentDirectory(1024, aDir);

 myDirectory = new TCollection_HAsciiString ( aDir );

 myOutput      = ( anOutMode == WOKNT_OutErrMixed ) ?
                  new WOKNT_MixedOutput () :
                  new WOKNT_OutErrOutput ();
 myCmdLine     = new TCollection_HAsciiString ();
 myProcess     = ( Standard_Integer )INVALID_HANDLE_VALUE;
 myLocked      = Standard_False;
 myExecMode    = anExecMode;
 myOutMode     = anOutMode;
 myEcho        = Standard_False;

 _WOKNT_get_env ( myEnvironment );

}  // end constructor

void WOKNT_Shell :: Destroy () {

 Kill ();

 WOKNT_MixedOutput* anOutput = (WOKNT_MixedOutput*)myOutput;
 delete anOutput;
}  // end WOKNT_Shell :: Destroy

void WOKNT_Shell :: Launch () {

}  // end WOKNT_Shell :: Launch

void WOKNT_Shell :: Kill () {

 if (  ( HANDLE )myProcess != INVALID_HANDLE_VALUE  ) {

  TerminateProcess (  ( HANDLE )myProcess, ( UINT )-1  );

  if (  ( HANDLE )myChannel != INVALID_HANDLE_VALUE  ) {
  
   CloseHandle (  ( HANDLE )myChannel  );
   myChannel = ( Standard_Integer )INVALID_HANDLE_VALUE;
  
  }  // end if

  myKillFlag = Standard_True;

 }  // end if

}  // end WOKNT_Shell :: Kill

void WOKNT_Shell :: Lock () {

 myLocked = Standard_True;

}  // end WOKNT_Shell :: Lock

void WOKNT_Shell :: UnLock () {

 myLocked = Standard_False;

}  // end WOKNT_Shell :: UnLock

void WOKNT_Shell :: ClearOutput () {

 (  ( WOKNT_ShellOutput* )myOutput  ) -> Clear ();

if (  !myStdOut.IsNull ()  ) myStdOut.Nullify ();
if (  !myStdErr.IsNull ()  ) myStdErr.Nullify ();

}  // end WOKNT_Shell :: ClearOutput

void WOKNT_Shell :: Execute (  const Handle( TCollection_HAsciiString )& aCmdLine  ) {

 int i, numCmd;

 if (  !myExeFlag  ) {
 
  Echo ( aCmdLine );
  Log  ( aCmdLine );
 
 } else


#ifdef WOK_VERBOSE
   VerboseMsg()("WOK_COMMAND") << "WOKNT_Shell::Execute" << aCmdLine << endm;
#endif


  myExeFlag = Standard_False;

 myKillFlag = Standard_False;

 Handle( TCollection_HAsciiString ) aCommandLine = new TCollection_HAsciiString ();

 if (  !myCmdLine.IsNull ()  ) aCommandLine = myCmdLine;

 aCommandLine -> AssignCat ( aCmdLine );

 if (  !aCmdLine.IsNull ()                         &&
       !aCmdLine -> IsEmpty ()                     &&
       !BuiltInCommand ( aCommandLine )            &&
       ( HANDLE )myProcess == INVALID_HANDLE_VALUE
 ) {

  PROCESS_INFORMATION                pi;
  STARTUPINFO                        si;
  HANDLE                             hStdInput;
  BOOL                               fRetry;
  HANDLE                             hStdOut;
  HANDLE                             hStdErr;
  SECURITY_ATTRIBUTES                sa;
  LPVOID                             env;
  Handle( TCollection_HAsciiString ) cmd = new TCollection_HAsciiString (
                                                aCommandLine -> String ()
                                               );

  sa.nLength              = sizeof ( SECURITY_ATTRIBUTES );
  sa.lpSecurityDescriptor = NULL;
  sa.bInheritHandle       = TRUE;

  if (  !CreatePipe (
          &hStdInput, ( PHANDLE )&myChannel, &sa, 0
         )
  ) {
  
   hStdInput = INVALID_HANDLE_VALUE;
   myChannel = ( Standard_Integer )INVALID_HANDLE_VALUE;
  
  }  // end if

  Handle( TColStd_HSequenceOfHAsciiString ) cmdSeq = new TColStd_HSequenceOfHAsciiString ();

  i = 1;

  while ( TRUE ) {

   aCommandLine = cmd -> Token (  _TEXT( "\x01" ), i++  );

   if (  aCommandLine -> IsEmpty ()  ) break;

   cmdSeq -> Append ( aCommandLine );

  }  // end while

  numCmd = cmdSeq -> Length ();

  if (  myStdOut.IsNull ()  ) myStdOut = new TColStd_HSequenceOfHAsciiString ();
  if (  myStdErr.IsNull ()  ) myStdErr = new TColStd_HSequenceOfHAsciiString ();

  env = BuildEnvironment ();

  for ( i = 1; i <= numCmd; ++i ) {

   cmd = new TCollection_HAsciiString (  cmdSeq -> Value ( i )  );

   if (
    BuiltInCommand (  cmd, Standard_False  ) ||
    cmd -> Value ( 1 ) == TEXT( '@' ) && myStatus
   ) continue;

   if (  cmd -> Value ( 1 ) == TEXT( '@' )  ) cmd -> Remove ( 1 );

   hStdOut = ( HANDLE )(  ( WOKNT_ShellOutput* )myOutput  ) -> OpenStdOut ();
   hStdErr = ( HANDLE )(  ( WOKNT_ShellOutput* )myOutput  ) -> OpenStdErr ();

   ZeroMemory (  &si, sizeof ( STARTUPINFO )  );
   si.cb          = sizeof ( STARTUPINFO );
   si.dwFlags     = STARTF_USESTDHANDLES | STARTF_USESHOWWINDOW;
   si.wShowWindow = SW_HIDE;
   si.hStdInput   = hStdInput;
   si.hStdOutput  = hStdOut;
   si.hStdError   = hStdErr;

   fRetry = FALSE;

   while ( TRUE ) {

    if (  !CreateProcess (
            NULL,                        // image name
            (char *) cmd -> ToCString (),         // command line
            NULL,                        // security attributes for process ( default )
            NULL,                        // security attributes for primary thread ( default )
            TRUE,                        // handle inheritance
            NORMAL_PRIORITY_CLASS,       // normal process with no special scheduling needs
            env,                         // environment
            myDirectory -> ToCString (), // current directory
            &si,                         // startup information
            &pi                          // process information
           )
    ) {

     if (  fRetry || GetLastError () != ERROR_FILE_NOT_FOUND  ) {

      ErrorMsg() << TEXT( "WOKNT_Shell :: Execute (): can not execute " )
               << TEXT( "'" ) << cmd -> ToCString () << TEXT( "' - " )
               << _WOKNT_get_last_error_text () << endm;

      myProcess = ( Standard_Integer )INVALID_HANDLE_VALUE;
      (  ( WOKNT_ShellOutput* )myOutput  ) -> Cleanup ();
      myStatus = -1;

      break;

     } else {

      OSD_Environment comSpec (  TEXT( "ComSpec" )  );

      aCommandLine = cmdSeq -> Value ( i );
      cmd -> Clear ();

      if (  comSpec.Failed ()  )
     
       cmd -> AssignCat (  TEXT( "cmd.exe" )  );

      else
         
       cmd -> AssignCat(  comSpec.Value ().ToCString ()  );

      cmd -> AssignCat (  TEXT( " /C " )  );
      cmd -> AssignCat (  aCommandLine -> ToCString ()  );
      fRetry = TRUE;

     }  // end else

    } else {

     CloseHandle ( pi.hThread );
     myProcess = ( Standard_Integer )pi.hProcess;

     if ( myExecMode == WOKNT_SynchronousMode ||
          numCmd > 1 && i < numCmd
     ) SyncAndStatus ();

     break;

    }  // end else
   
   }  // end while

  }  // end for

  CloseHandle ( hStdInput );
  delete [] env;

 }  // end if

 if ( myKillFlag && myStatus == -1 )
 
  OSD_Exception_CTRL_BREAK :: Raise (  TEXT( "*** INTERRUPT ***" )  );

 myCmdLine -> Clear ();

}  // end WOKNT_Shell :: Execute

void WOKNT_Shell :: Send ( const Handle(TCollection_HAsciiString)& aString ) {

 Standard_Integer i;
 DWORD            dwBytesWritten;


#ifdef WOK_VERBOSE
   VerboseMsg()("WOK_COMMAND") << "WOKNT_Shell::Send" << aString << endm;
#endif

 myCmdLine -> AssignCat ( aString );

 if (  !myCmdLine.IsNull () &&
        myCmdLine -> Value (  myCmdLine -> Length ()     ) == TEXT( '\n' ) &&
		myCmdLine -> Value (  myCmdLine -> Length () - 1 ) != TEXT( '\\' )
 ) {

  Echo ( myCmdLine );
  Log  ( myCmdLine );

  myCmdLine -> Trunc (  myCmdLine -> Length () - 1  );

  if (  ( HANDLE )myProcess == INVALID_HANDLE_VALUE ) {

   i         = 1;
   myExeFlag = Standard_True;
   Handle( TCollection_HAsciiString ) cmd;

   while ( TRUE ) {

    cmd = myCmdLine -> Token (  _TEXT( "\n" ), i++  );

    if (  cmd -> IsEmpty ()  ) break;

    Execute ( cmd );

   }  // end while

  } else
   
   WriteFile (  // send a string to the sub-process
    ( HANDLE )myChannel, myCmdLine -> ToCString (), myCmdLine -> Length (),
    &dwBytesWritten, NULL
   ); 
 
  myCmdLine -> Clear ();

 }  // end if

}  // end WOKNT_Shell :: Send

Standard_Integer WOKNT_Shell :: SyncAndStatus () {

 Handle( TColStd_HSequenceOfHAsciiString ) stdOut;
 Handle( TColStd_HSequenceOfHAsciiString ) stdErr;

 if (  ( HANDLE )myProcess != INVALID_HANDLE_VALUE  ) {

  (  ( WOKNT_ShellOutput* )myOutput  ) -> CloseStdOut ();
  (  ( WOKNT_ShellOutput* )myOutput  ) -> CloseStdErr ();

  if (  myStdOut.IsNull ()  ) myStdOut = new TColStd_HSequenceOfHAsciiString ();
  if (  myStdErr.IsNull ()  ) myStdErr = new TColStd_HSequenceOfHAsciiString ();

  stdOut = (  ( WOKNT_ShellOutput* )myOutput  ) -> SyncStdOut ();
  stdErr = myOutMode == WOKNT_OutErrMixed ?
            stdOut                           :
            (  ( WOKNT_ShellOutput* )myOutput  ) -> SyncStdErr ();

  if (  !stdOut.IsNull ()  ) myStdOut -> Append ( stdOut );
  if (  !stdErr.IsNull ()  ) myStdErr -> Append ( stdErr );

  (  ( WOKNT_ShellOutput* )myOutput  ) -> Cleanup ();

  WaitForSingleObject (  ( HANDLE )myProcess, INFINITE  );

  GetExitCodeProcess (  ( HANDLE )myProcess, ( LPDWORD )&myStatus  );
  CloseHandle (  ( HANDLE )myProcess  );
  CloseHandle (  ( HANDLE )myChannel  );
  myProcess = ( Standard_Integer )INVALID_HANDLE_VALUE;
  myChannel = ( Standard_Integer )INVALID_HANDLE_VALUE;

 }  // end if

 return myStatus;

}  // end WOKNT_Shell :: SyncAndStatus

Handle( TColStd_HSequenceOfHAsciiString ) WOKNT_Shell :: Errors () {

 Handle( TColStd_HSequenceOfHAsciiString ) retVal =
  myStdErr.IsNull () ? (  ( WOKNT_ShellOutput* )myOutput  ) -> Errors () : myStdErr;

 return retVal.IsNull () ? new TColStd_HSequenceOfHAsciiString () : retVal;

}  // end WOKNT_Shell :: Errors

Standard_Address WOKNT_Shell :: BuildEnvironment (
                                    const Standard_Boolean aRebuildFlag
                                   ) {

 if ( aRebuildFlag ) {
 
  myEnvironment.Nullify ();

  _WOKNT_get_env ( myEnvironment );

  return NULL;
 
 } else {

  int                      i;
  Standard_PCharacter         retVal;
  Standard_PCharacter        ptr;
  Standard_Integer         len = 0;
  WOKNT_CompareOfString comp;
  WOKNT_Array1OfString  env (  1, myEnvironment -> Length ()  );

  for ( i = 1; i <= myEnvironment -> Length (); ++i ) {
 
   len += (  lstrlen (  myEnvironment -> Value ( i ) -> ToCString ()  ) + 1  );
   env.SetValue (  i, myEnvironment -> Value ( i )  );
 
  }  // end for

  WOKNT_QuickSortOfString :: Sort ( env, comp );

  retVal = ptr = new Standard_Character[ ++len ];

  for ( i = env.Lower (); i <= env.Upper (); ++i ) {
 
   lstrcpy (  (char *)ptr,  env.Value ( i ) -> ToCString ()  );
   ptr += (  lstrlen ( ptr ) + 1  );
 
  }  // end for

  *ptr = TEXT( '\x00' );

  return retVal;

 }  // end else

}  // end WOKNT_Shell :: BuildEnvironment

void WOKNT_Shell :: AddEnvironmentVariable (
                        const Handle_TCollection_HAsciiString& aName,
                        const Handle_TCollection_HAsciiString& aValue
                       ) {

 Handle( TCollection_HAsciiString ) str = new TCollection_HAsciiString ();

 str -> AssignCat (  aName -> String ().ToCString ()   );

 RemoveEnvironmentVariable ( str );

 str -> AssignCat (  TEXT( "=" )  );
 str -> AssignCat ( aValue        );
 myEnvironment -> Prepend ( str );

}  // end WOKNT_Shell :: AddEnvironmentVariable

void WOKNT_Shell :: RemoveEnvironmentVariable (
                        const Handle_TCollection_HAsciiString& aName
                       ) {

 Standard_Integer i;

 if (  i = _WOKNT_find_environment_variable ( myEnvironment, aName )  )

  myEnvironment -> Remove ( i );

}  // end WOKNT_Shell :: RemoveEnvironmentVariabl

Handle( TCollection_HAsciiString )
 WOKNT_Shell :: EnvironmentVariable ( const Handle_TCollection_HAsciiString& aName ) const {
 
 Standard_Integer                   i;
 Handle( TCollection_HAsciiString ) retVal;

 if (  i = _WOKNT_find_environment_variable ( myEnvironment, aName )  )

  retVal = myEnvironment -> Value ( i ) -> Token (  _TEXT( "=" ), 2  );

 return retVal;
 
}  // end WOKNT_Shell :: EnvironmentVariable

void WOKNT_Shell :: Echo ( const Handle_TCollection_HAsciiString& aStr ) const {

 if ( myEcho ) {
 
  WOKTools_Info shellInfo = InfoMsg();

  shellInfo.Init             ();
  shellInfo.DontPrintHeader  ();
  shellInfo.DontPrintContext ();

  shellInfo << TEXT( "WOKNT_Shell :: Echo ()" ) << aStr << endm;
 
 }  // end if

}  // end WOKNT_Shell :: Echo

void WOKNT_Shell :: Log ( const Handle_TCollection_HAsciiString& aStr ) const {

 if (  !myLogFile.IsNull ()  ) {
 
  ofstream logFile (  myLogFile -> Name () -> ToCString (), ios :: app  );

  if (  logFile.good ()  ) {

   logFile << aStr -> ToCString ();
   logFile.close ();

  }  // end if
 
 }  // end if

}  // end WOKNT_Shell :: Log

void WOKNT_Shell :: LogInFile ( const Handle_WOKNT_Path& aPath ) {

 if (  !aPath.IsNull ()  ) {
 
  if (  !aPath -> Exists ()  ) {
  
   aPath -> CreateFile ( Standard_True );

   if (  aPath -> Exists ()  )

    myLogFile = aPath;
  
  }  // end if
 
 }  // end if

}  // end WOKNT_Shell :: LogInFile

void WOKNT_Shell :: NoLog () {

 myLogFile.Nullify ();

}  // end WOKNT_Shell :: NoLog

Standard_CString _WOKNT_get_last_error_text ( void ) {

 static Standard_Character buffer[ 2048 ];

 DWORD errCode = GetLastError ();

 if (  !FormatMessage (
         FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_ARGUMENT_ARRAY,
         0, errCode, MAKELANGID( LANG_NEUTRAL, SUBLANG_NEUTRAL ),
         buffer, 2048, NULL
        )
 ) {

  wsprintf (  buffer, TEXT( "error code %d" ), errCode  );
  SetLastError ( errCode );

 }  // end if

 return buffer;

}  // end _get_last_error_text

Standard_Integer __fastcall _WOKNT_find_environment_variable (
                             const Handle( TColStd_HSequenceOfHAsciiString )& envBlock,
                             const Handle( TCollection_HAsciiString )&        envVar
                            ) {
                            
 Standard_Integer retVal = 0;          
                            
 Handle( TCollection_HAsciiString ) str = new TCollection_HAsciiString ();

 str -> AssignCat  (  envVar -> String ().ToCString ()  );
 str -> AssignCat  (  TEXT( "=" )                       );
 str -> LeftAdjust ();

 for ( int i = 1; i <= envBlock -> Length (); ++i )

  if (   envBlock -> Value ( i ) -> Search ( str ) == 1   ) {
  
   retVal = i;
   break;
  
  }  // end if

 return retVal;

}  // end _WOKNT_find_environment_variable

static void _WOKNT_get_env (  Handle( TColStd_HSequenceOfHAsciiString )& seq  ) {

 Standard_PCharacter ptr, env;

 seq = new TColStd_HSequenceOfHAsciiString ();

 env = ptr = (Standard_PCharacter)GetEnvironmentStrings ();

  if (  *ptr != TEXT( '\x00' )  )

   do {

    seq -> Append (  new TCollection_HAsciiString ( ptr )  );
    ptr += (  lstrlen ( ptr ) + 1  );

   } while (  *ptr != TEXT( '\x00' )  );

 FreeEnvironmentStrings ( env );

}  // end _WOKNT_get_env
#endif
