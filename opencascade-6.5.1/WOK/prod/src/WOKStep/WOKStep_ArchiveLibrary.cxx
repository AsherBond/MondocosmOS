// File:	WOKStep_ArchiveLibrary.cxx
// Created:	Tue Aug 29 21:37:36 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <TCollection_HAsciiString.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKUtils_Shell.hxx>

#include <WOKernel_DevUnit.hxx>
#include <WOKernel_BasicUnitTypes.hxx>
#include <WOKernel_Session.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_FileType.hxx>
#include <WOKernel_FileTypeBase.hxx>
#include <WOKernel_UnitNesting.hxx>
#include <WOKernel_Workbench.hxx>
#include <WOKernel_HSequenceOfFile.hxx>

#include <WOKBuilder_Archiver.hxx>
#include <WOKBuilder_ObjectFile.hxx>
#include <WOKBuilder_ArchiveLibrary.hxx>
#include <WOKBuilder_Miscellaneous.hxx>
#include <WOKBuilder_HSequenceOfEntity.hxx>
#include <WOKBuilder_HSequenceOfObjectFile.hxx>

#include <WOKMake_HSequenceOfInputFile.hxx>
#include <WOKMake_InputFile.hxx>
#include <WOKMake_OutputFile.hxx>

#include <WOKStep_ArchiveLibrary.ixx>


//=======================================================================
//function : WOKStep_ArchiveLibrary
//purpose  : 
//=======================================================================
 WOKStep_ArchiveLibrary::WOKStep_ArchiveLibrary(const Handle(WOKMake_BuildProcess)& abp,
						const Handle(WOKernel_DevUnit)& aunit, 
						const Handle(TCollection_HAsciiString)& acode, 
						const Standard_Boolean checked, 
						const Standard_Boolean hidden) 
: WOKStep_Library(abp, aunit, acode, checked, hidden)
{
}

//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
void WOKStep_ArchiveLibrary::Execute(const Handle(WOKMake_HSequenceOfInputFile)& execlist)
{
  Handle(WOKBuilder_HSequenceOfObjectFile) aseq = new WOKBuilder_HSequenceOfObjectFile;
  Handle(WOKBuilder_Entity)            anent;
  Handle(WOKBuilder_ObjectFile)      anobject;
  Handle(WOKernel_FileType)         archtype   = Unit()->FileTypeBase()->Type("archive");
  Handle(WOKernel_FileType)         stadmtype = Unit()->FileTypeBase()->Type("stadmfile");
  Handle(WOKernel_FileType)         tmptype = Unit()->FileTypeBase()->Type("tmpdir");
  Handle(WOKernel_File)             tmppath;
  Handle(TCollection_HAsciiString)  logname;
  Handle(TCollection_HAsciiString)  libname;
  Handle(TCollection_HAsciiString)  bidname;

  Standard_Integer i,j;

  Handle(WOKBuilder_Archiver) ar = new WOKBuilder_Archiver(Unit()->Params());
  Handle(WOKUtils_Shell)      ashell = Shell();

  ar->SetShell(ashell);
  ashell->Lock();
  
  ar->SetOutputDir(OutputDir());

  logname = new TCollection_HAsciiString(Unit()->Name());

  if(!SubCode().IsNull())
    {
      logname->AssignCat("_");
      logname->AssignCat(SubCode());
    }

  logname->AssignCat(".Log");

  Handle(WOKernel_File) logfile = new WOKernel_File(logname, Unit(), Unit()->GetFileType("stadmfile"));
  logfile->GetPath();

  if(logfile->Path()->Exists()) logfile->Path()->RemoveFile();

  ashell->LogInFile(logfile->Path());

  for(j=1; j<=execlist->Length(); j++)
    {
      const Handle(WOKMake_InputFile)& infile = execlist->Value(j);
      anobject = Handle(WOKBuilder_ObjectFile)::DownCast(infile->BuilderEntity());
      
      if(anobject.IsNull())
	{
	  Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(infile);
	  
	  outfile->SetReference();
	  outfile->SetExtern();
	  
	  Handle(WOKernel_DevUnit) unit = Unit()->Session()->GetDevUnit(infile->File()->Nesting());
	  if(!unit.IsNull())
	    {
	      if(!strcmp(unit->Name()->ToCString(), Unit()->Name()->ToCString()))
		outfile->SetMember();
	    }
	  AddExecDepItem(infile, outfile, Standard_True);
	}
      else
	{
	  aseq->Append(anobject);
	}
    }


  ar->SetObjectList(aseq);

  if(SubCode().IsNull())
    {
      ar->SetTargetName(Unit()->Name());
    }
  else
    {
      ar->SetTargetName(SubCode());
    }

  // calcul de la librairie
  InfoMsg() << "WOKStep_ArchiveLibrary::Execute" << "Creating   : lib" << ar->TargetName() << ".a" << endm;

  switch(ar->Execute())
    {
    case WOKBuilder_Success:
      for(i=1; i<=ar->Produces()->Length(); i++)
	{
	  Handle(WOKernel_File)        file;
	  Handle(WOKBuilder_Entity)   outent = ar->Produces()->Value(i);
	  Handle(WOKMake_OutputFile) outfile;
	  Handle(WOKMake_OutputFile) outobjlist;

	  if(outent->IsKind(STANDARD_TYPE(WOKBuilder_ArchiveLibrary)))
	    file = new WOKernel_File(outent->Path()->FileName(), Unit(), archtype);
	  else
	    {
	      if(outent->IsKind(STANDARD_TYPE(WOKBuilder_Miscellaneous)))
		file = new WOKernel_File(outent->Path()->FileName(), Unit(), stadmtype);
	    }
	  
	  if(!file.IsNull())
	    {
	      file->GetPath();
	      outent->Path()->MoveTo(file->Path());
	      
	      outfile = new WOKMake_OutputFile(file->LocatorName(), file, outent, file->Path());
	      outfile->SetLocateFlag(Standard_True);
	      outfile->SetProduction();

	      for(j=1; j<=execlist->Length(); j++)
		{
		  AddExecDepItem(execlist->Value(j), outfile,    Standard_True);
		}
	    }
	}
      InfoMsg() << "WOKStep_ArchiveLibrary::Execute"
	      << "Succeeded  : lib" << ar->TargetName() << ".a" << endm;
      SetSucceeded();
      break;
    case WOKBuilder_Failed:
      ErrorMsg() << "WOKStep_ArchiveLibrary"
	       << "Failed     : lib" <<   ar->TargetName() << ".a" << endm;           
      SetFailed();
      break;
    default: break;
    }
  ashell->NoLog();
  ashell->UnLock();
  return;
}






