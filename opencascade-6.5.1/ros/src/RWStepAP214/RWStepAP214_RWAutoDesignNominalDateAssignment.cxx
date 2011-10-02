
#include <RWStepAP214_RWAutoDesignNominalDateAssignment.ixx>
#include <StepAP214_HArray1OfAutoDesignDatedItem.hxx>
#include <StepAP214_AutoDesignDatedItem.hxx>
#include <StepBasic_Date.hxx>
#include <StepBasic_DateRole.hxx>


#include <Interface_EntityIterator.hxx>


#include <StepAP214_AutoDesignNominalDateAssignment.hxx>


RWStepAP214_RWAutoDesignNominalDateAssignment::RWStepAP214_RWAutoDesignNominalDateAssignment () {}

void RWStepAP214_RWAutoDesignNominalDateAssignment::ReadStep
	(const Handle(StepData_StepReaderData)& data,
	 const Standard_Integer num,
	 Handle(Interface_Check)& ach,
	 const Handle(StepAP214_AutoDesignNominalDateAssignment)& ent) const
{


	// --- Number of Parameter Control ---

	if (!data->CheckNbParams(num,3,ach,"auto_design_nominal_date_assignment")) return;

	// --- inherited field : assignedDate ---

	Handle(StepBasic_Date) aAssignedDate;
#ifdef DEB
	Standard_Boolean stat1 = 
#endif
	  data->ReadEntity(num, 1,"assigned_date", ach, STANDARD_TYPE(StepBasic_Date), aAssignedDate);

	// --- inherited field : role ---

	Handle(StepBasic_DateRole) aRole;
#ifdef DEB
	Standard_Boolean stat2 = 
#endif
	  data->ReadEntity(num, 2,"role", ach, STANDARD_TYPE(StepBasic_DateRole), aRole);

	// --- own field : items ---

	Handle(StepAP214_HArray1OfAutoDesignDatedItem) aItems;
	StepAP214_AutoDesignDatedItem aItemsItem;
	Standard_Integer nsub3;
	if (data->ReadSubList (num,3,"items",ach,nsub3)) {
	  Standard_Integer nb3 = data->NbParams(nsub3);
	  aItems = new StepAP214_HArray1OfAutoDesignDatedItem (1, nb3);
	  for (Standard_Integer i3 = 1; i3 <= nb3; i3 ++) {
	    Standard_Boolean stat3 = data->ReadEntity
	         (nsub3,i3,"items",ach,aItemsItem);
	    if (stat3) aItems->SetValue(i3,aItemsItem);
	  }
	}

	//--- Initialisation of the read entity ---


	ent->Init(aAssignedDate, aRole, aItems);
}


void RWStepAP214_RWAutoDesignNominalDateAssignment::WriteStep
	(StepData_StepWriter& SW,
	 const Handle(StepAP214_AutoDesignNominalDateAssignment)& ent) const
{

	// --- inherited field assignedDate ---

	SW.Send(ent->AssignedDate());

	// --- inherited field role ---

	SW.Send(ent->Role());

	// --- own field : items ---

	SW.OpenSub();
	for (Standard_Integer i3 = 1;  i3 <= ent->NbItems();  i3 ++) {
	  SW.Send(ent->ItemsValue(i3).Value());
	}
	SW.CloseSub();
}


void RWStepAP214_RWAutoDesignNominalDateAssignment::Share(const Handle(StepAP214_AutoDesignNominalDateAssignment)& ent, Interface_EntityIterator& iter) const
{

	iter.GetOneItem(ent->AssignedDate());


	iter.GetOneItem(ent->Role());


	Standard_Integer nbElem3 = ent->NbItems();
	for (Standard_Integer is3=1; is3<=nbElem3; is3 ++) {
	  iter.GetOneItem(ent->ItemsValue(is3).Value());
	}

}

