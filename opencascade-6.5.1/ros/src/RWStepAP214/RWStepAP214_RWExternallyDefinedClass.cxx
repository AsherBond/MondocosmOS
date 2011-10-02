// File:	RWStepAP214_RWExternallyDefinedClass.cxx
// Created:	Wed May 10 15:09:07 2000 
// Author:	Andrey BETENEV
// Generator:	ExpToCas (EXPRESS -> CASCADE/XSTEP Translator) V1.1
// Copyright:	Matra Datavision 2000

#include <RWStepAP214_RWExternallyDefinedClass.ixx>
#include <StepBasic_ExternallyDefinedItem.hxx>

//=======================================================================
//function : RWStepAP214_RWExternallyDefinedClass
//purpose  : 
//=======================================================================

RWStepAP214_RWExternallyDefinedClass::RWStepAP214_RWExternallyDefinedClass ()
{
}

//=======================================================================
//function : ReadStep
//purpose  : 
//=======================================================================

void RWStepAP214_RWExternallyDefinedClass::ReadStep (const Handle(StepData_StepReaderData)& data,
                                                     const Standard_Integer num,
                                                     Handle(Interface_Check)& ach,
                                                     const Handle(StepAP214_ExternallyDefinedClass) &ent) const
{
  // Check number of parameters
  if ( ! data->CheckNbParams(num,4,ach,"externally_defined_class") ) return;

  // Inherited fields of Group

  Handle(TCollection_HAsciiString) aGroup_Name;
  data->ReadString (num, 1, "group.name", ach, aGroup_Name);

  Handle(TCollection_HAsciiString) aGroup_Description;
  Standard_Boolean hasGroup_Description = Standard_True;
  if ( data->IsParamDefined (num,2) ) {
    data->ReadString (num, 2, "group.description", ach, aGroup_Description);
  }
  else {
    hasGroup_Description = Standard_False;
  }

  // Inherited fields of ExternallyDefinedItem

  StepBasic_SourceItem aExternallyDefinedItem_ItemId;
  data->ReadEntity (num, 3, "externally_defined_item.item_id", ach, aExternallyDefinedItem_ItemId);

  Handle(StepBasic_ExternalSource) aExternallyDefinedItem_Source;
  data->ReadEntity (num, 4, "externally_defined_item.source", ach, STANDARD_TYPE(StepBasic_ExternalSource), aExternallyDefinedItem_Source);

  // Initialize entity
  ent->Init(aGroup_Name,
            hasGroup_Description,
            aGroup_Description,
            aExternallyDefinedItem_ItemId,
            aExternallyDefinedItem_Source);
}

//=======================================================================
//function : WriteStep
//purpose  : 
//=======================================================================

void RWStepAP214_RWExternallyDefinedClass::WriteStep (StepData_StepWriter& SW,
                                                      const Handle(StepAP214_ExternallyDefinedClass) &ent) const
{

  // Inherited fields of Group

  SW.Send (ent->StepBasic_Group::Name());

  if ( ent->StepBasic_Group::HasDescription() ) {
    SW.Send (ent->StepBasic_Group::Description());
  }
  else SW.SendUndef();

  // Inherited fields of ExternallyDefinedItem

  SW.Send (ent->ExternallyDefinedItem()->ItemId().Value());

  SW.Send (ent->ExternallyDefinedItem()->Source());
}

//=======================================================================
//function : Share
//purpose  : 
//=======================================================================

void RWStepAP214_RWExternallyDefinedClass::Share (const Handle(StepAP214_ExternallyDefinedClass) &ent,
                                                  Interface_EntityIterator& iter) const
{

  // Inherited fields of Group

  // Inherited fields of ExternallyDefinedItem

  iter.AddItem (ent->ExternallyDefinedItem()->ItemId().Value());

  iter.AddItem (ent->ExternallyDefinedItem()->Source());
}
