#include <StepShape_OrientedFace.ixx>


StepShape_OrientedFace::StepShape_OrientedFace ()  {}

void StepShape_OrientedFace::Init(
	const Handle(TCollection_HAsciiString)& aName,
	const Handle(StepShape_HArray1OfFaceBound)& aBounds)
{

	StepShape_Face::Init(aName, aBounds);
}

void StepShape_OrientedFace::Init(
	const Handle(TCollection_HAsciiString)& aName,
	const Handle(StepShape_Face)& aFaceElement,
	const Standard_Boolean aOrientation)
{
	// --- classe own fields ---
	faceElement = aFaceElement;
	orientation = aOrientation;
	// --- classe inherited fields ---
	Handle(StepShape_HArray1OfFaceBound) aBounds;
	aBounds.Nullify();
	StepShape_Face::Init(aName, aBounds);
}


void StepShape_OrientedFace::SetFaceElement(const Handle(StepShape_Face)& aFaceElement)
{
	faceElement = aFaceElement;
}

Handle(StepShape_Face) StepShape_OrientedFace::FaceElement() const
{
	return faceElement;
}

void StepShape_OrientedFace::SetOrientation(const Standard_Boolean aOrientation)
{
	orientation = aOrientation;
}

Standard_Boolean StepShape_OrientedFace::Orientation() const
{
	return orientation;
}

void StepShape_OrientedFace::SetBounds(const Handle(StepShape_HArray1OfFaceBound)& aBounds)
{
	// WARNING : the field is redefined.
	// field set up forbidden.
	cout << "Field is redefined, SetUp Forbidden" << endl;
}

Handle(StepShape_HArray1OfFaceBound) StepShape_OrientedFace::Bounds() const
{
  // WARNING : the field is redefined.
  // method body is not yet automaticly wrote
  // Attention, cette modif. est juste pour la compilation  
  return faceElement->Bounds();

}

Handle(StepShape_FaceBound) StepShape_OrientedFace::BoundsValue(const Standard_Integer num) const
{
  // WARNING : the field is redefined.
  // method body is not yet automaticly wrote
  // Attention, cette modif. est juste pour la compilation  
  return faceElement->BoundsValue(num);
}

Standard_Integer StepShape_OrientedFace::NbBounds () const
{
  // WARNING : the field is redefined.
  // method body is not yet automaticly wrote
  // Attention, cette modif. est juste pour la compilation  
  return faceElement->NbBounds();
}
