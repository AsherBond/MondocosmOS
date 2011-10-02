#include <WOKBuilder_StaticLibrary.ixx>

WOKBuilder_StaticLibrary :: WOKBuilder_StaticLibrary (
                             const Handle( WOKUtils_Path )& apath
                            ) : WOKBuilder_Library ( apath ) {
}  // end constructor ( 1 )

WOKBuilder_StaticLibrary :: WOKBuilder_StaticLibrary (
                             const Handle( TCollection_HAsciiString )& aname, 
						     const Handle( WOKUtils_Path )&            adir, 
						     const WOKBuilder_LibReferenceType         areftype
                            ) : WOKBuilder_Library ( aname, adir, areftype ) {
}  // end constructor ( 2 )

Handle( TCollection_HAsciiString ) WOKBuilder_StaticLibrary :: GetLibFileName (
                                                                const WOKUtils_Param& params
                                                               ) {

 params.Set (  "%LIB_LibName", Name () -> ToCString ()  );

 return params.Eval ( "LIB_FileName", Standard_True );

}  // end WOKBuilder_StaticLibrary
