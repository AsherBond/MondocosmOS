// File:	WOKBuilder_MSExtractor.cxx
// Created:	Wed Aug 23 20:09:45 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <WOKBuilder_MSExtractor.ixx>

#include <WOKBuilder_Compilable.hxx>
#include <WOKBuilder_Include.hxx>
#include <WOKBuilder_Miscellaneous.hxx>
#include <WOKBuilder_HSequenceOfEntity.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <TColStd_HSequenceOfAsciiString.hxx>

#include <OSD_SharedLibrary.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKTools_Messages.hxx>
#include <WOKUtils_SearchList.hxx>

//=======================================================================
//function : WOKBuilder_MSExtractor
//purpose  : 
//=======================================================================
 WOKBuilder_MSExtractor::WOKBuilder_MSExtractor(const Handle(TCollection_HAsciiString)& aname,
						const Handle(TCollection_HAsciiString)& ashared,
						const Handle(TColStd_HSequenceOfHAsciiString)& searchlist)
   : WOKBuilder_MSTool(aname, WOKUtils_Param())
{
  myprefix       = aname;
  myshared       = ashared;
  

  if(searchlist.IsNull() == Standard_False)
    {
      Standard_Integer i;

      mysearchlist = new WOKUtils_SearchList;
      for(i=1; i<=searchlist->Length(); i++)
	{
	  mysearchlist->AddNonPriorPath(new WOKUtils_Path(searchlist->Value(i)));
	}
    }

  mytemplfunc    = NULL;
  myextractfunc  = NULL;
}

//=======================================================================
//function : WOKBuilder_MSExtractor
//purpose  : 
//=======================================================================
WOKBuilder_MSExtractor::WOKBuilder_MSExtractor(const Handle(TCollection_HAsciiString)& aname,
					       const WOKUtils_Param& params)
  : WOKBuilder_MSTool(aname, params)
{
  SetParams(params);
  mytemplfunc    = NULL;
  myextractfunc  = NULL;  
}

//=======================================================================
//function : Load
//purpose  : 
//=======================================================================
void WOKBuilder_MSExtractor::Load()
{
  Handle(TCollection_HAsciiString) astr;
  
  if(myshared.IsNull())
    {
      myshared = EvalToolParameter("Shared");

      if(myshared.IsNull() == Standard_True)
	{
	  ErrorMsg() << "WOKBuilder_MSExtractor::Load" 
		   << "Parameter " << astr << " could not be evaluated" << endm;
	  return;
	}
    }

  
  Handle(WOKUtils_Path) libpath = new WOKUtils_Path(myshared);

  if(!libpath->Exists())
    {
      libpath = Params().SearchFile(myshared);
      if(libpath.IsNull())
	{
	  ErrorMsg() << "WOKBuilder_MSExtractor::Load" 
		   << "WOKBuilder_MSExtractor::Load Could not find file : " << myshared << endm;
	}
    }

  if(myprefix.IsNull())
    {
      myprefix = EvalToolParameter("Name");
      
      if(myprefix.IsNull())
	{
	  ErrorMsg() << "WOKBuilder_MSExtractor::Load" << "Parameter " << astr << " could not be evaluated" << endm;
	  return;
	}
    }

  OSD_SharedLibrary ashared(libpath->Name()->ToCString());

  if(ashared.DlOpen(OSD_RTLD_NOW) == Standard_False)
    {
      ErrorMsg() << "WOKBuilder_MSExtractor::Load" << ashared.DlError() << endm;
      return;
    }

  astr = new TCollection_HAsciiString(myprefix);
  astr->AssignCat("_TemplatesUsed");
  
  mytemplfunc = (WOKBuilder_MSExtractorTemplatesPtr) ashared.DlSymb(astr->ToCString());
  
  if( mytemplfunc == NULL) 
    {
      ErrorMsg() << "WOKBuilder_MSExtractor::Load" << ashared.DlError() << endm;
      return;
    }
  
  astr = new TCollection_HAsciiString(myprefix);
  astr->AssignCat("_Extract");

  myextractfunc = (WOKBuilder_MSExtractorExtractPtr) ashared.DlSymb(astr->ToCString());

  if(myextractfunc == NULL) 
    {
      ErrorMsg() << "WOKBuilder_MSExtractor::Load" << ashared.DlError() << endm;
      return;
    }

  if(mytemplates.IsNull())
    {
      Standard_Integer i;
      Handle(TColStd_HSequenceOfAsciiString) aseq = Params().SearchDirectories();
      mytemplates = new TColStd_HSequenceOfHAsciiString;

      for(i=1; i<=aseq->Length(); i++)
	{
	  mytemplates->Append(new TCollection_HAsciiString(aseq->Value(i)));
	}
    }

  astr = new TCollection_HAsciiString(myprefix);
  astr->AssignCat("_Init");

  myinitfunc = (Standard_Address) ashared.DlSymb(astr->ToCString());

  return;
}

//=======================================================================
//function : SetEntity
//purpose  : 
//=======================================================================
void WOKBuilder_MSExtractor::SetEntity(const Handle(WOKBuilder_MSEntity)& anentity)
{
  myentity = anentity;
}

//=======================================================================
//function : Entity
//purpose  : 
//=======================================================================
Handle(WOKBuilder_MSEntity) WOKBuilder_MSExtractor::Entity() const 
{
  return myentity;
}

//=======================================================================
//function : SetTemplateFiles
//purpose  : 
//=======================================================================
void WOKBuilder_MSExtractor::SetTemplateFiles(const Handle(TColStd_HSequenceOfHAsciiString)& templates)
{
  mytemplates = templates;
}

//=======================================================================
//function : TemplateFiles
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKBuilder_MSExtractor::TemplateFiles() const 
{
  return mytemplates;
}


//=======================================================================
//function : ExtractionStatus
//purpose  : 
//=======================================================================
WOKBuilder_MSActionStatus WOKBuilder_MSExtractor::ExtractionStatus(const Handle(WOKBuilder_MSAction)& )
{
  return WOKBuilder_OutOfDate;
}

//=======================================================================
//function : Extract
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_MSExtractor::Extract(const Handle(WOKBuilder_MSchema)&              ametaschema, 
						       const Handle(WOKBuilder_MSEntity)&             anentity)
{
  Handle(TColStd_HSequenceOfHAsciiString) aseq = new TColStd_HSequenceOfHAsciiString;
  Handle(TCollection_HAsciiString)        adbms;
  Handle(WOKBuilder_HSequenceOfEntity) entityseq = new WOKBuilder_HSequenceOfEntity;
  Handle(WOKUtils_Path) apath;
  Handle(WOKBuilder_Entity) anent;

  adbms = Params().Eval("%DBMS");

  if(adbms.IsNull())
    {
      WarningMsg() << "WOKBuilder_MSExtractor::Extract" 
		 << "No DBMS profile specified : using DFLT" << endm;
      adbms = new TCollection_HAsciiString("DFLT");
    }
  
  // verrue en attendant CLE
  if(!strcmp(adbms->ToCString(), "DFLT"))
    {
      adbms = new TCollection_HAsciiString("CSFDB");
    }
  
  (*myextractfunc)(ametaschema->MetaSchema(), anentity->Name(), mytemplates, OutputDir()->Name(), aseq, adbms->ToCString());

  for(Standard_Integer i=1; i<=aseq->Length(); i++)
    {
      apath = new WOKUtils_Path(aseq->Value(i));
      switch(apath->Extension())
	{
	case WOKUtils_CXXFile:
	  anent = new WOKBuilder_Compilable(apath);
	  break;
	case WOKUtils_HXXFile:
	case WOKUtils_IXXFile:
	case WOKUtils_JXXFile:
	case WOKUtils_LXXFile:
	case WOKUtils_GXXFile:
	  anent = new WOKBuilder_Include(apath);
	  break;
	case WOKUtils_TemplateFile:
	  anent = new WOKBuilder_Miscellaneous(apath);
	  break;
	default:
	  anent = new WOKBuilder_Miscellaneous(apath);
	  break;
	}
      entityseq->Append(anent);
    }

  SetProduction(entityseq);

  return WOKBuilder_Success;
}

//=======================================================================
//function : Extract
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_MSExtractor::Extract(const Handle(WOKBuilder_MSchema)&              ametaschema, 
						       const Handle(WOKBuilder_MSEntity)&             anentity,
						       const Standard_CString amode)
{
  Handle(TColStd_HSequenceOfHAsciiString) aseq = new TColStd_HSequenceOfHAsciiString;
  Handle(WOKBuilder_HSequenceOfEntity) entityseq = new WOKBuilder_HSequenceOfEntity;
  Handle(WOKUtils_Path) apath;
  Handle(WOKBuilder_Entity) anent;

  
  (*myextractfunc)(ametaschema->MetaSchema(), anentity->Name(), mytemplates, OutputDir()->Name(), aseq, amode);

  for(Standard_Integer i=1; i<=aseq->Length(); i++)
    {
      apath = new WOKUtils_Path(aseq->Value(i));
      switch(apath->Extension())
	{
	case WOKUtils_CXXFile:
	  anent = new WOKBuilder_Compilable(apath);
	  break;
	case WOKUtils_HXXFile:
	case WOKUtils_IXXFile:
	case WOKUtils_JXXFile:
	case WOKUtils_LXXFile:
	case WOKUtils_GXXFile:
	  anent = new WOKBuilder_Include(apath);
	  break;
	case WOKUtils_TemplateFile:
	  anent = new WOKBuilder_Miscellaneous(apath);
	  break;
	default:
	  anent = new WOKBuilder_Miscellaneous(apath);
	  break;
	}
      entityseq->Append(anent);
    }

  SetProduction(entityseq);

  return WOKBuilder_Success;
}

//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_MSExtractor::Execute()
{
  return WOKBuilder_Success;
}

