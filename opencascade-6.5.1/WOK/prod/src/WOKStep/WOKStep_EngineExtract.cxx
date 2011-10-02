// File:	WOKStep_EngineExtract.cxx
// Created:	Wed Jan  3 18:23:04 1996
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <WOKTools_Messages.hxx>

#include <WOKBuilder_MSEngineExtractor.hxx>
#include <WOKBuilder_MSEntity.hxx>

#include <WOKernel_File.hxx>

#include <WOKMake_InputFile.hxx>

#include <WOKStep_EngineExtract.ixx>


//=======================================================================
//function : WOKStep_HeaderExtract
//purpose  : 
//=======================================================================
WOKStep_EngineExtract::WOKStep_EngineExtract(const Handle(WOKMake_BuildProcess)& abp,
					     const Handle(WOKernel_DevUnit)& aunit, 
					     const Handle(TCollection_HAsciiString)& acode, 
					     const Standard_Boolean checked, 
					     const Standard_Boolean hidden)
: WOKStep_Extract(abp,aunit, acode, checked, hidden)
{
  SetExtractor(new WOKBuilder_MSEngineExtractor(Unit()->Params()));
}



//=======================================================================
//function : HandleInputFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKStep_EngineExtract::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
{
  if(!infile->IsPhysic())
    {
      if(!strcmp("msentity", infile->ID()->Token(":",2)->ToCString()) && 
	 !strcmp(Unit()->Name()->ToCString(), infile->ID()->Token(":",3)->ToCString()))
	{
	  infile->SetDirectFlag(Standard_True);
	  infile->SetBuilderEntity(new WOKBuilder_MSEntity(infile->ID()->Token(":",3)));
	  return Standard_True;
	}
    }
  return Standard_False;
}

//=======================================================================
//function : OutOfDateEntities
//purpose  : 
//=======================================================================
Handle(WOKMake_HSequenceOfInputFile) WOKStep_EngineExtract::OutOfDateEntities()
{
  return ForceBuild();
}


