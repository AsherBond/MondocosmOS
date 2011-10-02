// File:	WOKAPI_MakeOption.cxx
// Created:	Sat Apr 13 01:43:03 1996
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <WOKAPI_MakeOption.ixx>

//=======================================================================
//Author   : Jean Gautier (jga)
//function : WOKAPI_MakeOption
//purpose  : 
//=======================================================================
WOKAPI_MakeOption::WOKAPI_MakeOption()
  : myforce(Standard_False), mytype(WOKAPI_None)
{
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : WOKAPI_MakeOption
//purpose  : 
//=======================================================================
WOKAPI_MakeOption::WOKAPI_MakeOption(const WOKAPI_MakeOption& another)
  : mycode(another.Code()),
    myforce(another.IsForced()),
    mytype(another.Type()),
    mytargets(another.Targets()),
    myplatforms(another.Platforms())
{
}
//=======================================================================
//function : WOKAPI_MakeOption
//purpose  : 
//=======================================================================
WOKAPI_MakeOption::WOKAPI_MakeOption(const Handle(TCollection_HAsciiString)& astr,
				     const WOKAPI_StepType atype,
				     const Handle(TColStd_HSequenceOfHAsciiString)& targets,
				     const Standard_Boolean forced)
  : mycode(astr), myforce(forced), mytype(atype), mytargets(targets)
{
}
//=======================================================================
//function : WOKAPI_MakeOption
//purpose  : 
//=======================================================================
WOKAPI_MakeOption::WOKAPI_MakeOption(const Handle(TCollection_HAsciiString)& astr,
				     const WOKAPI_StepType atype,
				     const Handle(TColStd_HSequenceOfHAsciiString)& targets,
				     const Standard_Boolean forced,
				     const Handle(TColStd_HSequenceOfHAsciiString) & platforms)
  : mycode(astr), myforce(forced), mytype(atype), mytargets(targets), myplatforms(platforms)
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Code
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKAPI_MakeOption::Code() const {return mycode;}

void WOKAPI_MakeOption::SetCode(const Handle(TCollection_HAsciiString)& acode) {mycode = acode;}



//=======================================================================
//Author   : Jean Gautier (jga)
//function : Type
//purpose  : 
//=======================================================================
WOKAPI_StepType WOKAPI_MakeOption::Type() const {return mytype;}

void WOKAPI_MakeOption::SetType(const WOKAPI_StepType atype) {mytype = atype;}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : Targets
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKAPI_MakeOption::Targets() const {return mytargets;}

void WOKAPI_MakeOption::SetTargets(const Handle(TColStd_HSequenceOfHAsciiString)& targets ) {mytargets = targets;}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : IsForced
//purpose  : 
//=======================================================================
Standard_Boolean WOKAPI_MakeOption::IsForced() const {return myforce;}

void WOKAPI_MakeOption::SetForce(const Standard_Boolean aflg) {myforce = aflg;}

//=======================================================================
//function : Platforms
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKAPI_MakeOption::Platforms() const {return myplatforms;}

void WOKAPI_MakeOption::SetPlatforms(const Handle(TColStd_HSequenceOfHAsciiString)& platforms ) {myplatforms = platforms;}





