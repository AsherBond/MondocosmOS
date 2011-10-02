// File:	WOKAPI_Workbench.cxx
// Created:	Tue Aug  1 18:55:00 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <Standard_ErrorHandler.hxx>

#include <TCollection_HAsciiString.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_MapOfHAsciiString.hxx>
#include <WOKTools_Messages.hxx>
#include <WOKTools_Define.hxx>
#include <WOKTools_HSequenceOfDefine.hxx>
#include <WOKTools_Options.hxx>

#include <WOKUtils_HSequenceOfParamItem.hxx>
#include <WOKUtils_ParamItem.hxx>

#include <WOKernel_Session.hxx>
#include <WOKernel_Workshop.hxx>
#include <WOKernel_Workbench.hxx>
#include <WOKernel_BasicUnitTypes.hxx>
#include <WOKernel_UnitTypeBase.hxx>
#include <WOKernel_UnitTypeDescr.hxx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_Locator.hxx>
#include <WOKernel_UnitGraph.hxx>

#include <WOKAPI_Session.hxx>
#include <WOKAPI_Workshop.hxx>
#include <WOKAPI_SequenceOfWorkbench.hxx>
#include <WOKAPI_Unit.hxx>
#include <WOKAPI_SequenceOfUnit.hxx>

#include <WOKAPI_Workbench.ixx>

//=======================================================================
//function : WOKAPI_Workbench
//purpose  : 
//=======================================================================
 WOKAPI_Workbench::WOKAPI_Workbench()
{
}


//=======================================================================
//function : WOKAPI_Workbench
//purpose  : 
//=======================================================================
WOKAPI_Workbench::WOKAPI_Workbench(const WOKAPI_Entity& anent)
  : WOKAPI_Entity(anent)
{
}
//=======================================================================
//function : WOKAPI_Workbench
//purpose  : 
//=======================================================================
 WOKAPI_Workbench::WOKAPI_Workbench(const WOKAPI_Session& asession, 
				    const Handle(TCollection_HAsciiString)& apath,
				    const Standard_Boolean fatal, const Standard_Boolean getit)
{
  Set(asession.GetWorkbench(apath,fatal,getit));
}

//=======================================================================
//function : BuildParameters
//purpose  : 
//=======================================================================
Handle(WOKUtils_HSequenceOfParamItem) WOKAPI_Workbench::BuildParameters(const WOKAPI_Session& asession, 
									const Handle(TCollection_HAsciiString)& apath, 
									const Handle(TCollection_HAsciiString)& afather,
									const Handle(WOKTools_HSequenceOfDefine)& defines,
									const Standard_Boolean usedefaults)
{
  Handle(WOKUtils_HSequenceOfParamItem) aseq;
  Handle(TCollection_HAsciiString) name;
  Handle(TCollection_HAsciiString) nestname;
  Handle(WOKernel_Workbench)        Kbench;
  Handle(WOKernel_Workshop)         Kshop;

  name     = BuildName(apath);
  nestname = BuildNesting(apath);

  WOKAPI_Workshop ashop(asession,nestname);

  if(!ashop.IsValid())
    {
      ErrorMsg() << "WOKAPI_Workbench::BuildParameters"
	       << "Invalid nesting (" << nestname << ") to create workbench : " << name << endm;
      return aseq;
    }

  if(!WOKernel_Entity::IsValidName(name))
    {
      ErrorMsg() << "WOKAPI_Workbench::BuildParameters"
	       << "Invalid name (" << name << ") to create workbench" << endm;
      return aseq;
    }

  
  Kshop =  Handle(WOKernel_Workshop)::DownCast(ashop.Entity());

  WOKAPI_Workbench father;

  if(!afather.IsNull())
    {
      father = WOKAPI_Workbench(asession,afather);
      
      if(!father.IsValid())
	{
	  ErrorMsg() << "WOKAPI_Workbench::BuildParameters"
		   << "Invalid father (" << afather << ") to create workbench : " << name << endm;
	  return aseq;
	}
    }

  Kbench = new WOKernel_Workbench(name, Kshop, Handle(WOKernel_Workbench)::DownCast(father.Entity()));
  Set(Kbench);

  aseq = GetBuildParameters(asession, name, ashop, defines, usedefaults);
  
  return aseq;
}


//=======================================================================
//function : Build
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Workbench::Build(const WOKAPI_Session& asession, 
					 const Handle(TCollection_HAsciiString)& apath,
					 const Handle(TCollection_HAsciiString)& afather,
					 const Handle(WOKTools_HSequenceOfDefine)& defines, 
					 const Standard_Boolean usedefaults)
{
  Handle(TCollection_HAsciiString) name;
  Handle(TCollection_HAsciiString) nestname;
  Handle(WOKernel_Workbench)        Kbench;
  Handle(WOKernel_Workshop)         Kshop;

  name     = BuildName(apath);
  nestname = BuildNesting(apath);

  WOKAPI_Workshop ashop(asession,nestname);

  if(!ashop.IsValid())
    {
      ErrorMsg() << "WOKAPI_Workbench::Build"
	       << "Invalid nesting (" << nestname << ") to create workbench : " << name << endm;
      return Standard_True;
    }


  if(!WOKernel_Entity::IsValidName(name))
    {
      ErrorMsg() << "WOKAPI_Workbench::Build"
	       << "Invalid name (" << name << ") to create workbench" << endm;
      return Standard_True;
    }


  Kshop =  Handle(WOKernel_Workshop)::DownCast(ashop.Entity());

  //mettre a jour l 'EntityList
  UpdateBeforeBuild(Kshop);

  WOKAPI_Workbench father;

  if(!afather.IsNull())
    {
      father = WOKAPI_Workbench(asession,afather,Standard_False);
      
      if(!father.IsValid())
	{
	  ErrorMsg() << "WOKAPI_Workbench::Build"
		   << "Invalid father (" << afather << ") to create workbench : " << name << endm;
	  return Standard_True;
	}
      Handle(WOKernel_Workbench) Kfather = *((Handle(WOKernel_Workbench) *) &father.Entity());
      if(!Kfather.IsNull())
	{
	  Kfather->Open();
	}
    }

  Kbench = new WOKernel_Workbench(name, Kshop, Handle(WOKernel_Workbench)::DownCast(father.Entity()));
  Set(Kbench);
  
  if(!BuildEntity(asession, name, ashop, defines, usedefaults,Standard_True))
    {
      Kshop->AddWorkbench(Kbench);
      Kbench->Open();
    }
  else return Standard_True;
  return Standard_False;
}

//=======================================================================
//function : Destroy
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Workbench::Destroy()
{
  if(!IsValid()) return Standard_True;
  if(!myEntity->IsOpened()) myEntity->Open();

  Handle(WOKernel_Workshop)  aworkshop  = myEntity->Session()->GetWorkshop(myEntity->Nesting());

  //mettre a jour l 'EntityList
  UpdateBeforeDestroy(aworkshop);
  if (!IsValid()) return Standard_True;

  Handle(WOKernel_Workbench) aworkbench = *((Handle(WOKernel_Workbench) *) &myEntity);
  aworkbench->Open();

  if(aworkbench->Units()->Length())
    {
      ErrorMsg() << "WOKAPI_Workbench::Destroy"
	       << "Cannot destroy not empty workbench" << endm;
      return Standard_True;
    }

  aworkbench->Destroy();
  aworkshop->RemoveWorkbench(aworkbench);

  myEntity.Nullify();
  return Standard_False;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : KnownTypeKeys
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKAPI_Workbench::KnownTypeKeys() const 
{
  Handle(TCollection_HAsciiString) result;

  if(!IsValid()) return result;

  Handle(WOKernel_Workbench) Kbench = *((Handle(WOKernel_Workbench) *) &myEntity);

  const WOKernel_UnitTypeBase& typebase = Kbench->KnownTypes();

  result = new TCollection_HAsciiString;

  for(Standard_Integer i=1; i<=typebase.Length(); i++)
    {
      result->AssignCat(new TCollection_HAsciiString(typebase.Value(i)->Key()));
    }
  return result;
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : KnownTypeKeys
//purpose  : 
//=======================================================================
void WOKAPI_Workbench::KnownTypeNames(TColStd_SequenceOfHAsciiString& aseq) const 
{
  if(!IsValid()) return;

  Handle(WOKernel_Workbench) Kbench = *((Handle(WOKernel_Workbench) *) &myEntity);

  const WOKernel_UnitTypeBase& typebase = Kbench->KnownTypes();

  for(Standard_Integer i=1; i<=typebase.Length(); i++)
    {
      aseq.Append(typebase.Value(i)->Type());
    }
  return ;
}


//=======================================================================
//function : IsValid
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Workbench::IsValid() const 
{
  if(myEntity.IsNull()) return Standard_False;
  return myEntity->IsKind(STANDARD_TYPE(WOKernel_Workbench)); 
}

//=======================================================================
//function : ChangeFather
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Workbench::ChangeFather(const WOKAPI_Workbench& aFather) const
{
  Standard_Boolean result = Standard_False;

  if(!IsValid() || !aFather.IsValid()) {
    result = Standard_True;
  }
  else {
    WOKAPI_Entity fnest = aFather.NestingEntity();
    WOKAPI_Entity nest = NestingEntity();

    if (fnest.Name()->IsSameString(nest.Name())) {
      Handle(WOKernel_Entity) ent = aFather.Entity();
      Handle(WOKernel_Entity) shopent = fnest.Entity();

      Handle(WOKernel_Workbench) kbench = *((Handle(WOKernel_Workbench)*)&myEntity);
      Handle(WOKernel_Workbench) kfbench = *((Handle(WOKernel_Workbench)*)&ent);
      Handle(WOKernel_Workshop)  kshop = *((Handle(WOKernel_Workshop)*)&shopent);

      kbench->SetFather(kfbench);
      kshop->DumpWorkbenchList();
    }
    else {
      result = Standard_True;
    }
  }

  return result;
}

//=======================================================================
//function : Father
//purpose  : 
//=======================================================================
WOKAPI_Workbench WOKAPI_Workbench::Father() const 
{
  WOKAPI_Workbench afather;

  if(!IsValid()) return afather;
  if(!myEntity->IsOpened()) myEntity->Open();

  Handle(WOKernel_Workbench)  abench = *((Handle(WOKernel_Workbench) *) &myEntity);

  if(!abench->Father().IsNull()) 
    afather.Set(abench->Session()->GetWorkbench(abench->Father()));

  return afather;
}

//=======================================================================
//function : Ancestors
//purpose  : 
//=======================================================================
void WOKAPI_Workbench::Ancestors(WOKAPI_SequenceOfWorkbench& benchseq ) const 
{
  
  benchseq.Clear();

  if(!IsValid()) return;
  if(!myEntity->IsOpened()) myEntity->Open();
  
  Handle(WOKernel_Workbench)  abench = *((Handle(WOKernel_Workbench) *) &myEntity);
  Handle(WOKernel_Workbench)  thebench;
  Handle(WOKernel_Session)    asession = abench->Session();

  Handle(TColStd_HSequenceOfHAsciiString) aseq = abench->Ancestors();
  Standard_Integer i;

  WOKAPI_Workbench apibench;

  for(i=1; i<= aseq->Length(); i++)
    {
      apibench.Set(asession->GetWorkbench(aseq->Value(i)));
      benchseq.Append(apibench);
    }
  return;
}

//=======================================================================
//function : NestedEntities
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Workbench::NestedEntities(WOKAPI_SequenceOfEntity& aseq) const
{
  if(!IsValid()) return Standard_False;
  if(!myEntity->IsOpened()) myEntity->Open();

  aseq.Clear();
  Handle(WOKernel_Workbench)  abench = *((Handle(WOKernel_Workbench) *) &myEntity);
  Handle(WOKernel_Session)    asession = abench->Session();
  Handle(WOKernel_DevUnit)    aunit;
  Standard_Integer i;
  
  Handle(TColStd_HSequenceOfHAsciiString) aKseq = abench->Units();

  WOKAPI_Unit apiunit;
  
  for(i=1; i<= aKseq->Length(); i++)
    {
      apiunit.Set(asession->GetDevUnit(aKseq->Value(i)));
      aseq.Append(apiunit);
    }
  return Standard_True;
}

//=======================================================================
//function : Units
//purpose  : 
//=======================================================================
void WOKAPI_Workbench::Units(WOKAPI_SequenceOfUnit& unitseq) const 
{
  unitseq.Clear();

  if(!IsValid()) return;
  
  Handle(WOKernel_Workbench)  abench = *((Handle(WOKernel_Workbench) *) &myEntity);
  Handle(WOKernel_Session)    asession = abench->Session();
  Handle(WOKernel_DevUnit)    aunit;
  Standard_Integer i;

  Handle(TColStd_HSequenceOfHAsciiString) aseq = abench->Units();

  WOKAPI_Unit apiunit;

  for(i=1; i<= aseq->Length(); i++)
    {
      apiunit.Set(asession->GetDevUnit(aseq->Value(i)));
      unitseq.Append(apiunit);
    }
  return;
}

//=======================================================================
//function : Units
//purpose  : 
//=======================================================================
void WOKAPI_Workbench::UnitsOfType(const Handle(TCollection_HAsciiString)& atype, 
				   WOKAPI_SequenceOfUnit& unitseq,
				   const Standard_Boolean clearseq) const 
{
  
  if(!IsValid()) return;
  if(!myEntity->IsOpened()) myEntity->Open();
  
  if(clearseq) unitseq.Clear();

  Handle(WOKernel_Workbench)  abench = *((Handle(WOKernel_Workbench) *) &myEntity);
  Handle(WOKernel_Session)    asession = abench->Session();
  Handle(WOKernel_DevUnit)    aunit;
  Standard_Integer i;

  const Handle(WOKernel_UnitTypeDescr)& typedescr = abench->KnownTypes().GetTypeDescr(atype);

  if(typedescr.IsNull())
    {
      ErrorMsg() << "WOKAPI_Workbench::UnitsOfType"
	       << "Requesting unit type (" << atype << " is not a valid type in " << UserPath() << endm;
      return;
    }

  Standard_Character akey = typedescr->Key();

  Handle(TColStd_HSequenceOfHAsciiString) aseq = abench->Units();

  for(i=1; i<= aseq->Length(); i++)
    {
      const Handle(WOKernel_DevUnit)& Kunit = asession->GetDevUnit(aseq->Value(i));
      if(Kunit->TypeCode() == akey)
	{
	  WOKAPI_Unit unit;
	  unit.Set(Kunit);
	  unitseq.Append(unit);
	}
    }
  return;

}

//=======================================================================
//function : Toolkits
//purpose  : 
//=======================================================================
void WOKAPI_Workbench::Toolkits(WOKAPI_SequenceOfUnit& tkseq) const 
{
  tkseq.Clear();

  if(!IsValid()) return;
  if(!myEntity->IsOpened()) myEntity->Open();
  
  Handle(WOKernel_Workbench) abench = *((Handle(WOKernel_Workbench) *) &myEntity);
  Handle(WOKernel_Session)    asession = abench->Session();
  Handle(WOKernel_DevUnit)    aunit;

  Handle(TColStd_HSequenceOfHAsciiString) aseq = abench->Visibility();
  Handle(TColStd_HSequenceOfHAsciiString) units;

  WOKTools_MapOfHAsciiString tks;

  WOKAPI_Unit apiunit;

  for(Standard_Integer i=1; i<=aseq->Length(); i++) {
    Handle(WOKernel_UnitNesting) nesting = asession->GetUnitNesting(aseq->Value(i));

    if(!nesting.IsNull()) {
      nesting->Open();
      
      units = nesting->Units();
    
      for(Standard_Integer j=1; j<=units->Length(); j++) {
	aunit = asession->GetDevUnit(units->Value(j));
	
	if(!aunit.IsNull()) {
	  if(WOKernel_IsToolkit(aunit)) {
	    if (!tks.Contains(aunit->Name())) {
	      tks.Add(aunit->Name());
	      apiunit.Set(aunit);
	      tkseq.Append(apiunit);
	    }
	  }
	}
      }
    }
  }

}

//=======================================================================
//function : ImplSuppliers
//purpose  : 
//=======================================================================
void WOKAPI_Workbench::ImplSuppliers(const Handle(TCollection_HAsciiString)& aunitname, 
				     WOKAPI_SequenceOfUnit& unitseq) const 
{
  unitseq.Clear();
  
  if(!IsValid()) return;
  if(!myEntity->IsOpened()) myEntity->Open();

  Handle(TColStd_HSequenceOfHAsciiString) aseq;
  Standard_Integer i;

  Handle(WOKernel_Workbench) abench = *((Handle(WOKernel_Workbench) *) &myEntity);
  Handle(WOKernel_Locator)   alocator = new WOKernel_Locator(abench);
  Handle(WOKernel_UnitGraph) agraph   = new WOKernel_UnitGraph(alocator);
  Handle(WOKernel_DevUnit)   asupplier;

  Handle(WOKernel_DevUnit)     aunit    =  alocator->LocateDevUnit(aunitname);
  WOKAPI_Unit apiunit;

  if (aunit.IsNull())
    { 
      // perhaps it's an executable name instead of a unit name
      Handle(TCollection_HAsciiString) aname;
      Handle(TCollection_HAsciiString) apart;
      
      aname = aunitname->Token(":",1);
      apart = aunitname->Token(":",2);

      aunit = alocator->LocateDevUnit(aname);

      if (aunit.IsNull())
	{
	  ErrorMsg() << "WOKAPI_Workbench::ImplSuppliers"
	           << "Unit " << aname << " : unit does not exist in workbench visibility" << endm;
	  return;
	}
      else 
	{
	  if(!WOKernel_IsExecutable(aunit))
	    {
	      ErrorMsg() << "WOKAPI_Workbench::ImplSuppliers"
		<< "Unit " << aname << " is not an executable" << endm;
	      return;
	    }
	  aseq = aunit->ImplementationDep(apart,agraph);
	}
    }

  else
    aseq = aunit->ImplementationDep(agraph);

  if ( !aseq.IsNull() )
    {
      for(i=1; i<aseq->Length(); i++)
	{
	  asupplier = alocator->LocateDevUnit(aseq->Value(i));
	  apiunit.Set(asupplier);
	  unitseq.Append(apiunit);
	}
    }
  else 
    {
      ErrorMsg() << "WOKAPI_Workbench::ImplSuppliers"
	       << "Failed during getting suppliers of " << aunitname << endm;
      return;
    }

  return;
}

//=======================================================================
//function : ImplClients
//purpose  : 
//=======================================================================
void WOKAPI_Workbench::ImplClients(const Handle(TCollection_HAsciiString)& aunitname, 
				   WOKAPI_SequenceOfUnit& unitseq) const 
{

  Handle(WOKernel_Workbench)              abench      = Handle(WOKernel_Workbench)::DownCast(myEntity);
  Handle(WOKernel_Locator)                alocator    = new WOKernel_Locator(abench);
  Handle(WOKernel_Session)                asession    = abench->Session();
  Handle(WOKernel_UnitGraph)              clientgraph = new WOKernel_UnitGraph(alocator);
  Handle(WOKernel_UnitGraph)              agraph      = new WOKernel_UnitGraph(alocator);
  Handle(TColStd_HSequenceOfHAsciiString) aseq        = abench->Visibility();
  Handle(TCollection_HAsciiString)        anudname,asuppliername,dummy;
  WOKTools_MapOfHAsciiString              treated;
  Handle(TColStd_HSequenceOfHAsciiString) uds,suppliers,clients;
  Standard_Integer                        i,j,k;

  unitseq.Clear();
  
  if(!IsValid()) return;
  if(!myEntity->IsOpened()) myEntity->Open();
  
  Handle(WOKernel_DevUnit) aunit = alocator->LocateDevUnit(aunitname);

  if (aunit.IsNull())
    {     
      ErrorMsg() << "WOKAPI_Workbench::ImplClients"
	       << "Unit " << aunitname << " : unit does not exist in workbench visibility" << endm;
      return;
    }

  // construction du graphe des clients 

  for(i=1; i<=aseq->Length(); i++)
    {
      Handle(WOKernel_UnitNesting) nesting = asession->GetUnitNesting(aseq->Value(i));

      if(!nesting.IsNull())
	{
	  nesting->Open();
	  uds = nesting->Units();
      
	  for(j=1; j<=uds->Length(); j++)
	    {
	      Handle(WOKernel_DevUnit) anud;
	      
	      anud = asession->GetDevUnit(uds->Value(j));
	      
	      if(!anud.IsNull())
		{
		  anudname = anud->Name();
		  if (!treated.Contains(anudname))
		    {
		      suppliers = anud->ImplementationDepList(agraph);
		      
		      if (!suppliers.IsNull())
			{
			  for (k=1; k<=suppliers->Length(); k++)
			    {
			      asuppliername = suppliers->Value(k);
			      clientgraph->Add(asuppliername,anudname);
			    }
			}
		      else 
			{
			  ErrorMsg() << "WOKAPI_Workbench::ImplClients"
			    << "Failed during getting clients of " << aunitname << endm;
			  return;
			}
		      
		      clientgraph->Add(anudname,new TColStd_HSequenceOfHAsciiString); //toutes les Uds doivent apparaitre 
		      treated.Add(anudname);
		    }
		}
	    }
	}
    }

  // 
  clients = aunit->ImplClients(clientgraph);

  Handle(WOKernel_DevUnit) aclient;
  WOKAPI_Unit apiunit;

  if (!clients.IsNull())
    {
      for(i=1; i<=clients->Length(); i++)
	{
	  aclient = alocator->LocateDevUnit(clients->Value(i));
	  if (!aclient.IsNull())
	    {
	      apiunit.Set(aclient);
	      unitseq.Append(apiunit);
	    }
	}
    }
  return; 
}

//=======================================================================
//function : SortUnitList
//purpose  : order a given list by Implementation dependences
//=======================================================================
void WOKAPI_Workbench::SortUnitList(const Handle(TColStd_HSequenceOfHAsciiString)& unitlist,
				     Handle(TColStd_HSequenceOfHAsciiString)& sortedunitlist) const
{
  if(!IsValid()) return;
  if(!myEntity->IsOpened()) myEntity->Open();

  Standard_Integer i;
  Handle(WOKernel_Workbench) abench = *((Handle(WOKernel_Workbench) *) &myEntity);
  Handle(WOKernel_Locator)   alocator = new WOKernel_Locator(abench);
  Handle(WOKernel_UnitGraph) agraph   = new WOKernel_UnitGraph(alocator);
  WOKTools_MapOfHAsciiString amap;

  Handle(TColStd_HSequenceOfHAsciiString)  aunitseq  = new TColStd_HSequenceOfHAsciiString;
  Handle(TColStd_HSequenceOfHAsciiString)  anexecseq = new TColStd_HSequenceOfHAsciiString;

  for (i=1 ; i<=unitlist->Length(); i++)
    {
      Handle(TCollection_HAsciiString)        aunitname = unitlist->Value(i);
      Handle(WOKernel_DevUnit)                aunit = alocator->LocateDevUnit(aunitname);
      
      if (aunit.IsNull())
	{ 
	  // perhaps it's an executable name instead of a unit name
	  Handle(TCollection_HAsciiString) anexecname;
	  Handle(TCollection_HAsciiString) apart;
	  
	  anexecname = aunitname->Token(":",1);
	  apart      = aunitname->Token(":",2);
	  
	  aunit = alocator->LocateDevUnit(anexecname);
	  
	  if (aunit.IsNull())
	    {
	      ErrorMsg() << "WOKAPI_Workbench::SortUnitList"
		       << "Unit " << anexecname << " : unit does not exist in workbench visibility" << endm;
	      return;
	    }
	  else 
	    {
	      if(!WOKernel_IsExecutable(aunit))
		{
		  ErrorMsg() << "WOKAPI_Workbench::SortUnitList"
	                   << "Unit " << anexecname << " is not an executable" << endm;
		  return;
		}

	      anexecname->AssignCat("_");
	      anexecname->AssignCat(apart);

	      Handle(TColStd_HSequenceOfHAsciiString) execsuppliers = aunit->ImplementationDep(apart,agraph);
	      if ( execsuppliers.IsNull() )
		{
		  ErrorMsg() << "WOKAPI_Workbench::SortUnitList"
		           << "Failed during getting sorted unit list" << endm;
		  return;
		}

	      anexecseq->Append(anexecname);
	    }
	}
      else
	{
	  aunitseq->Append(aunitname);

	  if (!amap.Contains(aunitname))
	    amap.Add(aunitname);
	}
    }

  Handle(TCollection_HAsciiString) avirtualhead = new TCollection_HAsciiString("HEAD");

  Handle(TColStd_HSequenceOfHAsciiString) unitsuppliers = WOKernel_DevUnit::ImplementationDep(agraph,avirtualhead,aunitseq);

  if ( unitsuppliers.IsNull() )
    {
      ErrorMsg() << "WOKAPI_Workbench::SortUnitList"
	       << "Failed during getting sorted unit list" << endm;
      return;
    }

  sortedunitlist = new TColStd_HSequenceOfHAsciiString;

  for (i=1; i<unitsuppliers->Length(); i++)
    {
      Handle(TCollection_HAsciiString) asupplier = unitsuppliers->Value(i);
      if (amap.Contains(asupplier))
	{
	  sortedunitlist->Append(asupplier);
	}
    }
  sortedunitlist->Append(anexecseq);
}
