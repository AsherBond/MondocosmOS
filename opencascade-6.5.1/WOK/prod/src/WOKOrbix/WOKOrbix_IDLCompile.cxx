// File:	WOKOrbix_IDLCompile.cxx
// Created:	Mon Aug 18 16:43:12 1997
// Author:	Jean GAUTIER
//		<jga@hourax.paris1.matra-dtv.fr>
#ifdef HAVE_CONFIG_H
# include <config.h>
#endif

#ifdef WNT
#include <io.h>
#endif

#include <Standard_Stream.hxx>

#ifdef HAVE_UNISTD_H
# include <unistd.h>
#endif

#include <TCollection_HAsciiString.hxx>

#include <TColStd_HArray2OfInteger.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Messages.hxx>
#include <WOKTools_MapOfHAsciiString.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKUtils_Param.hxx>
#include <WOKUtils_Shell.hxx>

#ifndef WNT
# include <WOKUtils_RemoteShell.hxx>
#else
# include <WOKUtils_Shell.hxx>
# include <WOKNT_WNT_BREAK.hxx>
#endif  // WNT

#include <WOKernel_BasicUnitTypes.hxx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_Session.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_FileType.hxx>
#include <WOKernel_FileTypeBase.hxx>
#include <WOKernel_UnitNesting.hxx>
#include <WOKernel_Workbench.hxx>
#include <WOKernel_HSequenceOfFile.hxx>
#include <WOKernel_Locator.hxx>


#include <WOKBuilder_HSequenceOfToolInShell.hxx>
#include <WOKBuilder_HSequenceOfEntity.hxx>

#include <WOKMake_InputFile.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_HSequenceOfStepOption.hxx>
#include <WOKMake_AdmFileTypes.hxx>

#include <WOKOrbix_IDLFile.hxx>
#include <WOKOrbix_IDLCompiler.hxx>
#include <WOKOrbix_IDLCompilerIterator.hxx>

#include <WOKOrbix_IDLCompile.ixx>

//=======================================================================
//function : WOKOrbix_IDLCompile
//purpose  : 
//=======================================================================
WOKOrbix_IDLCompile::WOKOrbix_IDLCompile(const Handle(WOKMake_BuildProcess)& abp,
				 const Handle(WOKernel_DevUnit)& aunit, 
				 const Handle(TCollection_HAsciiString)& acode, 
				 const Standard_Boolean checked, 
				 const Standard_Boolean hidden) 
: WOKStep_ProcessStep(abp,aunit, acode, checked, hidden), 
  myiterator(new TCollection_HAsciiString("ORBIX"), aunit->Params())
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AdmFileType
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKOrbix_IDLCompile::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)DBADMFILE);
  return result;   
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : OutputDirTypeName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKOrbix_IDLCompile::OutputDirTypeName() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)DBTMPDIR);
  return result;   
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Init
//purpose  : 
//=======================================================================
void WOKOrbix_IDLCompile::Init()
{
  if(IsToExecute())
    {
      WOKStep_ProcessStep::Init();

      if(myiterator.LoadGroup())
	{
	  ErrorMsg() << "WOKOrbix_IDLCompile::Init"
		   << "Could not load idl compilers definition" << endm;
	  SetFailed();
	  return;
	}
    }
}

//=======================================================================
//function : HandleInputFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKOrbix_IDLCompile::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
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
	  result = new WOKOrbix_IDLFile(apath);
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
//function : Execute
//purpose  : 
//=======================================================================
void WOKOrbix_IDLCompile::Execute(const Handle(WOKMake_HSequenceOfInputFile)& execlist)
{
  Standard_Integer i,j;
  Handle(WOKMake_InputFile) infile;
  Handle(WOKMake_OutputFile) outfile;
  Handle(WOKMake_HSequenceOfInputFile) fails = new WOKMake_HSequenceOfInputFile;
  Handle(WOKMake_HSequenceOfInputFile) succeeds = new WOKMake_HSequenceOfInputFile;
  
  Handle(WOKernel_FileType) srctype = Unit()->FileTypeBase()->Type("source");
  Handle(WOKernel_FileType) inctype = Unit()->FileTypeBase()->Type("pubinclude");
  Handle(WOKernel_FileType) drvtype = Unit()->FileTypeBase()->Type("derivated");
  Handle(WOKernel_File) aoutfile;

  Handle(WOKUtils_HSequenceOfPath) incdirs = ComputeIncDirectories();

  // Set du debug mode
  // Obtention d'un shell
  Handle(WOKUtils_Shell) ashell = Shell();
  ashell->Lock();

  myiterator.Init(ashell, OutputDir(), incdirs);

  for(j=1; j<=execlist->Length(); j++)
    {
#ifdef WNT
_TEST_BREAK();
#endif  // WNT

      infile = execlist->Value(j);
      
      Handle(WOKOrbix_IDLFile) anidlfile = Handle(WOKOrbix_IDLFile)::DownCast(infile->BuilderEntity());
      
      if(infile->File()->Nesting()->IsSameString(Unit()->FullName()))
	{
	  InfoMsg() << "WOKOrbix_IDLCompile::Execute" << "-------> " << infile->File()->Name() << endm;
	}
      else
	{
	  InfoMsg() << "WOKOrbix_IDLCompile::Execute" << "-------> " << infile->File()->UserPathName() << endm;
	}
      
      switch(myiterator.Execute(anidlfile))
	{
	case WOKBuilder_Success:
	  {
	    WOK_TRACE {
	      if(VerboseMsg()("WOK_ORBIX").IsSet())
		{
		  VerboseMsg() << "WOKOrbix_IDLCompile::Execute" 
			     << anidlfile->Path()->Name() << " produces : " << endm;
		  for(i=1; i<=myiterator.Produces()->Length(); i++)
		    {
		      VerboseMsg() << "WOKOrbix_IDLCompile::Execute"
				 << "\t\t" << myiterator.Produces()->Value(i)->Path()->Name() << endm;
		    }
		}
	    }

	    TreatOutput(infile, myiterator.Produces());

	    succeeds->Append(infile);
	  }
	  break;
	case WOKBuilder_Failed:
	  fails->Append(infile);
	  ErrorMsg() << "WOKOrbix_IDLCompile::Execute" << "Failed    : " << infile->File()->Name() << endm;           
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
      InfoMsg() << "WOKOrbix_IDLCompile::Execute" 
	      << "----------------------- IDL Compilation Report -----------------------" << endm;

      for(i=1; i<= fails->Length(); i++)
	{
	  InfoMsg() << "WOKOrbix_IDLCompile::Execute" 
		  << "Failed : " << fails->Value(i)->File()->UserPathName() << endm;
	}
       InfoMsg() << "WOKOrbix_IDLCompile::Execute" 
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


