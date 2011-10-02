
#include <RWStepVisual_RWPresentationStyleAssignment.ixx>
#include <StepVisual_HArray1OfPresentationStyleSelect.hxx>
#include <StepVisual_PresentationStyleSelect.hxx>


#include <Interface_EntityIterator.hxx>


#include <StepVisual_PresentationStyleAssignment.hxx>


RWStepVisual_RWPresentationStyleAssignment::RWStepVisual_RWPresentationStyleAssignment () {}

void RWStepVisual_RWPresentationStyleAssignment::ReadStep
	(const Handle(StepData_StepReaderData)& data,
	 const Standard_Integer num,
	 Handle(Interface_Check)& ach,
	 const Handle(StepVisual_PresentationStyleAssignment)& ent) const
{


	// --- Number of Parameter Control ---

	if (!data->CheckNbParams(num,1,ach,"presentation_style_assignment")) return;

	// --- own field : styles ---

	Handle(StepVisual_HArray1OfPresentationStyleSelect) aStyles;
	StepVisual_PresentationStyleSelect aStylesItem;
	Standard_Integer nsub1;
	if (data->ReadSubList (num,1,"styles",ach,nsub1)) {
	  Standard_Integer nb1 = data->NbParams(nsub1);
	  aStyles = new StepVisual_HArray1OfPresentationStyleSelect (1, nb1);
	  for (Standard_Integer i1 = 1; i1 <= nb1; i1 ++) {
	    //szv#4:S4163:12Mar99 `Standard_Boolean stat1 =` not needed
	    if (data->ReadEntity (nsub1,i1,"styles",ach,aStylesItem))
	      aStyles->SetValue(i1,aStylesItem);
	  }
	}

	//--- Initialisation of the read entity ---


	ent->Init(aStyles);
}


void RWStepVisual_RWPresentationStyleAssignment::WriteStep
	(StepData_StepWriter& SW,
	 const Handle(StepVisual_PresentationStyleAssignment)& ent) const
{

	// --- own field : styles ---

	SW.OpenSub();
	for (Standard_Integer i1 = 1;  i1 <= ent->NbStyles();  i1 ++) {
	  SW.Send(ent->StylesValue(i1).Value());
	}
	SW.CloseSub();
}


void RWStepVisual_RWPresentationStyleAssignment::Share(const Handle(StepVisual_PresentationStyleAssignment)& ent, Interface_EntityIterator& iter) const
{

	Standard_Integer nbElem1 = ent->NbStyles();
	for (Standard_Integer is1=1; is1<=nbElem1; is1 ++) {
	  iter.GetOneItem(ent->StylesValue(is1).Value());
	}

}

