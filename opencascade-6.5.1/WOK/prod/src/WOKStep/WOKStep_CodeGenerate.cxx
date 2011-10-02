// File:	WOKStep_CodeGenerate.cxx
// Created:	Thu Jul 11 23:24:17 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>

#include <TCollection_HAsciiString.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKUtils_Param.hxx>
#include <WOKUtils_Shell.hxx>

#include <WOKernel_FileTypeBase.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_HSequenceOfFile.hxx>
#include <WOKernel_Locator.hxx>

#include <WOKBuilder_CodeGenFile.hxx>
#include <WOKBuilder_CodeGenerator.hxx>
#include <WOKBuilder_CodeGeneratorIterator.hxx>
#include <WOKBuilder_HSequenceOfToolInShell.hxx>
#include <WOKBuilder_HSequenceOfEntity.hxx>

#include <WOKMake_InputFile.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_HSequenceOfStepOption.hxx>
#include <WOKMake_AdmFileTypes.hxx>

#include <WOKStep_CodeGenerate.ixx>



//=======================================================================
//function : WOKStep_CodeGenerate
//purpose  : 
//=======================================================================
WOKStep_CodeGenerate::WOKStep_CodeGenerate(const Handle(WOKMake_BuildProcess)& abp,
					   const Handle(WOKernel_DevUnit)& aunit, 
					   const Handle(TCollection_HAsciiString)& acode, 
					   const Standard_Boolean checked, 
					   const Standard_Boolean hidden) 
: WOKStep_ProcessStep(abp, aunit, acode, checked, hidden), 
  myiterator(new TCollection_HAsciiString("CODEGEN"), aunit->Params())
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AdmFileType
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_CodeGenerate::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)ADMFILE);
  return result;   
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : OutputDirTypeName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_CodeGenerate::OutputDirTypeName() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)TMPDIR);
  return result;   
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Init
//purpose  : 
//=======================================================================
void WOKStep_CodeGenerate::Init()
{
  if(IsToExecute())
    {
      Handle(TCollection_HAsciiString) optline;
      
      if(myiterator.LoadGroup())
	{
	  ErrorMsg() << "WOKStep_CodeGenerate::Init"
	    << "Could not load code generators definition" << endm;
	  SetFailed();
	  return;
	}
   }
}

//=======================================================================
//function : HandleInputFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKStep_CodeGenerate::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
{
  Handle(WOKBuilder_Entity) result;
  Handle(WOKUtils_Path)     apath;

  if(!infile->File().IsNull())
    {
      apath = infile->File()->Path();
    }
  else if(!infile->LastPath().IsNull())
    {
      apath = infile->LastPath();
    }

  if(!apath.IsNull())
    {
      if(myiterator.IsTreatedExtension(apath->ExtensionName()))
	{
	  result = new WOKBuilder_CodeGenFile(apath);
	}
      else
	return Standard_False;
      
      infile->SetBuilderEntity(result);
      infile->SetDirectFlag(Standard_True);
      return Standard_True;
    }  
  return Standard_False;
}

//=======================================================================
//function : OutOfDateEntities
//purpose  : 
//=======================================================================
Handle(WOKMake_HSequenceOfInputFile) WOKStep_CodeGenerate::OutOfDateEntities()
{
  return WOKMake_Step::OutOfDateEntities();
}

//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
void WOKStep_CodeGenerate::Execute(const Handle(WOKMake_HSequenceOfInputFile)& execlist)
{
  Standard_Integer i,j;
  Handle(WOKMake_InputFile) infile;
  Handle(WOKMake_OutputFile) outfile;
  Handle(WOKMake_HSequenceOfInputFile) fails = new WOKMake_HSequenceOfInputFile;
  Handle(WOKMake_HSequenceOfInputFile) succeeds = new WOKMake_HSequenceOfInputFile;
  Handle(WOKernel_FileType) drvfile    = Unit()->FileTypeBase()->Type("drvfile");
  Handle(WOKernel_FileType) pubinclude = Unit()->FileTypeBase()->Type("pubinclude");
  Handle(WOKernel_FileType) source     = Unit()->FileTypeBase()->Type("source");
  Handle(WOKernel_File) aoutfile;


  // Obtention d'un shell
  Handle(WOKUtils_Shell) ashell = Shell();

  ashell->Lock();

  myiterator.Init(ashell, OutputDir());

  for(j=1; j<=execlist->Length(); j++)
    {
      infile = execlist->Value(j);
      
      Handle(WOKBuilder_CodeGenFile) codegen = Handle(WOKBuilder_CodeGenFile)::DownCast(infile->BuilderEntity());
      
      if(infile->File()->Nesting()->IsSameString(Unit()->FullName()))
	{
	  InfoMsg() << "WOKStep_CodeGenerate::Execute" << "-------> " << infile->File()->Name() << endm;
	}
      else
	{
	  InfoMsg() << "WOKStep_CodeGenerate::Execute" << "-------> " << infile->File()->UserPathName() << endm;
	}
      
      switch(myiterator.Execute(codegen))
	{
	case WOKBuilder_Success:
	  WOK_TRACE {
	    if(VerboseMsg()("WOK_CODEGEN").IsSet())
	      {
		VerboseMsg() << "WOKStep_CodeGenerate::Execute" 
			   << codegen->Path()->Name() << " produces : " << endm;

		for(i=1; i<=myiterator.Produces()->Length(); i++)
		  {
		    VerboseMsg() << "WOKStep_CodeGenerate::Execute"
			       << "\t\t" << myiterator.Produces()->Value(i)->Path()->Name() << endm;
		  }
	      }
	  }
	  
	  for(i=1; i<=myiterator.Produces()->Length(); i++)
	    {
	      Handle(WOKBuilder_Entity) outent = myiterator.Produces()->Value(i);
	      aoutfile.Nullify();
	      
	      switch(outent->Path()->Extension())
		{
		case WOKUtils_CFile:
		case WOKUtils_CXXFile:
		case WOKUtils_F77File:
		  aoutfile = new WOKernel_File(outent->Path()->FileName(), Unit(), drvfile );
		  break;
		case WOKUtils_HFile:
		case WOKUtils_HXXFile:
		case WOKUtils_INCFile:
		  aoutfile = new WOKernel_File(outent->Path()->FileName(), Unit(), pubinclude);
		  break;
		case WOKUtils_TemplateFile:
		  aoutfile = new WOKernel_File(outent->Path()->FileName(), Unit(), source);
		default:
		  break;
		}
	      
	      if(aoutfile.IsNull())
		{
		  ErrorMsg() << "WOKStep_CodeGenerate::Execute" 
			   << "Unrecognized file : " << outent->Path()->Name() << endm;
		}
	      else
		{
		  // je calcule le path de destination du file
		  aoutfile->GetPath();
		  
		  // je l'y deplace
		  outent->Path()->MoveTo(aoutfile->Path());
		  
		  outfile = new WOKMake_OutputFile(aoutfile->LocatorName(), aoutfile, outent, aoutfile->Path());
		  outfile->SetLocateFlag(Standard_True);
		  outfile->SetProduction();
		  
		  AddExecDepItem(infile, outfile, Standard_True);
		}
	    }
	  succeeds->Append(infile);
	  break;
	case WOKBuilder_Failed:
	  fails->Append(infile);
	  ErrorMsg() << "WOKStep_CodeGenerate::Execute" << "Failed    : " << infile->File()->Name() << endm;           
	  break;
        default: break;
	}
    }

  ashell->UnLock();

  if(execlist->Length() == 0)
    {
      SetUptodate();
      return;
    }
  
  if(fails->Length())
    {
      InfoMsg() << "WOKStep_CodeGenerate::Execute" 
	      << "----------------------- Compilation Report -----------------------" << endm;

      for(i=1; i<= fails->Length(); i++)
	{
	  InfoMsg() << "WOKStep_CodeGenerate::Execute" 
		  << "Failed : " << fails->Value(i)->File()->UserPathName() << endm;
	}
       InfoMsg() << "WOKStep_CodeGenerate::Execute" 
	       << "-----------------------------------------------------------------" << endm;
    }

  if(fails->Length() && succeeds->Length())
    {
      SetIncomplete();
      return;
    }
  if(fails->Length())
    {
      SetFailed();
      return;
    }
  SetSucceeded();
  return;
}


