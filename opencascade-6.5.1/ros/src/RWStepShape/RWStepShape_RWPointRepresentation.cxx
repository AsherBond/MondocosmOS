// File:	RWStepShape_RWPointRepresentation.cxx
// Created:	Thu Dec 12 15:38:08 2002 
// Author:	data exchange team
// Generator:	ExpToCas (EXPRESS -> CASCADE/XSTEP Translator) V1.2
// Copyright:	Open CASCADE 2002

#include <RWStepShape_RWPointRepresentation.ixx>
#include <StepRepr_HArray1OfRepresentationItem.hxx>
#include <StepRepr_RepresentationItem.hxx>

//=======================================================================
//function : RWStepShape_RWPointRepresentation
//purpose  : 
//=======================================================================

RWStepShape_RWPointRepresentation::RWStepShape_RWPointRepresentation ()
{
}

//=======================================================================
//function : ReadStep
//purpose  : 
//=======================================================================

void RWStepShape_RWPointRepresentation::ReadStep (const Handle(StepData_StepReaderData)& data,
                                                  const Standard_Integer num,
                                                  Handle(Interface_Check)& ach,
                                                  const Handle(StepShape_PointRepresentation) &ent) const
{
  // Check number of parameters
  if ( ! data->CheckNbParams(num,3,ach,"point_representation") ) return;

  // Inherited fields of Representation

  Handle(TCollection_HAsciiString) aRepresentation_Name;
  data->ReadString (num, 1, "representation.name", ach, aRepresentation_Name);

  Handle(StepRepr_HArray1OfRepresentationItem) aRepresentation_Items;
  Standard_Integer sub2 = 0;
  if ( data->ReadSubList (num, 2, "representation.items", ach, sub2) ) {
    Standard_Integer nb0 = data->NbParams(sub2);
    aRepresentation_Items = new StepRepr_HArray1OfRepresentationItem (1, nb0);
    Standard_Integer num2 = sub2;
    for ( Standard_Integer i0=1; i0 <= nb0; i0++ ) {
      Handle(StepRepr_RepresentationItem) anIt0;
      data->ReadEntity (num2, i0, "representation_item", ach, STANDARD_TYPE(StepRepr_RepresentationItem), anIt0);
      aRepresentation_Items->SetValue(i0, anIt0);
    }
  }

  Handle(StepRepr_RepresentationContext) aRepresentation_ContextOfItems;
  data->ReadEntity (num, 3, "representation.context_of_items", ach, STANDARD_TYPE(StepRepr_RepresentationContext), aRepresentation_ContextOfItems);

  // Initialize entity
  ent->Init(aRepresentation_Name,
            aRepresentation_Items,
            aRepresentation_ContextOfItems);
}

//=======================================================================
//function : WriteStep
//purpose  : 
//=======================================================================

void RWStepShape_RWPointRepresentation::WriteStep (StepData_StepWriter& SW,
                                                   const Handle(StepShape_PointRepresentation) &ent) const
{

  // Inherited fields of Representation

  SW.Send (ent->StepRepr_Representation::Name());

  SW.OpenSub();
  for (Standard_Integer i1=1; i1 <= ent->StepRepr_Representation::Items()->Length(); i1++ ) {
    Handle(StepRepr_RepresentationItem) Var0 = ent->StepRepr_Representation::Items()->Value(i1);
    SW.Send (Var0);
  }
  SW.CloseSub();

  SW.Send (ent->StepRepr_Representation::ContextOfItems());
}

//=======================================================================
//function : Share
//purpose  : 
//=======================================================================

void RWStepShape_RWPointRepresentation::Share (const Handle(StepShape_PointRepresentation) &ent,
                                               Interface_EntityIterator& iter) const
{

  // Inherited fields of Representation

  for (Standard_Integer i1=1; i1 <= ent->StepRepr_Representation::Items()->Length(); i1++ ) {
    Handle(StepRepr_RepresentationItem) Var0 = ent->StepRepr_Representation::Items()->Value(i1);
    iter.AddItem (Var0);
  }

  iter.AddItem (ent->StepRepr_Representation::ContextOfItems());
}
