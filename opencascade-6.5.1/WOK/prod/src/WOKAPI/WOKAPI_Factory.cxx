// File:	WOKAPI_Factory.cxx
// Created:	Tue Aug  1 18:52:43 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <Standard_ErrorHandler.hxx>

#include <TCollection_AsciiString.hxx>
#include <TCollection_HAsciiString.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Messages.hxx>
#include <WOKTools_Define.hxx>
#include <WOKTools_HSequenceOfDefine.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKUtils_HSequenceOfParamItem.hxx>
#include <WOKUtils_ParamItem.hxx>

#include <WOKernel_Session.hxx>
#include <WOKernel_Factory.hxx>
#include <WOKernel_Workshop.hxx>
#include <WOKernel_Warehouse.hxx>

#include <WOKAPI_Session.hxx>
#include <WOKAPI_Workshop.hxx>
#include <WOKAPI_SequenceOfWorkshop.hxx>

#include <WOKAPI_Factory.ixx>


//=======================================================================
//function : WOKAPI_Factory
//purpose  : 
//=======================================================================
WOKAPI_Factory::WOKAPI_Factory()
{
}

//=======================================================================
//function : WOKAPI_Factory
//purpose  : 
//=======================================================================
WOKAPI_Factory::WOKAPI_Factory(const WOKAPI_Entity& anent)
  : WOKAPI_Entity(anent)
{
}

//=======================================================================
//function : WOKAPI_Factory
//purpose  : 
//=======================================================================
WOKAPI_Factory::WOKAPI_Factory(const WOKAPI_Session& asession, 
			       const Handle(TCollection_HAsciiString)& apath,
			       const Standard_Boolean fatal, const Standard_Boolean getit)
{
  Set(asession.GetFactory(apath,fatal,getit));
}

//=======================================================================
//function : Build
//purpose  : 
//=======================================================================
Handle(WOKUtils_HSequenceOfParamItem) WOKAPI_Factory::BuildParameters(const WOKAPI_Session& asession,
								      const Handle(TCollection_HAsciiString)& apath, 
								      const Handle(WOKTools_HSequenceOfDefine)& defines, 
								      const Standard_Boolean usedefaults)  
{
  Handle(TCollection_HAsciiString) name;
  Handle(WOKUtils_HSequenceOfParamItem) aseq;
  
  name = BuildName(apath);
  
  Handle(WOKernel_Factory) Kfact    = new WOKernel_Factory(name, asession.Session());
  
  Set(Kfact);
  
  aseq = GetBuildParameters(asession, name, asession, defines, usedefaults);
  
  return aseq;
}

//=======================================================================
//function : Build
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Factory::Build(const WOKAPI_Session& asession, 
					const Handle(TCollection_HAsciiString)& apath, 
					const Handle(WOKTools_HSequenceOfDefine)& defines, 
					const Standard_Boolean usedefaults) 
		
{
  Handle(TCollection_HAsciiString) name;
  Handle(WOKernel_Session) Ksession = Handle(WOKernel_Session)::DownCast(asession.Entity());

  name     = BuildName(apath);

  if(!asession.IsValid())
    {
      ErrorMsg() << "WOKAPI_Factory::Build"
	       << "Invalid session to create factory : " << name << endm;
      return Standard_True;
    }
  Handle(WOKernel_Factory) Kfact    = new WOKernel_Factory(name, Ksession);

  Set(Kfact);
  
  if(!BuildEntity(asession, name, asession, defines, usedefaults))
    {
      Ksession->AddFactory(Kfact);
      Kfact->Open();
    } 
  else return Standard_True;
  return Standard_False;
}

//=======================================================================
//function : Destroy
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Factory::Destroy()
{
  if(!IsValid()) return Standard_True;

  if(!myEntity->IsOpened()) myEntity->Open();
  
  Handle(WOKernel_Factory) afact = Handle(WOKernel_Factory)::DownCast(myEntity);
  Handle(WOKernel_Session) asession = afact->Session();

  if(afact->Workshops()->Length())
    {
      ErrorMsg() << "WOKAPI_Factory::Destroy"
	       << "Cannot destroy not empty factory" << endm;
      return Standard_True;
    }

  afact->Destroy();

  asession->RemoveFactory(afact);
  myEntity.Nullify();
  return Standard_False;
}

//=======================================================================
//function : IsValid
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Factory::IsValid() const
{
  if(myEntity.IsNull()) return Standard_False;
  return myEntity->IsKind(STANDARD_TYPE(WOKernel_Factory));
}

//=======================================================================
//function : NestedEntities
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_Factory::NestedEntities(WOKAPI_SequenceOfEntity& aseq) const
{
  if(!IsValid()) return Standard_False;
  if(!myEntity->IsOpened()) myEntity->Open();
  
  aseq.Clear();

  Handle(WOKernel_Factory)                afact = Handle(WOKernel_Factory)::DownCast(myEntity);
  Handle(WOKernel_Workshop)               ashop;
  Handle(WOKernel_Session)                asession;
  Handle(TCollection_HAsciiString)        astr;
  Handle(TColStd_HSequenceOfHAsciiString) aKseq;
  Standard_Integer len, i;
  
  asession = afact->Session();
  aKseq     = afact->Workshops();
  len      = aKseq->Length();
  
  WOKAPI_Workshop apishop;
  
  for(i=1; i<=len; i++)
    {
      astr = aKseq->Value(i);
      
      apishop.Set(asession->GetWorkshop(astr));
      
      aseq.Append(apishop);
    }
  return Standard_True;
}


//=======================================================================
//function : Workshops
//purpose  : 
//=======================================================================
void WOKAPI_Factory::Workshops(WOKAPI_SequenceOfWorkshop& shopseq) const
{
  
  shopseq.Clear();

  if(IsValid())
    {
      if(!myEntity->IsOpened()) myEntity->Open();

      Handle(WOKernel_Factory)                afact = Handle(WOKernel_Factory)::DownCast(myEntity);
      Handle(WOKernel_Workshop)               ashop;
      Handle(WOKernel_Session)                asession;
      Handle(TCollection_HAsciiString)        astr;
      Handle(TColStd_HSequenceOfHAsciiString) aseq;
      Standard_Integer len, i;
      
      asession = afact->Session();
      aseq     = afact->Workshops();
      len      = aseq->Length();
	

      WOKAPI_Workshop apishop;

      for(i=1; i<=len; i++)
	{
	  astr = aseq->Value(i);

	  apishop.Set(asession->GetWorkshop(astr));

	  shopseq.Append(apishop);
	}
    }
  return;
}

//=======================================================================
//function : Warehouse
//purpose  : 
//=======================================================================
WOKAPI_Warehouse WOKAPI_Factory::Warehouse() const
{
  WOKAPI_Warehouse aware;

  if(IsValid())
    {
      if(!myEntity->IsOpened()) myEntity->Open();

      Handle(WOKernel_Warehouse) KWare;
      Handle(WOKernel_Factory)   afact    = Handle(WOKernel_Factory)::DownCast(myEntity);
      Handle(WOKernel_Session)   asession = afact->Session();
						
      aware.Set(asession->GetWarehouse(afact->Warehouse()));      
    }
  return aware;
}
