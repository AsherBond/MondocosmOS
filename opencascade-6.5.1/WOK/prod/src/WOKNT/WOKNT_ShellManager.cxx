#ifdef WNT
#define STRICT
#include <windows.h>
#include <tchar.h>


#include <OSD_Exception_CTRL_BREAK.hxx>

#include <WOKNT_ShellManager.ixx>

#include <WOKNT_Shell.hxx>
#include <WOKNT_SequenceOfShell.hxx>

static BOOL s_fCtrlBreak;

static WOKNT_SequenceOfShell shells;

static BOOL CALLBACK interruptHandler ( DWORD );

Handle( WOKNT_Shell ) WOKNT_ShellManager :: GetShell () {

 Standard_Integer         i;
 Handle( WOKNT_Shell ) aShell;

 for ( i = 1; i <= shells.Length (); i++ ) {

  aShell = shells.Value ( i );

  if (  !aShell -> IsLocked ()  ) {
  
   aShell -> BuildEnvironment ( Standard_True );
  
   return aShell;

  }  // end if
 
 }  // end for

 aShell = new WOKNT_Shell ();

 shells.Append ( aShell );

 aShell -> BuildEnvironment ( Standard_True );

 return aShell;

}  // end WOKNT_ShellManager :: GetShell

void WOKNT_ShellManager :: Arm () {

 s_fCtrlBreak = FALSE;
 SetConsoleCtrlHandler ( &interruptHandler, TRUE );

}  // end WOKNT_ShellManager :: Arm

void WOKNT_ShellManager :: UnArm () {

 SetConsoleCtrlHandler ( &interruptHandler, FALSE );

}  // end WOKNT_ShellManager :: UnArm

void WOKNT_ShellManager :: KillAll () {

 for ( Standard_Integer i = 1; i <= shells.Length (); ++i )
 
  shells.Value ( i ) -> Kill ();

 shells.Clear ();

}  // end WOKNT_ShellManager :: KillAll

void WOKNT_ShellManager :: Break () {

 if ( s_fCtrlBreak ) {

  s_fCtrlBreak = FALSE;
  
  OSD_Exception_CTRL_BREAK :: Raise (  TEXT( "*** INTERRUPT ***" )  ); 
 
 }  // end if

}  // end WOKNT_ShellManager :: Break

static BOOL CALLBACK interruptHandler ( DWORD dwCode ) {

 WOKNT_ShellManager :: KillAll ();
 
 if ( dwCode == CTRL_C_EVENT || dwCode == CTRL_BREAK_EVENT ) {
 
  MessageBeep ( MB_ICONEXCLAMATION );
  return s_fCtrlBreak = TRUE;

 }  // end if

 exit ( 254 );

 return TRUE;

}  // end interruptHandler
#endif
