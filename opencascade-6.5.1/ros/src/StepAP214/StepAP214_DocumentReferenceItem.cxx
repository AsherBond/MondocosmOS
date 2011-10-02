
#include <StepAP214_DocumentReferenceItem.ixx>
#include <Interface_Macros.hxx>

StepAP214_DocumentReferenceItem::StepAP214_DocumentReferenceItem () {  }

Standard_Integer StepAP214_DocumentReferenceItem::CaseNum(const Handle(Standard_Transient)& ent) const
{
	if (ent.IsNull()) return 0;
	if (ent->IsKind(STANDARD_TYPE(StepBasic_Approval))) return 1;
	if (ent->IsKind(STANDARD_TYPE(StepRepr_DescriptiveRepresentationItem))) return 2;
	if (ent->IsKind(STANDARD_TYPE(StepRepr_MaterialDesignation))) return 3;
	if (ent->IsKind(STANDARD_TYPE(StepBasic_ProductDefinition))) return 4;
	if (ent->IsKind(STANDARD_TYPE(StepBasic_ProductDefinitionRelationship))) return 5;
	if (ent->IsKind(STANDARD_TYPE(StepRepr_PropertyDefinition))) return 6;
	if (ent->IsKind(STANDARD_TYPE(StepRepr_Representation))) return 7;
	if (ent->IsKind(STANDARD_TYPE(StepRepr_ShapeAspect))) return 8;
	if (ent->IsKind(STANDARD_TYPE(StepRepr_ShapeAspectRelationship))) return 9;
	return 0;
}


Handle(StepBasic_Approval) StepAP214_DocumentReferenceItem::Approval() const
{  return GetCasted(StepBasic_Approval,Value());  }

Handle(StepRepr_DescriptiveRepresentationItem)  StepAP214_DocumentReferenceItem::DescriptiveRepresentationItem() const
{  return GetCasted(StepRepr_DescriptiveRepresentationItem,Value());  }

Handle(StepRepr_MaterialDesignation) StepAP214_DocumentReferenceItem::MaterialDesignation() const
{  return GetCasted(StepRepr_MaterialDesignation,Value());  }


Handle(StepBasic_ProductDefinition) StepAP214_DocumentReferenceItem::ProductDefinition () const
{  return GetCasted(StepBasic_ProductDefinition,Value());  }


Handle(StepBasic_ProductDefinitionRelationship) StepAP214_DocumentReferenceItem::ProductDefinitionRelationship() const
{  return GetCasted(StepBasic_ProductDefinitionRelationship,Value());  }

Handle(StepRepr_PropertyDefinition) StepAP214_DocumentReferenceItem::PropertyDefinition() const
{  return GetCasted(StepRepr_PropertyDefinition,Value());  }

Handle(StepRepr_Representation)  StepAP214_DocumentReferenceItem::Representation() const
{  return GetCasted(StepRepr_Representation,Value());  }

Handle(StepRepr_ShapeAspect)  StepAP214_DocumentReferenceItem::ShapeAspect() const
{  return GetCasted(StepRepr_ShapeAspect,Value());  }

Handle(StepRepr_ShapeAspectRelationship)  StepAP214_DocumentReferenceItem::ShapeAspectRelationship() const
{  return GetCasted(StepRepr_ShapeAspectRelationship,Value());  }
