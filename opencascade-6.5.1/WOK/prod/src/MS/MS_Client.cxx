#include <MS_Client.ixx>
#include <MS.hxx>
#include <MS_Interface.hxx>
#include <MS_MetaSchema.hxx>

MS_Client::MS_Client(const Handle(TCollection_HAsciiString)& aClient) : MS_GlobalEntity(aClient)
{
  myInterfaces = new TColStd_HSequenceOfHAsciiString;
  myMethods    = new TColStd_HSequenceOfHAsciiString;
  myUses       = new TColStd_HSequenceOfHAsciiString;
}

void MS_Client::Interface(const Handle(TCollection_HAsciiString)& anInter)
{
  myInterfaces->Append(anInter);
}

Handle(TColStd_HSequenceOfHAsciiString) MS_Client::Interfaces() const 
{
  return myInterfaces;
}

void MS_Client::Method(const Handle(TCollection_HAsciiString)& aMethod)
{
  myMethods->Append(aMethod);
}

Handle(TColStd_HSequenceOfHAsciiString) MS_Client::Methods() const 
{
  return myMethods;
}


void MS_Client::ComputeTypes(const Handle(MS_HSequenceOfExternMet)& SeqOfExternMet,
			     const Handle(MS_HSequenceOfMemberMet)& SeqOfMemberMet,
			     WOKTools_MapOfHAsciiString& ExtractionMap,
			     WOKTools_MapOfHAsciiString& ExtractionIncpMap,
			     WOKTools_MapOfHAsciiString& ExtractionSemiMap) const
{
  Standard_Integer      i,len = myInterfaces->Length();
  Handle(MS_Interface)  anInterface;
  Handle(TCollection_HAsciiString) aName;

  for(i = 1; i <= len; i++) {
    aName = myInterfaces->Value(i);
    if (GetMetaSchema()->IsInterface(aName)) {
      anInterface = GetMetaSchema()->GetInterface(aName);
    
      MS::StubClassesToExtract(GetMetaSchema(),anInterface->Classes(),ExtractionMap,ExtractionIncpMap,ExtractionSemiMap);
    }
  }

  for(i = 1; i <= len; i++) {
    aName = myInterfaces->Value(i);
    if (GetMetaSchema()->IsInterface(aName)) {
      anInterface = GetMetaSchema()->GetInterface(aName);

      MS::StubPackagesToExtract(GetMetaSchema(),anInterface,ExtractionMap,ExtractionIncpMap,ExtractionSemiMap);
    }
  }

  for(i = 1; i <= len; i++) {
    aName = myInterfaces->Value(i);
    if (GetMetaSchema()->IsInterface(aName)) {
      anInterface = GetMetaSchema()->GetInterface(aName);
      
      MS::StubMethodsToExtract(GetMetaSchema(),anInterface,SeqOfExternMet,SeqOfMemberMet,ExtractionMap,ExtractionIncpMap,ExtractionSemiMap);
    }
  }
}

void MS_Client :: Use (  const Handle( TCollection_HAsciiString )& aClient  ) {

 myUses -> Append ( aClient );

}  // end MS_Client :: Use

Handle( TColStd_HSequenceOfHAsciiString ) MS_Client :: Uses () const {

 return myUses;

}  // end MS_Client :: Uses



