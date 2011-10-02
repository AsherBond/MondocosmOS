
#include <RWStepBasic_RWSolidAngleMeasureWithUnit.ixx>
#include <StepBasic_NamedUnit.hxx>
#include <StepBasic_MeasureValueMember.hxx>

#include <TCollection_AsciiString.hxx>

#include <Interface_EntityIterator.hxx>


#include <StepBasic_SolidAngleMeasureWithUnit.hxx>

//=======================================================================
//function : RWStepBasic_RWSolidAngleMeasureWithUnit
//purpose  : 
//=======================================================================

RWStepBasic_RWSolidAngleMeasureWithUnit::RWStepBasic_RWSolidAngleMeasureWithUnit () {}

//=======================================================================
//function : ReadStep
//purpose  : 
//=======================================================================

void RWStepBasic_RWSolidAngleMeasureWithUnit::ReadStep (const Handle(StepData_StepReaderData)& data,
							const Standard_Integer num,
							Handle(Interface_Check)& ach,
							const Handle(StepBasic_SolidAngleMeasureWithUnit)& ent) const
{
  // --- Number of Parameter Control ---
  if (!data->CheckNbParams(num,2,ach,"solid_angle_measure_with_unit")) return;
  
  // --- inherited field : valueComponent ---
  // --- Update 12-02-96 by FMA ,  31-MARS-1997 by CKY
  Handle(StepBasic_MeasureValueMember) mvc = new StepBasic_MeasureValueMember;
  data->ReadMember (num,1, "value_component", ach, mvc);
  
  // --- inherited field : unitComponent ---
  StepBasic_Unit aUnitComponent;
  data->ReadEntity(num, 2,"unit_component", ach, aUnitComponent);
  
  //--- Initialisation of the read entity ---
  ent->Init(mvc, aUnitComponent);
}

//=======================================================================
//function : WriteStep	
//purpose  : 
//=======================================================================

void RWStepBasic_RWSolidAngleMeasureWithUnit::WriteStep	(StepData_StepWriter& SW,
							 const Handle(StepBasic_SolidAngleMeasureWithUnit)& ent) const
{
  // --- inherited field valueComponent ---
  SW.Send(ent->ValueComponentMember());

  // --- inherited field unitComponent ---
  SW.Send(ent->UnitComponent().Value());
}

//=======================================================================
//function : Share
//purpose  : 
//=======================================================================

void RWStepBasic_RWSolidAngleMeasureWithUnit::Share (const Handle(StepBasic_SolidAngleMeasureWithUnit)& ent, 
						     Interface_EntityIterator& iter) const
{
  iter.GetOneItem(ent->UnitComponent().Value());
}

