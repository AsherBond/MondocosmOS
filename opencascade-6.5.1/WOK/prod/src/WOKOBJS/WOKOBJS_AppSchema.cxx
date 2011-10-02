// File:	WOKOBJS_AppSchema.cxx
// Created:	Mon Feb 24 15:43:57 1997
// Author:	Jean GAUTIER
//		<jga@hourax.paris1.matra-dtv.fr>

#include <WOKOBJS_AppSchema.ixx>

//=======================================================================
//Author   : Jean Gautier (jga)
//function : WOKOBJS_AppSchema
//purpose  : 
//=======================================================================
WOKOBJS_AppSchema::WOKOBJS_AppSchema(const Handle(WOKUtils_Path)& apth)
: WOKBuilder_Entity(apth)
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetAppFileName
//purpose  : 
//=======================================================================
 Handle(TCollection_HAsciiString) WOKOBJS_AppSchema::GetAppFileName(const WOKUtils_Param& params,
								    const Handle(TCollection_HAsciiString)& aname) 
{
  Handle(TCollection_HAsciiString) astr;

  params.Set("%OBJS_AppSchemaName", aname->ToCString());

  astr = params.Eval("OBJS_AppSchemaFileName", Standard_True);
  
  return astr;
}


