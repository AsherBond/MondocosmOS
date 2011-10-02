#include <Vrml_AsciiText.ixx>

 Vrml_AsciiText::Vrml_AsciiText()
{
    TCollection_AsciiString tmpS("");
    myString =  new TColStd_HArray1OfAsciiString(1,1,tmpS);

    mySpacing = 1;
    myJustification = Vrml_LEFT;
    myWidth = 0;
}

 Vrml_AsciiText::Vrml_AsciiText(const Handle(TColStd_HArray1OfAsciiString)& aString, 
				const Standard_Real aSpacing, 
				const Vrml_AsciiTextJustification aJustification, 
				const Standard_Real aWidth)
{
    myString = aString;
    mySpacing = aSpacing;
    myJustification = aJustification;
    myWidth = aWidth;
}

void Vrml_AsciiText::SetString(const  Handle(TColStd_HArray1OfAsciiString)& aString)
{
    myString = aString;
}

Handle(TColStd_HArray1OfAsciiString) Vrml_AsciiText::String() const 
{
  return myString;
}

void Vrml_AsciiText::SetSpacing(const Standard_Real aSpacing)
{
    mySpacing = aSpacing;
}

Standard_Real Vrml_AsciiText::Spacing() const 
{
  return mySpacing;
}

void Vrml_AsciiText::SetJustification(const Vrml_AsciiTextJustification aJustification)
{
    myJustification = aJustification;
}

Vrml_AsciiTextJustification Vrml_AsciiText::Justification() const 
{
  return myJustification;
}

void Vrml_AsciiText::SetWidth(const Standard_Real aWidth)
{
    myWidth = aWidth;
}

Standard_Real Vrml_AsciiText::Width() const 
{
  return myWidth;
}

Standard_OStream& Vrml_AsciiText::Print(Standard_OStream& anOStream) const 
{
 Standard_Integer i;

 anOStream  << "AsciiText {" << endl;

 i = myString->Lower();

 if ( myString->Length() != 1 || myString->Value(i) != "" )
   {
    anOStream  << "    string [" << endl << '\t';

    for ( i = myString->Lower(); i <= myString->Upper(); i++ )
	{
	 anOStream << '"' << myString->Value(i) << '"';
	 if ( i < myString->Length() )
	    anOStream  << ',' << endl << '\t';
        }
    anOStream  << " ]" << endl;
   }

 if ( Abs(mySpacing - 1 ) > 0.0001 )
   {
    anOStream  << "    spacing" << "\t\t";
    anOStream << mySpacing << endl;
   }

  switch ( myJustification )
    {
     case Vrml_LEFT: break; // anOStream  << "    justification" << "\t LEFT";
     case Vrml_CENTER:        anOStream  << "    justification" << "\tCENTER" << endl; break;
     case Vrml_RIGHT: anOStream  << "    justification" << "\tRIGHT" << endl; break; 
    }

 if ( Abs(myWidth - 0 ) > 0.0001 )
   {
    anOStream  << "    width" << "\t\t";
    anOStream << myWidth << endl;
   }

 anOStream  << '}' << endl;
 return anOStream;
}
