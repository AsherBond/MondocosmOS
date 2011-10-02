#include <WOKBuilder_ManifestLibrary.ixx>

WOKBuilder_ManifestLibrary :: WOKBuilder_ManifestLibrary (
                             const Handle( WOKUtils_Path )& apath
                            ) : WOKBuilder_Library ( apath ) {
}  // end constructor ( 1 )

WOKBuilder_ManifestLibrary :: WOKBuilder_ManifestLibrary (
                             const Handle( TCollection_HAsciiString )& aname, 
						     const Handle( WOKUtils_Path )&            adir, 
						     const WOKBuilder_LibReferenceType         areftype
                            ) : WOKBuilder_Library ( aname, adir, areftype ) {
}  // end constructor ( 2 )

Handle( TCollection_HAsciiString ) WOKBuilder_ManifestLibrary :: GetLibFileName (
                                                                const WOKUtils_Param& params
                                                               ) {

 params.Set (  "%LIB_ManifestName", Name () -> ToCString ()  );

 return params.Eval ( "LIB_ManifestFileName", Standard_True );

}  // end WOKBuilder_ManifestLibrary :: GetLibFileName
