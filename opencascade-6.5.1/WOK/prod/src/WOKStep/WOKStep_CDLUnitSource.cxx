// File:        WOKStep_CDLUnitSource.cxx
// Created:     Tue Aug 29 21:40:36 1995
// Author:      Jean GAUTIER
//              <jga@cobrax>

#ifdef HAVE_CONFIG_H
# include <config.h>
#endif

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKUtils_Param.hxx>

#include <WOKernel_FileTypeBase.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_FileType.hxx>
#include <WOKernel_Session.hxx>
#include <WOKernel_Workbench.hxx>
#include <WOKernel_Locator.hxx>
#include <WOKernel_BasicUnitTypes.hxx>

#include <WOKBuilder_CDLFile.hxx>
#include <WOKBuilder_MSTool.hxx>
#include <WOKBuilder_MSAction.hxx>
#include <WOKBuilder_MSchema.hxx>
#include <WOKBuilder_MSEntity.hxx>
#include <WOKBuilder_HSequenceOfEntity.hxx>
#include <WOKBuilder_MSTranslatorIterator.hxx>
#include <WOKBuilder_MSTranslator.hxx>

#include <WOKMake_DepItem.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_InputFile.hxx>

#include <WOKStep_CDLUnitSource.ixx>

#ifdef WNT
# include <WOKNT_WNT_BREAK.hxx>
# include <windows.h>
# define sleep( nSec ) Sleep (  1000 * ( nSec )  )
#endif  // WNT

//=======================================================================
//function : WOKStep_CDLUnitSource
//purpose  : 
//=======================================================================
WOKStep_CDLUnitSource::WOKStep_CDLUnitSource(const Handle(WOKMake_BuildProcess)& abp,
                                             const Handle(WOKernel_DevUnit)& aunit, 
                                             const Handle(TCollection_HAsciiString)& acode, 
                                             const Standard_Boolean checked, 
                                             const Standard_Boolean hidden)
: WOKStep_Source(abp, aunit, acode, checked, hidden)
{
}

//=======================================================================
//function : GetUnitDescr
//purpose  : 
//=======================================================================
Handle(WOKernel_File) WOKStep_CDLUnitSource::GetUnitDescr() const
{
  Handle(TCollection_HAsciiString)    astr;
  Handle(TCollection_HAsciiString)    asourcetype = new TCollection_HAsciiString("source");
  Handle(WOKernel_File)               afile;

  astr  = new TCollection_HAsciiString(Unit()->Name());
  astr->AssignCat(".cdl");  
  afile = Locator()->Locate(Unit()->Name(), asourcetype, astr);
  return afile;
}

//=======================================================================
//function : ReadUnitDescr
//purpose  : 
//=======================================================================
void WOKStep_CDLUnitSource::ReadUnitDescr(const Handle(WOKMake_InputFile)& PKCDL)
{
  Handle(WOKBuilder_MSTranslator) acdlt = new WOKBuilder_MSTranslator(new TCollection_HAsciiString("CDLTranslate"), Unit()->Params());
  Handle(WOKBuilder_MSchema) ams = WOKBuilder_MSTool::GetMSchema();
  Handle(WOKBuilder_Specification) aspec;
  Handle(WOKernel_File) acdlfile, gefile;
  Handle(WOKMake_InputFile) geitem;
  Handle(TCollection_HAsciiString) astr;
  Handle(TCollection_HAsciiString) aunitname;
  Handle(TCollection_HAsciiString) sourcetype = new TCollection_HAsciiString("source");
  Handle(TColStd_HSequenceOfHAsciiString) aseq;
  Standard_Integer i;
  Standard_Boolean stop = Standard_False;

  // Initialisation du traducteur
  acdlt->Load();
  acdlt->SetMSchema(WOKBuilder_MSTool::GetMSchema());

  
  WOKBuilder_MSTranslatorIterator& anit = BuildProcess()->TranslatorIterator();
  Handle(WOKBuilder_MSEntity) theentity;

  if(WOKernel_IsExecutable(Unit()))
    {
      anit.AddInStack(Unit()->Name(), WOKBuilder_Executable);
    }
  else if(WOKernel_IsServer(Unit()))
    {
      anit.AddInStack(Unit()->Name(), WOKBuilder_Component);
    }
  else
    {
      anit.AddInStack(Unit()->Name(), WOKBuilder_GlobEnt);
    }

  while(anit.More() && !stop)
    {
#ifdef WNT
_TEST_BREAK();
#endif  // WNT
      Handle(WOKBuilder_MSAction) anaction = anit.Value();

      astr = ams->AssociatedFile(anaction->Entity()->Name());
      aunitname = ams->AssociatedEntity(anaction->Entity()->Name());
      
      acdlfile = Locator()->Locate(aunitname, new TCollection_HAsciiString("source"), astr);
      
      if(!acdlfile.IsNull())
        {
          aspec = new WOKBuilder_CDLFile(acdlfile->Path());
          
          switch(anit.Execute(acdlt, anaction, aspec))
            {
            case WOKBuilder_Unbuilt:
            case WOKBuilder_Success:
              break;
            case WOKBuilder_Failed:
              stop = Standard_True;
              anit.Reset();
              break;
            }
        }
      else
        {
          WarningMsg() << "WOKStep_MSFill::Execute" << "No file " << astr << " in " << aunitname << endm;
          SetIncomplete();
        }
      
      anit.Next();
    }
  
  const Handle(MS_MetaSchema)& theschema = WOKBuilder_MSTool::GetMSchema()->MetaSchema();
  if (!stop) {
    // test du type d'ud
    if (WOKernel_IsPackage(Unit())) {
      if (!theschema->IsPackage(Unit()->Name())) {
        stop = Standard_True;
      }
    }
    else if (WOKernel_IsSchema(Unit())) {
      if (!theschema->IsSchema(Unit()->Name())) {
        stop = Standard_True;
      }
    }
    else if (WOKernel_IsInterface(Unit())) {
      if (!theschema->IsInterface(Unit()->Name())) {
        stop = Standard_True;
      }
    }
    else if (WOKernel_IsClient(Unit())) {
      if (!theschema->IsClient(Unit()->Name())) {
        stop = Standard_True;
      }
    }
    else if (WOKernel_IsEngine(Unit())) {
      if (!theschema->IsEngine(Unit()->Name())) {
        stop = Standard_True;
      }
    }
    else if (WOKernel_IsExecutable(Unit())) {
      if (!theschema->IsExecutable(Unit()->Name())) {
        stop = Standard_True;
      }
    }
    else if (WOKernel_IsServer(Unit())) {
      if (!theschema->IsComponent(Unit()->Name())) {
        stop = Standard_True;
      }
    }
    if (stop) {
      ErrorMsg() << "WOKStep_MSFill::Execute" 
        << "Unit and cdl file definition type mismatch for unit " << Unit()->Name() << endm;
    }
  }

    
  Handle(WOKernel_File) anent;

  aseq = WOKBuilder_MSTool::GetMSchema()->GetEntityTypes(Unit()->Name());

  for(i=1; i<=aseq->Length(); i++)
    {
#ifdef WNT
_TEST_BREAK();
#endif  // WNT
      anent  = Locator()->Locate(Unit()->Name(), sourcetype, WOKBuilder_MSTool::GetMSchema()->AssociatedFile(aseq->Value(i)));
      if(!anent.IsNull())
        {
          Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(anent->LocatorName(), 
                                                                      anent,
                                                                      Handle(WOKBuilder_Entity)(), anent->Path());
          outfile->SetLocateFlag(Standard_True);
          outfile->SetProduction();
          AddExecDepItem(PKCDL, outfile, Standard_True);
        }
    }

  if(stop)
    SetFailed();
  else
    SetSucceeded();
  return;
}

//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
void WOKStep_CDLUnitSource::Execute(const Handle(WOKMake_HSequenceOfInputFile)& execlist)
{
  Handle(WOKernel_File) FILES = GetFILES();
  Handle(WOKernel_File) PKCDL = GetUnitDescr();

  if(execlist->Length())
    {
      Standard_Integer i;

      for(i=1; i<=execlist->Length(); i++)
        {
          if(!strcmp(execlist->Value(i)->File()->Name()->ToCString(), FILES->Name()->ToCString()))
            {
              ReadFILES(execlist->Value(i));
            }
          if(!strcmp(execlist->Value(i)->File()->Name()->ToCString(), PKCDL->Name()->ToCString()))
            {
              ReadFILES(execlist->Value(i));
            }
        }
    }
  else
    {
      if(!FILES.IsNull())
        {
          Handle(WOKMake_InputFile) infile = new WOKMake_InputFile(FILES->LocatorName(), FILES, 
                                                                   Handle(WOKBuilder_Entity)(), FILES->Path());
          execlist->Append(infile);
          infile->SetDirectFlag(Standard_True);
          infile->SetLocateFlag(Standard_True);
          
          ReadFILES(infile);
        }

      if(CheckStatus("FILES reading")) return;
      if(!PKCDL.IsNull())
        {
          Handle(WOKBuilder_Specification) cdlent = new WOKBuilder_CDLFile(PKCDL->Path());
          Handle(WOKMake_InputFile) infile = new WOKMake_InputFile(PKCDL->LocatorName(), PKCDL, 
                                                                   cdlent , PKCDL->Path());
          execlist->Append(infile);
          infile->SetDirectFlag(Standard_True);
          infile->SetLocateFlag(Standard_True);
          ReadUnitDescr(infile);
        }
      if(CheckStatus("CDL processing")) return;
    }
  return;
}
