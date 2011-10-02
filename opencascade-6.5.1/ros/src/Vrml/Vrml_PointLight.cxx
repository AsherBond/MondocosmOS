#include <Vrml_PointLight.ixx>

Vrml_PointLight::Vrml_PointLight():
  myOnOff(Standard_True),
  myIntensity(1)
{
  gp_Vec tmpVec(0,0,1);
  myLocation = tmpVec;

  Quantity_Color tmpColor(1,1,1,Quantity_TOC_RGB);
  myColor = tmpColor;
}

 Vrml_PointLight::Vrml_PointLight( const Standard_Boolean aOnOff, 
				   const Standard_Real aIntensity, 
			           const Quantity_Color& aColor, 
			           const gp_Vec& aLocation)
{
  if (aIntensity < 0. || aIntensity > 1.)
    {
      Standard_Failure::Raise("Error : Light intensity must be in the range 0.0 to 1.0, inclusive.");
    }
  myOnOff = aOnOff;
  myIntensity = aIntensity;
  myColor = aColor;
  myLocation = aLocation;
}

void Vrml_PointLight::SetOnOff(const Standard_Boolean aOnOff)
{
  myOnOff = aOnOff;
}

Standard_Boolean Vrml_PointLight::OnOff() const 
{
  return myOnOff;
}

void Vrml_PointLight::SetIntensity(const Standard_Real aIntensity)
{
  if (aIntensity < 0. || aIntensity > 1.)
    {
      Standard_Failure::Raise("Error : Light intensity must be in the range 0.0 to 1.0, inclusive.");
    }
  myIntensity = aIntensity;
}

Standard_Real Vrml_PointLight::Intensity() const 
{
  return myIntensity;
}

void Vrml_PointLight::SetColor(const Quantity_Color& aColor)
{
  myColor = aColor;
}

Quantity_Color Vrml_PointLight::Color() const 
{
  return  myColor;
}

void Vrml_PointLight::SetLocation(const gp_Vec& aLocation)
{
  myLocation = aLocation;
}

gp_Vec Vrml_PointLight::Location() const 
{
  return myLocation;
}

Standard_OStream& Vrml_PointLight::Print(Standard_OStream& anOStream) const 
{
 anOStream  << "PointLight {" << endl;

 if ( myOnOff != Standard_True )
   {
    anOStream  << "    on" << "\t\t" << "FALSE" << endl;
//    anOStream << myOnOff << endl;
   }

 if ( Abs(myIntensity - 1) > 0.0001 )
   {
    anOStream  << "    intensity" << '\t';
    anOStream << myIntensity << endl;
   }

 if ( Abs(myColor.Red() - 1) > 0.0001 || 
      Abs(myColor.Green() - 1) > 0.0001 || 
      Abs(myColor.Blue() - 1) > 0.0001 )
   {
    anOStream  << "    color" << '\t';
    anOStream << myColor.Red() << ' ' << myColor.Green() << ' ' << myColor.Blue() << endl;
   }

 if ( Abs(myLocation.X() - 0) > 0.0001 || 
     Abs(myLocation.Y() - 0) > 0.0001 || 
     Abs(myLocation.Z() - 1) > 0.0001 ) 
   {
    anOStream  << "    location" << '\t';
    anOStream << myLocation.X() << ' ' << myLocation.Y() << ' ' << myLocation.Z() << endl;
   }

 anOStream  << '}' << endl;
 return anOStream;
}
