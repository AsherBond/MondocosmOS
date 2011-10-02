// File:	WOKMake_StepBuilder.cxx
// Created:	Wed Oct 23 17:37:55 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>


#include <Standard_ProgramError.hxx>

#include <OSD_SharedLibrary.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKUtils_Path.hxx>

#include <WOKMake_MetaStep.hxx>
#include <WOKMake_TriggerStep.hxx>
#include <WOKMake_StepBuilder.ixx>



//=======================================================================
//Author   : Jean Gautier (jga)
//function : WOKMake_StepBuilder
//purpose  : 
//=======================================================================
WOKMake_StepBuilder::WOKMake_StepBuilder() 
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : WOKMake_StepBuilder
//purpose  : 
//=======================================================================
WOKMake_StepBuilder::WOKMake_StepBuilder(const Handle(TCollection_HAsciiString)& aname, const WOKMake_StepConstructPtr& aptr) 
  : myname(aname), myptr(aptr)
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Name
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKMake_StepBuilder::Name() const
{
  return myname;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Builder
//purpose  : 
//=======================================================================
WOKMake_StepConstructPtr WOKMake_StepBuilder::Builder() const
{
  return myptr;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : StepBuilders
//purpose  : 
//=======================================================================
WOKMake_DataMapOfHAsciiStringOfStepBuilder& WOKMake_StepBuilder::StepBuilders() 
{
  static WOKMake_DataMapOfHAsciiStringOfStepBuilder builders;
  return builders;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Add
//purpose  : 
//=======================================================================
void WOKMake_StepBuilder::Add() const
{
  WOK_TRACE {
    VerboseMsg()("WOK_STEP") << "WOKMake_StepBuilder::Add" 
			   << "Adding " << myname << " in cache" << endm;
  }
  WOKMake_StepBuilder::StepBuilders().Bind(Name(), *this);
}


//purpose  : 

//=======================================================================
//Author   : Jean Gautier (jga)
//function : BuildStep
//purpose  : 
//=======================================================================
Handle(WOKMake_Step) WOKMake_StepBuilder::BuildStep(const Handle(WOKMake_BuildProcess)&     aprocess,
						    const Handle(TCollection_HAsciiString)& name,
						    const Handle(WOKernel_DevUnit)&         aunit,
						    const Handle(TCollection_HAsciiString)& acode,
						    const Standard_Boolean checked,
						    const Standard_Boolean hidden)
{
  Handle(WOKMake_Step) result;
  WOKMake_DataMapOfHAsciiStringOfStepBuilder& knownsteps = WOKMake_StepBuilder::StepBuilders();
  Standard_Integer i = 1;

  if(name.IsNull() || aunit.IsNull() || acode.IsNull())
    {
      ErrorMsg() << "WOKMake_StepBuilder::BuildStep" 
	       << "Invalid Input to WOKMake_StepBuilder::BuildStep" << endm;
      Standard_ProgramError::Raise("Invalid Input to WOKMake_StepBuilder::BuildStep");
    }

  Handle(TCollection_HAsciiString) libs = aunit->Params().Eval("%WOKSteps_StepLibs",Standard_True);

  if(libs.IsNull())
    {
      ErrorMsg() << "WOKMake_Step::GetStep"
	       << "Could not eval parameter %WOKSteps_StepLibs" << endm;
      return result;
    }

  if(knownsteps.IsBound(name))
    {
      WOK_TRACE {
	VerboseMsg()("WOK_STEP") << "WOKMake_StepBuilder::BuildStep" 
			       << "Got " << name << " in cache at pos " << i << endm;
      }
      return (*knownsteps.Find(name).Builder())(aprocess,aunit,acode,checked,hidden);
    }

  Handle(TCollection_HAsciiString) key = new TCollection_HAsciiString(name);
  Handle(TCollection_HAsciiString) keyword;

  keyword = new TCollection_HAsciiString(" ");
  keyword->AssignCat(key);
  keyword->AssignCat(" ");
  
  key->AssignCat("_");
  
  Handle(TCollection_HAsciiString) alib = libs->Token(" ", i); 
  while(!alib->IsEmpty())
    {
      Handle(WOKUtils_Path) alibpath;
      TCollection_AsciiString stepprm("%");
      
      stepprm.AssignCat(alib->ToCString());
      stepprm.AssignCat("_DefinedSteps");
      
      Handle(TCollection_HAsciiString) steps = aunit->Params().Eval(stepprm.ToCString(),Standard_True);
      
      if(steps->Search(keyword) != -1)
	{
	  // la lib contient l'etape recherchee
	  TCollection_AsciiString libparam("%");
	  
	  libparam.AssignCat(alib->ToCString());
	  libparam.AssignCat("_LibPath");
	  
	  Handle(TCollection_HAsciiString) libpath = aunit->Params().Eval(libparam.ToCString(),Standard_True);
	  
	  if(libpath.IsNull())
	    {
	      ErrorMsg() << "WOKMake_Step::GetStep"
		       << "Could not eval parameter " << libparam.ToCString() << endm;
	      return result;
	    }
	  
	  alibpath = new WOKUtils_Path(libpath);
	  
	  if(!alibpath->Exists())
	    {
	      alibpath = aunit->Params().SearchFile(libpath);
	      
	      if(alibpath.IsNull())
		{
		  ErrorMsg() << "WOKMake_Step::GetStep"
			   << "Could not eval find library " << libpath << endm;
		  return result;
		}
	    }


	  OSD_SharedLibrary ashlib(alibpath->Name()->ToCString());

	  if(!ashlib.DlOpen(OSD_RTLD_LAZY))
	    {
	      ErrorMsg() << "WOKMake_Step::GetStep"
		       << "Could not open " << libpath << " : " << ashlib.DlError() << endm;
	      return result;
	    }

	  WOKMake_StepConstructPtr ptr = (WOKMake_StepConstructPtr) ashlib.DlSymb(key->ToCString());

	  if(ptr == NULL)
	    {
	      ErrorMsg() << "WOKMake_Step::GetStep"
		       << "Could not find " << key << " in " << libpath << endm;
	      return result;
	    }
	  
	  WOKMake_StepBuilder builder(name, ptr);
	  
	  WOK_TRACE {
	    VerboseMsg()("WOK_STEP") << "WOKMake_Step::GetStep"
				   << "Adding main : " << name->ToCString() << endm; 
	  }

	  builder.Add();
	  result = (*ptr) (aprocess, aunit, acode, checked, hidden);

	  //profitons du DLOpen
	  Standard_Integer k=1;
	  Handle(TCollection_HAsciiString) astr = steps->Token(" ", k);

	  while(!astr->IsEmpty())
	    {
	      if(!astr->IsSameString(name))
		{
		  Handle(TCollection_HAsciiString) otherkey = new TCollection_HAsciiString(astr);
		  otherkey->AssignCat("_");
	      
		  WOKMake_StepConstructPtr ptr = (WOKMake_StepConstructPtr) ashlib.DlSymb(otherkey->ToCString());

		  if(ptr == NULL)
		    {
		      WarningMsg() << "WOKMake_Step::GetStep"
			<< "Could not find declared " << otherkey << " in " << libpath << endm;
		    }
		  else 
		    {
		      WOKMake_StepBuilder builder(astr, ptr);

		      WOK_TRACE {
			VerboseMsg()("WOK_STEP") << "WOKMake_Step::GetStep"
					       << "Adding : " << astr->ToCString() << endm;
		      }
		      builder.Add();
		    }
		}
	      k++;
	      astr = steps->Token(" ", k);
	    }

	  
	  return result;
	}
      i++;
      alib = libs->Token(" ", i);
     }

  if(result.IsNull())
    {
      WOK_TRACE {
	VerboseMsg()("WOK_STEP") << "WOKMake_Step::GetStep"
			       << key << " is a triggerred step" << endm;
      }

      Handle(WOKMake_TriggerStep) trigg = new WOKMake_TriggerStep(aprocess,aunit, acode, checked, hidden);
      trigg->SetName(name);
      result = trigg;
      return result;
    }
  return result;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : BuildStep
//purpose  : 
//=======================================================================
Handle(WOKMake_Step) WOKMake_StepBuilder::BuildStep(const Handle(WOKMake_BuildProcess)&     aprocess,
						    const Handle(WOKernel_DevUnit)&         aunit,
						    const Handle(TCollection_HAsciiString)& acode,
						    const Handle(TCollection_HAsciiString)& asubcode)
{
  Handle(WOKMake_Step) result;
  
  if(aunit.IsNull())
    {
      ErrorMsg() << "WOKMake_StepBuilder::BuildStep"
	       << "Invalid Null Unit" << endm;
      return result;
    }
  if(acode.IsNull())
    {
      ErrorMsg() << "WOKMake_StepBuilder::BuildStep"
	       << "Invalid Null Step code" << endm;
      return result;
    }

  
  Handle(TCollection_HAsciiString) keyword;
  Handle(TCollection_HAsciiString) key;
  TCollection_AsciiString          astepdescrprm;
  
  astepdescrprm.AssignCat("%WOKSteps_");
  astepdescrprm.AssignCat(acode->ToCString());
  astepdescrprm.ChangeAll('.', '_');
  
  key = aunit->Params().Eval(astepdescrprm.ToCString(),Standard_True);

  if(key.IsNull())
    {
      return result;
    }

  Standard_Character ctlchar;
  Standard_Boolean   checked = Standard_False;
  Standard_Boolean   hidden  = Standard_False;
  Standard_Boolean   meta    = Standard_False;

  Standard_Integer begin = 1, end;
  
  // Carateres de controle en debut d'etape
  while(!IsAlphabetic((ctlchar=key->Value(begin))))
    {
      switch(ctlchar)
	{
	case '*':
	  checked = Standard_True;
	  begin++;
	  break;
	case '.':
	  hidden = Standard_True;
	  begin++;
	  break;
	case '#':
	  meta = Standard_True;
	  begin++;
	  break;
	case ' ':
	case '\t':
	  begin++;
	  break;
	default:
	  ErrorMsg() << "WOKMake_StepDescrExplorer" << "Bad WOKMake Step ctl Character in : " << key << endm;
	  Standard_ProgramError::Raise("WOKMake_StepDescrExplorer");
	}
    }

  Handle(TCollection_HAsciiString)        steprec;
  Handle(TColStd_HSequenceOfHAsciiString) precseq;
  Standard_Integer precbegin, precend;
  // A t'elle des etapes precedente
  if((precbegin = key->Location(1,'(', begin, key->Length())) == 0 )
    {
      end = key->Length();
      precseq.Nullify();
    }
  else
    {
      end = precbegin-1;
      precbegin++;
      precend =  key->Location(1, ')', precbegin, key->Length());
      
      if(precend == 0)
	{
	  ErrorMsg() << "WOKMake_StepBuilder::GetStep" << "Bad WOKMake Step format : " << key << endm;
	  Standard_ProgramError::Raise("WOKMake_StepBuilder::GetStep");
	}

      precend--;
      steprec = key->SubString(precbegin, precend);
      
      precseq = new TColStd_HSequenceOfHAsciiString;

      Standard_Integer precind =1;

      Handle(TCollection_HAsciiString) curstepprec = steprec->Token(",",precind);

      while(!curstepprec->IsEmpty())
	{
	  Handle(TCollection_HAsciiString) precname = WOKMake_Step::UniqueName(aunit, curstepprec, Handle(TCollection_HAsciiString)());

	  precseq->Append(precname);
	  precind++;
	  curstepprec = steprec->Token(",",precind);
	}
    }

  Handle(TCollection_HAsciiString) stepdef = key->SubString(begin,end);
  
  result = BuildStep(aprocess,stepdef, aunit, acode, checked, hidden);

  if(meta && asubcode.IsNull())
    {
      Handle(WOKMake_MetaStep) astep = new WOKMake_MetaStep(aprocess, aunit, 
							    acode, checked, hidden);
      astep->SetAdmFileType(result->AdmFileType());
      astep->SetOutputDirTypeName(result->OutputDirTypeName());
      result = astep;
    }
  
  if(!result.IsNull())
    {
      result->SetPrecedenceSteps(precseq);
    }
  return result;
}
