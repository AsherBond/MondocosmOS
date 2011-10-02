#ifndef _XCAFSchema_gp_Dir_HeaderFile
#include <XCAFSchema_gp_Dir.hxx>
#endif
#ifndef _gp_Dir_HeaderFile
#include <gp_Dir.hxx>
#endif
#include <XCAFSchema_gp_Dir.ixx>
#ifndef _Storage_Schema_HeaderFile
#include <Storage_Schema.hxx>
#endif
#ifndef _Storage_stCONSTclCOM_HeaderFile
#include <Storage_stCONSTclCOM.hxx>
#endif

void XCAFSchema_gp_Dir::SWrite(const gp_Dir& pp, Storage_BaseDriver& f, const Handle(Storage_Schema)& theSchema)
{
  f.BeginWriteObjectData();
    XCAFSchema_gp_XYZ::SWrite(pp._CSFDB_Getgp_Dircoord(),f,theSchema);

  f.EndWriteObjectData();
}

void XCAFSchema_gp_Dir::SRead(gp_Dir& pp, Storage_BaseDriver& f, const Handle(Storage_Schema)& theSchema)
{
  f.BeginReadObjectData();

    XCAFSchema_gp_XYZ::SRead((gp_XYZ&)pp._CSFDB_Getgp_Dircoord(),f,theSchema);

  f.EndReadObjectData();
}
