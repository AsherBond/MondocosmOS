// File:	WOKStep_LibExtract.cxx
// Created:	Tue Aug  6 11:02:26 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>


#include <WOKTools_Messages.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKUtils_Extension.hxx>
#include <WOKUtils_Shell.hxx>

#include <WOKernel_File.hxx>
#include <WOKernel_FileType.hxx>

#include <WOKBuilder_HSequenceOfEntity.hxx>
#include <WOKBuilder_CompressedFile.hxx>
#include <WOKBuilder_Command.hxx>
#include <WOKBuilder_ArchiveLibrary.hxx>
#include <WOKBuilder_ArchiveExtract.hxx>
#include <WOKBuilder_ObjectFile.hxx>

#include <WOKMake_InputFile.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_AdmFileTypes.hxx>

#include <WOKStep_LibExtract.ixx>



//=======================================================================
//Author   : Jean Gautier (jga)
//function : WOKStep_LibExtract
//purpose  : 
//=======================================================================
WOKStep_LibExtract::WOKStep_LibExtract(const Handle(WOKMake_BuildProcess)& abp,
				       const Handle(WOKernel_DevUnit)& aunit, 
				       const Handle(TCollection_HAsciiString)& acode, 
				       const Standard_Boolean checked, 
				       const Standard_Boolean hidden) 
  : WOKMake_Step(abp,aunit, acode, checked, hidden)
{
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : AdmFileType
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_LibExtract::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STADMFILE);
  return result;   
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : OutputDirTypeName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_LibExtract::OutputDirTypeName() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STTMPDIR);
  return result;   
}

//=======================================================================
//function : HandleInputFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKStep_LibExtract::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
{
  Handle(WOKBuilder_Entity) result;
  Handle(WOKUtils_Path)     apath;

  if(!infile->File().IsNull())
    {
      apath = infile->File()->Path();
      switch(apath->Extension())
	{
	case WOKUtils_ArchiveFile:  
	  result = new WOKBuilder_ArchiveLibrary(apath); break;
	case WOKUtils_CompressedFile:  
	  result = new WOKBuilder_CompressedFile(apath); break;
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
//function : Execute
//purpose  : 
//=======================================================================
void WOKStep_LibExtract::Execute(const Handle(WOKMake_HSequenceOfInputFile)& execlist)
{
  Handle(WOKBuilder_ArchiveExtract) anext = new WOKBuilder_ArchiveExtract(Unit()->Params());
  Standard_Integer i,j;
  Standard_Boolean stepfailed = Standard_False;
  Standard_Boolean failed     = Standard_False;
  Standard_Boolean wascompressed;
  
  anext->SetShell(Shell());
  
  Shell()->Launch();

  anext->SetOutputDir(OutputDir());

  Handle(WOKernel_FileType) objtype = Unit()->GetFileType("object");
  
  for(i=1; i<=execlist->Length(); i++)
    {
      Handle(WOKMake_InputFile) infile = execlist->Value(i);
      wascompressed = Standard_False;
      failed        = Standard_False;

      if(infile->BuilderEntity()->IsKind(STANDARD_TYPE(WOKBuilder_ArchiveLibrary)))
	{
	  anext->SetArchive(Handle(WOKBuilder_ArchiveLibrary)::DownCast(infile->BuilderEntity()));
	}
      else if (infile->BuilderEntity()->IsKind(STANDARD_TYPE(WOKBuilder_CompressedFile)))
	{
	  Handle(WOKBuilder_Command) acmd = new WOKBuilder_Command(new TCollection_HAsciiString("COMMAND"),
								   Unit()->Params());

	  Handle(WOKernel_File) liba = new WOKernel_File(infile->File()->Path()->BaseName(), Unit(), Unit()->GetFileType("sttmpfile"));
	  liba->GetPath();

	  acmd->SetShell(Shell());

	  

	  switch(acmd->UnCompressTo(infile->File()->Path(), liba->Path()))
	    {
	    case WOKBuilder_Success:
	      {
		Handle(WOKBuilder_ArchiveLibrary) anent = new WOKBuilder_ArchiveLibrary(liba->Path());
		InfoMsg() << "WOKStep_TKLibUnCompress::Execute" 
			<< "Uncompress : " << infile->File()->UserPathName() << endm;
		
		anext->SetArchive(anent);
		wascompressed = Standard_True;
	      }
	    break;
	    case WOKBuilder_Failed:
	      failed = Standard_True;
	      break;
	    default:
	      break;
	    }
	}
      else
	{
	  ErrorMsg() << "WOKStep_LibExtract::Execute" 
		   << "Unrecognized input file : " << infile->ID() << endm;
	  SetFailed();
	  return;
	}


      if(!failed)
	{
	  InfoMsg() << "WOKStep_LibExtract::Execute" 
		  << "Extracting : " << infile->File()->UserPathName() << endm;
	  
	  switch(anext->Execute())
	    {
	    case WOKBuilder_Success:
	      {
		Handle(WOKBuilder_HSequenceOfEntity) aseq = anext->Produces();
		
		for(j=1; j<=aseq->Length(); j++)
		  {
		    Handle(WOKBuilder_Entity) anent = aseq->Value(j);
		    
		    if(anent->IsKind(STANDARD_TYPE(WOKBuilder_ObjectFile)))
		      {
			Handle(WOKernel_File) Kfile = new WOKernel_File(anent->Path()->FileName(), Unit(), objtype);
			Kfile->GetPath();
			anent->Path()->MoveTo(Kfile->Path());
			
			Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(Kfile->LocatorName(), Kfile, anent, anent->Path());
			outfile->SetLocateFlag(Standard_True);
			outfile->SetProduction();
			AddExecDepItem(infile, outfile, Standard_True);
		      }
		    else
		      {
			WarningMsg() << "WOKStep_LibExtract::Execute" 
				   << "Ignoring non object result : " << anent->Path()->Name() << endm;
			failed = Standard_True;
		      }
		  }
	      }
	    break;
	    case WOKBuilder_Failed:
	      failed = Standard_True;
	      break;
	    default:
	      break;
	    }
	}

      stepfailed |= failed;
      if(wascompressed)
	{
	  anext->Archive()->Path()->RemoveFile();
	}
    }
  
  if(stepfailed) SetFailed();
  else       SetSucceeded();

  return;
}
