#include <WOKBuilder_StaticLibrarian.ixx>

#include <WOKBuilder_Entity.hxx>
#include <WOKBuilder_StaticLibrary.hxx>
#include <WOKBuilder_HSequenceOfEntity.hxx>

#include <WOKUtils_Path.hxx>

WOKBuilder_StaticLibrarian :: WOKBuilder_StaticLibrarian (
                               const Handle( TCollection_HAsciiString )& aName,
                               const WOKUtils_Param&                     aParams
                              ) : WOKBuilder_WNTLibrarian ( aName, aParams ) {
}  // end constructor
  
Handle( TCollection_HAsciiString ) WOKBuilder_StaticLibrarian :: EvalHeader () {

 return EvalToolTemplate ( "LibraryHeaderSTATIC" );

}  // end WOKBuilder_StaticLibrarian :: EvalHeader
  
Handle( TCollection_HAsciiString ) WOKBuilder_StaticLibrarian :: EvalFooter () {

 Handle( WOKBuilder_Entity )        outEnt;
 Handle( TCollection_HAsciiString ) tmp;
 Handle( TCollection_HAsciiString ) retVal = EvalToolParameter ( "LibraryOutput" );
 
 tmp = EvalToolTemplate ( "LibrarySTATLIB" );

 outEnt = new WOKBuilder_StaticLibrary (  new WOKUtils_Path ( tmp )  );

 retVal -> AssignCat ( tmp );

 SetProduction (  new WOKBuilder_HSequenceOfEntity ()  );

 Produces () -> Append ( outEnt );

 return retVal;

}  // end WOKBuilder_StaticLibrarian :: EvalFooter
