// File:	WOKOBJS_AppSchCxxFile.cxx
// Created:	Mon Feb 24 17:21:27 1997
// Author:	Jean GAUTIER
//		<jga@hourax.paris1.matra-dtv.fr>


#include <WOKOBJS_AppSchCxxFile.ixx>

//=======================================================================
//Author   : Jean Gautier (jga)
//function : WOKOBJS_AppSchCxxFile
//purpose  : 
//=======================================================================
WOKOBJS_AppSchCxxFile::WOKOBJS_AppSchCxxFile(const Handle(WOKUtils_Path)& apth)
: WOKBuilder_Entity(apth)
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetAppSchSourceFileName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKOBJS_AppSchCxxFile::GetAppSchSourceFileName(const WOKUtils_Param& params,
										const Handle(TCollection_HAsciiString)& aname) 
{
  Handle(TCollection_HAsciiString) astr;
  params.Set("%OBJS_AppSchemaName", aname->ToCString());
  astr = params.Eval("OBJS_AppSchemaCxxFileName", Standard_True);
  return astr;
}

