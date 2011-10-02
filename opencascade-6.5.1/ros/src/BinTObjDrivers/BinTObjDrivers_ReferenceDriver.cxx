// File      : BinTObjDrivers_ReferenceDriver.cxx
// Created   : Wed Nov 24 11:42:17 2004
// Author    : Edward AGAPOV
// Copyright:   Open CASCADE  2007
// The original implementation Copyright: (C) RINA S.p.A


#include <BinTObjDrivers_ReferenceDriver.hxx>
#include <CDM_MessageDriver.hxx>
#include <TDF_Attribute.hxx>
#include <TObj_TReference.hxx>
#include <BinObjMgt_Persistent.hxx>
#include <TObj_Object.hxx>
#include <TObj_Model.hxx>
#include <TObj_Assistant.hxx>
#include <TDF_ChildIterator.hxx>
#include <TDF_Tool.hxx>

IMPLEMENT_STANDARD_HANDLE(BinTObjDrivers_ReferenceDriver,BinMDF_ADriver)
IMPLEMENT_STANDARD_RTTIEXT(BinTObjDrivers_ReferenceDriver,BinMDF_ADriver)

//=======================================================================
//function : BinTObjDrivers_ReferenceDriver
//purpose  : constructor
//=======================================================================

BinTObjDrivers_ReferenceDriver::BinTObjDrivers_ReferenceDriver
                         (const Handle(CDM_MessageDriver)& theMessageDriver)
: BinMDF_ADriver( theMessageDriver, NULL)
{
}

//=======================================================================
//function : NewEmpty
//purpose  : Creates a new attribute
//=======================================================================

Handle(TDF_Attribute) BinTObjDrivers_ReferenceDriver::NewEmpty() const
{
  return new TObj_TReference;
}

//=======================================================================
//function : Paste
//purpose  : Translate the contents of <theSource> and put it
//           into <theTarget>.
//=======================================================================

Standard_Boolean BinTObjDrivers_ReferenceDriver::Paste
                         (const BinObjMgt_Persistent&  theSource,
                          const Handle(TDF_Attribute)& theTarget,
                          BinObjMgt_RRelocationTable&) const
{
  // master label
  TDF_Label aMasterLabel;
  Handle(TDF_Data) aDS = theTarget->Label().Data();
  if (! theSource.GetLabel (aDS, aMasterLabel)) return Standard_False;

  // isSameDoc flag
  Standard_Boolean isSameDoc = Standard_False;
  if (! (theSource >> isSameDoc)) return Standard_False;

  // DS for referred label
  if (!isSameDoc) 
  {
    TCollection_AsciiString aName;
    if (! (theSource >> aName)) return Standard_False;
    Handle(TObj_Model) aModel = Handle(TObj_Model)::DownCast
      ( TObj_Assistant::FindModel( aName.ToCString() ));
    if (aModel.IsNull())
    {
      TCollection_AsciiString anEntry;
      TDF_Tool::Entry (theTarget->Label(), anEntry);
      WriteMessage (TCollection_ExtendedString ("TObj_TReference retrieval: ")
                    + "wrong model ID " + aName + ", entry " + anEntry);
      return Standard_False;
    }
    aDS = aModel->GetLabel().Data();
  }
  // reffered label
  TDF_Label aLabel;
  if (! theSource.GetLabel (aDS, aLabel)) return Standard_False;

  // set reference attribute fields
  Handle(TObj_TReference) aTarget =
    Handle(TObj_TReference)::DownCast (theTarget);
  aTarget->Set ( aLabel, aMasterLabel );

  return !aLabel.IsNull() && !aMasterLabel.IsNull();
}

//=======================================================================
//function : Paste
//purpose  : Translate the contents of <theSource> and put it
//           into <theTarget>.
//           Store master and referred labels as entry, the other model referred
//           as entry in model-container
//=======================================================================

void BinTObjDrivers_ReferenceDriver::Paste
                         (const Handle(TDF_Attribute)& theSource,
                          BinObjMgt_Persistent&        theTarget,
                          BinObjMgt_SRelocationTable&) const
{
  Handle(TObj_TReference) aSource =
    Handle(TObj_TReference)::DownCast (theSource);

  Handle(TObj_Object) aLObject = aSource->Get();
  if (aLObject.IsNull())
    return;

  // labels
  TDF_Label aLabel = aLObject->GetLabel();
  TDF_Label aMasterLabel = aSource->GetMasterLabel();
  Standard_Boolean isSameDoc = (aLabel.Root() == aMasterLabel.Root());

  // store data
  // 1 - the master label;
  theTarget << aMasterLabel;
  // 2 - isSameDoc flag plus may be a Model ID
  theTarget << isSameDoc;
  if (! isSameDoc)
  {
    TCollection_AsciiString aName;
    Handle(TObj_Model) aModel =
      Handle(TObj_Model)::DownCast( aLObject->GetModel() );
    aName = TCollection_AsciiString( aModel->GetModelName()->String() );
    theTarget << aName;
  }
  // 3 - referred label;
  theTarget << aLabel;
}
