#include <WOKBuilder_WNTLibrarian.ixx>

WOKBuilder_WNTLibrarian::WOKBuilder_WNTLibrarian(const Handle(TCollection_HAsciiString)& aName,
						 const WOKUtils_Param&                   aParams)
: WOKBuilder_WNTCollector(aName,aParams)
{
}
                          
Handle(TCollection_HAsciiString) WOKBuilder_WNTLibrarian::EvalCFExt()
{
  return EvalToolParameter("LibraryCFExt");
}
