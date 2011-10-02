// File:	WOKMake_BuildProcess.cxx
// Created:	Mon Mar 24 13:45:01 1997
// Author:	Jean GAUTIER
//		<jga@hourax.paris1.matra-dtv.fr>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKBuilder_MSTool.hxx>

#include <WOKernel_Session.hxx>
#include <WOKernel_UnitNesting.hxx>

#include <WOKMake_StepBuilder.hxx>

#include <WOKMake_BuildProcess.ixx>


//=======================================================================
//Author   : Jean Gautier (jga)
//function : WOKMake_BuildProcess
//purpose  : 
//=======================================================================
WOKMake_BuildProcess::WOKMake_BuildProcess(const Handle(WOKernel_Locator)& alocator, 
					   const Handle(WOKUtils_Shell)& ashell, 
					   const Handle(WOKernel_UnitGraph)& agraph)
: myunitgraph(agraph),
  mylocator(alocator),
  myshell(ashell),
  mycdlit(WOKBuilder_MSTool::GetMSchema()){
}

//=======================================================================
//function : GetKnownUnits
//purpose  : 
//=======================================================================
void WOKMake_BuildProcess::GetKnownUnits()
{
  if(!myknownunits.Extent())
    {
      // Liste des Uds connues 
      Handle(TColStd_HSequenceOfHAsciiString) aseq = mylocator->Visibility();
      Handle(TColStd_HSequenceOfHAsciiString) units;
      Handle(TCollection_HAsciiString) aunitname;
      Handle(TCollection_HAsciiString) afullname, abasename;
      Standard_Integer i,j;
      
      for(i=1; i<=aseq->Length(); i++)
	{
	  const Handle(WOKernel_UnitNesting)& anesting = mylocator->Session()->GetUnitNesting(aseq->Value(i));
	  anesting->Open();
	  
	  units = anesting->Units();
	  
	  for(j=1; j<=units->Length(); j++)
	    {
	      aunitname = mylocator->Session()->GetDevUnit(units->Value(j))->Name();
	      if(!myknownunits.Contains(aunitname))
		{
		  myknownunits.Add(aunitname);
		}
	    }
	}
    }
  return;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : ComputeSteps
//purpose  : 
//=======================================================================
Standard_Boolean WOKMake_BuildProcess::ComputeSteps(const Handle(WOKernel_DevUnit)& aunit) 
{


  if(myunits.IsBound(aunit->Name())) return Standard_False;

  Handle(TCollection_HAsciiString) groups = aunit->Params().Eval("%WOKSteps_Groups");

  if(groups.IsNull())
    {
      ErrorMsg() << "WOKMake_BuildProcess::ComputeSteps" 
	       << "Could not eval %WOKSteps_Groups setting build process groups" << endm;
      return Standard_True;
    }

  Standard_Integer i = 1;
  Handle(TCollection_HAsciiString) group = groups->Token(" \t", i);
  TColStd_SequenceOfHAsciiString thesequense;
  myunits.Bind(aunit->Name(), thesequense);

  TColStd_SequenceOfHAsciiString& unitsteps = myunits(aunit->Name());

  while(!group->IsEmpty())
    {
      Handle(WOKMake_BuildProcessGroup) agroup = GetGroup(group);


      Handle(TCollection_HAsciiString)  groupstepsname = new TCollection_HAsciiString("%WOKSteps_");

      groupstepsname->AssignCat(group);
      groupstepsname->AssignCat("Group");
      
      Handle(TCollection_HAsciiString) groupsteps = aunit->Params().Eval(groupstepsname->ToCString());
      
      if(!groupsteps.IsNull())
	{
	  Standard_Integer j=1;
	  Handle(TCollection_HAsciiString) step = groupsteps->Token(" \t", j);

	  while(!step->IsEmpty()) 
	    {
	      Handle(WOKMake_Step) thestep = WOKMake_StepBuilder::BuildStep(this, aunit, step, Handle(TCollection_HAsciiString)());

	      if(!thestep.IsNull())
		{
		  agroup->AddStep(thestep->UniqueName());
		  unitsteps.Append(thestep->UniqueName());
		  if(!mysteps.IsBound(thestep->UniqueName())) mysteps.Bind(thestep->UniqueName(), thestep);
		}
	      else 
		{
		  ErrorMsg() << "WOKMake_BuildProcess::ComputeSteps" 
		           << "Could not build step " << step << " for unit " << aunit->Name() << endm;
		}
	      j++;
	      step = groupsteps->Token(" \t", j);
	    }
	}
      else
	{
	  WarningMsg() << "WOKMake_BuildProcess::ComputeSteps" 
	             << "Could not eval step group " << group << " (%WOKSteps_" << group << "Group" << ")" << endm;
	}

      if(!mygroups.Contains(agroup->Name()))
	mygroups.Add(agroup->Name(), agroup);

      i++;
      group = groups->Token(" \t", i);
    }
  
  
  return Standard_False;
}

//=======================================================================
//function : RemoveStep
//purpose  : 
//=======================================================================
Standard_Boolean WOKMake_BuildProcess::RemoveStep(const Handle(TCollection_HAsciiString)& anid)
{
  if(mysteps.IsBound(anid))
    {
      mysteps.UnBind(anid);
      return Standard_True;
    }
  return Standard_False;
}

//=======================================================================
//function : RemoveStep
//purpose  : 
//=======================================================================
Standard_Boolean WOKMake_BuildProcess::RemoveUnit(const Handle(TCollection_HAsciiString)& aname)
{
  if(myunits.IsBound(aname))
    {
      TColStd_SequenceOfHAsciiString& steps = myunits.ChangeFind(aname);
      
      for(Standard_Integer i=1; i<steps.Length(); i++)
	{
	  mysteps.UnBind(steps.Value(i));
	}
      steps.Clear();
      return Standard_True;
    }
  return Standard_False;
}
//=======================================================================
//function : ClearUnits
//purpose  : 
//=======================================================================
void WOKMake_BuildProcess::ClearUnits()
{
  myunits.Clear();
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetGroup
//purpose  : 
//=======================================================================
Handle(WOKMake_BuildProcessGroup) WOKMake_BuildProcess::GetGroup(const Handle(TCollection_HAsciiString)& aname) 
{
  if(mygroups.Contains(aname)) return mygroups.FindFromKey(aname);
  else 
    {
      Handle(WOKMake_BuildProcessGroup) result = new WOKMake_BuildProcessGroup(this, aname);
      mygroups.Add(result->Name(), result);
      return result;
    }
}

//=======================================================================
//function : ClearGroups
//purpose  : 
//=======================================================================
void WOKMake_BuildProcess::ClearGroups()
{
  mygroups.Clear();
}

//=======================================================================
//function : StepExists
//purpose  : 
//=======================================================================
Standard_Boolean WOKMake_BuildProcess::StepExists(const Handle(WOKernel_DevUnit)& aunit,
						  const Handle(TCollection_HAsciiString)& acode)
{
  Handle(TCollection_HAsciiString) anid = WOKMake_Step::UniqueName(aunit, acode, Handle(TCollection_HAsciiString)());

  if(mysteps.IsBound(anid)) return Standard_True;

  if(myunits.IsBound(aunit->Name()))
    {
      return Standard_False;
    }
  else
    {
      Handle(WOKMake_Step) thestep = WOKMake_StepBuilder::BuildStep(this, aunit, acode,Handle(TCollection_HAsciiString)());
      if(!thestep.IsNull())
	return Standard_True;
      else
	return Standard_False;
    } 
}

//=======================================================================
//function : GetStepFromID
//purpose  : 
//=======================================================================
Handle(WOKMake_Step) WOKMake_BuildProcess::GetStepFromID(const Handle(TCollection_HAsciiString)& anid)
{
  Handle(WOKMake_Step) result;

  if(mysteps.IsBound(anid))
    {
      result = mysteps.Find(anid);
    }

  if(result.IsNull())
    {
      Handle(TCollection_HAsciiString) uname, acode, asubcode;
      
      WOKMake_Step::SplitUniqueName(anid, uname, acode, asubcode);

      Handle(WOKernel_DevUnit) aunit = Locator()->LocateDevUnit(uname);
      
      if(!aunit.IsNull())
	{
	  result = WOKMake_StepBuilder::BuildStep(this, aunit, acode,asubcode);
	  
	  if(result.IsNull())
	    {
	      ErrorMsg() << "WOKMake_BuildProcess::GetStepFromID" 
		<< "Cannot get step " << acode << " for unit " << aunit->Name() << " (type : " << aunit->Type() << ")" << endm;
	      return result;
	    }
	  mysteps.Bind(result->UniqueName(), result);
	}
      else
	{
	  ErrorMsg() << "WOKMake_BuildProcess::GetStepFromID" 
		<< "Cannot locate dev unit : " << uname << endm;
	  return result;
	}
    }
  return result;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetAndAddStep
//purpose  : 
//=======================================================================
const Handle(WOKMake_Step)& WOKMake_BuildProcess::GetAndAddStep(const Handle(WOKernel_DevUnit)& aunit,
								const Handle(TCollection_HAsciiString)& acode,
								const Handle(TCollection_HAsciiString)& asubcode)
{
  static Handle(WOKMake_Step) NULLRESULT;
  Handle(TCollection_HAsciiString) anid = WOKMake_Step::UniqueName(aunit, acode, asubcode);

  if(mysteps.IsBound(anid)) 
    {
      const Handle(WOKMake_Step)& result = mysteps.Find(anid);

      if(result.IsNull())
	return NULLRESULT;
      else return result;
    }

  if(myunits.IsBound(aunit->Name()) && asubcode.IsNull())
    {
      WarningMsg() << "WOKMake_BuildProcess::GetAndAddStep" 
	       << "Cannot get step (" << acode << ") for unit " << aunit->Name() << " (type : " << aunit->Type() << ")" << endm;
//      return NULLRESULT;
    }
//  else
    {
      Handle(WOKMake_Step) thestep = WOKMake_StepBuilder::BuildStep(this, aunit, acode,asubcode);
      if(!thestep.IsNull())
	{
	  thestep->SetSubCode(asubcode);
	  mysteps.Bind(thestep->UniqueName(), thestep);
	  return mysteps.Find(thestep->UniqueName());
	}
      else
	{
	  ErrorMsg() << "WOKMake_BuildProcess::GetAndAddStep" 
	     << "Cannot get step " << acode << " for unit " << aunit->Name() << " (type : " << aunit->Type() << ")" << endm;
	   return NULLRESULT;
	}
    }
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : IsUnitInProcess
//purpose  : 
//=======================================================================
Standard_Boolean WOKMake_BuildProcess::IsUnitInProcess(const Handle(TCollection_HAsciiString)& aname) const
{
  return myunits.IsBound(aname);
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetUnitSteps
//purpose  : 
//=======================================================================
const TColStd_SequenceOfHAsciiString& WOKMake_BuildProcess::GetUnitSteps(const Handle(TCollection_HAsciiString)& aname) const
{
  static TColStd_SequenceOfHAsciiString EMPTYRESULT;
  
  if(!myunits.IsBound(aname))
    {
      return EMPTYRESULT;
    }
  else
    return myunits.Find(aname);
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetStep
//purpose  : 
//=======================================================================
const Handle(WOKMake_Step)& WOKMake_BuildProcess::Find(const Handle(WOKernel_DevUnit)& aunit,
						       const Handle(TCollection_HAsciiString)& acode,
						       const Handle(TCollection_HAsciiString)& asubcode) const
{
  Handle(TCollection_HAsciiString) anid = WOKMake_Step::UniqueName(aunit, acode, asubcode);
  
  static Handle(WOKMake_Step) NULLRESULT;
  
  if(mysteps.IsBound(anid)) return mysteps.Find(anid);
  
  return NULLRESULT;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetStep
//purpose  : 
//=======================================================================
const Handle(WOKMake_Step)& WOKMake_BuildProcess::Find(const Handle(TCollection_HAsciiString)& astepid) const
{
  static Handle(WOKMake_Step) NULLRESULT;

  if(mysteps.IsBound(astepid)) return mysteps.Find(astepid);
  return NULLRESULT;
}

