#include <WOKBuilder_ImportLibrary.ixx>

WOKBuilder_ImportLibrary :: WOKBuilder_ImportLibrary (
                             const Handle( WOKUtils_Path )& apath
                            ) : WOKBuilder_Library ( apath ) {
}  // end constructor ( 1 )

WOKBuilder_ImportLibrary :: WOKBuilder_ImportLibrary (
                             const Handle( TCollection_HAsciiString )& aname, 
						     const Handle( WOKUtils_Path )&            adir, 
						     const WOKBuilder_LibReferenceType         areftype
                            ) : WOKBuilder_Library ( aname, adir, areftype ) {
}  // end constructor ( 2 )

Handle( TCollection_HAsciiString ) WOKBuilder_ImportLibrary :: GetLibFileName (
                                                                const WOKUtils_Param& params
                                                               ) {

 params.Set (  "%LIB_ImplibName", Name () -> ToCString ()  );

 return params.Eval ( "LIB_ImplibFileName", Standard_True );

}  // end WOKBuilder_ImportLibrary :: GetLibFileName

Handle( TCollection_HAsciiString ) WOKBuilder_ImportLibrary :: GetLibFileName (
                                                                const WOKUtils_Param&                     params,
									                            const Handle( TCollection_HAsciiString )& aname
                                                               ) {

 params.Set (  "%LIB_ImplibName", aname -> ToCString ()  );

 return params.Eval ( "LIB_ImplibFileName", Standard_True );

}  // end WOKBuilder_ImportLibrary :: GetLibFileName
