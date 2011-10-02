// File:	WOKStep_ServerExtract.cxx
// Created:	Wed Jan  3 18:00:58 1996
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <WOKTools_Messages.hxx>

#include <WOKBuilder_MSServerExtractor.hxx>
#include <WOKBuilder_MSEntity.hxx>

#include <WOKMake_InputFile.hxx>
#include <WOKMake_OutputFile.hxx>

#include <WOKStep_ServerExtract.ixx>

//=======================================================================
//function : WOKStep_ServerExtract
//purpose  : 
//=======================================================================
WOKStep_ServerExtract::WOKStep_ServerExtract(const Handle(WOKMake_BuildProcess)& abp,
					     const Handle(WOKernel_DevUnit)& aunit, 
					     const Handle(TCollection_HAsciiString)& acode, 
					     const Standard_Boolean checked, 
					     const Standard_Boolean hidden)
: WOKStep_Extract(abp,aunit, acode, checked, hidden)
{
  Handle(WOKBuilder_MSServerExtractor) anextractor = new WOKBuilder_MSServerExtractor(Unit()->Params());

  anextractor->SetMSchema(WOKBuilder_MSTool::GetMSchema());
  SetExtractor(anextractor);
}

//=======================================================================
//function : OutOfDateEntities
//purpose  : 
//=======================================================================
Handle(WOKMake_HSequenceOfInputFile) WOKStep_ServerExtract::OutOfDateEntities()
{
  return ForceBuild();
}


