// File:	WOKOrbix_IDLSourceExtract.cxx
// Created:	Mon Aug 25 19:25:55 1997
// Author:	Jean GAUTIER
//		<jga@hourax.paris1.matra-dtv.fr>


#include <WOKTools_Messages.hxx>

#include <WOKUtils_Extension.hxx>
#include <WOKUtils_Path.hxx>


#include <WOKernel_Locator.hxx>
#include <WOKernel_File.hxx>

#include <WOKMake_OutputFile.hxx>
#include <WOKMake_AdmFileTypes.hxx>

#include <WOKOrbix_IDLSourceExtract.ixx>

//=======================================================================
//function : WOKOrbix_IDLSourceExtract
//purpose  : 
//=======================================================================
WOKOrbix_IDLSourceExtract::WOKOrbix_IDLSourceExtract(const Handle(WOKMake_BuildProcess)& abp,
						     const Handle(WOKernel_DevUnit)& aunit,
						     const Handle(TCollection_HAsciiString)& acode,
						     const Standard_Boolean checked,const Standard_Boolean hidden)
  : WOKMake_Step(abp,aunit,acode,checked,hidden)
{
}

//=======================================================================
//function : HandleInputFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKOrbix_IDLSourceExtract::HandleInputFile(const Handle(WOKMake_InputFile)& infile) 
{
  if(!infile->File().IsNull())
    {
      if(infile->File()->Path()->Extension() == WOKUtils_TemplateFile)
	{
	  infile->SetDirectFlag(Standard_True);
	  return Standard_True;
	}
    } 
  return Standard_False;
}

//=======================================================================
//function : AdmFileType
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKOrbix_IDLSourceExtract::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)ADMFILE);
  return result;   
}

//=======================================================================
//function : OutputDirTypeName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKOrbix_IDLSourceExtract::OutputDirTypeName() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)TMPDIR);
  return result;   
}

//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
void WOKOrbix_IDLSourceExtract::Execute(const Handle(WOKMake_HSequenceOfInputFile)& execlist) 
{
  Handle(TCollection_HAsciiString) srctype = new TCollection_HAsciiString("source");

  for(Standard_Integer i=1; i<=execlist->Length(); i++)
    {
      const Handle(WOKMake_InputFile)& infile = execlist->Value(i);

      Handle(TCollection_HAsciiString) filename = new TCollection_HAsciiString(infile->File()->Path()->FileName());

      Standard_Integer apos = filename->Search(".template");

      if(apos>0)
	{
	  filename->Trunc(apos-1);

	  Handle(WOKernel_File) srcfile = Locator()->Locate(Unit()->Name(), srctype, filename);

	  if(srcfile.IsNull())
	    {
	      WarningMsg() << "WOKOrbix_IDLSourceExtract::Execute" 
			 << "Extraction deducted source file " << filename << " is missing" << endm;
	      srcfile = new WOKernel_File(filename, Unit(), Unit()->GetFileType(srctype));
	      srcfile->GetPath();
	    }
	  
	  Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(srcfile->LocatorName(), srcfile,
								      Handle(WOKBuilder_Entity)(), srcfile->Path());
	  outfile->SetLocateFlag(Standard_True);
	  outfile->SetProduction();

	  AddExecDepItem(infile, outfile, Standard_True);
	}
      else
	{
	  ErrorMsg() << "WOKOrbix_IDLSourceExtract::Execute" 
		   << "Invalid extension for " << filename << " should be .template" << endm;
	  SetFailed();
	}
    }

  if(Status() == WOKMake_Unprocessed) SetSucceeded();
}

