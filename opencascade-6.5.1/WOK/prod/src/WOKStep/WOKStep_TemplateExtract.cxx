// File:	WOKStep_TemplateExtract.cxx
// Created:	Mon Nov 13 22:08:50 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <WOKBuilder_MSTemplateExtractor.hxx>

#include <WOKMake_AdmFileTypes.hxx>

#include <WOKStep_TemplateExtract.ixx>


//=======================================================================
//Author   : Jean Gautier (jga)
//function : WOKStep_TemplateExtract
//purpose  : 
//=======================================================================
WOKStep_TemplateExtract::WOKStep_TemplateExtract(const Handle(WOKMake_BuildProcess)& abp,
						 const Handle(WOKernel_DevUnit)& aunit, 
						 const Handle(TCollection_HAsciiString)& acode, 
						 const Standard_Boolean checked,
						 const Standard_Boolean hidden) 
: WOKStep_Extract(abp,aunit, acode, checked, hidden)
{
  SetExtractor(new WOKBuilder_MSTemplateExtractor(Unit()->Params()));
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AdmFileType
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_TemplateExtract::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)ADMFILE);
  return result;   
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : OutputDirTypeName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_TemplateExtract::OutputDirTypeName() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)TMPDIR);
  return result;   
}
