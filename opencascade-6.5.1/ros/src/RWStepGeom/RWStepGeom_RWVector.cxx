
#include <RWStepGeom_RWVector.ixx>
#include <StepGeom_Direction.hxx>


#include <Interface_EntityIterator.hxx>


#include <StepGeom_Vector.hxx>


RWStepGeom_RWVector::RWStepGeom_RWVector () {}

void RWStepGeom_RWVector::ReadStep
	(const Handle(StepData_StepReaderData)& data,
	 const Standard_Integer num,
	 Handle(Interface_Check)& ach,
	 const Handle(StepGeom_Vector)& ent) const
{


	// --- Number of Parameter Control ---

	if (!data->CheckNbParams(num,3,ach,"vector")) return;

	// --- inherited field : name ---

	Handle(TCollection_HAsciiString) aName;
	//szv#4:S4163:12Mar99 `Standard_Boolean stat1 =` not needed
	data->ReadString (num,1,"name",ach,aName);

	// --- own field : orientation ---

	Handle(StepGeom_Direction) aOrientation;
	//szv#4:S4163:12Mar99 `Standard_Boolean stat2 =` not needed
	data->ReadEntity(num, 2,"orientation", ach, STANDARD_TYPE(StepGeom_Direction), aOrientation);

	// --- own field : magnitude ---

	Standard_Real aMagnitude;
	//szv#4:S4163:12Mar99 `Standard_Boolean stat3 =` not needed
	data->ReadReal (num,3,"magnitude",ach,aMagnitude);

	//--- Initialisation of the read entity ---


	ent->Init(aName, aOrientation, aMagnitude);
}


void RWStepGeom_RWVector::WriteStep
	(StepData_StepWriter& SW,
	 const Handle(StepGeom_Vector)& ent) const
{

	// --- inherited field name ---

	SW.Send(ent->Name());

	// --- own field : orientation ---

	SW.Send(ent->Orientation());

	// --- own field : magnitude ---

	SW.Send(ent->Magnitude());
}


void RWStepGeom_RWVector::Share(const Handle(StepGeom_Vector)& ent, Interface_EntityIterator& iter) const
{

	iter.GetOneItem(ent->Orientation());
}



void RWStepGeom_RWVector::Check
  (const Handle(StepGeom_Vector)& ent,
   const Interface_ShareTool& ,
   Handle(Interface_Check)& ach) const
{
  if(Abs(ent->Magnitude()) < RealEpsilon()) {
    ach->AddFail("ERROR: Magnitude of Vector = 0.0");
  }
}
