#ifndef _ShapeSchema_Standard_Storable_HeaderFile
#include <ShapeSchema_Standard_Storable.hxx>
#endif
#ifndef _Standard_Storable_HeaderFile
#include <Standard_Storable.hxx>
#endif
#include <ShapeSchema_Standard_Storable.ixx>
#ifndef _Storage_Schema_HeaderFile
#include <Storage_Schema.hxx>
#endif
#ifndef _Storage_stCONSTclCOM_HeaderFile
#include <Storage_stCONSTclCOM.hxx>
#endif

void ShapeSchema_Standard_Storable::SWrite(const Standard_Storable& pp, Storage_BaseDriver& f, const Handle(Storage_Schema)& theSchema)
{
  f.BeginWriteObjectData();

  f.EndWriteObjectData();
}

void ShapeSchema_Standard_Storable::SRead(Standard_Storable& pp, Storage_BaseDriver& f, const Handle(Storage_Schema)& theSchema)
{
  f.BeginReadObjectData();

  f.EndReadObjectData();
}
