#ifndef _StdSchema_PDF_FieldOfHAttributeArray1_HeaderFile
#include <StdSchema_PDF_FieldOfHAttributeArray1.hxx>
#endif
#ifndef _PDF_FieldOfHAttributeArray1_HeaderFile
#include <PDF_FieldOfHAttributeArray1.hxx>
#endif
#include <StdSchema_PDF_FieldOfHAttributeArray1.ixx>
#ifndef _Storage_Schema_HeaderFile
#include <Storage_Schema.hxx>
#endif
#ifndef _Storage_stCONSTclCOM_HeaderFile
#include <Storage_stCONSTclCOM.hxx>
#endif

void StdSchema_PDF_FieldOfHAttributeArray1::SAdd(const PDF_FieldOfHAttributeArray1& p, const Handle(Storage_Schema)& theSchema)
{
    Standard_Integer i;
  for (i = 0; i < p.Length(); i++) {
    theSchema->PersistentToAdd(p.Value(i));    
  }

}

void StdSchema_PDF_FieldOfHAttributeArray1::SWrite(const PDF_FieldOfHAttributeArray1& pp, Storage_BaseDriver& f, const Handle(Storage_Schema)& theSchema)
{
  Standard_Integer i;

  f.BeginWriteObjectData();
  f.PutInteger(pp.Length());
  for (i = 0; i < pp.Length(); i++) {
    theSchema->WritePersistentReference(pp.Value(i),f);

  }
  f.EndWriteObjectData();
}

void StdSchema_PDF_FieldOfHAttributeArray1::SRead(PDF_FieldOfHAttributeArray1& pp, Storage_BaseDriver& f, const Handle(Storage_Schema)& theSchema)
{
  Standard_Integer size = 0;

  f.BeginReadObjectData();
  f.GetInteger(size);
  pp.Resize(size);

  for (Standard_Integer j = 0; j < size; j++) {
    Handle(PDF_Attribute) par;

    theSchema->ReadPersistentReference(par,f);
    pp.SetValue(j,par);

  }
  f.EndReadObjectData();
}
