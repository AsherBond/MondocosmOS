#include <Vrml_WWWInline.ixx>

Vrml_WWWInline::Vrml_WWWInline()
{
  myName = "";
  gp_Vec tmpVec(0,0,0);
  myBboxSize = tmpVec;
  myBboxCenter = tmpVec;
}

Vrml_WWWInline::Vrml_WWWInline(const TCollection_AsciiString& aName,
			       const gp_Vec& aBboxSize,
			       const gp_Vec& aBboxCenter)
{
  myName = aName;
  myBboxSize = aBboxSize;
  myBboxCenter = aBboxCenter;
}

void Vrml_WWWInline::SetName(const TCollection_AsciiString& aName) 
{
  myName = aName;
}

TCollection_AsciiString Vrml_WWWInline::Name() const
{
  return myName;
}

void Vrml_WWWInline::SetBboxSize(const gp_Vec& aBboxSize) 
{
  myBboxSize = aBboxSize;
}

gp_Vec Vrml_WWWInline::BboxSize() const
{
  return myBboxSize;
}

void Vrml_WWWInline::SetBboxCenter(const gp_Vec& aBboxCenter) 
{
  myBboxCenter = aBboxCenter;
}

gp_Vec Vrml_WWWInline::BboxCenter() const
{
  return myBboxCenter;
}

Standard_OStream& Vrml_WWWInline::Print(Standard_OStream& anOStream) const
{
 anOStream  << "WWWInline {" << endl;

 if ( !(myName.IsEqual ("") ) )
   {
    anOStream  << "    name" << '\t';
    anOStream << '"' << myName << '"' << endl;
   }

 if ( Abs(myBboxSize.X() - 0) > 0.0001 || 
     Abs(myBboxSize.Y() - 0) > 0.0001 || 
     Abs(myBboxSize.Z() - 0) > 0.0001 ) 
   {
    anOStream  << "    bboxSize" << '\t';
    anOStream << myBboxSize.X() << ' ' << myBboxSize.Y() << ' ' << myBboxSize.Z() << endl;
   }

 if ( Abs(myBboxCenter.X() - 0) > 0.0001 || 
     Abs(myBboxCenter.Y() - 0) > 0.0001 || 
     Abs(myBboxCenter.Z() - 0) > 0.0001 ) 
   {
    anOStream  << "    bboxCenter" << '\t';
    anOStream << myBboxCenter.X() << ' ' << myBboxCenter.Y() << ' ' << myBboxCenter.Z() << endl;
   }

 anOStream  << '}' << endl;
 return anOStream;
}
