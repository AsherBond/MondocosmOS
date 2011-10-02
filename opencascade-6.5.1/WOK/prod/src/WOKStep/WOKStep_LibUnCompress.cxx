// File:	WOKStep_LibUnCompress.cxx
// Created:	Thu Jul 18 13:23:36 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>

#include <WOKTools_Messages.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKUtils_Extension.hxx>
#include <WOKUtils_Shell.hxx>

#include <WOKernel_File.hxx>

#include <WOKBuilder_CompressedFile.hxx>
#include <WOKBuilder_Command.hxx>
#include <WOKBuilder_ArchiveLibrary.hxx>

#include <WOKMake_InputFile.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_AdmFileTypes.hxx>

#include <WOKStep_LibUnCompress.ixx>

//=======================================================================
//Author   : Jean Gautier (jga)
//function : WOKStep_LibUnCompress
//purpose  : 
//=======================================================================
WOKStep_LibUnCompress::WOKStep_LibUnCompress(const Handle(WOKMake_BuildProcess)& abp,
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
Handle(TCollection_HAsciiString) WOKStep_LibUnCompress::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STADMFILE);
  return result;   
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : OutputDirTypeName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_LibUnCompress::OutputDirTypeName() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STTMPDIR);
  return result;   
}

//=======================================================================
//function : HandleInputFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKStep_LibUnCompress::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
{
  Handle(WOKBuilder_Entity) result;
  Handle(WOKUtils_Path)     apath;

  if(!infile->File().IsNull())
    {
      apath = infile->File()->Path();
      switch(apath->Extension())
	{
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
void WOKStep_LibUnCompress::Execute(const Handle(WOKMake_HSequenceOfInputFile)& execlist)
{
  Handle(WOKBuilder_Command) acmd = new WOKBuilder_Command(new TCollection_HAsciiString("COMMAND"), Unit()->Params());
  Standard_Integer i;

  acmd->SetShell(Shell());
  
  if(!Shell()->IsLaunched()) Shell()->Launch();

  
  for(i=1; i<=execlist->Length(); i++)
    {
      Handle(WOKMake_InputFile) infile = execlist->Value(i);

      Handle(WOKernel_File) liba = new WOKernel_File(infile->File()->Path()->BaseName(), Unit(), Unit()->GetFileType("archive"));

      liba->GetPath();

      InfoMsg() << "WOKStep_LibUnCompress::Execute" 
	      << "Uncompress : " << infile->File()->UserPathName() << endm;

      switch(acmd->UnCompressTo(infile->File()->Path(), liba->Path()))
	{
	case WOKBuilder_Success:
	  {
	    Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(liba->LocatorName(), liba, 
									new WOKBuilder_ArchiveLibrary(liba->Path()), liba->Path());
	    outfile->SetLocateFlag(Standard_True);
	    outfile->SetProduction();
	    AddExecDepItem(infile, outfile, Standard_True);
	  }
	  break;
	case WOKBuilder_Failed:
	  break;
	default:
	  break;
	}
    }
  SetSucceeded();
}
