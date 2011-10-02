#include <WOKBuilder_ImportLibrarian.ixx>

#include <WOKBuilder_Entity.hxx>
#include <WOKBuilder_ImportLibrary.hxx>
#include <WOKBuilder_SharedLibrary.hxx>
#include <WOKBuilder_HSequenceOfEntity.hxx>

#include <WOKUtils_Path.hxx>

WOKBuilder_ImportLibrarian :: WOKBuilder_ImportLibrarian (
                               const Handle( TCollection_HAsciiString )& aName,
                               const WOKUtils_Param&                     aParams
                              ) : WOKBuilder_WNTLibrarian ( aName, aParams ) {
}  // end constructor

Handle( TCollection_HAsciiString ) WOKBuilder_ImportLibrarian :: EvalHeader () {

 return EvalToolTemplate ( "LibraryHeaderIMPORT" );

}  // end WOKBuilder_ImportLibrarian :: EvalHeader

Handle( TCollection_HAsciiString ) WOKBuilder_ImportLibrarian :: EvalFooter () {

 Handle( WOKBuilder_Entity )        outEnt[ 2 ];
 Handle( TCollection_HAsciiString ) tmp;
 Handle( TCollection_HAsciiString ) retVal = EvalToolParameter ( "LibraryOutput" );
 
 tmp = EvalToolTemplate ( "LibraryIMPLIB" );

 outEnt[ 0 ] = new WOKBuilder_ImportLibrary (  new WOKUtils_Path ( tmp )  );

 retVal -> AssignCat ( tmp );

 outEnt[ 1 ] = new WOKBuilder_SharedLibrary (
                    new WOKUtils_Path (  EvalToolTemplate ( "LibraryEXP" )  )
                   );

 SetProduction (  new WOKBuilder_HSequenceOfEntity ()  );

 Produces () -> Append ( outEnt[ 0 ] );
 Produces () -> Append ( outEnt[ 1 ] );

 return retVal;

}  // end WOKBuilder_ImportLibrarian :: EvalFooter
