// File:	WOKOBJS_LibSchema.cxx
// Created:	Mon Feb 24 15:41:51 1997
// Author:	Jean GAUTIER
//		<jga@hourax.paris1.matra-dtv.fr>


#include <WOKOBJS_LibSchema.ixx>

//=======================================================================
//Author   : Jean Gautier (jga)
//function : WOKOBJS_LibSchema
//purpose  : 
//=======================================================================
WOKOBJS_LibSchema::WOKOBJS_LibSchema(const Handle(WOKUtils_Path)& apth)
: WOKBuilder_Entity(apth)
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : GetLibFileName
//purpose  : 
//=======================================================================
 Handle(TCollection_HAsciiString) WOKOBJS_LibSchema::GetLibFileName(const WOKUtils_Param& params,
								    const Handle(TCollection_HAsciiString)& aname) 
{
  Handle(TCollection_HAsciiString) astr;

  params.Set("%OBJS_LibSchemaName", aname->ToCString());

  astr = params.Eval("OBJS_LibSchemaFileName", Standard_True);
  
  return astr;
}

