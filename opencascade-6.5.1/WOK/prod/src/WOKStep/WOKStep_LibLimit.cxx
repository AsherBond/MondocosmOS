// File:	WOKStep_LibLimit.cxx
// Created:	Thu Jan  9 14:57:06 1997
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>




#include <WOKTools_Messages.hxx>
#include <WOKTools_MapOfHAsciiString.hxx>
#include <WOKTools_MapIteratorOfMapOfHAsciiString.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKUtils_Extension.hxx>
#include <WOKUtils_Shell.hxx>

#include <WOKernel_Session.hxx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_FileType.hxx>

#include <WOKBuilder_HSequenceOfEntity.hxx>
#include <WOKBuilder_Command.hxx>
#include <WOKBuilder_ArchiveExtract.hxx>
#include <WOKBuilder_ObjectFile.hxx>
#ifndef WNT
#include <WOKBuilder_ArchiveLibrary.hxx>
#include <WOKBuilder_SharedLibrary.hxx>
#else
#include <WOKBuilder_StaticLibrary.hxx>
#include <WOKBuilder_ImportLibrary.hxx>
#endif
#include <WOKBuilder_Miscellaneous.hxx>

#include <WOKMake_InputFile.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_AdmFileTypes.hxx>

#include <WOKStep_LibLimit.ixx>




//=======================================================================
//Author   : Jean Gautier (jga)
//function : WOKStep_LibLimit
//purpose  : 
//=======================================================================
WOKStep_LibLimit::WOKStep_LibLimit(const Handle(WOKMake_BuildProcess)& abp,
				   const Handle(WOKernel_DevUnit)& aunit, 
				   const Handle(TCollection_HAsciiString)& acode, 
				   const Standard_Boolean checked, 
				   const Standard_Boolean hidden) 
  : WOKMake_MetaStep(abp,aunit, acode, checked, hidden)
{
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : AdmFileType
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_LibLimit::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STADMFILE);
  return result;   
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : OutputDirTypeName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_LibLimit::OutputDirTypeName() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STTMPDIR);
  return result;   
}

//=======================================================================
//function : HandleInputFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKStep_LibLimit::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
{
  Handle(WOKBuilder_Entity) result;
  Handle(WOKUtils_Path)     apath;

  if(!infile->File().IsNull())
    {
      apath = infile->File()->Path();
      switch(apath->Extension())
	{
	case WOKUtils_ObjectFile:  
	  {
	    if(SubCode().IsNull())
	      result = new WOKBuilder_ObjectFile(apath);
	    else
	      {
		Handle(WOKernel_DevUnit) entity = Unit()->Session()->GetDevUnit(infile->File()->Nesting());
		if(entity->Name()->IsSameString(SubCode()))
		  result = new WOKBuilder_ObjectFile(apath);
		else
		  return Standard_False;
	      }
	  }
	break;
#ifndef WNT
	case WOKUtils_ArchiveFile: result = new WOKBuilder_ArchiveLibrary(apath); break;
	case WOKUtils_DSOFile:     result = new WOKBuilder_SharedLibrary(apath);  break;
#else
	case WOKUtils_LIBFile: 	   result = new WOKBuilder_StaticLibrary(apath); break;
	case WOKUtils_IMPFile:	   result = new WOKBuilder_ImportLibrary(apath); break;
#endif // WNT
	default:  
	  break;
	}
      
      if(result.IsNull())
	{
	  if(!strcmp(apath->ExtensionName()->ToCString(), ".ImplDep"))
	    {
	      result = new WOKBuilder_Miscellaneous(apath);
	    }
	}
      if(!result.IsNull())
	{
	  infile->SetBuilderEntity(result);
	  infile->SetDirectFlag(Standard_True);
	  return Standard_True;
	}
    }  
  return Standard_False;
}

//=======================================================================
//function : OutOfDateEntities
//purpose  : 
//=======================================================================
Handle(WOKMake_HSequenceOfInputFile) WOKStep_LibLimit::OutOfDateEntities()
{
  return ForceBuild();
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Execute
//purpose  : 
//=======================================================================
void WOKStep_LibLimit::Execute(const Handle(WOKMake_HSequenceOfInputFile)& execlist)
{
  Handle(TCollection_HAsciiString) anbstr = Unit()->Params().Eval("%LDSHR_LibLimit",Standard_True);
  Standard_Integer limit = 0, i, j;

  if(anbstr.IsNull())  
    {
      limit = 0;
    } 
  else
    {
    if(anbstr->IsIntegerValue()) 
      {
	limit = anbstr->IntegerValue();
      }
    }

  if(SubCode().IsNull())
    {
      Standard_Boolean ok = Standard_True;

      if(limit && (limit < execlist->Length()))
	{
	  WOKTools_MapOfHAsciiString umap;
	  Standard_Integer i;

	  for(i=1; i<=execlist->Length(); i++)
	    {
	      const Handle(WOKMake_InputFile)& infile = execlist->Value(i);
	      
	      if(infile->BuilderEntity()->IsKind(STANDARD_TYPE(WOKBuilder_ObjectFile)) && infile->IsLocateAble())
		{
		  if(infile->IsLocateAble())
		    {
		      Handle(WOKernel_DevUnit) entity = Unit()->Session()->GetDevUnit(infile->File()->Nesting());
		      
		      if(!entity.IsNull())
			{
			  if(!umap.Contains(entity->Name()))
			    {
			      umap.Add(entity->Name());
			    }
			}
		    }
		}
	      else
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
	    }

	  WOKTools_MapIteratorOfMapOfHAsciiString it(umap);

	  for(;it.More();it.Next())
	    {
	      Handle(TCollection_HAsciiString) part = it.Key();
	      
	      Handle(TCollection_HAsciiString) id = WOKMake_Step::StepOutputID(Unit()->Name(),
									       Code(),
									       part);
	      
	      Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(id, Handle(WOKernel_File)(), 
									  Handle(WOKBuilder_Entity)(), Handle(WOKUtils_Path)());
	      outfile->SetProduction();
	      outfile->SetLocateFlag(Standard_True);
	      outfile->SetPhysicFlag(Standard_False);
	      outfile->SetStepID(Standard_True);
	      
//              cout << "WOKStep_LibLimit::Execute -> GetAndAddStep" << endl ;
	      Handle(WOKMake_Step) astep = BuildProcess()->GetAndAddStep(Unit(), Code(), it.Key());
	      
	      astep->DoExecute();
	      astep->SetTargets(Targets());
	      astep->SetOptions(Options());
	      
	      switch(astep->Make())
		{
		case WOKMake_Uptodate:
		  InfoMsg() << "WOKStep_LibLimit::Execute"
			  << "========> " << astep->SubCode() << " is uptodate" << endm;
		  break;
		case WOKMake_Success:
		  InfoMsg() << "WOKStep_LibLimit::Execute"
			  << "========> " << astep->SubCode() << " succeeded" << endm;
		  break;
		case WOKMake_Incomplete:
		  WarningMsg() << "WOKStep_LibLimit::Execute"
			     << "========> " << astep->SubCode() << " is incomplete" << endm;
		  break;
		case WOKMake_Failed:
		  ErrorMsg() << "WOKStep_LibLimit::Execute"
			   << "========> " << astep->SubCode() << " failed" << endm;
		  ok = Standard_False;
		  break;
		case WOKMake_Unprocessed:
		  WarningMsg() << "WOKStep_LibLimit::Execute"
			     << "========> " << astep->SubCode() << " is still unprocessed" << endm;
		  ok=Standard_False;
		  break;
                 default: break;
		}
	      Handle(TCollection_HAsciiString) Theid = WOKMake_Step::StepOutputID(Unit()->Name(),
									       astep->Code(),
									       astep->SubCode());
	      Handle(WOKMake_OutputFile) theoutfile = new WOKMake_OutputFile(Theid, Handle(WOKernel_File)(), 
									  Handle(WOKBuilder_Entity)(),
									  Handle(WOKUtils_Path)());
	      theoutfile->SetProduction();
	      theoutfile->SetLocateFlag(Standard_True);
	      theoutfile->SetPhysicFlag(Standard_False);
	      theoutfile->SetStepID(Standard_True);
	      
	      for(j=1; j<=execlist->Length(); j++)
		{
		  const Handle(WOKMake_InputFile)& infile = execlist->Value(j);
		  const Handle(WOKernel_DevUnit)&  entity = Unit()->Session()->GetDevUnit(infile->File()->Nesting());
		  if(entity->Name()->IsSameString(astep->SubCode()))
		    AddExecDepItem(infile,theoutfile, Standard_True);
		}
	    }
	}
      else
	{
	  InfoMsg() << "WOKStep_LibLimit::Execute"
		  << "No limitation required" << endm;
	  for( i=1; i<=execlist->Length(); i++)
	    {
	      const Handle(WOKMake_InputFile) infile = execlist->Value(i);
	      Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(infile);
	      AddExecDepItem(execlist->Value(i), outfile, Standard_True);
	    }
	}
      if(ok) SetSucceeded();
      else   SetFailed();
    }
  else
    {
      for( i=1; i<=execlist->Length(); i++)
	{
	  const Handle(WOKMake_InputFile) infile = execlist->Value(i);
	  Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(infile);
	  AddExecDepItem(execlist->Value(i), outfile, Standard_True);
	}
      SetSucceeded();
    }

}
