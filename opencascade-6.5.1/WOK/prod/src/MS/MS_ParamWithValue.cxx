#include <MS_ParamWithValue.ixx>
#include <Standard_NoSuchObject.hxx>
#include <MS_MetaSchema.hxx>
#include <MS.hxx>
#include <MS_HSequenceOfMemberMet.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <MS_MemberMet.hxx>
#include <MS_Class.hxx>

MS_ParamWithValue::MS_ParamWithValue(const Handle(MS_Method)& aMethod, 
		   const Handle(TCollection_HAsciiString)& aName) : MS_Param(aMethod,aName), myTypeVal(MS_NONE)
{
}

void MS_ParamWithValue::IntegerValue(const Handle(TCollection_HAsciiString)& anInteger)
{
  myValue     = anInteger;
  myTypeVal = MS_INTEGER;
}

void MS_ParamWithValue::RealValue(const Handle(TCollection_HAsciiString)& aReal)
{
  myValue = aReal;
  myTypeVal = MS_REAL;
}

void MS_ParamWithValue::StringValue(const Handle(TCollection_HAsciiString)& aString)
{
  myValue = aString;
  myTypeVal = MS_STRING;
}

void MS_ParamWithValue::EnumValue(const Handle(TCollection_HAsciiString)& anEnum)
{
  myValue = anEnum;
  myTypeVal = MS_ENUM;
}

void MS_ParamWithValue::Value(const Handle(TCollection_HAsciiString)& aValue, const MS_TypeOfValue TypeVal)
{
  myValue = aValue;
  myTypeVal = TypeVal;
}

Handle(TCollection_HAsciiString) MS_ParamWithValue::GetValue() const 
{
  return myValue;
}

MS_TypeOfValue MS_ParamWithValue::GetValueType() const 
{
  return myTypeVal;
}
