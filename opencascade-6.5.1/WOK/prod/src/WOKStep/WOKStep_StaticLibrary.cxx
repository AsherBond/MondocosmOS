#include <WOKStep_StaticLibrary.ixx>

#include <WOKBuilder_StaticLibrarian.hxx>

WOKStep_StaticLibrary :: WOKStep_StaticLibrary (
                          const Handle(WOKMake_BuildProcess)& abp,
			  const Handle( WOKernel_DevUnit )&         aUnit,
                          const Handle( TCollection_HAsciiString )& aCode,
                          const Standard_Boolean                    checked,
                          const Standard_Boolean                    hidden
                         ) : WOKStep_WNTLibrary ( abp, aUnit, aCode, checked, hidden ) {
}  // end constructor

Handle( WOKBuilder_WNTCollector ) WOKStep_StaticLibrary :: ComputeTool () {

 return new WOKBuilder_StaticLibrarian (
             new TCollection_HAsciiString ( "LIB" ), Unit () -> Params ()
            );

}  // end WOKStep_StaticLibrary :: ComputeTool
