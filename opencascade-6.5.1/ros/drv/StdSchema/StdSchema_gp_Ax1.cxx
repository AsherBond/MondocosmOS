#ifndef _StdSchema_gp_Ax1_HeaderFile
#include <StdSchema_gp_Ax1.hxx>
#endif
#ifndef _gp_Ax1_HeaderFile
#include <gp_Ax1.hxx>
#endif
#include <StdSchema_gp_Ax1.ixx>
#ifndef _Storage_Schema_HeaderFile
#include <Storage_Schema.hxx>
#endif
#ifndef _Storage_stCONSTclCOM_HeaderFile
#include <Storage_stCONSTclCOM.hxx>
#endif

void StdSchema_gp_Ax1::SWrite(const gp_Ax1& pp, Storage_BaseDriver& f, const Handle(Storage_Schema)& theSchema)
{
  f.BeginWriteObjectData();
    StdSchema_gp_Pnt::SWrite(pp._CSFDB_Getgp_Ax1loc(),f,theSchema);
    StdSchema_gp_Dir::SWrite(pp._CSFDB_Getgp_Ax1vdir(),f,theSchema);

  f.EndWriteObjectData();
}

void StdSchema_gp_Ax1::SRead(gp_Ax1& pp, Storage_BaseDriver& f, const Handle(Storage_Schema)& theSchema)
{
  f.BeginReadObjectData();

    StdSchema_gp_Pnt::SRead((gp_Pnt&)pp._CSFDB_Getgp_Ax1loc(),f,theSchema);

    StdSchema_gp_Dir::SRead((gp_Dir&)pp._CSFDB_Getgp_Ax1vdir(),f,theSchema);

  f.EndReadObjectData();
}
