#include <MS_Common.ixx>
#include <Standard_NullObject.hxx>

MS_Common::MS_Common(const Handle(TCollection_HAsciiString)& aName) : myName(aName),myFullName(aName),myMetaSchema((MS_MetaSchemaPtr)UndefinedHandleAddress)
{
}

MS_Common::MS_Common(const Handle(TCollection_HAsciiString)& aName, const Handle(MS_MetaSchema)& aMetaSchema) : myName(aName),myFullName(aName),myMetaSchema(aMetaSchema.operator->())
{
}

void MS_Common::Name(const Handle(TCollection_HAsciiString)& aName)
{
  if (!aName.IsNull()) {
    myName       = aName;
  }
  else {
    Standard_NullObject::Raise("MS_Common::Name - aName is NULL");
  }
}

const Handle(TCollection_HAsciiString)& MS_Common::Name() const 
{
  return myName;
}

void MS_Common::FullName(const Handle(TCollection_HAsciiString)& aName)
{
  if (!aName.IsNull()) {
    myFullName       = aName;
  }
  else {
    Standard_NullObject::Raise("MS_Common::FullName - aName is NULL");
  }
}

const Handle(TCollection_HAsciiString)& MS_Common::FullName() const 
{
  return myFullName;
}

void MS_Common::MetaSchema(const Handle(MS_MetaSchema)& aMetaSchema)
{
  myMetaSchema = aMetaSchema.operator->();
}

MS_MetaSchemaPtr MS_Common::GetMetaSchema() const 
{
  return myMetaSchema;
}
