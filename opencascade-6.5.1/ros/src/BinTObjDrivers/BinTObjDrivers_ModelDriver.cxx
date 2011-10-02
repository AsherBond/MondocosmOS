// File      : BinTObjDrivers_ModelDriver.cxx
// Created   : Wed Nov 24 11:27:45 2004
// Author    : Edward AGAPOV
// Copyright:   Open CASCADE  2007
// The original implementation Copyright: (C) RINA S.p.A

#include <BinTObjDrivers_ModelDriver.hxx>

#include <BinObjMgt_Persistent.hxx>
#include <CDM_MessageDriver.hxx>
#include <Standard_GUID.hxx>
#include <TDF_Attribute.hxx>

#include <TObj_TModel.hxx>
#include <TObj_Model.hxx>
#include <TObj_Assistant.hxx>

IMPLEMENT_STANDARD_HANDLE(BinTObjDrivers_ModelDriver,BinMDF_ADriver)
IMPLEMENT_STANDARD_RTTIEXT(BinTObjDrivers_ModelDriver,BinMDF_ADriver);

//=======================================================================
//function : BinTObjDrivers_ModelDriver
//purpose  : constructor
//=======================================================================

BinTObjDrivers_ModelDriver::BinTObjDrivers_ModelDriver
                         (const Handle(CDM_MessageDriver)& theMessageDriver)
: BinMDF_ADriver( theMessageDriver, NULL)
{
}

//=======================================================================
//function : NewEmpty
//purpose  : Creates a new attribute
//=======================================================================

Handle(TDF_Attribute) BinTObjDrivers_ModelDriver::NewEmpty() const
{
  return new TObj_TModel;
}

//=======================================================================
//function : Paste
//purpose  : Translate the contents of <theSource> and put it
//           into <theTarget>.
//           Set CurrentModel of TObj_Assistant into theTarget TObj_TModel
//           if its GUID and GUID stored in theSource are same
//=======================================================================

Standard_Boolean BinTObjDrivers_ModelDriver::Paste
                         (const BinObjMgt_Persistent&  theSource,
                          const Handle(TDF_Attribute)& theTarget,
                          BinObjMgt_RRelocationTable&) const
{
  Standard_GUID aGUID;
  if (! (theSource >> aGUID)) return Standard_False;

  Handle(TObj_Model) aCurrentModel = TObj_Assistant::GetCurrentModel();
  if (aCurrentModel.IsNull()) return Standard_False;

  if (aGUID != aCurrentModel->GetGUID())
  {
    WriteMessage("TObj_TModel retrieval: wrong model GUID");
    return Standard_False;
  }

  Handle(TObj_TModel) aTModel = Handle(TObj_TModel)::DownCast( theTarget );
  aCurrentModel->SetLabel ( aTModel->Label() );
  aTModel->Set( aCurrentModel );
  return Standard_True;
}

//=======================================================================
//function : Paste
//purpose  : Translate the contents of <theSource> and put it
//           into <theTarget>.
//           a Model is stored as its GUID
//=======================================================================

void BinTObjDrivers_ModelDriver::Paste
                         (const Handle(TDF_Attribute)& theSource,
                          BinObjMgt_Persistent&        theTarget,
                          BinObjMgt_SRelocationTable&) const
{
  Handle(TObj_TModel) aTModel =
    Handle(TObj_TModel)::DownCast( theSource );
  Handle(TObj_Model) aModel = aTModel->Model();
  if (!aModel.IsNull())
  {
    // Store model GUID.
    Standard_GUID aGUID = aModel->GetGUID();
    theTarget << aGUID;
  }
}
