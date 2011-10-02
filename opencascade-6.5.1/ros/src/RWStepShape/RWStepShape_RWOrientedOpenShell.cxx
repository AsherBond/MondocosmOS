
#include <RWStepShape_RWOrientedOpenShell.ixx>
#include <StepShape_OpenShell.hxx>
#include <StepShape_HArray1OfFace.hxx>
#include <StepShape_Face.hxx>


#include <Interface_EntityIterator.hxx>


#include <StepShape_OrientedOpenShell.hxx>


RWStepShape_RWOrientedOpenShell::RWStepShape_RWOrientedOpenShell () {}

void RWStepShape_RWOrientedOpenShell::ReadStep
	(const Handle(StepData_StepReaderData)& data,
	 const Standard_Integer num,
	 Handle(Interface_Check)& ach,
	 const Handle(StepShape_OrientedOpenShell)& ent) const
{


	// --- Number of Parameter Control ---

	if (!data->CheckNbParams(num,4,ach,"oriented_open_shell")) return;

	// --- inherited field : name ---

	Handle(TCollection_HAsciiString) aName;
	//szv#4:S4163:12Mar99 `Standard_Boolean stat1 =` not needed
	data->ReadString (num,1,"name",ach,aName);

	// --- inherited field : cfsFaces ---
	// --- this field is redefined ---
	//szv#4:S4163:12Mar99 `Standard_Boolean stat2 =` not needed
	data->CheckDerived(num,2,"cfs_faces",ach,Standard_False);

	// --- own field : openShellElement ---

	Handle(StepShape_OpenShell) aOpenShellElement;
	//szv#4:S4163:12Mar99 `Standard_Boolean stat3 =` not needed
	data->ReadEntity(num, 3,"open_shell_element", ach, STANDARD_TYPE(StepShape_OpenShell), aOpenShellElement);

	// --- own field : orientation ---

	Standard_Boolean aOrientation;
	//szv#4:S4163:12Mar99 `Standard_Boolean stat4 =` not needed
	data->ReadBoolean (num,4,"orientation",ach,aOrientation);

	//--- Initialisation of the read entity ---


	ent->Init(aName, aOpenShellElement, aOrientation);
}


void RWStepShape_RWOrientedOpenShell::WriteStep
	(StepData_StepWriter& SW,
	 const Handle(StepShape_OrientedOpenShell)& ent) const
{

	// --- inherited field name ---

	SW.Send(ent->Name());

	// --- inherited field cfsFaces ---

	SW.SendDerived();

	// --- own field : openShellElement ---

	SW.Send(ent->OpenShellElement());

	// --- own field : orientation ---

	SW.SendBoolean(ent->Orientation());
}


void RWStepShape_RWOrientedOpenShell::Share(const Handle(StepShape_OrientedOpenShell)& ent, Interface_EntityIterator& iter) const
{

	iter.GetOneItem(ent->OpenShellElement());
}

