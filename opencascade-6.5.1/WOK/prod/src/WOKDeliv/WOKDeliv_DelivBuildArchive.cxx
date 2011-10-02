// Copyright: 	Matra-Datavision 1996
// File:	WOKDeliv_DelivBuildArchive.cxx
// Created:	Wed Aug 14 13:11:49 1996
// Author:	Arnaud BOUZY
//		<adn>

#include <WOKDeliv_DelivBuildArchive.ixx>
#include <WOKDeliv_ParseDelivery.hxx>
#include <WOKMake_AdmFileTypes.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_HSequenceOfOutputFile.hxx>

#include <WOKBuilder_Archiver.hxx>
#include <WOKBuilder_ArchiveLibrary.hxx>
#include <WOKBuilder_Command.hxx>
#include <WOKBuilder_HSequenceOfObjectFile.hxx>
#include <WOKBuilder_ObjectFile.hxx>
#include <WOKBuilder_HSequenceOfEntity.hxx>
#include <WOKernel_FileTypeBase.hxx>
#include <WOKernel_Session.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_FileType.hxx>
#include <WOKernel_Locator.hxx>

#include <WOKUtils_Shell.hxx>
#include <WOKUtils_Path.hxx>
#include <WOKTools_Messages.hxx>

WOKDeliv_DelivBuildArchive::WOKDeliv_DelivBuildArchive(const Handle(WOKMake_BuildProcess)& aprocess,
						       const Handle(WOKernel_DevUnit)& aunit,
						       const Handle(TCollection_HAsciiString)& acode,
						       const Standard_Boolean checked,
						       const Standard_Boolean hidden)
: WOKDeliv_DeliveryCopy(aprocess,aunit,acode,checked,hidden)
{
}


void WOKDeliv_DelivBuildArchive::Execute(const Handle(WOKMake_HSequenceOfInputFile)& infiles)
{
  Handle(WOKernel_DevUnit) thesourceunit;
  if (myList.IsNull()) {
    myList = ParseCOMPONENTS(T_ARCHIVE);
  }
  Handle(WOKernel_Parcel) theParcel = GetParcel(Unit(),myList->GetName());
  Standard_Boolean okexec = Standard_False;

  // SetOutputDir
  Handle(WOKernel_DevUnit) parcelunit = GetParcelUnit(Unit(),theParcel,Unit());
  Handle(WOKernel_File) afile = new WOKernel_File(parcelunit, parcelunit->GetFileType(OutputDirTypeName()));
  afile->GetPath();
  SetOutputDir(afile->Path());

  if (!theParcel.IsNull()) {
    thesourceunit = Locator()->LocateDevUnit(SubCode());
    okexec = BuildArchive(theParcel,thesourceunit,infiles);
  }
  if (okexec) {
    SetSucceeded();
  }
  else {
    SetFailed();
  }
}



Standard_Boolean WOKDeliv_DelivBuildArchive::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
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
	default:  
	  return Standard_False;
	}
      
      infile->SetBuilderEntity(result);
      infile->SetDirectFlag(Standard_True);
      return Standard_True;
    }  
  return Standard_False; 
}



Standard_Boolean WOKDeliv_DelivBuildArchive::BuildArchive
(const Handle(WOKernel_Parcel)& theParcel,
 const Handle(WOKernel_DevUnit)& aunit, 
 const Handle(WOKMake_HSequenceOfInputFile)& execlist)
{
  Handle(WOKBuilder_HSequenceOfObjectFile) aseq = new WOKBuilder_HSequenceOfObjectFile;
  Handle(WOKBuilder_Entity)            anent;
  Handle(WOKBuilder_ObjectFile)      anobject;
  Handle(WOKernel_File)             libpath;
  Handle(WOKernel_File)             tmppath;
  Handle(TCollection_HAsciiString)  libname;
  Handle(TCollection_HAsciiString)  bidname;
  Handle(WOKernel_DevUnit) destunit = GetParcelUnit(Unit(),theParcel,aunit);
  if (destunit.IsNull()) return Standard_False;

  Handle(WOKernel_FileType)         libtype = destunit->FileTypeBase()->Type("library");

  Standard_Integer i,j;

  Handle(WOKBuilder_Archiver) ar = new WOKBuilder_Archiver(aunit->Params());
  Handle(WOKUtils_Shell)      ashell = Shell();

  ar->SetShell(ashell);
  ashell->Lock();
  
  ar->SetOutputDir(OutputDir());

  for(j=1; j<=execlist->Length(); j++)
    {
      anobject = Handle(WOKBuilder_ObjectFile)::DownCast(execlist->Value(j)->BuilderEntity());
      if (!anobject.IsNull()) {
	aseq->Append(anobject);
      }
    }

  ar->SetObjectList(aseq);
  ar->SetTargetName(aunit->Name());

  // calcul de la librairie

  switch(ar->Execute())
    {
    case WOKBuilder_Success:
      ashell->UnLock();
      for(i=1; i<=ar->Produces()->Length(); i++)
	{
	  Handle(WOKBuilder_Entity)   outent = ar->Produces()->Value(i);
	  Handle(WOKMake_OutputFile) outfile;
	  Handle(WOKUtils_Path) comppath;
	  Handle(WOKBuilder_Entity) bidon;

	  if (outent->Path()->Extension() == WOKUtils_ArchiveFile) {
		
	    Handle(TCollection_HAsciiString) thecomname = new TCollection_HAsciiString("COMMAND");
	    Handle(WOKBuilder_Command) comcomp 
	      = new WOKBuilder_Command(thecomname,
				       aunit->Params());
	    
	    comcomp->SetShell(ashell);
	    if (comcomp->Compress(outent->Path()) == WOKBuilder_Success) {
	      aunit->Params().Set("%File",outent->Path()->Name()->ToCString());
	      Handle(TCollection_HAsciiString) namecomp = aunit->Params().Eval("COMMAND_CompressName");
	      if (!namecomp.IsNull()) {
		comppath = new WOKUtils_Path(namecomp);
		libpath = new WOKernel_File(comppath->FileName(), destunit, libtype);

		libpath->GetPath();
	    
		comppath->MoveTo(libpath->Path());
		outfile = new WOKMake_OutputFile(libpath->LocatorName(), libpath,bidon , libpath->Path());
		outfile->SetLocateFlag(Standard_True);
		outfile->SetExtern();
		outfile->SetProduction();
		
		for(j=1; j<=execlist->Length(); j++) {
		  AddExecDepItem(execlist->Value(j), outfile, Standard_True);
		}
	      }
	      else {
		return Standard_False;
	      }
	    }
	  }
	    
	}
      break;
    case WOKBuilder_Failed:
      ashell->UnLock();
      ErrorMsg() << "WOKStep_ArchiveLibrary"
	       << "Failed     : " <<   libpath->Name() << endm;           
      return Standard_False;
     default: break;
    }

  return Standard_True;
}

void WOKDeliv_DelivBuildArchive::CompleteExecList(const Handle(WOKMake_HSequenceOfInputFile)& execlist)
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

  WOKDeliv_DeliveryCopy::CompleteExecList(execlist);
}

