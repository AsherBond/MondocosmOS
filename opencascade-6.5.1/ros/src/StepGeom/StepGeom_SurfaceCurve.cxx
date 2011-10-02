#include <StepGeom_SurfaceCurve.ixx>


StepGeom_SurfaceCurve::StepGeom_SurfaceCurve ()  {}

void StepGeom_SurfaceCurve::Init(
	const Handle(TCollection_HAsciiString)& aName)
{

	StepRepr_RepresentationItem::Init(aName);
}

void StepGeom_SurfaceCurve::Init(
	const Handle(TCollection_HAsciiString)& aName,
	const Handle(StepGeom_Curve)& aCurve3d,
	const Handle(StepGeom_HArray1OfPcurveOrSurface)& aAssociatedGeometry,
	const StepGeom_PreferredSurfaceCurveRepresentation aMasterRepresentation)
{
	// --- classe own fields ---
	curve3d = aCurve3d;
	associatedGeometry = aAssociatedGeometry;
	masterRepresentation = aMasterRepresentation;
	// --- classe inherited fields ---
	StepRepr_RepresentationItem::Init(aName);
}


void StepGeom_SurfaceCurve::SetCurve3d(const Handle(StepGeom_Curve)& aCurve3d)
{
	curve3d = aCurve3d;
}

Handle(StepGeom_Curve) StepGeom_SurfaceCurve::Curve3d() const
{
	return curve3d;
}

void StepGeom_SurfaceCurve::SetAssociatedGeometry(const Handle(StepGeom_HArray1OfPcurveOrSurface)& aAssociatedGeometry)
{
	associatedGeometry = aAssociatedGeometry;
}

Handle(StepGeom_HArray1OfPcurveOrSurface) StepGeom_SurfaceCurve::AssociatedGeometry() const
{
	return associatedGeometry;
}

StepGeom_PcurveOrSurface StepGeom_SurfaceCurve::AssociatedGeometryValue(const Standard_Integer num) const
{
	return associatedGeometry->Value(num);
}

Standard_Integer StepGeom_SurfaceCurve::NbAssociatedGeometry () const
{
	return associatedGeometry->Length();
}

void StepGeom_SurfaceCurve::SetMasterRepresentation(const StepGeom_PreferredSurfaceCurveRepresentation aMasterRepresentation)
{
	masterRepresentation = aMasterRepresentation;
}

StepGeom_PreferredSurfaceCurveRepresentation StepGeom_SurfaceCurve::MasterRepresentation() const
{
	return masterRepresentation;
}
