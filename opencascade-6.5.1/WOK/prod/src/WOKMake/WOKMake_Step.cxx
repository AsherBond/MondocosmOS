// File:	WOKMake_Step.cxx
// Created:	Wed Aug 23 14:46:05 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <TCollection_HAsciiString.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <OSD_SharedLibrary.hxx>

#include <WOKTools_Messages.hxx>
#include <WOKTools_MapOfHAsciiString.hxx>

#include <WOKUtils_AdmFile.hxx>
#include <WOKUtils_Path.hxx>
#include <WOKUtils_Param.hxx>
#include <WOKUtils_Shell.hxx>

#include <WOKernel_DBMSystem.hxx>
#include <WOKernel_Station.hxx>
#include <WOKernel_Entity.hxx>
#include <WOKernel_Session.hxx>
#include <WOKernel_UnitNesting.hxx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_FileType.hxx>
#include <WOKernel_HSequenceOfFile.hxx>
#include <WOKernel_FileTypeBase.hxx>

#include <WOKBuilder_Include.hxx>
#include <WOKBuilder_CDLFile.hxx>
#include <WOKBuilder_Compilable.hxx>
#include <WOKBuilder_CodeGenFile.hxx>
#include <WOKBuilder_Miscellaneous.hxx>
#include <WOKBuilder_ObjectFile.hxx>
#include <WOKBuilder_MFile.hxx>
#include <WOKBuilder_CompressedFile.hxx>
#include <WOKBuilder_ArchiveLibrary.hxx>
#include <WOKBuilder_SharedLibrary.hxx>
#include <WOKBuilder_Miscellaneous.hxx>
#include <WOKBuilder_TarFile.hxx>
#ifdef WNT
# include <WOKBuilder_StaticLibrary.hxx>
# include <WOKBuilder_ImportLibrary.hxx>
#endif  // WNT

#include <WOKMake_InputFile.hxx>
#include <WOKMake_DepItem.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_HSequenceOfStepOption.hxx>
#include <WOKMake_StepConstructPtr.hxx>
#include <WOKMake_MetaStep.hxx>
#include <WOKMake_StepBuilder.hxx>

#include <WOKMake_Step.ixx>
//---> EUG4YAN
Standard_EXPORT Standard_Boolean g_fCompOrLnk;
//<--- EUG4YAN

//=======================================================================
//function : WOKMake_Step
//purpose  : 
//=======================================================================
WOKMake_Step::WOKMake_Step(const Handle(WOKMake_BuildProcess)& aprocess,
			   const Handle(WOKernel_DevUnit)& aunit, 
			   const Handle(TCollection_HAsciiString)& acode,
			   const Standard_Boolean checked,
			   const Standard_Boolean hidden) 
: myunit(aunit),
  mycode(acode),
  myprocess(aprocess.operator->()),
  myinputcomp(Standard_False),
  mydeploaded(Standard_False),
  mystatus(WOKMake_Unprocessed),
  mycheck(checked),
  myhidden(hidden), 
  myexecflag(Standard_False)
{
}

//=======================================================================
//function : BuilderEntity
//purpose  : 
//=======================================================================
Handle(WOKBuilder_Entity) WOKMake_Step::BuilderEntity(const Handle(WOKernel_File)& afile) const
{
  Handle(WOKBuilder_Entity) result;
  if(!afile.IsNull())
    {
      return BuilderEntity(afile->Path());
    }
  return result;
}

//=======================================================================
//function : BuilderEntity
//purpose  : 
//=======================================================================
Handle(WOKBuilder_Entity) WOKMake_Step::BuilderEntity(const Handle(WOKUtils_Path)& apath) const
{
  Handle(WOKBuilder_Entity) result;
  if(!apath.IsNull())
    {
      switch(apath->Extension())
	{
	case WOKUtils_CFile:          return new WOKBuilder_Compilable(apath);
	case WOKUtils_HFile:          return new WOKBuilder_Include(apath);
	case WOKUtils_CDLFile:        return new WOKBuilder_CDLFile(apath);
	case WOKUtils_CXXFile:        return new WOKBuilder_Compilable(apath);
	case WOKUtils_HXXFile:        return new WOKBuilder_Include(apath);
	case WOKUtils_INCFile:        return new WOKBuilder_Include(apath);
	case WOKUtils_IXXFile:        return new WOKBuilder_Include(apath);
	case WOKUtils_JXXFile:        return new WOKBuilder_Include(apath);
	case WOKUtils_LXXFile:        return new WOKBuilder_Include(apath);
	case WOKUtils_GXXFile:        return new WOKBuilder_Include(apath);
	case WOKUtils_LexFile:        return new WOKBuilder_CodeGenFile(apath);
	case WOKUtils_YaccFile:       return new WOKBuilder_CodeGenFile(apath);
	case WOKUtils_PXXFile:        return new WOKBuilder_Include(apath);
	case WOKUtils_LWSFile:        return new WOKBuilder_CodeGenFile(apath);
	case WOKUtils_F77File:        return new WOKBuilder_Compilable(apath);
	case WOKUtils_PSWFile:        return new WOKBuilder_CodeGenFile(apath);
	case WOKUtils_CSHFile:        return new WOKBuilder_Miscellaneous(apath);
	case WOKUtils_ObjectFile:     return new WOKBuilder_ObjectFile(apath);
	case WOKUtils_MFile:	      return new WOKBuilder_MFile(apath);
	case WOKUtils_CompressedFile: return new WOKBuilder_CompressedFile(apath);
	case WOKUtils_ArchiveFile:    return new WOKBuilder_ArchiveLibrary(apath);
	case WOKUtils_DSOFile:        return new WOKBuilder_SharedLibrary(apath);
	case WOKUtils_DATFile: 	      return new WOKBuilder_Miscellaneous(apath);
	case WOKUtils_LispFile:	      return new WOKBuilder_Miscellaneous(apath);
	case WOKUtils_IconFile:       return new WOKBuilder_Miscellaneous(apath);
	case WOKUtils_TextFile:	      return new WOKBuilder_Miscellaneous(apath);
	case WOKUtils_TarFile:	      return new WOKBuilder_TarFile(apath);
	case WOKUtils_UnknownFile:    return new WOKBuilder_Miscellaneous(apath);
	case WOKUtils_NoExtFile:      return new WOKBuilder_Miscellaneous(apath);
#ifdef WNT
        case WOKUtils_LIBFile:        return new WOKBuilder_StaticLibrary ( apath );
        case WOKUtils_IMPFile:        return new WOKBuilder_ImportLibrary ( apath );
#endif  // WNT

	default:    	              return new WOKBuilder_Miscellaneous(apath);
	}
    }
  return result;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetInputFromStep
//purpose  : 
//=======================================================================
void WOKMake_Step::GetInputFromStep(const Handle(WOKMake_Step)& astep)
{
  Standard_Integer j;
  Handle(WOKMake_HSequenceOfOutputFile) out = astep->OutputFileList();

  WOK_TRACE {
    VerboseMsg()("WOK_MAKE") << "WOKMake_Step::GetInputFromStep" 
			   << "Compute Input Flow from step: " << astep->Unit()->Name() << ":" << astep->Code() << endm;
  }

  if(out.IsNull())
    {
      ErrorMsg() << "WOKMake_Step::GetInputFromStep"
	       << "Output file list of step (" << astep->Code() << ") is not available" << endm;
      ErrorMsg() << "WOKMake_Step::GetInputFromStep"
	       << "Please perform this step before using step : " << Code() << endm;
      SetFailed();
      return;
    }
      
  for(j=1; j<=out->Length(); j++)
    {
      const Handle(WOKMake_OutputFile)& outfile = out->Value(j);
      Handle(WOKMake_InputFile)         infile;

      if(!myinflow.Contains(outfile->ID()))
	{
	  if(!outfile->IsStepID() && outfile->IsLocateAble())
	    {
	      if(outfile->File().IsNull())
		{
		  if(!outfile->IsPhysic())
		    {
		      Handle(TCollection_HAsciiString) type = outfile->ID()->Token(":", 2);
		      if(!strcmp("msentity", type->ToCString()))
			{
			  Handle(TCollection_HAsciiString) name = outfile->ID()->Token(":", 3);
			  Handle(WOKernel_DevUnit) aunit = OutLocator()->LocateDevUnit(outfile->ID()->Token(":", 1));
			  
			  if(aunit.IsNull())
			    {
			      WarningMsg() << "WOKMake_Step::GetInputFromStep"
					 << "Skipping msentity " << name << " : unit " 
					 << outfile->ID()->Token(":", 1) << " not found" << endm;
			    }
			  else
			    {
			      infile = new WOKMake_InputFile(outfile->ID(), Handle(WOKernel_File)(), 
							     Handle(WOKBuilder_Entity)(), outfile->LastPath());
			      infile->SetLocateFlag(Standard_True);
			      infile->SetDirectFlag(Standard_True);
			      infile->SetPhysicFlag(Standard_False);
			    }
			}
		      else
			{
			  infile = new WOKMake_InputFile(outfile);
			  infile->SetDirectFlag(Standard_True);
			}
		    }
		  else
		    {
		      WarningMsg() << "WOKMake_Step::GetInputFromStep"
				 << "Skipping file " << outfile->LastPath()->Name() << " : not found" << endm;
		    }
		}
	      else
		{
		  infile = new WOKMake_InputFile(outfile);
		  infile->SetDirectFlag(Standard_True);
		}
	    }
	  else if(outfile->IsStepID())
	    {
	      Handle(WOKMake_MetaStep) meta = Handle(WOKMake_MetaStep)::DownCast(this);

	      if(!meta.IsNull())
		{
		  // In MetaSteps keep identical
		  infile = new WOKMake_InputFile(outfile);
		  infile->SetPhysicFlag(Standard_False);
		  infile->SetDirectFlag(Standard_True);
		}
	      else
		{
		  // in other ones expand underlying steps output
		  Handle(WOKMake_Step) ustep = myprocess->Find(outfile->ID());
		  if(!ustep.IsNull()) 
		    {
		      GetInputFromStep(ustep);
		    }
		}
	    }
	  if(!outfile->IsLocateAble())
	    {
	      infile = new WOKMake_InputFile(outfile);
	      infile->SetDirectFlag(Standard_True);
	    }
	}

      if(!infile.IsNull())
	{
	  if(HandleInputFile(infile))
	    {
	      myinflow.Add(infile->ID(), infile);
	    }
	}
    }
}

//=======================================================================
//function : GetInputFlow
//purpose  : 
//=======================================================================
void WOKMake_Step::GetInputFlow()
{
  if(myinputcomp) return;

  Standard_Integer i;
  Handle(TColStd_HSequenceOfHAsciiString) steps = PrecedenceSteps();

  WOK_TRACE {
    VerboseMsg()("WOK_MAKE") << "WOKMake_Step::GetInputFlow" 
			   << "Computing Input Flow" << endm;
  }

  if(steps.IsNull()) return;

  for(i=1; i<=steps->Length(); i++)
    {
      GetInputFromStep(myprocess->Find(steps->Value(i)));
      if(CheckStatus("WOKMake_Step::GetInputFlow")) return;
    }

  myinputcomp = Standard_True;
  return;
}

//=======================================================================
//function : InputFileList
//purpose  : 
//=======================================================================
const Handle(WOKMake_HSequenceOfInputFile)& WOKMake_Step::InputFileList()
{
  if(!myinput.IsNull()) return myinput;

  GetInputFlow();

  myinput = new WOKMake_HSequenceOfInputFile;

  for(Standard_Integer i=1; i<=myinflow.Extent(); i++)
    {
      const Handle(WOKMake_InputFile)& afile = myinflow(i);

      if(afile->IsDirectInput())
	{
	  switch(afile->Status())
	    {
	    case WOKMake_Undetermined:
	    case WOKMake_New:
	    case WOKMake_Same:
	    case WOKMake_Moved:
	      myinput->Append(afile);
	      break;
	    default:
	      break;
	    }
	}
    }
  return myinput;
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : LoadDependencies
//purpose  : 
//=======================================================================
void WOKMake_Step::LoadDependencies()
{
  if(mydeploaded) return;

  Standard_Integer i;

  Handle(WOKernel_File) inputfile  = LocateAdmFile(InLocator(), InputFilesFileName());
  if(inputfile.IsNull()) return;
  
  Handle(WOKernel_File) outputfile = LocateAdmFile(OutLocator(), OutputFilesFileName());
  if(outputfile.IsNull()) return;
  
  Handle(WOKernel_File) depfile    = LocateAdmFile(InLocator(), DepItemsFileName());
  if(depfile.IsNull()) return;

  WOKMake_InputFile::ReadFile(inputfile->Path(), InLocator(),   mydepin);
  WOKMake_OutputFile::ReadFile(outputfile->Path(), OutLocator(), mydepout);
  WOKMake_DepItem::ReadFile(depfile->Path(), mydepitems);

  if((mydepout.Extent()>0) && (mydepin.Extent()>0))
    {
      mydepmatrix = new TColStd_HArray2OfInteger(1, mydepout.Extent(), 1, mydepin.Extent(), 0);

      for(i=1; i<=mydepitems.Extent(); i++)
	{
	  const Handle(WOKMake_DepItem)& anitem = mydepitems(i);
	  Standard_Integer in_index, out_index;

	  if(!mydepin.Contains(anitem->IssuedFrom()))
	    {
	      ErrorMsg() << "WOKMake_Step::LoadDependencies"
		       << "Unknown origin : " << anitem->IssuedFrom() << endm;
	      ErrorMsg() << "WOKMake_Step::LoadDependencies"
		       << "Dependences could not be loaded : will force step" << endm;
	      mydepmatrix.Nullify();
	      mydepitems.Clear();
	      mydepin.Clear();
	      mydepout.Clear();
	      return;
	      //Standard_ProgramError::Raise("WOKMake_Step::LoadDependencies : Unknown origin");
	    }
	  else
	    in_index = mydepin.FindIndex(anitem->IssuedFrom());

	  if(!mydepout.Contains(anitem->OutputFile()))
	    {
	      ErrorMsg() << "WOKMake_Step::LoadDependencies"
		       << "Unknown output : " << anitem->OutputFile() << endm;
	      ErrorMsg() << "WOKMake_Step::LoadDependencies"
		       << "Dependences could not be loaded : will force step" << endm;
	      mydepmatrix.Nullify();
	      mydepitems.Clear();
	      mydepin.Clear();
	      mydepout.Clear();
	      mydeploaded = Standard_True;
	      return;
	      //Standard_ProgramError::Raise("WOKMake_Step::LoadDependencies : Unknown output");
	    }
	  else
	    out_index = mydepout.FindIndex(anitem->OutputFile());

	  if(mydepmatrix->Value(out_index, in_index))
	    {
	      WarningMsg() << "WOKMake_Step::LoadDependencies"
			 << "Ignoring duplicate line in depfile (" << anitem->OutputFile() << " : " << anitem->IssuedFrom() << ")" << endm;
	    }
	  else
	    {
	      mydepmatrix->SetValue(out_index, in_index, i);
	    }
	}
    }
  mydeploaded = Standard_True;
  return;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : StepFileStatus
//purpose  : 
//=======================================================================
WOKMake_FileStatus WOKMake_Step::StepFileStatus(const Handle(WOKMake_StepFile)& afile, const Handle(WOKernel_Locator)& alocator)
{
  if(afile->IsLocateAble() && afile->IsPhysic())
    {
      Handle(WOKernel_File) current = alocator->Locate(afile->ID());
      
      if(current.IsNull())
	{
	  afile->SetStatus(WOKMake_Disappeared);
	  return WOKMake_Disappeared;
	}
      else
	{
	  if(!current->Path()->Name()->IsSameString(afile->LastPath()->Name()))
	    {
	      afile->SetStatus(WOKMake_Moved);
	      return WOKMake_Moved;
	    }
	}
    }
  else if(afile->IsPhysic())
    {
      if(!afile->LastPath()->Exists())
	{
	  afile->SetStatus(WOKMake_Disappeared);
	  return WOKMake_Disappeared;
	}
    }

  afile->SetStatus(WOKMake_Undetermined);
  return WOKMake_Undetermined;
}

//=======================================================================
//function : OutOfDateEntities
//purpose  : 
//=======================================================================
Handle(WOKMake_HSequenceOfInputFile) WOKMake_Step::OutOfDateEntities()
{
  LoadDependencies();

  Handle(WOKMake_HSequenceOfInputFile) result = new WOKMake_HSequenceOfInputFile;
  Handle(WOKMake_InputFile)            infile;
  Handle(WOKMake_OutputFile)           outfile;
  WOKTools_MapOfHAsciiString           amap;
  Standard_Integer i,j, value;
  

  if(mydepmatrix.IsNull())
    {
      return ForceBuild();
    }
  

  for(i=1; i<=mydepin.Extent(); i++)
    {
      const Handle(WOKMake_InputFile)& infile = mydepin(i);

      if(infile->IsDirectInput())
	{
	  if(!myinflow.Contains(infile->ID()))
	    {
	      infile->SetStatus(WOKMake_Disappeared);
	    }
	  else
	    StepFileStatus(infile, InLocator());
	}
      else
	StepFileStatus(infile, InLocator());

      switch(infile->Status())
	{
	case WOKMake_New:
	case WOKMake_Moved:
	  if(myinflow.Contains(infile->ID()))
	    {
	      const Handle(WOKMake_InputFile)& inflowfile = myinflow.FindFromKey(infile->ID());
	      amap.Add(inflowfile->ID());
	      result->Append(inflowfile);
	    }
	  break;
	case WOKMake_Disappeared:
	  if(myinflow.Contains(infile->ID()))
	    {
	      ErrorMsg() << "WOKMake_Step::OutOfDateEntities"
		       << "Could not locate input file : " << infile->ID() << endm;
	      SetFailed();
	      return result;
	    }
	  break;
	case WOKMake_Same:
	case WOKMake_Undetermined:
	  break;
	}
    }

  for(i=1; i<=myinflow.Extent(); i++)
    {
      const Handle(WOKMake_InputFile)& infile = myinflow(i);
      if(!mydepin.Contains(infile->ID()))
	{
	  if(amap.Add(infile->ID()))
	    result->Append(infile);
	}
    }

  for(i=mydepmatrix->LowerRow(); i<=mydepmatrix->UpperRow(); i++)
    {
      Standard_Boolean outofdate      = Standard_False;
      Standard_Boolean founddirectdep = Standard_False;
      Handle(WOKMake_InputFile) adirect;

      const Handle(WOKMake_OutputFile)& outfile = mydepout(i);

      switch(StepFileStatus(outfile, OutLocator()))
	{
	case WOKMake_New:
	  outofdate = Standard_True;
	  break;
	case WOKMake_Disappeared:
	  outofdate = Standard_True;
	  break;
	case WOKMake_Moved:
	case WOKMake_Same:
	case WOKMake_Undetermined:
	  break;
	}
      

      for(j=mydepmatrix->LowerCol(); j<=mydepmatrix->UpperCol() && !outofdate ; j++)
	{
	  value = mydepmatrix->Value(i,j);
	  
	  if(value)
	    {
	      const Handle(WOKMake_InputFile)& infile = mydepin(j);
	      switch(infile->Status())
		{
		case WOKMake_New:
		case WOKMake_Moved:
		  outofdate = Standard_True;
		  break;
		case WOKMake_Disappeared:
		  outofdate = Standard_True;
		  break;
		case WOKMake_Same:
		case WOKMake_Undetermined:
		  if(infile->IsPhysic())
		    {
		      if(infile->LastPath()->IsNewer(outfile->LastPath()))
			outofdate = Standard_True;
		    }
		  else
		    {
		      if (infile->IsStepID()) 
			outofdate = Standard_True;
		      else 
			outofdate = Standard_False;
		    }
		  break;
		}
	      if(outofdate)
		{
		  const Handle(WOKMake_DepItem)& anitem = mydepitems(value);
		  if(anitem->IsDirectDep())
		    if(myinflow.Contains(anitem->IssuedFrom()))
		      {
			if(amap.Add(anitem->IssuedFrom()))
			    result->Append(myinflow.FindFromKey(anitem->IssuedFrom()));
			founddirectdep = Standard_True;
		      }
		  
		}
	    }
	}

      if(outofdate && (! founddirectdep))
	{
	  // rechercher une dep directe valide
	  for(j=mydepmatrix->LowerCol(); j<=mydepmatrix->UpperCol() && !founddirectdep ; j++)
	    {
	      value = mydepmatrix->Value(i,j);
	      if(value)
		{
		  const Handle(WOKMake_DepItem)& anitem = mydepitems(value);
		  if(anitem->IsDirectDep())
		    {
		      outofdate      = Standard_True;
		      founddirectdep = Standard_True;
		      if(myinflow.Contains(anitem->IssuedFrom()))
			if(amap.Add(anitem->IssuedFrom()))
			  result->Append(myinflow.FindFromKey(anitem->IssuedFrom()));
		    }
		}
	    }
	}
    }

  return result;
}

//=======================================================================
//function : HandleTargets
//purpose  : 
//=======================================================================
Handle(WOKMake_HSequenceOfInputFile)  WOKMake_Step::HandleTargets()
{
  Standard_Integer i;
  Handle(TColStd_HSequenceOfHAsciiString) targets = Targets();
  Handle(WOKMake_HSequenceOfInputFile)    result = new WOKMake_HSequenceOfInputFile;
  Handle(WOKMake_InputFile)               infile;
  WOKTools_MapOfHAsciiString amap;

  for(i=1; i<=targets->Length(); i++)
    {
      if(!amap.Contains(targets->Value(i)))
	{
	  amap.Add(targets->Value(i));
	}
    }

  for(i=1; i<=myinflow.Extent(); i++)
    {
      infile = myinflow(i);
      if (infile->IsLocateAble())
	{
	  Handle(TCollection_HAsciiString) astr = infile->ID()->Token(":", 3);
	  if(!astr.IsNull())
	    { 
	      if(amap.Contains(astr))
		{
		  result->Append(infile);
		}
	    }
	}
      else
	{ 
	  if (!infile->LastPath().IsNull()) 
	    {
	      if(amap.Contains(infile->LastPath()->FileName()))
		{
		  result->Append(infile);
		}
 	    }
	}
    }

  return result;
}

//=======================================================================
//function : ForceBuild
//purpose  : 
//=======================================================================
Handle(WOKMake_HSequenceOfInputFile) WOKMake_Step::ForceBuild()
{
  Standard_Integer i;
  Handle(WOKMake_HSequenceOfInputFile) result = new WOKMake_HSequenceOfInputFile;
  Handle(WOKMake_InputFile)            infile;

  for(i=1; i<=myinflow.Extent(); i++)
    {
      result->Append(myinflow(i));
    }
  return result;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : CompleteExecList
//purpose  : 
//=======================================================================
void WOKMake_Step::CompleteExecList(const Handle(WOKMake_HSequenceOfInputFile)& alist)
{
  Standard_Integer i;
  Handle(WOKMake_InputFile)  infile;
  WOKTools_MapOfHAsciiString inmap;
  

  for(i=1; i<=alist->Length(); i++)
    {
      inmap.Add(alist->Value(i)->ID());
    }

  LoadDependencies();
  
  if(!mydepmatrix.IsNull())
    {
      for(i=1; i<=myinflow.Extent(); i++)
	{
	  infile = myinflow(i);

	  if(mydepin.Contains(infile->ID()))
	    {
	      // Regarder s'il existe d'autres entrees directes 
	      Standard_Integer in_index = mydepin.FindIndex(infile->ID());
	      Standard_Integer j, k;
	      Standard_Boolean generatedoutput = Standard_False;
	      
	      for(j=mydepmatrix->LowerRow(); j<=mydepmatrix->UpperRow(); j++)
		{
		  const Standard_Integer value = mydepmatrix->Value(j,in_index);
		  
		  if(value)
		    {
		      const Handle(WOKMake_DepItem)& item = mydepitems(value);
		      if(item->IsDirectDep() && inmap.Contains(item->IssuedFrom()))
			{
			  for(k=mydepmatrix->LowerCol(); k<=mydepmatrix->UpperCol(); k++)
			    {
			      const Standard_Integer value = mydepmatrix->Value(j,k);
			      if(value)
				{
				  const Handle(WOKMake_DepItem)& item = mydepitems(value);
				  if(myinflow.Contains(item->IssuedFrom()))
				    {
				      const Handle(WOKMake_InputFile)& infile = myinflow.FindFromKey(item->IssuedFrom());
				  
				  
				      if(item->IsDirectDep())
					{
					  if(!inmap.Contains(item->IssuedFrom()))
					    {
					      inmap.Add(infile->ID());
					      alist->Append(infile);
					    }
					}
				    }
				}
			    }
			}
		      generatedoutput=Standard_True;
		    }
		}

	      if(!generatedoutput && infile->IsDirectInput())
		{
		  if(!inmap.Contains(infile->ID()) )
		    {
		      inmap.Add(infile->ID());
		      alist->Append(infile);
		    }
		}
	    }
	}
    }
  return;
}


//=======================================================================
//function : ExecutionInputList
//purpose  : 
//=======================================================================
Handle(WOKMake_HSequenceOfInputFile) WOKMake_Step::ExecutionInputList() 
{

  Handle(WOKMake_HSequenceOfInputFile) result;

  if(IsChecked() && !mytargets.IsNull() && !IsKind(STANDARD_TYPE(WOKMake_MetaStep))) return ForceBuild();

  if(!mytargets.IsNull() && SubCode().IsNull()) 
    {
      result = HandleTargets();
    }
  else
    {
      if (IsChecked()) return ForceBuild();
      else
	result = new WOKMake_HSequenceOfInputFile;
    }

  if(!result->Length())
    {
      if(!myoptions.IsNull())
	{
	  Standard_Integer i;
	  for(i=1; i<=myoptions->Length(); i++)
	    {
	      if(myoptions->Value(i) == WOKMake_Force)
		return ForceBuild();
	    }
	}
      result = OutOfDateEntities();
    }
  
  CompleteExecList(result);
  return result;
}


//=======================================================================
//function : CheckStatus
//purpose  : 
//=======================================================================
Standard_Boolean WOKMake_Step::CheckStatus(const Standard_CString amsg) const
{
  switch(Status())
    {
    case WOKMake_Uptodate:
    case WOKMake_Success:
    case WOKMake_Unprocessed:
    case WOKMake_Incomplete:
    case WOKMake_Processed:
      break;
    case WOKMake_Failed:
      ErrorMsg() << "WOKMake_Step::Make" << "Failed during " << amsg << endm;
      return Standard_True;
    }
  return Standard_False;
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : AddExecDepItem
//purpose  : 
//=======================================================================
void WOKMake_Step::AddExecDepItem(const Handle(WOKMake_InputFile)& infile, 
				  const Handle(WOKMake_OutputFile)& outfile,
				  const Standard_Boolean adirectflag)
{
  if(infile.IsNull() || outfile.IsNull())
    Standard_ProgramError::Raise("WOKMake_Step::AddExecDepItem : Null Input");

  myinflow.Add(infile->ID(), infile);
  myoutflow.Add(outfile->ID(), outfile);

  Handle(WOKMake_DepItem) item = new WOKMake_DepItem(outfile->ID(), infile->ID());
  if(adirectflag) item->SetDirect();
  else            item->SetIndirect();

  myitems.Add(item);
  return;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AcquitExecution
//purpose  : 
//=======================================================================
void WOKMake_Step::AcquitExecution(const Handle(WOKMake_HSequenceOfInputFile)& execlist)
{
  WOKMake_IndexedDataMapOfHAsciiStringOfInputFile  inmap;
  WOKMake_IndexedDataMapOfHAsciiStringOfOutputFile outmap;
  WOKMake_IndexedMapOfDepItem                      items;
  Handle(WOKMake_InputFile)  infile;
  Handle(WOKMake_OutputFile) outfile;
  Handle(WOKMake_DepItem)    item;
  Standard_Integer i, j, index;

  LoadDependencies();

  if(!PrecedenceSteps().IsNull())
    {
      Handle(WOKernel_File) outs[3];

      outs[0] = new WOKernel_File(InputFilesFileName(), Unit(), Unit()->GetFileType(AdmFileType()));
      outs[1] = new WOKernel_File(OutputFilesFileName(), Unit(), Unit()->GetFileType(AdmFileType()));
      outs[2] = new WOKernel_File(DepItemsFileName(), Unit(), Unit()->GetFileType(AdmFileType()));

      for(i=0; i<3; i++)
	{
	  outs[i]->GetPath();
	  Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(outs[i]->LocatorName(), outs[i], Handle(WOKBuilder_Entity)(), outs[i]->Path());
      
	  outfile->SetLocateFlag(Standard_True);
	  outfile->SetProduction();

	  for(j=1; j<=PrecedenceSteps()->Length(); j++)
	    {
	      const Handle(WOKMake_Step)& precstep = BuildProcess()->Find(PrecedenceSteps()->Value(j));

	      Handle(WOKernel_File) afile = precstep->LocateAdmFile(precstep->OutLocator(), precstep->OutputFilesFileName());

	      if(afile.IsNull())
		{
		  WarningMsg() << "WOKMake_Step::AcquitExecution" 
			     << "Could not find precedence step (" << precstep->UniqueName() 
			     << ") admin file : " << precstep->OutputFilesFileName() << endm;
		}
	      else
		{
		  Handle(WOKMake_InputFile) infile = new WOKMake_InputFile(afile->LocatorName(), afile,  Handle(WOKBuilder_Entity)(), afile->Path());
		  infile->SetLocateFlag(Standard_True);
		  infile->SetDirectFlag(Standard_False);

		  AddExecDepItem(infile, outfile, Standard_True);
		}
	    }
	}
    }
  else
    {
      Handle(WOKernel_File) outs[3];
      
      outs[0] = new WOKernel_File(InputFilesFileName(), Unit(), Unit()->GetFileType(AdmFileType()));
      outs[1] = new WOKernel_File(OutputFilesFileName(), Unit(), Unit()->GetFileType(AdmFileType()));
      outs[2] = new WOKernel_File(DepItemsFileName(), Unit(), Unit()->GetFileType(AdmFileType()));
     

      for(i=0; i<3; i++)
	{
	  outs[i]->GetPath();
	  Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(outs[i]->LocatorName(), outs[i], Handle(WOKBuilder_Entity)(), outs[i]->Path()); 
	  outfile->SetLocateFlag(Standard_True);
	  outfile->SetProduction();
	  
	  
	  Handle(WOKMake_InputFile) infile = new WOKMake_InputFile(outs[i]->LocatorName(), outs[i],  Handle(WOKBuilder_Entity)(), outs[i]->Path());
	  infile->SetLocateFlag(Standard_True);
	  infile->SetDirectFlag(Standard_False);
	  
	  AddExecDepItem(infile, outfile, Standard_True);
	}
    }

  // Chargement de la map d'execution
  WOKTools_MapOfHAsciiString execmap;
  for(i=1; i<=execlist->Length(); i++)
    {
      execmap.Add(execlist->Value(i)->ID());
    }

  // BILAN INPUT 
  for(i=1; i<= mydepin.Extent(); i++)
    {
      const Handle(WOKMake_InputFile)& infile = mydepin(i);
      infile->SetStatus(WOKMake_Disappeared);
      inmap.Add(infile->ID(), infile);
    }

  for(i=1; i<= myinflow.Extent(); i++)
    {
      const Handle(WOKMake_InputFile)& infile = myinflow(i);

      index = inmap.FindIndex(infile->ID());

      if(index)
	{
	  const Handle(WOKMake_InputFile)& oldfile = inmap(index);

	  inmap(index) = infile;
	  
	  if(infile->IsPhysic()&&!infile->LastPath().IsNull ()&&!oldfile->LastPath().IsNull ())
	    {
	      if(infile->LastPath()->Name()->IsSameString(oldfile->LastPath()->Name()))
		infile->SetStatus(WOKMake_Same);
	      else
		infile->SetStatus(WOKMake_Moved);
	    }
	  else
	    infile->SetStatus(WOKMake_Same);
	}
      else
	{
	  inmap.Add(infile->ID(), infile);
	  infile->SetStatus(WOKMake_New);
	}
    }
  myinflow.Clear();

  // BILAN OUTPUT

  for(i=1; i<= mydepout.Extent(); i++)
    {
      const Handle(WOKMake_OutputFile)& outfile = mydepout(i);
      outfile->SetStatus(WOKMake_Disappeared);
      outmap.Add(outfile->ID(), outfile);
    }

  for(i=1; i<= myoutflow.Extent(); i++)
    {
      const Handle(WOKMake_OutputFile)& outfile = myoutflow(i);

      index = outmap.FindIndex(outfile->ID());
      
      if(index)
	{
	  const Handle(WOKMake_OutputFile)& oldfile = outmap(index);
	  
	  outmap(index) = outfile;
	  
	  if(outfile->IsPhysic())
	    {
	      if(outfile->LastPath()->Name()->IsSameString(oldfile->LastPath()->Name()))
		outfile->SetStatus(WOKMake_Same);
	      else
		outfile->SetStatus(WOKMake_Moved);
	    }
	  else
	    outfile->SetStatus(WOKMake_Same);
	}
      else
	{
	  outmap.Add(outfile->ID(), outfile);
	  outfile->SetStatus(WOKMake_New);
	}
    }
  myoutflow.Clear();


  // BILAN MATRIX/ITEMS
  Handle(TCollection_HAsciiString) inid, outid;
  Handle(TColStd_HArray2OfInteger) matrix;

  Standard_Integer in_index, out_index;
  Standard_Integer depvalue, value;
  
  if(outmap.Extent()>0 && inmap.Extent()>0)
    {
      matrix = new TColStd_HArray2OfInteger(1, outmap.Extent(), 1, inmap.Extent(), 0);

      if(myitems.Extent())
	{
	  for(i=1; i<=myitems.Extent(); i++)
	    {
	      item = myitems(i);
	      out_index = outmap.FindIndex(item->OutputFile());
	      in_index  = inmap.FindIndex(item->IssuedFrom());
	      items.Add(item);
	      matrix->SetValue(out_index, in_index, items.Extent());
	      item->SetStatus(WOKMake_New);
	    }
	}
      

      if(!mydepmatrix.IsNull())
	{
	  Standard_Integer* idxtab = new Standard_Integer[mydepmatrix->UpperCol()+1];

	  for (j = mydepmatrix->LowerCol(); j <= mydepmatrix->UpperCol(); j++)
	    {
	      idxtab[j] = inmap.FindIndex(mydepin.FindKey(j));
	    }

	  for (i = mydepmatrix->LowerRow(); i <= mydepmatrix->UpperRow(); i++)
	    {
	      Standard_Boolean stillproduced = Standard_False;
	      Standard_Boolean stillforexec  = Standard_False;
	      out_index = outmap.FindIndex(mydepout.FindKey(i));

	      for (j = mydepmatrix->LowerCol(); j <= mydepmatrix->UpperCol(); j++)
		{
		  //in_index = inmap.FindIndex(mydepin.FindKey(j));
		  in_index = idxtab[j];
		  
		  const Handle(WOKMake_InputFile)& infile  = inmap(in_index);

		  depvalue = mydepmatrix->Value(i,j);
		  value = matrix->Value(out_index, in_index);


		  if(!stillproduced)
		    {
		      if(depvalue)
			{
			  const Handle(WOKMake_DepItem)&   depitem = mydepitems(depvalue);
			  Standard_Boolean isinexec = execmap.Contains(depitem->IssuedFrom());
			  
			  if( depitem->IsDirectDep() && infile->Status() != WOKMake_Disappeared)
			    {
			      if( value ) 
				{
				  stillproduced = Standard_True;
				  stillforexec  = Standard_True;
				}
			      else if (depvalue && !isinexec )
				{
				  stillproduced = Standard_True;
				}
			    }
			} 
		      else if(value)
			{
			  const Handle(WOKMake_DepItem)&   item = items(value);
			  //Standard_Boolean isinexec = execmap.Contains(item->IssuedFrom());
			  
			  if(!stillproduced &&  item->IsDirectDep())
			    {
			      stillproduced = Standard_True;
			      stillforexec  = Standard_True;
			    }
			}

		      if(stillproduced)
			{
			  // Here I shoul roll back
			}
		    }

		  if(depvalue && value)
		    {
		      // A LA FOIS AVANT et APRES
		      const Handle(WOKMake_DepItem)&   item    = items(value);
		      item->SetStatus(WOKMake_Same);
		    }
		  else if(depvalue)
		    {

		      // Dans les dependances seulement, on l'ajoute.
		      const Handle(WOKMake_DepItem)&   depitem = mydepitems(depvalue);
		      items.Add(depitem);
		      depitem->SetStatus(WOKMake_Disappeared);
		      matrix->SetValue(out_index, in_index, items.Extent());

		      Standard_Boolean isinexec = execmap.Contains(depitem->IssuedFrom());

		      if(!isinexec)
			{
			  if(infile->Status() != WOKMake_Disappeared)
			    {
			      depitem->SetStatus(WOKMake_Same);
			    }
			}

		      if(stillproduced && !stillforexec  ) 
			{
			  if(!depitem->IsDirectDep())
			    {
			      infile->SetStatus(WOKMake_Same);
			      depitem->SetStatus(WOKMake_Same);
			    }
			}
		    }
		  else if(value)
		    {
		      const Handle(WOKMake_DepItem)&   item    = items(value);
		      item->SetStatus(WOKMake_New);
		    }
		}

	      if(stillproduced) 
		{
		  const Handle(WOKMake_OutputFile)& depoutfile = mydepout(i);
		  const Handle(WOKMake_OutputFile)& outfile = outmap(out_index);
		  if(depoutfile->IsPhysic())
		    {
		      if(depoutfile->LastPath()->Name()->IsSameString(outmap(out_index)->LastPath()->Name()))
			outfile->SetStatus(WOKMake_Same);
		      else
			outfile->SetStatus(WOKMake_Moved);
		    }
		  else
		    {
		      outfile->SetStatus(WOKMake_Same);
		    }
		}
	      else
		{
		  outmap(out_index)->SetStatus(WOKMake_Disappeared);
		}
	    }
	  delete [] idxtab;
	}
    }

  mydepmatrix.Nullify();
  mydepin.Clear();
  mydepout.Clear();

  // BILAN ITEMS
  myinflow.Clear();
  myoutflow.Clear();
  myitems.Clear();

  if(!matrix.IsNull())
    {
      for (i = matrix->LowerRow(); i <= matrix->UpperRow(); i++)
	{
	  if (outmap(i)->Status() != WOKMake_Disappeared) {
	    for (j = matrix->LowerCol(); j <= matrix->UpperCol(); j++)
	      {
		value = matrix->Value(i,j);
		if(value)
		  {
		    const Handle(WOKMake_DepItem)& item = items(value);
		    if((item->Status() == WOKMake_Same) || (item->Status() == WOKMake_New) )
		      {
			myitems.Add(items(value));
		      }
		  }
		}
	    }
	}
    }

  for(i=1; i<=inmap.Extent(); i++)
    {
      const Handle(WOKMake_InputFile)& infile = inmap(i); 

      switch(inmap(i)->Status())
	{
	case WOKMake_New:
	case WOKMake_Moved:
	  if(mystatus == WOKMake_Uptodate) mystatus = WOKMake_Processed;
	case WOKMake_Same:
	  {
	    const Handle(WOKernel_File)& file = infile->File();
	    if(!file.IsNull()) 
	      {
		const Handle(WOKUtils_Path)& path = file->Path();
		if(!path.IsNull()) infile->SetLastPath(path);
	      }
	    myinflow.Add(infile->ID(), infile);
	  }
	  break;
	case WOKMake_Undetermined:
	case WOKMake_Disappeared:
	  if(mystatus == WOKMake_Uptodate) mystatus = WOKMake_Processed;
	  break;
	}
    }

  myoutput = new WOKMake_HSequenceOfOutputFile;

  for(i=1; i<=outmap.Extent(); i++)
    {
      const Handle(WOKMake_OutputFile)& outfile = outmap(i); 

      switch(outmap(i)->Status())
	{
	case WOKMake_New:
	case WOKMake_Moved:
	  if(mystatus == WOKMake_Uptodate) mystatus = WOKMake_Processed;
	case WOKMake_Same:
	  {
	    const Handle(WOKernel_File)& file = outfile->File();
	    if(!file.IsNull()) 
	      {
		const Handle(WOKUtils_Path)& path = file->Path();
		if(!path.IsNull()) outfile->SetLastPath(path);
	      }
	    myoutput->Append(outfile);
	    myoutflow.Add(outfile->ID(), outfile);
	    HandleOutputFile(outfile);
	    if(outfile->IsLocateAble() && outfile->IsPhysic())
	      {
		if (!outfile->File().IsNull()) {
		  OutLocator()->ChangeAdd(outfile->File());
		}
	      }
	  }
	  break;
	case WOKMake_Undetermined:
	case WOKMake_Disappeared:
	  HandleOutputFile(outfile);
	  if(mystatus == WOKMake_Uptodate) mystatus = WOKMake_Processed;
	  break;
	}
    }

//  if(mystatus != WOKMake_Uptodate) 
    {
      Handle(WOKernel_File) infile = AdmFile(InputFilesFileName());

      if(mystatus != WOKMake_Uptodate || !infile->Path()->Exists())
	{
	  WOKMake_InputFile::WriteFile(infile->Path(), myinflow);
	  InLocator()->ChangeAdd(infile);
	}

      Handle(WOKernel_File) outfile = AdmFile(OutputFilesFileName());

      if(mystatus != WOKMake_Uptodate || !outfile->Path()->Exists())
	{
	  WOKMake_OutputFile::WriteFile(outfile->Path(), myoutflow);
	  OutLocator()->ChangeAdd(outfile);
	}
      
      Handle(WOKernel_File) items = AdmFile(DepItemsFileName());
      
      if(mystatus != WOKMake_Uptodate || !items->Path()->Exists())
	{
	  WOKMake_DepItem::WriteFile(items->Path(), myitems);
	  InLocator()->ChangeAdd(items);
	}
    }

  myinflow.Clear();
  myoutflow.Clear();
  myitems.Clear();
  return;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Init
//purpose  : 
//=======================================================================
void WOKMake_Step::Init()
{
  
}

//=======================================================================
//function : Make
//purpose  : 
//=======================================================================
WOKMake_Status WOKMake_Step::Make()
{
//---> EUG4YAN
 g_fCompOrLnk = Standard_False;
//<--- EUG4YAN
  Init();

  if(CheckStatus("perform init of step")) {Terminate(); return Status();}

  if(IsToExecute())
    {
      Handle(WOKMake_HSequenceOfInputFile) execlist;
      
      GetInputFlow();
      
      if(CheckStatus("getting input list")) {Terminate(); return Status();}
      
      Handle( WOKMake_HSequenceOfInputFile ) flist = ForceBuild ();

      execlist = ExecutionInputList();
      
      if(CheckStatus("determine exec list")) {Terminate(); return Status();}
      
      if(execlist->Length() || IsChecked())
	{
	  Execute(execlist);
	  
	  if(CheckStatus("execution")) {Terminate(); return Status();}
	}
      else
	{
	  SetUptodate();
	}
//---> EUG4YAN
    Standard_CString aType = DynamicType () -> Name ();

    if (  !strcmp ( aType, "WOKStep_Compile"        ) ||
          !strcmp ( aType, "WOKStep_DynamicLibrary" ) ||
          !strcmp ( aType, "WOKStep_DLLink"         ) ||
          !strcmp ( aType, "WOKStep_ExecLink"       ) ||
          !strcmp ( aType, "WOKStep_ExeLink"        ) ||
          !strcmp ( aType, "WOKStep_LibLink"        )
    ) {

     InfoMsg() << "WOKMake_Step :: Make" << "Generating build file" << endm;

     g_fCompOrLnk = Standard_True;

     Execute ( flist );

    }  // end if
//<--- EUG4YAN     
      AcquitExecution(execlist);
      
      if(CheckStatus("acquit execution")) {Terminate(); return Status();}
    }

  Standard_Boolean process = Standard_False;

  if(!IsToExecute())
    {
      if(!PrecedenceSteps().IsNull())
	{
	  for(Standard_Integer k=1; k<=PrecedenceSteps()->Length() && !process ; k++)
	    {
	      const Handle(WOKMake_Step)& astep = BuildProcess()->Find(PrecedenceSteps()->Value(k));
	      
	      if(!astep.IsNull())
		{
		  switch(astep->Status())
		    {
		    case WOKMake_Unprocessed:
		    case WOKMake_Uptodate:
		    case WOKMake_Failed:
		      break;
		    default:
		      process = Standard_True;
		      break;
		    }
		}
	    }
	}
      else
	{
	  process = Standard_False;
	}
    }
  else
    {
      switch(Status())
	{
	case WOKMake_Uptodate:
	case WOKMake_Unprocessed:
	  break;
	default:
	  process = Standard_True;
	}
    }

  if(process)
    {
     if(mystatus == WOKMake_Unprocessed) mystatus = WOKMake_Processed;
    }

  Terminate();
  return Status();
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Terminate
//purpose  : 
//=======================================================================
void WOKMake_Step::Terminate()
{
  
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : OutputFileList
//purpose  : 
//=======================================================================
Handle(WOKMake_HSequenceOfOutputFile) WOKMake_Step::OutputFileList() const
{
  Handle(WOKMake_HSequenceOfOutputFile) result;

  if(Locator().IsNull())
    {
      Standard_ProgramError::Raise("WOKMake_Step::OutputFileList Null Locator");
    }
  
  if(myoutput.IsNull())
    {
      Handle(WOKernel_File) afile;
      
      afile = LocateAdmFile(OutLocator(),  OutputFilesFileName());
      
      if(afile.IsNull()) return result;
      
      result = new WOKMake_HSequenceOfOutputFile;
      
      WOKMake_OutputFile::ReadFile(afile->Path(), OutLocator(), result);
    }
  else
    {
      result = myoutput;
    }
  return result;
}

//=======================================================================
//function : HandleOutputFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKMake_Step::HandleOutputFile(const Handle(WOKMake_OutputFile)& afile) 
{
  if(afile.IsNull()) return Standard_False;
  if(afile->File().IsNull()) return Standard_False;

  if((afile->IsLocateAble() && afile->IsProduction() && afile->IsPhysic()) || afile->IsStepID())
    {
      switch(afile->Status())
	{
	case WOKMake_New:
	case WOKMake_Same:
	case WOKMake_Moved:
	  break;
	case WOKMake_Disappeared:
	  {
	    Handle(WOKernel_Entity) nesting = Unit()->Session()->GetEntity(afile->File()->Nesting());
	
	    if(nesting->FullName()->IsSameString(Unit()->FullName()))
	      {
		Handle(WOKUtils_Shell) ashell = Shell();
		Handle(TCollection_HAsciiString) astr, atempl, acmd;
	    
		if(!ashell->IsLaunched()) ashell->Launch();
		ashell->Lock();
	    
		astr = new TCollection_HAsciiString("%WOKSteps_Del_");
		astr->AssignCat(afile->File()->TypeName());

		if(! Unit()->Params().IsSet(astr->ToCString()))
		  {
		    astr = new TCollection_HAsciiString("%WOKSteps_Del_Default");
		    if(Unit()->Params().IsSet(astr->ToCString()))
		      {
			atempl =  Unit()->Params().Eval(astr->ToCString(),Standard_True);
		      }
		  }
		else
		  {
		    atempl = Unit()->Params().Eval(astr->ToCString(),Standard_True);
		  }
	    
		if(atempl.IsNull())
		  {
		    WarningMsg() << "WOKMake_Step::HandleOutputFile"
			       << "Could not determine Del action for type : " << afile->File()->TypeName() << endm;
		    ashell->UnLock();
		    return Standard_False;
		  }
	    
		if(! Unit()->Params().IsSet(atempl->ToCString()))
		  {
		    ErrorMsg() << "WOKMake_Step::HandleOutputFile"
			     << "Could not eval Del action (" << atempl << ") for type : " << afile->File()->TypeName() << endm;
		    ashell->UnLock();
		    return Standard_False;
		  }


		if(afile->File()->Path()->Exists() || afile->File()->Path()->IsSymLink())
		  {
		    Unit()->Params().Set("%FilePath", afile->File()->Path()->Name()->ToCString());
//                    cout << "WOKMake_Step : " << afile->File()->Path()->Name()->ToCString() << endl ;
		
		    acmd = Unit()->Params().Eval(atempl->ToCString(),Standard_True);
		
		    if(!acmd.IsNull())
		      {
			InfoMsg() << "WOKMake_Step::HandleOutputFile"
				<< "Invoking " << atempl << " on " << afile->File()->Path()->Name() << endm;
		      
			ashell->Execute(acmd);
		      
			if(ashell->Status())
			  {
			    Handle(TColStd_HSequenceOfHAsciiString) resseq = ashell->Errors();
			    Standard_Boolean ph = ErrorMsg().PrintHeader();
			
			    ErrorMsg() << "WOKMake_Step::HandleOutputFile" << "Errors occured in Shell" << endm;
			    ErrorMsg().DontPrintHeader();
			    for(Standard_Integer i=1; i<= resseq->Length(); i++)
			      {
				ErrorMsg() << "WOKMake_Step::HandleOutputFile" << resseq->Value(i) << endm;
			      }
			    if(ph) ErrorMsg().DoPrintHeader();
			  }
			OutLocator()->ChangeRemove(afile->File());
			ashell->ClearOutput();
			return Standard_True;
		      }
		  }
		else
		  {
		    if(afile->File()->Path()->IsSymLink())
		      {
			WarningMsg() << "WOKMake_Step::HandleOutputFile"
				   << "Disappeared File (" << afile->File()->UserPathName() << ") does not exists " << endm;
		      }
		  }
		ashell->UnLock();

	      }
	    else
	      {
		WarningMsg() << "WOKMake_Step::HandleOutputFile" 
			   << "File " << afile->File()->UserPathName() << " is not in " << Unit()->UserPathName() 
			   << " : Disappeared and left untouched" << endm;
		return Standard_False;
	      }
	  }
	break;
        default: break;
	}
    }
  return Standard_False;
}
