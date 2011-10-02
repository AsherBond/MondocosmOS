// File:	RWStepShape_RWSubface.cxx
// Created:	Fri Jan  4 17:42:45 2002 
// Author:	data exchange team
// Generator:	ExpToCas (EXPRESS -> CASCADE/XSTEP Translator) V1.1
// Copyright:	Matra Datavision 2000

#include <RWStepShape_RWSubface.ixx>
#include <StepShape_HArray1OfFaceBound.hxx>
#include <StepShape_FaceBound.hxx>

//=======================================================================
//function : RWStepShape_RWSubface
//purpose  : 
//=======================================================================

RWStepShape_RWSubface::RWStepShape_RWSubface ()
{
}

//=======================================================================
//function : ReadStep
//purpose  : 
//=======================================================================

void RWStepShape_RWSubface::ReadStep (const Handle(StepData_StepReaderData)& data,
                                      const Standard_Integer num,
                                      Handle(Interface_Check)& ach,
                                      const Handle(StepShape_Subface) &ent) const
{
  // Check number of parameters
  if ( ! data->CheckNbParams(num,3,ach,"subface") ) return;

  // Inherited fields of RepresentationItem

  Handle(TCollection_HAsciiString) aRepresentationItem_Name;
  data->ReadString (num, 1, "representation_item.name", ach, aRepresentationItem_Name);

  // Inherited fields of Face

  Handle(StepShape_HArray1OfFaceBound) aFace_Bounds;
  Standard_Integer sub2 = 0;
  if ( data->ReadSubList (num, 2, "face.bounds", ach, sub2) ) {
    Standard_Integer num2 = sub2;
    Standard_Integer nb0 = data->NbParams(num2);
    aFace_Bounds = new StepShape_HArray1OfFaceBound (1, nb0);
    for ( Standard_Integer i0=1; i0 <= nb0; i0++ ) {
      Handle(StepShape_FaceBound) anIt0;
      data->ReadEntity (num2, i0, "face.bounds", ach, STANDARD_TYPE(StepShape_FaceBound), anIt0);
      aFace_Bounds->SetValue(i0, anIt0);
    }
  }

  // Own fields of Subface

  Handle(StepShape_Face) aParentFace;
  data->ReadEntity (num, 3, "parent_face", ach, STANDARD_TYPE(StepShape_Face), aParentFace);

  // Initialize entity
  ent->Init(aRepresentationItem_Name,
            aFace_Bounds,
            aParentFace);
}

//=======================================================================
//function : WriteStep
//purpose  : 
//=======================================================================

void RWStepShape_RWSubface::WriteStep (StepData_StepWriter& SW,
                                       const Handle(StepShape_Subface) &ent) const
{

  // Inherited fields of RepresentationItem

  SW.Send (ent->StepRepr_RepresentationItem::Name());

  // Inherited fields of Face

  SW.OpenSub();
  for (Standard_Integer i1=1; i1 <= ent->StepShape_Face::Bounds()->Length(); i1++ ) {
    Handle(StepShape_FaceBound) Var0 = ent->StepShape_Face::Bounds()->Value(i1);
    SW.Send (Var0);
  }
  SW.CloseSub();

  // Own fields of Subface

  SW.Send (ent->ParentFace());
}

//=======================================================================
//function : Share
//purpose  : 
//=======================================================================

void RWStepShape_RWSubface::Share (const Handle(StepShape_Subface) &ent,
                                   Interface_EntityIterator& iter) const
{

  // Inherited fields of RepresentationItem

  // Inherited fields of Face

  for (Standard_Integer i1=1; i1 <= ent->StepShape_Face::Bounds()->Length(); i1++ ) {
    Handle(StepShape_FaceBound) Var0 = ent->StepShape_Face::Bounds()->Value(i1);
    iter.AddItem (Var0);
  }

  // Own fields of Subface

  iter.AddItem (ent->ParentFace());
}
