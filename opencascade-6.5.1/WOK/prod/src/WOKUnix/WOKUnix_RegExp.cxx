#ifndef WNT

#include <WOKUnix_RegExp.ixx>

#include <WOKUnix_regexp.h>

#define BUFFER (  ( PRE_PATTERN_BUFFER )myBuffer  )
#define RAISE( str ) Standard_ProgramError :: Raise (   _TEXT(  ( str )  )   );

static _TCHAR errBuff[ 128 ];

#ifndef WNT
# define _tcscpy strcpy
# define _tcscat strcat
#endif //WNT

WOKUnix_RegExp :: WOKUnix_RegExp () {

 myBuffer = NULL;

}  // end constructor ( 1 )

WOKUnix_RegExp :: WOKUnix_RegExp (
                    const Handle( TCollection_HAsciiString )& aPattern,
                    const WOKUnix_RESyntax                   aSyntax,
                    const Standard_Address                    aTransTbl,
                    const Standard_Integer                    aTblLen
                   ) {

 SetPattern ( aPattern, aSyntax, aTransTbl, aTblLen );

}  // end constructro ( 2 )

void WOKUnix_RegExp :: Destroy () {

 if ( myBuffer != NULL ) {

  if ( myAlloc )

   delete [] BUFFER -> translate;
 
  if (  BUFFER -> fastmap   != NULL  ) delete [] BUFFER -> fastmap;
  if (  BUFFER -> buffer    != NULL  ) free (  ( void* )( BUFFER -> buffer )  );
  
  delete BUFFER;

  myBuffer = NULL;
 
 }  // end if

}  // end WOKUnix_RegExp :: Destroy

void WOKUnix_RegExp :: SetPattern (
                         const Handle( TCollection_HAsciiString )& aPattern,
                         const WOKUnix_RESyntax aSyntax,
                         const Standard_Address aTransTbl,
                         const Standard_Integer aTblLen
                        ) {

 int     syntax = RE_SYNTAX_AWK;
 _TCHAR* errMsg;

 Destroy ();

 myBuffer = new RE_PATTERN_BUFFER;
 memset (  BUFFER, 0, sizeof ( RE_PATTERN_BUFFER )  );
 BUFFER -> fastmap = new _TCHAR[ ( 1 << BYTEWIDTH ) ];

 if ( aTransTbl != NULL ) {

  if ( aTblLen != 0 ) {

   BUFFER -> translate = new Standard_Character[ aTblLen ];
   memcpy ( BUFFER -> translate, aTransTbl, aTblLen );
   myAlloc = Standard_True;

  } else {
 
   BUFFER -> translate = ( _TCHAR* )aTransTbl;
   myAlloc = Standard_False;

  }  // end else
 
 }  // end if

 switch ( aSyntax ) {
 
  case WOKUnix_RESyntaxAWK:
  
  break;

  case WOKUnix_RESyntaxEGREP:
   syntax = RE_SYNTAX_EGREP;
  break;

  case WOKUnix_RESyntaxGREP:
   syntax = RE_SYNTAX_GREP;
  break;

  case WOKUnix_RESyntaxEMACS:
   syntax = RE_SYNTAX_EMACS;
  break;

  default:
   RAISE( "WOKUnix_RegExp (): incorrect parameter value ( syntax )" );
 
 }  // end switch

 re_set_syntax ( syntax );

 errMsg = re_compile_pattern (
           (char*)aPattern -> ToCString (), aPattern -> Length (), BUFFER
          );

 if ( errMsg != NULL ) {
 
  _tcscpy (  errBuff, _TEXT( "WOKUnix_RegExp (): error parsing specified pattern - " )  );
  _tcscat (  errBuff, errMsg );

  Standard_ProgramError :: Raise ( errBuff );
 
 }  // end if

 re_compile_fastmap ( BUFFER );

}  // end WOKUnix_RegExp :: SetPattern

Standard_Integer WOKUnix_RegExp :: Search (
                                     const Handle( TCollection_HAsciiString )& aString,
                                     const Standard_Integer                    aStartPos
                                    ) const {

 Standard_Integer retVal;
 
 retVal = re_search (
           BUFFER, (char*)aString -> ToCString (), aString -> Length (),
           aStartPos - 1, 0, NULL
          );

 if ( retVal >= 0 ) ++retVal;

 return retVal;

}  // end WOKUnix_RegExp :: Search

Standard_Integer WOKUnix_RegExp :: Match (
                                     const Handle( TCollection_HAsciiString )& aString,
                                     const Standard_Integer aStartPos,
                                     const Standard_Integer aStopPos
                                    ) const {

 Standard_Integer len = aString -> Length ();

 return re_match_2 (
         BUFFER, NULL, 0,
         ( _TUCHAR* )(  aString -> ToCString ()  ), len,
         aStartPos - 1, NULL, ( aStopPos == 1 ? len : aStopPos - 1 )
        );

}  // end WOKUnix_RegExp :: Match

#endif
