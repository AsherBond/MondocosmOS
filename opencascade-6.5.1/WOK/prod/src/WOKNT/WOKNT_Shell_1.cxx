#ifdef WNT
#define STRICT
#include <windows.h>

#include <WOKNT_Shell.hxx>
#include <TCollection_HAsciiString.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <WOKNT_Array1OfRegExp.hxx>
#include <WOKTools_Messages.hxx>
#include <OSD_Environment.hxx>
#include <OSD_Path.hxx>
#include <OSD_SharedLibrary.hxx>

#include <WOKNT_regexp.h>
#include <stdio.h>

typedef int ( *EXT_FUNC ) ( int, TCHAR** );

#define PATTERN_CHAR_STRING        1
#define PATTERN_ENV_VARIABLE       2
#define PATTERN_WORD               3
#define PATTERN_SET_COMMAND        4
#define PATTERN_SETENV_COMMAND     5
#define PATTERN_UNSETENV_COMMAND   6
#define PATTERN_CD_COMMAND         7
#define PATTERN_CHAR_STRING_1      8
#define PATTERN_COMMAND_SEPARATOR  9
#define PATTERN_CONTINUE          10

const Standard_CString patterns[] = {

 _TEXT( "\"(\"\"|[^\"])*\"" ), // character string pattern
 _TEXT( "%[^%]+%" ),           // Windows NT environment variable syntax
 _TEXT( "*.[^ \t\"%;]*" ),     // string quoted by whitespaces, '"' or '%' or ';'
 _TEXT( "set .+=.*" ),         // 'set' command syntax ( 'set var=val' )
 _TEXT( "setenv .+ .+" ),      // 'setenv' command syntax ( 'setenv var val' )
 _TEXT( "unsetenv .+" ),       // 'unsetenv' command syntax ( 'unsetenv ver' )
 _TEXT( "cd$|cd .+" ),         // 'cd' command syntax
 _TEXT( "'(''|[^\'])*'" ),     // character string pattern
 _TEXT( "[ \t]*;" ),           // command separator ( semicolon )
 _TEXT( "[ \t]*\\\\" )         // continuation line ( backslash )

};  // end patterns

#define NUM_PATTERNS (  sizeof ( patterns ) / sizeof ( Standard_CString )  )

static _TUCHAR caseTrans[ ( 1 << BYTEWIDTH ) ] = {  // translation table to perform
                                                    // character case translation
    000, 001, 002, 003, 004, 005, 006, 007,
    010, 011, 012, 013, 014, 015, 016, 017,
    020, 021, 022, 023, 024, 025, 026, 027,
    030, 031, 032, 033, 034, 035, 036, 037,
    040, 041, 042, 043, 044, 045, 046, 047,
    050, 051, 052, 053, 054, 055, 056, 057,
    060, 061, 062, 063, 064, 065, 066, 067,
    070, 071, 072, 073, 074, 075, 076, 077,
    0100, 0101, 0102, 0103, 0104, 0105, 0106, 0107,
    0110, 0111, 0112, 0113, 0114, 0115, 0116, 0117,
    0120, 0121, 0122, 0123, 0124, 0125, 0126, 0127,
    0130, 0131, 0132, 0133, 0134, 0135, 0136, 0137,
    0140, 0101, 0102, 0103, 0104, 0105, 0106, 0107,
    0110, 0111, 0112, 0113, 0114, 0115, 0116, 0117,
    0120, 0121, 0122, 0123, 0124, 0125, 0126, 0127,
    0130, 0131, 0132, 0173, 0174, 0175, 0176, 0177,
    0200, 0201, 0202, 0203, 0204, 0205, 0206, 0207,
    0210, 0211, 0212, 0213, 0214, 0215, 0216, 0217,
    0220, 0221, 0222, 0223, 0224, 0225, 0226, 0227,
    0230, 0231, 0232, 0233, 0234, 0235, 0236, 0237,
    0240, 0241, 0242, 0243, 0244, 0245, 0246, 0247,
    0250, 0251, 0252, 0253, 0254, 0255, 0256, 0257,
    0260, 0261, 0262, 0263, 0264, 0265, 0266, 0267,
    0270, 0271, 0272, 0273, 0274, 0275, 0276, 0277,
    0300, 0301, 0302, 0303, 0304, 0305, 0306, 0307,
    0310, 0311, 0312, 0313, 0314, 0315, 0316, 0317,
    0320, 0321, 0322, 0323, 0324, 0325, 0326, 0327,
    0330, 0331, 0332, 0333, 0334, 0335, 0336, 0337,
    0340, 0341, 0342, 0343, 0344, 0345, 0346, 0347,
    0350, 0351, 0352, 0353, 0354, 0355, 0356, 0357,
    0360, 0361, 0362, 0363, 0364, 0365, 0366, 0367,
    0370, 0371, 0372, 0373, 0374, 0375, 0376, 0377
  
};

static WOKNT_Array1OfRegExp _shellRegExp ( 1, NUM_PATTERNS );
static OSD_SharedLibrary       _extLib;
static HMODULE                 _hWOKCXModule;

extern "C" __declspec( dllexport ) int __fastcall __WOKNT_INIT__ ( 
                                                   unsigned hInstance, unsigned long reason_for_call
                                                  ) {

 if ( reason_for_call == DLL_PROCESS_ATTACH ) _hWOKCXModule = ( HMODULE )hInstance;

 return 1;

}  // end __WOKNT_INIT__

extern "C" __declspec( dllexport ) int __fastcall __TKWOK_INIT__ (
                                                   unsigned hInstance, unsigned long reason_for_call
                                                  ) {

 if ( reason_for_call == DLL_PROCESS_ATTACH ) _hWOKCXModule = ( HMODULE )hInstance;

 return 1;

}  // end __TKWOK_INIT__

class _regExpInit {  // provides initialisation in case of UNICODE environment

 public:
  
  _regExpInit () {

   int i;

   for ( i = ( 1 << CHAR_BIT ); i < ( 1 << BYTEWIDTH ); ++i  )

    caseTrans[ i ] = ( _TUCHAR )i;

   for ( i = 1; i <= NUM_PATTERNS; ++i )

    _shellRegExp.SetValue ( i, new WOKNT_RegExp (
                                    new TCollection_HAsciiString ( patterns[ i - 1 ] ),
                                    WOKNT_RESyntaxAWK, caseTrans
                                   )
                 );

  }  // end constructor

};  // end _regExpInit

static _regExpInit initializeShellRegExp;

Standard_Integer __fastcall _WOKNT_find_environment_variable (
                             const Handle( TColStd_HSequenceOfHAsciiString )&,
                             const Handle( TCollection_HAsciiString )&
                            );
Standard_CString _WOKNT_get_last_error_text ( void );
Standard_Integer _rm_command ( Standard_CString );
Standard_Integer _chmod_command ( Standard_CString );

static void __fastcall _WOKNT_strip_string (  Handle( TCollection_HAsciiString )&  );

#define MATCH( i ) (  _shellRegExp.Value (  ( i )  ) ->          \
                      Match (  tmpStr, 1, len+1  ) \
                   )

#define C_MATCH( i ) (  _shellRegExp.Value (  ( i )  ) ->        \
                        Match (  aCmd,1 ,len+1  ) \
                     )

Standard_Boolean WOKNT_Shell :: BuiltInCommand (
                                    Handle_TCollection_HAsciiString& aCmd,
                                    const Standard_Boolean           doParse
                                   ) {

 Handle( TCollection_HAsciiString )tmpStr =
  new TCollection_HAsciiString (  aCmd -> String ()  );
 Standard_Integer                  len;
 Standard_Integer                  envIdx;
 Standard_Integer                  pos = 1;
 EXT_FUNC                          xFunc;

 aCmd   -> Clear ();
 tmpStr -> RightAdjust ();
 len = tmpStr -> Length ();

 while ( len ) {

  if (   (  pos = MATCH( PATTERN_CHAR_STRING   )   ) > 0  ||
         (  pos = MATCH( PATTERN_CHAR_STRING_1 )   ) > 0
  ) {
  
   aCmd -> AssignCat (  tmpStr -> SubString ( 1, pos )  );
   len -= pos;
  
  } else if (   (  pos = MATCH( PATTERN_ENV_VARIABLE )  ) > 0   ) {

   envIdx = 0;

   if ( !doParse ) {

    Handle( TCollection_HAsciiString ) envVar = tmpStr -> SubString ( 2, pos - 1 );

    if (   !_tcscmp (  envVar -> ToCString (), _TEXT( "status" )  )   ) {
    
     TCHAR buff[ 20 ];

     envIdx = 1;
     aCmd -> AssignCat (
              new TCollection_HAsciiString (  _ltot ( myStatus, buff, 10 )  )
             );
    
    } else if (  envIdx = _WOKNT_find_environment_variable (
                           myEnvironment,
                           tmpStr -> SubString ( 2, pos - 1 )
                          )
           ) aCmd -> AssignCat (
                      myEnvironment -> Value ( envIdx ) -> Token (  _TEXT( "=" ), 2  )
                     );

   }  // end if
   
   if ( doParse || !envIdx ) aCmd -> AssignCat (  tmpStr -> SubString ( 1, pos )  );

  } else if (   ( pos = MATCH( PATTERN_COMMAND_SEPARATOR )  ) > 0   ) {

   aCmd -> AssignCat (  _TEXT( "\x01" )  );

  } else if (   ( pos = MATCH( PATTERN_CONTINUE )  ) > 0   ) {

   aCmd -> AssignCat (  _TEXT( " " )  );

  } else if (   (  pos = MATCH( PATTERN_WORD )  ) > 0   ) {

   aCmd -> AssignCat (  tmpStr -> SubString ( 1, pos )  );

  } else break;
  
  tmpStr -> Remove ( 1, pos );

  if (   tmpStr -> Length () > 0 && _istspace (  tmpStr -> Value ( 1 )  )   )

   aCmd -> AssignCat (  _TEXT( " " )  );

  tmpStr -> LeftAdjust ();
  len = tmpStr -> Length ();

 }  // end while

 if ( doParse ) return Standard_False;

 aCmd -> LeftAdjust ();
 len = aCmd -> Length ();

 if (  C_MATCH( PATTERN_SET_COMMAND ) > 0  ) {

  Standard_Integer                   valLen;
  Handle( TCollection_HAsciiString ) name, value;

  name   = aCmd -> Token (  _TEXT( "=" ), 1  );
  value  = aCmd -> Token (  _TEXT( "=" ), 2  );
  valLen = value -> Length ();

  name -> Remove ( 1, 3 );
  name -> LeftAdjust ();

  if (   !_tcscmp (  name -> ToCString (), _TEXT( "status" )  )   ) {
  
   if (  !value -> IsEmpty ()  ) myStatus = _ttoi (  value -> ToCString ()  );

   return Standard_True;
  
  } else if (  value -> IsEmpty ()  ) {
  
   RemoveEnvironmentVariable ( name );

  } else
  
   AddEnvironmentVariable ( name, value );

 } else if (  C_MATCH( PATTERN_SETENV_COMMAND ) > 0  ) {

  Handle( TCollection_HAsciiString ) name, value;

  name  = aCmd -> Token (  _TEXT( " " ), 2  );
  value = aCmd -> Token (  _TEXT( " " ), 3  );

  _WOKNT_strip_string ( name  );
  _WOKNT_strip_string ( value );

  AddEnvironmentVariable ( name, value );

 } else if (  C_MATCH( PATTERN_UNSETENV_COMMAND ) > 0  ) {
 
  Handle( TCollection_HAsciiString ) name = aCmd -> Token (  _TEXT( " " ), 2  );

  _WOKNT_strip_string ( name );

  RemoveEnvironmentVariable ( name );

 } else if (  C_MATCH( PATTERN_CD_COMMAND ) > 0  ) {
 
  aCmd -> AssignCat (  _TEXT( " " )  );

  Handle ( TCollection_HAsciiString ) newDir = aCmd -> SubString (  3, aCmd -> Length ()  );

  newDir -> LeftAdjust ();
  
  if ( newDir -> IsEmpty ()  ) {

   Handle( TCollection_HAsciiString ) homeDir = new TCollection_HAsciiString ();
   OSD_Environment                    homeDrive (  _TEXT( "HOMEDRIVE" )  );
   OSD_Environment                    homePath  (  _TEXT( "HOMEPATH"  )  );

   homeDir -> AssignCat (  homeDrive.Value ().ToCString ()  );
   homeDir -> AssignCat (  homePath.Value  ().ToCString ()  );

   myDirectory = homeDir;

  } else {
	  char aDir[1024];

	 GetCurrentDirectory(1024, aDir);

	 TCollection_AsciiString str = aDir;

    if (   !SetCurrentDirectory (  newDir -> ToCString ()  )   ) {
   
    Handle( TCollection_HAsciiString ) errMsg = new TCollection_HAsciiString ();

    errMsg -> Cat (  _TEXT( "'" )  );
    errMsg -> Cat ( newDir );
    errMsg -> Cat (  _TEXT( "' - " )  );
    errMsg -> Cat (  _WOKNT_get_last_error_text ()  );

    if (  myStdErr.IsNull ()  ) myStdErr = new TColStd_HSequenceOfHAsciiString ();

    myStdErr -> Append ( errMsg );

   } else {

    SetCurrentDirectory (  str.ToCString ()  );
    myDirectory = newDir;
   }  // end else
  
  }  // end else

 } else {

  Standard_Boolean                   fCond = Standard_False; 
  Standard_Integer                   cLen;
  Handle( TCollection_HAsciiString ) command = aCmd -> Token ( " \t", 1 );

  cLen = command -> Length ();

  if (  command -> Value ( 1 ) == _TEXT( '@' )  ) {
  
   fCond = Standard_True;
   command -> Remove ( 1 );

  }  // end if

  if (    (   xFunc = ( EXT_FUNC )GetProcAddress (
                                   _hWOKCXModule, command -> ToCString ()
                                  )
          ) != NULL
  ) {

   if (  fCond && !myStatus  ) return Standard_True;

   TCHAR** argv;

   Handle( TCollection_HAsciiString        ) item;
   Handle( TColStd_HSequenceOfHAsciiString ) args =
    new TColStd_HSequenceOfHAsciiString ();

   aCmd -> Remove ( 1, cLen );
   aCmd -> LeftAdjust ();

   args -> Append ( command );

   tmpStr = aCmd;
   len = tmpStr -> Length ();

   while ( len ) {
  
    if (   (  pos = MATCH( PATTERN_CHAR_STRING   )   ) > 0  ||
           (  pos = MATCH( PATTERN_CHAR_STRING_1 )   ) > 0
    ) {
  
     item = tmpStr -> SubString ( 1, pos );
     len -= pos;
  
     _WOKNT_strip_string ( item );

    } else if (   (  pos = MATCH( PATTERN_WORD )  ) > 0   ) {

     item = tmpStr -> SubString ( 1, pos );

    } else break;

    tmpStr -> Remove ( 1, pos );
    tmpStr -> LeftAdjust ();
    len = tmpStr -> Length ();
    args -> Append ( item );
 
   }  // end while

   argv = new TCHAR*[ args -> Length () ];

   for ( int i = 1; i <= args -> Length (); ++i )

    argv[ i - 1 ] = (TCHAR*)args -> Value ( i ) -> ToCString ();

   myStatus = ( *xFunc ) (  args -> Length (), argv  );

   delete [] argv;

  } else

   return Standard_False;

 }  // end else

 return Standard_True;

}  //end WOKNT_Shell :: BuiltInCommand

static void __fastcall _WOKNT_strip_string (  Handle( TCollection_HAsciiString )& str ) {

 Standard_Integer len = str -> Length ();

 if (  str -> Value ( 1 )   == _TEXT( '"' ) &&
       str -> Value ( len ) == _TEXT( '"' )
 ) {
 
  str -> Remove ( 1 );
  str -> Remove ( len - 1 );

  while (    (   len = str -> Search (  _TEXT( "\"\"" )  )   )!= -1    )

   str -> Remove ( len );
 
 } else if (  str -> Value ( 1 )   == _TEXT( '\'' ) &&
              str -> Value ( len ) == _TEXT( '\'' )
        ) {
 
  str -> Remove ( 1 );
  str -> Remove ( len - 1 );

  while (    (   len = str -> Search (  _TEXT( "\'\'" )  )   )!= -1    )

   str -> Remove ( len );

 }  // end else

}  // end _WOKNT_strip_string
#endif
