// File:	WOKAPI_Parcel.cxx
// Created:	Wed Apr  3 19:33:58 1996
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <TCollection_HAsciiString.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Messages.hxx>
#include <WOKTools_Define.hxx>
#include <WOKUtils_ParamItem.hxx>

#include <WOKernel_Warehouse.hxx>
#include <WOKernel_Parcel.hxx>
#include <WOKernel_Session.hxx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_BasicUnitTypes.hxx>

#include <WOKAPI_Unit.hxx>
#include <WOKAPI_SequenceOfUnit.hxx>

#include <WOKAPI_Parcel.ixx>

//=======================================================================
//function : WOKAPI_Parcel
//purpose  : 
//=======================================================================
WOKAPI_Parcel::WOKAPI_Parcel()
{
}

//=======================================================================
//function : WOKAPI_Parcel
//purpose  : 
//=======================================================================
WOKAPI_Parcel::WOKAPI_Parcel(const WOKAPI_Entity& anent)
  : WOKAPI_Entity(anent)
{
}

//=======================================================================
//function : WOKAPI_Parcel
//purpose  : 
//=======================================================================
WOKAPI_Parcel::WOKAPI_Parcel(const WOKAPI_Session& asession, 
			     const Handle(TCollection_HAsciiString)& apath,
			     const Standard_Boolean fatal, const Standard_Boolean getit)
{
  Set(asession.GetParcel(apath,fatal,getit));
}

//=======================================================================
//function : BuildParameters
//purpose  : 
//=======================================================================
Handle(WOKUtils_HSequenceOfParamItem) WOKAPI_Parcel::BuildParameters(const WOKAPI_Session& asession,
								     const Handle(TCollection_HAsciiString)& apath,
								     const Handle(WOKTools_HSequenceOfDefine)& defines,
								     const Standard_Boolean usedefaults) const 
{
  Handle(TCollection_HAsciiString) name;
  Handle(TCollection_HAsciiString) nestname;
  
  Handle(WOKUtils_HSequenceOfParamItem) aseq;

  name     = BuildName(apath);
  nestname = BuildNesting(apath);    

  WOKAPI_Entity anesting(asession, nestname);

  if (!anesting.IsValid())
    {
      ErrorMsg() << "WOKAPI_Parcel::BuildParameters"
	       << "Invalid nesting to create parcel : " << name << endm;
      return aseq;
    }

  if (myEntity.IsNull()) 
    {
      Handle(WOKernel_Parcel) aparc = new WOKernel_Parcel(name,
							  Handle(WOKernel_Warehouse)::DownCast(anesting.Entity()));
      Handle(TCollection_HAsciiString) aprefix = new TCollection_HAsciiString("%");
      aprefix->AssignCat(name);
      aprefix->AssignCat("_");
      
      Handle(WOKUtils_HSequenceOfParamItem) someparams = new WOKUtils_HSequenceOfParamItem;
      for(Standard_Integer i=1; i<= defines->Length() ; i++)
	{
	  Handle(TCollection_HAsciiString) astr = new TCollection_HAsciiString(aprefix);
	  astr->AssignCat(defines->Value(i).Name());
	  someparams->Append(WOKUtils_ParamItem(astr, defines->Value(i).Value()));
	}
      
      aseq =  aparc->BuildParameters(someparams, usedefaults);
      return aseq;
    }
  return GetBuildParameters(asession,name,anesting,defines,usedefaults);
}

//=======================================================================
//function : Declare
//purpose  : 
//=======================================================================
//Standard_Boolean WOKAPI_Parcel::Declare(const WOKAPI_Session& asession,
Standard_Boolean WOKAPI_Parcel::Declare(const WOKAPI_Session& ,
					const Handle(TCollection_HAsciiString)& name,
					const WOKAPI_Entity& anesting,
					const Handle(WOKTools_HSequenceOfDefine)& defines,
					const Standard_Boolean usedefaults)
{
  if(!myEntity.IsNull() && !myEntity->IsOpened()) myEntity->Open();


  Handle(WOKernel_Warehouse) theWarehouse = Handle(WOKernel_Warehouse)::DownCast(anesting.Entity());
  UpdateBeforeBuild(theWarehouse);
  Handle(WOKernel_Parcel) aparc = new WOKernel_Parcel(name,
						      theWarehouse);
  Handle(TCollection_HAsciiString) aprefix = new TCollection_HAsciiString("%");
  aprefix->AssignCat(name);
  aprefix->AssignCat("_");
  Handle(TCollection_HAsciiString) namedel = new TCollection_HAsciiString(aprefix->ToCString());
  namedel->AssignCat("Delivery");
  Handle(TCollection_HAsciiString) valnamedel;
  
  Handle(WOKUtils_HSequenceOfParamItem) someparams = new WOKUtils_HSequenceOfParamItem;
  for(Standard_Integer i=1; i<= defines->Length() ; i++)
    {
      Handle(TCollection_HAsciiString) astr = new TCollection_HAsciiString(aprefix);
      astr->AssignCat(defines->Value(i).Name());
      if (astr->IsSameString(namedel)) {
	valnamedel = new TCollection_HAsciiString(defines->Value(i).Value());
      }
      someparams->Append(WOKUtils_ParamItem(astr, defines->Value(i).Value()));
    }
  
  Handle(WOKUtils_HSequenceOfParamItem) aseq =  aparc->BuildParameters(someparams, usedefaults);

  if (valnamedel.IsNull()) {
    ErrorMsg() << "WOKAPI_Parcel::Declare"
	     << "Delivery name not given" << endm;
    return Standard_False;
  }
  WOKUtils_ParamItem itemdel(namedel,valnamedel);
  aseq->Append(itemdel);


  aparc->Build(aseq);

  Set(aparc);
      
  theWarehouse->AddParcel(aparc);
  return Standard_True;
}
  
  
//=======================================================================
//function : IsValid
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Parcel::IsValid() const
{
  if(myEntity.IsNull()) return Standard_False;
  return myEntity->IsKind(STANDARD_TYPE(WOKernel_Parcel)); 
}

//=======================================================================
//function : Delivery
//purpose  : 
//=======================================================================
void WOKAPI_Parcel::Delivery(WOKAPI_Unit& unit) const 
{
  if(!IsValid()) return;
  if(!myEntity->IsOpened()) myEntity->Open();

  Handle(WOKernel_Session) asession = myEntity->Session();
  Handle(WOKernel_Parcel)   aparcel = Handle(WOKernel_Parcel)::DownCast(myEntity);
  Handle(WOKernel_DevUnit)    aunit;
  Handle(WOKernel_Entity)     anent;

  Handle(TCollection_HAsciiString)  adel = aparcel->Delivery();
  Handle(TCollection_HAsciiString)  astr;
  
  if(!adel.IsNull())
    {
      aunit.Nullify();
      astr = aparcel->NestedUniqueName(adel);

      if(asession->IsKnownEntity(astr))
	{
	  aunit = asession->GetDevUnit(astr);

	  if(aunit.IsNull())
	    {
	      ErrorMsg() << "WOKAPI_Parcel::Deliveries"
		       << "Invalid name : " << adel << " in parcel " << aparcel->UserPathName() << endm;
	      return;
	    }
	  if(!WOKernel_IsDelivery(aunit))
	    {
	      ErrorMsg() << "WOKAPI_Parcel::Deliveries"
			<< "Invalid type for " << adel << " in parcel " << aparcel->UserPathName() << endm;
	      return;
	    }
	}
      else
	{
	  ErrorMsg() << "WOKAPI_Parcel::Units"
		   << "Invalid name : " << adel << " in parcel " << aparcel->UserPathName() << endm;
	  return;
	}
      if(!aunit.IsNull())
	{
	  aunit->Open();
	  unit.Set(aunit);
	}
    }
  return ;
}

//=======================================================================
//function : NestedEntities
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Parcel::NestedEntities(WOKAPI_SequenceOfEntity& aseq) const
{
  if(!IsValid()) return Standard_False;
  if(!myEntity->IsOpened()) myEntity->Open();

  aseq.Clear();

  Handle(WOKernel_Session) asession = myEntity->Session();
  Handle(WOKernel_Parcel)   aparcel = Handle(WOKernel_Parcel)::DownCast(myEntity);
  Handle(WOKernel_DevUnit)    aunit;
  Handle(WOKernel_Entity)     anent;
  WOKAPI_Unit apiunit;
  
  Handle(TColStd_HSequenceOfHAsciiString) afullseq = aparcel->Units();
  Handle(TCollection_HAsciiString)  astr;
  Standard_Integer i;
  
  for(i=1; i<=afullseq->Length(); i++)
    {
      astr = afullseq->Value(i);
      
      if(asession->IsKnownEntity(astr))
	{
	  aunit = asession->GetDevUnit(astr);
	  
	  if(aunit.IsNull())
	    {
	      ErrorMsg() << "WOKAPI_Parcel::NestedEntities"
		       << "Invalid name : " << afullseq->Value(i) << " in allcomponents of " << aparcel->UserPathName() << endm;
	      aseq.Clear();
	      return Standard_False;
	    }
	}
      else
	{
	  ErrorMsg() << "WOKAPI_Parcel::NestedEntities"
		   << "Invalid name : " << afullseq->Value(i) << " in allcomponents of " << aparcel->UserPathName() << endm;
	  aseq.Clear();
	  return Standard_False;
	}
      apiunit.Set(aunit);
      aseq.Append(apiunit);
    }
  return Standard_True;
}

//=======================================================================
//function : Units
//purpose  : 
//=======================================================================
void WOKAPI_Parcel::Units(WOKAPI_SequenceOfUnit& units) const 
{
  units.Clear();

  if(!IsValid()) return;
  if(!myEntity->IsOpened()) myEntity->Open();

  Handle(WOKernel_Session) asession = myEntity->Session();
  Handle(WOKernel_Parcel)   aparcel = Handle(WOKernel_Parcel)::DownCast(myEntity);
  Handle(WOKernel_DevUnit)    aunit;
  Handle(WOKernel_Entity)     anent;
  WOKAPI_Unit apiunit;

  Handle(TColStd_HSequenceOfHAsciiString) afullseq = aparcel->Units();
  Handle(TCollection_HAsciiString)  astr;
  Standard_Integer i;
  
  for(i=1; i<=afullseq->Length(); i++)
    {
      astr = afullseq->Value(i);

      if(asession->IsKnownEntity(astr))
	{
	  aunit = asession->GetDevUnit(astr);

	  if(aunit.IsNull())
	    {
	      ErrorMsg() << "WOKAPI_Parcel::Units"
		       << "Invalid name : " << afullseq->Value(i) << " in allcomponents of " << aparcel->UserPathName() << endm;
	      units.Clear();
	      return;
	    }
	}
      else
	{
	  ErrorMsg() << "WOKAPI_Parcel::Units"
		       << "Invalid name : " << afullseq->Value(i) << " in allcomponents of " << aparcel->UserPathName() << endm;
	  units.Clear();
	  return;
	}
      apiunit.Set(aunit);
      units.Append(apiunit);
    }
  return;  
}

