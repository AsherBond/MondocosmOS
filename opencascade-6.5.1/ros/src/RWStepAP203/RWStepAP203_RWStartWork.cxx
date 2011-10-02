// File:	RWStepAP203_RWStartWork.cxx
// Created:	Fri Nov 26 16:26:40 1999 
// Author:	Andrey BETENEV
// Generator:	ExpToCas (EXPRESS -> CASCADE/XSTEP Translator) V1.0
// Copyright:	Matra Datavision 1999

#include <RWStepAP203_RWStartWork.ixx>
#include <StepAP203_HArray1OfWorkItem.hxx>
#include <StepAP203_WorkItem.hxx>

//=======================================================================
//function : RWStepAP203_RWStartWork
//purpose  : 
//=======================================================================

RWStepAP203_RWStartWork::RWStepAP203_RWStartWork ()
{
}

//=======================================================================
//function : ReadStep
//purpose  : 
//=======================================================================

void RWStepAP203_RWStartWork::ReadStep (const Handle(StepData_StepReaderData)& data,
                                        const Standard_Integer num,
                                        Handle(Interface_Check)& ach,
                                        const Handle(StepAP203_StartWork) &ent) const
{
  // Check number of parameters
  if ( ! data->CheckNbParams(num,2,ach,"start_work") ) return;

  // Inherited fields of ActionAssignment

  Handle(StepBasic_Action) aActionAssignment_AssignedAction;
  data->ReadEntity (num, 1, "action_assignment.assigned_action", ach, STANDARD_TYPE(StepBasic_Action), aActionAssignment_AssignedAction);

  // Own fields of StartWork

  Handle(StepAP203_HArray1OfWorkItem) aItems;
  Standard_Integer sub2 = 0;
  if ( data->ReadSubList (num, 2, "items", ach, sub2) ) {
    Standard_Integer num2 = sub2;
    Standard_Integer nb0 = data->NbParams(num2);
    aItems = new StepAP203_HArray1OfWorkItem (1, nb0);
    for ( Standard_Integer i0=1; i0 <= nb0; i0++ ) {
      StepAP203_WorkItem anIt0;
      data->ReadEntity (num2, i0, "items", ach, anIt0);
      aItems->SetValue(i0, anIt0);
    }
  }

  // Initialize entity
  ent->Init(aActionAssignment_AssignedAction,
            aItems);
}

//=======================================================================
//function : WriteStep
//purpose  : 
//=======================================================================

void RWStepAP203_RWStartWork::WriteStep (StepData_StepWriter& SW,
                                         const Handle(StepAP203_StartWork) &ent) const
{

  // Inherited fields of ActionAssignment

  SW.Send (ent->StepBasic_ActionAssignment::AssignedAction());

  // Own fields of StartWork

  SW.OpenSub();
  for (Standard_Integer i1=1; i1 <= ent->Items()->Length(); i1++ ) {
    StepAP203_WorkItem Var0 = ent->Items()->Value(i1);
    SW.Send (Var0.Value());
  }
  SW.CloseSub();
}

//=======================================================================
//function : Share
//purpose  : 
//=======================================================================

void RWStepAP203_RWStartWork::Share (const Handle(StepAP203_StartWork) &ent,
                                     Interface_EntityIterator& iter) const
{

  // Inherited fields of ActionAssignment

  iter.AddItem (ent->StepBasic_ActionAssignment::AssignedAction());

  // Own fields of StartWork

  for (Standard_Integer i2=1; i2 <= ent->Items()->Length(); i2++ ) {
    StepAP203_WorkItem Var0 = ent->Items()->Value(i2);
    iter.AddItem (Var0.Value());
  }
}
