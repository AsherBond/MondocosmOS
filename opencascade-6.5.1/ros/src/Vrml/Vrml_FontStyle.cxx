#include <Vrml_FontStyle.ixx>

Vrml_FontStyle::Vrml_FontStyle(const Standard_Real aSize,
			       const Vrml_FontStyleFamily aFamily,
			       const Vrml_FontStyleStyle aStyle)
{
  mySize = aSize;
  myFamily = aFamily;
  myStyle = aStyle;
}

 void Vrml_FontStyle::SetSize(const Standard_Real aSize) 
{
  mySize = aSize;
}

 Standard_Real Vrml_FontStyle::Size() const
{
  return mySize;
}

 void Vrml_FontStyle::SetFamily(const Vrml_FontStyleFamily aFamily) 
{
  myFamily = aFamily;
}

 Vrml_FontStyleFamily Vrml_FontStyle::Family() const
{
  return myFamily;
}

 void Vrml_FontStyle::SetStyle(const Vrml_FontStyleStyle aStyle) 
{
  myStyle = aStyle;
}

 Vrml_FontStyleStyle Vrml_FontStyle::Style() const
{
  return myStyle;
}

 Standard_OStream& Vrml_FontStyle::Print(Standard_OStream& anOStream) const
{
 anOStream  << "FontStyle {" << endl;

 if ( Abs(mySize - 10) > 0.0001 )
   {
    anOStream  << "    size" << '\t';
    anOStream <<  mySize  << endl;
   }

  switch ( myFamily )
    {
     case Vrml_SERIF:      break; // anOStream  << "    family" << "\tSERIF ";
     case Vrml_SANS:       anOStream  << "    family" << "\tSANS" << endl; break;
     case Vrml_TYPEWRITER: anOStream  << "    family" << "\tTYPEWRITER" << endl; break; 
    }

  switch ( myStyle )
    {
     case Vrml_NONE:   break; // anOStream  << "    style" << "\tSERIF ";
     case Vrml_BOLD:   anOStream  << "    style" << "\tBOLD" << endl; break;
     case Vrml_ITALIC: anOStream  << "    style" << "\tITALIC" << endl; break; 
    }

 anOStream  << '}' << endl;
 return anOStream;

}
