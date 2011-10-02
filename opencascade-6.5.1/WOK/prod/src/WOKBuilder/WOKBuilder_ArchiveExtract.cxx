// File:	WOKBuilder_ArchiveExtract.cxx
// Created:	Tue Aug  6 11:09:31 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>


#include <TCollection_HAsciiString.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKUtils_Shell.hxx>
#include <WOKUtils_Path.hxx>
#include <WOKUtils_AdmFile.hxx>

#include <EDL_API.hxx>

#include <WOKBuilder_ArchiveLibrary.hxx>
#include <WOKBuilder_HSequenceOfEntity.hxx>
#include <WOKBuilder_ObjectFile.hxx>
#include <WOKBuilder_HSequenceOfObjectFile.hxx>

#include <WOKBuilder_ArchiveExtract.ixx>

#include <stdio.h>

//=======================================================================
//function : WOKBuilder_ArchiveExtract
//purpose  : 
//=======================================================================
WOKBuilder_ArchiveExtract::WOKBuilder_ArchiveExtract(const WOKUtils_Param& params) 
  : WOKBuilder_ToolInShell(new TCollection_HAsciiString("ARX"), params)
{
}

//=======================================================================
//function : Load
//purpose  : 
//=======================================================================
void WOKBuilder_ArchiveExtract::Load()
{
}

//=======================================================================
//function : SetTargetName
//purpose  : 
//=======================================================================
void WOKBuilder_ArchiveExtract::SetArchive(const Handle(WOKBuilder_ArchiveLibrary)& anarchive)
{
  mylib = anarchive;
}

//=======================================================================
//function : Archive
//purpose  : 
//=======================================================================
Handle(WOKBuilder_ArchiveLibrary) WOKBuilder_ArchiveExtract::Archive() const 
{
  return mylib;
}

//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKBuilder_ArchiveExtract::Execute()
{
  Handle(TCollection_HAsciiString)       objlist = new TCollection_HAsciiString;
  Handle(TCollection_HAsciiString)       objtempl, astr, templ;
  Handle(WOKBuilder_HSequenceOfEntity)    result = new WOKBuilder_HSequenceOfEntity;
  Handle(WOKBuilder_ArchiveLibrary)        anent;
  Handle(WOKUtils_Path)                     tmppath;
  Standard_Integer                             i;
  
  if(Shell()->IsLaunched() == Standard_False) Shell()->Launch();

  if(!IsLoaded()) Load();

  templ = EvalToolParameter("Template");
  
  if(templ.IsNull()) return WOKBuilder_Failed;

  SetTemplate(templ);
  
  tmppath = new WOKUtils_Path(new TCollection_HAsciiString (tmpnam(NULL)) );

  Params().Set("%Archive",   Archive()->Path()->Name()->ToCString());
  Params().Set("%TmpFile",   tmppath->Name()->ToCString());
  Params().Set("%OutputDir", OutputDir()->Name()->ToCString());

  astr = Params().Eval(Template()->ToCString());

  WOK_TRACE {
    VerboseMsg()("WOK_ARX") << "WOKBuilder_ArchiveExtract::Execute" 
			  << "Archive line : " << astr << endm;
  }

  Shell()->Execute(astr);

  if(Shell()->Status())
    {
      ErrorMsg() << "WOKBuilder_ArchiveExtract::Execute" << "Errors occured in Shell" << endm;
      Handle(TColStd_HSequenceOfHAsciiString) aseq = Shell()->Errors();
      
      for(Standard_Integer i=1; i<= aseq->Length(); i++)
	{
	  ErrorMsg() << "WOKBuilder_ArchiveExtract::Execute" << aseq->Value(i) << endm;
	}

      return WOKBuilder_Failed;
    }

  WOKUtils_AdmFile afile(  new WOKUtils_Path ( Params().Eval("%TmpFile") )  );
  Handle(TColStd_HSequenceOfHAsciiString) aseq;
  Standard_Boolean failed = Standard_False;

  aseq = afile.Read();
  
  for(i=1; i<=aseq->Length(); i++)
    {

      Handle(WOKUtils_Path) apath = new WOKUtils_Path(OutputDir()->Name(), aseq->Value(i));
      
      if(apath->Exists())
	{
	  Handle(WOKBuilder_ObjectFile) object = new WOKBuilder_ObjectFile(apath);
	  result->Append(object);
	}
      else
	{
	  ErrorMsg() << "WOKBuilder_ArchiveExtract::Execute"
		   << "Object " << aseq->Value(i) << " listed in archive was not extracted" << endm;
	  failed = Standard_True;
	}
      
    }
  
  tmppath->RemoveFile();

  if(failed)
    {
      ErrorMsg() << "WOKBuilder_ArchiveExtract::Execute"
	       << "Object(s) not found" << endm;
      return WOKBuilder_Failed;
    }


  Shell()->ClearOutput();

  SetProduction(result);

  return WOKBuilder_Success;
}

