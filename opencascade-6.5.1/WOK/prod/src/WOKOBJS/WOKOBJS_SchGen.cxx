// File:	WOKOBJS_SchGen.cxx
// Created:	Mon Feb 24 16:56:12 1997
// Author:	Jean GAUTIER
//		<jga@hourax.paris1.matra-dtv.fr>


#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKUtils_Extension.hxx>
#include <WOKUtils_Path.hxx>
#include <WOKUtils_Shell.hxx>

#include <WOKBuilder_Compilable.hxx>
#include <WOKBuilder_HSequenceOfEntity.hxx>
#include <WOKBuilder_Command.hxx>

#include <WOKernel_FileTypeKeyWords.hxx>
#include <WOKernel_UnitNesting.hxx>
#include <WOKernel_Workbench.hxx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_Session.hxx>
#include <WOKernel_Locator.hxx>

#include <WOKMake_AdmFileTypes.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_InputFile.hxx>

#include <WOKOBJS_AppSchema.hxx>
#include <WOKOBJS_OSSG.hxx>

#include <WOKOBJS_SchGen.ixx>

//=======================================================================
//Author   : Jean Gautier (jga)
//function : WOKOBJS_SchGen
//purpose  : 
//=======================================================================
WOKOBJS_SchGen::WOKOBJS_SchGen(const Handle(WOKMake_BuildProcess)& abp, const Handle(WOKernel_DevUnit)& aunit,const Handle(TCollection_HAsciiString)& acode,
			       const Standard_Boolean checked,const Standard_Boolean hidden)
: WOKMake_Step(abp, aunit, acode, checked, hidden)
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AdmFileType
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKOBJS_SchGen::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STADMFILE);
  return result;   
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : OutputDirTypeName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKOBJS_SchGen::OutputDirTypeName() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STTMPDIR);
  return result; 
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : HandleInputFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKOBJS_SchGen::HandleInputFile(const Handle(WOKMake_InputFile)& infile) 
{
  Handle(WOKBuilder_Entity) result;
  Handle(WOKUtils_Path)     apath;

  if(!infile->File().IsNull())
    {
      apath = infile->File()->Path();
      switch(apath->Extension())
	{
	case WOKUtils_CXXFile:          result = new WOKBuilder_Compilable(apath);   break;
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
//function : OutOfDateEntities
//purpose  : 
//=======================================================================
Handle(WOKMake_HSequenceOfInputFile) WOKOBJS_SchGen::OutOfDateEntities() 
{
  return ForceBuild();
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : ComputeIncDirectories
//purpose  : 
//=======================================================================
Handle(WOKUtils_HSequenceOfPath) WOKOBJS_SchGen::ComputeIncDirectories() const
{
  Handle(TColStd_HSequenceOfHAsciiString) nestingseq = Unit()->Session()->GetWorkbench(Unit()->Nesting())->Visibility();
  Handle(TCollection_HAsciiString) aname;
  Handle(WOKUtils_HSequenceOfPath) aseq = new WOKUtils_HSequenceOfPath;
  Handle(WOKernel_File)     afile;
  Handle(WOKernel_FileType) atype;
  Handle(WOKernel_DevUnit)  aunit;
  Handle(TCollection_HAsciiString) DOT = new TCollection_HAsciiString(".");
  Standard_Integer i;

  for(i=1; i<=nestingseq->Length(); i++)
    {
      // l'ud est-elle dans ce nesting
      Handle(WOKernel_UnitNesting) thenesting = Unit()->Session()->GetUnitNesting(nestingseq->Value(i));
      aname = thenesting->NestedUniqueName(Unit()->Name());

      if(Unit()->Session()->IsKnownEntity(aname))
	{
	  aunit = Unit()->Session()->GetDevUnit(aname);
	  
	  Handle(WOKernel_UnitNesting) anesting = Unit()->Session()->GetUnitNesting(aunit->Nesting());
	  if(anesting->IsKind(STANDARD_TYPE(WOKernel_Workbench)))
	    {
	      atype = aunit->GetFileType("userinclude");

	      afile = new WOKernel_File(DOT, aunit, atype);
	      afile->GetPath();
	      
	      aseq->Append(afile->Path());

	      atype = aunit->GetFileType("privinclude");
	      afile = new WOKernel_File(DOT, aunit, atype);
	      afile->GetPath();
	      aseq->Append(afile->Path());
	    }
	}

      //nesting de la visibilite
      atype = thenesting->GetFileType("pubincdir");
      afile = new WOKernel_File(DOT, thenesting, atype);
      afile->GetPath();
      aseq->Append(afile->Path());

    }
  return aseq;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Execute
//purpose  : 
//=======================================================================
void WOKOBJS_SchGen::Execute(const Handle(WOKMake_HSequenceOfInputFile)& execlist) 
{
  Standard_Integer i,j;
  Handle(TCollection_HAsciiString) aname    = Unit()->Name();

  // Used Types
  Handle(WOKernel_FileType) libraryfile      = Unit()->GetFileType("library");
  Handle(WOKernel_FileType) schcxxfile       = Unit()->GetFileType("schcxxfile");

  // Obtention d'un shell
  Handle(WOKUtils_Shell) ashell = Shell();
  ashell->Lock();

  // Commande
  Handle(WOKBuilder_Command) acmd   = new WOKBuilder_Command(new TCollection_HAsciiString("COMMAND"), Unit()->Params());
  acmd->SetShell(ashell);

  // Verification du profile DB
  if( Unit()->Session()->DBMSystem() != WOKernel_OBJS )
    {
      ErrorMsg() << "WOKOBJS_SchGen::Execute"
	       << "Wrong profile for this step : Only OBJS is valid" << endm;
      SetFailed();
      return;
    }

  // La FDDB
  Handle(WOKernel_File) appsch = new WOKernel_File(WOKOBJS_AppSchema::GetAppFileName(Unit()->Params(), Unit()->Name()),
						   Unit(),
						   Unit()->GetFileType("library"));
  appsch->GetPath();
  
  // les repertoires d'includes
  Handle(WOKUtils_HSequenceOfPath) incdirs = ComputeIncDirectories();

  // Obtention du processeur OSSG
  Handle(WOKOBJS_OSSG) ossg = new WOKOBJS_OSSG(new TCollection_HAsciiString("OBJS"), 
					       Unit()->Params());
  ossg->SetOutputDir(OutputDir());
  ossg->SetIncludeDirectories(incdirs);
  ossg->SetAppSchema(Unit()->Name());
  ossg->SetTargetDir(new WOKUtils_Path(appsch->Path()->DirName()));
  ossg->SetShell(ashell);
  
  // Obtention de la liste des classes
  Handle(WOKernel_DevUnit) aunit;
  Handle(WOKernel_File)    ddlfile;

  Handle(WOKMake_InputFile)   infile;

  for(i=1; i<=execlist->Length(); i++)
    {
      infile = execlist->Value(i);
      
      ossg->SetSchFile(new WOKBuilder_Compilable(infile->File()->Path()));
				
      InfoMsg() << "WOKOBJS_DDLFile" 
	      << "Extracting " << Unit()->Name() << endm;
			
      switch(ossg->Execute())
       {
	case WOKBuilder_Success:
	  {
	    WOK_TRACE {
	      if(VerboseMsg()("WOK_OBJS").IsSet())
		{
		  Standard_Integer i;
		  VerboseMsg() << "WOKOBJS_SchGen::Execute" 
			     << infile->File()->Name() << " produces : " << endm;
		  for(i=1; i<=ossg->Produces()->Length(); i++)
		    {
		      VerboseMsg() << "WOKOBJS_SchGen::Execute" 
				 << "\t\t" << ossg->Produces()->Value(i)->Path()->Name() << endm;
		    }
		}
	    }
	  
	    for(j=1; j<=ossg->Produces()->Length(); j++)
	      {
		Handle(WOKBuilder_Entity) anent = ossg->Produces()->Value(j);
		Handle(WOKernel_File) outfile;
		Handle(WOKernel_File) basefile;

		switch(anent->Path()->Extension())
		  {
		  case WOKUtils_CXXFile:
		    // Devivated Cxx file
		    outfile = new WOKernel_File(anent->Path()->FileName(), Unit(), schcxxfile);
		    break;
		  case WOKUtils_AppSchemaFile:
		    // Asdb file
		    outfile = new WOKernel_File(anent->Path()->FileName(), Unit(), libraryfile);
		    break;
		  default:
		    ErrorMsg() << "WOKOBJS_SchGen::Execute"
			     << "Unrecognized output file : " << anent->Path()->FileName() << endm;
		    break;
		  }

		outfile->GetPath();

		if(anent->Path()->Extension() != WOKUtils_AppSchemaFile)
		  {
		  
		    basefile = Locator()->Locate(Unit()->Name(), outfile->TypeName(), outfile->Name());
		  }
		else
		  {
		    basefile.Nullify();
		  }
	      
		WOKBuilder_BuildStatus astatus;
		      
		if(basefile.IsNull())
		  {
		    // pas encore de Fichier : Simply Move
		    astatus = acmd->Move(anent->Path(), outfile->Path());
		  }
		else
		  {
		    astatus = acmd->ReplaceIfChangedWith(anent->Path(), basefile->Path(), outfile->Path());
		  }
	      
		switch(astatus)
		  {
		  case WOKBuilder_Success:
		    {
		      anent->SetPath(outfile->Path());
		      Handle(WOKMake_OutputFile) out = new WOKMake_OutputFile(outfile->LocatorName(), outfile, anent, outfile->Path());
		      out->SetLocateFlag(Standard_True);
		      out->SetProduction();
		      AddExecDepItem(execlist->Value(i), out, Standard_True);
		      
		      InfoMsg()  << "WOKOBJS_SchGen::Execute" << "File : " << outfile->Path()->Name() << " is modified" << endm;
		    }
		    break;
		  case WOKBuilder_Unbuilt:
		    WOK_TRACE {
		      VerboseMsg()("WOK_OBJS")  << "WOKMake_Extract::Execute" 
					      << "File : " << outfile->Path()->Name() << " is unchanged" << endm;
		    }
		    {
		      anent->SetPath(basefile->Path());
		      Handle(WOKMake_OutputFile) out = new WOKMake_OutputFile(basefile->LocatorName(), basefile, anent, basefile->Path());
		      out->SetLocateFlag(Standard_True);
		      out->SetProduction();
		      AddExecDepItem(execlist->Value(i), out, Standard_True);
		    }
		    break;
		  case WOKBuilder_Failed:
		    SetFailed();
		    ErrorMsg() << "WOKOBJS_SchGen::Execute" << "Failed    : " << outfile->Name() << endm;
		    break;
		  }
	      }
	  }
	  break;
	case WOKBuilder_Failed:
	  ErrorMsg() << "WOKOBJS_SchGen::Execute" << "Failed    : " << Unit()->Name() << endm;          
	  SetFailed();
	  break;
         default: break;
	}
    }
  ashell->UnLock();
  SetSucceeded();
  return;
}

