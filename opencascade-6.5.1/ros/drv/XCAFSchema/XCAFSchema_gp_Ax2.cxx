#ifndef _XCAFSchema_gp_Ax2_HeaderFile
#include <XCAFSchema_gp_Ax2.hxx>
#endif
#ifndef _gp_Ax2_HeaderFile
#include <gp_Ax2.hxx>
#endif
#include <XCAFSchema_gp_Ax2.ixx>
#ifndef _Storage_Schema_HeaderFile
#include <Storage_Schema.hxx>
#endif
#ifndef _Storage_stCONSTclCOM_HeaderFile
#include <Storage_stCONSTclCOM.hxx>
#endif

void XCAFSchema_gp_Ax2::SWrite(const gp_Ax2& pp, Storage_BaseDriver& f, const Handle(Storage_Schema)& theSchema)
{
  f.BeginWriteObjectData();
    XCAFSchema_gp_Ax1::SWrite(pp._CSFDB_Getgp_Ax2axis(),f,theSchema);
    XCAFSchema_gp_Dir::SWrite(pp._CSFDB_Getgp_Ax2vydir(),f,theSchema);
    XCAFSchema_gp_Dir::SWrite(pp._CSFDB_Getgp_Ax2vxdir(),f,theSchema);

  f.EndWriteObjectData();
}

void XCAFSchema_gp_Ax2::SRead(gp_Ax2& pp, Storage_BaseDriver& f, const Handle(Storage_Schema)& theSchema)
{
  f.BeginReadObjectData();

    XCAFSchema_gp_Ax1::SRead((gp_Ax1&)pp._CSFDB_Getgp_Ax2axis(),f,theSchema);

    XCAFSchema_gp_Dir::SRead((gp_Dir&)pp._CSFDB_Getgp_Ax2vydir(),f,theSchema);

    XCAFSchema_gp_Dir::SRead((gp_Dir&)pp._CSFDB_Getgp_Ax2vxdir(),f,theSchema);

  f.EndReadObjectData();
}
