#include <EDL_Variable.ixx>

EDL_Variable::EDL_Variable()
{
}

EDL_Variable::EDL_Variable(const Standard_CString aName, const Standard_CString aValue)
{

  if (aName != NULL) { 
    myName  = new TCollection_HAsciiString(aName);
  }

  if (aValue != NULL) {
    myValue = new TCollection_HAsciiString(aValue);
  }
}

EDL_Variable::EDL_Variable(const EDL_Variable& aVar)
{
  Assign(aVar);
}

void EDL_Variable::Assign(const EDL_Variable& aVar)
{
  if (!aVar.myName.IsNull()) {
    myName  = aVar.myName;
  }

  if (!aVar.myValue.IsNull()) {
    myValue = aVar.myValue;
  }
}

void EDL_Variable::Destroy() const 
{
}

Standard_CString EDL_Variable::GetName() const 
{
  return myName->ToCString();
}

Standard_CString EDL_Variable::GetValue() const 
{
  return myValue->ToCString();
}

void EDL_Variable::SetValue(const Standard_CString aValue)
{
  myValue.Nullify();

  if (aValue != NULL) {
    myValue = new TCollection_HAsciiString(aValue);
  }
}

Standard_Integer EDL_Variable::HashCode(const EDL_Variable& aVar, const Standard_Integer Upper)
{
  return ::HashCode(aVar.GetName(),Upper);
}

Standard_Boolean EDL_Variable::IsEqual(const EDL_Variable& aVar1, const EDL_Variable& aVar2)
{
  Standard_Boolean aResult = Standard_False;

  if (strcmp(aVar1.GetName(),aVar2.GetName()) == 0) {
    aResult = Standard_True;
  }

  return aResult;
}

