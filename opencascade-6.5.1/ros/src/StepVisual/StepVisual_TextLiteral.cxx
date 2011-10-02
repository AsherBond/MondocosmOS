#include <StepVisual_TextLiteral.ixx>


StepVisual_TextLiteral::StepVisual_TextLiteral ()  {}

void StepVisual_TextLiteral::Init(
	const Handle(TCollection_HAsciiString)& aName)
{

	StepRepr_RepresentationItem::Init(aName);
}

void StepVisual_TextLiteral::Init(
	const Handle(TCollection_HAsciiString)& aName,
	const Handle(TCollection_HAsciiString)& aLiteral,
	const StepGeom_Axis2Placement& aPlacement,
	const Handle(TCollection_HAsciiString)& aAlignment,
	const StepVisual_TextPath aPath,
	const StepVisual_FontSelect& aFont)
{
	// --- classe own fields ---
	literal = aLiteral;
	placement = aPlacement;
	alignment = aAlignment;
	path = aPath;
	font = aFont;
	// --- classe inherited fields ---
	StepRepr_RepresentationItem::Init(aName);
}


void StepVisual_TextLiteral::SetLiteral(const Handle(TCollection_HAsciiString)& aLiteral)
{
	literal = aLiteral;
}

Handle(TCollection_HAsciiString) StepVisual_TextLiteral::Literal() const
{
	return literal;
}

void StepVisual_TextLiteral::SetPlacement(const StepGeom_Axis2Placement& aPlacement)
{
	placement = aPlacement;
}

StepGeom_Axis2Placement StepVisual_TextLiteral::Placement() const
{
	return placement;
}

void StepVisual_TextLiteral::SetAlignment(const Handle(TCollection_HAsciiString)& aAlignment)
{
	alignment = aAlignment;
}

Handle(TCollection_HAsciiString) StepVisual_TextLiteral::Alignment() const
{
	return alignment;
}

void StepVisual_TextLiteral::SetPath(const StepVisual_TextPath aPath)
{
	path = aPath;
}

StepVisual_TextPath StepVisual_TextLiteral::Path() const
{
	return path;
}

void StepVisual_TextLiteral::SetFont(const StepVisual_FontSelect& aFont)
{
	font = aFont;
}

StepVisual_FontSelect StepVisual_TextLiteral::Font() const
{
	return font;
}
