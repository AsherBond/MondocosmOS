// File:	WOKOBJS_SchExtract.cxx
// Created:	Mon Feb 24 16:49:03 1997
// Author:	Jean GAUTIER
//		<jga@hourax.paris1.matra-dtv.fr>


#include <WOKBuilder_MSTool.hxx>

#include <WOKOBJS_MSSchExtractor.hxx>

#include <WOKOBJS_SchExtract.ixx>

//=======================================================================
//Author   : Jean Gautier (jga)
//function : WOKOBJS_SchExtract
//purpose  : 
//=======================================================================
WOKOBJS_SchExtract::WOKOBJS_SchExtract(const Handle(WOKMake_BuildProcess)& abp, const Handle(WOKernel_DevUnit)& aunit,const Handle(TCollection_HAsciiString)& acode,
				       const Standard_Boolean checked,const Standard_Boolean hidden)
: WOKStep_Extract(abp, aunit, acode, checked,hidden)
{
  Handle(WOKOBJS_MSSchExtractor) anextractor = new WOKOBJS_MSSchExtractor(Unit()->Params());

  anextractor->SetMSchema(WOKBuilder_MSTool::GetMSchema());
  SetExtractor(anextractor);
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : OutOfDateEntities
//purpose  : 
//=======================================================================
Handle(WOKMake_HSequenceOfInputFile) WOKOBJS_SchExtract::OutOfDateEntities() 
{
  return ForceBuild();
}

