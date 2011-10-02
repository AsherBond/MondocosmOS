#include <StepGeom_Line.ixx>


StepGeom_Line::StepGeom_Line ()  {}

void StepGeom_Line::Init(
	const Handle(TCollection_HAsciiString)& aName)
{

	StepRepr_RepresentationItem::Init(aName);
}

void StepGeom_Line::Init(
	const Handle(TCollection_HAsciiString)& aName,
	const Handle(StepGeom_CartesianPoint)& aPnt,
	const Handle(StepGeom_Vector)& aDir)
{
	// --- classe own fields ---
	pnt = aPnt;
	dir = aDir;
	// --- classe inherited fields ---
	StepRepr_RepresentationItem::Init(aName);
}


void StepGeom_Line::SetPnt(const Handle(StepGeom_CartesianPoint)& aPnt)
{
	pnt = aPnt;
}

Handle(StepGeom_CartesianPoint) StepGeom_Line::Pnt() const
{
	return pnt;
}

void StepGeom_Line::SetDir(const Handle(StepGeom_Vector)& aDir)
{
	dir = aDir;
}

Handle(StepGeom_Vector) StepGeom_Line::Dir() const
{
	return dir;
}
