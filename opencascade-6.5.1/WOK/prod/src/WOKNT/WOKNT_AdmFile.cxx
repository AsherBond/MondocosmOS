#ifdef WNT
#define STRICT
#include <windows.h>

#include <WOKNT_AdmFile.ixx>

#include <WOKTools_Messages.hxx>
#include <OSD_Protection.hxx>

#define MAX_LINE_LENGTH 1024

#define RAISE() Standard_ProgramError :: Raise (  TEXT( "WOKNT_AdmFile :: Read () failed" )  );

WOKNT_AdmFile :: WOKNT_AdmFile ( const Handle( WOKNT_Path )& aPath ) :
                    OSD_File (   OSD_Path (  aPath -> Name () -> String ()  )   ) {

}  // end constructor

Handle( TColStd_HSequenceOfHAsciiString ) WOKNT_AdmFile :: Read () {

 TCollection_AsciiString                   str;
 Handle( TColStd_HSequenceOfHAsciiString ) retVal = new TColStd_HSequenceOfHAsciiString;

 if (  !Exists ()  ) {
   
  ErrorMsg() << TEXT( "WOKNT_AdmFile :: Read (): " << "file '" ) << Name ().ToCString ()
           << TEXT( "' does not exists" ) << endm;
  RAISE();
 
 } else if (  KindOfFile () != OSD_FILE  ) {
 
  ErrorMsg() << TEXT( "WOKNT_AdmFile :: Read (): " << "file '" ) << Name ().ToCString ()
           << TEXT( "' is not a file" ) << endm;
  RAISE();
  
 } else {
 
  Standard_Boolean fNext = Standard_False;
  Standard_Boolean isNext;
  Standard_Integer nRead;

  Open (  OSD_ReadOnly, OSD_Protection ()  );

  while ( 1 ) {
  
   ReadLine ( str, MAX_LINE_LENGTH, nRead );

   if (  IsAtEnd () || Failed ()  ) break;

   str.LeftAdjust ();
   str.RemoveAll ( '\r' );
   str.RemoveAll ( '\n' );

   if (  !str.IsEmpty () && str.Value ( 1 ) != TEXT( '#' )  ) {
   
    if (   str.Value (  str.Length ()  ) == TEXT( '\\' )   ) {
    
     str.Trunc (  str.Length () - 1  );
     isNext = Standard_True;
    
    } else

     isNext = Standard_False;

    if ( fNext )

     retVal -> Value (  retVal -> Length ()  ) -> AssignCat (  str.ToCString ()  );

    else

     retVal -> Append (   new TCollection_HAsciiString (  str.ToCString ()  )   );

    fNext = isNext;
   
   } else

    fNext = Standard_False;
  
  }  // end while
 
 }  // end else

 Close ();

 if (  Failed ()  ) {
 
  Perror ();
  RAISE();
 
 }  // end if

 return retVal;

}  // end WOKNT_AdmFile :: Read

TCollection_AsciiString WOKNT_AdmFile :: Name () const {

 TCollection_AsciiString retVal;
 OSD_Path                path;

 Path ( path );
 path.SystemName ( retVal );

 return retVal;

}  // end WOKNT_AdmFile :: Name
#endif
