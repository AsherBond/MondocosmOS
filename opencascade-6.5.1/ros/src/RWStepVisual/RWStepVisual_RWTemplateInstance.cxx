
#include <RWStepVisual_RWTemplateInstance.ixx>
#include <StepRepr_RepresentationMap.hxx>
#include <StepRepr_RepresentationItem.hxx>


#include <Interface_EntityIterator.hxx>


#include <StepVisual_TemplateInstance.hxx>


RWStepVisual_RWTemplateInstance::RWStepVisual_RWTemplateInstance () {}

void RWStepVisual_RWTemplateInstance::ReadStep
	(const Handle(StepData_StepReaderData)& data,
	 const Standard_Integer num,
	 Handle(Interface_Check)& ach,
	 const Handle(StepVisual_TemplateInstance)& ent) const
{


	// --- Number of Parameter Control ---

	if (!data->CheckNbParams(num,3,ach,"template_instance")) return;

	// --- inherited field : name ---

	Handle(TCollection_HAsciiString) aName;
	//szv#4:S4163:12Mar99 `Standard_Boolean stat1 =` not needed
	data->ReadString (num,1,"name",ach,aName);

	// --- inherited field : mappingSource ---

	Handle(StepRepr_RepresentationMap) aMappingSource;
	//szv#4:S4163:12Mar99 `Standard_Boolean stat2 =` not needed
	data->ReadEntity(num, 2,"mapping_source", ach, STANDARD_TYPE(StepRepr_RepresentationMap), aMappingSource);

	// --- inherited field : mappingTarget ---

	Handle(StepRepr_RepresentationItem) aMappingTarget;
	//szv#4:S4163:12Mar99 `Standard_Boolean stat3 =` not needed
	data->ReadEntity(num, 3,"mapping_target", ach, STANDARD_TYPE(StepRepr_RepresentationItem), aMappingTarget);

	//--- Initialisation of the read entity ---


	ent->Init(aName, aMappingSource, aMappingTarget);
}


void RWStepVisual_RWTemplateInstance::WriteStep
	(StepData_StepWriter& SW,
	 const Handle(StepVisual_TemplateInstance)& ent) const
{

	// --- inherited field name ---

	SW.Send(ent->Name());

	// --- inherited field mappingSource ---

	SW.Send(ent->MappingSource());

	// --- inherited field mappingTarget ---

	SW.Send(ent->MappingTarget());
}


void RWStepVisual_RWTemplateInstance::Share(const Handle(StepVisual_TemplateInstance)& ent, Interface_EntityIterator& iter) const
{

	iter.GetOneItem(ent->MappingSource());


	iter.GetOneItem(ent->MappingTarget());
}

