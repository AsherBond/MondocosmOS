#include <WOKStep_WNTCollect.ixx>

#include <WOKernel_DevUnit.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_Locator.hxx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_BasicUnitTypes.hxx>
#include <WOKernel_Session.hxx>
#include <WOKernel_UnitNesting.hxx>
#include <WOKernel_Workbench.hxx>

#include <WOKMake_InputFile.hxx>
#include <WOKMake_AdmFileTypes.hxx>
#include <WOKMake_HSequenceOfOutputFile.hxx>
#include <WOKMake_OutputFile.hxx>

#include <WOKTools_Messages.hxx>
#include <WOKTools_MapOfHAsciiString.hxx>

#include <WOKUtils_AdmFile.hxx>
#include <WOKUtils_Path.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

//=======================================================================
//function : WOKStep_WNTCollect
//purpose  : 
//=======================================================================
WOKStep_WNTCollect::WOKStep_WNTCollect(const Handle(WOKMake_BuildProcess)& abp,
					const Handle(WOKernel_DevUnit)&         aUnit,
					const Handle(TCollection_HAsciiString)& aCode,
					const Standard_Boolean                    checked,
					const Standard_Boolean                    hidden)
  : WOKMake_Step(abp, aUnit, aCode, checked, hidden)
{
  myInitFlag = Standard_True;
}

//=======================================================================
//function : AdmFileType
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_WNTCollect::AdmFileType()const 
{
  static Handle(TCollection_HAsciiString)retVal = new TCollection_HAsciiString((Standard_CString)STADMFILE);
  return retVal;
}

//=======================================================================
//function : OutputDirTypeName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_WNTCollect::OutputDirTypeName()const 
{
  static Handle(TCollection_HAsciiString)retVal = new TCollection_HAsciiString((Standard_CString)STTMPDIR);
  return retVal;
}

//=======================================================================
//function : ComputeObjectList
//purpose  : 
//=======================================================================
Handle(WOKBuilder_HSequenceOfObjectFile) WOKStep_WNTCollect::ComputeObjectList(const Handle(WOKMake_HSequenceOfInputFile)& anInput)
{
 Handle(WOKBuilder_ObjectFile)obj;
 Handle(WOKBuilder_HSequenceOfObjectFile)retVal = new WOKBuilder_HSequenceOfObjectFile;

 for(int i = 1; i <= anInput->Length(); ++i)
   {
     obj = Handle(WOKBuilder_ObjectFile)::DownCast(anInput->Value(i)->BuilderEntity());
     if( !obj.IsNull())retVal->Append(obj);
   }

 return retVal;
}

//=======================================================================
//function : CompleteExecList
//purpose  : 
//=======================================================================
void WOKStep_WNTCollect::CompleteExecList(const Handle(WOKMake_HSequenceOfInputFile)& anExecList)
{
  
  if(anExecList->Length()                     &&
      myinflow.Extent()> anExecList->Length()&&
      !mydepmatrix.IsNull())
    {
      Standard_Integer           i;
      Standard_Boolean           found = Standard_False;
      WOKTools_MapOfHAsciiString map;
      
      for(i = 1; i <= anExecList->Length(); ++i)
	map.Add(anExecList->Value(i)->ID());
      
      for(i = 1; i <= myinflow.Extent()&& !found; ++i)
	if(!map.Contains( myinflow(i)->ID()))
	  {
	    anExecList->Append( myinflow(i));
	    found = Standard_True;
	  }
    }

  WOKMake_Step::CompleteExecList(anExecList);
}

//=======================================================================
//function : HandleOutputFile
//purpose  : 
//=======================================================================
//Standard_Boolean WOKStep_WNTCollect::HandleOutputFile(const Handle(WOKMake_OutputFile)& aFile)
Standard_Boolean WOKStep_WNTCollect::HandleOutputFile(const Handle(WOKMake_OutputFile)& )
{
  return Standard_False;
}
