// File:	WOKBuilder_Entity.cxx
// Created:	Tue Aug 22 23:15:37 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <WOKBuilder_Entity.ixx>

#include <TCollection_HAsciiString.hxx>

//=======================================================================
//function : WOKBuilder_Entity
//purpose  : 
//=======================================================================
WOKBuilder_Entity::WOKBuilder_Entity(const Handle(WOKUtils_Path)& apath) : mypath(apath)
{
}

//=======================================================================
//function : SetPath
//purpose  : 
//=======================================================================
void WOKBuilder_Entity::SetPath(const Handle(WOKUtils_Path)& apath)
{
  mypath = apath;
}

