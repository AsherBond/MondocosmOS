#include <WOKBuilder_ExportLibrary.ixx>

WOKBuilder_ExportLibrary :: WOKBuilder_ExportLibrary (
                             const Handle( WOKUtils_Path )& apath
                            ) : WOKBuilder_Library ( apath ) {
}  // end constructor ( 1 )

WOKBuilder_ExportLibrary :: WOKBuilder_ExportLibrary (
                             const Handle( TCollection_HAsciiString )& aname, 
						     const Handle( WOKUtils_Path )&            adir, 
						     const WOKBuilder_LibReferenceType         areftype
                            ) : WOKBuilder_Library ( aname, adir, areftype ) {
}  // end constructor ( 2 )

Handle( TCollection_HAsciiString ) WOKBuilder_ExportLibrary :: GetLibFileName (
                                                                const WOKUtils_Param& params
                                                               ) {

 params.Set (  "%LIB_ExplibName", Name () -> ToCString ()  );

 return params.Eval ( "LIB_ExplibFileName", Standard_True );

}  // end WOKBuilder_ExportLibrary :: GetLibFileName
