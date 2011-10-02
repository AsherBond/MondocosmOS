#include <WOKStep_ImportLibrary.ixx>

#include <WOKBuilder_ImportLibrarian.hxx>

WOKStep_ImportLibrary :: WOKStep_ImportLibrary (
                          const Handle(WOKMake_BuildProcess)& abp,
			  const Handle( WOKernel_DevUnit )&         aUnit,
                          const Handle( TCollection_HAsciiString )& aCode,
                          const Standard_Boolean                    checked,
                          const Standard_Boolean                    hidden
                         ) : WOKStep_WNTLibrary ( abp, aUnit, aCode, checked, hidden ) {
}  // end constructor

Handle( WOKBuilder_WNTCollector ) WOKStep_ImportLibrary :: ComputeTool () {

 return new WOKBuilder_ImportLibrarian (
             new TCollection_HAsciiString ( "LIB" ), Unit () -> Params ()
            );

}  // end WOKStep_ImportLibrary :: ComputeTool
