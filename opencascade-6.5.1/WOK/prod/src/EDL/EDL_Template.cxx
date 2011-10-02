#include <EDL_Template.ixx>
#include <TCollection_AsciiString.hxx>
#include <EDL_Variable.hxx>

EDL_Template::EDL_Template()
{
  myValue    = new TColStd_HSequenceOfAsciiString;
  myEval     = new TColStd_HSequenceOfAsciiString;
  myVariable = new TColStd_HSequenceOfHAsciiString;
}

EDL_Template::EDL_Template(const Standard_CString aName)
{
  myValue = new TColStd_HSequenceOfAsciiString;
  myEval  = new TColStd_HSequenceOfAsciiString;
  myVariable = new TColStd_HSequenceOfHAsciiString;

  if (aName != NULL) {
    myName  = new TCollection_HAsciiString(aName);
  }
}

EDL_Template::EDL_Template(const EDL_Template& aTemplate)
{
  Assign(aTemplate);
}

void EDL_Template::Assign(const EDL_Template& aTemplate)
{
  Standard_Integer i;

  if (aTemplate.GetName() != NULL) {
    myName  = new TCollection_HAsciiString(aTemplate.GetName());
  }

  myValue = new TColStd_HSequenceOfAsciiString;

  for (i = 1; i <= aTemplate.myValue->Length(); i++) {
    myValue->Append(aTemplate.myValue->Value(i));
  }
  
  myEval  = new TColStd_HSequenceOfAsciiString;

  for (i = 1; i <= aTemplate.myEval->Length(); i++) {
    myEval->Append(aTemplate.myEval->Value(i));
  }

  myVariable  = new TColStd_HSequenceOfHAsciiString;

  for (i = 1; i <= aTemplate.myVariable->Length(); i++) {
    myVariable->Append(aTemplate.myVariable->Value(i));
  }
}

void EDL_Template::Destroy() const
{
}

Standard_CString EDL_Template::GetName() const 
{
  return myName->ToCString();
}

Standard_CString EDL_Template::GetLine(const Standard_Integer index) const 
{
  if (index > 0 && index <= myValue->Length()) {
    return myValue->Value(index).ToCString();
  }
  else return NULL;
}

void EDL_Template::SetLine(const Standard_Integer index, const Standard_CString aValue)
{
  if (index > 0 && index <= myValue->Length() && aValue != NULL) {
    myValue->SetValue(index,aValue);
  }
}

void EDL_Template::AddLine(const Standard_CString aValue)
{
  TCollection_AsciiString aLine(aValue);
  Standard_Integer        pos;

  pos = aLine.SearchFromEnd("\\^");

  if (pos > 0) {
    aLine.Trunc(pos-1);
  }

  myValue->Append(aLine);
}

void EDL_Template::ClearLines()
{
  myValue->Clear();
}

void EDL_Template :: Eval (  const Handle( EDL_HSequenceOfVariable )& aVar  ) {

 Standard_Integer nbVar  = aVar    -> Length ();
 Standard_Integer nbLine = myValue -> Length ();

 static char      newString[ 262144 ], result[ 400000 ];
 Standard_CString vname, vvalue;

 myEval -> Clear ();

 newString[ 0 ] = '\0';
 result[    0 ] = '\0';

 for ( Standard_Integer lineCount = 1; lineCount <= nbLine; ++lineCount ) {

  Standard_Integer ipos, lenvname, rpos;

  memcpy (
   newString,
   myValue -> Value ( lineCount ).ToCString (),
   myValue -> Value ( lineCount ).Length () + 1
  );

  for ( Standard_Integer varCount = 1; varCount <= nbVar; ++varCount ) {

   vname    = aVar -> Value ( varCount ).GetName  ();
   vvalue   = aVar -> Value ( varCount ).GetValue ();
   lenvname = strlen ( vname );

   for ( rpos = ipos = 0; newString[ ipos ] && ipos < 262144; ++ipos ) {

    if ( newString[ ipos ] == '%' ) {

     if (  memcmp ( &newString[ ipos ], vname, lenvname ) == 0  ) {

      for ( Standard_Integer vpos = 0; vvalue[ vpos ] != 0; ++vpos )

       result[ rpos++ ] = vvalue[ vpos ];
	    
	  ipos += lenvname - 1;

     } else result[ rpos++ ] = newString[ ipos ];

    } else result[ rpos++ ] = newString[ ipos ];

   }  // end for

   result[ rpos ] = '\0';
   memcpy ( newString, result, rpos + 1 );

  }  // end for

  myEval -> Append (  TCollection_AsciiString ()  );
  myEval -> ChangeValue (  myEval -> Length ()  ).Copy ( newString );

 }  // end for

}  // end EDL_Template :: Eval

Handle(TColStd_HSequenceOfAsciiString) EDL_Template::GetEval() const 
{
  return myEval;
}

void EDL_Template::VariableList(const Handle(TColStd_HSequenceOfHAsciiString)& aVar)
{
  myVariable = aVar;
}

void EDL_Template::AddToVariableList(const Handle(TCollection_HAsciiString)& aVarName)
{
  myVariable->Append(aVarName);
}

Handle(TColStd_HSequenceOfHAsciiString) EDL_Template::GetVariableList() const 
{
  return myVariable;
}

Standard_Integer EDL_Template::HashCode(const EDL_Template& aVar, const Standard_Integer Upper)
{
  return ::HashCode(aVar.GetName(),Upper);
}

Standard_Boolean EDL_Template::IsEqual(const EDL_Template& aTemp1, const EDL_Template& aTemp2)
{
  Standard_Boolean aResult = Standard_False;

  if (strcmp(aTemp1.GetName(),aTemp2.GetName()) == 0) {
    aResult = Standard_True;
  }

  return aResult;
}

