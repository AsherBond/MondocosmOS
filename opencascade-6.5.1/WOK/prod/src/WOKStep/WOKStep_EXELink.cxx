


#include <WOKStep_EXELink.ixx>

#include <WOKBuilder_EXELinker.hxx>
#include <WOKBuilder_MSTool.hxx>
#include <WOKBuilder_MSchema.hxx>
#include <WOKBuilder_Entity.hxx>
#include <WOKBuilder_Library.hxx>
#include <WOKBuilder_ImportLibrary.hxx>

#include <WOKernel_Locator.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_BasicUnitTypes.hxx>
#include <WOKernel_UnitGraph.hxx>

#include <WOKMake_InputFile.hxx>

#include <WOKTools_Messages.hxx>
#include <WOKTools_MapOfHAsciiString.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKUtils_AdmFile.hxx>

#ifndef WNT
# define FASTCALL 
#else
# define FASTCALL __fastcall
#endif // WNT

Handle(WOKBuilder_Library) FASTCALL  _get_unit_library(Handle(WOKernel_DevUnit)&,
						      Handle(TCollection_HAsciiString)&,
						      Handle(TCollection_HAsciiString)&,
						      const WOKUtils_Param&,
						      Standard_Boolean = Standard_False);

//=======================================================================
//function : WOKStep_EXELink
//purpose  : 
//=======================================================================
WOKStep_EXELink::WOKStep_EXELink(const Handle(WOKMake_BuildProcess)&     abp,
				 const Handle(WOKernel_DevUnit)&         aUnit,
				 const Handle(TCollection_HAsciiString)& aCode,
				 const Standard_Boolean                  checked,
				 const Standard_Boolean                  hidden)
  : WOKStep_WNTLink(abp, aUnit, aCode, checked, hidden)
{
}
  
//=======================================================================
//function : ComputeTool
//purpose  : 
//=======================================================================
Handle(WOKBuilder_WNTCollector) WOKStep_EXELink::ComputeTool()
{
  Handle(WOKBuilder_EXELinker) retVal = new WOKBuilder_EXELinker(new TCollection_HAsciiString("LINK"), Unit()->Params());
  return retVal;
}

//=======================================================================
//function : ComputeObjectList
//purpose  : 
//=======================================================================
Handle(WOKBuilder_HSequenceOfObjectFile) WOKStep_EXELink::ComputeObjectList(const Handle(WOKMake_HSequenceOfInputFile)& anInput)
{
  Handle(WOKBuilder_ObjectFile)            anObj;
  Handle(WOKBuilder_HSequenceOfObjectFile) retVal = new WOKBuilder_HSequenceOfObjectFile;
  
  for(int i = 1; i <= anInput->Length(); ++i)
    {
      anObj  = Handle(WOKBuilder_ObjectFile)::DownCast(anInput->Value(i)->BuilderEntity());
      if(!anObj.IsNull())retVal->Append(anObj);
    }  

  return retVal;
}

//=======================================================================
//function : ComputeLibraryList
//purpose  : 
//=======================================================================
Handle(WOKBuilder_HSequenceOfLibrary) WOKStep_EXELink::ComputeLibraryList(const Handle(WOKMake_HSequenceOfInputFile)& anInput)
{
  Handle(WOKBuilder_Library)            aLib;
  Handle(WOKBuilder_HSequenceOfLibrary) retVal = new WOKBuilder_HSequenceOfLibrary;

  for(int i = 1; i <= anInput->Length(); ++i)
    {
      aLib  = Handle(WOKBuilder_Library):: DownCast(anInput->Value(i)-> BuilderEntity());
      if(!aLib.IsNull())retVal->Append(aLib);
    }
  return retVal;
}
