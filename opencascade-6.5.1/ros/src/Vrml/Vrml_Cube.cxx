#include <Vrml_Cube.ixx>

Vrml_Cube::Vrml_Cube(const Standard_Real aWidth,
		     const Standard_Real aHeight,
		     const Standard_Real aDepth)
{
    myWidth = aWidth;
    myHeight = aHeight;
    myDepth = aDepth;
}

 void Vrml_Cube::SetWidth(const Standard_Real aWidth) 
{
    myWidth = aWidth;
}

 Standard_Real Vrml_Cube::Width() const
{
  return myWidth;
}

 void Vrml_Cube::SetHeight(const Standard_Real aHeight) 
{
    myHeight = aHeight;
}

 Standard_Real Vrml_Cube::Height() const
{
  return myHeight;
}

 void Vrml_Cube::SetDepth(const Standard_Real aDepth) 
{
    myDepth = aDepth;
}

 Standard_Real Vrml_Cube::Depth() const
{
  return myDepth;
}

 Standard_OStream& Vrml_Cube::Print(Standard_OStream& anOStream) const
{
 anOStream  << "Cube {" << endl;

 if ( Abs(myWidth - 2) > 0.0001 )
   {
    anOStream  << "    width" << '\t';
    anOStream << myWidth << endl;
   }

 if ( Abs(myHeight - 2) > 0.0001 )
   {
    anOStream  << "    height" << '\t';
    anOStream << myHeight << endl;
   }

 if ( Abs(myDepth - 2) > 0.0001 )
   {
    anOStream  << "    depth" << '\t';
    anOStream << myDepth << endl;
   }

 anOStream  << '}' << endl;
 return anOStream;

}
