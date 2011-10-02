// File:	WOKBuilder_MSEntity.cxx
// Created:	Mon Sep 18 15:11:49 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <sys/types.h>
#include <time.h>


#include <WOKBuilder_MSEntity.ixx>

WOKBuilder_MSEntity::WOKBuilder_MSEntity(const Handle(WOKBuilder_Specification)& aspecfile, 
					 const Handle(TCollection_HAsciiString)& aname) 
: WOKBuilder_Entity(Handle(WOKUtils_Path)()), myfile(aspecfile), myname(aname)
{
}

WOKBuilder_MSEntity::WOKBuilder_MSEntity(const Handle(TCollection_HAsciiString)& aname) 
: WOKBuilder_Entity(Handle(WOKUtils_Path)()), myname(aname)
{
}

void WOKBuilder_MSEntity::SetName(const Handle(TCollection_HAsciiString)& aname)
{
  myname = aname;
}

void WOKBuilder_MSEntity::SetFile(const Handle(WOKBuilder_Specification)& afile)
{
  myfile = afile;
}

Standard_Boolean WOKBuilder_MSEntity::IsType(const Handle(MS_MetaSchema)& ams) const 
{
  if(ams.IsNull() == Standard_True) return Standard_False;

  if(ams->IsDefined(myname)) return Standard_True;
  else return Standard_False;
}

Standard_Boolean WOKBuilder_MSEntity::IsEntity(const Handle(MS_MetaSchema)& ams) const 
{
  if(ams.IsNull() == Standard_True) return Standard_False;

  if(ams->IsPackage(myname))     return Standard_True;
  //if(ams->IsSchema(myname))      return Standard_True;
  if(ams->IsInterface(myname))   return Standard_True;
  //if(ams->IsEngine(myname))      return Standard_True;
  //if(ams->IsExecutable(myname)) return  Standard_True;
  return Standard_False;
}

const Handle(MS_Type)& WOKBuilder_MSEntity::GetType(const Handle(MS_MetaSchema)& ams) const 
{
  static Handle(MS_Type) NULLRESULT;
  if(ams.IsNull() == Standard_True) return NULLRESULT;
  return ams->GetType(myname);
}

const Handle(MS_GlobalEntity)& WOKBuilder_MSEntity::GetGlobalEntity(const Handle(MS_MetaSchema)& ams) const 
{
  static Handle(MS_GlobalEntity) NULLRESULT;
  if(ams->IsPackage(myname))     return ams->GetPackage(myname);
  //if(ams->IsSchema(myname))      return ams->GetSchema(myname);
  if(ams->IsInterface(myname))   return ams->GetInterface(myname);
  //if(ams->IsEngine(myname))      return ams->GetEngine(myname);
  //if(ams->IsExecutable(myname))  return ams->GetExecutable(myname);
  return NULLRESULT;
}


