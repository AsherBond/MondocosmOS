#ifndef _StdSchema_PTopoDS_HArray1OfShape1_HeaderFile
#include <StdSchema_PTopoDS_HArray1OfShape1.hxx>
#endif
#ifndef _PTopoDS_HArray1OfShape1_HeaderFile
#include <PTopoDS_HArray1OfShape1.hxx>
#endif
#include <StdSchema_PTopoDS_HArray1OfShape1.ixx>
#ifndef _Storage_Schema_HeaderFile
#include <Storage_Schema.hxx>
#endif
#ifndef _Storage_stCONSTclCOM_HeaderFile
#include <Storage_stCONSTclCOM.hxx>
#endif

IMPLEMENT_STANDARD_HANDLE(StdSchema_PTopoDS_HArray1OfShape1,Storage_CallBack)
IMPLEMENT_STANDARD_RTTIEXT(StdSchema_PTopoDS_HArray1OfShape1,Storage_CallBack)

Handle(Standard_Persistent) StdSchema_PTopoDS_HArray1OfShape1::New() const
{
  return new PTopoDS_HArray1OfShape1(Storage_stCONSTclCOM());
}

void StdSchema_PTopoDS_HArray1OfShape1::SAdd(const Handle(PTopoDS_HArray1OfShape1)& p, const Handle(Storage_Schema)& theSchema)
{
  if (!p.IsNull()) {
    if (theSchema->AddPersistent(p,"PTopoDS_HArray1OfShape1")) {
         StdSchema_PTopoDS_FieldOfHArray1OfShape1::SAdd(p->_CSFDB_GetPTopoDS_HArray1OfShape1Data(),theSchema);

    }
  }
}

void StdSchema_PTopoDS_HArray1OfShape1::Add(const Handle(Standard_Persistent)& p, const Handle(Storage_Schema)& theSchema) const
{
  StdSchema_PTopoDS_HArray1OfShape1::SAdd((Handle(PTopoDS_HArray1OfShape1)&)p,theSchema);
}

void StdSchema_PTopoDS_HArray1OfShape1::SWrite(const Handle(Standard_Persistent)& p, Storage_BaseDriver& f, const Handle(Storage_Schema)& theSchema)
{ 
  if (!p.IsNull()) {
    Handle(PTopoDS_HArray1OfShape1) &pp = (Handle(PTopoDS_HArray1OfShape1)&)p;
    theSchema->WritePersistentObjectHeader(p,f);
    
    f.BeginWritePersistentObjectData();
  f.PutInteger(pp->_CSFDB_GetPTopoDS_HArray1OfShape1LowerBound());
  f.PutInteger(pp->_CSFDB_GetPTopoDS_HArray1OfShape1UpperBound());
    StdSchema_PTopoDS_FieldOfHArray1OfShape1::SWrite(pp->_CSFDB_GetPTopoDS_HArray1OfShape1Data(),f,theSchema);

    f.EndWritePersistentObjectData();
  }
}

void StdSchema_PTopoDS_HArray1OfShape1::Write(const Handle(Standard_Persistent)& p, Storage_BaseDriver& f, const Handle(Storage_Schema)& theSchema) const
{ 
  StdSchema_PTopoDS_HArray1OfShape1::SWrite(p,f,theSchema);
}


void StdSchema_PTopoDS_HArray1OfShape1::SRead(const Handle(Standard_Persistent)& p, Storage_BaseDriver& f, const Handle(Storage_Schema)& theSchema)
{ 
  if (!p.IsNull()) {
    Handle(PTopoDS_HArray1OfShape1) &pp = (Handle(PTopoDS_HArray1OfShape1)&)p;

    theSchema->ReadPersistentObjectHeader(f);
    f.BeginReadPersistentObjectData();

    Standard_Integer PTopoDS_HArray1OfShape1LowerBound;
    f.GetInteger(PTopoDS_HArray1OfShape1LowerBound);
    pp->_CSFDB_SetPTopoDS_HArray1OfShape1LowerBound(PTopoDS_HArray1OfShape1LowerBound);

    Standard_Integer PTopoDS_HArray1OfShape1UpperBound;
    f.GetInteger(PTopoDS_HArray1OfShape1UpperBound);
    pp->_CSFDB_SetPTopoDS_HArray1OfShape1UpperBound(PTopoDS_HArray1OfShape1UpperBound);

    StdSchema_PTopoDS_FieldOfHArray1OfShape1::SRead((PTopoDS_FieldOfHArray1OfShape1&)pp->_CSFDB_GetPTopoDS_HArray1OfShape1Data(),f,theSchema);

    f.EndReadPersistentObjectData();
  }
}

void StdSchema_PTopoDS_HArray1OfShape1::Read(const Handle(Standard_Persistent)& p, Storage_BaseDriver& f, const Handle(Storage_Schema)& theSchema) const

{ 
  StdSchema_PTopoDS_HArray1OfShape1::SRead(p,f,theSchema);
}
