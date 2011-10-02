// File:	WOKMake_BuildProcessGroup.cxx
// Created:	Thu Jun 12 11:21:26 1997
// Author:	Jean GAUTIER
//		<jga@hourax.paris1.matra-dtv.fr>


#include <WOKMake_BuildProcessGroup.ixx>

//=======================================================================
//Author   : Jean Gautier (jga)
//function : WOKMake_BuildProcessGroup
//purpose  : 
//=======================================================================
WOKMake_BuildProcessGroup::WOKMake_BuildProcessGroup(const Handle(WOKMake_BuildProcess)& abp,
						     const Handle(TCollection_HAsciiString)& aname)
: myname(aname), mybp(abp.operator->()),myordered(Standard_True)
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Name
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKMake_BuildProcessGroup::Name() const
{
  return myname;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AddStep
//purpose  : 
//=======================================================================
void WOKMake_BuildProcessGroup::AddStep(const Handle(TCollection_HAsciiString)& astep) 
{
  mysteps.Append(astep);
  myordered = Standard_False;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Steps
//purpose  : 
//=======================================================================
const TColStd_SequenceOfHAsciiString& WOKMake_BuildProcessGroup::Steps() const
{
  return mysteps;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Step
//purpose  : 
//=======================================================================
const Handle(WOKMake_Step)& WOKMake_BuildProcessGroup::Step(const Standard_Integer anidx) const
{
  return mybp->Find(mysteps.Value(anidx));
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Length
//purpose  : 
//=======================================================================
Standard_Integer WOKMake_BuildProcessGroup::Length() const
{
  return mysteps.Length();
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : ChangeSteps
//purpose  : 
//=======================================================================
void WOKMake_BuildProcessGroup::ChangeSteps(const TColStd_SequenceOfHAsciiString& thesteps)
{
  mysteps.Clear();
  mysteps.Assign(thesteps);
  myordered = Standard_False;
}

Standard_Boolean WOKMake_BuildProcessGroup::IsOrdered() const
{
  return myordered;
}

void WOKMake_BuildProcessGroup::SetOrdered()
{
  myordered = Standard_True;
}
