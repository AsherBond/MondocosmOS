// File:	WOKOBJS_OSSG.cxx
// Created:	Mon Feb 24 15:53:10 1997
// Author:	Jean GAUTIER
//		<jga@hourax.paris1.matra-dtv.fr>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKUtils_Shell.hxx>

#include <WOKBuilder_HSequenceOfEntity.hxx>

#include <WOKOBJS_AppSchema.hxx>
#include <WOKOBJS_AppSchCxxFile.hxx>

#include <WOKOBJS_OSSG.ixx>

#define WOK_VERBOSE 1

//=======================================================================
//Author   : Jean Gautier (jga)
//function : WOKOBJS_OSSG
//purpose  : 
//=======================================================================
WOKOBJS_OSSG::WOKOBJS_OSSG(const Handle(TCollection_HAsciiString)& aname,const WOKUtils_Param& params)
: WOKBuilder_ToolInShell(aname, params)
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : IncludeDirectories
//purpose  : 
//=======================================================================
Handle(WOKUtils_HSequenceOfPath) WOKOBJS_OSSG::IncludeDirectories() const
{
  return myincdirs;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetIncludeDirectories
//purpose  : 
//=======================================================================
void WOKOBJS_OSSG::SetIncludeDirectories(const Handle(WOKUtils_HSequenceOfPath)& incdirs) 
{
  Handle(TCollection_HAsciiString) afile;
  Handle(TCollection_HAsciiString) astr = new TCollection_HAsciiString;

  Load();

  myincdirs = incdirs;

  for(Standard_Integer i=1; i<= IncludeDirectories()->Length(); i++)
    {
      Params().Set("%OBJS_IncDir", myincdirs->Value(i)->Name()->ToCString());

      astr->AssignCat(Params().Eval("OBJS_OSSG_IncDirective"));
    }
  Params().Set("%OBJS_IncDirectives", astr->ToCString());

  return;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetTargetDir
//purpose  : 
//=======================================================================
void WOKOBJS_OSSG::SetTargetDir(const Handle(WOKUtils_Path)& adir) 
{
  Params().Set("%OBJS_TargetDir", adir->Name()->ToCString());
  mytarget = adir;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : TargetDir
//purpose  : 
//=======================================================================
Handle(WOKUtils_Path) WOKOBJS_OSSG::TargetDir() const
{
  return mytarget;
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : SchFile
//purpose  : 
//=======================================================================
 Handle(WOKBuilder_Compilable) WOKOBJS_OSSG::SchFile() const
{
  return myschfile;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetSchFile
//purpose  : 
//=======================================================================
 void WOKOBJS_OSSG::SetSchFile(const Handle(WOKBuilder_Compilable)& afile) 
{
  Params().Set("%OBJS_SchFile", afile->Path()->Name()->ToCString());
  myschfile = afile;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AppSchema
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKOBJS_OSSG::AppSchema() const
{
  return myschname;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetAppSchema
//purpose  : 
//=======================================================================
void WOKOBJS_OSSG::SetAppSchema(const Handle(TCollection_HAsciiString)& aschema) 
{
  Params().Set("%OBJS_AppSchemaName",     aschema->ToCString());
  myschname = aschema;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Load
//purpose  : 
//=======================================================================
void WOKOBJS_OSSG::Load() 
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Execute
//purpose  : 
//=======================================================================
WOKBuilder_BuildStatus WOKOBJS_OSSG::Execute() 
{
  Handle(WOKBuilder_HSequenceOfEntity) aseq = new WOKBuilder_HSequenceOfEntity;
  Handle(TCollection_HAsciiString) astr;

  if(Shell()->IsLaunched() == Standard_False) Shell()->Launch();

  Load();

  Params().Set("%OBJS_OutputDir", OutputDir()->Name()->ToCString());

  astr = Params().Eval("OBJS_OSSG_CmdLine", Standard_True);

  WOK_TRACE {
    VerboseMsg()("WOK_OBJS") << "WOKOBJS_OSSG::Execute" << "OSSG line : " << endm;
    VerboseMsg()("WOK_OBJS") << "WOKOBJS_OSSG::Execute" << astr << endm;
  }

  Shell()->Execute(astr);


  Handle(TColStd_HSequenceOfHAsciiString) resseq = Shell()->Errors();

  if(Shell()->Status())
    {
      Standard_Boolean ph = ErrorMsg().PrintHeader();

      ErrorMsg() << "WOKOBJS_OSSG::Execute" << "Errors occured in Shell" << endm;
      ErrorMsg().DontPrintHeader();
      for(Standard_Integer i=1; i<= resseq->Length(); i++)
	{
	  ErrorMsg() << "WOKOBJS_OSSG::Execute" << resseq->Value(i) << endm;
	}
      if(ph) ErrorMsg().DoPrintHeader();
      return WOKBuilder_Failed;
    }
  else
    {
      Standard_Boolean ph = InfoMsg().PrintHeader();
      InfoMsg().DontPrintHeader();
      for(Standard_Integer i=1; i<= resseq->Length(); i++)
	{
	  InfoMsg() << "WOKOBJS_OSSG::Execute" << resseq->Value(i) << endm;
	}
      if(ph) InfoMsg().DoPrintHeader();
    }

  Shell()->ClearOutput();

  Handle(TCollection_HAsciiString) acxxname =
    WOKOBJS_AppSchCxxFile::GetAppSchSourceFileName(Params(), AppSchema());
  
  Handle(WOKBuilder_Compilable) acxx = new WOKBuilder_Compilable
    (new WOKUtils_Path(OutputDir()->Name(), acxxname));
  aseq->Append(acxx);

  Handle(TCollection_HAsciiString) appschname = 
    WOKOBJS_AppSchema::GetAppFileName(Params(), AppSchema());


  Handle(WOKOBJS_AppSchema) appsch = new WOKOBJS_AppSchema
    (new WOKUtils_Path(OutputDir()->Name(), appschname));
  aseq->Append(appsch);

  SetProduction(aseq);
  return WOKBuilder_Success;
}

