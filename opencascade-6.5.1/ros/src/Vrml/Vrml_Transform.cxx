#include <Vrml_Transform.ixx>

Vrml_Transform::Vrml_Transform()
{
  gp_Vec tmpV(0,0,0);
  myTranslation = tmpV;

  Vrml_SFRotation tmpSFR(0,0,1,0);
  myRotation = tmpSFR;

  tmpV.SetX(1);
  tmpV.SetY(1);
  tmpV.SetZ(1);
  myScaleFactor = tmpV;

  tmpSFR.SetRotationX(0);
  tmpSFR.SetRotationY(0);
  tmpSFR.SetRotationZ(1);
  tmpSFR.SetAngle(0);
  myScaleOrientation = tmpSFR;

  tmpV.SetX(0);
  tmpV.SetY(0);
  tmpV.SetZ(0);
  myCenter = tmpV;
}

Vrml_Transform::Vrml_Transform(const gp_Vec& aTranslation,
			       const Vrml_SFRotation& aRotation,
			       const gp_Vec& aScaleFactor,
			       const Vrml_SFRotation& aScaleOrientation,
			       const gp_Vec& aCenter)
{
  myTranslation = aTranslation;
  myRotation = aRotation;
  myScaleFactor = aScaleFactor;
  myScaleOrientation = aScaleOrientation;
  myCenter = aCenter;
}

 void Vrml_Transform::SetTranslation(const gp_Vec& aTranslation) 
{
  myTranslation = aTranslation;
}

 gp_Vec Vrml_Transform::Translation() const
{
  return  myTranslation;
}

 void Vrml_Transform::SetRotation(const Vrml_SFRotation& aRotation) 
{
  myRotation = aRotation;
}

 Vrml_SFRotation Vrml_Transform::Rotation() const
{
  return  myRotation;
}

 void Vrml_Transform::SetScaleFactor(const gp_Vec& aScaleFactor) 
{
  myScaleFactor = aScaleFactor;
}

 gp_Vec Vrml_Transform::ScaleFactor() const
{
  return myScaleFactor;
}

 void Vrml_Transform::SetScaleOrientation(const Vrml_SFRotation& aScaleOrientation) 
{
  myScaleOrientation = aScaleOrientation;
}

 Vrml_SFRotation Vrml_Transform::ScaleOrientation() const
{
  return  myScaleOrientation;
}

 void Vrml_Transform::SetCenter(const gp_Vec& aCenter) 
{
  myCenter = aCenter;
}

 gp_Vec Vrml_Transform::Center() const
{
  return  myCenter;
}

 Standard_OStream& Vrml_Transform::Print(Standard_OStream& anOStream) const
{
 anOStream  << "Transform {" << endl;

 if ( Abs(myTranslation.X() - 0) > 0.0001 || 
     Abs(myTranslation.Y() - 0) > 0.0001 || 
     Abs(myTranslation.Z() - 0) > 0.0001 ) 
   {
    anOStream  << "    translation" << "\t\t";
    anOStream << myTranslation.X() << ' ' << myTranslation.Y() << ' ' << myTranslation.Z() << endl;
   }

 if ( Abs(myRotation.RotationX() - 0) > 0.0001 || 
      Abs(myRotation.RotationY() - 0) > 0.0001 || 
      Abs(myRotation.RotationZ() - 1) > 0.0001 ||
      Abs(myRotation.Angle() - 0) > 0.0001 ) 
   {
    anOStream  << "    rotation" << "\t\t";
    anOStream << myRotation.RotationX() << ' ' << myRotation.RotationY() << ' ';
    anOStream << myRotation.RotationZ() << ' ' << myRotation.Angle() << endl;
   }

 if ( Abs(myScaleFactor.X() - 1) > 0.0001 || 
     Abs(myScaleFactor.Y() - 1) > 0.0001 || 
     Abs(myScaleFactor.Z() - 1) > 0.0001 ) 
   {
    anOStream  << "    scaleFactor" << "\t\t";
    anOStream << myTranslation.X() << ' ' << myTranslation.Y() << ' ' << myTranslation.Z() << endl;
   }

 if ( Abs(myScaleOrientation.RotationX() - 0) > 0.0001 || 
     Abs(myScaleOrientation.RotationY() - 0) > 0.0001 || 
     Abs(myScaleOrientation.RotationZ() - 1) > 0.0001 || 
     Abs(myScaleOrientation.Angle() - 0) > 0.0001 ) 
   {
    anOStream  << "    scaleOrientation" << '\t';
    anOStream << myScaleOrientation.RotationX() << ' ' << myScaleOrientation.RotationY() << ' ';
    anOStream << myScaleOrientation.RotationZ() << ' ' << myScaleOrientation.Angle() << endl;
   }

 if ( Abs(myCenter.X() - 0) > 0.0001 || 
     Abs(myCenter.Y() - 0) > 0.0001 || 
     Abs(myCenter.Z() - 0) > 0.0001 ) 
   {
    anOStream  << "    center" << "\t\t";
    anOStream << myCenter.X() << ' ' << myCenter.Y() << ' ' << myCenter.Z() << endl;
   }

 anOStream  << '}' << endl;
 return anOStream;
}
