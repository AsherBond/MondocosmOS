
#include <RWStepAP214_RWAutoDesignGroupAssignment.ixx>
#include <StepAP214_HArray1OfAutoDesignGroupedItem.hxx>
#include <StepAP214_AutoDesignGroupedItem.hxx>
#include <StepBasic_Group.hxx>


#include <Interface_EntityIterator.hxx>


#include <StepAP214_AutoDesignGroupAssignment.hxx>


RWStepAP214_RWAutoDesignGroupAssignment::RWStepAP214_RWAutoDesignGroupAssignment () {}

void RWStepAP214_RWAutoDesignGroupAssignment::ReadStep
	(const Handle(StepData_StepReaderData)& data,
	 const Standard_Integer num,
	 Handle(Interface_Check)& ach,
	 const Handle(StepAP214_AutoDesignGroupAssignment)& ent) const
{


	// --- Number of Parameter Control ---

	if (!data->CheckNbParams(num,2,ach,"auto_design_group_assignment")) return;

	// --- inherited field : assignedGroup ---

	Handle(StepBasic_Group) aAssignedGroup;
	data->ReadEntity(num, 1,"assigned_group", ach, STANDARD_TYPE(StepBasic_Group), aAssignedGroup);

	// --- own field : items ---

	Handle(StepAP214_HArray1OfAutoDesignGroupedItem) aItems;
	StepAP214_AutoDesignGroupedItem aItemsItem;
	Standard_Integer nsub2;
	if (data->ReadSubList (num,2,"items",ach,nsub2)) {
	  Standard_Integer nb2 = data->NbParams(nsub2);
	  aItems = new StepAP214_HArray1OfAutoDesignGroupedItem (1, nb2);
	  for (Standard_Integer i2 = 1; i2 <= nb2; i2 ++) {
	    Standard_Boolean stat2 = data->ReadEntity
	         (nsub2,i2,"items",ach,aItemsItem);
	    if (stat2) aItems->SetValue(i2,aItemsItem);
	  }
	}

	//--- Initialisation of the read entity ---


	ent->Init(aAssignedGroup, aItems);
}


void RWStepAP214_RWAutoDesignGroupAssignment::WriteStep
	(StepData_StepWriter& SW,
	 const Handle(StepAP214_AutoDesignGroupAssignment)& ent) const
{

	// --- inherited field assignedGroup ---

	SW.Send(ent->AssignedGroup());

	// --- own field : items ---

	SW.OpenSub();
	for (Standard_Integer i2 = 1;  i2 <= ent->NbItems();  i2 ++) {
	  SW.Send(ent->ItemsValue(i2).Value());
	}
	SW.CloseSub();
}


void RWStepAP214_RWAutoDesignGroupAssignment::Share(const Handle(StepAP214_AutoDesignGroupAssignment)& ent, Interface_EntityIterator& iter) const
{

	iter.GetOneItem(ent->AssignedGroup());


	Standard_Integer nbElem2 = ent->NbItems();
	for (Standard_Integer is2=1; is2<=nbElem2; is2 ++) {
	  iter.GetOneItem(ent->ItemsValue(is2).Value());
	}

}

