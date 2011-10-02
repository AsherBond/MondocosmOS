// File:	WOKAPI_BuildProcess.cxx
// Created:	Fri Jun 13 11:28:53 1997
// Author:	Jean GAUTIER
//		<jga@hourax.paris1.matra-dtv.fr>


#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Messages.hxx>
#include <WOKTools_MapOfHAsciiString.hxx>
#include <WOKTools_Define.hxx>
#include <WOKTools_HSequenceOfDefine.hxx>

#include <WOKUtils_Shell.hxx>
#include <WOKUtils_ShellManager.hxx>

#include <WOKernel_DevUnit.hxx>
#include <WOKernel_Locator.hxx>
#include <WOKernel_UnitGraph.hxx>
#include <WOKernel_DBMSystem.hxx>
#include <WOKernel_Station.hxx>
#include <WOKernel_Session.hxx>


#include <WOKMake_IndexedDataMapOfBuildProcessGroup.hxx>
#include <WOKMake_DataMapIteratorOfDataMapOfHAsciiStringOfStep.hxx>
#include <WOKMake_DataMapIteratorOfDataMapOfHAsciiStringOfSequenceOfHAsciiString.hxx>
#include <WOKMake_BuildProcessGroup.hxx>
#include <WOKMake_Step.hxx>
#include <WOKMake_BuildProcessIterator.hxx>
#include <WOKMake_HSequenceOfStepOption.hxx>

#include <WOKAPI_MakeStep.hxx>
#include <WOKAPI_SequenceOfMakeStep.hxx>
#include <WOKAPI_Session.hxx>

#include <WOKAPI_BuildProcess.ixx>


//=======================================================================
//Author   : Jean Gautier (jga)
//function : WOKAPI_BuildProcess
//purpose  : 
//=======================================================================
WOKAPI_BuildProcess::WOKAPI_BuildProcess()
: myinit(Standard_False), myselect(0)
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Init
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_BuildProcess::Init(const WOKAPI_Workbench& abench)
{
 
  if(!abench.IsValid()) 
    {
      ErrorMsg() << "WOKAPI_BuildProcess::Init" 
	       << "Invalid workbench for build process init" << endm;
      return myinit= Standard_False;
    }

  abench.Entity()->Open();

  mybench = abench;

  Handle(WOKernel_Locator)   alocator = new WOKernel_Locator(Handle(WOKernel_Workbench)::DownCast(abench.Entity()));
  Handle(WOKernel_UnitGraph) agraph   = new WOKernel_UnitGraph(alocator);
  Handle(WOKUtils_Shell)     ashell   = WOKUtils_ShellManager::GetShell();

  myprocess = new WOKMake_BuildProcess(alocator, ashell, agraph);

  return myinit = Standard_True;
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetForceFlag
//purpose  : 
//=======================================================================
void  WOKAPI_BuildProcess::SetForceFlag(const Standard_Boolean aflag)
{
  if(myoptions.IsNull() && aflag)
    {
      myoptions = new WOKMake_HSequenceOfStepOption;
      myoptions->Append(WOKMake_Force);
    }
  if(aflag)
    {
      for(Standard_Integer i=1; i<=myoptions->Length(); i++)
	{
	  if(myoptions->Value(i) == WOKMake_Force) return;
	}
      myoptions->Append(WOKMake_Force);
      return;
    }
  else if(!myoptions.IsNull())
    {
      for(Standard_Integer i=1; i<=myoptions->Length(); i++)
	{
	  if(myoptions->Value(i) == WOKMake_Force) 
	    {
	      myoptions->Remove(i);
	    }
	}
    }
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : Add
//purpose  : 
//=======================================================================
void WOKAPI_BuildProcess::Add(const WOKAPI_Unit& adevunit) 
{
  if(!myinit)
    {
      ErrorMsg() << "WOKAPI_BuildProcess::Add"   
	       << "Build process is not initialized" << endm;
      return;
    }


  if(adevunit.IsValid())
    {
      adevunit.Entity()->Open();

      Handle(WOKernel_Locator) locator = myprocess->Locator();
      Handle(TCollection_HAsciiString) umakename = new TCollection_HAsciiString(adevunit.Name());
      Handle(TCollection_HAsciiString) stepsname = new TCollection_HAsciiString(adevunit.Name());
      
      umakename->AssignCat("_WOKUMake.edl");
      stepsname->AssignCat("_WOKSteps.edl");
      
      Handle(TCollection_HAsciiString) sourcetype = new TCollection_HAsciiString("source");

      Handle(WOKernel_File) ancien  = locator->Locate(adevunit.Name(), sourcetype, umakename);
      Handle(WOKernel_File) nouveau = locator->Locate(adevunit.Name(), sourcetype, stepsname);
      
      if(!ancien.IsNull() && nouveau.IsNull())
	{
	  WarningMsg() << "WOKAPI_BuildProcess::Add"  
		     << "Unit " << adevunit.Name() << " contains a " << umakename << " file and no " << stepsname << endm;
	}

      if(!adevunit.CheckDirs())
	{
	  WarningMsg() << "WOKAPI_BuildProcess::Add"  
	    << "Unit " << adevunit.Name() << " is missing directories : ignored" << endm;
	}
      else
	myprocess->ComputeSteps(*((Handle(WOKernel_DevUnit) *) &(adevunit.Entity())));
    }
  else
    {
      ErrorMsg() << "WOKAPI_BuildProcess::Add"   
	       << "Development unit is invalid" << endm;
      return;
    }
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Add
//purpose  : 
//=======================================================================
void WOKAPI_BuildProcess::Add(const WOKAPI_SequenceOfUnit& units) 
{
  if(!myinit)
    {
      ErrorMsg() << "WOKAPI_BuildProcess::Add"   
	       << "Build process is not initialized" << endm;
      return;
    }
  
  for(Standard_Integer i=1; i<=units.Length(); i++)
    {
      Add( units.Value(i) );
    }
  return;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SelectStep
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_BuildProcess::SelectStep(const Handle(WOKMake_Step)& astep,
						 const Standard_Boolean selectflag) 
{
  Standard_Integer result = 0;
  if(!astep.IsNull())
    {
      if(!selectflag && astep->IsToExecute()) {myselect--;result=-1;}
      else                                    {myselect++;result=1;}
      if(selectflag) astep->DoExecute();
      else           astep->DontExecute();
      astep->SetOptions(myoptions);
    }
  return result;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SelectOnGroups
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_BuildProcess::SelectOnGroups(const WOKAPI_Unit& aunit, 
						     const Handle(TCollection_HAsciiString)& agroup,
						     const Standard_Boolean selectflag)
{
  Standard_Integer nbselect = 0;
  Handle(WOKMake_BuildProcessGroup) agrp;

  
  if(!myinit)
    {
      ErrorMsg() << "WOKAPI_BuildProcess::Add"   
	       << "Build process is not initialized" << endm;
      return 0;
    }

  if(!myprocess->IsUnitInProcess(aunit.Name())) Add(aunit);
  
  if(!agroup.IsNull())
    {
      agrp = myprocess->GetGroup(agroup);

      Handle(WOKernel_Entity) anent = aunit.Entity();
      
      const TColStd_SequenceOfHAsciiString& aseq = agrp->Steps();

      for(Standard_Integer i=1; i<=aseq.Length(); i++)
	{
	  const Handle(WOKMake_Step)& astep = myprocess->Find(aseq.Value(i));

	  if(!astep.IsNull())
	    {
	      if(!astep->IsHidden())
		{
		  if(!aunit.IsValid())
		    {
		      nbselect += SelectStep(astep,selectflag);
		    }
		  else
		    {
		      if(astep->Unit()->Name()->IsSameString(anent->Name()))
			{
			  nbselect += SelectStep(astep,selectflag);
			}
		    }
		}
	    }
	}
    }
  else
    {
      
      const WOKMake_IndexedDataMapOfBuildProcessGroup& groups = myprocess->Groups();
      for(Standard_Integer j=1; j<=groups.Extent(); j++)
	{
	  Handle(WOKMake_BuildProcessGroup) group = groups(j);

	  Handle(WOKernel_Entity) anent = aunit.Entity();
      
	  const TColStd_SequenceOfHAsciiString& aseq = group->Steps();
	  
	  for(Standard_Integer i=1; i<=aseq.Length(); i++)
	    {
	      const Handle(WOKMake_Step)& astep = myprocess->Find(aseq.Value(i));
	      if(!astep.IsNull())
		{
		  if(!astep->IsHidden())
		    {
		      if(!aunit.IsValid())
			{
			  nbselect += SelectStep(astep,selectflag);
			}
		      else
			{
			  if(astep->Unit()->Name()->IsSameString(anent->Name()))
			    {
			      nbselect += SelectStep(astep,selectflag);
			    }
			}
		    }
		}
	    }
	}      
    }
  
  return nbselect;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SelectOnGroups
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_BuildProcess::SelectOnGroups(const WOKAPI_SequenceOfUnit& units, 
						     const TColStd_SequenceOfHAsciiString& groups,
						     const Standard_Boolean selectflag)
{
  Standard_Integer nbselect = 0;
  Handle(WOKMake_BuildProcessGroup) agrp;
  WOKTools_MapOfHAsciiString amap;

  if(!myinit)
    {
      ErrorMsg() << "WOKAPI_BuildProcess::Add"   
	       << "Build process is not initialized" << endm;
      return 0;
    }
  
  if(!units.IsEmpty())
    {
      for(Standard_Integer i=1; i<=units.Length(); i++)
	{
	  const Handle(TCollection_HAsciiString)& aname = units.Value(i).Entity()->Name();

	  if(!myprocess->IsUnitInProcess(aname)) Add(units.Value(i));

	  if(!amap.Contains(aname))
	    {
	      amap.Add(aname);
	    }
	}
    }

  if(!groups.IsEmpty())
    {
      for(Standard_Integer j=1; j<=groups.Length(); j++)
	{
	  agrp = myprocess->GetGroup(groups.Value(j));
	  
	  const TColStd_SequenceOfHAsciiString& aseq = agrp->Steps();
	  
	  if (aseq.IsEmpty()) {
	    InfoMsg() << "WOKAPI_BuildProcess::SelectOnGroups"
	      << "group " << groups.Value(j) << " is empty " << endm;
	  }
	  for(Standard_Integer i=1; i<=aseq.Length(); i++)
	    {
	      const Handle(WOKMake_Step)& astep = myprocess->Find(aseq.Value(i));
	      
	      if(!astep.IsNull())
		{
		  if(!astep->IsHidden())
		    {
		      if(units.IsEmpty())
			{
			  nbselect += SelectStep(astep,selectflag);
			}
		      else
			{
			  if(amap.Contains(astep->Unit()->Name()))
			    {
			      nbselect += SelectStep(astep,selectflag);
			    }
			}
		    }
		}
	    }
	}
    }
  else
    {
      
      const WOKMake_IndexedDataMapOfBuildProcessGroup& groups = myprocess->Groups();
      for(Standard_Integer j=1; j<=groups.Extent(); j++)
	{
	  Handle(WOKMake_BuildProcessGroup) group = groups(j);

	  const TColStd_SequenceOfHAsciiString& aseq = group->Steps();
	  
	  for(Standard_Integer i=1; i<=aseq.Length(); i++)
	    {
	      const Handle(WOKMake_Step)& astep = myprocess->Find(aseq.Value(i));
	      if(!astep.IsNull())
		{
		  if(! astep->IsHidden() ) 
		    {
		      if(units.IsEmpty())
			{
			  nbselect += SelectStep(astep,selectflag);
			}
		      else
			{
			  if(amap.Contains(astep->Unit()->Name()))
			    {
			      nbselect += SelectStep(astep,selectflag);
			    }
			}
		    }
		}
	    }
	}      
    }

  return nbselect;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SelectOnGroups
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_BuildProcess::SelectOnTypesAndGroups(const TColStd_SequenceOfHAsciiString& types, 
							     const TColStd_SequenceOfHAsciiString& groups,
							     const Standard_Boolean selectflag)
{
  Standard_Integer nbselect = 0;
  Handle(WOKMake_BuildProcessGroup) agrp;
  WOKTools_MapOfHAsciiString amap;

  if(!myinit)
    {
      ErrorMsg() << "WOKAPI_BuildProcess::Add"   
	       << "Build process is not initialized" << endm;
      return 0;
    }
  
  if(!types.IsEmpty())
    {
      for(Standard_Integer i=1; i<=types.Length(); i++)
	{
	  const Handle(TCollection_HAsciiString)& atype = types.Value(i);

	  if(!amap.Contains(atype))
	    {
	      amap.Add(atype);
	    }
	}
    }

  if(!groups.IsEmpty())
    {
      for(Standard_Integer j=1; j<=groups.Length(); j++)
	{
	  agrp = myprocess->GetGroup(groups.Value(j));
	  
	  const TColStd_SequenceOfHAsciiString& aseq = agrp->Steps();
	  
	  for(Standard_Integer i=1; i<=aseq.Length(); i++)
	    {
	      const Handle(WOKMake_Step)& astep = myprocess->Find(aseq.Value(i));
	      
	      if(!astep.IsNull())
		{
		  if(!astep->IsHidden())
		    {
		      if(amap.IsEmpty())
			{
			  nbselect += SelectStep(astep,selectflag);
			}
		      else
			{
			  if(amap.Contains(astep->Unit()->Type()))
			    {
			      nbselect += SelectStep(astep,selectflag);
			    }
			}
		    }
		}
	    }
	}
    }
  else
    {
      
      const WOKMake_IndexedDataMapOfBuildProcessGroup& groups = myprocess->Groups();
      for(Standard_Integer j=1; j<=groups.Extent(); j++)
	{
	  Handle(WOKMake_BuildProcessGroup) group = groups(j);

	  const TColStd_SequenceOfHAsciiString& aseq = group->Steps();
	  
	  for(Standard_Integer i=1; i<=aseq.Length(); i++)
	    {
	      const Handle(WOKMake_Step)& astep = myprocess->Find(aseq.Value(i));
	      if(!astep.IsNull())
		{
		  if(! astep->IsHidden() ) 
		    {
		      if(amap.IsEmpty())
			{
			  nbselect += SelectStep(astep,selectflag);
			}
		      else
			{
			  if(amap.Contains(astep->Unit()->Type()))
			    {
			      nbselect += SelectStep(astep,selectflag);
			    }
			}
		    }
		}
	    }
	}      
    }

  return nbselect;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SelectOnSteps
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_BuildProcess::SelectOnSteps(const WOKAPI_Unit& aunit, 
						    const Handle(TCollection_HAsciiString)& astart,
						    const Handle(TCollection_HAsciiString)& aend,
						    const Standard_Boolean selectflag)
{
  Standard_Integer selected = 0;
  
  if(!myinit)
    {
      ErrorMsg() << "WOKAPI_BuildProcess::Add"   
	       << "Build process is not initialized" << endm;
      return 0;
    }
  
  if(!myprocess->IsUnitInProcess(aunit.Name())) Add(aunit);

  if(aunit.IsValid())
    {
      Standard_Boolean selecting   = Standard_False;
      Standard_Boolean endwasfound = Standard_False;

      if(aend.IsNull())   endwasfound = Standard_True;
      if(astart.IsNull()) selecting   = Standard_True;

      const TColStd_SequenceOfHAsciiString& steps = myprocess->GetUnitSteps(aunit.Name());
      
      for(Standard_Integer i=1; i<=steps.Length(); i++)
	{
	  Handle(WOKMake_Step) astep = myprocess->Find(steps.Value(i));
	  Standard_Boolean     stepselected = Standard_False;

	  if(!selecting)
	    {
	      // pas de selection en cours : puis-je entrer en selection
	      if(!astart.IsNull())
		{
		  if(astep->IsOrIsSubStepOf(astart))
		    {
		      if(astep->IsHidden())
			{
			  if(astart->IsSameString(astep->Code()))
			    {
			      if(!stepselected) {selected += SelectStep(astep,selectflag);stepselected=Standard_True;}
			      selecting = Standard_True;
			    }
			}
		      else
			{
			  if(!stepselected) {selected += SelectStep(astep,selectflag);stepselected=Standard_True;}
			  selecting = Standard_True;
			}
		    }
		}
	    }
	  if(selecting)
	    {
	      // selection en cours
	      if(astep->IsHidden())
		{
		  if(!astart.IsNull())
		    {
		      if(astart->IsSameString(astep->Code()))
			{
			  if(!stepselected) {selected += SelectStep(astep,selectflag);stepselected=Standard_True;}
			}
		    }
		}
	      else
		{
		  if(!stepselected) {selected += SelectStep(astep,selectflag);stepselected=Standard_True;}
		}
	      
	      if(!aend.IsNull())
		{
		  if( aend->IsSameString(astep->Code()) && astep->IsHidden())
		    {
		      if(!stepselected) {SelectStep(astep,selectflag);stepselected=Standard_True;selected++;}
		    }
		  
		  if(astep->IsOrIsSubStepOf(aend))
		    {
		      endwasfound = Standard_True;
		    }

		  if(i<steps.Length())
		    {
		      // je regarde si la suivante est encore a faire
		      Handle(WOKMake_Step) nextstep = myprocess->Find(steps.Value(i+1));
		      
		      if(!nextstep->IsOrIsSubStepOf(aend) && astep->IsOrIsSubStepOf(aend))
			{
			  selecting = Standard_False;
			}
		    }
		}
	    }
	}
      if(!endwasfound)
	{
	  ErrorMsg() << "WOKAPI_BuildProcess::SelectOnSteps"   
	    << "Specified end step (" << aend << " was not found" << endm;
	  UnSelectAll();
	  return 0;
	}
    }
  else
    {
      ErrorMsg() << "WOKAPI_BuildProcess::SelectOnSteps"   
	<< "Unit is invalid" << endm;
      return 0;
    }
 
  return selected;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SelectOnSteps
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_BuildProcess::SelectOnSteps(const WOKAPI_SequenceOfUnit& units, 
						    const Handle(TCollection_HAsciiString)& astart,
						    const Handle(TCollection_HAsciiString)& aend,
						    const Standard_Boolean selectflag)
{
  if(!myinit)
    {
      ErrorMsg() << "WOKAPI_BuildProcess::Add"   
	       << "Build process is not initialized" << endm;
      return 0;
    }

  Standard_Integer selected = 0;
  
  for(Standard_Integer i=1; i<=units.Length(); i++)
    {
      const WOKAPI_Unit& unit = units.Value(i);
      if(!myprocess->IsUnitInProcess(unit.Name())) Add(unit);      
      selected += SelectOnSteps(unit, astart,aend,selectflag);
    }

  return selected;
}

//=======================================================================
//function : SelectOnDefines
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_BuildProcess::SelectOnDefines(const Handle(WOKTools_HSequenceOfDefine)& defines)
{
  if(!myinit)
    {
      ErrorMsg() << "WOKAPI_BuildProcess::SelectOnDefines"   
	       << "Build process is not initialized" << endm;
      return 0;
    }

  if(defines.IsNull())   return 0;
  //if(defines->IsEmpty()) return 0;

  UnSelectAll();
  SetForceFlag(Standard_False);
  Standard_Integer i;

  for(i=1; i<=defines->Length(); i++)
    {
      const WOKTools_Define& adefine = defines->Value(i);
      
      if(!strcmp(adefine.Name()->ToCString(), "Force"))
	{
	  if(!strcmp(adefine.Value()->ToCString(), "Yes"))
	    {
	      SetForceFlag(Standard_True);
	    }
	  else
	    {
	      SetForceFlag(Standard_False);
	    }
	}
    }

  TColStd_SequenceOfHAsciiString groups;
  TColStd_SequenceOfHAsciiString xgroups;

  TColStd_SequenceOfHAsciiString types;
  TColStd_SequenceOfHAsciiString xtypes;
  
  WOKTools_MapOfHAsciiString amap;
  WOKTools_MapOfHAsciiString axmap;

  for( i=1; i<=defines->Length(); i++)
    {
      const WOKTools_Define& adefine = defines->Value(i);
      
      if(!strcmp(adefine.Name()->ToCString(), "Groups"))
	{
	  Standard_Integer j=1;
	  Handle(TCollection_HAsciiString) agroup = adefine.Value()->Token(" ", j);
	  
	  while(!agroup->IsEmpty())
	    {
	      groups.Append(agroup);
	      j++;
	      agroup = adefine.Value()->Token(" ", j);
	    }
	} 
      else if(!strcmp(adefine.Name()->ToCString(), "Units"))
	{
	  Standard_Integer j=1;
	  Handle(TCollection_HAsciiString) aunit = adefine.Value()->Token(" ", j);
	  
	  while(!aunit->IsEmpty())
	    {
	      if(!amap.Contains(aunit)) amap.Add(aunit);
	      j++;
	      aunit = adefine.Value()->Token(" ", j);
	    }
	}
      else if(!strcmp(adefine.Name()->ToCString(), "UnitTypes"))
	{
	  Standard_Integer j=1;
	  Handle(TCollection_HAsciiString) atype = adefine.Value()->Token(" ", j);
	  
	  while(!atype->IsEmpty())
	    {
	      types.Append(atype);
	      j++;
	      atype = adefine.Value()->Token(" ", j);
	    }
	}
      else if(!strcmp(adefine.Name()->ToCString(), "XGroups"))
	{
	  Standard_Integer j=1;
	  Handle(TCollection_HAsciiString) agroup = adefine.Value()->Token(" ", j);
	  
	  while(!agroup->IsEmpty())
	    {
	      xgroups.Append(agroup);
	      j++;
	      agroup = adefine.Value()->Token(" ", j);
	    }
	}  
      else if(!strcmp(adefine.Name()->ToCString(), "XUnits"))
	{
	  Standard_Integer j=1;
	  Handle(TCollection_HAsciiString) aunit = adefine.Value()->Token(" ", j);
	  
	  while(!aunit->IsEmpty())
	    {
	      if(!axmap.Contains(aunit)) axmap.Add(aunit);
	      j++;
	      aunit = adefine.Value()->Token(" ", j);
	    }
	}
      else if(!strcmp(adefine.Name()->ToCString(), "XUnitTypes"))
	{
	  Standard_Integer j=1;
	  Handle(TCollection_HAsciiString) atype = adefine.Value()->Token(" ", j);
	  
	  while(!atype->IsEmpty())
	    {
	      xtypes.Append(atype);
	      j++;
	      atype = adefine.Value()->Token(" ", j);
	    }
	}  
    }

  WOKAPI_SequenceOfUnit          units;
  WOKAPI_SequenceOfUnit          xunits;

  if(!(amap.IsEmpty() || axmap.IsEmpty())  && !(types.IsEmpty() || xtypes.IsEmpty()))
    {
      ErrorMsg() << "WOKAPI_BuildProcess::SelectOnDefines"
	<< "Cannot use Units or XUnits in conjunction with UnitTypes or XUnitTypes" << endm;
      return 0;
    }


  if(amap.IsEmpty())
    {
      if(!types.IsEmpty())
	{
	  WOKTools_MapOfHAsciiString typemap;

	  for(i=1; i<=types.Length(); i++)
	    {
	      const Handle(TCollection_HAsciiString)& atype = types.Value(i);
	      if(!typemap.Contains(atype)) typemap.Add(atype);
	    }

	  WOKAPI_SequenceOfUnit wbunits;
	  mybench.Units(wbunits);

	  for(i=1; i<=wbunits.Length(); i++)
	    {
	      const WOKAPI_Unit& aunit =  wbunits.Value(i);
	      const Handle(TCollection_HAsciiString)& atype = aunit.Type();
	      
	      if(typemap.Contains(atype)) units.Append(aunit);
	    }
	}
      else if(!xtypes.IsEmpty())
	{
	  WOKTools_MapOfHAsciiString typemap;

	  for(i=1; i<=types.Length(); i++)
	    {
	      const Handle(TCollection_HAsciiString)& atype = types.Value(i);
	      if(!typemap.Contains(atype)) typemap.Add(atype);
	    }
	  
	  WOKAPI_SequenceOfUnit wbunits;
	  mybench.Units(wbunits);
	  for(i=1; i<=wbunits.Length(); i++)
	    {
	      const WOKAPI_Unit& aunit =  wbunits.Value(i);
	      const Handle(TCollection_HAsciiString)& atype = aunit.Type();
	      
	      if(!typemap.Contains(atype)) units.Append(aunit);
	    }
	}
      else
	mybench.Units(units);
    }
  else
    {
      WOKAPI_SequenceOfUnit wbunits;
      mybench.Units(wbunits);
      for(i=1; i<=wbunits.Length(); i++)
	{
	  const WOKAPI_Unit& aunit =  wbunits.Value(i);
	  const Handle(TCollection_HAsciiString)& aname = aunit.Name();
	  
	  if(amap.Contains(aname)) units.Append(aunit);
	}
    }

  if(!axmap.IsEmpty())
    {
      WOKAPI_SequenceOfUnit wbunits;  
      mybench.Units(wbunits);

      for(i=1; i<=wbunits.Length(); i++)
	{
	  const WOKAPI_Unit& aunit =  wbunits.Value(i);
	  const Handle(TCollection_HAsciiString)& aname = aunit.Name();
	  
	  if(axmap.Contains(aname)) xunits.Append(aunit);
	}
    }

  Add(units);

  if(groups.IsEmpty())
    {
        Standard_Integer j=1;
	
	Handle(TCollection_HAsciiString) defaults = mybench.ParameterEval(new TCollection_HAsciiString("%WOKSteps_DefaultGroups"));

	if(defaults.IsNull())
	  {
	    defaults = mybench.ParameterEval(new TCollection_HAsciiString("%WOKSteps_Groups"));
	  }
	
	if(!defaults.IsNull())
	  {
	    Handle(TCollection_HAsciiString) agroup = defaults->Token(" ", j);
	    
	    while(!agroup->IsEmpty())
	      {
		groups.Append(agroup);
		j++;
		agroup = defaults->Token(" ", j);
	      }
	  }
    }

  Standard_Integer nbsteps = SelectOnGroups(units,groups);

  if(!xunits.IsEmpty())
    {
      nbsteps -= SelectOnGroups(xunits,groups,Standard_False);
    }
  if(!xgroups.IsEmpty())
    {
      nbsteps -= SelectOnGroups(units,xgroups,Standard_False);
    }

  return nbsteps;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : UnSelectAll
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_BuildProcess::UnSelectAll() 
{
  Standard_Integer unselected = 0;

  WOKMake_DataMapIteratorOfDataMapOfHAsciiStringOfStep anit(myprocess->Steps());

  while(anit.More())
    {
      if(anit.Value()->IsToExecute())
	{
	  unselected ++;
	  anit.Value()->DontExecute();
	}
      anit.Next();
    }
  myselect = 0;
  return unselected;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : ApplyTargetsToSteps
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_BuildProcess::ApplyTargetsToSteps(const Handle(TCollection_HAsciiString)& step,
							  const Handle(TColStd_HSequenceOfHAsciiString)& targets) const
{
  Standard_Integer targetted = 0;

  WOKMake_DataMapIteratorOfDataMapOfHAsciiStringOfStep anit(myprocess->Steps());

  while(anit.More())
    {
      const Handle(WOKMake_Step) astep = anit.Value();
      if(astep->IsToExecute() && astep->IsOrIsSubStepOf(step))
	{
	  targetted ++;
	  astep->SetTargets(targets);
	}
      anit.Next();
    }
  return targetted;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SelectedStepsNumber
//purpose  : 
//=======================================================================
Standard_Integer WOKAPI_BuildProcess::SelectedStepsNumber() const
{
  return myselect;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SelectedSteps
//purpose  : 
//=======================================================================
void WOKAPI_BuildProcess::SelectedSteps(WOKAPI_SequenceOfMakeStep& aseq) const
{
  WOKMake_BuildProcessIterator anit(myprocess, Standard_False);
  WOKAPI_MakeStep apistep;

  while(anit.More())
    {
      const Handle(WOKMake_Step)& astep = anit.CurStep();

      if(astep->IsToExecute())
	{
	  apistep.Set(astep);
	  aseq.Append(apistep);
	}
      anit.Next();
    }
  return;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : UnitSteps
//purpose  : 
//=======================================================================
void WOKAPI_BuildProcess::UnitSteps(const WOKAPI_Unit& aunit, WOKAPI_SequenceOfMakeStep& aseq) const
{
  WOKAPI_MakeStep apistep;
  const TColStd_SequenceOfHAsciiString& steps = myprocess->GetUnitSteps(aunit.Name());

  for(Standard_Integer i=1; i<=steps.Length(); i++)
    {
      const Handle(WOKMake_Step) astep = myprocess->Find(steps.Value(i));
      apistep.Set(astep);
      aseq.Append(apistep);
    }
  return;
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : PrintBanner
//purpose  : 
//=======================================================================
void WOKAPI_BuildProcess::PrintBanner() const
{
  static Standard_Integer WIDTH = 80;
  static Handle(TCollection_HAsciiString) TIRETS = new TCollection_HAsciiString(WIDTH, '-');
  if(!mybench.IsValid()) return;

  Handle(WOKernel_Session) asession = mybench.Entity()->Session();

  if(asession.IsNull()) return;

  InfoMsg().DontPrintHeader();

  InfoMsg() << "WOKAPI_BuildProcess::PrintBanner" << "\n" << endm;
  InfoMsg() << "WOKAPI_BuildProcess::PrintBanner" 
          <<  TIRETS << endm;

  InfoMsg() << "WOKAPI_BuildProcess::PrintBanner" 
          << "Workbench       :       " << mybench.UserPath() << endm;

  InfoMsg() << "WOKAPI_BuildProcess::PrintBanner" 
          << "Extraction mode :       " << WOKernel_DBMSystem::GetName(asession->DBMSystem()) << endm;
  InfoMsg() << "WOKAPI_BuildProcess::PrintBanner" 
          << "Station         :       " << WOKernel_Station::GetName(asession->Station()) << endm;

  if(asession->DebugMode())
    {
      InfoMsg() << "WOKAPI_BuildProcess::PrintBanner" 
	<< "Compile mode    :       Debug" << endm;
    }
  else
    {
      InfoMsg() << "WOKAPI_BuildProcess::PrintBanner" 
	<< "Compile mode    :       Optimized" << endm;
    }

  InfoMsg() << "WOKAPI_BuildProcess::PrintBanner" 
          << "Step number     :       " << myselect << endm;

  InfoMsg() << "WOKAPI_BuildProcess::PrintBanner" 
          << TIRETS << endm;

  const WOKMake_DataMapOfHAsciiStringOfSequenceOfHAsciiString& units = myprocess->Units();
  WOKMake_DataMapIteratorOfDataMapOfHAsciiStringOfSequenceOfHAsciiString anit(units);
  WOKAPI_Unit aunit;

  while(anit.More())
    {
      aunit.Set(myprocess->Locator()->LocateDevUnit(anit.Key()));

      if(aunit.IsValid())
	{
	  const TColStd_SequenceOfHAsciiString& steps = anit.Value();
	  TColStd_SequenceOfHAsciiString codes;

	  static Standard_Integer DEBUT = strlen("Step            :       ");
	  
	  Standard_Integer len = DEBUT;
	  
	  for(Standard_Integer i=1; i<=steps.Length(); i++)
	    {
	      const Handle(WOKMake_Step)& astep = myprocess->Find(steps.Value(i));
	      if(astep->IsToExecute()) 
		{
		  codes.Append(astep->Code());
		}
	    }
	  
	  if(codes.Length())
	    {
	      InfoMsg() << "WOKAPI_BuildProcess::PrintBanner" 
		      << "Unit            :       " << aunit.Type() << " " << aunit.Name() << endm;
	      
	      if(codes.Length() > 1)
		{
		  InfoMsg() << "WOKAPI_BuildProcess::PrintBanner" 
			  << "Steps           :       " ;
		}
	      else
		{
		  InfoMsg() << "WOKAPI_BuildProcess::PrintBanner" 
			  << "Step            :       " ;
		}

	      for(Standard_Integer i=1; i<=codes.Length(); i++)
		{
		  const Handle(TCollection_HAsciiString)& code = codes.Value(i);

		  len += code->Length() + 1;
		  
		  if(len > WIDTH )
		    {
		      InfoMsg() << endm;
		      InfoMsg() << "WOKAPI_BuildProcess::PrintBanner" << "                        " ;
		      len = DEBUT;
		    }
		  InfoMsg() << code << " ";
		}

	      InfoMsg() << endm;
	      InfoMsg() << "WOKAPI_BuildProcess::PrintBanner" 
		      << TIRETS << endm;
	    }
	}
      anit.Next();
    }
 
  InfoMsg().DoPrintHeader();
  return;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Execute
//purpose  : 
//=======================================================================
WOKAPI_BuildStatus WOKAPI_BuildProcess::Execute(const Standard_Boolean alogflag) 
{
  WOKAPI_BuildStatus status = WOKAPI_Success;
  WOKMake_BuildProcessIterator anit(myprocess, alogflag);

  while(anit.More())
    {
      switch(anit.MakeStep())
	{
	case WOKMake_Uptodate:
	case WOKMake_Success:
	case WOKMake_Processed:
	case WOKMake_Unprocessed:
	  break;
	case WOKMake_Incomplete:
	case WOKMake_Failed:
	  status = WOKAPI_Failed;
	  break;
	}
      anit.Next();
    }
  
  switch(anit.Terminate())
    {
    case WOKMake_Uptodate:
    case WOKMake_Success:
    case WOKMake_Processed:
    case WOKMake_Unprocessed:
      break;
    case WOKMake_Incomplete:
    case WOKMake_Failed:
      status = WOKAPI_Failed;
      break;
    };

  return status;
}



