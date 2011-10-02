// File:	WOKBuilder_MSExtractorIterator.cxx
// Created:	Wed Oct 11 16:09:50 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <WOKTools_Messages.hxx>

#include <WOKBuilder_MSExtractorIterator.ixx>

//=======================================================================
//function : WOKBuilder_MSExtractorIterator
//purpose  : 
//=======================================================================
 WOKBuilder_MSExtractorIterator::WOKBuilder_MSExtractorIterator(const Handle(WOKBuilder_MSchema)& ams, 
								const Handle(WOKBuilder_MSExtractor)& anext)
{
  mymeta = ams;
  myextractor = anext;
  myextractor->SetMSchema(ams);
}

//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_MSExtractorIterator::Execute(const Handle(WOKBuilder_MSEntity)& anentity)
{
  WOKBuilder_BuildStatus status;

  myproduction.Nullify();

  myextractor->SetEntity(anentity);

  InfoMsg() << "WOKBuilder_MSExtractorIterator::Execute" << "Extracting " << anentity->Name() << endm;

  status = myextractor->Extract(mymeta, anentity);
  myproduction = myextractor->Produces();

  return status;
}

//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_MSExtractorIterator::Execute(const Handle(WOKBuilder_MSEntity)& anentity, 
							       const Standard_CString amode)
{
  WOKBuilder_BuildStatus status;

  myproduction.Nullify();

  myextractor->SetEntity(anentity);

  InfoMsg() << "WOKBuilder_MSExtractorIterator::Execute" << "Extracting " << anentity->Name() << endm;

  status = myextractor->Extract(mymeta, anentity, amode);
  myproduction = myextractor->Produces();

  return status;
}

//=======================================================================
//function : Produces
//purpose  : 
//=======================================================================
Handle(WOKBuilder_HSequenceOfEntity) WOKBuilder_MSExtractorIterator::Produces() const 
{
  return myproduction;
}


