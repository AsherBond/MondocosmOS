#include <StepGeom_GeometricRepresentationContextAndParametricRepresentationContext.ixx>

#include <StepGeom_GeometricRepresentationContext.hxx>

#include <StepRepr_GlobalUnitAssignedContext.hxx>


StepGeom_GeometricRepresentationContextAndParametricRepresentationContext::StepGeom_GeometricRepresentationContextAndParametricRepresentationContext ()  {}

void StepGeom_GeometricRepresentationContextAndParametricRepresentationContext::Init(
	const Handle(TCollection_HAsciiString)& aContextIdentifier,
	const Handle(TCollection_HAsciiString)& aContextType)
{

	StepRepr_RepresentationContext::Init(aContextIdentifier, aContextType);
}

void StepGeom_GeometricRepresentationContextAndParametricRepresentationContext::Init
(const Handle(TCollection_HAsciiString)& aContextIdentifier,
 const Handle(TCollection_HAsciiString)& aContextType,
 const Handle(StepGeom_GeometricRepresentationContext)& aGeometricRepresentationContext,
 const Handle(StepRepr_ParametricRepresentationContext)& aParametricRepresentationContext)
{
  // --- classe own fields ---
  geometricRepresentationContext = aGeometricRepresentationContext;
  parametricRepresentationContext= aParametricRepresentationContext;
  // --- classe inherited fields ---
  StepRepr_RepresentationContext::Init(aContextIdentifier, aContextType);
}


void StepGeom_GeometricRepresentationContextAndParametricRepresentationContext::Init
(const Handle(TCollection_HAsciiString)& aContextIdentifier,
 const Handle(TCollection_HAsciiString)& aContextType,
 const Standard_Integer aCoordinateSpaceDimension)
{
  // --- classe inherited fields ---
  
  StepRepr_RepresentationContext::Init(aContextIdentifier, aContextType);
  
  // --- ANDOR componant fields ---
  
  geometricRepresentationContext = new StepGeom_GeometricRepresentationContext();
  geometricRepresentationContext->Init(aContextIdentifier, aContextType, aCoordinateSpaceDimension);
  
  // --- ANDOR componant fields ---
  
  parametricRepresentationContext = new StepRepr_ParametricRepresentationContext();
  parametricRepresentationContext->Init(aContextIdentifier, aContextType);
}


void StepGeom_GeometricRepresentationContextAndParametricRepresentationContext::SetGeometricRepresentationContext(const Handle(StepGeom_GeometricRepresentationContext)& aGeometricRepresentationContext)
{
  geometricRepresentationContext = aGeometricRepresentationContext;
}

Handle(StepGeom_GeometricRepresentationContext) StepGeom_GeometricRepresentationContextAndParametricRepresentationContext::GeometricRepresentationContext() const
{
  return geometricRepresentationContext;
}

void StepGeom_GeometricRepresentationContextAndParametricRepresentationContext::SetParametricRepresentationContext(const Handle(StepRepr_ParametricRepresentationContext)& aParametricRepresentationContext)
{
  parametricRepresentationContext = aParametricRepresentationContext;
}

Handle(StepRepr_ParametricRepresentationContext) StepGeom_GeometricRepresentationContextAndParametricRepresentationContext::ParametricRepresentationContext() const
{
	return parametricRepresentationContext;
}

//--- Specific Methods for AND classe field access ---


void StepGeom_GeometricRepresentationContextAndParametricRepresentationContext::SetCoordinateSpaceDimension(const Standard_Integer aCoordinateSpaceDimension)
{
  geometricRepresentationContext->SetCoordinateSpaceDimension(aCoordinateSpaceDimension);
}

Standard_Integer StepGeom_GeometricRepresentationContextAndParametricRepresentationContext::CoordinateSpaceDimension() const
{
  return geometricRepresentationContext->CoordinateSpaceDimension();
}
