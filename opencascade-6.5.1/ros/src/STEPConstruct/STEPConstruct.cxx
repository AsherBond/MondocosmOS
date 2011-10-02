// File:	STEPConstruct.cxx
// Created:	Tue Jan 11 09:37:57 2000
// Author:	Andrey BETENEV
//		<abv@doomox.nnov.matra-dtv.fr>

#include <STEPConstruct.ixx>
#include <TransferBRep.hxx>
#include <TransferBRep_ShapeMapper.hxx>
#include <Transfer_Binder.hxx>
#include <Transfer_SimpleBinderOfTransient.hxx>

#include <StepBasic_ProductDefinition.hxx>
#include <StepBasic_ProductDefinitionRelationship.hxx>
#include <StepRepr_PropertyDefinition.hxx>
#include <StepRepr_ProductDefinitionShape.hxx>

#ifdef DEBUG
void DumpBinder (const Handle(Transfer_Binder) &binder)
{
  Handle(Transfer_Binder) bbb = binder;
  while ( ! bbb.IsNull() ) {
    Handle(Transfer_SimpleBinderOfTransient) bx = 
      Handle(Transfer_SimpleBinderOfTransient)::DownCast ( bbb );
    if ( ! bx.IsNull() ) {
      cout << "--> " << bx->ResultTypeName() << " " << *(void**)&bx->Result() << endl;
    }
    else cout << "--> ???" << endl;
    bbb = bbb->NextResult();
  }
  cout << endl;
}
#endif

//=======================================================================
//function : FindEntity
//purpose  : 
//=======================================================================

Handle(StepRepr_RepresentationItem) STEPConstruct::FindEntity (const Handle(Transfer_FinderProcess) &FinderProcess,
							       const TopoDS_Shape &Shape)
{
  Handle(StepRepr_RepresentationItem) item;
  Handle(TransferBRep_ShapeMapper) mapper = TransferBRep::ShapeMapper ( FinderProcess, Shape );
  FinderProcess->FindTypedTransient (mapper,STANDARD_TYPE(StepRepr_RepresentationItem), item);
#ifdef DEB
  if ( item.IsNull() ) cout << Shape.TShape()->DynamicType()->Name() << ": RepItem not found" << endl;
  else cout << Shape.TShape()->DynamicType()->Name() << ": RepItem found: " << item->DynamicType()->Name() << endl;
#endif
  return item;
}

//=======================================================================
//function : FindEntity
//purpose  : 
//=======================================================================

Handle(StepRepr_RepresentationItem) STEPConstruct::FindEntity (const Handle(Transfer_FinderProcess) &FinderProcess,
							       const TopoDS_Shape &Shape,
							       TopLoc_Location &Loc)
{
  Handle(StepRepr_RepresentationItem) item;
  Loc = Shape.Location();
  Handle(TransferBRep_ShapeMapper) mapper = TransferBRep::ShapeMapper ( FinderProcess, Shape );
  if ( ! FinderProcess->FindTypedTransient (mapper,STANDARD_TYPE(StepRepr_RepresentationItem), item) && 
       ! Loc.IsIdentity() ) {
      Loc.Identity();
      TopoDS_Shape S = Shape;
      S.Location (Loc);
      mapper = TransferBRep::ShapeMapper ( FinderProcess, S );
      FinderProcess->FindTypedTransient (mapper,STANDARD_TYPE(StepRepr_RepresentationItem), item);
  }
#ifdef DEB
  if ( item.IsNull() ) cout << Shape.TShape()->DynamicType()->Name() << ": RepItem not found" << endl;
  else if ( Loc != Shape.Location() ) cout << Shape.TShape()->DynamicType()->Name() << ": RepItem found for shape without location: " << item->DynamicType()->Name() << endl;
  else cout << Shape.TShape()->DynamicType()->Name() << ": RepItem found: " << item->DynamicType()->Name() << endl;
#endif  
  return item;
}

//=======================================================================
//function : FindShape
//purpose  : 
//=======================================================================

TopoDS_Shape STEPConstruct::FindShape (const Handle(Transfer_TransientProcess) &TransientProcess,
				       const Handle(StepRepr_RepresentationItem) &item)
{
  TopoDS_Shape S;
  Handle(Transfer_Binder) binder = TransientProcess->Find(item);
  if ( ! binder.IsNull() && binder->HasResult() ) {
    S = TransferBRep::ShapeResult ( TransientProcess, binder );
  }
  return S;
}

//=======================================================================
//function : FindCDSR
//purpose  : 
//=======================================================================

Standard_Boolean STEPConstruct::FindCDSR
  (const Handle(Transfer_Binder)& ComponentBinder,
   const Handle(StepShape_ShapeDefinitionRepresentation)& AssemblySDR,
   Handle(StepShape_ContextDependentShapeRepresentation)& ComponentCDSR)
{
  Standard_Boolean result = Standard_False;

  Handle(StepRepr_PropertyDefinition) PropD = AssemblySDR->Definition().PropertyDefinition();
  if (!PropD.IsNull()) {
    Handle(StepBasic_ProductDefinition) AssemblyPD = PropD->Definition().ProductDefinition();
    if (!AssemblyPD.IsNull()) {
      Handle(Transfer_Binder) binder = ComponentBinder;
      Handle(Transfer_SimpleBinderOfTransient) trb;
      Handle(StepRepr_ProductDefinitionShape) PDS;
      Handle(StepBasic_ProductDefinitionRelationship) NAUO;
      Handle(StepBasic_ProductDefinition) ComponentPD;
      while (!binder.IsNull() && !result) {
	trb = Handle(Transfer_SimpleBinderOfTransient)::DownCast(binder);
	if (!trb.IsNull()) {
	  ComponentCDSR = Handle(StepShape_ContextDependentShapeRepresentation)::DownCast(trb->Result());
	  if (!ComponentCDSR.IsNull()) {
	    PDS = ComponentCDSR->RepresentedProductRelation();
	    if (!PDS.IsNull()) {
	      NAUO = PDS->Definition().ProductDefinitionRelationship();
	      if (!NAUO.IsNull()) {
		ComponentPD = NAUO->RelatingProductDefinition();
		result = (ComponentPD == AssemblyPD);
	      }
	    }
	  }
	}
	binder = binder->NextResult();
      }
    }
  }
  return result;
}
