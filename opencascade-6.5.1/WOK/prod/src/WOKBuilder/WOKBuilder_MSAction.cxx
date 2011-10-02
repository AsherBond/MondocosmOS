// File:	WOKBuilder_MSAction.cxx
// Created:	Wed Dec 20 17:59:15 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <Standard_ProgramError.hxx>

#ifdef HAVE_CONFIG_H
# include <config.h>
#endif

#include <WOKTools_Messages.hxx>

#include <WOKBuilder_MSAction.ixx>

#include <sys/types.h>

#if defined(HAVE_TIME_H) || defined(WNT)
# include <time.h>
#endif

//=======================================================================
//function : WOKBuilder_MSAction
//purpose  : 
//=======================================================================
 WOKBuilder_MSAction::WOKBuilder_MSAction()
{
}

//=======================================================================
//function : WOKBuilder_MSAction
//purpose  : 
//=======================================================================
WOKBuilder_MSAction::WOKBuilder_MSAction(const Handle(WOKBuilder_MSAction)& anaction,				 
					 const WOKBuilder_MSActionType atype)
: mytype(atype)
{
  myent  = anaction->Entity();
  mydate = anaction->Date();
  mystatus = anaction->Status();
}

//=======================================================================
//function : WOKBuilder_MSAction
//purpose  : 
//=======================================================================
WOKBuilder_MSAction::WOKBuilder_MSAction(const Handle(WOKBuilder_MSEntity)& anentity, 
					  const WOKBuilder_MSActionType atype)
: myent(anentity), mytype(atype), mystatus(WOKBuilder_NotDefined), mydate(-1)
{
}

//=======================================================================
//function : WOKBuilder_MSAction
//purpose  : 
//=======================================================================
WOKBuilder_MSAction::WOKBuilder_MSAction(const Handle(TCollection_HAsciiString)& aname, 
					  const WOKBuilder_MSActionType atype)
: mytype(atype), mystatus(WOKBuilder_NotDefined), mydate(-1)
{
  myent = new WOKBuilder_MSEntity(aname);
}

//=======================================================================
//function : SetEntity
//purpose  : 
//=======================================================================
void WOKBuilder_MSAction::SetEntity(const Handle(WOKBuilder_MSEntity)& anent)
{
  myent = anent;
}

//=======================================================================
//function : SetType
//purpose  : 
//=======================================================================
void WOKBuilder_MSAction::SetType(const WOKBuilder_MSActionType atype)
{
  mytype = atype;
}

//=======================================================================
//function : SetDate
//purpose  : 
//=======================================================================
void WOKBuilder_MSAction::SetDate(const WOKUtils_TimeStat& adate)
{
  mydate = adate;
}

//=======================================================================
//function : SetDate
//purpose  : 
//=======================================================================
void WOKBuilder_MSAction::GetDate()
{
  mydate = time(NULL);
  if(mydate == -1)
    {
      ErrorMsg() << "WOKBuilder_MSAction::GetDate" << "Could not obtain current date" << endm;
      Standard_ProgramError::Raise("WOKBuilder_MSAction::GetDate");
    }
}

//=======================================================================
//function : Status
//purpose  : 
//=======================================================================
WOKBuilder_MSActionStatus WOKBuilder_MSAction::Status() const
{
  return mystatus;
}

//=======================================================================
//function : SetStatus
//purpose  : 
//=======================================================================
void WOKBuilder_MSAction::SetStatus(const WOKBuilder_MSActionStatus astatus)
{
   mystatus = astatus;
}
