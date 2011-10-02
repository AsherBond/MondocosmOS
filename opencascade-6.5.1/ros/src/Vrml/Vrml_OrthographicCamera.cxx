#include <Vrml_OrthographicCamera.ixx>

Vrml_OrthographicCamera::Vrml_OrthographicCamera():
  myFocalDistance(5),
  myHeight(2)
{

  gp_Vec tmpVec(0,0,1);
  myPosition = tmpVec;

  Vrml_SFRotation tmpSFR(0,0,1,0);
  myOrientation = tmpSFR;
}

Vrml_OrthographicCamera::Vrml_OrthographicCamera( const gp_Vec&          aPosition, 
						  const Vrml_SFRotation& aOrientation, 
						  const Standard_Real    aFocalDistance, 
						  const Standard_Real    aHeight)
{
    myPosition      = aPosition;
    myOrientation   = aOrientation;
    myFocalDistance = aFocalDistance;
    myHeight        = aHeight;
}

void Vrml_OrthographicCamera::SetPosition(const gp_Vec& aPosition)
{
    myPosition = aPosition;
}

gp_Vec Vrml_OrthographicCamera::Position() const 
{
   return  myPosition;
}

void Vrml_OrthographicCamera::SetOrientation(const Vrml_SFRotation& aOrientation)
{
   myOrientation   = aOrientation;
}

Vrml_SFRotation Vrml_OrthographicCamera::Orientation() const 
{
   return myOrientation; 
}

void Vrml_OrthographicCamera::SetFocalDistance(const Standard_Real aFocalDistance)
{
   myFocalDistance = aFocalDistance;
}

Standard_Real Vrml_OrthographicCamera::FocalDistance() const 
{
   return myFocalDistance;
}

void Vrml_OrthographicCamera::SetHeight(const Standard_Real aHeight)
{
    myHeight = aHeight;
}

Standard_Real Vrml_OrthographicCamera::Height() const 
{
   return myHeight;
}

Standard_OStream& Vrml_OrthographicCamera::Print(Standard_OStream& anOStream) const 
{
 anOStream  << "OrthographicCamera {" << endl;
 if ( Abs(myPosition.X() - 0) > 0.0001 || 
      Abs(myPosition.Y() - 0) > 0.0001 || 
      Abs(myPosition.Z() - 1) > 0.0001 )
   {
    anOStream  << "    position" << "\t\t";
    anOStream << myPosition.X() << ' ' << myPosition.Y() << ' ' << myPosition.Z() << endl;
   }

 if ( Abs(myOrientation.RotationX() - 0) > 0.0001 || 
     Abs(myOrientation.RotationY() - 0) > 0.0001 || 
     Abs(myOrientation.RotationZ() - 1) > 0.0001 ||
     Abs(myOrientation.Angle() - 0) > 0.0001 )
   {
    anOStream  << "    orientation" << "\t\t";
    anOStream << myOrientation.RotationX() << ' ' << myOrientation.RotationY() << ' ';
    anOStream << myOrientation.RotationZ() << ' ' << myOrientation.Angle() << endl;
   }

 if ( Abs(myFocalDistance - 5) > 0.0001 )
   {
    anOStream  << "    focalDistance" << '\t';
    anOStream << myFocalDistance << endl;
   }
 if ( Abs(myHeight - 2) > 0.0001 )
   {
    anOStream  << "    height" << "\t\t";
    anOStream << myHeight << endl;
   }
 anOStream  << '}' << endl;
 return anOStream;
}
