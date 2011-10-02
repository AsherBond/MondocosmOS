// File:	WOKAPI_Warehouse.cxx
// Created:	Wed Mar 27 11:08:38 1996
// Author:	Jean GAUTIER
//		<jga@cobrax>




#include <Standard_ErrorHandler.hxx>

#include <TCollection_AsciiString.hxx>
#include <TCollection_HAsciiString.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Options.hxx>
#include <WOKTools_Return.hxx>
#include <WOKTools_Messages.hxx>
#include <WOKTools_Define.hxx>
#include <WOKTools_HSequenceOfDefine.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKUtils_HSequenceOfParamItem.hxx>
#include <WOKUtils_ParamItem.hxx>

#include <WOKernel_Session.hxx>
#include <WOKernel_Factory.hxx>
#include <WOKernel_Warehouse.hxx>

#include <WOKAPI_Parcel.hxx>
#include <WOKAPI_Factory.hxx>
#include <WOKAPI_Session.hxx>

#include <WOKAPI_Warehouse.ixx>

//=======================================================================
//function : WOKAPI_Warehouse
//purpose  : 
//=======================================================================
WOKAPI_Warehouse::WOKAPI_Warehouse()
{
}

//=======================================================================
//function : WOKAPI_Warehouse
//purpose  : 
//=======================================================================
WOKAPI_Warehouse::WOKAPI_Warehouse(const WOKAPI_Entity& anent)
  : WOKAPI_Entity(anent)
{
}

//=======================================================================
//function : WOKAPI_Warehouse
//purpose  : 
//=======================================================================
WOKAPI_Warehouse::WOKAPI_Warehouse(const WOKAPI_Session& asession, 
				   const Handle(TCollection_HAsciiString)& apath,
				   const Standard_Boolean fatal, const Standard_Boolean getit)
{
  Set(asession.GetWarehouse(apath,fatal,getit));
}

//=======================================================================
//function : IsValid
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Warehouse::IsValid() const
{
  if(myEntity.IsNull()) return Standard_False;
  return myEntity->IsKind(STANDARD_TYPE(WOKernel_Warehouse)); 
}


//=======================================================================
//function : BuildParameters
//purpose  : 
//=======================================================================
Handle(WOKUtils_HSequenceOfParamItem) WOKAPI_Warehouse::BuildParameters(const WOKAPI_Session& asession,
								       const Handle(TCollection_HAsciiString)& apath, 
								       const Handle(WOKTools_HSequenceOfDefine)& defines, 
								       const Standard_Boolean usedefaults)  
{
  Handle(WOKUtils_HSequenceOfParamItem) aseq;
  Handle(TCollection_HAsciiString) name;
  Handle(TCollection_HAsciiString) nestname;
  Handle(WOKernel_Warehouse)       Kware;
  Handle(WOKernel_Factory)         Kfact;

  name     = BuildName(apath);
  nestname = BuildNesting(apath);

  WOKAPI_Factory afact(asession,nestname);

  if(!afact.IsValid())
    {
      ErrorMsg() << "WOKAPI_Warehouse::Build"
	       << "Invalid nesting (" << nestname << ") to create Warehouse : " << name << endm;
      return aseq;
    }

  Kfact =  Handle(WOKernel_Factory)::DownCast(afact.Entity());

  Kware = new WOKernel_Warehouse(name,Kfact);
  Set(Kware);

  aseq = GetBuildParameters(asession, name, afact, defines, usedefaults);
  
  return aseq;
}

//=======================================================================
//function : Build
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Warehouse::Build(const WOKAPI_Session& asession, 
					const Handle(TCollection_HAsciiString)& apath, 
					const Handle(WOKTools_HSequenceOfDefine)& defines, 
					const Standard_Boolean usedefaults) 
		
{
  Handle(TCollection_HAsciiString) name;
  Handle(TCollection_HAsciiString) nestname;
  Handle(WOKernel_Warehouse)       Kware;
  Handle(WOKernel_Factory)         Kfact;

  name     = BuildName(apath);
  nestname = BuildNesting(apath);

  WOKAPI_Factory afact(asession,nestname);

  if(!afact.IsValid())
    {
      ErrorMsg() << "WOKAPI_Warehouse::Build"
	       << "Invalid nesting (" << nestname << ") to create Warehouse : " << name << endm;
      return Standard_True;
    }

  Kfact =  Handle(WOKernel_Factory)::DownCast(afact.Entity());
  UpdateBeforeBuild(Kfact);

  Kware = new WOKernel_Warehouse(name,Kfact);
  Set(Kware);

  if(!BuildEntity(asession, name, afact, defines, usedefaults))
    {
      Kfact->Session()->AddEntity(Kware);
      Kfact->SetWarehouse(Kware);
      Kware->Open();
    }
  else return Standard_True;
  return Standard_False;
}



//=======================================================================
//function : NestedEntities
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Warehouse::NestedEntities(WOKAPI_SequenceOfEntity& aseq) const
{
  if(!IsValid()) return Standard_False;
  if(!myEntity->IsOpened()) myEntity->Open();

  aseq.Clear();

  Handle(TColStd_HSequenceOfHAsciiString) fullseq;
  Standard_Integer i;
  
  Handle(WOKernel_Session)   asession = myEntity->Session();
  Handle(WOKernel_Warehouse) aware    = Handle(WOKernel_Warehouse)::DownCast(myEntity);
  Handle(WOKernel_Parcel)    aparcel;

  fullseq = aware->Parcels();

  WOKAPI_Parcel apiparcel;

  for(i=1; i<=fullseq->Length(); i++)
    {
      apiparcel.Set(asession->GetParcel(fullseq->Value(i)));
      aseq.Append(apiparcel);
    }

  return Standard_True;
}

//=======================================================================
//function : Parcels
//purpose  : 
//=======================================================================
void WOKAPI_Warehouse::Parcels( WOKAPI_SequenceOfParcel& parcels ) const
{
  Handle(TColStd_HSequenceOfHAsciiString) fullseq;
  Standard_Integer i;
  
  parcels.Clear();
  
  if(!IsValid()) return ;
  if(!myEntity->IsOpened()) myEntity->Open();

  Handle(WOKernel_Session)   asession = myEntity->Session();
  Handle(WOKernel_Warehouse) aware    = Handle(WOKernel_Warehouse)::DownCast(myEntity);
  Handle(WOKernel_Parcel)    aparcel;

  fullseq = aware->Parcels();

  WOKAPI_Parcel apiparcel;

  for(i=1; i<=fullseq->Length(); i++)
    {
      apiparcel.Set(asession->GetParcel(fullseq->Value(i)));
      parcels.Append(apiparcel);
    }
  return;
}

//=======================================================================
//function : Destroy
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Warehouse::Destroy()
{
  if(!IsValid()) return Standard_True;

  Handle(WOKernel_Factory)   afactory   = myEntity->Session()->GetFactory(myEntity->Nesting());

  UpdateBeforeDestroy(afactory);
  if (!IsValid()) return Standard_True;
  if(!myEntity->IsOpened()) myEntity->Open();

  Handle(WOKernel_Warehouse) awarehouse = Handle(WOKernel_Warehouse)::DownCast(myEntity);
  awarehouse->Open();

  if(awarehouse->Parcels()->Length())
    {
      ErrorMsg() << "WOKAPI_Warehouse::Destroy"
	       << "Cannot destroy not empty warehouse" << endm;
      return Standard_True;
    }

  awarehouse->Destroy();

  //afactory->RemoveWarehouse(awarehouse);
  myEntity.Nullify();
  return Standard_False;
}

