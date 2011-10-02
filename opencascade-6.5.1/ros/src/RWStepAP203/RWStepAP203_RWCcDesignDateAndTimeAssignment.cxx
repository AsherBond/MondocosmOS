// File:	RWStepAP203_RWCcDesignDateAndTimeAssignment.cxx
// Created:	Fri Nov 26 16:26:32 1999 
// Author:	Andrey BETENEV
// Generator:	ExpToCas (EXPRESS -> CASCADE/XSTEP Translator) V1.0
// Copyright:	Matra Datavision 1999

#include <RWStepAP203_RWCcDesignDateAndTimeAssignment.ixx>
#include <StepAP203_HArray1OfDateTimeItem.hxx>
#include <StepAP203_DateTimeItem.hxx>

//=======================================================================
//function : RWStepAP203_RWCcDesignDateAndTimeAssignment
//purpose  : 
//=======================================================================

RWStepAP203_RWCcDesignDateAndTimeAssignment::RWStepAP203_RWCcDesignDateAndTimeAssignment ()
{
}

//=======================================================================
//function : ReadStep
//purpose  : 
//=======================================================================

void RWStepAP203_RWCcDesignDateAndTimeAssignment::ReadStep (const Handle(StepData_StepReaderData)& data,
                                                            const Standard_Integer num,
                                                            Handle(Interface_Check)& ach,
                                                            const Handle(StepAP203_CcDesignDateAndTimeAssignment) &ent) const
{
  // Check number of parameters
  if ( ! data->CheckNbParams(num,3,ach,"cc_design_date_and_time_assignment") ) return;

  // Inherited fields of DateAndTimeAssignment

  Handle(StepBasic_DateAndTime) aDateAndTimeAssignment_AssignedDateAndTime;
  data->ReadEntity (num, 1, "date_and_time_assignment.assigned_date_and_time", ach, STANDARD_TYPE(StepBasic_DateAndTime), aDateAndTimeAssignment_AssignedDateAndTime);

  Handle(StepBasic_DateTimeRole) aDateAndTimeAssignment_Role;
  data->ReadEntity (num, 2, "date_and_time_assignment.role", ach, STANDARD_TYPE(StepBasic_DateTimeRole), aDateAndTimeAssignment_Role);

  // Own fields of CcDesignDateAndTimeAssignment

  Handle(StepAP203_HArray1OfDateTimeItem) aItems;
  Standard_Integer sub3 = 0;
  if ( data->ReadSubList (num, 3, "items", ach, sub3) ) {
    Standard_Integer num2 = sub3;
    Standard_Integer nb0 = data->NbParams(num2);
    aItems = new StepAP203_HArray1OfDateTimeItem (1, nb0);
    for ( Standard_Integer i0=1; i0 <= nb0; i0++ ) {
      StepAP203_DateTimeItem anIt0;
      data->ReadEntity (num2, i0, "items", ach, anIt0);
      aItems->SetValue(i0, anIt0);
    }
  }

  // Initialize entity
  ent->Init(aDateAndTimeAssignment_AssignedDateAndTime,
            aDateAndTimeAssignment_Role,
            aItems);
}

//=======================================================================
//function : WriteStep
//purpose  : 
//=======================================================================

void RWStepAP203_RWCcDesignDateAndTimeAssignment::WriteStep (StepData_StepWriter& SW,
                                                             const Handle(StepAP203_CcDesignDateAndTimeAssignment) &ent) const
{

  // Inherited fields of DateAndTimeAssignment

  SW.Send (ent->StepBasic_DateAndTimeAssignment::AssignedDateAndTime());

  SW.Send (ent->StepBasic_DateAndTimeAssignment::Role());

  // Own fields of CcDesignDateAndTimeAssignment

  SW.OpenSub();
  for (Standard_Integer i2=1; i2 <= ent->Items()->Length(); i2++ ) {
    StepAP203_DateTimeItem Var0 = ent->Items()->Value(i2);
    SW.Send (Var0.Value());
  }
  SW.CloseSub();
}

//=======================================================================
//function : Share
//purpose  : 
//=======================================================================

void RWStepAP203_RWCcDesignDateAndTimeAssignment::Share (const Handle(StepAP203_CcDesignDateAndTimeAssignment) &ent,
                                                         Interface_EntityIterator& iter) const
{

  // Inherited fields of DateAndTimeAssignment

  iter.AddItem (ent->StepBasic_DateAndTimeAssignment::AssignedDateAndTime());

  iter.AddItem (ent->StepBasic_DateAndTimeAssignment::Role());

  // Own fields of CcDesignDateAndTimeAssignment

  for (Standard_Integer i3=1; i3 <= ent->Items()->Length(); i3++ ) {
    StepAP203_DateTimeItem Var0 = ent->Items()->Value(i3);
    iter.AddItem (Var0.Value());
  }
}
