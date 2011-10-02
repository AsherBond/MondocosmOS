
#include <StepVisual_SurfaceStyleElementSelect.ixx>
#include <Interface_Macros.hxx>

StepVisual_SurfaceStyleElementSelect::StepVisual_SurfaceStyleElementSelect () {  }

Standard_Integer StepVisual_SurfaceStyleElementSelect::CaseNum(const Handle(Standard_Transient)& ent) const
{
	if (ent.IsNull()) return 0;
	if (ent->IsKind(STANDARD_TYPE(StepVisual_SurfaceStyleFillArea))) return 1;
	if (ent->IsKind(STANDARD_TYPE(StepVisual_SurfaceStyleBoundary))) return 2;
	if (ent->IsKind(STANDARD_TYPE(StepVisual_SurfaceStyleParameterLine))) return 3;
//	if (ent->IsKind(STANDARD_TYPE(StepVisual_SurfaceStyleSilhouette))) return 4;
//	if (ent->IsKind(STANDARD_TYPE(StepVisual_SurfaceStyleSegmentationCurve))) return 5;
//	if (ent->IsKind(STANDARD_TYPE(StepVisual_SurfaceStyleControlGrid))) return 6;
	return 0;
}

Handle(StepVisual_SurfaceStyleFillArea) StepVisual_SurfaceStyleElementSelect::SurfaceStyleFillArea () const
{
	return GetCasted(StepVisual_SurfaceStyleFillArea,Value());
}

Handle(StepVisual_SurfaceStyleBoundary) StepVisual_SurfaceStyleElementSelect::SurfaceStyleBoundary () const
{
	return GetCasted(StepVisual_SurfaceStyleBoundary,Value());
}

Handle(StepVisual_SurfaceStyleParameterLine) StepVisual_SurfaceStyleElementSelect::SurfaceStyleParameterLine () const
{
	return GetCasted(StepVisual_SurfaceStyleParameterLine,Value());
}
