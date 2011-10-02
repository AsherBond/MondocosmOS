//:S4134: abv 03.03.99: implementation of PreDefinedColour modified

#include <RWStepVisual_RWPreDefinedColour.ixx>
#include <StepVisual_PreDefinedItem.hxx>

RWStepVisual_RWPreDefinedColour::RWStepVisual_RWPreDefinedColour () {}

void RWStepVisual_RWPreDefinedColour::ReadStep
	(const Handle(StepData_StepReaderData)& data,
	 const Standard_Integer num,
	 Handle(Interface_Check)& ach,
	 const Handle(StepVisual_PreDefinedColour)& ent) const
{


	// --- Number of Parameter Control ---

	if (!data->CheckNbParams(num,1,ach,"pre_defined_colour")) return;

	// --- inherited field : name ---

	Handle(TCollection_HAsciiString) aName;
	//szv#4:S4163:12Mar99 `Standard_Boolean stat1 =` not needed
	data->ReadString (num,1,"name",ach,aName);

	//--- Initialisation of the read entity ---


	ent->GetPreDefinedItem()->Init(aName);
}


void RWStepVisual_RWPreDefinedColour::WriteStep
	(StepData_StepWriter& SW,
	 const Handle(StepVisual_PreDefinedColour)& ent) const
{

	// --- inherited field name ---

	SW.Send(ent->GetPreDefinedItem()->Name());
}
