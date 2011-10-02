// File:	WOKAPI_Workshop.cxx
// Created:	Tue Aug  1 18:53:50 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <WOKAPI_Workshop.ixx>

#include <WOKAPI_Session.hxx>
#include <WOKAPI_Factory.hxx>
#include <WOKAPI_Warehouse.hxx>
#include <WOKAPI_Workbench.hxx>
#include <WOKAPI_SequenceOfWorkbench.hxx>
#include <WOKAPI_Parcel.hxx>
#include <WOKAPI_SequenceOfParcel.hxx>

#include <WOKernel_Session.hxx>
#include <WOKernel_Factory.hxx>
#include <WOKernel_Parcel.hxx>
#include <WOKernel_Workshop.hxx>
#include <WOKernel_Workbench.hxx>
#include <WOKernel_DBMSystem.hxx>
#include <WOKernel_Station.hxx>
#include <WOKernel_FileTypeBase.hxx>

#include <WOKTools_Options.hxx>
#include <WOKTools_Define.hxx>
#include <WOKTools_HSequenceOfDefine.hxx>
#include <WOKTools_Return.hxx>
#include <WOKTools_Messages.hxx>
#include <WOKUtils_Path.hxx>
#include <WOKUtils_HSequenceOfParamItem.hxx>
#include <WOKUtils_ParamItem.hxx>

#include <TCollection_HAsciiString.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <Standard_ErrorHandler.hxx>

//=======================================================================
//function : WOKAPI_Workshop
//purpose  : 
//=======================================================================
WOKAPI_Workshop::WOKAPI_Workshop()
{
}

///=======================================================================
//function : WOKAPI_Workshop
//purpose  : 
//=======================================================================
WOKAPI_Workshop::WOKAPI_Workshop(const WOKAPI_Entity& anent)
  : WOKAPI_Entity(anent)
{
}

//=======================================================================
//function : WOKAPI_Workshop
//purpose  : 
//=======================================================================
WOKAPI_Workshop::WOKAPI_Workshop(const WOKAPI_Session& asession, 
				 const Handle(TCollection_HAsciiString)& apath,
				 const Standard_Boolean fatal, const Standard_Boolean getit)
{
  Set(asession.GetWorkshop(apath,fatal,getit));
}

//=======================================================================
//function : IsValid
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Workshop::IsValid() const
{
  if(myEntity.IsNull()) return Standard_False;
  return myEntity->IsKind(STANDARD_TYPE(WOKernel_Workshop)); 
}

//=======================================================================
//function : NestedEntities
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Workshop::NestedEntities(WOKAPI_SequenceOfEntity& aseq) const
{
  if(!IsValid()) return Standard_False;
  if(!myEntity->IsOpened()) myEntity->Open();

  aseq.Clear();
 
  Handle(WOKernel_Workbench) abench;
  Handle(WOKernel_Workshop) theshop  = Handle(WOKernel_Workshop)::DownCast(myEntity);
  Handle(WOKernel_Session)  asession = theshop->Session();

  Handle(TColStd_HSequenceOfHAsciiString) fullseq = theshop->Workbenches();

  if (!fullseq.IsNull()) 
    {
      WOKAPI_Workbench apibench;
      
      for(Standard_Integer i=1; i<=fullseq->Length(); i++)
	{
	  apibench.Set(asession->GetWorkbench(fullseq->Value(i)));
	  aseq.Append(apibench);
	}   
    }   

  return Standard_True;
}

//=======================================================================
//function : Workbenches
//purpose  : 
//=======================================================================
void WOKAPI_Workshop::Workbenches(WOKAPI_SequenceOfWorkbench& benchseq) const
{
  Handle(TColStd_HSequenceOfHAsciiString) fullseq;
  Standard_Integer i;

  benchseq.Clear();

  if(!IsValid()) return;
  if(!myEntity->IsOpened()) myEntity->Open();

  Handle(WOKernel_Workbench) abench;
  Handle(WOKernel_Workshop) theshop  = Handle(WOKernel_Workshop)::DownCast(myEntity);
  Handle(WOKernel_Session)  asession = theshop->Session();

  fullseq = theshop->Workbenches();

  if (!fullseq.IsNull()) {
    WOKAPI_Workbench apibench;
    
    for(i=1; i<=fullseq->Length(); i++)
      {
	apibench.Set(asession->GetWorkbench(fullseq->Value(i)));
	benchseq.Append(apibench);
      }
  }
}

//=======================================================================
//function : UsedParcels
//purpose  : 
//=======================================================================
void WOKAPI_Workshop::UsedParcels(WOKAPI_SequenceOfParcel& parcseq) const
{
  Handle(TColStd_HSequenceOfHAsciiString) fullseq;
  Standard_Integer i;
  
  parcseq.Clear();

  if(!IsValid()) return ;
  if(!myEntity->IsOpened()) myEntity->Open();

  Handle(WOKernel_Parcel)   aparcel;
  Handle(WOKernel_Workshop) theshop  = Handle(WOKernel_Workshop)::DownCast(myEntity);
  Handle(WOKernel_Session)  asession = theshop->Session();

  fullseq = theshop->ParcelsInUse();

  WOKAPI_Parcel apiparcel;

  for(i=1; i<=fullseq->Length(); i++)
    {
      apiparcel.Set(asession->GetParcel(fullseq->Value(i)));
      parcseq.Append(apiparcel);
    }

  return; 
}

//=======================================================================
//function : BuildParameters
//purpose  : 
//=======================================================================
Handle(WOKUtils_HSequenceOfParamItem) WOKAPI_Workshop::BuildParameters(const WOKAPI_Session& asession,
								       const Handle(TCollection_HAsciiString)& apath, 
								       const Handle(WOKTools_HSequenceOfDefine)& defines, 
								       const Standard_Boolean usedefaults)  
{
  Handle(WOKUtils_HSequenceOfParamItem) aseq;
  Handle(TCollection_HAsciiString) name;
  Handle(TCollection_HAsciiString) nestname;
  Handle(WOKernel_Workshop)        Kshop;
  Handle(WOKernel_Factory)         Kfact;

  name     = BuildName(apath);
  nestname = BuildNesting(apath);

  WOKAPI_Factory afact(asession,nestname);

  if(!afact.IsValid())
    {
      ErrorMsg() << "WOKAPI_Workshop::Build"
	       << "Invalid nesting (" << nestname << ") to create workshop : " << name << endm;
      return aseq;
    }

  WOKAPI_Warehouse aware = afact.Warehouse();
  if(!aware.IsValid())
    {
      ErrorMsg() << "WOKAPI_Workshop::Build"
	       << "No valid warehouse in factory : " << afact.Name() << endm;
      return aseq;
    }

  Kfact =  Handle(WOKernel_Factory)::DownCast(afact.Entity());

  Kshop = new WOKernel_Workshop(name,Kfact);
  Set(Kshop);

  aseq = GetBuildParameters(asession, name, afact, defines, usedefaults);
  
  return aseq;
}

//=======================================================================
//function : Build
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Workshop::Build(const WOKAPI_Session& asession, 
					const Handle(TCollection_HAsciiString)& apath, 
					const Handle(WOKTools_HSequenceOfDefine)& defines, 
					const Standard_Boolean usedefaults) 
		
{
  Handle(TCollection_HAsciiString) name;
  Handle(TCollection_HAsciiString) nestname;
  Handle(WOKernel_Workshop)        Kshop;
  Handle(WOKernel_Factory)         Kfact;

  name     = BuildName(apath);
  nestname = BuildNesting(apath);

  WOKAPI_Factory afact(asession,nestname);

  if(!afact.IsValid())
    {
      ErrorMsg() << "WOKAPI_Workshop::Build"
	       << "Invalid nesting (" << nestname << ") to create workshop : " << name << endm;
      return Standard_True;
    }

  WOKAPI_Warehouse aware = afact.Warehouse();

  if(!aware.IsValid())
    {
      ErrorMsg() << "WOKAPI_Workshop::Build"
	       << "No valid warehouse in factory : " << afact.Name() << endm;
      return Standard_True;
    }

  Kfact =  Handle(WOKernel_Factory)::DownCast(afact.Entity());
  UpdateBeforeBuild(Kfact);

  Kshop = new WOKernel_Workshop(name,Kfact);
  Set(Kshop);

  if(!BuildEntity(asession, name, afact, defines, usedefaults))
    {
      Kfact->AddWorkshop(Kshop);
      Kshop->Open();
    }
  else return Standard_True;
  return Standard_False;
}

//=======================================================================
//function : Destroy
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Workshop::Destroy()
{
  if(!IsValid()) return Standard_True;
  if(!myEntity->IsOpened()) myEntity->Open();

  Handle(WOKernel_Factory)  afactory = myEntity->Session()->GetFactory(myEntity->Nesting());

  UpdateBeforeDestroy(afactory);
  if (!IsValid()) return Standard_True;

  Handle(WOKernel_Workshop) aworkshop = Handle(WOKernel_Workshop)::DownCast(myEntity);
  aworkshop->Open();

  if(aworkshop->Workbenches()->Length())
    {
      ErrorMsg() << "WOKAPI_Workshop::Destroy"
	       << "Cannot destroy not empty workshop" << endm;
      return Standard_True;
    }

  aworkshop->Destroy();

  afactory->RemoveWorkshop(aworkshop);
  myEntity.Nullify();
  return Standard_False;
}



