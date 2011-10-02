// File:	WOKStep_DynamicLibrary.cxx
// Created:	Tue Aug 29 21:40:53 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKUtils_Shell.hxx>

#include <WOKBuilder_HSequenceOfEntity.hxx>
#include <WOKBuilder_HSequenceOfObjectFile.hxx>
#include <WOKBuilder_HSequenceOfLibrary.hxx>
#include <WOKBuilder_ObjectFile.hxx>
#include <WOKBuilder_SharedLinker.hxx>
#include <WOKBuilder_SharedLibrary.hxx>
#include <WOKBuilder_ArchiveLibrary.hxx>
#include <WOKBuilder_Miscellaneous.hxx>

#include <WOKernel_FileTypeBase.hxx>
#include <WOKernel_FileType.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_Session.hxx>
#include <WOKernel_Workbench.hxx>

#include <WOKMake_InputFile.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_HSequenceOfInputFile.hxx>

#include <WOKStep_DynamicLibrary.ixx>
//---> EUG4YAN
Standard_IMPORT Standard_Boolean g_fCompOrLnk;
//<--- EUG4YAN
//=======================================================================
//function : WOKStep_DynamicLibrary
//purpose  : 
//=======================================================================
WOKStep_DynamicLibrary::WOKStep_DynamicLibrary(const Handle(WOKMake_BuildProcess)& abp,
					       const Handle(WOKernel_DevUnit)& aunit, 
					       const Handle(TCollection_HAsciiString)& acode,
					       const Standard_Boolean checked, 
					       const Standard_Boolean hidden)
: WOKStep_Library(abp,aunit,acode,checked,hidden)
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : HandleInputFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKStep_DynamicLibrary::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
{
  Handle(WOKBuilder_Entity) result;
  Handle(WOKUtils_Path)     apath;

  if(!infile->File().IsNull())
    {
      apath = infile->File()->Path();
      switch(apath->Extension())
	{
	case WOKUtils_ObjectFile:  result = new WOKBuilder_ObjectFile(apath);     break;
	case WOKUtils_ArchiveFile: result = new WOKBuilder_ArchiveLibrary(apath); break;
	case WOKUtils_DSOFile:     result = new WOKBuilder_SharedLibrary(apath);  break;
	default:  
	  return Standard_False;
	}
      
      infile->SetBuilderEntity(result);
      infile->SetDirectFlag(Standard_True);
      return Standard_True;
    }  
  return Standard_False; 
}

//=======================================================================
//function : ComputeDatabaseDirectories
//purpose  : 
//=======================================================================
Handle(WOKUtils_HSequenceOfPath) WOKStep_DynamicLibrary::ComputeDatabaseDirectories() const
{
  Handle(WOKUtils_HSequenceOfPath) aseq;

   if(Unit()->Params().Eval("%WOKSteps_UseDatabaseDirectory").IsNull())
    {
      return aseq;
    }

  Handle(TColStd_HSequenceOfHAsciiString) nestingseq = Unit()->Session()->GetWorkbench(Unit()->Nesting())->Visibility();
  Handle(TCollection_HAsciiString) aname;
  Handle(WOKernel_File)     afile;
  Handle(WOKernel_FileType) atype;
  Handle(WOKernel_DevUnit)  aunit;
  Handle(TCollection_HAsciiString) DOT = new TCollection_HAsciiString(".");
  WOKTools_MapOfHAsciiString amap;
  Standard_Integer i;

  aseq = new WOKUtils_HSequenceOfPath;

  for(i=1; i<=nestingseq->Length(); i++)
    {
      Handle(WOKernel_UnitNesting) nesting = Unit()->Session()->GetUnitNesting(nestingseq->Value(i));
      // les wbs uniquement
      if(nesting->IsKind(STANDARD_TYPE(WOKernel_Workbench))) {
	atype = nesting->FileTypeBase()->Type("libdir");
	afile = new WOKernel_File(DOT, nesting, atype);
	afile->GetPath();
	if(!amap.Contains(afile->Path()->Name()))
	  {
	    aseq->Append(afile->Path());
	    amap.Add(afile->Path()->Name());
	  }
      }
    }

  return aseq;
}

//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
void WOKStep_DynamicLibrary::Execute(const Handle(WOKMake_HSequenceOfInputFile)& execlist)
{
  Handle(WOKBuilder_HSequenceOfObjectFile)  objseq = new WOKBuilder_HSequenceOfObjectFile;
  Handle(WOKBuilder_HSequenceOfLibrary)     libseq = new WOKBuilder_HSequenceOfLibrary;
  Handle(WOKBuilder_ObjectFile)           anobject;
  Handle(WOKernel_FileType)                libtype   = Unit()->GetFileType("library");
  Handle(WOKernel_FileType)                stadmtype = Unit()->GetFileType("stadmfile");
  Handle(WOKBuilder_Library)                  alib;
  Handle(TCollection_HAsciiString)         libname;
  Handle(TCollection_HAsciiString)         bidname;
  Standard_Integer                             i,j;

  Handle(WOKBuilder_SharedLinker)  ldshr = new WOKBuilder_SharedLinker(new TCollection_HAsciiString("LDSHR"),
								       Unit()->Params());

  Handle(WOKUtils_Shell) ashell = Shell();
//--> EUG4YAN
  bidname = new TCollection_HAsciiString (  Unit () -> Name ()  );
  bidname -> AssignCat ( ".lnk" );
  
  Handle( WOKernel_File ) lnkfile = new WOKernel_File (  bidname, Unit (), stadmtype  );
  lnkfile -> GetPath ();

  if (  lnkfile -> Path () -> Exists ()  ) lnkfile -> Path () -> RemoveFile ();

  if (  !lnkfile -> Path () -> CreateFile ()  )

   ErrorMsg() << "WOKStep_Link::ExecuteLink" 
            << "Unable to create link file '"
            << lnkfile -> Path () -> Name () -> ToCString ()
            << "'" << endm;
//<-- EUG4YAN
  ashell->Lock();
//--> EUG4YAN
  Unit () -> Params ().Set (  "%LnkFileName", lnkfile -> Path () -> Name () -> ToCString ()  );
//<-- EUG4YAN
  ldshr->SetShell(ashell);
  
  ldshr->SetOutputDir(OutputDir());
  
  for(j=1; j<=execlist->Length(); j++)
    {
      anobject = Handle(WOKBuilder_ObjectFile)::DownCast(execlist->Value(j)->BuilderEntity());
      
      if(!anobject.IsNull())
	{
	  objseq->Append(anobject);
	}
      
      alib= Handle(WOKBuilder_Library)::DownCast(execlist->Value(j)->BuilderEntity());
      
      if(!alib.IsNull())
	{
	  libseq->Append(alib);
	}
    }

  ldshr->SetObjectList(objseq);
  ldshr->SetLibraryList(libseq);
  ldshr->SetDatabaseDirectories(ComputeDatabaseDirectories());

  // Calcul du soname
  Handle(WOKUtils_Path) apath;

  alib = new WOKBuilder_SharedLibrary(Unit()->Name(), apath, WOKBuilder_FullPath);
  libname = alib->GetLibFileName(Unit()->Params());
  
  ldshr->SetLogicalName(libname);

  // TargetName
  if(SubCode().IsNull())
    {
      ldshr->SetTargetName(Unit()->Name());
    }
  else
    {
      ldshr->SetTargetName(SubCode());
    }

  // Externals is Empty in this Step
  ldshr->SetExternals(new TColStd_HSequenceOfHAsciiString);
//---> EUG4YAN
 if ( !g_fCompOrLnk )
//<--- EUG4YAN
  InfoMsg() << "WOKStep_DynamicLibrary::Execute"
	  << "Creating   : " << libname << endm;

  switch(ldshr->Execute())
    {
    case WOKBuilder_Success:
//---> EUG4YAN
 if ( !g_fCompOrLnk ) {
//<--- EUG4YAN
      for(i=1; i<=ldshr->Produces()->Length(); i++)
	{
	  Handle(WOKBuilder_Entity)   outent = ldshr->Produces()->Value(i);
	  Handle(WOKernel_File)       outitem;
	  Handle(WOKMake_OutputFile)  outfile;

	  if(outent->IsKind(STANDARD_TYPE(WOKBuilder_Miscellaneous)))
	    {
	      outitem = new WOKernel_File(outent->Path()->FileName(), Unit(), stadmtype);
	    }
	  else
	    {
	      switch(outent->Path()->Extension())
		{
		case WOKUtils_DSOFile:
		  outitem = new WOKernel_File(outent->Path()->FileName(), Unit(), libtype);
		  break;
		default:
		  ErrorMsg() << "WOKStep_DynamicLibrary::Execute"
			   << "Unrecognized production : " << outent->Path()->FileName() << endm;
		  SetFailed();
		  return;
		}
	    }

	  if(!outitem.IsNull())
	    {
	      outitem->GetPath();
	      
	      outent->Path()->MoveTo(outitem->Path());
	      
	      outfile = new WOKMake_OutputFile(outitem->LocatorName(), outitem, outent, outitem->Path());
	      outfile->SetLocateFlag(Standard_True);
	      outfile->SetProduction();

	      for(j=1; j<=execlist->Length(); j++)
		{
		  AddExecDepItem(execlist->Value(j), outfile,    Standard_True);
		}
	    }
	}
      InfoMsg() << "WOKStep_DynamicLibrary::Execute"
	      << "Succeeded  : " << libname << endm;
      SetSucceeded();
//---> EUG4YAN
 }  // end if
//<--- EUG4YAN
      break;
    case WOKBuilder_Failed:
      ErrorMsg() << "WOKStep_DynamicLibrary::Execute" 
	       << "Failed     : " <<   libname << endm;           
      SetFailed();
      break;
    default: break;
    }
  
  ashell->UnLock();
  return;
}

