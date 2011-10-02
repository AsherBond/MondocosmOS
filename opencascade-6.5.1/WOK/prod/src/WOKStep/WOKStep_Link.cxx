// File:	WOKStep_Link.cxx
// Created:	Tue Aug 29 21:41:14 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Messages.hxx>
#include <WOKTools_MapOfHAsciiString.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKUtils_MapOfPath.hxx>
#include <WOKUtils_MapIteratorOfMapOfPath.hxx>
#include <WOKUtils_AdmFile.hxx>
#include <WOKUtils_Param.hxx>
#include <WOKUtils_Shell.hxx>

#include <WOKernel_Session.hxx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_BasicUnitTypes.hxx>
#include <WOKernel_FileTypeBase.hxx>
#include <WOKernel_Workbench.hxx>
#include <WOKernel_Parcel.hxx>
#include <WOKernel_FileType.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_UnitGraph.hxx>
#include <WOKernel_Locator.hxx>


#include <WOKBuilder_ObjectFile.hxx>
#include <WOKBuilder_SharedLibrary.hxx>
#include <WOKBuilder_ArchiveLibrary.hxx>
#include <WOKBuilder_Executable.hxx>
#include <WOKBuilder_Linker.hxx>
#include <WOKBuilder_Miscellaneous.hxx>
#include <WOKBuilder_HSequenceOfEntity.hxx>

#include <WOKMake_InputFile.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_HSequenceOfInputFile.hxx>
#include <WOKMake_AdmFileTypes.hxx>

#include <WOKStep_Link.ixx>
//---> EUG4YAN
Standard_IMPORT Standard_Boolean g_fCompOrLnk;
//<--- EUG4YAN
//=======================================================================
//function : WOKStep_Link
//purpose  : 
//=======================================================================
 WOKStep_Link::WOKStep_Link(const Handle(WOKMake_BuildProcess)& abp,
			    const Handle(WOKernel_DevUnit)& aunit, 
			    const Handle(TCollection_HAsciiString)& acode,
			    const Standard_Boolean checked, 
			    const Standard_Boolean hidden)
: WOKMake_Step(abp, aunit, acode, checked, hidden)
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AdmFileType
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_Link::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STADMFILE);
  return result;   
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : OutputDirTypeName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_Link::OutputDirTypeName() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STTMPDIR);
  return result;   
}

//=======================================================================
//function : HandleInputFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKStep_Link::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
{
  Handle(WOKBuilder_Entity) result;
  Handle(WOKUtils_Path)     apath;

  if(!infile->IsPhysic())
    return Standard_True;
  
  if(!infile->File().IsNull())
    {
      apath = infile->File()->Path();
    }
  else
    {
      apath = infile->LastPath();
    }

  if(!apath.IsNull())
    {
      switch(apath->Extension())
	{
	case WOKUtils_ObjectFile:          result = new WOKBuilder_ObjectFile(apath);   break;
	case WOKUtils_ArchiveFile:         result = new WOKBuilder_ArchiveLibrary(apath); break;
	case WOKUtils_DSOFile:             result = new WOKBuilder_SharedLibrary(apath); break;
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
//Author   : Jean Gautier (jga)
//function : CompleteExecList
//purpose  : 
//=======================================================================
void WOKStep_Link::CompleteExecList(const Handle(WOKMake_HSequenceOfInputFile)& execlist)
{

  if((execlist->Length() != 0) && (myinflow.Extent() > execlist->Length()) && (!mydepmatrix.IsNull()))
    {
      WOKTools_MapOfHAsciiString amap;
      Standard_Integer i;
      
      for(i=1; i<=execlist->Length();i++)
	{
	  amap.Add(execlist->Value(i)->ID());
	}

      Standard_Boolean found = Standard_False;
      for(i=1; i<=myinflow.Extent() && !found; i++)
	{
	  if(!amap.Contains(myinflow(i)->ID()))
	    {
	      execlist->Append(myinflow(i));
	      found = Standard_True;
	    }
	}
    }

  WOKMake_Step::CompleteExecList(execlist);

  return;
}


//=======================================================================
//function : ComputeTool
//purpose  : 
//=======================================================================
Handle(WOKBuilder_Linker) WOKStep_Link::ComputeTool()
{
  return Handle(WOKBuilder_Linker)();
}

//=======================================================================
//function : ComputeTarget
//purpose  : 
//=======================================================================
Handle(WOKBuilder_Entity) WOKStep_Link::ComputeTarget()
{
  return Handle(WOKBuilder_Entity)();
}

//=======================================================================
//function : ComputeObjectList
//purpose  : 
//=======================================================================
Handle(WOKBuilder_HSequenceOfObjectFile) WOKStep_Link::ComputeObjectList(const Handle(WOKMake_HSequenceOfInputFile)& tobuild)
{
  Standard_Integer i;
  Handle(WOKBuilder_HSequenceOfObjectFile)  result = new WOKBuilder_HSequenceOfObjectFile;
  Handle(WOKBuilder_ObjectFile)           anobject;

  for(i=1; i<=tobuild->Length(); i++)
    {
      anobject = Handle(WOKBuilder_ObjectFile)::DownCast(tobuild->Value(i)->BuilderEntity());
	      
      if(!anobject.IsNull())
	{
	  result->Append(anobject);
	}
    }
  return result;
}


//=======================================================================
//function : ComputeLibraryList
//purpose  : 
//=======================================================================
Handle(WOKBuilder_HSequenceOfLibrary) WOKStep_Link::ComputeLibraryList(const Handle(WOKMake_HSequenceOfInputFile)& execlist )
{
  Standard_Integer                                i;
  Handle(WOKernel_File)                        alib;
  Handle(WOKernel_DevUnit)                    aunit;
  Handle(WOKernel_UnitNesting)             anesting;
  WOKBuilder_LibReferenceType               reftype;
  Handle(WOKBuilder_SharedLibrary)           ashlib;
  Handle(TCollection_HAsciiString)          libname;
  Handle(WOKBuilder_HSequenceOfLibrary)        aseq = new WOKBuilder_HSequenceOfLibrary;
  static Handle(TCollection_HAsciiString)   libtype = new TCollection_HAsciiString("library");

  for(i=1; i<=execlist->Length(); i++)
    {
      Handle(WOKMake_InputFile)  infile = execlist->Value(i);
      Handle(WOKBuilder_Entity)  anent  = infile->BuilderEntity();
      Handle(WOKBuilder_SharedLibrary)  library;
      Handle(WOKBuilder_ArchiveLibrary) archive;

      library  = Handle(WOKBuilder_SharedLibrary)::DownCast(anent);

      if(!library.IsNull())
	{
	  aunit    = Unit()->Session()->GetDevUnit(infile->File()->Nesting());
	  anesting = aunit->Session()->GetUnitNesting(aunit->Nesting());

	  reftype = WOKBuilder_ShortRef;
#if 0
	  if(anesting->IsKind(STANDARD_TYPE(WOKernel_Workbench)))
	    {
	      reftype = WOKBuilder_LongRef;
	      // modif K2 on tire les .so via -L -l
	      //reftype = WOKBuilder_FullPath;
	    }
	  else
	    {
	      if(anesting->IsKind(STANDARD_TYPE(WOKernel_Parcel)))
		{
		  reftype = WOKBuilder_LongRef;
		}
	      else
		{
		  WarningMsg() << "WOKStep_LinkList::GetUnitLibrary" 
		             << "Unknown Nesting for " << aunit->UserPathName() << endm;
		}
	    }
#endif
	  
	  ashlib = new WOKBuilder_SharedLibrary(aunit->Name(), new WOKUtils_Path, WOKBuilder_FullPath);
	  libname = ashlib->GetLibFileName(Unit()->Params());

	  alib = Locator()->Locate(aunit->Name(), libtype, libname);
	  
	  if(alib.IsNull())
	    {
	      ErrorMsg() << "WOKStep_Link::ComputeLibraryList" 
		       << "Could not find library in unit : " << aunit->UserPathName() << endm;
	      SetFailed();
	    }
	  
	  ashlib = new WOKBuilder_SharedLibrary(aunit->Name(), 
						new WOKUtils_Path(alib->Path()->DirName()),
					        reftype);
   
	  aseq->Append(ashlib);
	  
	}
      archive  = Handle(WOKBuilder_ArchiveLibrary)::DownCast(anent);

      if(!archive.IsNull())
	{
	  archive->SetReferenceType(WOKBuilder_FullPath);
	  aseq->Append(archive);
	}
      
    }

  return aseq;
}

//=======================================================================
//function : ComputeDatabaseDirectories
//purpose  : 
//=======================================================================
Handle(WOKUtils_HSequenceOfPath) WOKStep_Link::ComputeDatabaseDirectories() const
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
//function : ComputeLibrarySearchList
//purpose  : 
//=======================================================================
Handle(WOKUtils_HSequenceOfPath) WOKStep_Link::ComputeLibrarySearchList(const Handle(WOKMake_HSequenceOfInputFile)& execlist )
{
  Standard_Integer i;
  WOKUtils_MapOfPath amapin, amapcalc;
  Handle(WOKUtils_HSequenceOfPath) result = new WOKUtils_HSequenceOfPath;
  Handle(WOKernel_DevUnit) aunit;
  
  
  for(i=1; i<=execlist->Length(); i++)
    {
      Handle(WOKMake_InputFile)  infile = execlist->Value(i);
      Handle(WOKBuilder_Entity)  anent  = infile->BuilderEntity();
      Handle(WOKBuilder_Library) library;
      
      library  = Handle(WOKBuilder_Library)::DownCast(anent);
      
      if(!library.IsNull())
	{
	  Handle(WOKernel_DevUnit)     aunit    = Unit()->Session()->GetDevUnit(infile->File()->Nesting());
	  Handle(WOKernel_UnitNesting) anesting = aunit->Session()->GetUnitNesting(aunit->Nesting());
	  
	  Handle(WOKUtils_Path) adir = new WOKUtils_Path(library->Path()->DirName());
	  
	  adir = adir->ReducedPath();

	  if(!amapin.Contains(adir)) 	
	    {
	      amapin.Add(adir);
	    }
	}
    }

  Handle(TColStd_HSequenceOfHAsciiString) avisiblity = Locator()->Visibility();
  
  for(i=1; i<=avisiblity->Length(); i++)
    {
      static Handle(TCollection_HAsciiString) libdirtype = new TCollection_HAsciiString("libdir");

      Handle(WOKernel_UnitNesting) anesting = Unit()->Session()->GetUnitNesting(avisiblity->Value(i));

      Handle(WOKernel_File) libdir = new WOKernel_File(anesting, anesting->GetFileType(libdirtype));
      
      libdir->GetPath();
      
      Handle(WOKUtils_Path) adir = libdir->Path()->ReducedPath();

      if(!amapcalc.Contains(adir)) 
	{
	  amapcalc.Add(adir);
	  result->Append(adir);
	}
    }
  
  WOKUtils_MapIteratorOfMapOfPath anit(amapin);

  while(anit.More())
    {
      if(!amapcalc.Contains(anit.Key()))
	{
	  WarningMsg() << "WOKStep_Link::ComputeLibrarySearchList"
	             << "Library directory " << anit.Key()->Name() << " referenced in link is not in visibility" << endm;
	}
      anit.Next();
    }
  return result;
}

//=======================================================================
//function : ExecuteLink
//purpose  : 
//=======================================================================
WOKMake_Status WOKStep_Link::ExecuteLink(Handle(WOKMake_HSequenceOfOutputFile)& outputfiles)
{
  Standard_Integer i;
  Handle(WOKernel_Entity)  objentity;
  Handle(WOKernel_FileType) libtype   = Unit()->GetFileType("library");
  Handle(WOKernel_FileType) exectype  = Unit()->GetFileType("executable");
  Handle(WOKernel_FileType) stadmtype = Unit()->GetFileType("stadmfile");
  Handle(WOKernel_File)     lnkfile;
  Handle(WOKernel_File)     file;
  Handle(TCollection_HAsciiString) bidname;

  mylinker->SetOutputDir(OutputDir());

  mylinker->SetTargetName(mytarget);
  mylinker->SetObjectList(myobjects);
  mylinker->SetLibraryList(mylibraries);
  mylinker->SetLibrarySearchPathes(mylibpathes);
  mylinker->SetDatabaseDirectories(mydbdirs);
  mylinker->SetExternals(myexternals);

  bidname = new TCollection_HAsciiString(mytarget);
  bidname->AssignCat(".lnk");
  
  lnkfile = new WOKernel_File(bidname, Unit(), stadmtype);
  lnkfile->GetPath();
  if(lnkfile->Path()->Exists()) lnkfile->Path()->RemoveFile();
  if (!lnkfile->Path()->CreateFile()) {
    ErrorMsg() << "WOKStep_Link::ExecuteLink" 
      << "Unable to create link file " << lnkfile->Path()->Name()->ToCString() << endm;
  }
  Handle(WOKUtils_Shell) ashell = Shell();

  ashell->Lock();
  ashell->SetEcho();

  Unit () -> Params ().Set (  "%LnkFileName", lnkfile -> Path() -> Name () -> ToCString ()  );
  
  if(!ashell->IsLaunched()) ashell->Launch();

  mylinker->SetShell(ashell);
  
  switch(mylinker->Execute())
    {
    case WOKBuilder_Success:
//---> EUG4YAN
 if ( !g_fCompOrLnk )
//<--- EUG4YAN
      {
	Handle(WOKBuilder_Entity) outent;
	for(i=1; i<=mylinker->Produces()->Length(); i++)
	  {
	    outent = mylinker->Produces()->Value(i);
	    
	    if(outent->IsKind(STANDARD_TYPE(WOKBuilder_SharedLibrary)))
	      file = new WOKernel_File(outent->Path()->FileName(), Unit(), libtype);
	    else
	      {
		if(outent->IsKind(STANDARD_TYPE(WOKBuilder_Executable)))
		  file = new WOKernel_File(outent->Path()->FileName(), Unit(), exectype);
		else if(outent->IsKind(STANDARD_TYPE(WOKBuilder_Miscellaneous)))
		  file = new WOKernel_File(outent->Path()->FileName(), Unit(), stadmtype);
	      }
	    

	    file->GetPath();

	    // on prend de fait la liste des objets comme input des Produces du Link

	    // je l'y deplace
	    outent->Path()->MoveTo(file->Path());

	    if(outputfiles.IsNull())
	      outputfiles = new WOKMake_HSequenceOfOutputFile;

	    Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(file->LocatorName(), file,
									outent, file->Path());


	    outfile->SetLocateFlag(Standard_True);
	    outfile->SetProduction();

	    outputfiles->Append(outfile);

	    Handle(WOKMake_OutputFile) outlnk  = new WOKMake_OutputFile(lnkfile->LocatorName(), lnkfile,
									new WOKBuilder_Miscellaneous(lnkfile->Path()),
									lnkfile->Path());
	    outlnk->SetLocateFlag(Standard_True);
	    outlnk->SetProduction();
	    outputfiles->Append(outlnk);
	    // 
	  }
	SetSucceeded();
      }
      break;
    case WOKBuilder_Failed:
      ErrorMsg() << "WOKStep_Link::ExecuteLink" << "Failed    : " << mytarget << endm;
      SetFailed();
      break;
    default: break;
    }
  
  ashell->UnsetEcho();
  ashell->UnLock();

  return Status();
 }
