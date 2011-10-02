// File:	WOKernel_BaseEntity.cxx
// Created:	Tue Aug  8 16:44:52 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <WOKernel_BaseEntity.ixx>

//=======================================================================
//function : WOKernel_BaseEntity
//purpose  : WOKernel BaseEntity initializer
//=======================================================================
WOKernel_BaseEntity::WOKernel_BaseEntity(const Handle(TCollection_HAsciiString)& aname, const Handle(WOKernel_Entity)& anesting)
{
  myname = aname;
  if(anesting.IsNull() == Standard_False)
    {
      mysession = anesting->Session().operator->();
      mynestingentity = anesting->FullName();
    }
  else
    {
      mysession = Handle(WOKernel_Session)().operator->();
    }
}


//=======================================================================
//function : SetName
//purpose  : Sets the name of an entity
//=======================================================================
void WOKernel_BaseEntity::SetName(const Handle(TCollection_HAsciiString)& aname)
{
  myname = aname;
}

//=======================================================================
//function : SetSession
//purpose  : Set the session of Entity
//=======================================================================
void WOKernel_BaseEntity::SetSession(const Handle(WOKernel_Session)& asession)
{
  mysession = asession.operator->();
}

//=======================================================================
//function : SetSession
//purpose  : Set the session of Entity
//=======================================================================
void WOKernel_BaseEntity::SetNesting(const Handle(WOKernel_Entity)& anesting)
{
  mynestingentity = anesting->FullName();
}

//=======================================================================
//function : SetFullName
//purpose  : 
//=======================================================================
void WOKernel_BaseEntity::SetFullName(const Handle(TCollection_HAsciiString)& aname)
{
  myfullname = aname;
}
